package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.*;

import java.io.InvalidClassException;
import java.util.List;

public class ATNDeserializerHelper {
	public final ATN atn;
	private final List<String> tokenNames;

	public ATNDeserializerHelper(ATN atn, List<String> tokenNames) {
		this.atn = atn;
		this.tokenNames = tokenNames;
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

	private String getTokenName(int t) {
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
}
