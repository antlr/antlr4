/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.test.runtime.BaseRuntimeTestSupport;
import org.antlr.v4.test.runtime.RunOptions;
import org.antlr.v4.test.runtime.RuntimeTestUtils;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;

public class BaseNodeTest extends BaseRuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "JavaScript";
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
	private final static String newImportAntlrString =
			"import antlr4 from 'file://" + normalizedRuntimePath + "/src/antlr4/index.js'";

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		List<String> generatedFiles = generatedState.generatedFiles;
		for (String file : generatedFiles) {
			try {
				Path path = Paths.get(getTempDirPath(), file);
				String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
				String newContent = content.replaceAll("import antlr4 from 'antlr4';", newImportAntlrString);
				try (PrintWriter out = new PrintWriter(path.toString())) {
					out.println(newContent);
				}
			} catch (IOException e) {
				return new CompiledState(generatedState, e);
			}
		}

		writeFile(getTempDirPath(), "package.json",
				RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/package.json"));
		return new CompiledState(generatedState, null);
	}

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("runtimePath", normalizedRuntimePath);
	}
}
