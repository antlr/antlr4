package org.antlr.v4.runtime.atn;

import java.util.Arrays;

public class ArrayPredictionContext extends PredictionContext {
	public final PredictionContext[] parents;
	// sorted for merge sort, no duplicates
	public final String[] payloads;

	public ArrayPredictionContext(SingletonPredictionContext a) {
		this.parents = new PredictionContext[] {a.parent};
		this.payloads = new String[] {a.payload};
	}

	public ArrayPredictionContext(PredictionContext[] parents, String[] payloads) {
		this.parents = parents;
		this.payloads = payloads;
	}

	public ArrayPredictionContext(SingletonPredictionContext... nodes) {
		parents = new PredictionContext[nodes.length];
		payloads = new String[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			parents[i] = nodes[i].parent;
			payloads[i] = nodes[i].payload;
		}
	}

	@Override
	public int size() {
		return payloads.length;
	}

	@Override
	public PredictionContext getParent(int index) {
		return parents[index];
	}

	@Override
	public String getPayload(int index) {
		return payloads[index];
	}

	@Override
	public int findPayload(String payload) {
		return Arrays.binarySearch(payloads, payload);
	}

	public ArrayPredictionContext trim() {
		int i = parents.length-1;
		while ( i>=0 && parents[i]==null ) { i--; }
		// i is last non-null index
		if ( i < parents.length-1 ) {
			int n = i+1;
			return new ArrayPredictionContext(
				Arrays.copyOf(parents, n),
				Arrays.copyOf(payloads, n)
			);
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if ( !(o instanceof ArrayPredictionContext) ) {
			return false;
		}

		if ( this.hashCode() != o.hashCode() ) {
			return false; // can't be same if hash is different
		}

		ArrayPredictionContext a = (ArrayPredictionContext)o;
		if ( payloads.length != a.payloads.length ) {
			return false;
		}

		for (int i=0; i<payloads.length; i++) {
			if ( !payloads[i].equals(a.payloads[i]) ) return false;
			if ( !parents[i].equals(a.parents[i]) ) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return Arrays.toString(payloads)+":"+id;
	}
}
