/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// A source of characters for an ANTLR lexer.
/// 

public protocol CharStream: IntStream {
    /// 
    /// This method returns the text for a range of characters within this input
    /// stream. This method is guaranteed to not throw an exception if the
    /// specified `interval` lies entirely within a marked range. For more
    /// information about marked ranges, see _org.antlr.v4.runtime.IntStream#mark_.
    /// 
    /// - parameter interval: an interval within the stream
    /// - returns: the text of the specified interval
    /// 
    /// - throws: _ANTLRError.illegalArgument_ if `interval.a < 0`, or if
    /// `interval.b < interval.a - 1`, or if `interval.b` lies at or
    /// past the end of the stream
    /// - throws: _ANTLRError.unsupportedOperation_ if the stream does not support
    /// getting the text of the specified interval
    /// 
    func getText(_ interval: Interval) throws -> String
}
