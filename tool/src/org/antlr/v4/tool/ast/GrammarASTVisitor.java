package org.antlr.v4.tool.ast;

/*
GrammarAST t = ...;
SynDiagVisitor v = new ...;
t.visit(v);
*/
public interface GrammarASTVisitor {
	Object visit(RuleAST node);
	Object visit(AltAST node);
	Object visit(DownAST node);
	Object visit(GrammarAST node);
	Object visit(GrammarRootAST node);
	Object visit(NotAST node);
	Object visit(OptionalBlockAST node);
	Object visit(PlusBlockAST node);
	Object visit(PredAST node);
	Object visit(RangeAST node);
	Object visit(SetAST node);
	Object visit(StarBlockAST node);
	Object visit(TerminalAST node);
	Object visit(TreePatternAST node);
	Object visit(UpAST node);
}
