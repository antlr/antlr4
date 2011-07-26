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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

/** An ATN transition between any two ATN states.  Subclasses define
 *  atom, set, epsilon, action, predicate, rule transitions.
 *
 *  This is a one way link.  It emanates from a state (usually via a list of
 *  transitions) and has a target state.
 *
 *  Since we never have to change the ATN transitions once we construct it,
 *  we can fix these transitions as specific classes. The DFA transitions
 *  on the other hand need to update the labels as it adds transitions to
 *  the states. We'll use the term Edge for the DFA to distinguish them from
 *  ATN transitions.
 */
public abstract class Transition {
	// constants for serialization
	public static final int EPSILON			= 1;
	public static final int RANGE			= 2;
	public static final int RULE			= 3;
	public static final int PREDICATE		= 4;
	public static final int ATOM			= 5;
	public static final int ACTION			= 6;
	public static final int FORCED_ACTION	= 7;
	public static final int SET				= 8; // ~(A|B) or ~atom, wildcard, which convert to next 2
//	public static final int NOT_ATOM		= 9;
	public static final int NOT_SET			= 9;
	public static final int WILDCARD		= 10;


	public static String[] serializationNames = {
		"INVALID",
		"EPSILON",
		"RANGE",
		"RULE",
		"PREDICATE",
		"ATOM",
		"ACTION",
		"FORCED_ACTION",
		"SET",
//		"NOT_ATOM",
		"NOT_SET",
		"WILDCARD",
	};

	public static Map<Class, Integer> serializationTypes =
		new HashMap<Class, Integer>() {{
			put(EpsilonTransition.class, EPSILON);
			put(RangeTransition.class, RANGE);
			put(RuleTransition.class, RULE);
			put(PredicateTransition.class, PREDICATE);
			put(AtomTransition.class, ATOM);
			put(ActionTransition.class, ACTION); // TODO: FORCED?
			put(SetTransition.class, SET);
//			put(NotAtomTransition.class, NOT_ATOM);
			put(NotSetTransition.class, NOT_SET);
			put(WildcardTransition.class, WILDCARD);
		}};

	/** The target of this transition */
	public ATNState target;

	public Transition() { }

	public Transition(ATNState target) { this.target = target; }

	public int getSerializationType() { return 0; }

	/** Are we epsilon, action, sempred? */
	public boolean isEpsilon() { return false; }

	public IntervalSet label() { return null; }
}
