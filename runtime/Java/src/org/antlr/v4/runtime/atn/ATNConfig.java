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
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

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
		return create(state, alt, context, SemanticContext.NONE, null);
	}

	public static ATNConfig create(@NotNull ATNState state, int alt, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext) {
		return create(state, alt, context, semanticContext, null);
	}

	public static ATNConfig create(@NotNull ATNState state, int alt, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext, LexerActionExecutor lexerActionExecutor) {
		if (semanticContext != SemanticContext.NONE) {
			if (lexerActionExecutor != null) {
				return new ActionSemanticContextATNConfig(lexerActionExecutor, semanticContext, state, alt, context, false);
			}
			else {
				return new SemanticContextATNConfig(semanticContext, state, alt, context);
			}
		}
		else if (lexerActionExecutor != null) {
			return new ActionATNConfig(lexerActionExecutor, state, alt, context, false);
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
		assert outerContextDepth >= 0;
		// saturate at 0x7F - everything but zero/positive is only used for debug information anyway
		outerContextDepth = Math.min(outerContextDepth, 0x7F);
		this.altAndOuterContextDepth = (outerContextDepth << 24) | (altAndOuterContextDepth & ~0x7F000000);
	}

	@Nullable
	public LexerActionExecutor getLexerActionExecutor() {
		return null;
	}

	@NotNull
	public SemanticContext getSemanticContext() {
		return SemanticContext.NONE;
	}

	public boolean hasPassedThroughNonGreedyDecision() {
		return false;
	}

	@Override
	public final ATNConfig clone() {
		return transform(this.getState(), false);
	}

	public final ATNConfig transform(@NotNull ATNState state, boolean checkNonGreedy) {
		return transform(state, this.context, this.getSemanticContext(), checkNonGreedy, this.getLexerActionExecutor());
	}

	public final ATNConfig transform(@NotNull ATNState state, @NotNull SemanticContext semanticContext, boolean checkNonGreedy) {
		return transform(state, this.context, semanticContext, checkNonGreedy, this.getLexerActionExecutor());
	}

	public final ATNConfig transform(@NotNull ATNState state, @Nullable PredictionContext context, boolean checkNonGreedy) {
		return transform(state, context, this.getSemanticContext(), checkNonGreedy, this.getLexerActionExecutor());
	}

	public final ATNConfig transform(@NotNull ATNState state, LexerActionExecutor lexerActionExecutor, boolean checkNonGreedy) {
		return transform(state, context, this.getSemanticContext(), checkNonGreedy, lexerActionExecutor);
	}

	private ATNConfig transform(@NotNull ATNState state, @Nullable PredictionContext context, @NotNull SemanticContext semanticContext, boolean checkNonGreedy, LexerActionExecutor lexerActionExecutor) {
		boolean passedThroughNonGreedy = checkNonGreedy && checkNonGreedyDecision(this, state);
		if (semanticContext != SemanticContext.NONE) {
			if (lexerActionExecutor != null || passedThroughNonGreedy) {
				return new ActionSemanticContextATNConfig(lexerActionExecutor, semanticContext, this, state, context, passedThroughNonGreedy);
			}
			else {
				return new SemanticContextATNConfig(semanticContext, this, state, context);
			}
		}
		else if (lexerActionExecutor != null || passedThroughNonGreedy) {
			return new ActionATNConfig(lexerActionExecutor, this, state, context, passedThroughNonGreedy);
		}
		else {
			return new ATNConfig(this, state, context);
		}
	}

	private static boolean checkNonGreedyDecision(ATNConfig source, ATNState target) {
		return source.hasPassedThroughNonGreedyDecision()
			|| target instanceof DecisionState && ((DecisionState)target).nonGreedy;
	}

	public ATNConfig appendContext(int context, PredictionContextCache contextCache) {
		PredictionContext appendedContext = getContext().appendContext(context, contextCache);
		ATNConfig result = transform(getState(), appendedContext, false);
		return result;
	}

	public ATNConfig appendContext(PredictionContext context, PredictionContextCache contextCache) {
		PredictionContext appendedContext = getContext().appendContext(context, contextCache);
		ATNConfig result = transform(getState(), appendedContext, false);
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
					int index = left.findReturnState(right.getReturnState(i));
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
			&& this.getContext().equals(other.getContext())
			&& this.getSemanticContext().equals(other.getSemanticContext())
			&& this.hasPassedThroughNonGreedyDecision() == other.hasPassedThroughNonGreedyDecision()
			&& ObjectEqualityComparator.INSTANCE.equals(this.getLexerActionExecutor(), other.getLexerActionExecutor());
	}

	@Override
	public int hashCode() {
		int hashCode = MurmurHash.initialize(7);
		hashCode = MurmurHash.update(hashCode, getState().stateNumber);
		hashCode = MurmurHash.update(hashCode, getAlt());
		hashCode = MurmurHash.update(hashCode, getReachesIntoOuterContext() ? 1 : 0);
		hashCode = MurmurHash.update(hashCode, getContext());
		hashCode = MurmurHash.update(hashCode, getSemanticContext());
		hashCode = MurmurHash.update(hashCode, hasPassedThroughNonGreedyDecision() ? 1 : 0);
		hashCode = MurmurHash.update(hashCode, getLexerActionExecutor());
		hashCode = MurmurHash.finish(hashCode, 7);
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
				builder.append("[label=\"").append(current.getReturnState(i)).append("\"];\n");
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

		private final LexerActionExecutor lexerActionExecutor;
		private final boolean passedThroughNonGreedyDecision;

		public ActionATNConfig(LexerActionExecutor lexerActionExecutor, @NotNull ATNState state, int alt, @Nullable PredictionContext context, boolean passedThroughNonGreedyDecision) {
			super(state, alt, context);
			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
		}

		protected ActionATNConfig(LexerActionExecutor lexerActionExecutor, @NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context, boolean passedThroughNonGreedyDecision) {
			super(c, state, context);
			if (c.getSemanticContext() != SemanticContext.NONE) {
				throw new UnsupportedOperationException();
			}

			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
		}

		@Override
		public LexerActionExecutor getLexerActionExecutor() {
			return lexerActionExecutor;
		}

		@Override
		public boolean hasPassedThroughNonGreedyDecision() {
			return passedThroughNonGreedyDecision;
		}
	}

	private static class ActionSemanticContextATNConfig extends SemanticContextATNConfig {

		private final LexerActionExecutor lexerActionExecutor;
		private final boolean passedThroughNonGreedyDecision;

		public ActionSemanticContextATNConfig(LexerActionExecutor lexerActionExecutor, @NotNull SemanticContext semanticContext, @NotNull ATNState state, int alt, @Nullable PredictionContext context, boolean passedThroughNonGreedyDecision) {
			super(semanticContext, state, alt, context);
			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
		}

		public ActionSemanticContextATNConfig(LexerActionExecutor lexerActionExecutor, @NotNull SemanticContext semanticContext, @NotNull ATNConfig c, @NotNull ATNState state, @Nullable PredictionContext context, boolean passedThroughNonGreedyDecision) {
			super(semanticContext, c, state, context);
			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
		}

		@Override
		public LexerActionExecutor getLexerActionExecutor() {
			return lexerActionExecutor;
		}

		@Override
		public boolean hasPassedThroughNonGreedyDecision() {
			return passedThroughNonGreedyDecision;
		}
	}

}
