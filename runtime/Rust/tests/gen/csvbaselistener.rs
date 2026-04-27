// Generated from CSV.g4 by ANTLR 4.13.2

use super::csvparser::*;
use dbt_antlr4::tree::ParseTreeListener;

// A complete Visitor for a parse tree produced by CSVParser.

pub trait CSVBaseListener<'arena>:
    ParseTreeListener<'arena, CSVParserNodeKind> {

    /**
     * Enter a parse tree produced by \{@link CSVBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_csvfile(&mut self, _ctx: &CsvFileContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  CSVBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_csvfile(&mut self, _ctx: &CsvFileContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link CSVBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_hdr(&mut self, _ctx: &HdrContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  CSVBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_hdr(&mut self, _ctx: &HdrContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link CSVBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_row(&mut self, _ctx: &RowContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  CSVBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_row(&mut self, _ctx: &RowContext<'input, 'arena>) {}


    /**
     * Enter a parse tree produced by \{@link CSVBaseParser#s}.
     * @param ctx the parse tree
,      */
    fn enter_field(&mut self, _ctx: &FieldContext<'input, 'arena>) {}
    /**
     * Exit a parse tree produced by \{@link  CSVBaseParser#s}.
     * @param ctx the parse tree
     */
    fn exit_field(&mut self, _ctx: &FieldContext<'input, 'arena>) {}


}