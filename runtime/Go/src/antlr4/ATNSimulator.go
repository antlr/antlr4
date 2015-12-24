package antlr4

type ATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
}

func NewATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *ATNSimulator {

	this := new(ATNSimulator)

	this.atn = atn
	this.sharedContextCache = sharedContextCache

	return this
}

var ATNSimulatorERROR = NewDFAState(0x7FFFFFFF, NewATNConfigSet(false))

func (this *ATNSimulator) getCachedContext(context IPredictionContext) IPredictionContext {
	if this.sharedContextCache == nil {
		return context
	}
	var visited = make(map[IPredictionContext]IPredictionContext)
	return getCachedPredictionContext(context, this.sharedContextCache, visited)
}
