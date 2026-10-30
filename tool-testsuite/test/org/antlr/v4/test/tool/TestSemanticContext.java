/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.SemanticContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSemanticContext {
	// These two predicates share a bucket of a 16-bucket hash set, so the order
	// they are combined in decides the order the set iterates them in.
	private static final SemanticContext A = new SemanticContext.Predicate(0, 0, false);
	private static final SemanticContext B = new SemanticContext.Predicate(0, 2, false);

	@Test public void testAndIgnoresOperandOrder() {
		assertEquals(SemanticContext.and(A, B), SemanticContext.and(B, A));
		assertEquals(SemanticContext.and(A, B).hashCode(), SemanticContext.and(B, A).hashCode());
	}

	@Test public void testOrIgnoresOperandOrder() {
		assertEquals(SemanticContext.or(A, B), SemanticContext.or(B, A));
		assertEquals(SemanticContext.or(A, B).hashCode(), SemanticContext.or(B, A).hashCode());
	}
}
