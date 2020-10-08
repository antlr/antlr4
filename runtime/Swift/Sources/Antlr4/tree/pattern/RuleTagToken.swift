/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/// 
/// A _org.antlr.v4.runtime.Token_ object representing an entire subtree matched by a parser
/// rule; e.g., `<expr>`. These tokens are created for _org.antlr.v4.runtime.tree.pattern.TagChunk_
/// chunks where the tag corresponds to a parser rule.
/// 

public class RuleTagToken: Token, CustomStringConvertible {
    /// 
    /// This is the backing field for _#getRuleName_.
    /// 
    private let ruleName: String
    /// 
    /// The token type for the current token. This is the token type assigned to
    /// the bypass alternative for the rule during ATN deserialization.
    /// 
    private let bypassTokenType: Int
    /// 
    /// This is the backing field for _#getLabel_.
    /// 
    private let label: String?

    public var visited = false

    /// 
    /// Constructs a new instance of _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ with the specified rule
    /// name and bypass token type and no label.
    /// 
    /// - Parameter ruleName: The name of the parser rule this rule tag matches.
    /// - Parameter bypassTokenType: The bypass token type assigned to the parser rule.
    /// 
    /// - Throws: ANTLRError.illegalArgument if `ruleName` is `null`
    /// or empty.
    /// 
    public convenience init(_ ruleName: String, _ bypassTokenType: Int) {
        self.init(ruleName, bypassTokenType, nil)
    }

    /// 
    /// Constructs a new instance of _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ with the specified rule
    /// name, bypass token type, and label.
    /// 
    /// - Parameter ruleName: The name of the parser rule this rule tag matches.
    /// - Parameter bypassTokenType: The bypass token type assigned to the parser rule.
    /// - Parameter label: The label associated with the rule tag, or `null` if
    /// the rule tag is unlabeled.
    /// 
    /// - Throws: ANTLRError.illegalArgument if `ruleName` is `null`
    /// or empty.
    /// 
    public init(_ ruleName: String, _ bypassTokenType: Int, _ label: String?) {
        self.ruleName = ruleName
        self.bypassTokenType = bypassTokenType
        self.label = label
    }

    /// 
    /// Gets the name of the rule associated with this rule tag.
    /// 
    /// - Returns: The name of the parser rule associated with this rule tag.
    /// 
    public final func getRuleName() -> String {
        return ruleName
    }

    /// 
    /// Gets the label associated with the rule tag.
    /// 
    /// - Returns: The name of the label associated with the rule tag, or
    /// `null` if this is an unlabeled rule tag.
    /// 
    public final func getLabel() -> String? {
        return label
    }

    /// 
    /// Rule tag tokens are always placed on the _#DEFAULT_CHANNEL_.
    /// 
    public func getChannel() -> Int {
        return RuleTagToken.DEFAULT_CHANNEL
    }

    /// 
    /// This method returns the rule tag formatted with `<` and `>`
    /// delimiters.
    /// 
    public func getText() -> String? {
        if let label = label {
            return "<\(label):\(ruleName)>"
        }
        return "<\(ruleName)>"
    }

    /// 
    /// Rule tag tokens have types assigned according to the rule bypass
    /// transitions created during ATN deserialization.
    /// 
    public func getType() -> Int {
        return bypassTokenType
    }

    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns 0.
    /// 
    public func getLine() -> Int {
        return 0
    }

    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns -1.
    /// 
    public func getCharPositionInLine() -> Int {
        return -1
    }

    /// 
    /// 
    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns -1.
    /// 
    public func getTokenIndex() -> Int {
        return -1
    }

    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns -1.
    /// 
    public func getStartIndex() -> Int {
        return -1
    }

    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns -1.
    /// 
    public func getStopIndex() -> Int {
        return -1
    }

    /// 
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns `null`.
    /// 
    public func getTokenSource() -> TokenSource? {
        return nil
    }

    ///
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ always returns `null`.
    /// 
    public func getInputStream() -> CharStream? {
        return nil
    }

    public func getTokenSourceAndStream() -> TokenSourceAndStream {
        return TokenSourceAndStream.EMPTY
    }

    ///
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.RuleTagToken_ returns a string of the form
    /// `ruleName:bypassTokenType`.
    /// 
    public var description: String {
        return ruleName + ":" + String(bypassTokenType)
    }


}

