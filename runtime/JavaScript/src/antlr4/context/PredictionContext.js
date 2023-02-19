/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export default class PredictionContext {

	constructor(cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

	/**
	 * Stores the computed hash code of this {@link PredictionContext}. The hash
	 * code is computed in parts to match the following reference algorithm.
	 *
	 * <pre>
	 * private int referenceHashCode() {
	 * int hash = {@link MurmurHash//initialize MurmurHash.initialize}({@link
	 * //INITIAL_HASH});
	 *
	 * for (int i = 0; i &lt; {@link //size()}; i++) {
	 * hash = {@link MurmurHash//update MurmurHash.update}(hash, {@link //getParent
	 * getParent}(i));
	 * }
	 *
	 * for (int i = 0; i &lt; {@link //size()}; i++) {
	 * hash = {@link MurmurHash//update MurmurHash.update}(hash, {@link
	 * //getReturnState getReturnState}(i));
	 * }
	 *
	 * hash = {@link MurmurHash//finish MurmurHash.finish}(hash, 2// {@link
	 * //size()});
	 * return hash;
	 * }
	 * </pre>
	 * This means only the {@link //EMPTY} context is in set.
	 */
	isEmpty() {
		return this === PredictionContext.EMPTY;
	}

	hasEmptyPath() {
		return this.getReturnState(this.length - 1) === PredictionContext.EMPTY_RETURN_STATE;
	}

	hashCode() {
		return this.cachedHashCode;
	}

	updateHashCode(hash) {
		hash.update(this.cachedHashCode);
	}
}

/**
 * Represents {@code $} in local context prediction, which means wildcard.
 * {@code//+x =//}.
 */
PredictionContext.EMPTY = null;

/**
 * Represents {@code $} in an array in full context mode, when {@code $}
 * doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
 * {@code $} = {@link //EMPTY_RETURN_STATE}.
 */
PredictionContext.EMPTY_RETURN_STATE = 0x7FFFFFFF;

PredictionContext.globalNodeCount = 1;
PredictionContext.id = PredictionContext.globalNodeCount;
PredictionContext.trace_atn_sim = false;