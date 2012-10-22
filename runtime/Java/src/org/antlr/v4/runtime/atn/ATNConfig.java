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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;

/** A tuple: (ATN state, predicted alt, syntactic, semantic context).
 *  The syntactic context is a graph-structured stack node whose
 *  path(s) to the root is the rule invocation(s)
 *  chain used to arrive at the state.  The semantic context is
 *  the tree of semantic predicates encountered before reaching
 *  an ATN state.
 */
public class ATNConfig {
	/** The ATN state associated with this configuration */
	@NotNull
	private final ATNState state;

	private int altAndOuterContextDepth;

	/** The stack of invoking states leading to the rule/states associated
	 *  with this config.  We track only those contexts pushed during
	 *  execution of the ATN simulator.
	 */
	@NotNull
	private PredictionContext context;

	protected ATNConfig(@NotNull ATNState state,
						int alt,
						@NotNull PredictionContext context)
	{
		assert (alt & 0xFFFFFF) == alt;
		this.state = state;
		this.altAndOuterContextDepth = alt & 0x7FFFFFFF;
		this.context = context;
	}

	protected ATNConfig(@NotNull ATNConfig c, @NotNull ATNState state, @NotNull PredictionContext context)
    {
		this.state = state;
		this.altAndOuterContextDepth = c.altAndOuterContextDepth & 0x7FFFFFFF;
		this.context = context;
	}

	public static ATNConfig create(@NotNull ATNState state, int alt, @Nullable PredictionContext context) {
		return create(state, alt, context, SemanticContext.NONE, -1);
	}

	public static ATNConfig create(@NotNull ATNState state, int alt, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext) {
		return create(state, alt, context, semanticContext, -1);
	}

	public static ATNConfig create(@NotNull ATNState state, int alt, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext, int actionIndex) {
		if (semanticContext != SemanticContext.NONE) {
			if (actionIndex != -1) {
				return new ActionSemanticContextATNConfig(actionIndex, semanticContext, state, alt, context);
			}
			else {
				return new SemanticContextATNConfig(semanticContext, state, alt, context);
			}
		}
		else if (actionIndex != -1) {
			return new ActionATNConfig(actionIndex, state, alt, context);
		}
		else {
			return new ATNConfig(state, alt, context);
		}
	}

	/** Gets the ATN state associated with this configuration */
	@NotNull
	public final ATNState getState() {
		return state;
	}

	/** What alt (or lexer rule) is predicted by this configuration */
	public final int getAlt() {
		return altAndOuterContextDepth & 0x00FFFFFF;
	}

	public final boolean isHidden() {
		return altAndOuterContextDepth < 0;
	}

	public void setHidden(boolean value) {
		if (value) {
			altAndOuterContextDepth |= 0x80000000;
		} else {
			altAndOuterContextDepth &= ~0x80000000;
		}
	}
	@NotNull
	public final PredictionContext getContext() {
		return context;
	}

	public void setContext(@NotNull PredictionContext context) {
		this.context = context;
	}

	public final boolean getReachesIntoOuterContext() {
		return getOuterContextDepth() != 0;
	}

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
	public final int getOuterContextDepth() {
		return (altAndOuterContextDepth >>> 24) & 0x7F;
	}

	public void setOuterContextDepth(int outerContextDepth) {
		assert outerContextDepth >= 0 && outerContextDepth <= 0x7F;
		this.altAndOuterContextDepth = (outerContextDepth << 24) | (altAndOuterContextDepth & ~0x7F000000);
	}

	public int getActionIndex() {
		return -1;
	}

	@NotNull
	public SemanticContext getSemanticContext() {
		return SemanticContext.NONE;
	}

	public boolean isGreedy() {
		return true;
	}

	/** Lexer non-greedy implementations need to track information per
	 *  ATNConfig. When the lexer reaches an accept state for a lexer
	 *  rule, it needs to wipe out any configurations associated with
	 *  that rule that are part of a non-greedy subrule. To do that it
	 *  has to make sure that it tracks when a configuration was derived
	 *  from an element within a non-greedy subrule. We use depth for
	 *  that. We're greedy when the depth is 0.
	 */
	public int getNonGreedyDepth() {
		return 0;
	}

	@Override
	public final ATNConfig clone() {
		return transform(this.getState());
	}

	public final ATNConfig transform(@NotNull ATNState state) {
		return transform(state, this.context, this.getSemanticContext(), this.getActionIndex());
	}

	public final ATNConfig transform(@NotNull ATNState state, @NotNull SemanticContext semanticContext) {
		return transform(state, this.context, semanticContext, this.getActionIndex());
	}

	public final ATNConfig transform(@NotNull ATNState state, @Nullable PredictionContext context) {
		return transform(state, context, this.getSemanticContext(), this.getActionIndex());
	}

