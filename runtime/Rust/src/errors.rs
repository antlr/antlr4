//! Error types
use crate::atn_simulator::IATNSimulator;
use crate::interval_set::IntervalSet;
use crate::parser::Parser;
use crate::rule_context::states_stack;
use crate::token::{OwningToken, Token};
use crate::token_factory::TokenFactory;
use crate::transition::Transition;
use std::error::Error;
use std::fmt;
use std::fmt::Formatter;
use std::fmt::{Debug, Display};
use std::ops::Deref;
use std::sync::Arc;

/// Main ANTLR4 Rust runtime error
#[derive(Debug, Clone)]
pub enum ANTLRErrorKind {
    /// Returned from Lexer when it fails to find matching token type for current input
    ///
    /// Usually Lexers contain last rule that captures all invalid tokens like:
    /// ```text
    /// ERROR_TOKEN: . ;
    /// ```
    /// to prevent lexer from throwing errors and have all error handling in parser.
    LexerNoAltError {
        /// Index at which error has happened
        start_index: isize,
    },

    /// Indicates that the parser could not decide which of two or more paths
    /// to take based upon the remaining input. It tracks the starting token
    /// of the offending input and also knows where the parser was
    /// in the various paths when the error. Reported by reportNoViableAlternative()
    NoAltError(NoViableAltError),

    /// This signifies any kind of mismatched input exceptions such as
    /// when the current input does not match the expected token.
    InputMismatchError(InputMisMatchError),

    /// A semantic predicate failed during validation. Validation of predicates
    /// occurs when normally parsing the alternative just like matching a token.
    /// Disambiguating predicate evaluation occurs when we test a predicate during
    /// prediction.
    PredicateError(FailedPredicateError),

    /// Internal error. Or user provided type returned data that is
    /// incompatible with current parser state
    IllegalStateError(String),

    CustomError(String),

    /// Unrecoverable error. Indicates that error should not be processed by parser/error strategy
    /// and it should abort parsing and immediately return to caller.
    FallThrough(Arc<dyn Error + Send + Sync + 'static>),

    /// Potentially recoverable error.
    /// Used to allow user to emit his own errors from parser actions or from custom error strategy.
    /// Parser will try to recover with provided `ErrorStrategy`
    OtherError(Arc<dyn Error + Send + Sync + 'static>),
}

#[derive(Debug, Clone)]
pub struct ANTLRError(pub Box<ANTLRErrorKind>);

impl Display for ANTLRError {
    fn fmt(&self, _f: &mut Formatter<'_>) -> fmt::Result {
        <Self as Debug>::fmt(self, _f)
    }
}

impl Error for ANTLRError {
    fn source(&self) -> Option<&(dyn Error + 'static)> {
        match self.0.as_ref() {
            ANTLRErrorKind::FallThrough(x) => Some(x.as_ref()),
            ANTLRErrorKind::OtherError(x) => Some(x.as_ref()),
            _ => None,
        }
    }
}

impl From<ANTLRErrorKind> for ANTLRError {
    fn from(value: ANTLRErrorKind) -> Self {
        ANTLRError(Box::new(value))
    }
}

impl From<Box<dyn Error + Send + Sync + 'static>> for ANTLRError {
    fn from(value: Box<dyn Error + Send + Sync + 'static>) -> Self {
        ANTLRErrorKind::OtherError(Arc::from(value)).into()
    }
}

impl AsRef<ANTLRErrorKind> for ANTLRError {
    fn as_ref(&self) -> &ANTLRErrorKind {
        self.0.as_ref()
    }
}

impl Deref for ANTLRError {
    type Target = ANTLRErrorKind;

    fn deref(&self) -> &Self::Target {
        self.0.as_ref()
    }
}

impl ANTLRError {
    pub fn custom_error(msg: String) -> Self {
        ANTLRErrorKind::CustomError(msg).into()
    }

    pub fn lexer_no_alt(start_index: isize) -> Self {
        ANTLRErrorKind::LexerNoAltError { start_index }.into()
    }

    pub fn illegal_state(msg: String) -> Self {
        ANTLRErrorKind::IllegalStateError(msg).into()
    }

    pub fn fall_through<E: Error + Send + Sync + 'static>(err: E) -> Self {
        ANTLRErrorKind::FallThrough(Arc::new(err)).into()
    }

