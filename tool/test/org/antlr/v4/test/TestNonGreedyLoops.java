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

public class TestNonGreedyLoops extends BaseTest {
	@Test public void testNongreedyLoopOnEndIsNop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : any ID EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
			"any : .* ;\n"+
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x", true);
		assertEquals("x\n" +
					 "Decision 0:\n" +
					 "s0-ID->:s1=>2\n", found);
		assertEquals(null, this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "34 x", true);
		assertEquals("34x\n" +
					 "Decision 0:\n" +
					 "s0-INT->:s1=>2\n", found);
		assertEquals("line 1:0 extraneous input '34' expecting ID\n", this.stderrDuringParse);
	}

	@Test public void testNongreedyPlusLoopOnEndIsNop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : any ID EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
			"any : .+ ;\n"+ // .+ on end of rule always gives no viable alt. can't bypass but can't match
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x", true);
		assertEquals("x\n" +
					 "Decision 0:\n" +
					 "s0-ID->:s1=>2\n", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test public void testNongreedyLoopInOtherRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : a {System.out.println(\"alt 1\");} | b {System.out.println(\"alt 2\");} ;\n" +
			"a : .* ID ;\n"+
			"b : .* INT ;\n"+
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x", true);
		assertEquals("alt 1\n" +
					 "Decision 0:\n" +
					 "s0-ID->s1\n" +
					 "s1-EOF->:s2=>1\n" +
					 "\n" +
					 "Decision 1:\n" +
					 "s0-ID->:s1=>2\n", found);
		assertEquals(null, this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   "34", true);
		assertEquals("alt 2\n" +
					 "Decision 0:\n" +
					 "s0-INT->s1\n" +
					 "s1-EOF->:s2=>2\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-INT->:s1=>2\n", found);
		assertEquals(null, this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   "34 x", true);
		assertEquals("alt 1\n" +
					 "Decision 0:\n" +
					 "s0-INT->s1\n" +
					 "s1-ID->s2\n" +
					 "s2-EOF->:s3=>1\n" +
					 "\n" +
					 "Decision 1:\n" +
					 "s0-INT->:s1=>1\n" +
					 "s0-ID->:s2=>2\n", found);
		assertEquals(null, this.stderrDuringParse);
	}

	@Test public void testNongreedyPlusLoopInOtherRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : a {System.out.println(\"alt 1\");} | b {System.out.println(\"alt 2\");} ;\n" +
			"a : .+ ID ;\n"+
			"b : .+ INT ;\n"+
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "2 3 x", true);
		assertEquals("alt 1\n" +
					 "Decision 0:\n" +
					 "s0-INT->s1\n" +
					 "s1-INT->s2\n" +
					 "s2-ID->s3\n" +
					 "s3-EOF->:s4=>1\n" +
					 "\n" +
					 "Decision 1:\n" +
					 "s0-INT->:s1=>1\n" +
					 "s0-ID->:s2=>2\n", found);
		assertEquals(null, this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   "2 3", true);
		assertEquals("alt 2\n" +
					 "Decision 0:\n" +
					 "s0-INT->s1\n" +
					 "s1-INT->s2\n" +
					 "s2-EOF->:s3=>2\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-INT->:s1=>2\n", found);
		assertEquals("line 1:0 no viable alternative at input '2'\n", this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   "a b c 3", true);
		assertEquals("alt 2\n" +
					 "Decision 0:\n" +
					 "s0-ID->s1\n" +
					 "s1-ID->s2\n" +
					 "s2-INT->s3\n" +
					 "s2-ID->s2\n" +
					 "s3-EOF->:s4=>2\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-INT->:s2=>2\n" +
					 "s0-ID->:s1=>1\n", found);
		assertEquals(null, this.stderrDuringParse);
	}

	@Test public void testNongreedyLoopInOneAlt() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : a {System.out.println(\"alt 1\");} EOF | b {System.out.println(\"alt 2\");} EOF ;\n" +
			"a : .* ;\n"+ // s comes here upon ID but then bypasses, error on EOF
			"b : INT ;\n"+
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x", true);
		assertEquals("alt 1\n" +
					 "Decision 0:\n" +
					 "s0-ID->:s1=>1\n", found);
		assertNull(this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   "34", true);
		assertEquals("alt 1\n" +
					 "Decision 0:\n" +
					 "s0-INT->s1\n" +
					 "s1-EOF->:s2=>1\n", found);  // resolves INT EOF to alt 1 from s since ambig 'tween a and b
		assertEquals("line 1:2 reportAmbiguity d=0: ambigAlts={1..2}, input='34'\n",
					 this.stderrDuringParse);
	}

	@Test public void testNongreedyLoopCantSeeEOF() throws Exception {
		String grammar =
		"grammar T;\n" +
		"s @after {dumpDFA();} : block EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
		"block : '{' .* '}' ;\n"+
		"EQ : '=' ;\n" +
		"INT : '0'..'9'+ ;\n" +
		"ID : 'a'..'z'+ ;\n" +
		"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"{ }";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("{}\n" +
					 "Decision 0:\n" +
					 "s0-'}'->:s1=>2\n", found);
		input =
			"{a b { }";
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("{ab{}\n" +
					 "Decision 0:\n" +
					 "s0-'{'->:s1=>1\n" +
					 "s0-'}'->:s2=>2\n" +
					 "s0-ID->:s1=>1\n", found);
		input =
			"{ } a 2 { }"; // FAILS to match since it terminates loop at first { }
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("", found); // should not print output; resync kills rest of input til '}' then returns normally
	}

	@Test public void testNongreedyLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : ifstat ';' EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
			"ifstat : 'if' '(' .* ')' block ;\n" +
			"block : '{' '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"if ( x=34 ) { } ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("if(x=34){};\n" +
					 "Decision 0:\n" +
					 "s0-')'->s2\n" +
					 "s0-'='->:s1=>1\n" +
					 "s0-INT->:s1=>1\n" +
					 "s0-ID->:s1=>1\n" +
					 "s2-'{'->s3\n" +
					 "s3-'}'->:s4=>2\n", found);
		input =
			"if ( ))) ) { } ;";
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("if()))){};\n" +
					 "Decision 0:\n" +
					 "s0-')'->s1\n" +
					 "s1-'{'->s3\n" +
					 "s1-')'->:s2=>1\n" +
					 "s3-'}'->:s4=>2\n", found);
		input =
			"if (() { } a 2) { } ;";  // The first { } should match block so should stop
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("", found); // should not finish to print output
	}

	@Test public void testNongreedyLoopPassingThroughAnotherNongreedy() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : ifstat ';' EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
			"ifstat : 'if' '(' .* ')' block ;\n" +
			"block : '{' (block|.)* '}' ;\n"+
			"EQ : '=' ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String input =
			"if ( x=34 ) { {return a} b 34 } ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("if(x=34){{returna}b34};\n" +
					 "Decision 0:\n" +
					 "s0-')'->s2\n" +
					 "s0-'='->:s1=>1\n" +
					 "s0-INT->:s1=>1\n" +
					 "s0-ID->:s1=>1\n" +
					 "s2-'{'->s3\n" +
					 "s3-'{'->s4\n" +
					 "s4-'}'->:s5=>2\n" +
					 "s4-ID->s4\n" +
					 "\n" +
					 "Decision 1:\n" +
					 "s0-'{'->:s1=>1\n" +
					 "s0-INT->:s2=>2\n" +
					 "s0-ID->:s2=>2\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-'{'->:s1=>1\n" +
					 "s0-'}'->:s3=>2\n" +
					 "s0-INT->:s2=>1\n" +
					 "s0-ID->:s2=>1\n", found);

		input =
			"if ( ()) ) { {return a} b 34 } ;";
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("if(())){{returna}b34};\n" +
					 "Decision 0:\n" +
					 "s0-')'->s2\n" +
					 "s0-'('->:s1=>1\n" +
					 "s2-'{'->s4\n" +
					 "s2-')'->:s3=>1\n" +
					 "s4-'{'->s5\n" +
					 "s5-'}'->:s6=>2\n" +
					 "s5-ID->s5\n" +
					 "\n" +
					 "Decision 1:\n" +
					 "s0-'{'->:s1=>1\n" +
					 "s0-INT->:s2=>2\n" +
					 "s0-ID->:s2=>2\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-'{'->:s1=>1\n" +
					 "s0-'}'->:s3=>2\n" +
					 "s0-INT->:s2=>1\n" +
					 "s0-ID->:s2=>1\n", found);
	}

	@Test public void testStatLoopNongreedyNotNecessary() throws Exception {
		// EOF on end means LL(*) can identify when to stop the loop.
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : stat* ID '=' ID ';' EOF {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
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
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("x=1;a=b;\n" +
					 "Decision 0:\n" +
					 "s0-ID->s1\n" +
					 "s1-'='->s2\n" +
					 "s2-INT->:s3=>1\n" +
					 "s2-ID->s4\n" +
					 "s4-';'->s5\n" +
					 "s5-EOF->:s6=>2\n", found);
		input =
			"if ( 1 ) { x=3; { return 4; } } return 99; abc=def;";
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   input, true);
		assertEquals("if(1){x=3;{return4;}}return99;abc=def;\n" +
					 "Decision 0:\n" +
					 "s0-'if'->:s1=>1\n" +
					 "s0-'return'->:s2=>1\n" +
					 "s0-ID->s3\n" +
					 "s3-'='->s4\n" +
					 "s4-ID->s5\n" +
					 "s5-';'->s6\n" +
					 "s6-EOF->:s7=>2\n", found);
		input =
		"x=1; a=3;"; // FAILS to match since it can't match last element
		execParser("T.g4", grammar, "TParser", "TLexer", "s",
				   input, true);
		// can't match EOF to ID '=' '3' ';'
		assertEquals("line 1:9 no viable alternative at input '<EOF>'\n",
					 this.stderrDuringParse);

		input =
		"x=1; a=b; z=3;"; // FAILS to match since it can't match last element
		execParser("T.g4", grammar, "TParser", "TLexer", "s",
				   input, true);
		assertEquals("line 1:14 no viable alternative at input '<EOF>'\n",
					 this.stderrDuringParse);
		// should not finish to print output
	}

	@Test public void testStatLoopNongreedyNecessary() throws Exception {
		// stops scanning ahead at end of rule s since decision is nongreedy.
		// this says: "match statements until we see a=b; assignment; ignore any
		// statements that follow."
		String grammar =
			"grammar T;\n" +
			"random : s ;" + // call s so s isn't followed by EOF directly
			"s @after {dumpDFA();} : (options {greedy=false;} : stat)* ID '=' ID ';'\n" +
			"    {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
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
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("x=1;a=b;\n" +
					 "Decision 0:\n" +
					 "s0-ID->s1\n" +
					 "s1-'='->s2\n" +
					 "s2-INT->:s3=>1\n" +
					 "s2-ID->s4\n" +
					 "s4-';'->:s5=>2\n", found); // ignores x=1 that follows first a=b assignment
		input =
			"if ( 1 ) { x=3; { return 4; } } return 99; abc=def;";
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("if(1){x=3;{return4;}}return99;abc=def;\n" +
					 "Decision 0:\n" +
					 "s0-'if'->:s1=>1\n" +
					 "s0-'return'->:s2=>1\n" +
					 "s0-ID->s3\n" +
					 "s3-'='->s4\n" +
					 "s4-ID->s5\n" +
					 "s5-';'->:s6=>2\n", found);
		input =
			"x=1; a=3;"; // FAILS to match since it can't match either stat
		execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		// can't match EOF to ID '=' '0' ';'
		assertEquals("line 1:9 no viable alternative at input '<EOF>'\n",
					 this.stderrDuringParse);
		input =
			"x=1; a=b; z=3;"; // stops at a=b; ignores z=3;
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  input, true);
		assertEquals("x=1;a=b;\n" +
					 "Decision 0:\n" +
					 "s0-ID->s1\n" +
					 "s1-'='->s2\n" +
					 "s2-INT->:s3=>1\n" +
					 "s2-ID->s4\n" +
					 "s4-';'->:s5=>2\n", found); // should not finish all input
	}

	@Test public void testHTMLTags() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : (item)+ {System.out.println(_input.getText(Interval.of(0,_input.index()-1)));} ;\n" +
			"item : tag | . ;\n" +
			"tag : '<' '/'? .* '>'  ;\n" +
			"EQ : '=' ;\n" +
			"COMMA : ',' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"STR : '\"' .* '\"' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = null;
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "<a>foo</a>", true);
		assertEquals("<a>foo</a>\n" +
					 "Decision 1:\n" +
					 "s0-'<'->s1\n" +
					 "s0-ID->:s5=>2\n" +
					 "s1-'/'->s2\n" +
					 "s1-ID->s2\n" +
					 "s2-'>'->s3\n" +
					 "s2-ID->s2\n" +
					 "s3-EOF->s6^\n" +
					 "s3-'<'->s4^\n" +
					 "s3-ID->s3\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-'/'->:s2=>1\n" +
					 "s0-ID->:s1=>2\n" +
					 "\n" +
					 "Decision 3:\n" +
					 "s0-'>'->:s2=>2\n" +
					 "s0-ID->:s1=>1\n", found);
		assertEquals("line 1:6 reportAttemptingFullContext d=1, input='<a>foo<'\n" +
					 "line 1:6 reportAmbiguity d=1: ambigAlts={1..2}, input='<a>foo<'\n" +
					 "line 1:10 reportAttemptingFullContext d=1, input='</a>'\n" +
					 "line 1:10 reportAmbiguity d=1: ambigAlts={1..2}, input='</a>'\n" +
					 "line 1:7 reportAmbiguity d=2: ambigAlts={1..2}, input='/'\n",
					 this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "<a></a>", true);
		assertEquals("<a></a>\n" +
					 "Decision 1:\n" +
					 "s0-'<'->s1\n" +
					 "s1-'/'->s2\n" +
					 "s1-ID->s2\n" +
					 "s2-'>'->s3\n" +
					 "s2-ID->s2\n" +
					 "s3-EOF->s5^\n" +
					 "s3-'<'->s4^\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-'/'->:s2=>1\n" +
					 "s0-ID->:s1=>2\n" +
					 "\n" +
					 "Decision 3:\n" +
					 "s0-'>'->:s2=>2\n" +
					 "s0-ID->:s1=>1\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "</b><a src=\"abc\", width=32>", true);
		assertEquals("</b><asrc=\"abc\",width=32>\n" +
					 "Decision 1:\n" +
					 "s0-'<'->s1\n" +
					 "s1-'/'->s2\n" +
					 "s1-ID->s2\n" +
					 "s2-'>'->s3\n" +
					 "s2-'='->s2\n" +
					 "s2-','->s2\n" +
					 "s2-ID->s2\n" +
					 "s2-STR->s2\n" +
					 "s2-INT->s2\n" +
					 "s3-EOF->s5^\n" +
					 "s3-'<'->s4^\n" +
					 "\n" +
					 "Decision 2:\n" +
					 "s0-'/'->:s1=>1\n" +
					 "s0-ID->:s2=>2\n" +
					 "\n" +
					 "Decision 3:\n" +
					 "s0-'>'->:s2=>2\n" +
					 "s0-'='->:s1=>1\n" +
					 "s0-','->:s1=>1\n" +
					 "s0-ID->:s1=>1\n" +
					 "s0-STR->:s1=>1\n" +
					 "s0-INT->:s1=>1\n", found);
	}

	/** lookahead prediction with '.' can be misleading since nongreedy. Lookahead
	 *  that sees into a non-greedy loop, thinks it is greedy.
	 */
	@Test
	public void testFindHTMLTags() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {dumpDFA();} : ( .* (tag {System.out.println($tag.text);} |header) )* EOF;\n" +
			"tag : '<' .+ '>'  ;\n" +
			"header : 'x' 'y' ;\n" +
			"EQ : '=' ;\n" +
			"COMMA : ',' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"STR : '\"' .* '\"' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = null;
		System.out.println(grammar);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
						   ",=foo <a x= 3>32skidoo<a><img>", true);
		assertEquals("<ax=3>\n" +
					 "<a>\n" +
					 "<img>\n" +
					 "Decision 0:\n" +		// .*
					 "s0-'<'->s2\n" +
					 "s0-'='->:s1=>1\n" +
					 "s0-','->:s1=>1\n" +
					 "s0-ID->:s1=>1\n" +
					 "s0-INT->:s1=>1\n" +
					 "s2-ID->s3\n" +
					 "s3-'x'->s4\n" +
					 "s3-'>'->:s5=>2\n" +
					 "s3-INT->s3\n" +
					 "s4-'='->s3\n" +
					 "\n" +
					 "Decision 3:\n" +		// .+
					 "s0-'x'->:s1=>1\n" +
					 "s0-'>'->:s2=>2\n" +
					 "s0-'='->:s1=>1\n" +
					 "s0-ID->:s1=>1\n" +
					 "s0-INT->:s1=>1\n", found);
		assertEquals(null,
					 this.stderrDuringParse);

		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x x<a>", true);
		assertEquals("<a>\n" +
					 "Decision 0:\n" +
					 "s0-'x'->s1\n" +
					 "s0-'<'->s4\n" +
					 "s1-'x'->:s2=>1\n" +
					 "s1-'<'->:s3=>1\n" +
					 "s4-ID->s5\n" +
					 "s5-'>'->:s6=>2\n" +
					 "\n" +
					 "Decision 3:\n" +
					 "s0-'>'->:s2=>2\n" +
					 "s0-ID->:s1=>1\n", found);
		// gets line 1:3 no viable alternative at input '>'. Why??
		// oH! it sees .+ and figures it matches > so <> predicts tag CORRECT!
		// Seeing '.' in a lookahead prediction can be misleading!!
		found = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								  "x <><a>", true);
		assertEquals("<\n" +
					 "<a>\n" +
					 "Decision 0:\n" +
					 "s0-'x'->s1\n" +
					 "s0-'>'->:s6=>1\n" +
					 "s0-'<'->s3\n" +
					 "s1-'<'->:s2=>1\n" +
					 "s3-'>'->s4\n" +
					 "s3-ID->s4\n" +
					 "s4-'>'->:s7=>2\n" +
					 "s4-'<'->:s5=>2\n" +
					 "\n" +
					 "Decision 3:\n" +
					 "s0-'>'->:s1=>2\n" +
					 "s0-ID->:s2=>1\n", // doesn't match tag; null
					 found);
		assertEquals("line 1:3 no viable alternative at input '>'\n",
					 this.stderrDuringParse);
	}
}
