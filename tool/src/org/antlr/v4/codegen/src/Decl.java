package org.antlr.v4.codegen.src;

/** */
public class Decl extends SrcOp {
	public String varName;
	public String type;
	public Decl(String varName, String type) { this.varName = varName; this.type = type; }

	@Override
	public int hashCode() {
		return varName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return varName.equals(((Decl)obj).varName);
	}
}
