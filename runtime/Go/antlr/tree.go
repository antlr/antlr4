// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var TreeInvalidInterval = NewInterval(-1, -2)

type Tree interface {
	GetParent() Tree
	SetParent(Tree)
	GetPayload() interface{}
	GetChild(i int) Tree
	GetChildCount() int
	GetChildren() []Tree

	VisitFunc(nv NodeVisitor, tv ParserTreeVisitorHandlers, args ...interface{}) interface{}
}

type SyntaxTree interface {
	Tree

	GetSourceInterval() *Interval
}

type ParseTree interface {
	SyntaxTree

	Visit(visitor ParseTreeVisitor, args ...interface{}) interface{}
	GetText() string

	ToStringTree([]string, Recognizer) string
}

type RuleNode interface {
	ParseTree

	GetRuleContext() RuleContext
	GetBaseRuleContext() *BaseRuleContext
}

type TerminalNode interface {
	ParseTree

	GetSymbol() Token
}

type ErrorNode interface {
	TerminalNode

	errorNode()
}

type ParserTreeVisitorHandlers interface {
}

type ParseTreeVisitor interface {
	VisitTerminal(node TerminalNode)
	VisitErrorNode(node ErrorNode)
	VisitChildren(node RuleNode, delegate ParseTreeVisitor, args ...interface{}) (result interface{})
}
type NodeVisitor interface {
	TerminalVisitor
	ErrorNodeVisitor
}
type TerminalVisitor interface {
	VisitTerminal(node TerminalNode)
}
type ErrorNodeVisitor interface {
	VisitErrorNode(node ErrorNode)
}
type AggregateResultVisitor interface {
	AggregateResult(aggregate, nextResult interface{}) (result interface{})
}
type VisitNextCheck interface {
	VisitNext(next Tree, currentResult interface{}) bool
}
type VisitRestCheck interface {
	VisitRest(next RuleNode, currentResult interface{}) bool
}
type EnterEveryRuleVisitor interface {
	EnterEveryRule(ctx RuleNode)
}
type ExitEveryRuleVisitor interface {
	ExitEveryRule(ctx RuleNode)
}

// grammar Example;
// a : b ;
// b : 'c' # Y;

// in somefile.go
//go:generate java -jar path/antlr.har -o parser -package parser -visitor Example.g4

// -- generate example_visitor.go
// type AContextVisitor interface {
// 	VisitA(ctx IAContext, delegate antlr.ParseTreeVisitor, args ...interface{}) (result interface{})
// }
// type YContextVisitor interface {
// 	VisitY(ctx IYContext, delegate antlr.ParseTreeVisitor, args ...interface{}) (result interface{})
// }

// -- implemented visitor
// func (v *MyV) VisitA(ctx parser.IAContext, delegate antlr.ParseTreeVisitor, args ...interface{}) (result interface{}) {
// 	return
// }
// func (v *MyV) VisitY(ctx parser.IYContext, delegate antlr.ParseTreeVisitor, args ...interface{}) (result interface{}) {
// 	return
// }

type BaseParseTreeVisitor struct{}

func (*BaseParseTreeVisitor) VisitTerminal(node TerminalNode) {}
func (*BaseParseTreeVisitor) VisitErrorNode(node ErrorNode)   {}

func (*BaseParseTreeVisitor) VisitChildren(node RuleNode, delegate ParseTreeVisitor, args ...interface{}) interface{} {
	next, isNextCk := delegate.(VisitNextCheck)
	rest, isRestCk := delegate.(VisitRestCheck)
	entryV, isEnterV := delegate.(EnterEveryRuleVisitor)
	exitV, isExitEV := delegate.(ExitEveryRuleVisitor)
	aggre, isAggre := delegate.(AggregateResultVisitor)
	var result interface{}
	for _, child := range node.GetChildren() {
		if isNextCk && !next.VisitNext(child, result) {
			continue
		}
		switch child := child.(type) {
		case TerminalNode:
			delegate.VisitTerminal(child)
		case ErrorNode:
			delegate.VisitErrorNode(child)
		case RuleNode:
			if isRestCk && !rest.VisitRest(child, result) {
				break
			}
			if isEnterV {
				entryV.EnterEveryRule(child)
			}
			r := child.Visit(delegate, args...)
			if isExitEV {
				exitV.ExitEveryRule(child)
			}
			if isAggre {
				result = aggre.AggregateResult(result, r)
			} else {
				result = r
			}
		default:
			// can this happen??
		}
	}
	return result
}

