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

package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Look for errors and deadcode stuff */
public class UseDefAnalyzer {
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

	public static boolean actionIsContextDependent(ActionAST actionAST) {
		ANTLRStringStream in = new ANTLRStringStream(actionAST.token.getText());
		in.setLine(actionAST.token.getLine());
		in.setCharPositionInLine(actionAST.token.getCharPositionInLine());
		final boolean[] dependent = new boolean[] {false}; // can't be simple bool with anon class
		ActionSplitterListener listener = new BlankActionSplitterListener() {
			@Override
			public void nonLocalAttr(String expr, Token x, Token y) { dependent[0] = true; }
			@Override
			public void qualifiedAttr(String expr, Token x, Token y) { dependent[0] = true; }
			@Override
			public void setAttr(String expr, Token x, Token rhs) { dependent[0] = true; }
			@Override
			public void setExprAttribute(String expr) { dependent[0] = true; }
			@Override
			public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) { dependent[0] = true; }
			@Override
			public void attr(String expr, Token x) {  dependent[0] = true; }
		};
		ActionSplitter splitter = new ActionSplitter(in, listener);
		// forces eval, triggers listener methods
		splitter.getActionTokens();
		return dependent[0];
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
