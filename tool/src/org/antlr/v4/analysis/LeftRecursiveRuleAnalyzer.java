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

package org.antlr.v4.analysis;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.LeftRecursiveRuleWalker;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;

/** Using a tree walker on the rules, determine if a rule is directly left-recursive and if it follows
 *  our pattern.
 */
public class LeftRecursiveRuleAnalyzer extends LeftRecursiveRuleWalker {
	public static enum ASSOC { left, right }

	public Tool tool;
	public String ruleName;
	public LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> binaryAlts = new LinkedHashMap<Integer, LeftRecursiveRuleAltInfo>();
	public LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> ternaryAlts = new LinkedHashMap<Integer, LeftRecursiveRuleAltInfo>();
	public LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> suffixAlts = new LinkedHashMap<Integer, LeftRecursiveRuleAltInfo>();
	public List<LeftRecursiveRuleAltInfo> prefixAlts = new ArrayList<LeftRecursiveRuleAltInfo>();
	public List<LeftRecursiveRuleAltInfo> otherAlts = new ArrayList<LeftRecursiveRuleAltInfo>();

	/** Pointer to ID node of ^(= ID element) */
	public List<GrammarAST> leftRecursiveRuleRefLabels = new ArrayList<GrammarAST>();

	public GrammarAST retvals;

	public STGroup recRuleTemplates;
	public STGroup codegenTemplates;
	public String language;

	public Map<Integer, ASSOC> altAssociativity = new HashMap<Integer, ASSOC>();

	public LeftRecursiveRuleAnalyzer(TokenStream tokens, GrammarAST ruleAST,
									 Tool tool, String ruleName, String language)
	{
		super(new CommonTreeNodeStream(new GrammarASTAdaptor(ruleAST.token.getInputStream()), ruleAST));
		((CommonTreeNodeStream)input).setTokenStream(tokens);
		this.tool = tool;
		this.ruleName = ruleName;
		this.language = language;
		loadPrecRuleTemplates();
	}

	public void loadPrecRuleTemplates() {
		String templateGroupFile = "org/antlr/v4/tool/templates/LeftRecursiveRules.stg";
		recRuleTemplates = new STGroupFile(templateGroupFile);
		if ( !recRuleTemplates.isDefined("recRule") ) {
			tool.errMgr.toolError(ErrorType.MISSING_CODE_GEN_TEMPLATES, "LeftRecursiveRules");
		}

		// use codegen to get correct language templates; that's it though
		CodeGenerator gen = new CodeGenerator(tool, null, language);
		codegenTemplates = gen.templates;
	}

	@Override
	public void setReturnValues(GrammarAST t) {
		retvals = t;
	}

	@Override
	public void setTokenPrec(GrammarAST t, int alt) {
		ASSOC assoc = ASSOC.left;
		if ( t instanceof GrammarASTWithOptions ) {
			if ( ((GrammarASTWithOptions)t).getOptions()!=null ) {
				String a = ((GrammarASTWithOptions)t).getOptionString("assoc");
				if ( a!=null ) {
					if ( a.equals(ASSOC.right.toString()) ) {
						assoc = ASSOC.right;
					}
					else if ( a.equals(ASSOC.left.toString()) ) {
						assoc = ASSOC.left;
					}
					else {
						tool.errMgr.toolError(ErrorType.ILLEGAL_OPTION_VALUE, "assoc", assoc);
					}
				}
			}
		}

		if ( altAssociativity.get(alt)!=null && altAssociativity.get(alt)!=assoc ) {
			tool.errMgr.toolError(ErrorType.ALL_OPS_NEED_SAME_ASSOC, alt);
		}
		altAssociativity.put(alt, assoc);

		//System.out.println("op " + alt + ": " + t.getText()+", assoc="+assoc);
	}

