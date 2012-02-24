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

import java.util.*;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;

public abstract class PredictionContext {
	@NotNull
	public static final PredictionContext EMPTY = new EmptyPredictionContext();
	public static final int EMPTY_STATE_KEY = Integer.MAX_VALUE;

	private final int cachedHashCode;

	protected PredictionContext(int cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

	protected static int calculateParentHashCode(PredictionContext[] parents) {
		int hashCode = 1;
		for (PredictionContext context : parents) {
			hashCode = hashCode * 31 ^ context.hashCode();
		}

		return hashCode;
	}

	protected static int calculateInvokingStatesHashCode(int[] invokingStates) {
		int hashCode = 1;
		for (int state : invokingStates) {
			hashCode = hashCode * 31 ^ state;
		}

		return hashCode;
	}

	protected static int calculateHashCode(int parentHashCode, int invokingStateHashCode) {
		return 5 * 5 * 7 + 5 * parentHashCode + invokingStateHashCode;
	}

	public abstract int size();

	public abstract int getInvokingState(int index);

	public abstract int findInvokingState(int invokingState);

	@NotNull
	public abstract PredictionContext getParent(int index);

	protected abstract PredictionContext addEmptyContext();

	public static PredictionContext fromRuleContext(@NotNull RuleContext<?> outerContext) {
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

	private static PredictionContext addEmptyContext(PredictionContext context) {
		return context.addEmptyContext();
	}

	public static PredictionContext join(PredictionContext context0, PredictionContext context1, boolean local) {
		return join(context0, context1, local ? PredictionContextCache.UNCACHED_LOCAL : PredictionContextCache.UNCACHED_FULL);
	}

	/*package*/ static PredictionContext join(@NotNull final PredictionContext context0, @NotNull final PredictionContext context1, @NotNull PredictionContextCache contextCache) {
		if (context0 == context1) {
			return context0;
		}

		boolean local = !contextCache.isContextSensitive();
		if (context0.isEmpty()) {
			return local ? context0 : addEmptyContext(context1);
		} else if (context1.isEmpty()) {
			return local ? context1 : addEmptyContext(context0);
		}

		if (local && (context0.hasEmpty() || context1.hasEmpty())) {
			return PredictionContext.EMPTY;
		}

		final int context0size = context0.size();
		final int context1size = context1.size();
		if (context0size == 1 && context1size == 1 && context0.getInvokingState(0) == context1.getInvokingState(0)) {
			PredictionContext merged = contextCache.join(context0.getParent(0), context1.getParent(0));
			if (merged == context0.getParent(0)) {
				return context0;
			} else if (merged == context1.getParent(0)) {
				return context1;
			} else {
				return merged.getChild(context0.getInvokingState(0));
			}
		}

		int count = 0;
		int parentHashCode = 1;
		int invokingStateHashCode = 1;
		PredictionContext[] parentsList = new PredictionContext[context0size + context1size];
		int[] invokingStatesList = new int[parentsList.length];
		int leftIndex = 0;
		int rightIndex = 0;
		boolean canReturnLeft = true;
		boolean canReturnRight = true;
		while (leftIndex < context0size && rightIndex < context1size) {
			if (context0.getInvokingState(leftIndex) == context1.getInvokingState(rightIndex)) {
				parentsList[count] = contextCache.join(context0.getParent(leftIndex), context1.getParent(rightIndex));
				invokingStatesList[count] = context0.getInvokingState(leftIndex);
				canReturnLeft = canReturnLeft && parentsList[count] == context0.getParent(leftIndex);
				canReturnRight = canReturnRight && parentsList[count] == context1.getParent(rightIndex);
				leftIndex++;
				rightIndex++;
			}
			else if (context0.getInvokingState(leftIndex) < context1.getInvokingState(rightIndex)) {
				parentsList[count] = context0.getParent(leftIndex);
				invokingStatesList[count] = context0.getInvokingState(leftIndex);
				canReturnRight = false;
				leftIndex++;
			}
			else {
				assert context1.getInvokingState(rightIndex) < context0.getInvokingState(leftIndex);
				parentsList[count] = context1.getParent(rightIndex);
				invokingStatesList[count] = context1.getInvokingState(rightIndex);
				canReturnLeft = false;
				rightIndex++;
			}

			parentHashCode = 31 * parentHashCode ^ parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode ^ invokingStatesList[count];
			count++;
		}

		while (leftIndex < context0size) {
			parentsList[count] = context0.getParent(leftIndex);
			invokingStatesList[count] = context0.getInvokingState(leftIndex);
			leftIndex++;
			canReturnRight = false;
			parentHashCode = 31 * parentHashCode ^ parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode ^ invokingStatesList[count];
			count++;
		}

		while (rightIndex < context1size) {
			parentsList[count] = context1.getParent(rightIndex);
			invokingStatesList[count] = context1.getInvokingState(rightIndex);
			rightIndex++;
			canReturnLeft = false;
			parentHashCode = 31 * parentHashCode ^ parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode ^ invokingStatesList[count];
			count++;
		}

		if (canReturnLeft) {
			return context0;
		}
		else if (canReturnRight) {
			return context1;
		}

		if (count < parentsList.length) {
			parentsList = Arrays.copyOf(parentsList, count);
			invokingStatesList = Arrays.copyOf(invokingStatesList, count);
		}

		if (parentsList.length == 0) {
			return EMPTY;
		}
		else if (parentsList.length == 1) {
			return new SingletonPredictionContext(parentsList[0], invokingStatesList[0]);
		}
		else {
			return new ArrayPredictionContext(parentsList, invokingStatesList, parentHashCode, invokingStateHashCode);
		}
	}

	public static PredictionContext getCachedContext(
		@NotNull PredictionContext context,
		@NotNull Map<PredictionContext, PredictionContext> contextCache,
		@NotNull IdentityHashMap<PredictionContext, PredictionContext> visited) {
		if (context.isEmpty()) {
			return context;
		}

		PredictionContext existing = visited.get(context);
		if (existing != null) {
			return existing;
		}

		existing = contextCache.get(context);
		if (existing != null) {
			visited.put(context, existing);
			return existing;
		}

		boolean changed = false;
		PredictionContext[] parents = new PredictionContext[context.size()];
		for (int i = 0; i < parents.length; i++) {
			PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
			if (changed || parent != context.getParent(i)) {
				if (!changed) {
					parents = new PredictionContext[context.size()];
					for (int j = 0; j < context.size(); j++) {
						parents[j] = context.getParent(j);
					}

					changed = true;
				}

				parents[i] = parent;
			}
		}

		if (!changed) {
			contextCache.put(context, context);
			visited.put(context, context);
			return context;
		}

		PredictionContext updated;
		if (parents.length == 0) {
			updated = EMPTY;
		}
		else if (parents.length == 1) {
			updated = new SingletonPredictionContext(parents[0], context.getInvokingState(0));
		}
		else {
			ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
			updated = new ArrayPredictionContext(parents, arrayPredictionContext.invokingStates, context.cachedHashCode);
		}

		contextCache.put(updated, updated);
		visited.put(updated, updated);
		visited.put(context, updated);

		return updated;
	}

	public PredictionContext appendContext(int invokingContext, PredictionContextCache contextCache) {
		return appendContext(PredictionContext.EMPTY.getChild(invokingContext), contextCache);
	}

	public abstract PredictionContext appendContext(PredictionContext suffix, PredictionContextCache contextCache);

	public abstract PredictionContext popAll(int invokingState, PredictionContextCache contextCache);

	public PredictionContext getChild(int invokingState) {
		return new SingletonPredictionContext(this, invokingState);
	}

	public abstract boolean isEmpty();

	public abstract boolean hasEmpty();

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
		return this.suffix(other); // || this.equals(other);
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

		int currentSize = this.size();
		int otherSize = other.size();

		if (currentSize == 0 || otherSize == 0) {
			return true;
		}

		Deque<PredictionContext> leftWorkList = new ArrayDeque<PredictionContext>();
		Deque<PredictionContext> rightWorkList = new ArrayDeque<PredictionContext>();
		leftWorkList.add(this);
		rightWorkList.add(other);
		while (!leftWorkList.isEmpty()) {
			PredictionContext left = leftWorkList.pop();
			PredictionContext right = rightWorkList.pop();

			if (left == right || left.isEmpty() || right.isEmpty()) {
				continue;
			}

			final int leftSize = left.size();
			final int rightSize = right.size();
			int leftIndex = 0;
			int rightIndex = 0;
			int leftState = left.getInvokingState(0);
			int rightState = right.getInvokingState(0);
			while (leftIndex < leftSize || rightIndex < rightSize) {
				if (leftState == rightState) {
					PredictionContext leftParent = left.getParent(leftIndex);
					PredictionContext rightParent = right.getParent(rightIndex);
					if (leftParent != rightParent) {
						leftWorkList.push(left.getParent(leftIndex));
						rightWorkList.push(right.getParent(rightIndex));
					}
					if (leftState != -1) {
						leftIndex++;
						leftState = (leftIndex < leftSize) ? left.getInvokingState(leftIndex) : -1;
					}
					if (rightState != -1) {
						rightIndex++;
						rightState = (rightIndex < rightSize) ? right.getInvokingState(rightIndex) : -1;
					}

					continue;
				}

				if (leftState < rightState) {
					if (leftState == -1 && rightState != EMPTY_STATE_KEY) {
						return false;
					} else if (leftState != -1) {
						return false;
					}

					break;
				} else {
					assert rightState < leftState;
					if (rightState == -1 && leftState != EMPTY_STATE_KEY) {
						return false;
					} else if (rightState != -1) {
						return false;
					}

					break;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	@Override
	public abstract boolean equals(Object o);

	//@Override
	//public String toString() {
	//	return toString(null, Integer.MAX_VALUE);
	//}

	public String[] toStrings(Recognizer<?, ?> recognizer, int currentState) {
		return toStrings(recognizer, PredictionContext.EMPTY, currentState);
	}

	public String[] toStrings(Recognizer<?, ?> recognizer, PredictionContext stop, int currentState) {
		List<String> result = new ArrayList<String>();

		outer:
		for (int perm = 0; ; perm++) {
			int offset = 0;
			boolean last = true;
			PredictionContext p = this;
			int stateNumber = currentState;
			StringBuilder localBuffer = new StringBuilder();
			localBuffer.append("[");
			while ( p != null && p != stop ) {
				int index = 0;
				if (p.size() > 0) {
					int bits = 1;
					while ((1 << bits) < p.size()) {
						bits++;
					}

					int mask = (1 << bits) - 1;
					index = (perm >> offset) & mask;
					last &= index >= p.size() - 1;
					if (index >= p.size()) {
						continue outer;
					}
					offset += bits;
				}

				if ( recognizer!=null ) {
					if (localBuffer.length() > 1) {
						// first char is '[', if more than that this isn't the first rule
						localBuffer.append(' ');
					}

					ATN atn = recognizer.getATN();
					ATNState s = atn.states.get(stateNumber);
					String ruleName = recognizer.getRuleNames()[s.ruleIndex];
					localBuffer.append(ruleName);
				}
				else if ( p.getInvokingState(index)!=EMPTY_STATE_KEY ) {
					if ( !p.isEmpty() ) {
						if (localBuffer.length() > 1) {
							// first char is '[', if more than that this isn't the first rule
							localBuffer.append(' ');
						}

						localBuffer.append(p.getInvokingState(index));
					}
				}
				stateNumber = p.getInvokingState(index);
				p = p.getParent(index);
			}
			localBuffer.append("]");
			result.add(localBuffer.toString());

			if (last) {
				break;
			}
		}

		return result.toArray(new String[result.size()]);
	}
}
