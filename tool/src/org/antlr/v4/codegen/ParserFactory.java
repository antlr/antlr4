package org.antlr.v4.codegen;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.parse.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class ParserFactory extends DefaultOutputModelFactory {
	public ParserFactory(CodeGenerator gen) { super(gen); }

	/** Build a file with a parser containing rule functions. Use the
	 *  controller as factory in SourceGenTriggers so it triggers codegen
	 *  extensions too, not just the factory functions in this factory.
	 **/
	public OutputModelObject buildOutputModel(OutputModelController controller) {
		ParserFile file = new ParserFile(this, gen.getRecognizerFileName());
		setRoot(file);
		Parser parser = new Parser(this, file);
		file.parser = parser;
		for (Rule r : g.rules.values()) {
			RuleFunction function = new RuleFunction(this, r);
			parser.funcs.add(function);

			// TRIGGER factory functions for rule alts, elements
			currentRule.push(function);
			GrammarASTAdaptor adaptor = new GrammarASTAdaptor(r.ast.token.getInputStream());
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			SourceGenTriggers genTriggers = new SourceGenTriggers(nodes, controller);
			try {
				function.code = genTriggers.block(null,null); // walk AST of rule alts/elements
			}
			catch (Exception e){
				e.printStackTrace(System.err);
			}

			function.ctxType = gen.target.getRuleFunctionContextStructName(r);
			function.ruleCtx.name = function.ctxType;

			if ( function.ruleCtx.isEmpty() ) function.ruleCtx = null;
			currentRule.pop();
		}

		return file;
	}


	public List<SrcOp> epsilon() { return list(new CodeBlock(this)); }

	public List<SrcOp> alternative(List<SrcOp> elems) { return list(new CodeBlock(this, elems)); }

	public List<SrcOp> action(GrammarAST ast) { return list(new Action(this, ast)); }

	public List<SrcOp> forcedAction(GrammarAST ast) { return list(new ForcedAction(this, ast)); }

	public List<SrcOp> sempred(GrammarAST ast) { return list(new SemPred(this, ast)); }

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		InvokeRule r = new InvokeRule(this, ID, label);
		AddToLabelList a = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			a = new AddToLabelList(this, gen.target.getListLabel(label.getText()), r);
		}
		return list(r, a);
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		MatchToken matchOp = new MatchToken(this, (TerminalAST) ID, label);
		AddToLabelList labelOp = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			String listLabel = gen.target.getListLabel(label.getText());
			labelOp = new AddToLabelList(this, listLabel, matchOp);
		}
		/*
		SrcOp treeOp = null;
		if ( g.hasASTOption() ) {
			treeOp = new AddLeaf(this, ID, matchOp);
		}
		return list(matchOp, labelOp, treeOp);
		*/
		return list(matchOp, labelOp);
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
}
