/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.ast.ActionAST;

public class ExceptionClause extends SrcOp {
	@ModelElement public Action catchArg;
	@ModelElement public Action catchAction;

	public ExceptionClause(OutputModelFactory factory,
						   ActionAST catchArg,
						   ActionAST catchAction)
	{
		super(factory, catchArg);
		this.catchArg = new Action(factory, catchArg);
		this.catchAction = new Action(factory, catchAction);
	}
}
