package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class ContextTokenListIndexedGetterDecl extends ContextTokenListGetterDecl {
	public ContextTokenListIndexedGetterDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}

	@Override
	public String getArgType() {
		return "int";
	}
}
