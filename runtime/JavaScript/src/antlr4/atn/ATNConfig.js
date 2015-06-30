//
// [The "BSD license"]
//  Copyright (c) 2012 Terence Parr
//  Copyright (c) 2012 Sam Harwell
//  Copyright (c) 2014 Eric Vergnaud
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions
//  are met:
//
//  1. Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in the
//     documentation and/or other materials provided with the distribution.
//  3. The name of the author may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
//  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
///

// A tuple: (ATN state, predicted alt, syntactic, semantic context).
//  The syntactic context is a graph-structured stack node whose
//  path(s) to the root is the rule invocation(s)
//  chain used to arrive at the state.  The semantic context is
//  the tree of semantic predicates encountered before reaching
//  an ATN state.
///

var DecisionState = require('./ATNState').DecisionState;
var SemanticContext = require('./SemanticContext').SemanticContext;

function checkParams(params, isCfg) {
	if(params===null) {
		var result = { state:null, alt:null, context:null, semanticContext:null };
		if(isCfg) {
			result.reachesIntoOuterContext = 0;
		}
		return result;
	} else {
		var props = {};
		props.state = params.state || null;
		props.alt = params.alt || null;
		props.context = params.context || null;
		props.semanticContext = params.semanticContext || null;
		if(isCfg) {
			props.reachesIntoOuterContext = params.reachesIntoOuterContext || 0;
			props.precedenceFilterSuppressed = params.precedenceFilterSuppressed || false;
		}
		return props;
	}
}

function ATNConfig(params, config) {
	this.checkContext(params, config);
	params = checkParams(params);
	config = checkParams(config, true);
    // The ATN state associated with this configuration///
    this.state = params.state!==null ? params.state : config.state;
    // What alt (or lexer rule) is predicted by this configuration///
    this.alt = params.alt!==null ? params.alt : config.alt;
    // The stack of invoking states leading to the rule/states associated
    //  with this config.  We track only those contexts pushed during
    //  execution of the ATN simulator.
    this.context = params.context!==null ? params.context : config.context;
    this.semanticContext = params.semanticContext!==null ? params.semanticContext :
        (config.semanticContext!==null ? config.semanticContext : SemanticContext.NONE);
    // We cannot execute predicates dependent upon local context unless
    // we know for sure we are in the correct context. Because there is
    // no way to do this efficiently, we simply cannot evaluate
    // dependent predicates unless we are in the rule that initially
    // invokes the ATN simulator.
    //
    // closure() tracks the depth of how far we dip into the
    // outer context: depth &gt; 0.  Note that it may not be totally
    // accurate depth since I don't ever decrement. TODO: make it a boolean then
    this.reachesIntoOuterContext = config.reachesIntoOuterContext;
    this.precedenceFilterSuppressed = config.precedenceFilterSuppressed;
    return this;
}

ATNConfig.prototype.checkContext = function(params, config) {
	if((params.context===null || params.context===undefined) &&
			(config===null || config.context===null || config.context===undefined)) {
		this.context = null;
	}
};

// An ATN configuration is equal to another if both have
//  the same state, they predict the same alternative, and
//  syntactic/semantic contexts are the same.
///
ATNConfig.prototype.equals = function(other) {
    if (this === other) {
        return true;
    } else if (! (other instanceof ATNConfig)) {
        return false;
    } else {
        return this.state.stateNumber===other.state.stateNumber &&
            this.alt===other.alt &&
            (this.context===null ? other.context===null : this.context.equals(other.context)) &&
            this.semanticContext.equals(other.semanticContext) &&
            this.precedenceFilterSuppressed===other.precedenceFilterSuppressed;
    }
};

ATNConfig.prototype.shortHashString = function() {
    return "" + this.state.stateNumber + "/" + this.alt + "/" + this.semanticContext;
};

ATNConfig.prototype.hashString = function() {
    return "" + this.state.stateNumber + "/" + this.alt + "/" +
             (this.context===null ? "" : this.context.hashString()) +
             "/" + this.semanticContext.hashString();
};

ATNConfig.prototype.toString = function() {
    return "(" + this.state + "," + this.alt +
        (this.context!==null ? ",[" + this.context.toString() + "]" : "") +
        (this.semanticContext !== SemanticContext.NONE ?
                ("," + this.semanticContext.toString())
                : "") +
        (this.reachesIntoOuterContext>0 ?
                (",up=" + this.reachesIntoOuterContext)
                : "") + ")";
};


function LexerATNConfig(params, config) {
	ATNConfig.call(this, params, config);
    
    // This is the backing field for {@link //getLexerActionExecutor}.
	var lexerActionExecutor = params.lexerActionExecutor || null;
    this.lexerActionExecutor = lexerActionExecutor || (config!==null ? config.lexerActionExecutor : null);
    this.passedThroughNonGreedyDecision = config!==null ? this.checkNonGreedyDecision(config, this.state) : false;
    return this;
}

LexerATNConfig.prototype = Object.create(ATNConfig.prototype);
LexerATNConfig.prototype.constructor = LexerATNConfig;

LexerATNConfig.prototype.hashString = function() {
    return "" + this.state.stateNumber + this.alt + this.context +
            this.semanticContext + (this.passedThroughNonGreedyDecision ? 1 : 0) +
            this.lexerActionExecutor;
};

LexerATNConfig.prototype.equals = function(other) {
    if (this === other) {
        return true;
    } else if (!(other instanceof LexerATNConfig)) {
        return false;
    } else if (this.passedThroughNonGreedyDecision !== other.passedThroughNonGreedyDecision) {
        return false;
    } else if (this.lexerActionExecutor !== other.lexerActionExecutor) {
        return false;
    } else {
        return ATNConfig.prototype.equals.call(this, other);
    }
};

LexerATNConfig.prototype.checkNonGreedyDecision = function(source, target) {
    return source.passedThroughNonGreedyDecision ||
        (target instanceof DecisionState) && target.nonGreedy;
};

exports.ATNConfig = ATNConfig;
exports.LexerATNConfig = LexerATNConfig;