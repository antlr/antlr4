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
	public final ATN atn;
	private final List<String> tokenNames;

	public ATNSerializer(ATN atn) {
		assert atn.grammarType != null;
		this.atn = atn;
		this.tokenNames = null;
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
		ATNDataWriter writer = new ATNDataWriter(data);

		writer.writeUInt16(ATNDeserializer.SERIALIZED_VERSION, false);
		writer.writeUUID(ATNDeserializer.SERIALIZED_UUID);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		writer.writeUInt16(atn.grammarType.ordinal());
		writer.writeUInt16(atn.maxTokenType);
		int nedges = 0;

		// Note that we use a LinkedHashMap as a set to
		// maintain insertion order while deduplicating
		// entries with the same key.
		Map<IntervalSet, Boolean> sets = new LinkedHashMap<>();

		// dump states, count edges and collect sets while doing so
		IntegerList nonGreedyStates = new IntegerList();
		IntegerList precedenceStates = new IntegerList();
		writer.writeCompactUInt32(atn.states.size());
		for (ATNState s : atn.states) {
			if ( s==null ) { // might be optimized away
				writer.writeCompactUInt32(ATNState.INVALID_TYPE);
				continue;
			}

			int stateType = s.getStateType();
			if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
				nonGreedyStates.add(s.stateNumber);
			}

			if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
				precedenceStates.add(s.stateNumber);
			}

			writer.writeCompactUInt32(stateType);

			writer.writeUInt16(s.ruleIndex == -1 ? Character.MAX_VALUE : s.ruleIndex);

			if ( s.getStateType() == ATNState.LOOP_END ) {
				writer.writeCompactUInt32(((LoopEndState)s).loopBackState.stateNumber);
			}
			else if ( s instanceof BlockStartState ) {
				writer.writeCompactUInt32(((BlockStartState)s).endState.stateNumber);
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
		writer.writeCompactUInt32(nonGreedyStates.size());
		for (int i = 0; i < nonGreedyStates.size(); i++) {
			writer.writeCompactUInt32(nonGreedyStates.get(i));
		}

		// precedence states
		writer.writeCompactUInt32(precedenceStates.size());
		for (int i = 0; i < precedenceStates.size(); i++) {
			writer.writeCompactUInt32(precedenceStates.get(i));
		}

		int nrules = atn.ruleToStartState.length;
		writer.writeCompactUInt32(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			writer.writeCompactUInt32(ruleStartState.stateNumber);
			if (atn.grammarType == ATNType.LEXER) {
				writer.writeUInt16(atn.ruleToTokenType[r] == Token.EOF ? Character.MAX_VALUE : atn.ruleToTokenType[r]);
			}
		}

		int nmodes = atn.modeToStartState.size();
		writer.writeCompactUInt32(nmodes);
		if ( nmodes>0 ) {
			for (ATNState modeStartState : atn.modeToStartState) {
				writer.writeCompactUInt32(modeStartState.stateNumber);
			}
		}
		List<IntervalSet> bmpSets = new ArrayList<>();
		List<IntervalSet> smpSets = new ArrayList<>();
		for (IntervalSet set : sets.keySet()) {
			List<IntervalSet> localSets = !set.isNil() && set.getMaxElement() <= Character.MAX_VALUE ? bmpSets : smpSets;
			localSets.add(set);
		}
		serializeSets(writer, bmpSets, UnicodeSerializeMode.UNICODE_BMP);
		serializeSets(writer, smpSets, UnicodeSerializeMode.UNICODE_SMP);
		Map<IntervalSet, Integer> setIndices = new HashMap<>();
		int setIndex = 0;
		for (IntervalSet bmpSet : bmpSets) {
			setIndices.put(bmpSet, setIndex++);
		}
		for (IntervalSet smpSet : smpSets) {
			setIndices.put(smpSet, setIndex++);
		}

		writer.writeCompactUInt32(nedges);
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
						arg1 = t.target.stateNumber;
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
					case Transition.NOT_SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.WILDCARD :
						break;
				}

				writer.writeCompactUInt32(src);
				writer.writeCompactUInt32(trg);
				writer.writeUInt16(edgeType);
				writer.writeUInt16(arg1);
				writer.writeUInt16(arg2);
				writer.writeUInt16(arg3);
			}
		}

		int ndecisions = atn.decisionToState.size();
		writer.writeCompactUInt32(ndecisions);
		for (DecisionState decStartState : atn.decisionToState) {
			writer.writeCompactUInt32(decStartState.stateNumber);
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			writer.writeCompactUInt32(atn.lexerActions.length);
			for (LexerAction action : atn.lexerActions) {
				writer.writeUInt16(action.getActionType().ordinal());
				switch (action.getActionType()) {
				case CHANNEL:
					int channel = ((LexerChannelAction)action).getChannel();
					writer.writeUInt16(channel != -1 ? channel : 0xFFFF);
					writer.writeUInt16(0);
					break;

				case CUSTOM:
					int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
					int actionIndex = ((LexerCustomAction)action).getActionIndex();
					writer.writeUInt16(ruleIndex != -1 ? ruleIndex : 0xFFFF);
					writer.writeUInt16(actionIndex != -1 ? actionIndex : 0xFFFF);
					break;

				case MODE:
					int mode = ((LexerModeAction)action).getMode();
					writer.writeUInt16(mode != -1 ? mode : 0xFFFF);
					writer.writeUInt16(0);
					break;

				case MORE:
				case POP_MODE:
				case SKIP:
					writer.writeUInt16(0);
					writer.writeUInt16(0);
					break;

				case PUSH_MODE:
					mode = ((LexerPushModeAction)action).getMode();
					writer.writeUInt16(mode != -1 ? mode : 0xFFFF);
					writer.writeUInt16(0);
					break;

				case TYPE:
					int type = ((LexerTypeAction)action).getType();
					writer.writeUInt16(type != -1 ? type : 0xFFFF);
					writer.writeUInt16(0);
					break;

				default:
					String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
					throw new IllegalArgumentException(message);
				}
			}
		}

		return data;
	}

	private static void serializeSets(ATNDataWriter writer, Collection<IntervalSet> sets, UnicodeSerializeMode mode) {
		int nSets = sets.size();
		writer.writeCompactUInt32(nSets);

		for (IntervalSet set : sets) {
			boolean containsEof = set.contains(Token.EOF);
			int size = set.getIntervals().size();
			if (containsEof && set.getIntervals().get(0).b == Token.EOF) {
				size--;
			}
			writer.writeCompactUInt32(size);

			writer.writeUInt16(containsEof ? 1 : 0);
			for (Interval I : set.getIntervals()) {
				int firstValue;
				if (I.a == Token.EOF) {
					if (I.b == Token.EOF) {
						continue;
					}
					else {
						firstValue = 0;
					}
				}
				else {
					firstValue = I.a;
				}

				if (mode == UnicodeSerializeMode.UNICODE_BMP) {
					writer.writeUInt16(firstValue);
					writer.writeUInt16(I.b);
				} else {
					writer.writeUInt32(firstValue);
					writer.writeUInt32(I.b);
				}
			}
		}
	}

	public String decode(char[] data) {
		ATNDataReader dataReader = new ATNDataReader(data);
		StringBuilder buf = new StringBuilder();
		int version = dataReader.readUInt16(false);
		if (version != ATNDeserializer.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		UUID uuid = dataReader.readUUID();
		if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		dataReader.readUInt16(); // skip grammarType
		int maxType = dataReader.readUInt16();
		buf.append("max type ").append(maxType).append("\n");
		int nstates = dataReader.readCompactUInt32();
		for (int i=0; i<nstates; i++) {
			int stype = dataReader.readCompactUInt32();
            if ( stype==ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = dataReader.readUInt16();
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = dataReader.readCompactUInt32();
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = dataReader.readCompactUInt32();
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
		int numNonGreedyStates = dataReader.readCompactUInt32();
		for (int i = 0; i < numNonGreedyStates; i++) {
			dataReader.readCompactUInt32(); // Skip stateNumber
		}
		int numPrecedenceStates = dataReader.readCompactUInt32();
		for (int i = 0; i < numPrecedenceStates; i++) {
			dataReader.readCompactUInt32(); // Skip stateNumber
		}
		// finish
		int nrules = dataReader.readCompactUInt32();
		for (int i=0; i<nrules; i++) {
			int s = dataReader.readCompactUInt32();
			buf.append("rule ").append(i).append(":").append(s);
			if (atn.grammarType == ATNType.LEXER) {
				buf.append(" ").append(dataReader.readUInt16());
			}
			buf.append('\n');
		}
		int nmodes = dataReader.readCompactUInt32();
		for (int i=0; i<nmodes; i++) {
			int s = dataReader.readCompactUInt32();
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		int offset = appendSets(buf, dataReader, 0, UnicodeSerializeMode.UNICODE_BMP);
		appendSets(buf, dataReader, offset, UnicodeSerializeMode.UNICODE_SMP);
		int nedges = dataReader.readCompactUInt32();
		for (int i=0; i<nedges; i++) {
			int src = dataReader.readCompactUInt32();
			int trg = dataReader.readCompactUInt32();
			int ttype = dataReader.readUInt16();
			int arg1 = dataReader.readUInt16();
			int arg2 = dataReader.readUInt16();
			int arg3 = dataReader.readUInt16();
			buf.append(src).append("->").append(trg)
				.append(" ").append(Transition.serializationNames.get(ttype))
				.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
				.append("\n");
		}
		int ndecisions = dataReader.readCompactUInt32();
		for (int i=0; i<ndecisions; i++) {
			int s = dataReader.readCompactUInt32();
			buf.append(i).append(":").append(s).append("\n");
		}
		if (atn.grammarType == ATNType.LEXER) {
			// this code is meant to model the form of ATNDeserializer.deserialize,
			// since both need to be updated together whenever a change is made to
			// the serialization format. The "dead" code is only used in debugging
			// and testing scenarios, so the form you see here was kept for
			// improved maintainability.
			int lexerActionCount = dataReader.readCompactUInt32();
			for (int i = 0; i < lexerActionCount; i++) {
				dataReader.readUInt16(); // Skip actionType
				dataReader.readUInt16();
				dataReader.readUInt16();
			}
		}
		return buf.toString();
	}

	private int appendSets(StringBuilder buf, ATNDataReader dataReader, int setIndexOffset, UnicodeSerializeMode mode) {
		int nsets = dataReader.readCompactUInt32();
		for (int i=0; i<nsets; i++) {
			int nintervals = dataReader.readCompactUInt32();
			buf.append(i + setIndexOffset).append(":");
			boolean containsEof = dataReader.readUInt16() != 0;
			if (containsEof) {
				buf.append(getTokenName(Token.EOF));
			}

			for (int j=0; j<nintervals; j++) {
				if ( containsEof || j>0 ) {
					buf.append(", ");
				}

				int a, b;
				if (mode == UnicodeSerializeMode.UNICODE_BMP) {
					a = dataReader.readUInt16();
					b = dataReader.readUInt16();
				} else {
					a = dataReader.readUInt32();
					b = dataReader.readUInt32();
				}
				buf.append(getTokenName(a)).append("..").append(getTokenName(b));
			}
			buf.append("\n");
		}
		return nsets;
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
				return "'\\u"+hex+"'";
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
}
