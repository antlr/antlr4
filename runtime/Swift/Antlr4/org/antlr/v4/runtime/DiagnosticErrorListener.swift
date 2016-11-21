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


/**
* This implementation of {@link org.antlr.v4.runtime.ANTLRErrorListener} can be used to identify
* certain potential correctness and performance problems in grammars. "Reports"
* are made by calling {@link org.antlr.v4.runtime.Parser#notifyErrorListeners} with the appropriate
* message.
*
* <ul>
* <li><b>Ambiguities</b>: These are cases where more than one path through the
* grammar can match the input.</li>
* <li><b>Weak context sensitivity</b>: These are cases where full-context
* prediction resolved an SLL conflict to a unique alternative which equaled the
* minimum alternative of the SLL conflict.</li>
* <li><b>Strong (forced) context sensitivity</b>: These are cases where the
* full-context prediction resolved an SLL conflict to a unique alternative,
* <em>and</em> the minimum alternative of the SLL conflict was found to not be
* a truly viable alternative. Two-stage parsing cannot be used for inputs where
* this situation occurs.</li>
* </ul>
*
* @author Sam Harwell
*/

import Foundation

public class DiagnosticErrorListener: BaseErrorListener {
    /**
     * When {@code true}, only exactly known ambiguities are reported.
     */
    internal final var exactOnly: Bool
    
    /**
     * Initializes a new instance of {@link org.antlr.v4.runtime.DiagnosticErrorListener} which only
     * reports exact ambiguities.
     */
    public convenience override init() {
        self.init(true)
    }
    
    /**
     * Initializes a new instance of {@link org.antlr.v4.runtime.DiagnosticErrorListener}, specifying
     * whether all ambiguities or only exact ambiguities are reported.
     *
     * @param exactOnly {@code true} to report only exact ambiguities, otherwise
     * {@code false} to report all ambiguities.
     */
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
    
    /**
     * Computes the set of conflicting or ambiguous alternatives from a
     * configuration set, if that information was not already provided by the
     * parser.
     *
     * @param reportedAlts The set of conflicting or ambiguous alternatives, as
     * reported by the parser.
     * @param configs The conflicting or ambiguous configuration set.
     * @return Returns {@code reportedAlts} if it is not {@code null}, otherwise
     * returns the set of alternatives represented in {@code configs}.
     */
    internal func getConflictingAlts(_ reportedAlts: BitSet?, _ configs: ATNConfigSet) throws -> BitSet {
        if reportedAlts != nil {
            return reportedAlts!
        }
        let result = try configs.getAltBitSet()
        return result
    }
}
