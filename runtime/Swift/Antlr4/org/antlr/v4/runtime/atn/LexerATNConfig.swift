/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class LexerATNConfig: ATNConfig {
    /**
     * This is the backing field for {@link #getLexerActionExecutor}.
     */
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
    /**
     * Gets the {@link org.antlr.v4.runtime.atn.LexerActionExecutor} capable of executing the embedded
     * action(s) for the current configuration.
     */
    public final func getLexerActionExecutor() -> LexerActionExecutor? {
        return lexerActionExecutor
    }

    public final func hasPassedThroughNonGreedyDecision() -> Bool {
        return passedThroughNonGreedyDecision
    }

    override
    /*public func hashCode() -> Int {

    }*/
    public var hashValue: Int {
        var hashCode: Int = MurmurHash.initialize(7)
        hashCode = MurmurHash.update(hashCode, state.stateNumber)
        hashCode = MurmurHash.update(hashCode, alt)
        hashCode = MurmurHash.update(hashCode, context)
        hashCode = MurmurHash.update(hashCode, semanticContext)
        hashCode = MurmurHash.update(hashCode, passedThroughNonGreedyDecision ? 1 : 0)
        hashCode = MurmurHash.update(hashCode, lexerActionExecutor)
        hashCode = MurmurHash.finish(hashCode, 6)
        return hashCode

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
