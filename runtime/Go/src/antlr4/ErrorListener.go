package antlr4

import (
				"fmt"
	"strconv"
)

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.


type IErrorListener interface {
	syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException)
	reportAmbiguity(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet)
	reportAttemptingFullContext(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet)
	reportContextSensitivity(recognizer *Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet)
}

type DefaultErrorListener struct {

}

func NewErrorListener() *DefaultErrorListener {
	return new(DefaultErrorListener)
}

func (this *DefaultErrorListener) syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException) {
}

func (this *DefaultErrorListener) reportAmbiguity(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
}

func (this *DefaultErrorListener) reportAttemptingFullContext(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {
}

func (this *DefaultErrorListener) reportContextSensitivity(recognizer *Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
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
func (this *ConsoleErrorListener) syntaxError(recognizer *Parser, offendingSymbol interface{}, line, column int, msg string, e *RecognitionException) {
    fmt.Errorf("line " + strconv.Itoa(line) + ":" + strconv.Itoa(column) + " " + msg)
}

type ProxyErrorListener struct {
	*DefaultErrorListener
	delegates []IErrorListener
}

func NewProxyErrorListener(delegates []IErrorListener) *ProxyErrorListener {
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







