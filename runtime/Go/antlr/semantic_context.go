// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
	"strconv"
)

// SemanticContext is a tree structure used to record the semantic context in
// which an ATN configuration is valid.  It's either a single predicate, a
// conjunction p1&&p2, or a sum of products p1||p2.
//
// I have scoped the AND, OR, and Predicate subclasses of
// SemanticContext within the scope of this outer class.
type SemanticContext interface {
	comparable

	evaluate(parser Recognizer, outerContext RuleContext) bool
	evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext

	hash() int
	String() string
}

// SemanticContextandContext TODO: docs.
func SemanticContextandContext(a, b SemanticContext) SemanticContext {
	if a == nil || a == SemanticContextNone {
		return b
	}
	if b == nil || b == SemanticContextNone {
		return a
	}
	result := NewAND(a, b)
	if len(result.opnds) == 1 {
		return result.opnds[0]
	}

	return result
}

// SemanticContextorContext TODO: docs.
func SemanticContextorContext(a, b SemanticContext) SemanticContext {
	if a == nil {
		return b
	}
	if b == nil {
		return a
	}
	if a == SemanticContextNone || b == SemanticContextNone {
		return SemanticContextNone
	}
	result := NewOR(a, b)
	if len(result.opnds) == 1 {
		return result.opnds[0]
	}

	return result
}

// Predicate represents a semantic predicate
type Predicate struct {
	ruleIndex      int
	predIndex      int
	isCtxDependent bool
}

// NewPredicate returns a new instance of Predicate.
func NewPredicate(ruleIndex, predIndex int, isCtxDependent bool) *Predicate {
	return &Predicate{
		ruleIndex:      ruleIndex,
		predIndex:      predIndex,
		isCtxDependent: isCtxDependent,
	}
}

// SemanticContextNone is the default SemanticContext, which is semantically
// equivalent to a predicate of the form {true?}.
var SemanticContextNone SemanticContext = NewPredicate(-1, -1, false)

func (p *Predicate) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	return p
}

func (p *Predicate) evaluate(parser Recognizer, outerContext RuleContext) bool {

	var localctx RuleContext

	if p.isCtxDependent {
		localctx = outerContext
	}

	return parser.Sempred(localctx, p.ruleIndex, p.predIndex)
}

func (p *Predicate) equals(other interface{}) bool {
	if p == other {
		return true
	} else if _, ok := other.(*Predicate); !ok {
		return false
	} else {
		return p.ruleIndex == other.(*Predicate).ruleIndex &&
			p.predIndex == other.(*Predicate).predIndex &&
			p.isCtxDependent == other.(*Predicate).isCtxDependent
	}
}

func (p *Predicate) hash() int {
	return p.ruleIndex*43 + p.predIndex*47
}

func (p *Predicate) String() string {
	return "{" + strconv.Itoa(p.ruleIndex) + ":" + strconv.Itoa(p.predIndex) + "}?"
}

// PrecedencePredicate TODO: docs
type PrecedencePredicate struct {
	precedence int
}

// NewPrecedencePredicate returns a new instance of PrecedencePredicate.
func NewPrecedencePredicate(precedence int) *PrecedencePredicate {
	return &PrecedencePredicate{precedence: precedence}
}

func (p *PrecedencePredicate) evaluate(parser Recognizer, outerContext RuleContext) bool {
	return parser.Precpred(outerContext, p.precedence)
}

func (p *PrecedencePredicate) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	if parser.Precpred(outerContext, p.precedence) {
		return SemanticContextNone
	}

	return nil
}

func (p *PrecedencePredicate) compareTo(other *PrecedencePredicate) int {
	return p.precedence - other.precedence
}

func (p *PrecedencePredicate) equals(other interface{}) bool {
	if p == other {
		return true
	} else if _, ok := other.(*PrecedencePredicate); !ok {
		return false
	} else {
		return p.precedence == other.(*PrecedencePredicate).precedence
	}
}

func (p *PrecedencePredicate) hash() int {
	return p.precedence * 51
}

func (p *PrecedencePredicate) String() string {
	return "{" + strconv.Itoa(p.precedence) + ">=prec}?"
}

// PrecedencePredicatefilterPrecedencePredicates TODO: docs.
func PrecedencePredicatefilterPrecedencePredicates(s *set) []*PrecedencePredicate {
	result := make([]*PrecedencePredicate, 0)

	for _, v := range s.values() {
		if c2, ok := v.(*PrecedencePredicate); ok {
			result = append(result, c2)
		}
	}

	return result
}

// AND is true whenever none of the contained contexts is false.
type AND struct {
	opnds []SemanticContext
}

