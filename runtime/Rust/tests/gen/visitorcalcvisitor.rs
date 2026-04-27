#![allow(nonstandard_style)]
#![allow(dead_code)]
// Generated from VisitorCalc.g4 by ANTLR 4.13.2
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::tree::*;
use super::visitorcalcparser::*;

/// This interface defines a complete generic visitor for a parse tree produced
/// by {@link VisitorCalcParser}.
pub trait VisitorCalcVisitor<'input, 'arena>
where
    'input: 'arena,
{
    type Return: Default;

    /// Visit a parse tree produced by {@link VisitorCalcParser#s}.
    /// @param ctx the parse tree
    fn visit_s(&mut self, ctx: &'arena SContext<'input, 'arena>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by the {@code add}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn visit_add(&mut self, ctx: &'arena AddContext<'input, 'arena>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by the {@code number}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn visit_number(&mut self, ctx: &'arena NumberContext<'input, 'arena>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }

    /// Visit a parse tree produced by the {@code multiply}
    /// labeled alternative in {@link VisitorCalcParser#expr}.
    /// @param ctx the parse tree
    fn visit_multiply(&mut self, ctx: &'arena MultiplyContext<'input, 'arena>) -> Result<Self::Return, ANTLRError> { self.visit_children(ctx) }


    /// Called on terminal(leaf) node
    fn visit_terminal(
        &mut self,
        _node: &'arena TerminalNode<'input, 'arena>,
    ) -> Result<Self::Return, ANTLRError> { Ok(Self::Return::default()) }

    /// Called on error node
    fn visit_error_node(
        &mut self,
        _node: &'arena ErrorNode<'input, 'arena>,
    ) -> Result<Self::Return, ANTLRError> { Ok(Self::Return::default()) }

    fn visit(&mut self, tree: &'arena dyn NodeInner<'input, 'arena, VisitorCalcParserNodeKind>) -> Result<Self::Return, ANTLRError> {
        self.visit_node(tree.as_node())
    }

    fn visit_node(&mut self, node: &'arena VisitorCalcParserNode<'input, 'arena>) -> Result<Self::Return, ANTLRError> {
        node.accept(self)
    }

    fn visit_children(
        &mut self,
        node: &'arena dyn NodeInner<'input, 'arena, VisitorCalcParserNodeKind>,
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

    fn should_visit_next_child(&self, _node: &'arena VisitorCalcParserNode<'input, 'arena>, _current: &Self::Return) -> bool { true }
}