package antlr4

import (
	"reflect"
	"fmt"
	"strconv"
)

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
//

type IATNConfig interface {
	getPrecedenceFilterSuppressed() bool
	setPrecedenceFilterSuppressed(bool)

	getState() IATNState
	getAlt() int
	getSemanticContext() SemanticContext

	getContext() IPredictionContext
	setContext(IPredictionContext)

	getReachesIntoOuterContext() int
	setReachesIntoOuterContext(int)
}

type ATNConfig struct {
	precedenceFilterSuppressed bool
	state IATNState
	alt int
	context IPredictionContext
	semanticContext SemanticContext
	reachesIntoOuterContext int
}

func NewATNConfig7(old *ATNConfig) *ATNConfig { // dup
	a := new(ATNConfig)
	a.state = old.state;
	a.alt = old.alt;
	a.context = old.context;
	a.semanticContext = old.semanticContext;
	a.reachesIntoOuterContext = old.reachesIntoOuterContext;
	return a
}

func NewATNConfig6(state IATNState, alt int, context IPredictionContext) *ATNConfig {
	return NewATNConfig5(state, alt, context, SemanticContextNONE);
}

func NewATNConfig5(state IATNState,
alt int, context IPredictionContext, semanticContext SemanticContext) *ATNConfig {
	a := new(ATNConfig)

	a.InitATNConfig2(state, alt, context, semanticContext)
	return a
}

func NewATNConfig4(c *ATNConfig, state IATNState) *ATNConfig {
	return NewATNConfig(c, state, c.context, c.semanticContext);
}

func NewATNConfig3(c *ATNConfig, state IATNState, semanticContext SemanticContext) *ATNConfig {
	return NewATNConfig(c, state, c.context, semanticContext);
}

func NewATNConfig2(c *ATNConfig, semanticContext SemanticContext) *ATNConfig {
	return NewATNConfig(c, c.state, c.context, semanticContext);
}

func NewATNConfig1(c *ATNConfig, state IATNState, context IPredictionContext) *ATNConfig {
	return NewATNConfig(c, state, context, c.semanticContext);
}

func NewATNConfig(c *ATNConfig, state IATNState, context IPredictionContext, semanticContext SemanticContext) *ATNConfig {
	a := new(ATNConfig)

	a.InitATNConfig(c, state, context, semanticContext)
	return a
}

func (this *ATNConfig) getPrecedenceFilterSuppressed() bool {
	return this.precedenceFilterSuppressed
}

func (this *ATNConfig) setPrecedenceFilterSuppressed(v bool) {
	this.precedenceFilterSuppressed = v
}

func (this *ATNConfig) getState() IATNState {
	return this.state
}

func (this *ATNConfig) getAlt() int {
	return this.alt
}

func (this *ATNConfig) setContext(v IPredictionContext) {
	this.context = v
}
func (this *ATNConfig) getContext() IPredictionContext {
	return this.context
}

func (this *ATNConfig) getSemanticContext() SemanticContext {
	return this.semanticContext
}

func (this *ATNConfig) getReachesIntoOuterContext() int {
	return this.reachesIntoOuterContext
}

func (this *ATNConfig) setReachesIntoOuterContext(v int) {
	this.reachesIntoOuterContext = v
}


func (a *ATNConfig) InitATNConfig(c IATNConfig, state IATNState, context IPredictionContext, semanticContext  SemanticContext) {

	a.state = state;
	a.alt = c.getAlt();
	a.context = context;
	a.semanticContext = semanticContext;
	a.reachesIntoOuterContext = c.getReachesIntoOuterContext();

}

func (a *ATNConfig) InitATNConfig2(state IATNState, alt int, context IPredictionContext, semanticContext SemanticContext) {

	a.state = state;
	a.alt = alt;
	a.context = context;
	a.semanticContext = semanticContext;

}

// An ATN configuration is equal to another if both have
//  the same state, they predict the same alternative, and
//  syntactic/semantic contexts are the same.
///
func (this *ATNConfig) equals(other interface{}) bool {
    if (this == other) {
        return true
    } else if _, ok := other.(*ATNConfig); !ok {
        return false
    } else {
        return reflect.DeepEqual(this, other)
    }
}

