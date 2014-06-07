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

package org.antlr.v4.py3.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestSemPredEvalParser extends BasePython3Test {
	// TEST VALIDATING PREDS

	@Test public void testSimpleValidate() throws Exception {
		String grammar =
			"grammar T;\n" +
				"s : a ;\n" +
				"a : {False}? ID  {print(\"alt 1\")}\n" +
				"  | {True}?  INT {print(\"alt 2\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		/*String found = */execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x", false);

		String expecting = "line 1:0 no viable alternative at input 'x'\n";
		assertEquals(expecting, stderrDuringParse);
	}

	@Test public void testSimpleValidate2() throws Exception {
		String grammar =
			"grammar T;\n" +
				"s : a a a;\n" +
				"a : {False}? ID  {print(\"alt 1\")}\n" +
				"  | {True}?  INT {print(\"alt 2\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "3 4 x", false);
		String expecting =
			"alt 2\n" +
			"alt 2\n";
		assertEquals(expecting, found);

		expecting = "line 1:4 no viable alternative at input 'x'\n";
		assertEquals(expecting, stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#196
	 * "element+ in expression grammar doesn't parse properly"
	 * https://github.com/antlr/antlr4/issues/196
	 */
	@Test public void testAtomWithClosureInTranslatedLRRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : e[0] EOF;\n" +
			"e[int _p]\n" +
			"@init{$_p = 0}\n" +
			"    :   ( 'a'\n" +
			"        | 'b'+\n" +
			"        )\n" +
			"        ( {3 >= $_p}? '+' e[4]\n" +
			"        )*\n" +
			"    ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start",
								  "a+b+a", false);
		String expecting = "";
		assertEquals(expecting, found);
		assertNull(stderrDuringParse);
	}

	@Test public void testValidateInDFA() throws Exception {
		String grammar =
			"grammar T;\n" +
				"s : a ';' a;\n" +
				// ';' helps us to resynchronize without consuming
				// 2nd 'a' reference. We our testing that the DFA also
				// throws an exception if the validating predicate fails
				"a : {False}? ID  {print(\"alt 1\")}\n" +
				"  | {True}?  INT {print(\"alt 2\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x ; y", false);
		String expecting = "";
		assertEquals(expecting, found);

		expecting =
			"line 1:0 no viable alternative at input 'x'\n" +
			"line 1:4 no viable alternative at input 'y'\n";
		assertEquals(expecting, stderrDuringParse);
	}

	// TEST DISAMBIG PREDS

	@Test public void testSimple() throws Exception {
		String grammar =
			"grammar T;\n" +
				"s : a a a;\n" + // do 3x: once in ATN, next in DFA then INT in ATN
				"a : {False}? ID {print(\"alt 1\")}\n" +
				"  | {True}?  ID {print(\"alt 2\")}\n" +
				"  | INT         {print(\"alt 3\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x y 3", false);
		String expecting =
			"alt 2\n" +
				"alt 2\n" +
				"alt 3\n";
		assertEquals(expecting, found);
	}

	@Test public void testOrder() throws Exception {
		// Under new predicate ordering rules (see antlr/antlr4#29), the first
		// alt with an acceptable config (unpredicated, or predicated and evaluates
		// to true) is chosen.
		String grammar =
			"grammar T;\n" +
				"s : a {} a;\n" + // do 2x: once in ATN, next in DFA;
								  // action blocks lookahead from falling off of 'a'
								  // and looking into 2nd 'a' ref. !ctx dependent pred
				"a :          ID {print(\"alt 1\")}\n" +
				"  | {True}?  ID {print(\"alt 2\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x y", false);
		String expecting =
			"alt 1\n" +
			"alt 1\n";
		assertEquals(expecting, found);
	}

	@Test public void test2UnpredicatedAlts() throws Exception {
		// We have n-2 predicates for n alternatives. pick first alt
		String grammar =
			"grammar T;\n" +
			"s : {self._interp.predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION}\n" +
			"    a ';' a;\n" + // do 2x: once in ATN, next in DFA
			"a :          ID {print(\"alt 1\")}\n" +
			"  |          ID {print(\"alt 2\")}\n" +
			"  | {False}? ID {print(\"alt 3\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x; y", true);
		String expecting =
			"alt 1\n" +
			"alt 1\n";
		assertEquals(expecting, found);
		assertEquals("line 1:0 reportAttemptingFullContext d=0 (a), input='x'\n" +
					 "line 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\n" +
					 "line 1:3 reportAttemptingFullContext d=0 (a), input='y'\n" +
					 "line 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n",
                     this.stderrDuringParse);
	}

	@Test public void test2UnpredicatedAltsAndOneOrthogonalAlt() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : {self._interp.predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION}\n" +
			"    a ';' a ';' a;\n" +
			"a : INT         {print(\"alt 1\")}\n" +
			"  |          ID {print(\"alt 2\")}\n" + // must pick this one for ID since pred is false
			"  |          ID {print(\"alt 3\")}\n" +
			"  | {False}? ID {print(\"alt 4\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "34; x; y", true);
		String expecting =
			"alt 1\n" +
			"alt 2\n" +
			"alt 2\n";
		assertEquals(expecting, found);
		assertEquals("line 1:4 reportAttemptingFullContext d=0 (a), input='x'\n" +
					 "line 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\n" +
					 "line 1:7 reportAttemptingFullContext d=0 (a), input='y'\n" +
					 "line 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n",
					 this.stderrDuringParse);
	}

	@Test public void testRewindBeforePredEval() throws Exception {
		// The parser consumes ID and moves to the 2nd token INT.
		// To properly evaluate the predicates after matching ID INT,
		// we must correctly see come back to starting index so LT(1) works
		String grammar =
			"grammar T;\n" +
				"s : a a;\n" +
				"a : {self._input.LT(1).text==\"x\"}? ID INT {print(\"alt 1\")}\n" +
				"  | {self._input.LT(1).text==\"y\"}? ID INT {print(\"alt 2\")}\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "y 3 x 4", false);
		String expecting =
			"alt 2\n" +
			"alt 1\n";
		assertEquals(expecting, found);
	}

	@Test public void testNoTruePredsThrowsNoViableAlt() throws Exception {
		// checks that we throw exception if all alts
		// are covered with a predicate and none succeeds
		String grammar =
			"grammar T;\n" +
			"s : a a;\n" +
			"a : {False}? ID INT {print(\"alt 1\")}\n" +
			"  | {False}? ID INT {print(\"alt 2\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
				   "y 3 x 4", false);
		String expecting = "line 1:0 no viable alternative at input 'y'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testToLeft() throws Exception {
		String grammar =
			"grammar T;\n" +
				"s : a+ ;\n" +
   			"a : {False}? ID {print(\"alt 1\")}\n" +
   			"  | {True}?  ID {print(\"alt 2\")}\n" +
   			"  ;\n" +
   			"ID : 'a'..'z'+ ;\n" +
   			"INT : '0'..'9'+;\n" +
   			"WS : (' '|'\\n') -> skip ;\n";

   		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
   								  "x x y", false);
   		String expecting =
   			"alt 2\n" +
   			"alt 2\n" +
   			"alt 2\n";
   		assertEquals(expecting, found);
   	}

	@Test
	public void testUnpredicatedPathsInAlt() throws Exception{
		String grammar =
			"grammar T;\n" +
				"s : a {print(\"alt 1\")}\n" +
				"  | b {print(\"alt 2\")}\n" +
				"  ;\n" +
				"a : {False}? ID INT\n" +
				"  | ID INT\n" +
				"  ;\n" +
				"b : ID ID\n" +
				"  ;\n" +
				"ID : 'a'..'z'+ ;\n" +
				"INT : '0'..'9'+;\n" +
				"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x 4", false);
		String expecting =
			"alt 1\n";
		assertEquals(expecting, found);

		expecting = null;
		assertEquals(expecting, stderrDuringParse);
	}

	@Test public void testActionHidesPreds() throws Exception {
		// can't see preds, resolves to first alt found (1 in this case)
		String grammar =
			"grammar T;\n" +
			"@members {i=0}\n" +
			"s : a+ ;\n" +
			"a : {self.i=1} ID {self.i==1}? {print(\"alt 1\")}\n" +
			"  | {self.i=2;} ID {self.i==2}? {print(\"alt 2\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x x y", false);
		String expecting =
			"alt 1\n" +
			"alt 1\n" +
			"alt 1\n";
		assertEquals(expecting, found);
	}

	/** In this case, we use predicates that depend on global information
	 *  like we would do for a symbol table. We simply execute
	 *  the predicates assuming that all necessary information is available.
	 *  The i++ action is done outside of the prediction and so it is executed.
	 */
	@Test public void testToLeftWithVaryingPredicate() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {i=0}\n" +
			"s : ({self.i += 1\nprint(\"i=\"+str(self.i))} a)+ ;\n" +
			"a : {self.i % 2 == 0}? ID {print(\"alt 1\")}\n" +
			"  | {self.i % 2 != 0}? ID {print(\"alt 2\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "x x y", false);
		String expecting =
			"i=1\n" +
			"alt 2\n" +
			"i=2\n" +
			"alt 1\n" +
			"i=3\n" +
			"alt 2\n";
		assertEquals(expecting, found);
	}

	/**
	 * In this case, we're passing a parameter into a rule that uses that
	 * information to predict the alternatives. This is the special case
	 * where we know exactly which context we are in. The context stack
	 * is empty and we have not dipped into the outer context to make a decision.
	 */
	@Test public void testPredicateDependentOnArg() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : a[2] a[1];\n" +
			"a[int i]" +
			"  : {$i==1}? ID {print(\"alt 1\")}\n" +
			"  | {$i==2}? ID {print(\"alt 2\")}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "a b", false);
		String expecting =
			"alt 2\n" +
			"alt 1\n";
		assertEquals(expecting, found);
	}

	/** In this case, we have to ensure that the predicates are not
	 tested during the closure after recognizing the 1st ID. The
	 closure will fall off the end of 'a' 1st time and reach into the
	 a[1] rule invocation. It should not execute predicates because it
	 does not know what the parameter is. The context stack will not
	 be empty and so they should be ignored. It will not affect
	 recognition, however. We are really making sure the ATN
     simulation doesn't crash with context object issues when it
     encounters preds during FOLLOW.
     */
    @Test public void testPredicateDependentOnArg2() throws Exception {
        String grammar =
            "grammar T;\n" +
            "s : a[2] a[1];\n" +
            "a[int i]" +
            "  : {$i==1}? ID\n" +
            "  | {$i==2}? ID\n" +
            "  ;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') -> skip ;\n";

        String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
                                  "a b", false);
        String expecting =
        "";
        assertEquals(expecting, found);
    }

    @Test public void testDependentPredNotInOuterCtxShouldBeIgnored() throws Exception {
        // uses ID ';' or ID '.' lookahead to solve s. preds not tested.
        String grammar =
            "grammar T;\n" +
            "s : b[2] ';' |  b[2] '.' ;\n" + // decision in s drills down to ctx-dependent pred in a;
            "b[int i] : a[i] ;\n" +
            "a[int i]" +
            "  : {$i==1}? ID {print(\"alt 1\")}\n" +
            "  | {$i==2}? ID {print(\"alt 2\")}\n" +
            "  ;" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') -> skip ;\n";

        String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
                                  "a;", false);
        String expecting =
            "alt 2\n";
        assertEquals(expecting, found);
    }

    @Test public void testIndependentPredNotPassedOuterCtxToAvoidCastException() throws Exception {
        String grammar =
            "grammar T;\n" +
            "s : b ';' |  b '.' ;\n" +
            "b : a ;\n" +
            "a" +
            "  : {False}? ID {print(\"alt 1\")}\n" +
            "  | {True}? ID {print(\"alt 2\")}\n" +
            "  ;" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') -> skip ;\n";

        String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
                                  "a;", false);
        String expecting =
            "alt 2\n";
        assertEquals(expecting, found);
    }

    /** During a global follow operation, we still collect semantic
     *  predicates as long as they are not dependent on local context
     */
    @Test public void testPredsInGlobalFOLLOW() throws Exception {
        String grammar =
        "grammar T;\n" +
        "@members {" +
        "def f(self, s):\n" +
        "    print(str(s))\n" +
        "def p(self, v):\n" +
        "    print(\"eval=\"+str(v))\n" +
        "    return v\n" +
        "}\n" +
        "s : e {self.p(True)}? {self.f(\"parse\")} '!' ;\n" +
        "t : e {self.p(False)}? ID ;\n" +
        "e : ID | ;\n" + // non-LL(1) so we use ATN
        "ID : 'a'..'z'+ ;\n" +
        "INT : '0'..'9'+;\n" +
        "WS : (' '|'\\n') -> skip ;\n";

   		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
   								  "a!", false);
   		String expecting =
   			"eval=True\n" + // now we are parsing
   			"parse\n";
   		assertEquals(expecting, found);
   	}

   	/** We cannot collect predicates that are dependent on local context if
   	 *  we are doing a global follow. They appear as if they were not there at all.
   	 */
   	@Test public void testDepedentPredsInGlobalFOLLOW() throws Exception {
   		String grammar =
   			"grammar T;\n" +
   			"@members {" +
   			"def f(self, s):\n" +
   			"    print(str(s))\n" +
   			"def p(self, v):\n" +
   			"    print(\"eval=\"+str(v))\n" +
   			"    return v\n" +
   			"}\n" +
   			"s : a[99] ;\n" +
   			"a[int i] : e {self.p($i==99)}? {self.f(\"parse\")} '!' ;\n" +
   			"b[int i] : e {self.p($i==99)}? ID ;\n" +
   			"e : ID | ;\n" + // non-LL(1) so we use ATN
   			"ID : 'a'..'z'+ ;\n" +
   			"INT : '0'..'9'+;\n" +
   			"WS : (' '|'\\n') -> skip ;\n";

   		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
   								  "a!", false);
   		String expecting =
   			"eval=True\n" +
   			"parse\n";
   		assertEquals(expecting, found);
   	}

	/** Regular non-forced actions can create side effects used by semantic
	 *  predicates and so we cannot evaluate any semantic predicate
	 *  encountered after having seen a regular action. This includes
	 *  during global follow operations.
	 */
	@Test public void testActionsHidePredsInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"def f(self, s):\n" +
			"    print(str(s))\n\n" +
			"def p(self, v):\n" +
			"    print(\"eval=\"+str(v))\n" +
			"    return v\n\n" +
			"}\n" +
			"s : e {} {self.p(True)}? {self.f(\"parse\")} '!' ;\n" +
			"t : e {} {self.p(False)}? ID ;\n" +
			"e : ID | ;\n" + // non-LL(1) so we use ATN
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s",
								  "a!", false);
		String expecting =
			"eval=True\n" +
			"parse\n";
		assertEquals(expecting, found);
	}

	public String testPredTestedEvenWhenUnAmbig(String param) throws Exception {
		String grammar =
			"grammar T;\n" +
			"\n" +
			"@members {enumKeyword = True}\n" +
			"\n" +
			"primary\n" +
			"    :   ID                       {print(\"ID \"+$ID.text)}\n" +
			"    |   {not self.enumKeyword}? 'enum'   {print(\"enum\")}\n" +
			"    ;\n" +
			"\n" +
			"ID : [a-z]+ ;\n" +
			"\n" +
			"WS : [ \\t\\n\\r]+ -> skip ;\n";

		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "primary",
								  param, false);
	}

	@Test public void testPredTestedEvenWhenUnAmbig1() throws Exception {
		String found = testPredTestedEvenWhenUnAmbig("abc");
		assertEquals("ID abc\n", found);
	}
	
	@Test public void testPredTestedEvenWhenUnAmbig2() throws Exception {
		testPredTestedEvenWhenUnAmbig("enum");
		assertEquals("line 1:0 no viable alternative at input 'enum'\n", stderrDuringParse);
	}
		
	/**
	 * This is a regression test for antlr/antlr4#218 "ANTLR4 EOF Related Bug".
	 * https://github.com/antlr/antlr4/issues/218
	 */
	@Test public void testDisabledAlternative() {
		String grammar =
			"grammar AnnotProcessor;\n" +
			"\n" +
			"cppCompilationUnit : content+ EOF;\n" +
			"\n" +
			"content: anything | {False}? .;\n" +
			"\n" +
			"anything: ANY_CHAR;\n" +
			"\n" +
			"ANY_CHAR: [_a-zA-Z0-9];\n";

		String input = "hello";
		String found = execParser("AnnotProcessor.g4", grammar, "AnnotProcessorParser", "AnnotProcessorLexer", "AnnotProcessorListener", "AnnotProcessorVisitor", "cppCompilationUnit",
								  input, false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/** Loopback doesn't eval predicate at start of alt */
	public String testPredFromAltTestedInLoopBack(String input) {
		String grammar =
			"grammar T2;\n" +
			"\n" +
			"file_\n" +
			"@after {print($ctx.toStringTree(recog=self))}\n" +
			"  : para para EOF ;" +
			"para: paraContent NL NL ;\n"+
			"paraContent : ('s'|'x'|{self._input.LA(2)!=self.NL}? NL)+ ;\n"+
			"NL : '\\n' ;\n"+
			"S : 's' ;\n"+
			"X : 'x' ;\n";

		return execParser("T2.g4", grammar, "T2Parser", "T2Lexer", "T2Listener", "T2Visitor", "file_",
								  input, true);
	}
	
	@Test public void testPredFromAltTestedInLoopBack1() {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n");
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x \\n)) <EOF>)\n", found);
		assertEquals("line 5:2 mismatched input '<EOF>' expecting '\n'\n", stderrDuringParse);
	}
	
	@Test public void testPredFromAltTestedInLoopBack2() {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n\n");
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x) \\n \\n) <EOF>)\n", found);
		assertNull(stderrDuringParse);
	}
}
