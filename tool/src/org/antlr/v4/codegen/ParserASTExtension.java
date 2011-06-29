package org.antlr.v4.codegen;

import org.antlr.runtime.tree.Tree;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;

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
	public List<SrcOp> rootRule(List<SrcOp> ops) {
		InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
		SrcOp treeOp = new BecomeRoot(factory, invokeOp.ast, invokeOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}

	@Override
	public List<SrcOp> rootToken(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		SrcOp treeOp = new BecomeRoot(factory, matchOp.ast, matchOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}

	@Override
	public List<SrcOp> ruleRef(List<SrcOp> ops) {
		InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
		Tree parent = invokeOp.ast.getParent();
		if ( parent!=null && parent.getType()==ANTLRParser.BANG ) return ops;
		SrcOp treeOp = new AddLeaf(factory, invokeOp.ast, invokeOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}

	@Override
	public List<SrcOp> tokenRef(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		Tree parent = matchOp.ast.getParent();
		if ( parent!=null && parent.getType()==ANTLRParser.BANG ) return ops;
		SrcOp treeOp = new AddLeaf(factory, matchOp.ast, matchOp);
		return DefaultOutputModelFactory.list(ops, treeOp);
	}
}
