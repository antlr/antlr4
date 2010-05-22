package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class Choice extends SrcOp {
	public int decision;
	public List<CodeBlock> alts;
	public List<SrcOp> preamble;
	public IntervalSet expecting;
	public ThrowNoViableAlt error;

	public Choice(OutputModelFactory factory, GrammarAST blkOrEbnfRootAST, List<CodeBlock> alts) {
		super(factory, blkOrEbnfRootAST);
		this.alts = alts;
		this.decision = ((BlockStartState)blkOrEbnfRootAST.nfaState).decision;

		LinearApproximator approx = new LinearApproximator(factory.g);
		NFAState decisionState = ast.nfaState;
		expecting = approx.LOOK(decisionState);
		System.out.println("expecting="+expecting);

		this.error = new ThrowNoViableAlt(factory, blkOrEbnfRootAST, expecting); 
	}

	public void addPreambleOp(SrcOp op) {
		if ( preamble==null ) preamble = new ArrayList<SrcOp>();
		preamble.add(op);
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup);
//			add("alts"); add("preamble"); add("error"); }};
//	}
}
