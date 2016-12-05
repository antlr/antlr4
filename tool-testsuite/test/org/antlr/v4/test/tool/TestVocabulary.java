/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
		for (int i = Token.MIN_USER_TOKEN_TYPE; i <= vocabulary.getMaxTokenType(); i++) {
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
