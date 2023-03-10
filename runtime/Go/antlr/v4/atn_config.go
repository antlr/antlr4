// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
)

// ATNConfig is a tuple: (ATN state, predicted alt, syntactic, semantic
// context). The syntactic context is a graph-structured stack node whose
// path(s) to the root is the rule invocation(s) chain used to arrive in the
// state. The semantic context is the tree of semantic predicates encountered
// before reaching an ATN state.
type ATNConfig interface {

	// Equals compares this ATNConfig to another for equality
	Equals(o Collectable[ATNConfig]) bool

	// Hash returns the hash code for this ATNConfig for use in maps and comparisons
	Hash() int

	// GetState returns the ATN state associated with this configuration
	GetState() ATNState
	// GetAlt returns the alternative associated with this configuration
	GetAlt() int
	// GetSemanticContext returns the semantic context associated with this configuration
	GetSemanticContext() SemanticContext

	// GetContext returns the rule invocation stack associated with this configuration
	GetContext() PredictionContext
	// SetContext sets the rule invocation stack associated with this configuration
	SetContext(PredictionContext)

	// GetReachesIntoOuterContext returns the count of references to an outer context from this configuration
	GetReachesIntoOuterContext() int
	// SetReachesIntoOuterContext sets the count of references to an outer context from this configuration
	SetReachesIntoOuterContext(int)

	// String returns a string representation of the configuration
	String() string

	getPrecedenceFilterSuppressed() bool
	setPrecedenceFilterSuppressed(bool)
}

// BaseATNConfig is a base implementation of ATNConfig. Thi si s done to emulate Java's ability to have multiple
// constructors for a single class. This is not idiomatic Go, but it works for now.
// TODO: this isn't the way to do this I think, but it will take time to rework - JI Also, getters and setters are not Go. Might be better to just access the fields, though the compiler will probably eliminate the calls
type BaseATNConfig struct {
	precedenceFilterSuppressed bool
	state                      ATNState
	alt                        int
	context                    PredictionContext
	semanticContext            SemanticContext
	reachesIntoOuterContext    int
}

//goland:noinspection GoUnusedExportedFunction
func NewBaseATNConfig7(old *BaseATNConfig) ATNConfig { // TODO: Dup - maybe delete this
	return &BaseATNConfig{
		state:                   old.state,
		alt:                     old.alt,
		context:                 old.context,
		semanticContext:         old.semanticContext,
		reachesIntoOuterContext: old.reachesIntoOuterContext,
	}
}

// NewBaseATNConfig6 creates a new BaseATNConfig instance given a state, alt and context only
func NewBaseATNConfig6(state ATNState, alt int, context PredictionContext) *BaseATNConfig {
	return NewBaseATNConfig5(state, alt, context, SemanticContextNone)
}

// NewBaseATNConfig5 creates a new BaseATNConfig instance given a state, alt, context and semantic context
func NewBaseATNConfig5(state ATNState, alt int, context PredictionContext, semanticContext SemanticContext) *BaseATNConfig {
	if semanticContext == nil {
		panic("semanticContext cannot be nil") // TODO: Necessary?
	}

	return &BaseATNConfig{state: state, alt: alt, context: context, semanticContext: semanticContext}
}

// NewBaseATNConfig4 creates a new BaseATNConfig instance given an existing config, and a state only
func NewBaseATNConfig4(c ATNConfig, state ATNState) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
}

// NewBaseATNConfig3 creates a new BaseATNConfig instance given an existing config, a state and a semantic context
func NewBaseATNConfig3(c ATNConfig, state ATNState, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), semanticContext)
}

// NewBaseATNConfig2 creates a new BaseATNConfig instance given an existing config, and a context only
func NewBaseATNConfig2(c ATNConfig, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, c.GetState(), c.GetContext(), semanticContext)
}

// NewBaseATNConfig1 creates a new BaseATNConfig instance given an existing config, a state, and a context only
func NewBaseATNConfig1(c ATNConfig, state ATNState, context PredictionContext) *BaseATNConfig {
	return NewBaseATNConfig(c, state, context, c.GetSemanticContext())
}

