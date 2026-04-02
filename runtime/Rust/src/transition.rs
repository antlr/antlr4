use std::fmt::Debug;
use std::sync::OnceLock;

use crate::atn_state::ATNStateRef;
use crate::interval_set::IntervalSet;
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
#[derive(Debug)]
pub enum Transition {
    Atom(AtomTransition),
    Rule(RuleTransition),
    Epsilon(EpsilonTransition),
    Range(RangeTransition),
    Action(ActionTransition),
    Set(SetTransition),
    NotSet(NotSetTransition),
    Wildcard(WildcardTransition),
    Predicate(PredicateTransition),
    PrecedencePredicate(PrecedencePredicateTransition),
}

impl Transition {
    pub fn get_target(&self) -> ATNStateRef {
        match self {
            Transition::Atom(t) => t.get_target(),
            Transition::Rule(t) => t.get_target(),
            Transition::Epsilon(t) => t.get_target(),
            Transition::Range(t) => t.get_target(),
            Transition::Action(t) => t.get_target(),
            Transition::Set(t) => t.get_target(),
            Transition::NotSet(t) => t.get_target(),
            Transition::Wildcard(t) => t.get_target(),
            Transition::Predicate(t) => t.get_target(),
            Transition::PrecedencePredicate(t) => t.get_target(),
        }
    }

    pub fn set_target(&mut self, s: ATNStateRef) {
        match self {
            Transition::Atom(t) => t.set_target(s),
            Transition::Rule(t) => t.set_target(s),
            Transition::Epsilon(t) => t.set_target(s),
            Transition::Range(t) => t.set_target(s),
            Transition::Action(t) => t.set_target(s),
            Transition::Set(t) => t.set_target(s),
            Transition::NotSet(t) => t.set_target(s),
            Transition::Wildcard(t) => t.set_target(s),
            Transition::Predicate(t) => t.set_target(s),
            Transition::PrecedencePredicate(t) => t.set_target(s),
        }
    }

    pub fn is_epsilon(&self) -> bool {
        matches!(
            self,
            Transition::Rule(_)
                | Transition::Epsilon(_)
                | Transition::Action(_)
                | Transition::Predicate(_)
                | Transition::PrecedencePredicate(_)
        )
    }

    pub fn get_label(&self) -> Option<&IntervalSet> {
        match self {
            Transition::Atom(t) => t.get_label(),
            Transition::Range(t) => t.get_label(),
            Transition::Set(t) => t.get_label(),
            Transition::NotSet(t) => t.get_label(),
            _ => None,
        }
    }

