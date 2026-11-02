/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSemanticContext {
	// A and B share a bucket of a 16-bucket java.util.HashSet, so operands kept
	// in a hash set's iteration order fail the IgnoresOperandOrder tests.
	private static final SemanticContext A = new SemanticContext.Predicate(0, 0, false);
	private static final SemanticContext B = new SemanticContext.Predicate(0, 2, false);
	private static final SemanticContext C = new SemanticContext.Predicate(0, 3, false);
	private static final SemanticContext PREC_2 = new SemanticContext.PrecedencePredicate(2);
	private static final SemanticContext PREC_5 = new SemanticContext.PrecedencePredicate(5);

	@Test public void testAndIgnoresOperandOrder() {
		assertEquals(SemanticContext.and(A, B), SemanticContext.and(B, A));
		assertEquals(SemanticContext.and(A, B).hashCode(), SemanticContext.and(B, A).hashCode());

		assertEquals("{0:0}?&&{0:2}?", SemanticContext.and(B, A).toString());
	}

	@Test public void testOrIgnoresOperandOrder() {
		assertEquals(SemanticContext.or(A, B), SemanticContext.or(B, A));
		assertEquals(SemanticContext.or(A, B).hashCode(), SemanticContext.or(B, A).hashCode());
	}

	@Test public void testAndKeepsLowestPrecedenceOrKeepsHighest() {
		assertEquals("{0:0}?&&{2>=prec}?", SemanticContext.and(SemanticContext.and(PREC_5, A), PREC_2).toString());

		assertEquals("{0:0}?||{5>=prec}?", SemanticContext.or(SemanticContext.or(PREC_2, A), PREC_5).toString());
	}

	@Test public void testOrOrdersAndsOperandByOperand() {
		assertEquals("{0:0}?&&{0:2}?||{0:0}?&&{0:3}?",
			SemanticContext.or(SemanticContext.and(A, C), SemanticContext.and(A, B)).toString());
	}

	@Test public void testAndKeepsUnequalOperandsWithEqualHashCodes() {
		SemanticContext and = SemanticContext.and(new ConstantHashContext(), new ConstantHashContext());
		assertEquals(2, ((SemanticContext.AND)and).opnds.length);
	}

	private static class ConstantHashContext extends SemanticContext {
		@Override public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) { return true; }
		@Override public int hashCode() { return 0; }
	}
}
