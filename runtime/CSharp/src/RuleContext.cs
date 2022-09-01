/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime
{
    /// <summary>A rule context is a record of a single rule invocation.</summary>
    /// <remarks>
    /// A rule context is a record of a single rule invocation. It knows
    /// which context invoked it, if any. If there is no parent context, then
    /// naturally the invoking state is not valid.  The parent link
    /// provides a chain upwards from the current rule invocation to the root
    /// of the invocation tree, forming a stack. We actually carry no
    /// information about the rule associated with this context (except
    /// when parsing). We keep only the state number of the invoking state from
    /// the ATN submachine that invoked this. Contrast this with the s
    /// pointer inside ParserRuleContext that tracks the current state
    /// being "executed" for the current rule.
    /// The parent contexts are useful for computing lookahead sets and
    /// getting error information.
    /// These objects are used during parsing and prediction.
    /// For the special case of parsers, we use the subclass
    /// ParserRuleContext.
    /// </remarks>
    /// <seealso cref="ParserRuleContext"/>
    public class RuleContext : IRuleNode
    {
        /// <summary>What context invoked this rule?</summary>
        private RuleContext _parent;

        /// <summary>
        /// What state invoked the rule associated with this context?
        /// The "return address" is the followState of invokingState
        /// If parent is null, this should be -1.
        /// </summary>
        /// <remarks>
        /// What state invoked the rule associated with this context?
        /// The "return address" is the followState of invokingState
        /// If parent is null, this should be -1.
        /// </remarks>
        public int invokingState = -1;

        public RuleContext()
        {
        }

        public RuleContext(RuleContext parent, int invokingState)
        {
            _parent = parent;
            //if ( parent!=null ) System.out.println("invoke "+stateNumber+" from "+parent);
            this.invokingState = invokingState;
        }

        public static RuleContext GetChildContext(RuleContext parent, int invokingState)
        {
            return new RuleContext(parent, invokingState);
        }

        public virtual int Depth()
        {
            int n = 0;
            RuleContext p = this;
            while (p != null)
            {
                p = p._parent;
                n++;
            }
            return n;
        }

        /// <summary>
        /// A context is empty if there is no invoking state; meaning nobody call
        /// current context.
        /// </summary>
        /// <remarks>
        /// A context is empty if there is no invoking state; meaning nobody call
        /// current context.
        /// </remarks>
        public virtual bool IsEmpty => invokingState == -1;

        public virtual Interval SourceInterval =>
            // satisfy the ParseTree / SyntaxTree interface
            Interval.Invalid;

        RuleContext IRuleNode.RuleContext => this;

        public virtual RuleContext Parent
        {
            get => _parent;
            set => _parent = value;
        }

        IRuleNode IRuleNode.Parent => Parent;

        IParseTree IParseTree.Parent => Parent;

        ITree ITree.Parent => Parent;

        public virtual RuleContext Payload => this;

        object ITree.Payload => Payload;

        /// <summary>Return the combined text of all child nodes.</summary>
        /// <remarks>
        /// Return the combined text of all child nodes. This method only considers
        /// tokens which have been added to the parse tree.
        /// <p/>
        /// Since tokens on hidden channels (e.g. whitespace or comments) are not
        /// added to the parse trees, they will not appear in the output of this
        /// method.
        /// </remarks>
        public virtual string GetText()
        {
            if (ChildCount == 0)
            {
                return string.Empty;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ChildCount; i++)
            {
                builder.Append(GetChild(i).GetText());
            }
            return builder.ToString();
        }

        public virtual int RuleIndex => -1;

        /* For rule associated with this parse tree internal node, return
	 * the outer alternative number used to match the input. Default
	 * implementation does not compute nor store this alt num. Create
	 * a subclass of ParserRuleContext with backing field and set
	 * option contextSuperClass.
	 * to set it.
	 */
	public virtual int getAltNumber() { return Atn.ATN.INVALID_ALT_NUMBER; }

	/* Set the outer alternative number for this context node. Default
	 * implementation does nothing to avoid backing field overhead for
	 * trees that don't need it.  Create
     * a subclass of ParserRuleContext with backing field and set
     * option contextSuperClass.
	 */
	public virtual void setAltNumber(int altNumber) { }

        public virtual IParseTree GetChild(int i)
        {
            return null;
        }

        ITree ITree.GetChild(int i)
        {
            return GetChild(i);
        }

        public virtual int ChildCount => 0;

        public virtual T Accept<T>(IParseTreeVisitor<T> visitor)
        {
            return visitor.VisitChildren(this);
        }

        /// <summary>
        /// Print out a whole tree, not just a node, in LISP format
        /// (root child1 ..
        /// </summary>
        /// <remarks>
        /// Print out a whole tree, not just a node, in LISP format
        /// (root child1 .. childN). Print just a node if this is a leaf.
        /// We have to know the recognizer so we can get rule names.
        /// </remarks>
        public virtual string ToStringTree(Parser recog)
        {
            return Trees.ToStringTree(this, recog);
        }

        /// <summary>
        /// Print out a whole tree, not just a node, in LISP format
        /// (root child1 ..
        /// </summary>
        /// <remarks>
        /// Print out a whole tree, not just a node, in LISP format
        /// (root child1 .. childN). Print just a node if this is a leaf.
        /// </remarks>
        public virtual string ToStringTree(IList<string> ruleNames)
        {
            return Trees.ToStringTree(this, ruleNames);
        }

        public virtual string ToStringTree()
        {
            return ToStringTree((IList<string>)null);
        }

        public override string ToString()
        {
            return ToString((IList<string>)null, (RuleContext)null);
        }

        public string ToString(IRecognizer recog)
        {
            return ToString(recog, ParserRuleContext.EmptyContext);
        }

        public string ToString(IList<string> ruleNames)
        {
            return ToString(ruleNames, null);
        }

        // recog null unless ParserRuleContext, in which case we use subclass toString(...)
        public virtual string ToString(IRecognizer recog, RuleContext stop)
        {
            return ToString(recog?.RuleNames, stop);
        }

        public virtual string ToString(IList<string> ruleNames, RuleContext stop)
        {
            StringBuilder buf = new StringBuilder();
            RuleContext p = this;
            buf.Append("[");
            while (p != null && p != stop)
            {
                if (ruleNames == null)
                {
                    if (!p.IsEmpty)
                    {
                        buf.Append(p.invokingState);
                    }
                }
                else
                {
                    int ruleIndex = p.RuleIndex;
                    string ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.Count ? ruleNames[ruleIndex] : ruleIndex.ToString();
                    buf.Append(ruleName);
                }
                if (p.Parent != null && (ruleNames != null || !p.Parent.IsEmpty))
                {
                    buf.Append(" ");
                }
                p = p.Parent;
            }
            buf.Append("]");
            return buf.ToString();
        }
    }
}
