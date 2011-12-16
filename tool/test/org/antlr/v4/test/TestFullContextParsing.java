/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test;

import org.junit.Test;

/*
	 cover these cases:
	    dead end
	    single alt
	    single alt + preds
	    conflict
	    conflict + preds

 */
public class TestFullContextParsing extends BaseTest {
	@Test public void testAmbigYieldsNonCtxSensitiveDFA() {
		String grammar =
			"grammar T;\n"+
			"s" +
			"@after {dumpDFA();}\n" +
			"    : ID | ID {;} ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ {skip();} ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s",
								   "abc", true);
		String expecting =
			"Decision 0:\n" +
			"s0-ID->:s1=>1\n"; // not ctx sensitive
		assertEquals(expecting, result);
		assertEquals("line 1:0 reportAmbiguity d=0: {1..2}:[(1,1,[]), (1,2,[])],conflictingAlts={1..2}, input=abc\n",
					 this.stderrDuringParse);
	}

	@Test public void testCtxSensitiveDFA() {
		String grammar =
			"grammar T;\n"+
			"s @after {dumpDFA();}\n" +
			"  : '$' a | '@' b ;\n" +
			"a : e ID ;\n" +
			"b : e INT ID ;\n" +
			"e : INT | ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"INT : '0'..'9'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ {skip();} ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s",
								   "$ 34 abc", true);
		String expecting =
			"Decision 1:\n" +
			"s0-INT->s1\n" +
			"s1-ID->:s2@{[18 10]=1}\n";
		assertEquals(expecting, result);
		assertEquals("line 1:2 reportContextSensitivity d=1: [(20,1,[10])],uniqueAlt=1, input=34\n",
					 this.stderrDuringParse);

		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							"@ 34 abc", true);
		expecting =
			"Decision 1:\n" +
			"s0-INT->s1\n" +
			"s1-ID->:s2@{[22 14]=2}\n";
		assertEquals(expecting, result);
		assertEquals("line 1:5 reportContextSensitivity d=1: [(1,2,[])],uniqueAlt=2, input=34abc\n",
					 this.stderrDuringParse);
	}

	@Test public void testCtxSensitiveDFATwoDiffInput() {
		String grammar =
			"grammar T;\n"+
			"s @after {dumpDFA();}\n" +
			"  : ('$' a | '@' b)+ ;\n" +
			"a : e ID ;\n" +
			"b : e INT ID ;\n" +
			"e : INT | ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"INT : '0'..'9'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ {skip();} ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s",
								   "$ 34 abc @ 34 abc", true);
		String expecting =
			"Decision 1:\n" +
			"s0-EOF->:s3=>2\n" +
			"s0-'@'->:s2=>1\n" +
			"s0-'$'->:s1=>1\n" +
			"\n" +
			"Decision 2:\n" +
			"s0-INT->s1\n" +
			"s1-ID->:s2@{[20 10]=1, [24 14]=2}\n";
		assertEquals(expecting, result);
		assertEquals("line 1:2 reportContextSensitivity d=2: [(22,1,[10])],uniqueAlt=1, input=34\n" +
					 "line 1:14 reportContextSensitivity d=2: [(8,2,[]), (12,2,[]), (1,2,[])],uniqueAlt=2, input=34abc\n",
					 this.stderrDuringParse);
	}

	@Test public void testFullContextIF_THEN_ELSEParse() {
		String grammar =
			"grammar T;\n"+
			"s" +
			"@after {dumpDFA();}\n" +
			"    : '{' stat* '}'" +
			"    ;\n" +
			"stat: 'if' ID 'then' stat ('else' stat)?\n" +
			"    | 'break'\n" +
			"    | 'return'\n" +
			"    ;" +
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ {skip();} ;\n";
		String input = "{ if x then break }";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s",
								   input, true);
		String expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'}'->:s1=>2\n";
		assertEquals(expecting, result);
		assertEquals(null, this.stderrDuringParse);

		input =
			"{ if x then if y then break else break }";
		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							input, true);
		expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'else'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n";
		assertEquals(expecting, result);
		assertEquals("line 1:39 reportAmbiguity d=1: {1..2}:[(1,1,[]), (1,2,[])],conflictingAlts={1..2}, input=elsebreak}\n",
					 this.stderrDuringParse);

		input = "{ if x then break else return }";
		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							input, true);
		expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'else'->:s1@{[6]=1}\n";
		assertEquals(expecting, result);
		assertEquals("line 1:18 reportContextSensitivity d=1: [(15,1,[25 6]), (29,1,[25 6]), (31,1,[25 6])],uniqueAlt=1, input=else\n",
					 this.stderrDuringParse);

		input = "{ if x then break else return }";
		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							input, true);
		expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'else'->:s1@{[6]=1}\n";
		assertEquals(expecting, result);
		assertEquals("line 1:18 reportContextSensitivity d=1: [(15,1,[25 6]), (29,1,[25 6]), (31,1,[25 6])],uniqueAlt=1, input=else\n",
					 this.stderrDuringParse);

		input =
			"{ if x then break else return\n" +
			"if x then if y then break else return }";
		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							input, true);
		expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'else'->:s1@{[6]=1, [21 6]=1}\n" +
			"s0-'}'->:s2=>2\n";
		assertEquals(expecting, result);
		assertEquals("line 1:18 reportContextSensitivity d=1: [(15,1,[25 6]), (29,1,[25 6]), (31,1,[25 6])],uniqueAlt=1, input=else\n" +
					 "line 2:38 reportAmbiguity d=1: {1..2}:[(1,1,[]), (1,2,[])],conflictingAlts={1..2}, input=elsereturn}\n",
					 this.stderrDuringParse);

		input =
			"{ if x then break else return\n" +
			"if x then if y then break else return }";
		result = execParser("T.g", grammar, "TParser", "TLexer", "s",
							input, true);
		expecting =
			"Decision 0:\n" +
			"s0-'if'->:s1=>1\n" +
			"s0-'}'->:s2=>2\n" +
			"\n" +
			"Decision 1:\n" +
			"s0-'else'->:s1@{[6]=1, [21 6]=1}\n" +
			"s0-'}'->:s2=>2\n";
		assertEquals(expecting, result);
		assertEquals("line 1:18 reportContextSensitivity d=1: [(15,1,[25 6]), (29,1,[25 6]), (31,1,[25 6])],uniqueAlt=1, input=else\n" +
					 "line 2:38 reportAmbiguity d=1: {1..2}:[(1,1,[]), (1,2,[])],conflictingAlts={1..2}, input=elsereturn}\n",
					 this.stderrDuringParse);
	}

}
