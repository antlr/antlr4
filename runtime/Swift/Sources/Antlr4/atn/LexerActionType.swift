/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Represents the serialization type of a _org.antlr.v4.runtime.atn.LexerAction_.
/// 
/// -  Sam Harwell
/// -  4.2
/// 

public enum LexerActionType: Int {
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerChannelAction_ action.
    /// 
    case channel = 0
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerCustomAction_ action.
    /// 
    case custom
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerModeAction_ action.
    /// 
    case mode
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerMoreAction_ action.
    /// 
    case more
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerPopModeAction_ action.
    /// 
    case popMode
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerPushModeAction_ action.
    /// 
    case pushMode
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerSkipAction_ action.
    /// 
    case skip
    /// 
    /// The type of a _org.antlr.v4.runtime.atn.LexerTypeAction_ action.
    /// 
    case type
}
