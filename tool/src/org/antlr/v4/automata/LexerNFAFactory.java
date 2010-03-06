package org.antlr.v4.automata;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.NFABuilder;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.misc.Misc;

/** */
public class LexerNFAFactory extends ParserNFAFactory {
	public LexerNFAFactory(Grammar g) { super(g); }

	public NFA createNFA() {
		createRuleStartAndStopNFAStates();

		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		for (Rule r : g.rules.values()) {
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,r.ast);
			NFABuilder b = new NFABuilder(nodes,this);
			try { b.rule();	}
			catch (RecognitionException re) {
				ErrorManager.fatalInternalError("bad grammar AST structure", re);
			}
		}

		return nfa;
	}

	public Handle range(GrammarAST a, GrammarAST b) {
		BasicState left = newState(a);
		BasicState right = newState(b);
		int t1 = Target.getCharValueFromGrammarCharLiteral(a.getText());
		int t2 = Target.getCharValueFromGrammarCharLiteral(b.getText());
		left.transition = new RangeTransition(t1, t2, right);
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
		return new Handle(left, right);
	}

}
