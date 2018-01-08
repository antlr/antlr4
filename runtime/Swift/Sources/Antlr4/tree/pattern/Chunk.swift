/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/// 
/// A chunk is either a token tag, a rule tag, or a span of literal text within a
/// tree pattern.
/// 
/// The method _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#split(String)_ returns a list of
/// chunks in preparation for creating a token stream by
/// _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#tokenize(String)_. From there, we get a parse
/// tree from with _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#compile(String, int)_. These
/// chunks are converted to _org.antlr.v4.runtime.tree.pattern.RuleTagToken_, _org.antlr.v4.runtime.tree.pattern.TokenTagToken_, or the
/// regular tokens of the text surrounding the tags.
/// 

public class Chunk: Equatable {
    public static func ==(lhs: Chunk, rhs: Chunk) -> Bool {
        return lhs.isEqual(rhs)
    }

    public func isEqual(_ other: Chunk) -> Bool {
        return false
    }
}
