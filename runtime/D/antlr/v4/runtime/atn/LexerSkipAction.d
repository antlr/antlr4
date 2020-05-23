/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.LexerSkipAction;

import antlr.v4.runtime.InterfaceLexer;
import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.misc;

/**
 * Implements the {@code skip} lexer action by calling {@link Lexer#skip}.
 *
 * <p>The {@code skip} command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */
class LexerSkipAction : LexerAction
{

    /**
     * The single instance of LexerSkipAction.
     */
    private static __gshared LexerSkipAction instance_;

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@link LexerActionType#SKIP}.
     * @safe
     * @nothrow
     */
    public LexerActionType getActionType() @safe nothrow
    {
        return LexerActionType.SKIP;
    }

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@code false}.
     */
    public bool isPositionDependent()
    {
        return false;
    }

    /**
     * @uml
     * {@inheritDoc}
     *
     * <p>This action is implemented by calling {@link Lexer#skip}.</p>
     */
    public void execute(InterfaceLexer lexer)
    {
        lexer.skip();
    }

    /**
     * @uml
     * @override
     */
    public override size_t toHash()
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
        return "skip";
    }

    /**
     * Creates the single instance of LexerSkipAction.
     */
    private shared static this()
    {
        instance_ = new LexerSkipAction;
    }

    /**
     * Returns: A single instance of LexerSkipAction.
     */
    public static LexerSkipAction instance()
    {
        return instance_;
    }

}
