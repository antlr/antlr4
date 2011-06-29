package org.antlr.v4.codegen;


import org.antlr.v4.codegen.model.*;

import java.util.List;

/** Filter list of SrcOps and return; default is pass-through filter */
public class CodeGeneratorExtension {
	public OutputModelFactory factory;

	public CodeGeneratorExtension(OutputModelFactory factory) {
		this.factory = factory;
	}

	public ParserFile parserFile(ParserFile f) { return f; }

	public Parser parser(Parser p) { return p; }

	public LexerFile lexerFile(LexerFile f) { return f; }

	public Lexer lexer(Lexer l) { return l; }

	public RuleFunction rule(RuleFunction rf) { return rf; }

	public List<SrcOp> rulePostamble(List<SrcOp> ops) { return ops; }

	public List<SrcOp> alternative(List<SrcOp> ops) { return ops; }

	public List<SrcOp> ruleRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> tokenRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> stringRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> epsilon(List<SrcOp> ops) { return ops; }

	// ACTIONS

	public List<SrcOp> action(List<SrcOp> ops) { return ops; }

	public List<SrcOp> forcedAction(List<SrcOp> ops) { return ops; }

	public List<SrcOp> sempred(List<SrcOp> ops) { return ops; }

	// AST OPS

	public List<SrcOp> rootToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootRule(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafRule(List<SrcOp> ops) { return ops; }

	// BLOCKS

	public List<SrcOp> getChoiceBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getEBNFBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getLL1ChoiceBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getLLStarChoiceBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getLL1EBNFBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getLLStarEBNFBlock(List<SrcOp> ops) { return ops; }

	public List<SrcOp> getLL1Test(List<SrcOp> ops) { return ops; }
}
