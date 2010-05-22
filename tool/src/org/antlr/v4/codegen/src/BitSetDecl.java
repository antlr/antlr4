package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.misc.LABitSet;

/** */
public class BitSetDecl extends Decl {
	public LABitSet fset; // runtime bitset
	public BitSetDecl(OutputModelFactory factory, String name, IntervalSet fset) {
		super(factory, name);
		this.fset = fset.toRuntimeBitSet();
	}
}
