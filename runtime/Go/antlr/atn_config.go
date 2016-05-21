package antlr

import (
	"fmt"
	//	"reflect"
	"strconv"
)

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
//

type Comparable interface {
	equals(other interface{}) bool
}

type ATNConfig interface {
	Hasher
	Comparable

	getPrecedenceFilterSuppressed() bool
	setPrecedenceFilterSuppressed(bool)

	GetState() ATNState
	GetAlt() int
	GetSemanticContext() SemanticContext

	GetContext() PredictionContext
	SetContext(PredictionContext)

	GetReachesIntoOuterContext() int
	SetReachesIntoOuterContext(int)

	String() string

	shortHash() string
}

type BaseATNConfig struct {
	precedenceFilterSuppressed bool
	state                      ATNState
	alt                        int
	context                    PredictionContext
	semanticContext            SemanticContext
	reachesIntoOuterContext    int
}

func NewBaseATNConfig7(old *BaseATNConfig) *BaseATNConfig { // dup
	a := new(BaseATNConfig)
	a.state = old.state
	a.alt = old.alt
	a.context = old.context
	a.semanticContext = old.semanticContext
	a.reachesIntoOuterContext = old.reachesIntoOuterContext
	return a
}

func NewBaseATNConfig6(state ATNState, alt int, context PredictionContext) *BaseATNConfig {
	return NewBaseATNConfig5(state, alt, context, SemanticContextNone)
}

func NewBaseATNConfig5(state ATNState, alt int, context PredictionContext, semanticContext SemanticContext) *BaseATNConfig {
	a := new(BaseATNConfig)

	if semanticContext == nil {
		panic("SemanticContext cannot be null!")
	}

	a.state = state
	a.alt = alt
	a.context = context
	a.semanticContext = semanticContext

	return a
}

func NewBaseATNConfig4(c ATNConfig, state ATNState) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
}

func NewBaseATNConfig3(c ATNConfig, state ATNState, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, state, c.GetContext(), semanticContext)
}

func NewBaseATNConfig2(c ATNConfig, semanticContext SemanticContext) *BaseATNConfig {
	return NewBaseATNConfig(c, c.GetState(), c.GetContext(), semanticContext)
}

func NewBaseATNConfig1(c ATNConfig, state ATNState, context PredictionContext) *BaseATNConfig {
	return NewBaseATNConfig(c, state, context, c.GetSemanticContext())
}

func NewBaseATNConfig(c ATNConfig, state ATNState, context PredictionContext, semanticContext SemanticContext) *BaseATNConfig {
	a := new(BaseATNConfig)

	if semanticContext == nil {
		panic("SemanticContext cannot be null!")
	}

	a.state = state
	a.alt = c.GetAlt()
	a.context = context
	a.semanticContext = semanticContext
	a.reachesIntoOuterContext = c.GetReachesIntoOuterContext()

	return a
}

func (b *BaseATNConfig) getPrecedenceFilterSuppressed() bool {
	return b.precedenceFilterSuppressed
}

func (b *BaseATNConfig) setPrecedenceFilterSuppressed(v bool) {
	b.precedenceFilterSuppressed = v
}

func (b *BaseATNConfig) GetState() ATNState {
	return b.state
}

func (b *BaseATNConfig) GetAlt() int {
	return b.alt
}

func (b *BaseATNConfig) SetContext(v PredictionContext) {
	b.context = v
}
func (b *BaseATNConfig) GetContext() PredictionContext {
	return b.context
}

func (b *BaseATNConfig) GetSemanticContext() SemanticContext {
	return b.semanticContext
}

func (b *BaseATNConfig) GetReachesIntoOuterContext() int {
	return b.reachesIntoOuterContext
}

func (b *BaseATNConfig) SetReachesIntoOuterContext(v int) {
	b.reachesIntoOuterContext = v
}

