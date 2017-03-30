/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import org.antlr.v4.Tool;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.RuntimeTestSupport;
import org.antlr.v4.test.runtime.StreamVacuum;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertArrayEquals;

public class BaseJavaTest implements RuntimeTestSupport {
	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");

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
	 * When the {@code antlr.preserve-test-dir} runtime property is set to
	 * {@code true}, the temporary directories created by the test run will not
	 * be removed at the end of the test run, even for tests that completed
	 * successfully.
	 * <p>
	 * <p>
	 * The default behavior (used in all other cases) is removing the temporary
	 * directories for all tests which completed successfully, and preserving
	 * the directories for tests which failed.</p>
	 */
	public static final boolean PRESERVE_TEST_DIR = true; //Boolean.parseBoolean(System.getProperty("antlr.preserve-test-dir"));

	/**
	 * The base test directory is the directory where generated files get placed
	 * during unit test execution.
	 * <p>
	 * <p>
	 * The default value for this property is the {@code java.io.tmpdir} system
	 * property, and can be overridden by setting the
	 * {@code antlr.java-test-dir} property to a custom location. Note that the
	 * {@code antlr.java-test-dir} property directly affects the
	 * {@link #CREATE_PER_TEST_DIRECTORIES} value as well.</p>
	 */
	public static final String BASE_TEST_DIR;

	/**
	 * When {@code true}, a temporary directory will be created for each test
	 * executed during the test run.
	 * <p>
	 * <p>
	 * This value is {@code true} when the {@code antlr.java-test-dir} system
	 * property is set, and otherwise {@code false}.</p>
	 */
	public static final boolean CREATE_PER_TEST_DIRECTORIES;

	static {
		String baseTestDir = System.getProperty("antlr.java-test-dir");
		boolean perTestDirectories = false;
		if ( baseTestDir==null || baseTestDir.isEmpty() ) {
			baseTestDir = System.getProperty("java.io.tmpdir");
			perTestDirectories = true;
		}

		if ( !new File(baseTestDir).isDirectory() ) {
			throw new UnsupportedOperationException("The specified base test directory does not exist: "+baseTestDir);
		}

		BASE_TEST_DIR = baseTestDir;
		CREATE_PER_TEST_DIRECTORIES = perTestDirectories;
	}

	/**
	 * Build up the full classpath we need, including the surefire path (if present)
	 */
	public static final String CLASSPATH = System.getProperty("java.class.path");

	public String tmpdir = null;

	/**
	 * If error during parser execution, store stderr here; can't return
	 * stdout and stderr.  This doesn't trap errors from running antlr.
	 */
	protected String stderrDuringParse;

	/**
	 * Errors found while running antlr
	 */
	protected StringBuilder antlrToolErrors;

	@Override
	public void testSetUp() throws Exception {
//		STGroup.verbose = true;
//		System.err.println("testSetUp "+Thread.currentThread().getName());
		if ( CREATE_PER_TEST_DIRECTORIES ) {
			// new output dir for each test
			String threadName = Thread.currentThread().getName();
			String testDirectory = getClass().getSimpleName()+"-"+threadName+"-"+System.nanoTime();
			tmpdir = new File(BASE_TEST_DIR, testDirectory).getAbsolutePath();
		}
		else {
			tmpdir = new File(BASE_TEST_DIR).getAbsolutePath();
			if ( !PRESERVE_TEST_DIR && new File(tmpdir).exists() ) {
				eraseFiles();
			}
		}
		antlrToolErrors = new StringBuilder();
	}

	@Override
	public void testTearDown() throws Exception {
	}

	@Override
	public String getTmpDir() {
		return tmpdir;
	}

	@Override
	public String getStdout() {
		return null;
	}

	@Override
	public String getParseErrors() {
		return stderrDuringParse;
	}

	@Override
	public String getANTLRToolErrors() {
		if ( antlrToolErrors.length()==0 ) {
			return null;
		}
		return antlrToolErrors.toString();
	}

