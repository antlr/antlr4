package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.Stage;

public class ExecutedState extends State {
	@Override
	public Stage getStage() {
		return Stage.Execute;
	}

	public final String output;

	public final String errors;

	public ExecutedState(CompiledState previousState, String output, String errors, Exception exception) {
		super(previousState, exception);
		this.output = output != null ? output : "";
		this.errors = errors != null ? errors : "";
	}
}
