//! Base parser implementation
use std::borrow::Borrow;
use std::cell::Cell;
use std::collections::{HashMap, HashSet};

use std::marker::PhantomData;
use std::ptr;
use std::sync::Arc;

use bit_set::BitSet;

use crate::atn::{ATN, INVALID_ALT};
use crate::atn_config::ATNConfig;
use crate::atn_config_set::ATNConfigSet;
use crate::atn_simulator::{BaseATNSimulator, IATNSimulator};
use crate::atn_state::{
    ATNDecisionState, ATNState, ATNStateRef, DecisionState, ATNSTATE_BLOCK_END,
};
use crate::dfa::{DFAState, PredPrediction, ProposedDFAState, ScopeExt, DFA};
use crate::errors::ANTLRError;
use crate::int_stream::EOF;
use crate::interval_set::IntervalSet;
use crate::parser::Parser;

use crate::prediction_context::{
    MurmurHasherBuilder, PredictionContext, PredictionContextCache, EMPTY_PREDICTION_CONTEXT,
    PREDICTION_CONTEXT_EMPTY_RETURN_STATE,
};
use crate::prediction_mode::*;
use crate::semantic_context::SemanticContext;
use crate::token::{OwningToken, Token, TOKEN_EOF, TOKEN_EPSILON};

use crate::token_factory::TokenFactory;
use crate::token_stream::TokenStream;
use crate::transition::{
    ActionTransition, EpsilonTransition, PrecedencePredicateTransition, PredicateTransition,
    RuleTransition, Transition,
};

/// ### The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
///
/// <p>
/// The basic complexity of the adaptive strategy makes it harder to understand.
/// We begin with ATN simulation to build paths in a DFA. Subsequent prediction
/// requests go through the DFA first. If they reach a state without an edge for
/// the current symbol, the algorithm fails over to the ATN simulation to
/// complete the DFA path for the current input (until it finds a conflict state
/// or uniquely predicting state).</p>
///
/// <p>
/// All of that is done without using the outer context because we want to create
/// a DFA that is not dependent upon the rule invocation stack when we do a
/// prediction. One DFA works in all contexts. We avoid using context not
/// necessarily because it's slower, although it can be, but because of the DFA
/// caching problem. The closure routine only considers the rule invocation stack
/// created during prediction beginning in the decision rule. For example, if
/// prediction occurs without invoking another rule's ATN, there are no context
/// stacks in the configurations. When lack of context leads to a conflict, we
/// don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
/// strategy (versus full LL(*)).</p>
///
/// <p>
/// When SLL yields a configuration set with conflict, we rewind the input and
/// retry the ATN simulation, this time using full outer context without adding
/// to the DFA. Configuration context stacks will be the full invocation stacks
/// from the start rule. If we get a conflict using full context, then we can
/// definitively say we have a true ambiguity for that input sequence. If we
/// don't get a conflict, it implies that the decision is sensitive to the outer
/// context. (It is not context-sensitive in the sense of context-sensitive
/// grammars.)</p>
///
/// <p>
/// The next time we reach this DFA state with an SLL conflict, through DFA
/// simulation, we will again retry the ATN simulation using full context mode.
/// This is slow because we can't save the results and have to "interpret" the
/// ATN each time we get that input.</p>
///
/// **For more info see Java version**
#[derive(Debug)]
pub struct ParserATNSimulator {
    base: BaseATNSimulator,
    prediction_mode: Cell<PredictionMode>,
    start_index: Cell<isize>,
    // pd:PhantomData<P>
}

/// Just a local helper structure to spoil function parameters as little as possible
struct Local<'a, 'input, 'arena, TF, P>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    P: Parser<'input, 'arena, TF>,
{
    outer_context: &'arena P::Node,
    dfa_ref: &'a DFA,
    merge_cache: &'a mut MergeCache,
    precedence: i32,
    parser: &'a mut P,
    pd: PhantomData<Box<dyn TokenStream<'input, 'arena, TF>>>,
}

impl<'a, 'input, 'arena, TF, P> Local<'a, 'input, 'arena, TF, P>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    P: Parser<'input, 'arena, TF>,
{
    fn input(&mut self) -> &mut dyn TokenStream<'input, 'arena, TF> {
        self.parser.get_input_stream_mut()
    }
    // fn seek(&mut self, i: isize) { self.input().seek(i) }
    fn outer_context(&self) -> &'arena P::Node {
        self.outer_context
    }
}

pub(crate) type MergeCache = HashMap<
    (Arc<PredictionContext>, Arc<PredictionContext>),
    Arc<PredictionContext>,
    MurmurHasherBuilder,
>;

impl ParserATNSimulator {
    /// creates new `ParserATNSimulator`
    pub fn new(
        atn: &'static ATN,
        decision_to_dfa: &'static Vec<DFA>,
        shared_context_cache: &'static PredictionContextCache,
    ) -> ParserATNSimulator {
        ParserATNSimulator {
            base: BaseATNSimulator::new_base_atnsimulator(
                atn,
                decision_to_dfa,
                shared_context_cache,
            ),
            prediction_mode: Cell::new(PredictionMode::LL),
            start_index: Cell::new(0),
        }
    }

    /// Returns current prediction mode
    pub fn get_prediction_mode(&self) -> PredictionMode {
        self.prediction_mode.get()
    }

    /// Sets current prediction mode
    pub fn set_prediction_mode(&self, v: PredictionMode) {
        self.prediction_mode.set(v)
    }

    // fn reset(&self) { unimplemented!() }

