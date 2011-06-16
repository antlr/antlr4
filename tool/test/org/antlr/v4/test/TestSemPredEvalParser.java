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

}
