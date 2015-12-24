package antlr4

import (
	"fmt"
	"strconv"
)

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.

type IErrorListener interface {
	SyntaxError(recognizer IRecognizer, offendingSymbol interface{}, line, column int, msg string, e IRecognitionException)
	ReportAmbiguity(recognizer IParser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet)
	ReportAttemptingFullContext(recognizer IParser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet)
	ReportContextSensitivity(recognizer IParser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet)
}

type DefaultErrorListener struct {
}

func NewErrorListener() *DefaultErrorListener {
	return new(DefaultErrorListener)
}

func (this *DefaultErrorListener) SyntaxError(recognizer IRecognizer, offendingSymbol interface{}, line, column int, msg string, e IRecognitionException) {
}

func (this *DefaultErrorListener) ReportAmbiguity(recognizer IParser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
}

func (this *DefaultErrorListener) ReportAttemptingFullContext(recognizer IParser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {
}

func (this *DefaultErrorListener) ReportContextSensitivity(recognizer IParser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
}

type ConsoleErrorListener struct {
	*DefaultErrorListener
}

func NewConsoleErrorListener() *ConsoleErrorListener {
	return new(ConsoleErrorListener)
}

//
// Provides a default instance of {@link ConsoleErrorListener}.
//
var ConsoleErrorListenerINSTANCE = NewConsoleErrorListener()

//
// {@inheritDoc}
//
// <p>
// This implementation prints messages to {@link System//err} containing the
// values of {@code line}, {@code charPositionInLine}, and {@code msg} using
// the following format.</p>
//
// <pre>
// line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
// </pre>
//
func (this *ConsoleErrorListener) SyntaxError(recognizer IRecognizer, offendingSymbol interface{}, line, column int, msg string, e IRecognitionException) {
	fmt.Errorf("line " + strconv.Itoa(line) + ":" + strconv.Itoa(column) + " " + msg)
}

type ProxyErrorListener struct {
	*DefaultErrorListener
	delegates []IErrorListener
}

func NewProxyErrorListener(delegates []IErrorListener) *ProxyErrorListener {
	if delegates == nil {
		panic("delegates is not provided")
	}
	l := new(ProxyErrorListener)
	l.delegates = delegates
	return l
}

func (this *ProxyErrorListener) SyntaxError(recognizer IRecognizer, offendingSymbol interface{}, line, column int, msg string, e IRecognitionException) {
	for _, d := range this.delegates {
		d.SyntaxError(recognizer, offendingSymbol, line, column, msg, e)
	}
}

func (this *ProxyErrorListener) ReportAmbiguity(recognizer IParser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
	}
}

func (this *ProxyErrorListener) ReportAttemptingFullContext(recognizer IParser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
	}
}

func (this *ProxyErrorListener) ReportContextSensitivity(recognizer IParser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
	}
}
