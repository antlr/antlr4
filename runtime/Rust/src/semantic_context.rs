use std::borrow::Cow::{Borrowed, Owned};
use std::borrow::{Borrow, Cow};
use std::cmp::Ordering;
use std::sync::{Arc, LazyLock};

use hashbrown::{DefaultHashBuilder, HashSet};

use crate::parser::Parser;
use crate::token_factory::TokenFactory;

//pub trait SemanticContext:Sync + Send {
///    fn evaluate(&self, parser: &Recognizer, outerContext: &RuleContext) -> bool;
///    fn eval_precedence(&self, parser: &Recognizer, outerContext: &RuleContext, ) -> Box<dyn SemanticContext>;
//}

// fn empty() -> SemanticContext {
//     SemanticContext::Predicate {
//         rule_index: -1,
//         pred_index: -1,
//         is_ctx_dependent: false,
//     }
// }

#[derive(Clone, Eq, PartialEq, Hash, Debug)]
pub enum SemanticContext {
    Predicate {
        rule_index: i32,
        pred_index: i32,
        is_ctx_dependent: bool,
    },
    Precedence(i32),
    And(Arc<[SemanticContext]>),
    Or(Arc<[SemanticContext]>),
}

impl SemanticContext {
    pub const NONE: SemanticContext = SemanticContext::Predicate {
        rule_index: -1,
        pred_index: -1,
        is_ctx_dependent: false,
    };

    pub fn none() -> Arc<SemanticContext> {
        static NONE: LazyLock<Arc<SemanticContext>> =
            LazyLock::new(|| Arc::new(SemanticContext::NONE));
        NONE.clone()
    }

