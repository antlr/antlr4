use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use crate::atn_state::{ATNState, ATNStateRef, DecisionState};
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;

pub trait ATNConfigType: PartialEq + Eq + Hash + Debug + Clone {}

const SUPPRESS_PRECEDENCE_FILTER: i32 = 0x40000000;

#[derive(Clone)]
pub struct ATNConfig<'ephemeral> {
    state: ATNStateRef,
    alt: i32,
    //todo maybe option is unnecessary and PredictionContext::EMPTY would be enough
    context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    semantic_context: &'ephemeral SemanticContext<'ephemeral>,
    pub reaches_into_outer_context: i32,
}

impl Eq for ATNConfig<'_> {}

impl PartialEq for ATNConfig<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.get_state() == other.get_state()
            && self.get_alt() == other.get_alt()
            && crate::prediction_context::opt_eq((&self.context, &other.context))
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

impl ATNConfigType for ATNConfig<'_> {}

impl<'ephemeral> ATNConfig<'ephemeral> {
    pub fn new(
        state: ATNStateRef,
        alt: i32,
        context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    ) -> Self {
        ATNConfig {
            state,
            alt,
            context,
            semantic_context: &SemanticContext::NONE,
            reaches_into_outer_context: 0,
        }
    }

    pub fn with_semantic_context(self, semantic_context: &'ephemeral SemanticContext) -> Self {
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
        context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    ) -> Self {
        ATNConfig { context, ..self }
    }

    pub(crate) fn make_static(
        self,
        prediction_context: Option<&'static PredictionContext<'static>>,
        semantic_context: &'static SemanticContext,
    ) -> ATNConfig<'static> {
        ATNConfig {
            state: self.state,
            alt: self.alt,
            context: prediction_context,
            semantic_context,
            reaches_into_outer_context: self.reaches_into_outer_context,
        }
    }

    pub fn get_state(&self) -> ATNStateRef {
        self.state
    }

    pub fn get_alt(&self) -> i32 {
        self.alt
    }

    pub fn get_context(&self) -> Option<&'ephemeral PredictionContext<'ephemeral>> {
        self.context
    }

    pub fn semantic_context(&self) -> &'ephemeral SemanticContext {
        self.semantic_context
    }

    pub fn take_context(&mut self) -> &'ephemeral PredictionContext<'ephemeral> {
        self.context.take().unwrap()
    }

    pub fn set_context(&mut self, context: &'ephemeral PredictionContext<'ephemeral>) {
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
    lexer_action_executor: Option<&'ephemeral LexerActionExecutor>,
    passed_through_non_greedy_decision: bool,
}

impl Debug for LexerATNConfig<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_fmt(format_args!(
            "LexerATNConfig({:?},lexer_action_executor={:?},passed_through_non_greedy_decision={})",
            self.base, self.lexer_action_executor, self.passed_through_non_greedy_decision
        ))
    }
}

impl Hash for LexerATNConfig<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.base.hash(state);

        state.write_i32(if self.passed_through_non_greedy_decision {
            1
        } else {
            0
        });
        match self.lexer_action_executor {
            None => state.write_i32(0),
            Some(ex) => ex.hash(state),
        }
    }
}

impl<'ephemeral> PartialEq for LexerATNConfig<'ephemeral> {
    fn eq(&self, other: &Self) -> bool {
        self.base == other.base
            && self.passed_through_non_greedy_decision == other.passed_through_non_greedy_decision
            && self.lexer_action_executor == other.lexer_action_executor
    }
}

impl Eq for LexerATNConfig<'_> {}

impl ATNConfigType for LexerATNConfig<'_> {}

impl<'ephemeral> LexerATNConfig<'ephemeral> {
    pub fn new(
        state: ATNStateRef,
        alt: i32,
        context: &'ephemeral PredictionContext<'ephemeral>,
    ) -> Self {
        let base = ATNConfig::new(state, alt, Some(context));
        LexerATNConfig {
            base,
            lexer_action_executor: None,
            passed_through_non_greedy_decision: false,
        }
    }

    pub(crate) fn get_lexer_executor(&self) -> Option<&LexerActionExecutor> {
        self.lexer_action_executor
    }

    pub fn with_state(self, state: ATNStateRef) -> Self {
        let passed_through_non_greedy_decision = self.passed_through_non_greedy_decision
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
        context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    ) -> Self {
        Self {
            base: self.base.with_prediction_context(context),
            ..self
        }
    }

    pub(crate) fn make_static(
        self,
        prediction_context: Option<&'static PredictionContext<'static>>,
        semantic_context: &'static SemanticContext,
        lexer_action_executor: Option<&'static LexerActionExecutor>,
    ) -> LexerATNConfig<'static> {
        LexerATNConfig {
            base: self.base.make_static(prediction_context, semantic_context),
            lexer_action_executor,
            passed_through_non_greedy_decision: self.passed_through_non_greedy_decision,
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

    pub fn get_context(&self) -> Option<&'ephemeral PredictionContext<'ephemeral>> {
        self.base.get_context()
    }

    pub fn semantic_context(&self) -> &'ephemeral SemanticContext {
        self.base.semantic_context()
    }

    pub fn take_context(&mut self) -> &'ephemeral PredictionContext<'ephemeral> {
        self.base.take_context()
    }

    pub fn set_context(&mut self, context: &'ephemeral PredictionContext<'ephemeral>) {
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

    pub fn has_passed_through_non_greedy_decision(&self) -> bool {
        self.passed_through_non_greedy_decision
    }
}
