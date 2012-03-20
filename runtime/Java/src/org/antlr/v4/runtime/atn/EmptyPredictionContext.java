package org.antlr.v4.runtime.atn;

public class EmptyPredictionContext extends SingletonPredictionContext {
	public EmptyPredictionContext() {
		super(null,-1);
	}

	public boolean isEmpty() { return true; }

	@Override
	public int findInvokingState(int invokingState) {
		return 1;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public PredictionContext getParent(int index) {
		return null;
	}

	@Override
	public int getInvokingState(int index) {
		return invokingState;
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public String toString() {
		return "$";
	}
}
