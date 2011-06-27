package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class RootDecl extends Decl {
	public int level;
	public RootDecl(OutputModelFactory factory, int level) {
		super(factory, factory.getGenerator().target.getRootName(level));
	}
}
