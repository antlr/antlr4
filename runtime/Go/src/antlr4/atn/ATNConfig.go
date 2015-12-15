package atn

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
///

//var DecisionState = require('./ATNState').DecisionState
//var SemanticContext = require('./SemanticContext').SemanticContext

func checkParams(params, isCfg) {
	if(params==nil) {
		var result = { state:nil, alt:nil, context:nil, semanticContext:nil }
		if(isCfg) {
			result.reachesIntoOuterContext = 0
		}
		return result
	} else {
		var props = {}
		props.state = params.state || nil
		props.alt = params.alt || nil
		props.context = params.context || nil
		props.semanticContext = params.semanticContext || nil
		if(isCfg) {
			props.reachesIntoOuterContext = params.reachesIntoOuterContext || 0
			props.precedenceFilterSuppressed = params.precedenceFilterSuppressed || false
		}
		return props
	}
}

func ATNConfig(params, config) {
	this.checkContext(params, config)
	params = checkParams(params)
	config = checkParams(config, true)
    // The ATN state associated with this configuration///
    this.state = params.state!=nil ? params.state : config.state
    // What alt (or lexer rule) is predicted by this configuration///
    this.alt = params.alt!=nil ? params.alt : config.alt
    // The stack of invoking states leading to the rule/states associated
    //  with this config.  We track only those contexts pushed during
    //  execution of the ATN simulator.
    this.context = params.context!=nil ? params.context : config.context
    this.semanticContext = params.semanticContext!=nil ? params.semanticContext :
        (config.semanticContext!=nil ? config.semanticContext : SemanticContext.NONE)
    // We cannot execute predicates dependent upon local context unless
    // we know for sure we are in the correct context. Because there is
    // no way to do this efficiently, we simply cannot evaluate
    // dependent predicates unless we are in the rule that initially
    // invokes the ATN simulator.
    //
    // closure() tracks the depth of how far we dip into the
    // outer context: depth &gt 0.  Note that it may not be totally
    // accurate depth since I don't ever decrement. TODO: make it a boolean then
    this.reachesIntoOuterContext = config.reachesIntoOuterContext
    this.precedenceFilterSuppressed = config.precedenceFilterSuppressed
    return this
}

func (this *ATNConfig) checkContext(params, config) {
	if((params.context==nil || params.context==undefined) &&
			(config==nil || config.context==nil || config.context==undefined)) {
		this.context = nil
	}
}

// An ATN configuration is equal to another if both have
//  the same state, they predict the same alternative, and
//  syntactic/semantic contexts are the same.
///
func (this *ATNConfig) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof ATNConfig)) {
        return false
    } else {
        return this.state.stateNumber==other.state.stateNumber &&
            this.alt==other.alt &&
            (this.context==nil ? other.context==nil : this.context.equals(other.context)) &&
            this.semanticContext.equals(other.semanticContext) &&
            this.precedenceFilterSuppressed==other.precedenceFilterSuppressed
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

func (this *ATNConfig) toString() {
    return "(" + this.state + "," + this.alt +
        (this.context!=nil ? ",[" + this.context.toString() + "]" : "") +
        (this.semanticContext != SemanticContext.NONE ?
                ("," + this.semanticContext.toString())
                : "") +
        (this.reachesIntoOuterContext>0 ?
                (",up=" + this.reachesIntoOuterContext)
                : "") + ")"
}


func LexerATNConfig(params, config) {
	ATNConfig.call(this, params, config)
    
    // This is the backing field for {@link //getLexerActionExecutor}.
	var lexerActionExecutor = params.lexerActionExecutor || nil
    this.lexerActionExecutor = lexerActionExecutor || (config!=nil ? config.lexerActionExecutor : nil)
    this.passedThroughNonGreedyDecision = config!=nil ? this.checkNonGreedyDecision(config, this.state) : false
    return this
}

//LexerATNConfig.prototype = Object.create(ATNConfig.prototype)
//LexerATNConfig.prototype.constructor = LexerATNConfig

func (this *LexerATNConfig) hashString() {
    return "" + this.state.stateNumber + this.alt + this.context +
            this.semanticContext + (this.passedThroughNonGreedyDecision ? 1 : 0) +
            this.lexerActionExecutor
}

func (this *LexerATNConfig) equals(other) {
    if (this == other) {
        return true
    } else if (!(other instanceof LexerATNConfig)) {
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
        (target instanceof DecisionState) && target.nonGreedy
}


