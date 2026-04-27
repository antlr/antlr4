// Generated from Labels.g4 by ANTLR 4.13.2

use super::labelsparser::*;
use dbt_antlr4::tree::ParseTreeListener;

// A complete Visitor for a parse tree produced by LabelsParser.

pub trait LabelsBaseListener<'arena>:
    ParseTreeListener<'arena, LabelsParserNodeKind> {

    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_s(&mut self, _ctx: &SContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_s(&mut self, _ctx: &SContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_add(&mut self, _ctx: &AddContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_add(&mut self, _ctx: &AddContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_parens(&mut self, _ctx: &ParensContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_parens(&mut self, _ctx: &ParensContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_mult(&mut self, _ctx: &MultContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_mult(&mut self, _ctx: &MultContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_dec(&mut self, _ctx: &DecContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_dec(&mut self, _ctx: &DecContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_anid(&mut self, _ctx: &AnIDContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_anid(&mut self, _ctx: &AnIDContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_anint(&mut self, _ctx: &AnIntContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_anint(&mut self, _ctx: &AnIntContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link LabelsBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_inc(&mut self, _ctx: &IncContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  LabelsBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_inc(&mut self, _ctx: &IncContext<'input, 'arena>) {}


}