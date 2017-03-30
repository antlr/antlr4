/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime
{
    /// <summary>A rule invocation record for parsing.</summary>
    /// <remarks>
    /// A rule invocation record for parsing.
    /// Contains all of the information about the current rule not stored in the
    /// RuleContext. It handles parse tree children list, Any ATN state
    /// tracing, and the default values available for rule indications:
    /// start, stop, rule index, current alt number, current
    /// ATN state.
    /// Subclasses made for each rule and grammar track the parameters,
    /// return values, locals, and labels specific to that rule. These
    /// are the objects that are returned from rules.
    /// Note text is not an actual field of a rule return value; it is computed
    /// from start and stop using the input stream's toString() method.  I
    /// could add a ctor to this so that we can pass in and store the input
    /// stream, but I'm not sure we want to do that.  It would seem to be undefined
    /// to get the .text property anyway if the rule matches tokens from multiple
    /// input streams.
    /// I do not use getters for fields of objects that are used simply to
    /// group values such as this aggregate.  The getters/setters are there to
    /// satisfy the superclass interface.
    /// </remarks>
    public class ParserRuleContext : RuleContext
    {
		public static readonly Antlr4.Runtime.ParserRuleContext EMPTY = new Antlr4.Runtime.ParserRuleContext();

        /// <summary>
        /// If we are debugging or building a parse tree for a visitor,
        /// we need to track all of the tokens and rule invocations associated
        /// with this rule's context.
        /// </summary>
        /// <remarks>
        /// If we are debugging or building a parse tree for a visitor,
        /// we need to track all of the tokens and rule invocations associated
        /// with this rule's context. This is empty for parsing w/o tree constr.
        /// operation because we don't the need to track the details about
        /// how we parse this rule.
        /// </remarks>
        public IList<IParseTree> children;

        /// <summary>
        /// For debugging/tracing purposes, we want to track all of the nodes in
        /// the ATN traversed by the parser for a particular rule.
        /// </summary>
        /// <remarks>
        /// For debugging/tracing purposes, we want to track all of the nodes in
        /// the ATN traversed by the parser for a particular rule.
        /// This list indicates the sequence of ATN nodes used to match
        /// the elements of the children list. This list does not include
        /// ATN nodes and other rules used to match rule invocations. It
        /// traces the rule invocation node itself but nothing inside that
        /// other rule's ATN submachine.
        /// There is NOT a one-to-one correspondence between the children and
        /// states list. There are typically many nodes in the ATN traversed
        /// for each element in the children list. For example, for a rule
        /// invocation there is the invoking state and the following state.
        /// The parser setState() method updates field s and adds it to this list
        /// if we are debugging/tracing.
        /// This does not trace states visited during prediction.
        /// </remarks>
        private IToken _start;

        /// <summary>
        /// For debugging/tracing purposes, we want to track all of the nodes in
        /// the ATN traversed by the parser for a particular rule.
        /// </summary>
        /// <remarks>
        /// For debugging/tracing purposes, we want to track all of the nodes in
        /// the ATN traversed by the parser for a particular rule.
        /// This list indicates the sequence of ATN nodes used to match
        /// the elements of the children list. This list does not include
        /// ATN nodes and other rules used to match rule invocations. It
        /// traces the rule invocation node itself but nothing inside that
        /// other rule's ATN submachine.
        /// There is NOT a one-to-one correspondence between the children and
        /// states list. There are typically many nodes in the ATN traversed
        /// for each element in the children list. For example, for a rule
        /// invocation there is the invoking state and the following state.
        /// The parser setState() method updates field s and adds it to this list
        /// if we are debugging/tracing.
        /// This does not trace states visited during prediction.
        /// </remarks>
        private IToken _stop;

        /// <summary>The exception that forced this rule to return.</summary>
        /// <remarks>
        /// The exception that forced this rule to return. If the rule successfully
        /// completed, this is
        /// <see langword="null"/>
        /// .
        /// </remarks>
        public RecognitionException exception;

        public ParserRuleContext()
        {
        }

        public static Antlr4.Runtime.ParserRuleContext EmptyContext
        {
            get
            {
                //	public List<Integer> states;
                return EMPTY;
            }
        }

        /// <summary>
        /// COPY a ctx (I'm deliberately not using copy constructor) to avoid
        /// confusion with creating node with parent. Does not copy children.
        ///
        /// This is used in the generated parser code to flip a generic XContext
        /// node for rule X to a YContext for alt label Y. In that sense, it is
        /// not really a generic copy function.
        ///
        /// If we do an error sync() at start of a rule, we might add error nodes
        /// to the generic XContext so this function must copy those nodes to
        /// the YContext as well else they are lost!
        /// </summary>
        public virtual void CopyFrom(Antlr4.Runtime.ParserRuleContext ctx)
        {
            // from RuleContext
            this.Parent = ctx.Parent;
            this.invokingState = ctx.invokingState;
            this._start = ctx._start;
            this._stop = ctx._stop;

            // copy any error nodes to alt label node
            if (ctx.children != null)
            {
                children = new List<IParseTree>();
                // reset parent pointer for any error nodes
                foreach (var child in ctx.children)
                {
                    var errorChildNode = child as ErrorNodeImpl;
                    if (errorChildNode != null)
                    {
                        children.Add(errorChildNode);
                        errorChildNode.Parent = this;
                    }
                }
            }
        }

        public ParserRuleContext(Antlr4.Runtime.ParserRuleContext parent, int invokingStateNumber)
            : base(parent, invokingStateNumber)
        {
        }

        // Double dispatch methods for listeners
        public virtual void EnterRule(IParseTreeListener listener)
        {
        }

        public virtual void ExitRule(IParseTreeListener listener)
        {
        }

        /// <summary>Does not set parent link; other add methods do that</summary>
        public virtual void AddChild(ITerminalNode t)
        {
            if (children == null)
            {
                children = new List<IParseTree>();
            }
            children.Add(t);
        }

        public virtual void AddChild(RuleContext ruleInvocation)
        {
            if (children == null)
            {
                children = new List<IParseTree>();
            }
            children.Add(ruleInvocation);
        }

        /// <summary>
        /// Used by enterOuterAlt to toss out a RuleContext previously added as
        /// we entered a rule.
        /// </summary>
        /// <remarks>
        /// Used by enterOuterAlt to toss out a RuleContext previously added as
        /// we entered a rule. If we have # label, we will need to remove
        /// generic ruleContext object.
        /// </remarks>
        public virtual void RemoveLastChild()
        {
            if (children != null)
            {
                children.RemoveAt(children.Count - 1);
            }
        }

        //	public void trace(int s) {
        //		if ( states==null ) states = new ArrayList<Integer>();
        //		states.add(s);
        //	}
        public virtual ITerminalNode AddChild(IToken matchedToken)
        {
            TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
            AddChild(t);
            t.Parent = this;
            return t;
        }

        public virtual IErrorNode AddErrorNode(IToken badToken)
        {
            ErrorNodeImpl t = new ErrorNodeImpl(badToken);
            AddChild(t);
            t.Parent = this;
            return t;
        }


        public override IParseTree GetChild(int i)
        {
            return children != null && i >= 0 && i < children.Count ? children[i] : null;
        }

        public virtual T GetChild<T>(int i)
            where T : IParseTree
        {
            if (children == null || i < 0 || i >= children.Count)
            {
                return default(T);
            }
            int j = -1;
            // what element have we found with ctxType?
            foreach (IParseTree o in children)
            {
                if (o is T)
                {
                    j++;
                    if (j == i)
                    {
                        return (T)o;
                    }
                }
            }
            return default(T);
        }

        public virtual ITerminalNode GetToken(int ttype, int i)
        {
            if (children == null || i < 0 || i >= children.Count)
            {
                return null;
            }
            int j = -1;
            // what token with ttype have we found?
            foreach (IParseTree o in children)
            {
                if (o is ITerminalNode)
                {
                    ITerminalNode tnode = (ITerminalNode)o;
                    IToken symbol = tnode.Symbol;
                    if (symbol.Type == ttype)
                    {
                        j++;
                        if (j == i)
                        {
                            return tnode;
                        }
                    }
                }
            }
            return null;
        }

#if (NET45PLUS && !DOTNETCORE)
        public virtual IReadOnlyList<ITerminalNode> GetTokens(int ttype)
#else
        public virtual ITerminalNode[] GetTokens(int ttype)
#endif
        {
            if (children == null)
            {
                return Collections.EmptyList<ITerminalNode>();
            }
            List<ITerminalNode> tokens = null;
            foreach (IParseTree o in children)
            {
                if (o is ITerminalNode)
                {
                    ITerminalNode tnode = (ITerminalNode)o;
                    IToken symbol = tnode.Symbol;
                    if (symbol.Type == ttype)
                    {
                        if (tokens == null)
                        {
                            tokens = new List<ITerminalNode>();
                        }
                        tokens.Add(tnode);
                    }
                }
            }
            if (tokens == null)
            {
                return Collections.EmptyList<ITerminalNode>();
            }
#if (NET45PLUS && !DOTNETCORE)
            return tokens;
#else
            return tokens.ToArray();
#endif
        }

        public virtual T GetRuleContext<T>(int i)
            where T : Antlr4.Runtime.ParserRuleContext
        {
            return GetChild<T>(i);
        }

#if (NET45PLUS && !DOTNETCORE)
        public virtual IReadOnlyList<T> GetRuleContexts<T>()
            where T : Antlr4.Runtime.ParserRuleContext
#else
        public virtual T[] GetRuleContexts<T>()
            where T : Antlr4.Runtime.ParserRuleContext
#endif
        {
            if (children == null)
            {
                return Collections.EmptyList<T>();
            }
            List<T> contexts = null;
            foreach (IParseTree o in children)
            {
                if (o is T)
                {
                    if (contexts == null)
                    {
                        contexts = new List<T>();
                    }
                    contexts.Add((T)o);
                }
            }
            if (contexts == null)
            {
                return Collections.EmptyList<T>();
            }
#if (NET45PLUS && !DOTNETCORE)
            return contexts;
#else
            return contexts.ToArray();
#endif
        }

        public override int ChildCount
        {
            get
            {
                return children != null ? children.Count : 0;
            }
        }

        public override Interval SourceInterval
        {
            get
            {
                if (_start == null || _stop == null)
                {
                    return Interval.Invalid;
                }
                return Interval.Of(_start.TokenIndex, _stop.TokenIndex);
            }
        }

        public virtual IToken Start
        {
            get
            {
                return _start;
            }
			set
			{
				_start = value;
			}
        }

        public virtual IToken Stop
        {
            get
            {
                return _stop;
            }
			set
			{
				_stop = value;
			}
        }

        /// <summary>Used for rule context info debugging during parse-time, not so much for ATN debugging</summary>
        public virtual string ToInfoString(Parser recognizer)
        {
            List<string> rules = new List<string>(recognizer.GetRuleInvocationStack(this));
            rules.Reverse();
            return "ParserRuleContext" + rules + "{" + "start=" + _start + ", stop=" + _stop + '}';
        }
    }
}
