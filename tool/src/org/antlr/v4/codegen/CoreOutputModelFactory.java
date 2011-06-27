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
public abstract class CoreOutputModelFactory implements OutputModelFactory {
	// Interface to outside world
	public Grammar g;
	public CodeGenerator gen;

	// Post-processing
	List<CodeGeneratorExtension> extensions = new ArrayList<CodeGeneratorExtension>();

	// Context ptrs
	public OutputModelObject root; // normally ParserFile, LexerFile, ...
	public Stack<RuleFunction> currentRule = new Stack<RuleFunction>();
	public Alternative currentAlt;

	protected CoreOutputModelFactory(CodeGenerator gen) {
		this.gen = gen;
		this.g = gen.g;
	}

	public OutputModelObject buildOutputModel() {
		return null;
	}

	// ALTERNATIVES / ELEMENTS

	public CodeBlock alternative(List<SrcOp> elems) { return null; }

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) { return null; }

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) { return null; }

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) { return null; }

	public CodeBlock epsilon() { return null; }

	// ACTIONS

	public List<SrcOp> action(GrammarAST ast) { return null; }

	public List<SrcOp> forcedAction(GrammarAST ast) { return null; }

	public List<SrcOp> sempred(GrammarAST ast) { return null; }

	// AST OPS

	public List<SrcOp> rootToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootRule(List<SrcOp> ops) { return ops; }

	// BLOCKS

	public Choice getChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) { return null; }

	public Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) { return null; }

	public Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) { return null; }

	public Choice getLLStarChoiceBlock(BlockAST blkAST, List<CodeBlock> alts) { return null; }

	public Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) { return null; }

	public Choice getLLStarEBNFBlock(GrammarAST ebnfRoot, List<CodeBlock> alts) { return null; }

	public SrcOp getLL1Test(IntervalSet look, GrammarAST blkAST) { return null; }

	public List<SrcOp> list(Object... values) {
		List<SrcOp> x = new ArrayList<SrcOp>(values.length);
		for (Object v : values) {
			if ( v!=null ) {
				if ( v instanceof SrcOp ) x.add((SrcOp)v);
				else if ( v instanceof List<?> ) x.addAll((List)v);
				else g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR,
											 "add type " + v.getClass() + " to list");
			}
		}
		return x;
	}
}

