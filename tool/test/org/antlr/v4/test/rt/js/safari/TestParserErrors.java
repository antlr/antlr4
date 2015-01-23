package org.antlr.v4.test.rt.js.safari;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParserErrors extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTokenMismatch() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aa", false);
		assertEquals("", found);
		assertEquals("line 1:1 mismatched input 'a' expecting 'b'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletion() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aab", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting 'b'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionExpectingSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aab", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionConsumption() throws Exception {
		String grammar = "grammar T;\n" +
	                  "myset: ('b'|'c') ;\n" +
	                  "a: 'a' myset 'd' {document.getElementById('output').value += \"\" + $myset.stop + '\\n';} ; ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aabd", false);
		assertEquals("[@2,2:2='b',<1>,1:2]\n", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenInsertion() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b' 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("", found);
		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testConjuringUpToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' x='b' {document.getElementById('output').value += \"conjured=\" + $x + '\\n';} 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);
		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleSetInsertion() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'c') 'd' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ad", false);
		assertEquals("", found);
		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleSetInsertionConsumption() throws Exception {
		String grammar = "grammar T;\n" +
	                  "myset: ('b'|'c') ;\n" +
	                  "a: 'a' myset 'd' {document.getElementById('output').value += \"\" + $myset.stop + '\\n';} ; ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ad", false);
		assertEquals("[@0,0:0='a',<3>,1:0]\n", found);
		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testConjuringUpTokenFromSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' x=('b'|'c') {document.getElementById('output').value += \"conjured=\" + $x + '\\n';} 'd' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ad", false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);
		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLL2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'\n" +
	                  "  | 'a' 'c'\n" +
	                  ";\n" +
	                  "q : 'e' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ae", false);
		assertEquals("", found);
		assertEquals("line 1:1 no viable alternative at input 'ae'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLL3() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'* 'c'\n" +
	                  "  | 'a' 'b' 'd'\n" +
	                  ";\n" +
	                  "q : 'e' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abe", false);
		assertEquals("", found);
		assertEquals("line 1:2 no viable alternative at input 'abe'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLLStar() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a'+ 'b'\n" +
	                  "  | 'a'+ 'c'\n" +
	                  ";\n" +
	                  "q : 'e' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aaae", false);
		assertEquals("", found);
		assertEquals("line 1:3 no viable alternative at input 'aaae'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionBeforeLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'* ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}\nline 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultiTokenDeletionBeforeLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aacabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionDuringLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ababbc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultiTokenDeletionDuringLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaaababc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'c'}\nline 1:6 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionBeforeLoop2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'z'{})*;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}\nline 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultiTokenDeletionBeforeLoop2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'z'{})* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "aacabc", false);
		assertEquals("", found);
		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSingleTokenDeletionDuringLoop2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'z'{})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ababbc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultiTokenDeletionDuringLoop2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : 'a' ('b'|'z'{})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaaababc", false);
		assertEquals("", found);
		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\nline 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLL1ErrorInfo() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : animal (AND acClass)? service EOF;\n" +
	                  "animal : (DOG | CAT );\n" +
	                  "service : (HARDWARE | SOFTWARE) ;\n" +
	                  "AND : 'and';\n" +
	                  "DOG : 'dog';\n" +
	                  "CAT : 'cat';\n" +
	                  "HARDWARE: 'hardware';\n" +
	                  "SOFTWARE: 'software';\n" +
	                  "WS : ' ' -> skip ;\n" +
	                  "acClass\n" +
	                  "@init\n" +
	                  "{document.getElementById('output').value += this.getExpectedTokens().toString(this.literalNames) + '\\n';}\n" +
	                  "  : ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "dog and software", false);
		assertEquals("{'hardware', 'software'}\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testInvalidEmptyInput() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : ID+;\n" +
	                  "ID : [a-z]+;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "", false);
		assertEquals("", found);
		assertEquals("line 1:0 missing ID at '<EOF>'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testContextListGetters() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::members{\n" +
	                  "	function foo() {\n" +
	                  "		var s = new SContext();\n" +
	                  "	    var a = s.a();\n" +
	                  "	    var b = s.b();\n" +
	                  "    };\n" +
	                  "}\n" +
	                  "s : (a | b)+;\n" +
	                  "a : 'a' {document.getElementById('output').value += 'a';};\n" +
	                  "b : 'b' {document.getElementById('output').value += 'b';};";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "abab", false);
		assertEquals("abab\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testDuplicatedLeftRecursiveCall(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : expr EOF;\n" +
	                  "expr : 'x'\n" +
	                  "     | expr expr\n" +
	                  "     ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDuplicatedLeftRecursiveCall_1() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDuplicatedLeftRecursiveCall_2() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xxx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDuplicatedLeftRecursiveCall_3() throws Exception {
		String found = testDuplicatedLeftRecursiveCall("xxxx");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testInvalidATNStateRemoval() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : ID ':' expr;\n" +
	                  "expr : primary expr? {} | expr '->' ID;\n" +
	                  "primary : ID;\n" +
	                  "ID : [a-z]+;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "x:x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNoViableAltAvoidance() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : e '!' ;\n" +
	                  "e : 'a' 'b'\n" +
	                  "  | 'a'\n" +
	                  "  ;\n" +
	                  "DOT : '.' ;\n" +
	                  "WS : [ \\t\\r\\n]+ -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a.", false);
		assertEquals("", found);
		assertEquals("line 1:1 mismatched input '.' expecting '!'\n", this.stderrDuringParse);
	}


}