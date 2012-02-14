package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** public Token X() { } */
public class ContextTokenGetterDecl extends ContextGetterDecl {
	public ContextTokenGetterDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}
}
