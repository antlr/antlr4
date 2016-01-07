package antlr4

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
	evaluate(parser Recognizer, outerContext RuleContext) bool
	evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext
	equals(interface{}) bool
	String() string
}

func SemanticContextandContext(a, b SemanticContext) SemanticContext {
	if a == nil || a == SemanticContextNone {
		return b
	}
	if b == nil || b == SemanticContextNone {
		return a
	}
	var result = NewAND(a, b)
	if len(result.opnds) == 1 {
		return result.opnds[0]
	} else {
		return result
	}
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
	var result = NewOR(a, b)
	if len(result.opnds) == 1 {
		return result.opnds[0]
	} else {
		return result
	}
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

func (this *Predicate) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	return this
}

func (this *Predicate) evaluate(parser Recognizer, outerContext RuleContext) bool {

	var localctx RuleContext = nil

	if this.isCtxDependent {
		localctx = outerContext
	}

	return parser.Sempred(localctx, this.ruleIndex, this.predIndex)
}

func (this *Predicate) Hash() string {
	return strconv.Itoa(this.ruleIndex) + "/" + strconv.Itoa(this.predIndex) + "/" + fmt.Sprint(this.isCtxDependent)
}

func (this *Predicate) equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*Predicate); !ok {
		return false
	} else {
		return this.ruleIndex == other.(*Predicate).ruleIndex &&
			this.predIndex == other.(*Predicate).predIndex &&
			this.isCtxDependent == other.(*Predicate).isCtxDependent
	}
}

func (this *Predicate) String() string {
	return "{" + strconv.Itoa(this.ruleIndex) + ":" + strconv.Itoa(this.predIndex) + "}?"
}

type PrecedencePredicate struct {
	precedence int
}

func NewPrecedencePredicate(precedence int) *PrecedencePredicate {

	this := new(PrecedencePredicate)
	this.precedence = precedence

	return this
}

func (this *PrecedencePredicate) evaluate(parser Recognizer, outerContext RuleContext) bool {
	return parser.Precpred(outerContext, this.precedence)
}

func (this *PrecedencePredicate) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	if parser.Precpred(outerContext, this.precedence) {
		return SemanticContextNone
	} else {
		return nil
	}
}

func (this *PrecedencePredicate) compareTo(other *PrecedencePredicate) int {
	return this.precedence - other.precedence
}

func (this *PrecedencePredicate) Hash() string {
	return "31"
}

func (this *PrecedencePredicate) equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*PrecedencePredicate); !ok {
		return false
	} else {
		return this.precedence == other.(*PrecedencePredicate).precedence
	}
}

func (this *PrecedencePredicate) String() string {
	return "{" + strconv.Itoa(this.precedence) + ">=prec}?"
}

func PrecedencePredicatefilterPrecedencePredicates(set *Set) []*PrecedencePredicate {
	var result = make([]*PrecedencePredicate, 0)

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

	var operands = NewSet(nil, nil)
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
	if len(precedencePredicates) > 0 {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate = nil

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
		vs[i] = v.(SemanticContext)
	}

	this := new(AND)
	this.opnds = opnds

	return this
}

func (this *AND) equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*AND); !ok {
		return false
	} else {
		for i, v := range other.(*AND).opnds {
			if !this.opnds[i].equals(v) {
				return false
			}
		}
		return true
	}
}

func (this *AND) Hash() string {
	return fmt.Sprint(this.opnds) + "/AND"
}

//
// {@inheritDoc}
//
// <p>
// The evaluation of predicates by this context is short-circuiting, but
// unordered.</p>
//
func (this *AND) evaluate(parser Recognizer, outerContext RuleContext) bool {
	for i := 0; i < len(this.opnds); i++ {
		if !this.opnds[i].evaluate(parser, outerContext) {
			return false
		}
	}
	return true
}

func (this *AND) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	var differs = false
	var operands = make([]SemanticContext, 0)

	for i := 0; i < len(this.opnds); i++ {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser, outerContext)
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
		return this
	}

	if len(operands) == 0 {
		// all elements were true, so the AND context is true
		return SemanticContextNone
	}

	var result SemanticContext = nil

	for _, o := range operands {
		if result == nil {
			result = o
		} else {
			result = SemanticContextandContext(result, o)
		}
	}

	return result
}

func (this *AND) String() string {
	var s = ""

	for _, o := range this.opnds {
		s += "&& " + o.String()
	}

	if len(s) > 3 {
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
	opnds []SemanticContext
}

func NewOR(a, b SemanticContext) *OR {
	var operands = NewSet(nil, nil)
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
	if len(precedencePredicates) > 0 {
		// interested in the transition with the lowest precedence
		var reduced *PrecedencePredicate = nil

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
		vs[i] = v.(SemanticContext)
	}

	this := new(OR)
	this.opnds = opnds

	return this
}

func (this *OR) equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*OR); !ok {
		return false
	} else {
		for i, v := range other.(*OR).opnds {
			if !this.opnds[i].equals(v) {
				return false
			}
		}
		return true
	}
}

func (this *OR) Hash() string {
	return fmt.Sprint(this.opnds) + "/OR"
}

// <p>
// The evaluation of predicates by this context is short-circuiting, but
// unordered.</p>
//
func (this *OR) evaluate(parser Recognizer, outerContext RuleContext) bool {
	for i := 0; i < len(this.opnds); i++ {
		if this.opnds[i].evaluate(parser, outerContext) {
			return true
		}
	}
	return false
}

func (this *OR) evalPrecedence(parser Recognizer, outerContext RuleContext) SemanticContext {
	var differs = false
	var operands = make([]SemanticContext, 0)
	for i := 0; i < len(this.opnds); i++ {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser, outerContext)
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
		return this
	}
	if len(operands) == 0 {
		// all elements were false, so the OR context is false
		return nil
	}
	var result SemanticContext = nil

	for _, o := range operands {
		if result == nil {
			result = o
		} else {
			result = SemanticContextorContext(result, o)
		}
	}

	return result
}

func (this *OR) String() string {
	var s = ""

	for _, o := range this.opnds {
		s += "|| " + o.String()
	}

	if len(s) > 3 {
		return s[0:3]
	} else {
		return s
	}
}
