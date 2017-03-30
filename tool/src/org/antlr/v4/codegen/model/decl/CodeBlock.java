/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.ModelElement;
import org.antlr.v4.codegen.model.SrcOp;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock extends SrcOp {
	public int codeBlockLevel;
	public int treeLevel;

	@ModelElement public OrderedHashSet<Decl> locals;
	@ModelElement public List<SrcOp> preamble;
	@ModelElement public List<SrcOp> ops;

	public CodeBlock(OutputModelFactory factory) {
		super(factory);
	}

	public CodeBlock(OutputModelFactory factory, int treeLevel, int codeBlockLevel) {
		super(factory);
		this.treeLevel = treeLevel;
		this.codeBlockLevel = codeBlockLevel;
	}

	/** Add local var decl */
	public void addLocalDecl(Decl d) {
		if ( locals==null ) locals = new OrderedHashSet<Decl>();
		locals.add(d);
		d.isLocal = true;
	}

	public void addPreambleOp(SrcOp op) {
		if ( preamble==null ) preamble = new ArrayList<SrcOp>();
		preamble.add(op);
	}

	public void addOp(SrcOp op) {
		if ( ops==null ) ops = new ArrayList<SrcOp>();
		ops.add(op);
	}

	public void insertOp(int i, SrcOp op) {
		if ( ops==null ) ops = new ArrayList<SrcOp>();
		ops.add(i, op);
	}

	public void addOps(List<SrcOp> ops) {
		if ( this.ops==null ) this.ops = new ArrayList<SrcOp>();
		this.ops.addAll(ops);
	}
}
