package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.SrcOp;

/** */
public class Decl extends SrcOp {
	public String name;
	public String decl; // whole thing if copied from action
	public boolean isLocal; // if local var (not in RuleContext struct)

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
