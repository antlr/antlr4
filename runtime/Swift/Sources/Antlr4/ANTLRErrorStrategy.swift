/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// The interface for defining strategies to deal with syntax errors encountered
/// during a parse by ANTLR-generated parsers. We distinguish between three
/// different kinds of errors:
/// 
/// <ul>
/// <li>The parser could not figure out which path to take in the ATN (none of
/// the available alternatives could possibly match)</li>
/// <li>The current input does not match what we were looking for</li>
/// <li>A predicate evaluated to false</li>
/// </ul>
/// 
/// Implementations of this interface report syntax errors by calling
/// {@link org.antlr.v4.runtime.Parser#notifyErrorListeners}.
/// 
/// <p>TODO: what to do about lexers</p>

public protocol ANTLRErrorStrategy {
    /// Reset the error handler state for the specified {@code recognizer}.
    /// - parameter recognizer: the parser instance
    func reset(_ recognizer: Parser)

    /// This method is called when an unexpected symbol is encountered during an
    /// inline match operation, such as {@link org.antlr.v4.runtime.Parser#match}. If the error
    /// strategy successfully recovers from the match failure, this method
    /// returns the {@link org.antlr.v4.runtime.Token} instance which should be treated as the
    /// successful result of the match.
    /// 
    /// <p>This method handles the consumption of any tokens - the caller should
    /// <b>not</b> call {@link org.antlr.v4.runtime.Parser#consume} after a successful recovery.</p>
    /// 
    /// <p>Note that the calling code will not report an error if this method
    /// returns successfully. The error strategy implementation is responsible
    /// for calling {@link org.antlr.v4.runtime.Parser#notifyErrorListeners} as appropriate.</p>
    /// 
    /// - parameter recognizer: the parser instance
    /// -  org.antlr.v4.runtime.RecognitionException if the error strategy was not able to
    /// recover from the unexpected input symbol
    @discardableResult
    func recoverInline(_ recognizer: Parser) throws -> Token // RecognitionException;

    /// This method is called to recover from exception {@code e}. This method is
    /// called after {@link #reportError} by the default exception handler
    /// generated for a rule method.
    ///
    /// - seealso: #reportError
    ///
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception to recover from
    /// -  org.antlr.v4.runtime.RecognitionException if the error strategy could not recover from
    /// the recognition exception
    func recover(_ recognizer: Parser, _ e: AnyObject) throws // RecognitionException;

    /// This method provides the error handler with an opportunity to handle
    /// syntactic or semantic errors in the input stream before they result in a
    /// {@link org.antlr.v4.runtime.RecognitionException}.
    ///
    /// <p>The generated code currently contains calls to {@link #sync} after
    /// entering the decision state of a closure block ({@code (...)*} or
    /// {@code (...)+}).</p>
    ///
    /// <p>For an implementation based on Jim Idle's "magic sync" mechanism, see
    /// {@link org.antlr.v4.runtime.DefaultErrorStrategy#sync}.</p>
    ///
    /// - seealso: org.antlr.v4.runtime.DefaultErrorStrategy#sync
    ///
    /// - parameter recognizer: the parser instance
    /// -  org.antlr.v4.runtime.RecognitionException if an error is detected by the error
    /// strategy but cannot be automatically recovered at the current state in
    /// the parsing process
    func sync(_ recognizer: Parser) throws // RecognitionException;

    /// Tests whether or not {@code recognizer} is in the process of recovering
    /// from an error. In error recovery mode, {@link org.antlr.v4.runtime.Parser#consume} adds
    /// symbols to the parse tree by calling
    /// {@link org.antlr.v4.runtime.ParserRuleContext#addErrorNode(org.antlr.v4.runtime.Token)} instead of
    /// {@link org.antlr.v4.runtime.ParserRuleContext#addChild(org.antlr.v4.runtime.Token)}.
    ///
    /// - parameter recognizer: the parser instance
    /// - returns: {@code true} if the parser is currently recovering from a parse
    /// error, otherwise {@code false}
    func inErrorRecoveryMode(_ recognizer: Parser) -> Bool

    /// This method is called by when the parser successfully matches an input
    /// symbol.
    /// 
    /// - parameter recognizer: the parser instance
    func reportMatch(_ recognizer: Parser)

    /// Report any kind of {@link org.antlr.v4.runtime.RecognitionException}. This method is called by
    /// the default exception handler generated for a rule method.
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception to report
    func reportError(_ recognizer: Parser, _ e: AnyObject)
}
