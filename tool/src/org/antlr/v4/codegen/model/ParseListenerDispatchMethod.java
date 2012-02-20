package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

public class ParseListenerDispatchMethod extends DispatchMethod {
	public boolean isEnter;

	public ParseListenerDispatchMethod(OutputModelFactory factory, boolean isEnter) {
		super(factory);
		this.isEnter = isEnter;
	}

}
