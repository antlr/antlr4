/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.runtime.misc.IntegerList;

public class SerializedATN extends OutputModelObject {
	public final String[] serialized;
	public final String[][] segments;

	public SerializedATN(OutputModelFactory factory, IntegerList data) {
		super(factory);
		Target target = factory.getGenerator().getTarget();
		int size = data.size();
		int segmentLimit = target.getSerializedATNSegmentLimit();
		segments = new String[(int)(((long)size + segmentLimit - 1) / segmentLimit)][];
		int segmentIndex = 0;

		for (int i = 0; i < size; i += segmentLimit) {
			int segmentSize = Math.min(i + segmentLimit, size) - i;
			String[] segment = new String[segmentSize];
			segments[segmentIndex++] = segment;
			for (int j = 0; j < segmentSize; j++) {
				segment[j] = target.encodeIntAsCharEscape(data.get(i + j));
			}
		}

		serialized = segments[0];
	}

	public String[][] getSegments() {
		return segments;
	}
}
