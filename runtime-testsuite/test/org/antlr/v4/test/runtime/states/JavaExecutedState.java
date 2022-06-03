package org.antlr.v4.test.runtime.states;

import org.antlr.v4.runtime.tree.ParseTree;

public class JavaExecutedState extends ExecutedState {
	public final ParseTree parseTree;

	public JavaExecutedState(JavaCompiledState previousState, String output, String errors, ParseTree parseTree,
							 Exception exception) {
		super(previousState, output, errors, exception);
		this.parseTree = parseTree;
	}
}
