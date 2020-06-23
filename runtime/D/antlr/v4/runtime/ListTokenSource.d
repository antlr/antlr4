/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.ListTokenSource;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.CommonToken;
import antlr.v4.runtime.CommonTokenFactory;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenFactory;
import antlr.v4.runtime.TokenSource;
import std.algorithm;
import std.conv;
import std.string;
import std.typecons;
import std.variant;

alias TokenFactorySourcePair = Tuple!(TokenSource, "a", CharStream, "b");

/**
 * Provides an implementation of {@link TokenSource} as a wrapper around a list
 * of {@link Token} objects.
 *
 * <p>If the final token in the list is an {@link Token#EOF} token, it will be used
 * as the EOF token for every call to {@link #nextToken} after the end of the
 * list is reached. Otherwise, an EOF token will be created.</p>
 */
class ListTokenSource : TokenSource
{

    /**
     * The wrapped collection of {@link Token} objects to return.
     */
    public Token[] tokens;

    /**
     * The name of the input source. If this value is {@code null}, a call to
     * {@link #getSourceName} should return the source name used to create the
     * the next token in {@link #tokens} (or the previous token if the end of
     * the input has been reached).
     */
    private string sourceName;

    /**
     * The index into {@link #tokens} of token to return by the next call to
     * {@link #nextToken}. The end of the input is indicated by this value
     * being greater than or equal to the number of items in {@link #tokens}.
     */
    protected int i;

    /**
     * This field caches the EOF token for the token source.
     */
    protected Token eofToken;

    /**
     * This is the backing field for {@link #getTokenFactory} and
     *  {@link setTokenFactory}.
     */
    public TokenFactory!CommonToken _factory;

    /**
     * Constructs a new {@link ListTokenSource} instance from the specified
     * collection of {@link Token} objects.
     *
     *  @param tokens The collection of {@link Token} objects to provide as a
     *  {@link TokenSource}.
     *  @exception NullPointerException if {@code tokens} is {@code null}
     */
    public this(Token[] tokens)
    {
        this(tokens, null);
    }

    /**
     * Constructs a new {@link ListTokenSource} instance from the specified
     * collection of {@link Token} objects and source name.
     *
     *  @param tokens The collection of {@link Token} objects to provide as a
     * {@link TokenSource}.
     *  @param sourceName The name of the {@link TokenSource}. If this value is
     * {@code null}, {@link #getSourceName} will attempt to infer the name from
     * the next {@link Token} (or the previous token if the end of the input has
     * been reached).
     *
     *  @exception NullPointerException if {@code tokens} is {@code null}
     */
    public this(Token[] tokens, string sourceName)
    in
    {
        assert(tokens !is null, "tokens cannot be null");
    }
    do
    {
        _factory = CommonTokenFactory.DEFAULT;
        this.tokens = tokens;
        this.sourceName = sourceName;
    }

    public int getCharPositionInLine()
    {
    if (i < tokens.length) {
            return tokens[i].getCharPositionInLine();
        }
        else if (eofToken !is null) {
            return eofToken.getCharPositionInLine();
        }
        else if (tokens.length > 0) {
            // have to calculate the result from the line/column of the previous
            // token, along with the text of the token.
            Token lastToken = tokens[$ - 1];
            auto tokenText = lastToken.getText();
                        Variant Null;
            if (tokenText !is Null) {
                            auto lastNewLine = (to!string(tokenText)).lastIndexOf('\n');
                if (lastNewLine >= 0) {
                                    return to!int((tokenText.length) - lastNewLine - 1);
                }
            }

            return to!int(lastToken.getCharPositionInLine + lastToken.stopIndex - lastToken.startIndex) + 1;
        }

        // only reach this if tokens is empty, meaning EOF occurs at the first
        // position in the input
        return 0;
    }

    public Token nextToken()
    {
    if (i >= tokens.length) {
            if (eofToken is null) {
                size_t start = to!size_t(-1);
                if (tokens.length > 0) {
                    auto previousStop = tokens[$ - 1].stopIndex;
                    if (previousStop != -1) {
                        start = previousStop + 1;
                    }
                }

                auto stop = max(-1, start - 1);
                TokenFactorySourcePair tfsp = tuple(this, getInputStream());
                Variant v = "EOF";
                eofToken = _factory.create(tfsp, TokenConstantDefinition.EOF, v, TokenConstantDefinition.DEFAULT_CHANNEL, start, stop, getLine(), getCharPositionInLine());
            }
            return eofToken;
        }

        Token t = tokens[i];
        if (i == tokens.length - 1 && t.getType() == TokenConstantDefinition.EOF) {
            eofToken = t;
        }

        i++;
        return t;
    }

    public int getLine()
    {
    if (i < tokens.length) {
            return tokens[i].getLine();
        }
        else if (eofToken !is null) {
            return eofToken.getLine();
        }
        else if (tokens.length > 0) {
            // have to calculate the result from the line/column of the previous
            // token, along with the text of the token.
            Token lastToken = tokens[tokens.length - 1];
            int line = lastToken.getLine();

            string tokenText = to!string(lastToken.getText);
            if (tokenText !is null) {
                foreach (c; tokenText) {
                    if (c == '\n') {
                        line++;
                    }
                }
            }

            // if no text is available, assume the token did not contain any newline characters.
            return line;
        }

        // only reach this if tokens is empty, meaning EOF occurs at the first
        // position in the input
        return 1;
    }

    public CharStream getInputStream()
    {
    if (i < tokens.length) {
            return tokens[i].getInputStream();
        }
        else if (eofToken !is null) {
            return eofToken.getInputStream();
        }
        else if (tokens.length > 0) {
            return tokens[tokens.length - 1].getInputStream();
        }
        // no input stream information is available
        return null;
    }

    public string getSourceName()
    {
        if (sourceName !is null) {
            return sourceName;
        }

        CharStream inputStream = getInputStream();
        if (inputStream !is null) {
            return inputStream.getSourceName();
        }

        return "List";
    }

    public void tokenFactory(TokenFactory!CommonToken factory)
    {
        this._factory = factory;
    }

    public TokenFactory!CommonToken tokenFactory()
    {
        return _factory;
    }

}
