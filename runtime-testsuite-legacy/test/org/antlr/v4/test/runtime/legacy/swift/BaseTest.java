/*
 * [The "BSD license"]
 *  Copyright (c) 2012-2016 Terence Parr
 *  Copyright (c) 2012-2016 Sam Harwell
 *  Copyright (c) 2016 Janyou
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
package org.antlr.v4.test.runtime.legacy.swift;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.legacy.java.ErrorQueue;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DefaultToolListener;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.junit.Before;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseTest {
	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");

	/**
	 * When the {@code antlr.preserve-test-dir} runtime property is set to
	 * {@code true}, the temporary directories created by the test run will not
	 * be removed at the end of the test run, even for tests that completed
	 * successfully.
	 *
	 * <p>
	 * The default behavior (used in all other cases) is removing the temporary
	 * directories for all tests which completed successfully, and preserving
	 * the directories for tests which failed.</p>
	 */
	public static final boolean PRESERVE_TEST_DIR = Boolean.parseBoolean(System.getProperty("antlr-preserve-swift-test-dir"));

	/**
	 * The base test directory is the directory where generated files get placed
	 * during unit test execution.
	 *
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
	 *
	 * <p>
	 * This value is {@code true} when the {@code antlr.java-test-dir} system
	 * property is set, and otherwise {@code false}.</p>
	 */
	public static final boolean CREATE_PER_TEST_DIRECTORIES;

	public static final String EXEC_NAME = "Test";

	public static String ANTLR_FRAMEWORK_DIR;

	static {
		String baseTestDir = System.getProperty("antlr-swift-test-dir");
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

		//add antlr.swift
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//TODO
		final URL swiftRuntime = loader.getResource("Swift/Antlr4");
		if ( swiftRuntime==null ) {
			throw new RuntimeException("Swift runtime file not found at:" + swiftRuntime.getPath());
		}
		String swiftRuntimePath = swiftRuntime.getPath();

		//get Antlr4 framework

		try {
			String commandLine = "find " + swiftRuntimePath +  "/ -iname *.swift -not -name merge.swift -exec cat {} ;" ;
			ProcessBuilder builder = new ProcessBuilder(commandLine.split(" "));
			builder.redirectError(ProcessBuilder.Redirect.INHERIT);
			Process p = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(p.getInputStream());
			stdoutVacuum.start();
			p.waitFor();
			stdoutVacuum.join();

			String antlrSwift = stdoutVacuum.toString();
            //write to Antlr4
			ANTLR_FRAMEWORK_DIR =  new File(BASE_TEST_DIR, "Antlr4").getAbsolutePath();
			mkdir(ANTLR_FRAMEWORK_DIR);
			writeFile(ANTLR_FRAMEWORK_DIR,"Antlr4.swift",antlrSwift);
			//compile Antlr4  module
			buildAntlr4Framework();
			String argsString;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// shutdown logic
				eraseAntlrFrameWorkDir();
			}
		});

	}
	private static boolean buildAntlr4Framework() throws Exception {
		String argsString = "xcrun -sdk macosx swiftc -emit-library -emit-module Antlr4.swift -module-name Antlr4 -module-link-name Antlr4 -Xlinker -install_name -Xlinker " + ANTLR_FRAMEWORK_DIR + "/libAntlr4.dylib ";
		return runProcess(argsString,ANTLR_FRAMEWORK_DIR);
	}

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
			if (!PRESERVE_TEST_DIR) {
				eraseTempDir();
			}
		}

	};

	@Before
	public void setUp() throws Exception {
		if (CREATE_PER_TEST_DIRECTORIES) {
			// new output dir for each test
			String testDirectory = getClass().getSimpleName() + "-" + System.currentTimeMillis();
			tmpdir = new File(BASE_TEST_DIR, testDirectory).getAbsolutePath();
		}
		else {
			tmpdir = new File(BASE_TEST_DIR).getAbsolutePath();
			if (!PRESERVE_TEST_DIR && new File(tmpdir).exists()) {
				eraseFiles(tmpdir);
			}
		}

	}

//	private void copyAntlrFramework(){
//		 writeFile(tmpdir,"Antlr4.swift",ANTLR_FRAMEWORK);
//	}

	protected Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected Tool newTool() {
		Tool tool = new Tool(new String[] {"-o", tmpdir});
		return tool;
	}

	protected String load(String fileName, String encoding)
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


	protected ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions) {
		final List<String> options = new ArrayList<String>();
		Collections.addAll(options, extraOptions);
		options.add("-Dlanguage=Swift");
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
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				null,
				lexerName);
		assertTrue(success);
		writeFile(tmpdir, "input", input);
		writeLexerTestFile(lexerName, showDFA);
		addSourceFiles("main.swift");
