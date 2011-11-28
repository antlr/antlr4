package org.antlr.v4.test;

import org.junit.Test;

public class TestSemPredEvalParser extends BaseTest {
    @Test public void testSimple() throws Exception {
   		String grammar =
   			"grammar T;\n" +
   			"a : {false}? ID {System.out.println(\"alt 1\");}\n" +
            "  | {true}?  ID {System.out.println(\"alt 2\");}\n" +
            "  | INT         {System.out.println(\"alt 3\");}\n" +
   			"  ;\n" +
   			"ID : 'a'..'z'+ ;\n" +
   			"INT : '0'..'9'+;\n" +
   			"WS : (' '|'\\n') {skip();} ;\n";

   		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
   								  "x", false);
   		String expecting =
   			"alt 2\n" +
   			"alt 2\n" +
   			"alt 2\n";
   		assertEquals(expecting, found);
   	}

    @Test public void testToLeft() throws Exception {
   		String grammar =
   			"grammar T;\n" +
   			"s : a+ ;\n" +
   			"a : {false}? ID {System.out.println(\"alt 1\");}\n" +
   			"  | {true}?  ID {System.out.println(\"alt 2\");}\n" +
   			"  ;\n" +
   			"ID : 'a'..'z'+ ;\n" +
   			"INT : '0'..'9'+;\n" +
   			"WS : (' '|'\\n') {skip();} ;\n";

   		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
   								  "x x y", false);
   		String expecting =
   			"alt 2\n" +
   			"alt 2\n" +
   			"alt 2\n";
   		assertEquals(expecting, found);
   	}

	@Test public void testActionHidesPreds() throws Exception {
		// can't see preds, resolves to first alt found (1 in this case)
		String grammar =
			"grammar T;\n" +
			"@members {int i;}\n" +
			"s : a+ ;\n" +
			"a : {i=1;} ID {i==1}? {System.out.println(\"alt 1\");}\n" +
			"  | {i=2;} ID {i==2}? {System.out.println(\"alt 2\");}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
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
			"@members {int i=0;}\n" +
			"s : ({i++; System.out.println(\"i=\"+i);} a)+ ;\n" +
			"a : {i \\% 2 == 0}? ID {System.out.println(\"alt 1\");}\n" +
			"  | {i \\% 2 != 0}? ID {System.out.println(\"alt 2\");}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
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
			"@members {int i=0;}\n" +
			"s : a[2] a[1];\n" +
			"a[int i]" +
			"  : {$i==1}? ID {System.out.println(\"alt 1\");}\n" +
			"  | {$i==2}? ID {System.out.println(\"alt 2\");}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
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
			"@members {int i=0;}\n" +
			"s : a[2] a[1];\n" +
			"a[int i]" +
			"  : {$i==1}? ID\n" +
			"  | {$i==2}? ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "a b", false);
		String expecting =
			"";
		assertEquals(expecting, found);
	}

	@Test public void testToRightWithVaryingPredicate() throws Exception {
		// alternate predicted alt to ensure DFA doesn't cache
		String grammar =
			"grammar T;\n" +
			"@members {int i=0;}\n" +
			"s : ({i++; System.out.println(\"i=\"+i);} a)+ ;\n" +
			"a : ID {i \\% 2 == 0}? {System.out.println(\"alt 1\");}\n" +
			"  | ID {i \\% 2 != 0}? {System.out.println(\"alt 2\");}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
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

	/** During a global follow operation, we still execute semantic
	 *  predicates as long as they are not dependent on local context
	 */
	@Test public void testPredsInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"boolean p(boolean v) {System.out.println(\"eval=\"+v); return v;}\n" +
			"}\n" +
			"s : e {p(true)}? {f(\"parse\");} '!' ;\n" +
			"t : e {p(false)}? ID ;\n" +
			"e : ID | ;\n" + // non-LL(1) so we use ATN
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "a!", false);
		String expecting =
			"eval=true\n" +	// do p(true), p(false) once during s0 computation from epsilon edge in e
			"eval=false\n" +
			"eval=true\n" +	// do them again during closure after passing ID in e
			"eval=false\n" +
			"eval=true\n" + // now we are parsing
			"parse\n";
		assertEquals(expecting, found);
	}

	/** We cannot execute predicates that are dependent on local context if
	 *  we are doing a global follow. They appear as if they were true
	 *  predicates or not there at all.
	 */
	@Test public void testDepedentPredsInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"boolean p(boolean v) {System.out.println(\"eval=\"+v); return v;}\n" +
			"}\n" +
			"s : a[99] ;\n" +
			"a[int i] : e {p($i==99)}? {f(\"parse\");} '!' ;\n" +
			"b[int i] : e {p($i==99)}? ID ;\n" +
			"e : ID | ;\n" + // non-LL(1) so we use ATN
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "a!", false);
		String expecting =
			"eval=true\n" +
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
			"void f(Object s) {System.out.println(s);}\n" +
			"boolean p(boolean v) {System.out.println(\"eval=\"+v); return v;}\n" +
			"}\n" +
			"s : e {} {p(true)}? {f(\"parse\");} '!' ;\n" +
			"t : e {} {p(false)}? ID ;\n" +
			"e : ID | ;\n" + // non-LL(1) so we use ATN
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "a!", false);
		String expecting =
			"eval=true\n" +
			"parse\n";
		assertEquals(expecting, found);
	}

	/** if you call a rule as part of FOLLOW with $i, can't execute, but
	 *  what if there is a forced action in that called rule?  We should
	 *  NOT execute any actions after
	 *
	 *  a[int i] : e x[$i] ;
	 *  b[int i] : e x[$i] ;
	 *  e : ID | ;
	 *  x[int i] : {{$i=3;}} ID ;
	 *
	 *  use global context?
	 */

}
