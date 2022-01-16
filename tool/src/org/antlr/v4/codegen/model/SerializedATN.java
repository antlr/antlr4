/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;

import java.util.ArrayList;
import java.util.List;

public class SerializedATN extends OutputModelObject {
	// TODO: make this into a kind of decl or multiple?
	public final List<String> serialized;

	public SerializedATN(OutputModelFactory factory, ATN atn) {
		this(factory, ATNSerializer.getSerialized(atn));
	}

	public SerializedATN(OutputModelFactory factory, IntegerList data) {
		super(factory);
		int dataSize = data.size();
		serialized = new ArrayList<>(dataSize);
		Target target = factory.getGenerator().getTarget();
		for (int i = 0; i < dataSize; i++) {
			int c = data.get(i);
			serialized.add(target.encodeIntAsCharEscape(c == -1 ? Character.MAX_VALUE : c));
		}
	}

	public String[][] getSegments() {
		List<String[]> segments = new ArrayList<String[]>();
		int segmentLimit = factory.getGenerator().getTarget().getSerializedATNSegmentLimit();
		for (int i = 0; i < serialized.size(); i += segmentLimit) {
			List<String> currentSegment = serialized.subList(i, Math.min(i + segmentLimit, serialized.size()));
			segments.add(currentSegment.toArray(new String[0]));
		}

		return segments.toArray(new String[0][]);
	}
}
