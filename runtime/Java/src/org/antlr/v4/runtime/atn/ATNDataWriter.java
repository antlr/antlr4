package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntegerList;

import java.util.UUID;

public class ATNDataWriter {
	public static final int OptimizeOffset = 2;
	public static final int MaskBits = 14;

	private final IntegerList data;

	public ATNDataWriter(IntegerList data) {
		this.data = data;
	}

	/* Write int in range [-1..Integer.MAX_VALUE] in compact format
		| encoding                                                    | count | type         |
		| ----------------------------------------------------------- | ----- | ------------ |
		| 00xx xxxx xxxx xxxx                                         | 1     | int (14 bit) |
		| 01xx xxxx xxxx xxxx xxxx xxxx xxxx xxxx                     | 2     | int (30 bit) |
		| 1000 0000 0000 0000 xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx | 3     | int (32 bit) |
		| 1111 1111 1111 1111                                         | 1     | -1 (0xFFFF)  |
	 */
	public int write(int value) {
		if (value == -1) {
			writeUInt16(0xFFFF);
			return 1;
		}
		else if (value >= 0) {
			if (value < 1 << MaskBits) {
				writeUInt16(value);
				return 1;
			}
			else if (value < 1 << (MaskBits + 16)){
				writeUInt16(value & ((1 << MaskBits) - 1) | 0b01 << MaskBits);
				writeUInt16(value >>> MaskBits);
				return 2;
			} else {
				writeUInt16(0b10 << MaskBits);
				writeInt32(value);
				return 3;
			}
		} else {
			throw new UnsupportedOperationException("Value " + value + " out of range [-1.." + Integer.MAX_VALUE + "]");
		}
	}

	public void writeUUID(UUID uuid) {
		long leastSignificantBits = uuid.getLeastSignificantBits();
		writeInt32((int)leastSignificantBits);
		writeInt32((int)(leastSignificantBits >> 32));
		long mostSignificantBits = uuid.getMostSignificantBits();
		writeInt32((int)mostSignificantBits);
		writeInt32((int)(mostSignificantBits >> 32));
	}

	public void writeInt32(int value) {
		writeUInt16((char)value);
		writeUInt16((char)(value >> 16));
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
