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
    public class ParseTreeWalker
    {
        public static readonly ParseTreeWalker Default = new ParseTreeWalker();

        public virtual void Walk(IParseTreeListener listener, IParseTree t)
        {
            if (t is IErrorNode)
            {
                listener.VisitErrorNode((IErrorNode)t);
                return;
            }
            else
            {
                if (t is ITerminalNode)
                {
                    listener.VisitTerminal((ITerminalNode)t);
                    return;
                }
            }
            IRuleNode r = (IRuleNode)t;
            EnterRule(listener, r);
            int n = r.ChildCount;
            for (int i = 0; i < n; i++)
            {
                Walk(listener, r.GetChild(i));
            }
            ExitRule(listener, r);
        }

        /// <summary>
        /// The discovery of a rule node, involves sending two events: the generic
        /// <see cref="IParseTreeListener.EnterEveryRule(Antlr4.Runtime.ParserRuleContext)"/>
        /// and a
        /// <see cref="Antlr4.Runtime.RuleContext"/>
        /// -specific event. First we trigger the generic and then
        /// the rule specific. We to them in reverse order upon finishing the node.
        /// </summary>
        protected internal virtual void EnterRule(IParseTreeListener listener, IRuleNode r)
        {
            ParserRuleContext ctx = (ParserRuleContext)r.RuleContext;
            listener.EnterEveryRule(ctx);
            ctx.EnterRule(listener);
        }

        protected internal virtual void ExitRule(IParseTreeListener listener, IRuleNode r)
        {
            ParserRuleContext ctx = (ParserRuleContext)r.RuleContext;
            ctx.ExitRule(listener);
            listener.ExitEveryRule(ctx);
        }
    }
}
