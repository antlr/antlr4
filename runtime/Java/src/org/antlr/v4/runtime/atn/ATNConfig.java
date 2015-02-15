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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.MurmurHash;

/** A tuple: (ATN state, predicted alt, syntactic, semantic context).
 *  The syntactic context is a graph-structured stack node whose
 *  path(s) to the root is the rule invocation(s)
 *  chain used to arrive at the state.  The semantic context is
 *  the tree of semantic predicates encountered before reaching
 *  an ATN state.
 */
public class ATNConfig {
	/**
	 * This field stores the bit mask for implementing the
	 * {@link #isPrecedenceFilterSuppressed} property as a bit within the
	 * existing {@link #reachesIntoOuterContext} field.
	 */
	private static final int SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

	/** The ATN state associated with this configuration */
	public final ATNState state;

	/** What alt (or lexer rule) is predicted by this configuration */
	public final int alt;

	/** The stack of invoking states leading to the rule/states associated
	 *  with this config.  We track only those contexts pushed during
	 *  execution of the ATN simulator.
	 */
	public PredictionContext context;

	/**
	 * We cannot execute predicates dependent upon local context unless
	 * we know for sure we are in the correct context. Because there is
	 * no way to do this efficiently, we simply cannot evaluate
	 * dependent predicates unless we are in the rule that initially
	 * invokes the ATN simulator.
	 *
	 * <p>
	 * closure() tracks the depth of how far we dip into the outer context:
	 * depth &gt; 0.  Note that it may not be totally accurate depth since I
	 * don't ever decrement. TODO: make it a boolean then</p>
	 *
	 * <p>
	 * For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
	 * is also backed by this field. Since the field is publicly accessible, the
	 * highest bit which would not cause the value to become negative is used to
	 * store this field. This choice minimizes the risk that code which only
	 * compares this value to 0 would be affected by the new purpose of the
	 * flag. It also ensures the performance of the existing {@link ATNConfig}
	 * constructors as well as certain operations like
	 * {@link ATNConfigSet#add(ATNConfig, DoubleKeyMap)} method are
	 * <em>completely</em> unaffected by the change.</p>
	 */
	public int reachesIntoOuterContext;


    public final SemanticContext semanticContext;

	public ATNConfig(ATNConfig old) { // dup
		this.state = old.state;
		this.alt = old.alt;
		this.context = old.context;
		this.semanticContext = old.semanticContext;
		this.reachesIntoOuterContext = old.reachesIntoOuterContext;
	}

	public ATNConfig(ATNState state,
					 int alt,
					 PredictionContext context)
	{
		this(state, alt, context, SemanticContext.NONE);
	}

	public ATNConfig(ATNState state,
					 int alt,
					 PredictionContext context,
					 SemanticContext semanticContext)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
		this.semanticContext = semanticContext;
	}

    public ATNConfig(ATNConfig c, ATNState state) {
   		this(c, state, c.context, c.semanticContext);
   	}

	public ATNConfig(ATNConfig c, ATNState state,
		 SemanticContext semanticContext)
{
		this(c, state, c.context, semanticContext);
	}

	public ATNConfig(ATNConfig c,
					 SemanticContext semanticContext)
	{
		this(c, c.state, c.context, semanticContext);
	}

    public ATNConfig(ATNConfig c, ATNState state,
					 PredictionContext context)
	{
        this(c, state, context, c.semanticContext);
    }

	public ATNConfig(ATNConfig c, ATNState state,
					 PredictionContext context,
                     SemanticContext semanticContext)
    {
		this.state = state;
		this.alt = c.alt;
		this.context = context;
		this.semanticContext = semanticContext;
		this.reachesIntoOuterContext = c.reachesIntoOuterContext;
	}

	/**
	 * This method gets the value of the {@link #reachesIntoOuterContext} field
	 * as it existed prior to the introduction of the
	 * {@link #isPrecedenceFilterSuppressed} method.
	 */
	public final int getOuterContextDepth() {
		return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
	}

	public final boolean isPrecedenceFilterSuppressed() {
		return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
	}

	public final void setPrecedenceFilterSuppressed(boolean value) {
		if (value) {
			this.reachesIntoOuterContext |= 0x40000000;
		}
		else {
			this.reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
		}
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
			&& this.semanticContext.equals(other.semanticContext)
			&& this.isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed();
	}

	@Override
	public int hashCode() {
		int hashCode = MurmurHash.initialize(7);
		hashCode = MurmurHash.update(hashCode, state.stateNumber);
		hashCode = MurmurHash.update(hashCode, alt);
		hashCode = MurmurHash.update(hashCode, context);
		hashCode = MurmurHash.update(hashCode, semanticContext);
		hashCode = MurmurHash.finish(hashCode, 4);
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(null, true);
	}

	public String toString(Recognizer<?, ?> recog, boolean showAlt) {
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
        if ( getOuterContextDepth()>0 ) {
            buf.append(",up=").append(getOuterContextDepth());
        }
		buf.append(')');
		return buf.toString();
    }
}
