/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.php;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

import org.antlr.v4.test.runtime.*;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BasePHPTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "PHP";
	}

	public String getPropertyPrefix() {
		return "antlr-php";
	}

	@Override
	public String execLexer(
		String grammarFileName,
		String grammarStr,
		String lexerName,
		String input,
		boolean showDFA
	) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		return execModule("Test.php");
	}

	public String execParser(
		String grammarFileName,
		String grammarStr,
		String parserName,
		String lexerName,
		String listenerName,
		String visitorName,
		String startRuleName,
		String input,
		boolean showDiagnosticErrors
	) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, "-visitor");

		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);

		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false,
				false, listenerName != null, visitorName != null);

		return execModule("Test.php");
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(
		String grammarFileName,
		String grammarStr,
		String... extraOptions
	) {
		ErrorQueue enqueue = antlrOnString(getTempDirPath(), "PHP", grammarFileName, grammarStr, false, extraOptions);
		return enqueue.errors.isEmpty();
	}

	public String execModule(String fileName) {
		String phpPath = locatePhp();
		String runtimePath = getRuntimePath();

		String modulePath = new File(getTempTestDir(), fileName).getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
		Path outputPath = getTempTestDir().toPath().resolve("output").toAbsolutePath();

		try {
			HashMap<String, String> environment = new HashMap<>();
			environment.put("RUNTIME", runtimePath);
			ProcessorResult result = Processor.run(new String[]{phpPath, modulePath, inputPath, outputPath.toString()},
					getTempDirPath(),
					environment);

			setParseErrors(result.errors);
			return result.output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String locateTool(String tool) {
		final String phpPath = System.getProperty("PHP_PATH");

		if (phpPath != null && new File(phpPath).exists()) {
			return phpPath;
		}

		String[] roots = {"/usr/local/bin/", "/opt/local/bin", "/opt/homebrew/bin/", "/usr/bin/"};

		for (String root: roots) {
			if (new File(root + tool).exists()) {
				return root + tool;
			}
		}

		return "php";
	}

	protected String locatePhp() {
		String propName = getPropertyPrefix() + "-php";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("php");
		}

		File file = new File(prop);

		if (!file.exists()) {
			return "php";
		}

		return file.getAbsolutePath();
	}
}
