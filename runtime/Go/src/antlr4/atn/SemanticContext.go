package atn

// A tree structure used to record the semantic context in which
//  an ATN configuration is valid.  It's either a single predicate,
//  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
//
//  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
//  {@link SemanticContext} within the scope of this outer class.</p>
//

var Set = require('./../Utils').Set

type SemanticContext struct {
	return this
}

// For context independent predicates, we evaluate them without a local
// context (i.e., nil context). That way, we can evaluate them without
// having to create proper rule-specific context during prediction (as
// opposed to the parser, which creates them naturally). In a practical
// sense, this avoids a cast exception from RuleContext to myruleContext.
//
// <p>For context dependent predicates, we must pass in a local context so that
// references such as $arg evaluate properly as _localctx.arg. We only
// capture context dependent predicates in the context in which we begin
// prediction, so we passed in the outer context here in case of context
// dependent predicate evaluation.</p>
//
func (this *SemanticContext) evaluate(parser, outerContext) {
}

//
// Evaluate the precedence predicates for the context and reduce the result.
//
// @param parser The parser instance.
// @param outerContext The current parser context object.
// @return The simplified semantic context after precedence predicates are
// evaluated, which will be one of the following values.
// <ul>
// <li>{@link //NONE}: if the predicate simplifies to {@code true} after
// precedence predicates are evaluated.</li>
// <li>{@code nil}: if the predicate simplifies to {@code false} after
// precedence predicates are evaluated.</li>
// <li>{@code this}: if the semantic context is not changed as a result of
// precedence predicate evaluation.</li>
// <li>A non-{@code nil} {@link SemanticContext}: the new simplified
// semantic context after precedence predicates are evaluated.</li>
// </ul>
//
func (this *SemanticContext) evalPrecedence(parser, outerContext) {
	return this
}

SemanticContext.andContext = function(a, b) {
	if (a == nil || a == SemanticContext.NONE) {
		return b
	}
	if (b == nil || b == SemanticContext.NONE) {
		return a
	}
	var result = new AND(a, b)
	if (result.opnds.length == 1) {
		return result.opnds[0]
	} else {
		return result
	}
}

SemanticContext.orContext = function(a, b) {
	if (a == nil) {
		return b
	}
	if (b == nil) {
		return a
	}
	if (a == SemanticContext.NONE || b == SemanticContext.NONE) {
		return SemanticContext.NONE
	}
	var result = new OR(a, b)
	if (result.opnds.length == 1) {
		return result.opnds[0]
	} else {
		return result
	}
}

func Predicate(ruleIndex, predIndex, isCtxDependent) {
	SemanticContext.call(this)
	this.ruleIndex = ruleIndex == undefined ? -1 : ruleIndex
	this.predIndex = predIndex == undefined ? -1 : predIndex
	this.isCtxDependent = isCtxDependent == undefined ? false : isCtxDependent // e.g., $i ref in pred
	return this
}

Predicate.prototype = Object.create(SemanticContext.prototype)
Predicate.prototype.constructor = Predicate

//The default {@link SemanticContext}, which is semantically equivalent to
//a predicate of the form {@code {true}?}.
//
SemanticContext.NONE = new Predicate()


func (this *Predicate) evaluate(parser, outerContext) {
	var localctx = this.isCtxDependent ? outerContext : nil
	return parser.sempred(localctx, this.ruleIndex, this.predIndex)
}

func (this *Predicate) hashString() {
	return "" + this.ruleIndex + "/" + this.predIndex + "/" + this.isCtxDependent
}

func (this *Predicate) equals(other) {
	if (this == other) {
		return true
	} else if (!(other instanceof Predicate)) {
		return false
	} else {
		return this.ruleIndex == other.ruleIndex &&
				this.predIndex == other.predIndex &&
				this.isCtxDependent == other.isCtxDependent
	}
}

func (this *Predicate) toString() {
	return "{" + this.ruleIndex + ":" + this.predIndex + "}?"
}

func PrecedencePredicate(precedence) {
	SemanticContext.call(this)
	this.precedence = precedence == undefined ? 0 : precedence
}

PrecedencePredicate.prototype = Object.create(SemanticContext.prototype)
PrecedencePredicate.prototype.constructor = PrecedencePredicate

func (this *PrecedencePredicate) evaluate(parser, outerContext) {
	return parser.precpred(outerContext, this.precedence)
}

func (this *PrecedencePredicate) evalPrecedence(parser, outerContext) {
	if (parser.precpred(outerContext, this.precedence)) {
		return SemanticContext.NONE
	} else {
		return nil
	}
}

func (this *PrecedencePredicate) compareTo(other) {
	return this.precedence - other.precedence
}

func (this *PrecedencePredicate) hashString() {
	return "31"
}

func (this *PrecedencePredicate) equals(other) {
	if (this == other) {
		return true
	} else if (!(other instanceof PrecedencePredicate)) {
		return false
	} else {
		return this.precedence == other.precedence
	}
}

func (this *PrecedencePredicate) toString() {
	return "{"+this.precedence+">=prec}?"
}



