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

import org.antlr.v4.runtime.misc.NotNull;

public class SingletonPredictionContext extends PredictionContext {

	@NotNull
	public final PredictionContext parent;
	public final int invokingState;

	/*package*/ SingletonPredictionContext(@NotNull PredictionContext parent, int invokingState) {
		super(calculateHashCode(calculateParentHashCode(parent), calculateInvokingStateHashCode(invokingState)));
		assert invokingState != EMPTY_FULL_STATE_KEY && invokingState != EMPTY_LOCAL_STATE_KEY;
		this.parent = parent;
		this.invokingState = invokingState;
	}

	@Override
	public PredictionContext getParent(int index) {
		assert index == 0;
		return parent;
	}

	@Override
	public int getInvokingState(int index) {
		assert index == 0;
		return invokingState;
	}

	@Override
	public int findInvokingState(int invokingState) {
		return this.invokingState == invokingState ? 0 : -1;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public PredictionContext popAll(int invokingState, PredictionContextCache contextCache) {
		if (this.invokingState == invokingState) {
			return this.parent.popAll(invokingState, contextCache);
		}

		return this;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean hasEmpty() {
		return false;
	}

	@Override
	public PredictionContext appendContext(PredictionContext suffix, PredictionContextCache contextCache) {
		return contextCache.getChild(parent.appendContext(suffix, contextCache), invokingState);
	}

	@Override
	protected PredictionContext addEmptyContext() {
		PredictionContext[] parents = new PredictionContext[] { parent, EMPTY_FULL };
		int[] invokingStates = new int[] { invokingState, EMPTY_FULL_STATE_KEY };
		return new ArrayPredictionContext(parents, invokingStates);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		else if (!(o instanceof SingletonPredictionContext)) {
			return false;
		}

		SingletonPredictionContext other = (SingletonPredictionContext)o;
		if (this.hashCode() != other.hashCode()) {
			return false;
		}

		return invokingState == other.invokingState
			&& parent.equals(other.parent);
	}

}
