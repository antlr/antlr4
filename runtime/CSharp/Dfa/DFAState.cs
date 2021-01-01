/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
	/// <summary>A DFA state represents a set of possible ATN configurations.</summary>
	/// <remarks>
	/// A DFA state represents a set of possible ATN configurations.
	/// As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
	/// to keep track of all possible states the ATN can be in after
	/// reading each input symbol.  That is to say, after reading
	/// input a1a2..an, the DFA is in a state that represents the
	/// subset T of the states of the ATN that are reachable from the
	/// ATN's start state along some path labeled a1a2..an."
	/// In conventional NFA&#x2192;DFA conversion, therefore, the subset T
	/// would be a bitset representing the set of states the
	/// ATN could be in.  We need to track the alt predicted by each
	/// state as well, however.  More importantly, we need to maintain
	/// a stack of states, tracking the closure operations as they
	/// jump from rule to rule, emulating rule invocations (method calls).
	/// I have to add a stack to simulate the proper lookahead sequences for
	/// the underlying LL grammar from which the ATN was derived.
	/// <p>I use a set of ATNConfig objects not simple states.  An ATNConfig
	/// is both a state (ala normal conversion) and a RuleContext describing
	/// the chain of rules (if any) followed to arrive at that state.</p>
	/// <p>A DFA state may have multiple references to a particular state,
	/// but with different ATN contexts (with same or different alts)
	/// meaning that state was reached via a different set of rule invocations.</p>
	/// </remarks>
	public class DFAState
	{
		public int stateNumber = -1;


		public ATNConfigSet configSet = new ATNConfigSet();

		/** {@code edges[symbol]} points to target of symbol. Shift up by 1 so (-1)
		 *  {@link Token#EOF} maps to {@code edges[0]}.
		 */

		public DFAState[] edges;

		public bool isAcceptState = false;

		/** if accept state, what ttype do we match or alt do we predict?
		 *  This is set to {@link ATN#INVALID_ALT_NUMBER} when {@link #predicates}{@code !=null} or
		 *  {@link #requiresFullContext}.
		 */
		public int prediction;

		public LexerActionExecutor lexerActionExecutor;

		/**
		 * Indicates that this state was created during SLL prediction that
		 * discovered a conflict between the configurations in the state. Future
		 * {@link ParserATNSimulator#execATN} invocations immediately jumped doing
		 * full context prediction if this field is true.
		 */
		public bool requiresFullContext;

		/** During SLL parsing, this is a list of predicates associated with the
		 *  ATN configurations of the DFA state. When we have predicates,
		 *  {@link #requiresFullContext} is {@code false} since full context prediction evaluates predicates
		 *  on-the-fly. If this is not null, then {@link #prediction} is
		 *  {@link ATN#INVALID_ALT_NUMBER}.
		 *
		 *  <p>We only use these for non-{@link #requiresFullContext} but conflicting states. That
		 *  means we know from the context (it's $ or we don't dip into outer
		 *  context) that it's an ambiguity not a conflict.</p>
		 *
		 *  <p>This list is computed by {@link ParserATNSimulator#predicateDFAState}.</p>
		 */

		public PredPrediction[] predicates;



		public DFAState() { }

		public DFAState(int stateNumber) { this.stateNumber = stateNumber; }

		public DFAState(ATNConfigSet configs) { this.configSet = configs; }

		/** Get the set of all alts mentioned by all ATN configurations in this
		 *  DFA state.
		 */
		public HashSet<int> getAltSet()
		{
			HashSet<int> alts = new HashSet<int>();
			if (configSet != null)
			{
				foreach (ATNConfig c in configSet.configs)
				{
					alts.Add(c.alt);
				}
			}
			if (alts.Count==0)
				return null;
			return alts;
		}

		public override int GetHashCode()
		{
			int hash = MurmurHash.Initialize(7);
			hash = MurmurHash.Update(hash, configSet.GetHashCode());
			hash = MurmurHash.Finish(hash, 1);
			return hash;
		}

		/**
		 * Two {@link DFAState} instances are equal if their ATN configuration sets
		 * are the same. This method is used to see if a state already exists.
		 *
		 * <p>Because the number of alternatives and number of ATN configurations are
		 * finite, there is a finite number of DFA states that can be processed.
		 * This is necessary to show that the algorithm terminates.</p>
		 *
		 * <p>Cannot test the DFA state numbers here because in
		 * {@link ParserATNSimulator#addDFAState} we need to know if any other state
		 * exists that has this exact set of ATN configurations. The
		 * {@link #stateNumber} is irrelevant.</p>
		 */
		public override bool Equals(Object o)
		{
			// compare set of ATN configurations in this set with other
			if (this == o) return true;

			if (!(o is DFAState))
			{
				return false;
			}

			DFAState other = (DFAState)o;
			// TODO (sam): what to do when configs==null?
			bool sameSet = this.configSet.Equals(other.configSet);
			//		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
			return sameSet;
		}

		public override String ToString()
		{
			StringBuilder buf = new StringBuilder();
			buf.Append(stateNumber).Append(":").Append(configSet);
			if (isAcceptState)
			{
				buf.Append("=>");
				if (predicates != null)
				{
					buf.Append(Arrays.ToString(predicates));
				}
				else {
					buf.Append(prediction);
				}
			}
			return buf.ToString();
		}
	}

	/** Map a predicate to a predicted alternative. */
	public class PredPrediction
	{

		public SemanticContext pred; // never null; at least SemanticContext.NONE
		public int alt;
		public PredPrediction(SemanticContext pred, int alt)
		{
			this.alt = alt;
			this.pred = pred;
		}

		public override String ToString()
		{
			return "(" + pred + ", " + alt + ")";
		}
	}
}
