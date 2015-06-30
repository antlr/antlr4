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
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime.Tree
{
    /// <summary>The basic notion of a tree has a parent, a payload, and a list of children.</summary>
    /// <remarks>
    /// The basic notion of a tree has a parent, a payload, and a list of children.
    /// It is the most abstract interface for all the trees used by ANTLR.
    /// </remarks>
    public interface ITree
    {
        /// <summary>The parent of this node.</summary>
        /// <remarks>
        /// The parent of this node. If the return value is null, then this
        /// node is the root of the tree.
        /// </remarks>
        ITree Parent
        {
            get;
        }

        /// <summary>This method returns whatever object represents the data at this note.</summary>
        /// <remarks>
        /// This method returns whatever object represents the data at this note. For
        /// example, for parse trees, the payload can be a
        /// <see cref="Antlr4.Runtime.IToken"/>
        /// representing
        /// a leaf node or a
        /// <see cref="Antlr4.Runtime.RuleContext"/>
        /// object representing a rule
        /// invocation. For abstract syntax trees (ASTs), this is a
        /// <see cref="Antlr4.Runtime.IToken"/>
        /// object.
        /// </remarks>
        object Payload
        {
            get;
        }

        /// <summary>
        /// If there are children, get the
        /// <paramref name="i"/>
        /// th value indexed from 0.
        /// </summary>
        ITree GetChild(int i);

        /// <summary>
        /// How many children are there? If there is none, then this
        /// node represents a leaf node.
        /// </summary>
        /// <remarks>
        /// How many children are there? If there is none, then this
        /// node represents a leaf node.
        /// </remarks>
        int ChildCount
        {
            get;
        }

        /// <summary>
        /// Print out a whole tree, not just a node, in LISP format
        /// <c>(root child1 .. childN)</c>
        /// . Print just a node if this is a leaf.
        /// </summary>
        string ToStringTree();
    }
}
