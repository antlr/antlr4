/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
/// 3 kinds of errors: prediction errors, failed predicate errors, and
/// mismatched input errors. In each case, the parser knows where it is
/// in the input, where it is in the ATN, the rule invocation stack,
/// and what kind of problem occurred.
/// 

public class RecognitionException {
    /// 
    /// The _org.antlr.v4.runtime.Recognizer_ where this exception originated.
    /// 
    private final var recognizer: RecognizerProtocol?

    private final weak var ctx: RuleContext?

    private final var input: IntStream?

    /// 
    /// The current _org.antlr.v4.runtime.Token_ when an error occurred. Since not all streams
    /// support accessing symbols by index, we have to track the _org.antlr.v4.runtime.Token_
    /// instance itself.
    /// 
    private var offendingToken: Token!

    private var offendingState = -1

    public var message: String?

    public init(_ recognizer: RecognizerProtocol?,
                _ input: IntStream,
                _ ctx: ParserRuleContext? = nil,
                _ message: String? = nil) {
        self.recognizer = recognizer
        self.input = input
        self.ctx = ctx
        self.message = message
        if let recognizer = recognizer {
            self.offendingState = recognizer.getState()
        }
    }

    /// 
    /// Get the ATN state number the parser was in at the time the error
    /// occurred. For _org.antlr.v4.runtime.NoViableAltException_ and
    /// _org.antlr.v4.runtime.LexerNoViableAltException_ exceptions, this is the
    /// _org.antlr.v4.runtime.atn.DecisionState_ number. For others, it is the state whose outgoing
    /// edge we couldn't match.
    /// 
    /// If the state number is not known, this method returns -1.
    /// 
    public func getOffendingState() -> Int {
        return offendingState
    }

    internal final func setOffendingState(_ offendingState: Int) {
        self.offendingState = offendingState
    }

    /// 
    /// Gets the set of input symbols which could potentially follow the
    /// previously matched symbol at the time this exception was thrown.
    /// 
    /// If the set of expected tokens is not known and could not be computed,
    /// this method returns `null`.
    /// 
    /// - Returns: The set of token types that could potentially follow the current
    /// state in the ATN, or `null` if the information is not available.
    /// 
    public func getExpectedTokens() -> IntervalSet? {
        if let recognizer = recognizer {
            return try? recognizer.getATN().getExpectedTokens(offendingState, ctx!)
        }
        return nil
    }

    /// 
    /// Gets the _org.antlr.v4.runtime.RuleContext_ at the time this exception was thrown.
    /// 
    /// If the context is not available, this method returns `null`.
    /// 
    /// - Returns: The _org.antlr.v4.runtime.RuleContext_ at the time this exception was thrown.
    /// If the context is not available, this method returns `null`.
    /// 
    public func getCtx() -> RuleContext? {
        return ctx
    }

    /// 
    /// Gets the input stream which is the symbol source for the recognizer where
    /// this exception was thrown.
    /// 
    /// If the input stream is not available, this method returns `null`.
    /// 
    /// - Returns: The input stream which is the symbol source for the recognizer
    /// where this exception was thrown, or `null` if the stream is not
    /// available.
    /// 
    public func getInputStream() -> IntStream? {
        return input
    }

    public func clearInputStream() {
        input = nil
    }

    public func getOffendingToken() -> Token {
        return offendingToken
    }

    internal final func setOffendingToken(_ offendingToken: Token) {
        self.offendingToken = offendingToken
    }

    /// 
    /// Gets the _org.antlr.v4.runtime.Recognizer_ where this exception occurred.
    /// 
    /// If the recognizer is not available, this method returns `null`.
    /// 
    /// - Returns: The recognizer where this exception occurred, or `null` if
    /// the recognizer is not available.
    /// 
    public func getRecognizer() -> RecognizerProtocol? {
        return recognizer
    }

    public func clearRecognizer() {
        self.recognizer = nil
    }
}
