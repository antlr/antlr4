/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.automata;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ATNSerializer {
	public Grammar g;
	public ATN atn;

	public ATNSerializer(Grammar g, ATN atn) {
		this.g = g;
		this.atn = atn;
	}

	/** Serialize state descriptors, edge descriptors, and decision->state map
	 *  into list of ints:
	 *
	 * 		grammar-type, (ANTLRParser.LEXER, ...)
	 *  	max token type,
	 *  	num states,
	 *  	state-0-type ruleIndex, state-1-type ruleIndex, ... state-i-type ruleIndex optional-arg ...
	 *  	num rules,
	 *  	rule-1-start-state rule-1-args, rule-2-start-state  rule-2-args, ...
	 *  	(args are token type,actionIndex in lexer else 0,0)
	 *      num modes,
	 *      mode-0-start-state, mode-1-start-state, ... (parser has 0 modes)
	 *      num sets
	 *      set-0-interval-count intervals, set-1-interval-count intervals, ...
	 *  	num total edges,
	 *      src, trg, edge-type, edge arg1, optional edge arg2 (present always), ...
	 *      num decisions,
	 *      decision-0-start-state, decision-1-start-state, ...
	 *
	 *  Convenient to pack into unsigned shorts to make as Java string.
	 */
	public IntegerList serialize() {
		IntegerList data = new IntegerList();
		data.add(ATNSimulator.SERIALIZED_VERSION);
		serializeUUID(data, ATNSimulator.SERIALIZED_UUID);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		switch (g.getType()) {
		case ANTLRParser.LEXER:
			data.add(ATNType.LEXER.ordinal());
			break;

		case ANTLRParser.PARSER:
		case ANTLRParser.COMBINED:
			data.add(ATNType.PARSER.ordinal());
			break;

		default:
			throw new UnsupportedOperationException("Invalid grammar type.");
		}

		data.add(g.getMaxTokenType());
		int nedges = 0;

		Map<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
		List<IntervalSet> sets = new ArrayList<IntervalSet>();

		// dump states, count edges and collect sets while doing so
		IntegerList nonGreedyStates = new IntegerList();
		data.add(atn.states.size());
		for (ATNState s : atn.states) {
			if ( s==null ) { // might be optimized away
				data.add(ATNState.INVALID_TYPE);
				continue;
			}

			int stateType = s.getStateType();
			if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
				nonGreedyStates.add(s.stateNumber);
			}

			data.add(stateType);

			if (s.ruleIndex == -1) {
				data.add(Character.MAX_VALUE);
			}
			else {
				data.add(s.ruleIndex);
			}

			if ( s.getStateType() == ATNState.LOOP_END ) {
				data.add(((LoopEndState)s).loopBackState.stateNumber);
			}
			else if ( s instanceof BlockStartState ) {
				data.add(((BlockStartState)s).endState.stateNumber);
			}

			if (s.getStateType() != ATNState.RULE_STOP) {
				// the deserializer can trivially derive these edges, so there's no need to serialize them
				nedges += s.getNumberOfTransitions();
			}

			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);
				int edgeType = Transition.serializationTypes.get(t.getClass());
				if ( edgeType == Transition.SET || edgeType == Transition.NOT_SET ) {
					SetTransition st = (SetTransition)t;
					if (!setIndices.containsKey(st.set)) {
						sets.add(st.set);
						setIndices.put(st.set, sets.size() - 1);
					}
				}
			}
		}

		// non-greedy states
		data.add(nonGreedyStates.size());
		for (int i = 0; i < nonGreedyStates.size(); i++) {
			data.add(nonGreedyStates.get(i));
		}

		int nrules = atn.ruleToStartState.length;
		data.add(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			data.add(ruleStartState.stateNumber);
			if ( g.isLexer() ) {
				if (atn.ruleToTokenType[r] == Token.EOF) {
					data.add(Character.MAX_VALUE);
				}
				else {
					data.add(atn.ruleToTokenType[r]);
				}
				String ruleName = g.rules.getKey(r);
				Rule rule = g.getRule(ruleName);
				if (rule.actionIndex == -1) {
					data.add(Character.MAX_VALUE);
				}
				else {
					data.add(rule.actionIndex);
				}
			}
		}

		int nmodes = atn.modeToStartState.size();
		data.add(nmodes);
		if ( nmodes>0 ) {
			for (ATNState modeStartState : atn.modeToStartState) {
				data.add(modeStartState.stateNumber);
			}
		}

		int nsets = sets.size();
		data.add(nsets);
		for (IntervalSet set : sets) {
			boolean containsEof = set.contains(Token.EOF);
			if (containsEof && set.getIntervals().get(0).b == Token.EOF) {
				data.add(set.getIntervals().size() - 1);
			}
			else {
				data.add(set.getIntervals().size());
			}

			data.add(containsEof ? 1 : 0);
			for (Interval I : set.getIntervals()) {
				if (I.a == Token.EOF) {
					if (I.b == Token.EOF) {
						continue;
					}
					else {
						data.add(0);
					}
				}
				else {
					data.add(I.a);
				}

				data.add(I.b);
			}
		}

		data.add(nedges);
		for (ATNState s : atn.states) {
			if ( s==null ) {
				// might be optimized away
				continue;
			}

			if (s.getStateType() == ATNState.RULE_STOP) {
				continue;
			}

			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);

				if (atn.states.get(t.target.stateNumber) == null) {
					throw new IllegalStateException("Cannot serialize a transition to a removed state.");
				}

				int src = s.stateNumber;
				int trg = t.target.stateNumber;
				int edgeType = Transition.serializationTypes.get(t.getClass());
				int arg1 = 0;
				int arg2 = 0;
				int arg3 = 0;
				switch ( edgeType ) {
					case Transition.LEFT_RECUR_RULE :
					case Transition.RULE :
						trg = ((RuleTransition)t).followState.stateNumber;
						arg1 = ((RuleTransition)t).target.stateNumber;
						arg2 = ((RuleTransition)t).ruleIndex;
						break;
					case Transition.PREC_PREDICATE : // NOT serialized differently at moment
					case Transition.PREDICATE :
						PredicateTransition pt = (PredicateTransition)t;
						arg1 = pt.ruleIndex;
						arg2 = pt.predIndex;
						arg3 = pt.isCtxDependent ? 1 : 0 ;
						break;
					case Transition.RANGE :
						arg1 = ((RangeTransition)t).from;
						arg2 = ((RangeTransition)t).to;
						if (arg1 == Token.EOF) {
							arg1 = 0;
							arg3 = 1;
						}

						break;
					case Transition.ATOM :
						arg1 = ((AtomTransition)t).label;
						if (arg1 == Token.EOF) {
							arg1 = 0;
							arg3 = 1;
						}

						break;
					case Transition.ACTION :
						ActionTransition at = (ActionTransition)t;
						arg1 = at.ruleIndex;
						arg2 = at.actionIndex;
						if (arg2 == -1) {
							arg2 = 0xFFFF;
						}

						arg3 = at.isCtxDependent ? 1 : 0 ;
						break;
					case Transition.SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.NOT_SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.WILDCARD :
						break;
				}

				data.add(src);
				data.add(trg);
				data.add(edgeType);
				data.add(arg1);
				data.add(arg2);
				data.add(arg3);
			}
		}
		int ndecisions = atn.decisionToState.size();
		data.add(ndecisions);
		for (DecisionState decStartState : atn.decisionToState) {
			data.add(decStartState.stateNumber);
		}

		// don't adjust the first value since that's the version number
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i) < Character.MIN_VALUE || data.get(i) > Character.MAX_VALUE) {
				throw new UnsupportedOperationException("Serialized ATN data element out of range.");
			}

			int value = (data.get(i) + 2) & 0xFFFF;
			data.set(i, value);
		}

		return data;
	}

	public String decode(char[] data) {
		data = data.clone();
		// don't adjust the first value since that's the version number
		for (int i = 1; i < data.length; i++) {
			data[i] = (char)(data[i] - 2);
		}

		StringBuilder buf = new StringBuilder();
		int p = 0;
		int version = ATNSimulator.toInt(data[p++]);
		if (version != ATNSimulator.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNSimulator.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		UUID uuid = ATNSimulator.toUUID(data, p);
		p += 8;
		if (!uuid.equals(ATNSimulator.SERIALIZED_UUID)) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNSimulator.SERIALIZED_UUID);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		p++; // skip grammarType
		int maxType = ATNSimulator.toInt(data[p++]);
		buf.append("max type ").append(maxType).append("\n");
		int nstates = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nstates; i++) {
			int stype = ATNSimulator.toInt(data[p++]);
            if ( stype==ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = ATNSimulator.toInt(data[p++]);
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = ATNSimulator.toInt(data[p++]);
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = ATNSimulator.toInt(data[p++]);
				arg = " "+endStateNumber;
			}
			buf.append(i).append(":")
				.append(ATNState.serializationNames.get(stype)).append(" ")
				.append(ruleIndex).append(arg).append("\n");
		}
		int numNonGreedyStates = ATNSimulator.toInt(data[p++]);
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = ATNSimulator.toInt(data[p++]);
		}
		int nrules = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nrules; i++) {
			int s = ATNSimulator.toInt(data[p++]);
            if ( g.isLexer() ) {
                int arg1 = ATNSimulator.toInt(data[p++]);
                int arg2 = ATNSimulator.toInt(data[p++]);
				if (arg2 == Character.MAX_VALUE) {
					arg2 = -1;
				}
                buf.append("rule ").append(i).append(":").append(s).append(" ").append(arg1).append(",").append(arg2).append('\n');
            }
            else {
                buf.append("rule ").append(i).append(":").append(s).append('\n');
            }
		}
		int nmodes = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = ATNSimulator.toInt(data[p++]);
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		int nsets = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nsets; i++) {
			int nintervals = ATNSimulator.toInt(data[p++]);
			buf.append(i).append(":");
			boolean containsEof = data[p++] != 0;
			if (containsEof) {
				buf.append(getTokenName(Token.EOF));
			}

			for (int j=0; j<nintervals; j++) {
				if ( containsEof || j>0 ) {
					buf.append(", ");
				}

				buf.append(getTokenName(ATNSimulator.toInt(data[p]))).append("..").append(getTokenName(ATNSimulator.toInt(data[p + 1])));
				p += 2;
			}
			buf.append("\n");
		}
		int nedges = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nedges; i++) {
			int src = ATNSimulator.toInt(data[p]);
			int trg = ATNSimulator.toInt(data[p + 1]);
			int ttype = ATNSimulator.toInt(data[p + 2]);
			int arg1 = ATNSimulator.toInt(data[p + 3]);
			int arg2 = ATNSimulator.toInt(data[p + 4]);
			int arg3 = ATNSimulator.toInt(data[p + 5]);
			buf.append(src).append("->").append(trg)
				.append(" ").append(Transition.serializationNames.get(ttype))
				.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
				.append("\n");
			p += 6;
		}
		int ndecisions = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<ndecisions; i++) {
			int s = ATNSimulator.toInt(data[p++]);
			buf.append(i).append(":").append(s).append("\n");
		}
		return buf.toString();
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		if ( g!=null ) return g.getTokenDisplayName(t);
		return String.valueOf(t);
	}

	/** Used by Java target to encode short/int array as chars in string. */
	public static String getSerializedAsString(Grammar g, ATN atn) {
		return new String(getSerializedAsChars(g, atn));
	}

	public static IntegerList getSerialized(Grammar g, ATN atn) {
		return new ATNSerializer(g, atn).serialize();
	}

	public static char[] getSerializedAsChars(Grammar g, ATN atn) {
		return Utils.toCharArray(getSerialized(g, atn));
	}

	public static String getDecoded(Grammar g, ATN atn) {
		IntegerList serialized = getSerialized(g, atn);
		char[] data = Utils.toCharArray(serialized);
		return new ATNSerializer(g, atn).decode(data);
	}

	private void serializeUUID(IntegerList data, UUID uuid) {
		serializeLong(data, uuid.getLeastSignificantBits());
		serializeLong(data, uuid.getMostSignificantBits());
	}

	private void serializeLong(IntegerList data, long value) {
		serializeInt(data, (int)value);
		serializeInt(data, (int)(value >> 32));
	}

	private void serializeInt(IntegerList data, int value) {
		data.add((char)value);
		data.add((char)(value >> 16));
	}
}
