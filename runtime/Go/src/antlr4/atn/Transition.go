package atn

//  An ATN transition between any two ATN states.  Subclasses define
//  atom, set, epsilon, action, predicate, rule transitions.
//
//  <p>This is a one way link.  It emanates from a state (usually via a list of
//  transitions) and has a target state.</p>
//
//  <p>Since we never have to change the ATN transitions once we construct it,
//  we can fix these transitions as specific classes. The DFA transitions
//  on the other hand need to update the labels as it adds transitions to
//  the states. We'll use the term Edge for the DFA to distinguish them from
//  ATN transitions.</p>

var Token = require('./../Token').Token
var Interval = require('./../IntervalSet').Interval
var IntervalSet = require('./../IntervalSet').IntervalSet
var Predicate = require('./SemanticContext').Predicate
var PrecedencePredicate = require('./SemanticContext').PrecedencePredicate

func Transition (target) {
    // The target of this transition.
    if (target==undefined || target==nil) {
        throw "target cannot be nil."
    }
    this.target = target
    // Are we epsilon, action, sempred?
    this.isEpsilon = false
    this.label = nil
    return this
}
    // constants for serialization
Transition.EPSILON = 1
Transition.RANGE = 2
Transition.RULE = 3
Transition.PREDICATE = 4 // e.g., {isType(input.LT(1))}?
Transition.ATOM = 5
Transition.ACTION = 6
Transition.SET = 7 // ~(A|B) or ~atom, wildcard, which convert to next 2
Transition.NOT_SET = 8
Transition.WILDCARD = 9
Transition.PRECEDENCE = 10

Transition.serializationNames = [
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
            "PRECEDENCE"
        ]

Transition.serializationTypes = {
        EpsilonTransition: Transition.EPSILON,
        RangeTransition: Transition.RANGE,
        RuleTransition: Transition.RULE,
        PredicateTransition: Transition.PREDICATE,
        AtomTransition: Transition.ATOM,
        ActionTransition: Transition.ACTION,
        SetTransition: Transition.SET,
        NotSetTransition: Transition.NOT_SET,
        WildcardTransition: Transition.WILDCARD,
        PrecedencePredicateTransition: Transition.PRECEDENCE
    }


// TODO: make all transitions sets? no, should remove set edges
func AtomTransition(target, label) {
	Transition.call(this, target)
	this.label_ = label // The token type or character value or, signifies special label.
    this.label = this.makeLabel()
    this.serializationType = Transition.ATOM
    return this
}

AtomTransition.prototype = Object.create(Transition.prototype)
AtomTransition.prototype.constructor = AtomTransition

func (this *AtomTransition) makeLabel() {
	var s = NewIntervalSet()
    s.addOne(this.label_)
    return s
}

func (this *AtomTransition) matches( symbol, minVocabSymbol,  maxVocabSymbol) {
    return this.label_ == symbol
}

func (this *AtomTransition) toString() {
	return this.label_
}

func RuleTransition(ruleStart, ruleIndex, precedence, followState) {
	Transition.call(this, ruleStart)
    this.ruleIndex = ruleIndex // ptr to the rule definition object for this rule ref
    this.precedence = precedence
    this.followState = followState // what node to begin computations following ref to rule
    this.serializationType = Transition.RULE
    this.isEpsilon = true
    return this
}

RuleTransition.prototype = Object.create(Transition.prototype)
RuleTransition.prototype.constructor = RuleTransition

func (this *RuleTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false
}


func EpsilonTransition(target, outermostPrecedenceReturn) {
	Transition.call(this, target)
    this.serializationType = Transition.EPSILON
    this.isEpsilon = true
    this.outermostPrecedenceReturn = outermostPrecedenceReturn
    return this
}

EpsilonTransition.prototype = Object.create(Transition.prototype)
EpsilonTransition.prototype.constructor = EpsilonTransition

func (this *EpsilonTransition) matches( symbol, minVocabSymbol,  maxVocabSymbol) {
	return false
}

func (this *EpsilonTransition) toString() {
	return "epsilon"
}

func RangeTransition(target, start, stop) {
	Transition.call(this, target)
	this.serializationType = Transition.RANGE
    this.start = start
    this.stop = stop
    this.label = this.makeLabel()
    return this
}

RangeTransition.prototype = Object.create(Transition.prototype)
RangeTransition.prototype.constructor = RangeTransition

