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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class PredictionContext {
	@NotNull
	public static final PredictionContext EMPTY_LOCAL = EmptyPredictionContext.LOCAL_CONTEXT;
	@NotNull
	public static final PredictionContext EMPTY_FULL = EmptyPredictionContext.FULL_CONTEXT;

	public static final int EMPTY_LOCAL_STATE_KEY = Integer.MIN_VALUE;
	public static final int EMPTY_FULL_STATE_KEY = Integer.MAX_VALUE;

	private static final int INITIAL_HASH = 1;
	private static final int HASH_MULTIPLIER = 31;

	/**
	 * Stores the computed hash code of this PredictionContext. The hash code is
	 * computed in parts to match the following reference algorithm.
	 *
	 * <pre>
	 *  private int referenceHashCode() {
	 *  	int invokingStateHashCode = {@link #INITIAL_HASH INITIAL_HASH};
	 *  	for (int i = 0; i &lt; size(); i++) {
	 *  		invokingStateHashCode = invokingStateHashCode * {@link #HASH_MULTIPLIER HASH_MULTIPLIER} ^ getInvokingState(i);
	 *  	}
	 *
	 *  	int parentHashCode = INITIAL_HASH;
	 *  	for (int i = 0; i &lt; size(); i++) {
	 *  		parentHashCode = parentHashCode * HASH_MULTIPLIER ^ getParent(i).hashCode();
	 *  	}
	 *
	 *  	int hashCode = INITIAL_HASH;
	 *  	hashCode = hashCode * HASH_MULTIPLIER ^ parentHashCode;
	 *  	hashCode = hashCode * HASH_MULTIPLIER ^ invokingStateHashCode;
	 *  	return hashCode;
	 *  }
	 * </pre>
	 */
	private final int cachedHashCode;

	protected PredictionContext(int cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

	protected static int calculateEmptyParentHashCode() {
		return INITIAL_HASH;
	}

	protected static int calculateParentHashCode(PredictionContext parent) {
		return INITIAL_HASH * HASH_MULTIPLIER ^ parent.hashCode();
	}

	protected static int calculateParentHashCode(PredictionContext[] parents) {
		int hashCode = INITIAL_HASH;
		for (PredictionContext context : parents) {
			hashCode = hashCode * HASH_MULTIPLIER ^ context.hashCode();
		}

		return hashCode;
	}

	protected static int calculateEmptyInvokingStateHashCode() {
		return INITIAL_HASH;
	}

	protected static int calculateInvokingStateHashCode(int invokingState) {
		return INITIAL_HASH * HASH_MULTIPLIER ^ invokingState;
	}

	protected static int calculateInvokingStatesHashCode(int[] invokingStates) {
		int hashCode = INITIAL_HASH;
		for (int state : invokingStates) {
			hashCode = hashCode * HASH_MULTIPLIER ^ state;
		}

		return hashCode;
	}

	protected static int calculateHashCode(int parentHashCode, int invokingStateHashCode) {
		return (INITIAL_HASH * HASH_MULTIPLIER ^ parentHashCode) * HASH_MULTIPLIER ^ invokingStateHashCode;
	}

	public abstract int size();

	public abstract int getInvokingState(int index);

	public abstract int findInvokingState(int invokingState);

	@NotNull
	public abstract PredictionContext getParent(int index);

	protected abstract PredictionContext addEmptyContext();

	protected abstract PredictionContext removeEmptyContext();

	public static PredictionContext fromRuleContext(@NotNull RuleContext<?> outerContext) {
		return fromRuleContext(outerContext, true);
	}
	public static PredictionContext fromRuleContext(@NotNull RuleContext<?> outerContext, boolean fullContext) {
		if (outerContext.isEmpty()) {
			return fullContext ? EMPTY_FULL : EMPTY_LOCAL;
		}

		PredictionContext parent;
		if (outerContext.parent != null) {
			parent = PredictionContext.fromRuleContext(outerContext.parent, fullContext);
		} else {
			parent = fullContext ? EMPTY_FULL : EMPTY_LOCAL;
		}

		return parent.getChild(outerContext.invokingState);
	}

	private static PredictionContext addEmptyContext(PredictionContext context) {
		return context.addEmptyContext();
	}

	private static PredictionContext removeEmptyContext(PredictionContext context) {
		return context.removeEmptyContext();
	}

	public static PredictionContext join(PredictionContext context0, PredictionContext context1) {
		return join(context0, context1, PredictionContextCache.UNCACHED);
	}

	/*package*/ static PredictionContext join(@NotNull final PredictionContext context0, @NotNull final PredictionContext context1, @NotNull PredictionContextCache contextCache) {
		if (context0 == context1) {
			return context0;
		}

		if (context0.isEmpty()) {
			return isEmptyLocal(context0) ? context0 : addEmptyContext(context1);
		} else if (context1.isEmpty()) {
			return isEmptyLocal(context1) ? context1 : addEmptyContext(context0);
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

			count++;
		}

		while (leftIndex < context0size) {
			parentsList[count] = context0.getParent(leftIndex);
			invokingStatesList[count] = context0.getInvokingState(leftIndex);
			leftIndex++;
			canReturnRight = false;
			count++;
		}

		while (rightIndex < context1size) {
			parentsList[count] = context1.getParent(rightIndex);
			invokingStatesList[count] = context1.getInvokingState(rightIndex);
			rightIndex++;
			canReturnLeft = false;
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
			// if one of them was EMPTY_LOCAL, it would be empty and handled at the beginning of the method
			return EMPTY_FULL;
		}
		else if (parentsList.length == 1) {
			return new SingletonPredictionContext(parentsList[0], invokingStatesList[0]);
		}
		else {
			return new ArrayPredictionContext(parentsList, invokingStatesList);
		}
	}

	public static boolean isEmptyLocal(PredictionContext context) {
		return context == EMPTY_LOCAL;
	}

	public static PredictionContext getCachedContext(
		@NotNull PredictionContext context,
		@NotNull ConcurrentMap<PredictionContext, PredictionContext> contextCache,
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
			existing = contextCache.putIfAbsent(context, context);
			visited.put(context, existing != null ? existing : context);
			return context;
		}

		// We know parents.length>0 because context.isEmpty() is checked at the beginning of the method.
		PredictionContext updated;
		if (parents.length == 1) {
			updated = new SingletonPredictionContext(parents[0], context.getInvokingState(0));
		}
		else {
			ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
			updated = new ArrayPredictionContext(parents, arrayPredictionContext.invokingStates, context.cachedHashCode);
		}

		existing = contextCache.putIfAbsent(updated, updated);
		visited.put(updated, existing != null ? existing : updated);
		visited.put(context, existing != null ? existing : updated);

		return updated;
	}

	public PredictionContext appendContext(int invokingContext, PredictionContextCache contextCache) {
		return appendContext(PredictionContext.EMPTY_FULL.getChild(invokingContext), contextCache);
	}

	public abstract PredictionContext appendContext(PredictionContext suffix, PredictionContextCache contextCache);

	public PredictionContext getChild(int invokingState) {
		return new SingletonPredictionContext(this, invokingState);
	}

	public abstract boolean isEmpty();

	public abstract boolean hasEmpty();

	@Override
	public final int hashCode() {
		return cachedHashCode;
	}

	@Override
	public abstract boolean equals(Object o);

	//@Override
	//public String toString() {
	//	return toString(null, Integer.MAX_VALUE);
	//}

	public String[] toStrings(Recognizer<?, ?> recognizer, int currentState) {
		return toStrings(recognizer, PredictionContext.EMPTY_FULL, currentState);
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
			while ( !p.isEmpty() && p != stop ) {
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
				else if ( p.getInvokingState(index)!=EMPTY_FULL_STATE_KEY ) {
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
