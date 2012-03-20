package org.antlr.v4.runtime.atn;

import java.util.Iterator;

public class SingletonPredictionContext extends PredictionContext {
	public final PredictionContext parent;
	public final int invokingState;

	public SingletonPredictionContext(PredictionContext parent, int invokingState) {
		super(calculateHashCode(parent!=null?31^parent.hashCode():0, 31 ^ invokingState));
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
	public int findInvokingState(int invokingState) {
		return this.invokingState == invokingState ? 0 : -1;
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
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return String.valueOf(invokingState)+" "+parent.toString();
	}
}
