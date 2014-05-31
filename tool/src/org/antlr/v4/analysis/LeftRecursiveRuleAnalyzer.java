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

package org.antlr.v4.analysis;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.LeftRecursiveRuleWalker;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.antlr.v4.tool.ast.RuleRefAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	public List<Pair<GrammarAST,String>> leftRecursiveRuleRefLabels =
		new ArrayList<Pair<GrammarAST,String>>();

	/** Tokens from which rule AST comes from */
	public final TokenStream tokenStream;

	public GrammarAST retvals;

	public STGroup recRuleTemplates;
	public STGroup codegenTemplates;
	public String language;

	public Map<Integer, ASSOC> altAssociativity = new HashMap<Integer, ASSOC>();

	public LeftRecursiveRuleAnalyzer(GrammarAST ruleAST,
									 Tool tool, String ruleName, String language)
	{
		super(new CommonTreeNodeStream(new GrammarASTAdaptor(ruleAST.token.getInputStream()), ruleAST));
		this.tool = tool;
		this.ruleName = ruleName;
		this.language = language;
		this.tokenStream = ruleAST.g.tokenStream;
		if (this.tokenStream == null) {
			throw new NullPointerException("grammar must have a token stream");
		}

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
		codegenTemplates = gen.getTemplates();
	}

	@Override
	public void setReturnValues(GrammarAST t) {
		retvals = t;
	}

	@Override
	public void setAltAssoc(AltAST t, int alt) {
		ASSOC assoc = ASSOC.left;
		if ( t.getOptions()!=null ) {
			String a = t.getOptionString("assoc");
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

		if ( altAssociativity.get(alt)!=null && altAssociativity.get(alt)!=assoc ) {
			tool.errMgr.toolError(ErrorType.ALL_OPS_NEED_SAME_ASSOC, alt);
		}
		altAssociativity.put(alt, assoc);

//		System.out.println("setAltAssoc: op " + alt + ": " + t.getText()+", assoc="+assoc);
	}

	@Override
	public void binaryAlt(AltAST originalAltTree, int alt) {
		AltAST altTree = (AltAST)originalAltTree.dupTree();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;

		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		if ( lrlabel!=null ) {
			leftRecursiveRuleRefLabels.add(new Pair<GrammarAST,String>(lrlabel,altLabel));
		}
//		stripAssocOptions(altTree);
		stripAltLabel(altTree);

		// rewrite e to be e_[rec_arg]
		int nextPrec = nextPrecedence(alt);
		altTree = addPrecedenceArgToRules(altTree, nextPrec);

		stripAltLabel(altTree);
		String altText = text(altTree);
		altText = altText.trim();
		LeftRecursiveRuleAltInfo a =
			new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel, originalAltTree);
		a.nextPrec = nextPrec;
		binaryAlts.put(alt, a);
		//System.out.println("binaryAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void prefixAlt(AltAST originalAltTree, int alt) {
		AltAST altTree = (AltAST)originalAltTree.dupTree();
		stripAltLabel(altTree);

		int nextPrec = precedence(alt);
		// rewrite e to be e_[prec]
		altTree = addPrecedenceArgToRules(altTree, nextPrec);
		String altText = text(altTree);
		altText = altText.trim();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a =
			new LeftRecursiveRuleAltInfo(alt, altText, null, altLabel, originalAltTree);
		a.nextPrec = nextPrec;
		prefixAlts.add(a);
		//System.out.println("prefixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void suffixAlt(AltAST originalAltTree, int alt) {
		AltAST altTree = (AltAST)originalAltTree.dupTree();
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;

		GrammarAST lrlabel = stripLeftRecursion(altTree);
		String label = lrlabel != null ? lrlabel.getText() : null;
		if ( lrlabel!=null ) {
			leftRecursiveRuleRefLabels.add(new Pair<GrammarAST,String>(lrlabel,altLabel));
		}
		stripAltLabel(altTree);
		String altText = text(altTree);
		altText = altText.trim();
		LeftRecursiveRuleAltInfo a =
			new LeftRecursiveRuleAltInfo(alt, altText, label, altLabel, originalAltTree);
		suffixAlts.put(alt, a);
//		System.out.println("suffixAlt " + alt + ": " + altText + ", rewrite=" + rewriteText);
	}

	@Override
	public void otherAlt(AltAST originalAltTree, int alt) {
		AltAST altTree = (AltAST)originalAltTree.dupTree();
		stripAltLabel(altTree);
		String altText = text(altTree);
		String altLabel = altTree.altLabel!=null ? altTree.altLabel.getText() : null;
		LeftRecursiveRuleAltInfo a =
			new LeftRecursiveRuleAltInfo(alt, altText, null, altLabel, originalAltTree);
		otherAlts.add(a);
//		System.out.println("otherAlt " + alt + ": " + altText);
	}

	// --------- get transformed rules ----------------

	public String getArtificialOpPrecRule() {
		ST ruleST = recRuleTemplates.getInstanceOf("recRule");
		ruleST.add("ruleName", ruleName);
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
			altST.add("precOption", LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME);
			altST.add("opPrec", precedence(alt));
			ruleST.add("opAlts", altST);
		}

		ruleST.add("primaryAlts", prefixAlts);
		ruleST.add("primaryAlts", otherAlts);

		tool.log("left-recursion", ruleST.render());

		return ruleST.render();
	}

	public AltAST addPrecedenceArgToRules(AltAST t, int prec) {
		if ( t==null ) return null;
		// get all top-level rule refs from ALT
		List<GrammarAST> outerAltRuleRefs = t.getNodesWithTypePreorderDFS(IntervalSet.of(RULE_REF));
		for (GrammarAST x : outerAltRuleRefs) {
            RuleRefAST rref = (RuleRefAST)x;
			boolean recursive = rref.getText().equals(ruleName);
			boolean rightmost = rref == outerAltRuleRefs.get(outerAltRuleRefs.size()-1);
			if ( recursive && rightmost ) {
                GrammarAST dummyValueNode = new GrammarAST(new CommonToken(ANTLRParser.INT, ""+prec));
                rref.setOption(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME, dummyValueNode);
                //rref.setText(ruleName + "<" + LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME + "=" + prec + ">");
			}
		}
		return t;
	}

    /* no longer needed? routine that generates alt text can deal with assoc if it wants
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
	*/

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
			if ( first==null ) continue;
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
		int leftRecurRuleIndex = 0;
		if ( first.getType() == ELEMENT_OPTIONS ) {
			first = (GrammarAST)altAST.getChild(1);
			leftRecurRuleIndex = 1;
		}
		Tree rref = first.getChild(1); // if label=rule
		if ( (first.getType()==RULE_REF && first.getText().equals(ruleName)) ||
			 (rref!=null && rref.getType()==RULE_REF && rref.getText().equals(ruleName)) )
		{
			if ( first.getType()==ASSIGN ) lrlabel = (GrammarAST)first.getChild(0);
			// remove rule ref (first child unless options present)
			altAST.deleteChild(leftRecurRuleIndex);
			// reset index so it prints properly (sets token range of
			// ALT to start to right of left recur rule we deleted)
			GrammarAST newFirstChild = (GrammarAST)altAST.getChild(leftRecurRuleIndex);
			altAST.setTokenStartIndex(newFirstChild.getTokenStartIndex());
		}
		return lrlabel;
	}

	/** Strip last 2 tokens if -> label; alter indexes in altAST */
	public void stripAltLabel(GrammarAST altAST) {
		int start = altAST.getTokenStartIndex();
		int stop = altAST.getTokenStopIndex();
		// find =>
		for (int i=stop; i>=start; i--) {
			if ( tokenStream.get(i).getType()==POUND ) {
				altAST.setTokenStopIndex(i-1);
				return;
			}
		}
	}

	public String text(GrammarAST t) {
		if ( t==null ) return "";

        StringBuilder buf = new StringBuilder();
        int a = t.getTokenStartIndex();
        int b = t.getTokenStopIndex();

        IntervalSet ignore = new IntervalSet();
        // ignore tokens from existing option subtrees like:
        //      ('+' (ELEMENT_OPTIONS (= assoc right)))
        List<GrammarAST> optionsSubTrees = t.getNodesWithType(ELEMENT_OPTIONS);
        for (GrammarAST sub : optionsSubTrees) {
            ignore.add(sub.getTokenStartIndex(), sub.getTokenStopIndex());
        }
        IntervalSet noOptions = new IntervalSet();
        // No options on labels: (ASSIGN label element)
        List<GrammarAST> labeledSubTrees = t.getNodesWithType(new IntervalSet(ASSIGN,PLUS_ASSIGN));
        for (GrammarAST sub : labeledSubTrees) {
            noOptions.add(sub.getChild(0).getTokenStartIndex());
        }

        String options;
        for (int i=a; i<=b; i++) {
            options = "";
            if ( ignore.contains(i) ) continue;

            Token tok = tokenStream.get(i);
//            System.out.println("token "+tok);
            StringBuilder existingOptions = new StringBuilder();
            GrammarAST node = t.getNodeWithTokenIndex(tok.getTokenIndex());
            if ( node instanceof GrammarASTWithOptions ) {
                GrammarASTWithOptions o = (GrammarASTWithOptions)node;
                Map<String, GrammarAST> opts = o.getOptions();
                if ( opts.size()>0 ) {
                    existingOptions.append(",");
                    for (String key : opts.keySet()) {
                        existingOptions.append(key);
                        existingOptions.append('=');
                        existingOptions.append(opts.get(key).getText());
                    }
                }
            }
            if ( node!=null && !noOptions.contains(i) &&
                 (tok.getType()==TOKEN_REF ||
                  tok.getType()==STRING_LITERAL ||
                  tok.getType()==RULE_REF) )
            {
                options = String.format("<tokenIndex=%d%s>",
                        tok.getTokenIndex(),
                        existingOptions.toString());
            }
            buf.append(tok.getText());
            buf.append(options);
        }
        return buf.toString();
	}

	public int precedence(int alt) {
		return numAlts-alt+1;
	}

	// Assumes left assoc
	public int nextPrecedence(int alt) {
		int p = precedence(alt);
		if ( altAssociativity.get(alt)==ASSOC.right ) return p;
		return p+1;
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
