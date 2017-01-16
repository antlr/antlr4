/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This class represents profiling event information for an ambiguity.
/// Ambiguities are decisions where a particular input resulted in an SLL
/// conflict, followed by LL prediction also reaching a conflict state
/// (indicating a true ambiguity in the grammar).
/// 
/// <p>
/// This event may be reported during SLL prediction in cases where the
/// conflicting SLL configuration set provides sufficient information to
/// determine that the SLL conflict is truly an ambiguity. For example, if none
/// of the ATN configurations in the conflicting SLL configuration set have
/// traversed a global follow transition (i.e.
/// {@link org.antlr.v4.runtime.atn.ATNConfig#reachesIntoOuterContext} is 0 for all configurations), then
/// the result of SLL prediction for that input is known to be equivalent to the
/// result of LL prediction for that input.</p>
/// 
/// <p>
/// In some cases, the minimum represented alternative in the conflicting LL
/// configuration set is not equal to the minimum represented alternative in the
/// conflicting SLL configuration set. Grammars and inputs which result in this
/// scenario are unable to use {@link org.antlr.v4.runtime.atn.PredictionMode#SLL}, which in turn means
/// they cannot use the two-stage parsing strategy to improve parsing performance
/// for that input.</p>
/// 
/// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#reportAmbiguity
/// - seealso: org.antlr.v4.runtime.ANTLRErrorListener#reportAmbiguity
/// 
/// -  4.3

public class AmbiguityInfo: DecisionEventInfo {
    /// The set of alternative numbers for this decision event that lead to a valid parse.
    public var ambigAlts: BitSet

    /// Constructs a new instance of the {@link org.antlr.v4.runtime.atn.AmbiguityInfo} class with the
    /// specified detailed ambiguity information.
    /// 
    /// - parameter decision: The decision number
    /// - parameter configs: The final configuration set identifying the ambiguous
    /// alternatives for the current input
    /// - parameter ambigAlts: The set of alternatives in the decision that lead to a valid parse.
    /// - parameter input: The input token stream
    /// - parameter startIndex: The start index for the current prediction
    /// - parameter stopIndex: The index at which the ambiguity was identified during
    /// prediction
    /// - parameter fullCtx: {@code true} if the ambiguity was identified during LL
    /// prediction; otherwise, {@code false} if the ambiguity was identified
    /// during SLL prediction
    public init(_ decision: Int,
                _ configs: ATNConfigSet,
                _ ambigAlts: BitSet,
                _ input: TokenStream, _ startIndex: Int, _ stopIndex: Int,
                _ fullCtx: Bool) {
        self.ambigAlts = ambigAlts
        super.init(decision, configs, input, startIndex, stopIndex, fullCtx)

    }
}
