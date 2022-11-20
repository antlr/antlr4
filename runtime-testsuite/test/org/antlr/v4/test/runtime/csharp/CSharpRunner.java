/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.nio.file.Paths;

import static org.antlr.v4.test.runtime.FileUtils.mkdir;
import static org.antlr.v4.test.runtime.FileUtils.writeFile;

public class CSharpRunner extends RuntimeRunner {
	@Override
	public String getLanguage() { return "CSharp"; }

	@Override
	public String getTitleName() { return "C#"; }

	@Override
	public String getExtension() { return "cs"; }

	@Override
	public String getRuntimeToolName() { return "dotnet"; }

	@Override
	public String getExecFileName() { return getTestFileName() + ".dll"; }

	private final static String testProjectFileName = "Antlr4.Test.csproj";
	private final static String cSharpAntlrRuntimeDllName =
			Paths.get(getCachePath("CSharp"), "Antlr4.Runtime.Standard.dll").toString();

	private final static String cSharpTestProjectContent;

	static {
		ST projectTemplate = new ST(RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/Antlr4.Test.csproj.stg"));
		projectTemplate.add("runtimeLibraryPath", cSharpAntlrRuntimeDllName);
		cSharpTestProjectContent = projectTemplate.render();
	}

	@Override
	protected void initRuntime(RunOptions runOptions) throws Exception {
		String cachePath = getCachePath();
		mkdir(cachePath);
		String projectPath = Paths.get(getRuntimePath(), "src", "Antlr4.csproj").toString();
		String[] args = new String[]{getRuntimeToolPath(), "build", projectPath, "-c", "Release", "-o", cachePath};
		runCommand(args, cachePath, "build " + getTitleName() + " ANTLR runtime");
	}

	@Override
	public CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		Exception exception = null;
		try {
			writeFile(getTempDirPath(), testProjectFileName, cSharpTestProjectContent);
			runCommand(new String[]{getRuntimeToolPath(), "build", testProjectFileName, "-c", "Release"}, getTempDirPath(),
					"build C# test binary");
		} catch (Exception e) {
			exception = e;
		}
		return new CompiledState(generatedState, exception);
	}
}
