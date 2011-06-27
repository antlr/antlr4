package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.CoreOutputModelFactory;

public class RuleContextListDecl extends Decl {
	public RuleContextDecl decl;
	public RuleContextListDecl(CoreOutputModelFactory factory, String name, RuleContextDecl decl) {
		super(factory, name);
		this.decl = decl;
	}
}
