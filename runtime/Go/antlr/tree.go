// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// The basic notion of a tree has a parent, a payload, and a list of children.
///

// TreeInvalidInterval represents a nonexistent interval whithin the input.
var TreeInvalidInterval = NewInterval(-1, -2)

// Tree is the most abstract interface for all the trees used by ANTLR.
type Tree interface {
	GetParent() Tree
	SetParent(Tree)
	GetPayload() interface{}
	GetChild(i int) Tree
	GetChildCount() int
	GetChildren() []Tree
}

// SyntaxTree implements Tree.
type SyntaxTree interface {
	Tree

	GetSourceInterval() *Interval
}

// ParseTree implements SyntaxTree.
type ParseTree interface {
	SyntaxTree

	Accept(Visitor ParseTreeVisitor) interface{}
	GetText() string

	ToStringTree([]string, Recognizer) string
}

// RuleNode implements ParseTree for non-terminal rules.
type RuleNode interface {
	ParseTree

	GetRuleContext() RuleContext
	GetBaseRuleContext() *BaseRuleContext
}

// TerminalNode implements ParseTree for terminals.
type TerminalNode interface {
	ParseTree

	GetSymbol() Token
}

// ErrorNode implements TerminalNode for errors.
type ErrorNode interface {
	TerminalNode

	errorNode()
}

// ParseTreeVisitor is the base type for tree visitors.
type ParseTreeVisitor interface {
	Visit(tree ParseTree) interface{}
	VisitChildren(node RuleNode) interface{}
	VisitTerminal(node TerminalNode) interface{}
	VisitErrorNode(node ErrorNode) interface{}
}

// BaseParseTreeVisitor is the base implementation of ParseTreeVisitor.
type BaseParseTreeVisitor struct{}

var _ ParseTreeVisitor = &BaseParseTreeVisitor{}

// Visit visits the given parse tree.
func (v *BaseParseTreeVisitor) Visit(tree ParseTree) interface{} { return nil }

// VisitChildren visits the node's children.
func (v *BaseParseTreeVisitor) VisitChildren(node RuleNode) interface{} { return nil }

// VisitTerminal visits a terminal node.
func (v *BaseParseTreeVisitor) VisitTerminal(node TerminalNode) interface{} { return nil }

// VisitErrorNode visits an error node.
func (v *BaseParseTreeVisitor) VisitErrorNode(node ErrorNode) interface{} { return nil }

// TODO
//func (this ParseTreeVisitor) Visit(ctx) {
//	if (Utils.isArray(ctx)) {
//		self := this
//		return ctx.map(function(child) { return VisitAtom(self, child)})
//	} else {
//		return VisitAtom(this, ctx)
//	}
//}
//
//func VisitAtom(Visitor, ctx) {
//	if (ctx.parser == nil) { //is terminal
//		return
//	}
//
//	name := ctx.parser.ruleNames[ctx.ruleIndex]
//	funcName := "Visit" + Utils.titleCase(name)
//
//	return Visitor[funcName](ctx)
//}

// ParseTreeListener listens to the ParseTreeWalker.
type ParseTreeListener interface {
	VisitTerminal(node TerminalNode)
	VisitErrorNode(node ErrorNode)
	EnterEveryRule(ctx ParserRuleContext)
	ExitEveryRule(ctx ParserRuleContext)
}

// BaseParseTreeListener is the base type for any parse tree listener.
type BaseParseTreeListener struct{}

var _ ParseTreeListener = &BaseParseTreeListener{}

// VisitTerminal visits a terminal node.
func (l *BaseParseTreeListener) VisitTerminal(node TerminalNode) {}

// VisitErrorNode visits an error node.
func (l *BaseParseTreeListener) VisitErrorNode(node ErrorNode) {}

// EnterEveryRule is called before visiting each rule.
func (l *BaseParseTreeListener) EnterEveryRule(ctx ParserRuleContext) {}

// ExitEveryRule is called after visiting each rule.
func (l *BaseParseTreeListener) ExitEveryRule(ctx ParserRuleContext) {}

// TerminalNodeImpl implements TerminalNode.
type TerminalNodeImpl struct {
	parentCtx RuleContext

	symbol Token
}

var _ TerminalNode = &TerminalNodeImpl{}

// NewTerminalNodeImpl returns a new instance of TerminalNodeImpl.
func NewTerminalNodeImpl(symbol Token) *TerminalNodeImpl {
	return &TerminalNodeImpl{
		parentCtx: nil,
		symbol:    symbol,
	}
}

// GetChild returns nil. Terminal nodes cannot have children.
func (t *TerminalNodeImpl) GetChild(i int) Tree {
	return nil
}

// GetChildren returns nil. Terminal nodes cannot have children.
func (t *TerminalNodeImpl) GetChildren() []Tree {
	return nil
}

