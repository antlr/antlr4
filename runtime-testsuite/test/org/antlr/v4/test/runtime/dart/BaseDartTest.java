/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.dart;

import org.antlr.v4.test.runtime.*;

import java.io.*;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.readFile;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;


public class BaseDartTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "Dart";
	}

	private static String cacheDartPackages;
	private static String cacheDartPackageConfig;

	public String getPropertyPrefix() {
		return "antlr-dart";
	}

	@Override
	public String execLexer(String grammarFileName,
							String grammarStr,
							String lexerName,
							String input,
							boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, false);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		return execClass("Test");
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
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, false, "-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false,
				false, listenerName != null, visitorName != null);
		return execClass("Test");
	}

	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													boolean defaultListener,
													String... extraOptions) {
		ErrorQueue errorQueue =
			BaseRuntimeTest.antlrOnString(getTempDirPath(), "Dart", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!errorQueue.errors.isEmpty()) {
			return false;
		}

		String runtime = getRuntimePath();
		writeFile(getTempDirPath(), "pubspec.yaml",
			"name: \"test\"\n" +
				"dependencies:\n" +
				"  antlr4:\n" +
				"    path: " + runtime + "\n" +
				"environment:\n" +
  				"  sdk: \">=2.12.0 <3.0.0\"\n");
		final File dartToolDir = new File(getTempDirPath(), ".dart_tool");
		if (cacheDartPackages == null) {
			try {
				ProcessorResult result = Processor.run(new String[]{locateDart(), "pub", "get"}, getTempDirPath());
				if (!result.errors.isEmpty()) {
					System.out.println("Pub Get error: " + result.errors);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			cacheDartPackages = readFile(getTempDirPath(), ".packages");
			cacheDartPackageConfig = readFile(dartToolDir.getAbsolutePath(), "package_config.json");
		} else {
			writeFile(getTempDirPath(), ".packages", cacheDartPackages);
			//noinspection ResultOfMethodCallIgnored
			dartToolDir.mkdir();
			writeFile(dartToolDir.getAbsolutePath(), "package_config.json", cacheDartPackageConfig);
		}
		return true; // allIsWell: no compile
	}

	public String execClass(String className) {
		try {
			String[] args = new String[]{
				locateDart(),
				className + ".dart", new File(getTempTestDir(), "input").getAbsolutePath()
			};

			ProcessorResult result = Processor.run(args, getTempDirPath());
			setParseErrors(result.errors);
			return result.output;
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
				: new String[]{"/usr/local/bin/", "/opt/local/bin/", "/opt/homebrew/bin/", "/usr/bin/", "/usr/lib/dart/bin/", "/usr/local/opt/dart/libexec"};

		for (String root : roots) {
			for (String t : tools) {
				if (new File(root + t).exists()) {
					return root + t;
				}
			}
		}

		throw new RuntimeException("Could not locate " + tool);
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
}
