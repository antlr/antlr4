/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// This class represents profiling event information for a context sensitivity.
/// Context sensitivities are decisions where a particular input resulted in an
/// SLL conflict, but LL prediction produced a single unique alternative.
/// 
/// <p>
/// In some cases, the unique alternative identified by LL prediction is not
/// equal to the minimum represented alternative in the conflicting SLL
/// configuration set. Grammars and inputs which result in this scenario are
/// unable to use {@link org.antlr.v4.runtime.atn.PredictionMode#SLL}, which in turn means they cannot use
/// the two-stage parsing strategy to improve parsing performance for that
/// input.</p>
/// 
/// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#reportContextSensitivity
/// - seealso: org.antlr.v4.runtime.ANTLRErrorListener#reportContextSensitivity
/// 
/// -  4.3

public class ContextSensitivityInfo: DecisionEventInfo {
    /// Constructs a new instance of the {@link org.antlr.v4.runtime.atn.ContextSensitivityInfo} class
    /// with the specified detailed context sensitivity information.
    /// 
    /// - parameter decision: The decision number
    /// - parameter configs: The final configuration set containing the unique
    /// alternative identified by full-context prediction
    /// - parameter input: The input token stream
    /// - parameter startIndex: The start index for the current prediction
    /// - parameter stopIndex: The index at which the context sensitivity was
    /// identified during full-context prediction
    public init(_ decision: Int,
                _ configs: ATNConfigSet,
                _ input: TokenStream, _ startIndex: Int, _ stopIndex: Int) {
        super.init(decision, configs, input, startIndex, stopIndex, true)
    }
}
