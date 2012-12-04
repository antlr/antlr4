/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

public abstract class BlankOutputModelFactory implements OutputModelFactory {
	@Override
	public ParserFile parserFile(String fileName) { return null; }

	@Override
	public Parser parser(ParserFile file) { return null; }

	@Override
	public RuleFunction rule(Rule r) { return null; }

	@Override
	public List<SrcOp> rulePostamble(RuleFunction function, Rule r) { return null; }

	@Override
	public LexerFile lexerFile(String fileName) { return null; }

	@Override
	public Lexer lexer(LexerFile file) { return null; }

	// ALTERNATIVES / ELEMENTS

	@Override
	public CodeBlockForAlt alternative(Alternative alt, boolean outerMost) { return null; }

	@Override
	public CodeBlockForAlt finishAlternative(CodeBlockForAlt blk, List<SrcOp> ops) { return blk; }

	@Override
	public CodeBlockForAlt epsilon(Alternative alt, boolean outerMost) { return null; }

	@Override
	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) { return null; }

	@Override
	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) { return null; }

	@Override
	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) { return tokenRef(ID, label, null); }

	@Override
	public List<SrcOp> set(GrammarAST setAST, GrammarAST label, boolean invert) {	return null; }

	@Override
	public List<SrcOp> wildcard(GrammarAST ast, GrammarAST labelAST) { return null; }

	// ACTIONS

	@Override
	public List<SrcOp> action(ActionAST ast) { return null; }

	@Override
	public List<SrcOp> sempred(ActionAST ast) { return null; }

	// BLOCKS

	@Override
	public Choice getChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts, GrammarAST label) { return null; }

	@Override
	public Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) { return null; }

	@Override
	public Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts) { return null; }

	@Override
	public Choice getComplexChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts) { return null; }

	@Override
	public Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) { return null; }

	@Override
	public Choice getComplexEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) { return null; }

	@Override
	public List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST) { return null; }

	@Override
	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) { return false; }
}

