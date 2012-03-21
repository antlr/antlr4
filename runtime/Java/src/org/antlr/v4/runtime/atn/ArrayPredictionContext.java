package org.antlr.v4.runtime.atn;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayPredictionContext extends PredictionContext {
	// parent can be null only if full ctx mode and we make an array
	// from EMPTY and non-empty. We merge EMPTY by null parent and
	// invokingState = EMPTY_FULL_INVOKING_STATE
	public final PredictionContext[] parents;
	// sorted for merge sort, no duplicates; if present,
	// EMPTY_FULL_INVOKING_STATE is always first
	public final int[] invokingStates;

	public ArrayPredictionContext(SingletonPredictionContext a) {
		this(new PredictionContext[] {a.parent},
			 new int[] {a.invokingState});
	}

	public ArrayPredictionContext(PredictionContext[] parents, int[] invokingStates) {
		super(calculateHashCode(parents, invokingStates));
		assert parents!=null && parents.length>0;
//		System.out.println("CREATE ARRAY: "+Arrays.toString(parents)+", "+Arrays.toString(invokingStates));
		this.parents = parents;
		this.invokingStates = invokingStates;
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
	public boolean isEmpty() {
		return size()==1 &&
			   invokingStates[0]==EmptyPredictionContext.EMPTY_INVOKING_STATE;
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

	/** Find invokingState parameter (call it x) in this.invokingStates,
	 *  if present.  Call pop on all x's parent(s) and then pull other
	 *  elements from this context and merge into new context.
	 */
	@Override
	public PredictionContext popAll(int invokingState, boolean fullCtx) {
		int index = Arrays.binarySearch(this.invokingStates, invokingState);
		if ( index < 0 ) {
			return this;
		}

		PredictionContext newCtx = this.parents[index].popAll(invokingState, fullCtx);
		for (int i = 0; i < this.invokingStates.length; i++) {
			if (i == index) continue;
			PredictionContext next;
			if ( this.invokingStates[i] == EMPTY_FULL_CTX_INVOKING_STATE ) {
				next = PredictionContext.EMPTY;
			}
			else {
				next = new SingletonPredictionContext(this.parents[i],
													  this.invokingStates[i]);
			}
			boolean rootIsWildcard = fullCtx;
			newCtx = merge(newCtx, next, rootIsWildcard);
		}

		return newCtx;
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
		if ( isEmpty() ) return "[]";
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		for (int i=0; i<invokingStates.length; i++) {
			if ( i>0 ) buf.append(",");
			buf.append(invokingStates[i]);
			if ( parents[i]!=null ) {
				buf.append(parents[i].toString());
			}
			else {
				buf.append("null");
			}
		}
		buf.append("]");
		return buf.toString();
	}
}
