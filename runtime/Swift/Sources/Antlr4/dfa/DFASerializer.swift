/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// A DFA walker that knows how to dump them to serialized strings.
/// 

public class DFASerializer: CustomStringConvertible {
    private let dfa: DFA
    private let vocabulary: Vocabulary

    public init(_ dfa: DFA, _ vocabulary: Vocabulary) {
        self.dfa = dfa
        self.vocabulary = vocabulary
    }

    public var description: String {
        if dfa.s0 == nil {
            return ""
        }
        var buf = ""
        let states = dfa.getStates()
        for s in states {
            guard let edges = s.edges else {
                continue
            }
            for (i, t) in edges.enumerated() {
                guard let t = t, t.stateNumber != Int.max else {
                    continue
                }
                let edgeLabel = getEdgeLabel(i)
                buf += getStateString(s)
                buf += "-\(edgeLabel)->"
                buf += getStateString(t)
                buf += "\n"
            }
        }

        return buf
    }

    internal func getEdgeLabel(_ i: Int) -> String {
        return vocabulary.getDisplayName(i - 1)
    }


    internal func getStateString(_ s: DFAState) -> String {
        let n = s.stateNumber

        let s1 = s.isAcceptState ? ":" : ""
        let s2 = s.requiresFullContext ? "^" : ""
        let baseStateStr = s1 + "s" + String(n) + s2
        if s.isAcceptState {
            if let predicates = s.predicates {
                return baseStateStr + "=>\(predicates)"
            } else {
                return baseStateStr + "=>\(s.prediction)"
            }
        } else {
            return baseStateStr
        }
    }
}