	@Override
	public void binaryAlt(AltAST altTree, int alt) {
		altTree = (AltAST)altTree.dupTree();

		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		if ( lrlabel!=null ) leftRecursiveRuleRefLabels.add(lrlabel);
		stripAssocOptions(altTree);
		stripAltLabel(altTree);

		// rewrite e to be e_[rec_arg]
		int nextPrec = nextPrecedence(alt);
		altTree = addPrecedenceArgToRules(altTree, nextPrec);

		stripAltLabel(altTree);
		String altText = text(altTree);
		altText = altText.trim();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a = new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel);
		a.nextPrec = nextPrec;
		binaryAlts.put(alt, a);
		//System.out.println("binaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	/** Convert e ? e : e  ->  ? e : e_[nextPrec] */
	@Override
	public void ternaryAlt(AltAST altTree, int alt) {
		altTree = (AltAST)altTree.dupTree();

		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		if ( lrlabel!=null ) leftRecursiveRuleRefLabels.add(lrlabel);
		stripAssocOptions(altTree);
		stripAltLabel(altTree);

		int nextPrec = nextPrecedence(alt);
		altTree = addPrecedenceArgToLastRule(altTree, nextPrec);

		String altText = text(altTree);
		altText = altText.trim();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a = new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel);
		a.nextPrec = nextPrec;
		ternaryAlts.put(alt, a);
		//System.out.println("ternaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void prefixAlt(AltAST altTree, int alt) {
		altTree = (AltAST)altTree.dupTree();
		stripAltLabel(altTree);

		int nextPrec = precedence(alt);
		// rewrite e to be e_[prec]
		altTree = addPrecedenceArgToRules(altTree, nextPrec);
		String altText = text(altTree);
		altText = altText.trim();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a = new LeftRecursiveRuleAltInfo(alt, altText, null, altLabel);
		a.nextPrec = nextPrec;
		prefixAlts.add(a);
		//System.out.println("prefixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void suffixAlt(AltAST altTree, int alt) {
		altTree = (AltAST)altTree.dupTree();
		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		stripAltLabel(altTree);
		if ( lrlabel!=null ) leftRecursiveRuleRefLabels.add(lrlabel);
		String altText = text(altTree);
		altText = altText.trim();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a = new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel);
		suffixAlts.put(alt, a);
//		System.out.println("suffixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void otherAlt(AltAST altTree, int alt) {
		altTree = (AltAST)altTree.dupTree();
		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		stripAltLabel(altTree);
		if ( lrlabel!=null ) leftRecursiveRuleRefLabels.add(lrlabel);
		String altText = text(altTree);
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a = new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel);
//		if ( altLabel!=null ) {
//			a.startAction = codegenTemplates.getInstanceOf("recRuleReplaceContext");
//			a.startAction.add("ctxName", altLabel);
//		}
		otherAlts.add(a);
