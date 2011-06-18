package org.antlr.v4.codegen.model.actions;

import org.antlr.v4.codegen.model.ModelElement;

import java.util.List;

/** */
public class DynScopeAttrRef_index extends DynScopeAttrRef {
	@ModelElement public List<ActionChunk> indexChunks;

	public DynScopeAttrRef_index(String scope, String attr, List<ActionChunk> indexChunks) {
		super(scope, attr);
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
