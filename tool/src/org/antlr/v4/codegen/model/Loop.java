/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.QuantifierAST;

import java.util.ArrayList;
import java.util.List;

public class Loop extends Choice {
	public int blockStartStateNumber;
	public int loopBackStateNumber;
	public final int exitAlt;

	@ModelElement public List<SrcOp> iteration;

	public Loop(OutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				List<CodeBlockForAlt> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		boolean nongreedy = (blkOrEbnfRootAST instanceof QuantifierAST) && !((QuantifierAST)blkOrEbnfRootAST).isGreedy();
		exitAlt = nongreedy ? 1 : alts.size() + 1;
	}

	public void addIterationOp(SrcOp op) {
		if ( iteration==null ) iteration = new ArrayList<SrcOp>();
		iteration.add(op);
	}
}
