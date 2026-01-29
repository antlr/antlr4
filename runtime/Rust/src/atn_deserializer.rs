use std::slice::Iter;

use crate::atn::ATN;
use crate::atn_deserialization_options::ATNDeserializationOptions;
use crate::atn_state::ATNBlockStart;
use crate::atn_state::ATNDecisionState;
use crate::atn_state::ATNState;
use crate::atn_state::BaseATNState;
use crate::atn_state::*;
use crate::atn_type::ATNType;
use crate::int_stream::EOF;
use crate::interval_set::IntervalSet;
use crate::lexer_action::LexerAction::*;
use crate::lexer_action::*;
use crate::transition::Transition;
use crate::transition::*;

/*
static BASE_SERIALIZED_UUID: LazyLock<Uuid> =
    LazyLock::new(|| Uuid::from_str("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3").unwrap());
static ADDED_PRECEDENCE_TRANSITIONS: LazyLock<Uuid> =
    LazyLock::new(|| Uuid::from_str("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61").unwrap());
static ADDED_LEXER_ACTIONS: LazyLock<Uuid> =
    LazyLock::new(|| Uuid::from_str("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E").unwrap());
static ADDED_UNICODE_SMP: LazyLock<Uuid> =
    LazyLock::new(|| Uuid::from_str("59627784-3BE5-417A-B9EB-8131A7286089").unwrap());
static SUPPORTED_UUIDS: LazyLock<Vec<Uuid>> = LazyLock::new(|| {
    vec![
        *BASE_SERIALIZED_UUID,
        *ADDED_PRECEDENCE_TRANSITIONS,
        *ADDED_LEXER_ACTIONS,
        *ADDED_UNICODE_SMP,
    ]
});
*/

const SERIALIZED_VERSION: i32 = 4;

#[derive(Debug)]
pub struct ATNDeserializer {
    deserialization_options: ATNDeserializationOptions,
}

impl ATNDeserializer {
    pub fn new(options: Option<ATNDeserializationOptions>) -> ATNDeserializer {
        ATNDeserializer {
            deserialization_options: options.unwrap_or_default(),
        }
    }

    pub fn deserialize(&self, data: &mut Iter<i32>) -> ATN {
        self.check_version(*data.next().unwrap());

        let mut atn = self.read_atn(data);

        self.read_states(&mut atn, data);
        self.read_rules(&mut atn, data);
        self.read_modes(&mut atn, data);

        let sets = self.read_sets(&mut atn, data);

        self.read_edges(&mut atn, data, &sets);
        self.read_decisions(&mut atn, data);
        if atn.grammar_type == ATNType::Lexer {
            self.read_lexer_actions(&mut atn, data);
        }
        self.mark_precedence_decisions(&mut atn, data);
        if self.deserialization_options.is_verify() {
            self.verify_atn(&mut atn, data);
        }
        //      TODO parser
        //        if a.deserializationOptions.generateRuleBypassTransitions && atn.grammarType == ATNTypeParser {
        //            a.generateRuleBypassTransitions(atn)
        //            a.verifyATN(atn)
        //        }

        atn
    }

    // fn reset(&self, _data: Vec<u8>) { unimplemented!() }

    fn check_version(&self, version: i32) {
        if version != self::SERIALIZED_VERSION {
            panic!(
                "Could not deserialize ATN with version {} (expected {})",
                version, SERIALIZED_VERSION
            );
        }
    }

    fn read_atn(&self, data: &mut Iter<i32>) -> ATN {
        ATN::new_atn(
            match data.next() {
                Some(0) => ATNType::Lexer,
                Some(1) => ATNType::Parser,
                _ => panic!("invalid ATN type"),
            },
            *data.next().unwrap(),
        )
    }

    fn read_states(&self, atn: &mut ATN, data: &mut Iter<i32>) {
        //        let loop_back_states = Vec::<(BaseATNState,i32)>::new();
        //        let end_states = Vec::<(BaseATNState,i32)>::new();
        let states_count = *data.next().unwrap();
        atn.alloc_states(states_count as usize);
        for i in 0..states_count {
            let state_type = *data.next().unwrap();
            if state_type == ATNSTATE_INVALID_STATE_NUMBER {
                atn.add_state(self.state_factory(ATNSTATE_INVALID_TYPE, -1, i));
                panic!("why invalid state serialized?");
            }

            let mut rule_index = *data.next().unwrap();
            if rule_index == 0xFFFF {
                rule_index = -1;
            }
            let mut state = self.state_factory(state_type, rule_index, i);

            match state {
                ATNState::Decision(DecisionState {
                    state:
                        ATNDecisionState::BlockStartState {
                            ref mut end_state, ..
                        },
                    ..
                }) => *end_state = atn.make_state_ref(*data.next().unwrap()),
                ATNState::LoopEnd(LoopEndState {
                    ref mut loop_back_state,
                    ..
                }) => *loop_back_state = atn.make_state_ref(*data.next().unwrap()),
                _ => (),
            }
            atn.add_state(state);
        }

        let num_non_greedy = *data.next().unwrap();
        //println!("num_non_greedy {}", num_non_greedy);
        for _ in 0..num_non_greedy {
            let st = *data.next().unwrap();
            if let ATNState::Decision(DecisionState {
                ref mut nongreedy, ..
            }) = atn.get_state_mut(st)
            {
                *nongreedy = true
            }
        }

        //if (supportsPrecedencePredicates)
        if true {
            let num_precedence_states = *data.next().unwrap();
            for _ in 0..num_precedence_states {
                let st = *data.next().unwrap();
                if let ATNState::RuleStart(RuleStartState {
                    ref mut is_left_recursive,
                    ..
                }) = atn.get_state_mut(st)
                {
                    *is_left_recursive = true
                }
            }
        }
    }

