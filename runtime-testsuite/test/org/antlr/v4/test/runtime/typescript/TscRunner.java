/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.typescript;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.io.File;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;

public class TscRunner extends RuntimeRunner {

	/* TypeScript runtime is the same as JavaScript runtime */
	private final static String NORMALIZED_JAVASCRIPT_RUNTIME_PATH = getRuntimePath("JavaScript").replace('\\', '/');

	@Override
	public String getLanguage() {
		return "TypeScript";
	}

	@Override
	protected void initRuntime() throws Exception {
		installTsc();
		npmLinkRuntime();
	}

	private void installTsc() throws Exception {
		Processor.run(new String[] {"npm", "--silent", "install", "typescript", "--save-dev"}, NORMALIZED_JAVASCRIPT_RUNTIME_PATH);
	}

	private void npmLinkRuntime() throws Exception {
		File dir = new File(NORMALIZED_JAVASCRIPT_RUNTIME_PATH);
		if(!dir.exists())
			throw new RuntimeException("Can't locate JavaScript runtime!");
		Processor.run(new String[] {"npm", "--silent", "link"}, NORMALIZED_JAVASCRIPT_RUNTIME_PATH);
	}

	@Override
	public String getExtension() { return "ts"; }

	@Override
	protected String getExecFileName() { return getTestFileName() + ".js"; }

	@Override
	public String getBaseListenerSuffix() { return null; }

	@Override
	public String getBaseVisitorSuffix() { return null; }

	@Override
	public String getRuntimeToolName() { return "node"; }

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {

		try {
			writeFile(getTempDirPath(), "package.json",
					RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/package_ts.json"));

			writeFile(getTempDirPath(), "tsconfig.json",
					RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/tsconfig.json"));

			npmInstall();

			npmLinkAntlr4();

			tscCompile();

			return new CompiledState(generatedState, null);

		} catch (Exception e) {
			return new CompiledState(generatedState, e);
		}

	}

	private void npmInstall() throws Exception {
		Processor.run(new String[] {"npm", "--silent", "install"}, getTempDirPath());
	}

	private void npmLinkAntlr4() throws Exception {
		Processor.run(new String[] {"npm", "--silent", "link", "antlr4"}, getTempDirPath());
	}

	private void tscCompile() throws Exception {
		Processor.run(new String[] {"tsc", "--project", "tsconfig.json"}, getTempDirPath());
	}

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("runtimePath", NORMALIZED_JAVASCRIPT_RUNTIME_PATH);
	}
}
