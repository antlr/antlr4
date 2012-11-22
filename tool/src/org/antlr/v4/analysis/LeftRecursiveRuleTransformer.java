/*
 [The "BSD license"]
 Copyright (c) 2012 Terence Parr
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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.v4.Tool;
import org.antlr.v4.misc.OrderedHashMap;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.parse.ToolANTLRParser;
import org.antlr.v4.runtime.misc.Tuple2;
import org.antlr.v4.tool.AttributeDict;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarTransformPipeline;
import org.antlr.v4.tool.LabelElementPair;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Remove left-recursive rule refs, add precedence args to recursive rule refs.
 *  Rewrite rule so we can create ATN.
 *
 *  MODIFIES grammar AST in place.
 */
public class LeftRecursiveRuleTransformer {
	public GrammarRootAST ast;
	public Collection<Rule> rules;
	public Grammar g;
	public Tool tool;

	public LeftRecursiveRuleTransformer(GrammarRootAST ast, Collection<Rule> rules, Grammar g) {
		this.ast = ast;
		this.rules = rules;
		this.g = g;
		this.tool = g.tool;
	}

	public void translateLeftRecursiveRules() {
		String language = g.getOptionString("language");
		// translate all recursive rules
		List<String> leftRecursiveRuleNames = new ArrayList<String>();
		for (Rule r : rules) {
			if ( !Grammar.isTokenName(r.name) ) {
				if ( LeftRecursiveRuleAnalyzer.hasImmediateRecursiveRuleRefs(r.ast, r.name) ) {
					boolean fitsPattern = translateLeftRecursiveRule(ast, (LeftRecursiveRule)r, language);
					if ( fitsPattern ) leftRecursiveRuleNames.add(r.name);
				}
			}
		}

		// update all refs to recursive rules to have [0] argument
		for (GrammarAST r : ast.getNodesWithType(ANTLRParser.RULE_REF)) {
			if ( r.getParent().getType()==ANTLRParser.RULE ) continue; // must be rule def
			if ( r.getChildCount()>0 ) continue; // already has arg; must be in rewritten rule
			if ( leftRecursiveRuleNames.contains(r.getText()) ) {
				// found ref to recursive rule not already rewritten with arg
				ActionAST arg = new ActionAST(new CommonToken(ANTLRParser.ARG_ACTION, "0"));
				r.addChild(arg);
			}
		}
	}

	/** Return true if successful */
	public boolean translateLeftRecursiveRule(GrammarRootAST ast,
											  LeftRecursiveRule r,
											  String language)
	{
		//tool.log("grammar", ruleAST.toStringTree());
		GrammarAST prevRuleAST = r.ast;
		TokenStream tokens = ast.tokens;
		Grammar g = ast.g;
		String ruleName = prevRuleAST.getChild(0).getText();
		LeftRecursiveRuleAnalyzer leftRecursiveRuleWalker =
			new LeftRecursiveRuleAnalyzer(tokens, prevRuleAST, tool, ruleName, language);
		boolean isLeftRec;
		try {
//			System.out.println("TESTING ---------------\n"+
//							   leftRecursiveRuleWalker.text(ruleAST));
			isLeftRec = leftRecursiveRuleWalker.rec_rule();
		}
		catch (RecognitionException re) {
			isLeftRec = false; // didn't match; oh well
		}
		if ( !isLeftRec ) return false;

		// replace old rule's AST
		GrammarAST RULES = (GrammarAST)ast.getFirstChildWithType(ANTLRParser.RULES);
		String newRuleText = leftRecursiveRuleWalker.getArtificialOpPrecRule();
//		System.out.println("created: "+newRuleText);
		RuleAST t = parseArtificialRule(g, newRuleText);

		// update grammar AST and set rule's AST.
		RULES.setChild(prevRuleAST.getChildIndex(), t);
		r.ast = t;

		// Reduce sets in newly created rule tree
		GrammarTransformPipeline transform = new GrammarTransformPipeline(g, g.tool);
		transform.reduceBlocksToSets(r.ast);
		transform.expandParameterizedLoops(r.ast);

		// track recursive alt info for codegen
		r.recPrimaryAlts = new ArrayList<LeftRecursiveRuleAltInfo>();
		r.recPrimaryAlts.addAll(leftRecursiveRuleWalker.prefixAlts);
		r.recPrimaryAlts.addAll(leftRecursiveRuleWalker.otherAlts);
		r.recOpAlts = new OrderedHashMap<Integer, LeftRecursiveRuleAltInfo>();
		r.recOpAlts.putAll(leftRecursiveRuleWalker.binaryAlts);
		r.recOpAlts.putAll(leftRecursiveRuleWalker.ternaryAlts);
		r.recOpAlts.putAll(leftRecursiveRuleWalker.suffixAlts);

		// walk alt info records and set their altAST to point to appropriate ALT subtree
		// from freshly created AST
		setAltASTPointers(r, t);

		// update Rule to just one alt and add prec alt
		ActionAST arg = (ActionAST)r.ast.getFirstChildWithType(ANTLRParser.ARG_ACTION);
		if ( arg!=null ) {
			r.args = ScopeParser.parseTypedArgList(arg.getText(), g.tool.errMgr);
			r.args.type = AttributeDict.DictType.ARG;
			r.args.ast = arg;
			arg.resolver = r.alt[1]; // todo: isn't this Rule or something?
		}

		// define labels on recursive rule refs we delete; they don't point to nodes of course
		// these are so $label in action translation works
		for (Tuple2<GrammarAST,String> pair : leftRecursiveRuleWalker.leftRecursiveRuleRefLabels) {
			GrammarAST labelNode = pair.getItem1();
			GrammarAST labelOpNode = (GrammarAST)labelNode.getParent();
			GrammarAST elementNode = (GrammarAST)labelOpNode.getChild(1);
			LabelElementPair lp = new LabelElementPair(g, labelNode, elementNode, labelOpNode.getType());
			r.alt[1].labelDefs.map(labelNode.getText(), lp);
		}
		// copy to rule from walker
		r.leftRecursiveRuleRefLabels = leftRecursiveRuleWalker.leftRecursiveRuleRefLabels;

		tool.log("grammar", "added: "+t.toStringTree());
		return true;
	}