	public final ATNConfig transform(@NotNull ATNState state, int actionIndex) {
		return transform(state, context, this.getSemanticContext(), actionIndex);
	}

	public ATNConfig enterNonGreedyBlock() {
		return new NonGreedyATNConfig(this, 1);
	}

	public ATNConfig exitNonGreedyBlock() {
		return this;
	}

	private ATNConfig transform(@NotNull ATNState state, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext, int actionIndex) {
		if (this instanceof NonGreedyATNConfig) {
			ATNConfig transformed = ((NonGreedyATNConfig)this).config.transform(state, context, semanticContext, actionIndex);
			return new NonGreedyATNConfig(transformed, ((NonGreedyATNConfig)this).nonGreedyDepth);
		}

		if (semanticContext != SemanticContext.NONE) {
			if (actionIndex != -1) {
				return new ActionSemanticContextATNConfig(actionIndex, semanticContext, this, state, context);
			}
			else {
				return new SemanticContextATNConfig(semanticContext, this, state, context);
			}
		}
		else if (actionIndex != -1) {
			return new ActionATNConfig(actionIndex, this, state, context);
		}
		else {
			return new ATNConfig(this, state, context);
		}
	}

	public ATNConfig appendContext(int context, PredictionContextCache contextCache) {
		PredictionContext appendedContext = getContext().appendContext(context, contextCache);
		ATNConfig result = transform(getState(), appendedContext);
		return result;
	}

	public ATNConfig appendContext(PredictionContext context, PredictionContextCache contextCache) {
		PredictionContext appendedContext = getContext().appendContext(context, contextCache);
		ATNConfig result = transform(getState(), appendedContext);
		return result;
	}

	public boolean contains(ATNConfig subconfig) {
		if (this.getState().stateNumber != subconfig.getState().stateNumber
			|| this.getAlt() != subconfig.getAlt()
			|| !this.getSemanticContext().equals(subconfig.getSemanticContext())) {
			return false;
		}

		Deque<PredictionContext> leftWorkList = new ArrayDeque<PredictionContext>();
		Deque<PredictionContext> rightWorkList = new ArrayDeque<PredictionContext>();
		leftWorkList.add(getContext());
		rightWorkList.add(subconfig.getContext());
		while (!leftWorkList.isEmpty()) {
			PredictionContext left = leftWorkList.pop();
			PredictionContext right = rightWorkList.pop();

			if (left == right) {
				return true;
			}

			if (left.size() < right.size()) {
				return false;
			}

			if (right.isEmpty()) {
				return left.hasEmpty();
			} else {
				for (int i = 0; i < right.size(); i++) {
					int index = left.findInvokingState(right.getInvokingState(i));
					if (index < 0) {
						// assumes invokingStates has no duplicate entries
						return false;
					}

					leftWorkList.push(left.getParent(index));
					rightWorkList.push(right.getParent(i));
				}
			}
		}

		return false;
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

		return this.getState().stateNumber==other.getState().stateNumber
			&& this.getAlt()==other.getAlt()
			&& this.getReachesIntoOuterContext() == other.getReachesIntoOuterContext()
			&& (this.getContext()==other.getContext() || (this.getContext() != null && this.getContext().equals(other.getContext())))
			&& this.getSemanticContext().equals(other.getSemanticContext())
			&& this.getActionIndex() == other.getActionIndex();
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		hashCode = 5 * hashCode + getState().stateNumber;
		hashCode = 5 * hashCode + getAlt();
		hashCode = 5 * hashCode + (getReachesIntoOuterContext() ? 1 : 0);
		hashCode = 5 * hashCode + (getContext() != null ? getContext().hashCode() : 0);
		hashCode = 5 * hashCode + getSemanticContext().hashCode();
        return hashCode;
    }

	public String toDotString() {
		StringBuilder builder = new StringBuilder();
		builder.append("digraph G {\n");
		builder.append("rankdir=LR;\n");

		Map<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
		workList.add(getContext());
		visited.put(getContext(), getContext());
		while (!workList.isEmpty()) {
			PredictionContext current = workList.pop();
			for (int i = 0; i < current.size(); i++) {
				builder.append("  s").append(System.identityHashCode(current));
				builder.append("->");
				builder.append("s").append(System.identityHashCode(current.getParent(i)));
				builder.append("[label=\"").append(current.getInvokingState(i)).append("\"];\n");
				if (visited.put(current.getParent(i), current.getParent(i)) == null) {
					workList.push(current.getParent(i));
				}
			}
		}

		builder.append("}\n");
		return builder.toString();
	}

	@Override
	public String toString() {
		return toString(null, true, false);
	}

	public String toString(@Nullable Recognizer<?, ?> recog, boolean showAlt) {
		return toString(recog, showAlt, true);
	}

