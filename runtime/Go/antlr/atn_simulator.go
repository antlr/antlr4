package antlr

var ATNSimulatorError = NewDFAState(0x7FFFFFFF, NewBaseATNConfigSet(false))

type BaseATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
}

func NewBaseATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *BaseATNSimulator {
	var b = new(BaseATNSimulator)

	b.atn = atn
	b.sharedContextCache = sharedContextCache

	return b
}

func (b *BaseATNSimulator) getCachedContext(context PredictionContext) PredictionContext {
	if b.sharedContextCache == nil {
		return context
	}

	var visited = make(map[PredictionContext]PredictionContext)

	return getCachedBasePredictionContext(context, b.sharedContextCache, visited)
}
