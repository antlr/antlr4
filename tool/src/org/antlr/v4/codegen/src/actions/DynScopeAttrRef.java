package org.antlr.v4.codegen.src.actions;

/** */
public class DynScopeAttrRef extends ActionChunk {
	public String scope;
	public String attr;

	public DynScopeAttrRef(String scope, String attr) {
		this.attr = attr;
		this.scope = scope;
	}
}
