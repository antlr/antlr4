package org.antlr.v4.runtime.atn;

import java.nio.ByteBuffer;

public class ATNDataReaderByteBuffer extends ATNDataReader {
	private final ByteBuffer byteBuffer;

	public ATNDataReaderByteBuffer(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
		if (byteBuffer.position() != 0) {
			byteBuffer.flip();
		}
	}

	@Override
	public byte readByte() {
		return byteBuffer.get();
	}
}
