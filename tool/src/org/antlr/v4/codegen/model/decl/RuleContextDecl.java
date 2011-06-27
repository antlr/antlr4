package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.CoreOutputModelFactory;

/** */
public class RuleContextDecl extends Decl {
	public String ctxName;
	public RuleContextDecl(CoreOutputModelFactory factory, String name, String ctxName) {
		super(factory, name);
		this.ctxName = ctxName;
	}
}
