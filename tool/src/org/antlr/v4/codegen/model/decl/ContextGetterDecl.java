package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** public Token getID() { } */
public class ContextGetterDecl extends Decl {
	public ContextGetterDecl(OutputModelFactory factory, String name, String decl) {
		super(factory, name, decl);
	}
}
