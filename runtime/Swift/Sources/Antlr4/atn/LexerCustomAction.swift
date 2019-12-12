/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Executes a custom lexer action by calling _org.antlr.v4.runtime.Recognizer#action_ with the
/// rule and action indexes assigned to the custom action. The implementation of
/// a custom action is added to the generated code for the lexer in an override
/// of _org.antlr.v4.runtime.Recognizer#action_ when the grammar is compiled.
/// 
/// This class may represent embedded actions created with the {...}
/// syntax in ANTLR 4, as well as actions created for lexer commands where the
/// command argument could not be evaluated when the grammar was compiled.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerCustomAction: LexerAction {
    fileprivate let ruleIndex: Int
    fileprivate let actionIndex: Int

    /// 
    /// Constructs a custom lexer action with the specified rule and action
    /// indexes.
    /// 
    /// - parameter ruleIndex: The rule index to use for calls to
    /// _org.antlr.v4.runtime.Recognizer#action_.
    /// - parameter actionIndex: The action index to use for calls to
    /// _org.antlr.v4.runtime.Recognizer#action_.
    /// 
    public init(_ ruleIndex: Int, _ actionIndex: Int) {
        self.ruleIndex = ruleIndex
        self.actionIndex = actionIndex
    }

    /// 
    /// Gets the rule index to use for calls to _org.antlr.v4.runtime.Recognizer#action_.
    /// 
    /// - returns: The rule index for the custom action.
    /// 
    public func getRuleIndex() -> Int {
        return ruleIndex
    }

    /// 
    /// Gets the action index to use for calls to _org.antlr.v4.runtime.Recognizer#action_.
    /// 
    /// - returns: The action index for the custom action.
    /// 
    public func getActionIndex() -> Int {
        return actionIndex
    }

    /// 
    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#CUSTOM_.
    /// 

    public override func getActionType() -> LexerActionType {
        return LexerActionType.custom
    }

    /// 
    /// Gets whether the lexer action is position-dependent. Position-dependent
    /// actions may have different semantics depending on the _org.antlr.v4.runtime.CharStream_
    /// index at the time the action is executed.
    /// 
    /// Custom actions are position-dependent since they may represent a
    /// user-defined embedded action which makes calls to methods like
    /// _org.antlr.v4.runtime.Lexer#getText_.
    /// 
    /// - returns: This method returns `true`.
    /// 
    override
    public func isPositionDependent() -> Bool {
        return true
    }

    /// 
    /// 
    /// 
    /// Custom actions are implemented by calling _org.antlr.v4.runtime.Lexer#action_ with the
    /// appropriate rule and action indexes.
    /// 
    override
    public func execute(_ lexer: Lexer) throws {
        try lexer.action(nil, ruleIndex, actionIndex)
    }

    public override func hash(into hasher: inout Hasher) {
        hasher.combine(ruleIndex)
        hasher.combine(actionIndex)
    }
}

public func ==(lhs: LexerCustomAction, rhs: LexerCustomAction) -> Bool {
    if lhs === rhs {
        return true
    }

    return lhs.ruleIndex == rhs.ruleIndex
            && lhs.actionIndex == rhs.actionIndex
}
