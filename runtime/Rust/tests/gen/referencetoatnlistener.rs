#![allow(nonstandard_style)]
// Generated from ReferenceToATN.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::token::Token;
use dbt_antlr4::tree::ParseTreeListener;
use super::referencetoatnparser::*;

pub trait ReferenceToATNListener<'arena, Tok> : ParseTreeListener<'arena, ReferenceToATNParserNodeKind, Tok>
where
    Tok: Token + 'arena,
{
    /// Enter a parse tree produced by {@link ReferenceToATNParser#a}.
    /// @param ctx the parse tree
    fn enter_a<'input: 'arena>(&mut self, _ctx: &AContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link ReferenceToATNParser#a}.
    /// @param ctx the parse tree
    fn exit_a<'input: 'arena>(&mut self, _ctx: &AContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

}
