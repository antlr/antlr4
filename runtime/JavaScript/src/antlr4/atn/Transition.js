// [The "BSD license"]
//  Copyright (c) 2012 Terence Parr
//  Copyright (c) 2012 Sam Harwell
//  Copyright (c) 2014 Eric Vergnaud
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions
//  are met:
//
//  1. Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in the
//     documentation and/or other materials provided with the distribution.
//  3. The name of the author may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
//  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

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

var Token = require('./../Token').Token;
var Interval = require('./../IntervalSet').Interval;
var IntervalSet = require('./../IntervalSet').IntervalSet;
var Predicate = require('./SemanticContext').Predicate;
var PrecedencePredicate = require('./SemanticContext').PrecedencePredicate;

function Transition (target) {
    // The target of this transition.
    if (target===undefined || target===null) {
        throw "target cannot be null.";
    }
    this.target = target;
    // Are we epsilon, action, sempred?
    this.isEpsilon = false;
    this.label = null;
    return this;
}
    // constants for serialization
Transition.EPSILON = 1;
Transition.RANGE = 2;
Transition.RULE = 3;
Transition.PREDICATE = 4; // e.g., {isType(input.LT(1))}?
Transition.ATOM = 5;
Transition.ACTION = 6;
Transition.SET = 7; // ~(A|B) or ~atom, wildcard, which convert to next 2
Transition.NOT_SET = 8;
Transition.WILDCARD = 9;
Transition.PRECEDENCE = 10;

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
        ];

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
    };


// TODO: make all transitions sets? no, should remove set edges
function AtomTransition(target, label) {
	Transition.call(this, target);
	this.label_ = label; // The token type or character value; or, signifies special label.
    this.label = this.makeLabel();
    this.serializationType = Transition.ATOM;
    return this;
}

AtomTransition.prototype = Object.create(Transition.prototype);
AtomTransition.prototype.constructor = AtomTransition;

AtomTransition.prototype.makeLabel = function() {
	var s = new IntervalSet();
    s.addOne(this.label_);
    return s;
};

AtomTransition.prototype.matches = function( symbol, minVocabSymbol,  maxVocabSymbol) {
    return this.label_ === symbol;
};

AtomTransition.prototype.toString = function() {
	return this.label_;
};

function RuleTransition(ruleStart, ruleIndex, precedence, followState) {
	Transition.call(this, ruleStart);
    this.ruleIndex = ruleIndex; // ptr to the rule definition object for this rule ref
    this.precedence = precedence;
    this.followState = followState; // what node to begin computations following ref to rule
    this.serializationType = Transition.RULE;
    this.isEpsilon = true;
    return this;
}

RuleTransition.prototype = Object.create(Transition.prototype);
RuleTransition.prototype.constructor = RuleTransition;

RuleTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false;
};


function EpsilonTransition(target, outermostPrecedenceReturn) {
	Transition.call(this, target);
    this.serializationType = Transition.EPSILON;
    this.isEpsilon = true;
    this.outermostPrecedenceReturn = outermostPrecedenceReturn;
    return this;
}

EpsilonTransition.prototype = Object.create(Transition.prototype);
EpsilonTransition.prototype.constructor = EpsilonTransition;

EpsilonTransition.prototype.matches = function( symbol, minVocabSymbol,  maxVocabSymbol) {
	return false;
};

EpsilonTransition.prototype.toString = function() {
	return "epsilon";
};

function RangeTransition(target, start, stop) {
	Transition.call(this, target);
	this.serializationType = Transition.RANGE;
    this.start = start;
    this.stop = stop;
    this.label = this.makeLabel();
    return this;
}

RangeTransition.prototype = Object.create(Transition.prototype);
RangeTransition.prototype.constructor = RangeTransition;

RangeTransition.prototype.makeLabel = function() {
    var s = new IntervalSet();
    s.addRange(this.start, this.stop);
    return s;
};

RangeTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= this.start && symbol <= this.stop;
};

RangeTransition.prototype.toString = function() {
	return "'" + String.fromCharCode(this.start) + "'..'" + String.fromCharCode(this.stop) + "'";
};

function AbstractPredicateTransition(target) {
	Transition.call(this, target);
	return this;
}

AbstractPredicateTransition.prototype = Object.create(Transition.prototype);
AbstractPredicateTransition.prototype.constructor = AbstractPredicateTransition;

