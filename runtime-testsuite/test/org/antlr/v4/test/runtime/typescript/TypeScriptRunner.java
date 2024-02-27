/*
 * Copyright (c) 2012-2023 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.typescript;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.antlr.v4.test.runtime.FileUtils.*;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.isWindows;
public class TypeScriptRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "TypeScript";
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String runtimePath = getRuntimePath("JavaScript");
		String cachePath = getCachePath();

		copyDirectory(Paths.get(runtimePath), Paths.get(cachePath), StandardCopyOption.REPLACE_EXISTING);

		String npmExec = "npm" + (isWindows() ? ".cmd" : "");
		String webpackExec = "webpack" + (isWindows() ? ".cmd" : "");

		// link runtime
		Processor.run(new String[] {npmExec, "install"}, cachePath);
		Processor.run(new String[] {webpackExec}, cachePath);
		Processor.run(new String[] {npmExec, "link"}, cachePath);

		writePackageJson(cachePath);

		Processor.run(new String[] {npmExec, "install"}, cachePath);
		Processor.run(new String[] {npmExec, "link", "antlr4"}, cachePath);
	}

	@Override
	public String getExtension() { return "ts"; }

	@Override
	protected String getExecFileName() { return getTestFileName() + ".ts"; }

	@Override
	public String getBaseListenerSuffix() { return null; }

	@Override
	public String getBaseVisitorSuffix() { return null; }

	@Override
	public String getRuntimeToolName() { return "ts-node"  + (isWindows() ? ".cmd" : ""); }

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		Exception ex = null;
		try {
			String tempDirPath = getTempDirPath();

			writePackageJson(tempDirPath);

			writeFile(tempDirPath, "tsconfig.json",
					RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/tsconfig.json"));

			// IMPORTANT: The process should be run with admin privileges to make possible to create symlinks.
			Files.createSymbolicLink(Paths.get(getTempDirPath(), "node_modules"), Paths.get(getCachePath(), "node_modules"));
		} catch (Exception e) {
			ex = e;
		}
		return new CompiledState(generatedState, ex);
	}

	private void writePackageJson(String targetDirPath) {
		writeFile(targetDirPath, "package.json",
				RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/package_ts.json"));
	}
}
