// Generated from SimpleLR.g4 by ANTLR 4.13.2

use super::simplelrparser::*;
use dbt_antlr4::tree::ParseTreeListener;

// A complete Visitor for a parse tree produced by SimpleLRParser.

pub trait SimpleLRBaseListener<'arena>:
    ParseTreeListener<'arena, SimpleLRParserNodeKind> {

    /**
     * Enter a parse tree produced by \{@link SimpleLRBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_s(&mut self, _ctx: &SContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  SimpleLRBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_s(&mut self, _ctx: &SContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link SimpleLRBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_a(&mut self, _ctx: &AContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  SimpleLRBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_a(&mut self, _ctx: &AContext<'input, 'arena>) {}


}