package antlr4

import (
			"reflect"
)

//* A rule invocation record for parsing.
//
//  Contains all of the information about the current rule not stored in the
//  RuleContext. It handles parse tree children list, Any ATN state
//  tracing, and the default values available for rule indications:
//  start, stop, rule index, current alt number, current
//  ATN state.
//
//  Subclasses made for each rule and grammar track the parameters,
//  return values, locals, and labels specific to that rule. These
//  are the objects that are returned from rules.
//
//  Note text is not an actual field of a rule return value it is computed
//  from start and stop using the input stream's toString() method.  I
//  could add a ctor to prc so that we can pass in and store the input
//  stream, but I'm not sure we want to do that.  It would seem to be nil
//  to get the .text property anyway if the rule matches tokens from multiple
//  input streams.
//
//  I do not use getters for fields of objects that are used simply to
//  group values such as prc aggregate.  The getters/setters are there to
//  satisfy the superclass interface.


type ParserRuleContext struct {
	RuleContext

	ruleIndex int
	children []RuleContext
	start, stop *Token
	exception *RecognitionException
}

func NewParserRuleContext(parent *ParserRuleContext, invokingStateNumber int) *ParserRuleContext {

	prc := new(ParserRuleContext)

	prc.initRuleContext(parent, invokingStateNumber)
	prc.initParserRuleContext(parent, invokingStateNumber)

	return prc

}

func (prc *ParserRuleContext) initParserRuleContext(parent *ParserRuleContext, invokingStateNumber int){

	prc.initRuleContext(parent, invokingStateNumber)

	prc.ruleIndex = -1
	// * If we are debugging or building a parse tree for a visitor,
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

func (prc *ParserRuleContext) copyFrom(ctx *ParserRuleContext) {
    // from RuleContext
    prc.parentCtx = ctx.parentCtx
    prc.invokingState = ctx.invokingState
    prc.children = nil
    prc.start = ctx.start
    prc.stop = ctx.stop
}

// Double dispatch methods for listeners
func (prc *ParserRuleContext) enterRule(listener *ParseTreeListener) {
}

func (prc *ParserRuleContext) exitRule(listener *ParseTreeListener) {
}

// * Does not set parent link other add methods do that///
func (prc *ParserRuleContext) addChild(child *ParserRuleContext) {
    if (prc.children == nil) {
        prc.children = make([]*ParserRuleContext)
    }
    prc.children = append( prc.children, child )
    return child
}

// * Used by enterOuterAlt to toss out a RuleContext previously added as
// we entered a rule. If we have // label, we will need to remove
// generic ruleContext object.
// /
func (prc *ParserRuleContext) removeLastChild() {
    if (prc.children != nil && len(prc.children) > 0) {
		prc.children = prc.children[0:len(prc.children)-1]
    }
}

func (prc *ParserRuleContext) addTokenNode(token *Token) *TerminalNodeImpl {
    var node = NewTerminalNodeImpl(token)
    prc.addChild(node)
    node.parentCtx = prc
    return node
}

func (prc *ParserRuleContext) addErrorNode(badToken *Token) *ErrorNodeImpl {
    var node = NewErrorNodeImpl(badToken)
    prc.addChild(node)
    node.parentCtx = prc
    return node
}

func (prc *ParserRuleContext) getChild(i int, childType reflect.Type) {
	if (childType == nil) {
		if (prc.children != nil && len(prc.children) >= i){
			return prc.children[i]
		} else {
			return nil
		}
	} else {
		for j :=0; j<len(prc.children); j++ {
			var child = prc.children[j]
			if reflect.TypeOf(child) == childType {
				if(i==0) {
					return child
				} else {
					i -= 1
				}
			}
		}
		return nil
    }
}


func (prc *ParserRuleContext) getToken(ttype int, i int) *TerminalNode {

	for j :=0; j<len(prc.children); j++ {
		var child = prc.children[j]
		if _, ok := child.(*TerminalNode); ok {
			if (child.symbol.tokenType == ttype) {
				if(i==0) {
					return child
				} else {
					i -= 1
				}
			}
        }
	}
    return nil
}

func (prc *ParserRuleContext) getTokens(ttype int) []*TerminalNode {
    if (prc.children== nil) {
        return make([]*TerminalNode)
    } else {
		var tokens = make([]*TerminalNode)
		for j:=0; j<len(prc.children); j++ {
			var child = prc.children[j]
			if tchild, ok := child.(*TerminalNode); ok {
				if (tchild.symbol.tokenType == ttype) {
					tokens = append(tokens, tchild)
				}
			}
		}
		return tokens
    }
}

func (prc *ParserRuleContext) getTypedRuleContext(ctxType reflect.Type, i int) *interface{} {
	panic("getTypedRuleContexts not implemented")
//    return prc.getChild(i, ctxType)
}

func (prc *ParserRuleContext) getTypedRuleContexts(ctxType reflect.Type) []*interface{} {
	panic("getTypedRuleContexts not implemented")
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

func (prc *ParserRuleContext) getChildCount() {
	if (prc.children== nil) {
		return 0
	} else {
		return len(prc.children)
	}
}

func (prc *ParserRuleContext) getSourceInterval() {
    if( prc.start == nil || prc.stop == nil) {
        return TreeINVALID_INTERVAL
    } else {
        return NewInterval(prc.start.tokenIndex, prc.stop.tokenIndex)
    }
}

var RuleContextEMPTY = NewParserRuleContext(nil, nil)


type InterpreterRuleContext struct {
	ParserRuleContext
	ruleIndex int
}

func NewInterpreterRuleContext(parent *InterpreterRuleContext, invokingStateNumber, ruleIndex int) {

	prc := new(InterpreterRuleContext)
	prc.initParserRuleContext( parent, invokingStateNumber )

    prc.ruleIndex = ruleIndex

    return prc
}
