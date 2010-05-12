package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

/** */
public abstract class SrcOp extends OutputModelObject {
	public SrcOp() {;}
	public SrcOp(OutputModelFactory factory) { super(factory); }
	public SrcOp(OutputModelFactory factory, GrammarAST ast) { super(factory,ast); }
}
