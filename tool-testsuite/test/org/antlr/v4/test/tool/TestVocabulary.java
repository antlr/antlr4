/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.tool.Grammar;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 *
 * @author Sam Harwell
 */
public class TestVocabulary extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test
	public void testEmptyVocabulary() {
		Assert.assertNotNull(VocabularyImpl.EMPTY_VOCABULARY);
		Assert.assertEquals("EOF", VocabularyImpl.EMPTY_VOCABULARY.getSymbolicName(Token.EOF));
		Assert.assertEquals("0", VocabularyImpl.EMPTY_VOCABULARY.getDisplayName(Token.INVALID_TYPE));
	}

	@Test
	public void testVocabularyFromTokenNames() {
		String[] tokenNames = {
			"<INVALID>",
			"TOKEN_REF", "RULE_REF", "'//'", "'/'", "'*'", "'!'", "ID", "STRING"
		};

		Vocabulary vocabulary = VocabularyImpl.fromTokenNames(tokenNames);
		Assert.assertNotNull(vocabulary);
		Assert.assertEquals("EOF", vocabulary.getSymbolicName(Token.EOF));
		Assert.assertEquals(vocabulary.getMaxTokenType(), tokenNames.length-1);
		for (int i = 0; i <= vocabulary.getMaxTokenType(); i++) {
			Assert.assertEquals(tokenNames[i], vocabulary.getDisplayName(i));

			if (tokenNames[i].startsWith("'")) {
				Assert.assertEquals(tokenNames[i], vocabulary.getLiteralName(i));
				Assert.assertNull(vocabulary.getSymbolicName(i));
			}
			else if (Character.isUpperCase(tokenNames[i].charAt(0))) {
				Assert.assertNull(vocabulary.getLiteralName(i));
				Assert.assertEquals(tokenNames[i], vocabulary.getSymbolicName(i));
			}
			else {
				Assert.assertNull(vocabulary.getLiteralName(i));
				Assert.assertNull(vocabulary.getSymbolicName(i));
			}
		}
	}


	/** Test for https://github.com/antlr/antlr4/issues/1309
	 *  Hmm...can't reproduce. Seems ok.
	 */
	@Test
	public void testLastTokenType() throws Exception {
		Grammar g = new Grammar(
			"grammar T;\n" +
			"tokens {\n"+
			"   X, Y, Z\n" +  // tokens 1, 2, 3
			"}\n" +
			"a : A B ;\n" +   // tokens 4, 5
			"X : 'x' ;\n"
		);
		assertEquals(5, g.getMaxTokenType());
		Vocabulary v = g.getVocabulary();
		assertEquals(5, v.getMaxTokenType());
		assertEquals("'x'", v.getDisplayName(1));
		assertEquals("Y", v.getDisplayName(2));
		assertEquals("Z", v.getDisplayName(3));
		assertEquals("A", v.getDisplayName(4));
		assertEquals("B", v.getDisplayName(5));
		assertEquals("X", v.getSymbolicName(1));
		assertEquals(5, g.getTokenType("B"));
	}
}
