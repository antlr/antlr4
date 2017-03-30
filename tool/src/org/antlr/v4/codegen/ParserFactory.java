/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.codegen.model.Action;
import org.antlr.v4.codegen.model.AddToLabelList;
import org.antlr.v4.codegen.model.AltBlock;
import org.antlr.v4.codegen.model.Choice;
import org.antlr.v4.codegen.model.CodeBlockForAlt;
import org.antlr.v4.codegen.model.CodeBlockForOuterMostAlt;
import org.antlr.v4.codegen.model.InvokeRule;
import org.antlr.v4.codegen.model.LL1AltBlock;
import org.antlr.v4.codegen.model.LL1OptionalBlock;
import org.antlr.v4.codegen.model.LL1OptionalBlockSingleAlt;
import org.antlr.v4.codegen.model.LL1PlusBlockSingleAlt;
import org.antlr.v4.codegen.model.LL1StarBlockSingleAlt;
import org.antlr.v4.codegen.model.LabeledOp;
import org.antlr.v4.codegen.model.LeftRecursiveRuleFunction;
import org.antlr.v4.codegen.model.MatchNotSet;
import org.antlr.v4.codegen.model.MatchSet;
import org.antlr.v4.codegen.model.MatchToken;
import org.antlr.v4.codegen.model.OptionalBlock;
import org.antlr.v4.codegen.model.Parser;
import org.antlr.v4.codegen.model.ParserFile;
import org.antlr.v4.codegen.model.PlusBlock;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.SemPred;
import org.antlr.v4.codegen.model.SrcOp;
import org.antlr.v4.codegen.model.StarBlock;
import org.antlr.v4.codegen.model.TestSetInline;
import org.antlr.v4.codegen.model.Wildcard;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.RuleContextDecl;
import org.antlr.v4.codegen.model.decl.TokenDecl;
import org.antlr.v4.codegen.model.decl.TokenListDecl;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.PlusLoopbackState;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.List;

/** */
public class ParserFactory extends DefaultOutputModelFactory {
	public ParserFactory(CodeGenerator gen) { super(gen); }

	@Override
	public ParserFile parserFile(String fileName) {
		return new ParserFile(this, fileName);
	}

	@Override
	public Parser parser(ParserFile file) {
		return new Parser(this, file);
	}

	@Override
	public RuleFunction rule(Rule r) {
		if ( r instanceof LeftRecursiveRule ) {
			return new LeftRecursiveRuleFunction(this, (LeftRecursiveRule)r);
		}
		else {
			RuleFunction rf = new RuleFunction(this, r);
			return rf;
		}
	}

	@Override
	public CodeBlockForAlt epsilon(Alternative alt, boolean outerMost) {
		return alternative(alt, outerMost);
	}

	@Override
	public CodeBlockForAlt alternative(Alternative alt, boolean outerMost) {
		if ( outerMost ) return new CodeBlockForOuterMostAlt(this, alt);
		return new CodeBlockForAlt(this);
	}

	@Override
	public CodeBlockForAlt finishAlternative(CodeBlockForAlt blk, List<SrcOp> ops) {
		blk.ops = ops;
		return blk;
	}

	@Override
	public List<SrcOp> action(ActionAST ast) { return list(new Action(this, ast)); }

	@Override
	public List<SrcOp> sempred(ActionAST ast) { return list(new SemPred(this, ast)); }

	@Override
	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		InvokeRule invokeOp = new InvokeRule(this, ID, label);
		// If no manual label and action refs as token/rule not label, we need to define implicit label
		if ( controller.needsImplicitLabel(ID, invokeOp) ) defineImplicitLabel(ID, invokeOp);
		AddToLabelList listLabelOp = getAddToListOpIfListLabelPresent(invokeOp, label);
		return list(invokeOp, listLabelOp);
	}

	@Override
	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST labelAST, GrammarAST args) {
		MatchToken matchOp = new MatchToken(this, (TerminalAST) ID);
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			RuleFunction rf = getCurrentRuleFunction();
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
				// add Token _X and List<Token> X decls
				defineImplicitLabel(ID, matchOp); // adds _X
				TokenListDecl l = getTokenListLabelDecl(label);
				rf.addContextDecl(ID.getAltLabel(), l);
			}
			else {
				Decl d = getTokenLabelDecl(label);
				matchOp.labels.add(d);
				rf.addContextDecl(ID.getAltLabel(), d);
			}

