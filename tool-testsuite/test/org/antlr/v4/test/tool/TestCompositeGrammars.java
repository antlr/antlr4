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

package org.antlr.v4.test.tool;

import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.test.runtime.java.ErrorQueue;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.junit.Test;

import java.io.File;

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

	@Test public void testErrorInImportedGetsRightFilename() throws Exception {
		String slave =
			"parser grammar S;\n" +
			"a : 'a' | c;\n";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave);
		String master =
			"grammar M;\n" +
			"import S;\n";
		writeFile(tmpdir, "M.g4", master);
		ErrorQueue equeue = antlr("M.g4", false, "-lib", tmpdir);
		ANTLRMessage msg = equeue.errors.get(0);
		assertEquals(ErrorType.UNDEFINED_RULE_REF, msg.getErrorType());
		assertEquals("c", msg.getArgs()[0]);
		assertEquals(2, msg.line);
		assertEquals(10, msg.charPosition);
		assertEquals("S.g4", new File(msg.fileName).getName());
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
		/*Grammar g =*/ new Grammar(tmpdir+"/M.g4", master, equeue);

		assertEquals(ErrorType.SYNTAX_ERROR, equeue.errors.get(0).getErrorType());
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

	/**
	 * This is a regression test for antlr/antlr4#670 "exception when importing
	 * grammar".  I think this one always worked but I found that a different
	 * Java grammar caused an error and so I made the testImportLeftRecursiveGrammar() test below.
	 * https://github.com/antlr/antlr4/issues/670
	 */
	// TODO: migrate to test framework
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

	/**
	 * This is a regression test for antlr/antlr4#670 "exception when importing
	 * grammar".
	 * https://github.com/antlr/antlr4/issues/670
	 */
	// TODO: migrate to test framework
	@Test
	public void testImportLeftRecursiveGrammar() throws Exception {
		String slave =
			"grammar Java;\n" +
			"e : '(' e ')'\n" +
			"  | e '=' e\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : [a-z]+ ;\n";
		String master =
			"grammar T;\n" +
			"import Java;\n" +
			"s : e ;\n";

		System.out.println("dir "+tmpdir);
		mkdir(tmpdir);
		writeFile(tmpdir, "Java.g4", slave);
		String found = execParser("T.g4", master, "TParser", "TLexer", "s", "a=b", debug);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}
}