func (this *RangeTransition) makeLabel() {
    var s = NewIntervalSet()
    s.addRange(this.start, this.stop)
    return s
}

func (this *RangeTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= this.start && symbol <= this.stop
}

func (this *RangeTransition) toString() {
	return "'" + String.fromCharCode(this.start) + "'..'" + String.fromCharCode(this.stop) + "'"
}

func AbstractPredicateTransition(target) {
	Transition.call(this, target)
	return this
}

AbstractPredicateTransition.prototype = Object.create(Transition.prototype)
AbstractPredicateTransition.prototype.constructor = AbstractPredicateTransition

func PredicateTransition(target, ruleIndex, predIndex, isCtxDependent) {
	AbstractPredicateTransition.call(this, target)
    this.serializationType = Transition.PREDICATE
    this.ruleIndex = ruleIndex
    this.predIndex = predIndex
    this.isCtxDependent = isCtxDependent // e.g., $i ref in pred
    this.isEpsilon = true
    return this
}

PredicateTransition.prototype = Object.create(AbstractPredicateTransition.prototype)
PredicateTransition.prototype.constructor = PredicateTransition

func (this *PredicateTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false
}

func (this *PredicateTransition) getPredicate() {
	return NewPredicate(this.ruleIndex, this.predIndex, this.isCtxDependent)
}

func (this *PredicateTransition) toString() {
	return "pred_" + this.ruleIndex + ":" + this.predIndex
}

func ActionTransition(target, ruleIndex, actionIndex, isCtxDependent) {
	Transition.call(this, target)
    this.serializationType = Transition.ACTION
    this.ruleIndex = ruleIndex
    this.actionIndex = actionIndex==undefined ? -1 : actionIndex
    this.isCtxDependent = isCtxDependent==undefined ? false : isCtxDependent // e.g., $i ref in pred
    this.isEpsilon = true
    return this
}

ActionTransition.prototype = Object.create(Transition.prototype)
ActionTransition.prototype.constructor = ActionTransition


func (this *ActionTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false
}

func (this *ActionTransition) toString() {
	return "action_" + this.ruleIndex + ":" + this.actionIndex
}
        

// A transition containing a set of values.
func SetTransition(target, set) {
	Transition.call(this, target)
	this.serializationType = Transition.SET
    if (set !=undefined && set !=nil) {
        this.label = set
    } else {
        this.label = NewIntervalSet()
        this.label.addOne(Token.INVALID_TYPE)
    }
    return this
}

SetTransition.prototype = Object.create(Transition.prototype)
SetTransition.prototype.constructor = SetTransition

func (this *SetTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return this.label.contains(symbol)
}
        

func (this *SetTransition) toString() {
	return this.label.toString()
}

func NotSetTransition(target, set) {
	SetTransition.call(this, target, set)
	this.serializationType = Transition.NOT_SET
	return this
}

NotSetTransition.prototype = Object.create(SetTransition.prototype)
NotSetTransition.prototype.constructor = NotSetTransition

func (this *NotSetTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol &&
			!SetTransition.prototype.matches.call(this, symbol, minVocabSymbol, maxVocabSymbol)
}

func (this *NotSetTransition) toString() {
	return '~' + SetTransition.prototype.toString.call(this)
}

func WildcardTransition(target) {
	Transition.call(this, target)
	this.serializationType = Transition.WILDCARD
	return this
}

WildcardTransition.prototype = Object.create(Transition.prototype)
WildcardTransition.prototype.constructor = WildcardTransition


func (this *WildcardTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol
}

func (this *WildcardTransition) toString() {
	return "."
}

func PrecedencePredicateTransition(target, precedence) {
	AbstractPredicateTransition.call(this, target)
    this.serializationType = Transition.PRECEDENCE
    this.precedence = precedence
    this.isEpsilon = true
    return this
}

PrecedencePredicateTransition.prototype = Object.create(AbstractPredicateTransition.prototype)
PrecedencePredicateTransition.prototype.constructor = PrecedencePredicateTransition

func (this *PrecedencePredicateTransition) matches(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false
}

func (this *PrecedencePredicateTransition) getPredicate() {
	return NewPrecedencePredicate(this.precedence)
}

func (this *PrecedencePredicateTransition) toString() {
	return this.precedence + " >= _p"
}
        











