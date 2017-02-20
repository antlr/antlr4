/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This is the base class for gathering detailed information about prediction
/// events which occur during parsing.
/// 
/// Note that we could record the parser call stack at the time this event
/// occurred but in the presence of left recursive rules, the stack is kind of
/// meaningless. It's better to look at the individual configurations for their
/// individual stacks. Of course that is a {@link org.antlr.v4.runtime.atn.PredictionContext} object
/// not a parse tree node and so it does not have information about the extent
/// (start...stop) of the various subtrees. Examining the stack tops of all
/// configurations provide the return states for the rule invocations.
/// From there you can get the enclosing rule.
/// 
/// -  4.3

public class DecisionEventInfo {
    /// The invoked decision number which this event is related to.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ATN#decisionToState
    public let decision: Int

    /// The configuration set containing additional information relevant to the
    /// prediction state when the current event occurred, or {@code null} if no
    /// additional information is relevant or available.
    public let configs: ATNConfigSet?

    /// The input token stream which is being parsed.
    public let input: TokenStream

    /// The token index in the input stream at which the current prediction was
    /// originally invoked.
    public let startIndex: Int

    /// The token index in the input stream at which the current event occurred.
    public let stopIndex: Int

    /// {@code true} if the current event occurred during LL prediction;
    /// otherwise, {@code false} if the input occurred during SLL prediction.
    public let fullCtx: Bool

    public init(_ decision: Int,
                _ configs: ATNConfigSet?,
                _ input: TokenStream,
                _ startIndex: Int,
                _ stopIndex: Int,
                _ fullCtx: Bool) {
        self.decision = decision
        self.fullCtx = fullCtx
        self.stopIndex = stopIndex
        self.input = input
        self.startIndex = startIndex
        self.configs = configs
    }
}
