package org.antlr.v4.runtime.atn;

import java.util.Arrays;

public class ATNDataReaderBase64 extends ATNDataReader {
	private static final char[] toBase64 = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	private static final int[] fromBase64 = new int[256];
	static {
		Arrays.fill(fromBase64, -1);
		for (int i = 0; i < toBase64.length; i++)
			fromBase64[toBase64[i]] = i;
		fromBase64['='] = -2;
	}

	private final String string;
	private int charPos;
	private int rest;
	private int restBitsCount;

	public ATNDataReaderBase64(String string) {
		this.string = string;
	}

	@Override
	public byte readByte() {
		int result = 0;
		if (restBitsCount == 0) {
			int b1 = fromBase64[string.charAt(charPos++)];
			int b2 = fromBase64[string.charAt(charPos++)];
			result = b1 << 2 & 0b1111_1100 | b2 >> 4 & 0b0000_0011;
			rest = b2 & 0b0000_1111;
			restBitsCount = 4;
		}
		else if (restBitsCount == 4) {
			int b = fromBase64[string.charAt(charPos++)];
			result = rest << 4 & 0b1111_0000 | b >> 2 & 0b0000_1111;
			rest = b & 0b0000_0011;
			restBitsCount = 2;
		}
		else if (restBitsCount == 2) {
			int b = fromBase64[string.charAt(charPos++)];
			result = rest << 6 & 0b1100_0000 | b & 0b0011_1111;
			restBitsCount = 0;
		}
		return (byte)result;
	}
}
