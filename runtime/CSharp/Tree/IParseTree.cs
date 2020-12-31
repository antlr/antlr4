/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
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
