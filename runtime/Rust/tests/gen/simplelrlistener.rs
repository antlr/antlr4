#![allow(nonstandard_style)]
// Generated from SimpleLR.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::ParseTreeListener;
use super::simplelrparser::*;

pub trait SimpleLRListener<'arena> : ParseTreeListener<'arena, SimpleLRParserNodeKind>
{
    /// Enter a parse tree produced by {@link SimpleLRParser#s}.
    /// @param ctx the parse tree
    fn enter_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link SimpleLRParser#s}.
    /// @param ctx the parse tree
    fn exit_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link SimpleLRParser#a}.
    /// @param ctx the parse tree
    fn enter_a<'input: 'arena>(&mut self, _ctx: &AContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link SimpleLRParser#a}.
    /// @param ctx the parse tree
    fn exit_a<'input: 'arena>(&mut self, _ctx: &AContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

}