	protected org.antlr.v4.Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f;
			if ( g.isLexer() ) {
				f = new LexerATNFactory((LexerGrammar) g);
			}
			else {
				f = new ParserATNFactory(g);
			}

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if ( useSerializer ) {
			char[] serialized = ATNSerializer.getSerializedAsChars(atn);
			return new ATNDeserializer().deserialize(serialized);
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

	public DFA createDFA(Grammar g, DecisionState s) {
//		PredictionDFAFactory conv = new PredictionDFAFactory(g, s);
//		DFA dfa = conv.createDFA();
//		conv.issueAmbiguityWarnings();
//		System.out.print("DFA="+dfa);
//		return dfa;
		return null;
	}

//	public void minimizeDFA(DFA dfa) {
//		DFAMinimizer dmin = new DFAMinimizer(dfa);
//		dfa.minimized = dmin.minimize();
//	}

	IntegerList getTypesFromString(Grammar g, String expecting) {
		IntegerList expectingTokenTypes = new IntegerList();
		if ( expecting!=null && !expecting.trim().isEmpty() ) {
			for (String tname : expecting.replace(" ", "").split(",")) {
				int ttype = g.getTokenType(tname);
				expectingTokenTypes.add(ttype);
			}
		}
		return expectingTokenTypes;
	}

	public IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
		ANTLRInputStream in = new ANTLRInputStream(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while ( ttype!=Token.EOF );
		return tokenTypes;
	}

	public List<String> getTokenTypes(LexerGrammar lg,
	                                  ATN atn,
	                                  CharStream input) {
		LexerATNSimulator interp = new LexerATNSimulator(atn, new DFA[]{new DFA(atn.modeToStartState.get(Lexer.DEFAULT_MODE))}, null);
		List<String> tokenTypes = new ArrayList<String>();
		int ttype;
		boolean hitEOF = false;
		do {
			if ( hitEOF ) {
				tokenTypes.add("EOF");
				break;
			}
			int t = input.LA(1);
			ttype = interp.match(input, Lexer.DEFAULT_MODE);
			if ( ttype==Token.EOF ) {
				tokenTypes.add("EOF");
			}
			else {
				tokenTypes.add(lg.typeToTokenList.get(ttype));
			}

			if ( t==IntStream.EOF ) {
				hitEOF = true;
			}
		} while ( ttype!=Token.EOF );
		return tokenTypes;
	}

	List<ANTLRMessage> checkRuleDFA(String gtext, String ruleName, String expecting)
		throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(gtext, equeue);
		ATN atn = createATN(g, false);
		ATNState s = atn.ruleToStartState[g.getRule(ruleName).index];
		if ( s==null ) {
			System.err.println("no such rule: "+ruleName);
			return null;
		}
		ATNState t = s.transition(0).target;
		if ( !(t instanceof DecisionState) ) {
			System.out.println(ruleName+" has no decision");
			return null;
		}
		DecisionState blk = (DecisionState) t;
		checkRuleDFA(g, blk, expecting);
		return equeue.all;
	}

	List<ANTLRMessage> checkRuleDFA(String gtext, int decision, String expecting)
		throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(gtext, equeue);
		ATN atn = createATN(g, false);
		DecisionState blk = atn.decisionToState.get(decision);
		checkRuleDFA(g, blk, expecting);
		return equeue.all;
	}

	void checkRuleDFA(Grammar g, DecisionState blk, String expecting)
		throws Exception {
		DFA dfa = createDFA(g, blk);
		String result = null;
		if ( dfa!=null ) result = dfa.toString();
		assertEquals(expecting, result);
	}

	List<ANTLRMessage> checkLexerDFA(String gtext, String expecting)
		throws Exception {
		return checkLexerDFA(gtext, LexerGrammar.DEFAULT_MODE_NAME, expecting);
	}

	List<ANTLRMessage> checkLexerDFA(String gtext, String modeName, String expecting)
		throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		LexerGrammar g = new LexerGrammar(gtext, equeue);
		g.atn = createATN(g, false);
