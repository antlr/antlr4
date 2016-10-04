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
 * Represents a single action which can be executed following the successful
 * match of a lexer rule. Lexer actions are used for both embedded action syntax
 * and ANTLR 4's new lexer command syntax.
 *
 * @author Sam Harwell
 * @since 4.2
 */

public class LexerAction: Hashable {
    /**
     * Gets the serialization type of the lexer action.
     *
     * @return The serialization type of the lexer action.
     */
    public func getActionType() -> LexerActionType {
        RuntimeException(" must overriden ")
        fatalError()
    }


    /**
     * Gets whether the lexer action is position-dependent. Position-dependent
     * actions may have different semantics depending on the {@link org.antlr.v4.runtime.CharStream}
     * index at the time the action is executed.
     *
     * <p>Many lexer commands, including {@code type}, {@code skip}, and
     * {@code more}, do not check the input index during their execution.
     * Actions like this are position-independent, and may be stored more
     * efficiently as part of the {@link org.antlr.v4.runtime.atn.LexerATNConfig#lexerActionExecutor}.</p>
     *
     * @return {@code true} if the lexer action semantics can be affected by the
     * position of the input {@link org.antlr.v4.runtime.CharStream} at the time it is executed;
     * otherwise, {@code false}.
     */
    public func isPositionDependent() -> Bool {
        RuntimeException(" must overriden ")
        fatalError()
    }

    /**
     * Execute the lexer action in the context of the specified {@link org.antlr.v4.runtime.Lexer}.
     *
     * <p>For position-dependent actions, the input stream must already be
     * positioned correctly prior to calling this method.</p>
     *
     * @param lexer The lexer instance.
     */
    public func execute(_ lexer: Lexer) throws {
        RuntimeException(" must overriden ")
    }

    public var hashValue: Int {
        RuntimeException(" must overriden ")
        fatalError()
    }

}

public func ==(lhs: LexerAction, rhs: LexerAction) -> Bool {

    if lhs === rhs {
        return true
    }

    if (lhs is LexerChannelAction) && (rhs is LexerChannelAction) {
        return (lhs as! LexerChannelAction) == (rhs as! LexerChannelAction)
    } else if (lhs is LexerCustomAction) && (rhs is LexerCustomAction) {
        return (lhs as! LexerCustomAction) == (rhs as! LexerCustomAction)
    } else if (lhs is LexerIndexedCustomAction) && (rhs is LexerIndexedCustomAction) {
        return (lhs as! LexerIndexedCustomAction) == (rhs as! LexerIndexedCustomAction)
    } else if (lhs is LexerModeAction) && (rhs is LexerModeAction) {
        return (lhs as! LexerModeAction) == (rhs as! LexerModeAction)
    } else if (lhs is LexerMoreAction) && (rhs is LexerMoreAction) {
        return (lhs as! LexerMoreAction) == (rhs as! LexerMoreAction)
    } else if (lhs is LexerPopModeAction) && (rhs is LexerPopModeAction) {
        return (lhs as! LexerPopModeAction) == (rhs as! LexerPopModeAction)
    } else if (lhs is LexerPushModeAction) && (rhs is LexerPushModeAction) {
        return (lhs as! LexerPushModeAction) == (rhs as! LexerPushModeAction)
    } else if (lhs is LexerSkipAction) && (rhs is LexerSkipAction) {
        return (lhs as! LexerSkipAction) == (rhs as! LexerSkipAction)
    } else if (lhs is LexerTypeAction) && (rhs is LexerTypeAction) {
        return (lhs as! LexerTypeAction) == (rhs as! LexerTypeAction)
    }


    return false

}
 
