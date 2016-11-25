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



/** */

public final class RuleTransition: Transition {
    /** Ptr to the rule definition object for this rule ref */
    public final var ruleIndex: Int
    // no Rule object at runtime

    public final var precedence: Int

    /** What node to begin computations following ref to rule */
    public final var followState: ATNState

    /**
     * @deprecated Use
     * {@link #RuleTransition(org.antlr.v4.runtime.atn.RuleStartState, int, int, org.antlr.v4.runtime.atn.ATNState)} instead.
     */
    //@Deprecated
    public convenience init(_ ruleStart: RuleStartState,
                            _ ruleIndex: Int,
                            _ followState: ATNState) {
        self.init(ruleStart, ruleIndex, 0, followState)
    }

    public init(_ ruleStart: RuleStartState,
                _ ruleIndex: Int,
                _ precedence: Int,
                _ followState: ATNState) {

        self.ruleIndex = ruleIndex
        self.precedence = precedence
        self.followState = followState

        super.init(ruleStart)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.RULE
    }

    override
    public func isEpsilon() -> Bool {
        return true
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }
}
