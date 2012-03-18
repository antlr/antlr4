package org.antlr.v4.runtime.atn;

public class SingletonPredictionContext extends PredictionContext {
	public final PredictionContext parent;
	public final String payload;

	public SingletonPredictionContext(PredictionContext parent, String payload) {
		this.parent = parent;
		this.payload = payload;
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
	public String getPayload(int index) {
		assert index == 0;
		return payload;
	}

	@Override
	public int findPayload(String payload) {
		return this.payload.equals(payload) ? 0 : -1;
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
		return payload.equals(s.payload) && parent.equals(s.parent);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return payload+":"+id;
	}
}
