package org.antlr.v4.automata;

import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.*;

import java.util.List;

public class LexerATNFactory extends ParserATNFactory {
	public LexerATNFactory(LexerGrammar g) { super(g); }

	public ATN createATN() {
		// BUILD ALL START STATES (ONE PER MODE)
		for (String modeName : ((LexerGrammar)g).modes.keySet()) {
			// create s0, start state; implied Tokens rule node
			TokensStartState startState =
				(TokensStartState)newState(TokensStartState.class, null);
			atn.modeNameToStartState.put(modeName, startState);
			atn.modeToStartState.add(startState);
			atn.defineDecisionState(startState);
		}

		// CREATE ATN FOR EACH RULE
		_createATN(g.rules.values());

		// LINK MODE START STATE TO EACH TOKEN RULE
		for (String modeName : ((LexerGrammar)g).modes.keySet()) {
			List<Rule> rules = ((LexerGrammar)g).modes.get(modeName);
			TokensStartState startState = atn.modeNameToStartState.get(modeName);
			for (Rule r : rules) {
				if ( !r.isFragment() ) {
					RuleStartState s = atn.ruleToStartState.get(r);
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
		left.transition = new RangeTransition(t1, t2, right);
		a.atnState = left;
		b.atnState = left;
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
			prev.transition = new AtomTransition(chars.charAt(i), right);
			prev = right;
		}
		stringLiteralAST.atnState = left;
		return new Handle(left, right);
	}

	@Override
	public Handle tokenRef(TerminalAST node) {
		return _ruleRef(node);
	}
}
