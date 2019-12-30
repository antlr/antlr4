/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Implements the `more` lexer action by calling _org.antlr.v4.runtime.Lexer#more_.
/// 
/// The `more` command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by _#INSTANCE_.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerMoreAction: LexerAction, CustomStringConvertible {
    /// 
    /// Provides a singleton instance of this parameterless lexer action.
    /// 
    public static let INSTANCE: LexerMoreAction = LexerMoreAction()

    /// 
    /// Constructs the singleton instance of the lexer `more` command.
    /// 
    private override init() {
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#MORE_.
    /// 
    override
    public func getActionType() -> LexerActionType {
        return LexerActionType.more
    }

    /// 
    /// 
    /// - returns: This method returns `false`.
    /// 
    override
    public func isPositionDependent() -> Bool {
        return false
    }

    /// 
    /// 
    /// 
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#more_.
    /// 
    override
    public func execute(_ lexer: Lexer) {
        lexer.more()
    }


    public override func hash(into hasher: inout Hasher) {
        hasher.combine(ObjectIdentifier(self))
    }

    public var description: String {
        return "more"
    }
}

public func ==(lhs: LexerMoreAction, rhs: LexerMoreAction) -> Bool {
    return lhs === rhs
}
