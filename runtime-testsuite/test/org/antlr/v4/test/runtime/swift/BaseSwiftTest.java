/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.test.runtime.*;

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
	@Override
	public String getLanguage() {
		return "Swift";
	}

	@Override
	public String getTestFileName() {
		return "main";
	}

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
		writeLexerFile(lexerName, showDFA);
		addSourceFiles("main.swift");

		String projectName = "testcase-" + System.currentTimeMillis();
		String projectDir = new File(getTempTestDir(), projectName).getAbsolutePath();
		try {
			buildProject(projectDir, projectName);
			return execTest(projectDir, projectName);
		}
		catch (Exception e) {
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
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false,
				false, listenerName != null, visitorName != null);

		addSourceFiles("main.swift");
		String projectName = "testcase-" + System.currentTimeMillis();
		String projectDir = new File(getTempTestDir(), projectName).getAbsolutePath();
		try {
			buildProject(projectDir, projectName);
			return execTest(projectDir, projectName);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String execTest(String projectDir, String projectName) {
		try {
			ProcessorResult result = Processor.run(
					new String[]{ projectDir, "./.build/release/" + projectName, "input"},
					getTempDirPath());
			setParseErrors(result.errors);
			return result.output;
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

	private void buildProject(String projectDir, String projectName) throws Exception {
		mkdir(projectDir);
		fastFailRunProcess(projectDir, SWIFT_CMD, "package", "init", "--type", "executable");
		for (String sourceFile: sourceFiles) {
			String absPath = new File(getTempTestDir(), sourceFile).getAbsolutePath();
			fastFailRunProcess(getTempDirPath(), "mv", "-f", absPath, projectDir + "/Sources/" + projectName);
		}
		fastFailRunProcess(getTempDirPath(), "mv", "-f", "input", projectDir);
		String dylibPath = ANTLR_RUNTIME_PATH + "/.build/release/";
		Processor.run(new String[]{projectDir, SWIFT_CMD, "build",
				"-c", "release",
				"-Xswiftc", "-I" + dylibPath,
				"-Xlinker", "-L" + dylibPath,
				"-Xlinker", "-lAntlr4",
				"-Xlinker", "-rpath",
				"-Xlinker", dylibPath
		}, getTempDirPath());
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

	private static void fastFailRunProcess(String workingDir, String... command) throws IOException, InterruptedException {
		List<String> argsWithArch = new ArrayList<>();
		argsWithArch.addAll(Arrays.asList(command));
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
		if (status != 0) {
			System.err.println("Process exited with status " + status);
			throw new IOException("Process exited with status " + status);
		}
	}

	/**
	 * Generates the parser for one test case.
	 */
	private void generateParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String... extraOptions) {
		ErrorQueue errorQueue = antlrOnString(getTempDirPath(), "Swift", grammarFileName, grammarStr, false, extraOptions);
		assertTrue(errorQueue.errors.isEmpty());
		List<String> files = getGeneratedFiles(grammarFileName, lexerName, parserName, extraOptions);
		addSourceFiles(files.toArray(new String[0]));
	}
}
