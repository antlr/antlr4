package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

/** */
public class Decl extends SrcOp {
	public String name;
	public String decl; // whole thing if copied from action

	public Decl(OutputModelFactory factory, String name, String decl) {
		this(factory, name);
		this.decl = decl;
	}

	public Decl(OutputModelFactory factory, String name) {
		super(factory);
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(((Decl)obj).name);
	}
}
