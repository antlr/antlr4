package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class KidsListDecl extends Decl {
	public int level;
	public KidsListDecl(OutputModelFactory factory, int level) {
		super(factory, factory.getGenerator().target.getKidsListName(level));
	}
}
