package antlr

var ATNSimulatorError = NewDFAState(0x7FFFFFFF, NewBaseATNConfigSet(false))

type IATNSimulator interface {
	SharedContextCache() *PredictionContextCache
	ATN() *ATN
	DecisionToDFA() []*DFA
}

type BaseATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
	decisionToDFA  []*DFA
}

func NewBaseATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *BaseATNSimulator {
	b := new(BaseATNSimulator)

	b.atn = atn
	b.sharedContextCache = sharedContextCache

	return b
}

func (b *BaseATNSimulator) getCachedContext(context PredictionContext) PredictionContext {
	if b.sharedContextCache == nil {
		return context
	}

	visited := make(map[PredictionContext]PredictionContext)

	return getCachedBasePredictionContext(context, b.sharedContextCache, visited)
}

func (b *BaseATNSimulator) SharedContextCache() *PredictionContextCache {
	return b.sharedContextCache
}

func (b *BaseATNSimulator) ATN() *ATN {
	return b.atn
}

func (b *BaseATNSimulator) DecisionToDFA() []*DFA {
	return b.decisionToDFA
}