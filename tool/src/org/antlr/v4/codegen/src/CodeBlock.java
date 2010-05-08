package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

/** */
public class CodeBlock extends SrcOp {
	public List<SrcOp> ops;

	public CodeBlock(CodeGenerator gen) { this.gen = gen; }
	
	public CodeBlock(CodeGenerator gen, List<SrcOp> ops) {
		this.gen = gen;
		this.ops = ops;
	}

	public CodeBlock(CodeGenerator gen, final SrcOp elem) {
		this(gen, new ArrayList<SrcOp>() {{ add(elem); }});
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("ops"); }};
	}
}
