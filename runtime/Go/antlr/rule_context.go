// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// RuleContext is a record of a single rule invocation. It knows
// which context invoked it, if any. If there is no parent context, then
// naturally the invoking state is not valid.  The parent link
// provides a chain upwards from the current rule invocation to the root
// of the invocation tree, forming a stack. We actually carry no
// information about the rule associated with b context (except
// when parsing). We keep only the state number of the invoking state from
// the ATN submachine that invoked b. Contrast b with the s
// pointer inside ParserRuleContext that tracks the current state
// being "executed" for the current rule.
//
// The parent contexts are useful for computing lookahead sets and
// getting error information.
//
// These objects are used during parsing and prediction.
// For the special case of parsers, we use the subclass
// ParserRuleContext.
//
// @see ParserRuleContext
type RuleContext interface {
	RuleNode

	GetInvokingState() int
	SetInvokingState(int)

	GetRuleIndex() int
	IsEmpty() bool

	GetAltNumber() int
	SetAltNumber(altNumber int)

	String([]string, RuleContext) string
}

// BaseRuleContext is the base implementation for RuleContext.
type BaseRuleContext struct {
	// What context invoked b rule?
	parentCtx     RuleContext
	invokingState int
	RuleIndex     int
}

// NewBaseRuleContext returns a new instance of BaseRuleContext.
func NewBaseRuleContext(parent RuleContext, invokingState int) *BaseRuleContext {

	rn := &BaseRuleContext{
		parentCtx: parent,
	}

	// What state invoked the rule associated with b context?
	// The "return address" is the followState of invokingState
	// If parent is nil, b should be -1.
	if parent == nil {
		rn.invokingState = -1
	} else {
		rn.invokingState = invokingState
	}

	return rn
}

// GetBaseRuleContext returns the object itself.
func (b *BaseRuleContext) GetBaseRuleContext() *BaseRuleContext {
	return b
}

// SetParent sets the parent node of this context.
func (b *BaseRuleContext) SetParent(v Tree) {
	if v == nil {
		b.parentCtx = nil
	} else {
		b.parentCtx = v.(RuleContext)
	}
}

// GetInvokingState returns the invoking state of this context.
func (b *BaseRuleContext) GetInvokingState() int {
	return b.invokingState
}

// SetInvokingState sets the invoking state of this context.
func (b *BaseRuleContext) SetInvokingState(t int) {
	b.invokingState = t
}

// GetRuleIndex returns the rule index.
func (b *BaseRuleContext) GetRuleIndex() int {
	return b.RuleIndex
}

// GetAltNumber always returns ATNInvalidAltNumber
func (b *BaseRuleContext) GetAltNumber() int {
	return ATNInvalidAltNumber
}

// SetAltNumber does nothing.
func (b *BaseRuleContext) SetAltNumber(altNumber int) {}

// IsEmpty returns tru if there is no invoking state meaning nobody call
// current context.
func (b *BaseRuleContext) IsEmpty() bool {
	return b.invokingState == -1
}

// GetParent returns the combined text of all child nodes. This method only considers
// tokens which have been added to the parse tree.
//
// Since tokens on hidden channels (e.g. whitespace or comments) are not
// added to the parse trees, they will not appear in the output of b method.
func (b *BaseRuleContext) GetParent() Tree {
	return b.parentCtx
}
