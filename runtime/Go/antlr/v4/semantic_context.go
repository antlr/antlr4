// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
	"strconv"
)

// SemanticContext is a tree structure used to record the semantic context in which
//
//	an ATN configuration is valid.  It's either a single predicate,
//	a conjunction p1 && p2, or a sum of products p1 || p2.
//
//	I have scoped the AND, OR, and Predicate subclasses of
//	[SemanticContext] within the scope of this outer ``class''
type SemanticContext interface {
	Equals(other Collectable[SemanticContext]) bool
	Hash() int

	evaluate(parser Recognizer, outerContext RuleContext) bool
	evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext

	String() string
}

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

type Predicate struct {
	ruleIndex      int
	predIndex      int
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

var SemanticContextNone = NewPredicate(-1, -1, false)

func (p *Predicate) evalPrecedence(_ Recognizer, _ RuleContext) SemanticContext {
	return p
}

func (p *Predicate) evaluate(parser Recognizer, outerContext RuleContext) bool {

	var localctx RuleContext

	if p.isCtxDependent {
		localctx = outerContext
	}

	return parser.Sempred(localctx, p.ruleIndex, p.predIndex)
}

func (p *Predicate) Equals(other Collectable[SemanticContext]) bool {
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

func (p *Predicate) Hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, p.ruleIndex)
	h = murmurUpdate(h, p.predIndex)
	if p.isCtxDependent {
		h = murmurUpdate(h, 1)
	} else {
		h = murmurUpdate(h, 0)
	}
	return murmurFinish(h, 3)
}

func (p *Predicate) String() string {
	return "{" + strconv.Itoa(p.ruleIndex) + ":" + strconv.Itoa(p.predIndex) + "}?"
}

type PrecedencePredicate struct {
	precedence int
}

