/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.*;

/** A DFA state represents a set of possible ATN configurations.
 *  As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
 *  to keep track of all possible states the ATN can be in after
 *  reading each input symbol.  That is to say, after reading
 *  input a1a2..an, the DFA is in a state that represents the
 *  subset T of the states of the ATN that are reachable from the
 *  ATN's start state along some path labeled a1a2..an."
 *  In conventional NFA->DFA conversion, therefore, the subset T
 *  would be a bitset representing the set of states the
 *  ATN could be in.  We need to track the alt predicted by each
 *  state as well, however.  More importantly, we need to maintain
 *  a stack of states, tracking the closure operations as they
 *  jump from rule to rule, emulating rule invocations (method calls).
 *  Recall that ATNs do not normally have a stack like a pushdown-machine
 *  so I have to add one to simulate the proper lookahead sequences for
 *  the underlying LL grammar from which the ATN was derived.
 *
 *  I use a list of ATNConfig objects.  An ATNConfig
 *  is both a state (ala normal conversion) and a RuleContext describing
 *  the chain of rules (if any) followed to arrive at that state.
 *
 *  A DFA state may have multiple references to a particular state,
 *  but with different ATNContexts (with same or different alts)
 *  meaning that state was reached via a different set of rule invocations.
 */
public class DFAState {
	public int stateNumber = -1;

	/** The set of ATN configurations (state,alt,context) for this DFA state */
	public OrderedHashSet<ATNConfig> configs = new OrderedHashSet<ATNConfig>();

	/** edges[symbol] points to target of symbol */
	public DFAState[] edges;

//	public IntervalSet viableChars;

	public boolean isAcceptState = false;

	public int prediction; // if accept state, what ttype do we match?

	public int ruleIndex; // if accept, exec what action?

	public boolean complete; // all alts predict "prediction"
	public boolean isCtxSensitive;

	public Map<RuleContext, Integer> ctxToPrediction;

	public DFAState() { }

	public DFAState(int stateNumber) { this.stateNumber = stateNumber; }

	public DFAState(OrderedHashSet<ATNConfig> configs) { this.configs = configs; }

	/** Get the set of all alts mentioned by all ATN configurations in this
	 *  DFA state.
	 */
	public Set<Integer> getAltSet() {
		Set<Integer> alts = new HashSet<Integer>();
		for (ATNConfig c : configs) {
			alts.add(c.alt);
		}
		if ( alts.size()==0 ) return null;
		return alts;
	}

	/*
	public void setContextSensitivePrediction(RuleContext ctx, int predictedAlt) {
		isCtxSensitive = true;
		if ( ctxToPrediction==null ) {
			ctxToPrediction = new LinkedHashMap<RuleContext, Integer>();
		}
		ctxToPrediction.put(ctx, predictedAlt);
	}
	*/

	/** A decent hash for a DFA state is the sum of the ATN state/alt pairs. */
	public int hashCode() {
		int h = 0;
		for (ATNConfig c : configs) {
			h += c.alt;
		}
		return h;
	}

	/** Two DFAStates are equal if their ATN configuration sets are the
	 *  same. This method is used to see if a DFA state already exists.
	 *
	 *  Because the number of alternatives and number of ATN configurations are
	 *  finite, there is a finite number of DFA states that can be processed.
	 *  This is necessary to show that the algorithm terminates.
	 *
	 *  Cannot test the DFA state numbers here because in DFA.addState we need
	 *  to know if any other state exists that has this exact set of ATN
	 *  configurations.  The DFAState state number is irrelevant.
	 */
	public boolean equals(Object o) {
		// compare set of ATN configurations in this set with other
		if ( this==o ) return true;
		DFAState other = (DFAState)o;
		boolean sameSet = this.configs.equals(other.configs);
//		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
		return sameSet;
	}


	public String toString() {
		return stateNumber+":"+configs+(isAcceptState?("=>"+prediction):"");
	}
}
