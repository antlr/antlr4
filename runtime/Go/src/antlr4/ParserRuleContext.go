package antlr4

import (
	"reflect"
	"strconv"
	"fmt"
)

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

type BaseParserRuleContext struct {
	*BaseRuleContext

	start, stop Token
	exception   RecognitionException
	children []Tree
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

func (prc *BaseParserRuleContext) GetChildren() []Tree {
	return prc.children
}

func (prc *BaseParserRuleContext) CopyFrom(ctx *BaseParserRuleContext) {
	// from RuleContext
	prc.parentCtx = ctx.parentCtx
	prc.invokingState = ctx.invokingState
	prc.children = nil
	prc.start = ctx.start
	prc.stop = ctx.stop
}

func (this *BaseParserRuleContext) GetText() string {
	if this.GetChildCount() == 0 {
		return ""
	} else {
		var s string
		for _, child := range this.children {
			s += child.(ParseTree).GetText()
		}

		return s
	}
}

// Double dispatch methods for listeners
func (prc *BaseParserRuleContext) EnterRule(listener ParseTreeListener) {
	fmt.Println("Do nothing enter")
}

func (prc *BaseParserRuleContext) ExitRule(listener ParseTreeListener) {
	fmt.Println("Do nothing exit")
}

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

// * Used by EnterOuterAlt to toss out a RuleContext previously added as
// we entered a rule. If we have // label, we will need to remove
// generic ruleContext object.
// /
func (prc *BaseParserRuleContext) RemoveLastChild() {
	if prc.children != nil && len(prc.children) > 0 {
		prc.children = prc.children[0 : len(prc.children)-1]
	}
}

func (prc *BaseParserRuleContext) AddTokenNode(token Token) *TerminalNodeImpl {

	var node = NewTerminalNodeImpl(token)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node

}

func (prc *BaseParserRuleContext) AddErrorNode(badToken Token) *ErrorNodeImpl {
	var node = NewErrorNodeImpl(badToken)
	prc.addTerminalNodeChild(node)
	node.parentCtx = prc
	return node
}

func (prc *BaseParserRuleContext) GetChild(i int) Tree {
	if prc.children != nil && len(prc.children) >= i {
		return prc.children[i]
	} else {
		return nil
	}
}

func (prc *BaseParserRuleContext) GetChildOfType(i int, childType reflect.Type) RuleContext {
	if childType == nil {
		return prc.GetChild(i).(RuleContext)
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

func (this *BaseParserRuleContext) StringTree(ruleNames []string, recog Recognizer) string {
	return TreesStringTree(this, ruleNames, recog)
}

func (prc *BaseParserRuleContext) GetRuleContext() RuleContext {
	return prc
}

func (this *BaseParserRuleContext) Accept(Visitor ParseTreeVisitor) interface{} {
	return Visitor.VisitChildren(this)
}

func (prc *BaseParserRuleContext) SetStart(t Token) {
	prc.start = t
}

func (prc *BaseParserRuleContext) GetStart() Token {
	return prc.start
}

func (prc *BaseParserRuleContext) SetStop(t Token) {
	prc.stop = t
}

func (prc *BaseParserRuleContext) GetStop() Token {
	return prc.stop
}

func (prc *BaseParserRuleContext) GetToken(ttype int, i int) TerminalNode {

	for j := 0; j < len(prc.children); j++ {
		var child = prc.children[j]
		if c2, ok := child.(TerminalNode); ok {
			if c2.GetSymbol().GetTokenType() == ttype {
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
				if tchild.GetSymbol().GetTokenType() == ttype {
					tokens = append(tokens, tchild)
				}
			}
		}
		return tokens
	}
}

func (prc *BaseParserRuleContext) GetPayload() interface{}{
	return prc
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

func (prc *BaseParserRuleContext) GetChildCount() int {
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

//need to manage circular dependencies, so export now

// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if this is a leaf.
//

func (this *BaseParserRuleContext) String(ruleNames []string, stop RuleContext) string {

	var p ParserRuleContext = this
	var s = "["
	for p != nil && p != stop {
		if ruleNames == nil {
			if !p.IsEmpty() {
				s += strconv.Itoa(p.GetInvokingState())
			}
		} else {
			var ri = p.GetRuleIndex()
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


var RuleContextEmpty = NewBaseParserRuleContext(nil, -1)



type InterpreterRuleContext interface {
	ParserRuleContext
}

type BaseInterpreterRuleContext struct {
	*BaseParserRuleContext
}

func NewBaseInterpreterRuleContext(parent BaseInterpreterRuleContext, invokingStateNumber, ruleIndex int) *BaseInterpreterRuleContext {

	prc := new(BaseInterpreterRuleContext)

	prc.BaseParserRuleContext = NewBaseParserRuleContext(parent, invokingStateNumber)

	prc.RuleIndex = ruleIndex

	return prc
}
