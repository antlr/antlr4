/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.PredictionContextCache;

import std.conv;
import antlr.v4.runtime.atn.PredictionContext;

/**
 * Used to cache {@link PredictionContext} objects.
 *
 * Its used for the shared
 * context objects associated with contexts in DFA states. This cache
 * can be used for both lexers and parsers.
 */
class PredictionContextCache
{

    protected PredictionContext[PredictionContext] cache;

    /**
     * Add a context to the cache and return it.
     *
     * If the context already exists,
     * return that one instead and do not add a new context to the cache.
     * Protect shared cache from unsafe thread access.
     */
    public PredictionContext add(PredictionContext predictionContext)
    {
	if (predictionContext == PredictionContext.EMPTY)
            return predictionContext;
        if (hasKey(predictionContext)) {
            // System.out.println(name+" reuses "+existing);
            return cache[predictionContext];
        }
        cache[predictionContext] = predictionContext;
        return predictionContext;
    }

    /**
     * Get the predictionContext from cache.
     */
    public PredictionContext get(PredictionContext predictionContext)
    {
	return cache[predictionContext];
    }

    /**
     * Contains the cache the predictionContext?
     */
    public bool hasKey(PredictionContext predictionContext)
    {
        return (predictionContext in cache) !is null;
    }

    /**
     * Size of current cache.
     */
    public size_t size()
    {
        return cache.length;
    }

}
