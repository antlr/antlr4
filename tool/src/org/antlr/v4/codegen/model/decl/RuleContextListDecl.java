package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class RuleContextListDecl extends Decl {
	public RuleContextDecl decl;
	public RuleContextListDecl(OutputModelFactory factory, String name, RuleContextDecl decl) {
		super(factory, name);
		this.decl = decl;
	}
}
