package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.CoreOutputModelFactory;

public class KidsListDecl extends Decl {
	public int level;
	public KidsListDecl(CoreOutputModelFactory factory, int level) {
		super(factory, factory.gen.target.getKidsListName(level));
	}
}
