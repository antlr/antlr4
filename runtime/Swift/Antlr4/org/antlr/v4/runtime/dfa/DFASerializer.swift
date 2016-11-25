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


/** A DFA walker that knows how to dump them to serialized strings. */

public class DFASerializer: CustomStringConvertible {

    private let dfa: DFA

    private let vocabulary: Vocabulary

    /**
     * @deprecated Use {@link #DFASerializer(org.antlr.v4.runtime.dfa.DFA, org.antlr.v4.runtime.Vocabulary)} instead.
     */
    //@Deprecated
    public convenience init(_ dfa: DFA, _ tokenNames: [String?]?) {
        self.init(dfa, Vocabulary.fromTokenNames(tokenNames))
    }

    public init(_ dfa: DFA, _ vocabulary: Vocabulary) {
        self.dfa = dfa
        self.vocabulary = vocabulary
    }

    public var description: String {
        if dfa.s0 == nil {
            return ""
        }
        let buf: StringBuilder = StringBuilder()
        let states: Array<DFAState> = dfa.getStates()
        for s: DFAState in states {
            var n: Int = 0
            if let sEdges = s.edges {
                n = sEdges.count
            }
            for i in 0..<n {
                let t: DFAState? = s.edges![i]
                if let t = t , t.stateNumber != Int.max {
                    buf.append(getStateString(s))
                    let label: String = getEdgeLabel(i)
                    buf.append("-")
                    buf.append(label)
                    buf.append("->")
                    buf.append(getStateString(t))
                    buf.append("\n")
                }
            }
        }
        
        let output: String = buf.toString()
        if output.length == 0 {
            return ""
        }
        //return Utils.sortLinesInString(output);
        return output
 
    }

    public func toString() -> String {
        return description
    }

    internal func getEdgeLabel(_ i: Int) -> String {
        return vocabulary.getDisplayName(i - 1)
    }


    internal func getStateString(_ s: DFAState) -> String {
        let n: Int = s.stateNumber

        let s1 = s.isAcceptState ? ":" : ""
        let s2 = s.requiresFullContext ? "^" : ""
        let baseStateStr: String = s1 + "s" + String(n) + s2
        if s.isAcceptState {
            if let predicates = s.predicates {
                return baseStateStr + "=>\(predicates)"
            } else {
                return baseStateStr + "=>\(s.prediction!)"
            }
        } else {
            return baseStateStr
        }
    }
}
