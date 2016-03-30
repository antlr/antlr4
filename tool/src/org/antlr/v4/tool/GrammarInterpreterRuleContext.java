/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
