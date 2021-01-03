/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

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
