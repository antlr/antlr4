package org.antlr.v4.codegen.model.actions;

import org.antlr.v4.codegen.model.ModelElement;

import java.util.List;

/** */
public class SetDynScopeAttr extends ActionChunk {
	public String scope;
	public String attr;
	@ModelElement public List<ActionChunk> rhsChunks;

	public SetDynScopeAttr(String scope, String attr, List<ActionChunk> rhsChunks) {
		this.scope = scope;
		this.attr = attr;
		this.rhsChunks = rhsChunks;
	}
}
