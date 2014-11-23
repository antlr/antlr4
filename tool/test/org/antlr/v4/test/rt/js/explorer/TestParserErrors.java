package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParserErrors extends BaseTest {

	@Test
	public void testTokenMismatch() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aa", false);
		assertEquals("", found);
		assertEquals("line 1:1 mismatched input 'a' expecting 'b'\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletion() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aab", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting 'b'\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletionExpectingSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aab", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenInsertion() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b' 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("", found);
		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);
	}

	@Test
	public void testConjuringUpToken() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' x='b' {document.getElementById('output').value += \"conjured=\" + $x + '\\n';} 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);
		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleSetInsertion() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'c') 'd' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ad", false);
		assertEquals("", found);
		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);
	}

	@Test
	public void testConjuringUpTokenFromSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' x=('b'|'c') {document.getElementById('output').value += \"conjured=\" + $x + '\\n';} 'd' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ad", false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);
		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);
	}

	@Test
	public void testLL2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'\r\n" +
	                  "  | 'a' 'c'\r\n" +
	                  ";\r\n" +
	                  "q : 'e' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ae", false);
		assertEquals("", found);
		assertEquals("line 1:1 no viable alternative at input 'ae'\n", this.stderrDuringParse);
	}

	@Test
	public void testLL3() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'* 'c'\r\n" +
	                  "  | 'a' 'b' 'd'\r\n" +
	                  ";\r\n" +
	                  "q : 'e' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abe", false);
		assertEquals("", found);
		assertEquals("line 1:2 no viable alternative at input 'abe'\n", this.stderrDuringParse);
	}

	@Test
	public void testLLStar() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a'+ 'b'\r\n" +
	                  "  | 'a'+ 'c'\r\n" +
	                  ";\r\n" +
	                  "q : 'e' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aaae", false);
		assertEquals("", found);
		assertEquals("line 1:3 no viable alternative at input 'aaae'\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletionBeforeLoop() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'* ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}\nline 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);
	}

	@Test
	public void testMultiTokenDeletionBeforeLoop() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aacabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletionDuringLoop() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ababbc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testMultiTokenDeletionDuringLoop() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaaababc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'c'}\nline 1:6 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletionBeforeLoop2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'z'{})*;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}\nline 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);
	}

	@Test
	public void testMultiTokenDeletionBeforeLoop2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'z'{})* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aacabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSingleTokenDeletionDuringLoop2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'z'{})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ababbc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testMultiTokenDeletionDuringLoop2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : 'a' ('b'|'z'{})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaaababc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\nline 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	@Test
	public void testLL1ErrorInfo() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : animal (AND acClass)? service EOF;\r\n" +
	                  "animal : (DOG | CAT );\r\n" +
	                  "service : (HARDWARE | SOFTWARE) ;\r\n" +
	                  "AND : 'and';\r\n" +
	                  "DOG : 'dog';\r\n" +
	                  "CAT : 'cat';\r\n" +
	                  "HARDWARE: 'hardware';\r\n" +
	                  "SOFTWARE: 'software';\r\n" +
	                  "WS : ' ' -> skip ;\r\n" +
	                  "acClass\r\n" +
	                  "@init\r\n" +
	                  "{document.getElementById('output').value += this.getExpectedTokens().toString(this.literalNames) + '\\n';}\r\n" +
	                  "  : ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "dog and software", false);
		assertEquals("{'hardware', 'software'}\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testInvalidEmptyInput() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : ID+;\r\n" +
	                  "ID : [a-z]+;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "", false);
		assertEquals("", found);
		assertEquals("line 1:0 missing ID at '<EOF>'\n", this.stderrDuringParse);
	}

	@Test
	public void testContextListGetters() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@parser::members{\r\n" +
	                  "	function foo() {\r\n" +
	                  "		var s = new SContext();\r\n" +
	                  "	    var a = s.a();\r\n" +
	                  "	    var b = s.b();\r\n" +
	                  "    };\r\n" +
	                  "}\r\n" +
	                  "s : (a | b)+;\r\n" +
	                  "a : 'a' {document.getElementById('output').value += 'a';};\r\n" +
	                  "b : 'b' {document.getElementById('output').value += 'b';};\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "abab", false);
		assertEquals("abab\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testDuplicatedLeftRecursiveCall(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : expr EOF;\r\n" +
	                  "expr : 'x'\r\n" +
	                  "     | expr expr\r\n" +
	                  "     ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", input, false);
	}

	@Test
	public void testDuplicatedLeftRecursiveCall_1() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDuplicatedLeftRecursiveCall_2() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xxx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDuplicatedLeftRecursiveCall_3() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xxxx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testInvalidATNStateRemoval() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : ID ':' expr;\r\n" +
	                  "expr : primary expr? {} | expr '->' ID;\r\n" +
	                  "primary : ID;\r\n" +
	                  "ID : [a-z]+;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "x:x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNoViableAltAvoidance() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : e '!' ;\r\n" +
	                  "e : 'a' 'b'\r\n" +
	                  "  | 'a'\r\n" +
	                  "  ;\r\n" +
	                  "DOT : '.' ;\r\n" +
	                  "WS : [ \\t\\r\\n]+ -> skip;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a.", false);
		assertEquals("", found);
		assertEquals("line 1:1 mismatched input '.' expecting '!'\n", this.stderrDuringParse);
	}


}