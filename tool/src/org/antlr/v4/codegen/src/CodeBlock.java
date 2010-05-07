package org.antlr.v4.codegen.src;

import java.util.ArrayList;
import java.util.List;

/** */
public class CodeBlock extends SrcOp {
	public List<SrcOp> ops;

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("ops"); }};
	}
}
