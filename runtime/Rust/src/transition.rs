use std::fmt::Debug;
use std::mem::ManuallyDrop;
use std::ops::{Deref, DerefMut};

use crate::atn_state::ATNStateRef;
use crate::interval_set::{Interval, IntervalSet};
use crate::lexer::{LEXER_MAX_CHAR_VALUE, LEXER_MIN_CHAR_VALUE};
use crate::semantic_context::SemanticContext;

const _TRANSITION_NAMES: [&str; 11] = [
    "INVALID",
    "EPSILON",
    "RANGE",
    "RULE",
    "PREDICATE",
    "ATOM",
    "ACTION",
    "SET",
    "NOT_SET",
    "WILDCARD",
    "PRECEDENCE",
];

pub const TRANSITION_EPSILON: i32 = 1;
pub const TRANSITION_RANGE: i32 = 2;
pub const TRANSITION_RULE: i32 = 3;
pub const TRANSITION_PREDICATE: i32 = 4;
pub const TRANSITION_ATOM: i32 = 5;
pub const TRANSITION_ACTION: i32 = 6;
pub const TRANSITION_SET: i32 = 7;
pub const TRANSITION_NOTSET: i32 = 8;
pub const TRANSITION_WILDCARD: i32 = 9;
pub const TRANSITION_PRECEDENCE: i32 = 10;

/// Transition between ATNStates
#[derive(Debug, Clone, Copy)]
pub enum TransitionType {
    Atom,
    Rule,
    Epsilon,
    Range,
    Action,
    Set,
    NotSet,
    Wildcard,
    Predicate,
    PrecedencePredicate,
}

#[repr(C)]
pub struct Transition {
    transition_type: TransitionType,
    target: ATNStateRef,
    payload: TransitionPayload,
}

const PAYLOAD_OFFSET: usize = std::mem::offset_of!(Transition, payload);

impl Debug for Transition {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("Transition")
            .field("type", &self.transition_type)
            .field("target", &self.target)
            .finish()
    }
}

union TransitionPayload {
    atom: ManuallyDrop<AtomTransition>,
    rule: ManuallyDrop<RuleTransition>,
    epsilon: ManuallyDrop<EpsilonTransition>,
    range: ManuallyDrop<RangeTransition>,
    action: ManuallyDrop<ActionTransition>,
    set: ManuallyDrop<SetTransition>,
    not_set: ManuallyDrop<NotSetTransition>,
    wildcard: ManuallyDrop<WildcardTransition>,
    predicate: ManuallyDrop<PredicateTransition>,
    precedence_predicate: ManuallyDrop<PrecedencePredicateTransition>,
}

impl Transition {
    #[inline]
    pub fn transition_type(&self) -> TransitionType {
        self.transition_type
    }

    #[inline]
    pub fn get_target(&self) -> ATNStateRef {
        self.target
    }

    pub fn set_target(&mut self, s: ATNStateRef) {
        self.target = s;
    }

    pub fn is_epsilon(&self) -> bool {
        matches!(
            self.transition_type,
            TransitionType::Rule
                | TransitionType::Epsilon
                | TransitionType::Action
                | TransitionType::Predicate
                | TransitionType::PrecedencePredicate
        )
    }

    pub fn get_label(&self) -> Option<&IntervalSet> {
        match self {
            Transition {
                transition_type: TransitionType::Atom,
                payload,
                ..
            } => unsafe { payload.atom.get_label() },
            Transition {
                transition_type: TransitionType::Range,
                payload,
                ..
            } => unsafe { payload.range.get_label() },
            Transition {
                transition_type: TransitionType::Set,
                payload,
                ..
            } => unsafe { payload.set.get_label() },
            Transition {
                transition_type: TransitionType::NotSet,
                payload,
                ..
            } => unsafe { payload.not_set.get_label() },

            _ => None,
        }
    }

