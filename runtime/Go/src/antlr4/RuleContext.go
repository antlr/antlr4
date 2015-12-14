package antlr

//  A rule context is a record of a single rule invocation. It knows
//  which context invoked it, if any. If there is no parent context, then
//  naturally the invoking state is not valid.  The parent link
//  provides a chain upwards from the current rule invocation to the root
//  of the invocation tree, forming a stack. We actually carry no
//  information about the rule associated with this context (except
//  when parsing). We keep only the state number of the invoking state from
//  the ATN submachine that invoked this. Contrast this with the s
//  pointer inside ParserRuleContext that tracks the current state
//  being "executed" for the current rule.
//
//  The parent contexts are useful for computing lookahead sets and
//  getting error information.
//
//  These objects are used during parsing and prediction.
//  For the special case of parsers, we use the subclass
//  ParserRuleContext.
//
//  @see ParserRuleContext
///

var RuleNode = require('./tree/Tree').RuleNode
var INVALID_INTERVAL = require('./tree/Tree').INVALID_INTERVAL

func RuleContext(parent, invokingState) {
	RuleNode.call(this)
	// What context invoked this rule?
	this.parentCtx = parent || null
	// What state invoked the rule associated with this context?
	// The "return address" is the followState of invokingState
	// If parent is null, this should be -1.
	this.invokingState = invokingState || -1
	return this
}

RuleContext.prototype = Object.create(RuleNode.prototype)
RuleContext.prototype.constructor = RuleContext

func (this *RuleContext) depth() {
	var n = 0
	var p = this
	while (p !== null) {
		p = p.parentCtx
		n += 1
	}
	return n
}

// A context is empty if there is no invoking state meaning nobody call
// current context.
func (this *RuleContext) isEmpty() {
	return this.invokingState == -1
}

// satisfy the ParseTree / SyntaxTree interface

func (this *RuleContext) getSourceInterval() {
	return INVALID_INTERVAL
}

func (this *RuleContext) getRuleContext() {
	return this
}

func (this *RuleContext) getPayload() {
	return this
}

// Return the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
// <p>
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of this
// method.
// /
func (this *RuleContext) getText() {
	if (this.getChildCount() == 0) {
		return ""
	} else {
		return this.children.map(function(child) {
			return child.getText()
		}).join("")
	}
}

func (this *RuleContext) getChild(i) {
	return null
}

func (this *RuleContext) getChildCount() {
	return 0
}

func (this *RuleContext) accept(visitor) {
	return visitor.visitChildren(this)
}

//need to manage circular dependencies, so export now

var Trees = require('./tree/Trees').Trees


// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if this is a leaf.
//

func (this *RuleContext) toStringTree(ruleNames, recog) {
	return Trees.toStringTree(this, ruleNames, recog)
}

func (this *RuleContext) toString(ruleNames, stop) {
	ruleNames = ruleNames || null
	stop = stop || null
	var p = this
	var s = "["
	while (p !== null && p !== stop) {
		if (ruleNames == null) {
			if (!p.isEmpty()) {
				s += p.invokingState
			}
		} else {
			var ri = p.ruleIndex
			var ruleName = (ri >= 0 && ri < ruleNames.length) ? ruleNames[ri]
					: "" + ri
			s += ruleName
		}
		if (p.parentCtx !== null && (ruleNames !== null || !p.parentCtx.isEmpty())) {
			s += " "
		}
		p = p.parentCtx
	}
	s += "]"
	return s
}

