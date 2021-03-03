/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree.Pattern;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// A chunk is either a token tag, a rule tag, or a span of literal text within a
    /// tree pattern.
    /// </summary>
    /// <remarks>
    /// A chunk is either a token tag, a rule tag, or a span of literal text within a
    /// tree pattern.
    /// <p>The method
    /// <see cref="ParseTreePatternMatcher.Split(string)"/>
    /// returns a list of
    /// chunks in preparation for creating a token stream by
    /// <see cref="ParseTreePatternMatcher.Tokenize(string)"/>
    /// . From there, we get a parse
    /// tree from with
    /// <see cref="ParseTreePatternMatcher.Compile(string, int)"/>
    /// . These
    /// chunks are converted to
    /// <see cref="RuleTagToken"/>
    /// ,
    /// <see cref="TokenTagToken"/>
    /// , or the
    /// regular tokens of the text surrounding the tags.</p>
    /// </remarks>
    internal abstract class Chunk
    {
    }
}
