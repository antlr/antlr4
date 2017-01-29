/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// This implementation of {@link org.antlr.v4.runtime.ANTLRErrorListener} can be used to identify
/// certain potential correctness and performance problems in grammars. "Reports"
/// are made by calling {@link org.antlr.v4.runtime.Parser#notifyErrorListeners} with the appropriate
/// message.
/// 
/// <ul>
/// <li><b>Ambiguities</b>: These are cases where more than one path through the
/// grammar can match the input.</li>
/// <li><b>Weak context sensitivity</b>: These are cases where full-context
/// prediction resolved an SLL conflict to a unique alternative which equaled the
/// minimum alternative of the SLL conflict.</li>
/// <li><b>Strong (forced) context sensitivity</b>: These are cases where the
/// full-context prediction resolved an SLL conflict to a unique alternative,
/// <em>and</em> the minimum alternative of the SLL conflict was found to not be
/// a truly viable alternative. Two-stage parsing cannot be used for inputs where
/// this situation occurs.</li>
/// </ul>
/// 
/// -  Sam Harwell

import Foundation

public class DiagnosticErrorListener: BaseErrorListener {
    /// When {@code true}, only exactly known ambiguities are reported.
    internal final var exactOnly: Bool

    /// Initializes a new instance of {@link org.antlr.v4.runtime.DiagnosticErrorListener} which only
    /// reports exact ambiguities.
    public convenience override init() {
        self.init(true)
    }

    /// Initializes a new instance of {@link org.antlr.v4.runtime.DiagnosticErrorListener}, specifying
    /// whether all ambiguities or only exact ambiguities are reported.
    /// 
    /// - parameter exactOnly: {@code true} to report only exact ambiguities, otherwise
    /// {@code false} to report all ambiguities.
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
        _ configs: ATNConfigSet) throws {
            if exactOnly && !exact {
                return
            }

            let format: String = "reportAmbiguity d=%@: ambigAlts=%@, input='%@'"
            let decision: String = getDecisionDescription(recognizer, dfa)
            let conflictingAlts: BitSet = try getConflictingAlts(ambigAlts, configs)
            let text: String = try recognizer.getTokenStream()!.getText(Interval.of(startIndex, stopIndex))

            let message: String = NSString(format: format as NSString, decision, conflictingAlts.description, text) as String
            try recognizer.notifyErrorListeners(message)
    }

    override
    public func reportAttemptingFullContext(_ recognizer: Parser,
        _ dfa: DFA,
        _ startIndex: Int,
        _ stopIndex: Int,
        _ conflictingAlts: BitSet?,
        _ configs: ATNConfigSet) throws {
            let format: String = "reportAttemptingFullContext d=%@, input='%@'"
            let decision: String = getDecisionDescription(recognizer, dfa)
            let text: String = try recognizer.getTokenStream()!.getText(Interval.of(startIndex, stopIndex))
            let message: String = NSString(format: format as NSString, decision, text) as String
            try recognizer.notifyErrorListeners(message)
    }

    override
    public func reportContextSensitivity(_ recognizer: Parser,
        _ dfa: DFA,
        _ startIndex: Int,
        _ stopIndex: Int,
        _ prediction: Int,
        _ configs: ATNConfigSet) throws {
            let format: String = "reportContextSensitivity d=%@, input='%@'"
            let decision: String = getDecisionDescription(recognizer, dfa)
            let text: String = try recognizer.getTokenStream()!.getText(Interval.of(startIndex, stopIndex))
            let message: String = NSString(format: format as NSString, decision, text) as String
            try recognizer.notifyErrorListeners(message)
    }

    internal func getDecisionDescription(_ recognizer: Parser, _ dfa: DFA) -> String {
        let decision: Int = dfa.decision
        let ruleIndex: Int = dfa.atnStartState.ruleIndex!

        var ruleNames: [String] = recognizer.getRuleNames()
        if ruleIndex < 0 || ruleIndex >= ruleNames.count {
            return String(decision)
        }

        let ruleName: String = ruleNames[ruleIndex]
        //if (ruleName == nil || ruleName.isEmpty()) {
        if ruleName.isEmpty {
            return String(decision)
        }

        return NSString(format: "%d (%@)", decision, ruleName) as String
    }

    /// Computes the set of conflicting or ambiguous alternatives from a
    /// configuration set, if that information was not already provided by the
    /// parser.
    /// 
    /// - parameter reportedAlts: The set of conflicting or ambiguous alternatives, as
    /// reported by the parser.
    /// - parameter configs: The conflicting or ambiguous configuration set.
    /// - returns: Returns {@code reportedAlts} if it is not {@code null}, otherwise
    /// returns the set of alternatives represented in {@code configs}.
    internal func getConflictingAlts(_ reportedAlts: BitSet?, _ configs: ATNConfigSet) throws -> BitSet {
        if reportedAlts != nil {
            return reportedAlts!
        }
        let result = try configs.getAltBitSet()
        return result
    }
}
