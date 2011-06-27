package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

import java.util.*;

/** */
public class CodeBlock extends SrcOp {
	@ModelElement public List<SrcOp> ops;

	public CodeBlock(OutputModelFactory factory) { this.factory = factory; }

	public CodeBlock(OutputModelFactory factory, List<SrcOp> ops) {
		super(factory);
		this.ops = ops;
	}

	public CodeBlock(OutputModelFactory factory, final SrcOp elem) {
		this(factory, new ArrayList<SrcOp>() {{ add(elem); }});
	}
}
