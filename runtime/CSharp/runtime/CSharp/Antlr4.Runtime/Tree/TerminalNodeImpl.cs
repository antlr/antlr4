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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime.Tree
{
    public class TerminalNodeImpl : ITerminalNode
    {
        private IToken _symbol;

        private IRuleNode _parent;

        public TerminalNodeImpl(IToken symbol)
        {
            this._symbol = symbol;
        }

        public virtual IParseTree GetChild(int i)
        {
            return null;
        }

        ITree ITree.GetChild(int i)
        {
            return GetChild(i);
        }

        public virtual IToken Symbol
        {
            get
            {
                return _symbol;
            }
        }

        public virtual IRuleNode Parent
        {
            get
            {
                return _parent;
            }
			set 
			{
				_parent = value;
			}
        }

        IParseTree IParseTree.Parent
        {
            get
            {
                return Parent;
            }
        }

        ITree ITree.Parent
        {
            get
            {
                return Parent;
            }
        }

        public virtual IToken Payload
        {
            get
            {
                return Symbol;
            }
        }

        object ITree.Payload
        {
            get
            {
                return Payload;
            }
        }

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

        public virtual int ChildCount
        {
            get
            {
                return 0;
            }
        }

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
                if (Symbol.Type == TokenConstants.Eof)
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
