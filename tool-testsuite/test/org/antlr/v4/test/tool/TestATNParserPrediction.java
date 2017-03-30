/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH
// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH
// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH

public class TestATNParserPrediction extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testAorB() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : A{;} | B ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "b", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"a",
		"b",
		"a"
		};
		String[] dfa = {
		"s0-'a'->:s1=>1\n",

		"s0-'a'->:s1=>1\n" +
		"s0-'b'->:s2=>2\n",

		"s0-'a'->:s1=>1\n" + // don't change after it works
		"s0-'b'->:s2=>2\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testEmptyInput() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : A | ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"a",
		"",
		};
		String[] dfa = {
		"s0-'a'->:s1=>1\n",

		"s0-EOF->:s2=>2\n" +
		"s0-'a'->:s1=>1\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : A | A B ;");
		checkPredictedAlt(lg, g, 0, "a", 1);
		checkPredictedAlt(lg, g, 0, "ab", 2);
		checkPredictedAlt(lg, g, 0, "abc", 2);

		String[] inputs = {
		"a",
		"ab",
		"abc"
		};
		String[] dfa = {
		"s0-'a'->s1\n" +
		"s1-EOF->:s2=>1\n",

		"s0-'a'->s1\n" +
		"s1-EOF->:s2=>1\n" +
		"s1-'b'->:s3=>2\n",

		"s0-'a'->s1\n" +
		"s1-EOF->:s2=>1\n" +
		"s1-'b'->:s3=>2\n"
		};
		checkDFAConstruction(lg, g, 0, inputs, dfa);
	}

	@Test public void testRuleRefxory() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : x | y ;\n" +
		"x : A ;\n" +
		"y : B ;\n");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "b", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"a",
		"b",
		"a"
		};
		String[] dfa = {
		"s0-'a'->:s1=>1\n",

		"s0-'a'->:s1=>1\n" +
		"s0-'b'->:s2=>2\n",

		"s0-'a'->:s1=>1\n" + // don't change after it works
		"s0-'b'->:s2=>2\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testOptionalRuleChasesGlobalFollow() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"tokens {A,B,C}\n" +
		"a : x B ;\n" +
		"b : x C ;\n" +
		"x : A | ;\n");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "b", 2);
		checkPredictedAlt(lg, g, decision, "c", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"a",
		"b",
		"c",
		"c",
		};
		String[] dfa = {
		"s0-'a'->:s1=>1\n",

		"s0-'a'->:s1=>1\n" +
		"s0-'b'->:s2=>2\n",

		"s0-'a'->:s1=>1\n" +
		"s0-'b'->:s2=>2\n" +
		"s0-'c'->:s3=>2\n",

		"s0-'a'->:s1=>1\n" +
		"s0-'b'->:s2=>2\n" +
		"s0-'c'->:s3=>2\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testLL1Ambig() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : A | A | A B ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "ab", 3);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"a",
		"ab",
		"ab"
		};
		String[] dfa = {
		"s0-'a'->s1\n" +
		"s1-EOF->:s2^=>1\n",

		"s0-'a'->s1\n" +
		"s1-EOF->:s2^=>1\n" +
		"s1-'b'->:s3=>3\n",

		"s0-'a'->s1\n" +
		"s1-EOF->:s2^=>1\n" +
		"s1-'b'->:s3=>3\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testLL2Ambig() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n");
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"a : A B | A B | A B C ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "ab", 1);
		checkPredictedAlt(lg, g, decision, "abc", 3);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"ab",
		"abc",
		"ab"
		};
		String[] dfa = {
		"s0-'a'->s1\n" +
		"s1-'b'->s2\n" +
		"s2-EOF->:s3^=>1\n",

		"s0-'a'->s1\n" +
		"s1-'b'->s2\n" +
		"s2-EOF->:s3^=>1\n" +
		"s2-'c'->:s4=>3\n",

		"s0-'a'->s1\n" +
		"s1-'b'->s2\n" +
		"s2-EOF->:s3^=>1\n" +
		"s2-'c'->:s4=>3\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testRecursiveLeftPrefix() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n" +
		"LP : '(' ;\n" +
		"RP : ')' ;\n" +
		"INT : '0'..'9'+ ;\n"
		);
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"tokens {A,B,C,LP,RP,INT}\n" +
		"a : e B | e C ;\n" +
		"e : LP e RP\n" +
		"  | INT\n" +
		"  ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "34b", 1);
		checkPredictedAlt(lg, g, decision, "34c", 2);
		checkPredictedAlt(lg, g, decision, "((34))b", 1);
		checkPredictedAlt(lg, g, decision, "((34))c", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"34b",
		"34c",
		"((34))b",
		"((34))c"
		};
		String[] dfa = {
		"s0-INT->s1\n" +
		"s1-'b'->:s2=>1\n",

		"s0-INT->s1\n" +
		"s1-'b'->:s2=>1\n" +
		"s1-'c'->:s3=>2\n",

		"s0-'('->s4\n" +
		"s0-INT->s1\n" +
		"s1-'b'->:s2=>1\n" +
		"s1-'c'->:s3=>2\n" +
		"s4-'('->s5\n" +
		"s5-INT->s6\n" +
		"s6-')'->s7\n" +
		"s7-')'->s1\n",

		"s0-'('->s4\n" +
		"s0-INT->s1\n" +
		"s1-'b'->:s2=>1\n" +
		"s1-'c'->:s3=>2\n" +
		"s4-'('->s5\n" +
		"s5-INT->s6\n" +
		"s6-')'->s7\n" +
		"s7-')'->s1\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testRecursiveLeftPrefixWithAorABIssue() throws Exception {
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"A : 'a' ;\n" +
		"B : 'b' ;\n" +
		"C : 'c' ;\n" +
		"LP : '(' ;\n" +
		"RP : ')' ;\n" +
		"INT : '0'..'9'+ ;\n"
		);
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"tokens {A,B,C,LP,RP,INT}\n" +
		"a : e A | e A B ;\n" +
		"e : LP e RP\n" +
		"  | INT\n" +
		"  ;");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "34a", 1);
		checkPredictedAlt(lg, g, decision, "34ab", 2); // PEG would miss this one!
		checkPredictedAlt(lg, g, decision, "((34))a", 1);
		checkPredictedAlt(lg, g, decision, "((34))ab", 2);

		// After matching these inputs for decision, what is DFA after each prediction?
		String[] inputs = {
		"34a",
		"34ab",
		"((34))a",
		"((34))ab",
		};
		String[] dfa = {
		"s0-INT->s1\n" +
		"s1-'a'->s2\n" +
		"s2-EOF->:s3=>1\n",

		"s0-INT->s1\n" +
		"s1-'a'->s2\n" +
		"s2-EOF->:s3=>1\n" +
		"s2-'b'->:s4=>2\n",

		"s0-'('->s5\n" +
		"s0-INT->s1\n" +
		"s1-'a'->s2\n" +
		"s2-EOF->:s3=>1\n" +
		"s2-'b'->:s4=>2\n" +
		"s5-'('->s6\n" +
		"s6-INT->s7\n" +
		"s7-')'->s8\n" +
		"s8-')'->s1\n",

		"s0-'('->s5\n" +
		"s0-INT->s1\n" +
		"s1-'a'->s2\n" +
		"s2-EOF->:s3=>1\n" +
		"s2-'b'->:s4=>2\n" +
		"s5-'('->s6\n" +
		"s6-INT->s7\n" +
		"s7-')'->s8\n" +
		"s8-')'->s1\n",
		};
		checkDFAConstruction(lg, g, decision, inputs, dfa);
	}

	@Test public void testContinuePrediction() throws Exception {
		// Sam found prev def of ambiguity was too restrictive.
		// E.g., (13, 1, []), (13, 2, []), (12, 2, []) should not
		// be declared ambig since (12, 2, []) can take us to
		// unambig state maybe. keep going.
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"ID : 'a'..'z' ;\n" + // one char
		"SEMI : ';' ;\n"+
		"INT : '0'..'9'+ ;\n"
		);
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"tokens {ID,SEMI,INT}\n" +
		"a : (ID | ID ID?) SEMI ;");
		int decision = 1;
		checkPredictedAlt(lg, g, decision, "a;", 1);
		checkPredictedAlt(lg, g, decision, "ab;", 2);
	}

	@Test public void testContinuePrediction2() throws Exception {
		// ID is ambig for first two alts, but ID SEMI lets us move forward with alt 3
		LexerGrammar lg = new LexerGrammar(
		"lexer grammar L;\n" +
		"ID : 'a'..'z' ;\n" + // one char
		"SEMI : ';' ;\n"+
		"INT : '0'..'9'+ ;\n"
		);
		Grammar g = new Grammar(
		"parser grammar T;\n"+
		"tokens {ID,SEMI,INT}\n" +
		"a : ID | ID | ID SEMI ;\n");
		int decision = 0;
		checkPredictedAlt(lg, g, decision, "a", 1);
		checkPredictedAlt(lg, g, decision, "a;", 3);
	}

	@Test public void testAltsForLRRuleComputation() throws Exception {
		Grammar g = new Grammar(
		"grammar T;\n" +
		"e : e '*' e\n" +
		"  | INT\n" +
		"  | e '+' e\n" +
		"  | ID\n" +
		"  ;\n" +
		"ID : [a-z]+ ;\n" +
		"INT : [0-9]+ ;\n" +
		"WS : [ \\r\\t\\n]+ ;");
		Rule e = g.getRule("e");
		assertTrue(e instanceof LeftRecursiveRule);
		LeftRecursiveRule lr = (LeftRecursiveRule)e;
		assertEquals("[0, 2, 4]", Arrays.toString(lr.getPrimaryAlts()));
		assertEquals("[0, 1, 3]", Arrays.toString(lr.getRecursiveOpAlts()));
	}

	@Test public void testAltsForLRRuleComputation2() throws Exception {
		Grammar g = new Grammar(
		"grammar T;\n" +
		"e : INT\n" +
		"  | e '*' e\n" +
		"  | ID\n" +
		"  ;\n" +
		"ID : [a-z]+ ;\n" +
		"INT : [0-9]+ ;\n" +
		"WS : [ \\r\\t\\n]+ ;");
		Rule e = g.getRule("e");
		assertTrue(e instanceof LeftRecursiveRule);
		LeftRecursiveRule lr = (LeftRecursiveRule)e;
		assertEquals("[0, 1, 3]", Arrays.toString(lr.getPrimaryAlts()));
		assertEquals("[0, 2]", Arrays.toString(lr.getRecursiveOpAlts()));
	}

	@Test public void testAltsForLRRuleComputation3() throws Exception {
		Grammar g = new Grammar(
		"grammar T;\n" +
		"random : 'blort';\n" + // should have no effect
		"e : '--' e\n" +
		"  | e '*' e\n" +
		"  | e '+' e\n" +
		"  | e '--'\n" +
		"  | ID\n" +
		"  ;\n" +
		"ID : [a-z]+ ;\n" +
		"INT : [0-9]+ ;\n" +
		"WS : [ \\r\\t\\n]+ ;");
		Rule e = g.getRule("e");
		assertTrue(e instanceof LeftRecursiveRule);
		LeftRecursiveRule lr = (LeftRecursiveRule)e;
		assertEquals("[0, 1, 5]", Arrays.toString(lr.getPrimaryAlts()));
		assertEquals("[0, 2, 3, 4]", Arrays.toString(lr.getRecursiveOpAlts()));
	}

	/** first check that the ATN predicts right alt.
	 *  Then check adaptive prediction.
	 */
	public void checkPredictedAlt(LexerGrammar lg, Grammar g, int decision,
	                              String inputString, int expectedAlt)
	{
		Tool.internalOption_ShowATNConfigsInDFA = true;
		ATN lexatn = createATN(lg, true);
		LexerATNSimulator lexInterp =
		new LexerATNSimulator(lexatn,new DFA[] { new DFA(lexatn.modeToStartState.get(Lexer.DEFAULT_MODE)) },new PredictionContextCache());
		IntegerList types = getTokenTypesViaATN(inputString, lexInterp);
//		System.out.println(types);

		semanticProcess(lg);
		g.importVocab(lg);
		semanticProcess(g);

		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		DOTGenerator dot = new DOTGenerator(g);

		Rule r = g.getRule("a");
//		if ( r!=null) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));
		r = g.getRule("b");
//		if ( r!=null) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));
		r = g.getRule("e");
//		if ( r!=null) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));
		r = g.getRule("ifstat");
//		if ( r!=null) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));
		r = g.getRule("block");
//		if ( r!=null) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));

		// Check ATN prediction
//		ParserATNSimulator interp = new ParserATNSimulator(atn);
		TokenStream input = new IntTokenStream(types);
		ParserInterpreterForTesting interp = new ParserInterpreterForTesting(g, input);
		int alt = interp.adaptivePredict(input, decision, ParserRuleContext.EMPTY);

		assertEquals(expectedAlt, alt);

		// Check adaptive prediction
		input.seek(0);
		alt = interp.adaptivePredict(input, decision, null);
		assertEquals(expectedAlt, alt);
		// run 2x; first time creates DFA in atn
		input.seek(0);
		alt = interp.adaptivePredict(input, decision, null);
		assertEquals(expectedAlt, alt);
	}

	public void checkDFAConstruction(LexerGrammar lg, Grammar g, int decision,
	                                 String[] inputString, String[] dfaString)
	{
//		Tool.internalOption_ShowATNConfigsInDFA = true;
		ATN lexatn = createATN(lg, true);
		LexerATNSimulator lexInterp =
		new LexerATNSimulator(lexatn,new DFA[] { new DFA(lexatn.getDecisionState(Lexer.DEFAULT_MODE)) }, new PredictionContextCache());

		semanticProcess(lg);
		g.importVocab(lg);
		semanticProcess(g);

		ParserInterpreterForTesting interp = new ParserInterpreterForTesting(g, null);
		for (int i=0; i<inputString.length; i++) {
			// Check DFA
			IntegerList types = getTokenTypesViaATN(inputString[i], lexInterp);
//			System.out.println(types);
			TokenStream input = new IntTokenStream(types);
			try {
				interp.adaptivePredict(input, decision, ParserRuleContext.EMPTY);
			}
			catch (NoViableAltException nvae) {
				nvae.printStackTrace(System.err);
			}
			DFA dfa = interp.parser.decisionToDFA[decision];
			assertEquals(dfaString[i], dfa.toString(g.getVocabulary()));
		}
	}
}