    fn read_rules(&self, atn: &mut ATN, data: &mut Iter<i32>) {
        let nrules = *data.next().unwrap() as usize;
        //        if atn.grammar_type == ATNType::LEXER {
        //            atn.rule_to_token_type.resize(nrules, 0)
        //        }

        atn.rule_to_start_state
            .resize(nrules, ATNStateRef::invalid());
        for i in 0..nrules {
            atn.rule_to_start_state[i] = atn.make_state_ref(*data.next().unwrap());
            if atn.grammar_type == ATNType::Lexer {
                let token_type = *data.next().unwrap();

                atn.rule_to_token_type.push(token_type);
            }
        }
        //println!("rule_to_token_type {:?}", atn.rule_to_token_type);
        //println!("rule_to_start_state {:?}", atn.rule_to_start_state);

        atn.rule_to_stop_state
            .resize(nrules, ATNStateRef::invalid());
        for i in 0..atn.states_count() {
            let state = atn.get_state(i as i32);
            if let ATNState::RuleStop(_) = state {
                let rule_index = state.get_rule_index() as usize;
                atn.rule_to_stop_state[rule_index] = atn.make_state_ref(i as i32);
                unsafe {
                    if let ATNState::RuleStart(RuleStartState {
                        ref mut stop_state, ..
                    }) = atn.rule_to_start_state[rule_index].as_mut()
                    {
                        *stop_state = atn.make_state_ref(i as i32)
                    }
                }
            }
        }
    }

    fn read_modes(&self, atn: &mut ATN, data: &mut Iter<i32>) {
        let nmodes = *data.next().unwrap();
        for _i in 0..nmodes {
            atn.mode_to_start_state
                .push(atn.make_state_ref(*data.next().unwrap()));
        }
    }

    fn read_sets(&self, _atn: &mut ATN, data: &mut Iter<i32>) -> Vec<IntervalSet> {
        let nsets = *data.next().unwrap();
        let mut sets = Vec::new();
        for _i in 0..nsets {
            let intervals = *data.next().unwrap();

            let mut set = IntervalSet::new();

            // check if contains eof
            if *data.next().unwrap() != 0 {
                set.add_one(-1)
            }

            for _ in 0..intervals {
                set.add_range(*data.next().unwrap(), *data.next().unwrap());
            }
            sets.push(set);
        }

        sets
    }

