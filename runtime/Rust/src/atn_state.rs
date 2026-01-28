use std::fmt::Debug;
use std::ops::{Deref, DerefMut};
use std::sync::OnceLock;

use crate::interval_set::IntervalSet;
use crate::transition::Transition;

pub(crate) const ATNSTATE_INVALID_TYPE: i32 = 0;
pub(crate) const ATNSTATE_BASIC: i32 = 1;
pub(crate) const ATNSTATE_RULE_START: i32 = 2;
pub(crate) const ATNSTATE_BLOCK_START: i32 = 3;
pub(crate) const ATNSTATE_PLUS_BLOCK_START: i32 = 4;
pub(crate) const ATNSTATE_STAR_BLOCK_START: i32 = 5;
pub(crate) const ATNSTATE_TOKEN_START: i32 = 6;
pub(crate) const ATNSTATE_RULE_STOP: i32 = 7;
pub(crate) const ATNSTATE_BLOCK_END: i32 = 8;
pub(crate) const ATNSTATE_STAR_LOOP_BACK: i32 = 9;
pub(crate) const ATNSTATE_STAR_LOOP_ENTRY: i32 = 10;
pub(crate) const ATNSTATE_PLUS_LOOP_BACK: i32 = 11;
pub(crate) const ATNSTATE_LOOP_END: i32 = 12;
pub(crate) const ATNSTATE_INVALID_STATE_NUMBER: i32 = -1;

pub enum ATNState {
    RuleStart(RuleStartState),
    RuleStop(RuleStopState),
    BlockEnd(BlockEndState),
    LoopEnd(LoopEndState),
    StarLoopback(StarLoopbackState),
    Basic(BasicState),
    Decision(DecisionState),
    Invalid(BaseATNState),
}

#[doc(hidden)]
#[derive(Debug, Eq, PartialEq)]
pub enum ATNDecisionState {
    StarLoopEntry {
        loop_back_state: ATNStateRef,
        is_precedence: bool,
    },
    TokenStartState,
    PlusLoopBack,
    BlockStartState {
        end_state: ATNStateRef,
        en: ATNBlockStart,
    },
}

#[doc(hidden)]
#[derive(Debug, Eq, PartialEq)]
pub enum ATNBlockStart {
    BasicBlockStart,
    StarBlockStart,
    PlusBlockStart(ATNStateRef),
}

pub type ATNStateRef = i32;

// todo no need for trait here, it is too slow for hot code
impl ATNState {
    pub fn has_epsilon_only_transitions(&self) -> bool {
        match self {
            ATNState::RuleStart(s) => s.has_epsilon_only_transitions(),
            ATNState::RuleStop(s) => s.has_epsilon_only_transitions(),
            ATNState::BlockEnd(s) => s.has_epsilon_only_transitions(),
            ATNState::LoopEnd(s) => s.has_epsilon_only_transitions(),
            ATNState::StarLoopback(s) => s.has_epsilon_only_transitions(),
            ATNState::Basic(s) => s.has_epsilon_only_transitions(),
            ATNState::Decision(s) => s.has_epsilon_only_transitions(),
            ATNState::Invalid(s) => s.has_epsilon_only_transitions(),
        }
    }

    pub fn get_rule_index(&self) -> i32 {
        match self {
            ATNState::RuleStart(s) => s.get_rule_index(),
            ATNState::RuleStop(s) => s.get_rule_index(),
            ATNState::BlockEnd(s) => s.get_rule_index(),
            ATNState::LoopEnd(s) => s.get_rule_index(),
            ATNState::StarLoopback(s) => s.get_rule_index(),
            ATNState::Basic(s) => s.get_rule_index(),
            ATNState::Decision(s) => s.get_rule_index(),
            ATNState::Invalid(s) => s.get_rule_index(),
        }
    }

