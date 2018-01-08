/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class LL1Loop extends Choice {
	/** The state associated wih the (A|B|...) block not loopback, which
	 *  is super.stateNumber
	 */
	public int blockStartStateNumber;
	public int loopBackStateNumber;

	@ModelElement public OutputModelObject loopExpr;
	@ModelElement public List<SrcOp> iteration;

	public LL1Loop(OutputModelFactory factory,
				   GrammarAST blkAST,
				   List<CodeBlockForAlt> alts)
	{
		super(factory, blkAST, alts);
	}

	public void addIterationOp(SrcOp op) {
		if ( iteration==null ) iteration = new ArrayList<SrcOp>();
		iteration.add(op);
	}

	public SrcOp addCodeForLoopLookaheadTempVar(IntervalSet look) {
		TestSetInline expr = addCodeForLookaheadTempVar(look);
		if (expr != null) {
			CaptureNextTokenType nextType = new CaptureNextTokenType(factory, expr.varName);
			addIterationOp(nextType);
		}
		return expr;
	}
}
