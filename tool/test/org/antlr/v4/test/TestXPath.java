package org.antlr.v4.test;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestXPath extends BaseTest {
	public static final String grammar =
		"grammar Expr;\n" +
		"prog:   func+ ;\n" +
		"func:  'def' ID '(' arg (',' arg)* ')' body ;\n" +
		"body:  '{' stat+ '}' ;\n" +
		"arg :  ID ;\n" +
		"stat:   expr ';'                 # printExpr\n" +
		"    |   ID '=' expr ';'          # assign\n" +
		"    |   'return' expr ';'        # ret\n" +
		"    |   ';'                      # blank\n" +
		"    ;\n" +
		"expr:   expr ('*'|'/') expr      # MulDiv\n" +
		"    |   expr ('+'|'-') expr      # AddSub\n" +
		"    |   primary                  # prim\n" +
		"    ;\n" +
		"primary" +
		"    :   INT                      # int\n" +
		"    |   ID                       # id\n" +
		"    |   '(' expr ')'             # parens\n" +
		"	 ;" +
		"\n" +
		"MUL :   '*' ; // assigns token name to '*' used above in grammar\n" +
		"DIV :   '/' ;\n" +
		"ADD :   '+' ;\n" +
		"SUB :   '-' ;\n" +
		"ID  :   [a-zA-Z]+ ;      // match identifiers\n" +
		"INT :   [0-9]+ ;         // match integers\n" +
		"NEWLINE:'\\r'? '\\n' -> skip;     // return newlines to parser (is end-statement signal)\n" +
		"WS  :   [ \\t]+ -> skip ; // toss out whitespace\n";

	@Test public void test() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser", "ExprLexer", false);
		assertTrue(ok);

		String input =
			"def f(x,y) { x = 3+4; y; ; }\n" +
			"def g(x) { return 1+2*x; }\n";
		String xpath[] = {
			"/prog/func",		// all funcs under prog at root
			"/prog/*",			// all children of prog at root
			"/*/func",			// all func kids of any root node
			"prog",				// prog must be root node
			"/prog",			// prog must be root node
			"/*",				// any root
			"*",				// any root
			"//ID",				// any ID in tree
			"//expr/primary/ID",// any ID child of a primary under any expr
			"//body//ID",		// any ID under a body
			"//'return'",		// any 'return' literal in tree
			"//primary/*",		// all kids of any primary
			"//func/*/stat",	// all stat nodes grandkids of any func node
			"/prog/func/'def'",	// all def literal kids of func kid of prog
			"//stat/';'"		// all ';' under any stat node
		};
		String expected[] = {
			"[func, func]",
			"[func, func]",
			"[func, func]",
			"[prog]",
			"[prog]",
			"[prog]",
			"[prog]",
			"[f, x, y, x, y, g, x, x]",
			"[y, x]",
			"[x, y, x]",
			"[return]",
			"[3, 4, y, 1, 2, x]",
			"[stat, stat, stat, stat]",
			"[def, def]",
			"[;, ;, ;, ;]"
		};

		for (int i=0; i<xpath.length; i++) {
			List<String> nodes = getNodeStrings(input, xpath[i], "prog", "ExprParser", "ExprLexer");
			String result = nodes.toString();
			assertEquals("path "+xpath[i]+" failed", expected[i], result);
		}
	}

	public List<String> getNodeStrings(String input, String xpath,
									   String startRuleName,
									   String parserName, String lexerName)
		throws Exception
	{
		Pair<Parser, Lexer> pl = getParserAndLexer(input, parserName, lexerName);
		Parser parser = pl.a;
		ParseTree tree = execStartRule(startRuleName, parser);

		List<String> nodes = new ArrayList<String>();
		for (ParseTree t : tree.findAll(parser, xpath) ) {
			if ( t instanceof RuleContext) {
				RuleContext r = (RuleContext)t;
				nodes.add(parser.getRuleNames()[r.getRuleIndex()]);
			}
			else {
				TerminalNode token = (TerminalNode)t;
				nodes.add(token.getText());
			}
		}
		return nodes;
	}
}
