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
 * Implements the {@code type} lexer action by calling {@link org.antlr.v4.runtime.Lexer#setType}
 * with the assigned type.
 *
 * @author Sam Harwell
 * @since 4.2
 */

public class LexerTypeAction: LexerAction, CustomStringConvertible {
    fileprivate final var type: Int

    /**
     * Constructs a new {@code type} action with the specified token type value.
     * @param type The type to assign to the token using {@link org.antlr.v4.runtime.Lexer#setType}.
     */
    public init(_ type: Int) {
        self.type = type
    }

    /**
     * Gets the type to assign to a token created by the lexer.
     * @return The type to assign to a token created by the lexer.
     */
    public func getType() -> Int {
        return type
    }

    /**
     * {@inheritDoc}
     * @return This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#TYPE}.
     */

    public override func getActionType() -> LexerActionType {
        return LexerActionType.type
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
     * <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#setType} with the
     * value provided by {@link #getType}.</p>
     */

    public override func execute(_ lexer: Lexer) {
        lexer.setType(type)
    }


    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, type)
        return MurmurHash.finish(hash, 2)
    }
    public var description: String {
        return "type(\(type))"
    }

}

public func ==(lhs: LexerTypeAction, rhs: LexerTypeAction) -> Bool {

    if lhs === rhs {
        return true
    }

    return lhs.type == rhs.type
}
