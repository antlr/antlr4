package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.AmbiguityInfo;
import org.antlr.v4.runtime.atn.BasicBlockStartState;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarParserInterpreter;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestAmbigParseTrees {
	@Test public void testParseDecisionWithinAmbiguousStartRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A x C" +
			"  | A B C" +
			"  ;" +
			"x : B ; \n",
			lg);

		testInterpAtSpecificAlt(lg, g, "s", 1, "abc", "(s a (x b) c)");
		testInterpAtSpecificAlt(lg, g, "s", 2, "abc", "(s a b c)");
	}

	@Test public void testAmbigAltsAtRoot() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A x C" +
			"  | A B C" +
			"  ;" +
			"x : B ; \n",
			lg);

		String startRule = "s";
		String input = "abc";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 0;
		String expectedOverallTree = "(s a (x b) c)";
		String[] expectedParseTrees = {"(s a (x b) c)","(s a b c)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	@Test public void testAmbigAltsNotAtRoot() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : a ;" +
			"a : b ;" +
			"b : A x C" +
			"  | A B C" +
			"  ;" +
			"x : B ; \n",
			lg);

		String startRule = "s";
		String input = "abc";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 0;
		String expectedOverallTree = "(s (a (b a (x b) c)))";
		String[] expectedParseTrees = {"(b a (x b) c)","(b a b c)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	@Test public void testAmbigAltDipsIntoOuterContextToRoot() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"SELF : 'self' ;\n" +
			"ID : [a-z]+ ;\n" +
			"DOT : '.' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
//			"s : e ;\n"+
			"e : p (DOT ID)* ;\n"+
			"p : SELF" +
			"  | SELF DOT ID" +
			"  ;",
			lg);

		String startRule = "e";
		String input = "self.x";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 1; // decision in s
		String expectedOverallTree = "(e (p self) . x)";
		String[] expectedParseTrees = {"(e (p self) . x)","(p self . x)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	@Test public void testAmbigAltDipsIntoOuterContextBelowRoot() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"SELF : 'self' ;\n" +
			"ID : [a-z]+ ;\n" +
			"DOT : '.' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e ;\n"+
			"e : p (DOT ID)* ;\n"+
			"p : SELF" +
			"  | SELF DOT ID" +
			"  ;",
			lg);

		String startRule = "s";
		String input = "self.x";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 1; // decision in s
		String expectedOverallTree = "(s (e (p self) . x))";
		String[] expectedParseTrees = {"(e (p self) . x)","(p self . x)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	@Test public void testAmbigAltInLeftRecursiveBelowStartRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"SELF : 'self' ;\n" +
			"ID : [a-z]+ ;\n" +
			"DOT : '.' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e ;\n" +
			"e : p | e DOT ID ;\n"+
			"p : SELF" +
			"  | SELF DOT ID" +
			"  ;",
			lg);

		String startRule = "s";
		String input = "self.x";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 1; // decision in s
		String expectedOverallTree = "(s (e (e (p self)) . x))";
		String[] expectedParseTrees = {"(e (e (p self)) . x)","(p self . x)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	@Test public void testAmbigAltInLeftRecursiveStartRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"SELF : 'self' ;\n" +
			"ID : [a-z]+ ;\n" +
			"DOT : '.' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"e : p | e DOT ID ;\n"+
			"p : SELF" +
			"  | SELF DOT ID" +
			"  ;",
			lg);

		String startRule = "e";
		String input = "self.x";
		String expectedAmbigAlts = "{1, 2}";
		int decision = 1; // decision in s
		String expectedOverallTree = "(e (e (p self)) . x)";
		String[] expectedParseTrees = {"(e (e (p self)) . x)","(p self . x)"};

		testInterp(lg, g, startRule, input, decision,
				   expectedAmbigAlts,
				   expectedOverallTree, expectedParseTrees);
	}

	public void testInterp(LexerGrammar lg, Grammar g,
						   String startRule, String input, int decision,
						   String expectedAmbigAlts,
						   String overallTree,
						   String[] expectedParseTrees)
	{
		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		final GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		parser.setProfile(true);
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);

		// PARSE
		int ruleIndex = g.rules.get(startRule).index;
		ParserRuleContext parseTree = parser.parse(ruleIndex);
		assertEquals(overallTree, parseTree.toStringTree(parser));
		System.out.println();

		DecisionInfo[] decisionInfo = parser.getParseInfo().getDecisionInfo();
		List<AmbiguityInfo> ambiguities = decisionInfo[decision].ambiguities;
		assertEquals(1, ambiguities.size());
		AmbiguityInfo ambiguityInfo = ambiguities.get(0);

		List<ParserRuleContext> ambiguousParseTrees =
			GrammarParserInterpreter.getAllPossibleParseTrees(g,
															  parser,
															  tokens,
															  ambiguityInfo.decision,
															  ambiguityInfo.ambigAlts,
															  ambiguityInfo.startIndex,
															  ambiguityInfo.stopIndex,
															  ruleIndex);
		assertEquals(expectedAmbigAlts, ambiguityInfo.ambigAlts.toString());

		assertEquals(ambiguityInfo.ambigAlts.cardinality(), ambiguousParseTrees.size());
		for (int i = 0; i<ambiguousParseTrees.size(); i++) {
			ParserRuleContext t = ambiguousParseTrees.get(i);
			assertEquals(expectedParseTrees[i], t.toStringTree(parser));
		}
	}

	void testInterpAtSpecificAlt(LexerGrammar lg, Grammar g,
								 String startRule, int startAlt,
								 String input,
								 String expectedParseTree)
	{
		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		ParserInterpreter parser = g.createParserInterpreter(tokens);
		RuleStartState ruleStartState = g.atn.ruleToStartState[g.getRule(startRule).index];
		Transition tr = ruleStartState.transition(0);
		ATNState t2 = tr.target;
		if ( !(t2 instanceof BasicBlockStartState) ) {
			throw new IllegalArgumentException("rule has no decision: "+startRule);
		}
		parser.addDecisionOverride(((DecisionState)t2).decision, 0, startAlt);
		ParseTree t = parser.parse(g.rules.get(startRule).index);
		assertEquals(expectedParseTree, t.toStringTree(parser));
	}
}
