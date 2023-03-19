/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.isWindows;

public class NodeRunner extends RuntimeRunner {
	private final static String NPM_EXEC = "npm" + (isWindows() ? ".cmd" : "");

	@Override
	public String getLanguage() {
		return "JavaScript";
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		npmLinkRuntime();
	}

	private void npmLinkRuntime() throws Exception {
		Processor.run(new String[] {NPM_EXEC, "--silent", "install"}, normalizedRuntimePath);
		Processor.run(new String[] {NPM_EXEC, "--silent", "run", "build"}, normalizedRuntimePath);
		Processor.run(new String[] {NPM_EXEC, "--silent", "link"}, normalizedRuntimePath);
	}

	@Override
	public String getExtension() { return "js"; }

	@Override
	public String getBaseListenerSuffix() { return null; }

	@Override
	public String getBaseVisitorSuffix() { return null; }

	@Override
	public String getRuntimeToolName() { return "node"; }

	private final static String normalizedRuntimePath = getRuntimePath("JavaScript").replace('\\', '/');

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		try {
			writeFile(getTempDirPath(), "package.json",
					RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/package_js.json"));

			npmLinkAntlr4();

			return new CompiledState(generatedState, null);

		} catch (Exception e) {
			return new CompiledState(generatedState, e);
		}
	}

	private void npmLinkAntlr4() throws Exception {
		Processor.run(new String[] {NPM_EXEC, "--silent", "link", "antlr4"}, getTempDirPath());
	}

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("runtimePath", normalizedRuntimePath);
	}
}
