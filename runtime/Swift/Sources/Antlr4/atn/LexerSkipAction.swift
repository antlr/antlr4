/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Implements the `skip` lexer action by calling _org.antlr.v4.runtime.Lexer#skip_.
/// 
/// The `skip` command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by _#INSTANCE_.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerSkipAction: LexerAction, CustomStringConvertible {
    /// 
    /// Provides a singleton instance of this parameterless lexer action.
    /// 
    public static let INSTANCE: LexerSkipAction = LexerSkipAction()

    /// 
    /// Constructs the singleton instance of the lexer `skip` command.
    /// 
    private override init() {
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#SKIP_.
    /// 
    override
    public func getActionType() -> LexerActionType {
        return LexerActionType.skip
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
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#skip_.
    /// 
    override
    public func execute(_ lexer: Lexer) {
        lexer.skip()
    }


    override
    public var hashValue: Int {
        var hash = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        return MurmurHash.finish(hash, 1)
    }
    public var description: String {
        return "skip"
    }

}

public func ==(lhs: LexerSkipAction, rhs: LexerSkipAction) -> Bool {

    return lhs === rhs
}
