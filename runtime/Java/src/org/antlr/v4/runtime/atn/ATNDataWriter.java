package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntegerList;

import java.util.UUID;

public class ATNDataWriter {
	public static final int OptimizeOffset = 2;

	private final IntegerList data;

	public ATNDataWriter(IntegerList data) {
		this.data = data;
	}

	public void writeUUID(UUID uuid) {
		long leastSignificantBits = uuid.getLeastSignificantBits();
		writeUInt32((int)leastSignificantBits);
		writeUInt32((int)(leastSignificantBits >> 32));
		long mostSignificantBits = uuid.getMostSignificantBits();
		writeUInt32((int)mostSignificantBits);
		writeUInt32((int)(mostSignificantBits >> 32));
	}

	public void writeUInt32(int value) {
		writeUInt16((char)value);
		writeUInt16((char)(value >> 16));
	}

	public void writeCompactUInt32(int value) {
		if (value < 0b1000_0000_0000_0000) {
			writeUInt16(value);
		} else {
			writeUInt16((value & 0b0111_1111_1111_1111) | (1 << 15));
			writeUInt16(value >>> 15);
		}
	}

	public void writeUInt16(int value) {
		writeUInt16(value, true);
	}

	public void writeUInt16(int value, boolean optimize) {
		if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
			throw new UnsupportedOperationException("Serialized ATN data element "+
					data.size() + " element " + value + " out of range "+
					(int)Character.MIN_VALUE + ".." + (int)Character.MAX_VALUE);
		}
		// Note: This value shifting loop is documented in ATNDeserializer.
		// don't adjust the first value since that's the version number
		data.add(optimize ? (value + OptimizeOffset) & 0xFFFF : value);
	}
}
