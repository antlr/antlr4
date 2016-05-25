package antlr

// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
//  3 kinds of errors: prediction errors, failed predicate errors, and
//  mismatched input errors. In each case, the parser knows where it is
//  in the input, where it is in the ATN, the rule invocation stack,
//  and what kind of problem occurred.

type RecognitionException interface {
	GetOffendingToken() Token
	GetMessage() string
	GetInputStream() IntStream
}

type BaseRecognitionException struct {
	message        string
	recognizer     Recognizer
	offendingToken Token
	offendingState int
	ctx            RuleContext
	input          IntStream
}

func NewBaseRecognitionException(message string, recognizer Recognizer, input IntStream, ctx RuleContext) *BaseRecognitionException {

	// todo
	//	Error.call(this)
	//
	//	if (!!Error.captureStackTrace) {
	//        Error.captureStackTrace(this, RecognitionException)
	//	} else {
	//		var stack = NewError().stack
	//	}
	// TODO may be able to use - "runtime" func Stack(buf []byte, all bool) int

	t := new(BaseRecognitionException)

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
	// edge we couldn't Match.
	t.offendingState = -1
	if t.recognizer != nil {
		t.offendingState = t.recognizer.GetState()
	}

	return t
}

func (this *BaseRecognitionException) GetMessage() string {
	return this.message
}

func (this *BaseRecognitionException) GetOffendingToken() Token {
	return this.offendingToken
}

func (this *BaseRecognitionException) GetInputStream() IntStream {
	return this.input
}

// <p>If the state number is not known, this method returns -1.</p>

//
// Gets the set of input symbols which could potentially follow the
// previously Matched symbol at the time this exception was panicn.
//
// <p>If the set of expected tokens is not known and could not be computed,
// this method returns {@code nil}.</p>
//
// @return The set of token types that could potentially follow the current
// state in the ATN, or {@code nil} if the information is not available.
// /
func (this *BaseRecognitionException) getExpectedTokens() *IntervalSet {
	if this.recognizer != nil {
		return this.recognizer.GetATN().getExpectedTokens(this.offendingState, this.ctx)
	} else {
		return nil
	}
}

func (this *BaseRecognitionException) String() string {
	return this.message
}

type LexerNoViableAltException struct {
	*BaseRecognitionException

	startIndex     int
	deadEndConfigs ATNConfigSet
}

func NewLexerNoViableAltException(lexer Lexer, input CharStream, startIndex int, deadEndConfigs ATNConfigSet) *LexerNoViableAltException {

	this := new(LexerNoViableAltException)

	this.BaseRecognitionException = NewBaseRecognitionException("", lexer, input, nil)

	this.startIndex = startIndex
	this.deadEndConfigs = deadEndConfigs

	return this
}

func (this *LexerNoViableAltException) String() string {
	var symbol = ""
	if this.startIndex >= 0 && this.startIndex < this.input.Size() {
		symbol = this.input.(CharStream).GetTextFromInterval(NewInterval(this.startIndex, this.startIndex))
	}
	return "LexerNoViableAltException" + symbol
}

type NoViableAltException struct {
	*BaseRecognitionException

	startToken     Token
	offendingToken Token
	ctx            ParserRuleContext
	deadEndConfigs ATNConfigSet
}

// Indicates that the parser could not decide which of two or more paths
// to take based upon the remaining input. It tracks the starting token
// of the offending input and also knows where the parser was
// in the various paths when the error. Reported by ReportNoViableAlternative()
//
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

	this := new(NoViableAltException)
	this.BaseRecognitionException = NewBaseRecognitionException("", recognizer, input, ctx)

	// Which configurations did we try at input.Index() that couldn't Match
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

type InputMisMatchException struct {
	*BaseRecognitionException
}

// This signifies any kind of mismatched input exceptions such as
// when the current input does not Match the expected token.
//
func NewInputMisMatchException(recognizer Parser) *InputMisMatchException {

	this := new(InputMisMatchException)
	this.BaseRecognitionException = NewBaseRecognitionException("", recognizer, recognizer.GetInputStream(), recognizer.GetParserRuleContext())

	this.offendingToken = recognizer.GetCurrentToken()

	return this

}

// A semantic predicate failed during validation. Validation of predicates
// occurs when normally parsing the alternative just like Matching a token.
// Disambiguating predicate evaluation occurs when we test a predicate during
// prediction.

type FailedPredicateException struct {
	*BaseRecognitionException

	ruleIndex      int
	predicateIndex int
	predicate      string
}

func NewFailedPredicateException(recognizer Parser, predicate string, message string) *FailedPredicateException {

	this := new(FailedPredicateException)

	this.BaseRecognitionException = NewBaseRecognitionException(this.formatMessage(predicate, message), recognizer, recognizer.GetInputStream(), recognizer.GetParserRuleContext())

	var s = recognizer.GetInterpreter().atn.states[recognizer.GetState()]
	var trans = s.GetTransitions()[0]
	if trans2, ok := trans.(*PredicateTransition); ok {
		this.ruleIndex = trans2.ruleIndex
		this.predicateIndex = trans2.predIndex
	} else {
		this.ruleIndex = 0
		this.predicateIndex = 0
	}
	this.predicate = predicate
	this.offendingToken = recognizer.GetCurrentToken()

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
