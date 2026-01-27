
// Generated from VisitorBasic.g4 by ANTLR 4.13.2

use dbt_antlr4::tree::ParseTreeVisitor;
use super::visitorbasicparser::*;

// A complete Visitor for a parse tree produced by VisitorBasicParser.

pub trait VisitorBasicBaseVisitor<'input>:
    ParseTreeVisitor<'input, VisitorBasicParserContextType> {
	// Visit a parse tree produced by VisitorBasicParser#s.
	fn visit_s(&mut self, ctx: &SContext<'input>) {
            self.visit_children(ctx)
        }

}