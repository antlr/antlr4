package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;

import java.util.*;

/** */
public class CodeBlock extends SrcOp {
	@ModelElement public List<SrcOp> ops;

	public CodeBlock(CoreOutputModelFactory factory) { this.factory = factory; }

	public CodeBlock(CoreOutputModelFactory factory, List<SrcOp> ops) {
		super(factory);
		this.ops = ops;
	}

	public CodeBlock(CoreOutputModelFactory factory, final SrcOp elem) {
		this(factory, new ArrayList<SrcOp>() {{ add(elem); }});
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("ops"); }};
//	}
}
