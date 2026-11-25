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
 *  whole list with {@code All}, so the getters of different rules and tokens
 *  can end up with the same method name. These tests pin down the names the
 *  tool picks to keep them apart.
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
		assertGetters(parser, "AContext", "Foo() antlr.TerminalNode", "Foo_() IFooContext");
	}

	@Test public void testRenamedRuleGetterAvoidsOtherRuleGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo foo_ Foo ;\n" +
			"foo : Foo ;\n" +
			"foo_ : Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext",
			"Foo() antlr.TerminalNode", "Foo_() IFoo_Context", "Foo__() IFooContext");
	}

	@Test public void testRuleGetterAvoidsTokenListGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : allFoo Foo Foo ;\n" +
			"allFoo : Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext",
			"AllFoo() []antlr.TerminalNode", "AllFoo_() IAllFooContext", "Foo(i int) antlr.TerminalNode");
	}

	@Test public void testRuleListGetterAvoidsTokenGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo foo AllFoo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext",
			"AllFoo() antlr.TerminalNode", "AllFoo_() []IFooContext", "Foo_(i int) IFooContext");
	}

	@Test public void testRuleListGetterAvoidsRuleGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : allFoo foo foo ;\n" +
			"allFoo : Foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext",
			"AllFoo() IAllFooContext", "AllFoo_() []IFooContext", "Foo_(i int) IFooContext");
	}

	@Test public void testTokenListGetterAvoidsTokenGetter() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : AllFoo Foo Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext",
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
		assertGetters(parser, "WithTokContext", "Foo() antlr.TerminalNode", "Foo_() IFooContext");
		assertGetters(parser, "WithoutTokContext", "Foo_() IFooContext");
		assertGetters(parser, "BContext", "Foo_() IFooContext");
		assertGetters(parser, "FooContext", "Foo() antlr.TerminalNode");
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
		assertGetters(parser, "AContext", "Foo() antlr.TerminalNode", "Foo__() IFooContext");
		assertGetters(parser, "BContext", "Foo_() IFoo_Context", "Foo__() IFooContext");
	}

	@Test public void testGrammarWithoutClashKeepsGetterNames() throws Exception {
		String parser = generateGoParser(
			"grammar T;\n" +
			"a : foo ;\n" +
			"b : foo foo ;\n" +
			"c : Foo Foo ;\n" +
			"foo : Foo ;\n" +
			LEXER_RULES);
		assertGetters(parser, "AContext", "Foo() IFooContext");
		assertGetters(parser, "BContext", "AllFoo() []IFooContext", "Foo(i int) IFooContext");
		assertGetters(parser, "CContext", "AllFoo() []antlr.TerminalNode", "Foo(i int) antlr.TerminalNode");
		assertGetters(parser, "FooContext", "Foo() antlr.TerminalNode");
	}

	private static String generateGoParser(String grammarString) throws Exception {
		// This constructor also runs the semantic checks and builds the ATN.
		Grammar g = new Grammar(grammarString);
		assertFalse(g.ast.hasErrors);
		// It leaves the name unset, and the Go target derives file names from it.
		g.name = g.ast.getGrammarName();
		return CodeGenerator.create(g.tool, g, "Go").generateParser().render();
	}

	/** Checks the rule and token getters defined on {@code contextName}. */
	private static void assertGetters(String parser, String contextName, String... expected) {
		Pattern getter = Pattern.compile(
			"^func \\(s \\*" + contextName + "\\) (\\w+\\((?:i int)?\\) (?:\\[\\])?(?:antlr\\.TerminalNode|I\\w+Context)) \\{$",
			Pattern.MULTILINE);
		List<String> actual = new ArrayList<>();
		Matcher matcher = getter.matcher(parser);
		while (matcher.find()) {
			actual.add(matcher.group(1));
		}
		Collections.sort(actual);
		assertEquals(Arrays.asList(expected), actual);
	}
}
