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

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

/** An ATN state, predicted alt, and syntactic/semantic context.
 *  The syntactic context is a pointer into the rule invocation
 *  chain used to arrive at the state.  The semantic context is
 *  the unordered set semantic predicates encountered before reaching
 *  an ATN state.
 *
 *  (state, alt, rule context, semantic context)
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
	public PredictionContext context;

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

	/** Capture lexer action we traverse */
	public int lexerActionIndex = -1; // TOOD: move to subclass

    @NotNull
    public final SemanticContext semanticContext;

	public ATNConfig(@NotNull ATNState state,
					 int alt,
					 @Nullable PredictionContext context)
	{
		this(state, alt, context, SemanticContext.NONE);
	}

	public ATNConfig(@NotNull ATNState state,
					 int alt,
					 @Nullable PredictionContext context,
					 @NotNull SemanticContext semanticContext)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
		this.semanticContext = semanticContext;
	}

    public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state) {
   		this(c, state, c.context, c.semanticContext);
   	}

    public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state, @NotNull SemanticContext semanticContext) {
   		this(c, state, c.context, semanticContext);
   	}

    public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context) {
        this(c, state, context, c.semanticContext);
    }

	public ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context,
                     @NotNull SemanticContext semanticContext)
    {
		this.state = state;
		this.alt = c.alt;
		this.context = context;
		this.semanticContext = semanticContext;
		this.reachesIntoOuterContext = c.reachesIntoOuterContext;
		this.lexerActionIndex = c.lexerActionIndex;
	}

	/** An ATN configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */
    @Override
    public boolean equals(Object o) {
		if (!(o instanceof ATNConfig)) {
			return false;
		}

		return this.equals((ATNConfig)o);
	}

	public boolean equals(ATNConfig other) {
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		}

		return this.state.stateNumber==other.state.stateNumber
			&& this.alt==other.alt
			&& (this.context==other.context || (this.context != null && this.context.equals(other.context)))
			&& this.semanticContext.equals(other.semanticContext);
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		hashCode = 5 * hashCode + state.stateNumber;
		hashCode = 5 * hashCode + alt;
		hashCode = 5 * hashCode + (context != null ? context.hashCode() : 0);
		hashCode = 5 * hashCode + semanticContext.hashCode();
        return hashCode;
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
		buf.append('(');
		buf.append(state);
		if ( showAlt ) {
            buf.append(",");
            buf.append(alt);
        }
        if ( context!=null ) {
            buf.append(",[");
            buf.append(context.toString());
			buf.append("]");
        }
        if ( semanticContext!=null && semanticContext != SemanticContext.NONE ) {
            buf.append(",");
            buf.append(semanticContext);
        }
        if ( reachesIntoOuterContext>0 ) {
            buf.append(",up=").append(reachesIntoOuterContext);
        }
		buf.append(')');
		return buf.toString();
    }
}
