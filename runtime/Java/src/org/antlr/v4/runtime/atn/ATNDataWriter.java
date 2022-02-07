package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntegerList;

public class ATNDataWriter {
	public static final int MaskBits = 14;
	public static final int JavaOptimizeOffset = 2;

	private final IntegerList data;
	private final boolean isJava;

	public ATNDataWriter(IntegerList data, String language) {
		this.data = data;
		this.isJava = language.equals("Java");
	}

	/* Write int of full range [Integer.MIN_VALUE..Integer.MAX_VALUE] in compact format
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

		if (value >= 0) {
			if (value < 1 << MaskBits) {
				writeUInt16(value);
				return 1;
			}
			else if (value < 1 << (MaskBits + 16)) {
				writeUInt16(value & ((1 << MaskBits) - 1) | 0b01 << MaskBits);
				writeUInt16(value >>> MaskBits);
				return 2;
			}
		}

		writeUInt16(0b10 << MaskBits);
		writeInt32(value);
		return 3;
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

		data.add(isJava && optimize ? (value + JavaOptimizeOffset) & 0xFFFF : value);
	}
}
