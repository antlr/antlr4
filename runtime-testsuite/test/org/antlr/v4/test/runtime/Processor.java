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
	public final String[] arguments;
	public final String workingDirectory;
	public final Map<String, String> environmentVariables;
	public final boolean throwOnNonZeroErrorCode;

	public static ProcessorResult run(String[] arguments, String workingDirectory, Map<String, String> environmentVariables
	) throws InterruptedException, IOException {
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
			throw new InterruptedException(joinLines(output, errors));
		}
		return new ProcessorResult(process.exitValue(), output, errors);
	}
}
