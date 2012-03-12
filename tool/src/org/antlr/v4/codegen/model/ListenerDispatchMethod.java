package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

public class ListenerDispatchMethod extends DispatchMethod {
	public boolean isEnter;

	public ListenerDispatchMethod(OutputModelFactory factory, boolean isEnter) {
		super(factory);
		this.isEnter = isEnter;
	}
}
