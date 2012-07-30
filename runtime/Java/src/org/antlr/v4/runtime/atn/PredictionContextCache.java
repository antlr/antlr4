package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Map;

/** Used to cache PredictionContext objects. Its use for both the shared
 *  context cash associated with contacts in DFA states as well as the
 *  transient cash used for adaptivePredict().  This cache can be used for
 *  both lexers and parsers.
 */
public class PredictionContextCache {
	protected String name;
	protected Map<PredictionContext, PredictionContext> cache =
		new HashMap<PredictionContext, PredictionContext>();

	public PredictionContextCache() {
		this.name = name;
	}

	/** Add a context to the cache and return it. If the context already exists,
	 *  return that one instead and do not add a new context to the cache.
	 *  Protect shared cache from unsafe thread access.
	 */
	public synchronized PredictionContext add(PredictionContext ctx) {
		if ( ctx==PredictionContext.EMPTY ) return PredictionContext.EMPTY;
		PredictionContext existing = cache.get(ctx);
		if ( existing!=null ) {
//			System.out.println(name+" reuses "+existing);
			return existing;
		}
		cache.put(ctx, ctx);
		return ctx;
	}

	public synchronized PredictionContext get(PredictionContext ctx) {
		return cache.get(ctx);
	}

	public synchronized int size() {
		return cache.size();
	}
}
