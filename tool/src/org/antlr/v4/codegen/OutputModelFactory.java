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

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;

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

	List<SrcOp> action(GrammarAST ast);

	List<SrcOp> forcedAction(GrammarAST ast);

	List<SrcOp> sempred(GrammarAST ast);

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
