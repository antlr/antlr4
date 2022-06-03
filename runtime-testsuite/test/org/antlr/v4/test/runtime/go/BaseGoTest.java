/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;


import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BaseGoTest extends BaseRuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "Go";
	}

	@Override
	public String getLexerSuffix() { return "_lexer"; }

	@Override
	public String getParserSuffix() { return "_parser"; }

	@Override
	public String getBaseListenerSuffix() { return "_base_listener"; }

	@Override
	public String getListenerSuffix() { return "_listener"; }

	@Override
	public String getBaseVisitorSuffix() { return "_base_visitor"; }

	@Override
	public String getVisitorSuffix() { return "_visitor"; }

	@Override
	protected String grammarNameToFileName(String grammarName) {
		return grammarName.toLowerCase();
	}

	@Override
	public String[] getExtraRunArgs() { return new String[] { "run" }; }

	private final static String antlrTestPackageName = "antlr";
	private static final String goModFileName = "go.mod";
	private static final String GoRuntimeImportPath = "github.com/antlr/antlr4/runtime/Go/antlr";
	private static final Path newGoRoot;
	private static final String newGoRootString;
	private static String goModContent = null;

	private final static Map<String, String> environment;

	static {
		newGoRoot = Paths.get(cacheDirectory, "Go");
		newGoRootString = newGoRoot.toString();
		environment = new HashMap<>();
		environment.put("GOROOT", newGoRootString);
	}

	@Override
	protected String grammarParseRuleToRecognizerName(String startRuleName) {
		if (startRuleName == null) {
			return null;
		}

		return startRuleName.substring(0, 1).toUpperCase() + startRuleName.substring(1);
	}

	@Override
	protected void initRuntime() throws Exception {
		String goRoot = runCommand(new String[]{"go", "env", "GOROOT"}, null, "get GO root").output.trim();

		try {
			File newGoRootDirectory = newGoRoot.toFile();
			if (newGoRootDirectory.exists())
				FileUtils.deleteDirectory(newGoRootDirectory);
			FileUtils.copyDirectory(Paths.get(goRoot), newGoRoot);

			String packageDir = Paths.get(newGoRootString, "src", antlrTestPackageName).toString();
			FileUtils.mkdir(packageDir);
			File[] runtimeFiles = Paths.get(getRuntimePath("Go"), "antlr").toFile().listFiles(new GoFileFilter());
			if (runtimeFiles == null) {
				Assert.fail("Go runtime file list is empty.");
			}

			for (File runtimeFile : runtimeFiles) {
				File dest = new File(packageDir, runtimeFile.getName());
				FileUtils.copyFile(runtimeFile, dest);
			}
		}
		catch (IOException ioException) {
			throw new Exception("update GO runtime in " + newGoRoot, ioException);
		}

		try {
			new File(newGoRootString, goModFileName).deleteOnExit();
			Processor.run(new String[]{"go", "mod", "init", "test"}, newGoRootString);
			goModContent = new String(Files.readAllBytes(Paths.get(newGoRootString, goModFileName)), StandardCharsets.UTF_8);
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		List<String> files = generatedState.generatedFiles;
		String tempDirPath = getTempDirPath();
		File generatedFir = new File(tempDirPath, "parser");
		if (!generatedFir.mkdir()) {
			return new CompiledState(generatedState, new Exception("can't make dir " + generatedFir));
		}
		for (String file : files) {
			try {
				Path originalFile = Paths.get(tempDirPath, file);
				String content = new String(Files.readAllBytes(originalFile), StandardCharsets.UTF_8);
				String newContent = content.replaceAll(GoRuntimeImportPath, antlrTestPackageName);
				try (PrintWriter out = new PrintWriter(Paths.get(tempDirPath, "parser", file).toString())) {
					out.println(newContent);
				}
				originalFile.toFile().delete();
			} catch (IOException e) {
				return new CompiledState(generatedState, e);
			}
		}

		try (PrintWriter out = new PrintWriter(Paths.get(tempDirPath, goModFileName).toString())) {
			out.println(goModContent);
		} catch (FileNotFoundException e) {
			return new CompiledState(generatedState, e);
		}

		return new CompiledState(generatedState, null);
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
