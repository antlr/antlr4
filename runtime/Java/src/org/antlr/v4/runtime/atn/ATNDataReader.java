package org.antlr.v4.runtime.atn;

import java.util.UUID;

public abstract class ATNDataReader {
	protected abstract byte readByte();

	public int read() {
		int value = readByte();
		if ((value & 0b1000_0000) == 0) {
			return value;
		}

		int mask = value & 0b111_00000;
		if (mask == ATNDataWriter.MinusOneMask) {
			return -1;
		}
		else {
			return mask == ATNDataWriter.OneByteMask
					? value
					: mask == ATNDataWriter.TwoByteMask
					? (value & ATNDataWriter.ValueMask) << 8 | readByte() & 0xFF
					: mask == ATNDataWriter.ThreeByteMask
					? (value & ATNDataWriter.ValueMask) << 16 | readByte() << 8 & 0xFF00 | readByte() & 0xFF
					: (readByte() << 24 & 0xFF000000) | (readByte() << 16 & 0xFF0000) | (readByte() << 8 & 0xFF00) | readByte() & 0xFF;
		}
	}

	public UUID readUUID() {
		long leastSigBits = ((long) readInt32() & 0x00000000FFFFFFFFL) | ((long) readInt32() << 32);
		long mostSigBits = ((long) readInt32() & 0x00000000FFFFFFFFL) | ((long) readInt32() << 32);
		return new UUID(mostSigBits, leastSigBits);
	}

	public int readInt32() {
		return readUInt16() | (readUInt16() << 16);
	}

	public int readUInt16()  {
		return readByte() & 0xFF | readByte() << 8 & 0xFF00;
	}
}
