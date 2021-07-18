// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// RecognitionException is the root of the ANTLR exception hierarchy. In
// general, ANTLR tracks just 3 kinds of errors: prediction errors, failed
// predicate errors, and mismatched input errors. In each case, the parser knows
// where it is in the input, where it is in the ATN, the rule invocation stack,
// and what kind of problem occurred.
type RecognitionException interface {
	GetOffendingToken() Token
	GetMessage() string
	GetInputStream() IntStream
}

// BaseRecognitionException is the base implementation for RecognitionException.
type BaseRecognitionException struct {
	message    string
	recognizer Recognizer
	// The current Token when an error occurred. Since not all streams
	// support accessing symbols by index, we have to track the Token
	// instance itself.
	offendingToken Token
	offendingState int
	ctx            RuleContext
	input          IntStream
}

// NewBaseRecognitionException returns a new instance of RecognitionException.
func NewBaseRecognitionException(message string, recognizer Recognizer, input IntStream, ctx RuleContext) *BaseRecognitionException {
	t := &BaseRecognitionException{
		message:        message,
		recognizer:     recognizer,
		input:          input,
		ctx:            ctx,
		offendingToken: nil,
	}

	// Get the ATN state number the parser was in at the time the error
	// occurred. For NoViableAltException and
	// LexerNoViableAltException exceptions, this is the
	// DecisionState number. For others, it is the state whose outgoing
	// edge we couldn't Match.
	t.offendingState = -1
	if t.recognizer != nil {
		t.offendingState = t.recognizer.GetState()
	}

	return t
}

// GetMessage returns the message included with this exception.
func (b *BaseRecognitionException) GetMessage() string {
	return b.message
}

// GetOffendingToken returns the token that triggered this exception.
func (b *BaseRecognitionException) GetOffendingToken() Token {
	return b.offendingToken
}

// GetInputStream returns the stream that produced the offending token.
func (b *BaseRecognitionException) GetInputStream() IntStream {
	return b.input
}

// <p>If the state number is not known, b method returns -1.</p>

//
// Gets the set of input symbols which could potentially follow the
// previously Matched symbol at the time b exception was panicn.
//
// <p>If the set of expected tokens is not known and could not be computed,
// b method returns nil.</p>
//
// @return The set of token types that could potentially follow the current
// state in the ATN, or nil if the information is not available.
// /
func (b *BaseRecognitionException) getExpectedTokens() *IntervalSet {
	if b.recognizer != nil {
		return b.recognizer.GetATN().getExpectedTokens(b.offendingState, b.ctx)
	}

	return nil
}

func (b *BaseRecognitionException) String() string {
	return b.message
}

// LexerNoViableAltException represents that the lexer wasn't able to find a
// viable alternative to recognize the given input.
type LexerNoViableAltException struct {
	*BaseRecognitionException

	startIndex     int
	deadEndConfigs ATNConfigSet
}

// NewLexerNoViableAltException returns a new instance of
// LexerNoViableAltException.
func NewLexerNoViableAltException(lexer Lexer, input CharStream, startIndex int, deadEndConfigs ATNConfigSet) *LexerNoViableAltException {
	return &LexerNoViableAltException{
		BaseRecognitionException: NewBaseRecognitionException("", lexer, input, nil),
		startIndex:               startIndex,
		deadEndConfigs:           deadEndConfigs,
	}
}

func (l *LexerNoViableAltException) String() string {
	symbol := ""
	if l.startIndex >= 0 && l.startIndex < l.input.Size() {
		symbol = l.input.(CharStream).GetTextFromInterval(NewInterval(l.startIndex, l.startIndex))
	}
	return "LexerNoViableAltException" + symbol
}

// NoViableAltException indicates that the parser could not decide which of two
// or more paths to take based upon the remaining input. It tracks the starting
// token of the offending input and also knows where the parser was
// in the various paths when the error. Reported by ReportNoViableAlternative()
type NoViableAltException struct {
	*BaseRecognitionException

	// The token object at the start index the input stream might
	// not be buffering tokens so get a reference to it. (At the
	// time the error occurred, of course the stream needs to keep a
	// buffer all of the tokens but later we might not have access to those.)
	startToken     Token
	offendingToken Token
	ctx            ParserRuleContext
	// Which configurations did we try at input.Index() that couldn't Match
	// input.LT(1)?//
	deadEndConfigs ATNConfigSet
}

// NewNoViableAltException returns a new instance of NoViableAltException.
func NewNoViableAltException(recognizer Parser, input TokenStream, startToken Token, offendingToken Token, deadEndConfigs ATNConfigSet, ctx ParserRuleContext) *NoViableAltException {
	if ctx == nil {
		ctx = recognizer.GetParserRuleContext()
	}

	if offendingToken == nil {
		offendingToken = recognizer.GetCurrentToken()
	}

	if startToken == nil {
		startToken = recognizer.GetCurrentToken()
	}

	if input == nil {
		input = recognizer.GetInputStream().(TokenStream)
	}

	return &NoViableAltException{
		BaseRecognitionException: NewBaseRecognitionException("", recognizer, input, ctx),
		deadEndConfigs:           deadEndConfigs,
		startToken:               startToken,
		offendingToken:           offendingToken,
	}
}

// InputMisMatchException signifies any kind of mismatched input exceptions such
// as when the current input does not Match the expected token.
type InputMisMatchException struct {
	*BaseRecognitionException
}

// NewInputMisMatchException returns a new instance of InputMisMatchException.
func NewInputMisMatchException(recognizer Parser) *InputMisMatchException {
	i := &InputMisMatchException{
		BaseRecognitionException: NewBaseRecognitionException("", recognizer, recognizer.GetInputStream(), recognizer.GetParserRuleContext()),
	}
	i.offendingToken = recognizer.GetCurrentToken()

	return i
}

// FailedPredicateException represents that a semantic predicate failed during
// validation. Validation of predicates occurs when normally parsing the
// alternative just like Matching a token.
//
// Disambiguating predicate evaluation occurs when we test a predicate during
// prediction.
type FailedPredicateException struct {
	*BaseRecognitionException

	ruleIndex      int
	predicateIndex int
	predicate      string
}

// NewFailedPredicateException returns a new instance of
// FailedPredicateException.
func NewFailedPredicateException(recognizer Parser, predicate string, message string) *FailedPredicateException {

	f := &FailedPredicateException{}

	f.BaseRecognitionException = NewBaseRecognitionException(f.formatMessage(predicate, message), recognizer, recognizer.GetInputStream(), recognizer.GetParserRuleContext())

	s := recognizer.GetInterpreter().atn.states[recognizer.GetState()]
	trans := s.GetTransitions()[0]
	if trans2, ok := trans.(*PredicateTransition); ok {
		f.ruleIndex = trans2.ruleIndex
		f.predicateIndex = trans2.predIndex
	} else {
		f.ruleIndex = 0
		f.predicateIndex = 0
	}
	f.predicate = predicate
	f.offendingToken = recognizer.GetCurrentToken()

	return f
}

func (f *FailedPredicateException) formatMessage(predicate, message string) string {
	if message != "" {
		return message
	}

	return "failed predicate: {" + predicate + "}?"
}

// ParseCancellationException represents that the recognition is being
// cancelled.
type ParseCancellationException struct{}

// NewParseCancellationException returns a new instance of
// ParseCancellationException.
func NewParseCancellationException() *ParseCancellationException {
	//	Error.call(this)
	//	Error.captureStackTrace(this, ParseCancellationException)
	return &ParseCancellationException{}
}
