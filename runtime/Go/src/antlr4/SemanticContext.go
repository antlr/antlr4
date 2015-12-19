package antlr4

import (
	)

// A tree structure used to record the semantic context in which
//  an ATN configuration is valid.  It's either a single predicate,
//  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
//
//  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
//  {@link SemanticContext} within the scope of this outer class.</p>
//

type SemanticContext interface {
	evaluate(parser *Recognizer, outerContext *RuleContext) bool
	evalPrecedence(parser *Recognizer, outerContext *RuleContext) *SemanticContext
	toString() string
}

func SemanticContextandContext(a, b *SemanticContext) *SemanticContext {
	if (a == nil || a == SemanticContextNONE) {
		return b
	}
	if (b == nil || b == SemanticContextNONE) {
		return a
	}
	var result = NewAND(a, b)
	if ( len(result.opnds) == 1) {
		return result.opnds[0]
	} else {
		return result
	}
}

func SemanticContextorContext(a, b *SemanticContext) *SemanticContext {
	if (a == nil) {
		return b
	}
	if (b == nil) {
		return a
	}
	if (a == SemanticContextNONE || b == SemanticContextNONE) {
		return SemanticContextNONE
	}
	var result = NewOR(a, b *SemanticContext)
	if ( len(result.opnds) == 1) {
		return result.opnds[0]
	} else {
		return result
	}
}

type Predicate struct {
	ruleIndex int
	predIndex int
	isCtxDependent bool
}

func NewPredicate(ruleIndex, predIndex int, isCtxDependent bool) *Predicate {
	p := new(Predicate)

	p.ruleIndex = ruleIndex
	p.predIndex = predIndex
	p.isCtxDependent = isCtxDependent // e.g., $i ref in pred
	return p
}

//The default {@link SemanticContext}, which is semantically equivalent to
//a predicate of the form {@code {true}?}.

var SemanticContextNONE = NewPredicate(-1,-1,false)

func (this *Predicate) evaluate(parser *Recognizer, outerContext *RuleContext) *SemanticContext {

	var localctx *RuleContext = nil

	if (this.isCtxDependent){
		localctx = outerContext
	}

	return parser.sempred(localctx, this.ruleIndex, this.predIndex)
}

func (this *Predicate) hashString() {
	return "" + this.ruleIndex + "/" + this.predIndex + "/" + this.isCtxDependent
}

func (this *Predicate) equals(other interface{}) bool {
	if (this == other) {
		return true
	} else if _, ok := other.(*Predicate); !ok {
		return false
	} else {
		return this.ruleIndex == other.(*Predicate).ruleIndex &&
				this.predIndex == other.(*Predicate).predIndex &&
				this.isCtxDependent == other.(*Predicate).isCtxDependent
	}
}

func (this *Predicate) toString() string {
	return "{" + this.ruleIndex + ":" + this.predIndex + "}?"
}

type PrecedencePredicate struct {
	SemanticContext

	precedence int
}

func NewPrecedencePredicate(precedence int) *PrecedencePredicate {

	this := new(PrecedencePredicate)
	this.precedence = precedence

	return this
}

func (this *PrecedencePredicate) evaluate(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	return parser.precpred(outerContext, this.precedence)
}

func (this *PrecedencePredicate) evalPrecedence(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	if (parser.precpred(outerContext, this.precedence)) {
		return SemanticContextNONE
	} else {
		return nil
	}
}

func (this *PrecedencePredicate) compareTo(other *PrecedencePredicate) bool {
	return this.precedence - other.precedence
}

func (this *PrecedencePredicate) hashString() {
	return "31"
}

func (this *PrecedencePredicate) equals(other interface{}) bool {
	if (this == other) {
		return true
	} else if _, ok := other.(*PrecedencePredicate); !ok {
		return false
	} else {
		return this.precedence == other.(*PrecedencePredicate).precedence
	}
}

func (this *PrecedencePredicate) toString() string {
	return "{"+this.precedence+">=prec}?"
}


func PrecedencePredicatefilterPrecedencePredicates(set *Set) []*PrecedencePredicate {
	var result = make([]*PrecedencePredicate)

	for _,v := range set.values() {
		if c2, ok := v.(*PrecedencePredicate); ok {
			result = append(result, c2)
		}
	}

	return result
}

// A semantic context which is true whenever none of the contained contexts
// is false.


type AND struct {
	SemanticContext

	opnds []*SemanticContext
}

