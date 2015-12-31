package antlr4

// The basic notion of a tree has a parent, a payload, and a list of children.
//  It is the most abstract interface for all the trees used by ANTLR.
///

var TreeInvalidInterval = NewInterval(-1, -2)

type Tree interface {
	GetParent() Tree
	setParent(Tree)
	getPayload() interface{}
	getChild(i int) Tree
	getChildCount() int
	getChildren() []Tree
	setChildren([]Tree)
	//	StringTree() string
}

type SyntaxTree interface {
	Tree

	GetSourceInterval() *Interval
}

type ParseTree interface {
	SyntaxTree

	//	<T> T accept(ParseTreeVisitor<? extends T> Visitor);
	accept(Visitor ParseTreeVisitor) interface{}
	GetText() string
	//	StringTree([]string, IRecognizer) string
}

type RuleNode interface {
	ParseTree

	getRuleContext() RuleContext
}

type TerminalNode interface {
	ParseTree

	getSymbol() Token
}

type ErrorNode interface {
	TerminalNode
}

type ParseTreeVisitor interface {
	// NOTE: removed type arguments
	Visit(tree ParseTree) interface{}
	VisitChildren(node RuleNode) interface{}
	VisitTerminal(node TerminalNode) interface{}
	VisitErrorNode(node ErrorNode) interface{}
}

// TODO
//func (this ParseTreeVisitor) Visit(ctx) {
//	if (Utils.isArray(ctx)) {
//		var self = this
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
//	var name = ctx.parser.ruleNames[ctx.ruleIndex]
//	var funcName = "Visit" + Utils.titleCase(name)
//
//	return Visitor[funcName](ctx)
//}

type ParseTreeListener interface {
	VisitTerminal(node TerminalNode)
	VisitErrorNode(node ErrorNode)
	EnterEveryRule(ctx ParserRuleContext)
	ExitEveryRule(ctx ParserRuleContext)
}

type TerminalNodeImpl struct {
	parentCtx RuleContext

	symbol Token
}

func NewTerminalNodeImpl(symbol Token) *TerminalNodeImpl {
	tn := new(TerminalNodeImpl)

	tn.parentCtx = nil
	tn.symbol = symbol

	return tn
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

func (this *TerminalNodeImpl) getSymbol() Token {
	return this.symbol
}

func (this *TerminalNodeImpl) GetParent() Tree {
	return this.parentCtx
}

func (this *TerminalNodeImpl) setParent(t Tree) {
	this.parentCtx = t.(RuleContext)
}

func (this *TerminalNodeImpl) getPayload() interface{} {
	return this.symbol
}

func (this *TerminalNodeImpl) GetSourceInterval() *Interval {
	if this.symbol == nil {
		return TreeInvalidInterval
	}
	var tokenIndex = this.symbol.GetTokenIndex()
	return NewInterval(tokenIndex, tokenIndex)
}

func (this *TerminalNodeImpl) getChildCount() int {
	return 0
}

func (this *TerminalNodeImpl) accept(Visitor ParseTreeVisitor) interface{} {
	return Visitor.VisitTerminal(this)
}

func (this *TerminalNodeImpl) GetText() string {
	return this.symbol.GetText()
}

func (this *TerminalNodeImpl) String() string {
	if this.symbol.GetTokenType() == TokenEOF {
		return "<EOF>"
	} else {
		return this.symbol.GetText()
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

func NewErrorNodeImpl(token Token) *ErrorNodeImpl {
	en := new(ErrorNodeImpl)
	en.TerminalNodeImpl = NewTerminalNodeImpl(token)
	return en
}

func (this *ErrorNodeImpl) isErrorNode() bool {
	return true
}

func (this *ErrorNodeImpl) accept(Visitor ParseTreeVisitor) interface{} {
	return Visitor.VisitErrorNode(this)
}

type ParseTreeWalker struct {
}

func NewParseTreeWalker() *ParseTreeWalker {
	return new(ParseTreeWalker)
}

func (this *ParseTreeWalker) walk(listener ParseTreeListener, t Tree) {

	if errorNode, ok := t.(ErrorNode); ok {
		listener.VisitErrorNode(errorNode)
	} else if term, ok := t.(TerminalNode); ok {
		listener.VisitTerminal(term)
	} else {
		this.EnterRule(listener, t.(RuleNode))
		for i := 0; i < t.getChildCount(); i++ {
			var child = t.getChild(i)
			this.walk(listener, child)
		}
		this.ExitRule(listener, t.(RuleNode))
	}
}

//
// The discovery of a rule node, involves sending two events: the generic
// {@link ParseTreeListener//EnterEveryRule} and a
// {@link RuleContext}-specific event. First we trigger the generic and then
// the rule specific. We to them in reverse order upon finishing the node.
//
func (this *ParseTreeWalker) EnterRule(listener ParseTreeListener, r RuleNode) {
	var ctx = r.getRuleContext().(ParserRuleContext)
	listener.EnterEveryRule(ctx)
	ctx.EnterRule(listener)
}

func (this *ParseTreeWalker) ExitRule(listener ParseTreeListener, r RuleNode) {
	var ctx = r.getRuleContext().(ParserRuleContext)
	ctx.ExitRule(listener)
	listener.ExitEveryRule(ctx)
}

var ParseTreeWalkerDEFAULT = NewParseTreeWalker()
