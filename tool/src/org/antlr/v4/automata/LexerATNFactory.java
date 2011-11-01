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

package org.antlr.v4.automata;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.List;
import java.util.Set;

public class LexerATNFactory extends ParserATNFactory {
	public LexerATNFactory(LexerGrammar g) { super(g); }

	public ATN createATN() {
		// BUILD ALL START STATES (ONE PER MODE)
		System.out.println(((LexerGrammar)g).modes);
		Set<String> modes = ((LexerGrammar) g).modes.keySet();
		for (String modeName : modes) {
			// create s0, start state; implied Tokens rule node
			TokensStartState startState =
				(TokensStartState)newState(TokensStartState.class, null);
			atn.modeNameToStartState.put(modeName, startState);
			atn.modeToStartState.add(startState);
			atn.defineDecisionState(startState);
		}

		// INIT ACTION, RULE->TOKEN_TYPE MAP
		atn.ruleToTokenType = new int[g.rules.size()];
		atn.ruleToActionIndex = new int[g.rules.size()];
		for (Rule r : g.rules.values()) {
			atn.ruleToTokenType[r.index] = g.getTokenType(r.name);
			atn.ruleToActionIndex[r.index] = r.actionIndex;
		}

		// CREATE ATN FOR EACH RULE
		_createATN(g.rules.values());

		// LINK MODE START STATE TO EACH TOKEN RULE
		for (String modeName : modes) {
			List<Rule> rules = ((LexerGrammar)g).modes.get(modeName);
			TokensStartState startState = atn.modeNameToStartState.get(modeName);
			for (Rule r : rules) {
				if ( !r.isFragment() ) {
					RuleStartState s = atn.ruleToStartState[r.index];
					epsilon(startState, s);
				}
			}
		}

		return atn;
	}

	@Override
	public Handle action(ActionAST action) {
//		Handle h = super.action(action);
//		ActionTransition a = (ActionTransition)h.left.transition(0);
//		a.actionIndex = g.actions.get(action);
//		return h;
		// no actions in lexer ATN; just one on end and we exec via action number
		ATNState x = newState(action);
		return new Handle(x, x); // return just one blank state
	}

	@Override
	public Handle range(GrammarAST a, GrammarAST b) {
		ATNState left = newState(a);
		ATNState right = newState(b);
		int t1 = CharSupport.getCharValueFromGrammarCharLiteral(a.getText());
		int t2 = CharSupport.getCharValueFromGrammarCharLiteral(b.getText());
		left.addTransition(new  RangeTransition(t1, t2, right));
		a.atnState = left;
		b.atnState = left;
		return new Handle(left, right);
	}

	@Override
	public Handle set(GrammarAST associatedAST, List<GrammarAST> alts, boolean invert) {
		ATNState left = newState(associatedAST);
		ATNState right = newState(associatedAST);
		IntervalSet set = new IntervalSet();
		for (GrammarAST t : alts) {
			if ( t.getType()== ANTLRParser.RANGE ) {
				int a = CharSupport.getCharValueFromGrammarCharLiteral(t.getChild(0).getText());
				int b = CharSupport.getCharValueFromGrammarCharLiteral(t.getChild(1).getText());
				set.add(a, b);
			}
			else {
				int c = CharSupport.getCharValueFromGrammarCharLiteral(t.getText());
				set.add(c);
			}
		}
		if ( invert ) {
			// TODO: what? should be chars not token types
			IntervalSet notSet = (IntervalSet)set.complement(Token.MIN_TOKEN_TYPE, g.getMaxTokenType());
			left.addTransition(new NotSetTransition(set, notSet, right));
		}
		else {
			left.addTransition(new SetTransition(set, right));
		}
		right.incidentTransition = left.transition(0);
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	/** For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'->o-'o'->o-'g'->o and has n+1 states
	 *  for n characters.
	 */
	@Override
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		String chars = stringLiteralAST.getText();
		chars = CharSupport.getStringFromGrammarStringLiteral(chars);
		int n = chars.length();
		ATNState left = newState(stringLiteralAST);
		ATNState prev = left;
		ATNState right = null;
		for (int i=0; i<n; i++) {
			right = newState(stringLiteralAST);
			prev.addTransition(new AtomTransition(chars.charAt(i), right));
			prev = right;
		}
		stringLiteralAST.atnState = left;
		return new Handle(left, right);
	}

	@Override
	public Handle tokenRef(TerminalAST node) {
		// Ref to EOF in lexer yields char transition on -1
		if ( node.getText().equals("EOF") ) {
			ATNState left = newState(node);
			ATNState right = newState(node);
			left.addTransition(new AtomTransition(CharStream.EOF, right));
			return new Handle(left, right);
		}
		return _ruleRef(node);
	}
}