func NewAND(a, b *SemanticContext) *AND {

	var operands = NewSet(nil,nil)
	if aa, ok := a.(*AND); ok {
		for _, o := range aa.opnds {
			operands.add(o)
		}
	} else {
		operands.add(a)
	}

	if ba, ok := b.(*AND); ok {
		for _, o := range ba.opnds {
			operands.add(o)
		}
	} else {
		operands.add(b)
	}
	var precedencePredicates = PrecedencePredicatefilterPrecedencePredicates(operands)
	if ( len(precedencePredicates) > 0) {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate = nil

		for _,p := range precedencePredicates {
			if(reduced==nil || p.precedence < reduced.precedence) {
				reduced = p
			}
		}

		operands.add(reduced)
	}

	this := new(AND)
	this.opnds = operands.values()
	return this
}

func (this *AND) equals(other interface{}) bool {
	if (this == other) {
		return true
	} else if _, ok := other.(*AND); !ok {
		return false
	} else {
		return this.opnds == other.(*AND).opnds
	}
}

func (this *AND) hashString() {
	return "" + this.opnds + "/AND"
}
//
// {@inheritDoc}
//
// <p>
// The evaluation of predicates by this context is short-circuiting, but
// unordered.</p>
//
func (this *AND) evaluate(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	for i := 0; i < len(this.opnds); i++ {
		if (!this.opnds[i].evaluate(parser *Recognizer, outerContext *RuleContext)) {
			return false
		}
	}
	return true
}

func (this *AND) evalPrecedence(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	var differs = false
	var operands = make([]*SemanticContext)

	for i := 0; i < len(this.opnds); i++ {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser, outerContext)
		differs |= (evaluated != context)
		if (evaluated == nil) {
			// The AND context is false if any element is false
			return nil
		} else if (evaluated != SemanticContextNONE) {
			// Reduce the result by skipping true elements
			operands = append (operands, evaluated)
		}
	}
	if (!differs) {
		return this
	}

	if ( len(operands) == 0) {
		// all elements were true, so the AND context is true
		return SemanticContextNONE
	}

	var result *SemanticContext = nil

	for _,o := range operands {
		if (result == nil){
			result = 0
		} else  {
			result = SemanticContextandContext(result, o)
		}
	}

	return result
}

func (this *AND) toString() string {
	var s = ""

	for _,o := range this.opnds {
		s += "&& " + o.toString()
	}

	if (len(s) > 3){
		return s[0:3]
	} else {
		return s
	}
}

//
// A semantic context which is true whenever at least one of the contained
// contexts is true.
//

type OR struct {
	SemanticContext

	opnds []*SemanticContext
}

func NewOR(a, b *SemanticContext) *OR {
	var operands = NewSet(nil,nil)
	if aa, ok := a.(*OR); ok {
		for _, o := range aa.opnds {
			operands.add(o)
		}
	} else {
		operands.add(a)
	}

	if ba, ok := b.(*OR); ok {
		for _, o := range ba.opnds {
			operands.add(o)
		}
	} else {
		operands.add(b)
	}
	var precedencePredicates = PrecedencePredicatefilterPrecedencePredicates(operands)
	if ( len(precedencePredicates) > 0) {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate = nil

		for _,p := range precedencePredicates {
			if(reduced==nil || p.precedence > reduced.precedence) {
				reduced = p
			}
		}

		operands.add(reduced)
	}

	this := new(OR)
	this.opnds = operands.values()
	return this
}


func (this *OR) equals(other interface{}) bool {
	if (this == other) {
		return true
	} else if _, ok := other.(*OR); !ok {
		return false
	} else {
		return this.opnds == other.(*OR).opnds
	}
}

func (this *OR) hashString() {
	return "" + this.opnds + "/OR"
}

// <p>
// The evaluation of predicates by this context is short-circuiting, but
// unordered.</p>
//
func (this *OR) evaluate(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	for i := 0; i < len(this.opnds); i++ {
		if (this.opnds[i].evaluate(parser *Recognizer, outerContext *RuleContext)) {
			return true
		}
	}
	return false
}

func (this *OR) evalPrecedence(parser *Recognizer, outerContext *RuleContext) *SemanticContext {
	var differs = false
	var operands = make([]*SemanticContext)
	for i := 0; i < len(this.opnds); i++ {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser *Recognizer, outerContext *RuleContext)
		differs |= (evaluated != context)
		if (evaluated == SemanticContextNONE) {
			// The OR context is true if any element is true
			return SemanticContextNONE
		} else if (evaluated != nil) {
			// Reduce the result by skipping false elements
			operands = append(operands, evaluated)
		}
	}
	if (!differs) {
		return this
	}
	if (len(operands) == 0) {
		// all elements were false, so the OR context is false
		return nil
	}
	var result *SemanticContext = nil

	for _,o := range operands {
		if (result == nil) {
			result = o
		} else {
			result = SemanticContextorContext(result, o);
		}
	}

	return result
}

func (this *OR) toString() string {
	var s = ""

	for _,o := range this.opnds {
		s += "|| " + o.toString()
	}

	if (len(s) > 3){
		return s[0:3]
	} else {
		return s
	}
}




