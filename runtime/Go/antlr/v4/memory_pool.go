//go:build !goexperiment.arenas

package antlr

import (
	"runtime"
	"sync"
)

type memoryPool struct {
	sync.Pool
	Gets        int
	Puts        int
	Hits        int
	Misses      int
	Description string
}

type ATNCMemPool memoryPool

var ATNCPool = &ATNCMemPool{
	Pool:        sync.Pool{},
	Description: "ATNConfig memory sync.pool",
}

func FinalizeATC(a *ATNConfig) {
	ATNCPool.Put(a)
	//runtime.KeepAlive(a)
}

func (a *ATNCMemPool) Get() *ATNConfig {
	a.Gets++
	var cv *ATNConfig
	if v := a.Pool.Get(); v != nil {
		a.Hits++
		cv = v.(*ATNConfig)
		
		return cv
	} else {
		cv = &ATNConfig{}
		a.Misses++
	}
	runtime.SetFinalizer(cv, FinalizeATC)
	return cv
}

func (a *ATNCMemPool) Put(v *ATNConfig) {
	a.Puts++
	// Need to initialize the struct to nil values, which will also free up anything they are pointing to.
	//
	v.precedenceFilterSuppressed = false
	v.state = nil
	v.alt = 0
	v.semanticContext = nil
	v.reachesIntoOuterContext = 0
	v.context = nil
	v.cType = 0
	v.lexerActionExecutor = nil
	v.passedThroughNonGreedyDecision = false
	a.Pool.Put(v)
}

func (a *ATNCMemPool) ClearStats() {
	a.Hits = 0
	a.Misses = 0
	a.Puts = 0
	a.Gets = 0
}

func (a *ATNCMemPool) Free() {
	a.Pool = sync.Pool{}
}

type PCMemPool memoryPool

var PCPool = &PCMemPool{
	Pool:        sync.Pool{},
	Description: "PredictionCache memory sync.pool",
}

func FinalizePC(a *PredictionContext) {
	PCPool.Put(a)
	//runtime.KeepAlive(a)
}

func (a *PCMemPool) Get() *PredictionContext {
	a.Gets++
	var cv *PredictionContext
	if v := a.Pool.Get(); v != nil {
		a.Hits++
		cv = v.(*PredictionContext)
		
		return cv
	} else {
		cv = &PredictionContext{}
		a.Misses++
	}
	runtime.SetFinalizer(cv, FinalizePC)
	return cv
}

func (a *PCMemPool) Put(v *PredictionContext) {
	a.Puts++
	// Need to initialize the struct to nil values, which will also free up anything they are pointing to.
	//
	v.cachedHash = 0
	v.parentCtx = nil
	v.parents = nil
	v.returnStates = nil
	v.returnState = 0
	a.Pool.Put(v)
}

func (a *PCMemPool) ClearStats() {
	a.Hits = 0
	a.Misses = 0
	a.Puts = 0
	a.Gets = 0
}

func (a *PCMemPool) Free() {
	a.Pool = sync.Pool{}
}
