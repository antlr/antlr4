package antlr4


// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var TreeINVALID_INTERVAL = NewInterval(-1, -2)

type Tree interface {
	getParent() Tree
	getPayload() interface{}
	getChild(i int) Tree
	getChildCount() int
//	toStringTree() string
}

type SyntaxTree interface {
	Tree

	getSourceInterval() *Interval
}

type ParseTree interface {
	SyntaxTree

//	<T> T accept(ParseTreeVisitor<? extends T> visitor);
	accept(visitor *ParseTreeVisitor)
	getText() string
//	toStringTree([]string, IRecognizer) string
}

type RuleNode interface {
	ParseTree

	getRuleContext() IRuleContext
}

type TerminalNode interface {
	ParseTree

	getSymbol() *Token
}

type ErrorNode interface {
	TerminalNode
}

type ParseTreeVisitor interface {
	// NOTE: removed type arguments
	visit(tree *ParseTree) interface{}
	visitChildren(node *RuleNode) interface{}
	visitTerminal(node *TerminalNode) interface{}
	visitErrorNode(node *ErrorNode) interface{}
}

//func (this *ParseTreeVisitor) visit(ctx) {
//	if (Utils.isArray(ctx)) {
//		var self = this
//		return ctx.map(function(child) { return visitAtom(self, child)})
//	} else {
//		return visitAtom(this, ctx)
//	}
//}
//
//func visitAtom(visitor, ctx) {
//	if (ctx.parser == nil) { //is terminal
//		return
//	}
//
//	var name = ctx.parser.ruleNames[ctx.ruleIndex]
//	var funcName = "visit" + Utils.titleCase(name)
//
//	return visitor[funcName](ctx)
//}

type ParseTreeListener interface {
	visitTerminal(node *TerminalNode)
	visitErrorNode(node *ErrorNode)
	enterEveryRule(ctx *ParserRuleContext)
	exitEveryRule(ctx *ParserRuleContext)
}

type TerminalNodeImpl struct {
	parentCtx *RuleContext
	symbol *Token
}

func NewTerminalNodeImpl(symbol *Token) *TerminalNodeImpl {
	tn := &TerminalNodeImpl{TerminalNode{}}

	tn.InitTerminalNodeImpl(symbol)

	return tn
}

func (this *TerminalNodeImpl) InitTerminalNodeImpl(symbol *Token) {
	this.parentCtx = nil
	this.symbol = symbol
}

func (this *TerminalNodeImpl) getChild(i int) *Tree {
	return nil
}

func (this *TerminalNodeImpl) getSymbol() *Token {
	return this.symbol
}

func (this *TerminalNodeImpl) getParent() *Tree {
	return this.parentCtx
}

func (this *TerminalNodeImpl) getPayload() *Token {
	return this.symbol
}

func (this *TerminalNodeImpl) getSourceInterval() *Interval {
	if (this.symbol == nil) {
		return TreeINVALID_INTERVAL
	}
	var tokenIndex = this.symbol.tokenIndex
	return NewInterval(tokenIndex, tokenIndex)
}

func (this *TerminalNodeImpl) getChildCount() {
	return 0
}

func (this *TerminalNodeImpl) accept(visitor *ParseTreeVisitor ) interface{} {
	return (*visitor).visitTerminal(this)
}

func (this *TerminalNodeImpl) getText() string {
	return this.symbol.text()
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

type ErrorNodeImpl struct {
	TerminalNodeImpl
}

func NewErrorNodeImpl(token *Token) *ErrorNodeImpl {
	en := new(ErrorNodeImpl)
	en.InitTerminalNodeImpl(token)
	return en
}

func (this *ErrorNodeImpl) isErrorNode() bool {
	return true
}

func (this *ErrorNodeImpl) accept( visitor *ParseTreeVisitor ) interface{} {
	return (*visitor).visitErrorNode(this)
}



type ParseTreeWalker struct {

}

func NewParseTreeWalker() *ParseTreeWalker {
	return new(ParseTreeWalker)
}

func (this *ParseTreeWalker) walk(listener *ParseTreeListener, t *Tree) {

	if errorNode, ok := t.(*ErrorNode); ok {
		(*listener).visitErrorNode(errorNode)
	} else if term, ok := t.(TerminalNode); ok {
		(*listener).visitTerminal(term)
	} else {
		this.enterRule(listener, t)
		for i := 0; i < len(t.children); i++ {
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
func (this *ParseTreeWalker) enterRule(listener *ParseTreeListener, r *RuleNode) {
	var ctx = r.getRuleContext().(*ParserRuleContext)
	(*listener).enterEveryRule(ctx)
	ctx.enterRule(listener)
}

func (this *ParseTreeWalker) exitRule(listene *ParseTreeListener, r *RuleNode) {
	var ctx = r.getRuleContext().(*ParserRuleContext)
	ctx.exitRule(listener)
	listener.exitEveryRule(ctx)
}

var ParseTreeWalkerDEFAULT = NewParseTreeWalker()
