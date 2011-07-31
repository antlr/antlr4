package org.antlr.v4.test;

import org.junit.Test;

public class TestParserExec extends BaseTest {

	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID INT {System.out.println(input);} ;\n" +
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
			"a : ID+ {System.out.println(input);} ;\n" +
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
			"a : (ID|ID)+ {System.out.println(input);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";

		String found = execParser("T.g", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID* {System.out.println(input);} ;\n" +
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
			"a : (ID|ID)* {System.out.println(input);} ;\n" +
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
			"a : (ID|INT{;})+ {System.out.println(input);} ;\n" +
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
			"a : (ID|INT{;})* {System.out.println(input);} ;\n" +
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

}
