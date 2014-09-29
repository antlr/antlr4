package org.antlr.v4.test;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
		"RETURN : 'return' ;\n" +
		"ID  :   [a-zA-Z]+ ;      // match identifiers\n" +
		"INT :   [0-9]+ ;         // match integers\n" +
		"NEWLINE:'\\r'? '\\n' -> skip;     // return newlines to parser (is end-statement signal)\n" +
		"WS  :   [ \\t]+ -> skip ; // toss out whitespace\n";
	public static final String SAMPLE_PROGRAM =
			"def f(x,y) { x = 3+4; y; ; }\n" +
			"def g(x) { return 1+2*x; }\n";

	@Test public void testValidPaths() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

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
			"//'return'",		// any 'return' literal in tree, matched by literal name
			"//RETURN",			// any 'return' literal in tree, matched by symbolic name
			"//primary/*",		// all kids of any primary
			"//func/*/stat",	// all stat nodes grandkids of any func node
			"/prog/func/'def'",	// all def literal kids of func kid of prog
			"//stat/';'",		// all ';' under any stat node
			"//expr/primary/!ID",	// anything but ID under primary under any expr node
			"//expr/!primary",	// anything but primary under any expr node
			"//!*",				// nothing anywhere
			"/!*",				// nothing at root
			"//expr//ID",		// any ID under any expression (tests antlr/antlr4#370)
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
			"[return]",
			"[3, 4, y, 1, 2, x]",
			"[stat, stat, stat, stat]",
			"[def, def]",
			"[;, ;, ;, ;]",
			"[3, 4, 1, 2]",
			"[expr, expr, expr, expr, expr, expr]",
			"[]",
			"[]",
			"[y, x]",
		};

		for (int i=0; i<xpath.length; i++) {
			List<String> nodes = getNodeStrings(SAMPLE_PROGRAM, xpath[i], "prog", "ExprParser", "ExprLexer");
			String result = nodes.toString();
			assertEquals("path "+xpath[i]+" failed", expected[i], result);
		}
	}

	@Test public void testWeirdChar() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "&";
		String expected = "Invalid tokens or characters at index 0 in path '&'";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	@Test public void testWeirdChar2() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "//w&e/";
		String expected = "Invalid tokens or characters at index 3 in path '//w&e/'";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	@Test public void testBadSyntax() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "///";
		String expected = "/ at index 2 isn't a valid rule name";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	@Test public void testMissingWordAtEnd() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "//";
		String expected = "Missing path element at end of path";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	@Test public void testBadTokenName() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "//Ick";
		String expected = "Ick at index 2 isn't a valid token name";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	@Test public void testBadRuleName() throws Exception {
		boolean ok =
			rawGenerateAndBuildRecognizer("Expr.g4", grammar, "ExprParser",
										  "ExprLexer", false);
		assertTrue(ok);

		String path = "/prog/ick";
		String expected = "ick at index 6 isn't a valid rule name";

		testError(SAMPLE_PROGRAM, path, expected, "prog", "ExprParser", "ExprLexer");
	}

	protected void testError(String input, String path, String expected,
							 String startRuleName,
							 String parserName, String lexerName)
		throws Exception
	{
		Pair<Parser, Lexer> pl = getParserAndLexer(input, parserName, lexerName);
		Parser parser = pl.a;
		ParseTree tree = execStartRule(startRuleName, parser);

		IllegalArgumentException e = null;
		try {
			XPath.findAll(tree, path, parser);
		}
		catch (IllegalArgumentException iae) {
			e = iae;
		}
		assertNotNull(e);
		assertEquals(expected, e.getMessage());
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
		for (ParseTree t : XPath.findAll(tree, xpath, parser) ) {
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
