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
using System.IO;
using System.Linq;
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Xpath;

namespace Antlr4.Runtime.Tree.Xpath
{
    /// <summary>
    /// Represent a subset of XPath XML path syntax for use in identifying nodes in
    /// parse trees.
    /// </summary>
    /// <remarks>
    /// Represent a subset of XPath XML path syntax for use in identifying nodes in
    /// parse trees.
    /// <p>
    /// Split path into words and separators
    /// <c>/</c>
    /// and
    /// <c>//</c>
    /// via ANTLR
    /// itself then walk path elements from left to right. At each separator-word
    /// pair, find set of nodes. Next stage uses those as work list.</p>
    /// <p>
    /// The basic interface is
    /// <see cref="FindAll(Antlr4.Runtime.Tree.IParseTree, string, Antlr4.Runtime.Parser)">ParseTree.findAll</see>
    /// <c>(tree, pathString, parser)</c>
    /// .
    /// But that is just shorthand for:</p>
    /// <pre>
    /// <see cref="XPath"/>
    /// p = new
    /// <see cref="XPath(Antlr4.Runtime.Parser, string)">XPath</see>
    /// (parser, pathString);
    /// return p.
    /// <see cref="Evaluate(Antlr4.Runtime.Tree.IParseTree)">evaluate</see>
    /// (tree);
    /// </pre>
    /// <p>
    /// See
    /// <c>org.antlr.v4.test.TestXPath</c>
    /// for descriptions. In short, this
    /// allows operators:</p>
    /// <dl>
    /// <dt>/</dt> <dd>root</dd>
    /// <dt>//</dt> <dd>anywhere</dd>
    /// <dt>!</dt> <dd>invert; this must appear directly after root or anywhere
    /// operator</dd>
    /// </dl>
    /// <p>
    /// and path elements:</p>
    /// <dl>
    /// <dt>ID</dt> <dd>token name</dd>
    /// <dt>'string'</dt> <dd>any string literal token from the grammar</dd>
    /// <dt>expr</dt> <dd>rule name</dd>
    /// <dt>*</dt> <dd>wildcard matching any node</dd>
    /// </dl>
    /// <p>
    /// Whitespace is not allowed.</p>
    /// </remarks>
    public class XPath
    {
        public const string Wildcard = "*";

        public const string Not = "!";

        protected internal string path;

        protected internal XPathElement[] elements;

        protected internal Parser parser;

        public XPath(Parser parser, string path)
        {
            // word not operator/separator
            // word for invert operator
            this.parser = parser;
            this.path = path;
            elements = Split(path);
        }

        //		System.out.println(Arrays.toString(elements));
        // TODO: check for invalid token/rule names, bad syntax
        public virtual XPathElement[] Split(string path)
        {
            AntlrInputStream @in;
            try
            {
                @in = new AntlrInputStream(new StringReader(path));
            }
            catch (IOException ioe)
            {
                throw new ArgumentException("Could not read path: " + path, ioe);
            }
            XPathLexer lexer = new _XPathLexer_87(@in);
            lexer.RemoveErrorListeners();
            lexer.AddErrorListener(new XPathLexerErrorListener());
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            try
            {
                tokenStream.Fill();
            }
            catch (LexerNoViableAltException e)
            {
                int pos = lexer.Column;
                string msg = "Invalid tokens or characters at index " + pos + " in path '" + path + "'";
                throw new ArgumentException(msg, e);
            }
            IList<IToken> tokens = tokenStream.GetTokens();
            //		System.out.println("path="+path+"=>"+tokens);
            IList<XPathElement> elements = new List<XPathElement>();
            int n = tokens.Count;
            int i = 0;
            while (i < n)
            {
                IToken el = tokens[i];
                IToken next = null;
                switch (el.Type)
                {
                    case XPathLexer.Root:
                    case XPathLexer.Anywhere:
                    {
                        bool anywhere = el.Type == XPathLexer.Anywhere;
                        i++;
                        next = tokens[i];
                        bool invert = next.Type == XPathLexer.Bang;
                        if (invert)
                        {
                            i++;
                            next = tokens[i];
                        }
                        XPathElement pathElement = GetXPathElement(next, anywhere);
                        pathElement.invert = invert;
                        elements.Add(pathElement);
                        i++;
                        break;
                    }

                    case XPathLexer.TokenRef:
                    case XPathLexer.RuleRef:
                    case XPathLexer.Wildcard:
                    {
                        elements.Add(GetXPathElement(el, false));
                        i++;
                        break;
                    }

                    case TokenConstants.Eof:
                    {
                        goto loop_break;
                    }

                    default:
                    {
                        throw new ArgumentException("Unknowth path element " + el);
                    }
                }
            }
loop_break: ;
            return elements.ToArray();
        }

