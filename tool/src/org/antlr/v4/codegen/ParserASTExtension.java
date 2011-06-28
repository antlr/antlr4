package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.AddLeaf;
import org.antlr.v4.misc.Utils;

import java.util.List;

public class ParserASTExtension extends CodeGeneratorExtension {
	public ParserASTExtension(OutputModelFactory factory) {
		super(factory);
	}

	@Override
	public List<SrcOp> ruleRef(List<SrcOp> ops) {
		return super.ruleRef(ops);
	}

	@Override
	public List<SrcOp> tokenRef(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		SrcOp treeOp = new AddLeaf(factory, matchOp.ast, matchOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}
}
