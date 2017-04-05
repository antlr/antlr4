/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
