/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Implements the {@code skip} lexer action by calling {@link org.antlr.v4.runtime.Lexer#skip}.
/// 
/// <p>The {@code skip} command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
/// 
/// -  Sam Harwell
/// -  4.2

public final class LexerSkipAction: LexerAction, CustomStringConvertible {
    /// Provides a singleton instance of this parameterless lexer action.
    public static let INSTANCE: LexerSkipAction = LexerSkipAction()

    /// Constructs the singleton instance of the lexer {@code skip} command.
    private override init() {
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#SKIP}.
    override
    public func getActionType() -> LexerActionType {
        return LexerActionType.skip
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@code false}.
    override
    public func isPositionDependent() -> Bool {
        return false
    }

    /// {@inheritDoc}
    /// 
    /// <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#skip}.</p>
    override
    public func execute(_ lexer: Lexer) {
        lexer.skip()
    }


    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
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
