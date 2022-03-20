package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;

/** A serialized ATN for the java target, which requires we use strings and 16-bit unicode values */
public class SerializedJavaATN extends SerializedATN {
	private final String[] serializedAsString;
	private final String[][] segments;

	public SerializedJavaATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		Target target = factory.getGenerator().getTarget();
		IntegerList data = ATNSerializer.getSerialized(atn, target.getLanguage());

		// Flip -1 to 0xFFFF and shift by 2 (except version number)
		for (int i = 1; i < data.size(); i++) {
			int value = data.get(i);
			if ( value==-1 ) {
				value = 0xFFFF;
			}
			if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
				throw new UnsupportedOperationException("Serialized ATN data element " +
						value + " element " + i + " out of range " + (int) Character.MIN_VALUE + ".." + (int) Character.MAX_VALUE);
			}

			// Shift by 2, to avoid inefficient modified utf-8 and coding done by java class files
			data.set(i, (value + 2) & 0xFFFF);
		}

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

		serializedAsString = segments[0]; // serializedAsString is valid if only one segment
	}

	public Object getSerialized() { return serializedAsString; }
	public String[][] getSegments() { return segments; }
}
