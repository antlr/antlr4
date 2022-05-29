package org.antlr.v4.test.runtime;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Processor {
	public final String[] arguments;
	public final String workingDirectory;
	public final Map<String, String> environmentVariables;
	public final boolean throwOnNonZeroErrorCode;

	public static ProcessorResult run(String[] arguments, String workingDirectory, Map<String, String> environmentVariables) throws Exception {
		return new Processor(arguments, workingDirectory, environmentVariables, true).Start();
	}

	public static ProcessorResult run(String[] arguments, String workingDirectory) throws Exception {
		return new Processor(arguments, workingDirectory, new HashMap<>(), true).Start();
	}

	public Processor(String[] arguments, String workingDirectory, Map<String, String> environmentVariables,
					 boolean throwOnNonZeroErrorCode) {
		this.arguments = arguments;
		this.workingDirectory = workingDirectory;
		this.environmentVariables = environmentVariables;
		this.throwOnNonZeroErrorCode = throwOnNonZeroErrorCode;
	}

	public ProcessorResult Start() throws Exception {
		ProcessBuilder builder = new ProcessBuilder(arguments);
		if (workingDirectory != null) {
			builder.directory(new File(workingDirectory));
		}
		for (String key : environmentVariables.keySet()) {
			builder.environment().put(key, environmentVariables.get(key));
		}
		Process process = builder.start();
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		if (throwOnNonZeroErrorCode && process.exitValue() != 0) {
			throw new Exception(stderrVacuum.toString());
		}
		return new ProcessorResult(process.exitValue(), stdoutVacuum.toString(), stderrVacuum.toString());
	}
}
