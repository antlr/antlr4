/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

/** Test errors with the set stuff in lexer and parser */
public class TestErrorSets extends BaseTest {
	protected boolean debug = false;

	/** Public default constructor used by TestRig */
	public TestErrorSets() {
	}

	@Test public void testNotCharSetWithRuleRef() throws Exception {
		// might be a useful feature to add someday
		String[] pair = new String[] {
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|B) ;\n" +
			"B : 'b' ;\n",
			"error(" + ErrorType.UNSUPPORTED_REFERENCE_IN_LEXER_SET.code + "): T.g4:3:10: rule reference B is not currently supported in a set\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testNotCharSetWithString() throws Exception {
		// might be a useful feature to add someday
		String[] pair = new String[] {
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|'aa') ;\n" +
			"B : 'b' ;\n",
			"error(" + ErrorType.INVALID_LITERAL_IN_LEXER_SET.code + "): T.g4:3:10: multi-character literals are not allowed in lexer sets: 'aa'\n"
		};
		super.testErrors(pair, true);
	}


}
