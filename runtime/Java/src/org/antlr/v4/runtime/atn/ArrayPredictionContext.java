/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
