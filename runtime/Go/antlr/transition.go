// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
	"strconv"
)

// atom, set, epsilon, action, predicate, rule transitions.
//

// Transition is a one way link.  It emanates from a state (usually via a list
// of transitions) and has a target state.
//
// Since we never have to change the ATN transitions once we construct it,
// the states. We'll use the term Edge for the DFA to distinguish them from
// ATN transitions.
type Transition interface {
	getTarget() ATNState
	setTarget(ATNState)
	getIsEpsilon() bool
	getLabel() *IntervalSet
	getSerializationType() int
	Matches(int, int, int) bool
}

// BaseTransition is the base implementation for Transition.
type BaseTransition struct {
	target ATNState
	// Are we epsilon, action, sempred?
	isEpsilon         bool
	label             int
	intervalSet       *IntervalSet
	serializationType int
}

// NewBaseTransition returns a new instance of BaseTransition.
func NewBaseTransition(target ATNState) *BaseTransition {
	if target == nil {
		panic("target cannot be nil.")
	}

	return &BaseTransition{
		target:      target,
		isEpsilon:   false,
		intervalSet: nil,
	}
}

func (t *BaseTransition) getTarget() ATNState {
	return t.target
}

func (t *BaseTransition) setTarget(s ATNState) {
	t.target = s
}

func (t *BaseTransition) getIsEpsilon() bool {
	return t.isEpsilon
}

func (t *BaseTransition) getLabel() *IntervalSet {
	return t.intervalSet
}

func (t *BaseTransition) getSerializationType() int {
	return t.serializationType
}

// Matches is not implemented.
func (t *BaseTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	panic("Not implemented")
}

// Transition types
const (
	TransitionEPSILON    = 1
	TransitionRANGE      = 2
	TransitionRULE       = 3
	TransitionPREDICATE  = 4 // e.g., {isType(input.LT(1))}?
	TransitionATOM       = 5
	TransitionACTION     = 6
	TransitionSET        = 7 // ~(A|B) or ~atom, wildcard, which convert to next 2
	TransitionNOTSET     = 8
	TransitionWILDCARD   = 9
	TransitionPRECEDENCE = 10
)

// TransitionserializationNames are the names of the constants defined above.
var TransitionserializationNames = []string{
	"INVALID",
	"EPSILON",
	"RANGE",
	"RULE",
	"PREDICATE",
	"ATOM",
	"ACTION",
	"SET",
	"NOT_SET",
	"WILDCARD",
	"PRECEDENCE",
}

// AtomTransition TODO: make all transitions sets? no, should remove set edges
type AtomTransition struct {
	*BaseTransition
}

// NewAtomTransition returns a new instance of AtomTransition.
func NewAtomTransition(target ATNState, intervalSet int) *AtomTransition {
	t := &AtomTransition{
		BaseTransition: NewBaseTransition(target),
	}

	t.label = intervalSet // The token type or character value or, signifies special intervalSet.
	t.intervalSet = t.makeLabel()
	t.serializationType = TransitionATOM

	return t
}

func (t *AtomTransition) makeLabel() *IntervalSet {
	s := NewIntervalSet()
	s.addOne(t.label)
	return s
}

// Matches returns true if the symbol matches this transition's label.
func (t *AtomTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return t.label == symbol
}

func (t *AtomTransition) String() string {
	return strconv.Itoa(t.label)
}

// RuleTransition is a transition between two ATNStates.
type RuleTransition struct {
	*BaseTransition

	followState           ATNState
	ruleIndex, precedence int
}

// NewRuleTransition returns a new instance of RuleTransition
func NewRuleTransition(ruleStart ATNState, ruleIndex, precedence int, followState ATNState) *RuleTransition {
	t := &RuleTransition{
		BaseTransition: NewBaseTransition(ruleStart),
		followState:    followState,
		ruleIndex:      ruleIndex,
		precedence:     precedence,
	}

	t.serializationType = TransitionRULE
	t.isEpsilon = true

	return t
}