// An ATN configuration is equal to another if both have
//  the same state, they predict the same alternative, and
//  syntactic/semantic contexts are the same.
///
func (b *BaseATNConfig) equals(o interface{}) bool {

	if b == o {
		return true
	}

	other, ok := o.(*BaseATNConfig)

	if !ok {
		return false
	}

	var equal bool
	if b.context == nil {
		equal = other.context == nil
	} else {
		equal = b.context.equals(other.context)
	}

	return b.state.GetStateNumber() == other.state.GetStateNumber() &&
		b.alt == other.alt &&
		b.semanticContext.equals(other.semanticContext) &&
		b.precedenceFilterSuppressed == other.precedenceFilterSuppressed &&
		equal
}

func (b *BaseATNConfig) shortHash() string {
	return strconv.Itoa(b.state.GetStateNumber()) + "/" + strconv.Itoa(b.alt) + "/" + b.semanticContext.String()
}

func (b *BaseATNConfig) Hash() string {

	var c string
	if b.context == nil {
		c = ""
	} else {
		c = b.context.Hash()
	}

	return strconv.Itoa(b.state.GetStateNumber()) + "/" + strconv.Itoa(b.alt) + "/" + c + "/" + b.semanticContext.String()
}

func (b *BaseATNConfig) String() string {

	var s1 string
	if b.context != nil {
		s1 = ",[" + fmt.Sprint(b.context) + "]"
	}

	var s2 string
	if b.semanticContext != SemanticContextNone {
		s2 = "," + fmt.Sprint(b.semanticContext)
	}

	var s3 string
	if b.reachesIntoOuterContext > 0 {
		s3 = ",up=" + fmt.Sprint(b.reachesIntoOuterContext)
	}

	return "(" + fmt.Sprint(b.state) + "," + strconv.Itoa(b.alt) + s1 + s2 + s3 + ")"
}

type LexerATNConfig struct {
	*BaseATNConfig

	lexerActionExecutor            *LexerActionExecutor
	passedThroughNonGreedyDecision bool
}

func NewLexerATNConfig6(state ATNState, alt int, context PredictionContext) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)

	l.passedThroughNonGreedyDecision = false
	l.lexerActionExecutor = nil
	return l
}

func NewLexerATNConfig5(state ATNState, alt int, context PredictionContext, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)
	l.lexerActionExecutor = lexerActionExecutor
	l.passedThroughNonGreedyDecision = false
	return l
}

func NewLexerATNConfig4(c *LexerATNConfig, state ATNState) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
	l.lexerActionExecutor = c.lexerActionExecutor
	l.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return l
}

func NewLexerATNConfig3(c *LexerATNConfig, state ATNState, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
	l.lexerActionExecutor = lexerActionExecutor
	l.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return l
}

func NewLexerATNConfig2(c *LexerATNConfig, state ATNState, context PredictionContext) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig(c, state, context, c.GetSemanticContext())
	l.lexerActionExecutor = c.lexerActionExecutor
	l.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return l
}

func NewLexerATNConfig1(state ATNState, alt int, context PredictionContext) *LexerATNConfig {

	l := new(LexerATNConfig)

	l.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)

	l.lexerActionExecutor = nil
	l.passedThroughNonGreedyDecision = false

	return l
}

func (l *LexerATNConfig) Hash() string {
	var f string

	if l.passedThroughNonGreedyDecision {
		f = "1"
	} else {
		f = "0"
	}

	return strconv.Itoa(l.state.GetStateNumber()) + strconv.Itoa(l.alt) + fmt.Sprint(l.context) +
		fmt.Sprint(l.semanticContext) + f + fmt.Sprint(l.lexerActionExecutor)
}

func (l *LexerATNConfig) equals(other interface{}) bool {

	othert, ok := other.(*LexerATNConfig)

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
	} else {
		return l.BaseATNConfig.equals(othert.BaseATNConfig)
	}
}

func checkNonGreedyDecision(source *LexerATNConfig, target ATNState) bool {
	ds, ok := target.(DecisionState)
	return source.passedThroughNonGreedyDecision || (ok && ds.getNonGreedy())
}
