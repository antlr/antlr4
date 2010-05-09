package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntSet;

/** */
public class BitSetDef extends OutputModelObject {
	public String name;
	public IntSet fset;
	public BitSetDef(CodeGenerator gen, String name, IntSet fset) {
		this.gen = gen;
		this.name = name;
		this.fset = fset;
	}
}