    pub fn matches(&self, symbol: i32, min_vocab_symbol: i32, max_vocab_symbol: i32) -> bool {
        match self {
            Transition::Atom(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Rule(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Epsilon(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Range(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Action(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Set(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::NotSet(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Wildcard(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::Predicate(t) => t.matches(symbol, min_vocab_symbol, max_vocab_symbol),
            Transition::PrecedencePredicate(t) => {
                t.matches(symbol, min_vocab_symbol, max_vocab_symbol)
            }
        }
    }

    pub fn get_predicate(&self) -> Option<SemanticContext<'static>> {
        match self {
            Transition::Predicate(t) => t.get_predicate(),
            Transition::PrecedencePredicate(t) => t.get_predicate(),
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
}

pub trait ConcreteTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized;
}

#[derive(Debug)]
pub struct AtomTransition {
    pub target: ATNStateRef,
    pub label: i32,
    label_set: OnceLock<IntervalSet>,
}

impl AtomTransition {
    pub fn new(target: ATNStateRef, label: i32) -> Self {
        AtomTransition {
            target,
            label,
            label_set: OnceLock::new(),
        }
    }

    fn get_target(&self) -> ATNStateRef {
        self.target
    }

    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(self.label_set.get_or_init(|| {
            let mut r = IntervalSet::new();
            r.add_one(self.label);
            r
        }))
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol == self.label
    }
}

impl From<AtomTransition> for Transition {
    fn from(t: AtomTransition) -> Self {
        Transition::Atom(t)
    }
}

impl ConcreteTransition for AtomTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Atom(at) => Some(at),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct RuleTransition {
    pub target: ATNStateRef,
    pub follow_state: ATNStateRef,
    pub rule_index: i32,
    pub precedence: i32,
}

impl RuleTransition {
    pub fn get_target(&self) -> ATNStateRef {
        self.target
    }
    pub fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    pub fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        unimplemented!()
    }
}

impl From<RuleTransition> for Transition {
    fn from(t: RuleTransition) -> Self {
        Transition::Rule(t)
    }
}

impl ConcreteTransition for RuleTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Rule(rt) => Some(rt),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct EpsilonTransition {
    pub target: ATNStateRef,
    pub outermost_precedence_return: i32,
}

impl EpsilonTransition {
    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }
}

impl From<EpsilonTransition> for Transition {
    fn from(t: EpsilonTransition) -> Self {
        Transition::Epsilon(t)
    }
}

impl ConcreteTransition for EpsilonTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Epsilon(et) => Some(et),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct RangeTransition {
    pub target: ATNStateRef,
    pub start: i32,
    pub stop: i32,
    label_set: OnceLock<IntervalSet>,
}

impl RangeTransition {
    pub fn new(target: ATNStateRef, start: i32, stop: i32) -> Self {
        RangeTransition {
            target,
            start,
            stop,
            label_set: OnceLock::new(),
        }
    }

    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(self.label_set.get_or_init(|| {
            let mut r = IntervalSet::new();
            r.add_range(self.start, self.stop);
            r
        }))
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol >= self.start && _symbol <= self.stop
    }
}

impl From<RangeTransition> for Transition {
    fn from(t: RangeTransition) -> Self {
        Transition::Range(t)
    }
}

impl ConcreteTransition for RangeTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Range(rt) => Some(rt),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct ActionTransition {
    pub target: ATNStateRef,
    pub is_ctx_dependent: bool,
    pub rule_index: i32,
    pub action_index: i32,
    pub pred_index: i32,
}

impl ActionTransition {
    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }
}

impl From<ActionTransition> for Transition {
    fn from(t: ActionTransition) -> Self {
        Transition::Action(t)
    }
}

impl ConcreteTransition for ActionTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Action(at) => Some(at),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct SetTransition {
    pub target: ATNStateRef,
    pub set: IntervalSet,
}

impl SetTransition {
    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(&self.set)
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        self.set.contains(_symbol)
    }
}

impl From<SetTransition> for Transition {
    fn from(t: SetTransition) -> Self {
        Transition::Set(t)
    }
}

impl ConcreteTransition for SetTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Set(st) => Some(st),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct NotSetTransition {
    pub target: ATNStateRef,
    pub set: IntervalSet,
}

impl NotSetTransition {
    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn get_label(&self) -> Option<&IntervalSet> {
        Some(&self.set)
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol >= _min_vocab_symbol && _symbol <= _max_vocab_symbol && !self.set.contains(_symbol)
    }
}

impl From<NotSetTransition> for Transition {
    fn from(t: NotSetTransition) -> Self {
        Transition::NotSet(t)
    }
}

impl ConcreteTransition for NotSetTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::NotSet(nt) => Some(nt),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct WildcardTransition {
    pub target: ATNStateRef,
}

impl WildcardTransition {
    fn get_target(&self) -> ATNStateRef {
        self.target
    }
    fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        _symbol < _max_vocab_symbol && _symbol > _min_vocab_symbol
    }
}

impl From<WildcardTransition> for Transition {
    fn from(t: WildcardTransition) -> Self {
        Transition::Wildcard(t)
    }
}

impl ConcreteTransition for WildcardTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Wildcard(wt) => Some(wt),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct PredicateTransition {
    pub target: ATNStateRef,
    pub is_ctx_dependent: bool,
    pub rule_index: i32,
    pub pred_index: i32,
}

impl PredicateTransition {
    pub fn get_target(&self) -> ATNStateRef {
        self.target
    }

    pub fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    pub fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
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

impl From<PredicateTransition> for Transition {
    fn from(t: PredicateTransition) -> Self {
        Transition::Predicate(t)
    }
}

impl ConcreteTransition for PredicateTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::Predicate(pt) => Some(pt),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub struct PrecedencePredicateTransition {
    pub target: ATNStateRef,
    pub precedence: i32,
}

impl PrecedencePredicateTransition {
    pub fn get_target(&self) -> ATNStateRef {
        self.target
    }

    pub fn set_target(&mut self, s: ATNStateRef) {
        self.target = s
    }

    pub fn matches(&self, _symbol: i32, _min_vocab_symbol: i32, _max_vocab_symbol: i32) -> bool {
        false
    }

    pub fn get_predicate(&self) -> Option<SemanticContext<'static>> {
        Some(SemanticContext::Precedence(self.precedence))
    }
}

impl From<PrecedencePredicateTransition> for Transition {
    fn from(t: PrecedencePredicateTransition) -> Self {
        Transition::PrecedencePredicate(t)
    }
}

impl ConcreteTransition for PrecedencePredicateTransition {
    fn cast_from(t: &Transition) -> Option<&Self>
    where
        Self: Sized,
    {
        match t {
            Transition::PrecedencePredicate(pt) => Some(pt),
            _ => None,
        }
    }
}
