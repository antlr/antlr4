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
 * Implements the {@code skip} lexer action by calling {@link org.antlr.v4.runtime.Lexer#skip}.
 *
 * <p>The {@code skip} command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */

public final class LexerSkipAction: LexerAction, CustomStringConvertible {
    /**
     * Provides a singleton instance of this parameterless lexer action.
     */
    public static let INSTANCE: LexerSkipAction = LexerSkipAction()

    /**
     * Constructs the singleton instance of the lexer {@code skip} command.
     */
    private override init() {
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#SKIP}.
     */
    override
    public func getActionType() -> LexerActionType {
        return LexerActionType.skip
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@code false}.
     */
    override
    public func isPositionDependent() -> Bool {
        return false
    }

    /**
     * {@inheritDoc}
     *
     * <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#skip}.</p>
     */
    override
    public func execute(_ lexer: Lexer) {
        lexer.skip()
    }


    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        return MurmurHash.finish(hash, 1)
    }
    public var description: String {
        return "skip"
    }

}

public func ==(lhs: LexerSkipAction, rhs: LexerSkipAction) -> Bool {

    return lhs === rhs
}
