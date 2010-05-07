package org.antlr.v4.codegen.src;

import org.antlr.v4.tool.Alternative;

import java.util.ArrayList;
import java.util.List;

/** */
public class Choice extends SrcOp {
	public DFADef dfaDef;
	public List<CodeBlock> alts;

	public Choice(Alternative[] alts) {
		
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("alts"); }};
	}
}
