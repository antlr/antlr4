// Generated from ReferenceToATN.g4 by ANTLR 4.13.2

use super::referencetoatnparser::*;
use dbt_antlr4::tree::ParseTreeListener;

// A complete Visitor for a parse tree produced by ReferenceToATNParser.

pub trait ReferenceToATNBaseListener<'input>:
    ParseTreeListener<'input, ReferenceToATNParserContextType> {

    /**
     * Enter a parse tree produced by \{@link ReferenceToATNBaseParser#s}.
     * @param ctx the parse tree
     */
    fn enter_a(&mut self, _ctx: &AContext<'input>) {}
    /**
     * Exit a parse tree produced by \{@link  ReferenceToATNBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_a(&mut self, _ctx: &AContext<'input>) {}


}