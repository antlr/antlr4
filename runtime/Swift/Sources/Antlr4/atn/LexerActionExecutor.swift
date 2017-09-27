/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 





/// 
/// Represents an executor for a sequence of lexer actions which traversed during
/// the matching operation of a lexer rule (token).
/// 
/// The executor tracks position information for position-dependent lexer actions
/// efficiently, ensuring that actions appearing only at the end of the rule do
/// not cause bloating of the _org.antlr.v4.runtime.dfa.DFA_ created for the lexer.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public class LexerActionExecutor: Hashable {

    fileprivate final var lexerActions: [LexerAction]
    /// 
    /// Caches the result of _#hashCode_ since the hash code is an element
    /// of the performance-critical _org.antlr.v4.runtime.atn.LexerATNConfig#hashCode_ operation.
    /// 
    fileprivate final var hashCode: Int

    /// 
    /// Constructs an executor for a sequence of _org.antlr.v4.runtime.atn.LexerAction_ actions.
    /// - parameter lexerActions: The lexer actions to execute.
    /// 
    public init(_ lexerActions: [LexerAction]) {
        self.lexerActions = lexerActions

        var hash = MurmurHash.initialize()
        for lexerAction: LexerAction in lexerActions {
            hash = MurmurHash.update(hash, lexerAction)
        }

        self.hashCode = MurmurHash.finish(hash, lexerActions.count)
    }

    /// 
    /// Creates a _org.antlr.v4.runtime.atn.LexerActionExecutor_ which executes the actions for
    /// the input `lexerActionExecutor` followed by a specified
    /// `lexerAction`.
    /// 
    /// - parameter lexerActionExecutor: The executor for actions already traversed by
    /// the lexer while matching a token within a particular
    /// _org.antlr.v4.runtime.atn.LexerATNConfig_. If this is `null`, the method behaves as
    /// though it were an empty executor.
    /// - parameter lexerAction: The lexer action to execute after the actions
    /// specified in `lexerActionExecutor`.
    /// 
    /// - returns: A _org.antlr.v4.runtime.atn.LexerActionExecutor_ for executing the combine actions
    /// of `lexerActionExecutor` and `lexerAction`.
    /// 
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

    /// 
    /// Creates a _org.antlr.v4.runtime.atn.LexerActionExecutor_ which encodes the current offset
    /// for position-dependent lexer actions.
    /// 
    /// Normally, when the executor encounters lexer actions where
    /// _org.antlr.v4.runtime.atn.LexerAction#isPositionDependent_ returns `true`, it calls
    /// _org.antlr.v4.runtime.IntStream#seek_ on the input _org.antlr.v4.runtime.CharStream_ to set the input
    /// position to the __end__ of the current token. This behavior provides
    /// for efficient DFA representation of lexer actions which appear at the end
    /// of a lexer rule, even when the lexer rule matches a variable number of
    /// characters.
    /// 
    /// Prior to traversing a match transition in the ATN, the current offset
    /// from the token start index is assigned to all position-dependent lexer
    /// actions which have not already been assigned a fixed offset. By storing
    /// the offsets relative to the token start index, the DFA representation of
    /// lexer actions which appear in the middle of tokens remains efficient due
    /// to sharing among tokens of the same length, regardless of their absolute
    /// position in the input stream.
    /// 
    /// If the current executor already has offsets assigned to all
    /// position-dependent lexer actions, the method returns `this`.
    /// 
    /// - parameter offset: The current offset to assign to all position-dependent
    /// lexer actions which do not already have offsets assigned.
    /// 
    /// - returns: A _org.antlr.v4.runtime.atn.LexerActionExecutor_ which stores input stream offsets
    /// for all position-dependent lexer actions.
    /// 
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

    /// 
    /// Gets the lexer actions to be executed by this executor.
    /// - returns: The lexer actions to be executed by this executor.
    /// 
    public func getLexerActions() -> [LexerAction] {
        return lexerActions
    }

    /// 
    /// Execute the actions encapsulated by this executor within the context of a
    /// particular _org.antlr.v4.runtime.Lexer_.
    /// 
    /// This method calls _org.antlr.v4.runtime.IntStream#seek_ to set the position of the
    /// `input` _org.antlr.v4.runtime.CharStream_ prior to calling
    /// _org.antlr.v4.runtime.atn.LexerAction#execute_ on a position-dependent action. Before the
    /// method returns, the input position will be restored to the same position
    /// it was in when the method was invoked.
    /// 
    /// - parameter lexer: The lexer instance.
    /// - parameter input: The input stream which is the source for the current token.
    /// When this method is called, the current _org.antlr.v4.runtime.IntStream#index_ for
    /// `input` should be the start of the following token, i.e. 1
    /// character past the end of the current token.
    /// - parameter startIndex: The token start index. This value may be passed to
    /// _org.antlr.v4.runtime.IntStream#seek_ to set the `input` position to the beginning
    /// of the token.
    /// 
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
