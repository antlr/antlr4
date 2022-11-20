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

	private static final String GoRuntimeImportPath = "github.com/antlr/antlr4/runtime/Go/antlr/v4";

	private final static Map<String, String> environment;

	private static String cachedGoMod;

	static {
		environment = new HashMap<>();
		environment.put("GOWORK", "off");
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String cachePath = getCachePath();
		mkdir(cachePath);
		Path runtimeFilesPath = Paths.get(getRuntimePath("Go"), "antlr");
		String runtimeToolPath = getRuntimeToolPath();
		File goModFile = new File(cachePath, "go.mod");
		if (goModFile.exists())
			if (!goModFile.delete())
				throw new IOException("Can't delete " + goModFile);
		Processor.run(new String[] {runtimeToolPath, "mod", "init", "test"}, cachePath, environment);
		Processor.run(new String[] {runtimeToolPath, "mod", "edit",
				"-replace=" + GoRuntimeImportPath + "=" + runtimeFilesPath}, cachePath, environment);
		cachedGoMod = readFile(cachePath + FileSeparator, "go.mod");
	}

	@Override
	protected String grammarParseRuleToRecognizerName(String startRuleName) {
		if (startRuleName == null || startRuleName.length() == 0) {
			return null;
		}

		return startRuleName.substring(0, 1).toUpperCase() + startRuleName.substring(1);
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		List<GeneratedFile> generatedFiles = generatedState.generatedFiles;
		String tempDirPath = getTempDirPath();
		File generatedParserDir = new File(tempDirPath, "parser");
		if (!generatedParserDir.mkdir()) {
			return new CompiledState(generatedState, new Exception("can't make dir " + generatedParserDir));
		}

		// The generated files seem to need to be in the parser subdirectory.
		// We have no need to change the import of the runtime because of go mod replace so, we could just generate them
		// directly in to the parser subdir. But in case down the line, there is some reason to want to replace things in
		// the generated code, then I will leave this here, and we can use replaceInFile()
		//
		for (GeneratedFile generatedFile : generatedFiles) {
			try {
				Path originalFile = Paths.get(tempDirPath, generatedFile.name);
				Files.move(originalFile, Paths.get(tempDirPath, "parser", generatedFile.name));
			} catch (IOException e) {
				return new CompiledState(generatedState, e);
			}
		}

		writeFile(tempDirPath, "go.mod", cachedGoMod);
		Exception ex = null;
		try {
			Processor.run(new String[] {getRuntimeToolPath(), "mod", "tidy"}, tempDirPath, environment);
		} catch (InterruptedException | IOException e) {
			ex = e;
		}

		return new CompiledState(generatedState, ex);
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
