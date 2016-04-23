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

import org.antlr.v4.analysis.LeftRecursiveRuleTransformer;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Do as much semantic checking as we can and fill in grammar
 *  with rules, actions, and token definitions.
 *  The only side effects are in the grammar passed to process().
 *  We consume a bunch of memory here while we build up data structures
 *  to perform checking, but all of it goes away after this pipeline object
 *  gets garbage collected.
 *
 *  After this pipeline finishes, we can be sure that the grammar
 *  is syntactically correct and that it's semantically correct enough for us
 *  to attempt grammar analysis. We have assigned all token types.
 *  Note that imported grammars bring in token and rule definitions
 *  but only the root grammar and any implicitly created lexer grammar
 *  get their token definitions filled up. We are treating the
 *  imported grammars like includes.
 *
 *  The semantic pipeline works on root grammars (those that do the importing,
 *  if any). Upon entry to the semantic pipeline, all imported grammars
 *  should have been loaded into delegate grammar objects with their
 *  ASTs created.  The pipeline does the BasicSemanticChecks on the
 *  imported grammar before collecting symbols. We cannot perform the
 *  simple checks such as undefined rule until we have collected all
 *  tokens and rules from the imported grammars into a single collection.
 */
public class SemanticPipeline {
	public Grammar g;

	public SemanticPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		if ( g.ast==null ) return;

		// COLLECT RULE OBJECTS
		RuleCollector ruleCollector = new RuleCollector(g);
		ruleCollector.process(g.ast);

		// DO BASIC / EASY SEMANTIC CHECKS
		BasicSemanticChecks basics = new BasicSemanticChecks(g, ruleCollector);
		basics.process();

		// TRANSFORM LEFT-RECURSIVE RULES
		int prevErrors = g.tool.errMgr.getNumErrors();
		LeftRecursiveRuleTransformer lrtrans =
			new LeftRecursiveRuleTransformer(g.ast, ruleCollector.rules.values(), g);
		lrtrans.translateLeftRecursiveRules();

		// don't continue if we got errors during left-recursion elimination
		if ( g.tool.errMgr.getNumErrors()>prevErrors ) return;

		// STORE RULES IN GRAMMAR
		for (Rule r : ruleCollector.rules.values()) {
			g.defineRule(r);
		}

		// COLLECT SYMBOLS: RULES, ACTIONS, TERMINALS, ...
		SymbolCollector collector = new SymbolCollector(g);
		collector.process(g.ast);

		// CHECK FOR SYMBOL COLLISIONS
		SymbolChecks symcheck = new SymbolChecks(g, collector);
		symcheck.process(); // side-effect: strip away redef'd rules.

		for (GrammarAST a : collector.namedActions) {
			g.defineAction(a);
		}

		// LINK (outermost) ALT NODES WITH Alternatives
		for (Rule r : g.rules.values()) {
			for (int i=1; i<=r.numberOfAlts; i++) {
				r.alt[i].ast.alt = r.alt[i];
			}
		}

		// ASSIGN TOKEN TYPES
		g.importTokensFromTokensFile();
		if ( g.isLexer() ) {
			assignLexerTokenTypes(g, collector.tokensDefs);
		}
		else {
			assignTokenTypes(g, collector.tokensDefs,
							 collector.tokenIDRefs, collector.terminals);
		}

		symcheck.checkForModeConflicts(g);

		assignChannelTypes(g, collector.channelDefs);

