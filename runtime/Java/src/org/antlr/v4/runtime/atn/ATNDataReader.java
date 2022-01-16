package org.antlr.v4.runtime.atn;

import java.util.UUID;

public class ATNDataReader {
	private final char[] data;
	private int p;

	public ATNDataReader(char[] data) {
		this.data = data;
	}

	public int read() {
		int value = readUInt16();
		if (value == 0xFFFF) {
			return -1;
		} else {
			int mask = value >> ATNDataWriter.MaskBits & 0b11;
			return mask == 0
					? value
					: mask == 0b01
						? (readUInt16() << ATNDataWriter.MaskBits) | (value & ((1 << ATNDataWriter.MaskBits) - 1))
						: readInt32();
		}
	}

	public UUID readUUID() {
		long leastSigBits = ((long) readInt32() & 0x00000000FFFFFFFFL) | ((long) readInt32() << 32);
		long mostSigBits = (long) readInt32() | ((long) readInt32() << 32);
		return new UUID(mostSigBits, leastSigBits);
	}

	public int readInt32() {
		return readUInt16() | (readUInt16() << 16);
	}

	public int readUInt16() {
		return readUInt16(true);
	}

	public int readUInt16(boolean normalize) {
		int result = data[p++];
		// Each char value in data is shifted by +2 at the entry to this method.
		// This is an encoding optimization targeting the serialized values 0
		// and -1 (serialized to 0xFFFF), each of which are very common in the
		// serialized form of the ATN. In the modified UTF-8 that Java uses for
		// compiled string literals, these two character values have multi-byte
		// forms. By shifting each value by +2, they become characters 2 and 1
		// prior to writing the string, each of which have single-byte
		// representations. Since the shift occurs in the tool during ATN
		// serialization, each target is responsible for adjusting the values
		// during deserialization.
		//
		// As a special case, note that the first element of data is not
		// adjusted because it contains the major version number of the
		// serialized ATN, which was fixed at 3 at the time the value shifting
		// was implemented.
		return normalize ? (result > 1 ? result - ATNDataWriter.OptimizeOffset : result + 65534) : result;
	}
}
