package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;

/** A serialized ATN for the java target, which requires we use strings and 16-bit unicode values */
public class SerializedJavaATN extends SerializedATN {
	private final String[] serializedAsString;
	private final String[][] segments;

	public SerializedJavaATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		IntegerList data = ATNSerializer.getSerialized(atn);
		data = ATNDeserializer.encodeIntsWith16BitWords(data);

		// TODO: Because of the modified UTF-8 encoding used by the java class files, we
		// normally shift up by 2 so that 0 and 1, which are very common, are not encoded in
		// multiple bytes.  This could really screw up the compression I'm using to get 32 bit ints
		// though.  Not sure it's worth messing around with.  Maybe the idea is too use the shift
		// for the common case where there are no large (32 bit) values?
//		// shift by 2 (except version number)
//		for (int i = 1; i < data.size(); i++) {
//			int value = data.get(i);
//			data.set(i, (value + 2) & 0xFFFF);
//			}
//		}

		int size = data.size();
		Target target = factory.getGenerator().getTarget();
		int segmentLimit = target.getSerializedATNSegmentLimit();
		segments = new String[(int)(((long)size + segmentLimit - 1) / segmentLimit)][];
		int segmentIndex = 0;

		for (int i = 0; i < size; i += segmentLimit) {
			int segmentSize = Math.min(i + segmentLimit, size) - i;
			String[] segment = new String[segmentSize];
			segments[segmentIndex++] = segment;
			for (int j = 0; j < segmentSize; j++) {
				segment[j] = target.encodeInt16AsCharEscape(data.get(i + j));
			}
		}

		serializedAsString = segments[0]; // serializedAsString is valid if only one segment
	}

	public Object getSerialized() { return serializedAsString; }
	public String[][] getSegments() { return segments; }
}
