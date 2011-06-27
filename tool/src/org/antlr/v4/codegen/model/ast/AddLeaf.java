package org.antlr.v4.codegen.model.ast;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.tool.GrammarAST;

/** */
public class AddLeaf extends SrcOp {
	public LabeledOp opWithResultToAdd;
	public Decl label;

	public AddLeaf(CoreOutputModelFactory factory, GrammarAST ast, LabeledOp opWithResultToAdd) {
		super(factory, ast);
		this.opWithResultToAdd = opWithResultToAdd;
		label = opWithResultToAdd.getLabels().get(0);
	}
}
