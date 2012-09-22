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
