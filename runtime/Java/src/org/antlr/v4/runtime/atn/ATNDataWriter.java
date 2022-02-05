package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntegerList;

public class ATNDataWriter {
	public static final int JavaOptimizeOffset = 2;

	private final IntegerList data;
	private final String language;
	private final boolean isJava;

	public ATNDataWriter(IntegerList data, String language) {
		this.data = data;
		this.language = language;
		this.isJava = language.equals("Java");
	}

	public void writeUInt32(int value) {
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
