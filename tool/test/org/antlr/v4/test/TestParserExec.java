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

	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID INT {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "abc 34", false);
		assertEquals("abc34\n", found);
	}

	@Test public void testAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID+ {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
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

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID* {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
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

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
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

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
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

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}

	@Test public void testNongreedyLoopCantSeeEOF() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : block EOF {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"block : '{' .* '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"{ }";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("{}\n", found);
		input =
			"{a b { } ;";
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("{ab{}\n", found);
		input =
			"{ } a 2) { } ;"; // FAILS to match since it terminates loop at first { }
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("", found); // should not print output; resync kills rest of input
	}

	@Test public void testNongreedyLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ifstat ';' EOF {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ifstat : 'if' '(' .* ')' block ;\n" +
			"block : '{' '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"if ( x=34 ) { } ;";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("if(x=34){};\n", found);
		input =
			"if ( ))) ) { } ;";
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("if()))){};\n", found);
		input =
			"if (() { } a 2) { } ;"; // FAILS to match since it terminates loop at first { }
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("", found); // should not finish to print output
	}

	@Test public void testNongreedyLoopPassingThroughAnotherNongreedy() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ifstat ';' EOF {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ifstat : 'if' '(' .* ')' block ;\n" +
			"block : '{' (block|.)* '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input1 =
			"if ( x=34 ) { {return a} b 34 } ;";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input1, false);
		assertEquals("if(x=34){{returna}b34};\n", found);
	}

	@Test public void testStatLoopNongreedyNotNecessary() throws Exception {
		// EOF on end means LL(*) can identify when to stop the loop.
		String grammar =
			"grammar T;\n" +
			"s : stat* ID '=' ID ';' EOF {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"stat : 'if' '(' INT ')' stat\n" +
			"     | 'return' INT ';'\n" +
			"     | ID '=' (INT|ID) ';'\n" +
			"     | block\n" +
			"     ;\n" +
			"block : '{' stat* '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"x=1; a=b;";
		String found = null;
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("x=1;a=b;\n", found);
		input =
			"if ( 1 ) { x=3; { return 4; } } return 99; abc=def;";
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("if(1){x=3;{return4;}}return99;abc=def;\n", found);
		input =
			"x=1; a=3;"; // FAILS to match since it can't match last element
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		// can't match EOF to ID '=' '0' ';'
		assertEquals("no viable token at input EOF, index 8\n", found);
		input =
			"x=1; a=b; z=3;"; // FAILS to match since it can't match last element
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("no viable token at input EOF, index 12\n", found); // should not finish to print output
	}

	@Test public void testStatLoopNongreedyNecessary() throws Exception {
		// stops scanning ahead at end of rule s since decision is nongreedy.
		// this says: "match statements until we see a=b; assignment; ignore any
		// statements that follow."
		String grammar =
			"grammar T;\n" +
			"random : s ;" + // call s so s isn't followed by EOF directly
			"s : (options {greedy=false;} : stat)* ID '=' ID ';'\n" +
			"    {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"stat : 'if' '(' INT ')' stat\n" +
			"     | 'return' INT ';'\n" +
			"     | ID '=' (INT|ID) ';'\n" +
			"     | block\n" +
			"     ;\n" +
			"block : '{' stat* '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"x=1; a=b; x=y;";
		String found = null;
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("x=1;a=b;\n", found); // ignores x=1 that follows first a=b assignment
		input =
			"if ( 1 ) { x=3; { return 4; } } return 99; abc=def;";
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("if(1){x=3;{return4;}}return99;abc=def;\n", found);
		input =
			"x=1; a=3;"; // FAILS to match since it can't match either stat
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		// can't match EOF to ID '=' '0' ';'
		assertEquals("no viable token at input EOF, index 8\n", found);
		input =
			"x=1; a=b; z=3;"; // stops at a=b; ignores z=3;
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("x=1;a=b;\n", found); // should not finish all input
	}

	@Test public void testHTMLTags() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : tag+ {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"tag : '<' '/'? .* '>'  ;\n" +
			"EQ : '=' ;\n" +
			"COMMA : ',' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"STR : '\"' (options {greedy=false;}:.)* '\"' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = null;
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "<a>foo</a>", false);
		assertEquals("<a>foo</a>\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "<a></a>", false);
		assertEquals("<a></a>\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "</b><a src=\"abc\", width=32>", false);
		assertEquals("</b><asrc=\"abc\",width=32>\n", found);
	}

	/** lookahead prediction with '.' can be misleading since nongreedy. Lookahead
	 *  that sees into a non-greedy loop, thinks it is greedy.
	 */
	@Test public void testFindHTMLTags() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ( .* (tag {System.out.println($tag.text);} |header) )* EOF;\n" +
			"tag : '<' .+ '>'  ;\n" +
			"header : 'x' 'y' ;\n" +
			"EQ : '=' ;\n" +
			"COMMA : ',' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"STR : '\"' (options {greedy=false;}:.)* '\"' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = null;
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  ",=foo <a x= 3>32skidoo<a><img>", false);
		assertEquals("<ax=3>\n" +
					 "<a>\n" +
					 "<img>\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "x x<a>", false);
		assertEquals("<a>\n", found);
		// gets line 1:3 no viable alternative at input '>'. Why??
		// oH! it sees .+ and figures it matches > so <> predicts tag CORRECT!
		// Seeing '.' in a lookahead prediction can be misleading!!
		found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "x <><a>", false);
		assertEquals("null\n" + // doesn't match tag; null
					 "<a>\n", found);
	}

	/** See comment on testNongreedyLoopEndOfRuleStuffFollowing */
	@Test public void testNongreedyLoopEndOfRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : ifstat EOF {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
			"ifstat : 'if' '(' INT ')' .* ;\n" +
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"if ( 34 ) a b";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("if(34)ab\n", found);
		input =
		"if ( 34 ))) ) ( a = = b( ;";
		found = execParser("T.g", grammar, "TParser", "TLexer", "s",
						   input, false);
		assertEquals("if(34))))(a==b(\n", found);
	}

	/** When .* is on the end of a rule, no tokens predict the exit branch of the loop
	 *  since it immediately hits the end of the rule.  Non-greedy loops
	 *  never consume more tokens than exist following the .* end that
	 *  same rule. So, in this case, the greedy loop always wins and it will
	 *  suck tokens until end of file. Unfortunately, the '.' in rule s
	 *  will not match, leading to a syntax error.
	 */
	@Test public void testNongreedyLoopEndOfRuleStuffFollowing() throws Exception {
		String grammar =
		"grammar T;\n" +
		"s : ifstat '.' {System.out.println(_input.toString(0,_input.index()-1));} ;\n" +
		"ifstat : 'if' '(' INT ')' .* ;\n" +
		"EQ : '=' ;\n" +
		"INT : '0'..'9'+ ;\n" +
		"ID : 'a'..'z'+ ;\n" +
		"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
		"if ( 34 ) a b .";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  input, false);
		assertEquals("no viable token at input EOF, index 7\nif(34)ab.\n", found);
	}
}
