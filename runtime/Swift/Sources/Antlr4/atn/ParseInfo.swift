/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This class provides access to specific and aggregate statistics gathered
/// during profiling of a parser.
/// 
/// -  4.3

public class ParseInfo {
    internal let atnSimulator: ProfilingATNSimulator

    public init(_ atnSimulator: ProfilingATNSimulator) {
        self.atnSimulator = atnSimulator
    }

    /// Gets an array of {@link org.antlr.v4.runtime.atn.DecisionInfo} instances containing the profiling
    /// information gathered for each decision in the ATN.
    /// 
    /// - returns: An array of {@link org.antlr.v4.runtime.atn.DecisionInfo} instances, indexed by decision
    /// number.
    public func getDecisionInfo() -> [DecisionInfo] {
        return atnSimulator.getDecisionInfo()
    }

    /// Gets the decision numbers for decisions that required one or more
    /// full-context predictions during parsing. These are decisions for which
    /// {@link org.antlr.v4.runtime.atn.DecisionInfo#LL_Fallback} is non-zero.
    /// 
    /// - returns: A list of decision numbers which required one or more
    /// full-context predictions during parsing.
    public func getLLDecisions() -> Array<Int> {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var LL: Array<Int> = Array<Int>()
        let length = decisions.count
        for i in 0..<length {
            let fallBack: Int64 = decisions[i].LL_Fallback
            if fallBack > 0 {
                LL.append(i)
                // LL.add(i);
            }
        }
        return LL
    }

    /// Gets the total time spent during prediction across all decisions made
    /// during parsing. This value is the sum of
    /// {@link org.antlr.v4.runtime.atn.DecisionInfo#timeInPrediction} for all decisions.
    public func getTotalTimeInPrediction() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var t: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            t += decisions[i].timeInPrediction
        }
        return t
    }

    /// Gets the total number of SLL lookahead operations across all decisions
    /// made during parsing. This value is the sum of
    /// {@link org.antlr.v4.runtime.atn.DecisionInfo#SLL_TotalLook} for all decisions.
    public func getTotalSLLLookaheadOps() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var k: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            k += decisions[i].SLL_TotalLook
        }
        return k
    }

    /// Gets the total number of LL lookahead operations across all decisions
    /// made during parsing. This value is the sum of
    /// {@link org.antlr.v4.runtime.atn.DecisionInfo#LL_TotalLook} for all decisions.
    public func getTotalLLLookaheadOps() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var k: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            k += decisions[i].LL_TotalLook
        }
        return k
    }

    /// Gets the total number of ATN lookahead operations for SLL prediction
    /// across all decisions made during parsing.
    public func getTotalSLLATNLookaheadOps() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var k: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            k += decisions[i].SLL_ATNTransitions
        }
        return k
    }

    /// Gets the total number of ATN lookahead operations for LL prediction
    /// across all decisions made during parsing.
    public func getTotalLLATNLookaheadOps() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var k: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            k += decisions[i].LL_ATNTransitions
        }
        return k
    }

    /// Gets the total number of ATN lookahead operations for SLL and LL
    /// prediction across all decisions made during parsing.
    /// 
    /// <p>
    /// This value is the sum of {@link #getTotalSLLATNLookaheadOps} and
    /// {@link #getTotalLLATNLookaheadOps}.</p>
    public func getTotalATNLookaheadOps() -> Int64 {
        var decisions: [DecisionInfo] = atnSimulator.getDecisionInfo()
        var k: Int64 = 0
        let length = decisions.count
        for i in 0..<length {
            k += decisions[i].SLL_ATNTransitions
            k += decisions[i].LL_ATNTransitions
        }
        return k
    }

    /// Gets the total number of DFA states stored in the DFA cache for all
    /// decisions in the ATN.
    public func getDFASize() -> Int {
        var n: Int = 0
        let decisionToDFA: [DFA] = atnSimulator.decisionToDFA
        let length = decisionToDFA.count
        for i in 0..<length {
            n += getDFASize(i)
        }
        return n
    }

    /// Gets the total number of DFA states stored in the DFA cache for a
    /// particular decision.
    public func getDFASize(_ decision: Int) -> Int {
        let decisionToDFA: DFA = atnSimulator.decisionToDFA[decision]
        return decisionToDFA.states.count
    }
}