    /// Called by generated parser to choose an alternative when LL(1) parsing is not enough
    pub fn adaptive_predict<'input, 'arena, TF, P>(
        &self,
        decision: i32,
        parser: &mut P,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        self.start_index.set(parser.get_input_stream_mut().index());
        let mut merge_cache: MergeCache = HashMap::with_hasher(MurmurHasherBuilder {});
        let mut local = Local {
            outer_context: parser.get_current_context(),
            dfa_ref: &self.decision_to_dfa()[decision as usize],
            merge_cache: &mut merge_cache,
            precedence: parser.get_precedence(),
            parser,
            pd: PhantomData,
        };
        //        4!("adaptive_predict decision {}, is_prec {}",decision,local.dfa.is_precedence_dfa());

        let m = local.input().mark();

        let result = {
            let s0 = {
                let dfa = local.dfa_ref;

                if dfa.is_precedence_dfa() {
                    dfa.get_precedence_start_state(
                        local.precedence, /*parser.get_precedence()*/
                    )
                } else {
                    dfa.s0()
                }
            };

            let s0 = s0.unwrap_or_else(|| {
                let s0_closure = self.compute_start_state(
                    local.dfa_ref.borrow().atn_start_state,
                    // PredictionContext::from_rule_context::<'a,T::Node>(self.atn(), empty_ctx::<T::Node>().as_ref()),
                    EMPTY_PREDICTION_CONTEXT.clone(),
                    false,
                    &mut local,
                );

                if local.dfa_ref.is_precedence_dfa() {
                    let s0_closure_updated = self.apply_precedence_filter(&s0_closure, &mut local);

                    {
                        //let mut dfa_mut = local.dfa_ref.borrow_mut();
                        let dfa_ref = local.dfa_ref;
                        dfa_ref.set_s0_configs(Box::new(s0_closure));
                        let new_s0 =
                            self.add_dfastate(dfa_ref, ProposedDFAState::new(s0_closure_updated));

                        dfa_ref.set_precedence_start_state(local.precedence, new_s0);
                        new_s0
                    }
                } else {
                    //let mut dfa_mut = local.dfa_ref.borrow_mut();
                    let dfa_ref = local.dfa_ref;
                    let s0 = self.add_dfastate(dfa_ref, ProposedDFAState::new(s0_closure));
                    dfa_ref.set_s0(s0);
                    s0
                }
            });

            self.exec_atn(&mut local, s0)?
        };

        local.input().seek(self.start_index.get());
        local.input().release(m);
        //        println!("result = {}", result);
        Ok(result)
    }

    #[allow(non_snake_case)]
    fn exec_atn<'a, 'input, 'arena, TF, P>(
        &'a self,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
        s0: &'a DFAState<'a>,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let mut previousD = s0;

        let mut token = local.input().la(1);

