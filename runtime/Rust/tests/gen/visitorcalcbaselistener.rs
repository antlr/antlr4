// Generated from VisitorCalc.g4 by ANTLR 4.13.2

use super::visitorcalcparser::*;
use dbt_antlr4::tree::ParseTreeListener;

// A complete Visitor for a parse tree produced by VisitorCalcParser.

pub trait VisitorCalcBaseListener<'arena>:
    ParseTreeListener<'arena, VisitorCalcParserNodeKind> {

    /**
     * Enter a parse tree produced by \{@link VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_s(&mut self, _ctx: &SContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_s(&mut self, _ctx: &SContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_add(&mut self, _ctx: &AddContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_add(&mut self, _ctx: &AddContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_number(&mut self, _ctx: &NumberContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_number(&mut self, _ctx: &NumberContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_multiply(&mut self, _ctx: &MultiplyContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  VisitorCalcBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_multiply(&mut self, _ctx: &MultiplyContext<'input, 'arena>) {}


}