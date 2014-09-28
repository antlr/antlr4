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

import org.antlr.v4.Tool;
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
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.DefaultToolListener;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.junit.Before;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
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
import java.net.MalformedURLException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseTest {
	// -J-Dorg.antlr.v4.test.BaseTest.level=FINE
	private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");

	public static final boolean TEST_IN_SAME_PROCESS = Boolean.parseBoolean(System.getProperty("antlr.testinprocess"));

    /**
     * Build up the full classpath we need, including the surefire path (if present)
     */
    public static final String CLASSPATH = System.getProperty("java.class.path");

	public String tmpdir = null;

	/** If error during parser execution, store stderr here; can't return
     *  stdout and stderr.  This doesn't trap errors from running antlr.
     */
	protected String stderrDuringParse;

	@org.junit.Rule
	public final TestRule testWatcher = new TestWatcher() {

		@Override
		protected void succeeded(Description description) {
			// remove tmpdir if no error.
			eraseTempDir();
		}

	};

    @Before
	public void setUp() throws Exception {
        // new output dir for each test
        tmpdir = new File(System.getProperty("java.io.tmpdir"),
						  getClass().getSimpleName()+"-"+System.currentTimeMillis()).getAbsolutePath();
//		tmpdir = "/tmp";
    }

    protected org.antlr.v4.Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected Tool newTool() {
		org.antlr.v4.Tool tool = new Tool(new String[] {"-o", tmpdir});
		return tool;
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f;
			if ( g.isLexer() ) {
				f = new LexerATNFactory((LexerGrammar)g);
			}
			else {
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
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
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
		} while ( ttype!= Token.EOF );
		return tokenTypes;
	}

	public List<String> getTokenTypes(LexerGrammar lg,
									  ATN atn,
									  CharStream input)
	{
		LexerATNSimulator interp = new LexerATNSimulator(atn,new DFA[] { new DFA(atn.modeToStartState.get(Lexer.DEFAULT_MODE)) },null);
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
			if ( ttype == Token.EOF ) {
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
		throws Exception
	{
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
		DecisionState blk = (DecisionState)t;
		checkRuleDFA(g, blk, expecting);
		return equeue.all;
	}

	List<ANTLRMessage> checkRuleDFA(String gtext, int decision, String expecting)
		throws Exception
	{
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(gtext, equeue);
		ATN atn = createATN(g, false);
		DecisionState blk = atn.decisionToState.get(decision);
		checkRuleDFA(g, blk, expecting);
		return equeue.all;
	}

	void checkRuleDFA(Grammar g, DecisionState blk, String expecting)
		throws Exception
	{
		DFA dfa = createDFA(g, blk);
		String result = null;
		if ( dfa!=null ) result = dfa.toString();
		assertEquals(expecting, result);
	}

	List<ANTLRMessage> checkLexerDFA(String gtext, String expecting)
		throws Exception
	{
		return checkLexerDFA(gtext, LexerGrammar.DEFAULT_MODE_NAME, expecting);
	}

	List<ANTLRMessage> checkLexerDFA(String gtext, String modeName, String expecting)
		throws Exception
	{
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

	protected String load(String fileName, @Nullable String encoding)
		throws IOException
	{
		if ( fileName==null ) {
			return null;
		}

		String fullFileName = getClass().getPackage().getName().replace('.', '/') + '/' + fileName;
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
		}
		finally {
			isr.close();
		}
	}

	/** Wow! much faster than compiling outside of VM. Finicky though.
	 *  Had rules called r and modulo. Wouldn't compile til I changed to 'a'.
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
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}

//		List<String> errors = new ArrayList<String>();
//		for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
//			errors.add(
//				String.valueOf(diagnostic.getLineNumber())+
//				": " + diagnostic.getMessage(null));
//		}
//		if ( errors.size()>0 ) {
//			System.err.println("compile stderr from: "+cmdLine);
//			System.err.println(errors);
//			return false;
//		}
		return ok;

		/*
		File outputDir = new File(tmpdir);
		try {
			Process process =
				Runtime.getRuntime().exec(args, null, outputDir);
			StreamVacuum stdout = new StreamVacuum(process.getInputStream());
			StreamVacuum stderr = new StreamVacuum(process.getErrorStream());
			stdout.start();
			stderr.start();
			process.waitFor();
            stdout.join();
            stderr.join();
			if ( stdout.toString().length()>0 ) {
				System.err.println("compile stdout from: "+cmdLine);
				System.err.println(stdout);
			}
			if ( stderr.toString().length()>0 ) {
				System.err.println("compile stderr from: "+cmdLine);
				System.err.println(stderr);
			}
			int ret = process.exitValue();
			return ret==0;
		}
		catch (Exception e) {
			System.err.println("can't exec compilation");
			e.printStackTrace(System.err);
			return false;
		}
		*/
	}

	protected ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions) {
		final List<String> options = new ArrayList<String>();
		Collections.addAll(options, extraOptions);
		if ( !options.contains("-o") ) {
			options.add("-o");
			options.add(tmpdir);
		}
		if ( !options.contains("-lib") ) {
			options.add("-lib");
			options.add(tmpdir);
		}
		if ( !options.contains("-encoding") ) {
			options.add("-encoding");
			options.add("UTF-8");
		}
		options.add(new File(tmpdir,grammarFileName).toString());

		final String[] optionsA = new String[options.size()];
		options.toArray(optionsA);
		Tool antlr = newTool(optionsA);
		ErrorQueue equeue = new ErrorQueue(antlr);
		antlr.addListener(equeue);
		if (defaultListener) {
			antlr.addListener(new DefaultToolListener(antlr));
		}
		antlr.processGrammarsOnCommandLine();

		if ( !defaultListener && !equeue.errors.isEmpty() ) {
			System.err.println("antlr reports errors from "+options);
			for (int i = 0; i < equeue.errors.size(); i++) {
				ANTLRMessage msg = equeue.errors.get(i);
				System.err.println(msg);
			}
			System.out.println("!!!\ngrammar:");
			try {
				System.out.println(new String(Utils.readFile(tmpdir+"/"+grammarFileName)));
			}
			catch (IOException ioe) {
				System.err.println(ioe.toString());
			}
			System.out.println("###");
		}
		if ( !defaultListener && !equeue.warnings.isEmpty() ) {
			System.err.println("antlr reports warnings from "+options);
			for (int i = 0; i < equeue.warnings.size(); i++) {
				ANTLRMessage msg = equeue.warnings.get(i);
				System.err.println(msg);
			}
		}

		return equeue;
	}

	protected ErrorQueue antlr(String grammarFileName, String grammarStr, boolean defaultListener, String... extraOptions) {
		System.out.println("dir "+tmpdir);
		mkdir(tmpdir);
		writeFile(tmpdir, grammarFileName, grammarStr);
		return antlr(grammarFileName, defaultListener, extraOptions);
	}

	protected String execLexer(String grammarFileName,
							   String grammarStr,
							   String lexerName,
							   String input)
	{
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	protected String execLexer(String grammarFileName,
							   String grammarStr,
							   String lexerName,
							   String input,
							   boolean showDFA)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
									  grammarStr,
									  null,
									  lexerName);
		assertTrue(success);
		writeFile(tmpdir, "input", input);
		writeLexerTestFile(lexerName, showDFA);
		compile("Test.java");
		String output = execClass("Test");
		if ( stderrDuringParse!=null && stderrDuringParse.length()>0 ) {
			System.err.println(stderrDuringParse);
		}
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
			   NoSuchMethodException
	{
		Method startRule = null;
		Object[] args = null;
		try {
			startRule = parser.getClass().getMethod(startRuleName);
		}
		catch (NoSuchMethodException nsme) {
			// try with int _p arg for recursive func
			startRule = parser.getClass().getMethod(startRuleName, int.class);
			args = new Integer[] {0};
		}
		ParseTree result = (ParseTree)startRule.invoke(parser, args);
//		System.out.println("parse tree = "+result.toStringTree(parser));
		return result;
	}

	public Pair<Parser, Lexer> getParserAndLexer(String input,
												 String parserName, String lexerName)
		throws Exception
	{
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
			new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() },
							   ClassLoader.getSystemClassLoader());
		return loader.loadClass(name);
	}

	public Class<? extends Lexer> loadLexerClassFromTempDir(String name) throws Exception {
		return loadClassFromTempDir(name).asSubclass(Lexer.class);
	}

	public Class<? extends Parser> loadParserClassFromTempDir(String name) throws Exception {
		return loadClassFromTempDir(name).asSubclass(Parser.class);
	}

	protected String execParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String startRuleName,
								String input, boolean debug)
	{
		return execParser(grammarFileName, grammarStr, parserName,
				   lexerName, startRuleName, input, debug, false);
	}

	protected String execParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String startRuleName,
								String input, boolean debug,
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
								 debug,
								 profile);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													@Nullable String parserName,
													String lexerName,
													String... extraOptions)
	{
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													@Nullable String parserName,
													String lexerName,
													boolean defaultListener,
													String... extraOptions)
	{
		ErrorQueue equeue =
			antlr(grammarFileName, grammarStr, defaultListener, extraOptions);
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
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"BaseListener.java");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"BaseVisitor.java");
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
				if ( stderrVacuum.toString().length()>0 ) {
					this.stderrDuringParse = stderrVacuum.toString();
					System.err.println("exec stderrVacuum: "+ stderrVacuum);
				}
				return output;
			} catch (MalformedURLException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (InterruptedException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (IllegalAccessException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (IllegalArgumentException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (InvocationTargetException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (NoSuchMethodException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (SecurityException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			} catch (ClassNotFoundException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		}

		try {
			String[] args = new String[] {
				"java", "-classpath", tmpdir+pathSep+CLASSPATH,
				className, new File(tmpdir, "input").getAbsolutePath()
			};
			//String cmdLine = "java -classpath "+CLASSPATH+pathSep+tmpdir+" Test " + new File(tmpdir, "input").getAbsolutePath();
			//System.out.println("execParser: "+cmdLine);
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
			if ( stderrVacuum.toString().length()>0 ) {
				this.stderrDuringParse = stderrVacuum.toString();
				System.err.println("exec stderrVacuum: "+ stderrVacuum);
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	public void testErrors(String[] pairs, boolean printTree) {
        for (int i = 0; i < pairs.length; i+=2) {
            String input = pairs[i];
            String expect = pairs[i+1];

			String[] lines = input.split("\n");
			String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);
			ErrorQueue equeue = antlr(fileName, input, false);

			String actual = equeue.toString(true);
			actual = actual.replace(tmpdir + File.separator, "");
			System.err.println(actual);
			String msg = input;
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

	void checkRuleATN(Grammar g, String ruleName, String expecting) {
		DOTGenerator dot = new DOTGenerator(g);
		System.out.println(dot.getDOT(g.atn.ruleToStartState[g.getRule(ruleName).index]));

		Rule r = g.getRule(ruleName);
		ATNState startState = g.atn.ruleToStartState[r.index];
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

			CodeGenerator gen = new CodeGenerator(g);
			ST outputFileST = gen.generateParser();
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
			System.err.println(equeue.toString());
		}
	}

	public static class StreamVacuum implements Runnable {
		StringBuilder buf = new StringBuilder();
		BufferedReader in;
		Thread sucker;
		public StreamVacuum(InputStream in) {
			this.in = new BufferedReader( new InputStreamReader(in) );
		}
		public void start() {
			sucker = new Thread(this);
			sucker.start();
		}
		@Override
		public void run() {
			try {
				String line = in.readLine();
				while (line!=null) {
					buf.append(line);
					buf.append('\n');
					line = in.readLine();
				}
			}
			catch (IOException ioe) {
				System.err.println("can't read output from process");
			}
		}
		/** wait for the thread to finish */
		public void join() throws InterruptedException {
			sucker.join();
		}
		@Override
		public String toString() {
			return buf.toString();
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

	public static void writeFile(String dir, String fileName, String content) {
		try {
			Utils.writeFile(dir+"/"+fileName, content, "UTF-8");
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
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
			"import java.util.Arrays;\n"+
			"\n" +
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
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
			"import org.antlr.v4.runtime.*;\n" +
			"\n" +
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
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

    protected void eraseTempDir() {
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
		IntegerList types;
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

		@NotNull
		@Override
		public String getText() {
			throw new UnsupportedOperationException("can't give strings");
		}

		@NotNull
		@Override
		public String getText(Interval interval) {
			throw new UnsupportedOperationException("can't give strings");
		}

		@NotNull
		@Override
		public String getText(RuleContext ctx) {
			throw new UnsupportedOperationException("can't give strings");
		}

		@NotNull
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
