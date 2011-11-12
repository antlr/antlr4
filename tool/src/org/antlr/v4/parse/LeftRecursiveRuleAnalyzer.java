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

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import org.stringtemplate.v4.*;

import java.util.*;

/** */
public class LeftRecursiveRuleAnalyzer extends LeftRecursiveRuleWalker {
	public static enum ASSOC { left, right };

	public Tool tool;
	public String ruleName;
	public LinkedHashMap<Integer, String> binaryAlts = new LinkedHashMap<Integer, String>();
	public LinkedHashMap<Integer, String> ternaryAlts = new LinkedHashMap<Integer, String>();
	public LinkedHashMap<Integer, String> suffixAlts = new LinkedHashMap<Integer, String>();
	public List<String> prefixAlts = new ArrayList<String>();
	public List<String> otherAlts = new ArrayList<String>();

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
		if ( recRuleTemplates==null || !recRuleTemplates.isDefined("recRuleName") ) {
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
	public void binaryAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {
		altTree = altTree.dupTree();

		stripLeftRecursion(altTree);

		// rewrite e to be e_[rec_arg]
		int nextPrec = nextPrecedence(alt);
		ST refST = recRuleTemplates.getInstanceOf("recRuleRef");
		refST.add("ruleName", ruleName);
		refST.add("arg", nextPrec);
		altTree = replaceRuleRefs(altTree, refST.render());

		String altText = text(altTree);
		altText = altText.trim();
		altText += "{}"; // add empty alt to prevent pred hoisting
		ST nameST = recRuleTemplates.getInstanceOf("recRuleName");
		nameST.add("ruleName", ruleName);
		if ( rewriteTree!=null ) {
			rewriteTree = rewriteTree.dupTree();
			rewriteTree = replaceRuleRefs(rewriteTree, "$" + nameST.render());
		}
		String rewriteText = text(rewriteTree);
		binaryAlts.put(alt, altText + " " + rewriteText);
		//System.out.println("binaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	/** Convert e ? e : e  ->  ? e : e_[nextPrec] */
	@Override
	public void ternaryAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {
		altTree = altTree.dupTree();

		stripLeftRecursion(altTree);

		int nextPrec = nextPrecedence(alt);
		ST refST = recRuleTemplates.getInstanceOf("recRuleRef");
		refST.add("ruleName", ruleName);
		refST.add("arg", nextPrec);
		altTree = replaceLastRuleRef(altTree, refST.render());

		String altText = text(altTree);
		altText = altText.trim();
		altText += "{}"; // add empty alt to prevent pred hoisting
		ST nameST = recRuleTemplates.getInstanceOf("recRuleName");
		nameST.add("ruleName", ruleName);
		if ( rewriteTree!=null ) {
			rewriteTree = rewriteTree.dupTree();
			rewriteTree = replaceRuleRefs(rewriteTree, "$" + nameST.render());
		}
		String rewriteText = text(rewriteTree);
		ternaryAlts.put(alt, altText + " " + rewriteText);
		//System.out.println("ternaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void prefixAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {
		altTree = altTree.dupTree();

		int nextPrec = precedence(alt);
		// rewrite e to be e_[rec_arg]
		ST refST = recRuleTemplates.getInstanceOf("recRuleRef");
		refST.add("ruleName", ruleName);
		refST.add("arg", nextPrec);
		altTree = replaceRuleRefs(altTree, refST.render());
		String altText = text(altTree);
		altText = altText.trim();
		altText += "{}"; // add empty alt to prevent pred hoisting

		ST nameST = recRuleTemplates.getInstanceOf("recRuleName");
		nameST.add("ruleName", ruleName);
		if ( rewriteTree!=null ) {
			rewriteTree = rewriteTree.dupTree();
			rewriteTree = replaceRuleRefs(rewriteTree, nameST.render());
		}
		String rewriteText = text(rewriteTree);

		prefixAlts.add(altText + " " + rewriteText);
		//System.out.println("prefixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void suffixAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {
		altTree = altTree.dupTree();
		stripLeftRecursion(altTree);
		ST nameST = recRuleTemplates.getInstanceOf("recRuleName");
		nameST.add("ruleName", ruleName);
		if ( rewriteTree!=null ) {
			rewriteTree = rewriteTree.dupTree();
			rewriteTree = replaceRuleRefs(rewriteTree, "$" + nameST.render());
		}
		String rewriteText = text(rewriteTree);
		String altText = text(altTree);
		altText = altText.trim();
		suffixAlts.put(alt, altText + " " + rewriteText);
//		System.out.println("suffixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void otherAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {
		altTree = altTree.dupTree();
		if ( rewriteTree!=null ) rewriteTree = rewriteTree.dupTree();
		stripLeftRecursion(altTree);
		String altText = text(altTree);

		String rewriteText = text(rewriteTree);
		otherAlts.add(altText + " " + rewriteText);
		//System.out.println("otherAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	// --------- get transformed rules ----------------

	public String getArtificialPrecStartRule() {
		ST ruleST = recRuleTemplates.getInstanceOf("recRuleStart");
		ruleST.add("ruleName", ruleName);
		ruleST.add("minPrec", 0);
		ruleST.add("userRetvals", retvals);
		fillRetValAssignments(ruleST, "recRuleName");

		System.out.println(ruleST.render());
		return ruleST.render();
	}

	public String getArtificialOpPrecRule(boolean buildAST) {
		ST ruleST = recRuleTemplates.getInstanceOf("recRule");
		ruleST.add("ruleName", ruleName);
		ruleST.add("buildAST", buildAST);
		ST argDefST =
			codegenTemplates.getInstanceOf("recRuleDefArg");
		ruleST.add("precArgDef", argDefST);
		ST ruleArgST =
			codegenTemplates.getInstanceOf("recRuleArg");
		ruleST.add("argName", ruleArgST);
		ST setResultST =
			codegenTemplates.getInstanceOf("recRuleSetResultAction");
		ruleST.add("setResultAction", setResultST);
		ruleST.add("userRetvals", retvals);
		fillRetValAssignments(ruleST, "recPrimaryName");

		LinkedHashMap<Integer, String> opPrecRuleAlts = new LinkedHashMap<Integer, String>();
		opPrecRuleAlts.putAll(binaryAlts);
		opPrecRuleAlts.putAll(ternaryAlts);
		opPrecRuleAlts.putAll(suffixAlts);
		for (int alt : opPrecRuleAlts.keySet()) {
			String altText = opPrecRuleAlts.get(alt);
			ST altST = recRuleTemplates.getInstanceOf("recRuleAlt");
			ST predST =
				codegenTemplates.getInstanceOf("recRuleAltPredicate");
			predST.add("opPrec", precedence(alt));
			predST.add("ruleName", ruleName);
			altST.add("pred", predST);
			altST.add("alt", altText);
			ruleST.add("alts", altST);
		}

		System.out.println(ruleST.render());

		return ruleST.render();
	}

	public String getArtificialPrimaryRule() {
		ST ruleST = recRuleTemplates.getInstanceOf("recPrimaryRule");
		ruleST.add("ruleName", ruleName);
		ruleST.add("alts", prefixAlts);
		ruleST.add("alts", otherAlts);
		ruleST.add("userRetvals", retvals);
		System.out.println(ruleST.render());
		return ruleST.render();
	}

	public GrammarAST replaceRuleRefs(GrammarAST t, String name) {
		if ( t==null ) return null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) {
			if ( rref.getText().equals(ruleName) ) rref.setText(name);
		}
		return t;
	}

	/**
	 * Match (RULE ID (BLOCK (ALT .*) (ALT RULE_REF[self] .*) (ALT .*)))
	 */
	public static boolean hasImmediateRecursiveRuleRefs(GrammarAST t, String ruleName) {
		if ( t==null ) return false;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) {
			if ( rref.getChildIndex()==0 && rref.getText().equals(ruleName) ) return true;
		}
		return false;
	}

	public GrammarAST replaceLastRuleRef(GrammarAST t, String name) {
		if ( t==null ) return null;
		GrammarAST last = null;
		for (GrammarAST rref : t.getNodesWithType(RULE_REF)) { last = rref; }
		if ( last !=null && last.getText().equals(ruleName) ) last.setText(name);
		return t;
	}

	public void stripLeftRecursion(GrammarAST altAST) {
		GrammarAST rref = (GrammarAST)altAST.getChild(0);
		if ( rref.getType()== ANTLRParser.RULE_REF &&
			 rref.getText().equals(ruleName))
		{
			// remove rule ref
			altAST.deleteChild(0);
			// reset index so it prints properly
			GrammarAST newFirstChild = (GrammarAST) altAST.getChild(0);
			altAST.setTokenStartIndex(newFirstChild.getTokenStartIndex());
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

	public void fillRetValAssignments(ST ruleST, String srcName) {
		if ( retvals==null ) return;

		// complicated since we must be target-independent
		AttributeDict args = ScopeParser.parseTypedArgList(retvals.token.getText());

		for (String name : args.attributes.keySet()) {
			ST setRetValST =
				codegenTemplates.getInstanceOf("recRuleSetReturnAction");
			ST ruleNameST = recRuleTemplates.getInstanceOf(srcName);
			ruleNameST.add("ruleName", ruleName);
			setRetValST.add("src", ruleNameST);
			setRetValST.add("name", name);
			ruleST.add("userRetvalAssignments",setRetValST);
		}
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
