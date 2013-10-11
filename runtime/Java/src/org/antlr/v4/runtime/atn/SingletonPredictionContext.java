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

public class SingletonPredictionContext extends PredictionContext {
	public final PredictionContext parent;
	public final int returnState;

	SingletonPredictionContext(PredictionContext parent, int returnState) {
		super(parent != null ? calculateHashCode(parent, returnState) : calculateEmptyHashCode());
		assert returnState!=ATNState.INVALID_STATE_NUMBER;
		this.parent = parent;
		this.returnState = returnState;
	}

	public static SingletonPredictionContext create(PredictionContext parent, int returnState) {
		if ( returnState == EMPTY_RETURN_STATE && parent == null ) {
			// someone can pass in the bits of an array ctx that mean $
			return EMPTY;
		}
		return new SingletonPredictionContext(parent, returnState);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public PredictionContext getParent(int index) {
		assert index == 0;
		return parent;
	}

	@Override
	public int getReturnState(int index) {
		assert index == 0;
		return returnState;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if ( !(o instanceof SingletonPredictionContext) ) {
			return false;
		}

		if ( this.hashCode() != o.hashCode() ) {
			return false; // can't be same if hash is different
		}

		SingletonPredictionContext s = (SingletonPredictionContext)o;
		return returnState == s.returnState &&
			(parent!=null && parent.equals(s.parent));
	}

	@Override
	public String toString() {
		String up = parent!=null ? parent.toString() : "";
		if ( up.length()==0 ) {
			if ( returnState == EMPTY_RETURN_STATE ) {
				return "$";
			}
			return String.valueOf(returnState);
		}
		return String.valueOf(returnState)+" "+up;
	}
}
