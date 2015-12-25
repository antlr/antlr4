package antlr4

import (
	"strconv"
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

type IRuleContext interface {
	RuleNode

	getInvokingState() int
	setInvokingState(int)

	GetRuleIndex() int

	isEmpty() bool

	toString([]string, IRuleContext) string
}

type RuleContext struct {
	parentCtx     IRuleContext
	invokingState int
	RuleIndex     int
	children      []Tree
}

func NewRuleContext(parent IRuleContext, invokingState int) *RuleContext {

	rn := new(RuleContext)

	// What context invoked this rule?
	rn.parentCtx = parent

	// What state invoked the rule associated with this context?
	// The "return address" is the followState of invokingState
	// If parent is nil, this should be -1.
	if parent == nil {
		rn.invokingState = -1
	} else {
		rn.invokingState = invokingState
	}

	return rn
}

func (this *RuleContext) setChildren(elems []Tree) {
	this.children = elems
}

func (this *RuleContext) setParent(v Tree) {
	this.parentCtx = v.(IRuleContext)
}

func (this *RuleContext) getInvokingState() int {
	return this.invokingState
}

func (this *RuleContext) setInvokingState(t int) {
	this.invokingState = t
}

func (this *RuleContext) GetRuleIndex() int {
	return this.RuleIndex
}

func (this *RuleContext) getChildren() []Tree {
	return this.children
}

func (this *RuleContext) depth() int {
	var n = 0
	var p Tree = this
	for p != nil {
		p = p.GetParent()
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

func (this *RuleContext) GetSourceInterval() *Interval {
	return TreeINVALID_INTERVAL
}

func (this *RuleContext) getRuleContext() IRuleContext {
	return this
}

func (this *RuleContext) getPayload() interface{} {
	return this
}

// Return the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
// <p>
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of this
// method.
//
func (this *RuleContext) GetText() string {
	if this.getChildCount() == 0 {
		return ""
	} else {
		var s string
		for _, child := range this.children {
			s += child.(IRuleContext).GetText()
		}

		return s
	}
}

func (this *RuleContext) getChild(i int) Tree {
	return nil
}

func (this *RuleContext) GetParent() Tree {
	return this.parentCtx
}

func (this *RuleContext) getChildCount() int {
	return 0
}

func (this *RuleContext) accept(Visitor ParseTreeVisitor) interface{} {
	return Visitor.VisitChildren(this)
}

//need to manage circular dependencies, so export now

// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if this is a leaf.
//

func (this *RuleContext) toStringTree(ruleNames []string, recog IRecognizer) string {
	return TreestoStringTree(this, ruleNames, recog)
}

func (this *RuleContext) toString(ruleNames []string, stop IRuleContext) string {

	var p IRuleContext = this
	var s = "["
	for p != nil && p != stop {
		if ruleNames == nil {
			if !p.isEmpty() {
				s += strconv.Itoa(p.getInvokingState())
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
		if p.GetParent() != nil && (ruleNames != nil || !p.GetParent().(IRuleContext).isEmpty()) {
			s += " "
		}
		pi := p.GetParent()
		if (pi != nil){
			p = pi.(IRuleContext)
		} else {
			p = nil
		}
	}
	s += "]"
	return s
}
