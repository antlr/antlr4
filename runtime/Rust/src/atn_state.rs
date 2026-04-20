use std::fmt::{Debug, Formatter};
use std::mem::ManuallyDrop;
use std::ops::{Deref, DerefMut};
use std::pin::Pin;
use std::ptr::NonNull;
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

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
pub enum ATNStateType {
    RuleStart,
    RuleStop,
    BlockEnd,
    LoopEnd,
    StarLoopback,
    Basic,
    StarLoopEntry,
    TokenStart,
    PlusLoopBack,
    BasicBlockStart,
    StarBlockStart,
    PlusBlockStart,
    Invalid,
}

#[repr(C)]
pub struct ATNState {
    state_type: ATNStateType,
    epsilon_only_transitions: bool,
    rule_index: i32,
    state_number: i32,
    state_type_id: i32,
    next_tokens_within_rule: OnceLock<&'static IntervalSet>,
    transitions: Vec<Transition>,
    ext: ATNStateExt,
}

pub trait ATNStateExtTrait {
    fn self_type() -> ATNStateType;

    fn try_cast(state: &ATNState) -> Option<&Self>;

    fn try_cast_mut(state: &mut ATNState) -> Option<&mut Self>;
}

const EXT_OFFSET: usize = std::mem::offset_of!(ATNState, ext);

macro_rules! impl_deref_for_state {
    ($state_struct:ident, $state_type:ident, $field:ident) => {
        impl Deref for $state_struct {
            type Target = ATNState;

            fn deref(&self) -> &Self::Target {
                unsafe { &*((self as *const _ as *const u8).sub(EXT_OFFSET) as *const ATNState) }
            }
        }

        impl DerefMut for $state_struct {
            fn deref_mut(&mut self) -> &mut Self::Target {
                unsafe {
                    &mut *((self as *mut _ as *mut u8).sub(EXT_OFFSET) as *const ATNState
                        as *mut ATNState)
                }
            }
        }

        impl ATNStateExtTrait for $state_struct {
            fn self_type() -> ATNStateType {
                ATNStateType::$state_type
            }

            fn try_cast(state: &ATNState) -> Option<&Self> {
                if state.state_type == Self::self_type() {
                    Some(unsafe { &state.ext.$field })
                } else {
                    None
                }
            }

            fn try_cast_mut(state: &mut ATNState) -> Option<&mut Self> {
                if state.state_type == Self::self_type() {
                    Some(unsafe { &mut state.ext.$field })
                } else {
                    None
                }
            }
        }
    };
}

impl_deref_for_state!(RuleStartState, RuleStart, rule_start);
impl_deref_for_state!(RuleStopState, RuleStop, rule_stop);
impl_deref_for_state!(BlockEndState, BlockEnd, block_end);
impl_deref_for_state!(LoopEndState, LoopEnd, loop_end);
impl_deref_for_state!(StarLoopbackState, StarLoopback, star_loopback);
impl_deref_for_state!(BasicState, Basic, basic);
impl_deref_for_state!(StarLoopEntryState, StarLoopEntry, star_loop_entry);
impl_deref_for_state!(TokenStartState, TokenStart, token_start);
impl_deref_for_state!(PlusLoopBackState, PlusLoopBack, plus_loop_back);
impl_deref_for_state!(BasicBlockStartState, BasicBlockStart, basic_block_start);
impl_deref_for_state!(StarBlockStartState, StarBlockStart, star_block_start);
impl_deref_for_state!(PlusBlockStartState, PlusBlockStart, plus_block_start);

pub union ATNStateExt {
    rule_start: ManuallyDrop<RuleStartState>,
    rule_stop: ManuallyDrop<RuleStopState>,
    block_end: ManuallyDrop<BlockEndState>,
    loop_end: ManuallyDrop<LoopEndState>,
    star_loopback: ManuallyDrop<StarLoopbackState>,
    basic: ManuallyDrop<BasicState>,
    star_loop_entry: ManuallyDrop<StarLoopEntryState>,
    token_start: ManuallyDrop<TokenStartState>,
    plus_loop_back: ManuallyDrop<PlusLoopBackState>,
    basic_block_start: ManuallyDrop<BasicBlockStartState>,
    star_block_start: ManuallyDrop<StarBlockStartState>,
    plus_block_start: ManuallyDrop<PlusBlockStartState>,

    invalid: (),
}

impl ATNState {
    fn new(base: BaseATNState, state_type: ATNStateType, ext: ATNStateExt) -> Self {
        ATNState {
            state_type,
            epsilon_only_transitions: false,
            rule_index: base.rule_index,
            state_number: base.state_number,
            state_type_id: base.state_type_id,
            next_tokens_within_rule: OnceLock::new(),
            transitions: Vec::new(),
            ext,
        }
    }

