package antlr4
import (
		"fmt"
)

//  atom, set, epsilon, action, predicate, rule transitions.
//
//  <p>This is a one way link.  It emanates from a state (usually via a list of
//  transitions) and has a target state.</p>
//
//  <p>Since we never have to change the ATN transitions once we construct it,
//  the states. We'll use the term Edge for the DFA to distinguish them from
//  ATN transitions.</p>

//func ([A-Z]+Transition)[ ]?\([A-Za-z, ]+\) \*([A-Z]+Transition) {\n\tTransition\.call\(t, target\)

type Transition struct {
	target *ATNState
	isEpsilon bool
	label *IntervalSet
}

func Transition (target *ATNState) *Transition {

	if (target==nil || target==nil) {
        panic("target cannot be nil.")
    }

	t := new(Transition)
	t.initTransition(target)

    return t
}

func (t *Transition) initTransition(target *ATNState) {
	t.target = target
	// Are we epsilon, action, sempred?
	t.isEpsilon = false
	t.label = nil
}

const(
	TransitionEPSILON = 1
	TransitionRANGE = 2
	TransitionRULE = 3
	TransitionPREDICATE = 4 // e.g., {isType(input.LT(1))}?
	TransitionATOM = 5
	TransitionACTION = 6
	TransitionSET = 7 // ~(A|B) or ~atom, wildcard, which convert to next 2
	TransitionNOT_SET = 8
	TransitionWILDCARD = 9
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
	Transition
	label_ int
	label *IntervalSet
	serializationType int
}

func NewAtomTransition ( target *ATNState, label int ) *AtomTransition {

	t := new(AtomTransition)
	t.initTransition( target )

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

func (t *AtomTransition) matches( symbol, minVocabSymbol,  maxVocabSymbol int ) bool {
    return t.label_ == symbol
}

func (t *AtomTransition) toString() string {
	return t.label_
}

type RuleTransition struct {
	Transition
	ruleIndex, precedence, followState, serializationType int
}

func NewRuleTransition ( ruleStart *ATNState, ruleIndex, precedence, followState int ) *RuleTransition {

	t := new(RuleTransition)
	t.initTransition( ruleStart )

    t.ruleIndex = ruleIndex
	t.precedence = precedence
    t.followState = followState
	t.serializationType = TransitionRULE
    t.isEpsilon = true

    return t
}


func (t *RuleTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return false
}


type EpsilonTransition struct {
	Transition

	isEpsilon bool
	outermostPrecedenceReturn, serializationType int
}

func NewEpsilonTransition ( target *ATNState, outermostPrecedenceReturn int ) *EpsilonTransition {

	t := new(EpsilonTransition)
	t.initTransition( target )

    t.serializationType = TransitionEPSILON
    t.isEpsilon = true
    t.outermostPrecedenceReturn = outermostPrecedenceReturn
    return t
}


func (t *EpsilonTransition) matches( symbol, minVocabSymbol,  maxVocabSymbol int ) {
	return false
}

func (t *EpsilonTransition) toString() string {
	return "epsilon"
}

type RangeTransition struct {
	Transition

	serializationType, start, stop int
}

func NewRangeTransition ( target *ATNState, start, stop int ) *RangeTransition {

	t := new(RangeTransition)
	t.initTransition( target )

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

func (t *RangeTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) {
	return symbol >= t.start && symbol <= t.stop
}

func (t *RangeTransition) toString() string {
	return "'" + string(t.start) + "'..'" + string(t.stop) + "'"
}
//
//type AbstractPredicateTransition struct {
//	Transition
//}
//
//func NewAbstractPredicateTransition ( target *ATNState ) *AbstractPredicateTransition {
//
//	t := new(AbstractPredicateTransition)
//	t.initTransition( target )
//
//	return t
//}


type PredicateTransition struct {
	Transition

	isCtxDependent bool
	ruleIndex, predIndex, serializationType int
}

func PredicateTransition ( target *ATNState, ruleIndex, predIndex int, isCtxDependent bool ) *PredicateTransition {

	t := new(PredicateTransition)
	t.initTransition(target)

    t.serializationType = TransitionPREDICATE
    t.ruleIndex = ruleIndex
    t.predIndex = predIndex
    t.isCtxDependent = isCtxDependent // e.g., $i ref in pred
    t.isEpsilon = true
    return t
}


func (t *PredicateTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return false
}

func (t *PredicateTransition) getPredicate() *Predicate {
	return NewPredicate(t.ruleIndex, t.predIndex, t.isCtxDependent)
}

func (t *PredicateTransition) toString() string {
	return "pred_" + t.ruleIndex + ":" + t.predIndex
}

type ActionTransition struct {
	Transition

	isCtxDependent bool
	ruleIndex, actionIndex, predIndex, serializationType int
}

func NewActionTransition ( target *ATNState, ruleIndex, actionIndex int, isCtxDependent bool ) *ActionTransition {

	t := new(ActionTransition)
	t.initTransition( target )

    t.serializationType = TransitionACTION
    t.ruleIndex = ruleIndex
    t.actionIndex = actionIndex
    t.isCtxDependent = isCtxDependent // e.g., $i ref in pred
    t.isEpsilon = true
    return t
}



func (t *ActionTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return false
}

func (t *ActionTransition) toString() string {
	return "action_" + t.ruleIndex + ":" + t.actionIndex
}
        

type SetTransition struct {
	Transition

	serializationType int
}

func NewSetTransition ( target *ATNState, set *IntervalSet ) *SetTransition {

	t := new(SetTransition)
	t.initTransition( target )
	t.initSetTransition( set )

    return t
}

func (t *SetTransition) initSetTransition( set *IntervalSet ) {

	t.serializationType = TransitionSET
	if (set !=nil && set !=nil) {
		t.label = set
	} else {
		t.label = NewIntervalSet()
		t.label.addOne(TokenInvalidType)
	}

}


func (t *SetTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return t.label.contains(symbol)
}
        

func (t *SetTransition) toString() string {
	return t.label.toString()
}


type NotSetTransition struct {
	SetTransition
}

func NotSetTransition ( target *ATNState, set *IntervalSet) *NotSetTransition {

	t := new(NotSetTransition)
	t.initTransition( target )
	t.initSetTransition( target )

	t.serializationType = TransitionNOT_SET

	return t
}


func (t *NotSetTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !SetTransition.prototype.matches.call(t, symbol, minVocabSymbol, maxVocabSymbol)
}

func (t *NotSetTransition) toString() string {
	return '~' + t.label.toString()
}

type WildcardTransition struct {
	Transition

	serializationType int
}

func NewWildcardTransition ( target *ATNState ) *WildcardTransition {

	t := new(WildcardTransition)
	t.initTransition( target )

	t.serializationType = TransitionWILDCARD
	return t
}

func (t *WildcardTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) bool {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol
}

func (t *WildcardTransition) toString() string {
	return "."
}

type PrecedencePredicateTransition struct {
	Transition

	precedence int
	serializationType int
}

func PrecedencePredicateTransition ( target *ATNState, precedence int ) *PrecedencePredicateTransition {

	t := new(PrecedencePredicateTransition)
	t.initTransition( target )

    t.serializationType = TransitionPRECEDENCE
    t.precedence = precedence
    t.isEpsilon = true

    return t
}


func (t *PrecedencePredicateTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol int) {
	return false
}

func (t *PrecedencePredicateTransition) getPredicate() *NewPrecedencePredicate {
	return NewPrecedencePredicate(t.precedence)
}

func (t *PrecedencePredicateTransition) toString() string {
	return fmt.Sprint(t.precedence) + " >= _p"
}
        











