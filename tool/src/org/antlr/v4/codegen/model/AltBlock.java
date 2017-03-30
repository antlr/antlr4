/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

public class AltBlock extends Choice {
//	@ModelElement public ThrowNoViableAlt error;

	public AltBlock(OutputModelFactory factory,
					GrammarAST blkOrEbnfRootAST,
					List<CodeBlockForAlt> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		decision = ((BlockStartState)blkOrEbnfRootAST.atnState).decision;
		// interp.predict() throws exception
//		this.error = new ThrowNoViableAlt(factory, blkOrEbnfRootAST, null);
	}
}
