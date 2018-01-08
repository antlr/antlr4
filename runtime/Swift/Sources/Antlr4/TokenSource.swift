/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// 
/// A source of tokens must provide a sequence of tokens via _#nextToken()_
/// and also must reveal it's source of characters; _org.antlr.v4.runtime.CommonToken_'s text is
/// computed from a _org.antlr.v4.runtime.CharStream_; it only store indices into the char
/// stream.
/// 
/// Errors from the lexer are never passed to the parser. Either you want to keep
/// going or you do not upon token recognition error. If you do not want to
/// continue lexing then you do not want to continue parsing. Just throw an
/// exception not under _org.antlr.v4.runtime.RecognitionException_ and Java will naturally toss
/// you all the way out of the recognizers. If you want to continue lexing then
/// you should not throw an exception to the parser--it has already requested a
/// token. Keep lexing until you get a valid one. Just report errors and keep
/// going, looking for a valid token.
/// 

public protocol TokenSource: class {
    /// 
    /// Return a _org.antlr.v4.runtime.Token_ object from your input stream (usually a
    /// _org.antlr.v4.runtime.CharStream_). Do not fail/return upon lexing error; keep chewing
    /// on the characters until you get a good one; errors are not passed through
    /// to the parser.
    /// 
    func nextToken() throws -> Token

    /// 
    /// Get the line number for the current position in the input stream. The
    /// first line in the input is line 1.
    /// 
    /// - Returns: The line number for the current position in the input stream, or
    /// 0 if the current token source does not track line numbers.
    /// 
    func getLine() -> Int

    /// 
    /// Get the index into the current line for the current position in the input
    /// stream. The first character on a line has position 0.
    /// 
    /// - Returns: The line number for the current position in the input stream, or
    /// -1 if the current token source does not track character positions.
    /// 
    func getCharPositionInLine() -> Int

    /// 
    /// Get the _org.antlr.v4.runtime.CharStream_ from which this token source is currently
    /// providing tokens.
    /// 
    /// - Returns: The _org.antlr.v4.runtime.CharStream_ associated with the current position in
    /// the input, or `null` if no input stream is available for the token
    /// source.
    /// 
    func getInputStream() -> CharStream?

    /// 
    /// Gets the name of the underlying input source. This method returns a
    /// non-null, non-empty string. If such a name is not known, this method
    /// returns _org.antlr.v4.runtime.IntStream#UNKNOWN_SOURCE_NAME_.
    /// 
    func getSourceName() -> String

    /// 
    /// Set the _org.antlr.v4.runtime.TokenFactory_ this token source should use for creating
    /// _org.antlr.v4.runtime.Token_ objects from the input.
    /// 
    /// - Parameter factory: The _org.antlr.v4.runtime.TokenFactory_ to use for creating tokens.
    /// 
    func setTokenFactory(_ factory: TokenFactory)

    /// 
    /// Gets the _org.antlr.v4.runtime.TokenFactory_ this token source is currently using for
    /// creating _org.antlr.v4.runtime.Token_ objects from the input.
    /// 
    /// - Returns: The _org.antlr.v4.runtime.TokenFactory_ currently used by this token source.
    /// 
    func getTokenFactory() -> TokenFactory
}
