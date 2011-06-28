package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.*;

/** This receives events from SourceGenTriggers.g and asks factory to do work.
 *  Then runs extensions in order on resulting SrcOps to get final list.
 **/
public class OutputModelController implements OutputModelFactory {
	/** Who does the work? Doesn't have to be CoreOutputModelFactory. */
	public OutputModelFactory delegate;

	/** Post-processing CodeGeneratorExtension objects */
	List<CodeGeneratorExtension> extensions = new ArrayList<CodeGeneratorExtension>();

	public OutputModelController(OutputModelFactory factory) {
		this.delegate = factory;
	}

	public void addExtension(CodeGeneratorExtension ext) { extensions.add(ext); }

	public OutputModelObject buildOutputModel(OutputModelController controller) {
		OutputModelObject root = delegate.buildOutputModel(this);
		for (CodeGeneratorExtension ext : extensions) root = ext.buildOutputModel(root);
		return root;
	}

	public Grammar getGrammar() { return delegate.getGrammar(); }

	public CodeGenerator getGenerator() { return delegate.getGenerator(); }

	public List<SrcOp> alternative(List<SrcOp> elems) {
		List<SrcOp> ops = delegate.alternative(elems);
		for (CodeGeneratorExtension ext : extensions) ops = ext.alternative(ops);
		return ops;
	}

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		List<SrcOp> ops = delegate.ruleRef(ID, label, args);
		for (CodeGeneratorExtension ext : extensions) ops = ext.ruleRef(ops);
		return ops;
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		List<SrcOp> ops = delegate.tokenRef(ID, label, args);
		for (CodeGeneratorExtension ext : extensions) ops = ext.tokenRef(ops);
		return ops;
	}

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		List<SrcOp> ops = delegate.stringRef(ID, label);
		for (CodeGeneratorExtension ext : extensions) ops = ext.stringRef(ops);
		return ops;
	}

	public List<SrcOp> epsilon() {
		List<SrcOp> ops = delegate.epsilon();
		for (CodeGeneratorExtension ext : extensions) ops = ext.epsilon(ops);
		return ops;
	}

	public List<SrcOp> action(GrammarAST ast) {
		List<SrcOp> ops = delegate.action(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.action(ops);
		return ops;
	}

	public List<SrcOp> forcedAction(GrammarAST ast) {
		List<SrcOp> ops = delegate.forcedAction(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.forcedAction(ops);
		return ops;
	}

	public List<SrcOp> sempred(GrammarAST ast) {
		List<SrcOp> ops = delegate.sempred(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.sempred(ops);
		return ops;
	}

	public List<SrcOp> rootToken(List<SrcOp> ops) {
		ops = delegate.rootToken(ops);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rootToken(ops);
		return ops;
	}

	public List<SrcOp> rootRule(List<SrcOp> ops) {
		ops = delegate.rootRule(ops);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rootRule(ops);
		return ops;
	}

	public List<SrcOp> getChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getChoiceBlock(blkAST, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getChoiceBlock(ops);
		return ops;
	}

	public List<SrcOp> getEBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getEBNFBlock(ebnfRoot, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getEBNFBlock(ops);
		return ops;
	}

	public List<SrcOp> getLL1ChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getLL1ChoiceBlock(blkAST, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getLL1ChoiceBlock(ops);
		return ops;
	}

	public List<SrcOp> getLLStarChoiceBlock(BlockAST blkAST, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getLLStarChoiceBlock(blkAST, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getLLStarChoiceBlock(ops);
		return ops;
	}

	public List<SrcOp> getLL1EBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getLL1EBNFBlock(ebnfRoot, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getLL1EBNFBlock(ops);
		return ops;
	}

	public List<SrcOp> getLLStarEBNFBlock(GrammarAST ebnfRoot, List<SrcOp> alts) {
		List<SrcOp> ops = delegate.getLLStarEBNFBlock(ebnfRoot, alts);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getLLStarEBNFBlock(ops);
		return ops;
	}

	public List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST) {
		List<SrcOp> ops = delegate.getLL1Test(look, blkAST);
		for (CodeGeneratorExtension ext : extensions) ops = ext.getLL1Test(ops);
		return ops;
	}

	public OutputModelObject getRoot() { return delegate.getRoot(); }

	public void setRoot(OutputModelObject root) { delegate.setRoot(root); }

	public RuleFunction getCurrentRule() { return delegate.getCurrentRule(); }

	public void pushCurrentRule(RuleFunction r) { delegate.pushCurrentRule(r); }

	public RuleFunction popCurrentRule() { return delegate.popCurrentRule(); }

	public Alternative getCurrentAlt() { return delegate.getCurrentAlt(); }

	public void setCurrentAlt(Alternative alt) { delegate.setCurrentAlt(alt); }
}
