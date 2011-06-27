package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

/** */
public abstract class OutputModelObject {
	public CoreOutputModelFactory factory;
	public GrammarAST ast;

	public OutputModelObject() {;}

	public OutputModelObject(CoreOutputModelFactory factory) { this.factory = factory; }

	public OutputModelObject(CoreOutputModelFactory factory, GrammarAST ast) {
		this.factory = factory;
		this.ast = ast;
	}
}