// NewBaseATNConfig creates a new BaseATNConfig instance given an existing config, a state, a context and a semantic context, other 'constructors'
// are just wrappers around this one.
func NewBaseATNConfig(c ATNConfig, state ATNState, context PredictionContext, semanticContext SemanticContext) *BaseATNConfig {
	if semanticContext == nil {
		panic("semanticContext cannot be nil") // TODO: Remove this - probably put here for some bug that is now fixed
	}

	b := &BaseATNConfig{}
	b.InitBaseATNConfig(c, state, c.GetAlt(), context, semanticContext)

	return b
}

func (b *BaseATNConfig) InitBaseATNConfig(c ATNConfig, state ATNState, alt int, context PredictionContext, semanticContext SemanticContext) {

	b.state = state
	b.alt = alt
	b.context = context
	b.semanticContext = semanticContext
	b.reachesIntoOuterContext = c.GetReachesIntoOuterContext()
	b.precedenceFilterSuppressed = c.getPrecedenceFilterSuppressed()
}

func (b *BaseATNConfig) getPrecedenceFilterSuppressed() bool {
	return b.precedenceFilterSuppressed
}

func (b *BaseATNConfig) setPrecedenceFilterSuppressed(v bool) {
	b.precedenceFilterSuppressed = v
}

// GetState returns the ATN state associated with this configuration
func (b *BaseATNConfig) GetState() ATNState {
	return b.state
}

// GetAlt returns the alternative associated with this configuration
func (b *BaseATNConfig) GetAlt() int {
	return b.alt
}

// SetContext sets the rule invocation stack associated with this configuration
func (b *BaseATNConfig) SetContext(v PredictionContext) {
	b.context = v
}

// GetContext returns the rule invocation stack associated with this configuration
func (b *BaseATNConfig) GetContext() PredictionContext {
	return b.context
}

// GetSemanticContext returns the semantic context associated with this configuration
func (b *BaseATNConfig) GetSemanticContext() SemanticContext {
	return b.semanticContext
}

// GetReachesIntoOuterContext returns the count of references to an outer context from this configuration
func (b *BaseATNConfig) GetReachesIntoOuterContext() int {
	return b.reachesIntoOuterContext
}

// SetReachesIntoOuterContext sets the count of references to an outer context from this configuration
func (b *BaseATNConfig) SetReachesIntoOuterContext(v int) {
	b.reachesIntoOuterContext = v
}

// Equals is the default comparison function for an ATNConfig when no specialist implementation is required
// for a collection.
//
// An ATN configuration is equal to another if both have the same state, they
// predict the same alternative, and syntactic/semantic contexts are the same.
func (b *BaseATNConfig) Equals(o Collectable[ATNConfig]) bool {
	if b == o {
		return true
	} else if o == nil {
		return false
	}

	var other, ok = o.(*BaseATNConfig)

	if !ok {
		return false
	}

	var equal bool

	if b.context == nil {
		equal = other.context == nil
	} else {
		equal = b.context.Equals(other.context)
	}

	var (
		nums = b.state.GetStateNumber() == other.state.GetStateNumber()
		alts = b.alt == other.alt
		cons = b.semanticContext.Equals(other.semanticContext)
		sups = b.precedenceFilterSuppressed == other.precedenceFilterSuppressed
	)

	return nums && alts && cons && sups && equal
}

// Hash is the default hash function for BaseATNConfig, when no specialist hash function
// is required for a collection
func (b *BaseATNConfig) Hash() int {
	var c int
	if b.context != nil {
		c = b.context.Hash()
	}

	h := murmurInit(7)
	h = murmurUpdate(h, b.state.GetStateNumber())
	h = murmurUpdate(h, b.alt)
	h = murmurUpdate(h, c)
	h = murmurUpdate(h, b.semanticContext.Hash())
	return murmurFinish(h, 4)
}

// String returns a string representation of the BaseATNConfig, usually used for debugging purposes
func (b *BaseATNConfig) String() string {
	var s1, s2, s3 string

	if b.context != nil {
		s1 = ",[" + fmt.Sprint(b.context) + "]"
	}

	if b.semanticContext != SemanticContextNone {
		s2 = "," + fmt.Sprint(b.semanticContext)
	}

	if b.reachesIntoOuterContext > 0 {
		s3 = ",up=" + fmt.Sprint(b.reachesIntoOuterContext)
	}

	return fmt.Sprintf("(%v,%v%v%v%v)", b.state, b.alt, s1, s2, s3)
}

