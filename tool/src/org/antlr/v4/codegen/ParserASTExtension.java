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

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.*;

import java.util.List;

public class ParserASTExtension extends CodeGeneratorExtension {
	public ParserASTExtension(OutputModelFactory factory) {
		super(factory);
	}

	@Override
	public RuleFunction rule(RuleFunction rf) {
		rf.addLocalDecl(new RootDecl(factory, 0));
		return rf;
	}

	@Override
	public List<SrcOp> rulePostamble(List<SrcOp> ops) {
		AssignTreeResult setReturn = new AssignTreeResult(factory);
		return DefaultOutputModelFactory.list(ops, setReturn);
	}

	@Override
	public List<SrcOp> rootRule(List<SrcOp> ops) {
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			return ops;
		}
		else {
			InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
			SrcOp treeOp = new RuleBecomeRoot(factory, invokeOp.ast, invokeOp);
			return DefaultOutputModelFactory.list(ops, treeOp);
		}
	}

	@Override
	public List<SrcOp> rootToken(List<SrcOp> ops) {
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			return ops;
		}
		else {
			MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
			SrcOp treeOp = new TokenBecomeRoot(factory, matchOp.ast, matchOp);
			return DefaultOutputModelFactory.list(ops, treeOp);
		}
	}

	@Override
	public List<SrcOp> leafRule(List<SrcOp> ops) {
		InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			RuleFunction rf = factory.getCurrentRule();
			rf.addLocalDecl(new ElementListDecl(factory, invokeOp.ast));
			TrackRuleElement t = new TrackRuleElement(factory, invokeOp.ast, invokeOp);
			return DefaultOutputModelFactory.list(ops, t);
		}
		else {
			SrcOp treeOp = new AddRuleLeaf(factory, invokeOp.ast, invokeOp);
			return DefaultOutputModelFactory.list(ops, treeOp);
		}
	}

	@Override
	public List<SrcOp> leafToken(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			RuleFunction rf = factory.getCurrentRule();
			rf.addLocalDecl(new ElementListDecl(factory, matchOp.ast));
			TrackTokenElement t = new TrackTokenElement(factory, matchOp.ast, matchOp);
			return DefaultOutputModelFactory.list(ops, t);
		}
		else {
			SrcOp treeOp = new AddTokenLeaf(factory, matchOp.ast, matchOp);
			return DefaultOutputModelFactory.list(ops, treeOp);
		}
	}

	@Override
	public List<SrcOp> stringRef(List<SrcOp> ops) {	return leafToken(ops); }

	@Override
	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) {
		return op.getLabels().size()==0 && factory.getGrammar().hasASTOption();
	}

	// REWRITES


	@Override
	public List<SrcOp> rewrite_ruleRef(List<SrcOp> ops) {
		return super.rewrite_ruleRef(ops);
	}

	@Override
	public List<SrcOp> rewrite_tokenRef(List<SrcOp> ops) {
		return super.rewrite_tokenRef(ops);
	}

	@Override
	public List<SrcOp> rewrite_stringRef(List<SrcOp> ops) {
		return super.rewrite_stringRef(ops);
	}
}
