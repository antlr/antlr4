package antlr4

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

func (this *BaseATNSimulator) getCachedContext(context IPredictionContext) IPredictionContext {
	if this.sharedContextCache == nil {
		return context
	}
	var visited = make(map[IPredictionContext]IPredictionContext)
	return getCachedPredictionContext(context, this.sharedContextCache, visited)
}
