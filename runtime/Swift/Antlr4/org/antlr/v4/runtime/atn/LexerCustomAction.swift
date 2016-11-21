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
 * Executes a custom lexer action by calling {@link org.antlr.v4.runtime.Recognizer#action} with the
 * rule and action indexes assigned to the custom action. The implementation of
 * a custom action is added to the generated code for the lexer in an override
 * of {@link org.antlr.v4.runtime.Recognizer#action} when the grammar is compiled.
 *
 * <p>This class may represent embedded actions created with the <code>{...}</code>
 * syntax in ANTLR 4, as well as actions created for lexer commands where the
 * command argument could not be evaluated when the grammar was compiled.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */

public final class LexerCustomAction: LexerAction {
    fileprivate let ruleIndex: Int
    fileprivate let actionIndex: Int

    /**
     * Constructs a custom lexer action with the specified rule and action
     * indexes.
     *
     * @param ruleIndex The rule index to use for calls to
     * {@link org.antlr.v4.runtime.Recognizer#action}.
     * @param actionIndex The action index to use for calls to
     * {@link org.antlr.v4.runtime.Recognizer#action}.
     */
    public init(_ ruleIndex: Int, _ actionIndex: Int) {
        self.ruleIndex = ruleIndex
        self.actionIndex = actionIndex
    }

    /**
     * Gets the rule index to use for calls to {@link org.antlr.v4.runtime.Recognizer#action}.
     *
     * @return The rule index for the custom action.
     */
    public func getRuleIndex() -> Int {
        return ruleIndex
    }

    /**
     * Gets the action index to use for calls to {@link org.antlr.v4.runtime.Recognizer#action}.
     *
     * @return The action index for the custom action.
     */
    public func getActionIndex() -> Int {
        return actionIndex
    }

    /**
     * {@inheritDoc}
     *
     * @return This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#CUSTOM}.
     */

    public override func getActionType() -> LexerActionType {
        return LexerActionType.custom
    }

    /**
     * Gets whether the lexer action is position-dependent. Position-dependent
     * actions may have different semantics depending on the {@link org.antlr.v4.runtime.CharStream}
     * index at the time the action is executed.
     *
     * <p>Custom actions are position-dependent since they may represent a
     * user-defined embedded action which makes calls to methods like
     * {@link org.antlr.v4.runtime.Lexer#getText}.</p>
     *
     * @return This method returns {@code true}.
     */
    override
    public func isPositionDependent() -> Bool {
        return true
    }

    /**
     * {@inheritDoc}
     *
     * <p>Custom actions are implemented by calling {@link org.antlr.v4.runtime.Lexer#action} with the
     * appropriate rule and action indexes.</p>
     */
    override
    public func execute(_ lexer: Lexer) throws {
        try lexer.action(nil, ruleIndex, actionIndex)
    }

    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, ruleIndex)
        hash = MurmurHash.update(hash, actionIndex)
        return MurmurHash.finish(hash, 3)
    }

}

public func ==(lhs: LexerCustomAction, rhs: LexerCustomAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.ruleIndex == rhs.ruleIndex
            && lhs.actionIndex == rhs.actionIndex
}
