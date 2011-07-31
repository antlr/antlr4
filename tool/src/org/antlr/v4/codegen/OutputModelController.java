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

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.parse.*;
import org.antlr.v4.tool.*;

import java.util.*;

/** This receives events from SourceGenTriggers.g and asks factory to do work.
 *  Then runs extensions in order on resulting SrcOps to get final list.
 **/
public class OutputModelController {
	/** Who does the work? Doesn't have to be CoreOutputModelFactory. */
	public OutputModelFactory delegate;

	/** Post-processing CodeGeneratorExtension objects; done in order given. */
	public List<CodeGeneratorExtension> extensions = new ArrayList<CodeGeneratorExtension>();

	/** While walking code in rules, this is set to the tree walker that
	 *  triggers actions.
	 */
	public SourceGenTriggers walker;

	/** Context set by the SourceGenTriggers.g */
	public int codeBlockLevel = -1;
	public int treeLevel = -1;
	public OutputModelObject root; // normally ParserFile, LexerFile, ...
	public Stack<RuleFunction> currentRule = new Stack<RuleFunction>();
	public Alternative currentOuterMostAlt;
	public CodeBlock currentBlock;
	public CodeBlock currentOuterMostAlternativeBlock;


	public OutputModelController(OutputModelFactory factory) {
		this.delegate = factory;
	}

	public void addExtension(CodeGeneratorExtension ext) { extensions.add(ext); }

	/** Build a file with a parser containing rule functions. Use the
	 *  controller as factory in SourceGenTriggers so it triggers codegen
	 *  extensions too, not just the factory functions in this factory.
	 */
	public OutputModelObject buildParserOutputModel() {
		Grammar g = delegate.getGrammar();
		CodeGenerator gen = delegate.getGenerator();
		ParserFile file = parserFile(gen.getRecognizerFileName());
		setRoot(file);
		Parser parser = parser(file);
		file.parser = parser;

		for (Rule r : g.rules.values()) {
			buildRuleFunction(parser, r);
		}

		return file;
	}

	public OutputModelObject buildLexerOutputModel() {
		CodeGenerator gen = delegate.getGenerator();
		LexerFile file = lexerFile(gen.getRecognizerFileName());
		setRoot(file);
		file.lexer = lexer(file);

		Grammar g = delegate.getGrammar();
		for (Rule r : g.rules.values()) {
			buildLexerRuleActions(file.lexer, r);
		}

		return file;
	}

	public ParserFile parserFile(String fileName) {
		ParserFile f = delegate.parserFile(fileName);
		for (CodeGeneratorExtension ext : extensions) f = ext.parserFile(f);
		return f;
	}

	public Parser parser(ParserFile file) {
		Parser p = delegate.parser(file);
		for (CodeGeneratorExtension ext : extensions) p = ext.parser(p);
		return p;
	}

	public LexerFile lexerFile(String fileName) {
		return new LexerFile(delegate, getGenerator().getRecognizerFileName());
	}

	public Lexer lexer(LexerFile file) {
		return new Lexer(delegate, file);
	}

	/** Create RuleFunction per rule and update sempreds,actions of parser
	 *  output object with stuff found in r.
	 */
	public void buildRuleFunction(Parser parser, Rule r) {
		CodeGenerator gen = delegate.getGenerator();
		RuleFunction function = rule(r);
		parser.funcs.add(function);

		// TRIGGER factory functions for rule alts, elements
		pushCurrentRule(function);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(r.ast.token.getInputStream());
		GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
		walker = new SourceGenTriggers(nodes, this);
		try {
			function.code = DefaultOutputModelFactory.list(walker.block(null, null, null)); // walk AST of rule alts/elements
		}
		catch (Exception e){
			e.printStackTrace(System.err);
		}

		function.ctxType = gen.target.getRuleFunctionContextStructName(function);
		function.ruleCtx.name = function.ctxType;

		function.postamble = rulePostamble(function, r);

		Grammar g = getGrammar();
		for (ActionAST a : r.actions) {
			if ( a instanceof PredAST ) {
				PredAST p = (PredAST)a;
				RuleSempredFunction rsf = parser.sempredFuncs.get(r);
				if ( rsf==null ) {
					rsf = new RuleSempredFunction(delegate, r, function.ctxType);
					parser.sempredFuncs.put(r, rsf);
				}
				rsf.actions.put(g.sempreds.get(p), new Action(delegate, p));
			}
		}

		if ( function.ruleCtx.isEmpty() ) function.ruleCtx = null;
		popCurrentRule();
	}

