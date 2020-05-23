/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.VocabularyImpl;

import std.conv;
import std.ascii;
import std.algorithm.comparison;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;

/**
 * This class provides a default implementation of the {@link Vocabulary}
 * interface.
 */
class VocabularyImpl : Vocabulary
{

    public string[] EMPTY_NAMES;

    public string[] literalNames;

    public string[] symbolicNames;

    public string[] displayNames;

    /**
     * @uml
     * @final
     */
    private int maxTokenType;

    /**
     * Constructs a new instance of {@link VocabularyImpl} from the specified
     * literal and symbolic token names.
     *
     *  @param literalNames The literal names assigned to tokens, or {@code null}
     *  if no literal names are assigned.
     *  @param symbolicNames The symbolic names assigned to tokens, or
     *  {@code null} if no symbolic names are assigned.
     *
     *  @see #getLiteralName(int)
     *  @see #getSymbolicName(int)
     */
    public this(const string[] literalNames, const string[] symbolicNames)
    {
        this(literalNames, symbolicNames, null);
    }

    /**
     * Constructs a new instance of {@link VocabularyImpl} from the specified
     * literal, symbolic, and display token names.
     *
     *  @param literalNames The literal names assigned to tokens, or {@code null}
     * if no literal names are assigned.
     *  @param symbolicNames The symbolic names assigned to tokens, or
     *  {@code null} if no symbolic names are assigned.
     *  @param displayNames The display names assigned to tokens, or {@code null}
     * to use the values in {@code literalNames} and {@code symbolicNames} as
     * the source of display names, as described in
     *  {@link #getDisplayName(int)}.
     *
     *  @see #getLiteralName(int)
     *  @see #getSymbolicName(int)
     *  @see #getDisplayName(int)
     * @uml
     * Constructs a new instance of {@link VocabularyImpl} from the specified
     * literal, symbolic, and display token names.
     *
     *  @param literalNames The literal names assigned to tokens, or {@code null}
     * if no literal names are assigned.
     *  @param symbolicNames The symbolic names assigned to tokens, or
     *  {@code null} if no symbolic names are assigned.
     *  @param displayNames The display names assigned to tokens, or {@code null}
     * to use the values in {@code literalNames} and {@code symbolicNames} as
     * the source of display names, as described in
     *  {@link #getDisplayName(int)}.
     *
     *  @see #getLiteralName(int)
     *  @see #getSymbolicName(int)
     *  @see #getDisplayName(int)
     */
    public this(const string[] literalNames, const string[] symbolicNames, string[] displayNames)
    {
        this.literalNames = literalNames !is null ? to!(string[])(literalNames) : to!(string[])(EMPTY_NAMES);
        this.symbolicNames = symbolicNames !is null ? to!(string[])(symbolicNames) : to!(string[])(EMPTY_NAMES);
        this.displayNames = displayNames !is null ? displayNames : to!(string[])(EMPTY_NAMES);
        // See note here on -1 part: https://github.com/antlr/antlr4/pull/1146
        this.maxTokenType =
            max(to!int(this.displayNames.length),
                max(to!int(this.literalNames.length), to!int(this.symbolicNames.length))) - 1;
    }

    public static Vocabulary fromTokenNames(string[] tokenNames)
    {
        if (tokenNames is null || tokenNames.length == 0) {
            // return EMPTY_VOCABULARY;
            return new VocabularyImpl(null, null, null);
        }

        string[] literalNames = tokenNames.dup;
        string[] symbolicNames = tokenNames.dup;
        for (int i = 0; i < tokenNames.length; i++) {
            string tokenName = tokenNames[i];
            if (tokenName == null) {
                continue;
            }

            if (tokenName.length > 0) {
                char firstChar = tokenName[0];
                if (firstChar == '\'') {
                    symbolicNames[i] = null;
                    continue;
                }
                else if (isUpper(firstChar)) {
                    literalNames[i] = null;
                    continue;
                }
            }

            // wasn't a literal or symbolic name
            literalNames[i] = null;
            symbolicNames[i] = null;
        }

        return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
    }

    public string getSymbolicName(int tokenType)
    {
        if (tokenType >= 0 && tokenType < symbolicNames.length) {
            return symbolicNames[tokenType];
        }

        if (tokenType == TokenConstantDefinition.EOF) {
            return "EOF";
        }

        return null;
    }

    public int getMaxTokenType()
    {
        return maxTokenType;
    }

    public string getDisplayName(int tokenType)
    {
        if (tokenType >= 0 && tokenType < displayNames.length) {
            string displayName = displayNames[tokenType];
            if (displayName !is null) {
                return displayName;
            }
        }

        string literalName = getLiteralName(tokenType);
        if (literalName !is null) {
            return literalName;
        }

        string symbolicName = getSymbolicName(tokenType);
        if (symbolicName != null) {
            return symbolicName;
        }

        return to!string(tokenType);
    }

    public string getLiteralName(int tokenType)
    {
        if (tokenType >= 0 && tokenType < literalNames.length) {
            return literalNames[tokenType];
        }
        return null;
    }

}
