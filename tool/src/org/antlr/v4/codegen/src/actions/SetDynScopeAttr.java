package org.antlr.v4.codegen.src.actions;

import java.util.ArrayList;
import java.util.List;

/** */
public class SetDynScopeAttr extends ActionChunk {
	public String scope;
	public String attr;
	public List<ActionChunk> rhsChunks;

	public SetDynScopeAttr(String scope, String attr, List<ActionChunk> rhsChunks) {
		this.scope = scope;
		this.attr = attr;
		this.rhsChunks = rhsChunks;
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{
			if ( sup!=null ) addAll(sup);
			add("rhsChunks");
		}};
	}	
}
