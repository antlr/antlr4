/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// This implementation of _org.antlr.v4.runtime.ANTLRErrorListener_ can be used to identify
/// certain potential correctness and performance problems in grammars. "Reports"
/// are made by calling _org.antlr.v4.runtime.Parser#notifyErrorListeners_ with the appropriate
/// message.
/// 
/// * __Ambiguities__: These are cases where more than one path through the
/// grammar can match the input.
/// * __Weak context sensitivity__: These are cases where full-context
/// prediction resolved an SLL conflict to a unique alternative which equaled the
/// minimum alternative of the SLL conflict.
/// * __Strong (forced) context sensitivity__: These are cases where the
/// full-context prediction resolved an SLL conflict to a unique alternative,
/// __and__ the minimum alternative of the SLL conflict was found to not be
/// a truly viable alternative. Two-stage parsing cannot be used for inputs where
/// this situation occurs.
/// 
/// -  Sam Harwell
/// 

import Foundation

public class DiagnosticErrorListener: BaseErrorListener {
    /// 
    /// When `true`, only exactly known ambiguities are reported.
    /// 
    internal final var exactOnly: Bool

    /// 
    /// Initializes a new instance of _org.antlr.v4.runtime.DiagnosticErrorListener_ which only
    /// reports exact ambiguities.
    /// 
    public convenience override init() {
        self.init(true)
    }

    /// 
    /// Initializes a new instance of _org.antlr.v4.runtime.DiagnosticErrorListener_, specifying
    /// whether all ambiguities or only exact ambiguities are reported.
    /// 
    /// - parameter exactOnly: `true` to report only exact ambiguities, otherwise
    /// `false` to report all ambiguities.
    /// 
    public init(_ exactOnly: Bool) {
        self.exactOnly = exactOnly
    }

    override
    public func reportAmbiguity(_ recognizer: Parser,
        _ dfa: DFA,
        _ startIndex: Int,
        _ stopIndex: Int,
        _ exact: Bool,
        _ ambigAlts: BitSet,
        _ configs: ATNConfigSet) {
            if exactOnly && !exact {
                return
            }

            let decision = getDecisionDescription(recognizer, dfa)
            let conflictingAlts = getConflictingAlts(ambigAlts, configs)
            let text = getTextInInterval(recognizer, startIndex, stopIndex)
            let message = "reportAmbiguity d=\(decision): ambigAlts=\(conflictingAlts), input='\(text)'"
            recognizer.notifyErrorListeners(message)
    }

    override
    public func reportAttemptingFullContext(_ recognizer: Parser,
        _ dfa: DFA,
        _ startIndex: Int,
        _ stopIndex: Int,
        _ conflictingAlts: BitSet?,
        _ configs: ATNConfigSet) {
            let decision = getDecisionDescription(recognizer, dfa)
            let text = getTextInInterval(recognizer, startIndex, stopIndex)
            let message = "reportAttemptingFullContext d=\(decision), input='\(text)'"
            recognizer.notifyErrorListeners(message)
    }

    override
    public func reportContextSensitivity(_ recognizer: Parser,
        _ dfa: DFA,
        _ startIndex: Int,
        _ stopIndex: Int,
        _ prediction: Int,
        _ configs: ATNConfigSet) {
            let decision = getDecisionDescription(recognizer, dfa)
            let text = getTextInInterval(recognizer, startIndex, stopIndex)
            let message = "reportContextSensitivity d=\(decision), input='\(text)'"
            recognizer.notifyErrorListeners(message)
    }

    internal func getDecisionDescription(_ recognizer: Parser, _ dfa: DFA) -> String {
        let decision: Int = dfa.decision
        let ruleIndex: Int = dfa.atnStartState.ruleIndex!

        let ruleNames: [String] = recognizer.getRuleNames()
        if ruleIndex < 0 || ruleIndex >= ruleNames.count {
            return String(decision)
        }

        let ruleName: String = ruleNames[ruleIndex]
        //if (ruleName == nil || ruleName.isEmpty()) {
        if ruleName.isEmpty {
            return String(decision)
        }
        return "\(decision) (\(ruleName))"
    }

    /// 
    /// Computes the set of conflicting or ambiguous alternatives from a
    /// configuration set, if that information was not already provided by the
    /// parser.
    /// 
    /// - parameter reportedAlts: The set of conflicting or ambiguous alternatives, as
    /// reported by the parser.
    /// - parameter configs: The conflicting or ambiguous configuration set.
    /// - returns: Returns `reportedAlts` if it is not `null`, otherwise
    /// returns the set of alternatives represented in `configs`.
    /// 
    internal func getConflictingAlts(_ reportedAlts: BitSet?, _ configs: ATNConfigSet) -> BitSet {
        return reportedAlts ?? configs.getAltBitSet()
    }
}


fileprivate func getTextInInterval(_ recognizer: Parser, _ startIndex: Int, _ stopIndex: Int) -> String {
    do {
        return try recognizer.getTokenStream()?.getText(Interval.of(startIndex, stopIndex)) ?? "<unknown>"
    }
    catch {
        return "<unknown>"
    }
}