// LexerATNConfig represents a lexer ATN configuration which tracks the lexer action, and which "inherits" from the
// BaseATNConfig struct.
// TODO: Stop using a pointer and embed the struct instead as this saves allocations. Same for the LexerATNConfig "constructors"
type LexerATNConfig struct {
	BaseATNConfig
	lexerActionExecutor            *LexerActionExecutor
	passedThroughNonGreedyDecision bool
}

func NewLexerATNConfig6(state ATNState, alt int, context PredictionContext) *LexerATNConfig {

	return &LexerATNConfig{
		BaseATNConfig: BaseATNConfig{
			state:           state,
			alt:             alt,
			context:         context,
			semanticContext: SemanticContextNone,
		},
	}
}

func NewLexerATNConfig5(state ATNState, alt int, context PredictionContext, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig: BaseATNConfig{
			state:           state,
			alt:             alt,
			context:         context,
			semanticContext: SemanticContextNone,
		},
		lexerActionExecutor: lexerActionExecutor,
	}
}

func NewLexerATNConfig4(c *LexerATNConfig, state ATNState) *LexerATNConfig {
	lac := &LexerATNConfig{

		lexerActionExecutor:            c.lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
	lac.BaseATNConfig.InitBaseATNConfig(c, state, c.GetAlt(), c.GetContext(), c.GetSemanticContext())
	return lac
}

func NewLexerATNConfig3(c *LexerATNConfig, state ATNState, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {
	lac := &LexerATNConfig{
		lexerActionExecutor:            lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
	lac.BaseATNConfig.InitBaseATNConfig(c, state, c.GetAlt(), c.GetContext(), c.GetSemanticContext())
	return lac
}

func NewLexerATNConfig2(c *LexerATNConfig, state ATNState, context PredictionContext) *LexerATNConfig {
	lac := &LexerATNConfig{
		lexerActionExecutor:            c.lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
	lac.BaseATNConfig.InitBaseATNConfig(c, state, c.GetAlt(), context, c.GetSemanticContext())
	return lac
}

//goland:noinspection GoUnusedExportedFunction
func NewLexerATNConfig1(state ATNState, alt int, context PredictionContext) *LexerATNConfig {
	lac := &LexerATNConfig{
		BaseATNConfig: BaseATNConfig{
			state:           state,
			alt:             alt,
			context:         context,
			semanticContext: SemanticContextNone,
		},
	}
	return lac
}

// Hash is the default hash function for LexerATNConfig objects, it can be used directly or via
// the default comparator [ObjEqComparator].
func (l *LexerATNConfig) Hash() int {
	var f int
	if l.passedThroughNonGreedyDecision {
		f = 1
	} else {
		f = 0
	}
	h := murmurInit(7)
	h = murmurUpdate(h, l.state.GetStateNumber())
	h = murmurUpdate(h, l.alt)
	h = murmurUpdate(h, l.context.Hash())
	h = murmurUpdate(h, l.semanticContext.Hash())
	h = murmurUpdate(h, f)
	h = murmurUpdate(h, l.lexerActionExecutor.Hash())
	h = murmurFinish(h, 6)
	return h
}

// Equals is the default comparison function for LexerATNConfig objects, it can be used directly or via
// the default comparator [ObjEqComparator].
func (l *LexerATNConfig) Equals(other Collectable[ATNConfig]) bool {
	if l == other {
		return true
	}
	var otherT, ok = other.(*LexerATNConfig)

	if l == other {
		return true
	} else if !ok {
		return false
	} else if l.passedThroughNonGreedyDecision != otherT.passedThroughNonGreedyDecision {
		return false
	}

	var b bool

	if l.lexerActionExecutor != nil {
		b = !l.lexerActionExecutor.Equals(otherT.lexerActionExecutor)
	} else {
		b = otherT.lexerActionExecutor != nil
	}

	if b {
		return false
	}

	return l.BaseATNConfig.Equals(&otherT.BaseATNConfig)
}

func checkNonGreedyDecision(source *LexerATNConfig, target ATNState) bool {
	var ds, ok = target.(DecisionState)

	return source.passedThroughNonGreedyDecision || (ok && ds.getNonGreedy())
}
