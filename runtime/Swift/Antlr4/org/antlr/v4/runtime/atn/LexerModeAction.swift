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
 * Implements the {@code mode} lexer action by calling {@link org.antlr.v4.runtime.Lexer#mode} with
 * the assigned mode.
 *
 * @author Sam Harwell
 * @since 4.2
 */

public final class LexerModeAction: LexerAction, CustomStringConvertible {
    fileprivate final var mode: Int

    /**
     * Constructs a new {@code mode} action with the specified mode value.
     * @param mode The mode value to pass to {@link org.antlr.v4.runtime.Lexer#mode}.
     */
    public init(_ mode: Int) {
        self.mode = mode
    }

    /**
     * Get the lexer mode this action should transition the lexer to.
     *
     * @return The lexer mode for this {@code mode} command.
     */
    public func getMode() -> Int {
        return mode
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#MODE}.
     */

    public override func getActionType() -> LexerActionType {
        return LexerActionType.mode
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@code false}.
     */

    public override func isPositionDependent() -> Bool {
        return false
    }

    /**
     * {@inheritDoc}
     *
     * <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#mode} with the
     * value provided by {@link #getMode}.</p>
     */
    override
    public func execute(_ lexer: Lexer) {
        lexer.mode(mode)
    }
    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, mode)
        return MurmurHash.finish(hash, 2)
    }
    public var description: String {
        return "mode(\(mode))"
    }
}

public func ==(lhs: LexerModeAction, rhs: LexerModeAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.mode == rhs.mode

}
