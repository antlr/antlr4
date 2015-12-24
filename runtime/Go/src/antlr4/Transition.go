package antlr4

import (
	"fmt"
	"strconv"
)

//  atom, set, epsilon, action, predicate, rule transitions.
//
//  <p>This is a one way link.  It emanates from a state (usually via a list of
//  transitions) and has a target state.</p>
//
//  <p>Since we never have to change the ATN transitions once we construct it,
//  the states. We'll use the term Edge for the DFA to distinguish them from
//  ATN transitions.</p>

type ITransition interface {
	getTarGet() IATNState
	setTarGet(IATNState)
	getIsEpsilon() bool
	getLabel() *IntervalSet
	getSerializationType() int
	Matches(int, int, int) bool
}

type Transition struct {
	target            IATNState
	isEpsilon         bool
	label             *IntervalSet
	serializationType int
}

func NewTransition(target IATNState) *Transition {

	if target == nil || target == nil {
		panic("target cannot be nil.")
	}

	t := new(Transition)
	t.InitTransition(target)

	return t
}

func (t *Transition) InitTransition(target IATNState) {
	t.target = target
	// Are we epsilon, action, sempred?
	t.isEpsilon = false
	t.label = nil
}

func (t *Transition) getTarGet() IATNState {
	return t.target
}

func (t *Transition) setTarGet(s IATNState) {
	t.target = s
}

func (t *Transition) getIsEpsilon() bool {
	return t.isEpsilon
}

func (t *Transition) getLabel() *IntervalSet {
	return t.label
}

func (t *Transition) getSerializationType() int {
	return t.serializationType
}

func (t *Transition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	panic("Not implemented")
}

const (
	TransitionEPSILON    = 1
	TransitionRANGE      = 2
	TransitionRULE       = 3
	TransitionPREDICATE  = 4 // e.g., {isType(input.LT(1))}?
	TransitionATOM       = 5
	TransitionACTION     = 6
	TransitionSET        = 7 // ~(A|B) or ~atom, wildcard, which convert to next 2
	TransitionNOT_SET    = 8
	TransitionWILDCARD   = 9
	TransitionPRECEDENCE = 10
)

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

//var TransitionserializationTypes struct {
//	EpsilonTransition int
//	RangeTransition int
//	RuleTransition int
//	PredicateTransition int
//	AtomTransition int
//	ActionTransition int
//	SetTransition int
//	NotSetTransition int
//	WildcardTransition int
//	PrecedencePredicateTransition int
//}{
//	TransitionEPSILON,
//	TransitionRANGE,
//	TransitionRULE,
//	TransitionPREDICATE,
//	TransitionATOM,
//	TransitionACTION,
//	TransitionSET,
//	TransitionNOT_SET,
//	TransitionWILDCARD,
//	TransitionPRECEDENCE
//}

// TODO: make all transitions sets? no, should remove set edges
type AtomTransition struct {
	*Transition
	label_ int
	label  *IntervalSet
}

func NewAtomTransition(target IATNState, label int) *AtomTransition {

	t := new(AtomTransition)
	t.InitTransition(target)

	t.label_ = label // The token type or character value or, signifies special label.
	t.label = t.makeLabel()
	t.serializationType = TransitionATOM

	return t
}

func (t *AtomTransition) makeLabel() *IntervalSet {
	var s = NewIntervalSet()
	s.addOne(t.label_)
	return s
}

func (t *AtomTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return t.label_ == symbol
}

func (t *AtomTransition) toString() string {
	return strconv.Itoa(t.label_)
}

type RuleTransition struct {
	*Transition

	followState           IATNState
	ruleIndex, precedence int
}

func NewRuleTransition(ruleStart IATNState, ruleIndex, precedence int, followState IATNState) *RuleTransition {

	t := new(RuleTransition)
	t.InitTransition(ruleStart)

	t.ruleIndex = ruleIndex
	t.precedence = precedence
	t.followState = followState
	t.serializationType = TransitionRULE
	t.isEpsilon = true

	return t
}

