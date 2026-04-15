use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use crate::atn_config_set::{ConfigSet, LexerATNConfigSet};
use crate::atn_state::{ATNState, ATNStateRef, DecisionState};
use crate::dfa::DFAStateStore;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::PredictionContextRef;
use crate::semantic_context::SemanticContext;
use crate::PredictionContextCache;

pub trait ATNConfigType<'ephemeral>: PartialEq + Eq + Hash + Debug + Clone {}

const SUPPRESS_PRECEDENCE_FILTER: i32 = 0x40000000;

#[derive(Clone)]
pub struct ATNConfig<'ephemeral> {
    state: ATNStateRef,
    alt: i32,
    context: Option<PredictionContextRef<'ephemeral>>,
    semantic_context: &'ephemeral SemanticContext<'ephemeral>,
    pub reaches_into_outer_context: i32,
}

impl Eq for ATNConfig<'_> {}

impl PartialEq for ATNConfig<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.get_state() == other.get_state()
            && self.get_alt() == other.get_alt()
            && self.context == other.context
            && self.semantic_context == other.semantic_context
            && self.is_precedence_filter_suppressed() == other.is_precedence_filter_suppressed()
    }
}

impl Hash for ATNConfig<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_i32(self.get_state().get_state_number());
        state.write_i32(self.get_alt());
        match self.get_context() {
            None => state.write_i32(0),
            Some(c) => c.hash(state),
        }
        self.semantic_context.hash(state);
    }
}

impl Debug for ATNConfig<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_fmt(format_args!(
            "({:?},{},[{}]",
            self.state,
            self.alt,
            self.context.unwrap()
        ))?;
        if self.reaches_into_outer_context > 0 {
            f.write_fmt(format_args!(",up={}", self.reaches_into_outer_context))?;
        }

        f.write_str(")")
    }
}

impl<'ephemeral> ATNConfigType<'ephemeral> for ATNConfig<'ephemeral> {}

impl<'ephemeral> ATNConfig<'ephemeral> {
    pub fn new(
        state: ATNStateRef,
        alt: i32,
        context: Option<PredictionContextRef<'ephemeral>>,
    ) -> Self {
        ATNConfig {
            state,
            alt,
            context,
            semantic_context: &SemanticContext::NONE,
            reaches_into_outer_context: 0,
        }
    }

    pub fn with_semantic_context(
        self,
        semantic_context: &'ephemeral SemanticContext<'ephemeral>,
    ) -> Self {
        ATNConfig {
            semantic_context,
            ..self
        }
    }

    pub fn with_state(self, state: ATNStateRef) -> Self {
        Self { state, ..self }
    }

    pub fn with_prediction_context(
        self,
        context: Option<impl Into<PredictionContextRef<'ephemeral>>>,
    ) -> Self {
        ATNConfig {
            context: context.map(|c| c.into()),
            ..self
        }
    }

    pub(crate) fn finalize<'sim, CS>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, CS>,
    ) -> ATNConfig<'sim>
    where
        CS: ConfigSet<'sim>,
    {
        ATNConfig {
            context: self.context.map(|c| cache.get_shared_context(&c)),
            semantic_context: dfa.alloc_semantic_context(|| self.semantic_context.promote(dfa))
                as &'sim _,
            ..self
        }
    }

    pub fn get_state(&self) -> ATNStateRef {
        self.state
    }

    pub fn get_alt(&self) -> i32 {
        self.alt
    }

    pub fn get_context(&self) -> Option<PredictionContextRef<'ephemeral>> {
        self.context
    }

    pub fn semantic_context(&self) -> &'ephemeral SemanticContext<'ephemeral> {
        self.semantic_context
    }

    pub fn take_context(&mut self) -> PredictionContextRef<'ephemeral> {
        self.context.take().unwrap()
    }

    pub fn set_context(&mut self, context: PredictionContextRef<'ephemeral>) {
        self.context = Some(context);
    }

    pub fn get_reaches_into_outer_context(&self) -> i32 {
        self.reaches_into_outer_context
    }

    pub fn set_reaches_into_outer_context(&mut self, _v: i32) {
        self.reaches_into_outer_context = _v
    }

    #[inline]
    pub fn is_precedence_filter_suppressed(&self) -> bool {
        self.reaches_into_outer_context & SUPPRESS_PRECEDENCE_FILTER != 0
    }

    pub fn set_precedence_filter_suppressed(&mut self, value: bool) {
        if value {
            self.reaches_into_outer_context |= SUPPRESS_PRECEDENCE_FILTER;
        } else {
            self.reaches_into_outer_context &= !SUPPRESS_PRECEDENCE_FILTER;
        }
    }
}