PrecedencePredicate.filterPrecedencePredicates = function(set) {
	var result = []
	set.values().map( function(context) {
		if (context instanceof PrecedencePredicate) {
			result.push(context)
		}
	})
	return result
}


// A semantic context which is true whenever none of the contained contexts
// is false.
//
func AND(a, b) {
	SemanticContext.call(this)
	var operands = new Set()
	if (a instanceof AND) {
		a.opnds.map(function(o) {
			operands.add(o)
		})
	} else {
		operands.add(a)
	}
	if (b instanceof AND) {
		b.opnds.map(function(o) {
			operands.add(o)
		})
	} else {
		operands.add(b)
	}
	var precedencePredicates = PrecedencePredicate.filterPrecedencePredicates(operands)
	if (precedencePredicates.length > 0) {
		// interested in the transition with the lowest precedence
		var reduced = nil
		precedencePredicates.map( function(p) {
			if(reduced==nil || p.precedence<reduced.precedence) {
				reduced = p
			}
		})
		operands.add(reduced)
	}
	this.opnds = operands.values()
	return this
}

AND.prototype = Object.create(SemanticContext.prototype)
AND.prototype.constructor = AND

func (this *AND) equals(other) {
	if (this == other) {
		return true
	} else if (!(other instanceof AND)) {
		return false
	} else {
		return this.opnds == other.opnds
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
func (this *AND) evaluate(parser, outerContext) {
	for (var i = 0 i < this.opnds.length i++) {
		if (!this.opnds[i].evaluate(parser, outerContext)) {
			return false
		}
	}
	return true
}

func (this *AND) evalPrecedence(parser, outerContext) {
	var differs = false
	var operands = []
	for (var i = 0 i < this.opnds.length i++) {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser, outerContext)
		differs |= (evaluated !== context)
		if (evaluated == nil) {
			// The AND context is false if any element is false
			return nil
		} else if (evaluated !== SemanticContext.NONE) {
			// Reduce the result by skipping true elements
			operands.push(evaluated)
		}
	}
	if (!differs) {
		return this
	}
	if (operands.length == 0) {
		// all elements were true, so the AND context is true
		return SemanticContext.NONE
	}
	var result = nil
	operands.map(function(o) {
		result = result == nil ? o : SemanticPredicate.andContext(result, o)
	})
	return result
}

func (this *AND) toString() {
	var s = ""
	this.opnds.map(function(o) {
		s += "&& " + o.toString()
	})
	return s.length > 3 ? s.slice(3) : s
}

//
// A semantic context which is true whenever at least one of the contained
// contexts is true.
//
func OR(a, b) {
	SemanticContext.call(this)
	var operands = new Set()
	if (a instanceof OR) {
		a.opnds.map(function(o) {
			operands.add(o)
		})
	} else {
		operands.add(a)
	}
	if (b instanceof OR) {
		b.opnds.map(function(o) {
			operands.add(o)
		})
	} else {
		operands.add(b)
	}

	var precedencePredicates = PrecedencePredicate.filterPrecedencePredicates(operands)
	if (precedencePredicates.length > 0) {
		// interested in the transition with the highest precedence
		var s = precedencePredicates.sort(function(a, b) {
			return a.compareTo(b)
		})
		var reduced = s[s.length-1]
		operands.add(reduced)
	}
	this.opnds = operands.values()
	return this
}

OR.prototype = Object.create(SemanticContext.prototype)
OR.prototype.constructor = OR

func (this *OR) constructor(other) {
	if (this == other) {
		return true
	} else if (!(other instanceof OR)) {
		return false
	} else {
		return this.opnds == other.opnds
	}
}

func (this *OR) hashString() {
	return "" + this.opnds + "/OR"
}

// <p>
// The evaluation of predicates by this context is short-circuiting, but
// unordered.</p>
//
func (this *OR) evaluate(parser, outerContext) {
	for (var i = 0 i < this.opnds.length i++) {
		if (this.opnds[i].evaluate(parser, outerContext)) {
			return true
		}
	}
	return false
}

func (this *OR) evalPrecedence(parser, outerContext) {
	var differs = false
	var operands = []
	for (var i = 0 i < this.opnds.length i++) {
		var context = this.opnds[i]
		var evaluated = context.evalPrecedence(parser, outerContext)
		differs |= (evaluated !== context)
		if (evaluated == SemanticContext.NONE) {
			// The OR context is true if any element is true
			return SemanticContext.NONE
		} else if (evaluated !== nil) {
			// Reduce the result by skipping false elements
			operands.push(evaluated)
		}
	}
	if (!differs) {
		return this
	}
	if (operands.length == 0) {
		// all elements were false, so the OR context is false
		return nil
	}
	var result = nil
	operands.map(function(o) {
		return result == nil ? o : SemanticContext.orContext(result, o)
	})
	return result
}

func (this *AND) toString() {
	var s = ""
	this.opnds.map(function(o) {
		s += "|| " + o.toString()
	})
	return s.length > 3 ? s.slice(3) : s
}




