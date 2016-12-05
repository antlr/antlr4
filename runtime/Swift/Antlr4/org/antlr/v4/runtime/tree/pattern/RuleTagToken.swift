/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 * A {@link org.antlr.v4.runtime.Token} object representing an entire subtree matched by a parser
 * rule; e.g., {@code <expr>}. These tokens are created for {@link org.antlr.v4.runtime.tree.pattern.TagChunk}
 * chunks where the tag corresponds to a parser rule.
 */

public class RuleTagToken: Token, CustomStringConvertible {
    /**
     * This is the backing field for {@link #getRuleName}.
     */
    private final var ruleName: String
    /**
     * The token type for the current token. This is the token type assigned to
     * the bypass alternative for the rule during ATN deserialization.
     */
    private final var bypassTokenType: Int
    /**
     * This is the backing field for {@link #getLabel}.
     */
    private final var label: String?

    public var visited: Bool = false

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} with the specified rule
     * name and bypass token type and no label.
     *
     * @param ruleName The name of the parser rule this rule tag matches.
     * @param bypassTokenType The bypass token type assigned to the parser rule.
     *
     * @exception IllegalArgumentException if {@code ruleName} is {@code null}
     * or empty.
     */
    public convenience init(_ ruleName: String, _ bypassTokenType: Int) {
        self.init(ruleName, bypassTokenType, nil)
    }

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} with the specified rule
     * name, bypass token type, and label.
     *
     * @param ruleName The name of the parser rule this rule tag matches.
     * @param bypassTokenType The bypass token type assigned to the parser rule.
     * @param label The label associated with the rule tag, or {@code null} if
     * the rule tag is unlabeled.
     *
     * @exception IllegalArgumentException if {@code ruleName} is {@code null}
     * or empty.
     */
    public init(_ ruleName: String, _ bypassTokenType: Int, _ label: String?) {


        self.ruleName = ruleName
        self.bypassTokenType = bypassTokenType
        self.label = label
    }

    /**
     * Gets the name of the rule associated with this rule tag.
     *
     * @return The name of the parser rule associated with this rule tag.
     */

    public final func getRuleName() -> String {
        return ruleName
    }

    /**
     * Gets the label associated with the rule tag.
     *
     * @return The name of the label associated with the rule tag, or
     * {@code null} if this is an unlabeled rule tag.
     */

    public final func getLabel() -> String? {
        return label
    }

    /**
     * {@inheritDoc}
     *
     * <p>Rule tag tokens are always placed on the {@link #DEFAULT_CHANNEL}.</p>
     */

    public func getChannel() -> Int {
        return RuleTagToken.DEFAULT_CHANNEL
    }

    /**
     * {@inheritDoc}
     *
     * <p>This method returns the rule tag formatted with {@code <} and {@code >}
     * delimiters.</p>
     */

    public func getText() -> String? {
        if label != nil {
            return "<" + label! + ":" + ruleName + ">"
        }

        return "<" + ruleName + ">"
    }

    /**
     * {@inheritDoc}
     *
     * <p>Rule tag tokens have types assigned according to the rule bypass
     * transitions created during ATN deserialization.</p>
     */

    public func getType() -> Int {
        return bypassTokenType
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns 0.</p>
     */

    public func getLine() -> Int {
        return 0
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns -1.</p>
     */

    public func getCharPositionInLine() -> Int {
        return -1
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns -1.</p>
     */

    public func getTokenIndex() -> Int {
        return -1
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns -1.</p>
     */

    public func getStartIndex() -> Int {
        return -1
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns -1.</p>
     */

    public func getStopIndex() -> Int {
        return -1
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns {@code null}.</p>
     */

    public func getTokenSource() -> TokenSource? {
        return nil
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} always returns {@code null}.</p>
     */

    public func getInputStream() -> CharStream? {
        return nil
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} returns a string of the form
     * {@code ruleName:bypassTokenType}.</p>
     */


    public var description: String {
        return ruleName + ":" + String(bypassTokenType)
    }


}

