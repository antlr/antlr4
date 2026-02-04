use std::hash::{Hash, Hasher};

use fxhash::{hash64, FxHasher64};

use crate::char_stream::CharStream;
use crate::lexer::Lexer;
use crate::lexer_action::LexerAction;
use crate::lexer_action::LexerAction::LexerIndexedCustomAction;
use crate::token_factory::TokenFactory;

#[derive(Clone, Eq, PartialEq, Debug)]
pub(crate) struct LexerActionExecutor {
    cached_hash: u64,
    lexer_actions: Box<[LexerAction]>,
}

impl Hash for LexerActionExecutor {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.cached_hash)
    }
}

impl LexerActionExecutor {
    pub(crate) fn new_copy_append(
        old: Option<&Self>,
        lexer_action: LexerAction,
    ) -> LexerActionExecutor {
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
            let new_actions = lexer_actions
                .iter()
                .cloned()
                .chain(std::iter::once(lexer_action))
                .collect::<Box<[_]>>();

            LexerActionExecutor {
                lexer_actions: new_actions,
                cached_hash: new_hash,
            }
        } else {
            LexerActionExecutor {
                cached_hash: { hash64(&lexer_action) },
                lexer_actions: Box::new([lexer_action]),
            }
        }
    }

    pub fn fix_offset_before_match(mut self, offset: isize) -> LexerActionExecutor {
        for action in self.lexer_actions.iter_mut() {
            match action {
                LexerAction::LexerIndexedCustomAction { .. } => {}
                _ => {
                    if action.is_position_dependent() {
                        *action = LexerIndexedCustomAction {
                            offset,
                            action: Box::new(action.clone()),
                        };
                    }
                }
            }
        }
        self
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

    //    fn hash(&self) -> int { unimplemented!() }
}
