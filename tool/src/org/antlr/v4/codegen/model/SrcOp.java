package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

/** */
public abstract class SrcOp extends OutputModelObject {
	/** Used to create unique var names etc... */
	public int uniqueID;

	public SrcOp() {;}
	public SrcOp(CoreOutputModelFactory factory) { super(factory); }
	public SrcOp(CoreOutputModelFactory factory, GrammarAST ast) {
		super(factory,ast);
		uniqueID = ast.token.getTokenIndex();
	}
}