//		LexerATNToDFAConverter conv = new LexerATNToDFAConverter(g);
//		DFA dfa = conv.createDFA(modeName);
//		g.setLookaheadDFA(0, dfa); // only one decision to worry about
//
//		String result = null;
//		if ( dfa!=null ) result = dfa.toString();
//		assertEquals(expecting, result);
//
//		return equeue.all;
		return null;
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
			File f = new File(tmpdir, fileName);
			files.add(f);
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//		DiagnosticCollector<JavaFileObject> diagnostics =
//			new DiagnosticCollector<JavaFileObject>();

		StandardJavaFileManager fileManager =
			compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits =
			fileManager.getJavaFileObjectsFromFiles(files);

		Iterable<String> compileOptions =
			Arrays.asList("-g", "-source", "1.6", "-target", "1.6", "-implicit:class", "-Xlint:-options", "-d", tmpdir, "-cp", tmpdir+pathSep+CLASSPATH);

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
		writeFile(tmpdir, "input", input);
		writeLexerTestFile(lexerName, showDFA);
		compile("Test.java");
		String output = execClass("Test");
		return output;
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
		Method startRule = null;
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
			new URLClassLoader(new URL[]{new File(tmpdir).toURI().toURL()},
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
		writeFile(tmpdir, "input", input);
		return rawExecRecognizer(parserName,
								 lexerName,
								 startRuleName,
								 showDiagnosticErrors,
								 profile);
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
			BaseRuntimeTest.antlrOnString(getTmpDir(), "Java", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if ( lexerName!=null ) {
			files.add(lexerName+".java");
		}
		if ( parserName!=null ) {
			files.add(parserName+".java");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName+"Listener.java");
				files.add(grammarName+"BaseListener.java");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName+"Visitor.java");
				files.add(grammarName+"BaseVisitor.java");
			}
		}
		boolean allIsWell = compile(files.toArray(new String[files.size()]));
		return allIsWell;
	}

	protected String rawExecRecognizer(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug,
									   boolean profile)
	{
        this.stderrDuringParse = null;
		if ( parserName==null ) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeTestFile(parserName,
						  lexerName,
						  parserStartRuleName,
						  debug,
						  profile);
		}

		compile("Test.java");
		return execClass("Test");
	}

	public String execRecognizer() {
		return execClass("Test");
	}

	public String execClass(String className) {
		if (TEST_IN_SAME_PROCESS) {
			try {
				ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
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
						mainMethod.invoke(null, (Object)new String[] { new File(tmpdir, "input").getAbsolutePath() });
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
					this.stderrDuringParse = stderrVacuum.toString();
				}
				return output;
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		try {
			String[] args = new String[] {
				"java", "-classpath", tmpdir+pathSep+CLASSPATH,
				"-Dfile.encoding=UTF-8",
				className, new File(tmpdir, "input").getAbsolutePath()
			};
//			String cmdLine = Utils.join(args, " ");
//			System.err.println("execParser: "+cmdLine);
			Process process =
				Runtime.getRuntime().exec(args, null, new File(tmpdir));
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
				this.stderrDuringParse = stderrVacuum.toString();
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

//	void ambig(List<Message> msgs, int[] expectedAmbigAlts, String expectedAmbigInput)
//		throws Exception
//	{
//		ambig(msgs, 0, expectedAmbigAlts, expectedAmbigInput);
//	}

//	void ambig(List<Message> msgs, int i, int[] expectedAmbigAlts, String expectedAmbigInput)
//		throws Exception
//	{
//		List<Message> amsgs = getMessagesOfType(msgs, AmbiguityMessage.class);
//		AmbiguityMessage a = (AmbiguityMessage)amsgs.get(i);
//		if ( a==null ) assertNull(expectedAmbigAlts);
//		else {
//			assertEquals(a.conflictingAlts.toString(), Arrays.toString(expectedAmbigAlts));
//		}
//		assertEquals(expectedAmbigInput, a.input);
//	}

//	void unreachable(List<Message> msgs, int[] expectedUnreachableAlts)
//		throws Exception
//	{
//		unreachable(msgs, 0, expectedUnreachableAlts);
//	}

//	void unreachable(List<Message> msgs, int i, int[] expectedUnreachableAlts)
//		throws Exception
//	{
//		List<Message> amsgs = getMessagesOfType(msgs, UnreachableAltsMessage.class);
//		UnreachableAltsMessage u = (UnreachableAltsMessage)amsgs.get(i);
//		if ( u==null ) assertNull(expectedUnreachableAlts);
//		else {
//			assertEquals(u.conflictingAlts.toString(), Arrays.toString(expectedUnreachableAlts));
//		}
//	}

	List<ANTLRMessage> getMessagesOfType(List<ANTLRMessage> msgs, Class<? extends ANTLRMessage> c) {
		List<ANTLRMessage> filtered = new ArrayList<ANTLRMessage>();
		for (ANTLRMessage m : msgs) {
			if ( m.getClass() == c ) filtered.add(m);
		}
		return filtered;
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

			CodeGenerator gen = new CodeGenerator(g);
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

	protected void checkGrammarSemanticsError(ErrorQueue equeue,
											  GrammarSemanticsMessage expectedMessage)
		throws Exception
	{
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
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

	protected void checkError(ErrorQueue equeue,
							  ANTLRMessage expectedMessage)
		throws Exception
	{
		//System.out.println("errors="+equeue);
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertTrue("no error; "+expectedMessage.getErrorType()+" expected", !equeue.errors.isEmpty());
		assertTrue("too many errors; "+equeue.errors, equeue.errors.size()<=1);
		assertNotNull("couldn't find expected error: "+expectedMessage.getErrorType(), foundMsg);
		/*
		assertTrue("error is not a GrammarSemanticsMessage",
				   foundMsg instanceof GrammarSemanticsMessage);
		 */
		assertArrayEquals(expectedMessage.getArgs(), foundMsg.getArgs());
	}

    public static class FilteringTokenStream extends CommonTokenStream {
        public FilteringTokenStream(TokenSource src) { super(src); }
        Set<Integer> hide = new HashSet<Integer>();
        @Override
        protected boolean sync(int i) {
            if (!super.sync(i)) {
				return false;
			}

			Token t = get(i);
			if ( hide.contains(t.getType()) ) {
				((WritableToken)t).setChannel(Token.HIDDEN_CHANNEL);
			}

			return true;
        }
        public void setTokenTypeChannel(int ttype, int channel) {
            hide.add(ttype);
        }
    }

	protected void writeTestFile(String parserName,
								 String lexerName,
								 String parserStartRuleName,
								 boolean debug,
								 boolean profile)
	{
		ST outputFileST = new ST(
			"import org.antlr.v4.runtime.*;\n" +
			"import org.antlr.v4.runtime.tree.*;\n" +
			"import org.antlr.v4.runtime.atn.*;\n" +
			"import java.nio.file.Paths;\n"+
			"import java.util.Arrays;\n"+
			"\n" +
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = CharStreams.fromPath(Paths.get(args[0]));\n" +
			"        <lexerName> lex = new <lexerName>(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        <createParser>\n"+
			"		 parser.setBuildParseTree(true);\n" +
			"		 <profile>\n"+
			"        ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
			"		 <if(profile)>System.out.println(Arrays.toString(profiler.getDecisionInfo()));<endif>\n" +
			"        ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n" +
			"    }\n" +
			"\n" +
			"	static class TreeShapeListener implements ParseTreeListener {\n" +
			"		@Override public void visitTerminal(TerminalNode node) { }\n" +
			"		@Override public void visitErrorNode(ErrorNode node) { }\n" +
			"		@Override public void exitEveryRule(ParserRuleContext ctx) { }\n" +
			"\n" +
			"		@Override\n" +
			"		public void enterEveryRule(ParserRuleContext ctx) {\n" +
			"			for (int i = 0; i \\< ctx.getChildCount(); i++) {\n" +
			"				ParseTree parent = ctx.getChild(i).getParent();\n" +
			"				if (!(parent instanceof RuleNode) || ((RuleNode)parent).getRuleContext() != ctx) {\n" +
			"					throw new IllegalStateException(\"Invalid parse tree shape detected.\");\n" +
			"				}\n" +
			"			}\n" +
			"		}\n" +
			"	}\n" +
			"}"
			);
        ST createParserST = new ST("        <parserName> parser = new <parserName>(tokens);\n");
		if ( debug ) {
			createParserST =
				new ST(
				"        <parserName> parser = new <parserName>(tokens);\n" +
                "        parser.addErrorListener(new DiagnosticErrorListener());\n");
		}
		if ( profile ) {
			outputFileST.add("profile",
							 "ProfilingATNSimulator profiler = new ProfilingATNSimulator(parser);\n" +
							 "parser.setInterpreter(profiler);");
		}
		else {
			outputFileST.add("profile", new ArrayList<Object>());
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"import java.nio.file.Paths;\n" +
			"import org.antlr.v4.runtime.*;\n" +
			"\n" +
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = CharStreams.fromPath(Paths.get(args[0]));\n" +
			"        <lexerName> lex = new <lexerName>(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        tokens.fill();\n" +
			"        for (Object t : tokens.getTokens()) System.out.println(t);\n" +
			(showDFA?"System.out.print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString());\n":"")+
			"    }\n" +
			"}"
			);

		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "Test.java", outputFileST.render());
	}

	public void writeRecognizerAndCompile(String parserName, String lexerName,
										  String parserStartRuleName,
										  boolean debug,
										  boolean profile) {
		if ( parserName==null ) {
			writeLexerTestFile(lexerName, debug);
		}
		else {
			writeTestFile(parserName,
						  lexerName,
						  parserStartRuleName,
						  debug,
						  profile);
		}

		compile("Test.java");
	}


    protected void eraseFiles(final String filesEndingWith) {
        File tmpdirF = new File(tmpdir);
        String[] files = tmpdirF.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            if ( files[i].endsWith(filesEndingWith) ) {
                new File(tmpdir+"/"+files[i]).delete();
            }
        }
    }

    protected void eraseFiles() {
		if (tmpdir == null) {
			return;
		}

        File tmpdirF = new File(tmpdir);
        String[] files = tmpdirF.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            new File(tmpdir+"/"+files[i]).delete();
        }
    }

    public void eraseTempDir() {
        File tmpdirF = new File(tmpdir);
        if ( tmpdirF.exists() ) {
            eraseFiles();
            tmpdirF.delete();
        }
    }

	public String getFirstLineOfException() {
		if ( this.stderrDuringParse ==null ) {
			return null;
		}
		String[] lines = this.stderrDuringParse.split("\n");
		String prefix="Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(),lines[0].length());
	}

    /**
     * When looking at a result set that consists of a Map/HashTable
     * we cannot rely on the output order, as the hashing algorithm or other aspects
     * of the implementation may be different on differnt JDKs or platforms. Hence
     * we take the Map, convert the keys to a List, sort them and Stringify the Map, which is a
     * bit of a hack, but guarantees that we get the same order on all systems. We assume that
     * the keys are strings.
     *
     * @param m The Map that contains keys we wish to return in sorted order
     * @return A string that represents all the keys in sorted order.
     */
    public <K, V> String sortMapToString(Map<K, V> m) {
        // Pass in crap, and get nothing back
        //
        if  (m == null) {
            return null;
        }

        System.out.println("Map toString looks like: " + m.toString());

        // Sort the keys in the Map
        //
        TreeMap<K, V> nset = new TreeMap<K, V>(m);

        System.out.println("Tree map looks like: " + nset.toString());
        return nset.toString();
    }

	public List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}

	public void assertNotNullOrEmpty(String message, String text) {
		assertNotNull(message, text);
		assertFalse(message, text.isEmpty());
	}

	public void assertNotNullOrEmpty(String text) {
		assertNotNull(text);
		assertFalse(text.isEmpty());
	}

	public static class IntTokenStream implements TokenStream {
		public IntegerList types;
		int p=0;
		public IntTokenStream(IntegerList types) { this.types = types; }

		@Override
		public void consume() { p++; }

		@Override
		public int LA(int i) { return LT(i).getType(); }

		@Override
		public int mark() {
			return index();
		}

		@Override
		public int index() { return p; }

		@Override
		public void release(int marker) {
			seek(marker);
		}

		@Override
		public void seek(int index) {
			p = index;
		}

		@Override
		public int size() {
			return types.size();
		}

		@Override
		public String getSourceName() {
			return UNKNOWN_SOURCE_NAME;
		}

		@Override
		public Token LT(int i) {
			CommonToken t;
			int rawIndex = p + i - 1;
			if ( rawIndex>=types.size() ) t = new CommonToken(Token.EOF);
			else t = new CommonToken(types.get(rawIndex));
			t.setTokenIndex(rawIndex);
			return t;
		}

		@Override
		public Token get(int i) {
			return new org.antlr.v4.runtime.CommonToken(types.get(i));
		}

		@Override
		public TokenSource getTokenSource() {
			return null;
		}


		@Override
		public String getText() {
			throw new UnsupportedOperationException("can't give strings");
		}


		@Override
		public String getText(Interval interval) {
			throw new UnsupportedOperationException("can't give strings");
		}


		@Override
		public String getText(RuleContext ctx) {
			throw new UnsupportedOperationException("can't give strings");
		}


		@Override
		public String getText(Token start, Token stop) {
			throw new UnsupportedOperationException("can't give strings");
		}
	}

	/** Sort a list */
	public <T extends Comparable<? super T>> List<T> sort(List<T> data) {
		List<T> dup = new ArrayList<T>();
		dup.addAll(data);
		Collections.sort(dup);
		return dup;
	}

	/** Return map sorted by key */
	public <K extends Comparable<? super K>,V> LinkedHashMap<K,V> sort(Map<K,V> data) {
		LinkedHashMap<K,V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>();
		keys.addAll(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}
}
