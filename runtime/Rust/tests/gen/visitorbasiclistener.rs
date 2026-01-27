#![allow(nonstandard_style)]
// Generated from VisitorBasic.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::ParseTreeListener;
use super::visitorbasicparser::*;

pub trait VisitorBasicListener<'input, 'arena> : ParseTreeListener<'input, 'arena, VisitorBasicParserContextNode<'input, 'arena>>
where
    'input: 'arena,
{
    /// Enter a parse tree produced by {@link VisitorBasicParser#s}.
    /// @param ctx the parse tree
    fn enter_s(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link VisitorBasicParser#s}.
    /// @param ctx the parse tree
    fn exit_s(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

}
