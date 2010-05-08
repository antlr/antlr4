package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class Choice extends SrcOp {
	public DFADef dfaDef;
	public List<CodeBlock> alts;

	public Choice(CodeGenerator gen, List<CodeBlock> alts) {
		this.gen = gen;
		this.alts = alts;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("alts"); }};
	}
}
