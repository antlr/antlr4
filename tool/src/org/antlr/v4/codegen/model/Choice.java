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

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.TokenTypeDecl;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** The class hierarchy underneath SrcOp is pretty deep but makes sense that,
 *  for example LL1StarBlock is a kind of LL1Loop which is a kind of Choice.
 *  The problem is it's impossible to figure
 *  out how to construct one of these deeply nested objects because of the
 *  long super constructor call chain. Instead, I decided to in-line all of
 *  this and then look for opportunities to re-factor code into functions.
 *  It makes sense to use a class hierarchy to share data fields, but I don't
 *  think it makes sense to factor code using super constructors because
 *  it has too much work to do.
 */
public abstract class Choice extends RuleElement {
	public int decision = -1;
	public Decl label;

	@ModelElement public List<CodeBlockForAlt> alts;
	@ModelElement public List<SrcOp> preamble = new ArrayList<SrcOp>();

	public Choice(OutputModelFactory factory,
				  GrammarAST blkOrEbnfRootAST,
				  List<CodeBlockForAlt> alts)
	{
		super(factory, blkOrEbnfRootAST);
		this.alts = alts;
	}

	public void addPreambleOp(SrcOp op) {
		preamble.add(op);
	}

	public List<String[]> getAltLookaheadAsStringLists(IntervalSet[] altLookSets) {
		List<String[]> altLook = new ArrayList<String[]>();
		for (IntervalSet s : altLookSets) {
			altLook.add(factory.getGenerator().getTarget().getTokenTypesAsTargetLabels(factory.getGrammar(), s.toArray()));
		}
		return altLook;
	}

	public TestSetInline addCodeForLookaheadTempVar(IntervalSet look) {
		List<SrcOp> testOps = factory.getLL1Test(look, ast);
		TestSetInline expr = Utils.find(testOps, TestSetInline.class);
		if (expr != null) {
			Decl d = new TokenTypeDecl(factory, expr.varName);
			factory.getCurrentRuleFunction().addLocalDecl(d);
			CaptureNextTokenType nextType = new CaptureNextTokenType(factory,expr.varName);
			addPreambleOp(nextType);
		}
		return expr;
	}

	public ThrowNoViableAlt getThrowNoViableAlt(OutputModelFactory factory,
												GrammarAST blkAST,
												IntervalSet expecting) {
		return new ThrowNoViableAlt(factory, blkAST, expecting);
	}
}
