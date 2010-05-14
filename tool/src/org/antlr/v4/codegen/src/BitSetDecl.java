package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntSet;

/** */
public class BitSetDecl extends Decl {
	public IntSet fset;
	public BitSetDecl(OutputModelFactory factory, String name, IntSet fset) {
		super(factory, name);
		this.fset = fset;
	}
}
