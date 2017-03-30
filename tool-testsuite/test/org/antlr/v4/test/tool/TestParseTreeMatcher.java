/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestParseTreeMatcher extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testChunking() throws Exception {
		ParseTreePatternMatcher m = new ParseTreePatternMatcher(null, null);
		assertEquals("[ID, ' = ', expr, ' ;']", m.split("<ID> = <expr> ;").toString());
		assertEquals("[' ', ID, ' = ', expr]", m.split(" <ID> = <expr>").toString());
		assertEquals("[ID, ' = ', expr]", m.split("<ID> = <expr>").toString());
		assertEquals("[expr]", m.split("<expr>").toString());
		assertEquals("['<x> foo']", m.split("\\<x\\> foo").toString());
		assertEquals("['foo <x> bar ', tag]", m.split("foo \\<x\\> bar <tag>").toString());
	}

	@Test public void testDelimiters() throws Exception {
		ParseTreePatternMatcher m = new ParseTreePatternMatcher(null, null);
		m.setDelimiters("<<", ">>", "$");
		String result = m.split("<<ID>> = <<expr>> ;$<< ick $>>").toString();
		assertEquals("[ID, ' = ', expr, ' ;<< ick >>']", result);
	}

	@Test public void testInvertedTags() throws Exception {
		ParseTreePatternMatcher m= new ParseTreePatternMatcher(null, null);
		String result = null;
		try {
			m.split(">expr<");
		}
		catch (IllegalArgumentException iae) {
			result = iae.getMessage();
		}
		String expected = "tag delimiters out of order in pattern: >expr<";
		assertEquals(expected, result);
	}

	@Test public void testUnclosedTag() throws Exception {
		ParseTreePatternMatcher m = new ParseTreePatternMatcher(null, null);
		String result = null;
		try {
			m.split("<expr hi mom");
		}
		catch (IllegalArgumentException iae) {
			result = iae.getMessage();
		}
		String expected = "unterminated tag in pattern: <expr hi mom";
		assertEquals(expected, result);
	}

	@Test public void testExtraClose() throws Exception {
		ParseTreePatternMatcher m = new ParseTreePatternMatcher(null, null);
		String result = null;
		try {
			m.split("<expr> >");
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

		ParseTreePatternMatcher m = getPatternMatcher("X1");

		List<? extends Token> tokens = m.tokenize("<ID> = <expr> ;");
		String results = tokens.toString();
		String expected = "[ID:3, [@-1,1:1='=',<1>,1:1], expr:7, [@-1,1:1=';',<2>,1:1]]";
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

		ParseTreePatternMatcher m = getPatternMatcher("X2");

		ParseTreePattern t = m.compile("<ID> = <expr> ;", m.getParser().getRuleIndex("s"));
		String results = t.getPatternTree().toStringTree(m.getParser());
		String expected = "(s <ID> = (expr <expr>) ;)";
		assertEquals(expected, results);
	}

	@Test
	public void testCompilingPatternConsumesAllTokens() throws Exception {
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

		ParseTreePatternMatcher m = getPatternMatcher("X2");

		boolean failed = false;
		try {
			m.compile("<ID> = <expr> ; extra", m.getParser().getRuleIndex("s"));
		}
		catch (ParseTreePatternMatcher.StartRuleDoesNotConsumeFullPattern e) {
			failed = true;
		}
		assertTrue(failed);
	}

	@Test
	public void testPatternMatchesStartRule() throws Exception {
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

		ParseTreePatternMatcher m = getPatternMatcher("X2");

		boolean failed = false;
		try {
			m.compile("<ID> ;", m.getParser().getRuleIndex("s"));
		}
		catch (InputMismatchException e) {
			failed = true;
		}
		assertTrue(failed);
	}

	@Test
	public void testPatternMatchesStartRule2() throws Exception {
		String grammar =
			"grammar X2;\n" +
			"s : ID '=' expr ';' | expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X2.g4", grammar, "X2Parser", "X2Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher m = getPatternMatcher("X2");

		boolean failed = false;
		try {
			m.compile("<ID> <ID> ;", m.getParser().getRuleIndex("s"));
		}
		catch (NoViableAltException e) {
			failed = true;
		}
		assertTrue(failed);
	}

	@Test
	public void testHiddenTokensNotSeenByTreePatternParser() throws Exception {
		String grammar =
			"grammar X2;\n" +
			"s : ID '=' expr ';' ;\n" +
			"expr : ID | INT ;\n" +
			"ID : [a-z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> channel(HIDDEN) ;\n";
		boolean ok =
			rawGenerateAndBuildRecognizer("X2.g4", grammar, "X2Parser", "X2Lexer", false);
		assertTrue(ok);

		ParseTreePatternMatcher m = getPatternMatcher("X2");

		ParseTreePattern t = m.compile("<ID> = <expr> ;", m.getParser().getRuleIndex("s"));
		String results = t.getPatternTree().toStringTree(m.getParser());
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

		ParseTreePatternMatcher m =	getPatternMatcher("X2");

		ParseTreePattern t = m.compile("<ID> = <ID> ;", m.getParser().getRuleIndex("s"));
		String results = t.getPatternTree().toStringTree(m.getParser());
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
		checkPatternMatch(grammar, "s", input, pattern, "X3");
	}

	@Test public void testIDNodeWithLabelMatches() throws Exception {
		String grammar =
			"grammar X8;\n" +
			"s : ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x ;";
		String pattern = "<id:ID>;";
		ParseTreeMatch m = checkPatternMatch(grammar, "s", input, pattern, "X8");
		assertEquals("{ID=[x], id=[x]}", m.getLabels().toString());
		assertNotNull(m.get("id"));
		assertNotNull(m.get("ID"));
		assertEquals("x", m.get("id").getText());
		assertEquals("x", m.get("ID").getText());
		assertEquals("[x]", m.getAll("id").toString());
		assertEquals("[x]", m.getAll("ID").toString());

		assertNull(m.get("undefined"));
		assertEquals("[]", m.getAll("undefined").toString());
	}

	@Test public void testLabelGetsLastIDNode() throws Exception {
		String grammar =
			"grammar X9;\n" +
			"s : ID ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x y;";
		String pattern = "<id:ID> <id:ID>;";
		ParseTreeMatch m = checkPatternMatch(grammar, "s", input, pattern, "X9");
		assertEquals("{ID=[x, y], id=[x, y]}", m.getLabels().toString());
		assertNotNull(m.get("id"));
		assertNotNull(m.get("ID"));
		assertEquals("y", m.get("id").getText());
		assertEquals("y", m.get("ID").getText());
		assertEquals("[x, y]", m.getAll("id").toString());
		assertEquals("[x, y]", m.getAll("ID").toString());

		assertNull(m.get("undefined"));
		assertEquals("[]", m.getAll("undefined").toString());
	}

	@Test public void testIDNodeWithMultipleLabelMatches() throws Exception {
		String grammar =
			"grammar X7;\n" +
			"s : ID ID ID ';' ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\r\\n\\t]+ -> skip ;\n";

		String input = "x y z;";
		String pattern = "<a:ID> <b:ID> <a:ID>;";
		ParseTreeMatch m = checkPatternMatch(grammar, "s", input, pattern, "X7");
		assertEquals("{ID=[x, y, z], a=[x, z], b=[y]}", m.getLabels().toString());
		assertNotNull(m.get("a")); // get first
		assertNotNull(m.get("b"));
		assertNotNull(m.get("ID"));
		assertEquals("z", m.get("a").getText());
		assertEquals("y", m.get("b").getText());
		assertEquals("z", m.get("ID").getText()); // get last
		assertEquals("[x, z]", m.getAll("a").toString());
		assertEquals("[y]", m.getAll("b").toString());
		assertEquals("[x, y, z]", m.getAll("ID").toString()); // ordered

		assertEquals("xyz;", m.getTree().getText()); // whitespace stripped by lexer

		assertNull(m.get("undefined"));
		assertEquals("[]", m.getAll("undefined").toString());
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
		checkPatternMatch(grammar, "s", input, pattern, "X4");
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
		checkPatternMatch(grammar, "s", input, pattern, "X4", invertMatch);

		input = "x = 0;";
		pattern = "<ID> = 0;";
		invertMatch = false;
		checkPatternMatch(grammar, "s", input, pattern, "X4", invertMatch);

		input = "x = 0;";
		pattern = "x = 0;";
		invertMatch = false;
		checkPatternMatch(grammar, "s", input, pattern, "X4", invertMatch);

		input = "x = 0;";
		pattern = "y = 0;";
		invertMatch = true;
		checkPatternMatch(grammar, "s", input, pattern, "X4", invertMatch);
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
		checkPatternMatch(grammar, "s", input, pattern, "X5");
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
		checkPatternMatch(grammar, "expr", input, pattern, "X6");
	}

	public ParseTreeMatch checkPatternMatch(String grammar, String startRule,
											String input, String pattern,
											String grammarName)
		throws Exception
	{
		return checkPatternMatch(grammar, startRule, input, pattern, grammarName, false);
	}

	public ParseTreeMatch checkPatternMatch(String grammar, String startRule,
											String input, String pattern,
											String grammarName, boolean invertMatch)
		throws Exception
	{
		String grammarFileName = grammarName+".g4";
		String parserName = grammarName+"Parser";
		String lexerName = grammarName+"Lexer";
		boolean ok =
			rawGenerateAndBuildRecognizer(grammarFileName, grammar, parserName, lexerName, false);
		assertTrue(ok);

		ParseTree result = execParser(startRule, input, parserName, lexerName);

		ParseTreePattern p = getPattern(grammarName, pattern, startRule);
		ParseTreeMatch match = p.match(result);
		boolean matched = match.succeeded();
		if ( invertMatch ) assertFalse(matched);
		else assertTrue(matched);
		return match;
	}

	public ParseTreePattern getPattern(String grammarName, String pattern, String ruleName)
		throws Exception
	{
		Class<? extends Lexer> lexerClass = loadLexerClassFromTempDir(grammarName + "Lexer");
		Constructor<? extends Lexer> ctor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = ctor.newInstance((CharStream) null);

		Class<? extends Parser> parserClass = loadParserClassFromTempDir(grammarName + "Parser");
		Constructor<? extends Parser> pctor = parserClass.getConstructor(TokenStream.class);
		Parser parser = pctor.newInstance(new CommonTokenStream(lexer));

		return parser.compileParseTreePattern(pattern, parser.getRuleIndex(ruleName));
	}

	public ParseTreePatternMatcher getPatternMatcher(String grammarName)
		throws Exception
	{
		Class<? extends Lexer> lexerClass = loadLexerClassFromTempDir(grammarName + "Lexer");
		Constructor<? extends Lexer> ctor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = ctor.newInstance((CharStream) null);

		Class<? extends Parser> parserClass = loadParserClassFromTempDir(grammarName + "Parser");
		Constructor<? extends Parser> pctor = parserClass.getConstructor(TokenStream.class);
		Parser parser = pctor.newInstance(new CommonTokenStream(lexer));

		return new ParseTreePatternMatcher(lexer, parser);
	}
}
