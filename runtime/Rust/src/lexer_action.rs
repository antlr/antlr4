use std::hash::Hash;

use crate::lexer::Lexer;

pub(crate) const LEXER_ACTION_TYPE_CHANNEL: i32 = 0;
pub(crate) const LEXER_ACTION_TYPE_CUSTOM: i32 = 1;
pub(crate) const LEXER_ACTION_TYPE_MODE: i32 = 2;
pub(crate) const LEXER_ACTION_TYPE_MORE: i32 = 3;
pub(crate) const LEXER_ACTION_TYPE_POP_MODE: i32 = 4;
pub(crate) const LEXER_ACTION_TYPE_PUSH_MODE: i32 = 5;
pub(crate) const LEXER_ACTION_TYPE_SKIP: i32 = 6;
pub(crate) const LEXER_ACTION_TYPE_TYPE: i32 = 7;

#[allow(clippy::enum_variant_names)]
#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub(crate) enum LexerAction {
    LexerChannelAction(i32),
    LexerCustomAction {
        rule_index: i32,
        action_index: i32,
    },
    LexerModeAction(i32),
    LexerMoreAction,
    LexerPopModeAction,
    LexerPushModeAction(i32),
    LexerSkipAction,
    LexerTypeAction(i32),
    LexerIndexedCustomAction {
        offset: isize,
        action: Box<LexerAction>,
    },
}

impl LexerAction {
    //    fn get_action_type(&self) -> i32 {
    //        unimplemented!()
    ////        unsafe {discriminant_value(self)} as i32
    //    }
    pub fn is_position_dependent(&self) -> bool {
        matches!(
            self,
            LexerAction::LexerCustomAction { .. } | LexerAction::LexerIndexedCustomAction { .. }
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
            &LexerAction::LexerChannelAction(channel) => lexer.set_channel(channel),
            &LexerAction::LexerCustomAction {
                rule_index,
                action_index,
            } => {
                lexer.action(None, rule_index, action_index);
            }
            &LexerAction::LexerModeAction(mode) => lexer.set_mode(mode as usize),
            &LexerAction::LexerMoreAction => lexer.more(),
            &LexerAction::LexerPopModeAction => {
                lexer.pop_mode();
            }
            &LexerAction::LexerPushModeAction(mode) => lexer.push_mode(mode as usize),
            &LexerAction::LexerSkipAction => lexer.skip(),
            &LexerAction::LexerTypeAction(ty) => lexer.set_type(ty),
            LexerAction::LexerIndexedCustomAction { action, .. } => action.execute(lexer),
        }
    }
}