type ParseTreeListener interface {
	VisitTerminal(node TerminalNode)
	VisitErrorNode(node ErrorNode)
	EnterEveryRule(ctx ParserRuleContext)
	ExitEveryRule(ctx ParserRuleContext)
}

type BaseParseTreeListener struct{}

var _ ParseTreeListener = &BaseParseTreeListener{}

func (*BaseParseTreeListener) VisitTerminal(node TerminalNode)      {}
func (*BaseParseTreeListener) VisitErrorNode(node ErrorNode)        {}
func (*BaseParseTreeListener) EnterEveryRule(ctx ParserRuleContext) {}
func (*BaseParseTreeListener) ExitEveryRule(ctx ParserRuleContext)  {}

type TerminalNodeImpl struct {
	parentCtx RuleContext

	symbol Token
}

var _ TerminalNode = &TerminalNodeImpl{}

func NewTerminalNodeImpl(symbol Token) *TerminalNodeImpl {
	tn := new(TerminalNodeImpl)

	tn.parentCtx = nil
	tn.symbol = symbol

	return tn
}

func (t *TerminalNodeImpl) VisitFunc(nv NodeVisitor, x ParserTreeVisitorHandlers, args ...interface{}) interface{} {
	return nil
}

func (t *TerminalNodeImpl) GetChild(i int) Tree {
	return nil
}

func (t *TerminalNodeImpl) GetChildren() []Tree {
	return nil
}

func (t *TerminalNodeImpl) SetChildren(tree []Tree) {
	panic("Cannot set children on terminal node")
}

func (t *TerminalNodeImpl) GetSymbol() Token {
	return t.symbol
}

func (t *TerminalNodeImpl) GetParent() Tree {
	return t.parentCtx
}

func (t *TerminalNodeImpl) SetParent(tree Tree) {
	t.parentCtx = tree.(RuleContext)
}

func (t *TerminalNodeImpl) GetPayload() interface{} {
	return t.symbol
}

func (t *TerminalNodeImpl) GetSourceInterval() *Interval {
	if t.symbol == nil {
		return TreeInvalidInterval
	}
	tokenIndex := t.symbol.GetTokenIndex()
	return NewInterval(tokenIndex, tokenIndex)
}

func (t *TerminalNodeImpl) GetChildCount() int {
	return 0
}

func (t *TerminalNodeImpl) Visit(v ParseTreeVisitor, args ...interface{}) interface{} {
	v.VisitTerminal(t)
	return nil
}

func (t *TerminalNodeImpl) GetText() string {
	return t.symbol.GetText()
}

func (t *TerminalNodeImpl) String() string {
	if t.symbol.GetTokenType() == TokenEOF {
		return "<EOF>"
	}

	return t.symbol.GetText()
}

func (t *TerminalNodeImpl) ToStringTree(s []string, r Recognizer) string {
	return t.String()
}

// Represents a token that was consumed during reSynchronization
// rather than during a valid Match operation. For example,
// we will create this kind of a node during single token insertion
// and deletion as well as during "consume until error recovery set"
// upon no viable alternative exceptions.

type ErrorNodeImpl struct {
	*TerminalNodeImpl
}

var _ ErrorNode = &ErrorNodeImpl{}

func NewErrorNodeImpl(token Token) *ErrorNodeImpl {
	en := new(ErrorNodeImpl)
	en.TerminalNodeImpl = NewTerminalNodeImpl(token)
	return en
}

func (e *ErrorNodeImpl) errorNode() {}

func (e *ErrorNodeImpl) Visit(v ParseTreeVisitor, args ...interface{}) interface{} {
	v.VisitErrorNode(e)
	return nil
}

type ParseTreeWalker struct {
}

func NewParseTreeWalker() *ParseTreeWalker {
	return new(ParseTreeWalker)
}

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

//
// The discovery of a rule node, involves sending two events: the generic
// {@link ParseTreeListener//EnterEveryRule} and a
// {@link RuleContext}-specific event. First we trigger the generic and then
// the rule specific. We to them in reverse order upon finishing the node.
//
func (p *ParseTreeWalker) EnterRule(listener ParseTreeListener, r RuleNode) {
	ctx := r.GetRuleContext().(ParserRuleContext)
	listener.EnterEveryRule(ctx)
	ctx.EnterRule(listener)
}

func (p *ParseTreeWalker) ExitRule(listener ParseTreeListener, r RuleNode) {
	ctx := r.GetRuleContext().(ParserRuleContext)
	ctx.ExitRule(listener)
	listener.ExitEveryRule(ctx)
}

var ParseTreeWalkerDefault = NewParseTreeWalker()
