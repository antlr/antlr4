/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This implementation of {@link org.antlr.v4.runtime.atn.LexerAction} is used for tracking input offsets
/// for position-dependent actions within a {@link org.antlr.v4.runtime.atn.LexerActionExecutor}.
/// 
/// <p>This action is not serialized as part of the ATN, and is only required for
/// position-dependent lexer actions which appear at a location other than the
/// end of a rule. For more information about DFA optimizations employed for
/// lexer actions, see {@link org.antlr.v4.runtime.atn.LexerActionExecutor#append} and
/// {@link org.antlr.v4.runtime.atn.LexerActionExecutor#fixOffsetBeforeMatch}.</p>
/// 
/// -  Sam Harwell
/// -  4.2

public final class LexerIndexedCustomAction: LexerAction {
    fileprivate let offset: Int
    fileprivate let action: LexerAction

    /// Constructs a new indexed custom action by associating a character offset
    /// with a {@link org.antlr.v4.runtime.atn.LexerAction}.
    /// 
    /// <p>Note: This class is only required for lexer actions for which
    /// {@link org.antlr.v4.runtime.atn.LexerAction#isPositionDependent} returns {@code true}.</p>
    /// 
    /// - parameter offset: The offset into the input {@link org.antlr.v4.runtime.CharStream}, relative to
    /// the token start index, at which the specified lexer action should be
    /// executed.
    /// - parameter action: The lexer action to execute at a particular offset in the
    /// input {@link org.antlr.v4.runtime.CharStream}.
    public init(_ offset: Int, _ action: LexerAction) {
        self.offset = offset
        self.action = action
    }

    /// Gets the location in the input {@link org.antlr.v4.runtime.CharStream} at which the lexer
    /// action should be executed. The value is interpreted as an offset relative
    /// to the token start index.
    /// 
    /// - returns: The location in the input {@link org.antlr.v4.runtime.CharStream} at which the lexer
    /// action should be executed.
    public func getOffset() -> Int {
        return offset
    }

    /// Gets the lexer action to execute.
    /// 
    /// - returns: A {@link org.antlr.v4.runtime.atn.LexerAction} object which executes the lexer action.
    public func getAction() -> LexerAction {
        return action
    }

    /// {@inheritDoc}
    /// 
    /// - returns: This method returns the result of calling {@link #getActionType}
    /// on the {@link org.antlr.v4.runtime.atn.LexerAction} returned by {@link #getAction}.

    public override func getActionType() -> LexerActionType {
        return action.getActionType()
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@code true}.

    public override func isPositionDependent() -> Bool {
        return true
    }

    /// {@inheritDoc}
    /// 
    /// <p>This method calls {@link #execute} on the result of {@link #getAction}
    /// using the provided {@code lexer}.</p>

    public override func execute(_ lexer: Lexer) throws {
        // assume the input stream position was properly set by the calling code
        try action.execute(lexer)
    }


    public override var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, offset)
        hash = MurmurHash.update(hash, action)
        return MurmurHash.finish(hash, 2)
    }


}

public func ==(lhs: LexerIndexedCustomAction, rhs: LexerIndexedCustomAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.offset == rhs.offset
            && lhs.action == rhs.action

}
