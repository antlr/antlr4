package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.GrammarAST;

/** */
public abstract class SrcOp extends OutputModelObject {
	public SrcOp() {;}
	public SrcOp(CodeGenerator gen) { super(gen); }
	public SrcOp(CodeGenerator gen, GrammarAST ast) { super(gen,ast); }
}
