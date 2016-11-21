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
 * The interface for defining strategies to deal with syntax errors encountered
 * during a parse by ANTLR-generated parsers. We distinguish between three
 * different kinds of errors:
 *
 * <ul>
 * <li>The parser could not figure out which path to take in the ATN (none of
 * the available alternatives could possibly match)</li>
 * <li>The current input does not match what we were looking for</li>
 * <li>A predicate evaluated to false</li>
 * </ul>
 *
 * Implementations of this interface report syntax errors by calling
 * {@link org.antlr.v4.runtime.Parser#notifyErrorListeners}.
 *
 * <p>TODO: what to do about lexers</p>
 */

public protocol ANTLRErrorStrategy {
    /**
     * Reset the error handler state for the specified {@code recognizer}.
     * @param recognizer the parser instance
     */
    func reset(_ recognizer: Parser)

    /**
     * This method is called when an unexpected symbol is encountered during an
     * inline match operation, such as {@link org.antlr.v4.runtime.Parser#match}. If the error
     * strategy successfully recovers from the match failure, this method
     * returns the {@link org.antlr.v4.runtime.Token} instance which should be treated as the
     * successful result of the match.
     *
   * <p>This method handles the consumption of any tokens - the caller should
     * <b>not</b> call {@link org.antlr.v4.runtime.Parser#consume} after a successful recovery.</p>
     *
     * <p>Note that the calling code will not report an error if this method
     * returns successfully. The error strategy implementation is responsible
     * for calling {@link org.antlr.v4.runtime.Parser#notifyErrorListeners} as appropriate.</p>
     *
     * @param recognizer the parser instance
     * @throws org.antlr.v4.runtime.RecognitionException if the error strategy was not able to
     * recover from the unexpected input symbol
     */
    @discardableResult
    func recoverInline(_ recognizer: Parser) throws -> Token // RecognitionException;

    /**
     * This method is called to recover from exception {@code e}. This method is
     * called after {@link #reportError} by the default exception handler
     * generated for a rule method.
     *
     * @see #reportError
     *
     * @param recognizer the parser instance
     * @param e the recognition exception to recover from
     * @throws org.antlr.v4.runtime.RecognitionException if the error strategy could not recover from
     * the recognition exception
     */
    func recover(_ recognizer: Parser, _ e: AnyObject) throws // RecognitionException;

    /**
     * This method provides the error handler with an opportunity to handle
     * syntactic or semantic errors in the input stream before they result in a
     * {@link org.antlr.v4.runtime.RecognitionException}.
     *
     * <p>The generated code currently contains calls to {@link #sync} after
     * entering the decision state of a closure block ({@code (...)*} or
     * {@code (...)+}).</p>
     *
     * <p>For an implementation based on Jim Idle's "magic sync" mechanism, see
     * {@link org.antlr.v4.runtime.DefaultErrorStrategy#sync}.</p>
     *
     * @see org.antlr.v4.runtime.DefaultErrorStrategy#sync
     *
     * @param recognizer the parser instance
     * @throws org.antlr.v4.runtime.RecognitionException if an error is detected by the error
     * strategy but cannot be automatically recovered at the current state in
     * the parsing process
     */
    func sync(_ recognizer: Parser) throws // RecognitionException;

    /**
     * Tests whether or not {@code recognizer} is in the process of recovering
     * from an error. In error recovery mode, {@link org.antlr.v4.runtime.Parser#consume} adds
     * symbols to the parse tree by calling
     * {@link org.antlr.v4.runtime.ParserRuleContext#addErrorNode(org.antlr.v4.runtime.Token)} instead of
     * {@link org.antlr.v4.runtime.ParserRuleContext#addChild(org.antlr.v4.runtime.Token)}.
     *
     * @param recognizer the parser instance
     * @return {@code true} if the parser is currently recovering from a parse
     * error, otherwise {@code false}
     */
    func inErrorRecoveryMode(_ recognizer: Parser) -> Bool

    /**
     * This method is called by when the parser successfully matches an input
     * symbol.
     *
     * @param recognizer the parser instance
     */
    func reportMatch(_ recognizer: Parser)

    /**
     * Report any kind of {@link org.antlr.v4.runtime.RecognitionException}. This method is called by
     * the default exception handler generated for a rule method.
     *
     * @param recognizer the parser instance
     * @param e the recognition exception to report
     */
    func reportError(_ recognizer: Parser, _ e: AnyObject)
}
