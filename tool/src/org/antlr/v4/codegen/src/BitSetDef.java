package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntSet;

/** */
public class BitSetDef extends OutputModelObject {
	public String name;
	public IntSet fset;
	public BitSetDef(OutputModelFactory factory, String name, IntSet fset) {
		this.factory = factory;
		this.name = name;
		this.fset = fset;
	}
}
