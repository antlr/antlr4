/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Pattern;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// A tree pattern matching mechanism for ANTLR
    /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
    /// s.
    /// <p>Patterns are strings of source input text with special tags representing
    /// token or rule references such as:</p>
    /// <p>
    /// <c>&lt;ID&gt; = &lt;expr&gt;;</c>
    /// </p>
    /// <p>Given a pattern start rule such as
    /// <c>statement</c>
    /// , this object constructs
    /// a
    /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
    /// with placeholders for the
    /// <c>ID</c>
    /// and
    /// <c>expr</c>
    /// subtree. Then the
    /// <see cref="Match(Antlr4.Runtime.Tree.IParseTree, ParseTreePattern)"/>
    /// routines can compare an actual
    /// <see cref="Antlr4.Runtime.Tree.IParseTree"/>
    /// from a parse with this pattern. Tag
    /// <c>&lt;ID&gt;</c>
    /// matches
    /// any
    /// <c>ID</c>
    /// token and tag
    /// <c>&lt;expr&gt;</c>
    /// references the result of the
    /// <c>expr</c>
    /// rule (generally an instance of
    /// <c>ExprContext</c>
    /// .</p>
    /// <p>Pattern
    /// <c>x = 0;</c>
    /// is a similar pattern that matches the same pattern
    /// except that it requires the identifier to be
    /// <c>x</c>
    /// and the expression to
    /// be
    /// <c>0</c>
    /// .</p>
    /// <p>The
    /// <see cref="Matches(Antlr4.Runtime.Tree.IParseTree, ParseTreePattern)"/>
    /// routines return
    /// <see langword="true"/>
    /// or
    /// <see langword="false"/>
    /// based
    /// upon a match for the tree rooted at the parameter sent in. The
    /// <see cref="Match(Antlr4.Runtime.Tree.IParseTree, ParseTreePattern)"/>
    /// routines return a
    /// <see cref="ParseTreeMatch"/>
    /// object that
    /// contains the parse tree, the parse tree pattern, and a map from tag name to
    /// matched nodes (more below). A subtree that fails to match, returns with
    /// <see cref="ParseTreeMatch.MismatchedNode"/>
    /// set to the first tree node that did not
    /// match.</p>
    /// <p>For efficiency, you can compile a tree pattern in string form to a
    /// <see cref="ParseTreePattern"/>
    /// object.</p>
    /// <p>See
    /// <c>TestParseTreeMatcher</c>
    /// for lots of examples.
    /// <see cref="ParseTreePattern"/>
    /// has two static helper methods:
    /// <see cref="ParseTreePattern.FindAll(Antlr4.Runtime.Tree.IParseTree, string)"/>
    /// and
    /// <see cref="ParseTreePattern.Match(Antlr4.Runtime.Tree.IParseTree)"/>
    /// that
    /// are easy to use but not super efficient because they create new
    /// <see cref="ParseTreePatternMatcher"/>
    /// objects each time and have to compile the
    /// pattern in string form before using it.</p>
    /// <p>The lexer and parser that you pass into the
    /// <see cref="ParseTreePatternMatcher"/>
    /// constructor are used to parse the pattern in string form. The lexer converts
    /// the
    /// <c>&lt;ID&gt; = &lt;expr&gt;;</c>
    /// into a sequence of four tokens (assuming lexer
    /// throws out whitespace or puts it on a hidden channel). Be aware that the
    /// input stream is reset for the lexer (but not the parser; a
    /// <see cref="Antlr4.Runtime.ParserInterpreter"/>
    /// is created to parse the input.). Any user-defined
    /// fields you have put into the lexer might get changed when this mechanism asks
    /// it to scan the pattern string.</p>
    /// <p>Normally a parser does not accept token
    /// <c>&lt;expr&gt;</c>
    /// as a valid
    /// <c>expr</c>
    /// but, from the parser passed in, we create a special version of
    /// the underlying grammar representation (an
    /// <see cref="Antlr4.Runtime.Atn.ATN"/>
    /// ) that allows imaginary
    /// tokens representing rules (
    /// <c>&lt;expr&gt;</c>
    /// ) to match entire rules. We call
    /// these <em>bypass alternatives</em>.</p>
    /// <p>Delimiters are
    /// <c>&lt;</c>
    /// and
    /// <c>&gt;</c>
    /// , with
    /// <c>\</c>
    /// as the escape string
    /// by default, but you can set them to whatever you want using
    /// <see cref="SetDelimiters(string, string, string)"/>
    /// . You must escape both start and stop strings
    /// <c>\&lt;</c>
    /// and
    /// <c>\&gt;</c>
    /// .</p>
    /// </summary>
    public class ParseTreePatternMatcher
    {
        [System.Serializable]
        public class CannotInvokeStartRule : Exception
        {
            public CannotInvokeStartRule(Exception e)
                : base(e.Message, e)
            {
            }
        }

        [System.Serializable]
        public class StartRuleDoesNotConsumeFullPattern : Exception
        {
            // Fixes https://github.com/antlr/antlr4/issues/413
            // "Tree pattern compilation doesn't check for a complete parse"
        }

        /// <summary>
        /// This is the backing field for
        /// <see cref="Lexer()"/>
        /// .
        /// </summary>
        private readonly Lexer lexer;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Parser()"/>
        /// .
        /// </summary>
        private readonly Parser parser;

        protected internal string start = "<";

        protected internal string stop = ">";

        protected internal string escape = "\\";

        /// <summary>
        /// Constructs a
        /// <see cref="ParseTreePatternMatcher"/>
        /// or from a
        /// <see cref="Antlr4.Runtime.Lexer"/>
        /// and
        /// <see cref="Antlr4.Runtime.Parser"/>
        /// object. The lexer input stream is altered for tokenizing
        /// the tree patterns. The parser is used as a convenient mechanism to get
        /// the grammar name, plus token, rule names.
        /// </summary>
        public ParseTreePatternMatcher(Lexer lexer, Parser parser)
        {
            // e.g., \< and \> must escape BOTH!
            this.lexer = lexer;
            this.parser = parser;
        }

        /// <summary>
        /// Set the delimiters used for marking rule and token tags within concrete
        /// syntax used by the tree pattern parser.
        /// </summary>
        /// <remarks>
        /// Set the delimiters used for marking rule and token tags within concrete
        /// syntax used by the tree pattern parser.
        /// </remarks>
        /// <param name="start">The start delimiter.</param>
        /// <param name="stop">The stop delimiter.</param>
        /// <param name="escapeLeft">The escape sequence to use for escaping a start or stop delimiter.</param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="start"/>
        /// is
        /// <see langword="null"/>
        /// or empty.
        /// </exception>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="stop"/>
        /// is
        /// <see langword="null"/>
        /// or empty.
        /// </exception>
        public virtual void SetDelimiters(string start, string stop, string escapeLeft)
        {
            if (string.IsNullOrEmpty(start))
            {
                throw new ArgumentException("start cannot be null or empty");
            }
            if (string.IsNullOrEmpty(stop))
            {
                throw new ArgumentException("stop cannot be null or empty");
            }
            this.start = start;
            this.stop = stop;
            this.escape = escapeLeft;
        }

        /// <summary>
        /// Does
        /// <paramref name="pattern"/>
        /// matched as rule
        /// <paramref name="patternRuleIndex"/>
        /// match
        /// <paramref name="tree"/>
        /// ?
        /// </summary>
        public virtual bool Matches(IParseTree tree, string pattern, int patternRuleIndex)
        {
            ParseTreePattern p = Compile(pattern, patternRuleIndex);
            return Matches(tree, p);
        }

        /// <summary>
        /// Does
        /// <paramref name="pattern"/>
        /// matched as rule patternRuleIndex match tree? Pass in a
        /// compiled pattern instead of a string representation of a tree pattern.
        /// </summary>
        public virtual bool Matches(IParseTree tree, ParseTreePattern pattern)
        {
            MultiMap<string, IParseTree> labels = new MultiMap<string, IParseTree>();
            IParseTree mismatchedNode = MatchImpl(tree, pattern.PatternTree, labels);
            return mismatchedNode == null;
        }

        /// <summary>
        /// Compare
        /// <paramref name="pattern"/>
        /// matched as rule
        /// <paramref name="patternRuleIndex"/>
        /// against
        /// <paramref name="tree"/>
        /// and return a
        /// <see cref="ParseTreeMatch"/>
        /// object that contains the
        /// matched elements, or the node at which the match failed.
        /// </summary>
        public virtual ParseTreeMatch Match(IParseTree tree, string pattern, int patternRuleIndex)
        {
            ParseTreePattern p = Compile(pattern, patternRuleIndex);
            return Match(tree, p);
        }

        /// <summary>
        /// Compare
        /// <paramref name="pattern"/>
        /// matched against
        /// <paramref name="tree"/>
        /// and return a
        /// <see cref="ParseTreeMatch"/>
        /// object that contains the matched elements, or the
        /// node at which the match failed. Pass in a compiled pattern instead of a
        /// string representation of a tree pattern.
        /// </summary>
        [return: NotNull]
        public virtual ParseTreeMatch Match(IParseTree tree, ParseTreePattern pattern)
        {
            MultiMap<string, IParseTree> labels = new MultiMap<string, IParseTree>();
            IParseTree mismatchedNode = MatchImpl(tree, pattern.PatternTree, labels);
            return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
        }

        /// <summary>
        /// For repeated use of a tree pattern, compile it to a
        /// <see cref="ParseTreePattern"/>
        /// using this method.
        /// </summary>
        public virtual ParseTreePattern Compile(string pattern, int patternRuleIndex)
        {
            IList<IToken> tokenList = Tokenize(pattern);
            ListTokenSource tokenSrc = new ListTokenSource(tokenList);
            CommonTokenStream tokens = new CommonTokenStream(tokenSrc);
            ParserInterpreter parserInterp = new ParserInterpreter(parser.GrammarFileName,
                                                                   parser.Vocabulary,
                                                                   Arrays.AsList(parser.RuleNames),
                                                                   parser.GetATNWithBypassAlts(),
                                                                   tokens);
            IParseTree tree = null;
            try
            {
                parserInterp.ErrorHandler = new BailErrorStrategy();
                tree = parserInterp.Parse(patternRuleIndex);
            }
            catch (ParseCanceledException e)
            {
                //			System.out.println("pattern tree = "+tree.toStringTree(parserInterp));
                throw (RecognitionException)e.InnerException;
            }
            catch (RecognitionException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new ParseTreePatternMatcher.CannotInvokeStartRule(e);
            }
            // Make sure tree pattern compilation checks for a complete parse
            if (tokens.LA(1) != TokenConstants.EOF)
            {
                throw new ParseTreePatternMatcher.StartRuleDoesNotConsumeFullPattern();
            }
            return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
        }

        /// <summary>Used to convert the tree pattern string into a series of tokens.</summary>
        /// <remarks>
        /// Used to convert the tree pattern string into a series of tokens. The
        /// input stream is reset.
        /// </remarks>
        [NotNull]
        public virtual Lexer Lexer
        {
            get
            {
                return lexer;
            }
        }

        /// <summary>
        /// Used to collect to the grammar file name, token names, rule names for
        /// used to parse the pattern into a parse tree.
        /// </summary>
        /// <remarks>
        /// Used to collect to the grammar file name, token names, rule names for
        /// used to parse the pattern into a parse tree.
        /// </remarks>
        [NotNull]
        public virtual Parser Parser
        {
            get
            {
                return parser;
            }
        }

        // ---- SUPPORT CODE ----
        /// <summary>
        /// Recursively walk
        /// <paramref name="tree"/>
        /// against
        /// <paramref name="patternTree"/>
        /// , filling
        /// <c>match.</c>
        /// <see cref="ParseTreeMatch.Labels"/>
        /// .
        /// </summary>
        /// <returns>
        /// the first node encountered in
        /// <paramref name="tree"/>
        /// which does not match
        /// a corresponding node in
        /// <paramref name="patternTree"/>
        /// , or
        /// <see langword="null"/>
        /// if the match
        /// was successful. The specific node returned depends on the matching
        /// algorithm used by the implementation, and may be overridden.
        /// </returns>
        [return: Nullable]
        protected internal virtual IParseTree MatchImpl(IParseTree tree, IParseTree patternTree, MultiMap<string, IParseTree> labels)
        {
            if (tree == null)
            {
                throw new ArgumentException("tree cannot be null");
            }
            if (patternTree == null)
            {
                throw new ArgumentException("patternTree cannot be null");
            }
            // x and <ID>, x and y, or x and x; or could be mismatched types
            if (tree is ITerminalNode && patternTree is ITerminalNode)
            {
                ITerminalNode t1 = (ITerminalNode)tree;
                ITerminalNode t2 = (ITerminalNode)patternTree;
                IParseTree mismatchedNode = null;
                // both are tokens and they have same type
                if (t1.Symbol.Type == t2.Symbol.Type)
                {
                    if (t2.Symbol is TokenTagToken)
                    {
                        // x and <ID>
                        TokenTagToken tokenTagToken = (TokenTagToken)t2.Symbol;
                        // track label->list-of-nodes for both token name and label (if any)

                        labels.Map(tokenTagToken.TokenName, tree);
                        if (tokenTagToken.Label != null)
                        {
                            labels.Map(tokenTagToken.Label, tree);
                        }
                    }
                    else
                    {
                        if (t1.GetText().Equals(t2.GetText(), StringComparison.Ordinal))
                        {
                        }
                        else
                        {
                            // x and x
                            // x and y
                            if (mismatchedNode == null)
                            {
                                mismatchedNode = t1;
                            }
                        }
                    }
                }
                else
                {
                    if (mismatchedNode == null)
                    {
                        mismatchedNode = t1;
                    }
                }
                return mismatchedNode;
            }
            if (tree is ParserRuleContext && patternTree is ParserRuleContext)
            {
                ParserRuleContext r1 = (ParserRuleContext)tree;
                ParserRuleContext r2 = (ParserRuleContext)patternTree;
                IParseTree mismatchedNode = null;
                // (expr ...) and <expr>
                RuleTagToken ruleTagToken = GetRuleTagToken(r2);
                if (ruleTagToken != null)
                {
                    if (r1.RuleIndex == r2.RuleIndex)
                    {
                        // track label->list-of-nodes for both rule name and label (if any)
                        labels.Map(ruleTagToken.RuleName, tree);
                        if (ruleTagToken.Label != null)
                        {
                            labels.Map(ruleTagToken.Label, tree);
                        }
                    }
                    else
                    {
                        if (mismatchedNode == null)
                        {
                            mismatchedNode = r1;
                        }
                    }
                    return mismatchedNode;
                }
                // (expr ...) and (expr ...)
                if (r1.ChildCount != r2.ChildCount)
                {
                    if (mismatchedNode == null)
                    {
                        mismatchedNode = r1;
                    }
                    return mismatchedNode;
                }
                int n = r1.ChildCount;
                for (int i = 0; i < n; i++)
                {
                    IParseTree childMatch = MatchImpl(r1.GetChild(i), patternTree.GetChild(i), labels);
                    if (childMatch != null)
                    {
                        return childMatch;
                    }
                }
                return mismatchedNode;
            }
            // if nodes aren't both tokens or both rule nodes, can't match
            return tree;
        }

        /// <summary>
        /// Is
        /// <paramref name="t"/>
        ///
        /// <c>(expr &lt;expr&gt;)</c>
        /// subtree?
        /// </summary>
        protected internal virtual RuleTagToken GetRuleTagToken(IParseTree t)
        {
            if (t is IRuleNode)
            {
                IRuleNode r = (IRuleNode)t;
                if (r.ChildCount == 1 && r.GetChild(0) is ITerminalNode)
                {
                    ITerminalNode c = (ITerminalNode)r.GetChild(0);
                    if (c.Symbol is RuleTagToken)
                    {
                        //					System.out.println("rule tag subtree "+t.toStringTree(parser));
                        return (RuleTagToken)c.Symbol;
                    }
                }
            }
            return null;
        }

        public virtual IList<IToken> Tokenize(string pattern)
        {
            // split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
            IList<Chunk> chunks = Split(pattern);
            // create token stream from text and tags
            IList<IToken> tokens = new List<IToken>();
            foreach (Chunk chunk in chunks)
            {
                if (chunk is TagChunk)
                {
                    TagChunk tagChunk = (TagChunk)chunk;
                    // add special rule token or conjure up new token from name
                    if (System.Char.IsUpper(tagChunk.Tag[0]))
                    {
                        int ttype = parser.GetTokenType(tagChunk.Tag);
                        if (ttype == TokenConstants.InvalidType)
                        {
                            throw new ArgumentException("Unknown token " + tagChunk.Tag + " in pattern: " + pattern);
                        }
                        TokenTagToken t = new TokenTagToken(tagChunk.Tag, ttype, tagChunk.Label);
                        tokens.Add(t);
                    }
                    else
                    {
                        if (System.Char.IsLower(tagChunk.Tag[0]))
                        {
                            int ruleIndex = parser.GetRuleIndex(tagChunk.Tag);
                            if (ruleIndex == -1)
                            {
                                throw new ArgumentException("Unknown rule " + tagChunk.Tag + " in pattern: " + pattern);
                            }
                            int ruleImaginaryTokenType = parser.GetATNWithBypassAlts().ruleToTokenType[ruleIndex];
                            tokens.Add(new RuleTagToken(tagChunk.Tag, ruleImaginaryTokenType, tagChunk.Label));
                        }
                        else
                        {
                            throw new ArgumentException("invalid tag: " + tagChunk.Tag + " in pattern: " + pattern);
                        }
                    }
                }
                else
                {
                    TextChunk textChunk = (TextChunk)chunk;
                    AntlrInputStream @in = new AntlrInputStream(textChunk.Text);
                    lexer.SetInputStream(@in);
                    IToken t = lexer.NextToken();
                    while (t.Type != TokenConstants.EOF)
                    {
                        tokens.Add(t);
                        t = lexer.NextToken();
                    }
                }
            }
            //		System.out.println("tokens="+tokens);
            return tokens;
        }

        /// <summary>
        /// Split
        /// <c>&lt;ID&gt; = &lt;e:expr&gt; ;</c>
        /// into 4 chunks for tokenizing by
        /// <see cref="Tokenize(string)"/>
        /// .
        /// </summary>
        internal virtual IList<Chunk> Split(string pattern)
        {
            int p = 0;
            int n = pattern.Length;
            IList<Chunk> chunks = new List<Chunk>();
            // find all start and stop indexes first, then collect
            IList<int> starts = new List<int>();
            IList<int> stops = new List<int>();
            while (p < n)
            {
                if (p == pattern.IndexOf(escape + start, p))
                {
                    p += escape.Length + start.Length;
                }
                else
                {
                    if (p == pattern.IndexOf(escape + stop, p))
                    {
                        p += escape.Length + stop.Length;
                    }
                    else
                    {
                        if (p == pattern.IndexOf(start, p))
                        {
                            starts.Add(p);
                            p += start.Length;
                        }
                        else
                        {
                            if (p == pattern.IndexOf(stop, p))
                            {
                                stops.Add(p);
                                p += stop.Length;
                            }
                            else
                            {
                                p++;
                            }
                        }
                    }
                }
            }
            //		System.out.println("");
            //		System.out.println(starts);
            //		System.out.println(stops);
            if (starts.Count > stops.Count)
            {
                throw new ArgumentException("unterminated tag in pattern: " + pattern);
            }
            if (starts.Count < stops.Count)
            {
                throw new ArgumentException("missing start tag in pattern: " + pattern);
            }
            int ntags = starts.Count;
            for (int i = 0; i < ntags; i++)
            {
                if (starts[i] >= stops[i])
                {
                    throw new ArgumentException("tag delimiters out of order in pattern: " + pattern);
                }
            }
            // collect into chunks now
            if (ntags == 0)
            {
                string text = Sharpen.Runtime.Substring(pattern, 0, n);
                chunks.Add(new TextChunk(text));
            }
            if (ntags > 0 && starts[0] > 0)
            {
                // copy text up to first tag into chunks
                string text = Sharpen.Runtime.Substring(pattern, 0, starts[0]);
                chunks.Add(new TextChunk(text));
            }
            for (int i_1 = 0; i_1 < ntags; i_1++)
            {
                // copy inside of <tag>
                string tag = Sharpen.Runtime.Substring(pattern, starts[i_1] + start.Length, stops[i_1]);
                string ruleOrToken = tag;
                string label = null;
                int colon = tag.IndexOf(':');
                if (colon >= 0)
                {
                    label = Sharpen.Runtime.Substring(tag, 0, colon);
                    ruleOrToken = Sharpen.Runtime.Substring(tag, colon + 1, tag.Length);
                }
                chunks.Add(new TagChunk(label, ruleOrToken));
                if (i_1 + 1 < ntags)
                {
                    // copy from end of <tag> to start of next
                    string text = Sharpen.Runtime.Substring(pattern, stops[i_1] + stop.Length, starts[i_1 + 1]);
                    chunks.Add(new TextChunk(text));
                }
            }
            if (ntags > 0)
            {
                int afterLastTag = stops[ntags - 1] + stop.Length;
                if (afterLastTag < n)
                {
                    // copy text from end of last tag to end
                    string text = Sharpen.Runtime.Substring(pattern, afterLastTag, n);
                    chunks.Add(new TextChunk(text));
                }
            }
            // strip out the escape sequences from text chunks but not tags
            for (int i_2 = 0; i_2 < chunks.Count; i_2++)
            {
                Chunk c = chunks[i_2];
                if (c is TextChunk)
                {
                    TextChunk tc = (TextChunk)c;
                    string unescaped = tc.Text.Replace(escape, string.Empty);
                    if (unescaped.Length < tc.Text.Length)
                    {
                        chunks.Set(i_2, new TextChunk(unescaped));
                    }
                }
            }
            return chunks;
        }
    }
}
