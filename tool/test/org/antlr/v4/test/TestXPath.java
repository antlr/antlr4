package org.antlr.v4.test;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestXPath extends BaseTest {
	@Test public void test() throws Exception {
		String grammar =
			"grammar Expr;\n" +
			"prog:   func+ ;\n" +
			"func:  'def' ID '(' arg (',' arg)* ')' '{' stat+ '}' ;\n" +
			"arg :  ID ;\n" +
			"stat:   expr ';'                 # printExpr\n" +
			"    |   ID '=' expr ';'          # assign\n" +
			"    |   ';'                      # blank\n" +
			"    ;\n" +
			"expr:   expr ('*'|'/') expr      # MulDiv\n" +
			"    |   expr ('+'|'-') expr      # AddSub\n" +
			"    |   INT                      # int\n" +
			"    |   ID                       # id\n" +
			"    |   '(' expr ')'             # parens\n" +
			"    ;\n" +
			"\n" +
			"MUL :   '*' ; // assigns token name to '*' used above in grammar\n" +
			"DIV :   '/' ;\n" +
			"ADD :   '+' ;\n" +
			"SUB :   '-' ;\n" +
			"ID  :   [a-zA-Z]+ ;      // match identifiers\n" +
			"INT :   [0-9]+ ;         // match integers\n" +
			"NEWLINE:'\\r'? '\\n' -> skip;     // return newlines to parser (is end-statement signal)\n" +
			"WS  :   [ \\t]+ -> skip ; // toss out whitespace\n";

		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser", "ExprLexer", false);
		assertTrue(ok);

		String input = "def f(x,y) { x = 3+4; y; ; }";
		Pair<Parser, Lexer> pl = getParserAndLexer(input, "ExprParser", "ExprLexer");
		Parser parser = pl.a;
		ParseTree tree = execStartRule("prog", parser);

		for (ParseTree t : tree.findAll(parser, "/prog/func") ) {
			if ( t instanceof RuleContext) {
				RuleContext r = (RuleContext)t;
				System.out.println("  "+parser.getRuleNames()[r.getRuleIndex()]);
			}
			else {
				TerminalNode token = (TerminalNode)t;
				System.out.println("  "+token.getText());
			}
		}
	}
}
