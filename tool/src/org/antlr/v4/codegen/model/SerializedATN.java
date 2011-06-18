package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.ATN;

import java.util.*;

public class SerializedATN extends OutputModelObject {
	// TODO: make this into a kind of decl or multiple?
	public List<String> serialized;
	public SerializedATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		List<Integer> data = atn.getSerialized();
		serialized = new ArrayList<String>(data.size());
		for (int c : data) {
			String encoded = factory.gen.target.encodeIntAsCharEscape(c);
			serialized.add(encoded);
		}
	}
}
