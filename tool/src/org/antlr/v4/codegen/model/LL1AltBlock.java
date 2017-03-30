/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** (A | B | C) */
public class LL1AltBlock extends LL1Choice {
	public LL1AltBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlockForAlt> alts) {
		super(factory, blkAST, alts);
		this.decision = ((DecisionState)blkAST.atnState).decision;

		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		IntervalSet expecting = IntervalSet.or(altLookSets); // combine alt sets
		this.error = getThrowNoViableAlt(factory, blkAST, expecting);
	}
}
