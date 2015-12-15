package antlr4

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
//  could add a ctor to this so that we can pass in and store the input
//  stream, but I'm not sure we want to do that.  It would seem to be undefined
//  to get the .text property anyway if the rule matches tokens from multiple
//  input streams.
//
//  I do not use getters for fields of objects that are used simply to
//  group values such as this aggregate.  The getters/setters are there to
//  satisfy the superclass interface.

var RuleContext = require('./RuleContext').RuleContext
var Tree = require('./tree/Tree')
var INVALID_INTERVAL = Tree.INVALID_INTERVAL
var TerminalNode = Tree.TerminalNode
var TerminalNodeImpl = Tree.TerminalNodeImpl
var ErrorNodeImpl = Tree.ErrorNodeImpl
var Interval = require("./IntervalSet").Interval

func ParserRuleContext(parent, invokingStateNumber) {
	parent = parent || nil
	invokingStateNumber = invokingStateNumber || nil
	RuleContext.call(this, parent, invokingStateNumber)
	this.ruleIndex = -1
    // * If we are debugging or building a parse tree for a visitor,
    // we need to track all of the tokens and rule invocations associated
    // with this rule's context. This is empty for parsing w/o tree constr.
    // operation because we don't the need to track the details about
    // how we parse this rule.
    // /
    this.children = nil
    this.start = nil
    this.stop = nil
    // The exception that forced this rule to return. If the rule successfully
    // completed, this is {@code nil}.
    this.exception = nil
}

ParserRuleContext.prototype = Object.create(RuleContext.prototype)
ParserRuleContext.prototype.constructor = ParserRuleContext

// * COPY a ctx (I'm deliberately not using copy constructor)///
func (this *ParserRuleContext) copyFrom(ctx) {
    // from RuleContext
    this.parentCtx = ctx.parentCtx
    this.invokingState = ctx.invokingState
    this.children = nil
    this.start = ctx.start
    this.stop = ctx.stop
}

// Double dispatch methods for listeners
func (this *ParserRuleContext) enterRule(listener) {
}

func (this *ParserRuleContext) exitRule(listener) {
}

// * Does not set parent link other add methods do that///
func (this *ParserRuleContext) addChild(child) {
    if (this.children == nil) {
        this.children = []
    }
    this.children.push(child)
    return child
}

// * Used by enterOuterAlt to toss out a RuleContext previously added as
// we entered a rule. If we have // label, we will need to remove
// generic ruleContext object.
// /
func (this *ParserRuleContext) removeLastChild() {
    if (this.children != nil) {
        this.children.pop()
    }
}

func (this *ParserRuleContext) addTokenNode(token) {
    var node = new TerminalNodeImpl(token)
    this.addChild(node)
    node.parentCtx = this
    return node
}

func (this *ParserRuleContext) addErrorNode(badToken) {
    var node = new ErrorNodeImpl(badToken)
    this.addChild(node)
    node.parentCtx = this
    return node
}

func (this *ParserRuleContext) getChild(i, type) {
	type = type || nil
	if (type == nil) {
		return this.children.length>=i ? this.children[i] : nil
	} else {
		for(var j=0 j<this.children.length j++) {
			var child = this.children[j]
			if(child instanceof type) {
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


func (this *ParserRuleContext) getToken(ttype, i) {
	for(var j=0 j<this.children.length j++) {
		var child = this.children[j]
		if (child instanceof TerminalNode) {
			if (child.symbol.type == ttype) {
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

func (this *ParserRuleContext) getTokens(ttype ) {
    if (this.children== nil) {
        return []
    } else {
		var tokens = []
		for(var j=0 j<this.children.length j++) {
			var child = this.children[j]
			if (child instanceof TerminalNode) {
				if (child.symbol.type == ttype) {
					tokens.push(child)
				}
			}
		}
		return tokens
    }
}

func (this *ParserRuleContext) getTypedRuleContext(ctxType, i) {
    return this.getChild(i, ctxType)
}

func (this *ParserRuleContext) getTypedRuleContexts(ctxType) {
    if (this.children== nil) {
        return []
    } else {
		var contexts = []
		for(var j=0 j<this.children.length j++) {
			var child = this.children[j]
			if (child instanceof ctxType) {
				contexts.push(child)
			}
		}
		return contexts
	}
}

func (this *ParserRuleContext) getChildCount() {
	if (this.children== nil) {
		return 0
	} else {
		return this.children.length
	}
}

func (this *ParserRuleContext) getSourceInterval() {
    if( this.start == nil || this.stop == nil) {
        return INVALID_INTERVAL
    } else {
        return new Interval(this.start.tokenIndex, this.stop.tokenIndex)
    }
}

RuleContext.EMPTY = new ParserRuleContext()

func InterpreterRuleContext(parent, invokingStateNumber, ruleIndex) {
	ParserRuleContext.call(parent, invokingStateNumber)
    this.ruleIndex = ruleIndex
    return this
}

InterpreterRuleContext.prototype = Object.create(ParserRuleContext.prototype)
InterpreterRuleContext.prototype.constructor = InterpreterRuleContext