//		System.out.println("otherAlt " + alt + ": " + altText);
	}

	// --------- get transformed rules ----------------

	public String getArtificialOpPrecRule() {
		ST ruleST = recRuleTemplates.getInstanceOf("recRule");
		ruleST.add("ruleName", ruleName);
		ST argDefST = codegenTemplates.getInstanceOf("recRuleDefArg");
		ruleST.add("precArgDef", argDefST);
		ST ruleArgST = codegenTemplates.getInstanceOf("recRuleArg");
		ruleST.add("argName", ruleArgST);
		ST setResultST = codegenTemplates.getInstanceOf("recRuleSetResultAction");
		ruleST.add("setResultAction", setResultST);
		ruleST.add("userRetvals", retvals);

		LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> opPrecRuleAlts = new LinkedHashMap<Integer, LeftRecursiveRuleAltInfo>();
		opPrecRuleAlts.putAll(binaryAlts);
		opPrecRuleAlts.putAll(ternaryAlts);
		opPrecRuleAlts.putAll(suffixAlts);
		for (int alt : opPrecRuleAlts.keySet()) {
			LeftRecursiveRuleAltInfo altInfo = opPrecRuleAlts.get(alt);
			ST altST = recRuleTemplates.getInstanceOf("recRuleAlt");
			ST predST = codegenTemplates.getInstanceOf("recRuleAltPredicate");
			predST.add("opPrec", precedence(alt));
			predST.add("ruleName", ruleName);
			altST.add("pred", predST);
			altST.add("alt", altInfo);
			ruleST.add("opAlts", altST);
		}

		ruleST.add("primaryAlts", prefixAlts);
		ruleST.add("primaryAlts", otherAlts);

		ruleST.add("leftRecursiveRuleRefLabels", leftRecursiveRuleRefLabels);

		tool.log("left-recursion", ruleST.render());

		return ruleST.render();
	}

	public AltAST addPrecedenceArgToRules(AltAST t, int prec) {
		if ( t==null ) return null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) {
			if ( rref.getText().equals(ruleName) ) {
				rref.setText(ruleName+"["+prec+"]");
			}
		}
		return t;
	}

	public AltAST addPrecedenceArgToLastRule(AltAST t, int prec) {
		if ( t==null ) return null;
		GrammarAST last = null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) { last = rref; }
		if ( last !=null && last.getText().equals(ruleName) ) {
			last.setText(ruleName+"["+prec+"]");
		}
		return t;
	}

	public void stripAssocOptions(GrammarAST t) {
		if ( t==null ) return;
		for (GrammarAST options : t.getNodesWithType(ELEMENT_OPTIONS)) {
			int i=0;
			while ( i<options.getChildCount() ) {
				GrammarAST c = (GrammarAST)options.getChild(i);
				if ( c.getChild(0).getText().equals("assoc") ) {
					options.deleteChild(i); // kill this option
				}
				else {
					i++;
				}
			}
			if ( options.getChildCount()==0 )	{
				Tree parent = options.getParent();
				parent.deleteChild(options.getChildIndex()); // no more options
				return;
			}
		}
	}

	/**
	 * Match (RULE RULE_REF (BLOCK (ALT .*) (ALT RULE_REF[self] .*) (ALT .*)))
	 * Match (RULE RULE_REF (BLOCK (ALT .*) (ALT (ASSIGN ID RULE_REF[self]) .*) (ALT .*)))
	 */
	public static boolean hasImmediateRecursiveRuleRefs(GrammarAST t, String ruleName) {
		if ( t==null ) return false;
		GrammarAST blk = (GrammarAST)t.getFirstChildWithType(BLOCK);
		if ( blk==null ) return false;
		int n = blk.getChildren().size();
		for (int i = 0; i < n; i++) {
			GrammarAST alt = (GrammarAST)blk.getChildren().get(i);
			Tree first = alt.getChild(0);
			if ( first.getType()==RULE_REF && first.getText().equals(ruleName) ) return true;
			Tree rref = first.getChild(1);
			if ( rref!=null && rref.getType()==RULE_REF && rref.getText().equals(ruleName) ) return true;
		}
		return false;
	}

	// TODO: this strips the tree properly, but since text()
	// uses the start of stop token index and gets text from that
	// ineffectively ignores this routine.
	public GrammarAST stripLeftRecursion(GrammarAST altAST) {
		GrammarAST lrlabel=null;
		GrammarAST first = (GrammarAST)altAST.getChild(0);
		Tree rref = first.getChild(1); // if label=rule
		if ( (first.getType()==RULE_REF && first.getText().equals(ruleName)) ||
			 (rref!=null && rref.getType()==RULE_REF && rref.getText().equals(ruleName)) )
		{
			if ( first.getType()==ASSIGN ) lrlabel = (GrammarAST)first.getChild(0);
			// remove rule ref (first child)
			altAST.deleteChild(0);
			// reset index so it prints properly
			GrammarAST newFirstChild = (GrammarAST)altAST.getChild(0);
			altAST.setTokenStartIndex(newFirstChild.getTokenStartIndex());
		}
		return lrlabel;
	}

	/** Strip last 2 tokens if -> label; alter indexes in altAST */
	public void stripAltLabel(GrammarAST altAST) {
		int start = altAST.getTokenStartIndex();
		int stop = altAST.getTokenStopIndex();
		TokenStream tokens = input.getTokenStream();
		// find =>
		for (int i=stop; i>=start; i--) {
			if ( tokens.get(i).getType()==RARROW ) {
				altAST.setTokenStopIndex(i-1);
				return;
			}
		}
	}

	public String text(GrammarAST t) {
		if ( t==null ) return "";
		TokenStream tokens = input.getTokenStream();
		CommonToken ta = (CommonToken)tokens.get(t.getTokenStartIndex());
		CommonToken tb = (CommonToken)tokens.get(t.getTokenStopIndex());
		return tokens.toString(ta, tb);
	}

	public int precedence(int alt) {
		return numAlts-alt+1;
	}

	public int nextPrecedence(int alt) {
		int p = precedence(alt);
		if ( altAssociativity.get(alt)==ASSOC.left ) p++;
		return p;
	}

	@Override
	public String toString() {
		return "PrecRuleOperatorCollector{" +
			   "binaryAlts=" + binaryAlts +
			   ", ternaryAlts=" + ternaryAlts +
			   ", suffixAlts=" + suffixAlts +
			   ", prefixAlts=" + prefixAlts +
			   ", otherAlts=" + otherAlts +
			   '}';
	}
}
