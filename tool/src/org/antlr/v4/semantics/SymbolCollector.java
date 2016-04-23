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

import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LabelElementPair;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.antlr.v4.tool.ast.PredAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Collects (create) rules, terminals, strings, actions, scopes etc... from AST
 *  side-effects: sets resolver field of asts for actions and
 *  defines predicates via definePredicateInAlt(), collects actions and stores
 *  in alts.
 *  TODO: remove side-effects!
 */
public class SymbolCollector extends GrammarTreeVisitor {
	/** which grammar are we checking */
	public Grammar g;

	// stuff to collect
	public List<GrammarAST> rulerefs = new ArrayList<GrammarAST>();
	public List<GrammarAST> qualifiedRulerefs = new ArrayList<GrammarAST>();
	public List<GrammarAST> terminals = new ArrayList<GrammarAST>();
	public List<GrammarAST> tokenIDRefs = new ArrayList<GrammarAST>();
	public Set<String> strings = new HashSet<String>();
	public List<GrammarAST> tokensDefs = new ArrayList<GrammarAST>();
	public List<GrammarAST> channelDefs = new ArrayList<GrammarAST>();

	/** Track action name node in @parser::members {...} or @members {...} */
	List<GrammarAST> namedActions = new ArrayList<GrammarAST>();

	public ErrorManager errMgr;

	// context
	public Rule currentRule;

	public SymbolCollector(Grammar g) {
		this.g = g;
		this.errMgr = g.tool.errMgr;
	}

	@Override
	public ErrorManager getErrorManager() { return errMgr; }

	public void process(GrammarAST ast) { visitGrammar(ast); }

	@Override
	public void globalNamedAction(GrammarAST scope, GrammarAST ID, ActionAST action) {
		namedActions.add((GrammarAST)ID.getParent());
		action.resolver = g;
	}

	@Override
	public void defineToken(GrammarAST ID) {
		terminals.add(ID);
		tokenIDRefs.add(ID);
		tokensDefs.add(ID);
	}

	@Override
	public void defineChannel(GrammarAST ID) {
		channelDefs.add(ID);
	}

	@Override
	public void discoverRule(RuleAST rule, GrammarAST ID,
							 List<GrammarAST> modifiers, ActionAST arg,
							 ActionAST returns, GrammarAST thrws,
							 GrammarAST options, ActionAST locals,
							 List<GrammarAST> actions,
							 GrammarAST block)
	{
		currentRule = g.getRule(ID.getText());
	}

	@Override
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
								  GrammarAST block)
	{
		currentRule = g.getRule(ID.getText());
	}

	@Override
	public void discoverOuterAlt(AltAST alt) {
		currentRule.alt[currentOuterAltNumber].ast = alt;
	}

	@Override
	public void actionInAlt(ActionAST action) {
		currentRule.defineActionInAlt(currentOuterAltNumber, action);
		action.resolver = currentRule.alt[currentOuterAltNumber];
	}

	@Override
	public void sempredInAlt(PredAST pred) {
		currentRule.definePredicateInAlt(currentOuterAltNumber, pred);
		pred.resolver = currentRule.alt[currentOuterAltNumber];
	}

	@Override
	public void ruleCatch(GrammarAST arg, ActionAST action) {
		GrammarAST catchme = (GrammarAST)action.getParent();
		currentRule.exceptions.add(catchme);
		action.resolver = currentRule;
	}

	@Override
	public void finallyAction(ActionAST action) {
		currentRule.finallyAction = action;
		action.resolver = currentRule;
	}

	@Override
	public void label(GrammarAST op, GrammarAST ID, GrammarAST element) {
		LabelElementPair lp = new LabelElementPair(g, ID, element, op.getType());
		currentRule.alt[currentOuterAltNumber].labelDefs.map(ID.getText(), lp);
	}

	@Override
	public void stringRef(TerminalAST ref) {
		terminals.add(ref);
		strings.add(ref.getText());
		if ( currentRule!=null ) {
			currentRule.alt[currentOuterAltNumber].tokenRefs.map(ref.getText(), ref);
		}
	}

	@Override
	public void tokenRef(TerminalAST ref) {
		terminals.add(ref);
		tokenIDRefs.add(ref);
		if ( currentRule!=null ) {
			currentRule.alt[currentOuterAltNumber].tokenRefs.map(ref.getText(), ref);
		}
	}

	@Override
	public void ruleRef(GrammarAST ref, ActionAST arg) {
//		if ( inContext("DOT ...") ) qualifiedRulerefs.add((GrammarAST)ref.getParent());
		rulerefs.add(ref);
    	if ( currentRule!=null ) {
    		currentRule.alt[currentOuterAltNumber].ruleRefs.map(ref.getText(), ref);
    	}
	}

	@Override
	public void grammarOption(GrammarAST ID, GrammarAST valueAST) {
		setActionResolver(valueAST);
	}

	@Override
	public void ruleOption(GrammarAST ID, GrammarAST valueAST) {
		setActionResolver(valueAST);
	}

	@Override
	public void blockOption(GrammarAST ID, GrammarAST valueAST) {
		setActionResolver(valueAST);
	}

	@Override
	public void elementOption(GrammarASTWithOptions t, GrammarAST ID, GrammarAST valueAST) {
		setActionResolver(valueAST);
	}

	/** In case of option id={...}, set resolve in case they use $foo */
	private void setActionResolver(GrammarAST valueAST) {
		if ( valueAST instanceof ActionAST) {
			((ActionAST)valueAST).resolver = currentRule.alt[currentOuterAltNumber];
		}
	}
}
