package antlr4

import (
	"antlr4/tree"
)

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
//

type RuleContext struct {
	tree.RuleNode
	parentCtx *RuleContext
	invokingState int
	ruleIndex int
	children []*RuleContext
}

func NewRuleContext(parent *RuleContext, invokingState int)  *RuleContext {

	rn := &RuleContext{tree.RuleNode{}}

	rn.initRuleNode()
	rn.initRuleContext(parent, invokingState)

	return rn
}

func (rn *RuleContext) initRuleContext(parent *RuleContext, invokingState int) {

	// What context invoked this rule?
	rn.parentCtx = parent

	// What state invoked the rule associated with this context?
	// The "return address" is the followState of invokingState
	// If parent is nil, this should be -1.
	if (parent == nil){
		rn.invokingState = -1
	} else {
		rn.invokingState = invokingState
	}
}

func (this *RuleContext) depth() {
	var n = 0
	var p = this
	for (p != nil) {
		p = p.parentCtx
		n += 1
	}
	return n
}

// A context is empty if there is no invoking state meaning nobody call
// current context.
func (this *RuleContext) isEmpty() bool {
	return this.invokingState == -1
}

// satisfy the ParseTree / SyntaxTree interface

func (this *RuleContext) getSourceInterval() *Interval {
	return tree.TreeINVALID_INTERVAL
}

func (this *RuleContext) getRuleContext() *RuleContext {
	return this
}

func (this *RuleContext) getPayload() *RuleContext {
	return this
}

// Return the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
// <p>
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of this
// method.
//
func (this *RuleContext) getText() {
	if (this.getChildCount() == 0) {
		return ""
	} else {
		var s string
		for _, child := range this.children {
			s += child.getText()
		}

		return s
	}
}

func (this *RuleContext) getChild(i int) {
	return nil
}

func (this *RuleContext) getChildCount() {
	return 0
}

func (this *RuleContext) accept(visitor *tree.ParseTreeVisitor) {
	visitor.visitChildren(this)
}

//need to manage circular dependencies, so export now

// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if this is a leaf.
//

func (this *RuleContext) toStringTree(ruleNames []string, recog *Recognizer) string {
	return tree.Trees.toStringTree(this, ruleNames, recog)
}

func (this *RuleContext) toString(ruleNames []string, stop *RuleContext) string {
	ruleNames = ruleNames || nil
	stop = stop || nil
	var p *RuleContext = this
	var s = "["
	for (p != nil && p != stop) {
		if (ruleNames == nil) {
			if (!p.isEmpty()) {
				s += p.invokingState
			}
		} else {
			var ri = p.ruleIndex
			var ruleName string
			if (ri >= 0 && ri < len(ruleNames)) {
				ruleName = ruleNames[ri]
			} else {
				ruleName = "" + ri
			}
			s += ruleName
		}
		if (p.parentCtx != nil && (ruleNames != nil || !p.parentCtx.isEmpty())) {
			s += " "
		}
		p = p.parentCtx
	}
	s += "]"
	return s
}

