package org.antlr.v4.test;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestParseTreeMatcher extends BaseTest {
	@Test public void testChunking() throws Exception {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		assertEquals("[ID, ' = ', expr, ' ;']", p.split("<ID> = <expr> ;").toString());
		assertEquals("[' ', ID, ' = ', expr]", p.split(" <ID> = <expr>").toString());
		assertEquals("[ID, ' = ', expr]", p.split("<ID> = <expr>").toString());
		assertEquals("[expr]", p.split("<expr>").toString());
		assertEquals("['<x> foo']", p.split("\\<x\\> foo").toString());
		assertEquals("['foo <x> bar ', tag]", p.split("foo \\<x\\> bar <tag>").toString());
	}

	@Test public void testDelimiters() throws Exception {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		p.setDelimiters("<<", ">>", "$");
		String result = p.split("<<ID>> = <<expr>> ;$<< ick $>>").toString();
		assertEquals("[ID, ' = ', expr, ' ;<< ick >>']", result);
	}

	@Test public void testInvertedTags() throws Exception {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		String result = null;
		try {
			p.split(">expr<");
		}
		catch (IllegalArgumentException iae) {
			result = iae.getMessage();
		}
		String expected = "tag delimiters out of order in pattern: >expr<";
		assertEquals(expected, result);
	}

	@Test public void testUnclosedTag() throws Exception {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		String result = null;
		try {
			p.split("<expr hi mom");
		}
		catch (IllegalArgumentException iae) {
			result = iae.getMessage();
		}
		String expected = "unterminated tag in pattern: <expr hi mom";
		assertEquals(expected, result);
	}

	@Test public void testExtraClose() throws Exception {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		String result = null;
		try {
			p.split("<expr> >");
		}
		catch (IllegalArgumentException iae) {
			result = iae.getMessage();
		}
		String expected = "missing start tag in pattern: <expr> >";
		assertEquals(expected, result);
	}

	@Test public void testTokenizingPattern() throws Exception {
		String grammar =
			"grammar X1;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X1.g4", grammar, "X1Parser", "X1Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("X1Lexer"),
										loadParserClassFromTempDir("X1Parser"));

		List<? extends Token> tokens = p.tokenize("<ID> = <expr> ;");
		String results = tokens.toString();
		String expected = "[ID:3, [@-1,1:1='=',<1>,1:1], expr:1, [@-1,1:1=';',<2>,1:1]]";
		assertEquals(expected, results);
	}

	@Test
	public void testCompilingPattern() throws Exception {
		String grammar =
			"grammar X2;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X2.g4", grammar, "X2Parser", "X2Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("X2Lexer"),
										loadParserClassFromTempDir("X2Parser"));

		ParseTreePattern t = p.compile("s", "<ID> = <expr> ;");
		String results = t.patternTree.toStringTree(p.getParser());
		String expected = "(s <ID> = (expr <expr>) ;)";
		assertEquals(expected, results);
	}

	@Test
	public void testCompilingMultipleTokens() throws Exception {
		String grammar =
			"grammar X2;\n" +
			"s : ID '=' ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X2.g4", grammar, "X2Parser", "X2Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("X2Lexer"),
										loadParserClassFromTempDir("X2Parser"));

		ParseTreePattern t = p.compile("s", "<ID> = <ID> ;");
		String results = t.patternTree.toStringTree(p.getParser());
		String expected = "(s <ID> = <ID> ;)";
		assertEquals(expected, results);
	}

	@Test public void testIDNodeMatches() throws Exception {
		String grammar =
			"grammar X3;\n" +
			"s : ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x ;";
		String pattern = "<ID>;";
		checkPatternMatch("X3.g4", grammar, "s", input, pattern, "X3Parser", "X3Lexer");
	}

	@Test public void testTokenAndRuleMatch() throws Exception {
		String grammar =
			"grammar X4;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x = 99;";
		String pattern = "<ID> = <expr> ;";
		checkPatternMatch("X4.g4", grammar, "s", input, pattern, "X4Parser", "X4Lexer");
	}

	@Test public void testTokenTextMatch() throws Exception {
		String grammar =
			"grammar X4;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x = 0;";
		String pattern = "<ID> = 1;";
		boolean invertMatch = true; // 0!=1
		checkPatternMatch("X4.g4", grammar, "s", input, pattern, "X4Parser", "X4Lexer", invertMatch);

		input = "x = 0;";
		pattern = "<ID> = 0;";
		invertMatch = false;
		checkPatternMatch("X4.g4", grammar, "s", input, pattern, "X4Parser", "X4Lexer", invertMatch);

		input = "x = 0;";
		pattern = "x = 0;";
		invertMatch = false;
		checkPatternMatch("X4.g4", grammar, "s", input, pattern, "X4Parser", "X4Lexer", invertMatch);

		input = "x = 0;";
		pattern = "y = 0;";
		invertMatch = true;
		checkPatternMatch("X4.g4", grammar, "s", input, pattern, "X4Parser", "X4Lexer", invertMatch);
	}

	@Test public void testAssign() throws Exception {
		String grammar =
			"grammar X5;\n" +
			"s   : expr ';'\n" +
			//"    | 'return' expr ';'\n" +
			"    ;\n" +
			"expr: expr '.' ID\n" +
			"    | expr '*' expr\n" +
			"    | expr '=' expr\n" +
			"    | ID\n" +
			"    | INT\n" +
			"    ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x = 99;";
		String pattern = "<ID> = <expr>;";
		checkPatternMatch("X5.g4", grammar, "s", input, pattern, "X5Parser", "X5Lexer");
	}

	@Test public void testLRecursiveExpr() throws Exception {
		String grammar =
			"grammar X6;\n" +
			"s   : expr ';'\n" +
			"    ;\n" +
			"expr: expr '.' ID\n" +
			"    | expr '*' expr\n" +
			"    | expr '=' expr\n" +
			"    | ID\n" +
			"    | INT\n" +
			"    ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "3*4*5";
		String pattern = "<expr> * <expr> * <expr>";
		checkPatternMatch("X6.g4", grammar, "expr", input, pattern, "X6Parser", "X6Lexer");
	}

	/*
	Shit. issue is that <.> won't match any tokens and jumps to end of rule
	to recover but we need it to recover inline. Same if ID or (ID|FOO) decision
	in rule s.
	 */
	@Test
	public void testWildcard() throws Exception {
		String grammar =
			"grammar X2;\n" +
			"s : ID '=' INT ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X2.g4", grammar, "X2Parser", "X2Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("X2Lexer"),
										loadParserClassFromTempDir("X2Parser"));

		ParseTreePattern t = p.compile("s", "<.> = <INT> ;");
		String results = t.patternTree.toStringTree(p.getParser());
		String expected = "(s <ID> = (expr <expr>) ;)";
		assertEquals(expected, results);
	}

	@Test public void testLRWildcard() throws Exception {
		String grammar =
			"grammar X6;\n" +
			"s   : expr ';'\n" +
			"    ;\n" +
			"expr: expr '.' ID\n" +
			"    | expr '*' expr\n" +
			"    | expr '=' expr\n" +
			"    | ID\n" +
			"    | INT\n" +
			"    ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "a.b.c";
		//pattern = "<expr>.<ID>";
		String pattern = "<.> . <ID>"; // match any single node or subtree then .<ID>
		checkPatternMatch("X6.g4", grammar, "expr", input, pattern, "X6Parser", "X6Lexer");
	}

	public void checkPatternMatch(String grammarName, String grammar, String startRule,
								  String input, String pattern,
								  String parserName, String lexerName)
		throws Exception
	{
		checkPatternMatch(grammarName, grammar, startRule, input, pattern, parserName, lexerName, false);
	}

	public void checkPatternMatch(String grammarName, String grammar, String startRule,
								  String input, String pattern,
								  String parserName, String lexerName,
								  boolean invertMatch)
		throws Exception
	{
		boolean ok =
			rawGenerateAndBuildRecognizer(grammarName, grammar, parserName, lexerName, false);
		assertTrue(ok);

		ParseTree result = execParser(startRule, input, parserName, lexerName);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir(lexerName),
										loadParserClassFromTempDir(parserName));
		boolean matches = p.matches(result, startRule, pattern);
		if ( invertMatch ) assertFalse(matches);
		else assertTrue(matches);
	}
}
