package antlr4

import (
	"reflect"
)

type ParserRuleContext interface {
	RuleContext

	SetException(RecognitionException)
	addTokenNode(token Token) *TerminalNodeImpl
	addErrorNode(badToken Token) *ErrorNodeImpl
	EnterRule(listener ParseTreeListener)
	ExitRule(listener ParseTreeListener)

	setStart(Token)
	getStart() Token

	setStop(Token)
	getStop() Token

	addChild(child RuleContext) RuleContext
	removeLastChild()
}

type BaseParserRuleContext struct {
	*BaseRuleContext

	children    []ParseTree
	start, stop Token
	exception RecognitionException
}

func NewBaseParserRuleContext(parent ParserRuleContext, invokingStateNumber int) *BaseParserRuleContext {

	prc := new(BaseParserRuleContext)

	prc.BaseRuleContext = NewBaseRuleContext(parent, invokingStateNumber)

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

	return prc

}

func (prc *BaseParserRuleContext) SetException(e RecognitionException) {
	prc.exception = e
}

func (prc *BaseParserRuleContext) GetParent() Tree {
	return prc.parentCtx
}

func (prc *BaseParserRuleContext) setParent(ctx Tree) {
	prc.parentCtx = ctx.(ParserRuleContext)
}

func (prc *BaseParserRuleContext) setChildren(cs []Tree) {
	prc.children = make([]ParseTree, len(cs))
	for _, c := range cs {
		prc.addChild(c.(RuleContext))
	}
}

func (prc *BaseParserRuleContext) CopyFrom(ctx *BaseParserRuleContext) {
	// from RuleContext
	prc.parentCtx = ctx.parentCtx
	prc.invokingState = ctx.invokingState
	prc.children = nil
	prc.start = ctx.start
	prc.stop = ctx.stop
}

// Double dispatch methods for listeners
func (prc *BaseParserRuleContext) EnterRule(listener ParseTreeListener) {
}

func (prc *BaseParserRuleContext) ExitRule(listener ParseTreeListener) {
}

// * Does not set parent link other add methods do that///
func (prc *BaseParserRuleContext) addTerminalNodeChild(child TerminalNode) TerminalNode {
	if prc.children == nil {
		prc.children = make([]ParseTree, 0)
	}
	prc.children = append(prc.children, child)
	return child
}

func (prc *BaseParserRuleContext) addChild(child RuleContext) RuleContext {
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
func (prc *BaseParserRuleContext) removeLastChild() {
	if prc.children != nil && len(prc.children) > 0 {
		prc.children = prc.children[0 : len(prc.children)-1]
	}
}

func (prc *BaseParserRuleContext) addTokenNode(token Token) *TerminalNodeImpl {

	var node = NewTerminalNodeImpl(token)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node

}

func (prc *BaseParserRuleContext) addErrorNode(badToken Token) *ErrorNodeImpl {
	var node = NewErrorNodeImpl(badToken)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node
}

func (prc *BaseParserRuleContext) getChild(i int) Tree {
	if prc.children != nil && len(prc.children) >= i {
		return prc.children[i]
	} else {
		return nil
	}
}

func (prc *BaseParserRuleContext) getChildOfType(i int, childType reflect.Type) RuleContext {
	if childType == nil {
		return prc.getChild(i).(RuleContext)
	} else {
		for j := 0; j < len(prc.children); j++ {
			var child = prc.children[j]
			if reflect.TypeOf(child) == childType {
				if i == 0 {
					return child.(RuleContext)
				} else {
					i -= 1
				}
			}
		}
		return nil
	}
}

func (prc *BaseParserRuleContext) setStart(t Token) {
	prc.start = t
}

func (prc *BaseParserRuleContext) getStart() Token {
	return prc.start
}

func (prc *BaseParserRuleContext) setStop(t Token) {
	prc.stop = t
}

func (prc *BaseParserRuleContext) getStop() Token {
	return prc.stop
}

func (prc *BaseParserRuleContext) GetToken(ttype int, i int) TerminalNode {

	for j := 0; j < len(prc.children); j++ {
		var child = prc.children[j]
		if c2, ok := child.(TerminalNode); ok {
			if c2.getSymbol().GetTokenType() == ttype {
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

func (prc *BaseParserRuleContext) GetTokens(ttype int) []TerminalNode {
	if prc.children == nil {
		return make([]TerminalNode, 0)
	} else {
		var tokens = make([]TerminalNode, 0)
		for j := 0; j < len(prc.children); j++ {
			var child = prc.children[j]
			if tchild, ok := child.(TerminalNode); ok {
				if tchild.getSymbol().GetTokenType() == ttype {
					tokens = append(tokens, tchild)
				}
			}
		}
		return tokens
	}
}

func (prc *BaseParserRuleContext) GetTypedRuleContext(ctxType reflect.Type, i int) interface{} {
	panic("GetTypedRuleContexts not implemented")
	//    return prc.getChild(i, ctxType)
}

func (prc *BaseParserRuleContext) GetTypedRuleContexts(ctxType reflect.Type) []interface{} {
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

func (prc *BaseParserRuleContext) getChildCount() int {
	if prc.children == nil {
		return 0
	} else {
		return len(prc.children)
	}
}

func (prc *BaseParserRuleContext) GetSourceInterval() *Interval {
	if prc.start == nil || prc.stop == nil {
		return TreeInvalidInterval
	} else {
		return NewInterval(prc.start.GetTokenIndex(), prc.stop.GetTokenIndex())
	}
}

var RuleContextEMPTY = NewBaseParserRuleContext(nil, -1)

type IInterpreterRuleContext interface {
	ParserRuleContext
}

type InterpreterRuleContext struct {
	*BaseParserRuleContext
}

func NewInterpreterRuleContext(parent InterpreterRuleContext, invokingStateNumber, ruleIndex int) *InterpreterRuleContext {

	prc := new(InterpreterRuleContext)

	prc.BaseParserRuleContext = NewBaseParserRuleContext(parent, invokingStateNumber)

	prc.RuleIndex = ruleIndex

	return prc
}
