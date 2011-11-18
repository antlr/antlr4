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

import org.antlr.v4.Tool;
import org.antlr.v4.tool.*;
import org.junit.Test;

public class TestCompositeGrammars extends BaseTest {
	protected boolean debug = false;

	@Test public void testWildcardStillWorks() throws Exception {
		String grammar =
			"parser grammar S;\n" +
			"a : B . C ;\n"; // not qualified ID
		Grammar g = new Grammar(grammar);

		ErrorQueue equeue = new ErrorQueue();
		Tool antlr = new Tool();
		antlr.addListener(equeue);
		antlr.process(g,true);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());
	}

	@Test public void testDelegatorInvokesDelegateRule() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testBringInLiteralsFromDelegate() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : '=' 'a' {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "=a", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testDelegatorInvokesDelegateRuleWithArgs() throws Exception {
		// must generate something like:
		// public int a(int x) throws RecognitionException { return gS.a(x); }
		// in M.
		String slave =
			"parser grammar S;\n" +
			"a[int x] returns [int y] : B {System.out.print(\"S.a\"); $y=1000;} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : label=a[3] {System.out.println($label.y);} ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.a1000\n", found);
	}

	@Test public void testDelegatorInvokesDelegateRuleWithReturnStruct() throws Exception {
		// must generate something like:
		// public int a(int x) throws RecognitionException { return gS.a(x); }
		// in M.
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.print(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a {System.out.println($a.text);} ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.ab\n", found);
	}

	@Test public void testDelegatorAccessesDelegateMembers() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"@members {\n" +
			"  public void foo() {System.out.println(\"foo\");}\n" +
			"}\n" +
			"a : B ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +		// uses no rules from the import
			"import S;\n" +
			"s : 'b' {foo();} ;\n" + // gS is import pointer
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("foo\n", found);
	}

	@Test public void testDelegatorInvokesFirstVersionOfDelegateRule() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");} ;\n" +
			"b : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String slave2 =
			"parser grammar T;\n" +
			"a : B {System.out.println(\"T.a\");} ;\n"; // hidden by S.a
		writeFile(tmpdir, "T.g", slave2);
		String master =
			"grammar M;\n" +
			"import S,T;\n" +
			"s : a ;\n" +
			"B : 'b' ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testDelegatesSeeSameTokenType() throws Exception {
		String slave =
			"parser grammar S;\n" + // A, B, C token type order
			"tokens { A; B; C; }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String slave2 =
			"parser grammar T;\n" +
			"tokens { C; B; A; }\n" + // reverse order
			"y : A {System.out.println(\"T.y\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g", slave2);
		// The lexer will create rules to match letters a, b, c.
		// The associated token types A, B, C must have the same value
		// and all import'd parsers.  Since ANTLR regenerates all imports
		// for use with the delegator M, it can generate the same token type
		// mapping in each parser:
		// public static final int C=6;
		// public static final int EOF=-1;
		// public static final int B=5;
		// public static final int WS=7;
		// public static final int A=4;

		String master =
			"grammar M;\n" +
			"import S,T;\n" +
			"s : x y ;\n" + // matches AA, which should be "aa"
			"B : 'b' ;\n" + // another order: B, A, C
			"A : 'a' ;\n" +
			"C : 'c' ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "aa", debug);
		assertEquals("S.x\n" +
					 "T.y\n", found);
	}

	@Test public void testDelegatesSeeSameTokenType2() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" + // A, B, C token type order
			"tokens { A; B; C; }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String slave2 =
			"parser grammar T;\n" +
			"tokens { C; B; A; }\n" + // reverse order
			"y : A {System.out.println(\"T.y\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g", slave2);

		String master =
			"grammar M;\n" +
			"import S,T;\n" +
			"s : x y ;\n" + // matches AA, which should be "aa"
			"B : 'b' ;\n" + // another order: B, A, C
			"A : 'a' ;\n" +
			"C : 'c' ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, B=3, A=4, C=5, WS=6}";
		String expectedStringLiteralToTypeMap = "{'c'=5, 'a'=4, 'b'=3}";
		String expectedTypeToTokenList = "[B, A, C, WS]";

		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "aa", debug);
		assertEquals("S.x\n" +
					 "T.y\n", found);
	}

	@Test public void testCombinedImportsCombined() throws Exception {
		// for now, we don't allow combined to import combined
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"grammar S;\n" + // A, B, C token type order
			"tokens { A; B; C; }\n" +
			"x : 'x' INT {System.out.println(\"S.x\");} ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x INT ;\n";
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		String found = execParser("M.g", master, "MParser", "MLexer",
								  "s", "x 34 9", debug);
		assertEquals("S.x\n", found);
	}

	@Test public void testImportedTokenVocabIgnoredWithWarning() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" +
			"options {tokenVocab=whatever;}\n" +
			"tokens { A='a'; }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		Object expectedArg = "S";
		ErrorType expectedMsgID = ErrorType.OPTIONS_IN_DELEGATE;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g.fileName, null, expectedArg);
		checkGrammarSemanticsWarning(equeue, expectedMessage);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());
		assertEquals("unexpected warnings: "+equeue, 1, equeue.warnings.size());
	}

	@Test public void testSyntaxErrorsInImportsNotThrownOut() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" +
			"options {toke\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		assertEquals(ErrorType.SYNTAX_ERROR, equeue.errors.get(0).errorType);
	}

	@Test public void testDelegatorRuleOverridesDelegate() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");} ;\n" +
			"b : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"b : 'b'|'c' ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "a", "c", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testDelegatorRuleOverridesLookaheadInDelegate() throws Exception {
		String slave =
			"parser grammar JavaDecl;\n" +
			"type : 'int' ;\n" +
			"decl : type ID ';'\n" +
			"     | type ID init ';' {System.out.println(\"JavaDecl: \"+$text);}\n" +
			"     ;\n" +
			"init : '=' INT ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "JavaDecl.g", slave);
		String master =
			"grammar Java;\n" +
			"import JavaDecl;\n" +
			"prog : decl ;\n" +
			"type : 'int' | 'float' ;\n" +
			"\n" +
			"ID  : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		// for float to work in decl, type must be overridden
		String found = execParser("Java.g", master, "JavaParser", "JavaLexer",
								  "prog", "float x = 3;", debug);
		assertEquals("JavaDecl: floatx=3;\n", found);
	}

    @Test public void testDelegatorRuleOverridesDelegates() throws Exception {
        String slave =
            "parser grammar S;\n" +
            "a : b {System.out.println(\"S.a\");} ;\n" +
            "b : B ;\n" ;
        mkdir(tmpdir);
        writeFile(tmpdir, "S.g", slave);

        String slave2 =
            "parser grammar T;\n" +
            "tokens { A='x'; }\n" +
            "b : B {System.out.println(\"T.b\");} ;\n";
        writeFile(tmpdir, "T.g", slave2);

        String master =
            "grammar M;\n" +
            "import S, T;\n" +
            "b : 'b'|'c' {System.out.println(\"M.b\");}|B|A ;\n" +
            "WS : (' '|'\\n') {skip();} ;\n" ;
        String found = execParser("M.g", master, "MParser", "MLexer",
                                  "a", "c", debug);
        assertEquals("M.b\n" +
                     "S.a\n", found);
    }
	// LEXER INHERITANCE

	@Test public void testLexerDelegatorInvokesDelegateRule() throws Exception {
		String slave =
			"lexer grammar S;\n" +
			"A : 'a' {System.out.println(\"S.A\");} ;\n" +
			"C : 'c' ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"lexer grammar M;\n" +
			"import S;\n" +
			"B : 'b' ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String expecting =
			"S.A\n" +
			"[@0,0:0='a',<5>,1:0]\n" +
			"[@1,1:1='b',<3>,1:1]\n" +
			"[@2,2:2='c',<6>,1:2]\n" +
			"[@3,3:2='<EOF>',<-1>,1:3]\n";
		String found = execLexer("M.g", master, "M", "abc", debug);
		assertEquals(expecting, found);
	}

	@Test public void testLexerDelegatorRuleOverridesDelegate() throws Exception {
		String slave =
			"lexer grammar S;\n" +
			"A : 'a' {System.out.println(\"S.A\");} ;\n" +
			"B : 'b' {System.out.println(\"S.B\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"lexer grammar M;\n" +
			"import S;\n" +
			"A : 'a' B {System.out.println(\"M.A\");} ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execLexer("M.g", master, "M", "ab", debug);
		assertEquals("M.A\n" +
					 "[@0,0:1='ab',<3>,1:0]\n" +
					 "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
	}

	@Test public void testKeywordVSIDOrder() throws Exception {
		// rules in lexer are imported at END so rules in master override
		// *and* get priority over imported rules. So importing ID doesn't
		// mess up keywords in master grammar
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"lexer grammar S;\n" +
			"ID : 'a'..'z'+ ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : A {System.out.println(\"M.a: \"+$A);} ;\n" +
			"A : 'abc' {System.out.println(\"M.A\");} ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n" ;
		String found = execParser("M.g", master, "MParser", "MLexer",
								  "a", "abc", debug);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());
		assertEquals("unexpected warnings: "+equeue, 0, equeue.warnings.size());

		assertEquals("M.A\n" +
					 "M.a: [@0,0:2='abc',<3>,1:0]\n", found);
	}

	// Make sure that M can import S that imports T.
	@Test public void test3LevelImport() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"a : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g", slave);
		String slave2 =
			"parser grammar S;\n" +
			"import T;\n" +
			"a : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M ;\n" ;
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=3}"; // S and T aren't imported; overridden
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		boolean ok =
			rawGenerateAndBuildRecognizer("M.g", master, "MParser", null, false);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testBigTreeOfImports() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"x : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g", slave);
		slave =
			"parser grammar S;\n" +
			"import T;\n" +
			"y : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);

		slave =
			"parser grammar C;\n" +
			"i : C ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "C.g", slave);
		slave =
			"parser grammar B;\n" +
			"j : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "B.g", slave);
		slave =
			"parser grammar A;\n" +
			"import B,C;\n" +
			"k : A ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "A.g", slave);

		String master =
			"grammar M;\n" +
			"import S,A;\n" +
			"a : M ;\n" ;
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		assertEquals(equeue.errors.toString(), "[]");
		assertEquals(equeue.warnings.toString(), "[]");
		String expectedTokenIDToTypeMap = "{EOF=-1, M=3, S=4, T=5, A=6, B=7, C=8}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M, S, T, A, B, C]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		boolean ok =
			rawGenerateAndBuildRecognizer("M.g", master, "MParser", null, false);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testRulesVisibleThroughMultilevelImport() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"x : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g", slave);
		String slave2 =
			"parser grammar S;\n" + // A, B, C token type order
			"import T;\n" +
			"a : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M x ;\n" ; // x MUST BE VISIBLE TO M
		writeFile(tmpdir, "M.g", master);
		Grammar g = new Grammar(tmpdir+"/M.g", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=3, T=4}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M, T]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());
	}

	@Test public void testNestedComposite() throws Exception {
		// Wasn't compiling. http://www.antlr.org/jira/browse/ANTLR-438
		ErrorQueue equeue = new ErrorQueue();
		String gstr =
			"lexer grammar L;\n" +
			"T1: '1';\n" +
			"T2: '2';\n" +
			"T3: '3';\n" +
			"T4: '4';\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "L.g", gstr);
		gstr =
			"parser grammar G1;\n" +
			"s: a | b;\n" +
			"a: T1;\n" +
			"b: T2;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G1.g", gstr);

		gstr =
			"parser grammar G2;\n" +
			"import G1;\n" +
			"a: T3;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G2.g", gstr);
		String G3str =
			"grammar G3;\n" +
			"import G2;\n" +
			"b: T4;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G3.g", G3str);

		Grammar g = new Grammar(tmpdir+"/G3.g", G3str, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, T4=3, T3=4}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[T4, T3]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		boolean ok =
			rawGenerateAndBuildRecognizer("G3.g", G3str, "G3Parser", null, false);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testHeadersPropogatedCorrectlyToImportedGrammars() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.print(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"@header{package mypackage;}\n" +
			"@lexer::header{package mypackage;}\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') {skip();} ;\n" ;
		boolean ok = antlr("M.g", "M.g", master, debug);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}
}
