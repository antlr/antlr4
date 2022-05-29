/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.python;

import org.antlr.v4.test.runtime.*;
import org.junit.runner.Description;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.*;

public abstract class BasePythonTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getExtension() { return "py"; }

	@Override
	protected void testSucceeded(Description description) {
		eraseTempPyCache();
		eraseTempDir();
	}

	@Override
	protected String getPropertyPrefix() {
		return "antlr-" + getLanguage().toLowerCase();
	}

	@Override
	public  String execLexer(String grammarFileName,
	                         String grammarStr,
	                         String lexerName,
	                         String input,
	                         boolean showDFA)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr,
		                                                null,
		                                                lexerName,"-no-listener");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		return execModule("Test.py");
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
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		setParseErrors(null);
		writeRecognizerFile(lexerName, parserName, startRuleName, showDiagnosticErrors, false);
		return execRecognizer();
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr,
	                                                String parserName,
	                                                String lexerName,
	                                                String... extraOptions)
	{
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr,
	                                                String parserName,
	                                                String lexerName,
	                                                boolean defaultListener,
	                                                String... extraOptions)
	{
		ErrorQueue equeue = antlrOnString(getTempDirPath(), getLanguage(), grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if ( lexerName!=null ) {
			files.add(lexerName+".py");
		}
		if ( parserName!=null ) {
			files.add(parserName+".py");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Listener.py");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Visitor.py");
			}
		}
		return true; // allIsWell: no compile
	}

	public String execRecognizer() {
		return execModule("Test.py");
	}

	public String execModule(String fileName) {
		String pythonPath = locatePython();
		String runtimePath = Paths.get(getRuntimePath(), "src").toString();
		File tmpdirFile = new File(getTempDirPath());
		String modulePath = new File(tmpdirFile, fileName).getAbsolutePath();
		String inputPath = new File(tmpdirFile, "input").getAbsolutePath();
		Path outputPath = tmpdirFile.toPath().resolve("output").toAbsolutePath();
		try {
			ProcessBuilder builder = new ProcessBuilder( pythonPath, modulePath, inputPath, outputPath.toString() );
			builder.environment().put("PYTHONPATH",runtimePath);
			builder.environment().put("PYTHONIOENCODING", "utf-8");
			builder.directory(tmpdirFile);
			Process process = builder.start();
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stderrVacuum.start();
			process.waitFor();
			stderrVacuum.join();
			String output = TestOutputReading.read(outputPath);
			if ( stderrVacuum.toString().length()>0 ) {
				setParseErrors(stderrVacuum.toString());
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String locateTool(List<String> tools) {
		String[] roots = {
			"/opt/local/bin", "/usr/bin/", "/usr/local/bin/",
			"/Users/"+System.getProperty("user.name")+"/anaconda3/bin/",
			"/Users/"+System.getProperty("user.name")+"/opt/anaconda3/bin/"
		};
		for(String root : roots) {
			for (String tool : tools) {
				if ( new File(root+tool).exists() ) {
					return root+tool;
				}
			}
		}
		return "python";
	}

	protected String locatePython() {
		String propName = getPropertyPrefix() + "-python";
		String prop = System.getProperty(propName);
		if(prop==null || prop.length()==0)
			prop = locateTool(getPythonExecutables());
		File file = new File(prop);
		if(!file.exists())
			return "python";
		return file.getAbsolutePath();
	}

	protected abstract List<String> getPythonExecutables();

	protected void eraseTempPyCache() {
		File tmpdirF = new File(getTempTestDir() + "/__pycache__");
		if ( tmpdirF.exists() ) {
			eraseFilesInDir(tmpdirF);
			tmpdirF.delete();
		}
	}
}
