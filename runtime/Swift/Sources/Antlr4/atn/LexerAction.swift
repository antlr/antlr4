/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Represents a single action which can be executed following the successful
/// match of a lexer rule. Lexer actions are used for both embedded action syntax
/// and ANTLR 4's new lexer command syntax.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public class LexerAction: Hashable {
    /// 
    /// Gets the serialization type of the lexer action.
    /// 
    /// - returns: The serialization type of the lexer action.
    /// 
    public func getActionType() -> LexerActionType {
        fatalError(#function + " must be overridden")
    }


    /// 
    /// Gets whether the lexer action is position-dependent. Position-dependent
    /// actions may have different semantics depending on the _org.antlr.v4.runtime.CharStream_
    /// index at the time the action is executed.
    /// 
    /// Many lexer commands, including `type`, `skip`, and
    /// `more`, do not check the input index during their execution.
    /// Actions like this are position-independent, and may be stored more
    /// efficiently as part of the _org.antlr.v4.runtime.atn.LexerATNConfig#lexerActionExecutor_.
    /// 
    /// - returns: `true` if the lexer action semantics can be affected by the
    /// position of the input _org.antlr.v4.runtime.CharStream_ at the time it is executed;
    /// otherwise, `false`.
    /// 
    public func isPositionDependent() -> Bool {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Execute the lexer action in the context of the specified _org.antlr.v4.runtime.Lexer_.
    /// 
    /// For position-dependent actions, the input stream must already be
    /// positioned correctly prior to calling this method.
    /// 
    /// - parameter lexer: The lexer instance.
    /// 
    public func execute(_ lexer: Lexer) throws {
        fatalError(#function + " must be overridden")
    }

    public var hashValue: Int {
        fatalError(#function + " must be overridden")
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

