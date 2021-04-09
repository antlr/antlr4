/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Implements the `less` lexer action by calling _org.antlr.v4.runtime.Lexer#less_.
/// 
/// The `less` command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by _#INSTANCE_.
/// 
/// -  David Kleszyk
/// -  4.9.3
/// 

public final class LexerLessAction: LexerAction, CustomStringConvertible {
    /// 
    /// Provides a singleton instance of this parameterless lexer action.
    /// 
    public static let INSTANCE: LexerLessAction = LexerLessAction()

    /// 
    /// Constructs the singleton instance of the lexer `less` command.
    /// 
    private override init() {
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#LESS_.
    /// 
    override
    public func getActionType() -> LexerActionType {
        return LexerActionType.less
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
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#less_.
    /// 
    override
    public func execute(_ lexer: Lexer) {
        lexer.less()
    }


    public override func hash(into hasher: inout Hasher) {
        hasher.combine(ObjectIdentifier(self))
    }

    public var description: String {
        return "less"
    }
}

public func ==(lhs: LexerLessAction, rhs: LexerLessAction) -> Bool {
    return lhs === rhs
}
