package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.Decl;

/** */
public class RuleContextDecl extends Decl {
	public String ctxName;
	public RuleContextDecl(OutputModelFactory factory, String name, String ctxName) {
		super(factory, name);
		this.ctxName = ctxName;
	}
}
