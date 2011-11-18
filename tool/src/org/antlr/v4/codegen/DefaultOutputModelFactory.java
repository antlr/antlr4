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

package org.antlr.v4.codegen;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** Create output objects for elements *within* rule functions except
 *  buildOutputModel() which builds outer/root model object and any
 *  objects such as RuleFunction that surround elements in rule
 *  functions.
 */
public abstract class DefaultOutputModelFactory extends BlankOutputModelFactory {
	// Interface to outside world
	@NotNull
	public final Grammar g;
	@NotNull
	public final CodeGenerator gen;

	public OutputModelController controller;

	protected DefaultOutputModelFactory(@NotNull CodeGenerator gen) {
		this.gen = gen;
		this.g = gen.g;
	}

	@Override
	public void setController(OutputModelController controller) {
		this.controller = controller;
	}

	// Convenience methods

	@NotNull
	public Grammar getGrammar() { return g; }

	@Override
	public CodeGenerator getGenerator() { return gen; }

	@Override
	public OutputModelObject getRoot() { return controller.getRoot(); }

	@Override
	public RuleFunction getCurrentRuleFunction() { return controller.getCurrentRuleFunction(); }

	@Override
	public Alternative getCurrentOuterMostAlt() { return controller.getCurrentOuterMostAlt(); }

	@Override
	public CodeBlock getCurrentBlock() { return controller.getCurrentBlock(); }

	@Override
	public CodeBlock getCurrentOuterMostAlternativeBlock() { return controller.getCurrentOuterMostAlternativeBlock(); }

	@Override
	public int getCodeBlockLevel() { return controller.codeBlockLevel; }

	@Override
	public int getTreeLevel() { return controller.treeLevel; }

	@Override
	public TreeParserFile treeParserFile(String fileName) {
		return null;
	}

	@Override
	public TreeParserModel treeParser(TreeParserFile file) {
		return null;
	}

	@Override
	public MatchTree tree(GrammarAST treeBeginAST, List<? extends SrcOp> omos) {
		throw new UnsupportedOperationException("^(...) in non-tree grammar");
	}

	// MISC

	@NotNull
	public static List<SrcOp> list(Object... values) {
		List<SrcOp> x = new ArrayList<SrcOp>(values.length);
		for (Object v : values) {
			if ( v!=null ) {
				if ( v instanceof List<?> ) x.addAll((List) v);
				else x.add((SrcOp)v);
			}
		}
		return x;
	}

	@Nullable
	public Decl getCurrentDeclForName(String name) {
		if ( getCurrentBlock().locals==null ) return null;
		for (Decl d : getCurrentBlock().locals.elements()) {
			if ( d.name.equals(name) ) return d;
		}
		return null;
	}

}
