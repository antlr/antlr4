/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.*;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;

public class BaseJavaTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "Java";
	}

	/**
	 * When the {@code antlr.testinprocess} runtime property is set to
	 * {@code true}, the test suite will attempt to load generated classes into
	 * the test process for direct execution rather than invoking the JVM in a
	 * new process for testing.
	 * <p>
	 * <p>
	 * In-process testing results in a substantial performance improvement, but
	 * some test environments created by IDEs do not support the mechanisms
	 * currently used by the tests to dynamically load compiled code. Therefore,
	 * the default behavior (used in all other cases) favors reliable
	 * cross-system test execution by executing generated test code in a
	 * separate process.</p>
	 */
	public static final boolean TEST_IN_SAME_PROCESS = Boolean.parseBoolean(System.getProperty("antlr.testinprocess"));

	/**
	 * Build up the full classpath we need, including the surefire path (if present)
	 */
	public static final String CLASSPATH = System.getProperty("java.class.path");

	@Override
	protected String getPropertyPrefix() {
		return "antrl4-java";
	}

	protected String load(String fileName, String encoding)
		throws IOException {
		if ( fileName==null ) {
			return null;
		}

		String fullFileName = getClass().getPackage().getName().replace('.', '/')+'/'+fileName;
		int size = 65000;
		InputStreamReader isr;
		InputStream fis = getClass().getClassLoader().getResourceAsStream(fullFileName);
		if ( encoding!=null ) {
			isr = new InputStreamReader(fis, encoding);
		}
		else {
			isr = new InputStreamReader(fis);
		}
		try {
			char[] data = new char[size];
			int n = isr.read(data);
			return new String(data, 0, n);
		} finally {
			isr.close();
		}
	}

	/**
	 * Wow! much faster than compiling outside of VM. Finicky though.
	 * Had rules called r and modulo. Wouldn't compile til I changed to 'a'.
	 */
	protected boolean compile(String... fileNames) {
		List<File> files = new ArrayList<File>();
		for (String fileName : fileNames) {
			File f = new File(getTempTestDir(), fileName);
			files.add(f);
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager =
			compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits =
			fileManager.getJavaFileObjectsFromFiles(files);

		Iterable<String> compileOptions =
			Arrays.asList("-g", "-source", "1.8", "-target", "1.8", "-implicit:class", "-Xlint:-options", "-d", getTempDirPath(), "-cp", getTempDirPath() + PATH_SEP + CLASSPATH);

		JavaCompiler.CompilationTask task =
			compiler.getTask(null, fileManager, null, compileOptions, null,
			                 compilationUnits);
		boolean ok = task.call();

		try {
			fileManager.close();
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}

		return ok;
	}

	protected String execLexer(String grammarFileName,
	                           String grammarStr,
	                           String lexerName,
	                           String input) {
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	@Override
	public String execLexer(String grammarFileName,
	                        String grammarStr,
	                        String lexerName,
	                        String input,
	                        boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr,
		                                                null,
		                                                lexerName);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		compile("Test.java");
		return execClass("Test");
	}

	public ParseTree execParser(String startRuleName, String input,
	                            String parserName, String lexerName)
		throws Exception
	{
		Pair<Parser, Lexer> pl = getParserAndLexer(input, parserName, lexerName);
		Parser parser = pl.a;
		return execStartRule(startRuleName, parser);
	}

	public ParseTree execStartRule(String startRuleName, Parser parser)
		throws IllegalAccessException, InvocationTargetException,
		NoSuchMethodException {
		Method startRule;
		Object[] args = null;
		try {
			startRule = parser.getClass().getMethod(startRuleName);
		} catch (NoSuchMethodException nsme) {
			// try with int _p arg for recursive func
			startRule = parser.getClass().getMethod(startRuleName, int.class);
			args = new Integer[]{0};
		}
		ParseTree result = (ParseTree) startRule.invoke(parser, args);
//		System.out.println("parse tree = "+result.toStringTree(parser));
		return result;
	}

	public Pair<Parser, Lexer> getParserAndLexer(String input,
	                                             String parserName, String lexerName)
		throws Exception {
		final Class<? extends Lexer> lexerClass = loadLexerClassFromTempDir(lexerName);
		final Class<? extends Parser> parserClass = loadParserClassFromTempDir(parserName);

		ANTLRInputStream in = new ANTLRInputStream(new StringReader(input));

		Class<? extends Lexer> c = lexerClass.asSubclass(Lexer.class);
		Constructor<? extends Lexer> ctor = c.getConstructor(CharStream.class);
		Lexer lexer = ctor.newInstance(in);

		Class<? extends Parser> pc = parserClass.asSubclass(Parser.class);
		Constructor<? extends Parser> pctor = pc.getConstructor(TokenStream.class);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Parser parser = pctor.newInstance(tokens);
		return new Pair<Parser, Lexer>(parser, lexer);
	}

	public Class<?> loadClassFromTempDir(String name) throws Exception {
		ClassLoader loader =
			new URLClassLoader(new URL[]{getTempTestDir().toURI().toURL()},
			                   ClassLoader.getSystemClassLoader());
		return loader.loadClass(name);
	}

	public Class<? extends Lexer> loadLexerClassFromTempDir(String name) throws Exception {
		return loadClassFromTempDir(name).asSubclass(Lexer.class);
	}

	public Class<? extends Parser> loadParserClassFromTempDir(String name) throws Exception {
		return loadClassFromTempDir(name).asSubclass(Parser.class);
	}

	@Override
	public String execParser(String grammarFileName,
	                         String grammarStr,
	                         String parserName,
	                         String lexerName,
	                         String listenerName,
	                         String visitorName,
	                         String startRuleName,
	                         String input,
	                         boolean showDiagnosticErrors)
	{
		return execParser(grammarFileName, grammarStr, parserName, lexerName,
		                  listenerName, visitorName, startRuleName, input, showDiagnosticErrors, false);
	}

	/** ANTLR isn't thread-safe to process grammars so we use a global lock for testing */
	public static final Object antlrLock = new Object();

	public String execParser(String grammarFileName,
	                         String grammarStr,
	                         String parserName,
	                         String lexerName,
	                         String listenerName,
	                         String visitorName,
	                         String startRuleName,
	                         String input,
	                         boolean showDiagnosticErrors,
	                         boolean profile)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr,
		                                                parserName,
		                                                lexerName,
		                                                "-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, profile, false,
				listenerName != null, visitorName != null);
		compile("Test.java");
		return execClass("Test");
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													String... extraOptions)
	{
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean defaultListener,
													String... extraOptions)
	{
		ErrorQueue equeue =
			BaseRuntimeTest.antlrOnString(getTempDirPath(), "Java", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		return compile(getGeneratedFiles(grammarFileName, lexerName, parserName, extraOptions).toArray(new String[0]));
	}

	public String execClass(String className) {
		if (TEST_IN_SAME_PROCESS) {
			try {
				ClassLoader loader = new URLClassLoader(new URL[] { getTempTestDir().toURI().toURL() }, ClassLoader.getSystemClassLoader());
                final Class<?> mainClass = (Class<?>)loader.loadClass(className);
				final Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
				PipedInputStream stdoutIn = new PipedInputStream();
				PipedInputStream stderrIn = new PipedInputStream();
				PipedOutputStream stdoutOut = new PipedOutputStream(stdoutIn);
				PipedOutputStream stderrOut = new PipedOutputStream(stderrIn);
				StreamVacuum stdoutVacuum = new StreamVacuum(stdoutIn);
				StreamVacuum stderrVacuum = new StreamVacuum(stderrIn);

				PrintStream originalOut = System.out;
				System.setOut(new PrintStream(stdoutOut));
				try {
					PrintStream originalErr = System.err;
					try {
						System.setErr(new PrintStream(stderrOut));
						stdoutVacuum.start();
						stderrVacuum.start();
						mainMethod.invoke(null, (Object)new String[] { new File(getTempTestDir(), "input").getAbsolutePath() });
					}
					finally {
						System.setErr(originalErr);
					}
				}
				finally {
					System.setOut(originalOut);
				}

				stdoutOut.close();
				stderrOut.close();
				stdoutVacuum.join();
				stderrVacuum.join();
				String output = stdoutVacuum.toString();
				if ( output.length()==0 ) {
					output = null;
				}
				if ( stderrVacuum.toString().length()>0 ) {
					setParseErrors(stderrVacuum.toString());
				}
				return output;
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		try {
			String[] args = new String[] {
				"java", "-classpath", getTempDirPath() + PATH_SEP + CLASSPATH,
				"-Dfile.encoding=UTF-8",
				className, new File(getTempTestDir(), "input").getAbsolutePath()
			};
//			String cmdLine = Utils.join(args, " ");
//			System.err.println("execParser: "+cmdLine);
			Process process =
				Runtime.getRuntime().exec(args, null, getTempTestDir());
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();
			if ( output.length()==0 ) {
				output = null;
			}
			if ( stderrVacuum.toString().length()>0 ) {
				setParseErrors(stderrVacuum.toString());
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	public void checkRuleATN(Grammar g, String ruleName, String expecting) {
//		DOTGenerator dot = new DOTGenerator(g);
//		System.out.println(dot.getDOT(g.atn.ruleToStartState[g.getRule(ruleName).index]));

		Rule r = g.getRule(ruleName);
		ATNState startState = g.getATN().ruleToStartState[r.index];
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		//System.out.print(result);
		assertEquals(expecting, result);
	}

	public void testActions(String templates, String actionName, String action, String expected) throws org.antlr.runtime.RecognitionException {
		int lp = templates.indexOf('(');
		String name = templates.substring(0, lp);
		STGroup group = new STGroupString(templates);
		ST st = group.getInstanceOf(name);
		st.add(actionName, action);
		String grammar = st.render();
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(grammar, equeue);
		if ( g.ast!=null && !g.ast.hasErrors ) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g);
			g.atn = factory.createATN();

            AnalysisPipeline anal = new AnalysisPipeline(g);
            anal.process();

			CodeGenerator gen = CodeGenerator.create(g);
			ST outputFileST = gen.generateParser(false);
			String output = outputFileST.render();
			//System.out.println(output);
			String b = "#" + actionName + "#";
			int start = output.indexOf(b);
			String e = "#end-" + actionName + "#";
			int end = output.indexOf(e);
			String snippet = output.substring(start+b.length(),end);
			assertEquals(expected, snippet);
		}
		if ( equeue.size()>0 ) {
//			System.err.println(equeue.toString());
		}
	}



	protected void checkGrammarSemanticsWarning(ErrorQueue equeue,
											    GrammarSemanticsMessage expectedMessage)
		throws Exception
	{
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.warnings.size(); i++) {
			ANTLRMessage m = equeue.warnings.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; "+expectedMessage.getErrorType()+" expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
				   foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if ( equeue.size()!=1 ) {
			System.err.println(equeue);
		}
	}

	public List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}
}
