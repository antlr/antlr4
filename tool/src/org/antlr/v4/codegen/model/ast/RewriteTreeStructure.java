/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen.model.ast;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** ^(A B C) */
public class RewriteTreeStructure extends SrcOp {
	public int treeLevel;
	public int codeBlockLevel;

	@ModelElement public List<SrcOp> ops;
	@ModelElement public OrderedHashSet<Decl> locals;

	public RewriteTreeStructure(OutputModelFactory factory,
								GrammarAST ast,
								int treeLevel,
								int codeBlockLevel)
	{
		super(factory, ast);
		this.treeLevel = treeLevel;
		this.codeBlockLevel = codeBlockLevel;
	}

	/** Add local var decl */
	public void addLocalDecl(Decl d) {
		if ( locals==null ) locals = new OrderedHashSet<Decl>();
		locals.add(d);
		d.isLocal = true;
	}

	public int getEnclosingTreeLevel() { return treeLevel - 1; }
}
