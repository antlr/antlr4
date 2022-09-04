/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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

	/** A tuple: (ATN state, predicted alt, syntactic, semantic context).
	 *  The syntactic context is a graph-structured stack node whose
	 *  path(s) to the root is the rule invocation(s)
	 *  chain used to arrive at the state.  The semantic context is
	 *  the tree of semantic predicates encountered before reaching
	 *  an ATN state.
	 */
	public class ATNConfig
	{
		/**
		 * This field stores the bit mask for implementing the
		 * {@link #isPrecedenceFilterSuppressed} property as a bit within the
		 * existing {@link #reachesIntoOuterContext} field.
		 */
		private static readonly int SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

		/** The ATN state associated with this configuration */
		public readonly ATNState state;

		/** What alt (or lexer rule) is predicted by this configuration */
		public readonly int alt;

		/** The stack of invoking states leading to the rule/states associated
		 *  with this config.  We track only those contexts pushed during
		 *  execution of the ATN simulator.
		 */
		public PredictionContext context;

		/**
		 * We cannot execute predicates dependent upon local context unless
		 * we know for sure we are in the correct context. Because there is
		 * no way to do this efficiently, we simply cannot evaluate
		 * dependent predicates unless we are in the rule that initially
		 * invokes the ATN simulator.
		 *
		 * <p>
		 * closure() tracks the depth of how far we dip into the outer context:
		 * depth &gt; 0.  Note that it may not be totally accurate depth since I
		 * don't ever decrement. TODO: make it a boolean then</p>
		 *
		 * <p>
		 * For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
		 * is also backed by this field. Since the field is publicly accessible, the
		 * highest bit which would not cause the value to become negative is used to
		 * store this field. This choice minimizes the risk that code which only
		 * compares this value to 0 would be affected by the new purpose of the
		 * flag. It also ensures the performance of the existing {@link ATNConfig}
		 * constructors as well as certain operations like
		 * {@link ATNConfigSet#add(ATNConfig, DoubleKeyMap)} method are
		 * <em>completely</em> unaffected by the change.</p>
		 */
		public int reachesIntoOuterContext;


		public readonly SemanticContext semanticContext;

		public ATNConfig(ATNConfig old)
		{ // dup
			this.state = old.state;
			this.alt = old.alt;
			this.context = old.context;
			this.semanticContext = old.semanticContext;
			this.reachesIntoOuterContext = old.reachesIntoOuterContext;
		}

		public ATNConfig(ATNState state,
						 int alt,
						 PredictionContext context)
			: this(state, alt, context, SemanticContext.Empty.Instance)
		{
		}

		public ATNConfig(ATNState state,
						 int alt,
						 PredictionContext context,
						 SemanticContext semanticContext)
		{
			this.state = state;
			this.alt = alt;
			this.context = context;
			this.semanticContext = semanticContext;
		}

		public ATNConfig(ATNConfig c, ATNState state)
			: this(c, state, c.context, c.semanticContext)
		{
		}

		public ATNConfig(ATNConfig c, ATNState state,
			 SemanticContext semanticContext)
			: this(c, state, c.context, semanticContext)
		{
		}

		public ATNConfig(ATNConfig c,
						 SemanticContext semanticContext)
			: this(c, c.state, c.context, semanticContext)
		{
		}

		public ATNConfig(ATNConfig c, ATNState state,
						 PredictionContext context)
			: this(c, state, context, c.semanticContext)
		{
		}

		public ATNConfig(ATNConfig c, ATNState state,
						 PredictionContext context,
						 SemanticContext semanticContext)
		{
			this.state = state;
			this.alt = c.alt;
			this.context = context;
			this.semanticContext = semanticContext;
			this.reachesIntoOuterContext = c.reachesIntoOuterContext;
		}

		/**
		 * This method gets the value of the {@link #reachesIntoOuterContext} field
		 * as it existed prior to the introduction of the
		 * {@link #isPrecedenceFilterSuppressed} method.
		 */
		public int OuterContextDepth
		{
			get
			{
				return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
			}
		}

		public bool IsPrecedenceFilterSuppressed
		{
			get
			{
				return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
			}
		}

		public void SetPrecedenceFilterSuppressed(bool value)
		{
			if (value)
			{
				this.reachesIntoOuterContext |= SUPPRESS_PRECEDENCE_FILTER;
			}
			else {
				this.reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
			}
		}

		/** An ATN configuration is equal to another if both have
		 *  the same state, they predict the same alternative, and
		 *  syntactic/semantic contexts are the same.
		 */
		public override bool Equals(Object o)
		{
			if (!(o is ATNConfig)) {
				return false;
			}

			return this.Equals((ATNConfig)o);
		}

		public virtual bool Equals(ATNConfig other)
		{
			if (this == other)
			{
				return true;
			}
			else if (other == null)
			{
				return false;
			}

			return this.state.stateNumber == other.state.stateNumber
				&& this.alt == other.alt
				&& (this.context == other.context || (this.context != null && this.context.Equals(other.context)))
				&& this.semanticContext.Equals(other.semanticContext)
				&& this.IsPrecedenceFilterSuppressed == other.IsPrecedenceFilterSuppressed;
		}

		public override int GetHashCode()
		{
			int hashCode = MurmurHash.Initialize(7);
			hashCode = MurmurHash.Update(hashCode, state.stateNumber);
			hashCode = MurmurHash.Update(hashCode, alt);
			hashCode = MurmurHash.Update(hashCode, context);
			hashCode = MurmurHash.Update(hashCode, semanticContext);
			hashCode = MurmurHash.Finish(hashCode, 4);
			return hashCode;
		}

		public override String ToString()
		{
			return ToString(null, true);
		}

		public String ToString(IRecognizer recog, bool showAlt)
		{
			StringBuilder buf = new StringBuilder();
			//		if ( state.ruleIndex>=0 ) {
			//			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
			//			else buf.append(state.ruleIndex+":");
			//		}
			buf.Append('(');
			buf.Append(state);
			if (showAlt)
			{
				buf.Append(",");
				buf.Append(alt);
			}
			if (context != null)
			{
				buf.Append(",[");
				buf.Append(context.ToString());
				buf.Append("]");
			}
			if (semanticContext != null && semanticContext != SemanticContext.Empty.Instance)
			{
				buf.Append(",");
				buf.Append(semanticContext);
			}
			if (OuterContextDepth > 0)
			{
				buf.Append(",up=").Append(OuterContextDepth);
			}
			buf.Append(')');
			return buf.ToString();
		}
	}

}