//		addSourceFiles("Antlr4.swift");

		compile();
		String output = execTest();
		if ( stderrDuringParse!=null && stderrDuringParse.length()>0 ) {
			System.err.println(stderrDuringParse);
		}
		return output;
	}

	Set<String> sourceFiles = new HashSet<String>();

	private void addSourceFiles(String ... files) {
		for(String file : files)
			this.sourceFiles.add(file);
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
								String input, boolean debug,boolean profile)
	{
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		assertTrue(success);
		writeFile(tmpdir, "input", input);
		return rawExecRecognizer(parserName,
				lexerName,
				startRuleName,
				debug,profile);
	}

	/** Return true if all is well */
	protected boolean rawGenerateRecognizer(String grammarFileName,
											String grammarStr,
											String parserName,
											String lexerName,
											String... extraOptions)
	{
		return rawGenerateRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateRecognizer(String grammarFileName,
											String grammarStr,
											String parserName,
											String lexerName,
											boolean defaultListener,
											String... extraOptions)
	{
		ErrorQueue equeue = antlr(grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if ( lexerName!=null ) {
			files.add(lexerName+".swift");
			files.add(lexerName+"ATN.swift");
		}
		if ( parserName!=null ) {
			files.add(parserName+".swift");
			files.add(parserName+"ATN.swift");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName+"Listener.swift");
				files.add(grammarName+"BaseListener.swift");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName+"Visitor.swift");
				files.add(grammarName+"BaseVisitor.swift");
			}
		}
		addSourceFiles(files.toArray(new String[files.size()]));
		return true;
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
			writeParserTestFile(parserName,
					lexerName,
					parserStartRuleName,
					debug,
					profile);
		}

		addSourceFiles("main.swift");
