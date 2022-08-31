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

import static org.antlr.v4.test.runtime.FileUtils.replaceInFile;

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

	private final static String antlrTestPackageName = "antlr";
	private static final String goModFileName = "go.mod";
	private static final String GoRuntimeImportPath = "github.com/antlr/antlr4/runtime/Go/antlr/v4";
	private static final Path packageBase;
	private static final String packageBaseString;
	private static String goModContent = null;

	private final static Map<String, String> environment;

	static {
		packageBase = Paths.get(cacheDirectory, "Go");
		packageBaseString = packageBase.toString();
		environment = new HashMap<>();
		environment.put("GOWORK", "off");
	}

	@Override
	protected String grammarParseRuleToRecognizerName(String startRuleName) {
		if (startRuleName == null || startRuleName.length() == 0) {
			return null;
		}

		return startRuleName.substring(0, 1).toUpperCase() + startRuleName.substring(1);
	}

	@Override
	protected void initRuntime() throws Exception {
		// As we are using modules for the tests, we can use the `replace` directive to point
		// the tests at the runtime we are testing. We could use GOWORK, but then we would
		// have to remove each test module and add in the current one. So, we turn GOWORK off
		// as developers are quite likely to have that from go 1.18 onwards so that their copy
		// of the go runtime is source locally without messing with their go.mod files and
		// accidentally checking them in with a `replace` directive in there.
		//
		// So here, we need do nothing to initialize the runtime.
		//
	}

	static class GoFileFilter implements FilenameFilter {
		public final static GoFileFilter Instance = new GoFileFilter();

		public boolean accept(File dir, String name) {
			return name.endsWith(".go");
		}
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {

		List<GeneratedFile> generatedFiles = generatedState.generatedFiles;
		String tempDirPath = getTempDirPath();
		Path runtimeFiles = Paths.get(getRuntimePath("Go"), "antlr/v4");
		File generatedFir = new File(tempDirPath, "parser");
		if (!generatedFir.mkdir()) {
			return new CompiledState(generatedState, new Exception("can't make dir " + generatedFir));
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

		ProcessorResult pr = null;
		try {
			pr = Processor.run(new String[]{getRuntimeToolPath(), "mod", "init", "test"}, tempDirPath, environment);
			pr = Processor.run(new String[]{getRuntimeToolPath(), "mod", "edit", "-replace="+GoRuntimeImportPath+"="+runtimeFiles.toString()}, tempDirPath, environment);
			pr = Processor.run(new String[]{getRuntimeToolPath(), "mod", "tidy"}, tempDirPath, environment);
		} catch (InterruptedException | IOException e) {
			System.out.println("Output:");
			System.out.println(pr.output);
			System.out.println("Errors:");
			System.out.println(pr.errors);

			throw new RuntimeException(e);
		}



				return new CompiledState(generatedState, null);
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
