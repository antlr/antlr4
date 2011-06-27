package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.AddLeaf;

import java.util.List;

public class ParserASTExtension extends CodeGeneratorExtension {
	public ParserASTExtension(OutputModelFactory factory) {
		super(factory);
	}

	@Override
	public List<SrcOp> tokenRef(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken) DefaultOutputModelFactory.find(ops, MatchToken.class);
		SrcOp treeOp = new AddLeaf(factory, matchOp.ast, matchOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}
}
