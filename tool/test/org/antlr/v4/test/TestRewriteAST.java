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

public class TestRewriteAST extends BaseTest {
	protected boolean debug = false;

	@Test public void testDelete() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("", found);
	}

	@Test public void testSingleToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> ID;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testSingleLabeledToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=ID -> $x;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testSingleTokenToNewNode() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> ID[\"x\"];\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("x\n", found);
	}

	@Test public void testSingleTokenToNewNodeRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> ^(ID[\"x\"] INT);\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("(x INT)\n", found);
	}

	@Test public void testSingleCharLiteral() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'c' -> 'c';\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "c", debug);
		assertEquals("c\n", found);
	}

	@Test public void testSingleStringLiteral() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'ick' -> 'ick';\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "ick", debug);
		assertEquals("ick\n", found);
	}

	@Test public void testSingleRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b -> b;\n" +
			"b : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testReorderTokens() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> INT ID;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("34 abc\n", found);
	}

	@Test public void testReorderTokenAndRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b INT -> INT b;\n" +
			"b : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("34 abc\n", found);
	}

	@Test public void testTokenTree() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> ^(INT ID);\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("(34 abc)\n", found);
	}

	@Test public void testTokenTreeAfterOtherStuff() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'void' ID INT -> 'void' ^(INT ID);\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "void abc 34", debug);
		assertEquals("void (34 abc)\n", found);
	}

	@Test public void testNestedTokenTreeWithOuterLoop() throws Exception {
		// verify that ID and INT both iterate over outer index variable
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {DUH;}\n" +
			"a : ID INT ID INT -> ^( DUH ID ^( DUH INT) )* ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 1 b 2", debug);
		assertEquals("(DUH a (DUH 1)) (DUH b (DUH 2))\n", found);
	}

	@Test public void testOptionalSingleToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> ID? ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testClosureSingleToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ID -> ID* ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testOptionalSingleRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b -> b?;\n" +
			"b : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testClosureSingleRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b b -> b*;\n" +
			"b : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testClosureOfLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x+=b x+=b -> $x*;\n" +
			"b : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testOptionalLabelNoListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : (x=ID)? -> $x?;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a\n", found);
	}

	@Test public void testSinglePredicateT() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> {true}? ID -> ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testSinglePredicateF() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID -> {false}? ID -> ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc", debug);
		assertEquals("", found);
	}

	@Test public void testMultiplePredicate() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> {false}? ID\n" +
			"           -> {true}? INT\n" +
			"           -> \n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 2", debug);
		assertEquals("2\n", found);
	}

	@Test public void testMultiplePredicateTrees() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> {false}? ^(ID INT)\n" +
			"           -> {true}? ^(INT ID)\n" +
			"           -> ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 2", debug);
		assertEquals("(2 a)\n", found);
	}

	@Test public void testSimpleTree() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : op INT -> ^(op INT);\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "-34", debug);
		assertEquals("(- 34)\n", found);
	}

	@Test public void testSimpleTree2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : op INT -> ^(INT op);\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "+ 34", debug);
		assertEquals("(34 +)\n", found);
	}


	@Test public void testNestedTrees() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'var' (ID ':' type ';')+ -> ^('var' ^(':' ID type)*) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "var a:int; b:float;", debug);
		assertEquals("(var (: a int) (: b float))\n", found);
	}

	@Test public void testImaginaryTokenCopy() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {VAR;}\n" +
			"a : ID (',' ID)*-> ^(VAR ID)* ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a,b,c", debug);
		assertEquals("(VAR a) (VAR b) (VAR c)\n", found);
	}

	@Test public void testTokenUnreferencedOnLeftButDefined() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {VAR;}\n" +
			"a : b -> ID ;\n" +
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("ID\n", found);
	}

	@Test public void testImaginaryTokenCopySetText() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {VAR;}\n" +
			"a : ID (',' ID)*-> ^(VAR[\"var\"] ID)* ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a,b,c", debug);
		assertEquals("(var a) (var b) (var c)\n", found);
	}

	@Test public void testImaginaryTokenNoCopyFromToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : lc='{' ID+ '}' -> ^(BLOCK[$lc] ID*) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "{a b c}", debug);
		assertEquals("({ a b c)\n", found);
	}

	@Test public void testImaginaryTokenNoCopyFromTokenSetText() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : lc='{' ID+ '}' -> ^(BLOCK[$lc,\"block\"] ID*) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "{a b c}", debug);
		assertEquals("(block a b c)\n", found);
	}

	@Test public void testMixedRewriteAndAutoAST() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : b b^ ;\n" + // 2nd b matches only an INT; can make it root
			"b : ID INT -> INT ID\n" +
			"  | INT\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 1 2", debug);
		assertEquals("(2 1 a)\n", found);
	}

	@Test public void testSubruleWithRewrite() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : b b ;\n" +
			"b : (ID INT -> INT ID | INT INT -> INT* )\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 1 2 3", debug);
		assertEquals("1 a 2 3\n", found);
	}

	@Test public void testSubruleWithRewrite2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {TYPE;}\n" +
			"a : b b ;\n" +
			"b : 'int'\n" +
			"    ( ID -> ^(TYPE 'int' ID)\n" +
			"    | ID '=' INT -> ^(TYPE 'int' ID INT)\n" +
			"    )\n" +
			"    ';'\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a; int b=3;", debug);
		assertEquals("(TYPE int a) (TYPE int b 3)\n", found);
	}

	@Test public void testNestedRewriteShutsOffAutoAST() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : b b ;\n" +
			"b : ID ( ID (last=ID -> $last)* ) ';'\n" + // get last ID
			"  | INT\n" + // should still get auto AST construction
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b c; 42", debug);
		assertEquals("c 42\n", found);
	}

	@Test public void testRewriteActions() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : atom -> ^({_adaptor.create(INT,\"9\")} atom) ;\n" +
			"atom : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "3", debug);
		assertEquals("(9 3)\n", found);
	}

	@Test public void testRewriteActions2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : atom -> {_adaptor.create(INT,\"9\")} atom ;\n" +
			"atom : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "3", debug);
		assertEquals("9 3\n", found);
	}

	@Test public void testRefToOldValue() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : (atom -> atom) (op='+' r=atom -> ^($op $a $r) )* ;\n" +
			"atom : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "3+4+5", debug);
		assertEquals("(+ (+ 3 4) 5)\n", found);
	}

	@Test public void testCopySemanticsForRules() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : atom -> ^(atom atom) ;\n" + // NOT CYCLE! (dup atom)
			"atom : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "3", debug);
		assertEquals("(3 3)\n", found);
	}

	@Test public void testCopySemanticsForRules2() throws Exception {
		// copy type as a root for each invocation of (...)* in rewrite
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : type ID (',' ID)* ';' -> ^(type ID)* ;\n" +
			"type : 'int' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a,b,c;", debug);
		assertEquals("(int a) (int b) (int c)\n", found);
	}

	@Test public void testCopySemanticsForRules3() throws Exception {
		// copy type *and* modifier even though it's optional
		// for each invocation of (...)* in rewrite
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : modifier? type ID (',' ID)* ';' -> ^(type modifier? ID)* ;\n" +
			"type : 'int' ;\n" +
			"modifier : 'public' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "public int a,b,c;", debug);
		assertEquals("(int public a) (int public b) (int public c)\n", found);
	}

	@Test public void testCopySemanticsForRules3Double() throws Exception {
		// copy type *and* modifier even though it's optional
		// for each invocation of (...)* in rewrite
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : modifier? type ID (',' ID)* ';' -> ^(type modifier? ID)* ^(type modifier? ID)* ;\n" +
			"type : 'int' ;\n" +
			"modifier : 'public' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "public int a,b,c;", debug);
		assertEquals("(int public a) (int public b) (int public c) (int public a) (int public b) (int public c)\n", found);
	}

	@Test public void testCopySemanticsForRules4() throws Exception {
		// copy type *and* modifier even though it's optional
		// for each invocation of (...)* in rewrite
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {MOD;}\n" +
			"a : modifier? type ID (',' ID)* ';' -> ^(type ^(MOD modifier)? ID)* ;\n" +
			"type : 'int' ;\n" +
			"modifier : 'public' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "public int a,b,c;", debug);
		assertEquals("(int (MOD public) a) (int (MOD public) b) (int (MOD public) c)\n", found);
	}

	@Test public void testCopySemanticsLists() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {MOD;}\n" +
			"a : ID (',' ID)* ';' -> ID* ID* ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a,b,c;", debug);
		assertEquals("a b c a b c\n", found);
	}

	@Test public void testCopyRuleLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=b -> $x $x;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a a\n", found);
	}

	@Test public void testCopyRuleLabel2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=b -> ^($x $x);\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("(a a)\n", found);
	}

	@Test public void testQueueingOfTokens() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'int' ID (',' ID)* ';' -> ^('int' ID*) ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a,b,c;", debug);
		assertEquals("(int a b c)\n", found);
	}

	@Test public void testCopyOfTokens() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'int' ID ';' -> 'int' ID 'int' ID ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a;", debug);
		assertEquals("int a int a\n", found);
	}

	@Test public void testTokenCopyInLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'int' ID (',' ID)* ';' -> ^('int' ID)* ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a,b,c;", debug);
		assertEquals("(int a) (int b) (int c)\n", found);
	}

	@Test public void testTokenCopyInLoopAgainstTwoOthers() throws Exception {
		// must smear 'int' copies across as root of multiple trees
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'int' ID ':' INT (',' ID ':' INT)* ';' -> ^('int' ID INT)* ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "int a:1,b:2,c:3;", debug);
		assertEquals("(int a 1) (int b 2) (int c 3)\n", found);
	}

	@Test public void testListRefdOneAtATime() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID+ -> ID ID ID ;\n" + // works if 3 input IDs
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b c", debug);
		assertEquals("a b c\n", found);
	}

	@Test public void testSplitListWithLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {VAR;}\n"+
			"a : first=ID others+=ID* -> $first VAR $others* ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b c", debug);
		assertEquals("a VAR b c\n", found);
	}

	@Test public void testComplicatedMelange() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : A A b=B B b=B c+=C C c+=C D {String s=$D.text;} -> A* B* C* D ;\n" +
			"type : 'int' | 'float' ;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a a b b b c c c d", debug);
		assertEquals("a a b b b c c c d\n", found);
	}

	@Test public void testRuleLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=b -> $x;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a\n", found);
	}

	@Test public void testAmbiguousRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID a -> a | INT ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT: '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("34\n", found);
	}

	/*
	@Test public void testWeirdRuleRef() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID a -> $a | INT ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT: '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		// $a is ambig; is it previous root or ref to a ref in alt?
		assertEquals("unexpected errors: "+equeue, 1, equeue.errors.size());
	}
	*/

	@Test public void testRuleListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x+=b x+=b -> $x*;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testRuleListLabel2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x+=b x+=b -> $x $x*;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a a b\n", found);
	}

	@Test public void testOptional() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=b (y=b)? -> $x $y?;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a\n", found);
	}

	@Test public void testOptional2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=ID (y=b)? -> $x $y?;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testOptional3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x=ID (y=b)? -> ($x $y)?;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testOptional4() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x+=ID (y=b)? -> ($x $y)?;\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("a b\n", found);
	}

	@Test public void testOptional5() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : ID -> ID? ;\n"+ // match an ID to optional ID
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a\n", found);
	}

	@Test public void testArbitraryExprType() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : x+=b x+=b -> {new CommonTree()};\n"+
			"b : ID ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b", debug);
		assertEquals("", found);
	}

	@Test public void testSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options { output = AST; } \n" +
			"a: (INT|ID)+ -> INT* ID* ;\n" +
			"INT: '0'..'9'+;\n" +
			"ID : 'a'..'z'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "2 a 34 de", debug);
		assertEquals("2 34 a de\n", found);
	}

	@Test public void testSet2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options { output = AST; } \n" +
			"a: (INT|ID) -> INT? ID? ;\n" +
			"INT: '0'..'9'+;\n" +
			"ID : 'a'..'z'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "2", debug);
		assertEquals("2\n", found);
	}

	@Test public void testSetWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options { output = AST; } \n" +
			"a : x=(INT|ID) -> $x ;\n" +
			"INT: '0'..'9'+;\n" +
			"ID : 'a'..'z'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "2", debug);
		assertEquals("2\n", found);
	}

	@Test public void testRewriteAction() throws Exception {
		String grammar =
			"grammar T; \n" +
			"options { output = AST; }\n" +
			"tokens { FLOAT; }\n" +
			"r\n" +
			"    : INT -> {new CommonTree(new CommonToken(FLOAT,$INT.text+\".0\"))} \n" +
			"    ; \n" +
			"INT : '0'..'9'+; \n" +
			"WS: (' ' | '\\n' | '\\t')+ {$channel = HIDDEN;}; \n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "r", "25", debug);
		assertEquals("25.0\n", found);
	}

	@Test public void testOptionalSubruleWithoutRealElements() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;} \n" +
			"tokens {PARMS;} \n" +
			"\n" +
			"modulo \n" +
			" : 'modulo' ID ('(' parms+ ')')? -> ^('modulo' ID ^(PARMS parms*)?) \n" +
			" ; \n" +
			"parms : '#'|ID; \n" +
			"ID : ('a'..'z' | 'A'..'Z')+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "modulo", "modulo abc (x y #)", debug);
		assertEquals("(modulo abc (PARMS x y #))\n", found);
	}

	// C A R D I N A L I T Y  I S S U E S

	@Test public void testCardinality() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"tokens {BLOCK;}\n" +
			"a : ID ID INT INT INT -> (ID INT)*;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+; \n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a b 3 4 5", debug);
		String expecting =
			"org.antlr.v4.runtime.tree.RewriteCardinalityException: size==2 and out of elements";
		String found = getFirstLineOfException();
		assertEquals(expecting, found);
	}

	@Test public void testCardinality2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID+ -> ID ID ID ;\n" + // only 2 input IDs
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		execParser("T.g", grammar, "TParser", "TLexer",
				   "a", "a b", debug);
		String expecting =
			"org.antlr.v4.runtime.tree.RewriteCardinalityException: size==2 and out of elements";
		String found = getFirstLineOfException();
		assertEquals(expecting, found);
	}

	@Test public void testCardinality3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID? INT -> ID INT ;\n" +
			"op : '+'|'-' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		execParser("T.g", grammar, "TParser", "TLexer",
				   "a", "3", debug);
		String expecting =
			"org.antlr.v4.runtime.tree.RewriteEmptyStreamException: n/a";
		String found = getFirstLineOfException();
		assertEquals(expecting, found);
	}

	@Test public void testWildcard() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID c=. -> $c;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34", debug);
		assertEquals("34\n", found);
	}

	@Test public void testWildcard2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID c+=. c+=. -> $c*;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "abc 34 def", debug);
		assertEquals("34 def\n", found);
	}

	// E R R O R S

	/*
	@Test public void testUnknownRule() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> ugh ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_UNDEFINED_RULE_REF;
		Object expectedArg = "ugh";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}

	@Test public void testKnownRuleButNotInLHS() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> b ;\n" +
			"b : 'b' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_REWRITE_ELEMENT_NOT_PRESENT_ON_LHS;
		Object expectedArg = "b";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}

	@Test public void testUnknownToken() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> ICK ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_UNDEFINED_TOKEN_REF_IN_REWRITE;
		Object expectedArg = "ICK";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}

	@Test public void testUnknownLabel() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> $foo ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_UNDEFINED_LABEL_REF_IN_REWRITE;
		Object expectedArg = "foo";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}

	@Test public void testUnknownCharLiteralToken() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> 'a' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_UNDEFINED_TOKEN_REF_IN_REWRITE;
		Object expectedArg = "'a'";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}

	@Test public void testUnknownStringLiteralToken() throws Exception {
		ErrorQueue equeue = new ErrorQueue();

		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : INT -> 'foo' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		Grammar g = new Grammar(grammar);
		Tool antlr = newTool();
		antlr.addListener(equeue);
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer();

		int expectedMsgID = ErrorManager.MSG_UNDEFINED_TOKEN_REF_IN_REWRITE;
		Object expectedArg = "'foo'";
		Object expectedArg2 = null;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);

		checkError(equeue, expectedMessage);
	}
	*/

	@Test public void testExtraTokenInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"tokens {EXPR;}\n" +
			"decl : type ID '=' INT ';' -> ^(EXPR type ID INT) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "int 34 x=1;", debug);
		assertEquals("line 1:4 extraneous input '34' expecting ID\n", this.stderrDuringParse);
		assertEquals("(EXPR int x 1)\n", found); // tree gets correct x and 1 tokens
	}

	@Test public void testMissingIDInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"tokens {EXPR;}\n" +
			"decl : type ID '=' INT ';' -> ^(EXPR type ID INT) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "int =1;", debug);
		assertEquals("line 1:4 missing ID at '='\n", this.stderrDuringParse);
		assertEquals("(EXPR int <missing ID> 1)\n", found); // tree gets invented ID token
	}

	@Test public void testMissingSetInSimpleDecl() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"tokens {EXPR;}\n" +
			"decl : type ID '=' INT ';' -> ^(EXPR type ID INT) ;\n" +
			"type : 'int' | 'float' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "decl", "x=1;", debug);
		assertEquals("line 1:0 mismatched input 'x' expecting set null\n", this.stderrDuringParse);
		assertEquals("(EXPR <error: x> x 1)\n", found); // tree gets invented ID token
	}

	@Test public void testMissingTokenGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> ID INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "abc", debug);
		assertEquals("line 1:3 missing INT at '<EOF>'\n", this.stderrDuringParse);
		// doesn't do in-line recovery for sets (yet?)
		assertEquals("abc <missing INT>\n", found);
	}

	@Test public void testExtraTokenGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b c -> b c;\n" +
			"b : ID -> ID ;\n" +
			"c : INT -> INT ;\n" +
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
			"a : ID INT -> ID INT ;\n" +
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
			"a : b c -> b c;\n" +
			"b : ID -> ID ;\n" +
			"c : INT -> INT ;\n" +
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

	@Test
	public void testNoViableAltGivesErrorNode() throws Exception {
		String grammar =
			"grammar foo;\n" +
			"options {output=AST;}\n" +
			"a : b -> b | c -> c;\n" +
			"b : ID -> ID ;\n" +
			"c : INT -> INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"S : '*' ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("foo.g", grammar, "fooParser", "fooLexer",
								  "a", "*", debug);
		// finds an error at the first token, 34, and re-syncs.
		// re-synchronizing does not consume a token because 34 follows
		// ref to rule b (start of c). It then matches 34 in c.
		assertEquals("line 1:0 no viable alternative at input '*'\n", this.stderrDuringParse);
		assertEquals("<unexpected: [@0,0:0='*',<6>,1:0], resync=*>\n", found);
	}

}
