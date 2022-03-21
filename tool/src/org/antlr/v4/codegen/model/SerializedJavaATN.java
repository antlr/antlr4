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
		Target target = factory.getGenerator().getTarget();
		IntegerList data = ATNSerializer.getSerialized(atn);
		data = ATNDeserializer.encodeIntsWith16BitWords(data);

//		char[] data16 = new char[data.size()*2];
//		int i2 = 0;
//		for (int i = 0; i < data.size(); i++) {
//			int v = data.get(i);
//			if ( v==-1 ) { // use two max uint16 for -1
//				data16.add(0xFFFF;
//				data16.add(0xFFFF;
//			}
//			else if (v <= 0x7FFF) {
//				data16.add((char)(v & 0x7FFF);
//			}
//			else { // v > 0x7FFF
//				v = v & 0x7FFF_FFFF; // strip high bit (sentinel) if set
//				data16.add((char)(v >> 16);  	// store high 15-bit word first
//				data16.add((char)(v & 0xFFFF);  // then store lower 16-bit word
//			}
//		}

//		// Flip -1 to 0xFFFF and shift by 2 (except version number)
//		for (int i = 1; i < data.size(); i++) {
//			int value = data.get(i);
//			if ( value==-1 ) { // use two max uint16 for -1
//				data.set(i, 0xFFFF);
//				data.set(i, 0xFFFF);
//			}
//			else {
//				if ( value > 0x7FFF ) {
//					data.set(i, value & 0xFFFF);
//					data.set(i, value >> 16);
//				}
////				if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
////					throw new UnsupportedOperationException("Serialized ATN data element " +
////							value + " element " + i + " out of range " + (int) Character.MIN_VALUE + ".." + (int) Character.MAX_VALUE);
////				}
//				// Shift by 2, to avoid inefficient modified utf-8 and coding done by java class files
//				data.set(i, (value + 2) & 0xFFFF);
//			}
//		}

		int size = data.size();
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
