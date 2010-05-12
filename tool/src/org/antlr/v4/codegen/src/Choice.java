package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class Choice extends SrcOp {
	public int decision;
	public List<CodeBlock> alts;
	public List<SrcOp> preamble;

	public Choice(OutputModelFactory factory, GrammarAST blkOrEbnfRootAST, List<CodeBlock> alts) {
		this.factory = factory;
		this.ast = blkOrEbnfRootAST;
		this.alts = alts;
		this.decision = ((BlockStartState)blkOrEbnfRootAST.nfaState).decision;
	}

	public void addPreambleOp(SrcOp op) {
		if ( preamble==null ) preamble = new ArrayList<SrcOp>();
		preamble.add(op);
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("alts"); add("preamble"); }};
	}
}
