//! Implementation of lexer automata(DFA)
use std::cell::Cell;
use std::rc::Rc;

use crate::atn::ATN;
use crate::atn_config::LexerATNConfig;
use crate::atn_config_set::LexerATNConfigSet;
use crate::atn_simulator::{BaseATNSimulator, IATNSimulator};
use crate::atn_state::{ATNState, ATNStateRef};
use crate::char_stream::CharStream;
use crate::dfa::{DFAState, ProposedDFAState, DFA};
use crate::errors::ANTLRError;
use crate::int_stream::{IntStream, EOF};
use crate::lexer::{Lexer, LexerPosition, LEXER_MAX_CHAR_VALUE, LEXER_MIN_CHAR_VALUE};
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::PredictionContextRef;
use crate::prediction_context::{PredictionContext, PredictionContextCache};
use crate::token::TOKEN_EOF;

use crate::token_factory::TokenFactory;
use crate::transition::{ActionTransition, Transition};
use crate::utils::cell_update;

// todo rewrite this to be actually usable
#[doc(hidden)]
pub trait ILexerATNSimulator<'sim>: IATNSimulator<'sim, LexerATNConfigSet<'sim>> {
    fn reset(&mut self);
    fn match_token<'input, 'arena, Input, TF>(
        &mut self,
        mode: usize,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena;
    fn get_char_position_in_line(&self) -> i32;
    fn set_char_position_in_line(&mut self, column: i32);
    fn get_line(&self) -> u32;
    fn set_line(&mut self, line: u32);
    fn consume<T: IntStream + ?Sized>(&self, input: &mut T);
    #[cold]
    fn recover(&mut self, _re: ANTLRError, input: &mut impl IntStream) {
        if input.la(1) != EOF {
            self.consume(input)
        }
    }
}

/// Simple DFA implementation enough for lexer.
#[derive(Debug)]
pub struct LexerATNSimulator<'sim> {
    base: BaseATNSimulator<'sim, LexerATNConfigSet<'sim>>,

    //    merge_cache: DoubleDict,
    start_index: isize,
    pub(crate) current_pos: Rc<LexerPosition>,
    mode: usize,
    prev_accept: SimState<'sim>,
    // lexer_action_executor: Option<Box<LexerActionExecutor>>,
}

impl<'sim> ILexerATNSimulator<'sim> for LexerATNSimulator<'sim> {
    fn reset(&mut self) {
        self.prev_accept.reset()
    }

    fn match_token<'input, 'arena, Input, TF>(
        &mut self,
        mode: usize,
        //        input:&mut dyn CharStream,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let scratch = bumpalo::Bump::new();

        self.mode = mode;
        let mark = lexer.input().mark();
        //        println!("start matching on mode {}",mode);
        let result = (|| {
            self.start_index = lexer.input().index();
            self.prev_accept.reset();
            let dfa = self
                .decision_to_dfa(mode)
                .ok_or_else(|| ANTLRError::illegal_state("invalid mode".into()))?;

            match dfa.s0() {
                None => self.match_atn(lexer, dfa, &scratch),
                Some(s0) => self.exec_atn(s0, lexer, dfa, &scratch),
                //                Err(_) => panic!("dfa rwlock error")
            }
        })();
        lexer.input().release(mark);
        result
    }

    fn get_char_position_in_line(&self) -> i32 {
        self.current_pos.char_position_in_line.get()
    }

    fn set_char_position_in_line(&mut self, column: i32) {
        self.current_pos.char_position_in_line.set(column)
    }

    fn get_line(&self) -> u32 {
        self.current_pos.line.get()
    }

    fn set_line(&mut self, line: u32) {
        self.current_pos.line.set(line)
    }

    fn consume<T: IntStream + ?Sized>(&self, _input: &mut T) {
        let ch = _input.la(1);
        if ch == '\n' as i32 {
            cell_update(&self.current_pos.line, |x| x + 1);
            self.current_pos.char_position_in_line.set(0);
        } else {
            cell_update(&self.current_pos.char_position_in_line, |x| x + 1);
        }
        _input.consume();
    }

    //    fn get_recog(&self) -> Rc<RefCell<Box<Recognizer>>>{
    //        Rc::clone(&self.recog)
    //    }
}

impl<'sim> IATNSimulator<'sim, LexerATNConfigSet<'sim>> for LexerATNSimulator<'sim> {
    fn shared_context_cache(&self) -> &'sim PredictionContextCache<'sim> {
        self.base.shared_context_cache()
    }