	public RuleAST parseArtificialRule(final Grammar g, String ruleText) {
		ANTLRLexer lexer = new ANTLRLexer(new ANTLRStringStream(ruleText));
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		lexer.tokens = tokens;
		ToolANTLRParser p = new ToolANTLRParser(tokens, tool);
		p.setTreeAdaptor(adaptor);
		try {
			ParserRuleReturnScope r = p.rule();
			RuleAST tree = (RuleAST)r.getTree();
			GrammarTransformPipeline.setGrammarPtr(g, tree);
			return tree;
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.INTERNAL_ERROR,
								  "error parsing rule created during left-recursion detection: "+ruleText,
								  e);
		}
		return null;
	}

	/**
	 (RULE e int _p (returns int v)
	 	(BLOCK
	 	  (ALT
	 		(BLOCK
	 			(ALT INT {$v = $INT.int;})
	 			(ALT '(' (= x e) ')' {$v = $x.v;})
	 			(ALT ID))
	 		(* (BLOCK
	 			(ALT {7 >= $_p}? '*' (= b e) {$v = $a.v * $b.v;})
	 			(ALT {6 >= $_p}? '+' (= b e) {$v = $a.v + $b.v;})
	 			(ALT {3 >= $_p}? '++') (ALT {2 >= $_p}? '--'))))))

	 */
	public void setAltASTPointers(LeftRecursiveRule r, RuleAST t) {
//		System.out.println("RULE: "+t.toStringTree());
		BlockAST ruleBlk = (BlockAST)t.getFirstChildWithType(ANTLRParser.BLOCK);
		AltAST mainAlt = (AltAST)ruleBlk.getChild(0);
		BlockAST primaryBlk = (BlockAST)mainAlt.getChild(0);
		BlockAST opsBlk = (BlockAST)mainAlt.getChild(1).getChild(0); // (* BLOCK ...)
		for (int i = 0; i < r.recPrimaryAlts.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = r.recPrimaryAlts.get(i);
			altInfo.altAST = (AltAST)primaryBlk.getChild(i);
			altInfo.altAST.leftRecursiveAltInfo = altInfo;
			altInfo.originalAltAST.leftRecursiveAltInfo = altInfo;
//			altInfo.originalAltAST.parent = altInfo.altAST.parent;
//			System.out.println(altInfo.altAST.toStringTree());
		}
		for (int i = 0; i < r.recOpAlts.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = r.recOpAlts.getElement(i);
			altInfo.altAST = (AltAST)opsBlk.getChild(i);
			altInfo.altAST.leftRecursiveAltInfo = altInfo;
			altInfo.originalAltAST.leftRecursiveAltInfo = altInfo;
//			altInfo.originalAltAST.parent = altInfo.altAST.parent;
//			System.out.println(altInfo.altAST.toStringTree());
		}
	}

}
