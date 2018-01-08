/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
	public class DFA
	{
		/** A set of all DFA states. Use {@link Map} so we can get old state back
	 *  ({@link Set} only allows you to see if it's there).
     */

		public Dictionary<DFAState, DFAState> states = new Dictionary<DFAState, DFAState>();

		public DFAState s0;

		public int decision;

		/** From which ATN state did we create this DFA? */

		public DecisionState atnStartState;

		/**
		 * {@code true} if this DFA is for a precedence decision; otherwise,
		 * {@code false}. This is the backing field for {@link #isPrecedenceDfa}.
		 */
		private bool precedenceDfa;

		public DFA(DecisionState atnStartState)
			: this(atnStartState, 0)
		{
		}

		public DFA(DecisionState atnStartState, int decision)
		{
			this.atnStartState = atnStartState;
			this.decision = decision;

			this.precedenceDfa = false;
			if (atnStartState is StarLoopEntryState && ((StarLoopEntryState)atnStartState).isPrecedenceDecision)
			{
				this.precedenceDfa = true;
				DFAState precedenceState = new DFAState(new ATNConfigSet());
				precedenceState.edges = new DFAState[0];
				precedenceState.isAcceptState = false;
				precedenceState.requiresFullContext = false;
				this.s0 = precedenceState;
			}
		}

		/**
		 * Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
		 * start state {@link #s0} which is not stored in {@link #states}. The
		 * {@link DFAState#edges} array for this start state contains outgoing edges
		 * supplying individual start states corresponding to specific precedence
		 * values.
		 *
		 * @return {@code true} if this is a precedence DFA; otherwise,
		 * {@code false}.
		 * @see Parser#getPrecedence()
		 */
		public bool IsPrecedenceDfa
		{
			get
			{
				return precedenceDfa;
			}
		}

		/**
		 * Get the start state for a specific precedence value.
		 *
		 * @param precedence The current precedence.
		 * @return The start state corresponding to the specified precedence, or
		 * {@code null} if no start state exists for the specified precedence.
		 *
		 * @throws IllegalStateException if this is not a precedence DFA.
		 * @see #isPrecedenceDfa()
		 */
		public DFAState GetPrecedenceStartState(int precedence)
		{
			if (!IsPrecedenceDfa)
			{
				throw new Exception("Only precedence DFAs may contain a precedence start state.");
			}

			// s0.edges is never null for a precedence DFA
			if (precedence < 0 || precedence >= s0.edges.Length)
			{
				return null;
			}

			return s0.edges[precedence];
		}

		/**
		 * Set the start state for a specific precedence value.
		 *
		 * @param precedence The current precedence.
		 * @param startState The start state corresponding to the specified
		 * precedence.
		 *
		 * @throws IllegalStateException if this is not a precedence DFA.
		 * @see #isPrecedenceDfa()
		 */
		public void SetPrecedenceStartState(int precedence, DFAState startState)
		{
			if (!IsPrecedenceDfa)
			{
				throw new Exception("Only precedence DFAs may contain a precedence start state.");
			}

			if (precedence < 0)
			{
				return;
			}

			// synchronization on s0 here is ok. when the DFA is turned into a
			// precedence DFA, s0 will be initialized once and not updated again
			lock (s0)
			{
				// s0.edges is never null for a precedence DFA
				if (precedence >= s0.edges.Length)
				{
					s0.edges = Arrays.CopyOf(s0.edges, precedence + 1);
				}

				s0.edges[precedence] = startState;
			}
		}

		/**
		 * Return a list of all states in this DFA, ordered by state number.
		 */

		public List<DFAState> GetStates()
		{
			List<DFAState> result = new List<DFAState>(states.Keys);
			result.Sort((x, y) => x.stateNumber - y.stateNumber);
			return result;
		}

		public override String ToString() { return ToString(Vocabulary.EmptyVocabulary); }


		public String ToString(IVocabulary vocabulary)
		{
			if (s0 == null)
			{
				return "";
			}

			DFASerializer serializer = new DFASerializer(this, vocabulary);
			return serializer.ToString();
		}

		public String ToLexerString()
		{
			if (s0 == null)
				return "";
			DFASerializer serializer = new LexerDFASerializer(this);
			return serializer.ToString();
		}
	}
}
