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
import org.antlr.v4.codegen.model.ast.TreeRewrite;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.List;

public interface OutputModelFactory {
	Grammar getGrammar();

	CodeGenerator getGenerator();

	ParserFile parserFile(String fileName);

	Parser parser(ParserFile file);

	LexerFile lexerFile(String fileName);

	Lexer lexer(LexerFile file);

	RuleFunction rule(Rule r);

	List<SrcOp> rulePostamble(RuleFunction function, Rule r);

	// ELEMENT TRIGGERS

	CodeBlockForAlt alternative(Alternative alt);

	CodeBlockForAlt epsilon();

	List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args);

	List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args);

	List<SrcOp> stringRef(GrammarAST ID, GrammarAST label);

	List<SrcOp> action(GrammarAST ast);

	List<SrcOp> forcedAction(GrammarAST ast);

	List<SrcOp> sempred(GrammarAST ast);

	List<SrcOp> rootToken(List<SrcOp> ops);

	List<SrcOp> rootRule(List<SrcOp> ops);

	Choice getChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts);

	Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	Choice getLL1ChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts);

	Choice getLLStarChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts);

	Choice getLL1EBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	Choice getLLStarEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts);

	List<SrcOp> getLL1Test(IntervalSet look, GrammarAST blkAST);

	boolean needsImplicitLabel(GrammarAST ID, LabeledOp op);

	// AST REWRITE TRIGGERS
	// Though dealing with ASTs, we must deal with here since these are
	// triggered from elements in ANTLR's internal GrammarAST

	TreeRewrite treeRewrite(GrammarAST ast, int rewriteLevel);

	List<SrcOp> rewrite_tree(GrammarAST root, List<SrcOp> ops);

	List<SrcOp> rewrite_ruleRef(GrammarAST ID, boolean isRoot);

	List<SrcOp> rewrite_tokenRef(GrammarAST ID, boolean isRoot);

	List<SrcOp> rewrite_stringRef(GrammarAST ID, boolean isRoot);

	// CONTEXT MANIPULATION

	OutputModelObject getRoot();

	void setRoot(OutputModelObject root);

	RuleFunction getCurrentRuleFunction();

	void pushCurrentRule(RuleFunction r);

	RuleFunction popCurrentRule();

	Alternative getCurrentAlt();

	void setCurrentAlt(Alternative currentAlt);

	void setController(OutputModelController controller);

	void setCurrentBlock(CodeBlock blk);

	CodeBlock getCurrentBlock();
}
