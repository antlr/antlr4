package org.antlr.v4.runtime.atn;

public class EmptyPredictionContext extends SingletonPredictionContext {
	public static final int EMPTY_INVOKING_STATE = ATNState.INVALID_STATE_NUMBER;
	public EmptyPredictionContext() {
		super(null, EMPTY_INVOKING_STATE);
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
