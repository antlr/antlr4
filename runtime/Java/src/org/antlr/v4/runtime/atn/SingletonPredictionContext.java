package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.DoubleKeyMap;

import java.util.Iterator;

public class SingletonPredictionContext extends PredictionContext {
	public final PredictionContext parent;
	public final int returnState;

	SingletonPredictionContext(PredictionContext parent, int returnState) {
		super(calculateHashCode(parent!=null ? 31 ^ parent.hashCode() : 1,
								31 ^ returnState));
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
	public Iterator<SingletonPredictionContext> iterator() {
		final SingletonPredictionContext self = this;
		return new Iterator<SingletonPredictionContext>() {
			int i = 0;
			@Override
			public boolean hasNext() { return i==0; }

			@Override
			public SingletonPredictionContext next() { i++; return self; }

			@Override
			public void remove() { throw new UnsupportedOperationException(); }
		};
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
