/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.antlr.v4.test.runtime.FileUtils.*;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.getTextFromResource;
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

	private static final String swiftRuntimePath;
	private static final String buildSuffix;
	private static final Map<String, String> environment;

	private static final String includePath;
	private static final String libraryPath;

	static {
		swiftRuntimePath = getRuntimePath("Swift");
		buildSuffix = isWindows() ? "x86_64-unknown-windows-msvc" : "";
		includePath = Paths.get(swiftRuntimePath, ".build", buildSuffix, "release").toString();
		environment = new HashMap<>();
		if (isWindows()) {
			libraryPath = Paths.get(includePath, "Antlr4.lib").toString();
			String path = System.getenv("PATH");
			environment.put("PATH", path == null ? includePath : path + ";" + includePath);
		}
		else {
			libraryPath = includePath;
		}
	}

	@Override
	protected String getCompilerName() {
		return "swift";
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		runCommand(new String[] {getCompilerPath(), "build", "-c", "release"}, swiftRuntimePath, "build Swift runtime");
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		Exception exception = null;
		try {
			String tempDirPath = getTempDirPath();
			File tempDirFile = new File(tempDirPath);

			File[] ignoredFiles = tempDirFile.listFiles(NoSwiftFileFilter.Instance);
			assert ignoredFiles != null;
			List<String> excludedFiles = Arrays.stream(ignoredFiles).map(File::getName).collect(Collectors.toList());

			String text = getTextFromResource("org/antlr/v4/test/runtime/helpers/Package.swift.stg");
			ST outputFileST = new ST(text);
			outputFileST.add("excludedFiles", excludedFiles);
			writeFile(tempDirPath, "Package.swift", outputFileST.render());

			String[] buildProjectArgs = new String[]{
					getCompilerPath(),
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
			runCommand(buildProjectArgs, tempDirPath);
		} catch (Exception e) {
			exception = e;
		}

		return new CompiledState(generatedState, exception);
	}

	static class NoSwiftFileFilter implements FilenameFilter {
		public final static NoSwiftFileFilter Instance = new NoSwiftFileFilter();

		public boolean accept(File dir, String name) {
			return !name.endsWith(".swift");
		}
	}

	@Override
	public String getRuntimeToolName() {
		return null;
	}

	@Override
	public String getExecFileName() {
		return Paths.get(getTempDirPath(),
				".build",
				buildSuffix,
				"release",
				"Test" + (isWindows() ? ".exe" : "")).toString();
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
