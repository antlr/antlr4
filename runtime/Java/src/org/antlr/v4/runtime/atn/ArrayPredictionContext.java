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

import org.antlr.v4.runtime.atn.PredictionContextCache.IdentityCommutativeOperands;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class ArrayPredictionContext extends PredictionContext {

	@NotNull
	public final PredictionContext[] parents;
	@NotNull
	public final int[] invokingStates;

	/*package*/ ArrayPredictionContext(@NotNull PredictionContext[] parents, int[] invokingStates, int parentHashCode, int invokingStateHashCode) {
		super(calculateHashCode(parentHashCode, invokingStateHashCode));
		assert parents.length == invokingStates.length;
		assert invokingStates.length > 1 || invokingStates[0] != EMPTY_FULL_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";

		this.parents = parents;
		this.invokingStates = invokingStates;
	}

	/*package*/ ArrayPredictionContext(@NotNull PredictionContext[] parents, int[] invokingStates, int hashCode) {
		super(hashCode);
		assert parents.length == invokingStates.length;
		assert invokingStates.length > 1 || invokingStates[0] != EMPTY_FULL_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";

		this.parents = parents;
		this.invokingStates = invokingStates;
	}

	@Override
	public PredictionContext getParent(int index) {
		return parents[index];
	}

	@Override
	public int getInvokingState(int index) {
		return invokingStates[index];
	}

	@Override
	public int findInvokingState(int invokingState) {
		return Arrays.binarySearch(invokingStates, invokingState);
	}

	@Override
	public int size() {
		return invokingStates.length;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean hasEmpty() {
		return invokingStates[invokingStates.length - 1] == EMPTY_FULL_STATE_KEY;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	protected PredictionContext addEmptyContext() {
		if (hasEmpty()) {
			return this;
		}

		PredictionContext[] parents2 = Arrays.copyOf(parents, parents.length + 1);
		int[] invokingStates2 = Arrays.copyOf(invokingStates, invokingStates.length + 1);
		parents2[parents2.length - 1] = PredictionContext.EMPTY_FULL;
		invokingStates2[invokingStates2.length - 1] = PredictionContext.EMPTY_FULL_STATE_KEY;
		int newParentHashCode = calculateParentHashCode(parents2);
		int newInvokingStateHashCode = calculateInvokingStatesHashCode(invokingStates2);
		return new ArrayPredictionContext(parents2, invokingStates2, newParentHashCode, newInvokingStateHashCode);
	}

	@Override
	public PredictionContext appendContext(PredictionContext suffix, PredictionContextCache contextCache) {
		return appendContext(this, suffix, new IdentityHashMap<PredictionContext, PredictionContext>());
	}

	private static PredictionContext appendContext(PredictionContext context, PredictionContext suffix, IdentityHashMap<PredictionContext, PredictionContext> visited) {
		if (suffix.isEmpty()) {
			if (isEmptyLocal(suffix)) {
				if (context.hasEmpty()) {
					return EMPTY_LOCAL;
				}
				
				throw new UnsupportedOperationException("what to do here?");
			}

			return context;
		}

		if (suffix.size() != 1) {
			throw new UnsupportedOperationException("Appending a tree suffix is not yet supported.");
		}

		PredictionContext result = visited.get(context);
		if (result == null) {
			if (context.isEmpty()) {
				result = suffix;
			}
			else {
				int parentCount = context.size();
				if (context.hasEmpty()) {
					parentCount--;
				}

				PredictionContext[] updatedParents = new PredictionContext[parentCount];
				int[] updatedInvokingStates = new int[parentCount];
				for (int i = 0; i < parentCount; i++) {
					updatedInvokingStates[i] = context.getInvokingState(i);
				}

				int updatedParentHashCode = 1;
				int updatedInvokingStateHashCode = 1;
				for (int i = 0; i < parentCount; i++) {
					updatedParents[i] = appendContext(context.getParent(i), suffix, visited);
					updatedParentHashCode = 31 * updatedParentHashCode ^ updatedParents[i].hashCode();
					updatedInvokingStateHashCode = 31 * updatedInvokingStateHashCode ^ context.getInvokingState(i);
				}

				if (updatedParents.length == 1) {
					result = new SingletonPredictionContext(updatedParents[0], updatedInvokingStates[0]);
				}
				else {
					assert updatedParents.length > 0;
					result = new ArrayPredictionContext(updatedParents, updatedInvokingStates, updatedParentHashCode, updatedInvokingStateHashCode);
				}

				if (context.hasEmpty()) {
					result = PredictionContext.join(result, suffix);
				}
			}

			visited.put(context, result);
		}

		return result;
	}

	@Override
	public PredictionContext popAll(int invokingState, PredictionContextCache contextCache) {
		int index = Arrays.binarySearch(this.invokingStates, invokingState);
		if (index < 0) {
			return this;
		}

		PredictionContext result = this.parents[index].popAll(invokingState, contextCache);
		for (int i = 0; i < this.invokingStates.length; i++) {
			if (i == index) {
				continue;
			}

			PredictionContext next;
			if (this.invokingStates[i] == EMPTY_FULL_STATE_KEY) {
				next = PredictionContext.EMPTY_FULL;
			}
			else {
				next = contextCache.getChild(this.parents[i], this.invokingStates[i]);
			}

			result = PredictionContext.join(result, next, contextCache);
		}

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if (!(o instanceof ArrayPredictionContext)) {
			return false;
		}

		if ( this.hashCode() != o.hashCode() ) {
			return false; // can't be same if hash is different
		}

		ArrayPredictionContext other = (ArrayPredictionContext)o;
		return equals(other, new HashSet<IdentityCommutativeOperands<PredictionContext>>());
	}

	private boolean equals(ArrayPredictionContext other, Set<IdentityCommutativeOperands<PredictionContext>> visited) {
		Deque<PredictionContext> selfWorkList = new ArrayDeque<PredictionContext>();
		Deque<PredictionContext> otherWorkList = new ArrayDeque<PredictionContext>();
		selfWorkList.push(this);
		otherWorkList.push(other);
		while (!selfWorkList.isEmpty()) {
			IdentityCommutativeOperands<PredictionContext> operands = new IdentityCommutativeOperands<PredictionContext>(selfWorkList.pop(), otherWorkList.pop());
			if (!visited.add(operands)) {
				continue;
			}

			int selfSize = operands.getX().size();
			int otherSize = operands.getY().size();
			if (selfSize != otherSize) {
				return false;
			}

			for (int i = 0; i < selfSize; i++) {
				if (operands.getX().getInvokingState(i) != operands.getY().getInvokingState(i)) {
					return false;
				}

				PredictionContext selfParent = operands.getX().getParent(i);
				PredictionContext otherParent = operands.getY().getParent(i);
				if (selfParent != otherParent) {
					selfWorkList.push(selfParent);
					otherWorkList.push(otherParent);
				}
			}
		}

		return true;
	}

}
