/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.Choice;
import org.antlr.v4.codegen.model.CodeBlockForAlt;
import org.antlr.v4.codegen.model.CodeBlockForOuterMostAlt;
import org.antlr.v4.codegen.model.LabeledOp;
import org.antlr.v4.codegen.model.Lexer;
import org.antlr.v4.codegen.model.LexerFile;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.codegen.model.Parser;
import org.antlr.v4.codegen.model.ParserFile;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.SrcOp;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

public interface OutputModelFactory {
	Grammar getGrammar();

	CodeGenerator getGenerator();

	void setController(OutputModelController controller);

	OutputModelController getController();

	ParserFile parserFile(String fileName);

	Parser parser(ParserFile file);

	LexerFile lexerFile(String fileName);

	Lexer lexer(LexerFile file);

	RuleFunction rule(Rule r);

	List<SrcOp> rulePostamble(RuleFunction function, Rule r);

	// ELEMENT TRIGGERS

	CodeBlockForAlt alternative(Alternative alt, boolean outerMost);

	CodeBlockForAlt finishAlternative(CodeBlockForAlt blk, List<SrcOp> ops);

	CodeBlockForAlt epsilon(Alternative alt, boolean outerMost);

	List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args);

	List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args);

	List<SrcOp> stringRef(GrammarAST ID, GrammarAST label);

	List<SrcOp> set(GrammarAST setAST, GrammarAST label, boolean invert);

	List<SrcOp> wildcard(GrammarAST ast, GrammarAST labelAST);

	List<SrcOp> action(ActionAST ast);

	List<SrcOp> sempred(ActionAST ast);

	Choice getChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts, GrammarAST label);

	Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts);

	Choice getComplexChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts);

	Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	Choice getComplexEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST);

	boolean needsImplicitLabel(GrammarAST ID, LabeledOp op);

	// CONTEXT INFO

	OutputModelObject getRoot();

	RuleFunction getCurrentRuleFunction();

	Alternative getCurrentOuterMostAlt();

	CodeBlock getCurrentBlock();

	CodeBlockForOuterMostAlt getCurrentOuterMostAlternativeBlock();

	int getCodeBlockLevel();

	int getTreeLevel();

}
