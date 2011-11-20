/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.stringtemplate.v4.ST;

/** Rules return values in an object containing all the values.
 *  Besides the properties defined in
 *  RuleLabelScope.predefinedRulePropertiesScope there may be user-defined
 *  return values.  This class simply defines the minimum properties that
 *  are always defined and methods to access the others that might be
 *  available depending on output option such as template and tree.
 *
 *  Note text is not an actual property of the return value, it is computed
 *  from start and stop using the input stream's toString() method.  I
 *  could add a ctor to this so that we can pass in and store the input
 *  stream, but I'm not sure we want to do that.  It would seem to be undefined
 *  to get the .text property anyway if the rule matches tokens from multiple
 *  input streams.
 *
 *  I do not use getters for fields of objects that are used simply to
 *  group values such as this aggregate.  The getters/setters are there to
 *  satisfy the superclass interface.
 */
public class ParserRuleContext<Symbol> extends RuleContext {
	public Symbol start, stop;
	public ST st;

	/** Set during parsing to identify which rule parser is in. */
	public int ruleIndex;

	/** Set during parsing to identify which alt of rule parser is in. */
	public int altNum;

	public ParserRuleContext() { }

	/** COPY a ctx
	 */
	public void copyFrom(ParserRuleContext<Symbol> ctx) {
		// from RuleContext
		this.parent = ctx.parent;
		this.s = ctx.s;
		this.invokingState = ctx.invokingState;

		this.start = ctx.start;
		this.stop = ctx.stop;
		this.st = ctx.st;
		this.ruleIndex = ctx.ruleIndex;
	}

	public ParserRuleContext(RuleContext parent, int stateNumber) {
		super(parent, stateNumber);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + System.identityHashCode(s);
	}

	@Override
	public boolean equals(Object o) {
		if ( !super.equals(o) ) return false;
		return s != ((RuleContext)o).s; // must be parsing the same location in the ATN
	}

	public void enterRule(ParseTreeListener<Symbol> listener) { }
	public void exitRule(ParseTreeListener<Symbol> listener) { }

	@Override
	public int getRuleIndex() { return ruleIndex; }

	public ST getTemplate() { return st; }
	public Symbol getStart() { return start; }
	public Symbol getStop() { return stop; }
}
