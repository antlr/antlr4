/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.TokenFactory;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.TokenSource;
import std.typecons;
import std.variant;

alias TokenFactorySourcePair = Tuple!(TokenSource, "a", CharStream, "b");


/**
 * The default mechanism for creating tokens. It's used by default in Lexer and
 * the error handling strategy (to create missing tokens).  Notifying the parser
 * of a new factory means that it notifies it's token source and error strategy.
 */
interface TokenFactory(Symbol)
{

    /**
     * This is the method used to create tokens in the lexer and in the
     * error handling strategy. If text!=null, than the start and stop positions
     * are wiped to -1 in the text override is set in the CommonToken.
     */
    public Symbol create(TokenFactorySourcePair source, int type, Variant text, int channel,
        size_t start, size_t stop, int line, int charPositionInLine);

    /**
     * Generically useful
     */
    public Symbol create(int type, Variant text);

}