        private sealed class _XPathLexer_87 : XPathLexer
        {
            public _XPathLexer_87(ICharStream baseArg1)
                : base(baseArg1)
            {
            }

            public override void Recover(LexerNoViableAltException e)
            {
                throw e;
            }
        }

        /// <summary>
        /// Convert word like
        /// <c>*</c>
        /// or
        /// <c>ID</c>
        /// or
        /// <c>expr</c>
        /// to a path
        /// element.
        /// <paramref name="anywhere"/>
        /// is
        /// <see langword="true"/>
        /// if
        /// <c>//</c>
        /// precedes the
        /// word.
        /// </summary>
        protected internal virtual XPathElement GetXPathElement(IToken wordToken, bool anywhere)
        {
            if (wordToken.Type == TokenConstants.Eof)
            {
                throw new ArgumentException("Missing path element at end of path");
            }
            string word = wordToken.Text;
            int ttype = parser.GetTokenType(word);
            int ruleIndex = parser.GetRuleIndex(word);
            switch (wordToken.Type)
            {
                case XPathLexer.Wildcard:
                {
                    return anywhere ? new XPathWildcardAnywhereElement() : (XPathElement)new XPathWildcardElement();
                }

                case XPathLexer.TokenRef:
                case XPathLexer.String:
                {
                    if (ttype == TokenConstants.InvalidType)
                    {
                        throw new ArgumentException(word + " at index " + wordToken.StartIndex + " isn't a valid token name");
                    }
                    return anywhere ? new XPathTokenAnywhereElement(word, ttype) : (XPathElement)new XPathTokenElement(word, ttype);
                }

                default:
                {
                    if (ruleIndex == -1)
                    {
                        throw new ArgumentException(word + " at index " + wordToken.StartIndex + " isn't a valid rule name");
                    }
                    return anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : (XPathElement)new XPathRuleElement(word, ruleIndex);
                }
            }
        }

        public static ICollection<IParseTree> FindAll(IParseTree tree, string xpath, Parser parser)
        {
            Antlr4.Runtime.Tree.Xpath.XPath p = new Antlr4.Runtime.Tree.Xpath.XPath(parser, xpath);
            return p.Evaluate(tree);
        }

        /// <summary>
        /// Return a list of all nodes starting at
        /// <paramref name="t"/>
        /// as root that satisfy the
        /// path. The root
        /// <c>/</c>
        /// is relative to the node passed to
        /// <see cref="Evaluate(Antlr4.Runtime.Tree.IParseTree)"/>
        /// .
        /// </summary>
        public virtual ICollection<IParseTree> Evaluate(IParseTree t)
        {
            ParserRuleContext dummyRoot = new ParserRuleContext();
            dummyRoot.children = Antlr4.Runtime.Sharpen.Collections.SingletonList(t);
            // don't set t's parent.
            ICollection<IParseTree> work = new[] { dummyRoot };
            int i = 0;
            while (i < elements.Length)
            {
                HashSet<IParseTree> visited = new HashSet<IParseTree>();
                ICollection<IParseTree> next = new List<IParseTree>();
                foreach (IParseTree node in work)
                {
                    if (node.ChildCount > 0)
                    {
                        // only try to match next element if it has children
                        // e.g., //func/*/stat might have a token node for which
                        // we can't go looking for stat nodes.
                        ICollection<IParseTree> matching = elements[i].Evaluate(node);
                        foreach (IParseTree parseTree in matching)
                        {
                            if (visited.Add(parseTree))
                                next.Add(parseTree);
                        }
                    }
                }
                i++;
                work = next;
            }
            return work;
        }
    }
}
