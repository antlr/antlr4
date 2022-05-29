/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.test.runtime.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BaseCSharpTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "CSharp";
	}

	@Override
	public String getExtension() { return "cs"; }

	private static Boolean isRuntimeInitialized = false;
	private final static String cSharpAntlrRuntimeDllName = "Antlr4.Runtime.Standard.dll";
	private final static String testProjectFileName = "Antlr4.Test.csproj";
	private static String cSharpTestProjectContent;
	private static final String cSharpCachingDirectory = Paths.get(cachingDirectory, "CSharp").toString();

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-csharp";
	}

	Set<String> sourceFiles = new HashSet<>();

	private void addSourceFiles(String... files) {
		Collections.addAll(sourceFiles, files);
	}

	@Override
	public String execLexer(String grammarFileName,
							String grammarStr,
							String lexerName,
							String input,
							boolean showDFA) {
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				null,
				lexerName);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		addSourceFiles("Test.cs");
		return execRecognizer();
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
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false, false,
				listenerName != null, visitorName != null);
		addSourceFiles("Test.cs");
		return execRecognizer();
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateRecognizer(String grammarFileName,
											String grammarStr,
											String parserName,
											String lexerName,
											String... extraOptions) {
		ErrorQueue errorQueue = antlrOnString(getTempDirPath(), "CSharp", grammarFileName, grammarStr, false, extraOptions);
		if (!errorQueue.errors.isEmpty()) {
			return false;
		}

		addSourceFiles(getGeneratedFiles(grammarFileName, lexerName, parserName, extraOptions).toArray(new String[0]));
		return true;
	}

	public String execRecognizer() {
		if (!buildProject()) {
			System.err.println("Failed to compile!");
			return getParseErrors();
		}
		String exec = new File(getTempTestDir(), "bin/Release/netcoreapp3.1/Test.dll").getAbsolutePath();
		String output;
		try {
			ProcessorResult result =
					Processor.run(new String[]{"dotnet", exec, new File(getTempTestDir(), "input").getAbsolutePath()},
							getTempDirPath());
			setParseErrors(result.errors);
			output = result.output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
			return null;
		}
		return output;
	}

	public boolean buildProject() {
		try {
			assertTrue(initializeRuntime());
			// save auxiliary files
			try (PrintWriter out = new PrintWriter(new File(getTempTestDir(), testProjectFileName))) {
				out.print(cSharpTestProjectContent);
			}

			// build test
			String[] args = new String[] { "dotnet", "build", testProjectFileName, "-c", "Release" };
			Processor.run(args, getTempDirPath());
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
	}

	private boolean initializeRuntime() {
		// Compile runtime project once per overall maven test session (assuming forkCount=0)
		synchronized (BaseCSharpTest.class) {
			if ( isRuntimeInitialized) {
//				System.out.println("C# runtime build REUSED\n");
				return true;
			}

			System.out.println("Building C# runtime\n");

			// find runtime package
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL runtimeProj = loader.getResource("CSharp/src/Antlr4.csproj");
			if (runtimeProj == null) {
				throw new RuntimeException("C# runtime project file not found!");
			}
			File runtimeProjFile = new File(runtimeProj.getFile());
			String runtimeProjPath = runtimeProjFile.getPath();

			RuntimeTestUtils.mkdir(cSharpCachingDirectory);
			String[] args = new String[]{
					"dotnet",
					"build",
					runtimeProjPath,
					"-c",
					"Release",
					"-o",
					cSharpCachingDirectory
			};

			boolean success;
			try {
				String cSharpTestProjectResourceName = BaseCSharpTest.class.getPackage().getName().replace(".", "/") + "/";
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(cSharpTestProjectResourceName + testProjectFileName);
				int bufferSize = 1024;
				char[] buffer = new char[bufferSize];
				StringBuilder out = new StringBuilder();
				Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
					out.append(buffer, 0, numRead);
				}
				cSharpTestProjectContent = out.toString().replace(cSharpAntlrRuntimeDllName, Paths.get(cSharpCachingDirectory, cSharpAntlrRuntimeDllName).toString());

				success = Processor.run(args, cSharpCachingDirectory).isSuccess();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				success = false;
			}

			if (success) System.out.println("C# runtime build succeeded\n");
			else System.out.println("C# runtime build failed\n");

			isRuntimeInitialized = true; // try only once
			return success;
		}
	}
}
