package antlr4

import (
	"fmt"
	"strconv"
	"os"
)

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.

type ErrorListener interface {
	SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException)
	ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs ATNConfigSet)
	ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs ATNConfigSet)
	ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet)
}

type DefaultErrorListener struct {
}

func NewDefaultErrorListener() *DefaultErrorListener {
	return new(DefaultErrorListener)
}

func (this *DefaultErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
	if PortDebug {
		fmt.Println("SyntaxError!")
	}
}

func (this *DefaultErrorListener) ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs ATNConfigSet) {
	if PortDebug {
		fmt.Println("ReportAmbiguity!")
	}
}

func (this *DefaultErrorListener) ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs ATNConfigSet) {
	if PortDebug {
		fmt.Println("ReportAttemptingFullContext!")
	}
}

func (this *DefaultErrorListener) ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet) {
	if PortDebug {
		fmt.Println("ReportContextSensitivity!")
	}
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
func (this *ConsoleErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
	fmt.Fprintln(os.Stderr, "line " + strconv.Itoa(line) + ":" + strconv.Itoa(column) + " " + msg)
}

type ProxyErrorListener struct {
	*DefaultErrorListener
	delegates []ErrorListener
}

func NewProxyErrorListener(delegates []ErrorListener) *ProxyErrorListener {
	if delegates == nil {
		panic("delegates is not provided")
	}
	l := new(ProxyErrorListener)
	l.delegates = delegates
	return l
}

func (this *ProxyErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
	for _, d := range this.delegates {
		d.SyntaxError(recognizer, offendingSymbol, line, column, msg, e)
	}
}

func (this *ProxyErrorListener) ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
	}
}

func (this *ProxyErrorListener) ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
	}
}

func (this *ProxyErrorListener) ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet) {
	for _, d := range this.delegates {
		d.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
	}
}
