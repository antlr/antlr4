package org.antlr.v4.test.runtime;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.test.runtime.csharp.CSharpRunner;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.antlr.v4.test.runtime.swift.SwiftRunner;
import org.antlr.v4.tool.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.joinLines;
import static org.junit.jupiter.api.Assertions.fail;

/** Run a lexer/parser and dump ATN debug/trace information
 *
 *  java org.antlr.v4.test.runtime.TraceATN [X.g4|XParser.g4 XLexer.g4] startRuleName -target [Java|Cpp|...] inputFileName
 *
 *
 * In preparation, run this so we get right jars before trying this script:
 *
 * cd ANTLR-ROOT-DIR
 * mvn install -DskipTests=true
 * cd runtime-tests
 * mvn install jar:test-jar -DskipTests=true
 *
 * Run shell script with
 *
 * scripts/traceatn.sh /tmp/JSON.g4 json /tmp/foo.json
 *
 * Here is scripts/traceatn.sh:
 *
 * export ANTLRJAR=/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.2-SNAPSHOT/antlr4-4.11.2-SNAPSHOT-complete.jar
 * export TESTJAR=/Users/parrt/.m2/repository/org/antlr/antlr4-runtime-testsuite/4.11.2-SNAPSHOT/antlr4-runtime-testsuite-4.11.2-SNAPSHOT-tests.jar
 * java -classpath $ANTLRJAR:$TESTJAR org.antlr.v4.test.runtime.TraceATN $@
 */
public class TraceATN {
	protected static class IgnoreTokenVocabGrammar extends Grammar {
		public IgnoreTokenVocabGrammar(String fileName,
									   String grammarText,
									   Grammar tokenVocabSource,
									   ANTLRToolListener listener)
				throws RecognitionException
		{
			super(fileName, grammarText, tokenVocabSource, listener);
		}

		@Override
		public void importTokensFromTokensFile() {
			// don't try to import tokens files; must give me both grammars if split
		}
	}

	protected String grammarFileName;
	protected String parserGrammarFileName;
	protected String lexerGrammarFileName;
	protected String startRuleName;
	protected String inputFileName;
	protected String targetName = "Java";
	protected String encoding;

	public TraceATN(String[] args) {
		if ( args.length < 2 ) {
			System.err.println("java org.antlr.v4.test.runtime.TraceATN [X.g4|XParser.g4 XLexer.g4] startRuleName\n" +
					"    [-encoding encodingname] -target (Java|Cpp|...) input-filename");
			System.err.println("Omitting input-filename makes program read from stdin.");
			return;
		}
		int i=0;
		grammarFileName = args[i];
		i++;
		if ( args[i].endsWith(".g4") ) {
			parserGrammarFileName = grammarFileName;
			lexerGrammarFileName = args[i];
			i++;
			grammarFileName = null;

			if ( parserGrammarFileName.toLowerCase().endsWith("lexer.g4") ) { // swap
				String save = parserGrammarFileName;
				parserGrammarFileName = lexerGrammarFileName;
				lexerGrammarFileName = save;
			}
		}
		startRuleName = args[i];
		i++;
		while ( i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // input file name
				inputFileName = arg;
			}
			else if ( arg.equals("-encoding") ) {
				if ( i>=args.length ) {
					System.err.println("missing encoding on -encoding");
					return;
				}
				encoding = args[i];
				i++;
			}
			else if ( arg.equals("-target") ) {
				if ( i>=args.length ) {
					System.err.println("missing name on -target");
					return;
				}
				targetName = args[i];
				i++;
			}
		}
	}

	public String test(RuntimeTestDescriptor descriptor, RuntimeRunner runner, String targetName) {
		FileUtils.mkdir(runner.getTempDirPath());

		String grammarName = descriptor.grammarName;
		String grammar = descriptor.grammar;

		String lexerName, parserName;
		boolean useListenerOrVisitor;
		String superClass;
		if (descriptor.testType == GrammarType.Parser || descriptor.testType == GrammarType.CompositeParser) {
			lexerName = grammarName + "Lexer";
			parserName = grammarName + "Parser";
			useListenerOrVisitor = true;
			if ( targetName!=null && targetName.equals("Java") ) {
				superClass = JavaRunner.runtimeTestParserName;
			}
			else {
				superClass = null;
			}
		}
		else {
			lexerName = grammarName;
			parserName = null;
			useListenerOrVisitor = false;
			if (targetName.equals("Java")) {
				superClass = JavaRunner.runtimeTestLexerName;
			}
			else {
				superClass = null;
			}
		}

		RunOptions runOptions = new RunOptions(grammarName + ".g4",
				grammar,
				parserName,
				lexerName,
				useListenerOrVisitor,
				useListenerOrVisitor,
				descriptor.startRule,
				descriptor.input,
				false,
				descriptor.showDiagnosticErrors,
				descriptor.traceATN,
				descriptor.showDFA,
				Stage.Execute,
				false,
				targetName,
				superClass
		);

		State result = runner.run(runOptions);

		ExecutedState executedState;
		if (result instanceof ExecutedState) {
			executedState = (ExecutedState)result;
			if (executedState.exception != null) {
				return result.getErrorMessage();
			}
			return executedState.output;
		}
		else {
			return result.getErrorMessage();
		}
	}

	void execParse() throws Exception {
		if ( grammarFileName==null && (parserGrammarFileName==null && lexerGrammarFileName==null) ) {
			System.err.println("No grammar specified");
			return;
		}

		if ( inputFileName==null ) {
			System.err.println("No input file specified");
			return;
		}

		String grammarName =
				grammarFileName.substring(grammarFileName.lastIndexOf('/')+1, grammarFileName.length());
		grammarName = grammarName.substring(0, grammarName.indexOf(".g4"));
		if ( grammarFileName!=null ) {
			String grammar = new String(Files.readAllBytes(Paths.get(grammarFileName)));

			String input = new String(Files.readAllBytes(Paths.get(inputFileName)));

			RuntimeTestDescriptor descriptor = new RuntimeTestDescriptor(
					GrammarType.CompositeParser,
					"TraceATN-" + grammarFileName,
					"",
					input,
					"",
					"",
					startRuleName,
					grammarName,
					grammar,
					null,
					false,
					true,
					false,
					null,
					null);

			RuntimeRunner runner = getRunner(targetName);

			String result = test(descriptor, runner, targetName);
			System.out.println(result);
		}
	}

	public static RuntimeRunner getRunner(String targetName) throws Exception {
		Class<?> cl = Class.forName("org.antlr.v4.test.runtime."+
				targetName.toLowerCase() + "." + targetName + "Runner");
		return (RuntimeRunner)cl.newInstance();
	}

	public static void main(String[] args) throws Exception {
		TraceATN I = new TraceATN(args);
		I.execParse();
	}
}
