#![allow(nonstandard_style)]
// Generated from CSV.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::ParseTreeListener;
use super::csvparser::*;

pub trait CSVListener<'input, 'arena> : ParseTreeListener<'input, 'arena, CSVParserContextNode<'input, 'arena>>
where
    'input: 'arena,
{
    /// Enter a parse tree produced by {@link CSVParser#csvFile}.
    /// @param ctx the parse tree
    fn enter_csvFile(&mut self, _ctx: &CsvFileContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#csvFile}.
    /// @param ctx the parse tree
    fn exit_csvFile(&mut self, _ctx: &CsvFileContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#hdr}.
    /// @param ctx the parse tree
    fn enter_hdr(&mut self, _ctx: &HdrContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#hdr}.
    /// @param ctx the parse tree
    fn exit_hdr(&mut self, _ctx: &HdrContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#row}.
    /// @param ctx the parse tree
    fn enter_row(&mut self, _ctx: &RowContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#row}.
    /// @param ctx the parse tree
    fn exit_row(&mut self, _ctx: &RowContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#field}.
    /// @param ctx the parse tree
    fn enter_field(&mut self, _ctx: &FieldContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#field}.
    /// @param ctx the parse tree
    fn exit_field(&mut self, _ctx: &FieldContext<'input, 'arena>) -> Result<(), ANTLRError> { Ok(()) }

}
