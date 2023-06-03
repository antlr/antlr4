/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.antlr.v4.test.runtime.states.State;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;
import static org.antlr.v4.test.tool.ToolTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompositeGrammars {
	protected boolean debug = false;

	@Test public void testImportFileLocationInSubdir(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
		FileUtils.mkdir(tempDirPath);
		String subdir = tempDirPath + FileSeparator + "sub";
		FileUtils.mkdir(subdir);
		writeFile(subdir, "S.g4",
				"parser grammar S;\n" +
				"a : B {System.out.println(\"S.a\");} ;\n");

		checkGeneration(
				"grammar M;\n" +
				"import S;\n" +
				"s : a ;\n" +
				"B : 'b' ;" + // defines B from inherited token space
				"WS : (' '|'\\n') -> skip ;\n",

				tempDir, new String[] {"-lib", subdir}
		);
	}

	// Test for https://github.com/antlr/antlr4/issues/1317
	@Test public void testImportSelfLoop() {
		checkGeneration(
				"grammar M;\n" +
				"import M;\n" +
				"s : 'a' ;\n",

				null);
	}

	@Test public void testImportIntoLexerGrammar() {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"A : 'a';\n" +
				"B : 'b';\n",

				"lexer grammar S;\n" +
				"C : 'c';\n"
		);
	}

	@Test public void testImportModesIntoLexerGrammar() {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"A : 'a' -> pushMode(X);\n" +
				"B : 'b';\n",

				"lexer grammar S;\n" +
				"D : 'd';\n" +
				"mode X;\n" +
				"C : 'c' -> popMode;\n"
		);
	}

	@Test public void testImportChannelsIntoLexerGrammar() {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"channels {CH_A, CH_B}\n" +
				"A : 'a' -> channel(CH_A);\n" +
				"B : 'b' -> channel(CH_B);\n",

				"lexer grammar S;\n" +
				"C : 'c';\n"
		);
	}

	@Test public void testImportMixedChannelsIntoLexerGrammar() {
		checkGeneration("lexer grammar M;\n" +
				"import S;\n" +
				"channels {CH_A, CH_B}\n" +
				"A : 'a' -> channel(CH_A);\n" +
				"B : 'b' -> channel(CH_B);\n",

				"lexer grammar S;\n" +
				"channels {CH_C}\n" +
				"C : 'c' -> channel(CH_C);\n");
	}

	@Test public void testImportClashingChannelsIntoLexerGrammar() {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"channels {CH_A, CH_B, CH_C}\n" +
				"A : 'a' -> channel(CH_A);\n" +
				"B : 'b' -> channel(CH_B);\n" +
				"C : 'C' -> channel(CH_C);\n",

				"lexer grammar S;\n" +
				"channels {CH_C}\n" +
				"C : 'c' -> channel(CH_C);\n"
		);
	}

	@Test public void testMergeModesIntoLexerGrammar(@TempDir Path tempDir) {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"A : 'a' -> pushMode(X);\n" +
				"mode X;\n" +
				"B : 'b';\n",

				"lexer grammar S;\n" +
				"D : 'd';\n" +
				"mode X;\n" +
				"C : 'c' -> popMode;\n"
		);
	}

	@Test public void testEmptyModesInLexerGrammar() {
		checkGeneration(
				"lexer grammar M;\n" +
				"import S;\n" +
				"A : 'a';\n" +
				"C : 'e';\n" +
				"B : 'b';\n",

				"lexer grammar S;\n" +
				"D : 'd';\n" +
				"mode X;\n" +
				"C : 'c' -> popMode;\n"
		);
	}

	@Test public void testCombinedGrammarImportsModalLexerGrammar() {
		GeneratedState state = generate(
				"grammar M;\n" +
				"import S;\n" +
				"A : 'a';\n" +
				"B : 'b';\n" +
				"r : A B;\n",

				"lexer grammar S;\n" +
				"D : 'd';\n" +
				"mode X;\n" +
				"C : 'c' -> popMode;\n"
		);

		List<ANTLRMessage> errors = state.errorQueue.errors;
		assertEquals(1, errors.size());
		ANTLRMessage msg = errors.get(0);
		assertEquals(ErrorType.MODE_NOT_IN_LEXER, msg.getErrorType());
		assertEquals("X", msg.getArgs()[0]);
		assertEquals(3, msg.line);
		assertEquals(5, msg.charPosition);
		assertEquals("M.g4", new File(msg.fileName).getName());
	}

	@Test public void testDelegatesSeeSameTokenType(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		String slaveS =
			"parser grammar S;\n"+
			"tokens { A, B, C }\n"+
			"x : A ;\n";
		String slaveT =
			"parser grammar T;\n"+
			"tokens { C, B, A } // reverse order\n"+
			"y : A ;\n";

		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slaveS);
		writeFile(tempDirPath, "T.g4", slaveT);

		String master =
			"// The lexer will create rules to match letters a, b, c.\n"+
			"// The associated token types A, B, C must have the same value\n"+
			"// and all import'd parsers.  Since ANTLR regenerates all imports\n"+
			"// for use with the delegator M, it can generate the same token type\n"+
			"// mapping in each parser:\n"+
			"// public static final int C=6;\n"+
			"// public static final int EOF=-1;\n"+
			"// public static final int B=5;\n"+
			"// public static final int WS=7;\n"+
			"// public static final int A=4;\n"+
			"grammar M;\n"+
			"import S,T;\n"+
			"s : x y ; // matches AA, which should be 'aa'\n"+
			"B : 'b' ; // another order: B, A, C\n"+
			"A : 'a' ;\n"+
			"C : 'c' ;\n"+
			"WS : (' '|'\\n') -> skip ;\n";
		writeFile(tempDirPath, "M.g4", master);
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(tempDirPath+"/M.g4", master, equeue);
		String expectedTokenIDToTypeMap = "{EOF=-1, B=1, A=2, C=3, WS=4}";
		String expectedStringLiteralToTypeMap = "{'a'=2, 'b'=1, 'c'=3}";
		String expectedTypeToTokenList = "[B, A, C, WS]";
		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, sort(g.stringLiteralToTypeMap).toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());
		assertEquals(0, equeue.errors.size(), "unexpected errors: "+equeue);
	}

	@Test public void testErrorInImportedGetsRightFilename() {
		GeneratedState state = generate(
				"grammar M;\n" +
				"import S;\n",

				"parser grammar S;\n" +
				"a : 'a' | c;\n");

		ANTLRMessage msg = state.errorQueue.errors.get(0);
		assertEquals(ErrorType.UNDEFINED_RULE_REF, msg.getErrorType());
		assertEquals("c", msg.getArgs()[0]);
		assertEquals(2, msg.line);
		assertEquals(10, msg.charPosition);
		assertEquals("S.g4", new File(msg.fileName).getName());
	}

	@Test public void testImportFileNotSearchedForInOutputDir(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
		FileUtils.mkdir(tempDirPath);
		String outdir = tempDirPath + "/out";
		FileUtils.mkdir(outdir);
		writeFile(outdir, "S.g4",
				"parser grammar S;\n" +
				"a : B {System.out.println(\"S.a\");} ;\n"
		);

		GeneratedState state = generate(
				"grammar M;\n" +
				"import S;\n" +
				"s : a ;\n" +
				"B : 'b' ;" + // defines B from inherited token space
				"WS : (' '|'\\n') -> skip ;\n",

				null,

				tempDir, new String[] { "-o", outdir }, false
		);
		assertEquals(ErrorType.CANNOT_FIND_IMPORTED_GRAMMAR, state.errorQueue.errors.get(0).getErrorType());
	}

	@Test public void testOutputDirShouldNotEffectImports(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
		FileUtils.mkdir(tempDirPath);

		String subdir = tempDirPath + "/sub";
		FileUtils.mkdir(subdir);
		writeFile(subdir, "S.g4",
				"parser grammar S;\n" +
				"a : B {System.out.println(\"S.a\");} ;\n"
		);

		String outdir = tempDirPath + "/out";
		FileUtils.mkdir(outdir);

		checkGeneration(
				"grammar M;\n" +
				"import S;\n" +
				"s : a ;\n" +
				"B : 'b' ;" + // defines B from inherited token space
				"WS : (' '|'\\n') -> skip ;\n",

				tempDir, new String[] {"-o", outdir, "-lib", subdir}
		);
	}

	@Test public void testTokensFileInOutputDirAndImportFileInSubdir(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
		FileUtils.mkdir(tempDirPath);
		String subdir = tempDirPath + "/sub";
		FileUtils.mkdir(subdir);
		writeFile(subdir, "S.g4",
				"parser grammar S;\n" +
				"a : B {System.out.println(\"S.a\");} ;\n");
		String outdir = tempDirPath + "/out";
		FileUtils.mkdir(outdir);

		checkGeneration(
				"lexer grammar MLexer;\n" +
				"B : 'b' ;" + // defines B from inherited token space
				"WS : (' '|'\\n') -> skip ;\n",

				null,

				tempDir, new String[] {"-o", outdir}, true
		);

		checkGeneration(
				"parser grammar MParser;\n" +
						"import S;\n" +
						"options {tokenVocab=MLexer;}\n" +
						"s : a ;\n",

				tempDir, new String[] {"-o", outdir, "-lib", subdir}
		);
	}

	@Test public void testImportedTokenVocabIgnoredWithWarning(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" +
			"options {tokenVocab=whatever;}\n" +
			"tokens { A }\n" +
			"x : A {System.out.println(\"S.x\");} ;\n";
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tempDirPath, "M.g4", master);
		Grammar g = new Grammar(tempDirPath+"/M.g4", master, equeue);

		Object expectedArg = "S";
		ErrorType expectedMsgID = ErrorType.OPTIONS_IN_DELEGATE;
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g.fileName, null, expectedArg);
		checkGrammarSemanticsWarning(equeue, expectedMessage);

		assertEquals(0, equeue.errors.size(), "unexpected errors: "+equeue);
		assertEquals(1, equeue.warnings.size(), "unexpected warnings: "+equeue);
	}

	@Test public void testSyntaxErrorsInImportsNotThrownOut(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar S;\n" +
			"options {toke\n";
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slave);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"s : x ;\n" +
			"WS : (' '|'\\n') -> skip ;\n" ;
		writeFile(tempDirPath, "M.g4", master);
		/*Grammar g =*/ new Grammar(tempDirPath+"/M.g4", master, equeue);

		assertEquals(ErrorType.SYNTAX_ERROR, equeue.errors.get(0).getErrorType());
	}

	// Make sure that M can import S that imports T.
	@Test public void test3LevelImport(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"a : T ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "T.g4", slave);
		String slave2 =
			"parser grammar S;\n" +
			"import T;\n" +
			"a : S ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M ;\n" ;
		writeFile(tempDirPath, "M.g4", master);
		Grammar g = new Grammar(tempDirPath+"/M.g4", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=1}"; // S and T aren't imported; overridden
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals(0, equeue.errors.size(), "unexpected errors: "+equeue);
		checkCompilation(master, tempDir);
	}

	@Test public void testBigTreeOfImports(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"tokens{T}\n" +
			"x : T ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "T.g4", slave);
		slave =
			"parser grammar S;\n" +
			"import T;\n" +
			"tokens{S}\n" +
			"y : S ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slave);

		slave =
			"parser grammar C;\n" +
			"tokens{C}\n" +
			"i : C ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "C.g4", slave);
		slave =
			"parser grammar B;\n" +
			"tokens{B}\n" +
			"j : B ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "B.g4", slave);
		slave =
			"parser grammar A;\n" +
			"import B,C;\n" +
			"tokens{A}\n" +
			"k : A ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "A.g4", slave);

		String master =
			"grammar M;\n" +
			"import S,A;\n" +
			"tokens{M}\n" +
			"a : M ;\n" ;
		writeFile(tempDirPath, "M.g4", master);
		Grammar g = new Grammar(tempDirPath+"/M.g4", master, equeue);

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
		checkCompilation(master, tempDir);
	}

	@Test public void testRulesVisibleThroughMultilevelImport(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		ErrorQueue equeue = new ErrorQueue();
		String slave =
			"parser grammar T;\n" +
			"x : T ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "T.g4", slave);
		String slave2 =
			"parser grammar S;\n" + // A, B, C token type order
			"import T;\n" +
			"a : S ;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "S.g4", slave2);

		String master =
			"grammar M;\n" +
			"import S;\n" +
			"a : M x ;\n" ; // x MUST BE VISIBLE TO M
		writeFile(tempDirPath, "M.g4", master);
		Grammar g = new Grammar(tempDirPath+"/M.g4", master, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, M=1, T=2}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[M, T]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals(0, equeue.errors.size(), "unexpected errors: "+equeue);
	}

	@Test public void testNestedComposite(@TempDir Path tempDir) throws RecognitionException {
		String tempDirPath = tempDir.toString();
		// Wasn't compiling. http://www.antlr.org/jira/browse/ANTLR-438
		ErrorQueue equeue = new ErrorQueue();
		String gstr =
			"lexer grammar L;\n" +
			"T1: '1';\n" +
			"T2: '2';\n" +
			"T3: '3';\n" +
			"T4: '4';\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "L.g4", gstr);
		gstr =
			"parser grammar G1;\n" +
			"s: a | b;\n" +
			"a: T1;\n" +
			"b: T2;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "G1.g4", gstr);

		gstr =
			"parser grammar G2;\n" +
			"import G1;\n" +
			"a: T3;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "G2.g4", gstr);
		String G3str =
			"grammar G3;\n" +
			"import G2;\n" +
			"b: T4;\n" ;
		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "G3.g4", G3str);

		Grammar g = new Grammar(tempDirPath+"/G3.g4", G3str, equeue);

		String expectedTokenIDToTypeMap = "{EOF=-1, T4=1, T3=2}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[T4, T3]";

		assertEquals(expectedTokenIDToTypeMap,
					 g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList,
					 realElements(g.typeToTokenList).toString());

		assertEquals(0, equeue.errors.size(), "unexpected errors: "+equeue);

		checkCompilation(G3str, tempDir);
	}

	@Test public void testHeadersPropogatedCorrectlyToImportedGrammars() {
		checkGeneration(
				"grammar M;\n" +
				"import S;\n" +
				"@header{package mypackage;}\n" +
				"s : a ;\n" +
				"B : 'b' ;" + // defines B from inherited token space
				"WS : (' '|'\\n') -> skip ;\n",

				"parser grammar S;\n" +
				"a : B {System.out.print(\"S.a\");} ;\n"
		);
	}

	/**
	 * This is a regression test for antlr/antlr4#670 "exception when importing
	 * grammar".  I think this one always worked but I found that a different
	 * Java grammar caused an error and so I made the testImportLeftRecursiveGrammar() test below.
	 * https://github.com/antlr/antlr4/issues/670
	 */
	// TODO: migrate to test framework
	@Test
	public void testImportLargeGrammar(@TempDir Path tempDir) throws IOException {
		String tempDirPath = tempDir.toString();
		String slave = load("Java.g4");
		String master =
			"grammar NewJava;\n" +
			"import Java;\n";

		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "Java.g4", slave);
		ExecutedState executedState = execParser(master, "compilationUnit", "package Foo;", debug, tempDir, false);
		assertEquals("", executedState.output);
		assertEquals("", executedState.errors);
	}

	/**
	 * This is a regression test for antlr/antlr4#670 "exception when importing
	 * grammar".
	 * https://github.com/antlr/antlr4/issues/670
	 */
	// TODO: migrate to test framework
	@Test
	public void testImportLeftRecursiveGrammar(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
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

		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "Java.g4", slave);
		ExecutedState executedState = execParser(master, "s", "a=b", debug, tempDir, false);
		assertEquals("", executedState.output);
		assertEquals("", executedState.errors);
	}

	// ISSUE: https://github.com/antlr/antlr4/issues/2296
	@Test
	public void testCircularGrammarInclusion(@TempDir Path tempDir) {
		String tempDirPath = tempDir.toString();
		String g1 =
				"grammar G1;\n" +
				"import  G2;\n" +
				"r : 'R1';";

		String g2 =
				"grammar G2;\n" +
				"import  G1;\n" +
				"r : 'R2';";

		FileUtils.mkdir(tempDirPath);
		writeFile(tempDirPath, "G1.g4", g1);
		ExecutedState executedState = execParser(g2, "r", "R2", debug, tempDir, false);
		assertEquals("", executedState.errors);
	}

	private static void checkGrammarSemanticsWarning(ErrorQueue equeue, GrammarSemanticsMessage expectedMessage) {
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.warnings.size(); i++) {
			ANTLRMessage m = equeue.warnings.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertNotNull(foundMsg, "no error; "+expectedMessage.getErrorType()+" expected");
		assertTrue(foundMsg instanceof GrammarSemanticsMessage, "error is not a GrammarSemanticsMessage");
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if ( equeue.size()!=1 ) {
			System.err.println(equeue);
		}
	}

	public static <K extends Comparable<? super K>,V> LinkedHashMap<K,V> sort(Map<K,V> data) {
		LinkedHashMap<K,V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}

	private static void checkGeneration(String grammar, String slaveGrammar) {
		checkGeneration(grammar, slaveGrammar, null, null, false);
	}

	private static void checkGeneration(String grammar, Path workingDirectory, String[] extraOptions) {
		checkGeneration(grammar, null, workingDirectory, extraOptions, false);
	}

	private static void checkGeneration(String grammar, String slaveGrammar, Path workingDirectory, String[] extraOptions, boolean saveTestDir) {
		GeneratedState state = generate(grammar, slaveGrammar != null ? new String[] {slaveGrammar} : null,
				workingDirectory, extraOptions, saveTestDir);
		if (state.containsErrors())
			fail(state.getErrorMessage());
	}

	private static void checkCompilation(String grammarStr, Path tempDirPath) {
		RunOptions runOptions = RunOptions.createCompilationOptions(new String[] {grammarStr}, null,
				false, false, null, null);
		try (JavaRunner runner = new JavaRunner(tempDirPath, false)) {
			State state = runner.run(runOptions);
			if (state.containsErrors())
				fail(state.getErrorMessage());
		}
	}
}
