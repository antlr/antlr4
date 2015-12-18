package atn
import (
	"antlr4"
	"reflect"
)

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
//

type ATNConfig struct {
	precedenceFilterSuppressed int
	state *ATNState
	alt int
	context *antlr4.PredictionContext
	semanticContext int
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

func NewATNConfig6(state *ATNState, alt int, context *antlr4.PredictionContext) *ATNConfig {
	return NewATNConfig(state, alt, context, SemanticContextNONE);
}

func NewATNConfig5(state *ATNState, alt int, context *antlr4.PredictionContext, semanticContext *SemanticContext) *ATNConfig {
	a := new(ATNConfig)
	a.state = state;
	a.alt = alt;
	a.context = context;
	a.semanticContext = semanticContext;
	return a
}

func NewATNConfig4(c *ATNConfig, state *ATNState) *ATNConfig {
	return NewATNConfig(c, state, c.context, c.semanticContext);
}

func NewATNConfig3(c *ATNConfig, state *ATNState, semanticContext *SemanticContext) *ATNConfig {
	return NewATNConfig(c, state, c.context, semanticContext);
}

func NewATNConfig2(c *ATNConfig, semanticContext *SemanticContext) *ATNConfig {
	return NewATNConfig(c, c.state, c.context, semanticContext);
}

func NewATNConfig1(c *ATNConfig, state *ATNState, context *antlr4.PredictionContext) *ATNConfig {
	return NewATNConfig(c, state, context, c.semanticContext);
}

func NewATNConfig(c *ATNConfig, state *ATNState, context *antlr4.PredictionContext, semanticContext *SemanticContext) *ATNConfig {
	a := new(ATNConfig)
	a.state = state;
	a.alt = c.alt;
	a.context = context;
	a.semanticContext = semanticContext;
	a.reachesIntoOuterContext = c.reachesIntoOuterContext;
	return a
}

//
//
//func checkParams(params *ATNConfig, isCfg bool) *ATNConfigParams {
//	if(params == nil) {
//		var result = { state:nil, alt:nil, context:nil, semanticContext:nil }
//		if(isCfg) {
//			result.reachesIntoOuterContext = 0
//		}
//		return result
//	} else {
//		var props = {}
//		props.state = params.state || nil
//		props.alt = params.alt || nil
//		props.context = params.context || nil
//		props.semanticContext = params.semanticContext || nil
//		if(isCfg) {
//			props.reachesIntoOuterContext = params.reachesIntoOuterContext || 0
//			props.precedenceFilterSuppressed = params.precedenceFilterSuppressed || false
//		}
//		return props
//	}
//}
//
//
//func NewATNConfig(params *ATNConfig, config *ATNConfig) *ATNConfig {
//
//	this := new(ATNConfig)
//
//	this.checkContext(params, config)
//
//	params = checkParams(params, false)
//	config = checkParams(config, true)
//
//	if params.state != nil {
//		this.state = params.state
//	} else {
//		this.state = config.state
//	}
//
//	if params.alt != nil {
//		this.alt = params.alt
//	} else {
//		this.alt = config.alt
//	}
//
//    this.context = params.context!=nil ? params.context : config.context
//
//    this.semanticContext = params.semanticContext!=nil ? params.semanticContext :
//        (config.semanticContext!=nil ? config.semanticContext : SemanticContext.NONE)
//
//    this.reachesIntoOuterContext = config.reachesIntoOuterContext
//    this.precedenceFilterSuppressed = config.precedenceFilterSuppressed
//
//    return this
//}
//
//
//
//
//
//func (this *ATNConfig) checkContext(params, config) {
//	if((params.context==nil || params.context==nil) &&
//			(config==nil || config.context==nil || config.context==nil)) {
//		this.context = nil
//	}
//}

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

func (this *ATNConfig) shortHashString() {
    return "" + this.state.stateNumber + "/" + this.alt + "/" + this.semanticContext
}

func (this *ATNConfig) hashString() {
    return "" + this.state.stateNumber + "/" + this.alt + "/" +
             (this.context==nil ? "" : this.context.hashString()) +
             "/" + this.semanticContext.hashString()
}

func (this *ATNConfig) toString() string {
    return "(" + this.state + "," + this.alt +
        (this.context!=nil ? ",[" + this.context.toString() + "]" : "") +
        (this.semanticContext != SemanticContext.NONE ?
                ("," + this.semanticContext.toString())
                : "") +
        (this.reachesIntoOuterContext>0 ?
                (",up=" + this.reachesIntoOuterContext)
                : "") + ")"
}


type LexerATNConfig struct {
	ATNConfig
}

func LexerATNConfig(params, config) {
	ATNConfig.call(this, params, config)
    
    // This is the backing field for {@link //getLexerActionExecutor}.
	var lexerActionExecutor = params.lexerActionExecutor || nil
    this.lexerActionExecutor = lexerActionExecutor || (config!=nil ? config.lexerActionExecutor : nil)
    this.passedThroughNonGreedyDecision = config!=nil ? this.checkNonGreedyDecision(config, this.state) : false
    return this
}

func (this *LexerATNConfig) hashString() {
    return "" + this.state.stateNumber + this.alt + this.context +
            this.semanticContext + (this.passedThroughNonGreedyDecision ? 1 : 0) +
            this.lexerActionExecutor
}

func (this *LexerATNConfig) equals(other) {
    if (this == other) {
        return true
    } else if (!_, ok := other.(LexerATNConfig); ok) {
        return false
    } else if (this.passedThroughNonGreedyDecision != other.passedThroughNonGreedyDecision) {
        return false
    } else if (this.lexerActionExecutor ?
            !this.lexerActionExecutor.equals(other.lexerActionExecutor)
            : !other.lexerActionExecutor) {
        return false
    } else {
        return ATNConfig.prototype.equals.call(this, other)
    }
}

func (this *LexerATNConfig) checkNonGreedyDecision(source, target) {
    return source.passedThroughNonGreedyDecision ||
        _, ok := target.(DecisionState); ok && target.nonGreedy
}


