/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Implements the `popMode` lexer action by calling _org.antlr.v4.runtime.Lexer#popMode_.
/// 
/// The `popMode` command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by _#INSTANCE_.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerPopModeAction: LexerAction, CustomStringConvertible {
    /// 
    /// Provides a singleton instance of this parameterless lexer action.
    /// 
    public static let INSTANCE: LexerPopModeAction = LexerPopModeAction()

    /// 
    /// Constructs the singleton instance of the lexer `popMode` command.
    /// 
    private override init() {
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#popMode_.
    /// 
    override
    public func getActionType() -> LexerActionType {
        LexerActionType.popMode
    }

    /// 
    /// 
    /// - returns: This method returns `false`.
    /// 

    public override func isPositionDependent() -> Bool {
        false
    }

    /// 
    /// 
    /// 
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#popMode_.
    /// 

    public override func execute(_ lexer: Lexer) throws {
        try lexer.popMode()
    }


    public override func hash(into hasher: inout Hasher) {
        hasher.combine(ObjectIdentifier(self))
    }

    public var description: String {
        "popMode"
    }
}

public func ==(lhs: LexerPopModeAction, rhs: LexerPopModeAction) -> Bool {
    lhs === rhs
}
