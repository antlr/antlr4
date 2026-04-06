use std::hash::{Hash, Hasher};

use fxhash::{hash64, FxHasher64};

use crate::atn_config_set::ConfigSet;
use crate::char_stream::CharStream;
use crate::dfa::DFAStateStore;
use crate::lexer::Lexer;
use crate::lexer_action::LexerAction;
use crate::lexer_action::LexerAction::LexerIndexedCustomAction;
use crate::token_factory::TokenFactory;

#[derive(Clone, Eq, PartialEq, Debug)]
pub(crate) struct LexerActionExecutor<'ephemeral> {
    cached_hash: u64,
    lexer_actions: &'ephemeral [LexerAction<'ephemeral>],
}

impl Hash for LexerActionExecutor<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.cached_hash)
    }
}

impl<'ephemeral> LexerActionExecutor<'ephemeral> {
    pub(crate) fn new_copy_append(
        scratch: &'ephemeral bumpalo::Bump,
        old: Option<&Self>,
        lexer_action: LexerAction<'ephemeral>,
    ) -> LexerActionExecutor<'ephemeral> {
        if let Some(LexerActionExecutor {
            lexer_actions,
            cached_hash,
        }) = old
        {
            let new_hash = {
                let mut hasher = FxHasher64::default();
                hasher.write_u64(*cached_hash);
                lexer_action.hash(&mut hasher);
                hasher.finish()
            };
            let new_actions = scratch.alloc_slice_fill_with(lexer_actions.len() + 1, |i| {
                if i < lexer_actions.len() {
                    lexer_actions[i].clone()
                } else {
                    lexer_action.clone()
                }
            });

            LexerActionExecutor {
                lexer_actions: new_actions,
                cached_hash: new_hash,
            }
        } else {
            LexerActionExecutor {
                cached_hash: { hash64(&lexer_action) },
                lexer_actions: scratch.alloc_slice_fill_iter(std::iter::once(lexer_action)),
            }
        }
    }

    pub fn fix_offset_before_match(
        &self,
        arena: &'ephemeral bumpalo::Bump,
        offset: isize,
    ) -> LexerActionExecutor<'ephemeral> {
        let fixed_actions = arena.alloc_slice_fill_with(self.lexer_actions.len(), |i| {
            let action = self.lexer_actions[i].clone();
            if let LexerAction::LexerIndexedCustomAction { .. } = action {
                action
            } else if action.is_position_dependent() {
                LexerIndexedCustomAction {
                    offset,
                    action: arena.alloc(action),
                }
            } else {
                action
            }
        });
        LexerActionExecutor {
            lexer_actions: fixed_actions,
            cached_hash: self.cached_hash,
        }
    }

    pub fn execute<'input, 'arena, Input, TF>(
        &self,
        lexer: &mut impl Lexer<'input, 'arena, Input, TF>,
        start_index: isize,
    ) where
        'input: 'arena,
        Input: CharStream<'input>,
        TF: TokenFactory<'input, 'arena> + 'arena,
    {
        let mut requires_seek = false;
        let stop_index = lexer.input().index();
        for action in self.lexer_actions.iter() {
            //println!("executing action {:?}",action);
            if let LexerAction::LexerIndexedCustomAction { offset, .. } = action {
                lexer.input().seek(start_index + offset);
                requires_seek = start_index + offset != stop_index;
            } else if action.is_position_dependent() {
                lexer.input().seek(stop_index);
                requires_seek = false
            }
            action.execute(lexer);
        }
        if requires_seek {
            lexer.input().seek(stop_index);
        }
    }

    pub(crate) fn promote<'sim, CS>(
        &self,
        dfa: &DFAStateStore<'sim, CS>,
    ) -> LexerActionExecutor<'sim>
    where
        CS: ConfigSet<'sim> + 'sim,
    {
        if dfa.contains_slice(self.lexer_actions) {
            // Safety: the actions are already in the target arena, so can
            // live as long as the target lifetime:
            return unsafe { std::mem::transmute::<Self, LexerActionExecutor<'sim>>(self.clone()) };
        }

        let promoted_actions = dfa.alloc_slice_fill_with(self.lexer_actions.len(), |i| {
            self.lexer_actions[i].promote(dfa)
        });
        LexerActionExecutor {
            lexer_actions: promoted_actions,
            cached_hash: self.cached_hash,
        }
    }
}
