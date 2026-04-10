use std::cmp::Ordering;

use hashbrown::{DefaultHashBuilder, HashSet};

use crate::atn_config_set::ConfigSet;
use crate::dfa::DFAStateStore;
use crate::parser::Parser;
use crate::token_factory::TokenFactory;

#[derive(Clone, Eq, PartialEq, Hash, Debug)]
pub enum SemanticContext<'ephemeral> {
    Predicate {
        rule_index: i32,
        pred_index: i32,
        is_ctx_dependent: bool,
    },
    Precedence(i32),
    And(&'ephemeral [SemanticContext<'ephemeral>]),
    Or(&'ephemeral [SemanticContext<'ephemeral>]),
}

impl<'ephemeral> SemanticContext<'ephemeral> {
    pub const NONE: SemanticContext<'static> = SemanticContext::Predicate {
        rule_index: -1,
        pred_index: -1,
        is_ctx_dependent: false,
    };

    pub fn none() -> &'static SemanticContext<'static> {
        &Self::NONE
    }

    pub(crate) fn evaluate<'input, 'arena, TF, P>(
        &self,
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
            SemanticContext::And(ops) => ops.iter().all(|sem| sem.evaluate(parser, outer_context)),
            SemanticContext::Or(ops) => ops.iter().any(|sem| sem.evaluate(parser, outer_context)),
        }
    }

    pub(crate) fn eval_precedence<'a, 'scratch, 'input, 'arena, TF, P>(
        &'a self,
        scratch: &'scratch bumpalo::Bump,
        parser: &P,
        outer_context: &'arena P::Node,
    ) -> Option<&'scratch SemanticContext<'scratch>>
    where
        'a: 'scratch,
        'input: 'arena,
        P: Parser<'input, 'arena, TF>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        match self {
            SemanticContext::Predicate { .. } => Some(self),
            SemanticContext::Precedence(prec) => {
                if parser.precpred(Some(outer_context), *prec) {
                    Some(Self::none())
                } else {
                    None
                }
            }
            SemanticContext::Or(ops) => {
                let mut differs = false;
                let mut operands = bumpalo::collections::Vec::new_in(scratch);
                for context in ops.iter() {
                    let evaluated = context.eval_precedence(scratch, parser, outer_context);
                    differs |= evaluated.is_some() && context == evaluated.unwrap();

                    if let Some(evaluated) = evaluated {
                        if *evaluated == Self::NONE {
                            return Some(Self::none());
                        } else {
                            operands.push(evaluated);
                        }
                    }
                }

                if !differs {
                    return Some(self);
                }

                if operands.is_empty() {
                    return None;
                }

                Some(scratch.alloc(Self::new_or(scratch, operands.as_slice())))
            }
            SemanticContext::And(ops) => {
                let mut differs = false;
                let mut operands = bumpalo::collections::Vec::new_in(scratch);
                for context in ops.iter() {
                    let evaluated = context.eval_precedence(scratch, parser, outer_context);
                    differs |= evaluated.is_some() && context == evaluated.unwrap();

                    if let Some(evaluated) = evaluated {
                        if *evaluated != Self::NONE {
                            operands.push(evaluated);
                        }
                    } else {
                        return None;
                    }
                }

                if !differs {
                    return Some(self);
                }

                if operands.is_empty() {
                    return Some(Self::none());
                }

                Some(scratch.alloc(Self::new_and(scratch, operands.as_slice())))
            }
        }
    }

    pub fn new_and<'scratch>(
        scratch: &'scratch bumpalo::Bump,
        elems: &[&SemanticContext<'scratch>],
    ) -> SemanticContext<'scratch> {
        let mut operands = HashSet::new_in(scratch);
        elems.iter().for_each(|it| {
            if let SemanticContext::And(ops) = it {
                operands.extend(ops.iter().cloned())
            } else {
                operands.insert((*it).clone());
            }
        });

        let precedence_predicates = filter_precedence_predicate(scratch, &mut operands);
        if !precedence_predicates.is_empty() {
            let reduced = precedence_predicates.iter().min_by(sort_prec_pred);
            operands.insert(reduced.unwrap().clone());
        }

        if operands.len() == 1 {
            return operands.into_iter().next().unwrap();
        }

        let operands: &[_] = scratch.alloc_slice_fill_iter(operands);
        SemanticContext::And(operands)
    }

    pub fn new_or<'scratch>(
        scratch: &'scratch bumpalo::Bump,
        elems: &[&SemanticContext<'scratch>],
    ) -> SemanticContext<'scratch> {
        let mut operands = HashSet::new_in(scratch);
        elems.iter().for_each(|it| {
            if let SemanticContext::Or(ops) = it {
                operands.extend(ops.iter().cloned())
            } else {
                operands.insert((*it).clone());
            }
        });

        let precedence_predicates = filter_precedence_predicate(scratch, &mut operands);
        if !precedence_predicates.is_empty() {
            let reduced = precedence_predicates.iter().max_by(sort_prec_pred);
            operands.insert(reduced.unwrap().clone());
        }

        if operands.len() == 1 {
            return operands.into_iter().next().unwrap();
        }

        let operands: &[_] = scratch.alloc_slice_fill_iter(operands);
        SemanticContext::Or(operands)
    }

    pub fn and<'scratch>(
        scratch: &'scratch bumpalo::Bump,
        a: Option<&SemanticContext<'scratch>>,
        b: Option<&SemanticContext<'scratch>>,
    ) -> SemanticContext<'scratch> {
        match (a, b) {
            (None, None) => Self::NONE,
            (None, Some(b)) => b.clone(),
            (Some(a), None) => a.clone(),
            (Some(a), Some(b)) => {
                if *a == Self::NONE {
                    return b.clone();
                }
                if *b == Self::NONE {
                    return a.clone();
                }

                Self::new_and(scratch, &[a, b])
            }
        }
    }

    pub fn or<'scratch>(
        scratch: &'scratch bumpalo::Bump,
        a: Option<&SemanticContext<'scratch>>,
        b: Option<&SemanticContext<'scratch>>,
    ) -> SemanticContext<'scratch> {
        match (a, b) {
            (None, None) => Self::NONE,
            (None, Some(b)) => b.clone(),
            (Some(a), None) => a.clone(),
            (Some(a), Some(b)) => {
                if *a == Self::NONE || *b == Self::NONE {
                    return Self::NONE;
                }

                Self::new_or(scratch, &[a, b])
            }
        }
    }

    pub(crate) fn promote<'sim, CS>(&self, dfa: &DFAStateStore<'sim, CS>) -> SemanticContext<'sim>
    where
        CS: ConfigSet<'sim> + 'sim,
    {
        match self {
            SemanticContext::And(ops) if !dfa.contains_semantic_context_slice(ops) => {
                let promoted: &[_] =
                    dfa.alloc_semantic_context_slice(ops.iter().map(|it| it.promote(dfa)));
                SemanticContext::And(promoted)
            }
            SemanticContext::Or(ops) if !dfa.contains_semantic_context_slice(ops) => {
                let promoted: &[_] =
                    dfa.alloc_semantic_context_slice(ops.iter().map(|it| it.promote(dfa)));
                SemanticContext::Or(promoted)
            }
            _ => unsafe { std::mem::transmute::<Self, SemanticContext<'sim>>(self.clone()) },
        }
    }
}

fn sort_prec_pred(a: &&SemanticContext, b: &&SemanticContext) -> Ordering {
    match (*a, *b) {
        (SemanticContext::Precedence(a), SemanticContext::Precedence(b)) => a.cmp(b),
        _ => panic!("should be sorting list of precedence predicates"),
    }
}

fn filter_precedence_predicate<'scratch, 'ephemeral>(
    scratch: &'scratch bumpalo::Bump,
    collection: &mut HashSet<
        SemanticContext<'ephemeral>,
        DefaultHashBuilder,
        &'scratch bumpalo::Bump,
    >,
) -> bumpalo::collections::Vec<'scratch, SemanticContext<'ephemeral>> {
    let mut result = bumpalo::collections::Vec::new_in(scratch);
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
