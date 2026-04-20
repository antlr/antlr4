use bit_set::BitSet;
use hashbrown::DefaultHashBuilder;
use hashbrown::HashSet;

use crate::atn::ATN;
use crate::atn_config::ATNConfig;
use crate::atn_state::ATNState;
use crate::atn_state::ATNStateRef;
use crate::interval_set::IntervalSetBuf;
use crate::prediction_context::PredictionContext;
use crate::prediction_context::PredictionContextRef;
use crate::token::{TOKEN_EOF, TOKEN_EPSILON, TOKEN_INVALID_TYPE, TOKEN_MIN_USER_TOKEN_TYPE};
use crate::transition::TransitionType;
use crate::tree::RuleNode;

pub struct LL1Analyzer<'a> {
    atn: &'a ATN,
}

impl LL1Analyzer<'_> {
    pub fn new(atn: &ATN) -> LL1Analyzer<'_> {
        LL1Analyzer { atn }
    }

    //    fn get_decision_lookahead(&self, _s: &dyn ATNState) -> &Vec<IntervalSet> { unimplemented!() }

    pub fn look<'input, 'arena, Node>(
        &self,
        s: ATNStateRef,
        stop_state: Option<ATNStateRef>,
        ctx: Option<&'arena Node>,
    ) -> IntervalSetBuf
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
    {
        let arena = bumpalo::Bump::new();

        let mut r = IntervalSetBuf::new();
        let look_ctx = ctx.map(|x| PredictionContext::from_rule_context(self.atn, x, &arena));
        let mut looks_busy: HashSet<ATNConfig<'_>, _, &bumpalo::Bump> = HashSet::new_in(&arena);
        let mut called_rule_stack = BitSet::new();

        self.look_work(
            &arena,
            s,
            stop_state,
            look_ctx,
            &mut r,
            &mut looks_busy,
            &mut called_rule_stack,
            true,
            true,
        );
        r
    }

    #[allow(clippy::too_many_arguments)]
    fn look_work<'ephemeral>(
        &self,
        arena: &'ephemeral bumpalo::Bump,
        s: ATNStateRef,
        stop_state: Option<ATNStateRef>,
        ctx: Option<PredictionContextRef<'ephemeral>>,
        look: &mut IntervalSetBuf,
        look_busy: &mut HashSet<ATNConfig<'ephemeral>, DefaultHashBuilder, &bumpalo::Bump>,
        called_rule_stack: &mut BitSet,
        see_thru_preds: bool,
        add_eof: bool,
    ) {
        let c = ATNConfig::new(s, 0, ctx);
        if !look_busy.insert(c) {
            return;
        }

        if Some(s.get_state_number()) == stop_state.map(|x| x.get_state_number()) {
            match ctx {
                None => {
                    look.add_one(TOKEN_EPSILON);
                    return;
                }
                Some(x) if x.is_empty() && add_eof => {
                    look.add_one(TOKEN_EOF);
                    return;
                }
                _ => {}
            }
        }

        if let ATNState::RuleStop(_) = *s {
            match ctx {
                None => {
                    look.add_one(TOKEN_EPSILON);
                    return;
                }
                Some(x) if x.is_empty() && add_eof => {
                    look.add_one(TOKEN_EOF);
                    return;
                }
                Some(ctx) if ctx != PredictionContextRef::new_empty() => {
                    let removed = called_rule_stack.contains(s.get_rule_index() as usize);
                    called_rule_stack.remove(s.get_rule_index() as usize);
                    for i in 0..ctx.length() {
                        self.look_work(
                            arena,
                            ctx.get_return_state(i),
                            stop_state,
                            ctx.get_parent(i),
                            look,
                            look_busy,
                            called_rule_stack,
                            see_thru_preds,
                            add_eof,
                        )
                    }
                    if removed {
                        called_rule_stack.insert(s.get_rule_index() as usize);
                    }

                    return;
                }
                _ => {}
            }
        }

        for tr in s.get_transitions() {
            let target = tr.get_target();
            match tr.transition_type() {
                TransitionType::Rule => {
                    let rule_tr = tr.try_as::<crate::transition::RuleTransition>().unwrap();

                    if called_rule_stack.contains(target.get_rule_index() as usize) {
                        continue;
                    }

                    let new_ctx = PredictionContext::new_singleton(ctx, rule_tr.follow_state);
                    let new_ctx = arena.alloc(new_ctx);

                    called_rule_stack.insert(target.get_rule_index() as usize);
                    self.look_work(
                        arena,
                        target,
                        stop_state,
                        Some(new_ctx.into()),
                        look,
                        look_busy,
                        called_rule_stack,
                        see_thru_preds,
                        add_eof,
                    );
                    called_rule_stack.remove(target.get_rule_index() as usize);
                }
                TransitionType::Predicate | TransitionType::PrecedencePredicate => {
                    if see_thru_preds {
                        self.look_work(
                            arena,
                            target,
                            stop_state,
                            ctx,
                            look,
                            look_busy,
                            called_rule_stack,
                            see_thru_preds,
                            add_eof,
                        )
                    } else {
                        look.add_one(TOKEN_INVALID_TYPE);
                    }
                }
                TransitionType::Wildcard => {
                    look.add_range(TOKEN_MIN_USER_TOKEN_TYPE, self.atn.max_token_type)
                }
                _ if tr.is_epsilon() => self.look_work(
                    arena,
                    target,
                    stop_state,
                    ctx,
                    look,
                    look_busy,
                    called_rule_stack,
                    see_thru_preds,
                    add_eof,
                ),
                TransitionType::NotSet => {
                    let set = tr.get_label().unwrap();
                    look.add_set(
                        &set.complement(TOKEN_MIN_USER_TOKEN_TYPE, self.atn.max_token_type),
                    )
                }
                _ => {
                    if let Some(set) = tr.get_label() {
                        look.add_set(set)
                    }
                }
            }
        }
    }
}
