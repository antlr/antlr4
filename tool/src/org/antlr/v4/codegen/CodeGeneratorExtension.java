/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;


import org.antlr.v4.codegen.model.Choice;
import org.antlr.v4.codegen.model.CodeBlockForAlt;
import org.antlr.v4.codegen.model.LabeledOp;
import org.antlr.v4.codegen.model.Lexer;
import org.antlr.v4.codegen.model.LexerFile;
import org.antlr.v4.codegen.model.Parser;
import org.antlr.v4.codegen.model.ParserFile;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.SrcOp;
import org.antlr.v4.tool.ast.GrammarAST;

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

	public CodeBlockForAlt alternative(CodeBlockForAlt blk, boolean outerMost) { return blk; }

	public CodeBlockForAlt finishAlternative(CodeBlockForAlt blk, boolean outerMost) { return blk; }

	public CodeBlockForAlt epsilon(CodeBlockForAlt blk) { return blk; }

	public List<SrcOp> ruleRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> tokenRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> set(List<SrcOp> ops) { return ops; }

	public List<SrcOp> stringRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> wildcard(List<SrcOp> ops) { return ops; }

	// ACTIONS

	public List<SrcOp> action(List<SrcOp> ops) { return ops; }

	public List<SrcOp> sempred(List<SrcOp> ops) { return ops; }

	// BLOCKS

	public Choice getChoiceBlock(Choice c) { return c; }

	public Choice getEBNFBlock(Choice c) { return c; }

	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) { return false; }
}
