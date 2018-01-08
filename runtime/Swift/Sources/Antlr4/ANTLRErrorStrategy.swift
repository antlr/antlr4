/// 
/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 
/// 


/// 
/// 
/// The interface for defining strategies to deal with syntax errors
/// encountered during a parse by ANTLR-generated parsers. We distinguish between three
/// different kinds of errors:
/// 
/// * The parser could not figure out which path to take in the ATN (none of
/// the available alternatives could possibly match)
/// * The current input does not match what we were looking for
/// * A predicate evaluated to false
/// 
/// Implementations of this interface report syntax errors by calling
/// _org.antlr.v4.runtime.Parser#notifyErrorListeners_.
/// 
/// TODO: what to do about lexers
/// 
public protocol ANTLRErrorStrategy {
    /// 
    /// Reset the error handler state for the specified `recognizer`.
    /// - parameter recognizer: the parser instance
    /// 
    func reset(_ recognizer: Parser)

    /// 
    /// This method is called when an unexpected symbol is encountered during an
    /// inline match operation, such as _org.antlr.v4.runtime.Parser#match_. If the error
    /// strategy successfully recovers from the match failure, this method
    /// returns the _org.antlr.v4.runtime.Token_ instance which should be treated as the
    /// successful result of the match.
    /// 
    /// This method handles the consumption of any tokens - the caller should
    /// __not__ call _org.antlr.v4.runtime.Parser#consume_ after a successful recovery.
    /// 
    /// Note that the calling code will not report an error if this method
    /// returns successfully. The error strategy implementation is responsible
    /// for calling _org.antlr.v4.runtime.Parser#notifyErrorListeners_ as appropriate.
    /// 
    /// - parameter recognizer: the parser instance
    /// - throws: _RecognitionException_ if the error strategy was not able to
    /// recover from the unexpected input symbol
    /// 
    @discardableResult
    func recoverInline(_ recognizer: Parser) throws -> Token

    /// 
    /// This method is called to recover from exception `e`. This method is
    /// called after _#reportError_ by the default exception handler
    /// generated for a rule method.
    /// 
    /// - seealso: #reportError
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception to recover from
    /// - throws: _RecognitionException_ if the error strategy could not recover from
    /// the recognition exception
    /// 
    func recover(_ recognizer: Parser, _ e: RecognitionException) throws

    /// 
    /// This method provides the error handler with an opportunity to handle
    /// syntactic or semantic errors in the input stream before they result in a
    /// _org.antlr.v4.runtime.RecognitionException_.
    /// 
    /// The generated code currently contains calls to _#sync_ after
    /// entering the decision state of a closure block (`(...)*` or
    /// `(...)+`).
    /// 
    /// For an implementation based on Jim Idle's "magic sync" mechanism, see
    /// _org.antlr.v4.runtime.DefaultErrorStrategy#sync_.
    /// 
    /// - seealso: org.antlr.v4.runtime.DefaultErrorStrategy#sync
    /// 
    /// - parameter recognizer: the parser instance
    /// - throws: _RecognitionException_ if an error is detected by the error
    /// strategy but cannot be automatically recovered at the current state in
    /// the parsing process
    /// 
    func sync(_ recognizer: Parser) throws

    /// 
    /// Tests whether or not recognizer} is in the process of recovering
    /// from an error. In error recovery mode, _org.antlr.v4.runtime.Parser#consume_ adds
    /// symbols to the parse tree by calling
    /// _Parser#createErrorNode(ParserRuleContext, Token)_ then
    /// _ParserRuleContext#addErrorNode(ErrorNode)_ instead of
    /// _Parser#createTerminalNode(ParserRuleContext, Token)_.
    /// 
    /// - parameter recognizer: the parser instance
    /// - returns: `true` if the parser is currently recovering from a parse
    /// error, otherwise `false`
    /// 
    func inErrorRecoveryMode(_ recognizer: Parser) -> Bool

    /// 
    /// This method is called by when the parser successfully matches an input
    /// symbol.
    /// 
    /// - parameter recognizer: the parser instance
    /// 
    func reportMatch(_ recognizer: Parser)

    /// 
    /// Report any kind of _org.antlr.v4.runtime.RecognitionException_. This method is called by
    /// the default exception handler generated for a rule method.
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception to report
    /// 
    func reportError(_ recognizer: Parser, _ e: RecognitionException)
}