// Matches always returns false.
func (t *RuleTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

// EpsilonTransition represents a transition to an epsilon state.
type EpsilonTransition struct {
	*BaseTransition

	outermostPrecedenceReturn int
}

// NewEpsilonTransition returns a new instance of EpsilonTransition
func NewEpsilonTransition(target ATNState, outermostPrecedenceReturn int) *EpsilonTransition {
	t := &EpsilonTransition{
		BaseTransition:            NewBaseTransition(target),
		outermostPrecedenceReturn: outermostPrecedenceReturn,
	}

	t.serializationType = TransitionEPSILON
	t.isEpsilon = true

	return t
}

// Matches always returns false.
func (t *EpsilonTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *EpsilonTransition) String() string {
	return "epsilon"
}

// RangeTransition represents a transition range.
type RangeTransition struct {
	*BaseTransition

	start, stop int
}

// NewRangeTransition returns a new instance of RangeTransition.
func NewRangeTransition(target ATNState, start, stop int) *RangeTransition {

	t := &RangeTransition{
		BaseTransition: NewBaseTransition(target),
		start:          start,
		stop:           stop,
	}

	t.serializationType = TransitionRANGE
	t.intervalSet = t.makeLabel()
	return t
}

func (t *RangeTransition) makeLabel() *IntervalSet {
	s := NewIntervalSet()
	s.addRange(t.start, t.stop)
	return s
}

// Matches returns true if the given symbol is whithin the range this object
// represents.
func (t *RangeTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= t.start && symbol <= t.stop
}

func (t *RangeTransition) String() string {
	return fmt.Sprintf("'%d..%d'", t.start, t.stop)
}

// AbstractPredicateTransition TODO: docs
type AbstractPredicateTransition interface {
	Transition
	IAbstractPredicateTransitionFoo()
}

// BaseAbstractPredicateTransition TODO: docs
type BaseAbstractPredicateTransition struct {
	*BaseTransition
}

// NewBasePredicateTransition returns a new instance of BasePredicateTransition
func NewBasePredicateTransition(target ATNState) *BaseAbstractPredicateTransition {
	return &BaseAbstractPredicateTransition{
		BaseTransition: NewBaseTransition(target),
	}
}

// IAbstractPredicateTransitionFoo does nothing.
func (a *BaseAbstractPredicateTransition) IAbstractPredicateTransitionFoo() {}

// PredicateTransition TODO: docs
type PredicateTransition struct {
	*BaseAbstractPredicateTransition

	isCtxDependent       bool
	ruleIndex, predIndex int
}

// NewPredicateTransition returns a new instance of PredicateTransition.
func NewPredicateTransition(target ATNState, ruleIndex, predIndex int, isCtxDependent bool) *PredicateTransition {

	t := &PredicateTransition{
		BaseAbstractPredicateTransition: NewBasePredicateTransition(target),
		ruleIndex:                       ruleIndex,
		predIndex:                       predIndex,
		isCtxDependent:                  isCtxDependent,
	}

	t.serializationType = TransitionPREDICATE
	t.isEpsilon = true
	return t
}

// Matches returns false.
func (t *PredicateTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *PredicateTransition) getPredicate() *Predicate {
	return NewPredicate(t.ruleIndex, t.predIndex, t.isCtxDependent)
}

func (t *PredicateTransition) String() string {
	return "pred_" + strconv.Itoa(t.ruleIndex) + ":" + strconv.Itoa(t.predIndex)
}

// ActionTransition TODO: docs
type ActionTransition struct {
	*BaseTransition

	isCtxDependent                    bool
	ruleIndex, actionIndex, predIndex int
}

// NewActionTransition returns a new instance of ActionTransition.
func NewActionTransition(target ATNState, ruleIndex, actionIndex int, isCtxDependent bool) *ActionTransition {
	t := &ActionTransition{
		BaseTransition: NewBaseTransition(target),
		ruleIndex:      ruleIndex,
		actionIndex:    actionIndex,
		isCtxDependent: isCtxDependent,
	}

	t.serializationType = TransitionACTION
	t.isEpsilon = true
	return t
}

// Matches returns false.
func (t *ActionTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *ActionTransition) String() string {
	return "action_" + strconv.Itoa(t.ruleIndex) + ":" + strconv.Itoa(t.actionIndex)
}

// SetTransition TODO: docs.
type SetTransition struct {
	*BaseTransition
}

// NewSetTransition returns a new instance of SetTransition.
func NewSetTransition(target ATNState, s *IntervalSet) *SetTransition {
	t := &SetTransition{
		BaseTransition: NewBaseTransition(target),
	}

	t.serializationType = TransitionSET
	if s != nil {
		t.intervalSet = s
	} else {
		t.intervalSet = NewIntervalSet()
		t.intervalSet.addOne(TokenInvalidType)
	}

	return t
}

// Matches if symbol is contained in this interval.
func (t *SetTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return t.intervalSet.contains(symbol)
}

func (t *SetTransition) String() string {
	return t.intervalSet.String()
}

// NotSetTransition TODO: docs.
type NotSetTransition struct {
	*SetTransition
}

// NewNotSetTransition returns a new instance of NotSetTransition.
func NewNotSetTransition(target ATNState, s *IntervalSet) *NotSetTransition {
	t := &NotSetTransition{
		SetTransition: NewSetTransition(target, s),
	}

	t.serializationType = TransitionNOTSET

	return t
}

// Matches if the given symbol is not in this range.
func (t *NotSetTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !t.intervalSet.contains(symbol)
}

func (t *NotSetTransition) String() string {
	return "~" + t.intervalSet.String()
}

// WildcardTransition TODO: docs
type WildcardTransition struct {
	*BaseTransition
}

// NewWildcardTransition returns a new instance of WildcardTransition.
func NewWildcardTransition(target ATNState) *WildcardTransition {
	t := &WildcardTransition{
		BaseTransition: NewBaseTransition(target),
	}

	t.serializationType = TransitionWILDCARD
	return t
}

// Matches returns true if the symbol is between the given vocab symbols.
func (t *WildcardTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol
}

func (t *WildcardTransition) String() string {
	return "."
}

// PrecedencePredicateTransition represents a transition to a predicate.
type PrecedencePredicateTransition struct {
	*BaseAbstractPredicateTransition

	precedence int
}

// NewPrecedencePredicateTransition returns a new instance of
// PrecedencePredicateTransition.
func NewPrecedencePredicateTransition(target ATNState, precedence int) *PrecedencePredicateTransition {
	t := &PrecedencePredicateTransition{
		BaseAbstractPredicateTransition: NewBasePredicateTransition(target),
		precedence:                      precedence,
	}

	t.serializationType = TransitionPRECEDENCE
	t.isEpsilon = true

	return t
}

// Matches always returns false.
func (t *PrecedencePredicateTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *PrecedencePredicateTransition) getPredicate() *PrecedencePredicate {
	return NewPrecedencePredicate(t.precedence)
}

func (t *PrecedencePredicateTransition) String() string {
	return fmt.Sprint(t.precedence) + " >= _p"
}
