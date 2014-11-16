/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
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

import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestCompositeGrammars extends BaseTest {
	protected boolean debug = false;

	@Test public void testImportFileLocationInSubdir() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		String subdir = tmpdir + "/sub";
		mkdir(subdir);
		writeFile(subdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		ErrorQueue equeue = antlr("M.g4", false, "-lib", subdir);
		assertEquals(equeue.size(), 0);
	}

	@Test public void testImportFileNotSearchedForInOutputDir() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		String outdir = tmpdir + "/out";
		mkdir(outdir);
		writeFile(outdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		ErrorQueue equeue = antlr("M.g4", false, "-o", outdir);
		assertEquals(ErrorType.CANNOT_FIND_IMPORTED_GRAMMAR, equeue.errors.get(0).getErrorType());
	}

	@Test public void testOutputDirShouldNotEffectImports() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		String subdir = tmpdir + "/sub";
		mkdir(subdir);
		writeFile(subdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		String outdir = tmpdir + "/out";
		mkdir(outdir);
		ErrorQueue equeue = antlr("M.g4", false, "-o", outdir, "-lib", subdir);
		assertEquals(0, equeue.size());
	}

	@Test public void testTokensFileInOutputDirAndImportFileInSubdir() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		String subdir = tmpdir + "/sub";
		mkdir(subdir);
		writeFile(subdir, "S.g4", slave);
		String parser =
			"parser grammar MParser;\n" +
			"import S;\n" +
			"options {tokenVocab=MLexer;}\n" +
			"s : a ;\n";
		writeFile(tmpdir, "MParser.g4", parser);
		String lexer =
			"lexer grammar MLexer;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "MLexer.g4", lexer);
		String outdir = tmpdir + "/out";
		mkdir(outdir);
		ErrorQueue equeue = antlr("MLexer.g4", false, "-o", outdir);
		assertEquals(0, equeue.size());
		equeue = antlr("MParser.g4", false, "-o", outdir, "-lib", subdir);
		assertEquals(0, equeue.size());
	}

	@Test public void testDelegatorInvokesDelegateRule() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testBringInLiteralsFromDelegate() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : '=' 'a' {System.out.println(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
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
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : label=a[3] {System.out.println($label.y);} ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
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
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a {System.out.println($a.text);} ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.ab\n", found);
	}

	@Test public void testDelegatorAccessesDelegateMembers() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"@parser::members {\n" +
			"  public void foo() {System.out.println(\"foo\");}\n" +
			"}\n" +
			"a : B ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +		// uses no rules from the import
			"import S;\n" +
			"s : 'b' {foo();} ;\n" + // gS is import pointer
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("foo\n", found);
	}

	@Test public void testDelegatorInvokesFirstVersionOfDelegateRule() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");} ;\n" +
			"b : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String slave2 =
			"parser grammar T;\n" +
			"a : B {System.out.println(\"T.a\");} ;\n"; // hidden by S.a
		writeFile(tmpdir, "T.g4", slave2);
		String master =
			"grammar M;\n" +
			"import S,T;\n" +
			"s : a ;\n" +
			"B : 'b' ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("S.a\n", found);
	}

	@Test public void testDelegatesSeeSameTokenType() throws Exception {
		String slave =
			"parser grammar S;\n" + // A, B, C token type order
			"tokens { A, B, C }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String slave2 =
			"parser grammar T;\n" +
			"tokens { C, B, A }\n" + // reverse order
			"y : A {System.out.println(\"T.y\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave2);
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
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "aa", debug);
		assertEquals("S.x\n" +
					 "T.y\n", found);
	}

	@Test public void testDelegatesSeeSameTokenType2() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" + // A, B, C token type order
			"tokens { A, B, C }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String slave2 =
			"parser grammar T;\n" +
			"tokens { C, B, A }\n" + // reverse order
			"y : A {System.out.println(\"T.y\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave2);

		String master =
			"grammar M;\n" +
			"import S,T;\n" +
			"s : x y ;\n" + // matches AA, which should be "aa"
			"B : 'b' ;\n" + // another order: B, A, C
			"A : 'a' ;\n" +
			"C : 'c' ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, B=1, A=2, C=3, WS=4}";
		String expectedStringLiteralToTypeMap = "{'a'=2, 'b'=1, 'c'=3}";
		String expectedTypeToTokenList = "[B, A, C, WS]";

		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, sort(g.stringLiteralToTypeMap).toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "aa", debug);
		assertEquals("S.x\n" +
					 "T.y\n", found);
	}

	@Test public void testCombinedImportsCombined() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"grammar S;\n" + // A, B, C token type order
			"tokens { A, B, C }\n" +
			"x : 'x' INT {System.out.println(\"S.x\");} ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x INT ;\n";
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "x 34 9", debug);
		assertEquals("S.x\n", found);
	}

	@Test public void testImportedTokenVocabIgnoredWithWarning() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" +
			"options {tokenVocab=whatever;}\n" +
			"tokens { A }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

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
		writeFile(tmpdir, "S.g4", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		assertEquals(ErrorType.SYNTAX_ERROR, equeue.errors.get(0).getErrorType());
	}

	@Test public void testDelegatorRuleOverridesDelegate() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");} ;\n" +
			"b : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"b : 'b'|'c' ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
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
		writeFile(tmpdir, "JavaDecl.g4", slave);
		String master =
			"grammar Java;\n" +
			"import JavaDecl;\n" +
			"prog : decl ;\n" +
			"type : 'int' | 'float' ;\n" +
			"\n" +
			"ID  : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		// for float to work in decl, type must be overridden
		String found = execParser("Java.g4", master, "JavaParser", "JavaLexer",
								  "prog", "float x = 3;", debug);
		assertEquals("JavaDecl: floatx=3;\n", found);
	}

    @Test public void testDelegatorRuleOverridesDelegates() throws Exception {
        String slave =
            "parser grammar S;\n" +
            "a : b {System.out.println(\"S.a\");} ;\n" +
            "b : 'b' ;\n" ;
        mkdir(tmpdir);
        writeFile(tmpdir, "S.g4", slave);

        String slave2 =
            "parser grammar T;\n" +
            "tokens { A }\n" +
            "b : 'b' {System.out.println(\"T.b\");} ;\n";
        writeFile(tmpdir, "T.g4", slave2);

        String master =
            "grammar M;\n" +
            "import S, T;\n" +
            "b : 'b'|'c' {System.out.println(\"M.b\");}|B|A ;\n" +
            "WS : (' '|'\\n') -> skip ;\n" ;
        String found = execParser("M.g4", master, "MParser", "MLexer",
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
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"lexer grammar M;\n" +
			"import S;\n" +
			"B : 'b' ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String expecting =
			"S.A\n" +
			"[@0,0:0='a',<3>,1:0]\n" +
			"[@1,1:1='b',<1>,1:1]\n" +
			"[@2,2:2='c',<4>,1:2]\n" +
			"[@3,3:2='<EOF>',<-1>,1:3]\n";
		String found = execLexer("M.g4", master, "M", "abc", debug);
		assertEquals(expecting, found);
	}

	@Test public void testLexerDelegatorRuleOverridesDelegate() throws Exception {
		String slave =
			"lexer grammar S;\n" +
			"A : 'a' {System.out.println(\"S.A\");} ;\n" +
			"B : 'b' {System.out.println(\"S.B\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"lexer grammar M;\n" +
			"import S;\n" +
			"A : 'a' B {System.out.println(\"M.A\");} ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execLexer("M.g4", master, "M", "ab", debug);
		assertEquals("M.A\n" +
					 "[@0,0:1='ab',<1>,1:0]\n" +
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
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : A {System.out.println(\"M.a: \"+$A);} ;\n" +
			"A : 'abc' {System.out.println(\"M.A\");} ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "a", "abc", debug);

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());
		assertEquals("unexpected warnings: "+equeue, 0, equeue.warnings.size());

		assertEquals("M.A\n" +
					 "M.a: [@0,0:2='abc',<1>,1:0]\n", found);
	}

	// Make sure that M can import S that imports T.
	@Test public void test3LevelImport() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"a : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave);
		String slave2 =
			"parser grammar S;\n" +
			"import T;\n" +
			"a : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=1}"; // S and T aren't imported; overridden
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		boolean ok =
			rawGenerateAndBuildRecognizer("M.g4", master, "MParser", null);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testBigTreeOfImports() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"tokens{T}\n" +
			"x : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave);
		slave =
			"parser grammar S;\n" +
			"import T;\n" +
			"tokens{S}\n" +
			"y : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);

		slave =
			"parser grammar C;\n" +
			"tokens{C}\n" +
			"i : C ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "C.g4", slave);
		slave =
			"parser grammar B;\n" +
			"tokens{B}\n" +
			"j : B ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "B.g4", slave);
		slave =
			"parser grammar A;\n" +
			"import B,C;\n" +
			"tokens{A}\n" +
			"k : A ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "A.g4", slave);

		String master =
			"grammar M;\n" +
			"import S,A;\n" +
			"tokens{M}\n" +
			"a : M ;\n" ;
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		assertEquals("[]", equeue.errors.toString());
		assertEquals("[]", equeue.warnings.toString());
		String expectedTokenIDToTypeMap = "{EOF=-1, M=1, S=2, T=3, A=4, B=5, C=6}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M, S, T, A, B, C]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		boolean ok =
			rawGenerateAndBuildRecognizer("M.g4", master, "MParser", null);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testRulesVisibleThroughMultilevelImport() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"x : T ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave);
		String slave2 =
			"parser grammar S;\n" + // A, B, C token type order
			"import T;\n" +
			"a : S ;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M x ;\n" ; // x MUST BE VISIBLE TO M
		writeFile(tmpdir, "M.g4", master);
		Grammar g = new Grammar(tmpdir+"/M.g4", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=1, T=2}";
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
		writeFile(tmpdir, "L.g4", gstr);
		gstr =
			"parser grammar G1;\n" +
			"s: a | b;\n" +
			"a: T1;\n" +
			"b: T2;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G1.g4", gstr);

		gstr =
			"parser grammar G2;\n" +
			"import G1;\n" +
			"a: T3;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G2.g4", gstr);
		String G3str =
			"grammar G3;\n" +
			"import G2;\n" +
			"b: T4;\n" ;
		mkdir(tmpdir);
		writeFile(tmpdir, "G3.g4", G3str);

		Grammar g = new Grammar(tmpdir+"/G3.g4", G3str, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, T4=1, T3=2}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[T4, T3]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		boolean ok =
			rawGenerateAndBuildRecognizer("G3.g4", G3str, "G3Parser", null);
		boolean expecting = true; // should be ok
		assertEquals(expecting, ok);
	}

	@Test public void testHeadersPropogatedCorrectlyToImportedGrammars() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : B {System.out.print(\"S.a\");} ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"@header{package mypackage;}\n" +
			"s : a ;\n" +
			"B : 'b' ;" + // defines B from inherited token space
			"WS : (' '|'\\n') -> skip ;\n" ;
		ErrorQueue equeue = antlr("M.g4", master, false);
		int expecting = 0; // should be ok
		assertEquals(expecting, equeue.errors.size());
	}

	@Test public void testImportedRuleWithAction() throws Exception {
		// wasn't terminating. @after was injected into M as if it were @members
		String slave =
			"parser grammar S;\n" +
			"a @after {int x;} : B ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("", found);
	}

	@Test public void testImportedGrammarWithEmptyOptions() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"options {}\n" +
			"a : B ;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : a ;\n" +
			"B : 'b' ;" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		String found = execParser("M.g4", master, "MParser", "MLexer",
								  "s", "b", debug);
		assertEquals("", found);
	}

	/**
	 * This is a regression test for antlr/antlr4#248 "Including grammar with
	 * only fragments breaks generated lexer".
	 * https://github.com/antlr/antlr4/issues/248
	 */
	@Test public void testImportLexerWithOnlyFragmentRules() {
		String slave =
			"lexer grammar Unicode;\n" +
			"\n" +
			"fragment\n" +
			"UNICODE_CLASS_Zs    : '\\u0020' | '\\u00A0' | '\\u1680' | '\\u180E'\n" +
			"                    | '\\u2000'..'\\u200A'\n" +
			"                    | '\\u202F' | '\\u205F' | '\\u3000'\n" +
			"                    ;\n";
		String master =
			"grammar Test;\n" +
			"import Unicode;\n" +
			"\n" +
			"program : 'test' 'test' ;\n" +
			"\n" +
			"WS : (UNICODE_CLASS_Zs)+ -> skip;\n";

		mkdir(tmpdir);
		writeFile(tmpdir, "Unicode.g4", slave);
		String found = execParser("Test.g4", master, "TestParser", "TestLexer", "program", "test test", debug);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#670 "exception when importing
	 * grammar".
	 * https://github.com/antlr/antlr4/issues/670
	 */
	@Test
	public void testImportLargeGrammar() throws Exception {
		String slave = load("Java.g4", "UTF-8");
		String master =
			"grammar NewJava;\n" +
			"import Java;\n";

		System.out.println("dir "+tmpdir);
		mkdir(tmpdir);
		writeFile(tmpdir, "Java.g4", slave);
		String found = execParser("NewJava.g4", master, "NewJavaParser", "NewJavaLexer", "compilationUnit", "package Foo;", debug);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}
}
