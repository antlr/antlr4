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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.*;

/** An ATN state, predicted alt, and syntactic/semantic context.
 *  The syntactic context is a pointer into the rule invocation
 *  chain used to arrive at the state.  The semantic context is
 *  the unordered set semantic predicates encountered before reaching
 *  an ATN state.
 */
public class ATNConfig {
	/** The ATN state associated with this configuration */
	@NotNull
	public final ATNState state;

	/** What alt (or lexer rule) is predicted by this configuration */
	public final int alt;

	/** The stack of invoking states leading to the rule/states associated
	 *  with this config.  We track only those contexts pushed during
	 *  execution of the ATN simulator.
	 */
	@Nullable
	public final RuleContext context;

	/**
	 * Indicates that we have reached this ATN configuration after
	 * traversing a predicate transition. This is important because we
	 * cannot cache DFA states derived from such configurations
	 * otherwise predicates would not get executed again (DFAs don't
	 * have predicated edges in v4).
	 */
	public boolean traversedPredicate;

	/**
	 * Indicates that we have reached this ATN configuration after
	 * traversing a non-forced action transition. We do not execute
	 * predicates after such actions because the predicates could be
	 * functions of the side effects. Force actions must be either side
	 * effect free or automatically undone as the parse continues.
	 */
	public boolean traversedAction;

	/**
	 * We cannot execute predicates dependent upon local context unless
	 * we know for sure we are in the correct context. Because there is
	 * no way to do this efficiently, we simply cannot evaluate
	 * dependent predicates unless we are in the rule that initially
	 * invokes the ATN simulator.
	 *
	 * closure() tracks the depth of how far we dip into the
	 * outer context: depth > 0.  Note that it may not be totally
	 * accurate depth since I don't ever decrement. TODO: make it a boolean then
	 */
	public int reachesIntoOuterContext;

	public ATNConfig(@NotNull ATNState state,
					 int alt,
					 @Nullable RuleContext context)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
	}

	public ATNConfig(@NotNull ATNConfig c) {
		this(c, c.state, c.context);
	}

	public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state) {
		this(c, state, c.context);
	}

	public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state, @Nullable RuleContext context) {
		this.state = state;
		this.alt = c.alt;
		this.context = context;
		this.traversedPredicate = c.traversedPredicate;
		this.traversedAction = c.traversedAction;
		this.reachesIntoOuterContext = c.reachesIntoOuterContext;
	}

	public ATNConfig(@NotNull ATNConfig c, @Nullable RuleContext context) {
		this(c, c.state, context);
	}

	/** An ATN configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */
    @Override
    public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this==o ) return true;
		if (!(o instanceof ATNConfig)) {
			return false;
		}

		ATNConfig other = (ATNConfig)o;
		return this.state.stateNumber==other.state.stateNumber &&
		this.alt==other.alt &&
		(this.context==other.context || (this.context != null && this.context.equals(other.context)));
    }

    @Override
    public int hashCode() {
        int h = state.stateNumber + alt;
		if ( context!=null ) h += context.hashCode();
        return h;
    }

	@Override
	public String toString() {
		return toString(null, true);
	}

	public String toString(@Nullable Recognizer<?, ?> recog, boolean showAlt) {
		StringBuilder buf = new StringBuilder();
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
		if ( reachesIntoOuterContext>0 ) {
			buf.append("|up=").append(reachesIntoOuterContext);
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
