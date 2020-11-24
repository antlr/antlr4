/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public class LexerATNConfig: ATNConfig {
    /// 
    /// This is the backing field for _#getLexerActionExecutor_.
    /// 
    private let lexerActionExecutor: LexerActionExecutor?

    fileprivate let passedThroughNonGreedyDecision: Bool

    public init(_ state: ATNState,
                _ alt: Int,
                _ context: PredictionContext) {

        self.passedThroughNonGreedyDecision = false
        self.lexerActionExecutor = nil
        super.init(state, alt, context, SemanticContext.NONE)
    }

    public init(_ state: ATNState,
                _ alt: Int,
                _ context: PredictionContext,
                _ lexerActionExecutor: LexerActionExecutor?) {

        self.lexerActionExecutor = lexerActionExecutor
        self.passedThroughNonGreedyDecision = false
        super.init(state, alt, context, SemanticContext.NONE)
    }

    public init(_ c: LexerATNConfig, _ state: ATNState) {
        self.lexerActionExecutor = c.lexerActionExecutor
        self.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state)
        super.init(c, state, c.context, c.semanticContext)

    }

    public init(_ c: LexerATNConfig, _ state: ATNState,
                _ lexerActionExecutor: LexerActionExecutor?) {

        self.lexerActionExecutor = lexerActionExecutor
        self.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state)
        super.init(c, state, c.context, c.semanticContext)
    }

    public init(_ c: LexerATNConfig, _ state: ATNState,
                _ context: PredictionContext) {

        self.lexerActionExecutor = c.lexerActionExecutor
        self.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state)

        super.init(c, state, context, c.semanticContext)
    }

    private static func checkNonGreedyDecision(_ source: LexerATNConfig, _ target: ATNState) -> Bool {
        return source.passedThroughNonGreedyDecision
                || target is DecisionState && (target as! DecisionState).nonGreedy
    }
    /// 
    /// Gets the _org.antlr.v4.runtime.atn.LexerActionExecutor_ capable of executing the embedded
    /// action(s) for the current configuration.
    /// 
    public final func getLexerActionExecutor() -> LexerActionExecutor? {
        return lexerActionExecutor
    }

    public final func hasPassedThroughNonGreedyDecision() -> Bool {
        return passedThroughNonGreedyDecision
    }

    public override func hash(into hasher: inout Hasher) {
        hasher.combine(state.stateNumber)
        hasher.combine(alt)
        hasher.combine(context)
        hasher.combine(semanticContext)
        hasher.combine(passedThroughNonGreedyDecision)
        hasher.combine(lexerActionExecutor)
    }
}

//useless
public func ==(lhs: LexerATNConfig, rhs: LexerATNConfig) -> Bool {

    if lhs === rhs {
        return true
    }


    //let lexerOther : LexerATNConfig = rhs  // as! LexerATNConfig;
    if lhs.passedThroughNonGreedyDecision != rhs.passedThroughNonGreedyDecision {
        return false
    }



    if lhs.state.stateNumber != rhs.state.stateNumber {
        return false
    }
    if lhs.alt != rhs.alt {
        return false
    }

    if lhs.isPrecedenceFilterSuppressed() != rhs.isPrecedenceFilterSuppressed() {
        return false
    }

    if lhs.getLexerActionExecutor() == nil && rhs.getLexerActionExecutor() != nil {
        return false
    } else if lhs.getLexerActionExecutor() != nil && rhs.getLexerActionExecutor() == nil {
        return false
    } else if lhs.getLexerActionExecutor() == nil && rhs.getLexerActionExecutor() == nil {

    } else if !(lhs.getLexerActionExecutor()! == rhs.getLexerActionExecutor()!) {
        return false
    }


    var contextCompare = false

    if lhs.context == nil && rhs.context == nil {
        contextCompare = true
    } else if lhs.context == nil && rhs.context != nil {
        contextCompare = false
    } else if lhs.context != nil && rhs.context == nil {
        contextCompare = false
    } else {
        contextCompare = (lhs.context! == rhs.context!)
    }

    if !contextCompare{
        return false
    }

    return  lhs.semanticContext == rhs.semanticContext
}