    pub fn get_next_tokens_within_rule(&self) -> &OnceLock<IntervalSet> {
        match self {
            ATNState::RuleStart(s) => s.get_next_tokens_within_rule(),
            ATNState::RuleStop(s) => s.get_next_tokens_within_rule(),
            ATNState::BlockEnd(s) => s.get_next_tokens_within_rule(),
            ATNState::LoopEnd(s) => s.get_next_tokens_within_rule(),
            ATNState::StarLoopback(s) => s.get_next_tokens_within_rule(),
            ATNState::Basic(s) => s.get_next_tokens_within_rule(),
            ATNState::Decision(s) => s.get_next_tokens_within_rule(),
            ATNState::Invalid(s) => s.get_next_tokens_within_rule(),
        }
    }

    pub fn get_state_type_id(&self) -> i32 {
        match self {
            ATNState::RuleStart(s) => s.get_state_type_id(),
            ATNState::RuleStop(s) => s.get_state_type_id(),
            ATNState::BlockEnd(s) => s.get_state_type_id(),
            ATNState::LoopEnd(s) => s.get_state_type_id(),
            ATNState::StarLoopback(s) => s.get_state_type_id(),
            ATNState::Basic(s) => s.get_state_type_id(),
            ATNState::Decision(s) => s.get_state_type_id(),
            ATNState::Invalid(s) => s.get_state_type_id(),
        }
    }

    pub fn get_state_number(&self) -> i32 {
        match self {
            ATNState::RuleStart(s) => s.get_state_number(),
            ATNState::RuleStop(s) => s.get_state_number(),
            ATNState::BlockEnd(s) => s.get_state_number(),
            ATNState::LoopEnd(s) => s.get_state_number(),
            ATNState::StarLoopback(s) => s.get_state_number(),
            ATNState::Basic(s) => s.get_state_number(),
            ATNState::Decision(s) => s.get_state_number(),
            ATNState::Invalid(s) => s.get_state_number(),
        }
    }

    pub fn get_transitions(&self) -> &Vec<Transition> {
        match self {
            ATNState::RuleStart(s) => s.get_transitions(),
            ATNState::RuleStop(s) => s.get_transitions(),
            ATNState::BlockEnd(s) => s.get_transitions(),
            ATNState::LoopEnd(s) => s.get_transitions(),
            ATNState::StarLoopback(s) => s.get_transitions(),
            ATNState::Basic(s) => s.get_transitions(),
            ATNState::Decision(s) => s.get_transitions(),
            ATNState::Invalid(s) => s.get_transitions(),
        }
    }

    pub fn add_transition(&mut self, trans: Transition) {
        match self {
            ATNState::RuleStart(s) => s.add_transition(trans),
            ATNState::RuleStop(s) => s.add_transition(trans),
            ATNState::BlockEnd(s) => s.add_transition(trans),
            ATNState::LoopEnd(s) => s.add_transition(trans),
            ATNState::StarLoopback(s) => s.add_transition(trans),
            ATNState::Basic(s) => s.add_transition(trans),
            ATNState::Decision(s) => s.add_transition(trans),
            ATNState::Invalid(s) => s.add_transition(trans),
        }
    }
}

#[derive(Debug)]
pub struct BaseATNState {
    next_tokens_within_rule: OnceLock<IntervalSet>,

    epsilon_only_transitions: bool,

    pub rule_index: i32,

    pub state_number: i32,

    pub state_type_id: i32,

    transitions: Vec<Transition>,
}

impl BaseATNState {
    pub fn new(state_number: i32, rule_index: i32, state_type_id: i32) -> Self {
        BaseATNState {
            next_tokens_within_rule: OnceLock::new(),
            epsilon_only_transitions: false,
            rule_index,
            state_number,
            state_type_id,
            transitions: Vec::new(),
        }
    }
}

impl BaseATNState {
    pub fn has_epsilon_only_transitions(&self) -> bool {
        self.epsilon_only_transitions
    }
    pub fn get_rule_index(&self) -> i32 {
        self.rule_index
    }

    pub fn get_next_tokens_within_rule(&self) -> &OnceLock<IntervalSet> {
        &self.next_tokens_within_rule
    }

    pub fn get_state_type_id(&self) -> i32 {
        self.state_type_id
    }

    pub fn get_state_number(&self) -> i32 {
        self.state_number
    }

    pub fn get_transitions(&self) -> &Vec<Transition> {
        &self.transitions
    }

