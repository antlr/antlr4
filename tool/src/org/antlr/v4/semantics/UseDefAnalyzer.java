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

package org.antlr.v4.semantics;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.*;

import java.util.*;

/** Look for errors and deadcode stuff */
public class UseDefAnalyzer {
	public static void checkRewriteElementsPresentOnLeftSide(Grammar g) {
		for (Rule r : g.rules.values()) {
			for (int a=1; a<=r.numberOfAlts; a++) {
				Alternative alt = r.alt[a];
				for (GrammarAST e : alt.rewriteElements) {
					boolean ok =
						alt.ruleRefs.containsKey(e.getText()) ||
						g.getTokenType(e.getText()) != Token.INVALID_TYPE ||
						alt.labelDefs.containsKey(e.getText()) ||
						e.getText().equals(r.name);
					if ( !ok ) { // $r ok in rule r
						g.tool.errMgr.grammarError(ErrorType.REWRITE_ELEMENT_NOT_PRESENT_ON_LHS,
												   g.fileName, e.token, e.getText());
					}
				}
			}
		}
	}

	// side-effect: updates Alternative with refs in actions
	public static void trackTokenRuleRefsInActions(Grammar g) {
		for (Rule r : g.rules.values()) {
			for (int i=1; i<=r.numberOfAlts; i++) {
				Alternative alt = r.alt[i];
				for (ActionAST a : alt.actions) {
					ActionSniffer sniffer =	new ActionSniffer(g, r, alt, a, a.token);
					sniffer.examineAction();
				}
			}
		}
	}

	/** Given -> (ALT ...),  return list of element refs at
	 *  top level
	 */
	public static List<GrammarAST> getElementReferencesShallowInOuterAlt(Grammar g,
																		 GrammarAST altAST)
	{
		return UseDefAnalyzer.getRewriteElementRefs(g, altAST, 0, false);
	}

	/** Given (('?'|'*') (REWRITE_BLOCK (ALT ...))) return list of element refs at
	 *  top level of REWRITE_BLOCK. Must see into (nested) tree structures if
	 *  optional but not if closure (that might lead to inf loop when building tree).
	 */
	public static List<GrammarAST> getElementReferencesInEBNF(Grammar g,
															  GrammarAST ebnfRoot,
															  boolean deep)
	{
		return UseDefAnalyzer.getRewriteElementRefs(g, ebnfRoot, 1, deep);
	}

	/** Get list of rule refs, token refs mentioned on left, and labels not
	 *  referring to rule result like $e in rule e.
	 */
	public static List<GrammarAST> filterForRuleAndTokenRefs(Alternative alt,
															 List<GrammarAST> refs)
	{
		List<GrammarAST> elems = new ArrayList<GrammarAST>();
		if ( refs!=null ) {
			for (GrammarAST ref : refs) {
				boolean imaginary =
					ref.getType()== ANTLRParser.TOKEN_REF &&
					!alt.tokenRefs.containsKey(ref.getText());
				boolean selfLabel =
					ref.getType()==ANTLRParser.LABEL &&
					ref.getText().equals(alt.rule.name);
				if ( !imaginary && !selfLabel ) elems.add(ref);
			}
		}
		return elems;
	}

	public static List<GrammarAST> getRewriteElementRefs(Grammar g,
														 GrammarAST root,
														 int desiredShallowLevel,
														 boolean deep)
	{
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(root);
		Refs collector = new Refs(nodes, desiredShallowLevel);
		try {
			collector.start();
		}
		catch (org.antlr.runtime.RecognitionException re) {
			g.tool.errMgr.grammarError(ErrorType.INTERNAL_ERROR,
									   g.fileName, re.token, re);

		}
//		System.out.println("from "+root.toStringTree());
//		System.out.println("shallow: "+collector.shallow);
//		System.out.println("deep: "+collector.deep);
		return deep ? collector.deep : collector.shallow;
	}

	/** Find all rules reachable from r directly or indirectly for all r in g */
	public static Map<Rule, Set<Rule>> getRuleDependencies(Grammar g) {
		return getRuleDependencies(g, g.rules.values());
	}

	public static Map<Rule, Set<Rule>> getRuleDependencies(LexerGrammar g, String modeName) {
		return getRuleDependencies(g, g.modes.get(modeName));
	}

	public static Map<Rule, Set<Rule>> getRuleDependencies(Grammar g, Collection<Rule> rules) {
		Map<Rule, Set<Rule>> dependencies = new HashMap<Rule, Set<Rule>>();

		for (Rule r : rules) {
			List<GrammarAST> tokenRefs = r.ast.getNodesWithType(ANTLRParser.TOKEN_REF);
			for (GrammarAST tref : tokenRefs) {
				Set<Rule> calls = dependencies.get(r);
				if ( calls==null ) {
					calls = new HashSet<Rule>();
					dependencies.put(r, calls);
				}
				calls.add(g.getRule(tref.getText()));
			}
		}

		return dependencies;
	}

}
