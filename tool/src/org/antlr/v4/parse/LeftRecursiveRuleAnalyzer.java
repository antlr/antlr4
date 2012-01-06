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

package org.antlr.v4.parse;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;

/** */
public class LeftRecursiveRuleAnalyzer extends LeftRecursiveRuleWalker {
	public static enum ASSOC { left, right }

	public static class Alt {
		public Alt(String altText) {
			this(altText, null);
		}
		public Alt(String altText, String leftRecursiveRuleRefLabel) {
			this.altText = altText;
			this.leftRecursiveRuleRefLabel = leftRecursiveRuleRefLabel;
		}
		public String leftRecursiveRuleRefLabel;
		public String altLabel;
		public String altText;
	}

	public Tool tool;
	public String ruleName;
	public LinkedHashMap<Integer, Alt> binaryAlts = new LinkedHashMap<Integer, Alt>();
	public LinkedHashMap<Integer, Alt> ternaryAlts = new LinkedHashMap<Integer, Alt>();
	public LinkedHashMap<Integer, Alt> suffixAlts = new LinkedHashMap<Integer, Alt>();
	public List<Alt> prefixAlts = new ArrayList<Alt>();
	public List<Alt> otherAlts = new ArrayList<Alt>();
	public List<String> leftRecursiveRuleRefLabels = new ArrayList<String>();

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
	public void binaryAlt(GrammarAST altTree, int alt) {
		altTree = altTree.dupTree();

		String label = stripLeftRecursion(altTree);
		leftRecursiveRuleRefLabels.add(label);
		stripAssocOptions(altTree);

		// rewrite e to be e_[rec_arg]
		int nextPrec = nextPrecedence(alt);
		altTree = addPrecedenceArgToRules(altTree, nextPrec);

		String altText = text(altTree);
		altText = altText.trim();
		binaryAlts.put(alt, new Alt(altText, label));
		//System.out.println("binaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	/** Convert e ? e : e  ->  ? e : e_[nextPrec] */
	@Override
	public void ternaryAlt(GrammarAST altTree, int alt) {
		altTree = altTree.dupTree();

		String label = stripLeftRecursion(altTree);
		leftRecursiveRuleRefLabels.add(label);
		stripAssocOptions(altTree);

		int nextPrec = nextPrecedence(alt);
		altTree = addPrecedenceArgToLastRule(altTree, nextPrec);

		String altText = text(altTree);
		altText = altText.trim();
		ternaryAlts.put(alt, new Alt(altText, label));
		//System.out.println("ternaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void prefixAlt(GrammarAST altTree, int alt) {
		altTree = altTree.dupTree();

		int nextPrec = precedence(alt);
		// rewrite e to be e_[prec]
		altTree = addPrecedenceArgToRules(altTree, nextPrec);
		String altText = text(altTree);
		altText = altText.trim();
		prefixAlts.add(new Alt(altText));
		//System.out.println("prefixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void suffixAlt(GrammarAST altTree, int alt) {
		altTree = altTree.dupTree();
		String label = stripLeftRecursion(altTree);
		leftRecursiveRuleRefLabels.add(label);
		String altText = text(altTree);
		altText = altText.trim();
		suffixAlts.put(alt, new Alt(altText, label));
//		System.out.println("suffixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void otherAlt(GrammarAST altTree, int alt) {
		altTree = altTree.dupTree();
		String label = stripLeftRecursion(altTree);
		leftRecursiveRuleRefLabels.add(label);
		String altText = text(altTree);
		otherAlts.add(new Alt(altText, label));
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

		LinkedHashMap<Integer, Alt> opPrecRuleAlts = new LinkedHashMap<Integer, Alt>();
		opPrecRuleAlts.putAll(binaryAlts);
		opPrecRuleAlts.putAll(ternaryAlts);
		opPrecRuleAlts.putAll(suffixAlts);
		for (int alt : opPrecRuleAlts.keySet()) {
			Alt altInfo = opPrecRuleAlts.get(alt);
			ST altST = recRuleTemplates.getInstanceOf("recRuleAlt");
			ST predST = codegenTemplates.getInstanceOf("recRuleAltPredicate");
			ST altActionST = codegenTemplates.getInstanceOf("recRuleAltStartAction");
			altActionST.add("ctxName", ruleName); // todo: handle alt labels
			altActionST.add("ruleName", ruleName);
			altActionST.add("label", altInfo.leftRecursiveRuleRefLabel);
			predST.add("opPrec", precedence(alt));
			predST.add("ruleName", ruleName);
			altST.add("pred", predST);
			altST.add("alt", altInfo.altText);
			altST.add("startAction", altActionST);
			ruleST.add("opAlts", altST);
		}

		ruleST.add("primaryAlts", prefixAlts);
		ruleST.add("primaryAlts", otherAlts);

		ruleST.add("leftRecursiveRuleRefLabels", leftRecursiveRuleRefLabels);

		tool.log("left-recursion", ruleST.render());

		return ruleST.render();
	}

	public GrammarAST addPrecedenceArgToRules(GrammarAST t, int prec) {
		if ( t==null ) return null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) {
			if ( rref.getText().equals(ruleName) ) {
				rref.setText(ruleName+"["+prec+"]");
			}
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

	public GrammarAST addPrecedenceArgToLastRule(GrammarAST t, int prec) {
		if ( t==null ) return null;
		GrammarAST last = null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) { last = rref; }
		if ( last !=null && last.getText().equals(ruleName) ) {
			last.setText(ruleName+"["+prec+"]");
		}
		return t;
	}

	// TODO: this strips the tree properly, but since text()
	// uses the start of stop token index and gets text from that
	// ineffectively ignores this routine.
	public String stripLeftRecursion(GrammarAST altAST) {
		String label=null;
		GrammarAST first = (GrammarAST)altAST.getChild(0);
		Tree rref = first.getChild(1); // if label=rule
		if ( (first.getType()==RULE_REF && first.getText().equals(ruleName)) ||
			 (rref!=null && rref.getType()==RULE_REF && rref.getText().equals(ruleName)) )
		{
			if ( first.getType()==ASSIGN ) label = first.getChild(0).getText();
			// remove rule ref (first child)
			altAST.deleteChild(0);
			// reset index so it prints properly
			GrammarAST newFirstChild = (GrammarAST)altAST.getChild(0);
			altAST.setTokenStartIndex(newFirstChild.getTokenStartIndex());
		}
		return label;
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