//			Decl d = getTokenLabelDecl(label);
//			((MatchToken)matchOp).labels.add(d);
//			getCurrentRuleFunction().addContextDecl(ID.getAltLabel(), d);
//			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
//				TokenListDecl l = getTokenListLabelDecl(label);
//				getCurrentRuleFunction().addContextDecl(ID.getAltLabel(), l);
//			}
		}
		if ( controller.needsImplicitLabel(ID, matchOp) ) defineImplicitLabel(ID, matchOp);
		AddToLabelList listLabelOp = getAddToListOpIfListLabelPresent(matchOp, labelAST);
		return list(matchOp, listLabelOp);
	}

	public Decl getTokenLabelDecl(String label) {
		return new TokenDecl(this, label);
	}

	public TokenListDecl getTokenListLabelDecl(String label) {
		return new TokenListDecl(this, gen.getTarget().getListLabel(label));
	}

	@Override
	public List<SrcOp> set(GrammarAST setAST, GrammarAST labelAST, boolean invert) {
		MatchSet matchOp;
		if ( invert ) matchOp = new MatchNotSet(this, setAST);
		else matchOp = new MatchSet(this, setAST);
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			RuleFunction rf = getCurrentRuleFunction();
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
				defineImplicitLabel(setAST, matchOp);
				TokenListDecl l = getTokenListLabelDecl(label);
				rf.addContextDecl(setAST.getAltLabel(), l);
			}
			else {
				Decl d = getTokenLabelDecl(label);
				matchOp.labels.add(d);
				rf.addContextDecl(setAST.getAltLabel(), d);
			}
		}
		if ( controller.needsImplicitLabel(setAST, matchOp) ) defineImplicitLabel(setAST, matchOp);
		AddToLabelList listLabelOp = getAddToListOpIfListLabelPresent(matchOp, labelAST);
		return list(matchOp, listLabelOp);
	}

	@Override
	public List<SrcOp> wildcard(GrammarAST ast, GrammarAST labelAST) {
		Wildcard wild = new Wildcard(this, ast);
		// TODO: dup with tokenRef
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			Decl d = getTokenLabelDecl(label);
			wild.labels.add(d);
			getCurrentRuleFunction().addContextDecl(ast.getAltLabel(), d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
				TokenListDecl l = getTokenListLabelDecl(label);
				getCurrentRuleFunction().addContextDecl(ast.getAltLabel(), l);
			}
		}
		if ( controller.needsImplicitLabel(ast, wild) ) defineImplicitLabel(ast, wild);
		AddToLabelList listLabelOp = getAddToListOpIfListLabelPresent(wild, labelAST);
		return list(wild, listLabelOp);
	}

	@Override
	public Choice getChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts, GrammarAST labelAST) {
		int decision = ((DecisionState)blkAST.atnState).decision;
		Choice c;
		if ( !g.tool.force_atn && AnalysisPipeline.disjoint(g.decisionLOOK.get(decision)) ) {
			c = getLL1ChoiceBlock(blkAST, alts);
		}
		else {
			c = getComplexChoiceBlock(blkAST, alts);
		}

		if ( labelAST!=null ) { // for x=(...), define x or x_list
			String label = labelAST.getText();
			Decl d = getTokenLabelDecl(label);
			c.label = d;
			getCurrentRuleFunction().addContextDecl(labelAST.getAltLabel(), d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				String listLabel = gen.getTarget().getListLabel(label);
				TokenListDecl l = new TokenListDecl(this, listLabel);
				getCurrentRuleFunction().addContextDecl(labelAST.getAltLabel(), l);
			}
		}

		return c;
	}

	@Override
	public Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) {
		if (!g.tool.force_atn) {
			int decision;
			if ( ebnfRoot.getType()==ANTLRParser.POSITIVE_CLOSURE ) {
				decision = ((PlusLoopbackState)ebnfRoot.atnState).decision;
			}
			else if ( ebnfRoot.getType()==ANTLRParser.CLOSURE ) {
				decision = ((StarLoopEntryState)ebnfRoot.atnState).decision;
			}
			else {
				decision = ((DecisionState)ebnfRoot.atnState).decision;
			}

			if ( AnalysisPipeline.disjoint(g.decisionLOOK.get(decision)) ) {
				return getLL1EBNFBlock(ebnfRoot, alts);
			}
		}

		return getComplexEBNFBlock(ebnfRoot, alts);
	}

	@Override
	public Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts) {
		return new LL1AltBlock(this, blkAST, alts);
	}

	@Override
	public Choice getComplexChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts) {
		return new AltBlock(this, blkAST, alts);
	}

	@Override
	public Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) {
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
				else c = getComplexEBNFBlock(ebnfRoot, alts);
				break;
			case ANTLRParser.POSITIVE_CLOSURE :
				if ( alts.size()==1 ) c = new LL1PlusBlockSingleAlt(this, ebnfRoot, alts);
				else c = getComplexEBNFBlock(ebnfRoot, alts);
				break;
		}
		return c;
	}

	@Override
	public Choice getComplexEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) {
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
		return c;
	}

	@Override
	public List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST) {
		return list(new TestSetInline(this, blkAST, look, gen.getTarget().getInlineTestSetWordSize()));
	}

	@Override
	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) {
		Alternative currentOuterMostAlt = getCurrentOuterMostAlt();
		boolean actionRefsAsToken = currentOuterMostAlt.tokenRefsInActions.containsKey(ID.getText());
		boolean actionRefsAsRule = currentOuterMostAlt.ruleRefsInActions.containsKey(ID.getText());
		return	op.getLabels().isEmpty() &&	(actionRefsAsToken || actionRefsAsRule);
	}

	// support

	public void defineImplicitLabel(GrammarAST ast, LabeledOp op) {
		Decl d;
		if ( ast.getType()==ANTLRParser.SET || ast.getType()==ANTLRParser.WILDCARD ) {
			String implLabel =
				gen.getTarget().getImplicitSetLabel(String.valueOf(ast.token.getTokenIndex()));
			d = getTokenLabelDecl(implLabel);
			((TokenDecl)d).isImplicit = true;
		}
		else if ( ast.getType()==ANTLRParser.RULE_REF ) { // a rule reference?
			Rule r = g.getRule(ast.getText());
			String implLabel = gen.getTarget().getImplicitRuleLabel(ast.getText());
			String ctxName =
				gen.getTarget().getRuleFunctionContextStructName(r);
			d = new RuleContextDecl(this, implLabel, ctxName);
			((RuleContextDecl)d).isImplicit = true;
		}
		else {
			String implLabel = gen.getTarget().getImplicitTokenLabel(ast.getText());
			d = getTokenLabelDecl(implLabel);
			((TokenDecl)d).isImplicit = true;
		}
		op.getLabels().add(d);
		// all labels must be in scope struct in case we exec action out of context
		getCurrentRuleFunction().addContextDecl(ast.getAltLabel(), d);
	}

	public AddToLabelList getAddToListOpIfListLabelPresent(LabeledOp op, GrammarAST label) {
		AddToLabelList labelOp = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			String listLabel = gen.getTarget().getListLabel(label.getText());
			labelOp = new AddToLabelList(this, listLabel, op.getLabels().get(0));
		}
		return labelOp;
	}

}
