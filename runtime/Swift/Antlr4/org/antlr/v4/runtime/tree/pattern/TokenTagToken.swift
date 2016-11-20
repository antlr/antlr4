/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * Copyright (c) 2015 Janyou
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
