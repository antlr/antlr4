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

public class TestASTOps extends BaseTest {
	protected boolean debug = false;

	@Test
	public void testTokenList() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testTokenListInSingleAltBlock() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : (ID INT) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testSimpleChoiceBlock() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : (ID{;}|INT) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testSimpleRootAtOuterLevel() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID^ INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc 34", debug);
		assertEquals("(abc 34)\n", found);
	}

	@Test public void testSimpleRootAtOuterLevelReverse() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT ID^ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34 abc", debug);
		assertEquals("(abc 34)\n", found);
	}

	@Test public void testBang() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT! ID! INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34 dag 4532", debug);
		assertEquals("abc 4532\n", found);
	}

	@Test public void testOptionalThenRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ( ID INT )? ID^ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a 1 b", debug);
		assertEquals("(b a 1)\n", found);
	}

	@Test public void testLabeledStringRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : v='void'^ ID ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "void foo;", debug);
		assertEquals("(void foo ;)\n", found);
	}

	@Test public void testWildcard() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : v='void'^ . ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "void foo;", debug);
		assertEquals("(void foo ;)\n", found);
	}

	@Test public void testWildcardRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : v='void' .^ ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "void foo;", debug);
		assertEquals("(foo void ;)\n", found);
	}

	@Test public void testWildcardRootWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : v='void' x=.^ ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "void foo;", debug);
		assertEquals("(foo void ;)\n", found);
	}

    @Test public void testWildcardRootWithListLabel() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : v='void' x+=.^ ';' ;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                                  "a", "void foo;", debug);
        assertEquals("(foo void ;)\n", found);
    }

    @Test public void testWildcardBangWithListLabel() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : v='void' x=.! ';' ;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                                  "a", "void foo;", debug);
        assertEquals("void ;\n", found);
    }

	@Test public void testRootRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID^ INT^ ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a 34 c", debug);
		assertEquals("(34 a c)\n", found);
	}

	@Test public void testRootRoot2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT^ ID^ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a 34 c", debug);
		assertEquals("(c (34 a))\n", found);
	}

	@Test public void testRootThenRootInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID^ (INT '*'^ ID)+ ;\n" +
			"ID  : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a 34 * b 9 * c", debug);
		assertEquals("(* (* (a 34) b 9) c)\n", found);
	}

	@Test public void testNestedSubrule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'void' (({;}ID|INT) ID | 'null' ) ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "void a b;", debug);
		assertEquals("void a b ;\n", found);
	}

	@Test public void testInvokeRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a  : type ID ;\n" +
			"type : {;}'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "int a", debug);
		assertEquals("int a\n", found);
	}

	@Test public void testInvokeRuleAsRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a  : type^ ID ;\n" +
			"type : {;}'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "int a", debug);
		assertEquals("(int a)\n", found);
	}

	@Test public void testInvokeRuleAsRootWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a  : x=type^ ID ;\n" +
			"type : {;}'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "int a", debug);
		assertEquals("(int a)\n", found);
	}

	@Test public void testInvokeRuleAsRootWithListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a  : x+=type^ ID ;\n" +
			"type : {;}'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "int a", debug);
		assertEquals("(int a)\n", found);
	}

	@Test public void testRuleRootInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ('+'^ ID)* ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a+b+c+d", debug);
		assertEquals("(+ (+ (+ a b) c) d)\n", found);
	}

	@Test public void testRuleInvocationRuleRootInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID (op^ ID)* ;\n" +
			"op : {;}'+' | '-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a+b+c-d", debug);
		assertEquals("(- (+ (+ a b) c) d)\n", found);
	}

	@Test public void testTailRecursion() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"s : a ;\n" +
			"a : atom ('exp'^ a)? ;\n" +
			"atom : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "s", "3 exp 4 exp 5", debug);
		assertEquals("(exp 3 (exp 4 5))\n", found);
	}

	@Test public void testSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID|INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testSetRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ('+' | '-')^ ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "+abc", debug);
		assertEquals("(+ abc)\n", found);
	}

	@Test
    public void testSetRootWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=('+' | '-')^ ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "+abc", debug);
		assertEquals("(+ abc)\n", found);
	}

	@Test public void testSetAsRuleRootInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID (('+'|'-')^ ID)* ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a+b-c", debug);
		assertEquals("(- (+ a b) c)\n", found);
	}

	@Test public void testNotSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ~ID '+' INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34+2", debug);
		assertEquals("34 + 2\n", found);
	}

	@Test public void testNotSetWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=~ID '+' INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34+2", debug);
		assertEquals("34 + 2\n", found);
	}

	@Test public void testNotSetWithListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=~ID '+' INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34+2", debug);
		assertEquals("34 + 2\n", found);
	}

	@Test public void testNotSetRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ~'+'^ INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34 55", debug);
		assertEquals("(34 55)\n", found);
	}

	@Test public void testNotSetRootWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ~'+'^ INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34 55", debug);
		assertEquals("(34 55)\n", found);
	}

	@Test public void testNotSetRootWithListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ~'+'^ INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "34 55", debug);
		assertEquals("(34 55)\n", found);
	}

	@Test public void testNotSetRuleRootInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT (~INT^ INT)* ;\n" +
			"blort : '+' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"PLUS : '+';\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "3+4+5", debug);
		assertEquals("(+ (+ 3 4) 5)\n", found);
	}

	@Test public void testTokenLabelReuse() throws Exception {
		// check for compilation problem due to multiple defines
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : id=ID id=ID {System.out.print(\"2nd id=\"+$id.text+';');} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		assertEquals("2nd id=b;a b\n", found);
	}

	@Test public void testTokenLabelReuse2() throws Exception {
		// check for compilation problem due to multiple defines
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : id=ID id=ID^ {System.out.print(\"2nd id=\"+$id.text+';');} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		assertEquals("2nd id=b;(b a)\n", found);
	}

	@Test public void testTokenListLabelReuse() throws Exception {
		// check for compilation problem due to multiple defines
		// make sure ids has both ID tokens
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ids+=ID ids+=ID {System.out.print(\"id list=\"+$ids+';');} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		String expecting = "id list=[[@0,0:0='a',<3>,1:0], [@2,2:2='b',<3>,1:2]];a b\n";
		assertEquals(expecting, found);
	}

	@Test public void testTokenListLabelReuse2() throws Exception {
		// check for compilation problem due to multiple defines
		// make sure ids has both ID tokens
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ids+=ID^ ids+=ID {System.out.print(\"id list=\"+$ids+';');} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		String expecting = "id list=[[@0,0:0='a',<3>,1:0], [@2,2:2='b',<3>,1:2]];(a b)\n";
		assertEquals(expecting, found);
	}

	@Test public void testTokenListLabelRuleRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : id+=ID^ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a", debug);
		assertEquals("a\n", found);
	}

	@Test public void testTokenListLabelBang() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : id+=ID! ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a", debug);
		assertEquals("", found);
	}

	@Test public void testRuleListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x+=b x+=b {" +
			"Tree t=(Tree)$x.get(1).tree;" +
			"System.out.print(\"2nd x=\"+t.toStringTree()+';');} ;\n" +
			"b : ID;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		assertEquals("2nd x=b;a b\n", found);
	}

	@Test public void testRuleListLabelRuleRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ( x+=b^ )+ {" +
			"System.out.print(\"x=\"+((CommonTree)$x.get(1).tree).toStringTree()+';');} ;\n" +
			"b : ID;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		assertEquals("x=(b a);(b a)\n", found);
	}

	@Test public void testRuleListLabelBang() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x+=b! x+=b {" +
			"System.out.print(\"1st x=\"+((CommonTree)$x.get(0).tree).toStringTree()+';');} ;\n" +
			"b : ID;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b", debug);
		assertEquals("1st x=a;b\n", found);
	}

	@Test public void testComplicatedMelange() throws Exception {
		// check for compilation problem
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : A b=B b=B c+=C c+=C D {String s = $D.text;} ;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "a b b c c d", debug);
		assertEquals("a b b c c d\n", found);
	}

	@Test public void testReturnValueWithAST() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID b {System.out.println($b.i);} ;\n" +
			"b returns [int i] : INT {$i=Integer.parseInt($INT.text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc 34", debug);
		assertEquals("34\nabc 34\n", found);
	}

	@Test public void testSetLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options { output=AST; }\n" +
			"r : (INT|ID)+ ; \n" +
			"ID : 'a'..'z' + ;\n" +
			"INT : '0'..'9' +;\n" +
			"WS: (' ' | '\\n' | '\\t')+ {$channel = HIDDEN;};\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "r", "abc 34 d", debug);
		assertEquals("abc 34 d\n", found);
	}

	@Test public void testExtraTokenInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"decl : type^ ID '='! INT ';'! ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "int 34 x=1;", debug);
		assertEquals("line 1:4 extraneous input '34' expecting ID\n", this.stderrDuringParse);
		assertEquals("(int x 1)\n", found); // tree gets correct x and 1 tokens
	}

	@Test public void testMissingIDInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"tokens {EXPR;}\n" +
			"decl : type^ ID '='! INT ';'! ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "int =1;", debug);
		assertEquals("line 1:4 missing ID at '='\n", this.stderrDuringParse);
		assertEquals("(int <missing ID> 1)\n", found); // tree gets invented ID token
	}

	@Test public void testMissingSetInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"tokens {EXPR;}\n" +
			"decl : type^ ID '='! INT ';'! ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "x=1;", debug);
		assertEquals("line 1:0 mismatched input 'x' expecting set null\n", this.stderrDuringParse);
		assertEquals("(<error: x> x 1)\n", found); // tree gets invented ID token
	}

	@Test public void testMissingTokenGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID INT ;\n" + // follow is EOF
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc", debug);
		assertEquals("line 1:3 missing INT at '<EOF>'\n", this.stderrDuringParse);
		assertEquals("abc <missing INT>\n", found);
	}

	@Test public void testMissingTokenGivesErrorNodeInInvokedRule() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b ;\n" +
			"b : ID INT ;\n" + // follow should see EOF
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc", debug);
		assertEquals("line 1:3 mismatched input '<EOF>' expecting INT\n", this.stderrDuringParse);
		assertEquals("<mismatched token: [@1,3:3='<EOF>',<-1>,1:3], resync=abc>\n", found);
	}

	@Test public void testExtraTokenGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b c ;\n" +
			"b : ID ;\n" +
			"c : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc ick 34", debug);
		assertEquals("line 1:4 extraneous input 'ick' expecting INT\n", this.stderrDuringParse);
		assertEquals("abc 34\n", found);
	}

	@Test public void testMissingFirstTokenGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "34", debug);
		assertEquals("line 1:0 missing ID at '34'\n", this.stderrDuringParse);
		assertEquals("<missing ID> 34\n", found);
	}

	@Test public void testMissingFirstTokenGivesErrorNode2() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b c ;\n" +
			"b : ID ;\n" +
			"c : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "34", debug);
		// finds an error at the first token, 34, and re-syncs.
		// re-synchronizing does not consume a token because 34 follows
		// ref to rule b (start of c). It then matches 34 in c.
		assertEquals("line 1:0 missing ID at '34'\n", this.stderrDuringParse);
		assertEquals("<missing ID> 34\n", found);
	}

	@Test public void testNoViableAltGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b | c ;\n" +
			"b : ID ;\n" +
			"c : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"S : '*' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "*", debug);
		assertEquals("line 1:0 no viable alternative at input '*'\n", this.stderrDuringParse);
		assertEquals("<unexpected: [@0,0:0='*',<6>,1:0], resync=*>\n", found);
	}


	// S U P P O R T

	public void _test() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a :  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer", "a", "abc 34", debug);
		assertEquals("\n", found);
	}

}
