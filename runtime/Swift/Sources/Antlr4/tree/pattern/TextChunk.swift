/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// 
/// Represents a span of raw text (concrete syntax) between tags in a tree
/// pattern string.
/// 

public class TextChunk: Chunk, CustomStringConvertible {
    /// 
    /// This is the backing field for _#getText_.
    /// 

    private let text: String

    /// 
    /// Constructs a new instance of _org.antlr.v4.runtime.tree.pattern.TextChunk_ with the specified text.
    /// 
    /// - Parameter text: The text of this chunk.
    /// - Throws: ANTLRError.illegalArgument if `text` is `null`.
    /// 
    public init(_ text: String) {
        self.text = text
    }

    /// 
    /// Gets the raw text of this chunk.
    /// 
    /// - Returns: The text of the chunk.
    /// 

    public final func getText() -> String {
        return text
    }

    ///
    /// The implementation for _org.antlr.v4.runtime.tree.pattern.TextChunk_ returns the result of
    /// _#getText()_ in single quotes.
    ///
    public var description: String {
        return "'\(text)'"
    }


    override public func isEqual(_ other: Chunk) -> Bool {
        guard let other = other as? TextChunk else {
            return false
        }
        return text == other.text
    }
}
