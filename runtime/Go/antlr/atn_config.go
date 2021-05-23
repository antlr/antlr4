// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import "fmt"

type comparable interface{ equals(other interface{}) bool }

// ATNConfig is a tuple: (ATN state, predicted alt, syntactic, semantic
// context). The syntactic context is a graph-structured stack node whose
// path(s) to the root is the rule invocation(s) chain used to arrive at the
// state. The semantic context is the tree of semantic predicates encountered
// before reaching an ATN state.
type ATNConfig interface {
	comparable

	hash() int

	GetState() ATNState
	GetAlt() int
	GetSemanticContext() SemanticContext

	GetContext() PredictionContext
	SetContext(PredictionContext)

	GetReachesIntoOuterContext() int
	SetReachesIntoOuterContext(int)

	String() string

	getPrecedenceFilterSuppressed() bool
	setPrecedenceFilterSuppressed(bool)
}

// BaseATNConfig is the base type for ATN configurations.
type BaseATNConfig struct {
	precedenceFilterSuppressed bool
	state                      ATNState
	alt                        int
	context                    PredictionContext
	semanticContext            SemanticContext
	reachesIntoOuterContext    int
}

// CloneATNConfig clones an old instance of BaseATNConfig and returns that
// clone.
func CloneATNConfig(old *BaseATNConfig) *BaseATNConfig {
	return &BaseATNConfig{
		state:                   old.state,
		alt:                     old.alt,
		context:                 old.context,
		semanticContext:         old.semanticContext,
		reachesIntoOuterContext: old.reachesIntoOuterContext,
	}
}

// BaseAtnConfigDefaultContext creates a new instance of BaseATNConfig.
func BaseAtnConfigDefaultContext(state ATNState, alt int, context PredictionContext) *BaseATNConfig {
	return BaseATNConfigContext(state, alt, context, SemanticContextNone)
}

// BaseATNConfigContext creates a new instance of BaseATNConfig.
func BaseATNConfigContext(state ATNState, alt int, context PredictionContext, semanticContext SemanticContext) *BaseATNConfig {
	if semanticContext == nil {
		panic("semanticContext cannot be nil") // TODO: Necessary?
	}

	return &BaseATNConfig{
		state:           state,
		alt:             alt,
		context:         context,
		semanticContext: semanticContext,
	}
}

// BaseATNConfigState creates a new instance of BaseATNConfig.
func BaseATNConfigState(c ATNConfig, state ATNState) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
}

// ATNConfigStateContext creates a new instance of BaseATNConfig.
func ATNConfigStateContext(c ATNConfig, state ATNState, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), semanticContext)
}

// ATNConfigWithContext creates a new instance of BaseATNConfig.
func ATNConfigWithContext(c ATNConfig, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, c.GetState(), c.GetContext(), semanticContext)
}

// ATNConfigWithStateContext creates a new instance of BaseATNConfig.
func ATNConfigWithStateContext(
	c ATNConfig,
	state ATNState,
	context PredictionContext,
) *BaseATNConfig {
	return NewBaseATNConfig(c, state, context, c.GetSemanticContext())
}

// NewBaseATNConfig creates a new instance of BaseATNConfig.
func NewBaseATNConfig(
	c ATNConfig,
	state ATNState,
	context PredictionContext,
	semanticContext SemanticContext,
) *BaseATNConfig {
	if semanticContext == nil {
		panic("semanticContext cannot be nil")
	}

	return &BaseATNConfig{
		state:                      state,
		alt:                        c.GetAlt(),
		context:                    context,
		semanticContext:            semanticContext,
		reachesIntoOuterContext:    c.GetReachesIntoOuterContext(),
		precedenceFilterSuppressed: c.getPrecedenceFilterSuppressed(),
	}
}

func (b *BaseATNConfig) getPrecedenceFilterSuppressed() bool {
	return b.precedenceFilterSuppressed
}

func (b *BaseATNConfig) setPrecedenceFilterSuppressed(v bool) {
	b.precedenceFilterSuppressed = v
}

// GetState returns the ATNState of this config.
func (b *BaseATNConfig) GetState() ATNState {
	return b.state
}

// GetAlt returns the alternative number of this config.
func (b *BaseATNConfig) GetAlt() int { return b.alt }

// SetContext sets the context of this config.
func (b *BaseATNConfig) SetContext(v PredictionContext) {
	b.context = v
}

// GetContext returns the context of this config.
func (b *BaseATNConfig) GetContext() PredictionContext { return b.context }

// GetSemanticContext returns the semantic context of this config.
func (b *BaseATNConfig) GetSemanticContext() SemanticContext {
	return b.semanticContext
}

// TODO: Based on usage, I *think* this is how it works

// GetReachesIntoOuterContext returns how many outer contexts this config
// reaches into.
func (b *BaseATNConfig) GetReachesIntoOuterContext() int {
	return b.reachesIntoOuterContext
}

// SetReachesIntoOuterContext sets how many outer contexts this config
// reaches into
func (b *BaseATNConfig) SetReachesIntoOuterContext(v int) {
	b.reachesIntoOuterContext = v
}

