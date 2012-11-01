package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.DoubleKeyMap;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayPredictionContext extends PredictionContext {
	/** Parent can be null only if full ctx mode and we make an array
	 *  from EMPTY and non-empty. We merge EMPTY by using null parent and
	 *  returnState == EMPTY_FULL_RETURN_STATE
	 */
	public final PredictionContext[] parents;

	/** Sorted for merge, no duplicates; if present,
	 *  EMPTY_FULL_RETURN_STATE is always first
 	 */
	public final int[] returnStates;

	public ArrayPredictionContext(SingletonPredictionContext a) {
		this(new PredictionContext[] {a.parent}, new int[] {a.returnState});
	}

	public ArrayPredictionContext(PredictionContext[] parents, int[] returnStates) {
		super(calculateHashCode(parents, returnStates));
		assert parents!=null && parents.length>0;
		assert returnStates!=null && returnStates.length>0;
//		System.err.println("CREATE ARRAY: "+Arrays.toString(parents)+", "+Arrays.toString(returnStates));
		this.parents = parents;
		this.returnStates = returnStates;
	}

//ArrayPredictionContext(@NotNull PredictionContext[] parents, int[] returnStates, int parentHashCode, int returnStateHashCode) {
//		super(calculateHashCode(parentHashCode, returnStateHashCode));
//		assert parents.length == returnStates.length;
//		assert returnStates.length > 1 || returnStates[0] != EMPTY_FULL_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";
//
//		this.parents = parents;
//		this.returnStates = returnStates;
//	}
//
//ArrayPredictionContext(@NotNull PredictionContext[] parents, int[] returnStates, int hashCode) {
//		super(hashCode);
//		assert parents.length == returnStates.length;
//		assert returnStates.length > 1 || returnStates[0] != EMPTY_FULL_STATE_KEY : "Should be using PredictionContext.EMPTY instead.";
//
//		this.parents = parents;
//		this.returnStates = returnStates;
//	}

	protected static int calculateHashCode(PredictionContext[] parents, int[] returnStates) {
		return calculateHashCode(calculateParentHashCode(parents),
								 calculateReturnStatesHashCode(returnStates));
	}

	protected static int calculateParentHashCode(PredictionContext[] parents) {
		int hashCode = 1;
		for (PredictionContext p : parents) {
			if ( p!=null ) { // can be null for full ctx stack in ArrayPredictionContext
				hashCode = hashCode * 31 ^ p.hashCode();
			}
		}

		return hashCode;
	}

	protected static int calculateReturnStatesHashCode(int[] returnStates) {
		int hashCode = 1;
		for (int state : returnStates) {
			hashCode = hashCode * 31 ^ state;
		}

		return hashCode;
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
					SingletonPredictionContext.create(parents[i], returnStates[i]);
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
			   returnStates[0]==EMPTY_RETURN_STATE;
	}

	@Override
	public int size() {
		return returnStates.length;
	}

	@Override
	public PredictionContext getParent(int index) {
		return parents[index];
	}

	@Override
	public int getReturnState(int index) {
		return returnStates[index];
	}

//	@Override
//	public int findReturnState(int returnState) {
//		return Arrays.binarySearch(returnStates, returnState);
//	}

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
		return Arrays.equals(returnStates, a.returnStates) &&
		       Arrays.equals(parents, a.parents);
	}

	@Override
	public String toString() {
		if ( isEmpty() ) return "[]";
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		for (int i=0; i<returnStates.length; i++) {
			if ( i>0 ) buf.append(", ");
			if ( returnStates[i]==EMPTY_RETURN_STATE ) {
				buf.append("$");
				continue;
			}
			buf.append(returnStates[i]);
			if ( parents[i]!=null ) {
				buf.append(' ');
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