func NewPrecedencePredicate(precedence int) *PrecedencePredicate {

	p := new(PrecedencePredicate)
	p.precedence = precedence

	return p
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

func (p *PrecedencePredicate) Equals(other Collectable[SemanticContext]) bool {

	var op *PrecedencePredicate
	var ok bool
	if op, ok = other.(*PrecedencePredicate); !ok {
		return false
	}

	if p == op {
		return true
	}

	return p.precedence == other.(*PrecedencePredicate).precedence
}

func (p *PrecedencePredicate) Hash() int {
	h := uint32(1)
	h = 31*h + uint32(p.precedence)
	return int(h)
}

func (p *PrecedencePredicate) String() string {
	return "{" + strconv.Itoa(p.precedence) + ">=prec}?"
}

func PrecedencePredicatefilterPrecedencePredicates(set *JStore[SemanticContext, Comparator[SemanticContext]]) []*PrecedencePredicate {
	result := make([]*PrecedencePredicate, 0)

	set.Each(func(v SemanticContext) bool {
		if c2, ok := v.(*PrecedencePredicate); ok {
			result = append(result, c2)
		}
		return true
	})

	return result
}

// compareSemanticContexts orders contexts by kind and then by their fields, so an operator
// holds its operands in one order whichever order they were combined in.
func compareSemanticContexts(a, b SemanticContext) int {
	if c := compareInts(semanticContextKind(a), semanticContextKind(b)); c != 0 {
		return c
	}
	var x, y []SemanticContext
	switch p := a.(type) {
	case *Predicate:
		q := b.(*Predicate)
		if p.ruleIndex != q.ruleIndex {
			return compareInts(p.ruleIndex, q.ruleIndex)
		}
		if p.predIndex != q.predIndex {
			return compareInts(p.predIndex, q.predIndex)
		}
		if p.isCtxDependent == q.isCtxDependent {
			return 0
		}
		if q.isCtxDependent {
			return -1
		}
		return 1
	case *PrecedencePredicate:
		return compareInts(p.precedence, b.(*PrecedencePredicate).precedence)
	case *AND:
		x, y = p.opnds, b.(*AND).opnds
	case *OR:
		x, y = p.opnds, b.(*OR).opnds
	default:
		return compareInts(a.Hash(), b.Hash())
	}
	if len(x) != len(y) {
		return compareInts(len(x), len(y))
	}
	for i := range x {
		if c := compareSemanticContexts(x[i], y[i]); c != 0 {
			return c
		}
	}
	return 0
}

func semanticContextKind(c SemanticContext) int {
	switch c.(type) {
	case *Predicate:
		return 0
	case *PrecedencePredicate:
		return 1
	case *AND:
		return 2
	case *OR:
		return 3
	}
	return 4
}

func compareInts(a, b int) int {
	if a < b {
		return -1
	}
	if a > b {
		return 1
	}
	return 0
}

// mergeSemanticContexts merges two operand slices, each already in compareSemanticContexts
// order, into one slice in that order without duplicates.
func mergeSemanticContexts(x, y []SemanticContext) []SemanticContext {
	merged := make([]SemanticContext, 0, len(x)+len(y))
	i, j := 0, 0
	for i < len(x) && j < len(y) {
		// compareSemanticContexts orders contexts of other kinds by hash code, so it can tie two unequal ones.
		switch c := compareSemanticContexts(x[i], y[j]); {
		case c < 0, c == 0 && !x[i].Equals(y[j]):
			merged = append(merged, x[i])
			i++
		case c > 0:
			merged = append(merged, y[j])
			j++
		default:
			merged = append(merged, x[i])
			i++
			j++
		}
	}
	merged = append(merged, x[i:]...)
	return append(merged, y[j:]...)
}

// keepOnePrecedencePredicate removes all precedence predicates but the one with the lowest
// or highest precedence. They sort next to each other, by precedence.
func keepOnePrecedencePredicate(operands []SemanticContext, lowest bool) []SemanticContext {
	first, last := -1, -1
	for i, o := range operands {
		if _, ok := o.(*PrecedencePredicate); ok {
			if first < 0 {
				first = i
			}
			last = i
		}
	}
	if first < 0 {
		return operands
	}
	if !lowest {
		operands[first] = operands[last]
	}
	return append(operands[:first+1], operands[last+1:]...)
}

// A semantic context which is true whenever none of the contained contexts
// is false.`

type AND struct {
	opnds []SemanticContext
	hash  int
}

func NewAND(a, b SemanticContext) *AND {
	x := []SemanticContext{a}
	if aa, ok := a.(*AND); ok {
		x = aa.opnds
	}
	y := []SemanticContext{b}
	if ba, ok := b.(*AND); ok {
		y = ba.opnds
	}
	// interested in the transition with the lowest precedence
	opnds := keepOnePrecedencePredicate(mergeSemanticContexts(x, y), true)

	h := murmurInit(37) // Init with a value different from OR
	for _, op := range opnds {
		h = murmurUpdate(h, op.Hash())
	}

	return &AND{opnds: opnds, hash: murmurFinish(h, len(opnds))}
}

func (a *AND) Equals(other Collectable[SemanticContext]) bool {
	if a == other {
		return true
	}
	o, ok := other.(*AND)
	if !ok || a.hash != o.hash || len(a.opnds) != len(o.opnds) {
		return false
	}
	for i, v := range o.opnds {
		if !a.opnds[i].Equals(v) {
			return false
		}
	}
	return true
}

// {@inheritDoc}
//
// <p>
// The evaluation of predicates by a context is short-circuiting, but
// unordered.</p>
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

func (a *AND) Hash() int {
	return a.hash
}

func (o *OR) Hash() int {
	return o.hash
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

//
// A semantic context which is true whenever at least one of the contained
// contexts is true.
//

type OR struct {
	opnds []SemanticContext
	hash  int
}

func NewOR(a, b SemanticContext) *OR {
	x := []SemanticContext{a}
	if aa, ok := a.(*OR); ok {
		x = aa.opnds
	}
	y := []SemanticContext{b}
	if ba, ok := b.(*OR); ok {
		y = ba.opnds
	}
	// interested in the transition with the highest precedence
	opnds := keepOnePrecedencePredicate(mergeSemanticContexts(x, y), false)

	h := murmurInit(41) // Init with a value different from AND
	for _, op := range opnds {
		h = murmurUpdate(h, op.Hash())
	}

	return &OR{opnds: opnds, hash: murmurFinish(h, len(opnds))}
}

func (o *OR) Equals(other Collectable[SemanticContext]) bool {
	if o == other {
		return true
	}
	p, ok := other.(*OR)
	if !ok || o.hash != p.hash || len(o.opnds) != len(p.opnds) {
		return false
	}
	for i, v := range p.opnds {
		if !o.opnds[i].Equals(v) {
			return false
		}
	}
	return true
}

// <p>
// The evaluation of predicates by o context is short-circuiting, but
// unordered.</p>
func (o *OR) evaluate(parser Recognizer, outerContext RuleContext) bool {
	for i := 0; i < len(o.opnds); i++ {
		if o.opnds[i].evaluate(parser, outerContext) {
			return true
		}
	}
	return false
}

func (o *OR) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	differs := false
	operands := make([]SemanticContext, 0)
	for i := 0; i < len(o.opnds); i++ {
		context := o.opnds[i]
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
		return o
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

func (o *OR) String() string {
	s := ""

	for _, o := range o.opnds {
		s += "|| " + fmt.Sprint(o)
	}

	if len(s) > 3 {
		return s[0:3]
	}

	return s
}
