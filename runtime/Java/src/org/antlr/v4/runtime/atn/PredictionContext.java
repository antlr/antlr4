/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;

public class PredictionContext {
	@NotNull
	public static final PredictionContext EMPTY = new PredictionContext();
	public static final int EMPTY_STATE_KEY = Integer.MAX_VALUE;
	public static final int EMPTY_HASH_CODE = EMPTY.hashCode();

	public final PredictionContext parent;
	public final int invokingState;
	private final int parentHashCode;
	private final int invokingStateHashCode;

	private PredictionContext() {
		parent = null;
		invokingState = EMPTY_STATE_KEY;
		parentHashCode = EMPTY_HASH_CODE;
		invokingStateHashCode = 1;
	}

	private PredictionContext(@NotNull PredictionContext parent, int invokingState) {
		this(parent, invokingState, 31 + parent.hashCode(), 31 + invokingState);
		assert invokingState >= 0;
		assert parent != null;
	}

	private PredictionContext(@NotNull PredictionContext parent, int invokingState, int parentHashCode, int invokingStateHashCode) {
		assert invokingState != EMPTY_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";

		this.parent = parent;
		this.invokingState = invokingState;
		this.parentHashCode = parentHashCode;
		this.invokingStateHashCode = invokingStateHashCode;
	}

	public static PredictionContext fromRuleContext(@NotNull RuleContext outerContext) {
		if (outerContext.isEmpty()) {
			return PredictionContext.EMPTY;
		}

		PredictionContext parent;
		if (outerContext.parent != null) {
			parent = PredictionContext.fromRuleContext(outerContext.parent);
		} else {
			parent = PredictionContext.EMPTY;
		}

		return parent.getChild(outerContext.invokingState);
	}

	public PredictionContext getChild(int invokingState) {
		return new PredictionContext(this, invokingState);
	}

	public boolean isEmpty() {
		return invokingState == EMPTY_STATE_KEY;
	}

	/** Two contexts conflict() if they are equals() or one is a stack suffix
	 *  of the other.  For example, contexts [21 12 $] and [21 9 $] do not
	 *  conflict, but [21 $] and [21 12 $] do conflict.  Note that I should
	 *  probably not show the $ in this case.  There is a dummy node for each
	 *  stack that just means empty; $ is a marker that's all.
	 *
	 *  This is used in relation to checking conflicts associated with a
	 *  single NFA state's configurations within a single DFA state.
	 *  If there are configurations s and t within a DFA state such that
	 *  s.state=t.state && s.alt != t.alt && s.ctx conflicts t.ctx then
	 *  the DFA state predicts more than a single alt--it's nondeterministic.
	 *  Two contexts conflict if they are the same or if one is a suffix
	 *  of the other.
	 *
	 *  When comparing contexts, if one context has a stack and the other
	 *  does not then they should be considered the same context.  The only
	 *  way for an NFA state p to have an empty context and a nonempty context
	 *  is the case when closure falls off end of rule without a call stack
	 *  and re-enters the rule with a context.  This resolves the issue I
	 *  discussed with Sriram Srinivasan Feb 28, 2005 about not terminating
	 *  fast enough upon nondeterminism.
	 */
	public boolean conflictsWith(PredictionContext other) {
		return this.suffix(other) || this.equals(other);
	}

	/** [$] suffix any context
	 *  [21 $] suffix [21 12 $]
	 *  [21 12 $] suffix [21 $]
	 *  [21 18 $] suffix [21 18 12 9 $]
	 *  [21 18 12 9 $] suffix [21 18 $]
	 *  [21 12 $] not suffix [21 9 $]
	 *
	 *  Example "[21 $] suffix [21 12 $]" means: rule r invoked current rule
	 *  from state 21.  Rule s invoked rule r from state 12 which then invoked
	 *  current rule also via state 21.  While the context prior to state 21
	 *  is different, the fact that both contexts emanate from state 21 implies
	 *  that they are now going to track perfectly together.  Once they
	 *  converged on state 21, there is no way they can separate.  In other
	 *  words, the prior stack state is not consulted when computing where to
	 *  go in the closure operation.  ?$ and ??$ are considered the same stack.
	 *  If ? is popped off then $ and ?$ remain; they are now an empty and
	 *  nonempty context comparison.  So, if one stack is a suffix of
	 *  another, then it will still degenerate to the simple empty stack
	 *  comparison case.
	 */
	protected boolean suffix(PredictionContext other) {
		PredictionContext sp = this;
		// if one of the contexts is empty, it never enters loop and returns true
		while ( sp.parent!=null && other.parent!=null ) {
			if ( sp.invokingState != other.invokingState ) {
				return false;
			}
			sp = sp.parent;
			other = other.parent;
		}
		//System.out.println("suffix");
		return true;
	}

	@Override
	public int hashCode() {
		return 5 * 5 * 7 + 5 * parentHashCode + invokingStateHashCode;
	}

	public boolean equals(PredictionContext other) {
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		}

		if ( this.hashCode() != other.hashCode() ) {
			return false; // can't be same if hash is different
		}

		return invokingState == other.invokingState
			&& (parent == other.parent || (parent != null && parent.equals(other.parent)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof PredictionContext)) {
			return false;
		}

		return this.equals((PredictionContext)o);
	}

	@Override
	public String toString() {
		return toString(null, Integer.MAX_VALUE);
	}

	public String toString(Recognizer<?, ?> recognizer, int currentState) {
		return toString(recognizer, PredictionContext.EMPTY, currentState);
	}

	public String toString(Recognizer<?, ?> recognizer, PredictionContext stop, int currentState) {
		if ( recognizer==null ) {
			StringBuilder buf = new StringBuilder();
			PredictionContext p = this;
			buf.append("[");
			while ( p != null && p != stop ) {
				if ( !p.isEmpty() ) buf.append(p.invokingState);
				if ( p.parent != null && !p.parent.isEmpty() ) buf.append(" ");
				p = p.parent;
			}
			buf.append("]");
			return buf.toString();
		}
		else {
			StringBuilder buf = new StringBuilder();
			PredictionContext p = this;
			int stateNumber = currentState;
			buf.append("[");
			while ( p != null && p != stop ) {
				ATN atn = recognizer.getATN();
				ATNState s = atn.states.get(stateNumber);
				String ruleName = recognizer.getRuleNames()[s.ruleIndex];
				buf.append(ruleName);
				if ( p.parent != null ) buf.append(" ");
//				ATNState invoker = atn.states.get(ctx.invokingState);
//				RuleTransition rt = (RuleTransition)invoker.transition(0);
//				buf.append(recog.getRuleNames()[rt.target.ruleIndex]);
				stateNumber = p.invokingState;
				p = p.parent;
			}
			buf.append("]");
			return buf.toString();
		}
	}
}
