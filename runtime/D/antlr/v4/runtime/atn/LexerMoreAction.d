/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.LexerMoreAction;

import antlr.v4.runtime.InterfaceLexer;
import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.misc;

/**
 * Provides a singleton instance of this parameterless lexer action.
 */
class LexerMoreAction : LexerAction
{

    /**
     * The single instance of LexerMoreAction.
     */
    private static __gshared LexerMoreAction instance_;

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@link LexerActionType#MORE}.
     * @safe
     * @nothrow
     */
    public LexerActionType getActionType() @safe nothrow
    {
        return LexerActionType.MORE;
    }

    public bool isPositionDependent()
    {
        return false;
    }

    public void execute(InterfaceLexer lexer)
    {
        lexer.more();
    }

    /**
     * @uml
     * @safe
     * @nothrow
     * @override
     */
    public override size_t toHash() @safe nothrow
    {
	size_t hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, Utils.rank(getActionType));
        return MurmurHash.finish(hash, 1);
    }

    /**
     * @uml
     * - @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
     */
    public bool equals(Object obj)
    {
        return obj == this;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return "more";
    }

    /**
     * Creates the single instance of LexerMoreAction.
     */
    private shared static this()
    {
        instance_ = new LexerMoreAction;
    }

    /**
     * Returns: A single instance of LexerMoreAction.
     */
    public static LexerMoreAction instance()
    {
        return instance_;
    }

}
