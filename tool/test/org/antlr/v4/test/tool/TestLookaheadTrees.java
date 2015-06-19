package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.LookaheadEventInfo;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarParserInterpreter;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestLookaheadTrees {
	public static final String lexerText =
		"lexer grammar L;\n" +
		"DOT  : '.' ;\n" +
		"SEMI : ';' ;\n" +
		"BANG : '!' ;\n" +
		"PLUS : '+' ;\n" +
		"LPAREN : '(' ;\n" +
		"RPAREN : ')' ;\n" +
		"MULT : '*' ;\n" +
		"ID : [a-z]+ ;\n" +
		"INT : [0-9]+ ;\n" +
		"WS : [ \\r\\t\\n]+ ;\n";

	@Test
	public void testAlts() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e SEMI EOF ;\n" +
			"e : ID DOT ID\n"+
			"  | ID LPAREN RPAREN\n"+
			"  ;\n",
			lg);

		String startRuleName = "s";
		int decision = 0;

		testLookaheadTrees(lg, g, "a.b;", startRuleName, decision,
						   new String[] {"(e:1 a . b)", "(e:2 a <error .>)"});
	}

	@Test
	public void testAlts2() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e? SEMI EOF ;\n" +
			"e : ID\n" +
			"  | e BANG" +
			"  ;\n",
			lg);

		String startRuleName = "s";
		int decision = 1; // (...)* in e.

		testLookaheadTrees(lg, g, "a;", startRuleName, decision,
						   new String[] {"(e:2 (e:1 a) <error ;>)", // Decision for alt 1 is error as no ! char, but alt 2 (exit) is good.
										 "(s:1 (e:1 a) ; <EOF>)"}); // root s:1 is included to show ';' node
	}

	@Test
	public void testIncludeEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e ;\n" +
			"e : ID DOT ID EOF\n"+
			"  | ID DOT ID EOF\n"+
			"  ;\n",
			lg);

		int decision = 0;
		testLookaheadTrees(lg, g, "a.b", "s", decision,
						   new String[] {"(e:1 a . b <EOF>)", "(e:2 a . b <EOF>)"});
	}

	@Test
	public void testCallLeftRecursiveRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : a BANG EOF;\n" +
			"a : e SEMI \n" +
			"  | ID SEMI \n" +
			"  ;" +
			"e : e MULT e\n" +
			"  | e PLUS e\n" +
			"  | e DOT e\n" +
			"  | ID\n" +
			"  | INT\n" +
			"  ;\n",
			lg);

		int decision = 0;
		testLookaheadTrees(lg, g, "x;!", "s", decision,
						   new String[] {"(a:1 (e:4 x) ;)",
										 "(a:2 x ;)"}); // shouldn't include BANG, EOF
		decision = 2; // (...)* in e
		testLookaheadTrees(lg, g, "x+1;!", "s", decision,
						   new String[] {"(e:1 (e:4 x) <error +>)",
										 "(e:2 (e:4 x) + (e:5 1))",
										 "(e:3 (e:4 x) <error +>)"});
	}

	public void testLookaheadTrees(LexerGrammar lg, Grammar g,
								   String input,
								   String startRuleName,
								   int decision,
								   String[] expectedTrees)
	{
		int startRuleIndex = g.getRule(startRuleName).index;
		InterpreterTreeTextProvider nodeTextProvider =
					new InterpreterTreeTextProvider(g.getRuleNames());

		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		parser.setProfile(true);
		ParseTree t = parser.parse(startRuleIndex);

		DecisionInfo decisionInfo = parser.getParseInfo().getDecisionInfo()[decision];
		LookaheadEventInfo lookaheadEventInfo = decisionInfo.SLL_MaxLookEvent;

		List<ParserRuleContext> lookaheadParseTrees =
			GrammarParserInterpreter.getLookaheadParseTrees(g, parser, tokens, startRuleIndex, lookaheadEventInfo.decision,
															lookaheadEventInfo.startIndex, lookaheadEventInfo.stopIndex);

		assertEquals(expectedTrees.length, lookaheadParseTrees.size());
		for (int i = 0; i < lookaheadParseTrees.size(); i++) {
			ParserRuleContext lt = lookaheadParseTrees.get(i);
			assertEquals(expectedTrees[i], Trees.toStringTree(lt, nodeTextProvider));
		}
	}
}
