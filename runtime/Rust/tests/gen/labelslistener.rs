#![allow(nonstandard_style)]
// Generated from Labels.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::token::{CommonToken, Token};
use dbt_antlr4::tree::ParseTreeListener;
use super::labelsparser::*;

pub trait LabelsListener<'arena, Tok = CommonToken<'arena>> : ParseTreeListener<'arena, LabelsParserNodeKind, Tok>
where
    Tok: Token + 'arena,
{
    /// Enter a parse tree produced by {@link LabelsParser#s}.
    /// @param ctx the parse tree
    fn enter_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link LabelsParser#s}.
    /// @param ctx the parse tree
    fn exit_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code add}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_add<'input: 'arena>(&mut self, _ctx: &AddContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code add}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_add<'input: 'arena>(&mut self, _ctx: &AddContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code parens}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_parens<'input: 'arena>(&mut self, _ctx: &ParensContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code parens}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_parens<'input: 'arena>(&mut self, _ctx: &ParensContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code mult}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_mult<'input: 'arena>(&mut self, _ctx: &MultContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code mult}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_mult<'input: 'arena>(&mut self, _ctx: &MultContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code dec}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_dec<'input: 'arena>(&mut self, _ctx: &DecContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code dec}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_dec<'input: 'arena>(&mut self, _ctx: &DecContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code anID}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_anID<'input: 'arena>(&mut self, _ctx: &AnIDContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code anID}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_anID<'input: 'arena>(&mut self, _ctx: &AnIDContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code anInt}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_anInt<'input: 'arena>(&mut self, _ctx: &AnIntContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code anInt}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_anInt<'input: 'arena>(&mut self, _ctx: &AnIntContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code inc}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn enter_inc<'input: 'arena>(&mut self, _ctx: &IncContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code inc}
    /// labeled alternative in {@link LabelsParser#e}.
    /// @param ctx the parse tree
    fn exit_inc<'input: 'arena>(&mut self, _ctx: &IncContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

}
