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

import static org.antlr.v4.test.runtime.FileUtils.mkdir;
import static org.antlr.v4.test.runtime.FileUtils.moveFile;

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
	private static final String[] initPackageArgs;
	private static final String[] buildProjectArgs;

	static {
		String swiftRuntimePath = getRuntimePath("Swift");
		antlrRuntimePath = Paths.get(swiftRuntimePath, "..", "..").normalize().toString();
		String dylibPath = antlrRuntimePath + "/.build/release/";
		initPackageArgs = new String[]{"swift", "package", "init", "--type", "executable"};
		buildProjectArgs = new String[]{"swift", "build", "-c", "release", "-Xswiftc", "-I" + dylibPath, "-Xlinker", "-L" + dylibPath,
				"-Xlinker", "-lAntlr4", "-Xlinker", "-rpath", "-Xlinker", dylibPath};
	}

	@Override
	protected void initRuntime() throws Exception {
		runCommand(new String[] {"swift", "build", "-c", "release"}, antlrRuntimePath, "build Swift runtime");
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		Exception exception = null;
		try {
			String projectName = "Test";
			File testDir = tempTestDir.toFile();
			String projectDir = new File(testDir, projectName).getAbsolutePath();
			mkdir(projectDir);
			String destDir = projectDir + "/Sources/" + projectName;

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
		return Paths.get(getTempDirPath(), "Test/.build/release/Test").toString();
	}
}
