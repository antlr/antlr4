package org.antlr.v4.test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

		ParseTreePatternMatcher p = getMatcher("X1");

		List<? extends Token> tokens = p.tokenize("<ID> = <expr> ;");
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

		ParseTreePatternMatcher p = getMatcher("X2");

		ParseTreePattern t = p.compile("s", "<ID> = <expr> ;");
		String results = t.patternTree.toStringTree(p.getParser());
		String expected = "(s <ID> = (expr <expr>) ;)";
		assertEquals(expected, results);
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

		ParseTreePatternMatcher p = getMatcher("X2");

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

		ParseTreePatternMatcher p =	getMatcher("X2");

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
		assertEquals("x", m.get("a").getText());
		assertEquals("y", m.get("b").getText());
		assertEquals("x", m.get("ID").getText()); // get first
		assertEquals("[x, z]", m.getAll("a").toString());
		assertEquals("[y]", m.getAll("b").toString());
		assertEquals("[x, y, z]", m.getAll("ID").toString()); // ordered

		assertEquals("xyz;", m.getText()); // whitespace stripped by lexer

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

		ParseTreePatternMatcher p = getMatcher(grammarName);
		ParseTreeMatch match = p.match(result, pattern, startRule);
		boolean matched = match.succeeded();
		if ( invertMatch ) assertFalse(matched);
		else assertTrue(matched);
		return match;
	}

	public ParseTreePatternMatcher getMatcher(String name) throws Exception {
		Class<? extends Lexer> lexerClass = loadLexerClassFromTempDir(name+"Lexer");
		Class<? extends Parser> parserClass = loadParserClassFromTempDir(name + "Parser");
		Class<? extends Lexer> c = lexerClass.asSubclass(Lexer.class);
		Constructor<? extends Lexer> ctor = c.getConstructor(CharStream.class);
		Lexer lexer = ctor.newInstance((CharStream) null);

		Class<? extends Parser> pc = parserClass.asSubclass(Parser.class);
		Constructor<? extends Parser> pctor = pc.getConstructor(TokenStream.class);
		Parser parser = pctor.newInstance((TokenStream)null);

		return new ParseTreePatternMatcher(lexer, parser);
	}
}