// An ATN configuration is equal to another if both have the same state, they
// predict the same alternative, and syntactic/semantic contexts are the same.
func (b *BaseATNConfig) equals(o interface{}) bool {
	if b == o {
		return true
	}

	var other, ok = o.(*BaseATNConfig)

	if !ok {
		return false
	}

	var equal bool

	if b.context == nil {
		equal = other.context == nil
	} else {
		equal = b.context.equals(other.context)
	}

	var (
		nums = b.state.GetStateNumber() == other.state.GetStateNumber()
		alts = b.alt == other.alt
		cons = b.semanticContext.equals(other.semanticContext)
		sups = b.precedenceFilterSuppressed == other.precedenceFilterSuppressed
	)

	return nums && alts && cons && sups && equal
}

func (b *BaseATNConfig) hash() int {
	var c int
	if b.context != nil {
		c = b.context.hash()
	}

	h := murmurInit(7)
	h = murmurUpdate(h, b.state.GetStateNumber())
	h = murmurUpdate(h, b.alt)
	h = murmurUpdate(h, c)
	h = murmurUpdate(h, b.semanticContext.hash())
	return murmurFinish(h, 4)
}

// String implements the Stringer interface. The information is in the format:
//
//		(state,alt,[context],semanticContext,reachesIntoOuterContext)
func (b *BaseATNConfig) String() string {
	var s1, s2, s3 string

	if b.context != nil {
		s1 = fmt.Sprintf(",[%s]", b.context)
	}

	if b.semanticContext != SemanticContextNone {
		s2 = fmt.Sprint(",", b.semanticContext)
	}

	if b.reachesIntoOuterContext > 0 {
		s3 = fmt.Sprint(",up=", b.reachesIntoOuterContext)
	}

	return fmt.Sprintf("(%v,%v%v%v%v)", b.state, b.alt, s1, s2, s3)
}

// LexerATNConfig embeds BaseATNConfig for lexers.
type LexerATNConfig struct {
	*BaseATNConfig
	lexerActionExecutor            *LexerActionExecutor
	passedThroughNonGreedyDecision bool
}

// NewLexerATNConfig5 creates a new instance of LexerATNConfig.
func NewLexerATNConfig5(state ATNState, alt int, context PredictionContext, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig:       BaseATNConfigContext(state, alt, context, SemanticContextNone),
		lexerActionExecutor: lexerActionExecutor,
	}
}

// NewLexerATNConfig4 creates a new instance of LexerATNConfig.
func NewLexerATNConfig4(c *LexerATNConfig, state ATNState) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig:                  NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext()),
		lexerActionExecutor:            c.lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
}

// NewLexerATNConfig3 creates a new instance of LexerATNConfig.
func NewLexerATNConfig3(c *LexerATNConfig, state ATNState, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig:                  NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext()),
		lexerActionExecutor:            lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
}

// NewLexerATNConfig2 creates a new instance of LexerATNConfig.
func NewLexerATNConfig2(c *LexerATNConfig, state ATNState, context PredictionContext) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig: NewBaseATNConfig(
			c,
			state,
			context,
			c.GetSemanticContext(),
		),
		lexerActionExecutor:            c.lexerActionExecutor,
		passedThroughNonGreedyDecision: checkNonGreedyDecision(c, state),
	}
}

// NewLexerATNConfig creates a new instance of LexerATNConfig.
func NewLexerATNConfig(state ATNState, alt int, context PredictionContext) *LexerATNConfig {
	return &LexerATNConfig{
		BaseATNConfig: BaseATNConfigContext(
			state,
			alt,
			context,
			SemanticContextNone,
		),
	}
}

func (l *LexerATNConfig) hash() int {
	var f int
	if l.passedThroughNonGreedyDecision {
		f = 1
	} else {
		f = 0
	}
	h := murmurInit(7)
	h = murmurUpdate(h, l.state.hash())
	h = murmurUpdate(h, l.alt)
	h = murmurUpdate(h, l.context.hash())
	h = murmurUpdate(h, l.semanticContext.hash())
	h = murmurUpdate(h, f)
	h = murmurUpdate(h, l.lexerActionExecutor.hash())
	h = murmurFinish(h, 6)
	return h
}

func (l *LexerATNConfig) equals(other interface{}) bool {
	var othert, ok = other.(*LexerATNConfig)

	if l == other {
		return true
	} else if !ok {
		return false
	} else if l.passedThroughNonGreedyDecision != othert.passedThroughNonGreedyDecision {
		return false
	}

	var b bool

	if l.lexerActionExecutor != nil {
		b = !l.lexerActionExecutor.equals(othert.lexerActionExecutor)
	} else {
		b = othert.lexerActionExecutor != nil
	}

	if b {
		return false
	}

	return l.BaseATNConfig.equals(othert.BaseATNConfig)
}

func checkNonGreedyDecision(source *LexerATNConfig, target ATNState) bool {
	var ds, ok = target.(DecisionState)

	return source.passedThroughNonGreedyDecision || (ok && ds.getNonGreedy())
}
