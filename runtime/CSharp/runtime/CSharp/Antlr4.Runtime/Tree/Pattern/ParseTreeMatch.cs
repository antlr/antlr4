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
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Pattern;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// Represents the result of matching a
    /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
    /// against a tree pattern.
    /// </summary>
    public class ParseTreeMatch
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="Tree()"/>
        /// .
        /// </summary>
        private readonly IParseTree tree;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Pattern()"/>
        /// .
        /// </summary>
        private readonly ParseTreePattern pattern;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Labels()"/>
        /// .
        /// </summary>
        private readonly MultiMap<string, IParseTree> labels;

        /// <summary>
        /// This is the backing field for
        /// <see cref="MismatchedNode()"/>
        /// .
        /// </summary>
        private readonly IParseTree mismatchedNode;

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="ParseTreeMatch"/>
        /// from the specified
        /// parse tree and pattern.
        /// </summary>
        /// <param name="tree">The parse tree to match against the pattern.</param>
        /// <param name="pattern">The parse tree pattern.</param>
        /// <param name="labels">
        /// A mapping from label names to collections of
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// objects located by the tree pattern matching process.
        /// </param>
        /// <param name="mismatchedNode">
        /// The first node which failed to match the tree
        /// pattern during the matching process.
        /// </param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="tree"/>
        /// is
        /// <see langword="null"/>
        /// </exception>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="pattern"/>
        /// is
        /// <see langword="null"/>
        /// </exception>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="labels"/>
        /// is
        /// <see langword="null"/>
        /// </exception>
        public ParseTreeMatch(IParseTree tree, ParseTreePattern pattern, MultiMap<string, IParseTree> labels, IParseTree mismatchedNode)
        {
            if (tree == null)
            {
                throw new ArgumentException("tree cannot be null");
            }
            if (pattern == null)
            {
                throw new ArgumentException("pattern cannot be null");
            }
            if (labels == null)
            {
                throw new ArgumentException("labels cannot be null");
            }
            this.tree = tree;
            this.pattern = pattern;
            this.labels = labels;
            this.mismatchedNode = mismatchedNode;
        }

        /// <summary>
        /// Get the last node associated with a specific
        /// <paramref name="label"/>
        /// .
        /// <p>For example, for pattern
        /// <c>&lt;id:ID&gt;</c>
        /// ,
        /// <c>get("id")</c>
        /// returns the
        /// node matched for that
        /// <c>ID</c>
        /// . If more than one node
        /// matched the specified label, only the last is returned. If there is
        /// no node associated with the label, this returns
        /// <see langword="null"/>
        /// .</p>
        /// <p>Pattern tags like
        /// <c>&lt;ID&gt;</c>
        /// and
        /// <c>&lt;expr&gt;</c>
        /// without labels are
        /// considered to be labeled with
        /// <c>ID</c>
        /// and
        /// <c>expr</c>
        /// , respectively.</p>
        /// </summary>
        /// <param name="label">The label to check.</param>
        /// <returns>
        /// The last
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// to match a tag with the specified
        /// label, or
        /// <see langword="null"/>
        /// if no parse tree matched a tag with the label.
        /// </returns>
        [return: Nullable]
        public virtual IParseTree Get(string label)
        {
            IList<IParseTree> parseTrees = labels.Get(label);
            if (parseTrees == null || parseTrees.Count == 0)
            {
                return null;
            }
            return parseTrees[parseTrees.Count - 1];
        }

        // return last if multiple
        /// <summary>Return all nodes matching a rule or token tag with the specified label.</summary>
        /// <remarks>
        /// Return all nodes matching a rule or token tag with the specified label.
        /// <p>If the
        /// <paramref name="label"/>
        /// is the name of a parser rule or token in the
        /// grammar, the resulting list will contain both the parse trees matching
        /// rule or tags explicitly labeled with the label and the complete set of
        /// parse trees matching the labeled and unlabeled tags in the pattern for
        /// the parser rule or token. For example, if
        /// <paramref name="label"/>
        /// is
        /// <c>"foo"</c>
        /// ,
        /// the result will contain <em>all</em> of the following.</p>
        /// <ul>
        /// <li>Parse tree nodes matching tags of the form
        /// <c>&lt;foo:anyRuleName&gt;</c>
        /// and
        /// <c>&lt;foo:AnyTokenName&gt;</c>
        /// .</li>
        /// <li>Parse tree nodes matching tags of the form
        /// <c>&lt;anyLabel:foo&gt;</c>
        /// .</li>
        /// <li>Parse tree nodes matching tags of the form
        /// <c>&lt;foo&gt;</c>
        /// .</li>
        /// </ul>
        /// </remarks>
        /// <param name="label">The label.</param>
        /// <returns>
        /// A collection of all
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// nodes matching tags with
        /// the specified
        /// <paramref name="label"/>
        /// . If no nodes matched the label, an empty list
        /// is returned.
        /// </returns>
        [return: NotNull]
        public virtual IList<IParseTree> GetAll(string label)
        {
            IList<IParseTree> nodes = labels.Get(label);
            if (nodes == null)
            {
                return Sharpen.Collections.EmptyList<IParseTree>();
            }
            return nodes;
        }

        /// <summary>Return a mapping from label &#x2192; [list of nodes].</summary>
        /// <remarks>
        /// Return a mapping from label &#x2192; [list of nodes].
        /// <p>The map includes special entries corresponding to the names of rules and
        /// tokens referenced in tags in the original pattern. For additional
        /// information, see the description of
        /// <see cref="GetAll(string)"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// A mapping from labels to parse tree nodes. If the parse tree
        /// pattern did not contain any rule or token tags, this map will be empty.
        /// </returns>
        [NotNull]
        public virtual MultiMap<string, IParseTree> Labels
        {
            get
            {
                return labels;
            }
        }

        /// <summary>Get the node at which we first detected a mismatch.</summary>
        /// <remarks>Get the node at which we first detected a mismatch.</remarks>
        /// <returns>
        /// the node at which we first detected a mismatch, or
        /// <see langword="null"/>
        /// if the match was successful.
        /// </returns>
        [Nullable]
        public virtual IParseTree MismatchedNode
        {
            get
            {
                return mismatchedNode;
            }
        }

        /// <summary>Gets a value indicating whether the match operation succeeded.</summary>
        /// <remarks>Gets a value indicating whether the match operation succeeded.</remarks>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if the match operation succeeded; otherwise,
        /// <see langword="false"/>
        /// .
        /// </returns>
        public virtual bool Succeeded
        {
            get
            {
                return mismatchedNode == null;
            }
        }

        /// <summary>Get the tree pattern we are matching against.</summary>
        /// <remarks>Get the tree pattern we are matching against.</remarks>
        /// <returns>The tree pattern we are matching against.</returns>
        [NotNull]
        public virtual ParseTreePattern Pattern
        {
            get
            {
                return pattern;
            }
        }

        /// <summary>Get the parse tree we are trying to match to a pattern.</summary>
        /// <remarks>Get the parse tree we are trying to match to a pattern.</remarks>
        /// <returns>
        /// The
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// we are trying to match to a pattern.
        /// </returns>
        [NotNull]
        public virtual IParseTree Tree
        {
            get
            {
                return tree;
            }
        }

        /// <summary><inheritDoc/></summary>
        public override string ToString()
        {
            return string.Format("Match {0}; found {1} labels", Succeeded ? "succeeded" : "failed", Labels.Count);
        }
    }
}
