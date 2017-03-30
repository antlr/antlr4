/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import java.util.Arrays;

public class ArrayPredictionContext extends PredictionContext {
	/** Parent can be null only if full ctx mode and we make an array
	 *  from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
	 *  returnState == {@link #EMPTY_RETURN_STATE}.
	 */
	public final PredictionContext[] parents;

	/** Sorted for merge, no duplicates; if present,
	 *  {@link #EMPTY_RETURN_STATE} is always last.
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

	@Override
	public boolean isEmpty() {
		// since EMPTY_RETURN_STATE can only appear in the last position, we
		// don't need to verify that size==1
		return returnStates[0]==EMPTY_RETURN_STATE;
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
