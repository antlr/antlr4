package org.antlr.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.NFA;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/** */
public class TestNFABytecodeInterp extends BaseTest {
	@Test public void testString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'ab' ;");
		String expecting = "A, A, EOF";
		checkMatches(g, "abab", expecting);
	}

	@Test public void testIDandIntandKeyword() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'ab';\n" +
			"B : 'a'..'z'+ ;\n" +
			"I : '0'..'9'+ ;\n");
		String expecting = "A, I, B, EOF";
		checkMatches(g, "ab32abc", expecting);
	}

	@Test public void testNonGreedy() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '/*' (options {greedy=false;}:.)* '*/' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting = "ID, CMT, EOF";
		checkMatches(g, "ab/* x */", expecting);
	}

	@Test public void testNonGreedyAndCommonLeftPrefix() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '/*' (options {greedy=false;}:.)* '*/' ;\n" +
			"CMT2: '/*' (options {greedy=false;}:.)* '*/' '!' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting = "ID, CMT2, CMT, EOF";
		checkMatches(g, "ab/* x */!/* foo */", expecting);
	}

	@Test public void testCallFragment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : D+ ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		String expecting = "I, EOF";
		checkMatches(g, "32", expecting);
	}

	@Test public void testCallNonFragment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"QID : ID ('.' ID)+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : ' ' ;\n");
		String expecting = "ID, EOF";
		checkMatches(g, "z", expecting);
		expecting = "ID, WS, QID, WS, ID, WS, QID, WS, ID, EOF";
		checkMatches(g, "z a.b x c.d.e y", expecting);
	}

	@Test public void testRecursiveCall() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" + //TODO 
			"I : D+ ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		String expecting = "I, EOF";
		checkMatches(g, "32", expecting);
	}

	public void _template() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"\n");
		String expecting = "";
		checkMatches(g, "input", expecting);
	}

	void checkMatches(LexerGrammar g, String input, String expecting) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.process(imp);
				}
			}
		}

		List<Integer> expectingTokens = new ArrayList<Integer>();
		if ( expecting!=null && !expecting.trim().equals("") ) {
			for (String tname : expecting.replace(" ", "").split(",")) {
				int ttype = g.getTokenType(tname);
				expectingTokens.add(ttype);
			}
		}

		NFA nfa = NFABytecodeGenerator.getBytecode(g, LexerGrammar.DEFAULT_MODE_NAME);
		ANTLRStringStream in = new ANTLRStringStream(input);
		List<Integer> tokens = new ArrayList<Integer>();
		int ttype = 0;
		do {
			ttype = nfa.execThompson(in);
			tokens.add(ttype);
		} while ( ttype!= Token.EOF );
		assertEquals(expectingTokens, tokens);
	}
}
