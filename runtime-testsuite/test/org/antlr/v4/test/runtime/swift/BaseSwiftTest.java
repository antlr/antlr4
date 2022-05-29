/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.mkdir;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BaseSwiftTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	private static final boolean USE_ARCH_ARM64 = false;
	private static final boolean VERBOSE = false;

	/**
	 * Path of the ANTLR runtime.
	 */
	private static final String ANTLR_RUNTIME_PATH;

	/**
	 * Absolute path to swift command.
	 */
	private static final String SWIFT_CMD;

	/**
	 * Environment variable name for swift home.
	 */
	private static final String SWIFT_HOME_ENV_KEY = "SWIFT_HOME";

	private static String getParent(String resourcePath, int count) {
		String result = resourcePath;
		while (count > 0) {
			int index = result.lastIndexOf('/');
			if (index > 0) {
				result = result.substring(0, index);
			}
			count -= 1;
		}
		return result;
	}

	static {
		Map<String, String> env = System.getenv();
		String swiftHome = env.containsKey(SWIFT_HOME_ENV_KEY) ? env.get(SWIFT_HOME_ENV_KEY) : "";
		SWIFT_CMD = swiftHome + "swift";

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// build swift runtime
		// path like: file:/Users/100mango/Desktop/antlr4/runtime-testsuite/target/classes/Swift
		URL swiftRuntime = loader.getResource("Swift");
		if (swiftRuntime == null) {
			throw new RuntimeException("Swift runtime file not found");
		}

		//enter project root
		ANTLR_RUNTIME_PATH = getParent(swiftRuntime.getPath(),4);
		try {
			fastFailRunProcess(ANTLR_RUNTIME_PATH, SWIFT_CMD, "build", "-c", "release");
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// shutdown logic
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					fastFailRunProcess(ANTLR_RUNTIME_PATH, SWIFT_CMD, "package", "clean");
				}
				catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected String getPropertyPrefix() {
		return "antrl4-swift";
	}

	/**
	 * Source files used in each small swift project.
	 */
	private final Set<String> sourceFiles = new HashSet<>();


	@Override
	public String execLexer(String grammarFileName, String grammarStr, String lexerName, String input, boolean showDFA) {
		generateParser(grammarFileName,
				grammarStr,
				null,
				lexerName);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		addSourceFiles("main.swift");

		String projectName = "testcase-" + System.currentTimeMillis();
		String projectDir = new File(getTempTestDir(), projectName).getAbsolutePath();
		try {
			buildProject(projectDir, projectName);
			return execTest(projectDir, projectName);
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr, String parserName, String lexerName, String listenerName, String visitorName, String startRuleName, String input, boolean showDiagnosticErrors) {
		generateParser(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		writeFile(getTempDirPath(), "input", input);
		return execParser(parserName,
				lexerName,
				startRuleName,
				showDiagnosticErrors,false);
	}

	private String execTest(String projectDir, String projectName) {
		try {
			Pair<String, String> output = runProcess(projectDir, "./.build/release/" + projectName, "input");
			if (output.b.length() > 0) {
				setParseErrors(output.b);
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

	private void buildProject(String projectDir, String projectName) throws IOException, InterruptedException {
		mkdir(projectDir);
		fastFailRunProcess(projectDir, SWIFT_CMD, "package", "init", "--type", "executable");
		for (String sourceFile: sourceFiles) {
			String absPath = new File(getTempTestDir(), sourceFile).getAbsolutePath();
			fastFailRunProcess(getTempDirPath(), "mv", "-f", absPath, projectDir + "/Sources/" + projectName);
		}
		fastFailRunProcess(getTempDirPath(), "mv", "-f", "input", projectDir);
		String dylibPath = ANTLR_RUNTIME_PATH + "/.build/release/";
//		System.err.println(dylibPath);
		Pair<String, String> buildResult = runProcess(projectDir, SWIFT_CMD, "build",
				"-c", "release",
				"-Xswiftc", "-I"+dylibPath,
				"-Xlinker", "-L"+dylibPath,
				"-Xlinker", "-lAntlr4",
				"-Xlinker", "-rpath",
				"-Xlinker", dylibPath);
		if (buildResult.b.length() > 0) {
			throw new IOException("unit test build failed: " + buildResult.a + "\n" + buildResult.b);
		}
	}

	static Boolean IS_MAC_ARM_64 = null;

	private static boolean isMacOSArm64() {
		if (IS_MAC_ARM_64 == null) {
			IS_MAC_ARM_64 = computeIsMacOSArm64();
			System.err.println("IS_MAC_ARM_64 = " + IS_MAC_ARM_64);
		}
		return IS_MAC_ARM_64;
	}

	private static boolean computeIsMacOSArm64() {
		String os = System.getenv("RUNNER_OS");
		if(os==null || !os.equalsIgnoreCase("macos"))
			return false;
		try {
			Process p = Runtime.getRuntime().exec("uname -a");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String uname = in.readLine();
			return uname.contains("_ARM64_");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Pair<String,String> runProcess(String execPath, String... args) throws IOException, InterruptedException {
		List<String> argsWithArch = new ArrayList<>();
		if(USE_ARCH_ARM64 && isMacOSArm64())
			argsWithArch.addAll(Arrays.asList("arch", "-arm64"));
		argsWithArch.addAll(Arrays.asList(args));
		if(VERBOSE)
			System.err.println("Executing " + argsWithArch + " " + execPath);
		final Process process = Runtime.getRuntime().exec(argsWithArch.toArray(new String[0]), null, new File(execPath));
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					process.destroy();
				} catch(Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}, 120_000);
		int status = process.waitFor();
		timer.cancel();
		stdoutVacuum.join();
		stderrVacuum.join();
		if(VERBOSE)
			System.err.println("Done executing " + argsWithArch + " " + execPath);
		if (status != 0) {
			System.err.println("Process exited with status " + status);
			throw new IOException("Process exited with status " + status + ":\n" + stdoutVacuum + "\n" + stderrVacuum);
		}
		return new Pair<>(stdoutVacuum.toString(), stderrVacuum.toString());
	}

	private static void fastFailRunProcess(String workingDir, String... command) throws IOException, InterruptedException {
		List<String> argsWithArch = new ArrayList<>();
		if(USE_ARCH_ARM64 && isMacOSArm64())
			argsWithArch.addAll(Arrays.asList("arch", "-arm64"));
		argsWithArch.addAll(Arrays.asList(command));
		if(VERBOSE)
			System.err.println("Executing " + argsWithArch + " " + workingDir);
		ProcessBuilder builder = new ProcessBuilder(argsWithArch.toArray(new String[0]));
		builder.directory(new File(workingDir));
		final Process process = builder.start();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					process.destroy();
				} catch(Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}, 120_000);
		int status = process.waitFor();
		timer.cancel();
		if(VERBOSE)
			System.err.println("Done executing " + argsWithArch + " " + workingDir);
		if (status != 0) {
			System.err.println("Process exited with status " + status);
			throw new IOException("Process exited with status " + status);
		}
	}

	@SuppressWarnings("SameParameterValue")
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
		String projectDir = new File(getTempTestDir(), projectName).getAbsolutePath();
		try {
			buildProject(projectDir, projectName);
			return execTest(projectDir, projectName);
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
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
						"let args = CommandLine.arguments\n" +
						"let input = try ANTLRFileStream(args[1])\n" +
						"let lex = <lexerName>(input)\n" +
						"let tokens = CommonTokenStream(lex)\n" +
						"<createParser>\n" +
						"parser.setBuildParseTree(true)\n" +
						"<profile>\n" +
						"let tree = try parser.<parserStartRuleName>()\n" +
						"<if(profile)>print(profiler.getDecisionInfo().description)<endif>\n" +
						"try ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree)\n"
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
			outputFileST.add("profile", new ArrayList<>());
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(getTempDirPath(), "main.swift", outputFileST.render());
	}

	private void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import Antlr4\n" +
						"import Foundation\n" +

						"setbuf(stdout, nil)\n" +
						"let args = CommandLine.arguments\n" +
						"let input = try ANTLRFileStream(args[1])\n" +
						"let lex = <lexerName>(input)\n" +
						"let tokens = CommonTokenStream(lex)\n" +

						"try tokens.fill()\n" +

						"for t in tokens.getTokens() {\n" +
						"	print(t)\n" +
						"}\n" +
						(showDFA ? "print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString(), terminator: \"\" )\n" : ""));

		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "main.swift", outputFileST.render());
	}

	/**
	 * Generates the parser for one test case.
	 */
	private void generateParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTempDirPath(), "Swift", grammarFileName, grammarStr, false, extraOptions);
		assertTrue(equeue.errors.isEmpty());
//		System.out.println(getTmpDir());

		List<String> files = new ArrayList<>();
		if (lexerName != null) {
			files.add(lexerName + ".swift");
		}

		if (parserName != null) {
			files.add(parserName + ".swift");
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
		addSourceFiles(files.toArray(new String[0]));
	}

}
