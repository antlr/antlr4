/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

public class StarBlock extends Loop {
	public String loopLabel;

	public StarBlock(OutputModelFactory factory,
					 GrammarAST blkOrEbnfRootAST,
					 List<CodeBlockForAlt> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		loopLabel = factory.getGenerator().getTarget().getLoopLabel(blkOrEbnfRootAST);
		StarLoopEntryState star = (StarLoopEntryState)blkOrEbnfRootAST.atnState;
		loopBackStateNumber = star.loopBackState.stateNumber;
		decision = star.decision;
	}
}
