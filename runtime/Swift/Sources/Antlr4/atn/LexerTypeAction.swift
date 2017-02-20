/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Implements the {@code type} lexer action by calling {@link org.antlr.v4.runtime.Lexer#setType}
/// with the assigned type.
/// 
/// -  Sam Harwell
/// -  4.2

public class LexerTypeAction: LexerAction, CustomStringConvertible {
    fileprivate final var type: Int

    /// Constructs a new {@code type} action with the specified token type value.
    /// - parameter type: The type to assign to the token using {@link org.antlr.v4.runtime.Lexer#setType}.
    public init(_ type: Int) {
        self.type = type
    }

    /// Gets the type to assign to a token created by the lexer.
    /// - returns: The type to assign to a token created by the lexer.
    public func getType() -> Int {
        return type
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#TYPE}.

    public override func getActionType() -> LexerActionType {
        return LexerActionType.type
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@code false}.
    override
    public func isPositionDependent() -> Bool {
        return false
    }

    /// {@inheritDoc}
    /// 
    /// <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#setType} with the
    /// value provided by {@link #getType}.</p>

    public override func execute(_ lexer: Lexer) {
        lexer.setType(type)
    }


    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, type)
        return MurmurHash.finish(hash, 2)
    }
    public var description: String {
        return "type(\(type))"
    }

}

public func ==(lhs: LexerTypeAction, rhs: LexerTypeAction) -> Bool {

    if lhs === rhs {
        return true
    }

    return lhs.type == rhs.type
}
