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
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.*;

public class BaseJavaToolTest extends BaseJavaTest {
	protected ExecutedState execLexer(String grammarFileName, String grammarStr, String lexerName, String input) {
		return execRecognizer(grammarFileName, grammarStr, null, lexerName, null, input, false);
	}

	protected ExecutedState execParser(String grammarFileName, String grammarStr,
									String parserName, String lexerName, String startRuleName,
									String input, boolean showDiagnosticErrors
	) {
		return execRecognizer(grammarFileName, grammarStr, parserName, lexerName,
				startRuleName, input, showDiagnosticErrors);
	}

	private ExecutedState execRecognizer(String grammarFileName, String grammarStr,
										 String parserName, String lexerName, String startRuleName,
										 String input, boolean showDiagnosticErrors) {
		RunOptions runOptions = RunOptions.createOptionsForJavaToolTests(grammarFileName, grammarStr, parserName, lexerName,
				false, true, startRuleName, input,
				false, showDiagnosticErrors, Stage.Execute, false);
		State result = run(runOptions);
		if (!(result instanceof ExecutedState)) {
			fail(result.getErrorMessage());
		}
		return  (ExecutedState) result;
	}

	public void testErrors(String[] pairs, boolean printTree) {
        for (int i = 0; i < pairs.length; i+=2) {
            String grammarStr = pairs[i];
            String expect = pairs[i+1];

			String[] lines = grammarStr.split("\n");
			String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);
			ErrorQueue equeue = Generator.antlrOnString(getTempDirPath(), null, fileName, grammarStr, false); // use default language target in case test overrides

			String actual = equeue.toString(true);
			actual = actual.replace(getTempDirPath() + File.separator, "");
//			System.err.println(actual);
			String msg = grammarStr;
			msg = msg.replace("\n","\\n");
			msg = msg.replace("\r","\\r");
			msg = msg.replace("\t","\\t");

            assertEquals("error in: "+msg,expect,actual);
        }
    }

	public String getFilenameFromFirstLineOfGrammar(String line) {
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

	public List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}

	protected String load(String fileName)
			throws IOException {
		if ( fileName==null ) {
			return null;
		}

		String fullFileName = getClass().getPackage().getName().replace('.', '/')+'/'+fileName;
		int size = 65000;
		InputStream fis = getClass().getClassLoader().getResourceAsStream(fullFileName);
		try (InputStreamReader isr = new InputStreamReader(fis)) {
			char[] data = new char[size];
			int n = isr.read(data);
			return new String(data, 0, n);
		}
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
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

	protected void semanticProcess(Grammar g) {
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

	protected static IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
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
