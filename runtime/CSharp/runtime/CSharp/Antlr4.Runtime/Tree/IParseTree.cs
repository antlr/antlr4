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
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime.Tree
{
    /// <summary>
    /// An interface to access the tree of
    /// <see cref="Antlr4.Runtime.RuleContext"/>
    /// objects created
    /// during a parse that makes the data structure look like a simple parse tree.
    /// This node represents both internal nodes, rule invocations,
    /// and leaf nodes, token matches.
    /// <p>The payload is either a
    /// <see cref="Antlr4.Runtime.IToken"/>
    /// or a
    /// <see cref="Antlr4.Runtime.RuleContext"/>
    /// object.</p>
    /// </summary>
    public interface IParseTree : ISyntaxTree
    {
        new IParseTree Parent
        {
            get;
        }

        // the following methods narrow the return type; they are not additional methods
        new IParseTree GetChild(int i);

        /// <summary>
        /// The
        /// <see cref="IParseTreeVisitor{Result}"/>
        /// needs a double dispatch method.
        /// </summary>
        T Accept<T>(IParseTreeVisitor<T> visitor);

        /// <summary>Return the combined text of all leaf nodes.</summary>
        /// <remarks>
        /// Return the combined text of all leaf nodes. Does not get any
        /// off-channel tokens (if any) so won't return whitespace and
        /// comments if they are sent to parser on hidden channel.
        /// </remarks>
        string GetText();

        /// <summary>
        /// Specialize toStringTree so that it can print out more information
        /// based upon the parser.
        /// </summary>
        /// <remarks>
        /// Specialize toStringTree so that it can print out more information
        /// based upon the parser.
        /// </remarks>
        string ToStringTree(Parser parser);
    }
}
