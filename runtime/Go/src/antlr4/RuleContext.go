package antlr4

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

	GetInvokingState() int
	SetInvokingState(int)

	GetRuleIndex() int
	IsEmpty() bool

	String([]string, RuleContext) string
}

type BaseRuleContext struct {

	parentCtx     RuleContext
	invokingState int
	RuleIndex     int

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

func (this *BaseRuleContext) GetBaseRuleContext() *BaseRuleContext {
	return this
}

func (this *BaseRuleContext) SetParent(v Tree) {
	this.parentCtx = v.(RuleContext)
}

func (this *BaseRuleContext) GetInvokingState() int {
	return this.invokingState
}

func (this *BaseRuleContext) SetInvokingState(t int) {
	this.invokingState = t
}

func (this *BaseRuleContext) GetRuleIndex() int {
	return this.RuleIndex
}

// A context is empty if there is no invoking state meaning nobody call
// current context.
func (this *BaseRuleContext) IsEmpty() bool {
	return this.invokingState == -1
}

// Return the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
// <p>
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of this
// method.
//

func (this *BaseRuleContext) GetParent() Tree {
	return this.parentCtx
}
