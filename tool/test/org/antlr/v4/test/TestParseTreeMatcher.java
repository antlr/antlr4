package org.antlr.v4.test;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestParseTreeMatcher extends BaseTest {
	@Test public void testChunking() throws Exception {
		// tests
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		System.out.println( p.split("<ID> = <expr> ;") );
		System.out.println( p.split(" <ID> = <expr>") );
		System.out.println( p.split("<ID> = <expr>") );
		System.out.println( p.split("<expr>") );
		System.out.println(p.split("\\<x\\> foo"));
		System.out.println(p.split("foo \\<x\\> bar <tag>"));
//		System.out.println( p.split(">expr<") );

		p.setDelimiters("<<", ">>", "$");
		System.out.println(p.split("<<ID>> = <<expr>> ;$<< ick $>>"));
	}

	@Test public void testTokenizingPattern() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("T.g4", grammar, "TParser", "TLexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("TLexer"),
										loadParserClassFromTempDir("TParser"));

		List<? extends Token> tokens = p.tokenizePattern("<ID> = <expr> ;");
		String results = tokens.toString();
		String expected = "[ID:3, [@-1,1:1='=',<1>,1:1], expr:1, [@-1,0:0=';',<2>,1:0]]";
		assertEquals(expected, results);
	}

	@Test
	public void testCompilingPattern() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("T.g4", grammar, "TParser", "TLexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher p =
			new ParseTreePatternMatcher(loadLexerClassFromTempDir("TLexer"),
										loadParserClassFromTempDir("TParser"));

		ParseTree t = p.compilePattern("s", "<ID> = <expr> ;");
		String results = t.toStringTree(p.getParser());
		String expected = "(s <ID> = expr ;)";
		assertEquals(expected, results);
	}

	public void checkPatternMatch(String grammarName, String grammar, String startRule,
								  String input, String pattern,
								  String parserName, String lexerName)
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
		assertTrue(matches);
	}

	@Test public void testIDNodeMatches() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x ;";
		String pattern = "<ID>;";
		checkPatternMatch("T.g4", grammar, "s", input, pattern, "TParser", "TLexer");
	}

	@Test public void testTokenAndRuleMatch() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x = 99;";
		String pattern = "<ID> = <expr> ;";
		checkPatternMatch("T.g4", grammar, "s", input, pattern, "TParser", "TLexer");
	}
}
