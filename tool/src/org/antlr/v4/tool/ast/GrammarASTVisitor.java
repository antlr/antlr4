package org.antlr.v4.tool.ast;

/*
GrammarAST t = ...;
SynDiagVisitor v = new ...;
t.visit(v);
*/
public interface GrammarASTVisitor {
	Object visit(RuleAST node);
	Object visit(AltAST node);
}
