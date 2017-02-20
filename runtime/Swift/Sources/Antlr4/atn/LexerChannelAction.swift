/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Implements the {@code channel} lexer action by calling
/// {@link org.antlr.v4.runtime.Lexer#setChannel} with the assigned channel.
/// 
/// -  Sam Harwell
/// -  4.2

public final class LexerChannelAction: LexerAction, CustomStringConvertible {
    fileprivate let channel: Int

    /// Constructs a new {@code channel} action with the specified channel value.
    /// - parameter channel: The channel value to pass to {@link org.antlr.v4.runtime.Lexer#setChannel}.
    public init(_ channel: Int) {
        self.channel = channel
    }

    /// Gets the channel to use for the {@link org.antlr.v4.runtime.Token} created by the lexer.
    /// 
    /// - returns: The channel to use for the {@link org.antlr.v4.runtime.Token} created by the lexer.
    public func getChannel() -> Int {
        return channel
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@link org.antlr.v4.runtime.atn.LexerActionType#CHANNEL}.

    public override func getActionType() -> LexerActionType {
        return LexerActionType.channel
    }

    /// {@inheritDoc}
    /// - returns: This method returns {@code false}.

    public override func isPositionDependent() -> Bool {
        return false
    }

    /// {@inheritDoc}
    /// 
    /// <p>This action is implemented by calling {@link org.antlr.v4.runtime.Lexer#setChannel} with the
    /// value provided by {@link #getChannel}.</p>

    public override func execute(_ lexer: Lexer) {
        lexer.setChannel(channel)
    }


    override
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, getActionType().rawValue)
        hash = MurmurHash.update(hash, channel)
        return MurmurHash.finish(hash, 2)
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
