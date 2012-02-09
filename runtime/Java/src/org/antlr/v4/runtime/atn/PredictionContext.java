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

public class PredictionContext {
	@NotNull
	public static final PredictionContext EMPTY = new PredictionContext();
	public static final int EMPTY_STATE_KEY = Integer.MAX_VALUE;
	public static final int EMPTY_HASH_CODE = EMPTY.hashCode();

	@NotNull
	public final PredictionContext[] parents;
	@NotNull
	public final int[] invokingStates;
	private final int parentHashCode;
	private final int invokingStateHashCode;

	private PredictionContext() {
		parents = new PredictionContext[0];
		invokingStates = new int[0];
		parentHashCode = 1;
		invokingStateHashCode = 1;
	}

	private PredictionContext(@NotNull PredictionContext parent, int invokingState) {
		this(new PredictionContext[] { parent }, new int[] { invokingState }, 31 + parent.hashCode(), 31 + invokingState);
		assert invokingState >= 0;
		assert parent != null;
	}

	private PredictionContext(@NotNull PredictionContext[] parents, int[] invokingStates, int parentHashCode, int invokingStateHashCode) {
		assert parents.length == invokingStates.length;
		assert invokingStates.length > 0 && invokingStates[0] != EMPTY_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";

		this.parents = parents;
		this.invokingStates = invokingStates;
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

	private static PredictionContext addEmptyContext(PredictionContext context) {
		if (context.hasEmpty()) {
			return context;
		}

		PredictionContext[] parents = Arrays.copyOf(context.parents, context.parents.length + 1);
		int[] invokingStates = Arrays.copyOf(context.invokingStates, context.invokingStates.length + 1);
		parents[parents.length - 1] = PredictionContext.EMPTY;
		invokingStates[invokingStates.length - 1] = PredictionContext.EMPTY_STATE_KEY;
		int newParentHashCode = 31 * context.parentHashCode + EMPTY_HASH_CODE;
		int newInvokingStateHashCode = 31 * context.invokingStateHashCode + PredictionContext.EMPTY_STATE_KEY;
		return new PredictionContext(parents, invokingStates, newParentHashCode, newInvokingStateHashCode);
	}

	public static PredictionContext join(PredictionContext context0, PredictionContext context1, boolean local) {
		if (context0 == context1) {
			return context0;
		}

		if (context0.isEmpty()) {
			return local ? context0 : addEmptyContext(context1);
		} else if (context1.isEmpty()) {
			return local ? context1 : addEmptyContext(context0);
		}

		if (local && (context0.hasEmpty() || context1.hasEmpty())) {
			return PredictionContext.EMPTY;
		}

		if (context0.parents.length == 1 && context1.parents.length == 1 && context0.invokingStates[0] == context1.invokingStates[0]) {
			PredictionContext merged = join(context0.parents[0], context1.parents[0], local);
			if (merged == context0.parents[0]) {
				return context0;
			} else if (merged == context1.parents[0]) {
				return context1;
			} else {
				return new PredictionContext(merged, context0.invokingStates[0]);
			}
		}

		int count = 0;
		int parentHashCode = 1;
		int invokingStateHashCode = 1;
		PredictionContext[] parentsList = new PredictionContext[context0.parents.length + context1.parents.length];
		int[] invokingStatesList = new int[parentsList.length];
		int leftIndex = 0;
		int rightIndex = 0;
		boolean canReturnLeft = true;
		boolean canReturnRight = true;
		while (leftIndex < context0.parents.length && rightIndex < context1.parents.length) {
			if (context0.invokingStates[leftIndex] == context1.invokingStates[rightIndex]) {
				parentsList[count] = join(context0.parents[leftIndex], context1.parents[rightIndex], local);
				invokingStatesList[count] = context0.invokingStates[leftIndex];
				canReturnLeft = canReturnLeft && parentsList[count] == context0.parents[leftIndex];
				canReturnRight = canReturnRight && parentsList[count] == context1.parents[rightIndex];
				leftIndex++;
				rightIndex++;
			} else if (context0.invokingStates[leftIndex] < context1.invokingStates[rightIndex]) {
				parentsList[count] = context0.parents[leftIndex];
				invokingStatesList[count] = context0.invokingStates[leftIndex];
				canReturnRight = false;
				leftIndex++;
			} else {
				assert context1.invokingStates[rightIndex] < context0.invokingStates[leftIndex];
				parentsList[count] = context1.parents[rightIndex];
				invokingStatesList[count] = context1.invokingStates[rightIndex];
				canReturnLeft = false;
				rightIndex++;
			}

			parentHashCode = 31 * parentHashCode + parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode + invokingStatesList[count];
			count++;
		}

		while (leftIndex < context0.parents.length) {
			parentsList[count] = context0.parents[leftIndex];
			invokingStatesList[count] = context0.invokingStates[leftIndex];
			leftIndex++;
			canReturnRight = false;
			parentHashCode = 31 * parentHashCode + parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode + invokingStatesList[count];
			count++;
		}

		while (rightIndex < context1.parents.length) {
			parentsList[count] = context1.parents[rightIndex];
			invokingStatesList[count] = context1.invokingStates[rightIndex];
			rightIndex++;
			canReturnLeft = false;
			parentHashCode = 31 * parentHashCode + parentsList[count].hashCode();
			invokingStateHashCode = 31 * invokingStateHashCode + invokingStatesList[count];
			count++;
		}

		if (canReturnLeft) {
			return context0;
		} else if (canReturnRight) {
			return context1;
		}

		if (count < parentsList.length) {
			parentsList = Arrays.copyOf(parentsList, count);
			invokingStatesList = Arrays.copyOf(invokingStatesList, count);
		}

		return new PredictionContext(parentsList, invokingStatesList, parentHashCode, invokingStateHashCode);
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
		PredictionContext[] parents = new PredictionContext[context.parents.length];
		for (int i = 0; i < parents.length; i++) {
			PredictionContext parent = getCachedContext(context.parents[i], contextCache, visited);
			if (changed || parent != context.parents[i]) {
				if (!changed) {
					parents = Arrays.copyOf(context.parents, context.parents.length);
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

		PredictionContext updated = new PredictionContext(parents, context.invokingStates, context.parentHashCode, context.invokingStateHashCode);
		contextCache.put(updated, updated);
		visited.put(updated, updated);
		visited.put(context, updated);

		return updated;
	}

	public PredictionContext getChild(int invokingState) {
		return new PredictionContext(this, invokingState);
	}

	public boolean isEmpty() {
		return parents.length == 0;
	}

	public boolean hasEmpty() {
		return isEmpty() || invokingStates[invokingStates.length - 1] == EMPTY_STATE_KEY;
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
		PredictionContext sp = this;

		if (invokingStates.length == 0 || other.invokingStates.length == 0) {
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

			int leftIndex = 0;
			int rightIndex = 0;
			int leftState = left.invokingStates[0];
			int rightState = right.invokingStates[0];
			boolean leftHasEmpty = left.hasEmpty();
			boolean rightHasEmpty = right.hasEmpty();
			while (leftIndex < left.invokingStates.length || rightIndex < right.invokingStates.length) {
				if (leftState == rightState) {
					PredictionContext leftParent = left.parents[leftIndex];
					PredictionContext rightParent = right.parents[rightIndex];
					if (leftParent != rightParent) {
						leftWorkList.push(left.parents[leftIndex]);
						rightWorkList.push(right.parents[rightIndex]);
					}
					if (leftState != -1) {
						leftIndex++;
						leftState = (leftIndex < left.invokingStates.length) ? left.invokingStates[leftIndex] : -1;
					}
					if (rightState != -1) {
						rightIndex++;
						rightState = (rightIndex < right.invokingStates.length) ? right.invokingStates[rightIndex] : -1;
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

		return Arrays.equals(invokingStates, other.invokingStates)
			&& Arrays.equals(parents, other.parents);
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
				if (p.parents.length > 0) {
					int bits = 1;
					while ((1 << bits) < p.parents.length) {
						bits++;
					}

					int mask = (1 << bits) - 1;
					index = (perm >> offset) & mask;
					last &= index >= p.parents.length - 1;
					if (index >= p.parents.length) {
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
				else if ( p.invokingStates[index]!=EMPTY_STATE_KEY ) {
					if ( !p.isEmpty() ) {
						if (localBuffer.length() > 1) {
							// first char is '[', if more than that this isn't the first rule
							localBuffer.append(' ');
						}

						localBuffer.append(p.invokingStates[index]);
					}
				}
				stateNumber = p.invokingStates[index];
				p = p.parents[index];
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
