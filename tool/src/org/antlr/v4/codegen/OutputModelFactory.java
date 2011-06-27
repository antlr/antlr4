package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.*;

/** Create output objects for elements *within* rule functions except
 *  buildOutputModel() which builds outer/root model object and any
 *  objects such as RuleFunction that surround elements in rule
 *  functions.
 */
public abstract class OutputModelFactory {
	public Grammar g;
	public CodeGenerator gen;

	// Context ptrs
	public OutputModelObject file; // root
	public Stack<RuleFunction> currentRule = new Stack<RuleFunction>();
	public Alternative currentAlt;

	protected OutputModelFactory(CodeGenerator gen) {
		this.gen = gen;
		this.g = gen.g;
	}

	public OutputModelObject buildOutputModel() {
		return null;
	}

	// ALTERNATIVES / ELEMENTS

	public CodeBlock alternative(List<SrcOp> elems) {
		return null;
	}

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		return null;
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		return null;
	}

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		return null;
	}

	public CodeBlock epsilon() {
		return null;
	}

	// ACTIONS

	public SrcOp action(GrammarAST ast) {
		return null;
	}

	public SrcOp forcedAction(GrammarAST ast) {
		return null;
	}

	public SrcOp sempred(GrammarAST ast) {
		return null;
	}

	// AST OPS

	public List<SrcOp> rootToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootRule(List<SrcOp> ops) { return ops; }

	// BLOCKS

	public Choice getChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) {
		return null;
	}

	public Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) {
		return null;
	}

	public Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) {
		return null;
	}

	public Choice getLLStarChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) {
		return null;
	}

	public Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) {
		return null;
	}

	public Choice getLLStarEBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) {
		return null;
	}

	public SrcOp getLL1Test(IntervalSet look, GrammarAST blkAST) {
		return null;
	}
}

