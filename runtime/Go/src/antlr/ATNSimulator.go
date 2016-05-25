package antlr

type BaseATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
}

func NewBaseATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *BaseATNSimulator {

	this := new(BaseATNSimulator)

	this.atn = atn
	this.sharedContextCache = sharedContextCache

	return this
}

var ATNSimulatorError = NewDFAState(0x7FFFFFFF, NewBaseATNConfigSet(false))

func (this *BaseATNSimulator) getCachedContext(context PredictionContext) PredictionContext {
	if this.sharedContextCache == nil {
		return context
	}
	var visited = make(map[PredictionContext]PredictionContext)
	return getCachedBasePredictionContext(context, this.sharedContextCache, visited)
}
