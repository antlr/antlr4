package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.misc.Utils;

import java.util.List;

public class ParserASTExtension extends CodeGeneratorExtension {
	public ParserASTExtension(OutputModelFactory factory) {
		super(factory);
	}

	@Override
	public List<SrcOp> rulePostamble(List<SrcOp> ops) {
		AssignTreeResult setReturn = new AssignTreeResult(factory);
		return DefaultOutputModelFactory.list(ops, setReturn);
	}

	@Override
	public List<SrcOp> ruleRef(List<SrcOp> ops) {
		InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
		SrcOp treeOp = new BecomeRoot(factory, invokeOp.ast, invokeOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}

	@Override
	public List<SrcOp> tokenRef(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		SrcOp treeOp = new AddLeaf(factory, matchOp.ast, matchOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}
}
