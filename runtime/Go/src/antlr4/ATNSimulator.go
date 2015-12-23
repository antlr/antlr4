package antlr4

type ATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
}

func NewATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *ATNSimulator {

	// The context cache maps all PredictionContext objects that are ==
	//  to a single cached copy. This cache is shared across all contexts
	//  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
	//  to use only cached nodes/graphs in addDFAState(). We don't want to
	//  fill this during closure() since there are lots of contexts that
	//  pop up but are not used ever again. It also greatly slows down closure().
	//
	//  <p>This cache makes a huge difference in memory and a little bit in speed.
	//  For the Java grammar on java.*, it dropped the memory requirements
	//  at the end from 25M to 16M. We don't store any of the full context
	//  graphs in the DFA because they are limited to local context only,
	//  but apparently there's a lot of repetition there as well. We optimize
	//  the config contexts before storing the config set in the DFA states
	//  by literally rebuilding them with cached subgraphs only.</p>
	//
	//  <p>I tried a cache for use during closure operations, that was
	//  whacked after each adaptivePredict(). It cost a little bit
	//  more time I think and doesn't save on the overall footprint
	//  so it's not worth the complexity.</p>

	this := new(ATNSimulator)

	this.InitATNSimulator(atn, sharedContextCache)

	return this
}

func (this *ATNSimulator) InitATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) {
	this.atn = atn
	this.sharedContextCache = sharedContextCache
}

// Must distinguish between missing edge and edge we know leads nowhere///
var ATNSimulatorERROR = NewDFAState(0x7FFFFFFF, NewATNConfigSet(false))

func (this *ATNSimulator) getCachedContext(context IPredictionContext) IPredictionContext {
	if this.sharedContextCache == nil {
		return context
	}
	var visited = make(map[IPredictionContext]IPredictionContext)
	return getCachedPredictionContext(context, this.sharedContextCache, visited)
}
