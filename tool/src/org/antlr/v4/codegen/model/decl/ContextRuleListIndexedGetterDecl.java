package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class ContextRuleListIndexedGetterDecl extends ContextRuleListGetterDecl {
	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		super(factory, name, ctxName);
	}

	@Override
	public String getArgType() {
		return "int";
	}
}
