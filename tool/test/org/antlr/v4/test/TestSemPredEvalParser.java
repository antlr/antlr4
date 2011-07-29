package org.antlr.v4.test;

import org.junit.Test;

public class TestSemPredEvalParser extends BaseTest {
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

	@Test public void testToRight() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : a+ ;\n" +
			"a : ID {false}? {System.out.println(\"alt 1\");}\n" +
			"  | ID {true}?  {System.out.println(\"alt 2\");}\n" +
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

	@Test public void testToLeftWithVaryingPredicate() throws Exception {
		// alternate predicted alt to ensure DFA doesn't cache
		// must use forced action since i++ must exec; FOLLOW(a) sees
		// both preds since it loops around in s.
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

	/**
	 * The ATN simulator ignores the unforced action but sees the forced
	 * actions at the start of both alternatives. Further, during closure,
	 * it sees the 2nd forced action s1b in the 1st alternative. Once parsing
	 * begins, it executes the "parse" action as well as the forced actions
	 * in the 1st alternative.
	 */
	@Test public void testForcedAction() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : {f(\"parse\");} {{f(\"s1a\");}} ID {{f(\"s1b\");}} | {{f(\"s2\");}} ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x x y", false);
		String expecting =
			"s1a\n" +
			"s2\n" +
			"s1b\n" +
			"parse\n" +
			"s1a\n" +
			"s1b\n";
		assertEquals(expecting, found);
	}

	/** To distinguish the alternatives of rule e, we compute FOLLOW(e),
	 *  which includes all tokens that can be matched following all
	 *  references to e. In this case, it sees two forced actions after
	 *  references to e that it must execute.
 	 */
	@Test public void testForcedActionInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : e {{f(\"s1\");}} {f(\"alt 1\");} '!' ;\n" +
			"t : e {{f(\"t1\");}} ID ;\n" +
			"e : ID | ;\n" + // non-LL(1) so we use ATN
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "a!", false);
		String expecting =
			"s1\n" +	// do s1, t1 once during s0 computation from epsilon edge in e
			"t1\n" +
			"s1\n" +	// do them again during closure after passing ID in e
			"t1\n" +
			"s1\n" +	// now we are parsing
			"alt 1\n";
		assertEquals(expecting, found);
	}

	/**
	 * Actions that depend on local scope information such as local
	 * variables and arguments are executed if we are sure we have the
	 * right RuleContext object. Have the correct context for any rule
	 * that we invoke during ATN simulation (unless we fall off the edge
	 * of the initial rule into the outer context. see other unit
	 * tests). In this case, the forced actions are accessing the
	 * argument of the surrounding rule so everything is okay.
	 */
	@Test public void testForcedDepedentActionInContext() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : a[99] ;\n" +
			"a[int i] : {f(\"parse\");} {{f($i);}} ID {{f(\"s1b\");}} | {{f($i+1);}} ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x x y", false);
		String expecting =
			"99\n" +
			"100\n" +
			"s1b\n" +
			"parse\n" +
			"99\n" +
			"s1b\n";
		assertEquals(expecting, found);
	}

	/** In this case, we also know what the context is for $i. During
	 *  ATN simulation, rule a invokes rule b which creates the correct context.
 	 */
	@Test public void testForcedDepedentActionInContext2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : a ;\n" +
			"a : {f(\"parse\");} b {{f(\"s1b\");}} | b ;\n" +
			"b returns [int i=32] : {{f($i);}} ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x x y", false);
		String expecting =
			"32\n" +
			"32\n" +
			"s1b\n" +
			"parse\n" +
			"32\n" +
			"s1b\n";
		assertEquals(expecting, found);
	}

	/** We must execute all arguments for rules that we invoked during ATN
	 * 	simulation. In this case, the evaluation of the arguments is clearly
	 * 	okay because the arguments are not dependent on local context.
 	 */
	@Test public void testArgEval() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : {f(\"parse\");} b[1] {{f(\"s1b\");}} | b[2] ;\n" +
			"b[int i] : {{f($i);}} ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x x y", false);
		String expecting =
			"1\n" +
			"2\n" +
			"s1b\n" +
			"parse\n" +
			"1\n" +
			"s1b\n";
		assertEquals(expecting, found);
	}

	/** The arguments to rule b access $i, which makes it dependent upon
	 *  the local context. It's okay, because we are in the proper spot.
	 */
	@Test public void testDepedentArgEval() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"void f(Object s) {System.out.println(s);}\n" +
			"}\n" +
			"s : a[1] ;\n" +
			"a[int i] : {f(\"parse\");} b[$i] {{f(\"s1b\");}} | b[$i+1] ;\n" +
			"b[int i] : {{f($i);}} ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x x y", false);
		String expecting =
			"1\n" +
			"2\n" +
			"s1b\n" +
			"parse\n" +
			"1\n" +
			"s1b\n";
		assertEquals(expecting, found);
	}

	/** The call chain is s a e but since e is optional, we invoke the
	 *  global follow of e. This runs into the foo rule invocations, both of
	 *  which have parameters that are dependent upon the local context.
	 *  Because we can never be sure if we are in the proper context (without
	 *  using full LL(*) context parsing), we can't ever execute dependent
	 *  actions encountered during global follows. We create an uninitialized
	 *  foo_ctx object for use while chasing edges in foo.
	 */
	@Test public void testDepedentArgEvalInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"int f(int s) {System.out.println(s); return s;}\n" +
			"}\n" +
			"s : a[1] ;\n" +
			"a[int i] : e foo[f($i)] ;\n" +
			"b[int i] : e foo[f($i+1)] ;\n" +
			"e        : ID | ;\n" +
			"foo[int k] : ID ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x y;", false);
		String expecting =
			"1\n"; // f($i) executed only during real parse during call to foo.
		assertEquals(expecting, found);
	}

	/** Same as the previous test except that we have dependent forced
	 *  actions in the global fall not dependent rule arguments.
	 */
	@Test public void testForcedDepedentActionInGlobalFOLLOW() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {" +
			"int f(int s) {System.out.println(s); return s;}\n" +
			"}\n" +
			"s : a[1] ;\n" +
			"a[int i] : e {{f($i);}} ID '!' ;\n" +
			"b[int i] : e {{f($i+1);}} ID '?' ;\n" +
			"e        : ID | ;\n" +
			"foo[int k] : ID ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "s",
								  "x y !", false);
		String expecting =
			"1\n";
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
