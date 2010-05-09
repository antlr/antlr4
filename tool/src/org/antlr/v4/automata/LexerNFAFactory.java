package org.antlr.v4.automata;

import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.TerminalAST;
import org.stringtemplate.v4.misc.Misc;

import java.util.List;

// TODO: i don't think we create lexer NFAs anymore

public class LexerNFAFactory extends ParserNFAFactory {
	public LexerNFAFactory(LexerGrammar g) { super(g); }

	public NFA createNFA() {
		// BUILD ALL START STATES (ONE PER MODE)
		for (String modeName : ((LexerGrammar)g).modes.keySet()) {
			// create s0, start state; implied Tokens rule node
			TokensStartState startState =
				(TokensStartState)newState(TokensStartState.class, null);
			nfa.modeToStartState.put(modeName, startState);
			nfa.defineDecisionState(startState);			
		}

		// CREATE NFA FOR EACH RULE
		_createNFA(g.rules.values());

		// LINK MODE START STATE TO EACH TOKEN RULE
		for (String modeName : ((LexerGrammar)g).modes.keySet()) {
			List<Rule> rules = ((LexerGrammar)g).modes.get(modeName);
			TokensStartState startState = nfa.modeToStartState.get(modeName);
			for (Rule r : rules) {
				if ( !r.isFragment() ) {
					RuleStartState s = nfa.ruleToStartState.get(r);
					epsilon(startState, s);
				}
			}
		}

		return nfa;
	}

	public Handle range(GrammarAST a, GrammarAST b) {
		BasicState left = newState(a);
		BasicState right = newState(b);
		int t1 = CharSupport.getCharValueFromGrammarCharLiteral(a.getText());
		int t2 = CharSupport.getCharValueFromGrammarCharLiteral(b.getText());
		left.transition = new RangeTransition(t1, t2, right);
		a.nfaState = left;
		b.nfaState = left;
		return new Handle(left, right);
	}

	/** For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'->o-'o'->o-'g'->o and has n+1 states
	 *  for n characters.
	 */
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		String chars = stringLiteralAST.getText();
		chars = Misc.strip(chars, 1); // strip quotes
		int n = chars.length();
		BasicState left = newState(stringLiteralAST);
		BasicState prev = left;
		BasicState right = null;
		for (int i=0; i<n; i++) {
			right = newState(stringLiteralAST);
			prev.transition = new AtomTransition(chars.charAt(i), right);
			prev = right;
		}
		stringLiteralAST.nfaState = left;
		return new Handle(left, right);
	}

	@Override
	public Handle tokenRef(TerminalAST node) {
		return ruleRef(node);
	}
}