    pub fn state_type(&self) -> ATNStateType {
        self.state_type
    }

    pub fn is_decision_state(&self) -> bool {
        matches!(
            self.state_type,
            ATNStateType::StarLoopEntry
                | ATNStateType::TokenStart
                | ATNStateType::PlusLoopBack
                | ATNStateType::BasicBlockStart
                | ATNStateType::StarBlockStart
                | ATNStateType::PlusBlockStart
        )
    }

    pub fn is_nongreedy_decision(&self) -> Option<bool> {
        match self.state_type {
            ATNStateType::StarLoopEntry => Some(unsafe { self.ext.star_loop_entry.nongreedy }),
            ATNStateType::TokenStart => Some(unsafe { self.ext.token_start.nongreedy }),
            ATNStateType::PlusLoopBack => Some(unsafe { self.ext.plus_loop_back.nongreedy }),
            ATNStateType::BasicBlockStart => Some(unsafe { self.ext.basic_block_start.nongreedy }),
            ATNStateType::StarBlockStart => Some(unsafe { self.ext.star_block_start.nongreedy }),
            ATNStateType::PlusBlockStart => Some(unsafe { self.ext.plus_block_start.nongreedy }),
            _ => None,
        }
    }

    pub fn get_nongreedy_decision_mut(&mut self) -> Option<&mut bool> {
        match self.state_type {
            ATNStateType::StarLoopEntry => {
                Some(unsafe { &mut (*self.ext.star_loop_entry).nongreedy })
            }
            ATNStateType::TokenStart => Some(unsafe { &mut (*self.ext.token_start).nongreedy }),
            ATNStateType::PlusLoopBack => {
                Some(unsafe { &mut (*self.ext.plus_loop_back).nongreedy })
            }
            ATNStateType::BasicBlockStart => {
                Some(unsafe { &mut (*self.ext.basic_block_start).nongreedy })
            }
            ATNStateType::StarBlockStart => {
                Some(unsafe { &mut (*self.ext.star_block_start).nongreedy })
            }
            ATNStateType::PlusBlockStart => {
                Some(unsafe { &mut (*self.ext.plus_block_start).nongreedy })
            }
            _ => None,
        }
    }

    pub fn get_decision_end_state(&self) -> Option<ATNStateRef> {
        match self.state_type {
            ATNStateType::BasicBlockStart => Some(unsafe { self.ext.basic_block_start.end_state }),
            ATNStateType::StarBlockStart => Some(unsafe { self.ext.star_block_start.end_state }),
            ATNStateType::PlusBlockStart => Some(unsafe { self.ext.plus_block_start.end_state }),
            _ => None,
        }
    }

    pub fn get_decision_end_state_mut(&mut self) -> Option<&mut ATNStateRef> {
        match self.state_type {
            ATNStateType::BasicBlockStart => {
                Some(unsafe { &mut (*self.ext.basic_block_start).end_state })
            }
            ATNStateType::StarBlockStart => {
                Some(unsafe { &mut (*self.ext.star_block_start).end_state })
            }
            ATNStateType::PlusBlockStart => {
                Some(unsafe { &mut (*self.ext.plus_block_start).end_state })
            }
            _ => None,
        }
    }

    pub fn get_decision(&self) -> Option<i32> {
        match self.state_type {
            ATNStateType::StarLoopEntry => Some(unsafe { self.ext.star_loop_entry.decision }),
            ATNStateType::TokenStart => Some(unsafe { self.ext.token_start.decision }),
            ATNStateType::PlusLoopBack => Some(unsafe { self.ext.plus_loop_back.decision }),
            ATNStateType::BasicBlockStart => Some(unsafe { self.ext.basic_block_start.decision }),
            ATNStateType::StarBlockStart => Some(unsafe { self.ext.star_block_start.decision }),
            ATNStateType::PlusBlockStart => Some(unsafe { self.ext.plus_block_start.decision }),
            _ => None,
        }
    }

    pub fn get_decision_mut(&mut self) -> Option<&mut i32> {
        match self.state_type {
            ATNStateType::StarLoopEntry => {
                Some(unsafe { &mut (*self.ext.star_loop_entry).decision })
            }
            ATNStateType::TokenStart => Some(unsafe { &mut (*self.ext.token_start).decision }),
            ATNStateType::PlusLoopBack => Some(unsafe { &mut (*self.ext.plus_loop_back).decision }),
            ATNStateType::BasicBlockStart => {
                Some(unsafe { &mut (*self.ext.basic_block_start).decision })
            }
            ATNStateType::StarBlockStart => {
                Some(unsafe { &mut (*self.ext.star_block_start).decision })
            }
            ATNStateType::PlusBlockStart => {
                Some(unsafe { &mut (*self.ext.plus_block_start).decision })
            }
            _ => None,
        }
    }

