/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.tool;

import org.antlr.v4.runtime.InterpreterRuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

/** An {@link InterpreterRuleContext} that knows which alternative
 *  for a rule was matched.
 *
 *  @see GrammarParserInterpreter
 *  @since 4.5.1
 */
public class GrammarInterpreterRuleContext extends InterpreterRuleContext {
	protected int outerAltNum = 1;

	public GrammarInterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
		super(parent, invokingStateNumber, ruleIndex);
	}

	/** The predicted outermost alternative for the rule associated
	 *  with this context object.  If this node left recursive, the true original
	 *  outermost alternative is returned.
	 */
	public int getOuterAltNum() { return outerAltNum; }

	public void setOuterAltNum(int outerAltNum) {
		this.outerAltNum = outerAltNum;
	}

	@Override
	public int getAltNumber() {
		// override here and called old functionality; makes it backward compatible vs changing names
		return getOuterAltNum();
	}

	@Override
	public void setAltNumber(int altNumber) {
		setOuterAltNum(altNumber);
	}
}
