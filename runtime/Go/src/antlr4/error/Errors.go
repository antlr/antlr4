package error

// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
//  3 kinds of errors: prediction errors, failed predicate errors, and
//  mismatched input errors. In each case, the parser knows where it is
//  in the input, where it is in the ATN, the rule invocation stack,
//  and what kind of problem occurred.

var PredicateTransition = require('./../atn/Transition').PredicateTransition

func RecognitionException(params) {
	Error.call(this)
	if (!!Error.captureStackTrace) {
        Error.captureStackTrace(this, RecognitionException)
	} else {
		var stack = NewError().stack
	}
	this.message = params.message
    this.recognizer = params.recognizer
    this.input = params.input
    this.ctx = params.ctx
    // The current {@link Token} when an error occurred. Since not all streams
    // support accessing symbols by index, we have to track the {@link Token}
    // instance itself.
    this.offendingToken = nil
    // Get the ATN state number the parser was in at the time the error
    // occurred. For {@link NoViableAltException} and
    // {@link LexerNoViableAltException} exceptions, this is the
    // {@link DecisionState} number. For others, it is the state whose outgoing
    // edge we couldn't match.
    this.offendingState = -1
    if (this.recognizer!=nil) {
        this.offendingState = this.recognizer.state
    }
    return this
}

RecognitionException.prototype = Object.create(Error.prototype)
RecognitionException.prototype.constructor = RecognitionException

// <p>If the state number is not known, this method returns -1.</p>

//
// Gets the set of input symbols which could potentially follow the
// previously matched symbol at the time this exception was panicn.
//
// <p>If the set of expected tokens is not known and could not be computed,
// this method returns {@code nil}.</p>
//
// @return The set of token types that could potentially follow the current
// state in the ATN, or {@code nil} if the information is not available.
// /
func (this *RecognitionException) getExpectedTokens() {
    if (this.recognizer!=nil) {
        return this.recognizer.atn.getExpectedTokens(this.offendingState, this.ctx)
    } else {
        return nil
    }
}

func (this *RecognitionException) toString() {
    return this.message
}

func LexerNoViableAltException(lexer, input, startIndex, deadEndConfigs) {
	RecognitionException.call(this, {message:"", recognizer:lexer, input:input, ctx:nil})
    this.startIndex = startIndex
    this.deadEndConfigs = deadEndConfigs
    return this
}

LexerNoViableAltException.prototype = Object.create(RecognitionException.prototype)
LexerNoViableAltException.prototype.constructor = LexerNoViableAltException

func (this *LexerNoViableAltException) toString() {
    var symbol = ""
    if (this.startIndex >= 0 && this.startIndex < this.input.size) {
        symbol = this.input.getText((this.startIndex,this.startIndex))
    }
    return "LexerNoViableAltException" + symbol
}

// Indicates that the parser could not decide which of two or more paths
// to take based upon the remaining input. It tracks the starting token
// of the offending input and also knows where the parser was
// in the various paths when the error. Reported by reportNoViableAlternative()
//
func NoViableAltException(recognizer, input, startToken, offendingToken, deadEndConfigs, ctx) {
	ctx = ctx || recognizer._ctx
	offendingToken = offendingToken || recognizer.getCurrentToken()
	startToken = startToken || recognizer.getCurrentToken()
	input = input || recognizer.getInputStream()
	RecognitionException.call(this, {message:"", recognizer:recognizer, input:input, ctx:ctx})
    // Which configurations did we try at input.index() that couldn't match
	// input.LT(1)?//
    this.deadEndConfigs = deadEndConfigs
    // The token object at the start index the input stream might
    // not be buffering tokens so get a reference to it. (At the
    // time the error occurred, of course the stream needs to keep a
    // buffer all of the tokens but later we might not have access to those.)
    this.startToken = startToken
    this.offendingToken = offendingToken
}

NoViableAltException.prototype = Object.create(RecognitionException.prototype)
NoViableAltException.prototype.constructor = NoViableAltException

// This signifies any kind of mismatched input exceptions such as
// when the current input does not match the expected token.
//
func InputMismatchException(recognizer) {
	RecognitionException.call(this, {message:"", recognizer:recognizer, input:recognizer.getInputStream(), ctx:recognizer._ctx})
    this.offendingToken = recognizer.getCurrentToken()
}

InputMismatchException.prototype = Object.create(RecognitionException.prototype)
InputMismatchException.prototype.constructor = InputMismatchException

// A semantic predicate failed during validation. Validation of predicates
// occurs when normally parsing the alternative just like matching a token.
// Disambiguating predicate evaluation occurs when we test a predicate during
// prediction.

func FailedPredicateException(recognizer, predicate, message) {
	RecognitionException.call(this, {message:this.formatMessage(predicate,message || nil), recognizer:recognizer,
                         input:recognizer.getInputStream(), ctx:recognizer._ctx})
    var s = recognizer._interp.atn.states[recognizer.state]
    var trans = s.transitions[0]
    if (trans instanceof PredicateTransition) {
        this.ruleIndex = trans.ruleIndex
        this.predicateIndex = trans.predIndex
    } else {
        this.ruleIndex = 0
        this.predicateIndex = 0
    }
    this.predicate = predicate
    this.offendingToken = recognizer.getCurrentToken()
    return this
}

FailedPredicateException.prototype = Object.create(RecognitionException.prototype)
FailedPredicateException.prototype.constructor = FailedPredicateException

func (this *FailedPredicateException) formatMessage(predicate, message) {
    if (message !=nil) {
        return message
    } else {
        return "failed predicate: {" + predicate + "}?"
    }
}

type ParseCancellationException struct {
	Error.call(this)
	Error.captureStackTrace(this, ParseCancellationException)
	return this
}

ParseCancellationException.prototype = Object.create(Error.prototype)
ParseCancellationException.prototype.constructor = ParseCancellationException






