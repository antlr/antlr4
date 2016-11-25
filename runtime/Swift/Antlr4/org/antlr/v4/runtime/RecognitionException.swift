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
