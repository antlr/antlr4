package org.antlr.v4.test;

import org.junit.Test;

public class TestParseTrees extends BaseTest {
	@Test public void testTokenMismatch() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b' ;";
//		String found = execParser("T.g", grammar, "TParser", "TLexer", "a", "aa", false);
//		String expecting = "line 1:1 mismatched input 'a' expecting 'b'\n";
//		String result = stderrDuringParse;
//		assertEquals(expecting, result);
	}
}
