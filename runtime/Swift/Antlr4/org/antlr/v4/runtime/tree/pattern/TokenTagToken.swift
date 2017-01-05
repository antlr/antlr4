/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 * A {@link org.antlr.v4.runtime.Token} object representing a token of a particular type; e.g.,
 * {@code <ID>}. These tokens are created for {@link org.antlr.v4.runtime.tree.pattern.TagChunk} chunks where the
 * tag corresponds to a lexer rule or token type.
 */

public class TokenTagToken: CommonToken {
    /**
     * This is the backing field for {@link #getTokenName}.
     */

    private let tokenName: String
    /**
     * This is the backing field for {@link #getLabel}.
     */

    private let label: String?

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken} for an unlabeled tag
     * with the specified token name and type.
     *
     * @param tokenName The token name.
     * @param type The token type.
     */
    public convenience init(_ tokenName: String, _ type: Int) {
        self.init(tokenName, type, nil)
    }

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken} with the specified
     * token name, type, and label.
     *
     * @param tokenName The token name.
     * @param type The token type.
     * @param label The label associated with the token tag, or {@code null} if
     * the token tag is unlabeled.
     */
    public init(_ tokenName: String, _ type: Int, _ label: String?) {

        self.tokenName = tokenName
        self.label = label
        super.init(type)
    }

    /**
     * Gets the token name.
     * @return The token name.
     */

    public final func getTokenName() -> String {
        return tokenName
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
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken} returns the token tag
     * formatted with {@code <} and {@code >} delimiters.</p>
     */
    override
    public func getText() -> String {
        if label != nil {
            return "<" + label! + ":" + tokenName + ">"
        }

        return "<" + tokenName + ">"
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken} returns a string of the form
     * {@code tokenName:type}.</p>
     */

    override
    public var description: String {
        return tokenName + ":" + String(type)
    }
}
