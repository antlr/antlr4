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
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>A tuple: (ATN state, predicted alt, syntactic, semantic context).</summary>
    /// <remarks>
    /// A tuple: (ATN state, predicted alt, syntactic, semantic context).
    /// The syntactic context is a graph-structured stack node whose
    /// path(s) to the root is the rule invocation(s)
    /// chain used to arrive at the state.  The semantic context is
    /// the tree of semantic predicates encountered before reaching
    /// an ATN state.
    /// </remarks>
    public class ATNConfig
    {
        /// <summary>The ATN state associated with this configuration</summary>
        [NotNull]
        private readonly ATNState state;

        private int altAndOuterContextDepth;

        /// <summary>
        /// The stack of invoking states leading to the rule/states associated
        /// with this config.
        /// </summary>
        /// <remarks>
        /// The stack of invoking states leading to the rule/states associated
        /// with this config.  We track only those contexts pushed during
        /// execution of the ATN simulator.
        /// </remarks>
        [NotNull]
        private PredictionContext context;

        protected internal ATNConfig(ATNState state, int alt, PredictionContext context)
        {
            System.Diagnostics.Debug.Assert((alt & unchecked((int)(0xFFFFFF))) == alt);
            this.state = state;
            this.altAndOuterContextDepth = alt & unchecked((int)(0x7FFFFFFF));
            this.context = context;
        }

        protected internal ATNConfig(Antlr4.Runtime.Atn.ATNConfig c, ATNState state, PredictionContext
             context)
        {
            this.state = state;
            this.altAndOuterContextDepth = c.altAndOuterContextDepth & unchecked((int)(0x7FFFFFFF
                ));
            this.context = context;
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext
             context)
        {
            return Create(state, alt, context, SemanticContext.None, -1);
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext
             context, SemanticContext semanticContext)
        {
            return Create(state, alt, context, semanticContext, -1);
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext
             context, SemanticContext semanticContext, int actionIndex)
        {
            if (semanticContext != SemanticContext.None)
            {
                if (actionIndex != -1)
                {
                    return new ATNConfig.ActionSemanticContextATNConfig(actionIndex, semanticContext, 
                        state, alt, context);
                }
                else
                {
                    return new ATNConfig.SemanticContextATNConfig(semanticContext, state, alt, context
                        );
                }
            }
            else
            {
                if (actionIndex != -1)
                {
                    return new ATNConfig.ActionATNConfig(actionIndex, state, alt, context);
                }
                else
                {
                    return new Antlr4.Runtime.Atn.ATNConfig(state, alt, context);
                }
            }
        }

        /// <summary>Gets the ATN state associated with this configuration</summary>
        [NotNull]
        public ATNState GetState()
        {
            return state;
        }

        /// <summary>What alt (or lexer rule) is predicted by this configuration</summary>
        public int GetAlt()
        {
            return altAndOuterContextDepth & unchecked((int)(0x00FFFFFF));
        }

        public bool IsHidden()
        {
            return altAndOuterContextDepth < 0;
        }

        public virtual void SetHidden(bool value)
        {
            if (value)
            {
                altAndOuterContextDepth |= unchecked((int)(0x80000000));
            }
            else
            {
                altAndOuterContextDepth &= ~unchecked((int)(0x80000000));
            }
        }

        [NotNull]
        public PredictionContext GetContext()
        {
            return context;
        }

        public virtual void SetContext(PredictionContext context)
        {
            this.context = context;
        }

        public bool GetReachesIntoOuterContext()
        {
            return GetOuterContextDepth() != 0;
        }

        /// <summary>
        /// We cannot execute predicates dependent upon local context unless
        /// we know for sure we are in the correct context.
        /// </summary>
        /// <remarks>
        /// We cannot execute predicates dependent upon local context unless
        /// we know for sure we are in the correct context. Because there is
        /// no way to do this efficiently, we simply cannot evaluate
        /// dependent predicates unless we are in the rule that initially
        /// invokes the ATN simulator.
        /// closure() tracks the depth of how far we dip into the
        /// outer context: depth &gt; 0.  Note that it may not be totally
        /// accurate depth since I don't ever decrement. TODO: make it a boolean then
        /// </remarks>
        public int GetOuterContextDepth()
        {
            return ((int)(((uint)altAndOuterContextDepth) >> 24)) & unchecked((int)(0x7F));
        }

        public virtual void SetOuterContextDepth(int outerContextDepth)
        {
            System.Diagnostics.Debug.Assert(outerContextDepth >= 0);
            // saturate at 0x7F - everything but zero/positive is only used for debug information anyway
            outerContextDepth = Math.Min(outerContextDepth, unchecked((int)(0x7F)));
            this.altAndOuterContextDepth = (outerContextDepth << 24) | (altAndOuterContextDepth
                 & ~unchecked((int)(0x7F000000)));
        }

        public virtual int GetActionIndex()
        {
            return -1;
        }

        [NotNull]
        public virtual SemanticContext GetSemanticContext()
        {
            return SemanticContext.None;
        }

        public Antlr4.Runtime.Atn.ATNConfig Clone()
        {
            return Transform(this.GetState());
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state)
        {
            return Transform(state, this.context, this.GetSemanticContext(), this.GetActionIndex
                ());
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, SemanticContext semanticContext
            )
        {
            return Transform(state, this.context, semanticContext, this.GetActionIndex());
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, PredictionContext context
            )
        {
            return Transform(state, context, this.GetSemanticContext(), this.GetActionIndex()
                );
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, int actionIndex)
        {
            return Transform(state, context, this.GetSemanticContext(), actionIndex);
        }

        private Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, PredictionContext 
            context, SemanticContext semanticContext, int actionIndex)
        {
            if (semanticContext != SemanticContext.None)
            {
                if (actionIndex != -1)
                {
                    return new ATNConfig.ActionSemanticContextATNConfig(actionIndex, semanticContext, 
                        this, state, context);
                }
                else
                {
                    return new ATNConfig.SemanticContextATNConfig(semanticContext, this, state, context
                        );
                }
            }
            else
            {
                if (actionIndex != -1)
                {
                    return new ATNConfig.ActionATNConfig(actionIndex, this, state, context);
                }
                else
                {
                    return new Antlr4.Runtime.Atn.ATNConfig(this, state, context);
                }
            }
        }

        public virtual Antlr4.Runtime.Atn.ATNConfig AppendContext(int context, PredictionContextCache
             contextCache)
        {
            PredictionContext appendedContext = GetContext().AppendContext(context, contextCache
                );
            Antlr4.Runtime.Atn.ATNConfig result = Transform(GetState(), appendedContext);
            return result;
        }

        public virtual Antlr4.Runtime.Atn.ATNConfig AppendContext(PredictionContext context
            , PredictionContextCache contextCache)
        {
            PredictionContext appendedContext = GetContext().AppendContext(context, contextCache
                );
            Antlr4.Runtime.Atn.ATNConfig result = Transform(GetState(), appendedContext);
            return result;
        }

        public virtual bool Contains(Antlr4.Runtime.Atn.ATNConfig subconfig)
        {
            if (this.GetState().stateNumber != subconfig.GetState().stateNumber || this.GetAlt
                () != subconfig.GetAlt() || !this.GetSemanticContext().Equals(subconfig.GetSemanticContext
                ()))
            {
                return false;
            }
            IDeque<PredictionContext> leftWorkList = new ArrayDeque<PredictionContext>();
            IDeque<PredictionContext> rightWorkList = new ArrayDeque<PredictionContext>();
            leftWorkList.AddItem(GetContext());
            rightWorkList.AddItem(subconfig.GetContext());
            while (!leftWorkList.IsEmpty())
            {
                PredictionContext left = leftWorkList.Pop();
                PredictionContext right = rightWorkList.Pop();
                if (left == right)
                {
                    return true;
                }
                if (left.Size < right.Size)
                {
                    return false;
                }
                if (right.IsEmpty)
                {
                    return left.HasEmpty;
                }
                else
                {
                    for (int i = 0; i < right.Size; i++)
                    {
                        int index = left.FindReturnState(right.GetReturnState(i));
                        if (index < 0)
                        {
                            // assumes invokingStates has no duplicate entries
                            return false;
                        }
                        leftWorkList.Push(left.GetParent(index));
                        rightWorkList.Push(right.GetParent(i));
                    }
                }
            }
            return false;
        }

        /// <summary>
        /// An ATN configuration is equal to another if both have
        /// the same state, they predict the same alternative, and
        /// syntactic/semantic contexts are the same.
        /// </summary>
        /// <remarks>
        /// An ATN configuration is equal to another if both have
        /// the same state, they predict the same alternative, and
        /// syntactic/semantic contexts are the same.
        /// </remarks>
        public override bool Equals(object o)
        {
            if (!(o is Antlr4.Runtime.Atn.ATNConfig))
            {
                return false;
            }
            return this.Equals((Antlr4.Runtime.Atn.ATNConfig)o);
        }

        public virtual bool Equals(Antlr4.Runtime.Atn.ATNConfig other)
        {
            if (this == other)
            {
                return true;
            }
            else
            {
                if (other == null)
                {
                    return false;
                }
            }
            return this.GetState().stateNumber == other.GetState().stateNumber && this.GetAlt
                () == other.GetAlt() && this.GetReachesIntoOuterContext() == other.GetReachesIntoOuterContext
                () && this.GetContext().Equals(other.GetContext()) && this.GetSemanticContext
                ().Equals(other.GetSemanticContext()) && this.GetActionIndex() == other.GetActionIndex
                ();
        }

        public override int GetHashCode()
        {
            int hashCode = 7;
            hashCode = 5 * hashCode + GetState().stateNumber;
            hashCode = 5 * hashCode + GetAlt();
            hashCode = 5 * hashCode + (GetReachesIntoOuterContext() ? 1 : 0);
            hashCode = 5 * hashCode + (GetContext() != null ? GetContext().GetHashCode() : 0);
            hashCode = 5 * hashCode + GetSemanticContext().GetHashCode();
            return hashCode;
        }

        public virtual string ToDotString()
        {
            StringBuilder builder = new StringBuilder();
            builder.Append("digraph G {\n");
            builder.Append("rankdir=LR;\n");
            IDictionary<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext
                , PredictionContext>();
            IDeque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
            workList.AddItem(GetContext());
            visited.Put(GetContext(), GetContext());
            while (!workList.IsEmpty())
            {
                PredictionContext current = workList.Pop();
                for (int i = 0; i < current.Size; i++)
                {
                    builder.Append("  s").Append(Sharpen.Runtime.IdentityHashCode(current));
                    builder.Append("->");
                    builder.Append("s").Append(Sharpen.Runtime.IdentityHashCode(current.GetParent(i))
                        );
                    builder.Append("[label=\"").Append(current.GetReturnState(i)).Append("\"];\n");
                    if (visited.Put(current.GetParent(i), current.GetParent(i)) == null)
                    {
                        workList.Push(current.GetParent(i));
                    }
                }
            }
            builder.Append("}\n");
            return builder.ToString();
        }

        public override string ToString()
        {
            return ToString(null, true, false);
        }

        public virtual string ToString<_T0>(Recognizer<_T0> recog, bool showAlt)
        {
            return ToString(recog, showAlt, true);
        }

        public virtual string ToString<_T0>(Recognizer<_T0> recog, bool showAlt, bool showContext
            )
        {
            StringBuilder buf = new StringBuilder();
            //		if ( state.ruleIndex>=0 ) {
            //			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
            //			else buf.append(state.ruleIndex+":");
            //		}
            string[] contexts;
            if (showContext)
            {
                contexts = GetContext().ToStrings(recog, this.GetState().stateNumber);
            }
            else
            {
                contexts = new string[] { "?" };
            }
            bool first = true;
            foreach (string contextDesc in contexts)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    buf.Append(", ");
                }
                buf.Append('(');
                buf.Append(GetState());
                if (showAlt)
                {
                    buf.Append(",");
                    buf.Append(GetAlt());
                }
                if (GetContext() != null)
                {
                    buf.Append(",");
                    buf.Append(contextDesc);
                }
                if (GetSemanticContext() != null && GetSemanticContext() != SemanticContext.None)
                {
                    buf.Append(",");
                    buf.Append(GetSemanticContext());
                }
                if (GetReachesIntoOuterContext())
                {
                    buf.Append(",up=").Append(GetOuterContextDepth());
                }
                buf.Append(')');
            }
            return buf.ToString();
        }

        private class SemanticContextATNConfig : ATNConfig
        {
            [NotNull]
            private readonly SemanticContext semanticContext;

            public SemanticContextATNConfig(SemanticContext semanticContext, ATNState state, 
                int alt, PredictionContext context) : base(state, alt, context)
            {
                this.semanticContext = semanticContext;
            }

            public SemanticContextATNConfig(SemanticContext semanticContext, ATNConfig c, ATNState
                 state, PredictionContext context) : base(c, state, context)
            {
                this.semanticContext = semanticContext;
            }

            public override SemanticContext GetSemanticContext()
            {
                return semanticContext;
            }
        }

        private class ActionATNConfig : ATNConfig
        {
            private readonly int actionIndex;

            public ActionATNConfig(int actionIndex, ATNState state, int alt, PredictionContext
                 context) : base(state, alt, context)
            {
                this.actionIndex = actionIndex;
            }

            protected internal ActionATNConfig(int actionIndex, ATNConfig c, ATNState state, 
                PredictionContext context) : base(c, state, context)
            {
                if (c.GetSemanticContext() != SemanticContext.None)
                {
                    throw new NotSupportedException();
                }
                this.actionIndex = actionIndex;
            }

            public override int GetActionIndex()
            {
                return actionIndex;
            }
        }

        private class ActionSemanticContextATNConfig : ATNConfig.SemanticContextATNConfig
        {
            private readonly int actionIndex;

            public ActionSemanticContextATNConfig(int actionIndex, SemanticContext semanticContext
                , ATNState state, int alt, PredictionContext context) : base(semanticContext, 
                state, alt, context)
            {
                this.actionIndex = actionIndex;
            }

            public ActionSemanticContextATNConfig(int actionIndex, SemanticContext semanticContext
                , ATNConfig c, ATNState state, PredictionContext context) : base(semanticContext
                , c, state, context)
            {
                this.actionIndex = actionIndex;
            }

            public override int GetActionIndex()
            {
                return actionIndex;
            }
        }
    }
}
