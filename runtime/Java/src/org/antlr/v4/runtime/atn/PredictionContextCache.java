/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Map;

/** Used to cache {@link PredictionContext} objects. Its used for the shared
 *  context cash associated with contexts in DFA states. This cache
 *  can be used for both lexers and parsers.
 */
public class PredictionContextCache {
	protected final Map<PredictionContext, PredictionContext> cache =
		new HashMap<PredictionContext, PredictionContext>();

	/** Add a context to the cache and return it. If the context already exists,
	 *  return that one instead and do not add a new context to the cache.
	 *  Protect shared cache from unsafe thread access.
	 */
	public PredictionContext add(PredictionContext ctx) {
		if ( ctx==EmptyPredictionContext.Instance ) return EmptyPredictionContext.Instance;
		PredictionContext existing = cache.get(ctx);
		if ( existing!=null ) {
//			System.out.println(name+" reuses "+existing);
			return existing;
		}
		cache.put(ctx, ctx);
		return ctx;
	}

	public PredictionContext get(PredictionContext ctx) {
		return cache.get(ctx);
	}

	public int size() {
		return cache.size();
	}
}
