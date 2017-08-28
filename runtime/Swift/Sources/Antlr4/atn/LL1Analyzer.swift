/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public class LL1Analyzer {
    /// 
    /// Special value added to the lookahead sets to indicate that we hit
    /// a predicate during analysis if `seeThruPreds==false`.
    /// 
    public let HIT_PRED: Int = CommonToken.INVALID_TYPE

    public let atn: ATN

    public init(_ atn: ATN) {
        self.atn = atn
    }

    /// 
    /// Calculates the SLL(1) expected lookahead set for each outgoing transition
    /// of an _org.antlr.v4.runtime.atn.ATNState_. The returned array has one element for each
    /// outgoing transition in `s`. If the closure from transition
    /// __i__ leads to a semantic predicate before matching a symbol, the
    /// element at index __i__ of the result will be `null`.
    /// 
    /// - parameter s: the ATN state
    /// - returns: the expected symbols for each outgoing transition of `s`.
    /// 
    public func getDecisionLookahead(_ s: ATNState?) throws -> [IntervalSet?]? {
        
        guard let s = s else {
             return nil
        }
        let length = s.getNumberOfTransitions()
        var look: [IntervalSet?] = [IntervalSet?](repeating: nil, count: length)
        for alt in 0..<length {
            look[alt] = try IntervalSet()
            var lookBusy: Set<ATNConfig> = Set<ATNConfig>()
            let seeThruPreds: Bool = false // fail to get lookahead upon pred
            try _LOOK(s.transition(alt).target, nil, PredictionContext.EMPTY,
                    look[alt]!, &lookBusy, BitSet(), seeThruPreds, false)
            // Wipe out lookahead for this alternative if we found nothing
            // or we had a predicate when we !seeThruPreds
            if look[alt]!.size() == 0 || look[alt]!.contains(HIT_PRED) {
                look[alt] = nil
            }
        }
        return look
    }

    /// 
    /// Compute set of tokens that can follow `s` in the ATN in the
    /// specified `ctx`.
    /// 
    /// If `ctx` is `null` and the end of the rule containing
    /// `s` is reached, _org.antlr.v4.runtime.Token#EPSILON_ is added to the result set.
    /// If `ctx` is not `null` and the end of the outermost rule is
    /// reached, _org.antlr.v4.runtime.Token#EOF_ is added to the result set.
    /// 
    /// - parameter s: the ATN state
    /// - parameter ctx: the complete parser context, or `null` if the context
    /// should be ignored
    /// 
    /// - returns: The set of tokens that can follow `s` in the ATN in the
    /// specified `ctx`.
    /// 
    public func LOOK(_ s: ATNState, _ ctx: RuleContext?) throws -> IntervalSet {
        return try LOOK(s, nil, ctx)
    }

    /// 
    /// Compute set of tokens that can follow `s` in the ATN in the
    /// specified `ctx`.
    /// 
    /// If `ctx` is `null` and the end of the rule containing
    /// `s` is reached, _org.antlr.v4.runtime.Token#EPSILON_ is added to the result set.
    /// If `ctx` is not `null` and the end of the outermost rule is
    /// reached, _org.antlr.v4.runtime.Token#EOF_ is added to the result set.
    /// 
    /// - parameter s: the ATN state
    /// - parameter stopState: the ATN state to stop at. This can be a
    /// _org.antlr.v4.runtime.atn.BlockEndState_ to detect epsilon paths through a closure.
    /// - parameter ctx: the complete parser context, or `null` if the context
    /// should be ignored
    /// 
    /// - returns: The set of tokens that can follow `s` in the ATN in the
    /// specified `ctx`.
    /// 

    public func LOOK(_ s: ATNState, _ stopState: ATNState?, _ ctx: RuleContext?) throws -> IntervalSet {
        let r: IntervalSet = try IntervalSet()
        let seeThruPreds: Bool = true // ignore preds; get all lookahead
        let lookContext: PredictionContext? = ctx != nil ? PredictionContext.fromRuleContext(s.atn!, ctx) : nil
        var config = Set<ATNConfig>()
        try _LOOK(s, stopState, lookContext,
                r, &config, BitSet(), seeThruPreds, true)
        return r
    }

    /// 
    /// Compute set of tokens that can follow `s` in the ATN in the
    /// specified `ctx`.
    /// 
    /// If `ctx` is `null` and `stopState` or the end of the
    /// rule containing `s` is reached, _org.antlr.v4.runtime.Token#EPSILON_ is added to
    /// the result set. If `ctx` is not `null` and `addEOF` is
    /// `true` and `stopState` or the end of the outermost rule is
    /// reached, _org.antlr.v4.runtime.Token#EOF_ is added to the result set.
    /// 
    /// - parameter s: the ATN state.
    /// - parameter stopState: the ATN state to stop at. This can be a
    /// _org.antlr.v4.runtime.atn.BlockEndState_ to detect epsilon paths through a closure.
    /// - parameter ctx: The outer context, or `null` if the outer context should
    /// not be used.
    /// - parameter look: The result lookahead set.
    /// - parameter lookBusy: A set used for preventing epsilon closures in the ATN
    /// from causing a stack overflow. Outside code should pass
    /// `new HashSet<ATNConfig>` for this argument.
    /// - parameter calledRuleStack: A set used for preventing left recursion in the
    /// ATN from causing a stack overflow. Outside code should pass
    /// `new BitSet()` for this argument.
    /// - parameter seeThruPreds: `true` to true semantic predicates as
    /// implicitly `true` and "see through them", otherwise `false`
    /// to treat semantic predicates as opaque and add _#HIT_PRED_ to the
    /// result if one is encountered.
    /// - parameter addEOF: Add _org.antlr.v4.runtime.Token#EOF_ to the result if the end of the
    /// outermost context is reached. This parameter has no effect if `ctx`
    /// is `null`.
    /// 
    internal func _LOOK(_ s: ATNState,
                        _ stopState: ATNState?,
                        _ ctx: PredictionContext?,
                        _ look: IntervalSet,
                        _ lookBusy: inout Set<ATNConfig>,
                        _ calledRuleStack: BitSet,
                        _ seeThruPreds: Bool, _ addEOF: Bool) throws {
        // print ("_LOOK(\(s.stateNumber), ctx=\(ctx)");
        //TODO  var c : ATNConfig = ATNConfig(s, 0, ctx);
        if s.description == "273" {
            var s = 0
        }
        var c: ATNConfig = ATNConfig(s, 0, ctx)
        if lookBusy.contains(c) {
            return
        } else {
            lookBusy.insert(c)
        }

        if s == stopState {
            guard let ctx = ctx else {
                try look.add(CommonToken.EPSILON)
                return
            }

            if ctx.isEmpty() && addEOF {
                try look.add(CommonToken.EOF)
                return
            }

        }

        if s is RuleStopState {
            guard let ctx = ctx else {
                try look.add(CommonToken.EPSILON)
                return
            }

            if ctx.isEmpty() && addEOF {
                try look.add(CommonToken.EOF)
                return
            }


            if ctx != PredictionContext.EMPTY {
                // run thru all possible stack tops in ctx
                let length = ctx.size()
                for i in 0..<length {
                    var returnState: ATNState = atn.states[(ctx.getReturnState(i))]!


                    var removed: Bool = try calledRuleStack.get(returnState.ruleIndex!)
                    try calledRuleStack.clear(returnState.ruleIndex!)
                    try self._LOOK(returnState, stopState, ctx.getParent(i), look, &lookBusy, calledRuleStack, seeThruPreds, addEOF)
                    defer {
                        if removed {
                            try! calledRuleStack.set(returnState.ruleIndex!)
                        }
                    }
                }
                return
            }
        }

        var n: Int = s.getNumberOfTransitions()
        for i in 0..<n {
            var t: Transition = s.transition(i)
            if type(of: t) == RuleTransition.self {
                if try calledRuleStack.get((t as! RuleTransition).target.ruleIndex!) {
                    continue
                }

                var newContext: PredictionContext =
                SingletonPredictionContext.create(ctx, (t as! RuleTransition).followState.stateNumber)
                try calledRuleStack.set((t as! RuleTransition).target.ruleIndex!)
                try _LOOK(t.target, stopState, newContext, look, &lookBusy, calledRuleStack, seeThruPreds, addEOF)
                defer {
                    try! calledRuleStack.clear((t as! RuleTransition).target.ruleIndex!)
                }
            } else {
                if t is AbstractPredicateTransition {
                    if seeThruPreds {
                        try _LOOK(t.target, stopState, ctx, look, &lookBusy, calledRuleStack, seeThruPreds, addEOF)
                    } else {
                        try look.add(HIT_PRED)
                    }
                } else {
                    if t.isEpsilon() {
                        try _LOOK(t.target, stopState, ctx, look, &lookBusy, calledRuleStack, seeThruPreds, addEOF)
                    } else {
                        if type(of: t) == WildcardTransition.self {
                            try look.addAll(IntervalSet.of(CommonToken.MIN_USER_TOKEN_TYPE, atn.maxTokenType))
                        } else {

                            var set: IntervalSet? = try t.labelIntervalSet()
                            if set != nil {
                                if t is NotSetTransition {
                                    set = try set!.complement(IntervalSet.of(CommonToken.MIN_USER_TOKEN_TYPE, atn.maxTokenType)) as? IntervalSet
                                }
                                try look.addAll(set)
                            }
                        }
                    }
                }
            }
        }
    }
}
