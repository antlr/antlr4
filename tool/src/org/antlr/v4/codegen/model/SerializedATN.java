package org.antlr.v4.codegen.model;

import org.antlr.v4.automata.ATNSerializer;
import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.runtime.atn.ATN;

import java.util.*;

public class SerializedATN extends OutputModelObject {
	// TODO: make this into a kind of decl or multiple?
	public List<String> serialized;
	public SerializedATN(CoreOutputModelFactory factory, ATN atn) {
		super(factory);
		List<Integer> data = ATNSerializer.getSerialized(atn);
		serialized = new ArrayList<String>(data.size());
		for (int c : data) {
			String encoded = factory.gen.target.encodeIntAsCharEscape(c);
			serialized.add(encoded);
		}
	}
}
