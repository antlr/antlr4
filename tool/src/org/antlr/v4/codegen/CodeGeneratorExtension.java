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
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.tool.GrammarAST;

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

	public List<SrcOp> forcedAction(List<SrcOp> ops) { return ops; }

	public List<SrcOp> sempred(List<SrcOp> ops) { return ops; }

	// AST OPS

	public List<SrcOp> rootRule(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafRule(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafToken(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootString(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafString(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rootSet(List<SrcOp> ops) { return ops; }

	public List<SrcOp> leafSet(List<SrcOp> ops) { return ops; }

	// BLOCKS

	public Choice getChoiceBlock(Choice c) { return c; }

	public Choice getEBNFBlock(Choice c) { return c; }

	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) { return false; }

	// AST REWRITEs

	public TreeRewrite treeRewrite(TreeRewrite r) { return r; }

	public RewriteChoice rewrite_choice(RewriteChoice r) { return r; }

	public RewriteTreeOptional rewrite_optional(RewriteTreeOptional o) { return o; }

	public RewriteTreeClosure rewrite_closure(RewriteTreeClosure c) { return c; }

	public RewriteTreeStructure rewrite_treeStructure(RewriteTreeStructure t) { return t; }

	public List<SrcOp> rewrite_ruleRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rewrite_tokenRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rewrite_stringRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rewrite_labelRef(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rewrite_action(List<SrcOp> ops) { return ops; }

	public List<SrcOp> rewrite_epsilon(List<SrcOp> ops) { return ops; }
}
