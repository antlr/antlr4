/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SerializedATN extends OutputModelObject {
	public final char[] serialized;

	public SerializedATN(OutputModelFactory factory, ByteBuffer atnData) {
		super(factory);
		byte[] encoded = Base64.getEncoder().encode(atnData).array();
		serialized = new char[encoded.length];
		for (int i = 0; i < encoded.length; i++) {
			serialized[i] = (char)encoded[i];
		}
	}

	public char[][] getSegments() {
		List<char[]> segments = new ArrayList<>();
		int segmentLimit = factory.getGenerator().getTarget().getSerializedATNSegmentLimit();
		for (int i = 0; i < serialized.length; i += segmentLimit) {
			char[] currentSegment = new char[Math.min(i + segmentLimit, serialized.length) - i];
			System.arraycopy(serialized, i, currentSegment, 0, currentSegment.length);
			segments.add(currentSegment);
		}

		return segments.toArray(new char[0][]);
	}
}
