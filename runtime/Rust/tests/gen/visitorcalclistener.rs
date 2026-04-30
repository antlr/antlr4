#![allow(nonstandard_style)]
// Generated from VisitorCalc.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::token::Token;
use dbt_antlr4::tree::ParseTreeListener;
use super::visitorcalcparser::*;

pub trait VisitorCalcListener<'arena, Tok> : ParseTreeListener<'arena, VisitorCalcParserNodeKind, Tok>
where
    Tok: Token + 'arena,
{
    /// Enter a parse tree produced by {@link VisitorCalcParser#s}.
    /// @param ctx the parse tree
    fn enter_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link VisitorCalcParser#s}.
    /// @param ctx the parse tree
    fn exit_s<'input: 'arena>(&mut self, _ctx: &SContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code add}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_add<'input: 'arena>(&mut self, _ctx: &AddContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code add}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_add<'input: 'arena>(&mut self, _ctx: &AddContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code number}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_number<'input: 'arena>(&mut self, _ctx: &NumberContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code number}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_number<'input: 'arena>(&mut self, _ctx: &NumberContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code multiply}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_multiply<'input: 'arena>(&mut self, _ctx: &MultiplyContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code multiply}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_multiply<'input: 'arena>(&mut self, _ctx: &MultiplyContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

}
