package antlr4

import (
	"reflect"
)

type IParserRuleContext interface {
	IRuleContext

	SetException(IRecognitionException)
	addTokenNode(token *Token) *TerminalNodeImpl
	addErrorNode(badToken *Token) *ErrorNodeImpl
	EnterRule(listener ParseTreeListener)
	ExitRule(listener ParseTreeListener)

	setStart(*Token)
	getStart() *Token

	setStop(*Token)
	getStop() *Token

	addChild(child IRuleContext) IRuleContext
	removeLastChild()
}

type ParserRuleContext struct {
	*RuleContext

	children    []ParseTree
	start, stop *Token
	exception   IRecognitionException
}

func NewParserRuleContext(parent IParserRuleContext, invokingStateNumber int) *ParserRuleContext {

	prc := new(ParserRuleContext)

	prc.InitParserRuleContext(parent, invokingStateNumber)

	return prc

}

func (prc *ParserRuleContext) InitParserRuleContext(parent IParserRuleContext, invokingStateNumber int) {

	prc.InitRuleContext(parent, invokingStateNumber)

	prc.RuleIndex = -1
	// * If we are debugging or building a parse tree for a Visitor,
	// we need to track all of the tokens and rule invocations associated
	// with prc rule's context. This is empty for parsing w/o tree constr.
	// operation because we don't the need to track the details about
	// how we parse prc rule.
	// /
	prc.children = nil
	prc.start = nil
	prc.stop = nil
	// The exception that forced prc rule to return. If the rule successfully
	// completed, prc is {@code nil}.
	prc.exception = nil

}

func (prc *ParserRuleContext) SetException(e IRecognitionException) {
	prc.exception = e
}

func (prc *ParserRuleContext) GetParent() Tree {
	return prc.parentCtx
}

func (prc *ParserRuleContext) setParent(ctx Tree) {
	prc.parentCtx = ctx.(IParserRuleContext)
}

func (prc *ParserRuleContext) setChildren(cs []Tree) {
	prc.children = make([]ParseTree, len(cs))
	for _, c := range cs {
		prc.addChild(c.(IRuleContext))
	}
}

func (prc *ParserRuleContext) CopyFrom(ctx *ParserRuleContext) {
	// from RuleContext
	prc.parentCtx = ctx.parentCtx
	prc.invokingState = ctx.invokingState
	prc.children = nil
	prc.start = ctx.start
	prc.stop = ctx.stop
}

// Double dispatch methods for listeners
func (prc *ParserRuleContext) EnterRule(listener ParseTreeListener) {
}

func (prc *ParserRuleContext) ExitRule(listener ParseTreeListener) {
}

// * Does not set parent link other add methods do that///
func (prc *ParserRuleContext) addTerminalNodeChild(child TerminalNode) TerminalNode {
	if prc.children == nil {
		prc.children = make([]ParseTree, 0)
	}
	prc.children = append(prc.children, child)
	return child
}

func (prc *ParserRuleContext) addChild(child IRuleContext) IRuleContext {
	if prc.children == nil {
		prc.children = make([]ParseTree, 0)
	}
	prc.children = append(prc.children, child)
	return child
}

// * Used by EnterOuterAlt to toss out a RuleContext previously added as
// we entered a rule. If we have // label, we will need to remove
// generic ruleContext object.
// /
func (prc *ParserRuleContext) removeLastChild() {
	if prc.children != nil && len(prc.children) > 0 {
		prc.children = prc.children[0 : len(prc.children)-1]
	}
}

func (prc *ParserRuleContext) addTokenNode(token *Token) *TerminalNodeImpl {

	var node = NewTerminalNodeImpl(token)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node

}

func (prc *ParserRuleContext) addErrorNode(badToken *Token) *ErrorNodeImpl {
	var node = NewErrorNodeImpl(badToken)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node
}

func (prc *ParserRuleContext) getChild(i int) Tree {
	if prc.children != nil && len(prc.children) >= i {
		return prc.children[i]
	} else {
		return nil
	}
}

func (prc *ParserRuleContext) getChildOfType(i int, childType reflect.Type) IRuleContext {
	if childType == nil {
		return prc.getChild(i).(IRuleContext)
	} else {
		for j := 0; j < len(prc.children); j++ {
			var child = prc.children[j]
			if reflect.TypeOf(child) == childType {
				if i == 0 {
					return child.(IRuleContext)
				} else {
					i -= 1
				}
			}
		}
		return nil
	}
}

func (prc *ParserRuleContext) setStart(t *Token) {
	prc.start = t
}

func (prc *ParserRuleContext) getStart() *Token {
	return prc.start
}

func (prc *ParserRuleContext) setStop(t *Token) {
	prc.stop = t
}

func (prc *ParserRuleContext) getStop() *Token {
	return prc.stop
}

func (prc *ParserRuleContext) GetToken(ttype int, i int) TerminalNode {

	for j := 0; j < len(prc.children); j++ {
		var child = prc.children[j]
		if c2, ok := child.(TerminalNode); ok {
			if c2.getSymbol().tokenType == ttype {
				if i == 0 {
					return c2
				} else {
					i -= 1
				}
			}
		}
	}
	return nil
}

func (prc *ParserRuleContext) GetTokens(ttype int) []TerminalNode {
	if prc.children == nil {
		return make([]TerminalNode, 0)
	} else {
		var tokens = make([]TerminalNode, 0)
		for j := 0; j < len(prc.children); j++ {
			var child = prc.children[j]
			if tchild, ok := child.(TerminalNode); ok {
				if tchild.getSymbol().tokenType == ttype {
					tokens = append(tokens, tchild)
				}
			}
		}
		return tokens
	}
}

func (prc *ParserRuleContext) GetTypedRuleContext(ctxType reflect.Type, i int) interface{} {
	panic("GetTypedRuleContexts not implemented")
	//    return prc.getChild(i, ctxType)
}

func (prc *ParserRuleContext) GetTypedRuleContexts(ctxType reflect.Type) []interface{} {
	panic("GetTypedRuleContexts not implemented")
	//    if (prc.children== nil) {
	//        return []
	//    } else {
	//		var contexts = []
	//		for(var j=0 j<len(prc.children) j++) {
	//			var child = prc.children[j]
	//			if _, ok := child.(ctxType); ok {
	//				contexts.push(child)
	//			}
	//		}
	//		return contexts
	//	}
}

func (prc *ParserRuleContext) getChildCount() int {
	if prc.children == nil {
		return 0
	} else {
		return len(prc.children)
	}
}

func (prc *ParserRuleContext) GetSourceInterval() *Interval {
	if prc.start == nil || prc.stop == nil {
		return TreeINVALID_INTERVAL
	} else {
		return NewInterval(prc.start.tokenIndex, prc.stop.tokenIndex)
	}
}

var RuleContextEMPTY = NewParserRuleContext(nil, -1)

type IInterpreterRuleContext interface {
	IParserRuleContext
}

type InterpreterRuleContext struct {
	*ParserRuleContext
}

func NewInterpreterRuleContext(parent InterpreterRuleContext, invokingStateNumber, ruleIndex int) *InterpreterRuleContext {

	prc := new(InterpreterRuleContext)

	prc.InitParserRuleContext(parent, invokingStateNumber)

	prc.RuleIndex = ruleIndex

	return prc
}
