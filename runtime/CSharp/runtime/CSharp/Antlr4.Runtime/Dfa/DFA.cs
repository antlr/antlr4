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
using System.Collections.Concurrent;
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Interlocked = System.Threading.Interlocked;

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
