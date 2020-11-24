/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.dart;

import org.antlr.v4.Tool;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
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
import org.antlr.v4.test.runtime.descriptors.LexerExecDescriptors;
import org.antlr.v4.test.runtime.descriptors.PerformanceDescriptors;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.readFile;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertArrayEquals;


public class BaseDartTest implements RuntimeTestSupport {
	private static final List<String> AOT_COMPILE_TESTS = Arrays.asList(
		new PerformanceDescriptors.DropLoopEntryBranchInLRRule_4().input,
		new LexerExecDescriptors.LargeLexer().input
	);

	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");


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
	public static final boolean PRESERVE_TEST_DIR = Boolean.parseBoolean(System.getProperty("antlr.preserve-test-dir", "false"));

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
		String baseTestDir = System.getProperty("antlr.dart-test-dir");
		boolean perTestDirectories = false;
		if (baseTestDir == null || baseTestDir.isEmpty()) {
			baseTestDir = System.getProperty("java.io.tmpdir");
			perTestDirectories = true;
		}

		if (!new File(baseTestDir).isDirectory()) {
			throw new UnsupportedOperationException("The specified base test directory does not exist: " + baseTestDir);
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
	 * stdout and stderr. This doesn't trap errors from running antlr.
	 */
	protected String stderrDuringParse;

	/**
	 * Errors found while running antlr
	 */
	protected StringBuilder antlrToolErrors;

	private static String cacheDartPackages;

	private String getPropertyPrefix() {
		return "antlr-dart";
	}

	@Override
	public void testSetUp() throws Exception {
		if (CREATE_PER_TEST_DIRECTORIES) {
			// new output dir for each test
			String threadName = Thread.currentThread().getName();
			String testDirectory = getClass().getSimpleName() + "-" + threadName + "-" + System.nanoTime();
			tmpdir = new File(BASE_TEST_DIR, testDirectory).getAbsolutePath();
		} else {
			tmpdir = new File(BASE_TEST_DIR).getAbsolutePath();
			if (!PRESERVE_TEST_DIR && new File(tmpdir).exists()) {
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
		if (antlrToolErrors.length() == 0) {
			return null;
		}
		return antlrToolErrors.toString();
	}

	protected Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if (g.atn == null) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f;
			if (g.isLexer()) {
				f = new LexerATNFactory((LexerGrammar) g);
			} else {
				f = new ParserATNFactory(g);
			}

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if (useSerializer) {
			char[] serialized = ATNSerializer.getSerializedAsChars(atn);
			return new ATNDeserializer().deserialize(serialized);
		}

		return atn;
	}

	protected void semanticProcess(Grammar g) {
		if (g.ast != null && !g.ast.hasErrors) {
//			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if (g.getImportedGrammars() != null) { // process imported grammars (if any)
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
		if (expecting != null && !expecting.trim().isEmpty()) {
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
		} while (ttype != Token.EOF);
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
			if (hitEOF) {
				tokenTypes.add("EOF");
				break;
			}
			int t = input.LA(1);
			ttype = interp.match(input, Lexer.DEFAULT_MODE);
			if (ttype == Token.EOF) {
				tokenTypes.add("EOF");
			} else {
				tokenTypes.add(lg.typeToTokenList.get(ttype));
			}

			if (t == IntStream.EOF) {
				hitEOF = true;
			}
		} while (ttype != Token.EOF);
		return tokenTypes;
	}

	List<ANTLRMessage> checkRuleDFA(String gtext, String ruleName, String expecting)
		throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(gtext, equeue);
		ATN atn = createATN(g, false);
		ATNState s = atn.ruleToStartState[g.getRule(ruleName).index];
		if (s == null) {
			System.err.println("no such rule: " + ruleName);
			return null;
		}
		ATNState t = s.transition(0).target;
		if (!(t instanceof DecisionState)) {
			System.out.println(ruleName + " has no decision");
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
		if (dfa != null) result = dfa.toString();
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
		if (fileName == null) {
			return null;
		}

		String fullFileName = getClass().getPackage().getName().replace('.', '/') + '/' + fileName;
		int size = 65000;
		InputStreamReader isr;
		InputStream fis = getClass().getClassLoader().getResourceAsStream(fullFileName);
		if (encoding != null) {
			isr = new InputStreamReader(fis, encoding);
		} else {
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
		String output = execClass("Test", AOT_COMPILE_TESTS.contains(input));
		return output;
	}

	public ParseTree execParser(String startRuleName, String input,
								String parserName, String lexerName)
		throws Exception {
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
							 boolean showDiagnosticErrors) {
		return execParser(grammarFileName, grammarStr, parserName, lexerName,
			listenerName, visitorName, startRuleName, input, showDiagnosticErrors, false);
	}

	public String execParser(String grammarFileName,
							 String grammarStr,
							 String parserName,
							 String lexerName,
							 String listenerName,
							 String visitorName,
							 String startRuleName,
							 String input,
							 boolean showDiagnosticErrors,
							 boolean profile) {
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
			profile,
			AOT_COMPILE_TESTS.contains(input));
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													String... extraOptions) {
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean defaultListener,
													String... extraOptions) {
		ErrorQueue equeue =
			BaseRuntimeTest.antlrOnString(getTmpDir(), "Dart", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if (lexerName != null) {
			files.add(lexerName + ".dart");
		}
		if (parserName != null) {
			files.add(parserName + ".dart");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName + "Listener.dart");
				files.add(grammarName + "BaseListener.dart");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName + "Visitor.dart");
				files.add(grammarName + "BaseVisitor.dart");
			}
		}

		String runtime = locateRuntime();
		writeFile(tmpdir, "pubspec.yaml",
			"name: \"test\"\n" +
				"dependencies:\n" +
				"  antlr4:\n" +
				"    path: " + runtime + "\n");
		if (cacheDartPackages == null) {
			try {
				Process process = Runtime.getRuntime().exec(new String[]{locatePub(), "get"}, null, new File(tmpdir));
				StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
				stderrVacuum.start();
				process.waitFor();
				stderrVacuum.join();
				String stderrDuringPubGet = stderrVacuum.toString();
				if (!stderrDuringPubGet.isEmpty()) {
					System.out.println("Pub Get error: " + stderrVacuum.toString());
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			cacheDartPackages = readFile(tmpdir, ".packages");
		} else {
			writeFile(tmpdir, ".packages", cacheDartPackages);
		}
		return true; // allIsWell: no compile
	}

	protected String rawExecRecognizer(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug,
									   boolean profile,
									   boolean aotCompile) {
		this.stderrDuringParse = null;
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		} else {
			writeTestFile(parserName,
				lexerName,
				parserStartRuleName,
				debug,
				profile);
		}

		return execClass("Test", aotCompile);
	}

	public String execClass(String className, boolean compile) {
		try {
			if (compile) {
				String[] args = new String[]{
					locateDart2Native(),
					className + ".dart", "-o", className
				};
				String cmdLine = Utils.join(args, " ");
				System.err.println("Compile: " + cmdLine);
				Process process =
					Runtime.getRuntime().exec(args, null, new File(tmpdir));
				StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
				stderrVacuum.start();
				int result = process.waitFor();
				if (result != 0) {
					stderrVacuum.join();
					System.err.print("Error compiling dart file: " + stderrVacuum.toString());
				}
			}

			String[] args;
			if (compile) {
				args = new String[]{
					new File(tmpdir, className).getAbsolutePath(), new File(tmpdir, "input").getAbsolutePath()
				};
			} else {
				args = new String[]{
					locateDart(),
					className + ".dart", new File(tmpdir, "input").getAbsolutePath()
				};
			}
			//String cmdLine = Utils.join(args, " ");
			//System.err.println("execParser: " + cmdLine);
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
			if (output.length() == 0) {
				output = null;
			}
			if (stderrVacuum.toString().length() > 0) {
				this.stderrDuringParse = stderrVacuum.toString();
			}
			return output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String locateTool(String tool) {
		final String dartPath = System.getProperty("DART_PATH");

		final String[] tools = isWindows()
				? new String[]{tool + ".exe", tool + ".bat", tool}
				: new String[]{tool};

		if (dartPath != null) {
			for (String t : tools) {
				if (new File(dartPath + t).exists()) {
					return dartPath + t;
				}
			}
		}

		final String[] roots = isWindows()
				? new String[]{"C:\\tools\\dart-sdk\\bin\\"}
				: new String[]{"/usr/local/bin/", "/opt/local/bin/", "/usr/bin/", "/usr/lib/dart/bin/"};

		for (String root : roots) {
			for (String t : tools) {
				if (new File(root + t).exists()) {
					return root + t;
				}
			}
		}

		throw new RuntimeException("Could not locate " + tool);
	}

	protected String locatePub() {
		String propName = getPropertyPrefix() + "-pub";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("pub");
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		return file.getAbsolutePath();
	}

	protected String locateDart() {
		String propName = getPropertyPrefix() + "-dart";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("dart");
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		return file.getAbsolutePath();
	}

	protected String locateDart2Native() {
		String propName = getPropertyPrefix() + "-dart2native";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("dart2native");
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		return file.getAbsolutePath();
	}

	private String locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeSrc = loader.getResource("Dart");
		if (runtimeSrc == null) {
			throw new RuntimeException("Cannot find Dart runtime");
		}
		if (isWindows()) {
			return runtimeSrc.getPath().replaceFirst("/", "");
		}
		return runtimeSrc.getPath();
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
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
			if (m.getClass() == c) filtered.add(m);
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
		if (g.ast != null && !g.ast.hasErrors) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if (g.isLexer()) factory = new LexerATNFactory((LexerGrammar) g);
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
			String snippet = output.substring(start + b.length(), end);
			assertEquals(expected, snippet);
		}
		if (equeue.size() > 0) {
//			System.err.println(equeue.toString());
		}
	}

	protected void checkGrammarSemanticsError(ErrorQueue equeue,
											  GrammarSemanticsMessage expectedMessage)
		throws Exception {
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; " + expectedMessage.getErrorType() + " expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
			foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if (equeue.size() != 1) {
			System.err.println(equeue);
		}
	}

	protected void checkGrammarSemanticsWarning(ErrorQueue equeue,
												GrammarSemanticsMessage expectedMessage)
		throws Exception {
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.warnings.size(); i++) {
			ANTLRMessage m = equeue.warnings.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; " + expectedMessage.getErrorType() + " expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
			foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if (equeue.size() != 1) {
			System.err.println(equeue);
		}
	}

	protected void checkError(ErrorQueue equeue,
							  ANTLRMessage expectedMessage)
		throws Exception {
		//System.out.println("errors="+equeue);
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertTrue("no error; " + expectedMessage.getErrorType() + " expected", !equeue.errors.isEmpty());
		assertTrue("too many errors; " + equeue.errors, equeue.errors.size() <= 1);
		assertNotNull("couldn't find expected error: " + expectedMessage.getErrorType(), foundMsg);
		/*
		 * assertTrue("error is not a GrammarSemanticsMessage", foundMsg
		 * instanceof GrammarSemanticsMessage);
		 */
		assertArrayEquals(expectedMessage.getArgs(), foundMsg.getArgs());
	}

	public static class FilteringTokenStream extends CommonTokenStream {
		public FilteringTokenStream(TokenSource src) {
			super(src);
		}

		Set<Integer> hide = new HashSet<Integer>();

		@Override
		protected boolean sync(int i) {
			if (!super.sync(i)) {
				return false;
			}

			Token t = get(i);
			if (hide.contains(t.getType())) {
				((WritableToken) t).setChannel(Token.HIDDEN_CHANNEL);
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
								 boolean profile) {
		ST outputFileST = new ST(
			"import 'package:antlr4/antlr4.dart';\n" +
				"\n" +
				"import '<lexerName>.dart';\n" +
				"import '<parserName>.dart';\n" +
				"\n" +
				"void main(List\\<String> args) async {\n" +
				"  CharStream input = await InputStream.fromPath(args[0]);\n" +
				"  final lex = <lexerName>(input);\n" +
				"  final tokens = CommonTokenStream(lex);\n" +
				"  <createParser>\n" +
				"  parser.buildParseTree = true;\n" +
				"  <profile>\n" +
				"  ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
				"  <if(profile)>print('[${profiler.getDecisionInfo().join(', ')}]');<endif>\n" +
				"  ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree);\n" +
				"}\n" +
				"\n" +
				"class TreeShapeListener implements ParseTreeListener {\n" +
				"  @override void visitTerminal(TerminalNode node) {}\n" +
				"\n" +
				"  @override void visitErrorNode(ErrorNode node) {}\n" +
				"\n" +
				"  @override void exitEveryRule(ParserRuleContext ctx) {}\n" +
				"\n" +
				"  @override\n" +
				"  void enterEveryRule(ParserRuleContext ctx) {\n" +
				"    for (var i = 0; i \\< ctx.childCount; i++) {\n" +
				"      final parent = ctx.getChild(i).parent;\n" +
				"      if (!(parent is RuleNode) || (parent as RuleNode).ruleContext != ctx) {\n" +
				"        throw StateError('Invalid parse tree shape detected.');\n" +
				"      }\n" +
				"    }\n" +
				"  }\n" +
				"}\n"
		);
		ST createParserST = new ST("final parser = <parserName>(tokens);\n");
		if (debug) {
			createParserST =
				new ST(
					"final parser = <parserName>(tokens);\n" +
						"  parser.addErrorListener(new DiagnosticErrorListener());\n");
		}
		if (profile) {
			outputFileST.add("profile",
				"ProfilingATNSimulator profiler = ProfilingATNSimulator(parser);\n" +
					"parser.setInterpreter(profiler);");
		} else {
			outputFileST.add("profile", new ArrayList<Object>());
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.dart", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"import 'dart:io';\n" +
				"\n" +
				"import 'package:antlr4/antlr4.dart';\n" +
				"\n" +
				"import '<lexerName>.dart';\n" +
				"\n" +
				"void main(List\\<String> args) async {\n" +
				"  CharStream input = await InputStream.fromPath(args[0]);\n" +
				"  <lexerName> lex = <lexerName>(input);\n" +
				"  CommonTokenStream tokens = CommonTokenStream(lex);\n" +
				"  tokens.fill();\n" +
				"  for (Object t in tokens.getTokens())\n" +
				"    print(t);\n" +
				"\n" +
				(showDFA ? "stdout.write(lex.interpreter.getDFA(Lexer.DEFAULT_MODE).toLexerString());\n" : "") +
				"}\n"
		);

		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "Test.dart", outputFileST.render());
	}

	protected void eraseFiles() {
		if (tmpdir == null) {
			return;
		}

		File tmpdirF = new File(tmpdir);
		String[] files = tmpdirF.list();
		for (int i = 0; files != null && i < files.length; i++) {
			new File(tmpdir + "/" + files[i]).delete();
		}
	}

	@Override
	public void eraseTempDir() {
		File tmpdirF = new File(tmpdir);
		if (tmpdirF.exists()) {
			eraseFiles();
			tmpdirF.delete();
		}
	}

	public String getFirstLineOfException() {
		if (this.stderrDuringParse == null) {
			return null;
		}
		String[] lines = this.stderrDuringParse.split("\n");
		String prefix = "Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(), lines[0].length());
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
		if (m == null) {
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
		int p = 0;

		public IntTokenStream(IntegerList types) {
			this.types = types;
		}

		@Override
		public void consume() {
			p++;
		}

		@Override
		public int LA(int i) {
			return LT(i).getType();
		}

		@Override
		public int mark() {
			return index();
		}

		@Override
		public int index() {
			return p;
		}

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
			if (rawIndex >= types.size()) t = new CommonToken(Token.EOF);
			else t = new CommonToken(types.get(rawIndex));
			t.setTokenIndex(rawIndex);
			return t;
		}

		@Override
		public Token get(int i) {
			return new CommonToken(types.get(i));
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

	/**
	 * Sort a list
	 */
	public <T extends Comparable<? super T>> List<T> sort(List<T> data) {
		List<T> dup = new ArrayList<T>();
		dup.addAll(data);
		Collections.sort(dup);
		return dup;
	}

	/**
	 * Return map sorted by key
	 */
	public <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sort(Map<K, V> data) {
		LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>();
		keys.addAll(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}
}
