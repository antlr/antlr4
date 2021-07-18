// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
	"os"
	"strconv"
)

// ErrorListener provides an empty default implementation of ANTLRErrorListener.
// The default implementation of each method does nothing, but can be overridden
// as necessary.
type ErrorListener interface {
	SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException)
	ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *bitSet, configs ATNConfigSet)
	ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *bitSet, configs ATNConfigSet)
	ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet)
}

// DefaultErrorListener implements ErrorListener
type DefaultErrorListener struct {
}

// NewDefaultErrorListener returns a new instance of DefaultErrorListener.
func NewDefaultErrorListener() *DefaultErrorListener {
	return &DefaultErrorListener{}
}

// SyntaxError reports a syntax error in the source.
func (d *DefaultErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
}

// ReportAmbiguity reports a parsing ambiguity.
func (d *DefaultErrorListener) ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *bitSet, configs ATNConfigSet) {
}

// ReportAttemptingFullContext reports attempting a full context.
func (d *DefaultErrorListener) ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *bitSet, configs ATNConfigSet) {
}

// ReportContextSensitivity reports context sensitivity.
func (d *DefaultErrorListener) ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet) {
}

// ConsoleErrorListener extends DefaultErrorListener.
type ConsoleErrorListener struct {
	*DefaultErrorListener
}

// NewConsoleErrorListener returns a new instance of ConsoleErrorListener.
func NewConsoleErrorListener() *ConsoleErrorListener {
	return &ConsoleErrorListener{}
}

// ConsoleErrorListenerINSTANCE provides a default instance of ConsoleErrorListener.
var ConsoleErrorListenerINSTANCE = NewConsoleErrorListener()

// SyntaxError reports a syntax error in the source.
//
// This implementation prints messages to System//err containing the
// values of line, charPositionInLine, and msg using
// the following format.
//
//		line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
//
func (c *ConsoleErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
	fmt.Fprintln(os.Stderr, "line "+strconv.Itoa(line)+":"+strconv.Itoa(column)+" "+msg)
}

// ProxyErrorListener delegates it's calls to other error listeners.
type ProxyErrorListener struct {
	*DefaultErrorListener
	delegates []ErrorListener
}

// NewProxyErrorListener returns a new instance of ProxyErrorListener
func NewProxyErrorListener(delegates []ErrorListener) *ProxyErrorListener {
	if delegates == nil {
		panic("delegates is not provided")
	}
	return &ProxyErrorListener{delegates: delegates}
}

// SyntaxError reports a syntax error in the source.
func (p *ProxyErrorListener) SyntaxError(recognizer Recognizer, offendingSymbol interface{}, line, column int, msg string, e RecognitionException) {
	for _, d := range p.delegates {
		d.SyntaxError(recognizer, offendingSymbol, line, column, msg, e)
	}
}

// ReportAmbiguity reports a parsing ambiguity.
func (p *ProxyErrorListener) ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *bitSet, configs ATNConfigSet) {
	for _, d := range p.delegates {
		d.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
	}
}

// ReportAttemptingFullContext reports attempting a full context.
func (p *ProxyErrorListener) ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *bitSet, configs ATNConfigSet) {
	for _, d := range p.delegates {
		d.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
	}
}

// ReportContextSensitivity reports context sensitivity.
func (p *ProxyErrorListener) ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet) {
	for _, d := range p.delegates {
		d.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
	}
}
