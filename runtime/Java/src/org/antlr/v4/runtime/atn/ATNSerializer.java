/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Utils;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ATNSerializer {
	public ATN atn;
	private List<String> tokenNames;

	private interface CodePointSerializer {
		void serializeCodePoint(IntegerList data, int cp);
	}

	public ATNSerializer(ATN atn) {
		assert atn.grammarType != null;
		this.atn = atn;
	}

	public ATNSerializer(ATN atn, List<String> tokenNames) {
		assert atn.grammarType != null;
		this.atn = atn;
		this.tokenNames = tokenNames;
	}

	/** Serialize state descriptors, edge descriptors, and decision&rarr;state map
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
	 *      num unicode-bmp-sets
	 *      bmp-set-0-interval-count intervals, bmp-set-1-interval-count intervals, ...
	 *      num unicode-smp-sets
	 *      smp-set-0-interval-count intervals, smp-set-1-interval-count intervals, ...
	 *	num total edges,
	 *      src, trg, edge-type, edge arg1, optional edge arg2 (present always), ...
	 *      num decisions,
	 *      decision-0-start-state, decision-1-start-state, ...
	 *
	 *  Convenient to pack into unsigned shorts to make as Java string.
	 */
	public IntegerList serialize() {
		IntegerList data = new IntegerList();
		data.add(ATNDeserializer.SERIALIZED_VERSION);
		serializeUUID(data, ATNDeserializer.SERIALIZED_UUID);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		data.add(atn.grammarType.ordinal());
		data.add(atn.maxTokenType);
		int nedges = 0;

		// Note that we use a LinkedHashMap as a set to
		// maintain insertion order while deduplicating
		// entries with the same key.
		Map<IntervalSet, Boolean> sets = new LinkedHashMap<>();

		// dump states, count edges and collect sets while doing so
		IntegerList nonGreedyStates = new IntegerList();
		IntegerList precedenceStates = new IntegerList();
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

			if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
				precedenceStates.add(s.stateNumber);
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
					sets.put(st.set, true);
				}
			}
		}

		// non-greedy states
		data.add(nonGreedyStates.size());
		for (int i = 0; i < nonGreedyStates.size(); i++) {
			data.add(nonGreedyStates.get(i));
		}

		// precedence states
		data.add(precedenceStates.size());
		for (int i = 0; i < precedenceStates.size(); i++) {
			data.add(precedenceStates.get(i));
		}

		int nrules = atn.ruleToStartState.length;
		data.add(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			data.add(ruleStartState.stateNumber);
			if (atn.grammarType == ATNType.LEXER) {
				if (atn.ruleToTokenType[r] == Token.EOF) {
					data.add(Character.MAX_VALUE);
				}
				else {
					data.add(atn.ruleToTokenType[r]);
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
		List<IntervalSet> bmpSets = new ArrayList<>();
		List<IntervalSet> smpSets = new ArrayList<>();
		for (IntervalSet set : sets.keySet()) {
			if (set.getMaxElement() <= Character.MAX_VALUE) {
				bmpSets.add(set);
			}
			else {
				smpSets.add(set);
			}
		}
		serializeSets(
			data,
			bmpSets,
			new CodePointSerializer() {
				@Override
				public void serializeCodePoint(IntegerList data, int cp) {
					data.add(cp);
				}
			});
		serializeSets(
			data,
			smpSets,
			new CodePointSerializer() {
				@Override
				public void serializeCodePoint(IntegerList data, int cp) {
					serializeInt(data, cp);
				}
			});
		Map<IntervalSet, Integer> setIndices = new HashMap<>();
		int setIndex = 0;
		for (IntervalSet bmpSet : bmpSets) {
			setIndices.put(bmpSet, setIndex++);
		}
		for (IntervalSet smpSet : smpSets) {
			setIndices.put(smpSet, setIndex++);
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
					case Transition.RULE :
						trg = ((RuleTransition)t).followState.stateNumber;
						arg1 = ((RuleTransition)t).target.stateNumber;
						arg2 = ((RuleTransition)t).ruleIndex;
						arg3 = ((RuleTransition)t).precedence;
						break;
					case Transition.PRECEDENCE:
						PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
						arg1 = ppt.precedence;
						break;
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

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			data.add(atn.lexerActions.length);
			for (LexerAction action : atn.lexerActions) {
				data.add(action.getActionType().ordinal());
				switch (action.getActionType()) {
				case CHANNEL:
					int channel = ((LexerChannelAction)action).getChannel();
					data.add(channel != -1 ? channel : 0xFFFF);
					data.add(0);
					break;

				case CUSTOM:
					int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
					int actionIndex = ((LexerCustomAction)action).getActionIndex();
					data.add(ruleIndex != -1 ? ruleIndex : 0xFFFF);
					data.add(actionIndex != -1 ? actionIndex : 0xFFFF);
					break;

				case MODE:
					int mode = ((LexerModeAction)action).getMode();
					data.add(mode != -1 ? mode : 0xFFFF);
					data.add(0);
					break;

				case MORE:
					data.add(0);
					data.add(0);
					break;

				case POP_MODE:
					data.add(0);
					data.add(0);
					break;

				case PUSH_MODE:
					mode = ((LexerPushModeAction)action).getMode();
					data.add(mode != -1 ? mode : 0xFFFF);
					data.add(0);
					break;

				case SKIP:
					data.add(0);
					data.add(0);
					break;

				case TYPE:
					int type = ((LexerTypeAction)action).getType();
					data.add(type != -1 ? type : 0xFFFF);
					data.add(0);
					break;

				default:
					String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
					throw new IllegalArgumentException(message);
				}
			}
		}

		// Note: This value shifting loop is documented in ATNDeserializer.
		// don't adjust the first value since that's the version number
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i) < Character.MIN_VALUE || data.get(i) > Character.MAX_VALUE) {
				throw new UnsupportedOperationException("Serialized ATN data element "+
					                                        data.get(i)+
					                                        " element "+i+" out of range "+
					                                        (int)Character.MIN_VALUE+
					                                        ".."+
					                                        (int)Character.MAX_VALUE);
			}

			int value = (data.get(i) + 2) & 0xFFFF;
			data.set(i, value);
		}

		return data;
	}

	private static void serializeSets(
			IntegerList data,
			Collection<IntervalSet> sets,
			CodePointSerializer codePointSerializer)
	{
		int nSets = sets.size();
		data.add(nSets);

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
						codePointSerializer.serializeCodePoint(data, 0);
					}
				}
				else {
					codePointSerializer.serializeCodePoint(data, I.a);
				}

				codePointSerializer.serializeCodePoint(data, I.b);
			}
		}
	}

	public String decode(char[] data) {
		data = data.clone();
		// don't adjust the first value since that's the version number
		for (int i = 1; i < data.length; i++) {
			data[i] = (char)(data[i] - 2);
		}

		StringBuilder buf = new StringBuilder();
		int p = 0;
		int version = ATNDeserializer.toInt(data[p++]);
		if (version != ATNDeserializer.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		UUID uuid = ATNDeserializer.toUUID(data, p);
		p += 8;
		if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		p++; // skip grammarType
		int maxType = ATNDeserializer.toInt(data[p++]);
		buf.append("max type ").append(maxType).append("\n");
		int nstates = ATNDeserializer.toInt(data[p++]);
		for (int i=0; i<nstates; i++) {
			int stype = ATNDeserializer.toInt(data[p++]);
            if ( stype==ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = ATNDeserializer.toInt(data[p++]);
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = ATNDeserializer.toInt(data[p++]);
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = ATNDeserializer.toInt(data[p++]);
				arg = " "+endStateNumber;
			}
			buf.append(i).append(":")
				.append(ATNState.serializationNames.get(stype)).append(" ")
				.append(ruleIndex).append(arg).append("\n");
		}
		// this code is meant to model the form of ATNDeserializer.deserialize,
		// since both need to be updated together whenever a change is made to
		// the serialization format. The "dead" code is only used in debugging
		// and testing scenarios, so the form you see here was kept for
		// improved maintainability.
		// start
		int numNonGreedyStates = ATNDeserializer.toInt(data[p++]);
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = ATNDeserializer.toInt(data[p++]);
		}
		int numPrecedenceStates = ATNDeserializer.toInt(data[p++]);
		for (int i = 0; i < numPrecedenceStates; i++) {
			int stateNumber = ATNDeserializer.toInt(data[p++]);
		}
		// finish
		int nrules = ATNDeserializer.toInt(data[p++]);
		for (int i=0; i<nrules; i++) {
			int s = ATNDeserializer.toInt(data[p++]);
            if (atn.grammarType == ATNType.LEXER) {
                int arg1 = ATNDeserializer.toInt(data[p++]);
                buf.append("rule ").append(i).append(":").append(s).append(" ").append(arg1).append('\n');
            }
            else {
                buf.append("rule ").append(i).append(":").append(s).append('\n');
            }
		}
		int nmodes = ATNDeserializer.toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = ATNDeserializer.toInt(data[p++]);
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		int numBMPSets = ATNDeserializer.toInt(data[p++]);
		p = appendSets(buf, data, p, numBMPSets, 0, ATNDeserializer.getUnicodeDeserializer(ATNDeserializer.UnicodeDeserializingMode.UNICODE_BMP));
		int numSMPSets = ATNDeserializer.toInt(data[p++]);
		p = appendSets(buf, data, p, numSMPSets, numBMPSets, ATNDeserializer.getUnicodeDeserializer(ATNDeserializer.UnicodeDeserializingMode.UNICODE_SMP));
		int nedges = ATNDeserializer.toInt(data[p++]);
		for (int i=0; i<nedges; i++) {
			int src = ATNDeserializer.toInt(data[p]);
			int trg = ATNDeserializer.toInt(data[p + 1]);
			int ttype = ATNDeserializer.toInt(data[p + 2]);
			int arg1 = ATNDeserializer.toInt(data[p + 3]);
			int arg2 = ATNDeserializer.toInt(data[p + 4]);
			int arg3 = ATNDeserializer.toInt(data[p + 5]);
			buf.append(src).append("->").append(trg)
				.append(" ").append(Transition.serializationNames.get(ttype))
				.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
				.append("\n");
			p += 6;
		}
		int ndecisions = ATNDeserializer.toInt(data[p++]);
		for (int i=0; i<ndecisions; i++) {
			int s = ATNDeserializer.toInt(data[p++]);
			buf.append(i).append(":").append(s).append("\n");
		}
		if (atn.grammarType == ATNType.LEXER) {
			// this code is meant to model the form of ATNDeserializer.deserialize,
			// since both need to be updated together whenever a change is made to
			// the serialization format. The "dead" code is only used in debugging
			// and testing scenarios, so the form you see here was kept for
			// improved maintainability.
			int lexerActionCount = ATNDeserializer.toInt(data[p++]);
			for (int i = 0; i < lexerActionCount; i++) {
				LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
				int data1 = ATNDeserializer.toInt(data[p++]);
				int data2 = ATNDeserializer.toInt(data[p++]);
			}
		}
		return buf.toString();
	}

	private int appendSets(StringBuilder buf, char[] data, int p, int nsets, int setIndexOffset, ATNDeserializer.UnicodeDeserializer unicodeDeserializer) {
		for (int i=0; i<nsets; i++) {
			int nintervals = ATNDeserializer.toInt(data[p++]);
			buf.append(i+setIndexOffset).append(":");
			boolean containsEof = data[p++] != 0;
			if (containsEof) {
				buf.append(getTokenName(Token.EOF));
			}

			for (int j=0; j<nintervals; j++) {
				if ( containsEof || j>0 ) {
					buf.append(", ");
				}

				int a = unicodeDeserializer.readUnicode(data, p);
				p += unicodeDeserializer.size();
				int b = unicodeDeserializer.readUnicode(data, p);
				p += unicodeDeserializer.size();
				buf.append(getTokenName(a)).append("..").append(getTokenName(b));
			}
			buf.append("\n");
		}
		return p;
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";

		if ( atn.grammarType == ATNType.LEXER &&
			 t >= Character.MIN_VALUE && t <= Character.MAX_VALUE )
		{
			switch (t) {
			case '\n':
				return "'\\n'";
			case '\r':
				return "'\\r'";
			case '\t':
				return "'\\t'";
			case '\b':
				return "'\\b'";
			case '\f':
				return "'\\f'";
			case '\\':
				return "'\\\\'";
			case '\'':
				return "'\\''";
			default:
				if ( Character.UnicodeBlock.of((char)t)==Character.UnicodeBlock.BASIC_LATIN &&
					 !Character.isISOControl((char)t) ) {
					return '\''+Character.toString((char)t)+'\'';
				}
				// turn on the bit above max "\uFFFF" value so that we pad with zeros
				// then only take last 4 digits
				String hex = Integer.toHexString(t|0x10000).toUpperCase().substring(1,5);
				String unicodeStr = "'\\u"+hex+"'";
				return unicodeStr;
			}
		}

		if (tokenNames != null && t >= 0 && t < tokenNames.size()) {
			return tokenNames.get(t);
		}

		return String.valueOf(t);
	}

	/** Used by Java target to encode short/int array as chars in string. */
	public static String getSerializedAsString(ATN atn) {
		return new String(getSerializedAsChars(atn));
	}

	public static IntegerList getSerialized(ATN atn) {
		return new ATNSerializer(atn).serialize();
	}

	public static char[] getSerializedAsChars(ATN atn) {
		return Utils.toCharArray(getSerialized(atn));
	}

	public static String getDecoded(ATN atn, List<String> tokenNames) {
		IntegerList serialized = getSerialized(atn);
		char[] data = Utils.toCharArray(serialized);
		return new ATNSerializer(atn, tokenNames).decode(data);
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