// NewAND returns a new instance of NewAND.
func NewAND(a, b SemanticContext) *AND {

	operands := newSet(nil, nil)
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
	precedencePredicates := PrecedencePredicatefilterPrecedencePredicates(operands)
	if len(precedencePredicates) > 0 {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate

		for _, p := range precedencePredicates {
			if reduced == nil || p.precedence < reduced.precedence {
				reduced = p
			}
		}

		operands.add(reduced)
	}

	vs := operands.values()
	opnds := make([]SemanticContext, len(vs))
	for i, v := range vs {
		opnds[i] = v.(SemanticContext)
	}

	return &AND{opnds: opnds}
}

func (a *AND) equals(other interface{}) bool {
	if a == other {
		return true
	} else if _, ok := other.(*AND); !ok {
		return false
	} else {
		for i, v := range other.(*AND).opnds {
			if !a.opnds[i].equals(v) {
				return false
			}
		}
		return true
	}
}

//
// {@inheritDoc}
//
//
// The evaluation of predicates by a context is short-circuiting, but
// unordered.
//
func (a *AND) evaluate(parser Recognizer, outerContext RuleContext) bool {
	for i := 0; i < len(a.opnds); i++ {
		if !a.opnds[i].evaluate(parser, outerContext) {
			return false
		}
	}
	return true
}

func (a *AND) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	differs := false
	operands := make([]SemanticContext, 0)

	for i := 0; i < len(a.opnds); i++ {
		context := a.opnds[i]
		evaluated := context.evalPrecedence(parser, outerContext)
		differs = differs || (evaluated != context)
		if evaluated == nil {
			// The AND context is false if any element is false
			return nil
		} else if evaluated != SemanticContextNone {
			// Reduce the result by Skipping true elements
			operands = append(operands, evaluated)
		}
	}
	if !differs {
		return a
	}

	if len(operands) == 0 {
		// all elements were true, so the AND context is true
		return SemanticContextNone
	}

	var result SemanticContext

	for _, o := range operands {
		if result == nil {
			result = o
		} else {
			result = SemanticContextandContext(result, o)
		}
	}

	return result
}

func (a *AND) hash() int {
	h := murmurInit(37) // Init with a value different from OR
	for _, op := range a.opnds {
		h = murmurUpdate(h, op.hash())
	}
	return murmurFinish(h, len(a.opnds))
}

func (a *OR) hash() int {
	h := murmurInit(41) // Init with a value different from AND
	for _, op := range a.opnds {
		h = murmurUpdate(h, op.hash())
	}
	return murmurFinish(h, len(a.opnds))
}

func (a *AND) String() string {
	s := ""

	for _, o := range a.opnds {
		s += "&& " + fmt.Sprint(o)
	}

	if len(s) > 3 {
		return s[0:3]
	}

	return s
}

// OR is true whenever at least one of the contained contexts is true.
type OR struct {
	opnds []SemanticContext
}

// NewOR returns a new instance of OR.
func NewOR(a, b SemanticContext) *OR {

	operands := newSet(nil, nil)
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
	precedencePredicates := PrecedencePredicatefilterPrecedencePredicates(operands)
	if len(precedencePredicates) > 0 {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate

		for _, p := range precedencePredicates {
			if reduced == nil || p.precedence > reduced.precedence {
				reduced = p
			}
		}

		operands.add(reduced)
	}

	vs := operands.values()

	opnds := make([]SemanticContext, len(vs))
	for i, v := range vs {
		opnds[i] = v.(SemanticContext)
	}

	return &OR{opnds: opnds}
}

func (a *OR) equals(other interface{}) bool {
	if a == other {
		return true
	} else if _, ok := other.(*OR); !ok {
		return false
	} else {
		for i, v := range other.(*OR).opnds {
			if !a.opnds[i].equals(v) {
				return false
			}
		}
		return true
	}
}

//
// The evaluation of predicates by o context is short-circuiting, but
// unordered.
//
func (a *OR) evaluate(parser Recognizer, outerContext RuleContext) bool {
	for i := 0; i < len(a.opnds); i++ {
		if a.opnds[i].evaluate(parser, outerContext) {
			return true
		}
	}
	return false
}

func (a *OR) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	differs := false
	operands := make([]SemanticContext, 0)
	for i := 0; i < len(a.opnds); i++ {
		context := a.opnds[i]
		evaluated := context.evalPrecedence(parser, outerContext)
		differs = differs || (evaluated != context)
		if evaluated == SemanticContextNone {
			// The OR context is true if any element is true
			return SemanticContextNone
		} else if evaluated != nil {
			// Reduce the result by Skipping false elements
			operands = append(operands, evaluated)
		}
	}
	if !differs {
		return a
	}
	if len(operands) == 0 {
		// all elements were false, so the OR context is false
		return nil
	}
	var result SemanticContext

	for _, o := range operands {
		if result == nil {
			result = o
		} else {
			result = SemanticContextorContext(result, o)
		}
	}

	return result
}

func (a *OR) String() string {
	s := ""

	for _, o := range a.opnds {
		s += "|| " + fmt.Sprint(o)
	}

	if len(s) > 3 {
		return s[0:3]
	}

	return s
}
