package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

public class RuleElement extends SrcOp {
	/** Associated ATN state for this rule elements (action, token, ruleref, ...) */
	public int stateNumber;

	public RuleElement(CoreOutputModelFactory factory, GrammarAST ast) {
		super(factory, ast);
		if ( ast.atnState!=null ) stateNumber = ast.atnState.stateNumber;
	}
}
