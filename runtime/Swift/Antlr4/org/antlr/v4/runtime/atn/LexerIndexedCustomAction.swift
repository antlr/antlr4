/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  Copyright (c) 2015 Janyou
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



/**
 * This implementation of {@link org.antlr.v4.runtime.atn.LexerAction} is used for tracking input offsets
 * for position-dependent actions within a {@link org.antlr.v4.runtime.atn.LexerActionExecutor}.
 *
 * <p>This action is not serialized as part of the ATN, and is only required for
 * position-dependent lexer actions which appear at a location other than the
 * end of a rule. For more information about DFA optimizations employed for
 * lexer actions, see {@link org.antlr.v4.runtime.atn.LexerActionExecutor#append} and
 * {@link org.antlr.v4.runtime.atn.LexerActionExecutor#fixOffsetBeforeMatch}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */

public final class LexerIndexedCustomAction: LexerAction {
    fileprivate let offset: Int
    fileprivate let action: LexerAction

    /**
     * Constructs a new indexed custom action by associating a character offset
     * with a {@link org.antlr.v4.runtime.atn.LexerAction}.
     *
     * <p>Note: This class is only required for lexer actions for which
     * {@link org.antlr.v4.runtime.atn.LexerAction#isPositionDependent} returns {@code true}.</p>
     *
     * @param offset The offset into the input {@link org.antlr.v4.runtime.CharStream}, relative to
     * the token start index, at which the specified lexer action should be
     * executed.
     * @param action The lexer action to execute at a particular offset in the
     * input {@link org.antlr.v4.runtime.CharStream}.
     */
    public init(_ offset: Int, _ action: LexerAction) {
        self.offset = offset
        self.action = action
    }

    /**
     * Gets the location in the input {@link org.antlr.v4.runtime.CharStream} at which the lexer
     * action should be executed. The value is interpreted as an offset relative
     * to the token start index.
     *
     * @return The location in the input {@link org.antlr.v4.runtime.CharStream} at which the lexer
     * action should be executed.
     */
    public func getOffset() -> Int {
        return offset
    }

    /**
     * Gets the lexer action to execute.
     *
     * @return A {@link org.antlr.v4.runtime.atn.LexerAction} object which executes the lexer action.
     */
    public func getAction() -> LexerAction {
        return action
    }

    /**
     * {@inheritDoc}
     *
     * @return This method returns the result of calling {@link #getActionType}
     * on the {@link org.antlr.v4.runtime.atn.LexerAction} returned by {@link #getAction}.
     */

    public override func getActionType() -> LexerActionType {
        return action.getActionType()
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@code true}.
     */

    public override func isPositionDependent() -> Bool {
        return true
    }

    /**
     * {@inheritDoc}
     *
     * <p>This method calls {@link #execute} on the result of {@link #getAction}
     * using the provided {@code lexer}.</p>
     */

    public override func execute(_ lexer: Lexer) throws {
        // assume the input stream position was properly set by the calling code
        try action.execute(lexer)
    }


    public override var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, offset)
        hash = MurmurHash.update(hash, action)
        return MurmurHash.finish(hash, 2)
    }


}

public func ==(lhs: LexerIndexedCustomAction, rhs: LexerIndexedCustomAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.offset == rhs.offset
            && lhs.action == rhs.action

}
