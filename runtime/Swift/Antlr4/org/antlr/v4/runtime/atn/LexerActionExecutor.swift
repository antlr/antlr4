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
 * Represents an executor for a sequence of lexer actions which traversed during
 * the matching operation of a lexer rule (token).
 *
 * <p>The executor tracks position information for position-dependent lexer actions
 * efficiently, ensuring that actions appearing only at the end of the rule do
 * not cause bloating of the {@link org.antlr.v4.runtime.dfa.DFA} created for the lexer.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */

public class LexerActionExecutor: Hashable {

    fileprivate final var lexerActions: [LexerAction]
    /**
     * Caches the result of {@link #hashCode} since the hash code is an element
     * of the performance-critical {@link org.antlr.v4.runtime.atn.LexerATNConfig#hashCode} operation.
     */
    fileprivate final var hashCode: Int

    /**
     * Constructs an executor for a sequence of {@link org.antlr.v4.runtime.atn.LexerAction} actions.
     * @param lexerActions The lexer actions to execute.
     */
    public init(_ lexerActions: [LexerAction]) {
        self.lexerActions = lexerActions

        var hash: Int = MurmurHash.initialize()
        for lexerAction: LexerAction in lexerActions {
            hash = MurmurHash.update(hash, lexerAction)
        }

        self.hashCode = MurmurHash.finish(hash, lexerActions.count)
    }

    /**
     * Creates a {@link org.antlr.v4.runtime.atn.LexerActionExecutor} which executes the actions for
     * the input {@code lexerActionExecutor} followed by a specified
     * {@code lexerAction}.
     *
     * @param lexerActionExecutor The executor for actions already traversed by
     * the lexer while matching a token within a particular
     * {@link org.antlr.v4.runtime.atn.LexerATNConfig}. If this is {@code null}, the method behaves as
     * though it were an empty executor.
     * @param lexerAction The lexer action to execute after the actions
     * specified in {@code lexerActionExecutor}.
     *
     * @return A {@link org.antlr.v4.runtime.atn.LexerActionExecutor} for executing the combine actions
     * of {@code lexerActionExecutor} and {@code lexerAction}.
     */
    public static func append(_ lexerActionExecutor: LexerActionExecutor?, _ lexerAction: LexerAction) -> LexerActionExecutor {
        if lexerActionExecutor == nil {
            return LexerActionExecutor([lexerAction])
        }

        //var lexerActions : [LexerAction] = lexerActionExecutor.lexerActions, //lexerActionExecutor.lexerActions.length + 1);
        var lexerActions: [LexerAction] = lexerActionExecutor!.lexerActions
        lexerActions.append(lexerAction)
        //lexerActions[lexerActions.length - 1] = lexerAction;
        return LexerActionExecutor(lexerActions)
    }

    /**
     * Creates a {@link org.antlr.v4.runtime.atn.LexerActionExecutor} which encodes the current offset
     * for position-dependent lexer actions.
     *
     * <p>Normally, when the executor encounters lexer actions where
     * {@link org.antlr.v4.runtime.atn.LexerAction#isPositionDependent} returns {@code true}, it calls
     * {@link org.antlr.v4.runtime.IntStream#seek} on the input {@link org.antlr.v4.runtime.CharStream} to set the input
     * position to the <em>end</em> of the current token. This behavior provides
     * for efficient DFA representation of lexer actions which appear at the end
     * of a lexer rule, even when the lexer rule matches a variable number of
     * characters.</p>
     *
     * <p>Prior to traversing a match transition in the ATN, the current offset
     * from the token start index is assigned to all position-dependent lexer
     * actions which have not already been assigned a fixed offset. By storing
     * the offsets relative to the token start index, the DFA representation of
     * lexer actions which appear in the middle of tokens remains efficient due
     * to sharing among tokens of the same length, regardless of their absolute
     * position in the input stream.</p>
     *
     * <p>If the current executor already has offsets assigned to all
     * position-dependent lexer actions, the method returns {@code this}.</p>
     *
     * @param offset The current offset to assign to all position-dependent
     * lexer actions which do not already have offsets assigned.
     *
     * @return A {@link org.antlr.v4.runtime.atn.LexerActionExecutor} which stores input stream offsets
     * for all position-dependent lexer actions.
     */
    public func fixOffsetBeforeMatch(_ offset: Int) -> LexerActionExecutor {
        var updatedLexerActions: [LexerAction]? = nil
        let length = lexerActions.count
        for i in 0..<length {
            if lexerActions[i].isPositionDependent() && !(lexerActions[i] is LexerIndexedCustomAction) {
                if updatedLexerActions == nil {
                    updatedLexerActions = lexerActions   //lexerActions.clone();
                }

                updatedLexerActions![i] = LexerIndexedCustomAction(offset, lexerActions[i])
            }
        }

        if updatedLexerActions == nil {
            return self
        }

        return LexerActionExecutor(updatedLexerActions!)
    }

    /**
     * Gets the lexer actions to be executed by this executor.
     * @return The lexer actions to be executed by this executor.
     */
    public func getLexerActions() -> [LexerAction] {
        return lexerActions
    }

    /**
     * Execute the actions encapsulated by this executor within the context of a
     * particular {@link org.antlr.v4.runtime.Lexer}.
     *
     * <p>This method calls {@link org.antlr.v4.runtime.IntStream#seek} to set the position of the
     * {@code input} {@link org.antlr.v4.runtime.CharStream} prior to calling
     * {@link org.antlr.v4.runtime.atn.LexerAction#execute} on a position-dependent action. Before the
     * method returns, the input position will be restored to the same position
     * it was in when the method was invoked.</p>
     *
     * @param lexer The lexer instance.
     * @param input The input stream which is the source for the current token.
     * When this method is called, the current {@link org.antlr.v4.runtime.IntStream#index} for
     * {@code input} should be the start of the following token, i.e. 1
     * character past the end of the current token.
     * @param startIndex The token start index. This value may be passed to
     * {@link org.antlr.v4.runtime.IntStream#seek} to set the {@code input} position to the beginning
     * of the token.
     */
    public func execute(_ lexer: Lexer, _ input: CharStream, _ startIndex: Int) throws {
        var requiresSeek: Bool = false
        var stopIndex: Int = input.index()
        defer {
            if requiresSeek {
                try! input.seek(stopIndex)
            }
        }
        //try {
        for var lexerAction: LexerAction in self.lexerActions {
            if let runLexerAction = lexerAction as? LexerIndexedCustomAction {
                let offset: Int = runLexerAction.getOffset()
                try input.seek(startIndex + offset)
                lexerAction = runLexerAction.getAction()
                requiresSeek = (startIndex + offset) != stopIndex
            } else {
                if lexerAction.isPositionDependent() {
                    try input.seek(stopIndex)
                    requiresSeek = false
                }
            }

            try lexerAction.execute(lexer)
        }
        //}

    }


    public var hashValue: Int {
        return self.hashCode
    }


}

public func ==(lhs: LexerActionExecutor, rhs: LexerActionExecutor) -> Bool {
    if lhs === rhs {
        return true
    }
    if lhs.lexerActions.count != rhs.lexerActions.count {
        return false
    }
    let length = lhs.lexerActions.count
    for i in 0..<length {
        if !(lhs.lexerActions[i] == rhs.lexerActions[i]) {
            return false
        }
    }


    return lhs.hashCode == rhs.hashCode

}
