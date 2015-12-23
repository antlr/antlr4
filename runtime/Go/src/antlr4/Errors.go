package antlr4

import ()

// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
//  3 kinds of errors: prediction errors, failed predicate errors, and
//  mismatched input errors. In each case, the parser knows where it is
//  in the input, where it is in the ATN, the rule invocation stack,
//  and what kind of problem occurred.

type IRecognitionException interface {
	getOffendingToken() *Token
	getMessage() string
}

type RecognitionException struct {
	message        string
	recognizer     IRecognizer
	offendingToken *Token
	offendingState int
	ctx            IRuleContext
	input          CharStream
}

func NewRecognitionException(message string, recognizer IRecognizer, input CharStream, ctx IRuleContext) *RecognitionException {

	// todo
	//	Error.call(this)
	//
	//	if (!!Error.captureStackTrace) {
	//        Error.captureStackTrace(this, RecognitionException)
	//	} else {
	//		var stack = NewError().stack
	//	}
	// TODO may be able to use - "runtime" func Stack(buf []byte, all bool) int

	t := new(RecognitionException)
	t.InitRecognitionException(message, recognizer, input, ctx)

	return t
}

func (t *RecognitionException) InitRecognitionException(message string, recognizer IRecognizer, input CharStream, ctx IRuleContext) {

	t.message = message
	t.recognizer = recognizer
	t.input = input
	t.ctx = ctx
	// The current {@link Token} when an error occurred. Since not all streams
	// support accessing symbols by index, we have to track the {@link Token}
	// instance itself.
	t.offendingToken = nil
	// Get the ATN state number the parser was in at the time the error
	// occurred. For {@link NoViableAltException} and
	// {@link LexerNoViableAltException} exceptions, this is the
	// {@link DecisionState} number. For others, it is the state whose outgoing
	// edge we couldn't match.
	t.offendingState = -1
	if t.recognizer != nil {
		t.offendingState = t.recognizer.getState()
	}
}

func (this *RecognitionException) getMessage() string {
	return this.message
}

func (this *RecognitionException) getOffendingToken() *Token {
	return this.offendingToken
}

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
func (this *RecognitionException) getExpectedTokens() *IntervalSet {
	if this.recognizer != nil {
		return this.recognizer.getATN().getExpectedTokens(this.offendingState, this.ctx)
	} else {
		return nil
	}
}

func (this *RecognitionException) toString() string {
	return this.message
}

type LexerNoViableAltException struct {
	RecognitionException

	startIndex     int
	deadEndConfigs *ATNConfigSet
}

func NewLexerNoViableAltException(lexer *Lexer, input CharStream, startIndex int,
	deadEndConfigs *ATNConfigSet) *LexerNoViableAltException {

	this := new(LexerNoViableAltException)

	this.InitRecognitionException("", lexer, input, nil)

	this.startIndex = startIndex
	this.deadEndConfigs = deadEndConfigs

	return this
}

func (this *LexerNoViableAltException) toString() string {
	var symbol = ""
	if this.startIndex >= 0 && this.startIndex < this.input.size() {
		symbol = this.input.getTextFromInterval(NewInterval(this.startIndex, this.startIndex))
	}
	return "LexerNoViableAltException" + symbol
}

type NoViableAltException struct {
	RecognitionException

	startToken     *Token
	offendingToken *Token
	ctx            IParserRuleContext
	deadEndConfigs *ATNConfigSet
}

// Indicates that the parser could not decide which of two or more paths
// to take based upon the remaining input. It tracks the starting token
// of the offending input and also knows where the parser was
// in the various paths when the error. Reported by reportNoViableAlternative()
//
func NewNoViableAltException(recognizer IParser, input CharStream, startToken *Token, offendingToken *Token, deadEndConfigs *ATNConfigSet, ctx IParserRuleContext) *NoViableAltException {

	if ctx == nil {
		ctx = recognizer.getParserRuleContext()
	}

	if offendingToken == nil {
		offendingToken = recognizer.getCurrentToken()
	}

	if startToken == nil {
		startToken = recognizer.getCurrentToken()
	}

	if input == nil {
		input = recognizer.getInputStream()
	}

	this := new(NoViableAltException)
	this.InitRecognitionException("", recognizer, input, ctx)

	// Which configurations did we try at input.index() that couldn't match
	// input.LT(1)?//
	this.deadEndConfigs = deadEndConfigs
	// The token object at the start index the input stream might
	// not be buffering tokens so get a reference to it. (At the
	// time the error occurred, of course the stream needs to keep a
	// buffer all of the tokens but later we might not have access to those.)
	this.startToken = startToken
	this.offendingToken = offendingToken

	return this
}

type InputMismatchException struct {
	RecognitionException
}

// This signifies any kind of mismatched input exceptions such as
// when the current input does not match the expected token.
//
func NewInputMismatchException(recognizer IParser) *InputMismatchException {

	this := new(InputMismatchException)
	this.InitRecognitionException("", recognizer, recognizer.getInputStream(), recognizer.getParserRuleContext())

	this.offendingToken = recognizer.getCurrentToken()

	return this

}

// A semantic predicate failed during validation. Validation of predicates
// occurs when normally parsing the alternative just like matching a token.
// Disambiguating predicate evaluation occurs when we test a predicate during
// prediction.

type FailedPredicateException struct {
	RecognitionException

	ruleIndex      int
	predicateIndex int
	predicate      string
}

func NewFailedPredicateException(recognizer *Parser, predicate string, message string) *FailedPredicateException {

	this := new(FailedPredicateException)

	this.InitRecognitionException(this.formatMessage(predicate, message), recognizer, recognizer.getInputStream(), recognizer._ctx)

	var s = recognizer.Interpreter.atn.states[recognizer.state]
	var trans = s.getTransitions()[0]
	if trans2, ok := trans.(*PredicateTransition); ok {
		this.ruleIndex = trans2.ruleIndex
		this.predicateIndex = trans2.predIndex
	} else {
		this.ruleIndex = 0
		this.predicateIndex = 0
	}
	this.predicate = predicate
	this.offendingToken = recognizer.getCurrentToken()

	return this
}

func (this *FailedPredicateException) formatMessage(predicate, message string) string {
	if message != "" {
		return message
	} else {
		return "failed predicate: {" + predicate + "}?"
	}
}

type ParseCancellationException struct {
}

func NewParseCancellationException() *ParseCancellationException {
	//	Error.call(this)
	//	Error.captureStackTrace(this, ParseCancellationException)
	return new(ParseCancellationException)
}
