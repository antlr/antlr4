/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
