/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.LexerNoViableAltException;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.LexerATNSimulator;
import antlr.v4.runtime.misc.Interval;
import antlr.v4.runtime.misc.Utils;
import std.format;

/**
 * TODO add class description
 */
class LexerNoViableAltException : RecognitionException
{

    /**
     * Matching attempted at what input index?
     */
    private int startIndex;

    /**
     * Which configurations did we try at input.index() that couldn't match input.LA(1)?
     */
    private ATNConfigSet deadEndConfigs;

    public this(Lexer lexer, CharStream input, int startIndex, ATNConfigSet deadEndConfigs)
    {
        super(lexer, input, null);
        this.startIndex = startIndex;
        this.deadEndConfigs = deadEndConfigs;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public ATNConfigSet getDeadEndConfigs()
    {
        return deadEndConfigs;
    }

    /**
     * @uml
     * @override
     */
    public override CharStream getInputStream()
    {
        return cast(CharStream)super.getInputStream;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
	string symbol = "";
        if (startIndex >= 0 && startIndex < getInputStream().size) {
            symbol = getInputStream().getText(Interval.of(startIndex,startIndex));
            symbol = Utils.escapeWhitespace(symbol, false);
        }

        return format("%s('%s')", "LexerNoViableAltException", symbol);
    }

}
