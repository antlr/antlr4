/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java.api;

import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class provides runtime API tests for the {@code baseContext} rule option.
 */
public class TestBaseContext {
	/**
	 * This JUnit rule is used when testing exception results.
	 */
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	/**
	 * This tests the basic case of a proper return value from a rule specifying {@code baseContext}.
	 */
	@Test
	public void testMainRule() {
		String input = "B";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		// mainRule should produce a MainRuleContext
		BaseContextParser.MainRuleContext result = parser.mainRule();
		Assert.assertEquals(BaseContextParser.RULE_mainRule, result.getRuleIndex());
		Assert.assertNull(result.exception);

		// mainRuleNoA should also produce a MainRuleContext
		parser.getInputStream().seek(0);
		result = parser.mainRuleNoA();
		Assert.assertEquals(BaseContextParser.RULE_mainRule, result.getRuleIndex());
		Assert.assertNull(result.exception);
	}

	/**
	 * This test, when combined with {@link #testMainRuleNoAFailsOnAB}, verifies that rules still match the inputs
	 * defined by the grammar when the {@code baseContext} option is used.
	 */
	@Test
	public void testMainRuleRecognizesAB() {
		String input = "AB";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		// mainRule should produce a MainRuleContext
		BaseContextParser.MainRuleContext result = parser.mainRule();
		Assert.assertEquals(BaseContextParser.RULE_mainRule, result.getRuleIndex());
		Assert.assertNull(result.exception);
	}

	@Test
	public void testMainRuleNoAFailsOnAB() {
		String input = "AB";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));
		parser.setErrorHandler(new BailErrorStrategy());

		// mainRuleNoA does not recognize the input AB (which mainRule does recognize)
		thrown.expect(ParseCancellationException.class);
		parser.mainRuleNoA();
	}

	/**
	 * Verifies that alternatives in rules that specify {@code baseContext} are merged with alternatives in the primary
	 * rule when generating accessor methods.
	 */
	@Test
	public void testListLabels() {
		String input = "B";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		// listLabelPrimary should produce a ListLabelPrimaryContext
		BaseContextParser.ListLabelPrimaryContext result = parser.listLabelPrimary();
		Assert.assertEquals(BaseContextParser.RULE_listLabelPrimary, result.getRuleIndex());
		Assert.assertNull(result.exception);

		// The accessor for C should be a single terminal
		TerminalNode c = result.C();
		Assert.assertNull(c);

		// listLabelAlternative should also produce a ListLabelPrimaryContext
		parser.getInputStream().seek(0);
		result = parser.listLabelAlternative();
		Assert.assertEquals(BaseContextParser.RULE_listLabelPrimary, result.getRuleIndex());
		Assert.assertNull(result.exception);

		// The accessor for 'B' should be a list, because listLabelAlternative has a positive closure
		List<TerminalNode> bList = result.B();
		TerminalNode b = result.B(0);
		Assert.assertSame(b, bList.get(0));
	}

	/**
	 * This test verifies no context class is emitted for {@link BaseContextParser#mainRuleNoA()}.
	 */
	@Test
	public void testOmittedContexts() {
		boolean foundMainRuleContext = false;
		for (Class<?> clazz : BaseContextParser.class.getClasses()) {
			if ("MainRuleContext".equals(clazz.getSimpleName())) {
				foundMainRuleContext = true;
				break;
			}
		}

		// This check ensures the test is capable of detecting regressions
		Assert.assertTrue(foundMainRuleContext);

		for (Class<?> clazz : BaseContextParser.class.getClasses()) {
			Assert.assertNotEquals("MainRuleNoAContext", clazz.getSimpleName());
		}
	}

	/**
	 * Verifies that labeled alternatives work properly with the {@code baseContext} option.
	 */
	@Test
	public void testLabeledAlternatives() {
		String input = "B";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		// labeledAlts1 should produce a LabeledAlts1Context
		BaseContextParser.LabeledAlts1Context result = parser.labeledAlts1();
		Assert.assertEquals(BaseContextParser.RULE_labeledAlts1, result.getRuleIndex());
		Assert.assertThat(result, CoreMatchers.instanceOf(BaseContextParser.ContextName1Context.class));

		// labeledAlts2 should also produce a LabeledAlts1Context, but a different subcontext
		parser.getInputStream().seek(0);
		result = parser.labeledAlts2();
		Assert.assertEquals(BaseContextParser.RULE_labeledAlts1, result.getRuleIndex());
		Assert.assertThat(result, CoreMatchers.instanceOf(BaseContextParser.ContextName2Context.class));
	}

	/**
	 * Verifies that a non-left-recursive rule can specify the name of a left-recursive rule in the {@code baseContext}
	 * option.
	 */
	@Test
	public void testLeftRecursiveBaseContextRule() {
		String input = "B";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		// expr should produce a ExprContext
		BaseContextParser.ExprContext result = parser.expr();
		Assert.assertEquals(BaseContextParser.RULE_expr, result.getRuleIndex());

		// exprPrimaryOnly should also produce a ExprContext
		parser.getInputStream().seek(0);
		result = parser.exprPrimaryOnly();
		Assert.assertEquals(BaseContextParser.RULE_expr, result.getRuleIndex());
	}

	/**
	 * Verifies that two rules with different names are analyzed correctly for the purpose of generating accessor
	 * methods when referenced from another alternative.
	 */
	@Test
	public void testIndirectReference() {
		String input = "BB";
		BaseContextLexer lexer = new BaseContextLexer(new ANTLRInputStream(input));
		BaseContextParser parser = new BaseContextParser(new CommonTokenStream(lexer));

		BaseContextParser.IndirectReferenceContext result = parser.indirectReference();
		Assert.assertEquals(BaseContextParser.RULE_indirectReference, result.getRuleIndex());

		BaseContextParser.MainRuleContext a = result.a;
		Assert.assertNotNull(a);

		BaseContextParser.MainRuleContext b = result.b;
		Assert.assertNotNull(b);

		List<BaseContextParser.MainRuleContext> mainRules = result.mainRule();
		Assert.assertEquals(2, mainRules.size());
		Assert.assertSame(a, mainRules.get(0));
		Assert.assertSame(b, mainRules.get(1));
	}
}