    fn read_edges(&self, atn: &mut ATN, data: &mut Iter<i32>, sets: &[IntervalSet]) {
        let nedges = *data.next().unwrap();

        for _i in 0..nedges {
            let src = *data.next().unwrap();
            let trg = *data.next().unwrap();
            let ttype = *data.next().unwrap();
            let arg1 = *data.next().unwrap();
            let arg2 = *data.next().unwrap();
            let arg3 = *data.next().unwrap();

            let transition = self.edge_factory(
                atn,
                ttype,
                atn.make_state_ref(src),
                atn.make_state_ref(trg),
                arg1,
                arg2,
                arg3,
                sets,
            );

            atn.get_state_mut(src).add_transition(transition);
        }

        for i in atn.iter_states() {
            for tr in i.get_transitions() {
                match tr {
                    Transition::Rule(tr) => {
                        //                        println!("TRANSITION_RULE");
                        let target = tr.get_target();

                        let outermost_prec_return = if let ATNState::RuleStart(RuleStartState {
                            is_left_recursive: true,
                            ..
                        }) =
                            *atn.rule_to_start_state[target.get_rule_index() as usize]
                        {
                            if tr.precedence == 0 {
                                target.get_rule_index()
                            } else {
                                -1
                            }
                        } else {
                            -1
                        };

                        let return_tr = EpsilonTransition {
                            target: tr.follow_state,
                            outermost_precedence_return: outermost_prec_return,
                        };

                        unsafe {
                            atn.rule_to_stop_state[target.get_rule_index() as usize]
                                .as_mut()
                                .add_transition(return_tr.into());
                        }
                    }
                    _ => continue,
                }
            }
        }
        // new_tr
        //     .drain(..)
        //     .for_each(|(state, tr)| atn.states[state as usize].add_transition(tr));

        // for i in 0..atn.states.len() {
        //     let atn_state = atn.states.get(i).unwrap();
        //     match atn_state {
        //         ATNState::Decision(DecisionState {
        //             state:
        //                 ATNDecisionState::BlockStartState {
        //                     end_state: _,
        //                     en: _,
        //                 },
        //             ..
        //         }) => {

        //             //                    if *end_state == 0 { panic!("invalid state")}
        //             // looks like it is never used during recognition
        //             // todo missed part
        //         }
        //         //                ATNStateType::DecisionState {state:ATNDecisionState::PlusLoopBack,..} =>{
        //         //                    for tr in atn_state.get_transitions(){
        //         //                        if let ATNStateType::DecisionState {
        //         //                                        state:ATNDecisionState::BlockStartState {
        //         //                                            en:ATNBlockStart::PlusBlockStart(loopBack),..},..}
        //         //                        = atn.states.get_mut(tr.get_target()).unwrap().get_state_type_mut(){
        //         //                            *loopBack = i;
        //         //
        //         //                        }
        //         //                    }
        //         //                }
        //         _x => { /*println!("{:?}",x);*/ }
        //     }
        // }
    }

    fn read_decisions(&self, atn: &mut ATN, _data: &mut Iter<i32>) {
        let ndecisions = *_data.next().unwrap();
        for i in 0..ndecisions {
            let s = atn.make_state_ref(*_data.next().unwrap());
            atn.decision_to_state.push(s);
            unsafe {
                if let ATNState::Decision(DecisionState {
                    ref mut decision, ..
                }) = s.as_mut()
                {
                    *decision = i
                }
            }
        }
    }

    fn read_lexer_actions(&self, atn: &mut ATN, _data: &mut Iter<i32>) {
        //lexer actions are always supported here
        let nactions = *_data.next().unwrap();

        for _i in 0..nactions {
            let action_type = *_data.next().unwrap();

            let data1 = *_data.next().unwrap();
            let data2 = *_data.next().unwrap();

            let lexer_action = self.lexer_action_factory(action_type, data1, data2);

            atn.lexer_actions.push(lexer_action);
        }
    }

    fn mark_precedence_decisions(&self, _atn: &mut ATN, _data: &mut Iter<i32>) {
        let mut precedence_states = Vec::new();
        for state in _atn.iter_states() {
            if let ATNState::Decision(DecisionState {
                state: ATNDecisionState::StarLoopEntry { .. },
                ..
            }) = state
            {
                if let ATNState::RuleStart(RuleStartState {
                    is_left_recursive: true,
                    ..
                }) = *_atn.rule_to_start_state[state.get_rule_index() as usize]
                {
                    let maybe_loop_end =
                        state.get_transitions().iter().last().unwrap().get_target();
                    if let ATNState::LoopEnd(_) = *maybe_loop_end {
                        if maybe_loop_end.has_epsilon_only_transitions() {
                            if let ATNState::RuleStop(_) =
                                *maybe_loop_end.get_transitions()[0].get_target()
                            {
                                precedence_states.push(state.get_state_number())
                            }
                        }
                    }
                }
            }
        }
        for st in precedence_states.into_iter() {
            if let ATNState::Decision(DecisionState {
                state:
                    ATNDecisionState::StarLoopEntry {
                        loop_back_state: _,
                        ref mut is_precedence,
                    },
                ..
            }) = _atn.get_state_mut(st)
            {
                *is_precedence = true
            }
        }
    }

    fn verify_atn(&self, _atn: &mut ATN, _data: &mut Iter<i32>) {
        //TODO
    }

    // fn check_condition(&self, _condition: bool, _message: String) { unimplemented!() }

