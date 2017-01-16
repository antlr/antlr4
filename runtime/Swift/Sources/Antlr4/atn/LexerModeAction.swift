/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// Implements the {@code mode} lexer action by calling {@link org.antlr.v4.runtime.Lexer#mode} with
/// the assigned mode.
/// 
/// -  Sam Harwell
/// -  4.2

public final class LexerModeAction: LexerAction, CustomStringConvertible {
    fileprivate final var mode: Int

    /// Constructs a new {@code mode} action with the specified mode value.
    /// - parameter mode: The mode value to pass to {@link org.antlr.v4.runtime.Lexer#mode}.
    public init(_ mode: Int) {
        self.mode = mode
    }

    /// Get the lexer mode this action should transition the lexer to.
    /// 
    /// - returns: The lexer mode for this {@code mode} command.
    public func getMode() -> Int {
        return mode
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#MODE}.

    public override func getActionType() -> LexerActionType {
        return LexerActionType.mode
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@code false}.

    public override func isPositionDependent() -> Bool {
        return false
    }

    /// {@inheritDoc}
    /// 
    /// <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#mode} with the
    /// value provided by {@link #getMode}.</p>
    override
    public func execute(_ lexer: Lexer) {
        lexer.mode(mode)
    }
    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, mode)
        return MurmurHash.finish(hash, 2)
    }
    public var description: String {
        return "mode(\(mode))"
    }
}

public func ==(lhs: LexerModeAction, rhs: LexerModeAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.mode == rhs.mode

}