	public void buildLexerRuleActions(Lexer lexer, Rule r) {
		CodeGenerator gen = delegate.getGenerator();
		Grammar g = delegate.getGrammar();
		String ctxType = gen.target.getRuleFunctionContextStructName(r);
		for (ActionAST a : r.actions) {
			if ( a instanceof PredAST ) {
				PredAST p = (PredAST)a;
				RuleSempredFunction rsf = lexer.sempredFuncs.get(r);
				if ( rsf==null ) {
					rsf = new RuleSempredFunction(delegate, r, ctxType);
					lexer.sempredFuncs.put(r, rsf);
				}
				rsf.actions.put(g.sempreds.get(p), new Action(delegate, p));
			}
			else if ( a.getType()== ANTLRParser.ACTION ) {
				RuleActionFunction raf = lexer.sempredFuncs.get(r);
				if ( raf==null ) {
					raf = new RuleActionFunction(delegate, r, ctxType);
					lexer.actionFuncs.put(r, raf);
				}
				raf.actions.put(g.lexerActions.get(a), new ForcedAction(delegate, a));
			}

			if ( a instanceof PredAST ) {
				PredAST p = (PredAST)a;
				RuleSempredFunction rsf = new RuleSempredFunction(delegate, r, ctxType);
				lexer.sempredFuncs.put(r, rsf);
				rsf.actions.put(g.sempreds.get(p), new Action(delegate, p));
			}
			else if ( a.getType()==ANTLRParser.ACTION ) {
				// lexer sees {{...}} and {..} as same; neither are done until accept
				RuleActionFunction raf = new RuleActionFunction(delegate, r, ctxType);
				lexer.actionFuncs.put(r, raf);
				raf.actions.put(g.lexerActions.get(a), new ForcedAction(delegate, a));
			}
		}
	}

	public RuleFunction rule(Rule r) {
		RuleFunction rf = delegate.rule(r);
		for (CodeGeneratorExtension ext : extensions) rf = ext.rule(rf);
		return rf;
	}

	public List<SrcOp> rulePostamble(RuleFunction function, Rule r) {
		List<SrcOp> ops = delegate.rulePostamble(function, r);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rulePostamble(ops);
		return ops;
	}

	public Grammar getGrammar() { return delegate.getGrammar(); }

	public CodeGenerator getGenerator() { return delegate.getGenerator(); }

	public CodeBlockForAlt alternative(Alternative alt, boolean outerMost) {
		CodeBlockForAlt blk = delegate.alternative(alt);
		if ( outerMost ) currentOuterMostAlternativeBlock = blk;
		for (CodeGeneratorExtension ext : extensions) blk = ext.alternative(blk, outerMost);
		return blk;
	}

