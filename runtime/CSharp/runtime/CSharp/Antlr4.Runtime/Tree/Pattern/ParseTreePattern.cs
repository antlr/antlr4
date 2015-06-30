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
using System.Collections.Generic;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Pattern;
using Antlr4.Runtime.Tree.Xpath;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// A pattern like
    /// <c>&lt;ID&gt; = &lt;expr&gt;;</c>
    /// converted to a
    /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
    /// by
    /// <see cref="ParseTreePatternMatcher.Compile(string, int)"/>
    /// .
    /// </summary>
    public class ParseTreePattern
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="PatternRuleIndex()"/>
        /// .
        /// </summary>
        private readonly int patternRuleIndex;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Pattern()"/>
        /// .
        /// </summary>
        [NotNull]
        private readonly string pattern;

        /// <summary>
        /// This is the backing field for
        /// <see cref="PatternTree()"/>
        /// .
        /// </summary>
        [NotNull]
        private readonly IParseTree patternTree;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Matcher()"/>
        /// .
        /// </summary>
        [NotNull]
        private readonly ParseTreePatternMatcher matcher;

        /// <summary>
        /// Construct a new instance of the
        /// <see cref="ParseTreePattern"/>
        /// class.
        /// </summary>
        /// <param name="matcher">
        /// The
        /// <see cref="ParseTreePatternMatcher"/>
        /// which created this
        /// tree pattern.
        /// </param>
        /// <param name="pattern">The tree pattern in concrete syntax form.</param>
        /// <param name="patternRuleIndex">
        /// The parser rule which serves as the root of the
        /// tree pattern.
        /// </param>
        /// <param name="patternTree">
        /// The tree pattern in
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// form.
        /// </param>
        public ParseTreePattern(ParseTreePatternMatcher matcher, string pattern, int patternRuleIndex, IParseTree patternTree)
        {
            this.matcher = matcher;
            this.patternRuleIndex = patternRuleIndex;
            this.pattern = pattern;
            this.patternTree = patternTree;
        }

        /// <summary>Match a specific parse tree against this tree pattern.</summary>
        /// <remarks>Match a specific parse tree against this tree pattern.</remarks>
        /// <param name="tree">The parse tree to match against this tree pattern.</param>
        /// <returns>
        /// A
        /// <see cref="ParseTreeMatch"/>
        /// object describing the result of the
        /// match operation. The
        /// <see cref="ParseTreeMatch.Succeeded()"/>
        /// method can be
        /// used to determine whether or not the match was successful.
        /// </returns>
        [return: NotNull]
        public virtual ParseTreeMatch Match(IParseTree tree)
        {
            return matcher.Match(tree, this);
        }

        /// <summary>Determine whether or not a parse tree matches this tree pattern.</summary>
        /// <remarks>Determine whether or not a parse tree matches this tree pattern.</remarks>
        /// <param name="tree">The parse tree to match against this tree pattern.</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if
        /// <paramref name="tree"/>
        /// is a match for the current tree
        /// pattern; otherwise,
        /// <see langword="false"/>
        /// .
        /// </returns>
        public virtual bool Matches(IParseTree tree)
        {
            return matcher.Match(tree, this).Succeeded;
        }

        /// <summary>
        /// Find all nodes using XPath and then try to match those subtrees against
        /// this tree pattern.
        /// </summary>
        /// <remarks>
        /// Find all nodes using XPath and then try to match those subtrees against
        /// this tree pattern.
        /// </remarks>
        /// <param name="tree">
        /// The
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// to match against this pattern.
        /// </param>
        /// <param name="xpath">An expression matching the nodes</param>
        /// <returns>
        /// A collection of
        /// <see cref="ParseTreeMatch"/>
        /// objects describing the
        /// successful matches. Unsuccessful matches are omitted from the result,
        /// regardless of the reason for the failure.
        /// </returns>
        [return: NotNull]
        public virtual IList<ParseTreeMatch> FindAll(IParseTree tree, string xpath)
        {
            ICollection<IParseTree> subtrees = XPath.FindAll(tree, xpath, matcher.Parser);
            IList<ParseTreeMatch> matches = new List<ParseTreeMatch>();
            foreach (IParseTree t in subtrees)
            {
                ParseTreeMatch match = Match(t);
                if (match.Succeeded)
                {
                    matches.Add(match);
                }
            }
            return matches;
        }

        /// <summary>
        /// Get the
        /// <see cref="ParseTreePatternMatcher"/>
        /// which created this tree pattern.
        /// </summary>
        /// <returns>
        /// The
        /// <see cref="ParseTreePatternMatcher"/>
        /// which created this tree
        /// pattern.
        /// </returns>
        [NotNull]
        public virtual ParseTreePatternMatcher Matcher
        {
            get
            {
                return matcher;
            }
        }

        /// <summary>Get the tree pattern in concrete syntax form.</summary>
        /// <remarks>Get the tree pattern in concrete syntax form.</remarks>
        /// <returns>The tree pattern in concrete syntax form.</returns>
        [NotNull]
        public virtual string Pattern
        {
            get
            {
                return pattern;
            }
        }

        /// <summary>
        /// Get the parser rule which serves as the outermost rule for the tree
        /// pattern.
        /// </summary>
        /// <remarks>
        /// Get the parser rule which serves as the outermost rule for the tree
        /// pattern.
        /// </remarks>
        /// <returns>
        /// The parser rule which serves as the outermost rule for the tree
        /// pattern.
        /// </returns>
        public virtual int PatternRuleIndex
        {
            get
            {
                return patternRuleIndex;
            }
        }

        /// <summary>
        /// Get the tree pattern as a
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// . The rule and token tags from
        /// the pattern are present in the parse tree as terminal nodes with a symbol
        /// of type
        /// <see cref="RuleTagToken"/>
        /// or
        /// <see cref="TokenTagToken"/>
        /// .
        /// </summary>
        /// <returns>
        /// The tree pattern as a
        /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
        /// .
        /// </returns>
        [NotNull]
        public virtual IParseTree PatternTree
        {
            get
            {
                return patternTree;
            }
        }
    }
}
