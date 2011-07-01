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

import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class ParserFactory extends DefaultOutputModelFactory {
	public ParserFactory(CodeGenerator gen) { super(gen); }

	public ParserFile parserFile(String fileName) {
		return new ParserFile(this, fileName);
	}

	public Parser parser(ParserFile file) {
		return new Parser(this, file);
	}

	public RuleFunction rule(Rule r) {
		return new RuleFunction(this, r);
	}

	public List<SrcOp> epsilon() { return list(new CodeBlock(this)); }

	public List<SrcOp> alternative(List<SrcOp> elems) { return list(new CodeBlock(this, elems)); }

	public List<SrcOp> action(GrammarAST ast) { return list(new Action(this, ast)); }

	public List<SrcOp> forcedAction(GrammarAST ast) { return list(new ForcedAction(this, ast)); }

	public List<SrcOp> sempred(GrammarAST ast) { return list(new SemPred(this, ast)); }

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		InvokeRule invokeOp = new InvokeRule(this, ID, label);
		// If no manual label and action refs as token/rule not label or
		// we're adding to trees, we need to define implicit label
		if ( controller.needsImplicitLabel(ID, invokeOp) ) defineImplicitLabel(ID, invokeOp);
		AddToLabelList listLabelOp = getListLabel(invokeOp, label);
		return list(invokeOp, listLabelOp);
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		LabeledOp matchOp = new MatchToken(this, (TerminalAST) ID, label);
		if ( controller.needsImplicitLabel(ID, matchOp) ) defineImplicitLabel(ID, matchOp);
		AddToLabelList listLabelOp = getListLabel(matchOp, label);
		return list(matchOp, listLabelOp);
	}

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		return tokenRef(ID, label, null);
	}

	public List<SrcOp> getChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		int decision = ((DecisionState)blkAST.atnState).decision;
		if ( AnalysisPipeline.disjoint(g.decisionLOOK.get(decision)) ) {
			return getLL1ChoiceBlock(blkAST, alts);
		}
		else {
			return getLLStarChoiceBlock(blkAST, alts);
		}
	}

	public List<SrcOp> getEBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		int decision;
		if ( ebnfRoot.getType()==ANTLRParser.POSITIVE_CLOSURE ) {
			decision = ((PlusBlockStartState)ebnfRoot.atnState).loopBackState.decision;
		}
		else if ( ebnfRoot.getType()==ANTLRParser.CLOSURE ) {
			decision = ((BlockStartState)ebnfRoot.atnState).decision;
		}
		else {
			decision = ((DecisionState)ebnfRoot.atnState).decision;
		}
		if ( AnalysisPipeline.disjoint(g.decisionLOOK.get(decision)) ) {
			return getLL1EBNFBlock(ebnfRoot, alts);
		}
		else {
			return getLLStarEBNFBlock(ebnfRoot, alts);
		}
	}

	public List<SrcOp> getLL1ChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		return list(new LL1AltBlock(this, blkAST, alts));
	}

	public List<SrcOp> getLLStarChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		return list(new AltBlock(this, blkAST, alts));
	}

	public List<SrcOp> getLL1EBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		int ebnf = 0;
		if ( ebnfRoot!=null ) ebnf = ebnfRoot.getType();
		Choice c = null;
		switch ( ebnf ) {
			case ANTLRParser.OPTIONAL :
				if ( alts.size()==1 ) c = new LL1OptionalBlockSingleAlt(this, ebnfRoot, alts);
				else c = new LL1OptionalBlock(this, ebnfRoot, alts);
				break;
			case ANTLRParser.CLOSURE :
				if ( alts.size()==1 ) c = new LL1StarBlockSingleAlt(this, ebnfRoot, alts);
				else c = new LL1StarBlock(this, ebnfRoot, alts);
				break;
			case ANTLRParser.POSITIVE_CLOSURE :
				if ( alts.size()==1 ) c = new LL1PlusBlockSingleAlt(this, ebnfRoot, alts);
				else c = new LL1PlusBlock(this, ebnfRoot, alts);
				break;
		}
		return list(c);
	}

	public List<SrcOp> getLLStarEBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		int ebnf = 0;
		if ( ebnfRoot!=null ) ebnf = ebnfRoot.getType();
		Choice c = null;
		switch ( ebnf ) {
			case ANTLRParser.OPTIONAL :
				c = new OptionalBlock(this, ebnfRoot, alts);
				break;
			case ANTLRParser.CLOSURE :
				c = new StarBlock(this, ebnfRoot, alts);
				break;
			case ANTLRParser.POSITIVE_CLOSURE :
				c = new PlusBlock(this, ebnfRoot, alts);
				break;
		}
		return list(c);
	}

	public List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST) {
		return list(new TestSetInline(this, blkAST, look));
	}

	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) {
		return op.getLabels().size()==0 &&
		(getCurrentAlt().tokenRefsInActions.containsKey(ID.getText()) ||
		getCurrentAlt().ruleRefsInActions.containsKey(ID.getText()));
	}

	// AST REWRITE

	public List<SrcOp> rewrite_ruleRef(GrammarAST ID) {
		return null;
	}

	public List<SrcOp> rewrite_tokenRef(GrammarAST ID) {
		return null;
	}

	// support

	public void defineImplicitLabel(GrammarAST ID, LabeledOp op) {
		Decl d;
		Rule r = g.getRule(ID.getText());
		if ( r!=null ) {
			String implLabel = gen.target.getImplicitRuleLabel(ID.getText());
			String ctxName = gen.target.getRuleFunctionContextStructName(r);
			d = new RuleContextDecl(this, implLabel, ctxName);
		}
		else {
			String implLabel = gen.target.getImplicitTokenLabel(ID.getText());
			d = new TokenDecl(this, implLabel);
		}
		op.getLabels().add(d);
		getCurrentRule().addLocalDecl(d);
	}

	public AddToLabelList getListLabel(LabeledOp op, GrammarAST label) {
		AddToLabelList labelOp = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			String listLabel = gen.target.getListLabel(label.getText());
			labelOp = new AddToLabelList(this, listLabel, op);
		}
		return labelOp;
	}

}
