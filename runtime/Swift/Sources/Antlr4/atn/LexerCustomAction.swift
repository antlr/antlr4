/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Executes a custom lexer action by calling {@link org.antlr.v4.runtime.Recognizer#action} with the
/// rule and action indexes assigned to the custom action. The implementation of
/// a custom action is added to the generated code for the lexer in an override
/// of {@link org.antlr.v4.runtime.Recognizer#action} when the grammar is compiled.
/// 
/// <p>This class may represent embedded actions created with the <code>{...}</code>
/// syntax in ANTLR 4, as well as actions created for lexer commands where the
/// command argument could not be evaluated when the grammar was compiled.</p>
/// 
/// -  Sam Harwell
/// -  4.2

public final class LexerCustomAction: LexerAction {
    fileprivate let ruleIndex: Int
    fileprivate let actionIndex: Int

    /// Constructs a custom lexer action with the specified rule and action
    /// indexes.
    /// 
    /// - parameter ruleIndex: The rule index to use for calls to
    /// {@link org.antlr.v4.runtime.Recognizer#action}.
    /// - parameter actionIndex: The action index to use for calls to
    /// {@link org.antlr.v4.runtime.Recognizer#action}.
    public init(_ ruleIndex: Int, _ actionIndex: Int) {
        self.ruleIndex = ruleIndex
        self.actionIndex = actionIndex
    }

    /// Gets the rule index to use for calls to {@link org.antlr.v4.runtime.Recognizer#action}.
    /// 
    /// - returns: The rule index for the custom action.
    public func getRuleIndex() -> Int {
        return ruleIndex
    }

    /// Gets the action index to use for calls to {@link org.antlr.v4.runtime.Recognizer#action}.
    /// 
    /// - returns: The action index for the custom action.
    public func getActionIndex() -> Int {
        return actionIndex
    }

    /// {@inheritDoc}
    /// 
    /// - returns: This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#CUSTOM}.

    public override func getActionType() -> LexerActionType {
        return LexerActionType.custom
    }

    /// Gets whether the lexer action is position-dependent. Position-dependent
    /// actions may have different semantics depending on the {@link org.antlr.v4.runtime.CharStream}
    /// index at the time the action is executed.
    /// 
    /// <p>Custom actions are position-dependent since they may represent a
    /// user-defined embedded action which makes calls to methods like
    /// {@link org.antlr.v4.runtime.Lexer#getText}.</p>
    /// 
    /// - returns: This method returns {@code true}.
    override
    public func isPositionDependent() -> Bool {
        return true
    }

    /// {@inheritDoc}
    /// 
    /// <p>Custom actions are implemented by calling {@link org.antlr.v4.runtime.Lexer#action} with the
    /// appropriate rule and action indexes.</p>
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
