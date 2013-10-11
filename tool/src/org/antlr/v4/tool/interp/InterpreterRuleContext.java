package org.antlr.v4.tool.interp;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Nullable;

public class InterpreterRuleContext extends ParserRuleContext {
	protected int ruleIndex;

	public InterpreterRuleContext(@Nullable ParserRuleContext parent,
								  int invokingStateNumber,
								  int ruleIndex)
	{
		super(parent, invokingStateNumber);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public int getRuleIndex() {	return ruleIndex; }
}