func (t *RuleTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

type EpsilonTransition struct {
	*Transition

	isEpsilon                 bool
	outermostPrecedenceReturn int
}

func NewEpsilonTransition(target IATNState, outermostPrecedenceReturn int) *EpsilonTransition {

	t := new(EpsilonTransition)
	t.InitTransition(target)

	t.serializationType = TransitionEPSILON
	t.isEpsilon = true
	t.outermostPrecedenceReturn = outermostPrecedenceReturn
	return t
}

func (t *EpsilonTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *EpsilonTransition) toString() string {
	return "epsilon"
}

type RangeTransition struct {
	*Transition

	start, stop int
}

func NewRangeTransition(target IATNState, start, stop int) *RangeTransition {

	t := new(RangeTransition)
	t.InitTransition(target)

	t.serializationType = TransitionRANGE
	t.start = start
	t.stop = stop
	t.label = t.makeLabel()
	return t
}

func (t *RangeTransition) makeLabel() *IntervalSet {
	var s = NewIntervalSet()
	s.addRange(t.start, t.stop)
	return s
}

func (t *RangeTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= t.start && symbol <= t.stop
}

func (t *RangeTransition) toString() string {
	return "'" + string(t.start) + "'..'" + string(t.stop) + "'"
}

type AbstractPredicateTransition struct {
	*Transition
}

func NewAbstractPredicateTransition(target IATNState) *AbstractPredicateTransition {

	t := new(AbstractPredicateTransition)
	t.InitTransition(target)

	return t
}

type PredicateTransition struct {
	*Transition

	isCtxDependent       bool
	ruleIndex, predIndex int
}

func NewPredicateTransition(target IATNState, ruleIndex, predIndex int, isCtxDependent bool) *PredicateTransition {

	t := new(PredicateTransition)
	t.InitTransition(target)

	t.serializationType = TransitionPREDICATE
	t.ruleIndex = ruleIndex
	t.predIndex = predIndex
	t.isCtxDependent = isCtxDependent // e.g., $i ref in pred
	t.isEpsilon = true
	return t
}

func (t *PredicateTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *PredicateTransition) getPredicate() *Predicate {
	return NewPredicate(t.ruleIndex, t.predIndex, t.isCtxDependent)
}

func (t *PredicateTransition) toString() string {
	return "pred_" + strconv.Itoa(t.ruleIndex) + ":" + strconv.Itoa(t.predIndex)
}

type ActionTransition struct {
	*Transition

	isCtxDependent                    bool
	ruleIndex, actionIndex, predIndex int
}

func NewActionTransition(target IATNState, ruleIndex, actionIndex int, isCtxDependent bool) *ActionTransition {

	t := new(ActionTransition)
	t.InitTransition(target)

	t.serializationType = TransitionACTION
	t.ruleIndex = ruleIndex
	t.actionIndex = actionIndex
	t.isCtxDependent = isCtxDependent // e.g., $i ref in pred
	t.isEpsilon = true
	return t
}

func (t *ActionTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *ActionTransition) toString() string {
	return "action_" + strconv.Itoa(t.ruleIndex) + ":" + strconv.Itoa(t.actionIndex)
}

type SetTransition struct {
	*Transition
}

func NewSetTransition(target IATNState, set *IntervalSet) *SetTransition {

	t := new(SetTransition)
	t.InitTransition(target)
	t.InitSetTransition(set)

	return t
}

func (t *SetTransition) InitSetTransition(set *IntervalSet) {

	t.serializationType = TransitionSET
	if set != nil && set != nil {
		t.label = set
	} else {
		t.label = NewIntervalSet()
		t.label.addOne(TokenInvalidType)
	}

}

func (t *SetTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return t.label.contains(symbol)
}

func (t *SetTransition) toString() string {
	return t.label.toString()
}

type NotSetTransition struct {
	SetTransition
}

func NewNotSetTransition(target IATNState, set *IntervalSet) *NotSetTransition {

	t := new(NotSetTransition)
	t.InitTransition(target)
	t.InitSetTransition(set)

	t.serializationType = TransitionNOT_SET

	return t
}

func (t *NotSetTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !t.label.contains(symbol)
}

func (t *NotSetTransition) toString() string {
	return "~" + t.label.toString()
}

type WildcardTransition struct {
	*Transition
}

func NewWildcardTransition(target IATNState) *WildcardTransition {

	t := new(WildcardTransition)
	t.InitTransition(target)

	t.serializationType = TransitionWILDCARD
	return t
}

func (t *WildcardTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol
}

func (t *WildcardTransition) toString() string {
	return "."
}

type PrecedencePredicateTransition struct {
	*Transition

	precedence int
}

func NewPrecedencePredicateTransition(target IATNState, precedence int) *PrecedencePredicateTransition {

	t := new(PrecedencePredicateTransition)
	t.InitTransition(target)

	t.serializationType = TransitionPRECEDENCE
	t.precedence = precedence
	t.isEpsilon = true

	return t
}

func (t *PrecedencePredicateTransition) Matches(symbol, minVocabSymbol, maxVocabSymbol int) bool {
	return false
}

func (t *PrecedencePredicateTransition) getPredicate() *PrecedencePredicate {
	return NewPrecedencePredicate(t.precedence)
}

func (t *PrecedencePredicateTransition) toString() string {
	return fmt.Sprint(t.precedence) + " >= _p"
}
