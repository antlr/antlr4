/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

namespace Antlr4.Runtime.Tree
{
    public class ParseTreeWalker
    {
        public static readonly ParseTreeWalker Default = new ParseTreeWalker();

        /// <summary>
        /// Performs a walk on the given parse tree starting at the root and going down recursively
	    /// with depth-first search. On each node, 
        /// <see cref="ParseTreeWalker.EnterRule(IParseTreeListener, IRuleNode)"/> is called before
	    /// recursively walking down into child nodes, then
	    /// <see cref="ParseTreeWalker.ExitRule(IParseTreeListener, IRuleNode)"/>
        /// is called after the recursive call to wind up.
        /// </summary>
        /// <param name="listener">The listener used by the walker to process grammar rules</param>
        /// <param name="t">The parse tree to be walked on</param>
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
        /// Enters a grammar rule by first triggering the generic event 
        /// <see cref="IParseTreeListener.EnterEveryRule"/>
	    /// then by triggering the event specific to the given parse tree node
        /// </summary>
        /// <param name="listener"> The listener responding to the trigger events </param>
        /// <param name="r">The grammar rule containing the rule context</param>
        protected internal virtual void EnterRule(IParseTreeListener listener, IRuleNode r)
        {
            ParserRuleContext ctx = (ParserRuleContext)r.RuleContext;
            listener.EnterEveryRule(ctx);
            ctx.EnterRule(listener);
        }

        /// <summary>
        /// Exits a grammar rule by first triggering the event specific to the given parse tree node
	    /// then by triggering the generic event 
        /// <see cref="IParseTreeListener.ExitEveryRule"/>
        /// </summary>
        /// <param name="listener"> The listener responding to the trigger events </param>
        /// <param name="r">The grammar rule containing the rule context</param>
        protected internal virtual void ExitRule(IParseTreeListener listener, IRuleNode r)
        {
            ParserRuleContext ctx = (ParserRuleContext)r.RuleContext;
            ctx.ExitRule(listener);
            listener.ExitEveryRule(ctx);
        }
    }
}
