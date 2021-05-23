// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"reflect"
	"strconv"
)

// ParserRuleContext represents the nodes of the tree generated by the parser.
type ParserRuleContext interface {
	RuleContext

	SetException(RecognitionException)

	AddTokenNode(token Token) *TerminalNodeImpl
	AddErrorNode(badToken Token) *ErrorNodeImpl

	EnterRule(listener ParseTreeListener)
	ExitRule(listener ParseTreeListener)

	SetStart(Token)
	GetStart() Token

	SetStop(Token)
	GetStop() Token

	AddChild(child RuleContext) RuleContext
	RemoveLastChild()
}

// BaseParserRuleContext is the default implementation of ParserRuleContext.
type BaseParserRuleContext struct {
	*BaseRuleContext

	// The exception that forced prc rule to return. If the rule successfully
	// completed, prc is nil.
	exception   RecognitionException
	start, stop Token
	children    []Tree
}

// NewBaseParserRuleContext returns a new instance of BaseParserRuleContext
func NewBaseParserRuleContext(parent ParserRuleContext, invokingStateNumber int) *BaseParserRuleContext {
	prc := &BaseParserRuleContext{
		BaseRuleContext: NewBaseRuleContext(parent, invokingStateNumber),
		children:        nil,
		start:           nil,
		stop:            nil,
		exception:       nil,
	}

	prc.RuleIndex = -1
	// * If we are debugging or building a parse tree for a Visitor,
	// we need to track all of the tokens and rule invocations associated
	// with prc rule's context. This is empty for parsing w/o tree constr.
	// operation because we don't the need to track the details about
	// how we parse prc rule.
	// /

	return prc
}

// SetException TODO: docs.
func (prc *BaseParserRuleContext) SetException(e RecognitionException) {
	prc.exception = e
}

// GetChildren returns this rule's children.
func (prc *BaseParserRuleContext) GetChildren() []Tree {
	return prc.children
}

// CopyFrom copies the information from the given node into this one.
func (prc *BaseParserRuleContext) CopyFrom(ctx *BaseParserRuleContext) {
	// from RuleContext
	prc.parentCtx = ctx.parentCtx
	prc.invokingState = ctx.invokingState
	prc.children = nil
	prc.start = ctx.start
	prc.stop = ctx.stop
}

// GetText returns the text in this node.
func (prc *BaseParserRuleContext) GetText() string {
	if prc.GetChildCount() == 0 {
		return ""
	}

	var s string
	for _, child := range prc.children {
		s += child.(ParseTree).GetText()
	}

	return s
}

// Double dispatch methods for listeners

// EnterRule executes when entering this node.
func (prc *BaseParserRuleContext) EnterRule(listener ParseTreeListener) {}

// ExitRule executes when exiting this node.
func (prc *BaseParserRuleContext) ExitRule(listener ParseTreeListener) {}

// * Does not set parent link other add methods do that///
func (prc *BaseParserRuleContext) addTerminalNodeChild(child TerminalNode) TerminalNode {
	if prc.children == nil {
		prc.children = make([]Tree, 0)
	}
	if child == nil {
		panic("Child may not be null")
	}
	prc.children = append(prc.children, child)
	return child
}

// AddChild adds the given rule to this node's children.
func (prc *BaseParserRuleContext) AddChild(child RuleContext) RuleContext {
	if prc.children == nil {
		prc.children = make([]Tree, 0)
	}
	if child == nil {
		panic("Child may not be null")
	}
	prc.children = append(prc.children, child)
	return child
}

// RemoveLastChild is used by EnterOuterAlt to toss out a RuleContext previously
// added as we entered a rule. If we have label, we will need to remove
// generic ruleContext object.
func (prc *BaseParserRuleContext) RemoveLastChild() {
	if prc.children != nil && len(prc.children) > 0 {
		prc.children = prc.children[0 : len(prc.children)-1]
	}
}

// AddTokenNode to this one's children.
func (prc *BaseParserRuleContext) AddTokenNode(token Token) *TerminalNodeImpl {
	node := NewTerminalNodeImpl(token)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node
}

// AddErrorNode to this one's children.
func (prc *BaseParserRuleContext) AddErrorNode(badToken Token) *ErrorNodeImpl {
	node := NewErrorNodeImpl(badToken)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node
}

// GetChild returns this node's i-th child.
func (prc *BaseParserRuleContext) GetChild(i int) Tree {
	if prc.children != nil && len(prc.children) >= i {
		return prc.children[i]
	}

	return nil
}

// GetChildOfType returns the i-th child of the given type.
func (prc *BaseParserRuleContext) GetChildOfType(i int, childType reflect.Type) RuleContext {
	if childType == nil {
		return prc.GetChild(i).(RuleContext)
	}

	for j := 0; j < len(prc.children); j++ {
		child := prc.children[j]
		if reflect.TypeOf(child) == childType {
			if i == 0 {
				return child.(RuleContext)
			}

			i--
		}
	}

	return nil
}

// ToStringTree returns the lisp-like representation of this node and it's
// children.
func (prc *BaseParserRuleContext) ToStringTree(ruleNames []string, recog Recognizer) string {
	return TreesStringTree(prc, ruleNames, recog)
}

// GetRuleContext returns the node itself.
func (prc *BaseParserRuleContext) GetRuleContext() RuleContext {
	return prc
}

