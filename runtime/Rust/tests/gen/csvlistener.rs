#![allow(nonstandard_style)]
// Generated from CSV.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::token::Token;
use dbt_antlr4::tree::ParseTreeListener;
use super::csvparser::*;

pub trait CSVListener<'arena, Tok> : ParseTreeListener<'arena, CSVParserNodeKind, Tok>
where
    Tok: Token + 'arena,
{
    /// Enter a parse tree produced by {@link CSVParser#csvFile}.
    /// @param ctx the parse tree
    fn enter_csvFile<'input: 'arena>(&mut self, _ctx: &CsvFileContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#csvFile}.
    /// @param ctx the parse tree
    fn exit_csvFile<'input: 'arena>(&mut self, _ctx: &CsvFileContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#hdr}.
    /// @param ctx the parse tree
    fn enter_hdr<'input: 'arena>(&mut self, _ctx: &HdrContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#hdr}.
    /// @param ctx the parse tree
    fn exit_hdr<'input: 'arena>(&mut self, _ctx: &HdrContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#row}.
    /// @param ctx the parse tree
    fn enter_row<'input: 'arena>(&mut self, _ctx: &RowContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#row}.
    /// @param ctx the parse tree
    fn exit_row<'input: 'arena>(&mut self, _ctx: &RowContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Enter a parse tree produced by {@link CSVParser#field}.
    /// @param ctx the parse tree
    fn enter_field<'input: 'arena>(&mut self, _ctx: &FieldContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

    /// Exit a parse tree produced by {@link CSVParser#field}.
    /// @param ctx the parse tree
    fn exit_field<'input: 'arena>(&mut self, _ctx: &FieldContext<'input, 'arena, Tok>) -> Result<(), ANTLRError> { Ok(()) }

}