func (this *ATNConfig) shortHashString() string {
    return "" + strconv.Itoa(this.state.getStateNumber()) + "/" + strconv.Itoa(this.alt) + "/" + this.semanticContext.toString()
}

func (this *ATNConfig) hashString() string {

	var c string
	if (this.context == nil){
		c = ""
	} else {
		c = this.context.hashString()
	}

    return "" + strconv.Itoa(this.state.getStateNumber()) + "/" + strconv.Itoa(this.alt) + "/" + c + "/" + this.semanticContext.toString()
}

func (this *ATNConfig) toString() string {

	var a string
	if (this.context != nil){
		a = ",[" + fmt.Sprint(this.context) + "]"
	}

	var b string
	if (this.semanticContext != SemanticContextNONE){
		b = ("," + fmt.Sprint(this.semanticContext))
	}

	var c string
	if (this.reachesIntoOuterContext > 0){
		c = ",up=" + fmt.Sprint(this.reachesIntoOuterContext)
	}

    return "(" + fmt.Sprint(this.state) + "," + strconv.Itoa(this.alt) + a + b + c + ")"
}



type LexerATNConfig struct {
	ATNConfig

	lexerActionExecutor *LexerActionExecutor
	passedThroughNonGreedyDecision bool
}

func NewLexerATNConfig6(state IATNState, alt int, context IPredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.InitATNConfig2(state, alt, context, SemanticContextNONE)

	this.passedThroughNonGreedyDecision = false
	this.lexerActionExecutor = nil
	return this
}

func NewLexerATNConfig5(state IATNState, alt int, context IPredictionContext, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.InitATNConfig2(state, alt, context, SemanticContextNONE)
	this.lexerActionExecutor = lexerActionExecutor
	this.passedThroughNonGreedyDecision = false
	return this
}

func NewLexerATNConfig4(c *LexerATNConfig, state IATNState)  *LexerATNConfig {

	this := new(LexerATNConfig)

	this.InitATNConfig(c, state, c.context, c.semanticContext)
	this.lexerActionExecutor = c.lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}

func NewLexerATNConfig3(c *LexerATNConfig, state IATNState, lexerActionExecutor *LexerActionExecutor) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.InitATNConfig(c, state, c.context, c.semanticContext)
	this.lexerActionExecutor = lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}

func NewLexerATNConfig2(c *LexerATNConfig, state IATNState,  context IPredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	this.InitATNConfig(c, state, context, c.semanticContext)
	this.lexerActionExecutor = c.lexerActionExecutor
	this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state)
	return this
}


func NewLexerATNConfig1( state IATNState, alt int, context IPredictionContext) *LexerATNConfig {

	this := new(LexerATNConfig)

	// c *ATNConfig, state IATNState, context IPredictionContext, semanticContext  SemanticContext
	this.InitATNConfig2(state, alt, context, SemanticContextNONE)

    this.lexerActionExecutor = nil
    this.passedThroughNonGreedyDecision = false

    return this
}


func (this *LexerATNConfig) hashString() string {
	var f string

	if this.passedThroughNonGreedyDecision {
		f = "1"
	} else {
		f = "0"
	}

    return "" + strconv.Itoa(this.state.getStateNumber()) + strconv.Itoa(this.alt) + fmt.Sprint(this.context) +
		fmt.Sprint(this.semanticContext) + f + fmt.Sprint(this.lexerActionExecutor)
}

func (this *LexerATNConfig) equals(other interface{}) bool {

	othert, ok := other.(*LexerATNConfig)

    if (this == other) {
        return true
    } else if !ok {
        return false
    } else if (this.passedThroughNonGreedyDecision != othert.passedThroughNonGreedyDecision) {
        return false
    }

	var b bool
	if (this.lexerActionExecutor != nil){
		b  = !this.lexerActionExecutor.equals(othert.lexerActionExecutor)
	} else {
		b = othert.lexerActionExecutor != nil
	}

	if (b) {
        return false
    } else {
		panic("Not implemented")
//        return ATNConfig.prototype.equals.call(this, other)
    }
}

func checkNonGreedyDecision(source *LexerATNConfig, target IATNState) bool {
	ds, ok := target.(*DecisionState)
	return source.passedThroughNonGreedyDecision || (ok && ds.nonGreedy)
}
