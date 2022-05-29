/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.test.runtime.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.*;

public class BaseNodeTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "JavaScript";
	}

	@Override
	public String getExtension() { return "js"; }

	@Override
	public String getBaseListenerSuffix() { return null; }

	@Override
	public String getBaseVisitorSuffix() { return null; }

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-javascript";
	}

	@Override
	public  String execLexer(String grammarFileName, String grammarStr,
	                         String lexerName, String input, boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr, null, lexerName);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		writeFile(getTempDirPath(), "package.json", "{\"type\": \"module\"}");
		String output = execModule("Test.js");
		if ( output!=null && output.length()==0 ) {
			output = null;
		}
		return output;
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr,
	                         String parserName, String lexerName, String listenerName,
	                         String visitorName, String startRuleName, String input,
	                         boolean showDiagnosticErrors)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
				grammarStr, parserName, lexerName, "-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false,
				false, listenerName != null, visitorName != null);
		writeFile(getTempDirPath(), "package.json", "{\"type\": \"module\"}");
		return execModule("Test.js");
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr, String parserName, String lexerName,
	                                                String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTempDirPath(), "JavaScript", grammarFileName, grammarStr,
				false, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = getGeneratedFiles(grammarFileName, lexerName, parserName, extraOptions);

		String newImportAntlrString = "import antlr4 from 'file://" + getRuntimePath() + "/src/antlr4/index.js'";
		for (String file : files) {
			Path path = Paths.get(getTempDirPath(), file);
			try {
				String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
				String newContent = content.replaceAll("import antlr4 from 'antlr4';", newImportAntlrString);
				try (PrintWriter out = new PrintWriter(path.toString())) {
					out.println(newContent);
				}
			} catch (IOException e) {
				fail("File not found: " + path);
			}
		}

		return true; // allIsWell: no compile
	}

	public String execModule(String fileName) {
		try {
			String modulePath = new File(getTempTestDir(), fileName).getAbsolutePath();
			String nodejsPath = locateNodeJS();
			String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
			ProcessBuilder builder = new ProcessBuilder(nodejsPath, modulePath,
					inputPath);
			builder.environment().put("NODE_PATH", getTempDirPath());
			builder.directory(getTempTestDir());
			Process process = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(
					process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(
					process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(1L, TimeUnit.MINUTES))
			//	process.destroyForcibly();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();
			if ( output.length()==0 ) {
				output = null;
			}
			if (stderrVacuum.toString().length() > 0) {
				setParseErrors(stderrVacuum.toString());
			}
			return output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
			System.err.println();
			return null;
		}
	}

	private boolean canExecute(String tool) {
		try {
			ProcessBuilder builder = new ProcessBuilder(tool, "--version");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamVacuum vacuum = new StreamVacuum(process.getInputStream());
			vacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(30L, TimeUnit.SECONDS))
			//	process.destroyForcibly();
			vacuum.join();
			return process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private String locateNodeJS() {
		// typically /usr/local/bin/node
		String prop = System.getProperty("antlr-javascript-nodejs");
		if ( prop!=null && prop.length()!=0 ) {
			if(prop.contains(" "))
				prop = "\"" + prop + "\"";
			return prop;
		}
		if (canExecute("nodejs")) {
			return "nodejs"; // nodejs on Debian without node-legacy package
		}
		return "node"; // everywhere else
	}
}
