/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.test.runtime.RunOptions;
import org.antlr.v4.test.runtime.Stage;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.antlr.v4.test.tool.ToolTestUtils.createOptionsForJavaToolTests;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class TestParserProfiler {
	final static LexerGrammar lg;

	static {
		try {
			lg = new LexerGrammar(
					"lexer grammar L;\n" +
							"WS : [ \\r\\t\\n]+ -> channel(HIDDEN) ;\n" +
							"SEMI : ';' ;\n" +
							"DOT : '.' ;\n" +
							"ID : [a-zA-Z]+ ;\n" +
							"INT : [0-9]+ ;\n" +
							"PLUS : '+' ;\n" +
							"MULT : '*' ;\n");
		} catch (RecognitionException e) {
			throw new RuntimeException(e);
		}
	}

	@Test public void testLL1() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ';'{}\n" +
				"  | '.'\n" +
				"  ;\n",
				lg);

		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", ";");
		assertEquals(1, info.length);
		String expecting =
				"{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=1, " +
				"SLL_ATNTransitions=1, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}";
		assertEquals(expecting, info[0].toString());
	}

	@Test public void testLL2() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ID ';'{}\n" +
				"  | ID '.'\n" +
				"  ;\n",
				lg);

		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "xyz;");
		assertEquals(1, info.length);
		String expecting =
				"{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=2, " +
				"SLL_ATNTransitions=2, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}";
		assertEquals(expecting, info[0].toString());
	}

	@Test public void testRepeatedLL2() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ID ';'{}\n" +
				"  | ID '.'\n" +
				"  ;\n",
				lg);

		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "xyz;", "abc;");
		assertEquals(1, info.length);
		String expecting =
				"{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=4, " +
				"SLL_ATNTransitions=2, SLL_DFATransitions=2, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}";
		assertEquals(expecting, info[0].toString());
	}

	@Test public void test3xLL2() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ID ';'{}\n" +
				"  | ID '.'\n" +
				"  ;\n",
				lg);

		// The '.' vs ';' causes another ATN transition
		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "xyz;", "abc;", "z.");
		assertEquals(1, info.length);
		String expecting =
				"{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=6, " +
				"SLL_ATNTransitions=3, SLL_DFATransitions=3, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}";
		assertEquals(expecting, info[0].toString());
	}

	@Test public void testOptional() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ID ('.' ID)? ';'\n" +
				"  | ID INT \n" +
				"  ;\n",
				lg);

		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "a.b;");
		assertEquals(2, info.length);
		String expecting =
			"[{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=1, " +
			  "SLL_ATNTransitions=1, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, " +
			 "{decision=1, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=2, " +
			  "SLL_ATNTransitions=2, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}]";
		assertEquals(expecting, Arrays.toString(info));
	}

	@Test public void test2xOptional() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : ID ('.' ID)? ';'\n" +
				"  | ID INT \n" +
				"  ;\n",
				lg);

		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "a.b;", "a.b;");
		assertEquals(2, info.length);
		String expecting =
			"[{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=2, " +
			  "SLL_ATNTransitions=1, SLL_DFATransitions=1, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, " +
			 "{decision=1, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=4, " +
			  "SLL_ATNTransitions=2, SLL_DFATransitions=2, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}]";
		assertEquals(expecting, Arrays.toString(info));
	}

	@Test public void testContextSensitivity() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : '.' e ID \n" +
			"  | ';' e INT ID ;\n" +
			"e : INT | ;\n",
			lg);
		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "a", "; 1 x");
		assertEquals(2, info.length);
		String expecting =
			"{decision=1, contextSensitivities=1, errors=0, ambiguities=0, SLL_lookahead=3, SLL_ATNTransitions=2, SLL_DFATransitions=0, LL_Fallback=1, LL_lookahead=3, LL_ATNTransitions=2}";
		assertEquals(expecting, info[1].toString());
	}

	@Disabled
	@Test public void testSimpleLanguage() throws Exception {
		Grammar g = new Grammar(TestXPath.grammar);
		String input =
			"def f(x,y) { x = 3+4*1*1/5*1*1+1*1+1; y; ; }\n" +
			"def g(x,a,b,c,d,e) { return 1+2*x; }\n"+
			"def h(x) { a=3; x=0+1; return a*x; }\n";
		DecisionInfo[] info = interpAndGetDecisionInfo(g.getImplicitLexer(), g, "prog", input);
		String expecting =
			"[{decision=0, contextSensitivities=1, errors=0, ambiguities=0, SLL_lookahead=3, " +
			"SLL_ATNTransitions=2, SLL_DFATransitions=0, LL_Fallback=1, LL_ATNTransitions=1}]";


		assertEquals(expecting, Arrays.toString(info));
		assertEquals(1, info.length);
	}

	@Disabled
	@Test public void testDeepLookahead() throws Exception {
		Grammar g = new Grammar(
				"parser grammar T;\n" +
				"s : e ';'\n" +
				"  | e '.' \n" +
				"  ;\n" +
				"e : (ID|INT) ({true}? '+' e)*\n" +       // d=1 entry, d=2 bypass
				"  ;\n",
				lg);

		// pred forces to
		// ambig and ('+' e)* tail recursion forces lookahead to fall out of e
		// any non-precedence predicates are always evaluated as true by the interpreter
		DecisionInfo[] info = interpAndGetDecisionInfo(lg, g, "s", "a+b+c;");
		// at "+b" it uses k=1 and enters loop then calls e for b...
		// e matches and d=2 uses "+c;" for k=3
		assertEquals(2, info.length);
		String expecting =
			"[{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=6, " +
			  "SLL_ATNTransitions=6, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, " +
			 "{decision=1, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=4, " +
			  "SLL_ATNTransitions=2, SLL_DFATransitions=2, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}]";
		assertEquals(expecting, Arrays.toString(info));
	}

	@Test public void testProfilerGeneratedCode() {
		String grammar =
			"grammar T;\n" +
			"s : a+ ID EOF ;\n" +
			"a : ID ';'{}\n" +
			"  | ID '.'\n" +
			"  ;\n"+
			"WS : [ \\r\\t\\n]+ -> channel(HIDDEN) ;\n" +
			"SEMI : ';' ;\n" +
			"DOT : '.' ;\n" +
			"ID : [a-zA-Z]+ ;\n" +
			"INT : [0-9]+ ;\n" +
			"PLUS : '+' ;\n" +
			"MULT : '*' ;\n";

		RunOptions runOptions = createOptionsForJavaToolTests("T.g4", grammar, "TParser", "TLexer",
				false, false, "s", "xyz;abc;z.q",
				true, false, Stage.Execute, false);
		try (JavaRunner runner = new JavaRunner()) {
			ExecutedState state = (ExecutedState) runner.run(runOptions);
			String expecting =
					"[{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=6, SLL_ATNTransitions=4, " +
							"SLL_DFATransitions=2, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}," +
							" {decision=1, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=6, " +
							"SLL_ATNTransitions=3, SLL_DFATransitions=3, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}]\n";
			assertEquals(expecting, state.output);
			assertEquals("", state.errors);
		}
	}

	public DecisionInfo[] interpAndGetDecisionInfo(
			LexerGrammar lg, Grammar g,
			String startRule, String... input)
	{

		LexerInterpreter lexEngine = lg.createLexerInterpreter(null);
		ParserInterpreter parser = g.createParserInterpreter(null);
		parser.setProfile(true);
		for (String s : input) {
			lexEngine.reset();
			parser.reset();
			lexEngine.setInputStream(new ANTLRInputStream(s));
			CommonTokenStream tokens = new CommonTokenStream(lexEngine);
			parser.setInputStream(tokens);
			Rule r = g.rules.get(startRule);
			if ( r==null ) {
				return parser.getParseInfo().getDecisionInfo();
			}
			ParserRuleContext t = parser.parse(r.index);
//			try {
//				Utils.waitForClose(t.inspect(parser).get());
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			System.out.println(t.toStringTree(parser));
		}
		return parser.getParseInfo().getDecisionInfo();
	}
}
