/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.pattern.TokenTagToken;

import antlr.v4.runtime.CommonToken;
import std.conv;
import std.format;
import std.variant;

/**
 * @uml
 * {@link Token} object representing a token of a particular type; e.g.,
 * {@code <ID>}. These tokens are created for {@link TagChunk} chunks where the
 * tag corresponds to a lexer rule or token type.
 */
class TokenTagToken : CommonToken
{

    /**
     * @uml
     * This is the backing field for {@link #getTokenName}.
     * @final
     */
    private string tokenName;

    /**
     * @uml
     * This is the backing field for {@link #getLabel}.
     * @final
     */
    private string label;

    /**
     * @uml
     * Constructs a new instance of {@link TokenTagToken} for an unlabeled tag
     * with the specified token name and type.
     *
     *  @param tokenName The token name.
     *  @param type The token type.
     */
    public this(string tokenName, int type)
    {
        this(tokenName, type, null);
    }

    /**
     * @uml
     * Constructs a new instance of {@link TokenTagToken} with the specified
     *  token name, type, and label.
     *
     *  @param tokenName The token name.
     *  @param type The token type.
     *  @param label The label associated with the token tag, or {@code null} if
     *  the token tag is unlabeled.
     */
    public this(string tokenName, int type, string label)
    {
        super(type);
        this.tokenName = tokenName;
        this.label = label;
    }

    /**
     * @uml
     * Gets the token name.
     *  @return The token name.
     */
    public string getTokenName()
    {
        return tokenName;
    }

    /**
     * @uml
     * Gets the label associated with the rule tag.
     *
     *  @return The name of the label associated with the rule tag, or
     *  {@code null} if this is an unlabeled rule tag.
     */
    public string getLabel()
    {
        return label;
    }

    /**
     * @uml
     * <p>The implementation for {@link TokenTagToken} returns the token tag
     *  formatted with {@code <} and {@code >} delimiters.</p>
     * @override
     */
    public override Variant getText()
    {
        if (label !is null) {
            Variant r = format("<%s:%s>", label, tokenName);
            return r;
        }
        Variant r = format("<%s>", tokenName);
        return r;
    }

    /**
     * @uml
     * <p>The implementation for {@link TokenTagToken} returns a string of the form
     *  {@code tokenName:type}.</p>
     * @override
     */
    public override string toString()
    {
        return tokenName ~ ":" ~ to!string(type);
    }

}