    fn atn(&self) -> &'static ATN {
        self.base.atn()
    }

    fn decision_to_dfa(&self, decision: usize) -> Option<&'sim DFA<'sim, LexerATNConfigSet<'sim>>> {
        self.base.decision_to_dfa(decision)
    }
}

#[allow(missing_docs)]
pub const MIN_DFA_EDGE: i32 = 0;
#[allow(missing_docs)]
pub const MAX_DFA_EDGE: i32 = 127;
pub const LEXER_DFA_EDGE_SET_SIZE: usize = (MAX_DFA_EDGE - MIN_DFA_EDGE + 1) as usize;

impl<'sim> LexerATNSimulator<'sim> {
    /// Creates `LexerATNSimulator` instance which creates DFA over `atn`
    pub fn new(base: BaseATNSimulator<'sim, LexerATNConfigSet<'sim>>) -> LexerATNSimulator<'sim> {
        LexerATNSimulator {
            base,
            start_index: 0,
            current_pos: Rc::new(LexerPosition {
                line: Cell::new(0),
                char_position_in_line: Cell::new(0),
            }),
            mode: 0,
            prev_accept: SimState::new(),
            // lexer_action_executor: None,
        }
    }

    //    fn copy_state(&self, _simulator: &mut LexerATNSimulator) {
    //        unimplemented!()
    //    }

    #[cold]
    fn match_atn<'scratch, 'input, 'arena, Input, TF>(
        &mut self,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        dfa: &'sim DFA<LexerATNConfigSet<'sim>>,
        scratch: &'scratch bumpalo::Bump,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        //        let start_state = self.atn().mode_to_start_state.get(self.mode as usize).ok_or(ANTLRError::IllegalStateError("invalid mode".into()))?;
        let atn = self.atn();
        let start_state = *atn
            .mode_to_start_state
            .get(self.mode)
            .ok_or_else(|| ANTLRError::illegal_state("invalid mode".into()))?;

        let _old_mode = self.mode;
        let mut s0_closure = self.compute_start_state(start_state.as_ref(), lexer, scratch);
        let _supress_edge = s0_closure.has_semantic_context();
        s0_closure.set_has_semantic_context(false);

        let next_state = self.add_dfastate(dfa, s0_closure)?;
        if !_supress_edge {
            dfa.set_s0(next_state);
        }

        self.exec_atn(next_state, lexer, dfa, scratch)
    }

    fn exec_atn<'scratch, 'input, 'arena, Input, TF>(
        &mut self,
        //        input: &'a mut dyn CharStream,
        ds0: &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        dfa: &'sim DFA<LexerATNConfigSet<'sim>>,
        scratch: &'scratch bumpalo::Bump,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        //        if self.get_dfa().states.read().unwrap().get(ds0).unwrap().is_accept_state{
        self.capture_sim_state(lexer.input(), ds0);
        //        }

        let mut symbol = lexer.input().la(1);
        let mut s = ds0;
        loop {
            let target = if let Some(target) = dfa.get_edge(s, (symbol - MIN_DFA_EDGE) as usize) {
                target
            } else {
                self.compute_target_state(dfa, s, symbol, lexer, scratch)?
            };

            if target.is_error_state() {
                break;
            }
            //            println!(" --- target computed {:?}", self.get_dfa().states.read().unwrap()[target].configs.configs.iter().map(|it|it.get_state()).collect::<Vec<_>>());

            if symbol != EOF {
                self.consume(lexer.input());
            }

            if self.capture_sim_state(lexer.input(), target) && symbol == EOF {
                break;
            }

            symbol = lexer.input().la(1);

            s = target;
        }
        // let _last = self.get_dfa().states.read().get(s).unwrap();

        self.fail_or_accept(symbol, lexer)
    }

    #[cold]
    fn compute_target_state<'scratch, 'input, 'arena, Input, TF>(
        &self,
        dfa: &'sim DFA<LexerATNConfigSet<'sim>>,
        s: &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
        _t: i32,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        scratch: &'scratch bumpalo::Bump,
    ) -> Result<&'sim DFAState<'sim, LexerATNConfigSet<'sim>>, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let mut reach = LexerATNConfigSet::new(scratch);
        self.get_reachable_config_set(s.configs(), &mut reach, _t, lexer, scratch);
        //        println!(" --- target computed {:?}", reach.configs.iter().map(|it|it.get_state()).collect::<Vec<_>>());

        // let mut states = dfa_mut.states;
        if reach.is_empty() {
            if !reach.has_semantic_context() {
                self.add_dfaedge(dfa, s, _t, dfa.get_error_state());
            }

            return Ok(dfa.get_error_state());
        }

        let supress_edge = reach.has_semantic_context();
        reach.set_has_semantic_context(false);
        let to = self.add_dfastate(dfa, reach)?;
        if !supress_edge {
            let from = s;
            self.add_dfaedge(dfa, from, _t, to);
        }
        //        println!("target state computed from {:?} to {:?} on symbol {}", _s, to, char::try_from(_t as u32).unwrap());
        Ok(to)
    }

    fn get_reachable_config_set<'scratch, 'input, 'arena, Input, TF>(
        &self,
        // _states: &V,
        //        _input: &mut dyn CharStream,
        _closure: &'scratch LexerATNConfigSet<'scratch>,
        _reach: &mut LexerATNConfigSet<'scratch>,
        _t: i32,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        scratch: &'scratch bumpalo::Bump,
    ) where
        'input: 'arena,
        'sim: 'scratch,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let mut skip_alt = 0;
        //        println!(" --- source {:?}", _closure.configs.iter().map(|it|it.get_state()).collect::<Vec<_>>());
        for config in _closure.get_items() {
            let current_alt_reached_accept_state = config.get_alt() == skip_alt;
            if current_alt_reached_accept_state && config.has_passed_through_non_greedy_decision() {
                continue;
            }
            let atn_state = config.get_state();
            for tr in atn_state.get_transitions() {
                if let Some(target) = tr.get_reachable_target(_t) {
                    let exec = config.get_lexer_executor().map(|x| {
                        scratch.alloc(x.fix_offset_before_match(
                            scratch,
                            lexer.input().index() - self.start_index,
                        )) as &'scratch LexerActionExecutor<'scratch>
                    });

                    let new = config.clone().with_state(target).with_lexer_executor(exec);
                    if self.closure(
                        new,
                        _reach,
                        current_alt_reached_accept_state,
                        true,
                        _t == EOF,
                        lexer,
                        scratch,
                    ) {
                        skip_alt = config.get_alt();
                        break;
                    }
                }
            }
        }
    }

    //    fn get_reachable_target<T>(&self, states: &T, _trans: &Transition, _t: i32) -> &ATNState
    //    where
    //        T: Deref<Target = Vec<DFAState>>,
    //    {
    //        unimplemented!()
    //    }

    fn fail_or_accept<'input, 'arena, Input, TF>(
        &mut self,
        _t: i32,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        //        println!("fail_or_accept");
        if let Some(state) = self.prev_accept.dfa_state {
            //            let lexer_action_executor;
            self.accept(lexer.input());

            let prediction = {
                //                println!("accepted, prediction = {}, on dfastate {}", dfa_state_prediction.prediction, dfa_state_prediction.state_number);
                //                lexer_action_executor = dfa_state_prediction.lexer_action_executor.clone();
                //                let recog = self.recog.clone();
                if let Some(x) = state.lexer_action_executor() {
                    x.execute(lexer, self.start_index)
                }

                state.prediction()
            };

            //            self.lexer_action_executor = lexer_action_executor;
            Ok(prediction)
        } else {
            if _t == EOF && lexer.input().index() == self.start_index {
                return Ok(TOKEN_EOF);
            }
            Err(ANTLRError::lexer_no_alt(self.start_index))
        }
    }

    fn accept(&mut self, input: &mut impl IntStream) {
        input.seek(self.prev_accept.index);
        self.current_pos.line.set(self.prev_accept.line);
        self.current_pos
            .char_position_in_line
            .set(self.prev_accept.column);
    }

    fn compute_start_state<'scratch, 'input, 'arena, Input, TF>(
        &self,
        p: &ATNState,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        scratch: &'scratch bumpalo::Bump,
    ) -> LexerATNConfigSet<'scratch>
    where
        'input: 'arena,
        'sim: 'scratch,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        //        let initial_context = &EMPTY_PREDICTION_CONTEXT;
        let mut config_set = LexerATNConfigSet::new(scratch);
        for (i, tr) in p.get_transitions().iter().enumerate() {
            let target = tr.get_target();
            let atn_config =
                LexerATNConfig::new(target, (i + 1) as i32, PredictionContextRef::new_empty());
            self.closure(
                atn_config,
                &mut config_set,
                false,
                false,
                false,
                lexer,
                scratch,
            );
        }

        config_set
    }

    #[allow(clippy::too_many_arguments)]
    fn closure<'scratch, 'input, 'arena, Input, TF>(
        &self,
        mut config: LexerATNConfig<'scratch>,
        config_set: &mut LexerATNConfigSet<'scratch>,
        mut _current_alt_reached_accept_state: bool,
        speculative: bool,
        treat_eofas_epsilon: bool,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        scratch: &'scratch bumpalo::Bump,
    ) -> bool
    where
        'input: 'arena,
        'sim: 'scratch,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let state = config.get_state();
        //        println!("closure called on state {} {:?}", state.get_state_number(), state.get_state_type());

        if let ATNState::RuleStop(_) = *state {
            //            println!("reached rulestopstate {}",state.get_state_number());
            if config.get_context().map(|x| x.has_empty_path()) != Some(false) {
                if config.get_context().map(|x| x.is_empty()) != Some(false) {
                    config_set.add(config);
                    return true;
                } else {
                    config_set.add(
                        config
                            .clone()
                            .with_state(state)
                            .with_prediction_context(Some(PredictionContextRef::new_empty())),
                    );
                    _current_alt_reached_accept_state = true
                }
            }

            if config.get_context().map(|x| x.is_empty()) == Some(false) {
                let ctx = config.take_context();
                for i in 0..ctx.length() {
                    if ctx.get_return_state(i) != ATNStateRef::invalid() {
                        let new_ctx = ctx.get_parent(i);
                        let return_state = ctx.get_return_state(i);
                        let next_config = config
                            .clone()
                            .with_state(return_state)
                            .with_prediction_context(new_ctx);
                        _current_alt_reached_accept_state = self.closure(
                            next_config,
                            config_set,
                            _current_alt_reached_accept_state,
                            speculative,
                            treat_eofas_epsilon,
                            lexer,
                            scratch,
                        )
                    }
                }
            }

            return _current_alt_reached_accept_state;
        }

        if !state.has_epsilon_only_transitions()
            && (!_current_alt_reached_accept_state
                || !config.has_passed_through_non_greedy_decision())
        {
            config_set.add(config.clone());
        }

        let state = config.get_state();
        let config = scratch.alloc(config);

        for tr in state.get_transitions() {
            let c = self.get_epsilon_target(
                config,
                tr,
                config_set,
                speculative,
                treat_eofas_epsilon,
                lexer,
                scratch,
            );

            if let Some(c) = c {
                _current_alt_reached_accept_state = self.closure(
                    c,
                    config_set,
                    _current_alt_reached_accept_state,
                    speculative,
                    treat_eofas_epsilon,
                    lexer,
                    scratch,
                );
            }
        }

        _current_alt_reached_accept_state
    }

    #[allow(clippy::too_many_arguments)]
    fn get_epsilon_target<'scratch, 'input, 'arena, Input, TF>(
        &self,
        //        _input: &mut dyn CharStream,
        _config: &'scratch LexerATNConfig<'scratch>,
        _trans: &Transition,
        _configs: &mut LexerATNConfigSet<'scratch>,
        _speculative: bool,
        _treat_eofas_epsilon: bool,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        scratch: &'scratch bumpalo::Bump,
    ) -> Option<LexerATNConfig<'scratch>>
    where
        'input: 'arena,
        'sim: 'scratch,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let mut result = None;
        let target = _trans.get_target();
        //        println!("epsilon target for {:?} is {:?}", _trans, target.get_state_type());
        match _trans {
            Transition::Epsilon(_) => {
                result = Some(_config.clone().with_state(target));
            }
            Transition::Rule(rt) => {
                //println!("rule transition follow state{}", rt.follow_state);
                let pred_ctx = PredictionContext::new_singleton(
                    Some(_config.get_context().unwrap()),
                    rt.follow_state,
                );
                result = Some(
                    _config
                        .clone()
                        .with_state(target)
                        .with_prediction_context(Some(scratch.alloc(pred_ctx))),
                );
            }
            Transition::Predicate(tr) => {
                _configs.set_has_semantic_context(true);
                if self.evaluate_predicate(tr.rule_index, tr.pred_index, _speculative, lexer) {
                    result = Some(_config.clone().with_state(target));
                }
            }
            Transition::Action(_) => {
                //println!("action transition");
                if _config.get_context().map(|x| x.has_empty_path()) != Some(false) {
                    let tr = _trans.try_as::<ActionTransition>().unwrap();
                    let lexer_action = self.atn().lexer_actions[tr.action_index as usize].clone();
                    //dbg!(&lexer_action);
                    let lexer_action_executor = LexerActionExecutor::new_copy_append(
                        scratch,
                        _config.get_lexer_executor(),
                        lexer_action,
                    );
                    result = Some(
                        _config
                            .clone()
                            .with_state(target)
                            .with_lexer_executor(Some(scratch.alloc(lexer_action_executor))),
                    )
                } else {
                    result = Some(_config.clone().with_state(target));
                }
            }
            Transition::Range(_) | Transition::Set(_) | Transition::Atom(_) => {
                if _treat_eofas_epsilon
                    && _trans.matches(EOF, LEXER_MIN_CHAR_VALUE, LEXER_MAX_CHAR_VALUE)
                {
                    let target = _trans.get_target();
                    result = Some(_config.clone().with_state(target));
                }
            }
            Transition::Wildcard(_) => {}
            Transition::NotSet(_) => {}
            Transition::PrecedencePredicate(_) => {
                panic!("precedence predicates are not supposed to be in lexer");
            }
        }

        result
    }

    fn evaluate_predicate<'input, 'arena, Input, TF, T: Lexer<'input, 'arena, Input, TF>>(
        &self,
        //        input: &mut dyn CharStream,
        rule_index: i32,
        pred_index: i32,
        speculative: bool,
        lexer: &mut T,
    ) -> bool
    where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        if !speculative {
            return lexer.sempred(None, rule_index, pred_index);
        }

        let saved_column = self.current_pos.char_position_in_line.get();
        let saved_line = self.current_pos.line.get();
        let index = lexer.input().index();
        let marker = lexer.input().mark();
        self.consume(lexer.input());

        let result = lexer.sempred(None, rule_index, pred_index);

        self.current_pos.char_position_in_line.set(saved_column);
        self.current_pos.line.set(saved_line);
        lexer.input().seek(index);
        lexer.input().release(marker);
        result
    }

    fn capture_sim_state(
        &mut self,
        input: &impl IntStream,
        dfa_state: &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
    ) -> bool {
        if dfa_state.is_accept_state() {
            self.set_prev_accept(SimState {
                index: input.index(),
                line: self.current_pos.line.get(),
                column: self.current_pos.char_position_in_line.get(),
                dfa_state: Some(dfa_state),
            });
            // self.prev_accept.index = input.index();
            // self.prev_accept.dfa_state = Some(dfa_state);
            return true;
        }
        false
    }

    fn set_prev_accept(&mut self, s: SimState<'sim>) {
        self.prev_accept = s;
    }

    fn add_dfaedge(
        &self,
        dfa: &'sim DFA<LexerATNConfigSet<'sim>>,
        from: &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
        t: i32,
        to: &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
    ) {
        if !(MIN_DFA_EDGE..=MAX_DFA_EDGE).contains(&t) {
            return;
        }

        dfa.set_edge(from, (t - MIN_DFA_EDGE) as usize, to);
    }

    fn add_dfastate(
        &self,
        dfa: &'sim DFA<LexerATNConfigSet<'sim>>,
        configs: LexerATNConfigSet,
    ) -> Result<&'sim DFAState<'sim, LexerATNConfigSet<'sim>>, ANTLRError> {
        assert!(!configs.has_semantic_context());

        let state = ProposedDFAState::new(configs);

        self.base.check_allocation_limit()?;
        Ok(dfa.add_lexer_state(state, self))
    }

    /// Returns current DFA that is currently used.
    pub fn get_dfa(&self) -> &'sim DFA<'sim, LexerATNConfigSet<'sim>> {
        self.decision_to_dfa(self.mode)
            .expect("self.mode should be valid")
    }

    /// Returns current DFA for particular lexer mode
    pub fn get_dfa_for_mode(&self, mode: usize) -> &'sim DFA<'sim, LexerATNConfigSet<'sim>> {
        self.decision_to_dfa(mode).expect("mode should be valid")
    }

    // fn get_token_name(&self, _tt: i32) -> String { unimplemented!() }

    // fn reset_sim_state(_sim: &mut SimState) { unimplemented!() }
}

#[derive(Debug)]
pub(crate) struct SimState<'sim> {
    index: isize,
    line: u32,
    column: i32,
    dfa_state: Option<&'sim DFAState<'sim, LexerATNConfigSet<'sim>>>,
}

impl<'sim> SimState<'sim> {
    pub(crate) fn new() -> SimState<'sim> {
        SimState {
            index: -1,
            line: 0,
            column: -1,
            dfa_state: None,
        }
    }

    pub(crate) fn reset(&mut self) {
        // self.index = -1;
        // self.line = 0;
        // self.column = -1;
        self.dfa_state = None;
    }
}
