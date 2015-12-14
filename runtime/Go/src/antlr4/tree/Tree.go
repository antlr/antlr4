package tree

// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var Token = require('./../Token').Token
var Interval = require('./../IntervalSet').Interval
var INVALID_INTERVAL = new Interval(-1, -2)
var Utils = require('../Utils.js')


type Tree struct {
	return this
}

type SyntaxTree struct {
	Tree.call(this)
	return this
}

SyntaxTree.prototype = Object.create(Tree.prototype)
SyntaxTree.prototype.constructor = SyntaxTree

type ParseTree struct {
	SyntaxTree.call(this)
	return this
}

ParseTree.prototype = Object.create(SyntaxTree.prototype)
ParseTree.prototype.constructor = ParseTree

type RuleNode struct {
	ParseTree.call(this)
	return this
}

RuleNode.prototype = Object.create(ParseTree.prototype)
RuleNode.prototype.constructor = RuleNode

type TerminalNode struct {
	ParseTree.call(this)
	return this
}

TerminalNode.prototype = Object.create(ParseTree.prototype)
TerminalNode.prototype.constructor = TerminalNode

type ErrorNode struct {
	TerminalNode.call(this)
	return this
}

ErrorNode.prototype = Object.create(TerminalNode.prototype)
ErrorNode.prototype.constructor = ErrorNode

type ParseTreeVisitor struct {
	return this
}

func (this *ParseTreeVisitor) visit(ctx) {
	if (Utils.isArray(ctx)) {
		var self = this
		return ctx.map(function(child) { return visitAtom(self, child)})
	} else {
		return visitAtom(this, ctx)
	}
}

var visitAtom = function(visitor, ctx) {
	if (ctx.parser == undefined) { //is terminal
		return
	}

	var name = ctx.parser.ruleNames[ctx.ruleIndex]
	var funcName = "visit" + Utils.titleCase(name)

	return visitor[funcName](ctx)
}

type ParseTreeListener struct {
}

func NewParseTreeListener() ParseTreeListener {
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

func TerminalNodeImpl(symbol) {
	TerminalNode.call(this)
	this.parentCtx = null
	this.symbol = symbol
	return this
}

TerminalNodeImpl.prototype = Object.create(TerminalNode.prototype)
TerminalNodeImpl.prototype.constructor = TerminalNodeImpl

func (this *TerminalNodeImpl) getChild(i) {
	return null
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
	if (this.symbol == null) {
		return INVALID_INTERVAL
	}
	var tokenIndex = this.symbol.tokenIndex
	return new Interval(tokenIndex, tokenIndex)
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

func (this *TerminalNodeImpl) toString() {
	if (this.symbol.type == Token.EOF) {
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

ErrorNodeImpl.prototype = Object.create(TerminalNodeImpl.prototype)
ErrorNodeImpl.prototype.constructor = ErrorNodeImpl

func (this *ErrorNodeImpl) isErrorNode() {
	return true
}

func (this *ErrorNodeImpl) accept(visitor) {
	return visitor.visitErrorNode(this)
}

type ParseTreeWalker struct {
	return this
}

func (this *ParseTreeWalker) walk(listener, t) {
	var errorNode = t instanceof ErrorNode ||
			(t.isErrorNode !== undefined && t.isErrorNode())
	if (errorNode) {
		listener.visitErrorNode(t)
	} else if (t instanceof TerminalNode) {
		listener.visitTerminal(t)
	} else {
		this.enterRule(listener, t)
		for (var i = 0 i < t.getChildCount() i++) {
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

ParseTreeWalker.DEFAULT = new ParseTreeWalker()









exports.INVALID_INTERVAL = INVALID_INTERVAL