/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
