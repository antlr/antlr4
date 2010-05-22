package org.antlr.v4.codegen.src.actions;

import java.util.List;

/** */
public class SetAttr extends ActionChunk {
	public String name;
	public List<ActionChunk> rhsChunks;

	public SetAttr(String name, List<ActionChunk> rhsChunks) {
		this.name = name;
		this.rhsChunks = rhsChunks;
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup);
//			add("rhsChunks");
//		}};
//	}
}
