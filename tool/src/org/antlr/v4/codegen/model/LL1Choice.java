/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

public abstract class LL1Choice extends Choice {
	/** Token names for each alt 0..n-1 */
	public List<TokenInfo[]> altLook;
	@ModelElement public ThrowNoViableAlt error;

	public LL1Choice(OutputModelFactory factory, GrammarAST blkAST,
					 List<CodeBlockForAlt> alts)
	{
		super(factory, blkAST, alts);
	}
}
