package antlr4

import (
	"fmt"
	"reflect"
	"strconv"
)

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
//

type ATNConfig interface {
	Hasher

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
	state ATNState
	alt                        int
	context PredictionContext
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

	if (semanticContext == nil){
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

	if (semanticContext == nil){
		panic("SemanticContext cannot be null!")
	}

	a.state = state
	a.alt = c.GetAlt()
	a.context = context
	a.semanticContext = semanticContext
	a.reachesIntoOuterContext = c.GetReachesIntoOuterContext()

	return a
}

func (this *BaseATNConfig) getPrecedenceFilterSuppressed() bool {
	return this.precedenceFilterSuppressed
}

func (this *BaseATNConfig) setPrecedenceFilterSuppressed(v bool) {
	this.precedenceFilterSuppressed = v
}

func (this *BaseATNConfig) GetState() ATNState {
	return this.state
}

func (this *BaseATNConfig) GetAlt() int {
	return this.alt
}

func (this *BaseATNConfig) SetContext(v PredictionContext) {
	this.context = v
}
func (this *BaseATNConfig) GetContext() PredictionContext {
	return this.context
}

func (this *BaseATNConfig) GetSemanticContext() SemanticContext {
	return this.semanticContext
}

func (this *BaseATNConfig) GetReachesIntoOuterContext() int {
	return this.reachesIntoOuterContext
}

func (this *BaseATNConfig) SetReachesIntoOuterContext(v int) {
	this.reachesIntoOuterContext = v
}

// An ATN configuration is equal to another if both have
//  the same state, they predict the same alternative, and
//  syntactic/semantic contexts are the same.
///
func (this *BaseATNConfig) equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*BaseATNConfig); !ok {
		return false
	} else {
		return reflect.DeepEqual(this, other)
	}
}

func (this *BaseATNConfig) shortHash() string {
	return strconv.Itoa(this.state.GetStateNumber()) + "/" + strconv.Itoa(this.alt) + "/" + this.semanticContext.String()
}

func (this *BaseATNConfig) Hash() string {

	var c string
	if this.context == nil {
		c = ""
	} else {
		c = this.context.Hash()
	}

	return strconv.Itoa(this.state.GetStateNumber()) + "/" + strconv.Itoa(this.alt) + "/" + c + "/" + this.semanticContext.String()
}

func (this *BaseATNConfig) String() string {

	var a string
	if this.context != nil {
		a = ",[" + fmt.Sprint(this.context) + "]"
	}

	var b string
	if this.semanticContext != SemanticContextNone {
		b = "," + fmt.Sprint(this.semanticContext)
	}

	var c string
	if this.reachesIntoOuterContext > 0 {
		c = ",up=" + fmt.Sprint(this.reachesIntoOuterContext)
	}

	return "(" + fmt.Sprint(this.state) + "," + strconv.Itoa(this.alt) + a + b + c + ")"
}





type LexerATNConfig struct {
	*BaseATNConfig

	lexerActionExecutor            *LexerActionExecutor
	passedThroughNonGreedyDecision bool
}

func NewLexerATNConfig6(state ATNState, alt int, context PredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)

	this.passedThroughNonGreedyDecision = false
	this.lexerActionExecutor = nil
	return this
}

func NewLexerATNConfig5(state ATNState, alt int, context PredictionContext, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)
	this.lexerActionExecutor = lexerActionExecutor
	this.passedThroughNonGreedyDecision = false
	return this
}

func NewLexerATNConfig4(c *LexerATNConfig, state ATNState) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
	this.lexerActionExecutor = c.lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}

func NewLexerATNConfig3(c *LexerATNConfig, state ATNState, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig(c, state, c.GetContext(), c.GetSemanticContext())
	this.lexerActionExecutor = lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}

func NewLexerATNConfig2(c *LexerATNConfig, state ATNState, context PredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig(c, state, context, c.GetSemanticContext())
	this.lexerActionExecutor = c.lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}

func NewLexerATNConfig1(state ATNState, alt int, context PredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.BaseATNConfig = NewBaseATNConfig5(state, alt, context, SemanticContextNone)

	this.lexerActionExecutor = nil
	this.passedThroughNonGreedyDecision = false

	return this
}

func (this *LexerATNConfig) Hash() string {
	var f string

	if this.passedThroughNonGreedyDecision {
		f = "1"
	} else {
		f = "0"
	}

	return strconv.Itoa(this.state.GetStateNumber()) + strconv.Itoa(this.alt) + fmt.Sprint(this.context) +
		fmt.Sprint(this.semanticContext) + f + fmt.Sprint(this.lexerActionExecutor)
}

func (this *LexerATNConfig) equals(other interface{}) bool {

	othert, ok := other.(*LexerATNConfig)

	if this == other {
		return true
	} else if !ok {
		return false
	} else if this.passedThroughNonGreedyDecision != othert.passedThroughNonGreedyDecision {
		return false
	}

	var b bool
	if this.lexerActionExecutor != nil {
		b = !this.lexerActionExecutor.equals(othert.lexerActionExecutor)
	} else {
		b = othert.lexerActionExecutor != nil
	}

	if b {
		return false
	} else {
		panic("Not implemented")
		//        return ATNConfig.prototype.equals.call(this, other)
	}
}

func checkNonGreedyDecision(source *LexerATNConfig, target ATNState) bool {
	ds, ok := target.(DecisionState)
	return source.passedThroughNonGreedyDecision || (ok && ds.getNonGreedy())
}
