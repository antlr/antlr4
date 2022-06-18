/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.antlr.v4.test.runtime.FileUtils.mkdir;
import static org.antlr.v4.test.runtime.FileUtils.moveFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.isWindows;

public class SwiftRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Swift";
	}

	@Override
	public String getTestFileName() {
		return "main";
	}

	private static final String antlrRuntimePath;
	private static final String buildSuffix;
	private static final String[] initPackageArgs;
	private static final String[] buildProjectArgs;
	private static final Map<String, String> environment;

	static {
		String swiftRuntimePath = getRuntimePath("Swift");
		antlrRuntimePath = Paths.get(swiftRuntimePath, "..", "..").normalize().toString();
		String libraryPath;
		String includePath;
		buildSuffix = isWindows() ? "x86_64-unknown-windows-msvc" : "";
		includePath = Paths.get(antlrRuntimePath, ".build", buildSuffix, "release").toString();
		environment = new HashMap<>();
		if (isWindows()) {
			libraryPath = Paths.get(includePath, "Antlr4.lib").toString();
			String path = System.getenv("PATH");
			environment.put("PATH", path == null ? includePath : path + ";" + includePath);
		}
		else {
			libraryPath = includePath;
		}
		initPackageArgs = new String[]{"swift", "package", "init", "--type", "executable"};
		buildProjectArgs = new String[]{
				"swift",
				"build",
				"-c",
				"release",
				"-Xswiftc",
				"-I" + includePath,
				"-Xlinker",
				"-L" + includePath,
				"-Xlinker",
				"-lAntlr4",
				"-Xlinker",
				"-rpath",
				"-Xlinker",
				libraryPath
		};
	}

	@Override
	protected void initRuntime() throws Exception {
		runCommand(new String[] {"swift", "build", "-c", "release"}, antlrRuntimePath, "build Swift runtime");
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		Exception exception = null;
		try {
			final String projectName = "Test";
			File testDir = tempTestDir.toFile();
			String projectDir = new File(testDir, projectName).getAbsolutePath();
			mkdir(projectDir);
			String destDir = Paths.get(projectDir, "Sources", projectName).toString();

			runCommand(initPackageArgs, projectDir);

			for (GeneratedFile generatedFile : generatedState.generatedFiles) {
				moveFile(testDir, destDir, generatedFile.name);
			}
			moveFile(testDir, destDir, getTestFileWithExt());

			runCommand(buildProjectArgs, projectDir);
		} catch (Exception e) {
			exception = e;
		}

		return new CompiledState(generatedState, exception);
	}

	@Override
	public String getRuntimeToolName() {
		return null;
	}

	@Override
	public String getExecFileName() {
		return Paths.get(getTempDirPath(),
				"Test",
				".build",
				isWindows() ? "x86_64-unknown-windows-msvc" : "",
				"release",
				"Test" + (isWindows() ? ".exe" : "")).toString();
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
