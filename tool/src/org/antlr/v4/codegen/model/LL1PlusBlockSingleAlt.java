/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlockSingleAlt extends LL1Loop {
	public LL1PlusBlockSingleAlt(OutputModelFactory factory, GrammarAST plusRoot, List<CodeBlockForAlt> alts) {
		super(factory, plusRoot, alts);

		BlockAST blkAST = (BlockAST)plusRoot.getChild(0);
		PlusBlockStartState blkStart = (PlusBlockStartState)blkAST.atnState;

		stateNumber = blkStart.loopBackState.stateNumber;
		blockStartStateNumber = blkStart.stateNumber;
		PlusBlockStartState plus = (PlusBlockStartState)blkAST.atnState;
		this.decision = plus.loopBackState.decision;
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);

		IntervalSet loopBackLook = altLookSets[0];
		loopExpr = addCodeForLoopLookaheadTempVar(loopBackLook);
	}
}
