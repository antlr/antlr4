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

type RuleContext interface {
	RuleNode

	getInvokingState() int
	setInvokingState(int)

	GetRuleIndex() int

	isEmpty() bool

	String([]string, RuleContext) string
}

type BaseRuleContext struct {
	parentCtx RuleContext
	invokingState int
	RuleIndex     int
	children      []Tree
}

func NewBaseRuleContext(parent RuleContext, invokingState int) *BaseRuleContext {

	rn := new(BaseRuleContext)

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

func (this *BaseRuleContext) setChildren(elems []Tree) {
	this.children = elems
}

func (this *BaseRuleContext) setParent(v Tree) {
	this.parentCtx = v.(RuleContext)
}

func (this *BaseRuleContext) getInvokingState() int {
	return this.invokingState
}

func (this *BaseRuleContext) setInvokingState(t int) {
	this.invokingState = t
}

func (this *BaseRuleContext) GetRuleIndex() int {
	return this.RuleIndex
}

func (this *BaseRuleContext) getChildren() []Tree {
	return this.children
}

func (this *BaseRuleContext) depth() int {
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
func (this *BaseRuleContext) isEmpty() bool {
	return this.invokingState == -1
}

// satisfy the ParseTree / SyntaxTree interface

func (this *BaseRuleContext) GetSourceInterval() *Interval {
	return TreeINVALID_INTERVAL
}

func (this *BaseRuleContext) getRuleContext() RuleContext {
	return this
}

func (this *BaseRuleContext) getPayload() interface{} {
	return this
}

// Return the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
// <p>
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of this
// method.
//
func (this *BaseRuleContext) GetText() string {
	if this.getChildCount() == 0 {
		return ""
	} else {
		var s string
		for _, child := range this.children {
			s += child.(RuleContext).GetText()
		}

		return s
	}
}

func (this *BaseRuleContext) getChild(i int) Tree {
	return nil
}

func (this *BaseRuleContext) GetParent() Tree {
	return this.parentCtx
}

func (this *BaseRuleContext) getChildCount() int {
	return 0
}

func (this *BaseRuleContext) accept(Visitor ParseTreeVisitor) interface{} {
	return Visitor.VisitChildren(this)
}

//need to manage circular dependencies, so export now

// Print out a whole tree, not just a node, in LISP format
// (root child1 .. childN). Print just a node if this is a leaf.
//

func (this *BaseRuleContext) StringTree(ruleNames []string, recog Recognizer) string {
	return TreesStringTree(this, ruleNames, recog)
}

func (this *BaseRuleContext) String(ruleNames []string, stop RuleContext) string {

	var p RuleContext = this
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
		if p.GetParent() != nil && (ruleNames != nil || !p.GetParent().(RuleContext).isEmpty()) {
			s += " "
		}
		pi := p.GetParent()
		if (pi != nil){
			p = pi.(RuleContext)
		} else {
			p = nil
		}
	}
	s += "]"
	return s
}