    pub fn matches(&self, symbol: i32, min_vocab_symbol: i32, max_vocab_symbol: i32) -> bool {
        match self.transition_type {
            TransitionType::Atom => unsafe {
                self.payload
                    .atom
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Rule => unsafe {
                self.payload
                    .rule
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Epsilon => unsafe {
                self.payload
                    .epsilon
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Range => unsafe {
                self.payload
                    .range
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Action => unsafe {
                self.payload
                    .action
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Set => unsafe {
                self.payload
                    .set
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::NotSet => unsafe {
                self.payload
                    .not_set
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Wildcard => unsafe {
                self.payload
                    .wildcard
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::Predicate => unsafe {
                self.payload
                    .predicate
                    .matches(symbol, min_vocab_symbol, max_vocab_symbol)
            },
            TransitionType::PrecedencePredicate => unsafe {
                self.payload.precedence_predicate.matches(
                    symbol,
                    min_vocab_symbol,
                    max_vocab_symbol,
                )
            },
        }
    }

    pub fn get_predicate(&self) -> Option<SemanticContext<'static>> {
        match self.transition_type {
            TransitionType::Predicate => unsafe { self.payload.predicate.get_predicate() },
            TransitionType::PrecedencePredicate => unsafe {
                self.payload.precedence_predicate.get_predicate()
            },
            _ => None,
        }
    }

    pub fn get_reachable_target(&self, symbol: i32) -> Option<ATNStateRef> {
        //        println!("reachable target called on {:?}", self);
        if self.matches(symbol, LEXER_MIN_CHAR_VALUE, LEXER_MAX_CHAR_VALUE) {
            return Some(self.get_target());
        }
        None
    }

    pub fn try_as<T: ConcreteTransition>(&self) -> Option<&T> {
        T::cast_from(self)
    }

    #[inline(always)]
    fn self_ptr(payload_ptr: *const u8) -> *const Transition {
        unsafe { payload_ptr.sub(PAYLOAD_OFFSET) as *const Transition }
    }
}

pub trait ConcreteTransition: Deref<Target = Transition> {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized;
}

macro_rules! impl_deref_for_transition {
    ($struct_name:ident) => {
        impl Deref for $struct_name {
            type Target = Transition;

            fn deref(&self) -> &Self::Target {
                unsafe { &*Transition::self_ptr(self as *const _ as *const u8) }
            }
        }

        impl DerefMut for $struct_name {
            fn deref_mut(&mut self) -> &mut Self::Target {
                unsafe {
                    &mut *(Transition::self_ptr(self as *const _ as *const u8) as *mut Transition)
                }
            }
        }
    };
}

#[derive(Debug)]
pub struct AtomTransition {
    label: Interval,
}

impl AtomTransition {
    pub fn create(target: ATNStateRef, label: i32) -> Transition {
        Transition {
            transition_type: TransitionType::Atom,
            target,
            payload: TransitionPayload {
                atom: ManuallyDrop::new(AtomTransition {
                    label: Interval::new(label, label),
                }),
            },
        }
    }

    pub fn label(&self) -> i32 {
        self.label.a
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(IntervalSet::from_interval(&self.label))
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol == self.label()
    }
}

impl_deref_for_transition!(AtomTransition);

impl ConcreteTransition for AtomTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Atom => Some(unsafe { &t.payload.atom }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct RuleTransition {
    pub follow_state: ATNStateRef,
    rule_index: i32,
    precedence: i32,
}

impl RuleTransition {
    pub fn create(
        target: ATNStateRef,
        follow_state: ATNStateRef,
        rule_index: i32,
        precedence: i32,
    ) -> Transition {
        Transition {
            transition_type: TransitionType::Rule,
            target,
            payload: TransitionPayload {
                rule: ManuallyDrop::new(RuleTransition {
                    follow_state,
                    rule_index,
                    precedence,
                }),
            },
        }
    }

    pub fn rule_index(&self) -> i32 {
        self.rule_index
    }

    pub fn precedence(&self) -> i32 {
        self.precedence
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        unimplemented!()
    }
}

impl_deref_for_transition!(RuleTransition);

impl ConcreteTransition for RuleTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Rule => Some(unsafe { &t.payload.rule }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct EpsilonTransition {
    outermost_precedence_return: i32,
}

impl EpsilonTransition {
    pub fn create(target: ATNStateRef, outermost_precedence_return: i32) -> Transition {
        Transition {
            transition_type: TransitionType::Epsilon,
            target,
            payload: TransitionPayload {
                epsilon: ManuallyDrop::new(EpsilonTransition {
                    outermost_precedence_return,
                }),
            },
        }
    }

    pub fn outermost_precedence_return(&self) -> i32 {
        self.outermost_precedence_return
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }
}

impl_deref_for_transition!(EpsilonTransition);

impl ConcreteTransition for EpsilonTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Epsilon => Some(unsafe { &t.payload.epsilon }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct RangeTransition {
    range: Interval,
}

impl RangeTransition {
    pub fn create(target: ATNStateRef, start: i32, stop: i32) -> Transition {
        Transition {
            transition_type: TransitionType::Range,
            target,
            payload: TransitionPayload {
                range: ManuallyDrop::new(RangeTransition {
                    range: Interval::new(start, stop),
                }),
            },
        }
    }

    pub fn start(&self) -> i32 {
        self.range.a
    }

    pub fn stop(&self) -> i32 {
        self.range.b
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(IntervalSet::from_interval(&self.range))
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol >= self.start() && _symbol <= self.stop()
    }
}

impl_deref_for_transition!(RangeTransition);

impl ConcreteTransition for RangeTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Range => Some(unsafe { &t.payload.range }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct ActionTransition {
    pub is_ctx_dependent: bool,
    rule_index: i32,
    action_index: i32,
    pred_index: i32,
}

impl ActionTransition {
    pub fn create(
        target: ATNStateRef,
        is_ctx_dependent: bool,
        rule_index: i32,
        action_index: i32,
        pred_index: i32,
    ) -> Transition {
        Transition {
            transition_type: TransitionType::Action,
            target,
            payload: TransitionPayload {
                action: ManuallyDrop::new(ActionTransition {
                    is_ctx_dependent,
                    rule_index,
                    action_index,
                    pred_index,
                }),
            },
        }
    }

    pub fn rule_index(&self) -> i32 {
        self.rule_index
    }

    pub fn action_index(&self) -> i32 {
        self.action_index
    }

    pub fn pred_index(&self) -> i32 {
        self.pred_index
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }
}

impl_deref_for_transition!(ActionTransition);

impl ConcreteTransition for ActionTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Action => Some(unsafe { &t.payload.action }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct SetTransition {
    set: &'static IntervalSet,
}

impl SetTransition {
    pub fn create(target: ATNStateRef, set: &'static IntervalSet) -> Transition {
        Transition {
            transition_type: TransitionType::Set,
            target,
            payload: TransitionPayload {
                set: ManuallyDrop::new(SetTransition { set }),
            },
        }
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(self.set)
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        self.set.contains(_symbol)
    }
}

impl_deref_for_transition!(SetTransition);

impl ConcreteTransition for SetTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Set => Some(unsafe { &t.payload.set }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct NotSetTransition {
    set: &'static IntervalSet,
}

impl NotSetTransition {
    pub fn create(target: ATNStateRef, set: &'static IntervalSet) -> Transition {
        Transition {
            transition_type: TransitionType::NotSet,
            target,
            payload: TransitionPayload {
                not_set: ManuallyDrop::new(NotSetTransition { set }),
            },
        }
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(self.set)
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol >= _min_vocab_symbol && _symbol <= _max_vocab_symbol && !self.set.contains(_symbol)
    }
}

impl_deref_for_transition!(NotSetTransition);

impl ConcreteTransition for NotSetTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::NotSet => Some(unsafe { &t.payload.not_set }),
            _ => None,
        }
    }
}

#[derive(Debug)]
#[non_exhaustive]
pub struct WildcardTransition {}

impl WildcardTransition {
    pub fn create(target: ATNStateRef) -> Transition {
        Transition {
            transition_type: TransitionType::Wildcard,
            target,
            payload: TransitionPayload {
                wildcard: ManuallyDrop::new(WildcardTransition {}),
            },
        }
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol < _max_vocab_symbol && _symbol > _min_vocab_symbol
    }
}

impl_deref_for_transition!(WildcardTransition);

impl ConcreteTransition for WildcardTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Wildcard => Some(unsafe { &t.payload.wildcard }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct PredicateTransition {
    is_ctx_dependent: bool,
    rule_index: i32,
    pred_index: i32,
}

impl PredicateTransition {
    pub fn create(
        target: ATNStateRef,
        is_ctx_dependent: bool,
        rule_index: i32,
        pred_index: i32,
    ) -> Transition {
        Transition {
            transition_type: TransitionType::Predicate,
            target,
            payload: TransitionPayload {
                predicate: ManuallyDrop::new(PredicateTransition {
                    is_ctx_dependent,
                    rule_index,
                    pred_index,
                }),
            },
        }
    }

    pub fn is_ctx_dependent(&self) -> bool {
        self.is_ctx_dependent
    }

    pub fn rule_index(&self) -> i32 {
        self.rule_index
    }

    pub fn pred_index(&self) -> i32 {
        self.pred_index
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }

    pub fn get_predicate(&self) -> Option<SemanticContext<'static>> {
        Some(SemanticContext::Predicate {
            rule_index: self.rule_index,
            pred_index: self.pred_index,
            is_ctx_dependent: self.is_ctx_dependent,
        })
    }
}

impl_deref_for_transition!(PredicateTransition);

impl ConcreteTransition for PredicateTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::Predicate => Some(unsafe { &t.payload.predicate }),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct PrecedencePredicateTransition {
    precedence: i32,
}

impl PrecedencePredicateTransition {
    pub fn create(target: ATNStateRef, precedence: i32) -> Transition {
        Transition {
            transition_type: TransitionType::PrecedencePredicate,
            target,
            payload: TransitionPayload {
                precedence_predicate: ManuallyDrop::new(PrecedencePredicateTransition {
                    precedence,
                }),
            },
        }
    }

    pub fn precedence(&self) -> i32 {
        self.precedence
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }

    pub fn get_predicate(&self) -> Option<SemanticContext<'static>> {
        Some(SemanticContext::Precedence(self.precedence))
    }
}

impl_deref_for_transition!(PrecedencePredicateTransition);

impl ConcreteTransition for PrecedencePredicateTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t.transition_type {
            TransitionType::PrecedencePredicate => Some(unsafe { &t.payload.precedence_predicate }),
            _ => None,
        }
    }
}
