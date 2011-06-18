package org.antlr.v4.codegen.model.actions;

import java.util.List;

/** */
public class SetDynScopeAttr_index extends SetDynScopeAttr {
	public List<ActionChunk> indexChunks;
	public SetDynScopeAttr_index(String scope, String attr, List<ActionChunk> indexChunks, List<ActionChunk> rhsChunks) {
		super(scope, attr, rhsChunks);
		this.indexChunks = indexChunks;
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup);
//			add("indexChunks");
//		}};
//	}
}
