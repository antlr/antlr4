package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** x=ID or implicit _tID label in tree grammar */
public class NodeDecl extends TokenDecl {
	public NodeDecl(OutputModelFactory factory, String varName) {
		super(factory, varName);
	}
}
