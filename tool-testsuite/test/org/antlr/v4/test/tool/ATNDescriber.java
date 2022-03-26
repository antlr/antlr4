package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.*;

import java.io.InvalidClassException;
import java.util.List;

/** Make human readable set of ints from serialized ATN like this (for debugging / testing):
 *
 * max type 1
 * 0:TOKEN_START -1
 * 1:RULE_START 0
 * 2:RULE_STOP 0
 * 3:BASIC 0
 * 4:BASIC 0
 * rule 0:1 1
 * mode 0:0
 * 0:'a'..128169
 * 0->1 EPSILON 0,0,0
 * 1->3 EPSILON 0,0,0
 * 3->4 SET 0,0,0
 * 4->2 EPSILON 0,0,0
 * 0:0
 */
public class ATNDescriber {
	public ATN atn;
	private List<String> tokenNames;

	public ATNDescriber(ATN atn, List<String> tokenNames) {
		assert atn.grammarType != null;
		this.atn = atn;
		this.tokenNames = tokenNames;
	}

	/** For testing really; gives a human readable version of the ATN */
	public String decode(int[] data) {
		StringBuilder buf = new StringBuilder();
		int p = 0;
		int version = data[p++];
		if (version != ATNDeserializer.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		p++; // skip grammarType
		int maxType = data[p++];
		buf.append("max type ").append(maxType).append("\n");
		int nstates = data[p++];
		for (int i=0; i<nstates; i++) {
			int stype = data[p++];
			if ( stype== ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = data[p++];
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = data[p++];
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = data[p++];
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
		int numNonGreedyStates = data[p++];
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = data[p++];
		}
		int numPrecedenceStates = data[p++];
		for (int i = 0; i < numPrecedenceStates; i++) {
			int stateNumber = data[p++];
		}
		// finish
		int nrules = data[p++];
		for (int i=0; i<nrules; i++) {
			int s = data[p++];
			if (atn.grammarType == ATNType.LEXER) {
				int arg1 = data[p++];
				buf.append("rule ").append(i).append(":").append(s).append(" ").append(arg1).append('\n');
			}
			else {
				buf.append("rule ").append(i).append(":").append(s).append('\n');
			}
		}
		int nmodes = data[p++];
		for (int i=0; i<nmodes; i++) {
			int s = data[p++];
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		int numBMPSets = data[p++];
		p = appendSets(buf, data, p, numBMPSets);
		int nedges = data[p++];
		for (int i=0; i<nedges; i++) {
			int src = data[p];
			int trg = data[p + 1];
			int ttype = data[p + 2];
			int arg1 = data[p + 3];
			int arg2 = data[p + 4];
			int arg3 = data[p + 5];
			buf.append(src).append("->").append(trg)
					.append(" ").append(Transition.serializationNames.get(ttype))
					.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
					.append("\n");
			p += 6;
		}
		int ndecisions = data[p++];
		for (int i=0; i<ndecisions; i++) {
			int s = data[p++];
			buf.append(i).append(":").append(s).append("\n");
		}
		if (atn.grammarType == ATNType.LEXER) {
			// this code is meant to model the form of ATNDeserializer.deserialize,
			// since both need to be updated together whenever a change is made to
			// the serialization format. The "dead" code is only used in debugging
			// and testing scenarios, so the form you see here was kept for
			// improved maintainability.
			int lexerActionCount = data[p++];
			for (int i = 0; i < lexerActionCount; i++) {
				LexerActionType actionType = LexerActionType.values()[data[p++]];
				int data1 = data[p++];
				int data2 = data[p++];
			}
		}
		return buf.toString();
	}

	private int appendSets(StringBuilder buf, int[] data, int p, int nsets) {
		for (int i=0; i<nsets; i++) {
			int nintervals = data[p++];
			buf.append(i).append(":");
			boolean containsEof = data[p++] != 0;
			if (containsEof) {
				buf.append(getTokenName(Token.EOF));
			}

			for (int j=0; j<nintervals; j++) {
				if ( containsEof || j>0 ) {
					buf.append(", ");
				}

				int a = data[p++];
				int b = data[p++];
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

}
