package org.antlr.v4.runtime.atn;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ATNDataWriter {
	public static final int DefaultBufferSize = 1024;
	public static final int ValueMask     = 0b000_11111;
	public static final int OneByteMask   = 0b000_00000;
	public static final int TwoByteMask   = 0b100_00000;
	public static final int ThreeByteMask = 0b101_00000;
	public static final int FullIntMask   = 0b110_00000;
	public static final int MinusOneMask  = 0b111_00000;

	private ByteBuffer data = ByteBuffer.allocate(DefaultBufferSize);

	public ByteBuffer getData() {
		data.flip();
		return data;
	}

	/* Write int in range [-1..Integer.MAX_VALUE] in compact format
		| encoding                                     | bytes count | type          |
		| -------------------------------------------- | ----------- | ------------- |
		| 0xxxxxxx                                     | 1           | uint (7 bit)  |
		| 100xxxxx xxxxxxxx                            | 2           | uint (13 bit) |
		| 101xxxxx xxxxxxxx xxxxxxxx                   | 3           | uint (21 bit) |
		| 11000000 xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx | 5           | uint (32 bit) |
		| 11100000                                     | 1           | -1 (0xFFFF)   |
	 */
	public int write(int value) {
		if (value == -1) {
			writeMinusOne();
			return 1;
		}
		else if (value >= 0) {
			if (value < (1 << 7)) {
				ensureCapacity(1);
				data.put((byte)value);
				return 1;
			}
			else if (value < (1 << 13)) {
				ensureCapacity(2);
				data.put((byte)(TwoByteMask | (value >> 8) & ValueMask));
				data.put((byte)value);
				return 2;
			} else if (value < (1 << 21)) {
				ensureCapacity(3);
				data.put((byte)(ThreeByteMask | (value >> 16) & ValueMask));
				data.put((byte)(value >> 8));
				data.put((byte)(value));
				return 3;
			} else {
				ensureCapacity(5);
				data.put((byte)FullIntMask);
				data.put((byte)(value >> 24));
				data.put((byte)(value >> 16));
				data.put((byte)(value >> 8));
				data.put((byte)(value));
				return 5;
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
		ensureCapacity(4);
		data.put((byte)(value));
		data.put((byte)(value >> 8));
		data.put((byte)(value >> 16));
		data.put((byte)(value >> 24));
	}

	public void writeUInt16(int value) {
		ensureCapacity(2);
		data.put((byte)(value));
		data.put((byte)(value >> 8));
	}

	public void writeMinusOne() {
		ensureCapacity(1);
		data.put((byte)MinusOneMask);
	}

	private void ensureCapacity(int elementsCount) {
		if (data.remaining() >= elementsCount) {
			return;
		}

		int newSize = Math.max(data.limit() + elementsCount, data.capacity() * 2);
		ByteBuffer temp = ByteBuffer.allocate(newSize);
		data.flip();
		temp.put(data);
		data = temp;
	}
}
