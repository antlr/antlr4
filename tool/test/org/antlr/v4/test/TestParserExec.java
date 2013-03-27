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

package org.antlr.v4.test;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/** Test parser execution.
 *
 *  For the non-greedy stuff, the rule is that .* or any other non-greedy loop
 *  (any + or * loop that has an alternative with '.' in it is automatically
 *  non-greedy) never sees past the end of the rule containing that loop.
 *  There is no automatic way to detect when the exit branch of a non-greedy
 *  loop has seen enough input to determine how much the loop should consume
 *  yet still allow matching the entire input. Of course, this is extremely
 *  inefficient, particularly for things like
 *
 *     block : '{' (block|.)* '}' ;
 *
 *  that need only see one symbol to know when it hits a '}'. So, I
 *  came up with a practical solution.  During prediction, the ATN
 *  simulator never fall off the end of a rule to compute the global
 *  FOLLOW. Instead, we terminate the loop, choosing the exit branch.
 *  Otherwise, we predict to reenter the loop.  For example, input
 *  "{ foo }" will allow the loop to match foo, but that's it. During
 *  prediction, the ATN simulator will see that '}' reaches the end of a
 *  rule that contains a non-greedy loop and stop prediction. It will choose
 *  the exit branch of the inner loop. So, the way in which you construct
 *  the rule containing a non-greedy loop dictates how far it will scan ahead.
 *  Include everything after the non-greedy loop that you know it must scan
 *  in order to properly make a prediction decision. these beasts are tricky,
 *  so be careful. don't liberally sprinkle them around your code.
 *
 *  To simulate filter mode, use ( .* (pattern1|pattern2|...) )*
 *
 *  Nongreedy loops match as much input as possible while still allowing
 *  the remaining input to match.
 */
public class TestParserExec extends BaseTest {
	@Test public void testLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : b1=b b2+=b* b3+=';' ;\n" +
			"b : id=ID val+=INT*;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34;", false);
		assertEquals("", found);
		assertEquals(null, stderrDuringParse);
	}

	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID INT {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34", false);
		assertEquals("abc34\n", found);
	}

	@Test public void testAorB() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID {System.out.println(\" alt 1\");}" +
			"  | INT {System.out.println(\"alt 2\");}" +
				";\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "34", false);
		assertEquals("alt 2\n", found);
	}

	@Test public void testAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	// force complex decision
	@Test public void testAorAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ID)+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	private static final String ifIfElseGrammarFormat =
		"grammar T;\n" +
		"start : statement+ ;\n" +
		"statement : 'x' | ifStatement;\n" +
		"ifStatement : 'if' 'y' statement %s {System.out.println($text);};\n" +
		"ID : 'a'..'z'+ ;\n" +
		"WS : (' '|'\\n') -> channel(HIDDEN);\n";

	@Test public void testIfIfElseGreedyBinding() throws Exception {
		final String input = "if y if y x else x";
		final String expectedInnerBound = "if y x else x\nif y if y x else x\n";

		String grammar = String.format(ifIfElseGrammarFormat, "('else' statement)?");
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedInnerBound, found);

		grammar = String.format(ifIfElseGrammarFormat, "('else' statement|)");
		found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedInnerBound, found);
	}

	@Test public void testIfIfElseNonGreedyBinding() throws Exception {
		final String input = "if y if y x else x";
		final String expectedOuterBound = "if y x\nif y if y x else x\n";

		String grammar = String.format(ifIfElseGrammarFormat, "('else' statement)??");
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedOuterBound, found);

		grammar = String.format(ifIfElseGrammarFormat, "(|'else' statement)");
		found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedOuterBound, found);
	}

	@Test public void testAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	// force complex decision
	@Test public void testAorAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ID)* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testAorBPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|INT{;})+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}

	@Test public void testAorBStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|INT{;})* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}


	/**
	 * This test is meant to detect regressions of bug antlr/antlr4#41.
	 * https://github.com/antlr/antlr4/issues/41
	 */
	@Test
	public void testOptional() throws Exception {
		String grammar =
			"grammar T;\n" +
			"stat : ifstat | 'x';\n" +
			"ifstat : 'if' stat ('else' stat)?;\n" +
			"WS : [ \\n\\t]+ -> skip ;"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if x else x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if if x else x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This test is meant to test the expected solution to antlr/antlr4#42.
	 * https://github.com/antlr/antlr4/issues/42
	 */
	@Test
	public void testPredicatedIfIfElse() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : stmt EOF ;\n" +
			"stmt : ifStmt | ID;\n" +
			"ifStmt : 'if' ID stmt ('else' stmt | {_input.LA(1) != ELSE}?);\n" +
			"ELSE : 'else';\n" +
			"ID : [a-zA-Z]+;\n" +
			"WS : [ \\n\\t]+ -> skip;\n"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "if x if x a else b", true);
		String expecting = "";
		assertEquals(expecting, found);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#118.
	 * https://github.com/antlr/antlr4/issues/118
	 */
	@Ignore("Performance impact of passing this test may not be worthwhile")
	@Test public void testStartRuleWithoutEOF() {
		String grammar =
			"grammar T;\n"+
			"s @after {dumpDFA();}\n" +
			"  : ID | ID INT ID ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"INT : '0'..'9'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								   "abc 34", true);
		String expecting =
			"Decision 0:\n" +
			"s0-ID->s1\n" +
			"s1-INT->s2\n" +
			"s2-EOF->:s3=>1\n"; // Must point at accept state
		assertEquals(expecting, result);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#195 "label 'label' type
	 * mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL"
	 * https://github.com/antlr/antlr4/issues/195
	 */
	@Test public void testLabelAliasingAcrossLabeledAlternatives() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : a* EOF;\n" +
			"a\n" +
			"  : label=subrule {System.out.println($label.text);} #One\n" +
			"  | label='y' {System.out.println($label.text);} #Two\n" +
			"  ;\n" +
			"subrule : 'x';\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start",
								  "xy", false);
		assertEquals("x\ny\n", found);
	}

}