    pub fn no_alt<'input, 'arena, TF, P>(recog: &mut P) -> Self
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        ANTLRErrorKind::NoAltError(NoViableAltError {
            base: BaseRecognitionError {
                message: "".to_string(),
                offending_token: OwningToken::from(recog.get_current_token() as &dyn Token),
                offending_state: recog.get_state(),
                // ctx: recog.get_parser_rule_context().clone(),
                states_stack: states_stack(recog.get_current_context()).collect(),
            },
            start_token: OwningToken::from(recog.get_current_token() as &dyn Token),
            //            ctx: recog.get_parser_rule_context().clone()
        })
        .into()
    }

    pub fn no_alt_full<'input, 'arena, TF, P>(
        recog: &mut P,
        start_token: OwningToken,
        offending_token: OwningToken,
    ) -> Self
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        ANTLRErrorKind::NoAltError(NoViableAltError {
            base: BaseRecognitionError {
                message: "".to_string(),
                offending_token,
                offending_state: recog.get_state(),
                states_stack: states_stack(recog.get_current_context()).collect(), // ctx: recog.get_parser_rule_context().clone(),
            },
            start_token,
            //            ctx
        })
        .into()
    }

    pub fn input_mismatch<'input, 'arena, TF, P>(recognizer: &mut P) -> Self
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        ANTLRErrorKind::InputMismatchError(InputMisMatchError {
            base: BaseRecognitionError::new(recognizer),
        })
        .into()
    }

    pub fn input_mismatch_with_state<'input, 'arena, TF, P>(
        recognizer: &mut P,
        offending_state: i32,
        ctx: &'arena P::Node,
    ) -> Self
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let mut a = InputMisMatchError {
            base: BaseRecognitionError::new(recognizer),
        };
        // a.base.ctx = ctx;
        a.base.offending_state = offending_state;
        a.base.states_stack = states_stack(ctx).collect();
        ANTLRErrorKind::InputMismatchError(a).into()
    }

    pub fn failed_predicate<'input, 'arena, TF, P>(
        recog: &mut P,
        predicate: Option<String>,
        msg: Option<String>,
    ) -> Self
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        let tr = recog.get_interpreter().atn().states[recog.get_state() as usize]
            .get_transitions()
            .first()
            .unwrap();
        let (rule_index, _) = if let Transition::Predicate(pr) = tr {
            (pr.rule_index, pr.pred_index)
        } else {
            (0, 0)
        };

        ANTLRErrorKind::PredicateError(FailedPredicateError {
            base: BaseRecognitionError {
                message: msg.unwrap_or_else(|| {
                    format!(
                        "failed predicate: {}",
                        predicate.as_deref().unwrap_or("None")
                    )
                }),
                offending_token: OwningToken::from(recog.get_current_token() as &dyn Token),
                offending_state: recog.get_state(),
                states_stack: states_stack(recog.get_current_context()).collect(), // ctx: recog.get_parser_rule_context().clone()
            },
            rule_index,
            predicate: predicate.unwrap_or_default(),
        })
        .into()
    }

    pub fn is_recoverable(&self) -> bool {
        !matches!(self.0.as_ref(), ANTLRErrorKind::FallThrough(_))
    }

    /// Returns first token that caused parser to fail.
    pub fn get_offending_token(&self) -> Option<&OwningToken> {
        Some(match self.0.as_ref() {
            ANTLRErrorKind::NoAltError(e) => &e.base.offending_token,
            ANTLRErrorKind::InputMismatchError(e) => &e.base.offending_token,
            ANTLRErrorKind::PredicateError(e) => &e.base.offending_token,
            _ => return None,
        })
    }
}

//impl ANTLRError {
//    fn get_expected_tokens(&self, _atn: &ATN) -> IntervalSet {
//        atn.get_expected_tokens(se)
//        unimplemented!()
//    }
//}

/// Common part of ANTLR parser errors
#[derive(Debug, Clone)]
#[allow(missing_docs)]
pub struct BaseRecognitionError {
    pub message: String,
    //    recognizer: Box<Recognizer>,
    pub offending_token: OwningToken,
    pub offending_state: i32,
    states_stack: Vec<i32>, // ctx: Rc<dyn ParserRuleContext>
                            //    input: Box<IntStream>
}

impl BaseRecognitionError {
    /// Returns tokens that were expected by parser in error place
    pub fn get_expected_tokens<'input, 'arena, TF, P>(&self, recognizer: &P) -> IntervalSet
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        recognizer
            .get_interpreter()
            .atn()
            .get_expected_tokens(self.offending_state, self.states_stack.iter().copied())
    }

    fn new<'input, 'arena, TF, P>(recog: &mut P) -> BaseRecognitionError
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        BaseRecognitionError {
            message: "".to_string(),
            offending_token: OwningToken::from(recog.get_current_token() as &dyn Token),
            offending_state: recog.get_state(),
            // ctx: recog.get_parser_rule_context().clone(),
            states_stack: states_stack(recog.get_current_context()).collect(),
        }
    }
}

/// See `ANTLRError::NoAltError`
#[derive(Debug, Clone)]
#[allow(missing_docs)]
pub struct NoViableAltError {
    pub base: BaseRecognitionError,
    pub start_token: OwningToken,
    //    ctx: Rc<dyn ParserRuleContext>,
    //    dead_end_configs: BaseATNConfigSet,
}

/// See `ANTLRError::InputMismatchError`
#[derive(Debug, Clone)]
#[allow(missing_docs)]
pub struct InputMisMatchError {
    pub base: BaseRecognitionError,
}

/// See `ANTLRError::PredicateError`
#[derive(Debug, Clone)]
#[allow(missing_docs)]
pub struct FailedPredicateError {
    pub base: BaseRecognitionError,
    pub rule_index: i32,
    pub predicate: String,
}
