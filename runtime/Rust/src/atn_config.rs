use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use murmur3::murmur3_32::MurmurHasher;

use crate::atn_config::ATNConfigType::LexerATNConfig;
use crate::atn_state::{ATNState, ATNStateRef, DecisionState};
use crate::dfa::ScopeExt;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;

#[derive(Clone)]
pub struct ATNConfig<'ephemeral> {
    precedence_filter_suppressed: bool,
    state: ATNStateRef,
    alt: i32,
    //todo maybe option is unnecessary and PredictionContext::EMPTY would be enough
    context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    semantic_context: &'ephemeral SemanticContext,
    pub reaches_into_outer_context: i32,
    pub(crate) config_type: ATNConfigType<'ephemeral>,
}

impl Eq for ATNConfig<'_> {}

impl PartialEq for ATNConfig<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.get_state() == other.get_state()
            && self.get_alt() == other.get_alt()
            && crate::prediction_context::opt_eq((&self.context, &other.context))
            && self.get_type() == other.get_type()
            && self.semantic_context == other.semantic_context
            && self.precedence_filter_suppressed == other.precedence_filter_suppressed
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
        if let LexerATNConfig {
            lexer_action_executor,
            passed_through_non_greedy_decision,
        } = &self.config_type
        {
            state.write_i32(if *passed_through_non_greedy_decision {
                1
            } else {
                0
            });
            match lexer_action_executor {
                None => state.write_i32(0),
                Some(ex) => ex.hash(state),
            }
        }
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

#[derive(Eq, PartialEq, Clone, Debug)]
pub(crate) enum ATNConfigType<'ephemeral> {
    BaseATNConfig,
    LexerATNConfig {
        lexer_action_executor: Option<&'ephemeral LexerActionExecutor>,
        passed_through_non_greedy_decision: bool,
    },
}

impl<'ephemeral> ATNConfig<'ephemeral> {
    pub(crate) fn get_lexer_executor(&self) -> Option<&LexerActionExecutor> {
        match &self.config_type {
            ATNConfigType::BaseATNConfig => None,
            ATNConfigType::LexerATNConfig {
                lexer_action_executor,
                ..
            } => lexer_action_executor.as_deref(),
        }
    }

    pub fn default_hash(&self) -> u64 {
        MurmurHasher::default().convert_with(|mut x| {
            self.hash(&mut x);
            x.finish()
        })
    }

    pub fn new(
        state: ATNStateRef,
        alt: i32,
        context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    ) -> Self {
        ATNConfig {
            precedence_filter_suppressed: false,
            state,
            alt,
            context,
            semantic_context: &SemanticContext::NONE,
            reaches_into_outer_context: 0,
            config_type: ATNConfigType::BaseATNConfig,
        }
    }

    pub fn new_lexer(
        state: ATNStateRef,
        alt: i32,
        context: &'ephemeral PredictionContext<'ephemeral>,
    ) -> Self {
        let mut atnconfig = Self::new(state, alt, Some(context));
        atnconfig.config_type = ATNConfigType::LexerATNConfig {
            lexer_action_executor: None,
            passed_through_non_greedy_decision: false,
        };
        atnconfig
    }

    pub fn with_semantic_context(self, semantic_context: &'ephemeral SemanticContext) -> Self {
        ATNConfig {
            semantic_context,
            ..self
        }
    }

    pub fn with_state(self, state: ATNStateRef) -> Self {
        let config_type = if let ATNConfigType::LexerATNConfig {
            lexer_action_executor,
            passed_through_non_greedy_decision,
        } = self.config_type
        {
            ATNConfigType::LexerATNConfig {
                lexer_action_executor,
                passed_through_non_greedy_decision: passed_through_non_greedy_decision
                    || matches!(
                        *state,
                        ATNState::Decision(DecisionState {
                            nongreedy: true,
                            ..
                        })
                    ),
            }
        } else {
            self.config_type
        };
        Self {
            state,
            config_type,
            ..self
        }
    }

    pub fn with_prediction_context(
        self,
        context: Option<&'ephemeral PredictionContext<'ephemeral>>,
    ) -> Self {
        ATNConfig { context, ..self }
    }

    pub(crate) fn with_lexer_executor(
        self,
        lexer_action_executor: Option<&'ephemeral LexerActionExecutor>,
    ) -> Self {
        let config_type = if let ATNConfigType::LexerATNConfig {
            passed_through_non_greedy_decision,
            ..
        } = self.config_type
        {
            ATNConfigType::LexerATNConfig {
                lexer_action_executor,
                passed_through_non_greedy_decision,
            }
        } else {
            self.config_type
        };
        ATNConfig {
            config_type,
            ..self
        }
    }

    pub(crate) fn make_static(
        self,
        prediction_context: Option<&'static PredictionContext<'static>>,
        semantic_context: &'static SemanticContext,
        config_type: ATNConfigType<'static>,
    ) -> ATNConfig<'static> {
        ATNConfig {
            precedence_filter_suppressed: self.precedence_filter_suppressed,
            state: self.state,
            alt: self.alt,
            context: prediction_context,
            semantic_context,
            reaches_into_outer_context: self.reaches_into_outer_context,
            config_type,
        }
    }

    pub fn get_state(&self) -> ATNStateRef {
        self.state
    }

    pub fn get_alt(&self) -> i32 {
        self.alt
    }

    pub(crate) fn get_type(&self) -> &ATNConfigType<'ephemeral> {
        &self.config_type
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

    pub fn is_precedence_filter_suppressed(&self) -> bool {
        self.precedence_filter_suppressed
    }

    pub fn set_precedence_filter_suppressed(&mut self, _v: bool) {
        self.precedence_filter_suppressed = _v;
    }
}
