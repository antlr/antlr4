package antlr4

// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var TreeINVALID_INTERVAL = NewInterval(-1, -2)

type Tree interface {
	GetParent() Tree
	setParent(Tree)
	getPayload() interface{}
	getChild(i int) Tree
	getChildCount() int
	getChildren() []Tree
	setChildren([]Tree)
	//	toStringTree() string
}

type SyntaxTree interface {
	Tree

	getSourceInterval() *Interval
}

type ParseTree interface {
	SyntaxTree

	//	<T> T accept(ParseTreeVisitor<? extends T> visitor);
	accept(visitor ParseTreeVisitor) interface{}
	GetText() string
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
	visit(tree ParseTree) interface{}
	visitChildren(node RuleNode) interface{}
	visitTerminal(node TerminalNode) interface{}
	visitErrorNode(node ErrorNode) interface{}
}

// TODO
//func (this ParseTreeVisitor) visit(ctx) {
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
	visitTerminal(node TerminalNode)
	visitErrorNode(node ErrorNode)
	enterEveryRule(ctx IParserRuleContext)
	exitEveryRule(ctx IParserRuleContext)
}

type TerminalNodeImpl struct {
	parentCtx IRuleContext

	symbol *Token
}

func NewTerminalNodeImpl(symbol *Token) *TerminalNodeImpl {
	tn := new(TerminalNodeImpl)

	tn.InitTerminalNodeImpl(symbol)

	return tn
}

func (this *TerminalNodeImpl) InitTerminalNodeImpl(symbol *Token) {
	this.parentCtx = nil
	this.symbol = symbol
}

func (this *TerminalNodeImpl) getChild(i int) Tree {
	return nil
}

func (this *TerminalNodeImpl) getChildren() []Tree {
	return nil
}

func (this *TerminalNodeImpl) setChildren(t []Tree) {
	panic("Cannot set children on terminal node")
}

func (this *TerminalNodeImpl) getSymbol() *Token {
	return this.symbol
}

func (this *TerminalNodeImpl) GetParent() Tree {
	return this.parentCtx
}

func (this *TerminalNodeImpl) setParent(t Tree) {
	this.parentCtx = t.(IRuleContext)
}

func (this *TerminalNodeImpl) getPayload() interface{} {
	return this.symbol
}

func (this *TerminalNodeImpl) getSourceInterval() *Interval {
	if this.symbol == nil {
		return TreeINVALID_INTERVAL
	}
	var tokenIndex = this.symbol.tokenIndex
	return NewInterval(tokenIndex, tokenIndex)
}

func (this *TerminalNodeImpl) getChildCount() int {
	return 0
}

func (this *TerminalNodeImpl) accept(visitor ParseTreeVisitor) interface{} {
	return visitor.visitTerminal(this)
}

func (this *TerminalNodeImpl) GetText() string {
	return this.symbol.text()
}

func (this *TerminalNodeImpl) toString() string {
	if this.symbol.tokenType == TokenEOF {
		return "<EOF>"
	} else {
		return this.symbol.text()
	}
}

// Represents a token that was consumed during reSynchronization
// rather than during a valid Match operation. For example,
// we will create this kind of a node during single token insertion
// and deletion as well as during "consume until error recovery set"
// upon no viable alternative exceptions.

type ErrorNodeImpl struct {
	*TerminalNodeImpl
}

func NewErrorNodeImpl(token *Token) *ErrorNodeImpl {
	en := new(ErrorNodeImpl)
	en.InitTerminalNodeImpl(token)
	return en
}

func (this *ErrorNodeImpl) isErrorNode() bool {
	return true
}

func (this *ErrorNodeImpl) accept(visitor ParseTreeVisitor) interface{} {
	return visitor.visitErrorNode(this)
}

type ParseTreeWalker struct {
}

func NewParseTreeWalker() *ParseTreeWalker {
	return new(ParseTreeWalker)
}

func (this *ParseTreeWalker) walk(listener ParseTreeListener, t Tree) {

	if errorNode, ok := t.(ErrorNode); ok {
		listener.visitErrorNode(errorNode)
	} else if term, ok := t.(TerminalNode); ok {
		listener.visitTerminal(term)
	} else {
		this.EnterRule(listener, t.(RuleNode))
		for i := 0; i < t.getChildCount(); i++ {
			var child = t.getChild(i)
			this.walk(listener, child)
		}
		this.exitRule(listener, t.(RuleNode))
	}
}

//
// The discovery of a rule node, involves sending two events: the generic
// {@link ParseTreeListener//enterEveryRule} and a
// {@link RuleContext}-specific event. First we trigger the generic and then
// the rule specific. We to them in reverse order upon finishing the node.
//
func (this *ParseTreeWalker) EnterRule(listener ParseTreeListener, r RuleNode) {
	var ctx = r.getRuleContext().(IParserRuleContext)
	listener.enterEveryRule(ctx)
	ctx.EnterRule(listener)
}

func (this *ParseTreeWalker) exitRule(listener ParseTreeListener, r RuleNode) {
	var ctx = r.getRuleContext().(IParserRuleContext)
	ctx.exitRule(listener)
	listener.exitEveryRule(ctx)
}

var ParseTreeWalkerDEFAULT = NewParseTreeWalker()
