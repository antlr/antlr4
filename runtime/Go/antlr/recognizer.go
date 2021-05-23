// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
	"strconv"
)

// Recognizer is the base type for generated lexers and parsers.
type Recognizer interface {
	GetLiteralNames() []string
	GetSymbolicNames() []string
	GetRuleNames() []string

	Sempred(RuleContext, int, int) bool
	Precpred(RuleContext, int) bool

	GetState() int
	SetState(int)
	Action(RuleContext, int, int)
	AddErrorListener(ErrorListener)
	RemoveErrorListeners()
	GetATN() *ATN
	GetErrorListenerDispatch() ErrorListener
}

// BaseRecognizer is the base implementation for Recognizer.
type BaseRecognizer struct {
	listeners []ErrorListener
	state     int

	RuleNames       []string
	LiteralNames    []string
	SymbolicNames   []string
	GrammarFileName string
}

// NewBaseRecognizer returns a new instance of BaseRecognizer
func NewBaseRecognizer() *BaseRecognizer {
	return &BaseRecognizer{
		listeners: []ErrorListener{ConsoleErrorListenerINSTANCE},
		state:     -1,
	}
}

var tokenTypeMapCache = make(map[string]int)
var ruleIndexMapCache = make(map[string]int)

func (b *BaseRecognizer) checkVersion(toolVersion string) {
	runtimeVersion := "4.9.2"
	if runtimeVersion != toolVersion {
		fmt.Println("ANTLR runtime and generated code versions disagree: " + runtimeVersion + "!=" + toolVersion)
	}
}

// Action does nothing by default.
func (b *BaseRecognizer) Action(context RuleContext, ruleIndex, actionIndex int) {
	panic("action not implemented on Recognizer!")
}

// AddErrorListener subscribes the given listener to this recognizer.
func (b *BaseRecognizer) AddErrorListener(listener ErrorListener) {
	b.listeners = append(b.listeners, listener)
}

// RemoveErrorListeners removes all the listeners from this recognizer.
func (b *BaseRecognizer) RemoveErrorListeners() {
	b.listeners = b.listeners[:0]
}

// GetRuleNames returns the names for the rules in this recognizer.
func (b *BaseRecognizer) GetRuleNames() []string {
	return b.RuleNames
}

// GetTokenNames returns the literal names contained in this recognizer.
func (b *BaseRecognizer) GetTokenNames() []string {
	return b.LiteralNames
}

// GetSymbolicNames returns the symbolic names contained in this recognizer.
func (b *BaseRecognizer) GetSymbolicNames() []string {
	return b.SymbolicNames
}

// GetLiteralNames returns the literal names contained in this recognizer.
func (b *BaseRecognizer) GetLiteralNames() []string {
	return b.LiteralNames
}

// GetState returns the current state of this recognizer.
func (b *BaseRecognizer) GetState() int {
	return b.state
}

// SetState sets the current state of this recognizer.
func (b *BaseRecognizer) SetState(v int) {
	b.state = v
}

// GetRuleIndexMap returns a map from rule names to rule indexes.
//
// By default it's not implemented.
//
// Used for XPath and tree pattern compilation.
func (b *BaseRecognizer) GetRuleIndexMap() map[string]int {
	panic("Method not defined!")
}

// GetTokenType does nothing by default.
func (b *BaseRecognizer) GetTokenType(tokenName string) int {
	panic("Method not defined!")
}

// GetErrorHeader returns what the error header is, normally line/character
// position information
func (b *BaseRecognizer) GetErrorHeader(e RecognitionException) string {
	line := e.GetOffendingToken().GetLine()
	column := e.GetOffendingToken().GetColumn()
	return "line " + strconv.Itoa(line) + ":" + strconv.Itoa(column)
}

// GetErrorListenerDispatch returns a new proxy error listener.
func (b *BaseRecognizer) GetErrorListenerDispatch() ErrorListener {
	return NewProxyErrorListener(b.listeners)
}

// subclass needs to override these if there are sempreds or actions
// that the ATN interp needs to execute

// Sempred always returns true.
func (b *BaseRecognizer) Sempred(localctx RuleContext, ruleIndex int, actionIndex int) bool {
	return true
}

// Precpred always returns true.
func (b *BaseRecognizer) Precpred(localctx RuleContext, precedence int) bool {
	return true
}
