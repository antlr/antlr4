package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** public XContext X() { } */
public class ContextRuleGetterDecl extends ContextGetterDecl {
	public String ctxName;
	public ContextRuleGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		super(factory, name);
		this.ctxName = ctxName;
	}
}
