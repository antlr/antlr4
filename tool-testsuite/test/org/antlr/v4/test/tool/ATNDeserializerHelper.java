package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.*;

import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ATNDeserializerHelper {
	public static String getDecoded(ATN atn, List<String> tokenNames) {
		ByteBuffer serialized = new ATNSerializer(atn).serialize();
		return new ATNDeserializerHelper(atn, tokenNames).decode(serialized);
	}

	public final ATN atn;
	private final List<String> tokenNames;

	public ATNDeserializerHelper(ATN atn, List<String> tokenNames) {
		this.atn = atn;
		this.tokenNames = tokenNames;
	}

	public String decode(ByteBuffer data) {
		ATNDataReader dataReader = new ATNDataReader(data);
		StringBuilder buf = new StringBuilder();
		int version = dataReader.readUInt16();
		if (version != ATNDeserializer.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		UUID uuid = dataReader.readUUID();
		if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		dataReader.read(); // skip grammarType
		int maxType = dataReader.read();
		buf.append("max type ").append(maxType).append("\n");
		int nstates = dataReader.read();
		for (int i=0; i<nstates; i++) {
			int stype = dataReader.read();
			if ( stype== ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = dataReader.read();

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = dataReader.read();
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = dataReader.read();
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
		int numNonGreedyStates = dataReader.read();
		for (int i = 0; i < numNonGreedyStates; i++) {
			dataReader.read(); // Skip stateNumber
		}
		int numPrecedenceStates = dataReader.read();
		for (int i = 0; i < numPrecedenceStates; i++) {
			dataReader.read(); // Skip stateNumber
		}
		// finish
		int nrules = dataReader.read();
		for (int i=0; i<nrules; i++) {
			int s = dataReader.read();
			buf.append("rule ").append(i).append(":").append(s);
			if (atn.grammarType == ATNType.LEXER) {
				buf.append(" ").append(dataReader.read());
			}
			buf.append('\n');
		}
		int nmodes = dataReader.read();
		for (int i=0; i<nmodes; i++) {
			int s = dataReader.read();
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		int offset = appendSets(buf, dataReader, 0, UnicodeSerializeMode.UNICODE_BMP);
		appendSets(buf, dataReader, offset, UnicodeSerializeMode.UNICODE_SMP);
		int nedges = dataReader.read();
		for (int i=0; i<nedges; i++) {
			int src = dataReader.read();
			int trg = dataReader.read();
			int ttype = dataReader.read();
			int arg1 = dataReader.read();
			int arg2 = dataReader.read();
			int arg3 = dataReader.read();
			buf.append(src).append("->").append(trg)
					.append(" ").append(Transition.serializationNames.get(ttype))
					.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
					.append("\n");
		}
		int ndecisions = dataReader.read();
		for (int i=0; i<ndecisions; i++) {
			int s = dataReader.read();
			buf.append(i).append(":").append(s).append("\n");
		}
		if (atn.grammarType == ATNType.LEXER) {
			// this code is meant to model the form of ATNDeserializer.deserialize,
			// since both need to be updated together whenever a change is made to
			// the serialization format. The "dead" code is only used in debugging
			// and testing scenarios, so the form you see here was kept for
			// improved maintainability.
			int lexerActionCount = dataReader.read();
			for (int i = 0; i < lexerActionCount; i++) {
				dataReader.read(); // Skip actionType
				dataReader.read();
				dataReader.read();
			}
		}
		return buf.toString();
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

	private int appendSets(StringBuilder buf, ATNDataReader dataReader, int setIndexOffset, UnicodeSerializeMode mode) {
		int nsets = dataReader.read();
		for (int i=0; i<nsets; i++) {
			int nintervals = dataReader.read();
			buf.append(i + setIndexOffset).append(":");
			boolean containsEof = dataReader.read() != 0;
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
					a = dataReader.readInt32();
					b = dataReader.readInt32();
				}
				buf.append(getTokenName(a)).append("..").append(getTokenName(b));
			}
			buf.append("\n");
		}
		return nsets;
	}
}
