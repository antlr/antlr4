package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.misc.LABitSet;

import java.util.*;

/** */
public class BitSetDecl extends Decl {
	public LABitSet fset; // runtime bitset
	public List<String> hexWords;
	public BitSetDecl(OutputModelFactory factory, String name, IntervalSet fset) {
		super(factory, name);
		this.fset = fset.toRuntimeBitSet();
		long[] words = this.fset.bits;

		hexWords = new ArrayList<String>();
		for (long w : words) {
			String h = factory.gen.target.getTarget64BitStringFromValue(w);
			hexWords.add(h);
		}
	}
}
