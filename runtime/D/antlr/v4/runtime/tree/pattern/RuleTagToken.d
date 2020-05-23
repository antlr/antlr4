/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.pattern.RuleTagToken;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenSource;
import std.conv;
import std.format;
import std.variant;

/**
 * @uml
 * A {@link Token} object representing an entire subtree matched by a parser
 * rule; e.g., {@code <expr>}. These tokens are created for {@link TagChunk}
 * chunks where the tag corresponds to a parser rule.
 */
class RuleTagToken : Token
{

    /**
     * @uml
     * This is the backing field for {@link #getRuleName}.
     */
    private string ruleName;

    /**
     * @uml
     * The token type for the current token. This is the token type assigned to
     * the bypass alternative for the rule during ATN deserialization.
     */
    private int bypassTokenType;

    /**
     * @uml
     * This is the backing field for {@link #getLabel}.
     */
    private string label;

    /**
     * @uml
     * Constructs a new instance of {@link RuleTagToken} with the specified rule
     * name and bypass token type and no label.
     *
     *  @param ruleName The name of the parser rule this rule tag matches.
     *  @param bypassTokenType The bypass token type assigned to the parser rule.
     *
     *  @exception IllegalArgumentException if {@code ruleName} is {@code null}
     * or empty.
     */
    public this(string ruleName, int bypassTokenType)
    {
        this(ruleName, bypassTokenType, null);
    }

    /**
     * @uml
     * Constructs a new instance of {@link RuleTagToken} with the specified rule
     *  name, bypass token type, and label.
     *
     *  @param ruleName The name of the parser rule this rule tag matches.
     *  @param bypassTokenType The bypass token type assigned to the parser rule.
     *  @param label The label associated with the rule tag, or {@code null} if
     * the rule tag is unlabeled.
     *
     *  @exception IllegalArgumentException if {@code ruleName} is {@code null}
     *  or empty.
     */
    public this(string ruleName, int bypassTokenType, string label)
    {
    if (ruleName is null || ruleName.length == 0) {
            throw new IllegalArgumentException("ruleName cannot be null or empty.");
        }
        this.ruleName = ruleName;
        this.bypassTokenType = bypassTokenType;
        this.label = label;
    }

    /**
     * @uml
     * Gets the name of the rule associated with this rule tag.
     *
     *  @return The name of the parser rule associated with this rule tag.
     */
    public string getRuleName()
    {
        return ruleName;
    }

    /**
     * @uml
     * Gets the label associated with the rule tag.
     *
     *  @return The name of the label associated with the rule tag, or
     * {@code null} if this is an unlabeled rule tag.
     */
    public string getLabel()
    {
        return label;
    }

    /**
     * @uml
     * @override
     * <p>Rule tag tokens are always placed on the {@link #DEFAULT_CHANNEL}.</p>
     */
    public override int getChannel()
    {
        return TokenConstantDefinition.DEFAULT_CHANNEL;
    }

    /**
     * @uml
     * @override
     * <p>This method returns the rule tag formatted with {@code <} and {@code >}
     * delimiters.</p>
     */
    public override Variant getText()
    {
        if (label !is null) {
            Variant r = format("<%s:%s>", label, ruleName);
            return r;
        }
        Variant r = format("<%s>", ruleName);
        return r;
    }

    /**
     * @uml
     * @override
     * <p>Rule tag tokens have types assigned according to the rule bypass
     * transitions created during ATN deserialization.</p>
     */
    public override int getType()
    {
        return bypassTokenType;
    }

    /**
     * @uml
     * @override
     */
    public override int getLine()
    {
        return 0;
    }

    /**
     * @uml
     * @override
     */
    public override int getCharPositionInLine()
    {
        return -1;
    }

    /**
     * @uml
     * @override
     */
    public override size_t getTokenIndex()
    {
        return to!size_t(-1);
    }

    /**
     * @uml
     * @override
     */
    public override size_t startIndex()
    {
        return -1;
    }

    /**
     * @uml
     * @override
     */
    public override size_t stopIndex()
    {
        return -1;
    }

    /**
     * @uml
     * @override
     */
    public override TokenSource getTokenSource()
    {
        return null;
    }

    /**
     * @uml
     * @override
     */
    public override CharStream getInputStream()
    {
        return null;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return ruleName ~ ":" ~ to!string(bypassTokenType);
    }

}
