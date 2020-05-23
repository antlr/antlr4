/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.LexerIndexedCustomAction;

import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.InterfaceLexer;
import antlr.v4.runtime.misc.MurmurHash;

/**
 * This implementation of {@link LexerAction} is used for tracking input offsets
 * for position-dependent actions within a {@link LexerActionExecutor}.
 *
 * <p>This action is not serialized as part of the ATN, and is only required for
 * position-dependent lexer actions which appear at a location other than the
 * end of a rule. For more information about DFA optimizations employed for
 * lexer actions, see {@link LexerActionExecutor#append} and
 * {@link LexerActionExecutor#fixOffsetBeforeMatch}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */
class LexerIndexedCustomAction : LexerAction
{

    private size_t offset;

    private LexerAction action;

    /**
     * @uml
     * Constructs a new indexed custom action by associating a character offset
     * with a {@link LexerAction}.
     *
     * <p>Note: This class is only required for lexer actions for which
     * {@link LexerAction#isPositionDependent} returns {@code true}.</p>
     *
     *  @param offset The offset into the input {@link CharStream}, relative to
     * the token start index, at which the specified lexer action should be
     * executed.
     *  @param action The lexer action to execute at a particular offset in the
     * input {@link CharStream}.
     */
    public this(size_t offset, LexerAction action)
    {
        this.offset = offset;
        this.action = action;
    }

    /**
     * @uml
     * Gets the location in the input {@link CharStream} at which the lexer
     * action should be executed. The value is interpreted as an offset relative
     * to the token start index.
     *
     *  @return The location in the input {@link CharStream} at which the lexer
     * action should be executed.
     */
    public size_t getOffset()
    {
        return offset;
    }

    /**
     * @uml
     * Gets the lexer action to execute.
     *
     *  @return A {@link LexerAction} object which executes the lexer action.
     */
    public LexerAction getAction()
    {
        return action;
    }

    /**
     * @uml
     * {@inheritDoc}
     *
     *  @return This method returns the result of calling {@link #getActionType}
     * on the {@link LexerAction} returned by {@link #getAction}.
     */
    public LexerActionType getActionType()
    {
        return action.getActionType();
    }

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@code true}.
     */
    public bool isPositionDependent()
    {
        return true;
    }

    /**
     * @uml
     * {@inheritDoc}
     *
     * <p>This method calls {@link #execute} on the result of {@link #getAction}
     * using the provided {@code lexer}.</p>
     */
    public void execute(InterfaceLexer lexer)
    {
        // assume the input stream position was properly set by the calling code
        action.execute(lexer);
    }

    /**
     * @uml
     * @nothrow
     * @trusted
     * @override
     */
    public override size_t toHash() @trusted nothrow
    {
        size_t hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, offset);
        hash = MurmurHash.update!LexerAction(hash, action);
        return MurmurHash.finish(hash, 2);
    }

    public bool equals(Object obj)
    {
        if (obj is this) {
            return true;
        }
        else if (!cast(LexerIndexedCustomAction)obj) {
            return false;
        }
        LexerIndexedCustomAction other = cast(LexerIndexedCustomAction)obj;
        return offset == other.getOffset
            && action == other.getAction;
    }

}
