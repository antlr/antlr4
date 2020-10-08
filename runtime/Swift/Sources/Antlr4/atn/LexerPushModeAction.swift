/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Implements the `pushMode` lexer action by calling
/// _org.antlr.v4.runtime.Lexer#pushMode_ with the assigned mode.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerPushModeAction: LexerAction, CustomStringConvertible {
    fileprivate let mode: Int

    /// 
    /// Constructs a new `pushMode` action with the specified mode value.
    /// - parameter mode: The mode value to pass to _org.antlr.v4.runtime.Lexer#pushMode_.
    /// 
    public init(_ mode: Int) {
        self.mode = mode
    }

    /// 
    /// Get the lexer mode this action should transition the lexer to.
    /// 
    /// - returns: The lexer mode for this `pushMode` command.
    /// 
    public func getMode() -> Int {
        return mode
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#pushMode_.
    /// 

    public override func getActionType() -> LexerActionType {
        return LexerActionType.pushMode
    }

    /// 
    /// 
    /// - returns: This method returns `false`.
    /// 

    public override func isPositionDependent() -> Bool {
        return false
    }

    /// 
    /// 
    /// 
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#pushMode_ with the
    /// value provided by _#getMode_.
    /// 
    override
    public func execute(_ lexer: Lexer) {
        lexer.pushMode(mode)
    }

    public override func hash(into hasher: inout Hasher) {
        hasher.combine(mode)
    }

    public var description: String {
        return "pushMode(\(mode))"
    }
}


public func ==(lhs: LexerPushModeAction, rhs: LexerPushModeAction) -> Bool {
    if lhs === rhs {
        return true
    }
    return lhs.mode == rhs.mode
}