// Accept TODO: docs.
func (prc *BaseParserRuleContext) Accept(visitor ParseTreeVisitor) interface{} {
	return visitor.VisitChildren(prc)
}

// SetStart of this node's range.
func (prc *BaseParserRuleContext) SetStart(t Token) {
	prc.start = t
}

// GetStart of this node's range.
func (prc *BaseParserRuleContext) GetStart() Token {
	return prc.start
}

// SetStop of this node's range.
func (prc *BaseParserRuleContext) SetStop(t Token) {
	prc.stop = t
}

// GetStop of this node's range.
func (prc *BaseParserRuleContext) GetStop() Token {
	return prc.stop
}

// GetToken returns the i-th terminal of the given token type.
func (prc *BaseParserRuleContext) GetToken(ttype int, i int) TerminalNode {

	for j := 0; j < len(prc.children); j++ {
		child := prc.children[j]
		if c2, ok := child.(TerminalNode); ok {
			if c2.GetSymbol().GetTokenType() == ttype {
				if i == 0 {
					return c2
				}

				i--
			}
		}
	}
	return nil
}

// GetTokens returns all the terminals in this node's children.
func (prc *BaseParserRuleContext) GetTokens(ttype int) []TerminalNode {
	if prc.children == nil {
		return make([]TerminalNode, 0)
	}

	tokens := make([]TerminalNode, 0)

	for j := 0; j < len(prc.children); j++ {
		child := prc.children[j]
		if tchild, ok := child.(TerminalNode); ok {
			if tchild.GetSymbol().GetTokenType() == ttype {
				tokens = append(tokens, tchild)
			}
		}
	}

	return tokens
}

// GetPayload TODO: docs.
func (prc *BaseParserRuleContext) GetPayload() interface{} {
	return prc
}

func (prc *BaseParserRuleContext) getChild(ctxType reflect.Type, i int) RuleContext {
	if prc.children == nil || i < 0 || i >= len(prc.children) {
		return nil
	}

	j := -1 // what element have we found with ctxType?
	for _, o := range prc.children {

		childType := reflect.TypeOf(o)

		if childType.Implements(ctxType) {
			j++
			if j == i {
				return o.(RuleContext)
			}
		}
	}
	return nil
}

// Go lacks generics, so it's not possible for us to return the child with the correct type, but we do
// check for convertibility

// GetTypedRuleContext TODO: docs.
func (prc *BaseParserRuleContext) GetTypedRuleContext(ctxType reflect.Type, i int) RuleContext {
	return prc.getChild(ctxType, i)
}

// GetTypedRuleContexts TODO: docs.
func (prc *BaseParserRuleContext) GetTypedRuleContexts(ctxType reflect.Type) []RuleContext {
	if prc.children == nil {
		return make([]RuleContext, 0)
	}

	contexts := make([]RuleContext, 0)

	for _, child := range prc.children {
		childType := reflect.TypeOf(child)

		if childType.ConvertibleTo(ctxType) {
			contexts = append(contexts, child.(RuleContext))
		}
	}
	return contexts
}

// GetChildCount returns how many children this node has.
func (prc *BaseParserRuleContext) GetChildCount() int {
	if prc.children == nil {
		return 0
	}

	return len(prc.children)
}

// GetSourceInterval TODO: docs.
func (prc *BaseParserRuleContext) GetSourceInterval() *Interval {
	if prc.start == nil || prc.stop == nil {
		return TreeInvalidInterval
	}

	return NewInterval(prc.start.GetTokenIndex(), prc.stop.GetTokenIndex())
}

//need to manage circular dependencies, so export now

// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if b is a leaf.
//

func (prc *BaseParserRuleContext) String(ruleNames []string, stop RuleContext) string {

	var p ParserRuleContext = prc
	s := "["
	for p != nil && p != stop {
		if ruleNames == nil {
			if !p.IsEmpty() {
				s += strconv.Itoa(p.GetInvokingState())
			}
		} else {
			ri := p.GetRuleIndex()
			var ruleName string
			if ri >= 0 && ri < len(ruleNames) {
				ruleName = ruleNames[ri]
			} else {
				ruleName = strconv.Itoa(ri)
			}
			s += ruleName
		}
		if p.GetParent() != nil && (ruleNames != nil || !p.GetParent().(ParserRuleContext).IsEmpty()) {
			s += " "
		}
		pi := p.GetParent()
		if pi != nil {
			p = pi.(ParserRuleContext)
		} else {
			p = nil
		}
	}
	s += "]"
	return s
}

// RuleContextEmpty TODO: docs.
var RuleContextEmpty = NewBaseParserRuleContext(nil, -1)

// InterpreterRuleContext TODO: docs.
type InterpreterRuleContext interface {
	ParserRuleContext
}

// BaseInterpreterRuleContext TODO: docs.
type BaseInterpreterRuleContext struct {
	*BaseParserRuleContext
}

// NewBaseInterpreterRuleContext returns a new instance of
// BaseInterpreterRuleContext.
func NewBaseInterpreterRuleContext(parent BaseInterpreterRuleContext, invokingStateNumber, ruleIndex int) *BaseInterpreterRuleContext {
	prc := &BaseInterpreterRuleContext{
		BaseParserRuleContext: NewBaseParserRuleContext(parent, invokingStateNumber),
	}

	prc.RuleIndex = ruleIndex

	return prc
}