    pub fn has_epsilon_only_transitions(&self) -> bool {
        self.epsilon_only_transitions
    }

    pub fn get_rule_index(&self) -> i32 {
        self.rule_index
    }

    fn next_tokens_within_rule(&self) -> &OnceLock<&'static IntervalSet> {
        &self.next_tokens_within_rule
    }

    pub fn get_state_type_id(&self) -> i32 {
        self.state_type_id
    }

    pub fn get_state_number(&self) -> i32 {
        self.state_number
    }

    pub fn get_transitions(&self) -> &[Transition] {
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

    pub fn try_as<T: ATNStateExtTrait>(&self) -> Option<&T> {
        T::try_cast(self)
    }

    pub fn try_as_mut<T: ATNStateExtTrait>(&mut self) -> Option<&mut T> {
        T::try_cast_mut(self)
    }

    const fn invalid() -> Self {
        ATNState {
            state_type: ATNStateType::Invalid,
            epsilon_only_transitions: false,
            rule_index: -1,
            state_number: ATNSTATE_INVALID_STATE_NUMBER,
            state_type_id: ATNSTATE_INVALID_TYPE,
            next_tokens_within_rule: OnceLock::new(),
            transitions: Vec::new(),
            ext: ATNStateExt { invalid: () },
        }
    }
}

impl Default for ATNState {
    fn default() -> Self {
        Self::invalid()
    }
}

#[derive(Debug)]
pub struct BaseATNState {
    pub rule_index: i32,
    pub state_number: i32,
    pub state_type_id: i32,
}

impl BaseATNState {
    pub fn new(state_number: i32, rule_index: i32, state_type_id: i32) -> Self {
        BaseATNState {
            rule_index,
            state_number,
            state_type_id,
        }
    }
}

pub struct RuleStartState {
    pub stop_state: ATNStateRef,
    pub is_left_recursive: bool,

    _marker: std::marker::PhantomData<()>,
}

