package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

/** */
public abstract class OutputModelObject {
	public OutputModelFactory factory;
	public GrammarAST ast;

	public OutputModelObject() {;}
	
	public OutputModelObject(OutputModelFactory factory) { this.factory = factory; }

	public OutputModelObject(OutputModelFactory factory, GrammarAST ast) {
		this.factory = factory;
		this.ast = ast;
	}
}
