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
using Antlr4.Runtime.Sharpen;

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
        /// <summary>
        /// This field stores the bit mask for implementing the
        /// <see cref="PrecedenceFilterSuppressed()"/>
        /// property as a bit within the
        /// existing
        /// <see cref="altAndOuterContextDepth"/>
        /// field.
        /// </summary>
        private const int SuppressPrecedenceFilter = unchecked((int)(0x80000000));

        /// <summary>The ATN state associated with this configuration</summary>
        [NotNull]
        private readonly ATNState state;

        /// <summary>This is a bit-field currently containing the following values.</summary>
        /// <remarks>
        /// This is a bit-field currently containing the following values.
        /// <ul>
        /// <li>0x00FFFFFF: Alternative</li>
        /// <li>0x7F000000: Outer context depth</li>
        /// <li>0x80000000: Suppress precedence filter</li>
        /// </ul>
        /// </remarks>
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
            this.altAndOuterContextDepth = alt;
            this.context = context;
        }

        protected internal ATNConfig(Antlr4.Runtime.Atn.ATNConfig c, ATNState state, PredictionContext context)
        {
            this.state = state;
            this.altAndOuterContextDepth = c.altAndOuterContextDepth;
            this.context = context;
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext context)
        {
            return Create(state, alt, context, Antlr4.Runtime.Atn.SemanticContext.None, null);
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext context, Antlr4.Runtime.Atn.SemanticContext semanticContext)
        {
            return Create(state, alt, context, semanticContext, null);
        }

        public static Antlr4.Runtime.Atn.ATNConfig Create(ATNState state, int alt, PredictionContext context, Antlr4.Runtime.Atn.SemanticContext semanticContext, LexerActionExecutor lexerActionExecutor)
        {
            if (semanticContext != Antlr4.Runtime.Atn.SemanticContext.None)
            {
                if (lexerActionExecutor != null)
                {
                    return new ATNConfig.ActionSemanticContextATNConfig(lexerActionExecutor, semanticContext, state, alt, context, false);
                }
                else
                {
                    return new ATNConfig.SemanticContextATNConfig(semanticContext, state, alt, context);
                }
            }
            else
            {
                if (lexerActionExecutor != null)
                {
                    return new ATNConfig.ActionATNConfig(lexerActionExecutor, state, alt, context, false);
                }
                else
                {
                    return new Antlr4.Runtime.Atn.ATNConfig(state, alt, context);
                }
            }
        }

        /// <summary>Gets the ATN state associated with this configuration.</summary>
        /// <remarks>Gets the ATN state associated with this configuration.</remarks>
        public ATNState State
        {
            get
            {
                return state;
            }
        }

        /// <summary>What alt (or lexer rule) is predicted by this configuration.</summary>
        /// <remarks>What alt (or lexer rule) is predicted by this configuration.</remarks>
        public int Alt
        {
            get
            {
                return altAndOuterContextDepth & unchecked((int)(0x00FFFFFF));
            }
        }

        public virtual PredictionContext Context
        {
            get
            {
                return context;
            }
            set
            {
                PredictionContext context = value;
                this.context = context;
            }
        }

        public bool ReachesIntoOuterContext
        {
            get
            {
                return OuterContextDepth != 0;
            }
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
        /// <p>
        /// closure() tracks the depth of how far we dip into the outer context:
        /// depth &gt; 0.  Note that it may not be totally accurate depth since I
        /// don't ever decrement. TODO: make it a boolean then</p>
        /// </remarks>
        public virtual int OuterContextDepth
        {
            get
            {
                return ((int)(((uint)altAndOuterContextDepth) >> 24)) & unchecked((int)(0x7F));
            }
            set
            {
                int outerContextDepth = value;
                System.Diagnostics.Debug.Assert(outerContextDepth >= 0);
                // saturate at 0x7F - everything but zero/positive is only used for debug information anyway
                outerContextDepth = Math.Min(outerContextDepth, unchecked((int)(0x7F)));
                this.altAndOuterContextDepth = (outerContextDepth << 24) | (altAndOuterContextDepth & ~unchecked((int)(0x7F000000)));
            }
        }

        public virtual LexerActionExecutor ActionExecutor
        {
            get
            {
                return null;
            }
        }

        public virtual Antlr4.Runtime.Atn.SemanticContext SemanticContext
        {
            get
            {
                return Antlr4.Runtime.Atn.SemanticContext.None;
            }
        }

        public virtual bool PassedThroughNonGreedyDecision
        {
            get
            {
                return false;
            }
        }

        public Antlr4.Runtime.Atn.ATNConfig Clone()
        {
            return Transform(this.State, false);
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, bool checkNonGreedy)
        {
            return Transform(state, this.context, this.SemanticContext, checkNonGreedy, this.ActionExecutor);
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, Antlr4.Runtime.Atn.SemanticContext semanticContext, bool checkNonGreedy)
        {
            return Transform(state, this.context, semanticContext, checkNonGreedy, this.ActionExecutor);
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, PredictionContext context, bool checkNonGreedy)
        {
            return Transform(state, context, this.SemanticContext, checkNonGreedy, this.ActionExecutor);
        }

        public Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, LexerActionExecutor lexerActionExecutor, bool checkNonGreedy)
        {
            return Transform(state, context, this.SemanticContext, checkNonGreedy, lexerActionExecutor);
        }

        private Antlr4.Runtime.Atn.ATNConfig Transform(ATNState state, PredictionContext context, Antlr4.Runtime.Atn.SemanticContext semanticContext, bool checkNonGreedy, LexerActionExecutor lexerActionExecutor)
        {
            bool passedThroughNonGreedy = checkNonGreedy && CheckNonGreedyDecision(this, state);
            if (semanticContext != Antlr4.Runtime.Atn.SemanticContext.None)
            {
                if (lexerActionExecutor != null || passedThroughNonGreedy)
                {
                    return new ATNConfig.ActionSemanticContextATNConfig(lexerActionExecutor, semanticContext, this, state, context, passedThroughNonGreedy);
                }
                else
                {
                    return new ATNConfig.SemanticContextATNConfig(semanticContext, this, state, context);
                }
            }
            else
            {
                if (lexerActionExecutor != null || passedThroughNonGreedy)
                {
                    return new ATNConfig.ActionATNConfig(lexerActionExecutor, this, state, context, passedThroughNonGreedy);
                }
                else
                {
                    return new Antlr4.Runtime.Atn.ATNConfig(this, state, context);
                }
            }
        }

        private static bool CheckNonGreedyDecision(Antlr4.Runtime.Atn.ATNConfig source, ATNState target)
        {
            return source.PassedThroughNonGreedyDecision || target is DecisionState && ((DecisionState)target).nonGreedy;
        }

        public virtual Antlr4.Runtime.Atn.ATNConfig AppendContext(int context, PredictionContextCache contextCache)
        {
            PredictionContext appendedContext = Context.AppendContext(context, contextCache);
            Antlr4.Runtime.Atn.ATNConfig result = Transform(State, appendedContext, false);
            return result;
        }

        public virtual Antlr4.Runtime.Atn.ATNConfig AppendContext(PredictionContext context, PredictionContextCache contextCache)
        {
            PredictionContext appendedContext = Context.AppendContext(context, contextCache);
            Antlr4.Runtime.Atn.ATNConfig result = Transform(State, appendedContext, false);
            return result;
        }

        public virtual bool Contains(Antlr4.Runtime.Atn.ATNConfig subconfig)
        {
            if (this.state.stateNumber != subconfig.State.stateNumber || this.Alt != subconfig.Alt || !this.SemanticContext.Equals(subconfig.SemanticContext))
            {
                return false;
            }
            Stack<PredictionContext> leftWorkList = new Stack<PredictionContext>();
            Stack<PredictionContext> rightWorkList = new Stack<PredictionContext>();
            leftWorkList.Push(Context);
            rightWorkList.Push(subconfig.Context);
            while (leftWorkList.Count > 0)
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

        public bool PrecedenceFilterSuppressed
        {
            get
            {
                return (altAndOuterContextDepth & SuppressPrecedenceFilter) != 0;
            }
            set
            {
                if (value)
                {
                    this.altAndOuterContextDepth |= SuppressPrecedenceFilter;
                }
                else
                {
                    this.altAndOuterContextDepth &= ~SuppressPrecedenceFilter;
                }
            }
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
            return this.State.stateNumber == other.State.stateNumber && this.Alt == other.Alt && this.ReachesIntoOuterContext == other.ReachesIntoOuterContext && this.Context.Equals(other.Context) && this.SemanticContext.Equals(other.SemanticContext) && this.PrecedenceFilterSuppressed == other.PrecedenceFilterSuppressed && this.PassedThroughNonGreedyDecision == other.PassedThroughNonGreedyDecision && EqualityComparer<LexerActionExecutor>.Default.Equals(this.ActionExecutor, other.ActionExecutor);
        }

        public override int GetHashCode()
        {
            int hashCode = MurmurHash.Initialize(7);
            hashCode = MurmurHash.Update(hashCode, State.stateNumber);
            hashCode = MurmurHash.Update(hashCode, Alt);
            hashCode = MurmurHash.Update(hashCode, ReachesIntoOuterContext ? 1 : 0);
            hashCode = MurmurHash.Update(hashCode, Context);
            hashCode = MurmurHash.Update(hashCode, SemanticContext);
            hashCode = MurmurHash.Update(hashCode, PassedThroughNonGreedyDecision ? 1 : 0);
            hashCode = MurmurHash.Update(hashCode, ActionExecutor);
            hashCode = MurmurHash.Finish(hashCode, 7);
            return hashCode;
        }

        public virtual string ToDotString()
        {
#if COMPACT
            throw new NotImplementedException("The current platform does not provide RuntimeHelpers.GetHashCode(object).");
#else
            StringBuilder builder = new StringBuilder();
            builder.Append("digraph G {\n");
            builder.Append("rankdir=LR;\n");
            HashSet<PredictionContext> visited = new HashSet<PredictionContext>();
            Stack<PredictionContext> workList = new Stack<PredictionContext>();
            workList.Push(Context);
            visited.Add(Context);
            while (workList.Count > 0)
            {
                PredictionContext current = workList.Pop();
                for (int i = 0; i < current.Size; i++)
                {
                    builder.Append("  s").Append(System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(current));
                    builder.Append("->");
                    builder.Append("s").Append(System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(current.GetParent(i)));
                    builder.Append("[label=\"").Append(current.GetReturnState(i)).Append("\"];\n");
                    if (visited.Add(current.GetParent(i)))
                    {
                        workList.Push(current.GetParent(i));
                    }
                }
            }
            builder.Append("}\n");
            return builder.ToString();
#endif
        }

        public override string ToString()
        {
            return ToString(null, true, false);
        }

        public virtual string ToString(IRecognizer recog, bool showAlt)
        {
            return ToString(recog, showAlt, true);
        }

        public virtual string ToString(IRecognizer recog, bool showAlt, bool showContext)
        {
            StringBuilder buf = new StringBuilder();
            //		if ( state.ruleIndex>=0 ) {
            //			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
            //			else buf.append(state.ruleIndex+":");
            //		}
            string[] contexts;
            if (showContext)
            {
                contexts = Context.ToStrings(recog, this.State.stateNumber);
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
                buf.Append(State);
                if (showAlt)
                {
                    buf.Append(",");
                    buf.Append(Alt);
                }
                if (Context != null)
                {
                    buf.Append(",");
                    buf.Append(contextDesc);
                }
                if (SemanticContext != null && SemanticContext != Antlr4.Runtime.Atn.SemanticContext.None)
                {
                    buf.Append(",");
                    buf.Append(SemanticContext);
                }
                if (ReachesIntoOuterContext)
                {
                    buf.Append(",up=").Append(OuterContextDepth);
                }
                buf.Append(')');
            }
            return buf.ToString();
        }

        private class SemanticContextATNConfig : ATNConfig
        {
            [NotNull]
            private readonly Antlr4.Runtime.Atn.SemanticContext semanticContext;

            public SemanticContextATNConfig(Antlr4.Runtime.Atn.SemanticContext semanticContext, ATNState state, int alt, PredictionContext context)
                : base(state, alt, context)
            {
                this.semanticContext = semanticContext;
            }

            public SemanticContextATNConfig(Antlr4.Runtime.Atn.SemanticContext semanticContext, ATNConfig c, ATNState state, PredictionContext context)
                : base(c, state, context)
            {
                this.semanticContext = semanticContext;
            }

            public override Antlr4.Runtime.Atn.SemanticContext SemanticContext
            {
                get
                {
                    return semanticContext;
                }
            }
        }

        private class ActionATNConfig : ATNConfig
        {
            private readonly LexerActionExecutor lexerActionExecutor;

            private readonly bool passedThroughNonGreedyDecision;

            public ActionATNConfig(LexerActionExecutor lexerActionExecutor, ATNState state, int alt, PredictionContext context, bool passedThroughNonGreedyDecision)
                : base(state, alt, context)
            {
                this.lexerActionExecutor = lexerActionExecutor;
                this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
            }

            protected internal ActionATNConfig(LexerActionExecutor lexerActionExecutor, ATNConfig c, ATNState state, PredictionContext context, bool passedThroughNonGreedyDecision)
                : base(c, state, context)
            {
                if (c.SemanticContext != SemanticContext.None)
                {
                    throw new NotSupportedException();
                }
                this.lexerActionExecutor = lexerActionExecutor;
                this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
            }

            public override LexerActionExecutor ActionExecutor
            {
                get
                {
                    return lexerActionExecutor;
                }
            }

            public override bool PassedThroughNonGreedyDecision
            {
                get
                {
                    return passedThroughNonGreedyDecision;
                }
            }
        }

        private class ActionSemanticContextATNConfig : ATNConfig.SemanticContextATNConfig
        {
            private readonly LexerActionExecutor lexerActionExecutor;

            private readonly bool passedThroughNonGreedyDecision;

            public ActionSemanticContextATNConfig(LexerActionExecutor lexerActionExecutor, SemanticContext semanticContext, ATNState state, int alt, PredictionContext context, bool passedThroughNonGreedyDecision)
                : base(semanticContext, state, alt, context)
            {
                this.lexerActionExecutor = lexerActionExecutor;
                this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
            }

            public ActionSemanticContextATNConfig(LexerActionExecutor lexerActionExecutor, SemanticContext semanticContext, ATNConfig c, ATNState state, PredictionContext context, bool passedThroughNonGreedyDecision)
                : base(semanticContext, c, state, context)
            {
                this.lexerActionExecutor = lexerActionExecutor;
                this.passedThroughNonGreedyDecision = passedThroughNonGreedyDecision;
            }

            public override LexerActionExecutor ActionExecutor
            {
                get
                {
                    return lexerActionExecutor;
                }
            }

            public override bool PassedThroughNonGreedyDecision
            {
                get
                {
                    return passedThroughNonGreedyDecision;
                }
            }
        }
    }
}
