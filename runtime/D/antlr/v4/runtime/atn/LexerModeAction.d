/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

module antlr.v4.runtime.atn.LexerModeAction;

import std.format;
import antlr.v4.runtime.InterfaceLexer;
import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.misc.MurmurHash;
import antlr.v4.runtime.misc.Utils;

/**
 * @uml
 * Implements the {@code mode} lexer action by calling {@link Lexer#mode} with
 * the assigned mode.
 */
class LexerModeAction : LexerAction
{

    public int mode;

    public this(int mode)
    {
        this.mode = mode;
    }

    /**
     * @uml
     * Get the lexer mode this action should transition the lexer to.
     *
     *  @return The lexer mode for this {@code mode} command.
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@link LexerActionType#MODE}.
     * @safe
     * @nothrow
     */
    public LexerActionType getActionType() @safe nothrow
    {
        return LexerActionType.MODE;
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
     *  <p>This action is implemented by calling {@link Lexer#mode} with the
     *  value provided by {@link #getMode}.</p>
     */
    public void execute(InterfaceLexer lexer)
    {
        lexer.mode(mode);
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
        hash = MurmurHash.update(hash, mode);
        return MurmurHash.finish(hash, 2);
    }

    public bool equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        else if (obj.classinfo != LexerModeAction.classinfo) {
            return false;
        }
        return mode == (cast(LexerModeAction)obj).mode;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return format("mode(%d)", mode);
    }

}
