/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.antlr.v4.test.runtime.FileUtils.deleteDirectory;
import static org.antlr.v4.test.runtime.Generator.antlrOnString;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.TempDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ToolTestUtils {
	public static ExecutedState execLexer(String grammarFileName, String grammarStr, String lexerName, String input) {
		return execLexer(grammarFileName, grammarStr, lexerName, input, null, false);
	}

	public static ExecutedState execLexer(String grammarFileName, String grammarStr, String lexerName, String input,
									  Path tempDir, boolean saveTestDir) {
		return execRecognizer(grammarFileName, grammarStr, null, lexerName,
				null, input, false, tempDir, saveTestDir);
	}

	public static ExecutedState execParser(String grammarFileName, String grammarStr,
									   String parserName, String lexerName, String startRuleName,
									   String input, boolean showDiagnosticErrors
	) {
		return execParser(grammarFileName, grammarStr, parserName, lexerName, startRuleName,
				input, showDiagnosticErrors, null);
	}

	public static ExecutedState execParser(String grammarFileName, String grammarStr,
									String parserName, String lexerName, String startRuleName,
									String input, boolean showDiagnosticErrors, Path workingDir
	) {
		return execRecognizer(grammarFileName, grammarStr, parserName, lexerName,
				startRuleName, input, showDiagnosticErrors, workingDir, false);
	}

	private static ExecutedState execRecognizer(String grammarFileName, String grammarStr,
										 String parserName, String lexerName, String startRuleName,
										 String input, boolean showDiagnosticErrors,
										 Path workingDir, boolean saveTestDir) {
		RunOptions runOptions = createOptionsForJavaToolTests(grammarFileName, grammarStr, parserName, lexerName,
				false, true, startRuleName, input,
				false, showDiagnosticErrors, Stage.Execute, false);
		try (JavaRunner runner = new JavaRunner(workingDir, saveTestDir)) {
			State result = runner.run(runOptions);
			if (!(result instanceof ExecutedState)) {
				fail(result.getErrorMessage());
			}
			return  (ExecutedState) result;
		}
	}

	public static RunOptions createOptionsForJavaToolTests(
			String grammarFileName, String grammarStr, String parserName, String lexerName,
			boolean useListener, boolean useVisitor, String startRuleName,
			String input, boolean profile, boolean showDiagnosticErrors,
			Stage endStage, boolean returnObject
	) {
		return new RunOptions(grammarFileName, grammarStr, parserName, lexerName, useListener, useVisitor, startRuleName,
				input, profile, showDiagnosticErrors, false, endStage, returnObject, "Java",
				JavaRunner.runtimeTestParserName);
	}

	public static void testErrors(String[] pairs, boolean printTree) {
		for (int i = 0; i < pairs.length; i += 2) {
			String grammarStr = pairs[i];
			String expect = pairs[i + 1];

			String[] lines = grammarStr.split("\n");
			String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);

			String tempDirName = "AntlrTestErrors-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
			String tempTestDir = Paths.get(TempDirectory, tempDirName).toString();

			try {
				ErrorQueue equeue = antlrOnString(tempTestDir, null, fileName, grammarStr, false);

				String actual = equeue.toString(true);
				actual = actual.replace(tempTestDir + File.separator, "");
				String msg = grammarStr;
				msg = msg.replace("\n", "\\n");
				msg = msg.replace("\r", "\\r");
				msg = msg.replace("\t", "\\t");

				assertEquals(expect, actual, "error in: " + msg);
			}
			finally {
				try {
					deleteDirectory(new File(tempTestDir));
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static String getFilenameFromFirstLineOfGrammar(String line) {
		String fileName = "A" + Tool.GRAMMAR_EXTENSION;
		int grIndex = line.lastIndexOf("grammar");
		int semi = line.lastIndexOf(';');
		if ( grIndex>=0 && semi>=0 ) {
			int space = line.indexOf(' ', grIndex);
			fileName = line.substring(space+1, semi)+Tool.GRAMMAR_EXTENSION;
		}
		if ( fileName.length()==Tool.GRAMMAR_EXTENSION.length() ) fileName = "A" + Tool.GRAMMAR_EXTENSION;
		return fileName;
	}

	public static List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}

	public static String load(String fileName)
			throws IOException {
		if ( fileName==null ) {
			return null;
		}

		String fullFileName = ToolTestUtils.class.getPackage().getName().replace('.', '/')+'/'+fileName;
		int size = 65000;
		InputStream fis = ToolTestUtils.class.getClassLoader().getResourceAsStream(fullFileName);
		try (InputStreamReader isr = new InputStreamReader(fis)) {
			char[] data = new char[size];
			int n = isr.read(data);
			return new String(data, 0, n);
		}
	}

	public static ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f = g.isLexer() ? new LexerATNFactory((LexerGrammar) g) : new ParserATNFactory(g);

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if ( useSerializer ) {
			// sets some flags in ATN
			IntegerList serialized = ATNSerializer.getSerialized(atn);
			return new ATNDeserializer().deserialize(serialized.toArray());
		}

		return atn;
	}

	public static void semanticProcess(Grammar g) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
//			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp, false);
				}
			}
		}
	}

	public static IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
		ANTLRInputStream in = new ANTLRInputStream(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while ( ttype!= Token.EOF );
		return tokenTypes;
	}
}
