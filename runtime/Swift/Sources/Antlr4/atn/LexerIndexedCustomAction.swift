/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// This implementation of _org.antlr.v4.runtime.atn.LexerAction_ is used for tracking input offsets
/// for position-dependent actions within a _org.antlr.v4.runtime.atn.LexerActionExecutor_.
/// 
/// This action is not serialized as part of the ATN, and is only required for
/// position-dependent lexer actions which appear at a location other than the
/// end of a rule. For more information about DFA optimizations employed for
/// lexer actions, see _org.antlr.v4.runtime.atn.LexerActionExecutor#append_ and
/// _org.antlr.v4.runtime.atn.LexerActionExecutor#fixOffsetBeforeMatch_.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerIndexedCustomAction: LexerAction {
    fileprivate let offset: Int
    fileprivate let action: LexerAction

    /// 
    /// Constructs a new indexed custom action by associating a character offset
    /// with a _org.antlr.v4.runtime.atn.LexerAction_.
    /// 
    /// Note: This class is only required for lexer actions for which
    /// _org.antlr.v4.runtime.atn.LexerAction#isPositionDependent_ returns `true`.
    /// 
    /// - parameter offset: The offset into the input _org.antlr.v4.runtime.CharStream_, relative to
    /// the token start index, at which the specified lexer action should be
    /// executed.
    /// - parameter action: The lexer action to execute at a particular offset in the
    /// input _org.antlr.v4.runtime.CharStream_.
    /// 
    public init(_ offset: Int, _ action: LexerAction) {
        self.offset = offset
        self.action = action
    }

    /// 
    /// Gets the location in the input _org.antlr.v4.runtime.CharStream_ at which the lexer
    /// action should be executed. The value is interpreted as an offset relative
    /// to the token start index.
    /// 
    /// - returns: The location in the input _org.antlr.v4.runtime.CharStream_ at which the lexer
    /// action should be executed.
    /// 
    public func getOffset() -> Int {
        return offset
    }

    /// 
    /// Gets the lexer action to execute.
    /// 
    /// - returns: A _org.antlr.v4.runtime.atn.LexerAction_ object which executes the lexer action.
    /// 
    public func getAction() -> LexerAction {
        return action
    }

    /// 
    /// 
    /// 
    /// - returns: This method returns the result of calling _#getActionType_
    /// on the _org.antlr.v4.runtime.atn.LexerAction_ returned by _#getAction_.
    /// 

    public override func getActionType() -> LexerActionType {
        return action.getActionType()
    }

    /// 
    /// 
    /// - returns: This method returns `true`.
    /// 

    public override func isPositionDependent() -> Bool {
        return true
    }

    /// 
    /// 
    /// 
    /// This method calls _#execute_ on the result of _#getAction_
    /// using the provided `lexer`.
    /// 

    public override func execute(_ lexer: Lexer) throws {
        // assume the input stream position was properly set by the calling code
        try action.execute(lexer)
    }


    public override var hashValue: Int {
        var hash = MurmurHash.initialize()
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
