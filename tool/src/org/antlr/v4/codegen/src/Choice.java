package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class Choice extends SrcOp {
	public int decision = -1;
	public List<CodeBlock> alts;
	public List<SrcOp> preamble;
	public IntervalSet expecting;

	public Choice(OutputModelFactory factory,
				  GrammarAST blkOrEbnfRootAST,
				  List<CodeBlock> alts,
				  int decision)
	{
		super(factory, blkOrEbnfRootAST);
		this.alts = alts;
		this.decision = decision;

		// TODO: use existing lookahead! don't compute
		LinearApproximator approx = new LinearApproximator(factory.g, decision);
		NFAState decisionState = ast.nfaState;
		expecting = approx.FIRST(decisionState);
		System.out.println(blkOrEbnfRootAST.toStringTree()+" choice expecting="+expecting);
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

	public List<String[]> getAltLookaheadAsStringLists(IntervalSet[] altLookSets) {
		List<String[]> altLook = new ArrayList<String[]>();
		for (int a=1; a<altLookSets.length; a++) {
			IntervalSet s = altLookSets[a];
			altLook.add(factory.gen.target.getTokenTypesAsTargetLabels(factory.g, s.toArray()));
		}
		return altLook;
	}
}
