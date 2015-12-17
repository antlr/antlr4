package tree

import (
	"antlr4"
)

// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var TreeINVALID_INTERVAL = antlr4.NewInterval(-1, -2)

type Tree struct {

}

func NewTree() *Tree {
	return new(Tree)
}

type SyntaxTree struct {
	Tree
}

func NewSyntaxTree() *SyntaxTree{
	Tree.call(this)
	return this
}

type ParseTree struct {
	SyntaxTree
}

func NewParseTree() *ParseTree{
	SyntaxTree.call(this)
	return this
}


type RuleNode struct {
	ParseTree
}

func NewRuleNode() *RuleNode{
	ParseTree.call(this)
	return this
}


type TerminalNode struct {
	ParseTree
}

func NewTerminalNode() *TerminalNode{
	ParseTree.call(this)
	return this
}



type ErrorNode struct {
	TerminalNode
}

func NewErrorNode() *ErrorNode{
	TerminalNode.call(this)
	return this
}

type ParseTreeVisitor struct {

}

func NewParseTreeVisitor() *ParseTreeVisitor {
	return new(ParseTreeVisitor)
}

func (this *ParseTreeVisitor) visit(ctx) {
	if (Utils.isArray(ctx)) {
		var self = this
		return ctx.map(function(child) { return visitAtom(self, child)})
	} else {
		return visitAtom(this, ctx)
	}
}

func visitAtom(visitor, ctx) {
	if (ctx.parser == nil) { //is terminal
		return
	}

	var name = ctx.parser.ruleNames[ctx.ruleIndex]
	var funcName = "visit" + Utils.titleCase(name)

	return visitor[funcName](ctx)
}

type ParseTreeListener struct {

}

func NewParseTreeListener() *ParseTreeListener {
	return new(ParseTreeListener)
}

func (this *ParseTreeListener) visitTerminal(node) {
}

func (this *ParseTreeListener) visitErrorNode(node) {
}

func (this *ParseTreeListener) enterEveryRule(node) {
}

func (this *ParseTreeListener) exitEveryRule(node) {
}

type TerminalNodeImpl struct {
	TerminalNode
	parentCtx *antlr4.RuleContext
	symbol
}

func TerminalNodeImpl(symbol) {
	tn := &TerminalNodeImpl{TerminalNode{}}
	tn.parentCtx = nil
	tn.symbol = symbol
	return tn
}

func (this *TerminalNodeImpl) getChild(i) {
	return nil
}

func (this *TerminalNodeImpl) getSymbol() {
	return this.symbol
}

func (this *TerminalNodeImpl) getParent() {
	return this.parentCtx
}

func (this *TerminalNodeImpl) getPayload() {
	return this.symbol
}

func (this *TerminalNodeImpl) getSourceInterval() {
	if (this.symbol == nil) {
		return INVALID_INTERVAL
	}
	var tokenIndex = this.symbol.tokenIndex
	return NewInterval(tokenIndex, tokenIndex)
}

func (this *TerminalNodeImpl) getChildCount() {
	return 0
}

func (this *TerminalNodeImpl) accept(visitor) {
	return visitor.visitTerminal(this)
}

func (this *TerminalNodeImpl) getText() {
	return this.symbol.text
}

func (this *TerminalNodeImpl) toString() string {
	if (this.symbol.tokenType == TokenEOF) {
		return "<EOF>"
	} else {
		return this.symbol.text
	}
}

// Represents a token that was consumed during resynchronization
// rather than during a valid match operation. For example,
// we will create this kind of a node during single token insertion
// and deletion as well as during "consume until error recovery set"
// upon no viable alternative exceptions.

func ErrorNodeImpl(token) {
	TerminalNodeImpl.call(this, token)
	return this
}



func (this *ErrorNodeImpl) isErrorNode() {
	return true
}

func (this *ErrorNodeImpl) accept(visitor) {
	return visitor.visitErrorNode(this)
}

type ParseTreeWalker struct {

}

func NewParseTreeWalker() *ParseTreeWalker{
	return this
}

func (this *ParseTreeWalker) walk(listener, t) {
	var errorNode = t instanceof ErrorNode ||
			(t.isErrorNode != nil && t.isErrorNode())
	if (errorNode) {
		listener.visitErrorNode(t)
	} else if _, ok := t.(TerminalNode); ok {
		listener.visitTerminal(t)
	} else {
		this.enterRule(listener, t)
		for i := 0 i < t.getChildCount() i++) {
			var child = t.getChild(i)
			this.walk(listener, child)
		}
		this.exitRule(listener, t)
	}
}
//
// The discovery of a rule node, involves sending two events: the generic
// {@link ParseTreeListener//enterEveryRule} and a
// {@link RuleContext}-specific event. First we trigger the generic and then
// the rule specific. We to them in reverse order upon finishing the node.
//
func (this *ParseTreeWalker) enterRule(listener, r) {
	var ctx = r.getRuleContext()
	listener.enterEveryRule(ctx)
	ctx.enterRule(listener)
}

func (this *ParseTreeWalker) exitRule(listener, r) {
	var ctx = r.getRuleContext()
	ctx.exitRule(listener)
	listener.exitEveryRule(ctx)
}

var ParseTreeWalkerDEFAULT = NewParseTreeWalker()