    pub fn add_transition(&mut self, trans: Transition) {
        if self.transitions.is_empty() {
            self.epsilon_only_transitions = trans.is_epsilon()
        } else {
            self.epsilon_only_transitions &= trans.is_epsilon()
        }

        let mut already_present = false;
        for existing in self.transitions.iter() {
            if existing.get_target() == trans.get_target() {
                #[allow(clippy::if_same_then_else)]
                if existing.get_label().is_some()
                    && trans.get_label().is_some()
                    && existing.get_label() == trans.get_label()
                {
                    already_present = true;
                    break;
                } else if existing.is_epsilon() && trans.is_epsilon() {
                    already_present = true;
                    break;
                }
            }
        }
        if !already_present {
            self.transitions.push(trans);
        }
    }
}

pub struct RuleStartState {
    base: BaseATNState,
    pub stop_state: ATNStateRef,
    pub is_left_recursive: bool,
}

impl RuleStartState {
    pub fn new(base: BaseATNState, stop_state: ATNStateRef, is_left_recursive: bool) -> Self {
        RuleStartState {
            base,
            stop_state,
            is_left_recursive,
        }
    }
}

pub struct RuleStopState {
    base: BaseATNState,
}

impl RuleStopState {
    pub fn new(base: BaseATNState) -> Self {
        RuleStopState { base }
    }
}

pub struct BlockEndState {
    base: BaseATNState,
    pub end_state: ATNStateRef,
}

impl BlockEndState {
    pub fn new(base: BaseATNState, end_state: ATNStateRef) -> Self {
        BlockEndState { base, end_state }
    }
}

pub struct LoopEndState {
    base: BaseATNState,
    pub loop_back_state: ATNStateRef,
}

impl LoopEndState {
    pub fn new(base: BaseATNState, loop_back_state: ATNStateRef) -> Self {
        LoopEndState {
            base,
            loop_back_state,
        }
    }
}

pub struct StarLoopbackState {
    base: BaseATNState,
}

impl StarLoopbackState {
    pub fn new(base: BaseATNState) -> Self {
        StarLoopbackState { base }
    }
}

pub struct BasicState {
    base: BaseATNState,
}

impl BasicState {
    pub fn new(base: BaseATNState) -> Self {
        BasicState { base }
    }
}

pub struct DecisionState {
    base: BaseATNState,
    pub decision: i32,
    pub nongreedy: bool,
    pub state: ATNDecisionState,
}

impl DecisionState {
    pub fn new(
        base: BaseATNState,
        decision: i32,
        nongreedy: bool,
        state: ATNDecisionState,
    ) -> Self {
        DecisionState {
            base,
            decision,
            nongreedy,
            state,
        }
    }
}

impl Deref for RuleStartState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for RuleStartState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<RuleStartState> for ATNState {
    fn from(value: RuleStartState) -> Self {
        ATNState::RuleStart(value)
    }
}

impl Deref for RuleStopState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for RuleStopState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<RuleStopState> for ATNState {
    fn from(value: RuleStopState) -> Self {
        ATNState::RuleStop(value)
    }
}

impl Deref for BlockEndState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for BlockEndState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<BlockEndState> for ATNState {
    fn from(value: BlockEndState) -> Self {
        ATNState::BlockEnd(value)
    }
}

impl Deref for LoopEndState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for LoopEndState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<LoopEndState> for ATNState {
    fn from(value: LoopEndState) -> Self {
        ATNState::LoopEnd(value)
    }
}

impl Deref for StarLoopbackState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for StarLoopbackState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<StarLoopbackState> for ATNState {
    fn from(value: StarLoopbackState) -> Self {
        ATNState::StarLoopback(value)
    }
}

impl Deref for BasicState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for BasicState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<BasicState> for ATNState {
    fn from(value: BasicState) -> Self {
        ATNState::Basic(value)
    }
}

impl Deref for DecisionState {
    type Target = BaseATNState;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl DerefMut for DecisionState {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

impl From<DecisionState> for ATNState {
    fn from(value: DecisionState) -> Self {
        ATNState::Decision(value)
    }
}
