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
import org.antlr.v4.codegen.model.ast.RuleAST;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;

import java.util.List;

public class ParserASTExtension extends CodeGeneratorExtension {
	public ParserASTExtension(OutputModelFactory factory) {
		super(factory);
	}

	@Override
	public List<SrcOp> getChoiceBlock(List<SrcOp> ops) {
		Choice choice = (Choice)Utils.find(ops, Choice.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() && choice.label!=null ) {
			trackExplicitLabel(choice.preamble, choice.label, choice);
		}
		return ops;
	}

	@Override
	public CodeBlockForAlt alternative(CodeBlockForAlt blk) {
		Alternative alt = factory.getCurrentAlt();
		if ( !alt.hasRewrite() ) blk.addLocalDecl( new RootDecl(factory, 0) );
		return blk;
	}

	@Override
	public CodeBlockForAlt finishAlternative(CodeBlockForAlt blk) {
		Alternative alt = factory.getCurrentAlt();
		if ( !alt.hasRewrite() ) blk.addOp(new AssignTreeResult(factory));
		return blk;
	}

	@Override
	public List<SrcOp> rulePostamble(List<SrcOp> ops) {
		RuleASTCleanup cleanup = new RuleASTCleanup(factory);
		return DefaultOutputModelFactory.list(ops, cleanup);
	}

	@Override
	public List<SrcOp> rootRule(List<SrcOp> ops) {
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			return ops;
		}
		else {
			InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
			SrcOp treeOp = new RuleAST(factory, invokeOp.ast, invokeOp.getLabels().get(0));
			String rootName = factory.getGenerator().target.getRootName(0);
			SrcOp add = new BecomeRoot(factory, rootName, treeOp);
			return DefaultOutputModelFactory.list(ops, add);
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
			SrcOp treeOp = new TokenAST(factory, matchOp.ast, matchOp.getLabels().get(0));
			String rootName = factory.getGenerator().target.getRootName(0);
			SrcOp add = new BecomeRoot(factory, rootName, treeOp);
			return DefaultOutputModelFactory.list(ops, add);
		}
	}

	@Override
	public List<SrcOp> leafRule(List<SrcOp> ops) {
		InvokeRule invokeOp = (InvokeRule)Utils.find(ops, InvokeRule.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			return leafRuleInRewriteAlt(invokeOp, ops);
		}
		else {
			RuleContextDecl label = (RuleContextDecl)invokeOp.getLabels().get(0);
			SrcOp treeOp = new RuleAST(factory, invokeOp.ast, label);
			String rootName = factory.getGenerator().target.getRootName(0);
			SrcOp add = new AddChild(factory, rootName, treeOp);
			ops.add(add);
			return ops;
		}
	}

	@Override
	public List<SrcOp> leafToken(List<SrcOp> ops) {
		MatchToken matchOp = (MatchToken)Utils.find(ops, MatchToken.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			return leafTokenInRewriteAlt(matchOp, ops);
		}
		else {
			TokenDecl label = (TokenDecl)matchOp.getLabels().get(0);
			SrcOp treeOp = new TokenAST(factory, matchOp.ast, label);
			String rootName = factory.getGenerator().target.getRootName(0);
			SrcOp add = new AddChild(factory, rootName, treeOp);
			ops.add(add);
			return ops;
		}
	}

	public List<SrcOp> leafRuleInRewriteAlt(InvokeRule invokeOp, List<SrcOp> ops) {
		RuleContextDecl label = (RuleContextDecl)invokeOp.getLabels().get(0);
		CodeBlock blk = factory.getCurrentAlternativeBlock();
		String elemListName = factory.getGenerator().target.getElementListName(invokeOp.ast.getText());
		blk.addLocalDecl(new ElementListDecl(factory, elemListName));

		// add code to track rule results in _track_r
		String trackName = factory.getGenerator().target.getElementListName(invokeOp.ast.getText());
		TrackRuleElement t = new TrackRuleElement(factory, invokeOp.ast, trackName, label);
		clearTrackingIfSingularLabel(ops, invokeOp, trackName);
		ops.add(t);

		// track any explicit label like _track_label but not implicit label
		if ( !label.isImplicit ) trackExplicitLabel(ops, label, invokeOp);

		return ops;
	}

	public List<SrcOp> leafTokenInRewriteAlt(SrcOp matchOp, List<SrcOp> ops) {
		CodeBlock blk = factory.getCurrentAlternativeBlock();
		TokenDecl label = (TokenDecl)((LabeledOp)matchOp).getLabels().get(0);
		// First declare tracking lists for elements, labels
		// track the named element like _track_A
		String elemListName = factory.getGenerator().target.getElementListName(matchOp.ast.getText());
		blk.addLocalDecl(new ElementListDecl(factory, elemListName));
		// Now, generate track instructions for element and any labels
		// do element
		String trackName = factory.getGenerator().target.getElementListName(matchOp.ast.getText());
		TrackTokenElement t = new TrackTokenElement(factory, matchOp.ast, trackName,
													label);
		clearTrackingIfSingularLabel(ops, matchOp, trackName);
		ops.add(t);
		if ( !label.isImplicit ) trackExplicitLabel(ops, label, matchOp);
		return ops;
	}

	@Override
	public List<SrcOp> wildcard(List<SrcOp> ops) {
		Wildcard wild = (Wildcard)Utils.find(ops, Wildcard.class);
		Alternative alt = factory.getCurrentAlt();
		if ( alt.hasRewrite() ) {
			TokenDecl label = (TokenDecl)((LabeledOp)wild).getLabels().get(0);
			if ( !label.isImplicit ) trackExplicitLabel(ops, label, wild);
			return ops;
		}
		else {
			TokenDecl label = (TokenDecl)wild.getLabels().get(0);
			SrcOp treeOp = new TokenAST(factory, wild.ast, label);
			String rootName = factory.getGenerator().target.getRootName(0);
			SrcOp add = new AddChild(factory, rootName, treeOp);
			ops.add(add);
			return ops;
		}
	}

	public void trackExplicitLabel(List<SrcOp> ops, Decl label, SrcOp opWithLabel) {
		CodeBlock blk = factory.getCurrentAlternativeBlock();
		// declare _track_label
		String labelListName =
			factory.getGenerator().target.getElementListName(label.name);
		blk.addLocalDecl(new ElementListDecl(factory, labelListName));

		// add elements to _track_label
		SrcOp trk;
		if ( opWithLabel instanceof InvokeRule ) {
			trk = new TrackRuleElement(factory, opWithLabel.ast, labelListName, label);
		}
		else if ( opWithLabel instanceof Choice ||
				  opWithLabel instanceof MatchToken ||
				  opWithLabel instanceof Wildcard )
		{
			trk = new TrackTokenElement(factory, opWithLabel.ast, labelListName, label);
		}
		else {
			trk = null;
		}
		clearTrackingIfSingularLabel(ops, opWithLabel, labelListName);
		ops.add(trk);
	}

	public void clearTrackingIfSingularLabel(List<SrcOp> ops, SrcOp opWithLabel, String trackName) {
		if ( opWithLabel.ast.parent.getType() == ANTLRParser.ASSIGN ) {
			// if x=A must keep it a single-element list; clear before add
			ClearElementList c = new ClearElementList(factory, opWithLabel.ast, trackName);
			ops.add(c);
		}
	}

	@Override
	public List<SrcOp> stringRef(List<SrcOp> ops) {	return leafToken(ops); }

	@Override
	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) {
		return op.getLabels().size()==0 && factory.getGrammar().hasASTOption();
	}

	// REWRITES
}