#[derive(Clone)]
pub struct LexerATNConfig<'ephemeral> {
    base: ATNConfig<'ephemeral>,
    lexer_action_executor: Option<&'ephemeral LexerActionExecutor<'ephemeral>>,
    passed_through_non_greedy_decision: bool,
}

impl Debug for LexerATNConfig<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_fmt(format_args!(
            "LexerATNConfig({:?},lexer_action_executor={:?},passed_through_non_greedy_decision={})",
            self.base,
            self.get_lexer_executor(),
            self.has_passed_through_non_greedy_decision()
        ))
    }
}

impl Hash for LexerATNConfig<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.base.hash(state);

        state.write_i32(if self.has_passed_through_non_greedy_decision() {
            1
        } else {
            0
        });
        match self.get_lexer_executor() {
            None => state.write_i32(0),
            Some(ex) => ex.hash(state),
        }
    }
}

impl<'ephemeral> PartialEq for LexerATNConfig<'ephemeral> {
    fn eq(&self, other: &Self) -> bool {
        self.base == other.base
            && self.has_passed_through_non_greedy_decision()
                == other.has_passed_through_non_greedy_decision()
            && self.get_lexer_executor() == other.get_lexer_executor()
    }
}

impl Eq for LexerATNConfig<'_> {}

impl<'ephemeral> ATNConfigType<'ephemeral> for LexerATNConfig<'ephemeral> {}

impl<'ephemeral> LexerATNConfig<'ephemeral> {
    pub fn new(state: ATNStateRef, alt: i32, context: PredictionContextRef<'ephemeral>) -> Self {
        let base = ATNConfig::new(state, alt, Some(context));
        LexerATNConfig {
            base,
            lexer_action_executor: None,
            passed_through_non_greedy_decision: false,
        }
    }

    pub fn with_state(self, state: ATNStateRef) -> Self {
        let passed_through_non_greedy_decision = self.has_passed_through_non_greedy_decision()
            || matches!(
                *state,
                ATNState::Decision(DecisionState {
                    nongreedy: true,
                    ..
                })
            );
        Self {
            base: self.base.with_state(state),
            passed_through_non_greedy_decision,
            ..self
        }
    }

    pub fn with_prediction_context(
        self,
        context: Option<impl Into<PredictionContextRef<'ephemeral>>>,
    ) -> Self {
        Self {
            base: self.base.with_prediction_context(context),
            ..self
        }
    }

    pub(crate) fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, LexerATNConfigSet<'sim>>,
    ) -> LexerATNConfig<'sim> {
        LexerATNConfig {
            base: self.base.finalize(cache, dfa),
            lexer_action_executor: self
                .lexer_action_executor
                .map(|ex| dfa.alloc_lexer_action_executor(|| ex.promote(dfa)) as &'sim _),
            ..self
        }
    }

    pub(crate) fn with_lexer_executor(
        self,
        lexer_action_executor: Option<&'ephemeral LexerActionExecutor>,
    ) -> Self {
        Self {
            lexer_action_executor,
            ..self
        }
    }

    pub fn get_state(&self) -> ATNStateRef {
        self.base.get_state()
    }

    pub fn get_alt(&self) -> i32 {
        self.base.get_alt()
    }

    pub fn get_context(&self) -> Option<PredictionContextRef<'ephemeral>> {
        self.base.get_context()
    }

    pub fn semantic_context(&self) -> &'ephemeral SemanticContext<'ephemeral> {
        self.base.semantic_context()
    }

    pub fn take_context(&mut self) -> PredictionContextRef<'ephemeral> {
        self.base.take_context()
    }

    pub fn set_context(&mut self, context: PredictionContextRef<'ephemeral>) {
        self.base.set_context(context);
    }

    pub fn get_reaches_into_outer_context(&self) -> i32 {
        self.base.get_reaches_into_outer_context()
    }

    pub fn set_reaches_into_outer_context(&mut self, v: i32) {
        self.base.set_reaches_into_outer_context(v);
    }

    #[inline]
    pub fn is_precedence_filter_suppressed(&self) -> bool {
        self.base.is_precedence_filter_suppressed()
    }

    pub fn set_precedence_filter_suppressed(&mut self, v: bool) {
        self.base.set_precedence_filter_suppressed(v);
    }

    #[inline]
    pub(crate) fn get_lexer_executor(&self) -> Option<&'ephemeral LexerActionExecutor<'ephemeral>> {
        self.lexer_action_executor
    }

    #[inline]
    pub fn has_passed_through_non_greedy_decision(&self) -> bool {
        self.passed_through_non_greedy_decision
    }
}
