//
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

// A tree structure used to record the semantic context in which
//  an ATN configuration is valid.  It's either a single predicate,
//  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
//
//  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
//  {@link SemanticContext} within the scope of this outer class.</p>
//


function SemanticContext() {
	return this;
}
//
// The default {@link SemanticContext}, which is semantically equivalent to
// a predicate of the form {@code {true}?}.
//
SemanticContext.NONE = new Predicate();

// For context independent predicates, we evaluate them without a local
// context (i.e., null context). That way, we can evaluate them without
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
SemanticContext.prototype.eval = function(parser, outerContext){
};

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
// <li>{@code null}: if the predicate simplifies to {@code false} after
// precedence predicates are evaluated.</li>
// <li>{@code this}: if the semantic context is not changed as a result of
// precedence predicate evaluation.</li>
// <li>A non-{@code null} {@link SemanticContext}: the new simplified
// semantic context after precedence predicates are evaluated.</li>
// </ul>
//
SemanticContext.prototype.evalPrecedence = function(parser, outerContext) {
	return this;
};


SemanticContext.andContext = function(a, b) {
    if (a === null || a === SemanticContext.NONE) {
        return b;
    }
    if (b === null || b === SemanticContext.NONE) {
        return a;
    }
    var result = new AND(a, b);
    if (result.opnds.length === 1) {
        return result.opnds[0];
    } else {
        return result;
    }
};

SemanticContext.orContext = function(a, b) {
    if (a ===null) {
        return b;
    }
    if (b === null) {
        return a;
    }
    if (a === SemanticContext.NONE || b === SemanticContext.NONE) {
        return SemanticContext.NONE;
    }
    var result = new OR(a, b);
    if (result.opnds.length === 1) {
        return result.opnds[0];
    } else {
        return result;
    }
};


function Predicate(ruleIndex, predIndex, isCtxDependent) {
	SemanticContext.call(this);
    this.ruleIndex = ruleIndex===undefined ? -1 : ruleIndex;
    this.predIndex = predIndex===undefined ? -1 : predIndex;
    this.isCtxDependent = isCtxDependent===undefined ? false : isCtxDependent; // e.g., $i ref in pred
    return this;
};

Predicate.prototype = Object.create(SemanticContext.prototype);
Predicate.prototype.constructor = Predicate;

Predicate.prototype.eval = function(parser, outerContext) {
    var localctx = this.isCtxDependent ? outerContext : null;
    return parser.sempred(localctx, this.ruleIndex, this.predIndex);
};

Predicate.prototype.hashCode = function() {
    return "" + this.ruleIndex + "/" + this.predIndex + "/" + this.isCtxDependent; // TODO hash
};

Predicate.prototype.equals = function(other) {
    if (this === other) {
        return true;
    } else if (!(other instanceof Predicate)) {
        return false;
    } else {
    	return this.ruleIndex === other.ruleIndex &&
           this.predIndex === other.predIndex &&
           this.isCtxDependent === other.isCtxDependent;
    }
};

Predicate.prototype.toString = function() {
    return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
};


class PrecedencePredicate(SemanticContext):

    def __init__(this, precedence=0):
        this.precedence = precedence

    def eval(this, parser, outerContext):
        return parser.precpred(outerContext, this.precedence)

    def evalPrecedence(this, parser, outerContext):
        if parser.precpred(outerContext, this.precedence):
            return SemanticContext.NONE
        else:
            return None

    def __cmp__(this, other):
        return this.precedence - other.precedence

    def __hash__(this):
        return 31

    def __eq__(this, other):
        if this is other:
            return True
        elif not isinstance(other, PrecedencePredicate):
            return False
        else:
            return this.precedence == other.precedence

def filterPrecedencePredicates(collection):
    result = []
    for context in collection:
        if isinstance(context, PrecedencePredicate):
            if result is None:
                result = []
            result.append(context)
    return result



