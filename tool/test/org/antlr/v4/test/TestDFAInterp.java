package org.antlr.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.codegen.CompiledPDA;
import org.antlr.v4.codegen.DFACompiler;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.List;

/** */
public class TestDFAInterp extends BaseTest {

	public static class InterpLexer extends Lexer {
		public InterpLexer(CharStream input, PDA pda) {
			super(input);
			modeToPDA = new PDA[] { pda };
		}
	}

	@Test public void testSimpleLL1Decision() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"ID  : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n");

		Grammar g = new Grammar(
			"parser grammar P;\n" +
			"a : ID | INT ;\n"
		);
		int expecting = 1;
		checkDFAMatches(g, lg, 0, "ab", expecting);

		expecting = 2;
		checkDFAMatches(g, lg, 0, "32", expecting);
	}

	@Test public void testArbCommonPrefix() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"SEMI  : ';' ;\n" +
			"DOT   : '.' ;\n" +
			"WS    : ' ' ;\n" +
			"ID    : 'a'..'z'+ ;\n" +
			"INT  : '0'..'9'+ ;\n");

		Grammar g = new Grammar(
			"parser grammar P;\n" +
			"tokens { WS; }\n" +
			"a : ID+ SEMI\n" +
			"  | ID+ DOT\n" +
			"  ;\n"
		);
		int expecting = 1;
		checkDFAMatches(g, lg, 2, "a b c ;", expecting);

		expecting = 2;
		checkDFAMatches(g, lg, 2, "a b c .", expecting);
	}

	int interp(Grammar g, LexerGrammar lg, int decision, String input) {
		NFA nfa = createNFA(g);
		DecisionState blk = nfa.decisionToNFAState.get(decision);
		DFA dfa = createDFA(g, blk);
		DFACompiler comp = new DFACompiler(dfa);
		CompiledPDA obj = comp.compile();
		PDA pda = new PDA(obj.code, obj.altToAddr, obj.nLabels);

		lg.importVocab(g);
		PDA lexerPDA = getLexerPDA(lg);
		Lexer lexer = new InterpLexer(new ANTLRStringStream(input), lexerPDA);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		List<Token> list = tokens.getTokens();
		for (Token t : list) {// hide WS
			if ( t.getType()==g.getTokenType("WS") ) t.setChannel(Token.HIDDEN_CHANNEL);
		}
		System.out.println("tokens="+ list);
		int alt = pda.execNoRecursion(tokens, 0);
		return alt;
	}

	void checkDFAMatches(Grammar g, LexerGrammar lg, int decision,
						 String input, int expecting) {
		int result = interp(g, lg, decision, input);
		assertEquals(expecting, result);
	}
}
