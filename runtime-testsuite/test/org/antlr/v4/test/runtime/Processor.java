/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.joinLines;

public class Processor {
	/** Turn this on to see output like:
	 *  RUNNING cmake . -DCMAKE_BUILD_TYPE=Release in /Users/parrt/antlr/code/antlr4/runtime/Cpp
	 *  RUNNING make -j 20 in /Users/parrt/antlr/code/antlr4/runtime/Cpp
	 *  RUNNING ln -s /Users/parrt/antlr/code/antlr4/runtime/Cpp/dist/libantlr4-runtime.dylib in /var/folders/w1/_nr4stn13lq0rvjdkwh7q8cc0000gn/T/CppRunner-ForkJoinPool-1-worker-23-1668284191961
	 *  RUNNING clang++ -std=c++17 -I /Users/parrt/antlr/code/antlr4/runtime/Cpp/runtime/src -L. -lantlr4-runtime -pthread -o Test.out Test.cpp TLexer.cpp TParser.cpp TListener.cpp TBaseListener.cpp TVisitor.cpp TBaseVisitor.cpp in /var/folders/w1/_nr4stn13lq0rvjdkwh7q8cc0000gn/T/CppRunner-ForkJoinPool-1-worker-23-1668284191961
	 */
	public static final boolean WATCH_COMMANDS_EXEC = false;
	public final String[] arguments;
	public final String workingDirectory;
	public final Map<String, String> environmentVariables;
	public final boolean throwOnNonZeroErrorCode;

	public static ProcessorResult run(String[] arguments, String workingDirectory, Map<String, String> environmentVariables)
			throws InterruptedException, IOException
	{
		return new Processor(arguments, workingDirectory, environmentVariables, true).start();
	}

	public static ProcessorResult run(String[] arguments, String workingDirectory) throws InterruptedException, IOException {
		return new Processor(arguments, workingDirectory, new HashMap<>(), true).start();
	}

	public Processor(String[] arguments, String workingDirectory, Map<String, String> environmentVariables,
					 boolean throwOnNonZeroErrorCode) {
		this.arguments = arguments;
		this.workingDirectory = workingDirectory;
		this.environmentVariables = environmentVariables;
		this.throwOnNonZeroErrorCode = throwOnNonZeroErrorCode;
	}

	public ProcessorResult start() throws InterruptedException, IOException {
		if ( WATCH_COMMANDS_EXEC ) {
			System.out.println("RUNNING "+ String.join(" ", arguments)+" in "+workingDirectory);
		}
		ProcessBuilder builder = new ProcessBuilder(arguments);
		if (workingDirectory != null) {
			builder.directory(new File(workingDirectory));
		}
		if (environmentVariables != null && environmentVariables.size() > 0) {
			Map<String, String> environment = builder.environment();
			for (String key : environmentVariables.keySet()) {
				environment.put(key, environmentVariables.get(key));
			}
		}

		Process process = builder.start();
		StreamReader stdoutReader = new StreamReader(process.getInputStream());
		StreamReader stderrReader = new StreamReader(process.getErrorStream());
		stdoutReader.start();
		stderrReader.start();
		process.waitFor();
		stdoutReader.join();
		stderrReader.join();

		String output = stdoutReader.toString();
		String errors = stderrReader.toString();
		if (throwOnNonZeroErrorCode && process.exitValue() != 0) {
			throw new InterruptedException("Exit code "+process.exitValue()+" with output:\n"+joinLines(output, errors));
		}
		return new ProcessorResult(process.exitValue(), output, errors);
	}
}
