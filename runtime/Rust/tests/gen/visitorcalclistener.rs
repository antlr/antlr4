#![allow(nonstandard_style)]
// Generated from VisitorCalc.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::ParseTreeListener;
use super::visitorcalcparser::*;

pub trait VisitorCalcListener<'input, 'arena> : ParseTreeListener<'input, 'arena, VisitorCalcParserContextNode<'input, 'arena>>
where
    'input: 'arena,
{
    /// Enter a parse tree produced by {@link VisitorCalcParser#s}.
    /// @param ctx the parse tree
    fn enter_s(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link VisitorCalcParser#s}.
    /// @param ctx the parse tree
    fn exit_s(&mut self, _ctx: &SContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code add}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_add(&mut self, _ctx: &AddContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code add}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_add(&mut self, _ctx: &AddContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code number}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_number(&mut self, _ctx: &NumberContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code number}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_number(&mut self, _ctx: &NumberContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by the {@code multiply}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn enter_multiply(&mut self, _ctx: &MultiplyContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by the {@code multiply}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn exit_multiply(&mut self, _ctx: &MultiplyContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

}
