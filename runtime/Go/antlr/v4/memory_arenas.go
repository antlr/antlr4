//go:build goexperiment.arenas

package antlr

import (
	"arena"
)

type memoryPool struct {
	arena       *arena.Arena
	Gets        int
	Puts        int
	Hits        int
	Misses      int
	Description string
}
type ATNCMemPool memoryPool

var ATNCPool = &ATNCMemPool{
	arena:       arena.NewArena(),
	Description: "ATNConfig memory arena",
}

func (a *ATNCMemPool) Get() *ATNConfig {
	a.Gets++
	var cv *ATNConfig
	cv = arena.New[ATNConfig](a.arena)
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
}

func (a *ATNCMemPool) ClearStats() {
	a.Hits = 0
	a.Misses = 0
	a.Puts = 0
	a.Gets = 0
}

func (a *ATNCMemPool) Free() {
	a.arena.Free()
	a.arena = arena.NewArena()
}

type PCMemPool memoryPool

var PCPool = &PCMemPool{
	arena:       arena.NewArena(),
	Description: "PredictionCache memory arena",
}

func (a *PCMemPool) Get() *PredictionContext {
	a.Gets++
	var cv *PredictionContext
	cv = arena.New[PredictionContext](a.arena)
	return cv
}

func FinalizePC(a *PredictionContext) {
}

func (a *PCMemPool) Put(v *PredictionContext) {
	a.Puts++
	// Need to initialize the struct to nil values, which will also free up anything they are pointing to.
	//
}

func (a *PCMemPool) ClearStats() {
	a.Hits = 0
	a.Misses = 0
	a.Puts = 0
	a.Gets = 0
}

func (a *PCMemPool) Free() {
	a.arena.Free()
	a.arena = arena.NewArena()
}
