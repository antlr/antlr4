/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Implements the `channel` lexer action by calling
/// _org.antlr.v4.runtime.Lexer#setChannel_ with the assigned channel.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public final class LexerChannelAction: LexerAction, CustomStringConvertible {
    fileprivate let channel: Int

    /// 
    /// Constructs a new `channel` action with the specified channel value.
    /// - parameter channel: The channel value to pass to _org.antlr.v4.runtime.Lexer#setChannel_.
    /// 
    public init(_ channel: Int) {
        self.channel = channel
    }

    /// 
    /// Gets the channel to use for the _org.antlr.v4.runtime.Token_ created by the lexer.
    /// 
    /// - returns: The channel to use for the _org.antlr.v4.runtime.Token_ created by the lexer.
    /// 
    public func getChannel() -> Int {
        return channel
    }

    /// 
    /// 
    /// - returns: This method returns _org.antlr.v4.runtime.atn.LexerActionType#CHANNEL_.
    /// 

    public override func getActionType() -> LexerActionType {
        return LexerActionType.channel
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
    /// This action is implemented by calling _org.antlr.v4.runtime.Lexer#setChannel_ with the
    /// value provided by _#getChannel_.
    /// 

    public override func execute(_ lexer: Lexer) {
        lexer.setChannel(channel)
    }


    public override func hash(into hasher: inout Hasher) {
        hasher.combine(getActionType())
        hasher.combine(channel)
    }

    public var description: String {
        return "channel\(channel)"
    }

}


public func ==(lhs: LexerChannelAction, rhs: LexerChannelAction) -> Bool {

    if lhs === rhs {
        return true
    }


    return lhs.channel == rhs.channel
}
