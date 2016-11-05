package antlr

import (
	"fmt"
	"strconv"
)

// A tree structure used to record the semantic context in which
//  an ATN configuration is valid.  It's either a single predicate,
//  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
//
//  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
//  {@link SemanticContext} within the scope of this outer class.</p>
//

type SemanticContext interface {
	Comparable

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

func (p *Predicate) Hash() string {
	return strconv.Itoa(p.ruleIndex) + "/" + strconv.Itoa(p.predIndex) + "/" + fmt.Sprint(p.isCtxDependent)
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

func (p *PrecedencePredicate) Hash() string {
	return "31"
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

func (p *PrecedencePredicate) String() string {
	return "{" + strconv.Itoa(p.precedence) + ">=prec}?"
}

func PrecedencePredicatefilterPrecedencePredicates(set *Set) []*PrecedencePredicate {
	result := make([]*PrecedencePredicate, 0)

	for _, v := range set.values() {
		if c2, ok := v.(*PrecedencePredicate); ok {
			result = append(result, c2)
		}
	}

	return result
}

// A semantic context which is true whenever none of the contained contexts
// is false.`

type AND struct {
	opnds []SemanticContext
}

func NewAND(a, b SemanticContext) *AND {

	operands := NewSet(nil, nil)
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

	and := new(AND)
	and.opnds = opnds

	return and
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

func (a *AND) Hash() string {
	return fmt.Sprint(a.opnds) + "/AND"
}

//
// {@inheritDoc}
//
// <p>
// The evaluation of predicates by a context is short-circuiting, but
// unordered.</p>
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
}

func NewOR(a, b SemanticContext) *OR {

	operands := NewSet(nil, nil)
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

	o := new(OR)
	o.opnds = opnds

	return o
}

func (o *OR) equals(other interface{}) bool {
	if o == other {
		return true
	} else if _, ok := other.(*OR); !ok {
		return false
	} else {
		for i, v := range other.(*OR).opnds {
			if !o.opnds[i].equals(v) {
				return false
			}
		}
		return true
	}
}

func (o *OR) Hash() string {
	return fmt.Sprint(o.opnds) + "/OR"
}

// <p>
// The evaluation of predicates by o context is short-circuiting, but
// unordered.</p>
//
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
