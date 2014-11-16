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

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.analysis.LeftRecursiveRuleAltInfo;
import org.antlr.v4.codegen.model.Action;
import org.antlr.v4.codegen.model.AltBlock;
import org.antlr.v4.codegen.model.BaseListenerFile;
import org.antlr.v4.codegen.model.BaseVisitorFile;
import org.antlr.v4.codegen.model.Choice;
import org.antlr.v4.codegen.model.CodeBlockForAlt;
import org.antlr.v4.codegen.model.CodeBlockForOuterMostAlt;
import org.antlr.v4.codegen.model.LabeledOp;
import org.antlr.v4.codegen.model.LeftRecursiveRuleFunction;
import org.antlr.v4.codegen.model.Lexer;
import org.antlr.v4.codegen.model.LexerFile;
import org.antlr.v4.codegen.model.ListenerFile;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.codegen.model.Parser;
import org.antlr.v4.codegen.model.ParserFile;
import org.antlr.v4.codegen.model.RuleActionFunction;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.RuleSempredFunction;
import org.antlr.v4.codegen.model.SrcOp;
import org.antlr.v4.codegen.model.StarBlock;
import org.antlr.v4.codegen.model.VisitorFile;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.PredAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
	public CodeBlockForOuterMostAlt currentOuterMostAlternativeBlock;

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

	public OutputModelObject buildListenerOutputModel() {
		CodeGenerator gen = delegate.getGenerator();
		return new ListenerFile(delegate, gen.getListenerFileName());
	}

	public OutputModelObject buildBaseListenerOutputModel() {
		CodeGenerator gen = delegate.getGenerator();
		return new BaseListenerFile(delegate, gen.getBaseListenerFileName());
	}

	public OutputModelObject buildVisitorOutputModel() {
		CodeGenerator gen = delegate.getGenerator();
		return new VisitorFile(delegate, gen.getVisitorFileName());
	}

	public OutputModelObject buildBaseVisitorOutputModel() {
		CodeGenerator gen = delegate.getGenerator();
		return new BaseVisitorFile(delegate, gen.getBaseVisitorFileName());
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
		return new LexerFile(delegate, fileName);
	}

	public Lexer lexer(LexerFile file) {
		return new Lexer(delegate, file);
	}

	/** Create RuleFunction per rule and update sempreds,actions of parser
	 *  output object with stuff found in r.
	 */
	public void buildRuleFunction(Parser parser, Rule r) {
		RuleFunction function = rule(r);
		parser.funcs.add(function);
		pushCurrentRule(function);
		function.fillNamedActions(delegate, r);

		if ( r instanceof LeftRecursiveRule ) {
			buildLeftRecursiveRuleFunction((LeftRecursiveRule)r,
										   (LeftRecursiveRuleFunction)function);
		}
		else {
			buildNormalRuleFunction(r, function);
		}

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

		popCurrentRule();
	}

	public void buildLeftRecursiveRuleFunction(LeftRecursiveRule r, LeftRecursiveRuleFunction function) {
		buildNormalRuleFunction(r, function);

		// now inject code to start alts
		CodeGenerator gen = delegate.getGenerator();
		STGroup codegenTemplates = gen.getTemplates();

		// pick out alt(s) for primaries
		CodeBlockForOuterMostAlt outerAlt = (CodeBlockForOuterMostAlt)function.code.get(0);
		List<CodeBlockForAlt> primaryAltsCode = new ArrayList<CodeBlockForAlt>();
		SrcOp primaryStuff = outerAlt.ops.get(0);
		if ( primaryStuff instanceof Choice ) {
			Choice primaryAltBlock = (Choice) primaryStuff;
			primaryAltsCode.addAll(primaryAltBlock.alts);
		}
		else { // just a single alt I guess; no block
			primaryAltsCode.add((CodeBlockForAlt)primaryStuff);
		}

		// pick out alt(s) for op alts
		StarBlock opAltStarBlock = (StarBlock)outerAlt.ops.get(1);
		CodeBlockForAlt altForOpAltBlock = opAltStarBlock.alts.get(0);
		List<CodeBlockForAlt> opAltsCode = new ArrayList<CodeBlockForAlt>();
		SrcOp opStuff = altForOpAltBlock.ops.get(0);
		if ( opStuff instanceof AltBlock ) {
			AltBlock opAltBlock = (AltBlock)opStuff;
			opAltsCode.addAll(opAltBlock.alts);
		}
		else { // just a single alt I guess; no block
			opAltsCode.add((CodeBlockForAlt)opStuff);
		}

		// Insert code in front of each primary alt to create specialized ctx if there was a label
		for (int i = 0; i < primaryAltsCode.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = r.recPrimaryAlts.get(i);
			if ( altInfo.altLabel==null ) continue;
			ST altActionST = codegenTemplates.getInstanceOf("recRuleReplaceContext");
			altActionST.add("ctxName", Utils.capitalize(altInfo.altLabel));
			Action altAction =
				new Action(delegate, function.altLabelCtxs.get(altInfo.altLabel), altActionST);
			CodeBlockForAlt alt = primaryAltsCode.get(i);
			alt.insertOp(0, altAction);
		}

		// Insert code to set ctx.stop after primary block and before op * loop
		ST setStopTokenAST = codegenTemplates.getInstanceOf("recRuleSetStopToken");
		Action setStopTokenAction = new Action(delegate, function.ruleCtx, setStopTokenAST);
		outerAlt.insertOp(1, setStopTokenAction);

		// Insert code to set _prevctx at start of * loop
		ST setPrevCtx = codegenTemplates.getInstanceOf("recRuleSetPrevCtx");
		Action setPrevCtxAction = new Action(delegate, function.ruleCtx, setPrevCtx);
		opAltStarBlock.addIterationOp(setPrevCtxAction);

		// Insert code in front of each op alt to create specialized ctx if there was an alt label
		for (int i = 0; i < opAltsCode.size(); i++) {
			ST altActionST;
			LeftRecursiveRuleAltInfo altInfo = r.recOpAlts.getElement(i);
			String templateName;
			if ( altInfo.altLabel!=null ) {
				templateName = "recRuleLabeledAltStartAction";
				altActionST = codegenTemplates.getInstanceOf(templateName);
				altActionST.add("currentAltLabel", altInfo.altLabel);
			}
			else {
				templateName = "recRuleAltStartAction";
				altActionST = codegenTemplates.getInstanceOf(templateName);
				altActionST.add("ctxName", Utils.capitalize(r.name));
			}
			altActionST.add("ruleName", r.name);
			// add label of any lr ref we deleted
			altActionST.add("label", altInfo.leftRecursiveRuleRefLabel);
			if (altActionST.impl.formalArguments.containsKey("isListLabel")) {
				altActionST.add("isListLabel", altInfo.isListLabel);
			}
			else if (altInfo.isListLabel) {
				delegate.getGenerator().tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, "isListLabel");
			}
			Action altAction =
				new Action(delegate, function.altLabelCtxs.get(altInfo.altLabel), altActionST);
			CodeBlockForAlt alt = opAltsCode.get(i);
			alt.insertOp(0, altAction);
		}
	}

	public void buildNormalRuleFunction(Rule r, RuleFunction function) {
		CodeGenerator gen = delegate.getGenerator();
		// TRIGGER factory functions for rule alts, elements
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(r.ast.token.getInputStream());
		GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
		walker = new SourceGenTriggers(nodes, this);
		try {
			// walk AST of rule alts/elements
			function.code = DefaultOutputModelFactory.list(walker.block(null, null));
			function.hasLookaheadBlock = walker.hasLookaheadBlock;
		}
		catch (org.antlr.runtime.RecognitionException e){
			e.printStackTrace(System.err);
		}

		function.ctxType = gen.getTarget().getRuleFunctionContextStructName(function);

		function.postamble = rulePostamble(function, r);
	}

	public void buildLexerRuleActions(Lexer lexer, final Rule r) {
		if (r.actions.isEmpty()) {
			return;
		}

		CodeGenerator gen = delegate.getGenerator();
		Grammar g = delegate.getGrammar();
		String ctxType = gen.getTarget().getRuleFunctionContextStructName(r);
		RuleActionFunction raf = lexer.actionFuncs.get(r);
		if ( raf==null ) {
			raf = new RuleActionFunction(delegate, r, ctxType);
		}

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
				raf.actions.put(g.lexerActions.get(a), new Action(delegate, a));
			}
		}

		if (!raf.actions.isEmpty() && !lexer.actionFuncs.containsKey(r)) {
			// only add to lexer if the function actually contains actions
			lexer.actionFuncs.put(r, raf);
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
		CodeBlockForAlt blk = delegate.alternative(alt, outerMost);
		if ( outerMost ) {
			currentOuterMostAlternativeBlock = (CodeBlockForOuterMostAlt)blk;
		}
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

	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		List<SrcOp> ops = delegate.ruleRef(ID, label, args);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.ruleRef(ops);
		}
		return ops;
	}

	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args)
	{
		List<SrcOp> ops = delegate.tokenRef(ID, label, args);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.tokenRef(ops);
		}
		return ops;
	}

	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		List<SrcOp> ops = delegate.stringRef(ID, label);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.stringRef(ops);
		}
		return ops;
	}

	/** (A|B|C) possibly with ebnfRoot and label */
	public List<SrcOp> set(GrammarAST setAST, GrammarAST labelAST, boolean invert) {
		List<SrcOp> ops = delegate.set(setAST, labelAST, invert);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.set(ops);
		}
		return ops;
	}

	public CodeBlockForAlt epsilon(Alternative alt, boolean outerMost) {
		CodeBlockForAlt blk = delegate.epsilon(alt, outerMost);
		for (CodeGeneratorExtension ext : extensions) blk = ext.epsilon(blk);
		return blk;
	}

	public List<SrcOp> wildcard(GrammarAST ast, GrammarAST labelAST) {
		List<SrcOp> ops = delegate.wildcard(ast, labelAST);
		for (CodeGeneratorExtension ext : extensions) {
			ops = ext.set(ops);
		}
		return ops;
	}

	public List<SrcOp> action(ActionAST ast) {
		List<SrcOp> ops = delegate.action(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.action(ops);
		return ops;
	}

	public List<SrcOp> sempred(ActionAST ast) {
		List<SrcOp> ops = delegate.sempred(ast);
		for (CodeGeneratorExtension ext : extensions) ops = ext.sempred(ops);
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

	public OutputModelObject getRoot() { return root; }

	public void setRoot(OutputModelObject root) { this.root = root; }

	public RuleFunction getCurrentRuleFunction() {
		if ( !currentRule.isEmpty() )	return currentRule.peek();
		return null;
	}

	public void pushCurrentRule(RuleFunction r) { currentRule.push(r); }

	public RuleFunction popCurrentRule() {
		if ( !currentRule.isEmpty() ) return currentRule.pop();
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

	public void setCurrentOuterMostAlternativeBlock(CodeBlockForOuterMostAlt currentOuterMostAlternativeBlock) {
		this.currentOuterMostAlternativeBlock = currentOuterMostAlternativeBlock;
	}

	public CodeBlockForOuterMostAlt getCurrentOuterMostAlternativeBlock() {
		return currentOuterMostAlternativeBlock;
	}

	public int getCodeBlockLevel() { return codeBlockLevel; }
}