        loop {
            //            println!("exec atn loop previous D {}",previousD as i32 -1);
            let D = if let Some(s) = { Self::get_existing_target_state(previousD, token) } {
                s
            } else {
                self.compute_target_state(previousD, token, local)
            };

            // let dfa = local.dfa.take().unwrap();
            // let states = &dfa.states;
            if D.is_error_state() {
                let err = self.no_viable_alt(local, previousD.configs(), self.start_index.get());
                local.input().seek(self.start_index.get());
                let alt = self.get_syn_valid_or_sem_invalid_alt_that_finished_decision_entry_rule(
                    previousD.configs(),
                    local,
                );
                if alt != INVALID_ALT {
                    return Ok(alt);
                }
                return Err(err);
            }

            let dfa = local.dfa_ref;
            let Dstate = D;
            if Dstate.requires_full_context && self.prediction_mode.get() != PredictionMode::SLL {
                let mut conflicting_alts = Dstate.configs().conflicting_alts.clone(); //todo get rid of clone?
                if !Dstate.predicates.is_empty() {
                    let conflict_index = local.input().index();
                    if conflict_index != self.start_index.get() {
                        local.input().seek(self.start_index.get())
                    }

                    conflicting_alts = self.eval_semantic_context(local, &Dstate.predicates, true);
                    //                    println!("conflicting_alts {:?}",&conflicting_alts);
                    if conflicting_alts.len() == 1 {
                        return Ok(conflicting_alts.iter().next().unwrap() as i32);
                    }

                    if conflict_index != self.start_index.get() {
                        local.input().seek(conflict_index)
                    }
                }

                self.report_attempting_full_context(
                    dfa,
                    &conflicting_alts,
                    Dstate.configs(),
                    self.start_index.get(),
                    local.input().index(),
                    local.parser,
                );
                let atn_start_state = dfa.atn_start_state;

                let s0_closure = self.compute_start_state(
                    atn_start_state,
                    PredictionContext::from_rule_context(self.atn(), local.outer_context()),
                    true,
                    local,
                );

                return self.exec_atn_with_full_context(local, s0_closure);
            }

            if Dstate.is_accept_state {
                if Dstate.predicates.is_empty() {
                    //                    println!("prediction !!{}",Dstate.prediction);
                    return Ok(Dstate.prediction);
                }

                let stop_index = local.input().index();
                local.input().seek(self.start_index.get());

                let alts = self.eval_semantic_context(local, &Dstate.predicates, true);
                match alts.len() {
                    0 => {
                        return Err(self.no_viable_alt(
                            local,
                            Dstate.configs(),
                            self.start_index.get(),
                        ))
                    }
                    1 => return Ok(alts.iter().next().unwrap() as i32),
                    _ => {
                        self.report_ambiguity(
                            dfa,
                            self.start_index.get(),
                            stop_index,
                            false,
                            &alts,
                            Dstate.configs(),
                            local.parser,
                        );
                        return Ok(alts.iter().next().unwrap() as i32);
                    }
                }
            }
            previousD = D;

            if token != EOF {
                local.input().consume();
                token = local.input().la(1);
            }
        }
    }

    #[allow(non_snake_case)]
    fn get_existing_target_state<'dfa>(
        previousD: &DFAState<'dfa>,
        t: i32,
    ) -> Option<&'dfa DFAState<'dfa>> {
        previousD.get_edge((t + 1) as usize)
    }

    #[allow(non_snake_case)]
    fn compute_target_state<'a, 'input, 'arena, TF, P>(
        &'a self,
        // dfa: &mut DFA,
        previousD: &'a DFAState<'a>,
        t: i32,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> &'a DFAState<'a>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        println!("source config {:?}",dfa.states.read()[previousD].configs.as_ref());
        let reach = {
            let closure = previousD.configs();
            self.compute_reach_set(closure, t, false, local)
        };

        let reach = match reach {
            None => {
                self.add_dfaedge(previousD, t, local.dfa_ref.get_error_state());
                return local.dfa_ref.get_error_state();
            }
            Some(x) => x,
        };

        let predicted_alt = self.get_unique_alt(&reach);
        //        println!("predicted_alt {}",predicted_alt);

        let mut D = ProposedDFAState::new(reach);

        if predicted_alt != INVALID_ALT {
            D.is_accept_state = true;
            D.configs.set_unique_alt(predicted_alt);
            D.prediction = predicted_alt
        } else if self.all_configs_in_rule_stop_state(&D.configs)
            || has_sll_conflict_terminating_prediction(self.prediction_mode.get(), &D.configs)
        {
            let alts = self.get_conflicting_alts(&D.configs);
            D.prediction = alts.iter().next().unwrap() as i32;
            D.configs.conflicting_alts = alts;
            D.requires_full_context = true;
            D.is_accept_state = true;
        }

        //        println!("target config {:?}",&D.configs);
        if D.is_accept_state && D.configs.has_semantic_context() {
            let decision_state =
                self.atn().decision_to_state[local.dfa_ref.borrow().decision as usize];
            self.predicate_dfa_state(&mut D, &self.atn().states[decision_state as usize]);
            //            println!("predicates compute target {:?}",&D.predicates);
            if !D.predicates.is_empty() {
                D.prediction = INVALID_ALT
            }
        }

        let dfa_ref = local.dfa_ref;
        let D = self.add_dfastate(dfa_ref, D);
        self.add_dfaedge(previousD, t, D);
        D
    }

    fn predicate_dfa_state(&self, dfa_state: &mut ProposedDFAState, decision_state: &ATNState) {
        let nalts = decision_state.get_transitions().len();
        let alts_to_collect_preds_from =
            self.get_conflicting_alts_or_unique_alt(&dfa_state.configs);
        let alt_to_pred =
            self.get_preds_for_ambig_alts(&alts_to_collect_preds_from, &dfa_state.configs, nalts);
        if let Some(alt_to_pred) = alt_to_pred {
            dfa_state.predicates =
                self.get_predicate_predictions(&alts_to_collect_preds_from, alt_to_pred);
            dfa_state.prediction = INVALID_ALT;
        } else {
            dfa_state.prediction = alts_to_collect_preds_from
                .iter()
                .next()
                .unwrap_or(0 /*in java it is -1 but looks like 0 is good enough*/)
                as i32;
        }
    }

    fn exec_atn_with_full_context<'a, 'input, 'arena, TF, P>(
        &'a self,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
        // _D: &DFAState,
        s0: ATNConfigSet,
    ) -> Result<i32, ANTLRError>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //println!("exec_atn_with_full_context");
        let full_ctx = true;
        let mut found_exact_ambig = false;
        let mut prev = s0;
        local.input().seek(self.start_index.get());
        let mut t = local.input().la(1);
        let mut predicted_alt;
        // local.upgrade_lock();
        loop {
            //            println!("full_ctx loop");

            let reach = self.compute_reach_set(&prev, t, full_ctx, local);
            prev = match reach {
                None => {
                    local.input().seek(self.start_index.get());
                    let alt = self
                        .get_syn_valid_or_sem_invalid_alt_that_finished_decision_entry_rule(
                            &prev, local,
                        );
                    if alt != INVALID_ALT {
                        return Ok(alt);
                    }
                    return Err(self.no_viable_alt(local, &prev, self.start_index.get()));
                }
                Some(x) => x,
            };

            let alt_sub_sets = get_conflicting_alt_subsets(&prev);
            prev.set_unique_alt(self.get_unique_alt(&prev));
            if prev.get_unique_alt() != INVALID_ALT {
                predicted_alt = prev.get_unique_alt();
                break;
            }
            if self.prediction_mode.get() != PredictionMode::LL_EXACT_AMBIG_DETECTION {
                predicted_alt = resolves_to_just_one_viable_alt(&alt_sub_sets);
                if predicted_alt != INVALID_ALT {
                    break;
                }
            } else if all_subsets_conflict(&alt_sub_sets) && all_subsets_equal(&alt_sub_sets) {
                found_exact_ambig = true;
                predicted_alt = get_single_viable_alt(&alt_sub_sets);
                break;
            }

            if t != TOKEN_EOF {
                local.input().consume();
                t = local.input().la(1);
            }
        }

        // local.downgrade_lock();
        let dfa = local.dfa_ref;
        if prev.get_unique_alt() != INVALID_ALT {
            self.report_context_sensitivity(
                dfa,
                predicted_alt,
                &prev,
                self.start_index.get(),
                local.input().index(),
                local.parser,
            );
            return Ok(predicted_alt);
        }
        self.report_ambiguity(
            dfa,
            self.start_index.get(),
            local.input().index(),
            found_exact_ambig,
            &prev.get_alts(),
            &prev,
            local.parser,
        );

        Ok(predicted_alt)
    }

    // ATNConfigSet is pretty big so should be boxed to move it cheaper
    fn compute_reach_set<'a, 'input, 'arena, TF, P>(
        &'a self,
        closure: &ATNConfigSet,
        t: i32,
        full_ctx: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> Option<ATNConfigSet>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        println!("in computeReachSet, starting closure: {:?}",closure);
        let mut intermediate = ATNConfigSet::new_base_atnconfig_set(full_ctx);

        let mut skipped_stop_states = Vec::<&ATNConfig>::new();

        for c in closure.get_items() {
            let state = &self.atn().states[c.get_state() as usize];
            if let ATNState::RuleStop(_) = state {
                assert!(c.get_context().unwrap().is_empty());
                if full_ctx || t == TOKEN_EOF {
                    skipped_stop_states.push(c);
                }
                continue;
            }

            for tr in state.get_transitions() {
                self.get_reachable_target(tr, t).map(|target| {
                    let added = c.cloned(&self.atn().states[target as usize]);
                    intermediate.add_cached(added, Some(local.merge_cache))
                });
            }
        }
        //        println!("intermediate {:?}",intermediate);

        let mut look_to_end_of_rule = false;
        let mut reach = if skipped_stop_states.is_empty()
            && t != TOKEN_EOF
            && (intermediate.length() == 1 || self.get_unique_alt(&intermediate) != INVALID_ALT)
        {
            look_to_end_of_rule = true;
            intermediate
        } else {
            let mut reach = ATNConfigSet::new_base_atnconfig_set(full_ctx);
            let mut closure_busy = HashSet::new();
            //            println!("calc reach {:?}",intermediate.length());

            for c in intermediate.configs {
                let treat_eofas_epsilon = t == TOKEN_EOF;
                self.closure(
                    c,
                    &mut reach,
                    &mut closure_busy,
                    false,
                    full_ctx,
                    treat_eofas_epsilon,
                    local,
                );
            }
            //            println!("calc reach {:?}",reach);
            reach
        };

        if t == TOKEN_EOF {
            reach = self.remove_all_configs_not_in_rule_stop_state(
                reach,
                look_to_end_of_rule,
                local.merge_cache,
            );
        }

        if !skipped_stop_states.is_empty()
            && (!full_ctx || !self.has_config_in_rule_stop_state(&reach))
        {
            for c in skipped_stop_states {
                reach.add_cached(c.clone(), Some(local.merge_cache));
            }
        }
        //        println!("result?");
        if reach.is_empty() {
            return None;
        }

        //        println!("result {:?}",&reach);
        Some(reach)
    }

    fn has_config_in_rule_stop_state(&self, configs: &ATNConfigSet) -> bool {
        configs.get_items().any(|c| {
            matches!(
                self.atn().states[c.get_state() as usize],
                ATNState::RuleStop(_)
            )
        })
    }

    fn all_configs_in_rule_stop_state(&self, configs: &ATNConfigSet) -> bool {
        configs.get_items().all(|c| {
            matches!(
                self.atn().states[c.get_state() as usize],
                ATNState::RuleStop(_)
            )
        })
    }

    fn remove_all_configs_not_in_rule_stop_state(
        &self,
        configs: ATNConfigSet,
        look_to_end_of_rule: bool,
        merge_cache: &mut MergeCache,
    ) -> ATNConfigSet {
        if self.all_configs_in_rule_stop_state(&configs) {
            return configs;
        }

        // can just remove instead of creating new instance because we own configs
        // it significantly differs from java version though
        let mut result = ATNConfigSet::new_base_atnconfig_set(configs.full_context());
        for c in configs.configs {
            let state = &self.atn().states[c.get_state() as usize];
            if matches!(state, ATNState::RuleStop(_)) {
                result.add_cached(c, Some(merge_cache));
                continue;
            }

            if look_to_end_of_rule && state.has_epsilon_only_transitions() {
                let next_tokens = self.atn().next_tokens(state);
                if next_tokens.contains(TOKEN_EPSILON) {
                    let end_of_rule_state =
                        self.atn().rule_to_stop_state[state.get_rule_index() as usize];
                    result.add_cached(
                        c.cloned(&self.atn().states[end_of_rule_state as usize]),
                        Some(merge_cache),
                    );
                }
            }
        }

        result
    }

    fn compute_start_state<'a, 'input, 'arena, TF, P>(
        &'a self,
        a: ATNStateRef,
        initial_ctx: Arc<PredictionContext>,
        full_ctx: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> ATNConfigSet
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        let initial_ctx = PredictionContext::prediction_context_from_rule_context(self.atn(),ctx);
        let mut configs = ATNConfigSet::new_base_atnconfig_set(full_ctx);
        //        println!("initial {:?}",initial_ctx);
        //        println!("initial state {:?}",a);

        let atn_states = &self.atn().states;
        for (i, tr) in atn_states[a as usize].get_transitions().iter().enumerate() {
            let target = &atn_states[tr.get_target() as usize];
            let c = ATNConfig::new(
                target.get_state_number(),
                (i + 1) as i32,
                Some(initial_ctx.clone()),
            );
            let mut closure_busy = HashSet::new();
            self.closure(
                c,
                &mut configs,
                &mut closure_busy,
                true,
                full_ctx,
                false,
                local,
            );
        }
        //        println!("start state {:?}",configs);

        configs
    }

    fn apply_precedence_filter<'a, 'input, 'arena, TF, P>(
        &'a self,
        configs: &ATNConfigSet,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> ATNConfigSet
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //println!("apply_precedence_filter");
        let mut states_from_alt1 = HashMap::new();
        let mut config_set = ATNConfigSet::new_base_atnconfig_set(configs.full_context());

        for config in configs.get_items() {
            if config.get_alt() != 1 {
                continue;
            }

            let updated_sem_ctx = config
                .semantic_context
                .eval_precedence(local.parser, local.outer_context());

            if let Some(updated_sem_ctx) = updated_sem_ctx.as_deref() {
                states_from_alt1.insert(config.get_state(), config.get_context());

                if *updated_sem_ctx != *config.semantic_context {
                    config_set.add_cached(
                        ATNConfig::new_with_semantic(
                            config.get_state(),
                            config.get_alt(),
                            config.get_context().cloned(),
                            Box::new(updated_sem_ctx.clone()),
                        ),
                        Some(local.merge_cache),
                    );
                } else {
                    config_set.add_cached(config.clone(), Some(local.merge_cache));
                }
            }
        }

        for config in configs.get_items() {
            if config.get_alt() == 1 {
                continue;
            }
            if !config.is_precedence_filter_suppressed() {
                if let Some(context) = states_from_alt1.get(&config.get_state()) {
                    if *context == config.get_context() {
                        continue;
                    }
                }
            }
            config_set.add(config.clone());
        }

        config_set
    }

    fn get_reachable_target(&self, trans: &Transition, ttype: i32) -> Option<ATNStateRef> {
        if trans.matches(ttype, 0, self.atn().max_token_type) {
            return Some(trans.get_target());
        }
        None
    }

    fn get_preds_for_ambig_alts(
        &self,
        ambig_alts: &BitSet,
        configs: &ATNConfigSet,
        nalts: usize,
    ) -> Option<Vec<SemanticContext>> {
        let mut alt_to_pred = Vec::with_capacity(nalts + 1);
        alt_to_pred.resize_with(nalts + 1, || None);
        for c in configs.configs.iter() {
            let alt = c.get_alt() as usize;
            if ambig_alts.contains(alt) {
                alt_to_pred[alt] = Some(SemanticContext::or(
                    alt_to_pred[alt].as_ref(),
                    Some(&*c.semantic_context),
                ));
            }
        }

        let alt_to_pred: Vec<SemanticContext> = alt_to_pred
            .into_iter()
            .map(|it| {
                if let Some(inner) = it {
                    inner
                } else {
                    SemanticContext::NONE
                }
            })
            .collect();

        let npred_alts = alt_to_pred
            .iter()
            .filter(|it| **it != SemanticContext::NONE)
            .count();

        if npred_alts == 0 {
            return None;
        }
        Some(alt_to_pred)
    }

    fn get_predicate_predictions(
        &self,
        ambig_alts: &BitSet,
        alt_to_pred: Vec<SemanticContext>,
    ) -> Vec<PredPrediction> {
        let mut pairs = vec![];
        let mut contains_predicate = false;
        for (i, pred) in alt_to_pred.into_iter().enumerate().skip(1) {
            if pred != SemanticContext::NONE {
                contains_predicate = true
            }

            if ambig_alts.contains(i) {
                pairs.push(PredPrediction {
                    alt: i as i32,
                    pred,
                })
            }
        }
        if !contains_predicate {
            return Vec::new();
        }

        pairs
    }

    fn get_syn_valid_or_sem_invalid_alt_that_finished_decision_entry_rule<
        'a,
        'input,
        'arena,
        TF,
        P,
    >(
        &'a self,
        configs: &ATNConfigSet,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> i32
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let (sem_valid_configs, sem_invalid_configs) =
            self.split_according_to_semantic_validity(configs, local);

        let alt = self.get_alt_that_finished_decision_entry_rule(&sem_valid_configs);
        if alt != INVALID_ALT {
            return alt;
        }

        if !sem_invalid_configs.is_empty() {
            let alt = self.get_alt_that_finished_decision_entry_rule(&sem_invalid_configs);
            if alt != INVALID_ALT {
                return alt;
            }
        }

        INVALID_ALT
    }

    fn split_according_to_semantic_validity<'a, 'input, 'arena, TF, P>(
        &'a self,
        configs: &ATNConfigSet,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> (ATNConfigSet, ATNConfigSet)
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let mut succeeded = ATNConfigSet::new_base_atnconfig_set(configs.full_context());
        let mut failed = ATNConfigSet::new_base_atnconfig_set(configs.full_context());
        for c in configs.get_items() {
            let clone = c.clone();
            if *c.semantic_context != SemanticContext::NONE {
                let predicate_eval_result = self.eval_predicate(
                    local,
                    &*c.semantic_context,
                    c.get_alt(),
                    configs.full_context(),
                );
                if predicate_eval_result {
                    succeeded.add(clone);
                } else {
                    failed.add(clone);
                }
            } else {
                succeeded.add(clone);
            }
        }
        (succeeded, failed)
    }

    fn get_alt_that_finished_decision_entry_rule(&self, configs: &ATNConfigSet) -> i32 {
        let mut alts = IntervalSet::new();
        for c in configs.get_items() {
            let has_empty_path = c.get_context().map(|x| x.has_empty_path()) == Some(true);
            let is_stop = matches!(
                self.atn().states[c.get_state() as usize],
                ATNState::RuleStop(_)
            );
            if c.get_reaches_into_outer_context() > 0 || (is_stop && has_empty_path) {
                alts.add_one(c.get_alt())
            }
        }

        alts.get_min().unwrap_or(INVALID_ALT)
    }

    fn eval_semantic_context<'a, 'input, 'arena, TF, P>(
        &'a self,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
        pred_predictions: &Vec<PredPrediction>,
        complete: bool,
    ) -> BitSet
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let mut predictions = BitSet::new();
        for pred in pred_predictions {
            if pred.pred == SemanticContext::NONE {
                predictions.insert(pred.alt as usize);

                if !complete {
                    break;
                }
                continue;
            }

            let full_ctx = false;
            let predicate_evaluation_result =
                self.eval_predicate(local, &pred.pred, pred.alt, full_ctx);

            if predicate_evaluation_result {
                predictions.insert(pred.alt as usize);
                if !complete {
                    break;
                }
            }
        }
        predictions
    }

    fn eval_predicate<'a, 'input, 'arena, TF, P>(
        &'a self,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
        pred: impl Borrow<SemanticContext>,
        _alt: i32,
        _full_ctx: bool,
    ) -> bool
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        pred.borrow().evaluate(local.parser, local.outer_context)
    }

    #[allow(clippy::too_many_arguments)]
    fn closure<'a, 'input, 'arena, TF, P>(
        &'a self,
        config: ATNConfig,
        configs: &mut ATNConfigSet,
        closure_busy: &mut HashSet<ATNConfig>,
        collect_predicates: bool,
        full_ctx: bool,
        treat_eofas_epsilon: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        println!("cl{}", config.get_state());
        let initial_depth = 0;
        //        local.merge_cache.clear();

        self.closure_checking_stop_state(
            config,
            configs,
            closure_busy,
            collect_predicates,
            full_ctx,
            initial_depth,
            treat_eofas_epsilon,
            local,
        );
        assert!(!full_ctx || !configs.get_dips_into_outer_context())
    }

    #[allow(clippy::too_many_arguments)]
    fn closure_checking_stop_state<'a, 'input, 'arena, TF, P>(
        &'a self,
        mut config: ATNConfig,
        configs: &mut ATNConfigSet,
        closure_busy: &mut HashSet<ATNConfig>,
        collect_predicates: bool,
        full_ctx: bool,
        depth: i32,
        treat_eofas_epsilon: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        println!("closure({:?})",config);
        if matches!(
            self.atn().states[config.get_state() as usize],
            ATNState::RuleStop(_)
        ) {
            if !config.get_context().unwrap().is_empty() {
                config.get_context().unwrap().run(|temp| {
                    if temp.get_return_state(temp.length() - 1)
                        == PREDICTION_CONTEXT_EMPTY_RETURN_STATE
                    {
                        if full_ctx {
                            let new_config = config.cloned_with_new_ctx(
                                &self.atn().states[config.get_state() as usize],
                                Some(EMPTY_PREDICTION_CONTEXT.clone()),
                            );
                            configs.add_cached(new_config, Some(local.merge_cache));
                        } else {
                            self.closure_work(
                                config.clone(),
                                configs,
                                closure_busy,
                                collect_predicates,
                                full_ctx,
                                depth,
                                treat_eofas_epsilon,
                                local,
                            )
                        }
                    }
                });
                let context = config.take_context();
                for i in 0..context.length() {
                    if context.get_return_state(i) == PREDICTION_CONTEXT_EMPTY_RETURN_STATE {
                        if i != context.length() - 1 {
                            panic!("EMPTY_RETURN_STATE is not last for some reason, please report error")
                        }
                        continue;
                    }
                    let return_state = context.get_return_state(i) as ATNStateRef;
                    //                    let new_ctx = context.take_parent(i).unwrap();
                    let new_ctx = context.get_parent(i).cloned();
                    let mut c = ATNConfig::new_with_semantic(
                        return_state,
                        config.get_alt(),
                        new_ctx,
                        config.semantic_context.clone(),
                    );
                    c.set_reaches_into_outer_context(config.get_reaches_into_outer_context());
                    assert!(depth > i32::MIN);
                    self.closure_checking_stop_state(
                        c,
                        configs,
                        closure_busy,
                        collect_predicates,
                        full_ctx,
                        depth - 1,
                        treat_eofas_epsilon,
                        local,
                    )
                }
                return;
            } else if full_ctx {
                configs.add_cached(config, Some(local.merge_cache));
                return;
            }
        }
        self.closure_work(
            config,
            configs,
            closure_busy,
            collect_predicates,
            full_ctx,
            depth,
            treat_eofas_epsilon,
            local,
        )
    }

    #[allow(clippy::too_many_arguments)]
    fn closure_work<'a, 'input, 'arena, TF, P>(
        &'a self,
        config: ATNConfig,
        configs: &mut ATNConfigSet,
        closure_busy: &mut HashSet<ATNConfig>,
        collect_predicates: bool,
        full_ctx: bool,
        depth: i32,
        treat_eofas_epsilon: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //println!("depth {}",depth);
        //        println!("closure_work started {:?}",config);
        let p = &self.atn().states[config.get_state() as usize];
        if !p.has_epsilon_only_transitions() {
            configs.add_cached(config.clone(), Some(local.merge_cache));
        }

        for (i, tr) in p.get_transitions().iter().enumerate() {
            if i == 0 && self.can_drop_loop_entry_edge_in_left_recursive_rule(&config) {
                continue;
            }

            let continue_collecting = !matches!(tr, Transition::Action(_)) && collect_predicates;
            let c = self.get_epsilon_target(
                &config,
                tr,
                continue_collecting,
                depth == 0,
                full_ctx,
                treat_eofas_epsilon,
                local,
            );
            if let Some(mut c) = c {
                let mut new_depth = depth;
                if matches!(
                    self.atn().states[config.get_state() as usize],
                    ATNState::RuleStop(_)
                ) {
                    assert!(!full_ctx);

                    let dfa = local.dfa_ref;
                    if dfa.is_precedence_dfa() {
                        let outermost_precedence_return = tr
                            .try_as::<EpsilonTransition>()
                            .unwrap()
                            .outermost_precedence_return;
                        let atn_start_state = &self.atn().states[dfa.atn_start_state as usize];
                        if outermost_precedence_return == atn_start_state.get_rule_index() {
                            c.set_precedence_filter_suppressed(true);
                        }
                    }

                    c.reaches_into_outer_context += 1;
                    if !closure_busy.insert(c.clone()) {
                        continue;
                    }
                    configs.set_dips_into_outer_context(true);
                    assert!(new_depth > i32::MIN);
                    new_depth -= 1;
                } else {
                    if !tr.is_epsilon() && !closure_busy.insert(c.clone()) {
                        continue;
                    }

                    if matches!(tr, Transition::Rule(_)) && new_depth >= 0 {
                        new_depth += 1
                    }
                }

                self.closure_checking_stop_state(
                    c,
                    configs,
                    closure_busy,
                    continue_collecting,
                    full_ctx,
                    new_depth,
                    treat_eofas_epsilon,
                    local,
                )
            };
        }
        //        println!("closure_work ended {:?}",config);
    }

    fn can_drop_loop_entry_edge_in_left_recursive_rule(&self, _config: &ATNConfig) -> bool {
        //        if std::env::var("TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT").ok()
        //            .and_then(|it|str::parse::<bool>(&it).ok()) == Some(true)
        //        { return false }

        let state = &self.atn().states[_config.get_state() as usize];

        if let ATNState::Decision(DecisionState {
            state: ATNDecisionState::StarLoopEntry { is_precedence, .. },
            ..
        }) = state
        {
            if !*is_precedence
                || _config.get_context().unwrap().is_empty()
                || _config.get_context().unwrap().has_empty_path()
            {
                return false;
            }
        } else {
            return false;
        }

        let pred_ctx = _config.get_context().unwrap();
        let ctx_len = pred_ctx.length();
        for i in 0..ctx_len {
            let return_state = &self.atn().states[pred_ctx.get_return_state(i) as usize];
            if return_state.get_rule_index() != state.get_rule_index() {
                return false;
            }
        }

        let decision_start_state = state.get_transitions()[0].get_target();
        let decision_start_state = &self.atn().states[decision_start_state as usize];
        let block_end_state_num = if let ATNState::Decision(DecisionState {
            state: ATNDecisionState::BlockStartState { end_state, .. },
            ..
        }) = decision_start_state
        {
            *end_state
        } else {
            unreachable!("cast error")
        };

        for i in 0..ctx_len {
            let return_state = &self.atn().states[pred_ctx.get_return_state(i) as usize];
            if return_state.get_transitions().len() != 1
                || !return_state.get_transitions()[0].is_epsilon()
            {
                //                println!("test1");
                return false;
            }
            let return_state_target =
                &self.atn().states[return_state.get_transitions()[0].get_target() as usize];
            if return_state.get_state_type_id() == ATNSTATE_BLOCK_END
                && ptr::eq(return_state_target, state)
            {
                continue;
            }
            if return_state.get_state_number() == block_end_state_num {
                continue;
            }
            if return_state_target.get_state_number() == block_end_state_num {
                continue;
            }

            if return_state_target.get_state_type_id() == ATNSTATE_BLOCK_END
                && return_state_target.get_transitions().len() == 1
                && return_state_target.get_transitions()[0].is_epsilon()
                && return_state_target.get_transitions()[0].get_target() == state.get_state_number()
            {
                continue;
            }
            //            println!("test2");
            return false;
        }
        //        println!("dropping on state {} ", state.get_state_number());

        true
    }
    //
    //    fn get_rule_name(&self, index: i32) -> String { unimplemented!() }

    #[allow(clippy::too_many_arguments)]
    fn get_epsilon_target<'a, 'input, 'arena, TF, P>(
        &'a self,
        config: &ATNConfig,
        t: &Transition,
        collect_predicates: bool,
        in_context: bool,
        full_ctx: bool,
        treat_eofas_epsilon: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> Option<ATNConfig>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        match t {
            Transition::Epsilon(_) => {
                Some(config.cloned(&self.atn().states[t.get_target() as usize]))
            }
            Transition::Rule(_) => {
                Some(self.rule_transition(config, t.try_as::<RuleTransition>().unwrap()))
            }
            Transition::Predicate(_) => self.pred_transition(
                config,
                t.try_as::<PredicateTransition>().unwrap(),
                collect_predicates,
                in_context,
                full_ctx,
                local,
            ),
            Transition::Action(_) => {
                Some(self.action_transition(config, t.try_as::<ActionTransition>().unwrap()))
            }
            Transition::PrecedencePredicate(_) => self.precedence_transition(
                config,
                t.try_as::<PrecedencePredicateTransition>().unwrap(),
                collect_predicates,
                in_context,
                full_ctx,
                local,
            ),
            Transition::Atom(_) | Transition::Set(_) | Transition::Range(_) => {
                if treat_eofas_epsilon && t.matches(TOKEN_EOF, 0, 1) {
                    Some(config.cloned(&self.atn().states[t.get_target() as usize]))
                } else {
                    None
                }
            }
            Transition::NotSet(_) | Transition::Wildcard(_) => None,
        }
    }

    fn action_transition(&self, config: &ATNConfig, t: &ActionTransition) -> ATNConfig {
        config.cloned(&self.atn().states[t.target as usize])
    }

    fn precedence_transition<'a, 'input, 'arena, TF, P>(
        &'a self,
        config: &ATNConfig,
        pt: &PrecedencePredicateTransition,
        collect_predicates: bool,
        in_context: bool,
        full_ctx: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> Option<ATNConfig>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let target = &self.atn().states[pt.target as usize];
        if collect_predicates && in_context {
            if full_ctx {
                let curr_pos = local.input().index();
                local.input().seek(self.start_index.get());
                let prec_succeeds = self.eval_predicate(
                    local,
                    pt.get_predicate().unwrap(),
                    config.get_alt(),
                    full_ctx,
                );
                local.input().seek(curr_pos);
                if prec_succeeds {
                    return Some(config.cloned(target));
                }
            } else {
                let new_sem_ctx =
                    SemanticContext::and(Some(&*config.semantic_context), pt.get_predicate());
                return Some(config.cloned_with_new_semantic(target, Box::new(new_sem_ctx)));
            }
        } else {
            return Some(config.cloned(target));
        }

        None
    }

    fn pred_transition<'a, 'input, 'arena, TF, P>(
        &'a self,
        config: &ATNConfig,
        pt: &PredicateTransition,
        collect_predicates: bool,
        in_context: bool,
        full_ctx: bool,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
    ) -> Option<ATNConfig>
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        #![allow(clippy::nonminimal_bool)]
        let target = &self.atn().states[pt.target as usize];
        if collect_predicates && (!pt.is_ctx_dependent || (pt.is_ctx_dependent && in_context)) {
            if full_ctx {
                let curr_pos = local.input().index();
                local.input().seek(self.start_index.get());
                let prec_succeeds = self.eval_predicate(
                    local,
                    pt.get_predicate().unwrap(),
                    config.get_alt(),
                    full_ctx,
                );
                local.input().seek(curr_pos);
                if prec_succeeds {
                    return Some(config.cloned(target));
                }
            } else {
                let new_sem_ctx =
                    SemanticContext::and(Some(&*config.semantic_context), pt.get_predicate());
                return Some(config.cloned_with_new_semantic(target, Box::new(new_sem_ctx)));
            }
        } else {
            return Some(config.cloned(target));
        }

        None
    }

    fn rule_transition(&self, config: &ATNConfig, t: &RuleTransition) -> ATNConfig {
        assert!(config.get_context().is_some());
        let new_ctx =
            PredictionContext::new_singleton(config.get_context().cloned(), t.follow_state);
        config.cloned_with_new_ctx(&self.atn().states[t.target as usize], Some(new_ctx.into()))
    }

    fn get_conflicting_alts(&self, configs: &ATNConfigSet) -> BitSet {
        let altsets = get_conflicting_alt_subsets(configs);
        get_alts(&altsets)
    }

    //todo can return Cow
    fn get_conflicting_alts_or_unique_alt(&self, configs: &ATNConfigSet) -> BitSet {
        if configs.get_unique_alt() != INVALID_ALT {
            BitSet::new().modify_with(|it| {
                it.insert(configs.get_unique_alt() as usize);
            })
        } else {
            configs.conflicting_alts.clone()
        }
    }
    //
    //    fn get_token_name(&self, t: i32) -> String { unimplemented!() }
    //
    //    fn get_lookahead_name(&self, input: TokenStream) -> String { unimplemented!() }
    //
    //    fn dump_dead_end_configs(&self, nvae: * NoViableAltError) { unimplemented!() }
    //
    fn no_viable_alt<'a, 'input, 'arena, TF, P>(
        &'a self,
        local: &mut Local<'a, 'input, 'arena, TF, P>,
        _configs: &ATNConfigSet,
        start_index: isize,
    ) -> ANTLRError
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let start_token = local.parser.get_input_stream().get(start_index);
        let start_token = OwningToken::from(start_token as &dyn Token);
        let offending_token = local.input().lt(1).unwrap();
        let offending_token = OwningToken::from(offending_token as &dyn Token);
        ANTLRError::no_alt_full(local.parser, start_token, offending_token)
    }

    fn get_unique_alt(&self, configs: &ATNConfigSet) -> i32 {
        let mut alt = INVALID_ALT;
        for c in configs.get_items() {
            if alt == INVALID_ALT {
                alt = c.get_alt()
            } else if c.get_alt() != alt {
                return INVALID_ALT;
            }
        }

        alt
    }

    fn add_dfaedge<'dfa>(
        &self,
        from: &'dfa DFAState<'dfa>,
        t: i32,
        to: &'dfa DFAState<'dfa>,
    ) -> &'dfa DFAState<'dfa> {
        if t < -1 || t > self.atn().max_token_type {
            return to;
        }
        from.set_edge((t + 1) as usize, to);

        to
    }

    fn add_dfastate<'dfa>(&self, dfa: &'dfa DFA, state: ProposedDFAState) -> &'dfa DFAState<'dfa> {
        dfa.add_state(state, self)
    }

    fn report_attempting_full_context<'input, 'arena, TF, P>(
        &self,
        dfa: &DFA,
        conflicting_alts: &BitSet,
        configs: &ATNConfigSet,
        start_index: isize,
        stop_index: isize,
        parser: &mut P,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        //        let ambig_index = parser.get_current_token().get_token_index();
        parser
            .get_error_lister_dispatch()
            .report_attempting_full_context(
                parser,
                dfa,
                start_index,
                stop_index,
                conflicting_alts,
                configs,
            )
    }

    fn report_context_sensitivity<'input, 'arena, TF, P>(
        &self,
        dfa: &DFA,
        prediction: i32,
        configs: &ATNConfigSet,
        start_index: isize,
        stop_index: isize,
        parser: &mut P,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        parser
            .get_error_lister_dispatch()
            .report_context_sensitivity(parser, dfa, start_index, stop_index, prediction, configs)
    }

    #[allow(clippy::too_many_arguments)]
    fn report_ambiguity<'input, 'arena, TF, P>(
        &self,
        dfa: &DFA,
        start_index: isize,
        stop_index: isize,
        exact: bool,
        ambig_alts: &BitSet,
        configs: &ATNConfigSet,
        parser: &mut P,
    ) where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        parser.get_error_lister_dispatch().report_ambiguity(
            parser,
            dfa,
            start_index,
            stop_index,
            exact,
            ambig_alts,
            configs,
        )
    }
}

impl IATNSimulator for ParserATNSimulator {
    fn shared_context_cache(&self) -> &PredictionContextCache {
        self.base.shared_context_cache()
    }

    fn atn(&self) -> &ATN {
        self.base.atn()
    }

    fn decision_to_dfa(&self) -> &Vec<DFA> {
        self.base.decision_to_dfa()
    }
}