//		addSourceFiles("Antlr4.swift");
		return execRecognizer();
	}

	public String execRecognizer() {
		compile();
		return execTest();
	}

	public boolean compile() {
		try {

			if(!buildProject())
				return false;
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	private boolean buildProject() throws Exception {
		String fileList =  sourceFiles.toString().replace("[", "").replace("]", "")
				.replace(", ", " ");

		String argsString = "xcrun -sdk macosx swiftc " + fileList +  " -o " + EXEC_NAME + " -I "+ ANTLR_FRAMEWORK_DIR + " -L "+ ANTLR_FRAMEWORK_DIR +   " -module-link-name Antlr4" ;
		return runProcess(argsString,tmpdir);
	}

	private static boolean runProcess(String argsString, String execPath) throws IOException, InterruptedException {
		String[] args = argsString.split(" ");
		System.err.println("Starting build "+ argsString);//Utils.join(args, " "))
		Process process = Runtime.getRuntime().exec(args, null, new File(execPath));
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		if ( stderrVacuum.toString().length()>0 ) {
			//this.stderrDuringParse = stderrVacuum.toString();
			System.err.println("buildProject stderrVacuum: "+ stderrVacuum);
		}
		return process.exitValue()==0;
	}

	public String execTest() {
		try {
			String exec = tmpdir + "/" + EXEC_NAME ;
			String[] args =
					new String[] { exec,"input"};//new File(tmpdir, "input").getAbsolutePath()
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(tmpdir));
			Process p = pb.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(p.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(p.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			p.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();
			if ( stderrVacuum.toString().length()>0 ) {
				this.stderrDuringParse = stderrVacuum.toString();
				System.err.println("execTest stderrVacuum: "+ stderrVacuum);
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

			org.junit.Assert.assertEquals("error in: "+msg,expect,actual);
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


	List<ANTLRMessage> getMessagesOfType(List<ANTLRMessage> msgs, Class<? extends ANTLRMessage> c) {
		List<ANTLRMessage> filtered = new ArrayList<ANTLRMessage>();
		for (ANTLRMessage m : msgs) {
			if ( m.getClass() == c ) filtered.add(m);
		}
		return filtered;
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

	protected static void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeParserTestFile(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug,
									   boolean profile)
	{

		ST outputFileST = new ST(
			        "import Antlr4\n" +
					"import Foundation\n" +
					"setbuf(__stdoutp, nil)\n" +
					"class TreeShapeListener: ParseTreeListener{\n" +
					"    func visitTerminal(_ node: TerminalNode){ }\n" +
					"    func visitErrorNode(_ node: ErrorNode){ }\n" +
					"    func enterEveryRule(_ ctx: ParserRuleContext) throws { }\n" +
					"    func exitEveryRule(_ ctx: ParserRuleContext) throws {\n" +
					"        for i in 0..\\<ctx.getChildCount() {\n" +
					"            let parent = ctx.getChild(i)?.getParent()\n" +
					"            if (!(parent is RuleNode) || (parent as! RuleNode ).getRuleContext() !== ctx) {\n" +
					"                throw ANTLRError.illegalState(msg: \"Invalid parse tree shape detected.\")\n" +
					"            }\n" +
					"        }\n" +
					"    }\n" +
					"}\n" +
					"\n" +
					"do {\n" +
					"let args = CommandLine.arguments\n" +
					"let input = ANTLRFileStream(args[1])\n" +
					"let lex = <lexerName>(input)\n" +
					"let tokens = CommonTokenStream(lex)\n" +
					"<createParser>\n" +
					"parser.setBuildParseTree(true)\n" +
					"<profile>\n" +
					"let tree = try parser.<parserStartRuleName>()\n" +
					"<if(profile)>print(profiler.getDecisionInfo().description)<endif>\n" +
					"try ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree)\n" +
					"}catch ANTLRException.cannotInvokeStartRule {\n" +
					"    print(\"error occur: cannotInvokeStartRule\")\n" +
					"}catch ANTLRException.recognition(let e )   {\n" +
					"    print(\"error occur\\(e)\")\n" +
					"}catch {\n" +
					"    print(\"error occur\")\n" +
					"}\n"
		);
		ST createParserST = new ST("       let parser = try <parserName>(tokens)\n");
		if ( debug ) {
			createParserST =
					new ST(
							"        let parser = try <parserName>(tokens)\n" +
									"        parser.addErrorListener(DiagnosticErrorListener())\n");
		}
		if ( profile ) {
			outputFileST.add("profile",
					"let profiler = ProfilingATNSimulator(parser)\n" +
							"parser.setInterpreter(profiler)");
		}
		else {
			outputFileST.add("profile", new ArrayList<Object>());
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "main.swift", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import Antlr4\n" +
				"import Foundation\n" +
				"setbuf(__stdoutp, nil)\n" +
				"let args = CommandLine.arguments\n" +
				"let input = ANTLRFileStream(args[1])\n" +
				"let lex = <lexerName>(input)\n" +
				"let tokens = CommonTokenStream(lex)\n" +
				"do {\n" +
				"    try tokens.fill()\n" +
				"}catch ANTLRException.cannotInvokeStartRule {\n" +
				"    print(\"error occur: cannotInvokeStartRule\")\n" +
				"}catch ANTLRException.recognition(let e )   {\n" +
				"    print(\"error occur\\(e)\")\n" +
				"}catch {\n" +
				"    print(\"error occur\")\n" +
				"}\n" +
				"for t in tokens.getTokens() {\n" +
				"  print(t)\n" +
				"}\n" +
				(showDFA?"print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString(), terminator: \"\" )\n":"") );

		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "main.swift", outputFileST.render());
	}

	public void writeRecognizerAndCompile(String parserName, String lexerName,
										  String parserStartRuleName,
										  boolean debug,
										  boolean profile) {
		if ( parserName==null ) {
			writeLexerTestFile(lexerName, debug);
		}
		else {
			writeParserTestFile(parserName,
					lexerName,
					parserStartRuleName,
					debug,
					profile);
		}

		addSourceFiles("main.swift");
//		addSourceFiles("Antlr4.swift");
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


	protected void eraseTempDir() {
		File tmpdirF = new File(tmpdir);
		if ( tmpdirF.exists() ) {
			eraseFilesIn(tmpdir);
			tmpdirF.delete();
		}
	}
	protected static void eraseFilesIn(String dirName) {
		if (dirName == null) {
			return;
		}

		File dir = new File(dirName);
		String[] files = dir.list();
		if(files!=null) for(String file : files) {
			new File(dirName+"/"+file).delete();
		}
	}
	protected static  void eraseAntlrFrameWorkDir() {
		File frameworkdir = new File(ANTLR_FRAMEWORK_DIR);
		if ( frameworkdir.exists() ) {
			eraseFilesIn(ANTLR_FRAMEWORK_DIR);
			frameworkdir.delete();
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

	protected static void assertEquals(String msg, int a, int b) {
		org.junit.Assert.assertEquals(msg, a, b);
	}

	protected static void assertEquals(String a, String b) {
		a = absorbExpectedDifferences(a);
		b = absorbActualDifferences(b);
		org.junit.Assert.assertEquals(a, b);
	}

	protected static void assertNull(String a) {
		a = absorbActualDifferences(a);
		org.junit.Assert.assertNull(a);
	}

	private static String absorbExpectedDifferences(String a) {
		if(a==null)
			return a;
		// work around the lack of requiresFullContext field in DFAState
		//if(a.startsWith("Decision"))
			//a = a.replaceAll("\\^", "");
		// work around the algo difference for full context
		//a = stripOutUnwantedLinesWith(a, "reportAttemptingFullContext","reportContextSensitivity", "reportAmbiguity");
		if(a.isEmpty())
			a = null;
		return a;
	}

	private static String absorbActualDifferences(String a) {
		if(a==null)
			return a;
		// work around the algo difference for full context
		// work around the algo difference for semantic predicates
		//a = stripOutUnwantedLinesWith(a, "reportContextSensitivity","eval=false");
		if(a.isEmpty())
			a = null;
		return a;
	}

	private static String stripOutUnwantedLinesWith(String a, String ... unwanteds) {
		String[] lines = a.split("\n");
		StringBuilder sb = new StringBuilder();
		for(String line : lines) {
			boolean wanted = true;
			for(String unwanted : unwanteds) {
				if(line.contains(unwanted) ) {
					wanted = false;
					break;
				}
			}
			if(!wanted)
				continue;
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

}
