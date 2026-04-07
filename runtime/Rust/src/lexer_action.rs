use std::hash::Hash;

use crate::{atn_config_set::ConfigSet, dfa::DFAStateStore, lexer::Lexer};

pub(crate) const LEXER_ACTION_TYPE_CHANNEL: i32 = 0;
pub(crate) const LEXER_ACTION_TYPE_CUSTOM: i32 = 1;
pub(crate) const LEXER_ACTION_TYPE_MODE: i32 = 2;
pub(crate) const LEXER_ACTION_TYPE_MORE: i32 = 3;
pub(crate) const LEXER_ACTION_TYPE_POP_MODE: i32 = 4;
pub(crate) const LEXER_ACTION_TYPE_PUSH_MODE: i32 = 5;
pub(crate) const LEXER_ACTION_TYPE_SKIP: i32 = 6;
pub(crate) const LEXER_ACTION_TYPE_TYPE: i32 = 7;

#[allow(clippy::enum_variant_names)]
#[derive(Clone, Default, Eq, PartialEq, Debug, Hash)]
pub(crate) enum LexerAction<'ephemeral> {
    Channel(i32),
    Custom {
        rule_index: i32,
        action_index: i32,
    },
    Mode(i32),
    More,
    PopMode,
    PushMode(i32),
    #[default]
    Skip,
    Type(i32),
    IndexedCustom(&'ephemeral LexerIndexedCustomAction<'ephemeral>),
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub(crate) struct LexerIndexedCustomAction<'ephemeral> {
    pub(crate) offset: isize,
    pub(crate) action: LexerAction<'ephemeral>,
}

impl<'ephemeral> LexerAction<'ephemeral> {
    //    fn get_action_type(&self) -> i32 {
    //        unimplemented!()
    ////        unsafe {discriminant_value(self)} as i32
    //    }
    pub fn is_position_dependent(&self) -> bool {
        matches!(
            self,
            LexerAction::Custom { .. } | LexerAction::IndexedCustom { .. }
        )
    }
    pub(crate) fn execute<'input, 'arena, Input, TF, L>(&self, lexer: &mut L)
    where
        'input: 'arena,
        L: Lexer<'input, 'arena, Input, TF>,
        Input: crate::char_stream::CharStream<'input>,
        TF: crate::token_factory::TokenFactory<'input, 'arena> + 'arena,
    {
        match self {
            &LexerAction::Channel(channel) => lexer.set_channel(channel),
            &LexerAction::Custom {
                rule_index,
                action_index,
            } => {
                lexer.action(None, rule_index, action_index);
            }
            &LexerAction::Mode(mode) => lexer.set_mode(mode as usize),
            &LexerAction::More => lexer.more(),
            &LexerAction::PopMode => {
                lexer.pop_mode();
            }
            &LexerAction::PushMode(mode) => lexer.push_mode(mode as usize),
            &LexerAction::Skip => lexer.skip(),
            &LexerAction::Type(ty) => lexer.set_type(ty),
            LexerAction::IndexedCustom(action) => action.action.execute(lexer),
        }
    }

    pub(crate) fn promote<'sim, CS>(&self, dfa: &DFAStateStore<'sim, CS>) -> LexerAction<'sim>
    where
        CS: ConfigSet<'sim> + 'sim,
    {
        match self {
            LexerAction::IndexedCustom(action) if dfa.contains_ref(action) => {
                // Safety: the action is already in the target arena, so can
                // live as long as the target lifetime:
                unsafe { std::mem::transmute::<Self, LexerAction<'sim>>(self.clone()) }
            }
            LexerAction::IndexedCustom(action) => {
                LexerAction::IndexedCustom(dfa.alloc(LexerIndexedCustomAction {
                    offset: action.offset,
                    action: action.action.promote(dfa),
                }))
            }
            // Safety: these don't hold any references, so effectively 'static
            _ => unsafe { std::mem::transmute::<Self, LexerAction<'sim>>(self.clone()) },
        }
    }
}
