package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.StarBlockStartState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlockSingleAlt extends LL1Loop {
	public LL1StarBlockSingleAlt(OutputModelFactory factory, GrammarAST starRoot, List<SrcOp> alts) {
		super(factory, starRoot, alts);

		StarBlockStartState star = (StarBlockStartState)starRoot.atnState;
		this.decision = star.decision;
//		DFA dfa = factory.g.decisionDFAs.get(decision);
//		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		IntervalSet enterLook = altLookSets[1];
		IntervalSet exitLook = altLookSets[2];
		loopExpr = addCodeForLoopLookaheadTempVar(enterLook);

		IntervalSet enterExpecting = (IntervalSet)exitLook.or(enterLook);
		this.sync = new Sync(factory, starRoot, enterExpecting, decision, "iter");
	}
}
