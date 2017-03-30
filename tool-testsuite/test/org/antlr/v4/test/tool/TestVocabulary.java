/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		for (int i = 0; i < tokenNames.length; i++) {
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

}
