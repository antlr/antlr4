package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.Stage;

public class CompiledState extends State {
	@Override
	public Stage getStage() {
		return Stage.Compile;
	}

	public CompiledState(GeneratedState previousState, Exception exception) {
		super(previousState, exception);
	}
}