// A semantic context which is true whenever none of the contained contexts
// is false.
//
class AND(SemanticContext):

    def __init__(this, a, b):
        operands = set()
        if isinstance( a, AND):
            for o in a.opnds:
                operands.add(o)
        else:
            operands.add(a)
        if isinstance( b, AND):
            for o in b.opnds:
                operands.add(o)
        else:
            operands.add(b)

        precedencePredicates = filterPrecedencePredicates(operands)
        if len(precedencePredicates)>0:
            // interested in the transition with the lowest precedence
            reduced = min(precedencePredicates)
            operands.add(reduced)

        this.opnds = [ o for o in operands ]

    def __eq__(this, other):
        if this is other:
            return True
        elif not isinstance(other, AND):
            return False
        else:
            return this.opnds == other.opnds

    def __hash__(this):
        return hash(str(this.opnds)+ "/AND")

    //
    // {@inheritDoc}
    //
    // <p>
    // The evaluation of predicates by this context is short-circuiting, but
    // unordered.</p>
    //
    def eval(this, parser, outerContext):
        for opnd in this.opnds:
            if not opnd.eval(parser, outerContext):
                return False
        return True

    def evalPrecedence(this, parser, outerContext):
        differs = False
        operands = []
        for context in this.opnds:
            evaluated = context.evalPrecedence(parser, outerContext)
            differs |= evaluated is not context
            if evaluated is None:
                // The AND context is false if any element is false
                return None
            elif evaluated is not SemanticContext.NONE:
                // Reduce the result by skipping true elements
                operands.append(evaluated)

        if not differs:
            return this

        if len(operands)==0:
            // all elements were true, so the AND context is true
            return SemanticContext.NONE

        result = None
        for o in operands:
            result = o if result is None else andContext(result, o)

        return result

    def __unicode__(this):
        with StringIO() as buf:
            first = True
            for o in this.opnds:
                if not first:
                    buf.write(u"&&")
                buf.write(unicode(o))
                first = False
            return buf.getvalue()

//
// A semantic context which is true whenever at least one of the contained
// contexts is true.
//
class OR (SemanticContext):

    def __init__(this, a, b):
        operands = set()
        if isinstance( a, OR):
            for o in a.opnds:
                operands.add(o)
        else:
            operands.add(a);
        if isinstance( b, OR):
            for o in b.opnds:
                operands.add(o)
        else:
            operands.add(b)

        precedencePredicates = filterPrecedencePredicates(operands)
        if len(precedencePredicates)>0:
            // interested in the transition with the highest precedence
            s = sorted(precedencePredicates)
            reduced = s[len(s)-1]
            operands.add(reduced)

        this.opnds = [ o for o in operands ]

    def __eq__(this, other):
        if this is other:
            return True
        elif not isinstance(other, OR):
            return False
        else:
            return this.opnds == other.opnds

    def __hash__(this):
        return hash(str(this.opnds)+"/OR")

    // <p>
    // The evaluation of predicates by this context is short-circuiting, but
    // unordered.</p>
    //
    def eval(this, parser, outerContext):
        for opnd in this.opnds:
            if opnd.eval(parser, outerContext):
                return True
        return False

    def evalPrecedence(this, parser, outerContext):
        differs = False
        operands = []
        for context in this.opnds:
            evaluated = context.evalPrecedence(parser, outerContext);
            differs |= evaluated is not context
            if evaluated is SemanticContext.NONE:
                // The OR context is true if any element is true
                return SemanticContext.NONE
            elif evaluated is not None:
                // Reduce the result by skipping false elements
                operands.append(evaluated)

        if not differs:
            return this

        if len(operands)==0:
            // all elements were false, so the OR context is false
            return None

        result = None
        for o in operands:
            result = o if result is None else orContext(result, o)

        return result

    def __unicode__(this):
        with StringIO() as buf:
            first = True
            for o in this.opnds:
                if not first:
                    buf.write(u"||")
                buf.write(unicode(o))
                first = False
            return buf.getvalue()

