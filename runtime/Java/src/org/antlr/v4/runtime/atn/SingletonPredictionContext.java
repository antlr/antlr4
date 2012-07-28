package org.antlr.v4.runtime.atn;

import java.util.Iterator;

public class SingletonPredictionContext extends PredictionContext {
	public final PredictionContext parent;
	public final int invokingState;

	public SingletonPredictionContext(PredictionContext parent, int invokingState) {
		super(calculateHashCode(parent!=null ? 31 ^ parent.hashCode() : 1,
								31 ^ invokingState));
		assert invokingState!=EMPTY_FULL_CTX_INVOKING_STATE &&
		       invokingState!=ATNState.INVALID_STATE_NUMBER;
		this.parent = parent;
		this.invokingState = invokingState;
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
	public int getInvokingState(int index) {
		assert index == 0;
		return invokingState;
	}

	@Override
	public PredictionContext popAll(int invokingState,
									boolean fullCtx)
	{
		if ( invokingState == this.invokingState ) {
			return parent.popAll(invokingState, fullCtx);
		}
		return this;
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
		return invokingState == s.invokingState && parent.equals(s.parent);
	}

	@Override
	public String toString() {
		String up = parent!=null ? parent.toString() : "";
		if ( up.length()==0 ) return String.valueOf(invokingState);
		return String.valueOf(invokingState)+" "+up;
	}
}
