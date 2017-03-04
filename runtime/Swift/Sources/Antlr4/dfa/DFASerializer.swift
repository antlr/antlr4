/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// A DFA walker that knows how to dump them to serialized strings.

public class DFASerializer: CustomStringConvertible {

    private let dfa: DFA

    private let vocabulary: Vocabulary

    /// -  Use {@link #DFASerializer(org.antlr.v4.runtime.dfa.DFA, org.antlr.v4.runtime.Vocabulary)} instead.
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