		// CHECK RULE REFS NOW (that we've defined rules in grammar)
		symcheck.checkRuleArgs(g, collector.rulerefs);
		identifyStartRules(collector);
		symcheck.checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// don't continue if we got symbol errors
		if ( g.tool.getNumErrors()>0 ) return;

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		UseDefAnalyzer.trackTokenRuleRefsInActions(g);
	}

	void identifyStartRules(SymbolCollector collector) {
		for (GrammarAST ref : collector.rulerefs) {
			String ruleName = ref.getText();
			Rule r = g.getRule(ruleName);
			if ( r!=null ) r.isStartRule = false;
		}
	}

	void assignLexerTokenTypes(Grammar g, List<GrammarAST> tokensDefs) {
		Grammar G = g.getOutermostGrammar(); // put in root, even if imported
		for (GrammarAST def : tokensDefs) {
			// tokens { id (',' id)* } so must check IDs not TOKEN_REF
			if ( Grammar.isTokenName(def.getText()) ) {
				G.defineTokenName(def.getText());
			}
		}

		/* Define token types for nonfragment rules which do not include a 'type(...)'
		 * or 'more' lexer command.
		 */
		for (Rule r : g.rules.values()) {
			if ( !r.isFragment() && !hasTypeOrMoreCommand(r) ) {
				G.defineTokenName(r.name);
			}
		}

		// FOR ALL X : 'xxx'; RULES, DEFINE 'xxx' AS TYPE X
		List<Pair<GrammarAST,GrammarAST>> litAliases =
			Grammar.getStringLiteralAliasesFromLexerRules(g.ast);
		Set<String> conflictingLiterals = new HashSet<String>();
		if ( litAliases!=null ) {
			for (Pair<GrammarAST,GrammarAST> pair : litAliases) {
				GrammarAST nameAST = pair.a;
				GrammarAST litAST = pair.b;
				if ( !G.stringLiteralToTypeMap.containsKey(litAST.getText()) ) {
					G.defineTokenAlias(nameAST.getText(), litAST.getText());
				}
				else {
					// oops two literal defs in two rules (within or across modes).
					conflictingLiterals.add(litAST.getText());
				}
			}
			for (String lit : conflictingLiterals) {
				// Remove literal if repeated across rules so it's not
				// found by parser grammar.
				Integer value = G.stringLiteralToTypeMap.remove(lit);
				if (value != null && value > 0 && value < G.typeToStringLiteralList.size() && lit.equals(G.typeToStringLiteralList.get(value))) {
					G.typeToStringLiteralList.set(value, null);
				}
			}
		}

	}

	boolean hasTypeOrMoreCommand(Rule r) {
		GrammarAST ast = r.ast;
		if (ast == null) {
			return false;
		}

		GrammarAST altActionAst = (GrammarAST)ast.getFirstDescendantWithType(ANTLRParser.LEXER_ALT_ACTION);
		if (altActionAst == null) {
			// the rule isn't followed by any commands
			return false;
		}

		// first child is the alt itself, subsequent are the actions
		for (int i = 1; i < altActionAst.getChildCount(); i++) {
			GrammarAST node = (GrammarAST)altActionAst.getChild(i);
			if (node.getType() == ANTLRParser.LEXER_ACTION_CALL) {
				if ("type".equals(node.getChild(0).getText())) {
					return true;
				}
			}
			else if ("more".equals(node.getText())) {
				return true;
			}
		}

		return false;
	}

	void assignTokenTypes(Grammar g, List<GrammarAST> tokensDefs,
						  List<GrammarAST> tokenIDs, List<GrammarAST> terminals)
	{
		//Grammar G = g.getOutermostGrammar(); // put in root, even if imported

		// create token types for tokens { A, B, C } ALIASES
		for (GrammarAST alias : tokensDefs) {
			if (g.getTokenType(alias.getText()) != Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.TOKEN_NAME_REASSIGNMENT, g.fileName, alias.token, alias.getText());
			}

			g.defineTokenName(alias.getText());
		}

		// DEFINE TOKEN TYPES FOR TOKEN REFS LIKE ID, INT
		for (GrammarAST idAST : tokenIDs) {
			if (g.getTokenType(idAST.getText()) == Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.IMPLICIT_TOKEN_DEFINITION, g.fileName, idAST.token, idAST.getText());
			}

			g.defineTokenName(idAST.getText());
		}

		// VERIFY TOKEN TYPES FOR STRING LITERAL REFS LIKE 'while', ';'
		for (GrammarAST termAST : terminals) {
			if (termAST.getType() != ANTLRParser.STRING_LITERAL) {
				continue;
			}

			if (g.getTokenType(termAST.getText()) == Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.IMPLICIT_STRING_DEFINITION, g.fileName, termAST.token, termAST.getText());
			}
		}

		g.tool.log("semantics", "tokens="+g.tokenNameToTypeMap);
        g.tool.log("semantics", "strings="+g.stringLiteralToTypeMap);
	}

	/**
	 * Assign constant values to custom channels defined in a grammar.
	 *
	 * @param g The grammar.
	 * @param channelDefs A collection of AST nodes defining individual channels
	 * within a {@code channels{}} block in the grammar.
	 */
	void assignChannelTypes(Grammar g, List<GrammarAST> channelDefs) {
		Grammar outermost = g.getOutermostGrammar();
		for (GrammarAST channel : channelDefs) {
			String channelName = channel.getText();

			// Channel names can't alias tokens or modes, because constant
			// values are also assigned to them and the ->channel(NAME) lexer
			// command does not distinguish between the various ways a constant
			// can be declared. This method does not verify that channels do not
			// alias rules, because rule names are not associated with constant
			// values in ANTLR grammar semantics.

			if (g.getTokenType(channelName) != Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.CHANNEL_CONFLICTS_WITH_TOKEN, g.fileName, channel.token, channelName);
			}

			if (LexerATNFactory.COMMON_CONSTANTS.containsKey(channelName)) {
				g.tool.errMgr.grammarError(ErrorType.CHANNEL_CONFLICTS_WITH_COMMON_CONSTANTS, g.fileName, channel.token, channelName);
			}

			if (outermost instanceof LexerGrammar) {
				LexerGrammar lexerGrammar = (LexerGrammar)outermost;
				if (lexerGrammar.modes.containsKey(channelName)) {
					g.tool.errMgr.grammarError(ErrorType.CHANNEL_CONFLICTS_WITH_MODE, g.fileName, channel.token, channelName);
				}
			}

			outermost.defineChannelName(channel.getText());
		}
	}
}
