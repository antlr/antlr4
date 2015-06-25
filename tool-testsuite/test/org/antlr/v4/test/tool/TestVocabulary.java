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
import org.antlr.v4.test.runtime.java.BaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Sam Harwell
 */
public class TestVocabulary extends BaseTest {

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