function PredicateTransition(target, ruleIndex, predIndex, isCtxDependent) {
	AbstractPredicateTransition.call(this, target);
    this.serializationType = Transition.PREDICATE;
    this.ruleIndex = ruleIndex;
    this.predIndex = predIndex;
    this.isCtxDependent = isCtxDependent; // e.g., $i ref in pred
    this.isEpsilon = true;
    return this;
}

PredicateTransition.prototype = Object.create(AbstractPredicateTransition.prototype);
PredicateTransition.prototype.constructor = PredicateTransition;

PredicateTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false;
};

PredicateTransition.prototype.getPredicate = function() {
	return new Predicate(this.ruleIndex, this.predIndex, this.isCtxDependent);
};

PredicateTransition.prototype.toString = function() {
	return "pred_" + this.ruleIndex + ":" + this.predIndex;
};

function ActionTransition(target, ruleIndex, actionIndex, isCtxDependent) {
	Transition.call(this, target);
    this.serializationType = Transition.ACTION;
    this.ruleIndex = ruleIndex;
    this.actionIndex = actionIndex===undefined ? -1 : actionIndex;
    this.isCtxDependent = isCtxDependent===undefined ? false : isCtxDependent; // e.g., $i ref in pred
    this.isEpsilon = true;
    return this;
}

ActionTransition.prototype = Object.create(Transition.prototype);
ActionTransition.prototype.constructor = ActionTransition;


ActionTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false;
};

ActionTransition.prototype.toString = function() {
	return "action_" + this.ruleIndex + ":" + this.actionIndex;
};
        

// A transition containing a set of values.
function SetTransition(target, set) {
	Transition.call(this, target);
	this.serializationType = Transition.SET;
    if (set !==undefined && set !==null) {
        this.label = set;
    } else {
        this.label = new IntervalSet();
        this.label.addOne(Token.INVALID_TYPE);
    }
    return this;
}

SetTransition.prototype = Object.create(Transition.prototype);
SetTransition.prototype.constructor = SetTransition;

SetTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return this.label.contains(symbol);
};
        

SetTransition.prototype.toString = function() {
	return this.label.toString();
};

function NotSetTransition(target, set) {
	SetTransition.call(this, target, set);
	this.serializationType = Transition.NOT_SET;
	return this;
}

NotSetTransition.prototype = Object.create(SetTransition.prototype);
NotSetTransition.prototype.constructor = NotSetTransition;

NotSetTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol &&
			!SetTransition.prototype.matches.call(this, symbol, minVocabSymbol, maxVocabSymbol);
};

NotSetTransition.prototype.toString = function() {
	return '~' + SetTransition.prototype.toString.call(this);
};

function WildcardTransition(target) {
	Transition.call(this, target);
	this.serializationType = Transition.WILDCARD;
	return this;
}

WildcardTransition.prototype = Object.create(Transition.prototype);
WildcardTransition.prototype.constructor = WildcardTransition;


WildcardTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
};

WildcardTransition.prototype.toString = function() {
	return ".";
};

function PrecedencePredicateTransition(target, precedence) {
	AbstractPredicateTransition.call(this, target);
    this.serializationType = Transition.PRECEDENCE;
    this.precedence = precedence;
    this.isEpsilon = true;
    return this;
}

PrecedencePredicateTransition.prototype = Object.create(AbstractPredicateTransition.prototype);
PrecedencePredicateTransition.prototype.constructor = PrecedencePredicateTransition;

PrecedencePredicateTransition.prototype.matches = function(symbol, minVocabSymbol,  maxVocabSymbol) {
	return false;
};

PrecedencePredicateTransition.prototype.getPredicate = function() {
	return new PrecedencePredicate(this.precedence);
};

PrecedencePredicateTransition.prototype.toString = function() {
	return this.precedence + " >= _p";
};
        
exports.Transition = Transition;
exports.AtomTransition = AtomTransition;
exports.SetTransition = SetTransition;
exports.NotSetTransition = NotSetTransition;
exports.RuleTransition = RuleTransition;
exports.ActionTransition = ActionTransition;
exports.EpsilonTransition = EpsilonTransition;
exports.RangeTransition = RangeTransition;
exports.WildcardTransition = WildcardTransition;
exports.PredicateTransition = PredicateTransition;
exports.PrecedencePredicateTransition = PrecedencePredicateTransition;
exports.AbstractPredicateTransition = AbstractPredicateTransition;