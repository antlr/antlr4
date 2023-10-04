/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.antlr.v4.test.runtime.FileUtils.*;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;

public class GoRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Go";
	}

	@Override
	public String getLexerSuffix() {
		return "_lexer";
	}

	@Override
	public String getParserSuffix() {
		return "_parser";
	}

	@Override
	public String getBaseListenerSuffix() {
		return "_base_listener";
	}

	@Override
	public String getListenerSuffix() {
		return "_listener";
	}

	@Override
	public String getBaseVisitorSuffix() {
		return "_base_visitor";
	}

	@Override
	public String getVisitorSuffix() {
		return "_visitor";
	}

	@Override
	protected String grammarNameToFileName(String grammarName) {
		return grammarName.toLowerCase();
	}

	@Override
	public String[] getExtraRunArgs() {
		return new String[]{"run"};
	}

	private static final String GoRuntimeImportPath = "github.com/antlr4-go/antlr/v4";

	private final static Map<String, String> environment;

	private static String cachedGoMod;
	private static String cachedGoSum;
	private static ArrayList<String> options = new ArrayList<>();

	static {
		environment = new HashMap<>();
		environment.put("GOWORK", "off");
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String cachePath = getCachePath();
		mkdir(cachePath);
		Path runtimeFilesPath = Paths.get(getRuntimePath("Go"), "antlr", "v4");
		String runtimeToolPath = getRuntimeToolPath();
		File goModFile = new File(cachePath, "go.mod");
		if (goModFile.exists())
			if (!goModFile.delete())
				throw new IOException("Can't delete " + goModFile);
		Processor.run(new String[]{runtimeToolPath, "mod", "init", "test"}, cachePath, environment);
		Processor.run(new String[]{runtimeToolPath, "mod", "edit",
				"-replace=" + GoRuntimeImportPath + "=" + runtimeFilesPath}, cachePath, environment);
		Processor.run(new String[]{runtimeToolPath, "mod", "edit",
				"-require=" + GoRuntimeImportPath + "@v4.0.0"}, cachePath, environment);
		cachedGoMod = readFile(cachePath + FileSeparator, "go.mod");
	}

	@Override
	protected String grammarParseRuleToRecognizerName(String startRuleName) {
		if (startRuleName == null || startRuleName.length() == 0) {
			return null;
		}

		// The rule name start is now translated to Start_ at runtime to avoid clashes with labels.
		// Some tests use start as the first rule name, and we must cater for that
		//
		String rn = startRuleName.substring(0, 1).toUpperCase() + startRuleName.substring(1);
		switch (rn) {
			case "Start":
			case "End":
			case "Exception":
				rn += "_";
			default:
		}
		return rn;
	}

	@Override
	protected List<String> getTargetToolOptions(RunOptions ro) {
		// Unfortunately this cannot be cached because all the synchronization is out of whack, and
		// we end up return the options before they are populated. I prefer to make this small change
		// at the expense of an object rather than try to change teh synchronized initialization, which is
		// very fragile.
		// Also, the options may need to change in the future according to the test options. This is safe
		ArrayList<String> options = new ArrayList<>();
		options.add("-o");
		options.add(tempTestDir.resolve("parser").toString());
		return options;
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		// We have already created a suitable go.mod file, though it may need to have go mod tidy run on it one time
		//
		writeFile(getTempDirPath(), "go.mod", cachedGoMod);

		// We need to run a go mod tidy once, now that we have source code. This will generate a valid go.sum file and
		// recognize the indirect requirements in the go.mod file. Then we re-cache the go.mod and cache
		// the go.sum and therefore save sparking a new process for all the remaining go tests. This is probably
		// a race condition as these tests are run in parallel, but it does not matter as they are all going to
		// generate the same go.mod and go.sum file anyway.
		//
		Exception ex = null;
		if (cachedGoSum == null) {
			try {
				Processor.run(new String[]{getRuntimeToolPath(), "mod", "tidy"}, getTempDirPath(), environment);
			} catch (InterruptedException | IOException e) {
				ex = e;
			}
			cachedGoMod = readFile(getTempDirPath() + FileSeparator, "go.mod");
			cachedGoSum = readFile(getTempDirPath() + FileSeparator, "go.sum");
		}

		// We can now write the go.sum file, which will allow the go compiler to build the module
		//
		writeFile(getTempDirPath(), "go.sum", cachedGoSum);

		return new CompiledState(generatedState, ex);
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
