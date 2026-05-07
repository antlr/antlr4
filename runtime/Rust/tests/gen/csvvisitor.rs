#![allow(nonstandard_style)]
#![allow(dead_code)]
// Generated from CSV.g4 by ANTLR 4.13.2
use dbt_antlr4::token::{CommonToken, Token};
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::*;
use super::csvparser::*;

/// This interface defines a complete generic visitor for a parse tree produced
/// by {@link CSVParser}.
pub trait CSVVisitor<'input, 'arena, Tok = CommonToken<'input>>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    type Return: Default;

    /// Visit a parse tree produced by {@link CSVParser#csvFile}.
    /// @param ctx the parse tree
    fn visit_csvFile(&mut self, ctx: &'arena CsvFileContext<'input, 'arena, Tok>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by {@link CSVParser#hdr}.
    /// @param ctx the parse tree
    fn visit_hdr(&mut self, ctx: &'arena HdrContext<'input, 'arena, Tok>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by {@link CSVParser#row}.
    /// @param ctx the parse tree
    fn visit_row(&mut self, ctx: &'arena RowContext<'input, 'arena, Tok>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by {@link CSVParser#field}.
    /// @param ctx the parse tree
    fn visit_field(&mut self, ctx: &'arena FieldContext<'input, 'arena, Tok>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }


    /// Called on terminal(leaf) node
    fn visit_terminal(
        &mut self,
        _node: &'arena TerminalNode<'input, 'arena, Tok>,
    ) -> Result<Self::Return, ANTLRError> { Ok(Self::Return::default()) }

    /// Called on error node
    fn visit_error_node(
        &mut self,
        _node: &'arena ErrorNode<'input, 'arena, Tok>,
    ) -> Result<Self::Return, ANTLRError> { Ok(Self::Return::default()) }

    fn visit(&mut self, tree: &'arena dyn NodeInner<'input, 'arena, CSVParserNodeKind, Tok>) -> Result<Self::Return, ANTLRError> {
        self.visit_node(tree.as_node())
    }

    fn visit_node(&mut self, node: &'arena CSVParserNode<'input, 'arena, Tok>) -> Result<Self::Return, ANTLRError> {
        node.accept(self)
    }

    fn visit_children(
        &mut self,
        node: &'arena dyn NodeInner<'input, 'arena, CSVParserNodeKind, Tok>,
    ) -> Result<Self::Return, ANTLRError> {
        let mut result = Self::Return::default();
        for child in node.iter_child_nodes() {
            if !self.should_visit_next_child(child, &result) {
                break;
            }

            let child_result = self.visit_node(child)?;
            result = self.aggregate_results(result, child_result)?;
        }
        Ok(result)
    }

    fn aggregate_results(
        &self,
        _aggregate: Self::Return,
        next: Self::Return,
    ) -> Result<Self::Return, ANTLRError> { Ok(next) }

    fn should_visit_next_child(&self, _node: &'arena CSVParserNode<'input, 'arena, Tok>, _current: &Self::Return) -> bool { true }
}