package antlr4

import (
    "antlr4/atn"
)

//var Set = require('./Utils').Set
//var BitSet = require('./Utils').BitSet
//var Token = require('./Token').Token
//var ATNConfig = require('./atn/ATNConfig').ATNConfig
//var Interval = require('./IntervalSet').Interval
//var IntervalSet = require('./IntervalSet').IntervalSet
//var RuleStopState = require('./atn/ATNState').RuleStopState
//var RuleTransition = require('./atn/Transition').RuleTransition
//var NotSetTransition = require('./atn/Transition').NotSetTransition
//var WildcardTransition = require('./atn/Transition').WildcardTransition
//var AbstractPredicateTransition = require('./atn/Transition').AbstractPredicateTransition
//
//var pc = require('./PredictionContext')
//var predictionContextFromRuleContext = pc.predictionContextFromRuleContext
//var PredictionContext = pc.PredictionContext
//var SingletonPredictionContext = pc.SingletonPredictionContext

type LL1Analyzer struct {
    atn atn.ATN
}

func NewLL1Analyzer (atn) *LL1Analyzer {
    la = new(LL1Analyzer)
    la.atn = atn
    return la
}

//* Special value added to the lookahead sets to indicate that we hit
//  a predicate during analysis if {@code seeThruPreds==false}.
///
LL1Analyzer.HIT_PRED = TokenInvalidType

//*
// Calculates the SLL(1) expected lookahead set for each outgoing transition
// of an {@link ATNState}. The returned array has one element for each
// outgoing transition in {@code s}. If the closure from transition
// <em>i</em> leads to a semantic predicate before matching a symbol, the
// element at index <em>i</em> of the result will be {@code nil}.
//
// @param s the ATN state
// @return the expected symbols for each outgoing transition of {@code s}.
///
func (la *LL1Analyzer) getDecisionLookahead(s) {
    if (s == nil) {
        return nil
    }
    var count = s.transitions.length
    var look = []
    for(var alt=0 alt< count alt++) {
        look[alt] = NewIntervalSet()
        var lookBusy = NewSet()
        var seeThruPreds = false // fail to get lookahead upon pred
        la._LOOK(s.transition(alt).target, nil, PredictionContext.EMPTY,
              look[alt], lookBusy, NewBitSet(), seeThruPreds, false)
        // Wipe out lookahead for la alternative if we found nothing
        // or we had a predicate when we !seeThruPreds
        if (look[alt].length==0 || look[alt].contains(LL1Analyzer.HIT_PRED)) {
            look[alt] = nil
        }
    }
    return look
}

//*
// Compute set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
//
// <p>If {@code ctx} is {@code nil} and the end of the rule containing
// {@code s} is reached, {@link Token//EPSILON} is added to the result set.
// If {@code ctx} is not {@code nil} and the end of the outermost rule is
// reached, {@link Token//EOF} is added to the result set.</p>
//
// @param s the ATN state
// @param stopState the ATN state to stop at. This can be a
// {@link BlockEndState} to detect epsilon paths through a closure.
// @param ctx the complete parser context, or {@code nil} if the context
// should be ignored
//
// @return The set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
///
func (la *LL1Analyzer) LOOK(s, stopState, ctx) {
    var r = NewIntervalSet()
    var seeThruPreds = true // ignore preds get all lookahead
	ctx = ctx || nil
    var lookContext = ctx!=nil ? predictionContextFromRuleContext(s.atn, ctx) : nil
    la._LOOK(s, stopState, lookContext, r, NewSet(), NewBitSet(), seeThruPreds, true)
    return r
}
    
//*
// Compute set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
//
// <p>If {@code ctx} is {@code nil} and {@code stopState} or the end of the
// rule containing {@code s} is reached, {@link Token//EPSILON} is added to
// the result set. If {@code ctx} is not {@code nil} and {@code addEOF} is
// {@code true} and {@code stopState} or the end of the outermost rule is
// reached, {@link Token//EOF} is added to the result set.</p>
//
// @param s the ATN state.
// @param stopState the ATN state to stop at. This can be a
// {@link BlockEndState} to detect epsilon paths through a closure.
// @param ctx The outer context, or {@code nil} if the outer context should
// not be used.
// @param look The result lookahead set.
// @param lookBusy A set used for preventing epsilon closures in the ATN
// from causing a stack overflow. Outside code should pass
// {@code NewSet<ATNConfig>} for la argument.
// @param calledRuleStack A set used for preventing left recursion in the
// ATN from causing a stack overflow. Outside code should pass
// {@code NewBitSet()} for la argument.
// @param seeThruPreds {@code true} to true semantic predicates as
// implicitly {@code true} and "see through them", otherwise {@code false}
// to treat semantic predicates as opaque and add {@link //HIT_PRED} to the
// result if one is encountered.
// @param addEOF Add {@link Token//EOF} to the result if the end of the
// outermost context is reached. This parameter has no effect if {@code ctx}
// is {@code nil}.
///
func (la *LL1Analyzer) _LOOK(s, stopState , ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF) {
    var c = NewATNConfig({state:s, alt:0}, ctx)
    if (lookBusy.contains(c)) {
        return
    }
    lookBusy.add(c)
    if (s == stopState) {
        if (ctx ==nil) {
            look.addOne(TokenEpsilon)
            return
        } else if (ctx.isEmpty() && addEOF) {
            look.addOne(TokenEOF)
            return
        }
    }
    if (s instanceof RuleStopState ) {
        if (ctx ==nil) {
            look.addOne(TokenEpsilon)
            return
        } else if (ctx.isEmpty() && addEOF) {
            look.addOne(TokenEOF)
            return
        }
        if (ctx != PredictionContext.EMPTY) {
            // run thru all possible stack tops in ctx
            for(var i=0 i<ctx.length i++) {
                var returnState = la.atn.states[ctx.getReturnState(i)]
                var removed = calledRuleStack.contains(returnState.ruleIndex)
                try {
                    calledRuleStack.remove(returnState.ruleIndex)
                    la._LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
                } finally {
                    if (removed) {
                        calledRuleStack.add(returnState.ruleIndex)
                    }
                }
            }
            return
        }
    }
    for j :=0; j<s.transitions.length; j++ {
        var t = s.transitions[j]
        if (t.constructor == RuleTransition) {
            if (calledRuleStack.contains(t.target.ruleIndex)) {
                continue
            }
            var newContext = SingletonPredictionContext.create(ctx, t.followState.stateNumber)
            try {
                calledRuleStack.add(t.target.ruleIndex)
                la._LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
            } finally {
                calledRuleStack.remove(t.target.ruleIndex)
            }
        } else if (t instanceof AbstractPredicateTransition ) {
            if (seeThruPreds) {
                la._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
            } else {
                look.addOne(LL1Analyzer.HIT_PRED)
            }
        } else if( t.isEpsilon) {
            la._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
        } else if (t.constructor == WildcardTransition) {
            look.addRange( TokenMinUserTokenType, la.atn.maxTokenType )
        } else {
            var set = t.label
            if (set != nil) {
                if _, ok := t.(NotSetTransition); ok {
                    set = set.complement(TokenMinUserTokenType, la.atn.maxTokenType)
                }
                look.addSet(set)
            }
        }
    }
}