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

package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.v4.Tool;
import org.antlr.v4.parse.*;

import java.util.*;

/** Handle left-recursion and block-set transforms */
public class GrammarTransformPipeline {
	public Tool tool;

	public GrammarTransformPipeline(Tool tool) {
		this.tool = tool;
	}

	public void process(GrammarRootAST ast) {
		if ( ast==null ) return;
		System.out.println("before: "+ast.toStringTree());

		if ( ast.grammarType==ANTLRParser.PARSER || ast.grammarType==ANTLRParser.COMBINED ) {
			translateLeftRecursiveRules(ast);
		}

		reduceBlocksToSets(ast);
		System.out.println("after: "+ast.toStringTree());
	}

	public void reduceBlocksToSets(GrammarRootAST ast) {
		org.antlr.runtime.tree.CommonTreeNodeStream nodes =
			new org.antlr.runtime.tree.CommonTreeNodeStream(ast);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		BlockSetTransformer transformer = new BlockSetTransformer(nodes);
		transformer.setTreeAdaptor(adaptor);
		transformer.downup(ast);
	}

	public void translateLeftRecursiveRules(GrammarRootAST ast) {
		String language = Grammar.getLanguageOption(ast);
		for (GrammarAST r : ast.getNodesWithType(ANTLRParser.RULE)) {
			String ruleName = r.getChild(0).getText();
			if ( !Character.isUpperCase(ruleName.charAt(0)) ) {
				if ( LeftRecursiveRuleAnalyzer.hasImmediateRecursiveRuleRefs(r, ruleName) ) {
					translateLeftRecursiveRule(ast, r, language);
				}
			}
		}
	}

	public void translateLeftRecursiveRule(GrammarRootAST ast,
										   GrammarAST ruleAST,
										   String language)
	{
		//System.out.println(ruleAST.toStringTree());
		TokenStream tokens = ast.tokens;
		String ruleName = ruleAST.getChild(0).getText();
		LeftRecursiveRuleAnalyzer leftRecursiveRuleWalker =
			new LeftRecursiveRuleAnalyzer(tokens, ruleAST, tool, ruleName, language);
		boolean isLeftRec = false;
		try {
//			System.out.println("TESTING ---------------\n"+
//							   leftRecursiveRuleWalker.text(ruleAST));
			isLeftRec = leftRecursiveRuleWalker.rec_rule();
		}
		catch (RecognitionException re) {
			tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "bad ast structure", re);
		}
		if ( !isLeftRec ) return;

		// delete old rule
		GrammarAST RULES = (GrammarAST)ast.getFirstChildWithType(ANTLRParser.RULES);
		RULES.deleteChild(ruleAST);

		List<String> rules = new ArrayList<String>();
		rules.add( leftRecursiveRuleWalker.getArtificialPrecStartRule() ) ;
		rules.add( leftRecursiveRuleWalker.getArtificialOpPrecRule() );
		rules.add( leftRecursiveRuleWalker.getArtificialPrimaryRule() );
		for (String ruleText : rules) {
//			System.out.println("created: "+ruleText);
			GrammarAST t = parseArtificialRule(ruleText);
			// insert into grammar tree
			RULES.addChild(t);
			System.out.println("added: "+t.toStringTree());
		}
	}

	public GrammarAST parseArtificialRule(String ruleText) {
		ANTLRLexer lexer = new ANTLRLexer(new ANTLRStringStream(ruleText));
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ToolANTLRParser p = new ToolANTLRParser(tokens, tool);
		p.setTreeAdaptor(adaptor);
		try {
			ParserRuleReturnScope r = p.rule();
			return (GrammarAST)r.getTree();
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.INTERNAL_ERROR,
								  "error parsing rule created during left-recursion detection: "+ruleText,
								  e);
		}
		return null;
	}

}
