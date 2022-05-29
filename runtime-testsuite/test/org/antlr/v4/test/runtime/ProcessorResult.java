package org.antlr.v4.test.runtime;

public class ProcessorResult {
	public final int exitCode;
	public final String output;
	public final String errors;

	public ProcessorResult(int exitCode, String output, String errors) {
		this.exitCode = exitCode;
		this.output = output;
		this.errors = errors;
	}

	public boolean isSuccess() {
		return exitCode == 0;
	}
}