    #[allow(clippy::too_many_arguments)]
    fn edge_factory(
        &self,
        atn: &ATN,
        type_index: i32,
        _src: ATNStateRef,
        target: ATNStateRef,
        arg1: i32,
        arg2: i32,
        arg3: i32,
        sets: &[IntervalSet],
    ) -> Transition {
        //        //        let target = atn.states.get
        //        let mut base = BaseTransition {
        //            target: trg,
        //            //            is_epsilon: false,
        //            //            label: 0,
        //            interval_set: IntervalSet::new_interval_set(),
        //        };

        match type_index {
            TRANSITION_EPSILON => EpsilonTransition {
                target,
                outermost_precedence_return: 0,
            }
            .into(),
            TRANSITION_RANGE => RangeTransition::new(
                target,
                if arg3 != 0 {
                    super::token::TOKEN_EOF
                } else {
                    arg1
                },
                arg2,
            )
            .into(),
            TRANSITION_RULE => {
                //                base.set_target(arg1 as usize);
                RuleTransition {
                    target: atn.make_state_ref(arg1),
                    follow_state: target,
                    rule_index: arg2,
                    precedence: arg3,
                }
            }
            .into(),
            TRANSITION_PREDICATE => PredicateTransition {
                target,
                is_ctx_dependent: arg3 != 0,
                rule_index: arg1,
                pred_index: arg2,
            }
            .into(),
            TRANSITION_ATOM => {
                AtomTransition::new(target, if arg3 != 0 { EOF } else { arg1 }).into()
            }
            TRANSITION_ACTION => ActionTransition {
                target,
                is_ctx_dependent: arg3 != 0,
                rule_index: arg1,
                action_index: arg2,
                pred_index: 0,
            }
            .into(),
            TRANSITION_SET => SetTransition {
                target,
                set: sets[arg1 as usize].clone(),
            }
            .into(),
            TRANSITION_NOTSET => NotSetTransition {
                target,
                set: sets[arg1 as usize].clone(),
            }
            .into(),
            TRANSITION_WILDCARD => WildcardTransition { target }.into(),
            TRANSITION_PRECEDENCE => PrecedencePredicateTransition {
                target,
                precedence: arg1,
            }
            .into(),
            _ => panic!("invalid transition type"),
        }
    }

    fn state_factory(&self, type_index: i32, rule_index: i32, state_number: i32) -> ATNState {
        let base = BaseATNState::new(state_number, rule_index, type_index);
        match type_index {
            ATNSTATE_INVALID_TYPE => ATNState::Invalid(base),
            ATNSTATE_BASIC => BasicState::new(base).into(),
            ATNSTATE_RULE_START => RuleStartState::new(base, ATNStateRef::invalid(), false).into(),
            ATNSTATE_BLOCK_START => DecisionState::new(
                base,
                -1,
                false,
                ATNDecisionState::BlockStartState {
                    end_state: ATNStateRef::invalid(),
                    en: ATNBlockStart::BasicBlockStart,
                },
            )
            .into(),
            ATNSTATE_PLUS_BLOCK_START => DecisionState::new(
                base,
                -1,
                false,
                ATNDecisionState::BlockStartState {
                    end_state: ATNStateRef::invalid(),
                    en: ATNBlockStart::PlusBlockStart(ATNStateRef::invalid()),
                },
            )
            .into(),
            ATNSTATE_STAR_BLOCK_START => DecisionState::new(
                base,
                -1,
                false,
                ATNDecisionState::BlockStartState {
                    end_state: ATNStateRef::invalid(),
                    en: ATNBlockStart::StarBlockStart,
                },
            )
            .into(),
            ATNSTATE_TOKEN_START => {
                DecisionState::new(base, -1, false, ATNDecisionState::TokenStartState).into()
            }
            ATNSTATE_RULE_STOP => RuleStopState::new(base).into(),
            ATNSTATE_BLOCK_END => BlockEndState::new(base, ATNStateRef::invalid()).into(),
            ATNSTATE_STAR_LOOP_BACK => StarLoopbackState::new(base).into(),
            ATNSTATE_STAR_LOOP_ENTRY => DecisionState::new(
                base,
                -1,
                false,
                ATNDecisionState::StarLoopEntry {
                    loop_back_state: ATNStateRef::invalid(),
                    is_precedence: false,
                },
            )
            .into(),
            ATNSTATE_PLUS_LOOP_BACK => {
                DecisionState::new(base, -1, false, ATNDecisionState::PlusLoopBack).into()
            }
            ATNSTATE_LOOP_END => LoopEndState::new(base, ATNStateRef::invalid()).into(),
            t => panic!("invalid ATN state type {}", t),
        }
    }

    fn lexer_action_factory(&self, action_type: i32, data1: i32, data2: i32) -> LexerAction {
        match action_type {
            LEXER_ACTION_TYPE_CHANNEL => LexerChannelAction(data1),
            LEXER_ACTION_TYPE_CUSTOM => LexerCustomAction {
                rule_index: data1,
                action_index: data2,
            },
            LEXER_ACTION_TYPE_MODE => LexerModeAction(data1),
            LEXER_ACTION_TYPE_MORE => LexerMoreAction,
            LEXER_ACTION_TYPE_POP_MODE => LexerPopModeAction,
            LEXER_ACTION_TYPE_PUSH_MODE => LexerPushModeAction(data1),
            LEXER_ACTION_TYPE_SKIP => LexerSkipAction,
            LEXER_ACTION_TYPE_TYPE => LexerTypeAction(data1),
            _ => panic!("invalid action type {}", action_type),
        }
    }
}
