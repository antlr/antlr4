package org.antlr.v4.test;

import org.junit.Test;

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
			"a : b1=b b2+=b*;\n" +
			"b : id=ID val+=INT*;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34", false);
		assertEquals("", found);
		assertEquals(null, stderrDuringParse);
	}

	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID INT {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

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
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "34", false);
		assertEquals("alt 2\n", found);
	}

	@Test public void testAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID+ {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	// force complex decision
	@Test public void testAorAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ID)+ {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID* {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

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
			"a : (ID|ID)* {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

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
			"a : (ID|INT{;})+ {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}

	@Test public void testAorBStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|INT{;})* {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

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
			"s : a | 'x';\n" +
			"a : 'a' s ('b' s)?;\n"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "axbx", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "ax", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "aaxbx", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This test is meant to test the expected solution to antlr/antlr4#42.
	 * https://github.com/antlr/antlr4/issues/42
	 */
	@Test
	public void testIfIfElse() throws Exception {
		String grammar =
			"grammar T;\n" +
			"stmt : ifStmt | ID;\n" +
			"ifStmt : 'if' ID stmt ('else' stmt | {_input.LA(1) != ELSE}?);\n" +
			"ELSE : 'else';\n" +
			"ID : [a-zA-Z]+;\n" +
			"WS : (' ' | '\\t')+ -> skip;\n"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stmt",
								  "if x if x a else b", true);
		String expecting = "";
		assertEquals(expecting, found);
		assertNull(this.stderrDuringParse);
	}

}
