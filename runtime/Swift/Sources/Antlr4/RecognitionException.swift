/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/** The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
 *  3 kinds of errors: prediction errors, failed predicate errors, and
 *  mismatched input errors. In each case, the parser knows where it is
 *  in the input, where it is in the ATN, the rule invocation stack,
 *  and what kind of problem occurred.
 */

public class RecognitionException<T:ATNSimulator>  {
    /** The {@link org.antlr.v4.runtime.Recognizer} where this exception originated. */
    private final var recognizer: Recognizer<T>?
    //Recognizer<AnyObject,ATNSimulator>? ;

    private final var ctx: RuleContext?

    private final var input: IntStream

    /**
     * The current {@link org.antlr.v4.runtime.Token} when an error occurred. Since not all streams
     * support accessing symbols by index, we have to track the {@link org.antlr.v4.runtime.Token}
     * instance itself.
     */
    private var offendingToken: Token!

    private var offendingState: Int = -1

    public var message: String?
    public init(_ recognizer: Recognizer<T>?,
                _ input: IntStream,
                _ ctx: ParserRuleContext?) {
        self.recognizer = recognizer
        self.input = input
        self.ctx = ctx
        if let recognizer = recognizer {
            self.offendingState = recognizer.getState()
        }
    }

    public init(_ message: String,
                _ recognizer: Recognizer<T>?,
                _ input: IntStream,
                _ ctx: ParserRuleContext?) {
        self.message = message
        self.recognizer = recognizer
        self.input = input
        self.ctx = ctx
        if let recognizer = recognizer {
            self.offendingState = recognizer.getState()
        }
    }

    /**
     * Get the ATN state number the parser was in at the time the error
     * occurred. For {@link org.antlr.v4.runtime.NoViableAltException} and
     * {@link org.antlr.v4.runtime.LexerNoViableAltException} exceptions, this is the
     * {@link org.antlr.v4.runtime.atn.DecisionState} number. For others, it is the state whose outgoing
     * edge we couldn't match.
     *
     * <p>If the state number is not known, this method returns -1.</p>
     */
    public func getOffendingState() -> Int {
        return offendingState
    }

    internal final func setOffendingState(_ offendingState: Int) {
        self.offendingState = offendingState
    }

    /**
     * Gets the set of input symbols which could potentially follow the
     * previously matched symbol at the time this exception was thrown.
     *
     * <p>If the set of expected tokens is not known and could not be computed,
     * this method returns {@code null}.</p>
     *
     * @return The set of token types that could potentially follow the current
     * state in the ATN, or {@code null} if the information is not available.
     */
    public func getExpectedTokens() -> IntervalSet? {
        if let recognizer = recognizer {
            return try? recognizer.getATN().getExpectedTokens(offendingState, ctx!)
        }

        return nil
    }

    /**
     * Gets the {@link org.antlr.v4.runtime.RuleContext} at the time this exception was thrown.
     *
     * <p>If the context is not available, this method returns {@code null}.</p>
     *
     * @return The {@link org.antlr.v4.runtime.RuleContext} at the time this exception was thrown.
     * If the context is not available, this method returns {@code null}.
     */
    public func getCtx() -> RuleContext? {
        return ctx
    }

    /**
     * Gets the input stream which is the symbol source for the recognizer where
     * this exception was thrown.
     *
     * <p>If the input stream is not available, this method returns {@code null}.</p>
     *
     * @return The input stream which is the symbol source for the recognizer
     * where this exception was thrown, or {@code null} if the stream is not
     * available.
     */
    public func getInputStream() -> IntStream {
        return input
    }


    public func getOffendingToken() -> Token {
        return offendingToken
    }

    internal final func setOffendingToken(_ offendingToken: Token) {
        self.offendingToken = offendingToken
    }

    /**
     * Gets the {@link org.antlr.v4.runtime.Recognizer} where this exception occurred.
     *
     * <p>If the recognizer is not available, this method returns {@code null}.</p>
     *
     * @return The recognizer where this exception occurred, or {@code null} if
     * the recognizer is not available.
     */
    public func getRecognizer() -> Recognizer<T>? {
        return recognizer
    }
}