    pub(crate) fn evaluate<'ephemeral, 'input, 'arena, TF, P>(
        &self,
        ephemerals: &'ephemeral bumpalo::Bump,
        parser: &mut P,
        outer_context: &'arena P::Node,
    ) -> bool
    where
        'input: 'arena,
        TF: TokenFactory<'input, 'arena> + 'arena,
        P: Parser<'input, 'arena, TF>,
    {
        match self {
            SemanticContext::Predicate {
                rule_index,
                pred_index,
                is_ctx_dependent,
            } => {
                let _localctx = if *is_ctx_dependent {
                    Some(outer_context)
                } else {
                    None
                };
                parser.sempred(_localctx, *rule_index, *pred_index)
            }
            SemanticContext::Precedence(prec) => parser.precpred(Some(outer_context), *prec),
            SemanticContext::And(ops) => ops
                .iter()
                .all(|sem| sem.evaluate(ephemerals, parser, outer_context)),
            SemanticContext::Or(ops) => ops
                .iter()
                .any(|sem| sem.evaluate(ephemerals, parser, outer_context)),
        }
    }

    pub(crate) fn eval_precedence<'a, 'ephemeral, 'input, 'arena, TF, P>(
        &'a self,
        ephemerals: &'ephemeral bumpalo::Bump,
        parser: &P,
        outer_context: &'arena P::Node,
    ) -> Option<Cow<'a, SemanticContext>>
    where
        'input: 'arena,
        P: Parser<'input, 'arena, TF>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        match self {
            SemanticContext::Predicate { .. } => Some(Borrowed(self)),
            SemanticContext::Precedence(prec) => {
                if parser.precpred(Some(outer_context), *prec) {
                    Some(Owned(Self::NONE))
                } else {
                    None
                }
            }
            SemanticContext::Or(ops) => {
                let mut differs = false;
                let mut operands = bumpalo::collections::Vec::new_in(ephemerals);
                for context in ops.iter() {
                    let evaluated = context.eval_precedence(ephemerals, parser, outer_context);
                    differs |= evaluated.is_some() && context == evaluated.as_deref().unwrap();

                    if let Some(evaluated) = evaluated {
                        if *evaluated == Self::NONE {
                            return Some(Owned(Self::NONE));
                        } else {
                            operands.push(evaluated);
                        }
                    }
                }

                if !differs {
                    return Some(Borrowed(self));
                }

                if operands.is_empty() {
                    return None;
                }

                let mut operands = operands.drain(..);
                let result = operands.next().unwrap();
                Some(operands.fold(result, |acc, it| {
                    Owned(SemanticContext::or(ephemerals, Some(acc), Some(it)))
                }))
            }
            SemanticContext::And(ops) => {
                let mut differs = false;
                let mut operands = bumpalo::collections::Vec::new_in(ephemerals);
                for context in ops.iter() {
                    let evaluated = context.eval_precedence(ephemerals, parser, outer_context);
                    differs |= evaluated.is_some() && context == evaluated.as_deref().unwrap();

                    if let Some(evaluated) = evaluated {
                        if *evaluated != Self::NONE {
                            operands.push(evaluated);
                        }
                    } else {
                        return None;
                    }
                }

                if !differs {
                    return Some(Borrowed(self));
                }

                if operands.is_empty() {
                    return Some(Owned(Self::NONE));
                }

                let mut operands = operands.drain(..);
                let result = operands.next().unwrap();
                Some(operands.fold(result, |acc, it| {
                    Owned(SemanticContext::and(ephemerals, Some(acc), Some(it)))
                }))
            }
        }
    }

    pub fn new_and<'ephemeral>(
        ephemerals: &'ephemeral bumpalo::Bump,
        a: &SemanticContext,
        b: &SemanticContext,
    ) -> SemanticContext {
        let mut operands = HashSet::new_in(ephemerals);
        if let SemanticContext::And(ops) = a {
            operands.extend(ops.iter().cloned())
        } else {
            operands.insert(a.clone());
        }
        if let SemanticContext::And(ops) = b {
            operands.extend(ops.iter().cloned())
        } else {
            operands.insert(b.clone());
        }

        let precedence_predicates = filter_precedence_predicate(ephemerals, &mut operands);
        if !precedence_predicates.is_empty() {
            let reduced = precedence_predicates.iter().min_by(sort_prec_pred);
            operands.insert(reduced.unwrap().clone());
        }

        if operands.len() == 1 {
            return operands.into_iter().next().unwrap();
        }

        SemanticContext::And(operands.into_iter().collect())
    }

    pub fn new_or<'ephemeral>(
        ephemerals: &'ephemeral bumpalo::Bump,
        a: &SemanticContext,
        b: &SemanticContext,
    ) -> SemanticContext {
        let mut operands = HashSet::new_in(ephemerals);
        if let SemanticContext::Or(ops) = a {
            operands.extend(ops.iter().cloned())
        } else {
            operands.insert(a.clone());
        }
        if let SemanticContext::Or(ops) = b {
            ops.iter().for_each(|it| {
                operands.insert(it.clone());
            });
        } else {
            operands.insert(b.clone());
        }

        let precedence_predicates = filter_precedence_predicate(ephemerals, &mut operands);
        if !precedence_predicates.is_empty() {
            let reduced = precedence_predicates.iter().max_by(sort_prec_pred);
            operands.insert(reduced.unwrap().clone());
        }

        if operands.len() == 1 {
            return operands.into_iter().next().unwrap();
        }

        SemanticContext::Or(operands.into_iter().collect())
    }

    pub fn and<'ephemeral>(
        ephemerals: &'ephemeral bumpalo::Bump,
        a: Option<impl Borrow<SemanticContext>>,
        b: Option<impl Borrow<SemanticContext>>,
    ) -> SemanticContext {
        match (a, b) {
            (None, None) => Self::NONE,
            (None, Some(b)) => b.borrow().clone(),
            (Some(a), None) => a.borrow().clone(),
            (Some(a), Some(b)) => {
                let (a, b) = (a.borrow(), b.borrow());
                if *a == Self::NONE {
                    return b.clone();
                }
                if *b == Self::NONE {
                    return a.clone();
                }

                Self::new_and(ephemerals, a, b)
            }
        }
    }

    pub fn or<'ephemeral>(
        ephemerals: &'ephemeral bumpalo::Bump,
        a: Option<impl Borrow<SemanticContext>>,
        b: Option<impl Borrow<SemanticContext>>,
    ) -> SemanticContext {
        match (a, b) {
            (None, None) => Self::NONE,
            (None, Some(b)) => b.borrow().clone(),
            (Some(a), None) => a.borrow().clone(),
            (Some(a), Some(b)) => {
                let (a, b) = (a.borrow(), b.borrow());
                if *a == Self::NONE || *b == Self::NONE {
                    return Self::NONE;
                }

                Self::new_or(ephemerals, a, b)
            }
        }
    }
}

fn sort_prec_pred(a: &&SemanticContext, b: &&SemanticContext) -> Ordering {
    match (*a, *b) {
        (SemanticContext::Precedence(a), SemanticContext::Precedence(b)) => a.cmp(b),
        _ => panic!("should be sorting list of precedence predicates"),
    }
}

fn filter_precedence_predicate<'ephemeral>(
    ephemerals: &'ephemeral bumpalo::Bump,
    collection: &mut HashSet<SemanticContext, DefaultHashBuilder, &bumpalo::Bump>,
) -> bumpalo::collections::Vec<'ephemeral, SemanticContext> {
    let mut result = bumpalo::collections::Vec::new_in(ephemerals);
    collection.retain(|it| {
        if let SemanticContext::Precedence(_) = it {
            result.push(it.clone());
            false
        } else {
            true
        }
    });
    result
}
