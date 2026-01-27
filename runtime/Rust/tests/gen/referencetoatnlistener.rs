#![allow(nonstandard_style)]
// Generated from ReferenceToATN.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::ParseTreeListener;
use super::referencetoatnparser::*;

pub trait ReferenceToATNListener<'input, 'arena> : ParseTreeListener<'input, 'arena, ReferenceToATNParserContextNode<'input, 'arena>>
where
    'input: 'arena,
{
    /// Enter a parse tree produced by {@link ReferenceToATNParser#a}.
    /// @param ctx the parse tree
    fn enter_a(&mut self, _ctx: &AContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link ReferenceToATNParser#a}.
    /// @param ctx the parse tree
    fn exit_a(&mut self, _ctx: &AContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

}
