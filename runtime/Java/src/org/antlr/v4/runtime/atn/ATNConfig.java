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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.*;

/** An ATN state, predicted alt, and syntactic/semantic context.
 *  The syntactic context is a pointer into the rule invocation
 *  chain used to arrive at the state.  The semantic context is
 *  the unordered set semantic predicates encountered before reaching
 *  an ATN state.
 */
public class ATNConfig {
	/** The ATN state associated with this configuration */
	public ATNState state;

	/** What alt (or lexer rule) is predicted by this configuration */
	public int alt;

	/** The stack of invoking states leading to the rule/states associated
	 *  with this config.  We track only those contexts pushed during
	 *  execution of the ATN simulator.
	 */
	public RuleContext context;

	/** The stack acquired from parser invoking the ATN interpreter.
	 *  Initially it's the rule stack upon entry into interp.adaptivePredict().
	 *
	 *  We only pop from outerContext if we hit rule stop state
	 *  of rule that initiates the adaptivePredict(). outerContext
	 *  tracks as we go back up the entry context. If we do a call,
	 *  we again push onto the regular context, rather than pushing onto
	 *  outerContext.
	 *
	 *  At an accept state, we ignore outerContext unless we had to use it
	 *  during prediction. If outerContext != originalContext
	 *  (stored in the ATN interpreter), then this config needed context
	 *  to find another symbol.
	 *
	 *  Lexer matches with ATN so there is no external context; this is null.
	 */
	//public RuleContext outerContext;

	/**
	 Indicates that we have reached this ATN configuration after
	 traversing a predicate transition. This is important because we
	 cannot cache DFA states derived from such configurations
	 otherwise predicates would not get executed again (DFAs don't
	 have predicated edges in v4).
	 */
	public boolean traversedPredicate;

	/**
	 Indicates that we have reached this ATN configuration after
	 traversing a non-forced action transition. We do not execute
	 predicates after such actions because the predicates could be
	 functions of the side effects. Force actions must be either side
	 effect free or automatically undone as the parse continues.
	 */
	public boolean traversedAction;

	public ATNConfig(ATNState state,
					 int alt,
					 RuleContext context)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
	}

	public ATNConfig(ATNConfig c) {
		this.state = c.state;
		this.alt = c.alt;
		this.context = c.context;
//		this.outerContext = c.outerContext;
		this.traversedPredicate = c.traversedPredicate;
		this.traversedAction = c.traversedAction;
	}

	public ATNConfig(ATNConfig c, ATNState state) {
		this(c);
		this.state = state;
	}

	public ATNConfig(ATNConfig c, ATNState state, RuleContext context) {
		this(c);
		this.state = state;
		this.context = context;
	}

	public ATNConfig(ATNConfig c, RuleContext context) {
		this(c);
		this.context = context;
	}

	/** An ATN configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */
    public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this==o ) return true;
		ATNConfig other = (ATNConfig)o;
		return this.state.stateNumber==other.state.stateNumber &&
		this.alt==other.alt &&
		(this.context==other.context ||
		this.context.equals(other.context));
    }

    public int hashCode() {
		if ( state==null ) {
			System.out.println("eh?");
		}
        int h = state.stateNumber + alt;
		if ( context!=null ) h += context.hashCode();
        return h;
    }

	public String toString() {
		return toString(null, true);
	}

	public String toString(Recognizer<?> recog, boolean showAlt) {
		StringBuffer buf = new StringBuffer();
//		if ( state.ruleIndex>=0 ) {
//			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
//			else buf.append(state.ruleIndex+":");
//		}
		buf.append(state);
		if ( showAlt ) {
			buf.append("|");
			buf.append(alt);
		}
		if ( context!=null ) {
            buf.append("|");
            buf.append(context);
        }
//		if (isAccept) {
//			buf.append("|=>"+alt);
//		}
//		if ( context.approximated ) {
//			buf.append("|approx");
//		}
		return buf.toString();
    }
}
