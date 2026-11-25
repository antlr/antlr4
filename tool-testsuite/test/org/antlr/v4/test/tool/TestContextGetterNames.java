/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Go target capitalizes context getters and prefixes the getter for a
 *  whole list with {@code All}, and PHP method names are case-insensitive, so
 *  the getters of different rules and tokens can end up with the same method
 *  name. These tests pin down the names the tool picks to keep them apart.
 */
public class TestContextGetterNames {
	private static final String LEXER_RULES =
		"AllFoo : 'all' ;\n" +
		"Foo : [a-z]+ ;\n";

	@Test public void testRuleGetterAvoidsTokenGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo Foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext", "Foo() antlr.TerminalNode", "Foo_() IFooContext");
	}

	@Test public void testRenamedRuleGetterAvoidsOtherRuleGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo foo_ Foo ;\n" +
			"foo : Foo ;\n" +
			"foo_ : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext",
			"Foo() antlr.TerminalNode", "Foo_() IFoo_Context", "Foo__() IFooContext");
	}

	@Test public void testRuleGetterAvoidsTokenListGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : allFoo Foo Foo ;\n" +
			"allFoo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext",
			"AllFoo() []antlr.TerminalNode", "AllFoo_() IAllFooContext", "Foo(i int) antlr.TerminalNode");
	}

	@Test public void testRuleListGetterAvoidsTokenGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo foo AllFoo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext",
			"AllFoo() antlr.TerminalNode", "AllFoo_() []IFooContext", "Foo_(i int) IFooContext");
	}

	@Test public void testRuleListGetterAvoidsRuleGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : allFoo foo foo ;\n" +
			"allFoo : Foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext",
			"AllFoo() IAllFooContext", "AllFoo_() []IFooContext", "Foo_(i int) IFooContext");
	}

	@Test public void testTokenListGetterAvoidsTokenGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : AllFoo Foo Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext",
			"AllFoo() antlr.TerminalNode", "AllFoo_() []antlr.TerminalNode", "Foo_(i int) antlr.TerminalNode");
		// The renamed getters still ask for the token type under its own name.
		assertTrue(parser.contains("return s.GetTokens(TParserFoo)"), parser);
		assertTrue(parser.contains("return s.GetToken(TParserFoo, i)"), parser);
	}

	@Test public void testGetterNameIsTheSameInEveryContext() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo Foo #withTok\n" +
			"  | foo #withoutTok\n" +
			"  ;\n" +
			"b : foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "WithTokContext", "Foo() antlr.TerminalNode", "Foo_() IFooContext");
		assertGoGetters(parser, "WithoutTokContext", "Foo_() IFooContext");
		assertGoGetters(parser, "BContext", "Foo_() IFooContext");
		assertGoGetters(parser, "FooContext", "Foo() antlr.TerminalNode");
	}

	@Test public void testRenameForOneContextAvoidsGettersOfAnother() throws Exception {
		// Renaming foo to stay clear of Foo in a must not land on foo_ in b.
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo Foo ;\n" +
			"b : foo foo_ ;\n" +
			"foo : Foo ;\n" +
			"foo_ : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext", "Foo() antlr.TerminalNode", "Foo__() IFooContext");
		assertGoGetters(parser, "BContext", "Foo_() IFoo_Context", "Foo__() IFooContext");
	}

	@Test public void testGrammarWithoutClashKeepsGetterNames() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo ;\n" +
			"b : foo foo ;\n" +
			"c : Foo Foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGoGetters(parser, "AContext", "Foo() IFooContext");
		assertGoGetters(parser, "BContext", "AllFoo() []IFooContext", "Foo(i int) IFooContext");
		assertGoGetters(parser, "CContext", "AllFoo() []antlr.TerminalNode", "Foo(i int) antlr.TerminalNode");
		assertGoGetters(parser, "FooContext", "Foo() antlr.TerminalNode");
	}

	@Test public void testPHPRuleGetterAvoidsTokenGetter() throws Exception {
		String parser = generateParser(
			"grammar T;\n" +
			"a : foo Foo ;\n" +
			"b : foo foo Foo Foo ;\n" +
			"c : foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES, "PHP");
		assertPHPGetters(parser, "AContext",
			"Foo(): ?TerminalNode", "foo_(): ?FooContext");
		assertPHPGetters(parser, "BContext",
			"Foo(?int $index = null)", "foo_(?int $index = null)");
		assertPHPGetters(parser, "CContext", "foo_(): ?FooContext");
		// The renamed getters still ask for the token type under its own name.
		assertTrue(parser.contains("return $this->getToken(TParser::Foo, 0);"), parser);
	}

	@Test public void testPHPGetterNamesDifferingBeyondASCIICaseAreKept() throws Exception {
		// PHP folds the case of ASCII letters only.
		String parser = generateParser(
			"grammar T;\n" +
			"a : \u00e9t\u00e9 \u00c9t\u00e9 ;\n" +
			"\u00e9t\u00e9 : \u00c9t\u00e9 ;\n" +
			"\u00c9t\u00e9 : [a-z]+ ;\n", "PHP");
		assertPHPGetters(parser, "AContext",
			"\u00c9t\u00e9(): ?TerminalNode", "\u00e9t\u00e9(): ?\u00c9t\u00e9Context");
	}

	@Test public void testPHPGrammarWithoutClashKeepsGetterNames() throws Exception {
		// The PHP getters of rules named after reserved words are not escaped.
		String parser = generateParser(
			"grammar T;\n" +
			"a : list Foo ;\n" +
			"b : list list ;\n" +
			"list : Foo ;\n" +
			LEXER_RULES, "PHP");
		assertPHPGetters(parser, "AContext", "Foo(): ?TerminalNode", "list(): ?ListContext");
		assertPHPGetters(parser, "BContext", "list(?int $index = null)");
	}

	private static String generateGoParser(String grammarString) throws Exception {
		return generateParser(grammarString, "Go");
	}

	private static String generateParser(String grammarString, String language) throws Exception {
		// This constructor also runs the semantic checks and builds the ATN.
		Grammar g = new Grammar(grammarString);
		assertFalse(g.ast.hasErrors);
		// It leaves the name unset, and the Go target derives file names from it.
		g.name = g.ast.getGrammarName();
		return CodeGenerator.create(g.tool, g, language).generateParser().render();
	}

	/** Checks the rule and token getters that Go defines on {@code contextName}. */
	private static void assertGoGetters(String parser, String contextName, String... expected) {
		Pattern getter = Pattern.compile(
			"^func \\(s \\*" + contextName + "\\) (\\w+\\((?:i int)?\\) (?:\\[\\])?(?:antlr\\.TerminalNode|I\\w+Context)) \\{$",
			Pattern.MULTILINE);
		assertMatches(getter, parser, expected);
	}

	/** Checks the rule and token getters in the PHP class {@code contextName}. */
	private static void assertPHPGetters(String parser, String contextName, String... expected) {
		String classStart = " extends ParserRuleContext";
		int start = parser.indexOf("class " + contextName + classStart);
		assertTrue(start >= 0, parser);
		int end = parser.indexOf(classStart, start + contextName.length() + classStart.length());
		String context = parser.substring(start, end < 0 ? parser.length() : end);
		Pattern getter = Pattern.compile(
			"public function ([^ (]+\\((?:\\): \\?(?:TerminalNode|[^ ]+Context)|\\?int \\$index = null\\)))$",
			Pattern.MULTILINE);
		assertMatches(getter, context, expected);
	}

	private static void assertMatches(Pattern getter, String text, String... expected) {
		List<String> actual = new ArrayList<>();
		Matcher matcher = getter.matcher(text);
		while (matcher.find()) {
			actual.add(matcher.group(1));
		}
		Collections.sort(actual);
		assertEquals(Arrays.asList(expected), actual);
	}
}
