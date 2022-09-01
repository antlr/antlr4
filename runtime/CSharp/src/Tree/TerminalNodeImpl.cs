/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Tree
{
    public class TerminalNodeImpl : ITerminalNode
    {
        private IToken _symbol;

        private IRuleNode _parent;

        public TerminalNodeImpl(IToken symbol)
        {
            _symbol = symbol;
        }

        public virtual IParseTree GetChild(int i)
        {
            return null;
        }

        ITree ITree.GetChild(int i)
        {
            return GetChild(i);
        }

        public virtual IToken Symbol => _symbol;

        public virtual IRuleNode Parent
        {
            get => _parent;
            set => _parent = value;
        }

        IParseTree IParseTree.Parent => Parent;

        ITree ITree.Parent => Parent;

        public virtual IToken Payload => Symbol;

        object ITree.Payload => Payload;

        public virtual Interval SourceInterval
        {
            get
            {
                if (Symbol != null)
                {
                    int tokenIndex = Symbol.TokenIndex;
                    return new Interval(tokenIndex, tokenIndex);
                }
                return Interval.Invalid;
            }
        }

        public virtual int ChildCount => 0;

        public virtual T Accept<T>(IParseTreeVisitor<T> visitor)
        {
            return visitor.VisitTerminal(this);
        }

        public virtual string GetText()
        {
            if (Symbol != null)
            {
                return Symbol.Text;
            }
            return null;
        }

        public virtual string ToStringTree(Parser parser)
        {
            return ToString();
        }

        public override string ToString()
        {
            if (Symbol != null)
            {
                if (Symbol.Type == TokenConstants.EOF)
                {
                    return "<EOF>";
                }
                return Symbol.Text;
            }
            else
            {
                return "<null>";
            }
        }

        public virtual string ToStringTree()
        {
            return ToString();
        }
    }
}
