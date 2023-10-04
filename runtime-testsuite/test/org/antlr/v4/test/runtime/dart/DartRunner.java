/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.dart;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.io.*;

import static org.antlr.v4.test.runtime.FileUtils.*;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;

public class DartRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Dart";
	}

	private static String cacheDartPackageConfig;

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String cachePath = getCachePath();
		mkdir(cachePath);

		ST projectTemplate = new ST(RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/pubspec.yaml.stg"));
		projectTemplate.add("runtimePath", getRuntimePath());

		writeFile(cachePath, "pubspec.yaml", projectTemplate.render());

		runCommand(new String[]{getRuntimeToolPath(), "pub", "get"}, cachePath);

		cacheDartPackageConfig = readFile(cachePath + FileSeparator + ".dart_tool", "package_config.json");
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		String dartToolDirPath = new File(getTempDirPath(), ".dart_tool").getAbsolutePath();
		mkdir(dartToolDirPath);
		writeFile(dartToolDirPath, "package_config.json", cacheDartPackageConfig);

		return new CompiledState(generatedState, null);
	}
}