// SetChildren always panics. Terminal nodes cannot have children.
func (t *TerminalNodeImpl) SetChildren(tree []Tree) {
	panic("Cannot set children on terminal node")
}

// GetSymbol returns this node's symbol
func (t *TerminalNodeImpl) GetSymbol() Token {
	return t.symbol
}

// GetParent returns this node's parent.
func (t *TerminalNodeImpl) GetParent() Tree {
	return t.parentCtx
}

// SetParent sets this node's parent.
func (t *TerminalNodeImpl) SetParent(tree Tree) {
	t.parentCtx = tree.(RuleContext)
}

// GetPayload returns this node's symbol.
func (t *TerminalNodeImpl) GetPayload() interface{} {
	return t.symbol
}

// GetSourceInterval returns the interval that this node covers.
func (t *TerminalNodeImpl) GetSourceInterval() *Interval {
	if t.symbol == nil {
		return TreeInvalidInterval
	}
	tokenIndex := t.symbol.GetTokenIndex()
	return NewInterval(tokenIndex, tokenIndex)
}

// GetChildCount always returns 0.
func (t *TerminalNodeImpl) GetChildCount() int {
	return 0
}

// Accept wraps around VisitTerminal.
func (t *TerminalNodeImpl) Accept(v ParseTreeVisitor) interface{} {
	return v.VisitTerminal(t)
}

// GetText returns the text whithin this node's token.
func (t *TerminalNodeImpl) GetText() string {
	return t.symbol.GetText()
}

// String implements the Stringer interface
func (t *TerminalNodeImpl) String() string {
	if t.symbol.GetTokenType() == TokenEOF {
		return "<EOF>"
	}

	return t.symbol.GetText()
}

// ToStringTree wraps around String
func (t *TerminalNodeImpl) ToStringTree(s []string, r Recognizer) string {
	return t.String()
}

// ErrorNodeImpl represents a token that was consumed during reSynchronization
// rather than during a valid Match operation. For example,
// we will create this kind of a node during single token insertion
// and deletion as well as during "consume until error recovery set"
// upon no viable alternative exceptions.
type ErrorNodeImpl struct{ *TerminalNodeImpl }

var _ ErrorNode = &ErrorNodeImpl{}

// NewErrorNodeImpl returns a new instance of ErrorNodeImpl.
func NewErrorNodeImpl(token Token) *ErrorNodeImpl {
	return &ErrorNodeImpl{
		TerminalNodeImpl: NewTerminalNodeImpl(token),
	}
}

func (e *ErrorNodeImpl) errorNode() {}

// Accept visits this node.
func (e *ErrorNodeImpl) Accept(v ParseTreeVisitor) interface{} {
	return v.VisitErrorNode(e)
}

// ParseTreeWalker walks a given parse tree recursively.
type ParseTreeWalker struct{}

// NewParseTreeWalker returns a new instance of ParseTreeWalker.
func NewParseTreeWalker() *ParseTreeWalker {
	return &ParseTreeWalker{}
}

// Walk performs a walk on the given parse tree starting at the root and going
// down recursively with depth-first search. On each node, EnterRule is called
// before recursively walking down into child nodes, then ExitRule is called
// after the recursive call to wind up.
func (p *ParseTreeWalker) Walk(listener ParseTreeListener, t Tree) {
	switch tt := t.(type) {
	case ErrorNode:
		listener.VisitErrorNode(tt)
	case TerminalNode:
		listener.VisitTerminal(tt)
	default:
		p.EnterRule(listener, t.(RuleNode))
		for i := 0; i < t.GetChildCount(); i++ {
			child := t.GetChild(i)
			p.Walk(listener, child)
		}
		p.ExitRule(listener, t.(RuleNode))
	}
}

// EnterRule enters a grammar rule by first triggering the generic event ParseTreeListener//EnterEveryRule
// then by triggering the event specific to the given parse tree node
func (p *ParseTreeWalker) EnterRule(listener ParseTreeListener, r RuleNode) {
	ctx := r.GetRuleContext().(ParserRuleContext)
	listener.EnterEveryRule(ctx)
	ctx.EnterRule(listener)
}

// ExitRule exits a grammar rule by first triggering the event specific to the
// given parse tree node then by triggering the generic event ParseTreeListener//ExitEveryRule
func (p *ParseTreeWalker) ExitRule(listener ParseTreeListener, r RuleNode) {
	ctx := r.GetRuleContext().(ParserRuleContext)
	ctx.ExitRule(listener)
	listener.ExitEveryRule(ctx)
}

// ParseTreeWalkerDefault can be used to walk a parse tree without having to
// instance it first.
var ParseTreeWalkerDefault = NewParseTreeWalker()