	public String toString(@Nullable Recognizer<?, ?> recog, boolean showAlt, boolean showContext) {
		StringBuilder buf = new StringBuilder();
//		if ( state.ruleIndex>=0 ) {
//			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
//			else buf.append(state.ruleIndex+":");
//		}
		String[] contexts;
		if (showContext) {
			contexts = getContext().toStrings(recog, this.getState().stateNumber);
		}
		else {
			contexts = new String[] { "?" };
		}
		boolean first = true;
		for (String contextDesc : contexts) {
			if ( first ) {
				first = false;
			}
			else {
				buf.append(", ");
			}

			buf.append('(');
			buf.append(getState());
			if ( showAlt ) {
				buf.append(",");
				buf.append(getAlt());
			}
			if ( getContext()!=null ) {
				buf.append(",");
				buf.append(contextDesc);
			}
			if ( getSemanticContext()!=null && getSemanticContext() != SemanticContext.NONE ) {
				buf.append(",");
				buf.append(getSemanticContext());
			}
			if ( getReachesIntoOuterContext() ) {
				buf.append(",up=").append(getOuterContextDepth());
			}
			buf.append(')');
		}
		return buf.toString();
    }

	private static class SemanticContextATNConfig extends ATNConfig {

		@NotNull
		private final SemanticContext semanticContext;

		public SemanticContextATNConfig(SemanticContext semanticContext, @NotNull ATNState state, int alt, @Nullable PredictionContext context) {
			super(state, alt, context);
			this.semanticContext = semanticContext;
		}

		public SemanticContextATNConfig(SemanticContext semanticContext, @NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context) {
			super(c, state, context);
			this.semanticContext = semanticContext;
		}

		@Override
		public SemanticContext getSemanticContext() {
			return semanticContext;
		}

	}

	private static class ActionATNConfig extends ATNConfig {

		private final int actionIndex;

		public ActionATNConfig(int actionIndex, @NotNull ATNState state, int alt, @Nullable PredictionContext context) {
			super(state, alt, context);
			this.actionIndex = actionIndex;
		}

		protected ActionATNConfig(int actionIndex, @NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context) {
			super(c, state, context);
			if (c.getSemanticContext() != SemanticContext.NONE) {
				throw new UnsupportedOperationException();
			}

			this.actionIndex = actionIndex;
		}

		@Override
		public int getActionIndex() {
			return actionIndex;
		}

	}

	private static class ActionSemanticContextATNConfig extends SemanticContextATNConfig {

		private final int actionIndex;

		public ActionSemanticContextATNConfig(int actionIndex, @NotNull SemanticContext semanticContext, @NotNull ATNState state, int alt, @Nullable PredictionContext context) {
			super(semanticContext, state, alt, context);
			this.actionIndex = actionIndex;
		}

		public ActionSemanticContextATNConfig(int actionIndex, @NotNull SemanticContext semanticContext, @NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context) {
			super(semanticContext, c, state, context);
			this.actionIndex = actionIndex;
		}

		@Override
		public int getActionIndex() {
			return actionIndex;
		}

	}

	private static class NonGreedyATNConfig extends ATNConfig {
		private final ATNConfig config;
		private final int nonGreedyDepth;

		public NonGreedyATNConfig(ATNConfig config, int nonGreedyDepth) {
			super(config, config.state, config.context);
			this.nonGreedyDepth = nonGreedyDepth;
			this.config = config;
		}

		public NonGreedyATNConfig(int nonGreedyDepth, ATNConfig config, ATNState state, int alt, PredictionContext context) {
			super(state, alt, context);
			this.nonGreedyDepth = nonGreedyDepth;
			this.config = config;
		}

		public ATNConfig getConfig() {
			return config;
		}

		@Override
		public boolean isGreedy() {
			return false;
		}

		@Override
		public int getNonGreedyDepth() {
			return nonGreedyDepth;
		}

		@Override
		public int getActionIndex() {
			return config.getActionIndex();
		}

		@Override
		public SemanticContext getSemanticContext() {
			return config.getSemanticContext();
		}

		@Override
		public void setContext(PredictionContext context) {
			super.setContext(context);
			config.setContext(context);
		}

		@Override
		public void setHidden(boolean value) {
			super.setHidden(value);
			config.setHidden(value);
		}

		@Override
		public void setOuterContextDepth(int outerContextDepth) {
			super.setOuterContextDepth(outerContextDepth);
			config.setOuterContextDepth(outerContextDepth);
		}

		@Override
		public ATNConfig enterNonGreedyBlock() {
			return new NonGreedyATNConfig(config, nonGreedyDepth + 1);
		}

		@Override
		public ATNConfig exitNonGreedyBlock() {
			if (nonGreedyDepth == 1) {
				return config;
			}

			return new NonGreedyATNConfig(config, nonGreedyDepth - 1);
		}

	}
}
