/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
package org.antlr.v4.test;

import org.junit.*;

@Ignore
public class TestRewriteTemplates extends BaseTest {
	protected boolean debug = false;

	@Test public void testDelete() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("", found);
	}

	@Test public void testAction() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> {new StringTemplate($ID.text)} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testEmbeddedLiteralConstructor() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> {%{$ID.text}} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc\n", found);
	}

	@Test public void testInlineTemplate() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> template(x={$ID},y={$INT}) <<x:<x.text>, y:<y.text>;>> ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("x:abc, y:34;\n", found);
	}

	@Test public void testNamedTemplate() throws Exception {
		// the support code adds template group in it's output Test.java
		// that defines template foo.
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> foo(x={$ID.text},y={$INT.text}) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testIndirectTemplate() throws Exception {
		// the support code adds template group in it's output Test.java
		// that defines template foo.
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> ({\"foo\"})(x={$ID.text},y={$INT.text}) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testInlineTemplateInvokingLib() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> template(x={$ID.text},y={$INT.text}) \"<foo(...)>\" ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testPredicatedAlts() throws Exception {
		// the support code adds template group in it's output Test.java
		// that defines template foo.
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : ID INT -> {false}? foo(x={$ID.text},y={$INT.text})\n" +
			"           -> foo(x={\"hi\"}, y={$ID.text})\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("hi abc\n", found);
	}

	@Test public void testTemplateReturn() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : b {System.out.println($b.st);} ;\n" +
			"b : ID INT -> foo(x={$ID.text},y={$INT.text}) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc 34\n", found);
	}

	@Test public void testReturnValueWithTemplate() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a : b {System.out.println($b.i);} ;\n" +
			"b returns [int i] : ID INT {$i=8;} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("8\n", found);
	}

	@Test public void testTemplateRefToDynamicAttributes() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=template;}\n" +
			"a scope {String id;} : ID {$a::id=$ID.text;} b\n" +
			"	{System.out.println($b.st.toString());}\n" +
			"   ;\n" +
			"b : INT -> foo(x={$a::id}) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abc 34", debug);
		assertEquals("abc \n", found);
	}

	// tests for rewriting templates in tree parsers

	@Test public void testSingleNode() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template;}\n" +
			"s : a {System.out.println($a.st);} ;\n" +
			"a : ID -> template(x={$ID.text}) <<|<x>|>> ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
									  treeGrammar, "TP", "TLexer", "a", "s", "abc");
		assertEquals("|abc|\n", found);
	}

	@Test public void testSingleNodeRewriteMode() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template; rewrite=true;}\n" +
			"s : a {System.out.println(input.getTokenStream().toString(0,0));} ;\n" +
			"a : ID -> template(x={$ID.text}) <<|<x>|>> ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
									  treeGrammar, "TP", "TLexer", "a", "s", "abc");
		assertEquals("|abc|\n", found);
	}

	@Test public void testRewriteRuleAndRewriteModeOnSimpleElements() throws Exception {
		String grammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template; rewrite=true;}\n" +
			"a: ^(A B) -> {ick}\n" +
			" | y+=INT -> {ick}\n" +
			" | x=ID -> {ick}\n" +
			" | BLORT -> {ick}\n" +
			" ;\n";
		String expecting = null;
		testErrors(new String[]{grammar, expecting}, false);
	}

	@Test public void testRewriteRuleAndRewriteModeIgnoreActionsPredicates() throws Exception {
		String grammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template; rewrite=true;}\n" +
			"a: {action} {action2} x=A -> {ick}\n" +
			" | {pred1}? y+=B -> {ick}\n" +
			" | C {action} -> {ick}\n" +
			" | {pred2}?=> z+=D -> {ick}\n" +
			" | (E)=> ^(F G) -> {ick}\n" +
			" ;\n";
		String expecting = null;
		testErrors(new String[]{grammar, expecting}, false);
	}

	@Test public void testRewriteRuleAndRewriteModeNotSimple() throws Exception {
		String grammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template; rewrite=true;}\n" +
			"a  : ID+ -> {ick}\n" +
			"   | INT INT -> {ick}\n" +
			"   ;\n";
		String expecting = null;
		testErrors(new String[] {grammar, expecting}, false);
	}

	@Test public void testRewriteRuleAndRewriteModeRefRule() throws Exception {
		String grammar =
			"tree grammar TP;\n"+
			"options {ASTLabelType=CommonTree; output=template; rewrite=true;}\n" +
			"a  : b+ -> {ick}\n" +
			"   | b b A -> {ick}\n" +
			"   ;\n" +
			"b  : B ;\n";
		String expecting = null;
		testErrors(new String[] {grammar, expecting}, false);
	}

}
