package antlr

var BasePredictionContextEMPTY = NewEmptyPredictionContext()

// PredictionContextCache is Used to cache [PredictionContext] objects. It is used for the shared
// context cash associated with contexts in DFA states. This cache
// can be used for both lexers and parsers.
type PredictionContextCache struct {
	cache map[PredictionContext]PredictionContext
}

func NewPredictionContextCache() *PredictionContextCache {
	t := new(PredictionContextCache)
	t.cache = make(map[PredictionContext]PredictionContext)
	return t
}

// Add a context to the cache and return it. If the context already exists,
// return that one instead and do not add a new context to the cache.
// Protect shared cache from unsafe thread access.
func (p *PredictionContextCache) add(ctx PredictionContext) PredictionContext {
	if ctx.isEmpty() {
		return BasePredictionContextEMPTY
	}
	existing := p.cache[ctx]
	if existing != nil {
		return existing
	}
	p.cache[ctx] = ctx
	return ctx
}

func (p *PredictionContextCache) Get(ctx PredictionContext) PredictionContext {
	return p.cache[ctx]
}

func (p *PredictionContextCache) length() int {
	return len(p.cache)
}
