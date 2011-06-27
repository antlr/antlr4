package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.CoreOutputModelFactory;

public class RootDecl extends Decl {
	public int level;
	public RootDecl(CoreOutputModelFactory factory, int level) {
		super(factory, factory.gen.target.getRootName(level));
	}
}
