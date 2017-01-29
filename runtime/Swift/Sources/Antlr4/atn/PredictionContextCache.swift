/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Used to cache {@link org.antlr.v4.runtime.atn.PredictionContext} objects. Its used for the shared
/// context cash associated with contexts in DFA states. This cache
/// can be used for both lexers and parsers.

public final class PredictionContextCache {
    //internal final var
    var cache: HashMap<PredictionContext, PredictionContext> =
    HashMap<PredictionContext, PredictionContext>()
    public init() {
    }
    /// Add a context to the cache and return it. If the context already exists,
    /// return that one instead and do not add a new context to the cache.
    /// Protect shared cache from unsafe thread access.
    @discardableResult
    public func add(_ ctx: PredictionContext) -> PredictionContext {
        if ctx === PredictionContext.EMPTY {
            return PredictionContext.EMPTY
        }
        let existing: PredictionContext? = cache[ctx]
        if existing != nil {
//			print(name+" reuses "+existing);
            return existing!
        }
        cache[ctx] = ctx
        return ctx
    }

    public func get(_ ctx: PredictionContext) -> PredictionContext? {
        return cache[ctx]
    }

    public func size() -> Int {
        return cache.count
    }
}
