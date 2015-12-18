package antlr4

import (
				"fmt"
)

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.

type ErrorListener struct {

}

func NewErrorListener() *ErrorListener {
	return new(ErrorListener)
}

func (this *ErrorListener) syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException) {
}

func (this *ErrorListener) reportAmbiguity(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
}

func (this *ErrorListener) reportAttemptingFullContext(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {
}

func (this *ErrorListener) reportContextSensitivity(recognizer *Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
}

type ConsoleErrorListener struct {
	ErrorListener
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
func (this *ConsoleErrorListener) syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException) {
    fmt.Errorf("line " + line + ":" + column + " " + msg)
}

type ProxyErrorListener struct {
	ErrorListener
	delegates []ErrorListener
}

func NewProxyErrorListener(delegates []ErrorListener) *ConsoleErrorListener {
    if (delegates==nil) {
        panic("delegates is not provided")
    }
	l := new(ProxyErrorListener)
    l.delegates = delegates
	return l
}

func (this *ProxyErrorListener) syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException) {
    for _,d := range this.delegates {
		d.syntaxError(recognizer, offendingSymbol, line, column, msg, e)
	}
}

func (this *ProxyErrorListener) reportAmbiguity(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
	for _,d := range this.delegates {
		d.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
	}
}

func (this *ProxyErrorListener) reportAttemptingFullContext(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {
	for _,d := range this.delegates {
		d.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
	}
}

func (this *ProxyErrorListener) reportContextSensitivity(recognizer *Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
	for _,d := range this.delegates {
		d.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
	}
}







