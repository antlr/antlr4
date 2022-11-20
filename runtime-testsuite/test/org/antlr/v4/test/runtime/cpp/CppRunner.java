/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.cpp;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.getOS;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.isWindows;

/**
 * For my own information on I'm recording what I needed to do to get a unit test to compile and run in C++ on the Mac.
 * I got a segmentation violation and couldn't figure out how to get information about it, so I turned on debugging
 * and then figured out lldb enough to create this issue: https://github.com/antlr/antlr4/issues/3845 on a bug.
 *
 * cd ~/antlr/code/antlr4/runtime/Cpp
 * cmake . -D CMAKE_OSX_ARCHITECTURES="arm64; x86_64" -DCMAKE_BUILD_TYPE=Debug
 * make -j 8
 *
 * In test dir with generated test code:
 *
 * clang++ -g -std=c++17 -I /Users/parrt/antlr/code/antlr4/runtime/Cpp/runtime/src -L. -lantlr4-runtime *.cpp
 * ./a.out input
 *
 * $ lldb ./a.out input
 * (lldb) run
 * ... crash ...
 * (lldb) thread backtrace
 */
public class CppRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Cpp";
	}

	@Override
	public String getTitleName() { return "C++"; }

	private static final String runtimeSourcePath;
	private static final String runtimeBinaryPath;
	private static final String runtimeLibraryFileName;
	private static String compilerName;
	private static final String visualStudioProjectContent;
	private static final Map<String, String> environment;

	static {
		String runtimePath = getRuntimePath("Cpp");
		runtimeSourcePath = Paths.get(runtimePath, "runtime", "src").toString();

		environment = new HashMap<>();
		if (isWindows()) {
			runtimeBinaryPath = Paths.get(runtimePath, "runtime", "bin", "vs-2022", "x64", "Release DLL").toString();
			runtimeLibraryFileName = Paths.get(runtimeBinaryPath, "antlr4-runtime.dll").toString();
			String path = System.getenv("PATH");
			environment.put("PATH", path == null ? runtimeBinaryPath : path + ";" + runtimeBinaryPath);
		}
		else {
			runtimeBinaryPath = Paths.get(runtimePath, "dist").toString();
			runtimeLibraryFileName = Paths.get(runtimeBinaryPath,
					"libantlr4-runtime." + (getOS() == OSType.Mac ? "dylib" : "so")).toString();
			environment.put("LD_PRELOAD", runtimeLibraryFileName);
		}

		if (isWindows()) {
			visualStudioProjectContent = RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/Test.vcxproj.stg");
		} else {
			visualStudioProjectContent = null;
		}
	}

	@Override
	protected String getCompilerName() {
		if (compilerName == null) {
			if (isWindows()) {
				compilerName = "MSBuild";
			}
			else {
				compilerName = "clang++";
			}
		}

		return compilerName;
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String runtimePath = getRuntimePath();

		if (isWindows()) {
			String[] command = {
				getCompilerPath(), "antlr4cpp-vs2022.vcxproj", "/p:configuration=Release DLL", "/p:platform=x64"
			};

			runCommand(command, runtimePath + "\\runtime","build c++ ANTLR runtime using MSBuild");
		}
		else {
			// cmake ignores default of OFF and must explicitly say yes or no on tracing arg. grrr...
			String trace = "-DTRACE_ATN="+(runOptions.traceATN?"ON":"OFF");
			String[] command = {"cmake", ".", trace, "-DCMAKE_BUILD_TYPE=Release"};
			runCommand(command, runtimePath, "run cmake on antlr c++ runtime");

			command = new String[] {"make", "-j", Integer.toString(Runtime.getRuntime().availableProcessors())};
			runCommand(command, runtimePath, "run make on antlr c++ runtime");
		}
	}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		if (isWindows()) {
			writeVisualStudioProjectFile(runOptions.grammarName, runOptions.lexerName, runOptions.parserName,
					runOptions.useListener, runOptions.useVisitor);
		}

		Exception exception = null;
		try {
			if (!isWindows()) {
				String[] linkCommand = new String[]{"ln", "-s", runtimeLibraryFileName};
				runCommand(linkCommand, getTempDirPath(), "sym link C++ runtime");
			}

			List<String> buildCommand = new ArrayList<>();
			buildCommand.add(getCompilerPath());
			if (isWindows()) {
				buildCommand.add(getTestFileName() + ".vcxproj");
				buildCommand.add("/p:configuration=Release");
				buildCommand.add("/p:platform=x64");
			}
			else {
				buildCommand.add("-std=c++17");
				buildCommand.add("-I");
				buildCommand.add(runtimeSourcePath);
				buildCommand.add("-L.");
				buildCommand.add("-lantlr4-runtime");
				buildCommand.add("-pthread");
				buildCommand.add("-o");
				buildCommand.add(getTestFileName() + ".out");
				buildCommand.add(getTestFileWithExt());
				buildCommand.addAll(generatedState.generatedFiles.stream().map(file -> file.name).collect(Collectors.toList()));
			}

			runCommand(buildCommand.toArray(new String[0]), getTempDirPath(), "build test c++ binary");
		}
		catch (Exception ex) {
			exception = ex;
		}
		return new CompiledState(generatedState, exception);
	}

	private void writeVisualStudioProjectFile(String grammarName, String lexerName, String parserName,
											  boolean useListener, boolean useVisitor) {
		ST projectFileST = new ST(visualStudioProjectContent);
		projectFileST.add("runtimeSourcePath", runtimeSourcePath);
		projectFileST.add("runtimeBinaryPath", runtimeBinaryPath);
		projectFileST.add("grammarName", grammarName);
		projectFileST.add("lexerName", lexerName);
		projectFileST.add("parserName", parserName);
		projectFileST.add("useListener", useListener);
		projectFileST.add("useVisitor", useVisitor);
		writeFile(getTempDirPath(), "Test.vcxproj", projectFileST.render());
	}

	@Override
	public String getRuntimeToolName() {
		return null;
	}

	@Override
	public String getExecFileName() {
		return Paths.get(getTempDirPath(), getTestFileName() + "." + (isWindows() ? "exe" : "out")).toString();
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}

