/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.RuntimeTestSupport;
import org.antlr.v4.test.runtime.StreamVacuum;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.mkdir;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BaseSwiftTest implements RuntimeTestSupport {

	/**
	 * Path of the ANTLR runtime.
	 */
	private static String ANTLR_RUNTIME_PATH;

	/**
	 * Absolute path to swift command.
	 */
	private static String SWIFT_CMD;

	/**
	 * Environment variable name for swift home.
	 */
	private static final String SWIFT_HOME_ENV_KEY = "SWIFT_HOME";

	static {
		Map<String, String> env = System.getenv();
		String swiftHome = env.containsKey(SWIFT_HOME_ENV_KEY) ? env.get(SWIFT_HOME_ENV_KEY) : "";
		SWIFT_CMD = swiftHome + "swift";

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// build swift runtime
		URL swiftRuntime = loader.getResource("Swift");
		if (swiftRuntime == null) {
			throw new RuntimeException("Swift runtime file not found at:" + swiftRuntime.getPath());
		}
		ANTLR_RUNTIME_PATH = swiftRuntime.getPath();
		fastFailRunProcess(ANTLR_RUNTIME_PATH, SWIFT_CMD, "build");

		// shutdown logic
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				fastFailRunProcess(ANTLR_RUNTIME_PATH, SWIFT_CMD, "package", "clean");
			}
		});
	}

	public String tmpdir = null;

	/**
	 * If error during parser execution, store stderr here; can't return
	 * stdout and stderr.  This doesn't trap errors from running antlr.
	 */
	private String stderrDuringParse;

	/**
	 * Errors found while running antlr
	 */
	private StringBuilder antlrToolErrors;

	/**
	 * Source files used in each small swift project.
	 */
	private Set<String> sourceFiles = new HashSet<>();

	@Override
	public void testSetUp() throws Exception {
		// new output dir for each test
		String propName = "antlr-swift-test-dir";
		String prop = System.getProperty(propName);
		if (prop != null && prop.length() > 0) {
			tmpdir = prop;
		}
		else {
			String classSimpleName = getClass().getSimpleName();
			String threadName = Thread.currentThread().getName();
			String childPath = String.format("%s-%s-%s", classSimpleName, threadName, System.currentTimeMillis());
			tmpdir = new File(System.getProperty("java.io.tmpdir"), childPath).getAbsolutePath();
		}
		antlrToolErrors = new StringBuilder();
	}

	@Override
	public void testTearDown() throws Exception {
	}

	@Override
	public void eraseTempDir() {
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

	@Override
	public String execLexer(String grammarFileName, String grammarStr, String lexerName, String input, boolean showDFA) {
		generateParser(grammarFileName,
				grammarStr,
				null,
				lexerName);
		writeFile(tmpdir, "input", input);
		writeLexerTestFile(lexerName, showDFA);
		addSourceFiles("main.swift");

		String projectName = "testcase-" + System.currentTimeMillis();
		String projectDir = getTmpDir() + "/" + projectName;
		buildProject(projectDir, projectName);
		return execTest(projectDir, projectName);
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr, String parserName, String lexerName, String listenerName, String visitorName, String startRuleName, String input, boolean showDiagnosticErrors) {
		generateParser(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		writeFile(getTmpDir(), "input", input);
		return execParser(parserName,
				lexerName,
				startRuleName,
				showDiagnosticErrors,false);
	}

	private String execTest(String projectDir, String projectName) {
		try {
			Pair<String, String> output = runProcess(projectDir, "./.build/debug/" + projectName, "input");
			if (output.b.length() > 0) {
				stderrDuringParse = output.b;
			}
			String stdout = output.a;
			return stdout.length() > 0 ? stdout : null;
		}
		catch (Exception e) {
			System.err.println("Execution of testcase failed.");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private void addSourceFiles(String... files) {
		Collections.addAll(this.sourceFiles, files);
	}

	private void buildProject(String projectDir, String projectName) {
		mkdir(projectDir);
		fastFailRunProcess(projectDir, SWIFT_CMD, "package", "init", "--type", "executable");
		for (String sourceFile: sourceFiles) {
			String absPath = getTmpDir() + "/" + sourceFile;
			fastFailRunProcess(getTmpDir(), "mv", "-f", absPath, projectDir + "/Sources/" + projectName);
		}
		fastFailRunProcess(getTmpDir(), "mv", "-f", "input", projectDir);

		try {
			String dylibPath = ANTLR_RUNTIME_PATH + "/.build/debug/";
			Pair<String, String> buildResult = runProcess(projectDir, SWIFT_CMD, "build",
					"-Xswiftc", "-I"+dylibPath,
					"-Xlinker", "-L"+dylibPath,
					"-Xlinker", "-lAntlr4",
					"-Xlinker", "-rpath",
					"-Xlinker", dylibPath);
			if (buildResult.b.length() > 0) {
				throw new RuntimeException("unit test build failed: " + buildResult.a + "\n" + buildResult.b);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Pair<String,String> runProcess(String execPath, String... args) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(args, null, new File(execPath));
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		return new Pair<>(stdoutVacuum.toString(), stderrVacuum.toString());
	}

	private static void fastFailRunProcess(String workingDir, String... command) {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(workingDir));
		try {
			Process p = builder.start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String execParser(String parserName,
							  String lexerName,
							  String parserStartRuleName,
							  boolean debug,
							  boolean profile)
	{
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
		String projectName = "testcase-" + System.currentTimeMillis();
		String projectDir = getTmpDir() + "/" + projectName;
		buildProject(projectDir, projectName);
		return execTest(projectDir, projectName);
	}

	private void writeParserTestFile(String parserName,
									 String lexerName,
									 String parserStartRuleName,
									 boolean debug,
									 boolean profile) {

		ST outputFileST = new ST(
				"import Antlr4\n" +
						"import Foundation\n" +
						"setbuf(stdout, nil)\n" +
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
		if (debug) {
			createParserST =
					new ST(
							"        let parser = try <parserName>(tokens)\n" +
									"        parser.addErrorListener(DiagnosticErrorListener())\n");
		}
		if (profile) {
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

	private void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import Antlr4\n" +
						"import Foundation\n" +

						"setbuf(stdout, nil)\n" +
						"let args = CommandLine.arguments\n" +
						"let input = ANTLRFileStream(args[1])\n" +
						"let lex = <lexerName>(input)\n" +
						"let tokens = CommonTokenStream(lex)\n" +

						"do {\n" +
						"	try tokens.fill()\n" +
						"} catch ANTLRException.cannotInvokeStartRule {\n" +
						"	print(\"error occur: cannotInvokeStartRule\")\n" +
						"} catch ANTLRException.recognition(let e )   {\n" +
						"	print(\"error occur\\(e)\")\n" +
						"} catch {\n" +
						"	print(\"error occur\")\n" +
						"}\n" +

						"for t in tokens.getTokens() {\n" +
						"	print(t)\n" +
						"}\n" +
						(showDFA ? "print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString(), terminator: \"\" )\n" : ""));

		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "main.swift", outputFileST.render());
	}

	/**
	 * Generates the parser for one test case.
	 */
	private void generateParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTmpDir(), "Swift", grammarFileName, grammarStr, false, extraOptions);
		assertTrue(equeue.errors.isEmpty());
//		System.out.println(getTmpDir());

		List<String> files = new ArrayList<>();
		if (lexerName != null) {
			files.add(lexerName + ".swift");
			files.add(lexerName + "ATN.swift");
		}

		if (parserName != null) {
			files.add(parserName + ".swift");
			files.add(parserName + "ATN.swift");
			Set<String> optionsSet = new HashSet<>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName + "Listener.swift");
				files.add(grammarName + "BaseListener.swift");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName + "Visitor.swift");
				files.add(grammarName + "BaseVisitor.swift");
			}
		}
		addSourceFiles(files.toArray(new String[files.size()]));
	}
}
