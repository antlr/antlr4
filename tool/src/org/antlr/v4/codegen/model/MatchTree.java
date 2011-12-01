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

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.LL1Analyzer;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TreePatternAST;

import java.util.List;

public class MatchTree extends RuleElement {
	public boolean isNullable;

	@ModelElement public SrcOp root;
	@ModelElement public List<? extends SrcOp> leftActions;
	@ModelElement public SrcOp down;
	@ModelElement public List<? extends SrcOp> kids;
	@ModelElement public SrcOp up;
	@ModelElement public List<? extends SrcOp> rightActions;

	public MatchTree(OutputModelFactory factory, GrammarAST ast, List<? extends SrcOp> elems) {
		super(factory, ast);
		TreePatternAST rootNode = (TreePatternAST)ast;
		this.isNullable = isNullable(rootNode);
		List<? extends SrcOp> afterRoot = elems.subList(1, elems.size());
		int downIndex =
			Utils.indexOf(afterRoot, new Utils.Filter<SrcOp>() {
				public boolean select(SrcOp op) {
					return op instanceof MatchToken && ((MatchToken)op).ttype==Token.DOWN;
				}
			});
		downIndex++; // we skipped root
		down = elems.get(downIndex);
		int upIndex =
			Utils.lastIndexOf(elems, new Utils.Filter<SrcOp>() {
				public boolean select(SrcOp op) {
					return op instanceof MatchToken && ((MatchToken) op).ttype == Token.UP;
				}
			});
		up = elems.get(upIndex);
		root = elems.get(0);
		leftActions = elems.subList(1, downIndex);
		rightActions = elems.subList(upIndex+1, elems.size());
		this.kids = elems.subList(downIndex+1, upIndex);
	}

	boolean isNullable(TreePatternAST rootNode) {
		ATNState firstChildState = rootNode.downState.transition(0).target;
		LL1Analyzer analyzer = new LL1Analyzer(firstChildState.atn);
		IntervalSet look = analyzer.LOOK(firstChildState, ParserRuleContext.EMPTY);
		return look.contains(Token.UP);
	}

}