impl RuleStartState {
    pub fn create(
        base: BaseATNState,
        stop_state: ATNStateRef,
        is_left_recursive: bool,
    ) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::RuleStart,
            ATNStateExt {
                rule_start: ManuallyDrop::new(RuleStartState {
                    stop_state,
                    is_left_recursive,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct RuleStopState {}

impl RuleStopState {
    pub fn create(base: BaseATNState) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::RuleStop,
            ATNStateExt {
                rule_stop: ManuallyDrop::new(RuleStopState {}),
            },
        )
    }
}

pub struct BlockEndState {
    pub end_state: ATNStateRef,

    _marker: std::marker::PhantomData<()>,
}

impl BlockEndState {
    pub fn create(base: BaseATNState, end_state: ATNStateRef) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::BlockEnd,
            ATNStateExt {
                block_end: ManuallyDrop::new(BlockEndState {
                    end_state,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct LoopEndState {
    pub loop_back_state: ATNStateRef,

    _marker: std::marker::PhantomData<()>,
}

impl LoopEndState {
    pub fn create(base: BaseATNState, loop_back_state: ATNStateRef) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::LoopEnd,
            ATNStateExt {
                loop_end: ManuallyDrop::new(LoopEndState {
                    loop_back_state,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct StarLoopbackState {
    _marker: std::marker::PhantomData<()>,
}

impl StarLoopbackState {
    pub fn create(base: BaseATNState) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::StarLoopback,
            ATNStateExt {
                star_loopback: ManuallyDrop::new(StarLoopbackState {
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct BasicState {
    _marker: std::marker::PhantomData<()>,
}

impl BasicState {
    pub fn create(base: BaseATNState) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::Basic,
            ATNStateExt {
                basic: ManuallyDrop::new(BasicState {
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct StarLoopEntryState {
    pub decision: i32,
    pub nongreedy: bool,
    pub loop_back_state: ATNStateRef,
    pub is_precedence: bool,

    _marker: std::marker::PhantomData<()>,
}

impl StarLoopEntryState {
    pub fn create(
        base: BaseATNState,
        decision: i32,
        nongreedy: bool,
        loop_back_state: ATNStateRef,
        is_precedence: bool,
    ) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::StarLoopEntry,
            ATNStateExt {
                star_loop_entry: ManuallyDrop::new(StarLoopEntryState {
                    decision,
                    nongreedy,
                    loop_back_state,
                    is_precedence,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct TokenStartState {
    pub decision: i32,
    pub nongreedy: bool,

    _marker: std::marker::PhantomData<()>,
}

impl TokenStartState {
    pub fn create(base: BaseATNState, decision: i32, nongreedy: bool) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::TokenStart,
            ATNStateExt {
                token_start: ManuallyDrop::new(TokenStartState {
                    decision,
                    nongreedy,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct PlusLoopBackState {
    pub decision: i32,
    pub nongreedy: bool,

    _marker: std::marker::PhantomData<()>,
}

impl PlusLoopBackState {
    pub fn create(base: BaseATNState, decision: i32, nongreedy: bool) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::PlusLoopBack,
            ATNStateExt {
                plus_loop_back: ManuallyDrop::new(PlusLoopBackState {
                    decision,
                    nongreedy,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct BasicBlockStartState {
    pub decision: i32,
    pub nongreedy: bool,
    pub end_state: ATNStateRef,

    _marker: std::marker::PhantomData<()>,
}

impl BasicBlockStartState {
    pub fn create(
        base: BaseATNState,
        decision: i32,
        nongreedy: bool,
        end_state: ATNStateRef,
    ) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::BasicBlockStart,
            ATNStateExt {
                basic_block_start: ManuallyDrop::new(BasicBlockStartState {
                    decision,
                    nongreedy,
                    end_state,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct StarBlockStartState {
    pub decision: i32,
    pub nongreedy: bool,
    pub end_state: ATNStateRef,

    _marker: std::marker::PhantomData<()>,
}

impl StarBlockStartState {
    pub fn create(
        base: BaseATNState,
        decision: i32,
        nongreedy: bool,
        end_state: ATNStateRef,
    ) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::StarBlockStart,
            ATNStateExt {
                star_block_start: ManuallyDrop::new(StarBlockStartState {
                    decision,
                    nongreedy,
                    end_state,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct PlusBlockStartState {
    pub decision: i32,
    pub nongreedy: bool,
    pub end_state: ATNStateRef,
    pub loop_back_state: ATNStateRef,

    _marker: std::marker::PhantomData<()>,
}

impl PlusBlockStartState {
    pub fn create(
        base: BaseATNState,
        decision: i32,
        nongreedy: bool,
        end_state: ATNStateRef,
        loop_back_state: ATNStateRef,
    ) -> ATNState {
        ATNState::new(
            base,
            ATNStateType::PlusBlockStart,
            ATNStateExt {
                plus_block_start: ManuallyDrop::new(PlusBlockStartState {
                    decision,
                    nongreedy,
                    end_state,
                    loop_back_state,
                    _marker: std::marker::PhantomData,
                }),
            },
        )
    }
}

pub struct ATNStateRef(NonNull<ATNState>);

impl ATNStateRef {
    #[inline]
    pub fn invalid() -> Self {
        static INVALID_STATE: ATNState = ATNState::invalid();

        ATNStateRef(NonNull::from(&INVALID_STATE))
    }

    #[allow(clippy::mut_from_ref)]
    #[inline]
    pub(crate) unsafe fn as_mut(&self) -> &mut ATNState {
        unsafe { &mut *(self.0.as_ptr()) }
    }

    pub fn as_usize(&self) -> usize {
        self.0.as_ptr() as usize
    }

    pub fn get_next_tokens_within_rule(&self, atn: &crate::atn::ATN) -> &'static IntervalSet {
        self.next_tokens_within_rule().get_or_init(|| {
            atn.next_tokens_in_ctx::<crate::rule_context::EmptyRuleNode>(*self, None)
                .into_static()
        })
    }
}

impl From<&Pin<Box<ATNState>>> for ATNStateRef {
    fn from(value: &Pin<Box<ATNState>>) -> Self {
        ATNStateRef(NonNull::from(&**value))
    }
}

impl PartialEq for ATNStateRef {
    fn eq(&self, other: &Self) -> bool {
        self.0 == other.0
    }
}

impl Eq for ATNStateRef {}

impl PartialOrd for ATNStateRef {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        Some(self.cmp(other))
    }
}

impl Ord for ATNStateRef {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.get_state_number().cmp(&other.get_state_number())
    }
}

impl Deref for ATNStateRef {
    type Target = ATNState;

    fn deref(&self) -> &Self::Target {
        unsafe { &*self.0.as_ptr() }
    }
}

impl AsRef<ATNState> for ATNStateRef {
    fn as_ref(&self) -> &ATNState {
        unsafe { &*self.0.as_ptr() }
    }
}

impl Clone for ATNStateRef {
    fn clone(&self) -> Self {
        *self
    }
}

impl Copy for ATNStateRef {}

impl std::hash::Hash for ATNStateRef {
    fn hash<H: std::hash::Hasher>(&self, state: &mut H) {
        state.write_usize(self.0.as_ptr() as usize);
    }
}

impl Debug for ATNStateRef {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "ATNStateRef({})", self.get_state_number())
    }
}

unsafe impl Send for ATNStateRef {}
unsafe impl Sync for ATNStateRef {}
