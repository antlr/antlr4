/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;

/** */
public class Sync extends SrcOp {
	public int decision;
//	public BitSetDecl expecting;
	public Sync(OutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				IntervalSet expecting,
				int decision,
				String position)
	{
		super(factory, blkOrEbnfRootAST);
		this.decision = decision;
//		this.expecting = factory.createExpectingBitSet(ast, decision, expecting, position);
//		factory.defineBitSet(this.expecting);
	}
}
