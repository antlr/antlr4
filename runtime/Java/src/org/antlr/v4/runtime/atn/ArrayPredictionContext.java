package org.antlr.v4.runtime.atn;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayPredictionContext extends PredictionContext {
	public final PredictionContext[] parents;
	// sorted for merge sort, no duplicates
	public final int[] invokingStates;

	public ArrayPredictionContext(SingletonPredictionContext a) {
		this.parents = new PredictionContext[] {a.parent};
		this.invokingStates = new int[] {a.invokingState};
	}

	public ArrayPredictionContext(PredictionContext[] parents, int[] invokingStates) {
		this.parents = parents;
		this.invokingStates = invokingStates;
	}

	public ArrayPredictionContext(SingletonPredictionContext... nodes) {
		parents = new PredictionContext[nodes.length];
		invokingStates = new int[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			parents[i] = nodes[i].parent;
			invokingStates[i] = nodes[i].invokingState;
		}
	}

	@Override
	public Iterator<SingletonPredictionContext> iterator() {
		return new Iterator<SingletonPredictionContext>() {
			int i = 0;
			@Override
			public boolean hasNext() { return i < parents.length; }

			@Override
			public SingletonPredictionContext next() {
				SingletonPredictionContext ctx =
					new SingletonPredictionContext(parents[i], invokingStates[i]);
				i++;
				return ctx;
			}

			@Override
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}

	@Override
	public int size() {
		return invokingStates.length;
	}

	@Override
	public PredictionContext getParent(int index) {
		return parents[index];
	}

	@Override
	public int getInvokingState(int index) {
		return invokingStates[index];
	}

	@Override
	public int findInvokingState(int invokingState) {
		return Arrays.binarySearch(invokingStates, invokingState);
	}

	public ArrayPredictionContext trim() {
		int i = parents.length-1;
		while ( i>=0 && parents[i]==null ) { i--; }
		// i is last non-null index
		if ( i < parents.length-1 ) {
			int n = i+1;
			return new ArrayPredictionContext(
				Arrays.copyOf(parents, n),
				Arrays.copyOf(invokingStates, n)
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
		if ( invokingStates.length != a.invokingStates.length ) {
			return false;
		}

		for (int i=0; i< invokingStates.length; i++) {
			if ( invokingStates[i]!=a.invokingStates[i] ) return false;
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
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		for (int i=0; i< invokingStates.length; i++) {
			buf.append(invokingStates[i]);
			buf.append(parents[i].toString());
		}
		buf.append("[");
		return buf.toString();
	}
}
