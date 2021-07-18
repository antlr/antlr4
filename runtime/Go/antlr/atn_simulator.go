// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// ATNSimulatorError TODO: docs.
var ATNSimulatorError = NewDFAState(0x7FFFFFFF, NewBaseATNConfigSet(false))

// IATNSimulator TODO: docs.
type IATNSimulator interface {
	SharedContextCache() *PredictionContextCache
	ATN() *ATN
	DecisionToDFA() []*DFA
}

// BaseATNSimulator TODO: docs
type BaseATNSimulator struct {
	atn                *ATN
	sharedContextCache *PredictionContextCache
	decisionToDFA      []*DFA
}

// NewBaseATNSimulator TODO: docs.
func NewBaseATNSimulator(atn *ATN, sharedContextCache *PredictionContextCache) *BaseATNSimulator {
	return &BaseATNSimulator{
		atn:                atn,
		sharedContextCache: sharedContextCache,
	}
}

func (b *BaseATNSimulator) getCachedContext(context PredictionContext) PredictionContext {
	if b.sharedContextCache == nil {
		return context
	}

	visited := make(map[PredictionContext]PredictionContext)

	return getCachedBasePredictionContext(context, b.sharedContextCache, visited)
}

// SharedContextCache TODO: docs.
func (b *BaseATNSimulator) SharedContextCache() *PredictionContextCache {
	return b.sharedContextCache
}

// ATN TODO: docs.
func (b *BaseATNSimulator) ATN() *ATN {
	return b.atn
}

// DecisionToDFA TODO: docs.
func (b *BaseATNSimulator) DecisionToDFA() []*DFA {
	return b.decisionToDFA
}