	public CodeBlockForAlt finishAlternative(CodeBlockForAlt blk, List<SrcOp> ops,
											 boolean outerMost)
	{
		blk = delegate.finishAlternative(blk, ops);
		for (CodeGeneratorExtension ext : extensions) blk = ext.finishAlternative(blk, outerMost);
		return blk;
	}

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args,
								GrammarAST astOp)
	{
		List<SrcOp> ops = delegate.ruleRef(ID, label, args, astOp);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.ruleRef(ops);
			if ( astOp!=null && astOp.getType()==ANTLRParser.ROOT ) {
				ops = ext.rootRule(ops);
			}
			else if ( astOp==null ) {
				ops = ext.leafRule(ops);
			}
		}
		return ops;
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args,
								GrammarAST astOp)
	{
		List<SrcOp> ops = delegate.tokenRef(ID, label, args, astOp);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.tokenRef(ops);
			if ( astOp!=null && astOp.getType()==ANTLRParser.ROOT ) {
				ops = ext.rootToken(ops);
			}
			else if ( astOp==null ) {
				ops = ext.leafToken(ops);
			}
		}
		return ops;
	}

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label, GrammarAST astOp) {
		List<SrcOp> ops = delegate.stringRef(ID, label, astOp);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.stringRef(ops);
			if ( astOp!=null && astOp.getType()==ANTLRParser.ROOT ) {
				ops = ext.rootString(ops);
			}
			else if ( astOp==null ) {
				ops = ext.leafString(ops);
			}
		}
		return ops;
	}

	/** (A|B|C) possibly with ebnfRoot and label */
	public List<SrcOp> set(GrammarAST setAST, GrammarAST labelAST,
						   GrammarAST astOp, boolean invert) {
		List<SrcOp> ops = delegate.set(setAST, labelAST, astOp, invert);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.set(ops);
			if ( astOp!=null && astOp.getType()==ANTLRParser.ROOT ) {
				ops = ext.rootSet(ops);
			}
			else if ( astOp==null ) {
				ops = ext.leafSet(ops);
			}
		}
		return ops;
	}

	public CodeBlockForAlt epsilon() {
		CodeBlockForAlt blk = delegate.epsilon();
		for (CodeGeneratorExtension ext : extensions) blk = ext.epsilon(blk);
		return blk;
	}

	public List<SrcOp> action(GrammarAST ast) {
		List<SrcOp> ops = delegate.action(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.action(ops);
		return ops;
	}

	public List<SrcOp> sempred(GrammarAST ast) {
		List<SrcOp> ops = delegate.sempred(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.sempred(ops);
		return ops;
	}

	public List<SrcOp> wildcard(GrammarAST ast, GrammarAST labelAST, GrammarAST astOp) {
		List<SrcOp> ops = delegate.wildcard(ast, labelAST, astOp);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.set(ops);
			if ( astOp!=null && astOp.getType()==ANTLRParser.ROOT ) {
				ops = ext.rootWildcard(ops);
			}
			else if ( astOp==null ) {
				ops = ext.leafWildcard(ops);
			}
		}
		return ops;
	}

	public Choice getChoiceBlock(BlockAST blkAST, List<CodeBlockForAlt> alts, GrammarAST label) {
		Choice c = delegate.getChoiceBlock(blkAST, alts, label);
		for (CodeGeneratorExtension ext : extensions) c = ext.getChoiceBlock(c);
		return c;
	}

	public Choice getEBNFBlock(GrammarAST ebnfRoot, List<CodeBlockForAlt> alts) {
		Choice c = delegate.getEBNFBlock(ebnfRoot, alts);
		for (CodeGeneratorExtension ext : extensions) c = ext.getEBNFBlock(c);
		return c;
	}

	public boolean needsImplicitLabel(GrammarAST ID, LabeledOp op) {
		boolean needs = delegate.needsImplicitLabel(ID, op);
		for (CodeGeneratorExtension ext : extensions) needs |= ext.needsImplicitLabel(ID, op);
		return needs;
	}

	// REWRITES

	public TreeRewrite treeRewrite(GrammarAST ast) {
		TreeRewrite r = delegate.treeRewrite(ast);
		for (CodeGeneratorExtension ext : extensions) r = ext.treeRewrite(r);
		return r;
	}

	public RewriteChoice rewrite_choice(PredAST pred, List<SrcOp> ops) {
		RewriteChoice r = delegate.rewrite_choice(pred, ops);
		for (CodeGeneratorExtension ext : extensions) r = ext.rewrite_choice(r);
		return r;
	}

	public RewriteTreeOptional rewrite_optional(GrammarAST ast) {
		RewriteTreeOptional o = delegate.rewrite_optional(ast);
		for (CodeGeneratorExtension ext : extensions) o = ext.rewrite_optional(o);
		return o;
	}

	public RewriteTreeClosure rewrite_closure(GrammarAST ast) {
		RewriteTreeClosure c = delegate.rewrite_closure(ast);
		for (CodeGeneratorExtension ext : extensions) c = ext.rewrite_closure(c);
		return c;
	}

	public RewriteTreeStructure rewrite_treeStructure(GrammarAST root) {
		RewriteTreeStructure t = delegate.rewrite_treeStructure(root);
		for (CodeGeneratorExtension ext : extensions) t = ext.rewrite_treeStructure(t);
		return t;
	}

	public List<SrcOp> rewrite_ruleRef(GrammarAST ID, boolean isRoot) {
		List<SrcOp> ops = delegate.rewrite_ruleRef(ID, isRoot);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rewrite_ruleRef(ops);
		return ops;
	}

	public List<SrcOp> rewrite_tokenRef(GrammarAST ID, boolean isRoot, ActionAST argAST) {
		List<SrcOp> ops = delegate.rewrite_tokenRef(ID, isRoot, argAST);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rewrite_tokenRef(ops);
		return ops;
	}

	public List<SrcOp> rewrite_stringRef(GrammarAST ID, boolean isRoot) {
		return rewrite_tokenRef(ID, isRoot, null);
	}

	public List<SrcOp> rewrite_labelRef(GrammarAST ID, boolean isRoot) {
		List<SrcOp> ops = delegate.rewrite_labelRef(ID, isRoot);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rewrite_labelRef(ops);
		return ops;
	}

	public List<SrcOp> rewrite_action(ActionAST action, boolean isRoot) {
		List<SrcOp> ops = delegate.rewrite_action(action, isRoot);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rewrite_action(ops);
		return ops;
	}

	public List<SrcOp> rewrite_epsilon(GrammarAST epsilon) {
		List<SrcOp> ops = delegate.rewrite_epsilon(epsilon);
		for (CodeGeneratorExtension ext : extensions) ops = ext.rewrite_epsilon(ops);
		return ops;
	}

	public OutputModelObject getRoot() { return root; }

	public void setRoot(OutputModelObject root) { this.root = root; }

	public RuleFunction getCurrentRuleFunction() {
		if ( currentRule.size()>0 )	return currentRule.peek();
		return null;
	}

	public void pushCurrentRule(RuleFunction r) { currentRule.push(r); }

	public RuleFunction popCurrentRule() {
		if ( currentRule.size()>0 ) return currentRule.pop();
		return null;
	}

	public Alternative getCurrentOuterMostAlt() { return currentOuterMostAlt; }

	public void setCurrentOuterMostAlt(Alternative currentOuterMostAlt) { this.currentOuterMostAlt = currentOuterMostAlt; }

	public void setCurrentBlock(CodeBlock blk) {
		currentBlock = blk;
	}

	public CodeBlock getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentOuterMostAlternativeBlock(CodeBlock currentOuterMostAlternativeBlock) {
		this.currentOuterMostAlternativeBlock = currentOuterMostAlternativeBlock;
	}

	public CodeBlock getCurrentOuterMostAlternativeBlock() {
		return currentOuterMostAlternativeBlock;
	}

	public int getCodeBlockLevel() { return codeBlockLevel; }

	public int getTreeLevel() { return treeLevel; }
}
