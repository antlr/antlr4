/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.tool.Grammar;

public class ParserInterpreterForTesting {
	public static class DummyParser extends Parser {
		public final ATN atn;
		public final DFA[] decisionToDFA; // not shared for interp
		public final PredictionContextCache sharedContextCache =
			new PredictionContextCache();

		public Grammar g;
		public DummyParser(Grammar g, ATN atn, TokenStream input) {
			super(input);
			this.g = g;
			this.atn = atn;
			this.decisionToDFA = new DFA[atn.getNumberOfDecisions()];
			for (int i = 0; i < decisionToDFA.length; i++) {
				decisionToDFA[i] = new DFA(atn.getDecisionState(i), i);
			}
		}

		@Override
		public String getGrammarFileName() {
			throw new UnsupportedOperationException("not implemented");
		}

		@Override
		public String[] getRuleNames() {
			return g.rules.keySet().toArray(new String[0]);
		}

		@Override
		@Deprecated
		public String[] getTokenNames() {
			return g.getTokenNames();
		}

		@Override
		public ATN getATN() {
			return atn;
		}
	}

	protected Grammar g;
	public DummyParser parser;
	protected ParserATNSimulator atnSimulator;
	protected TokenStream input;

	public ParserInterpreterForTesting(Grammar g) {
		this.g = g;
	}

	public ParserInterpreterForTesting(Grammar g, TokenStream input) {
		Tool antlr = new Tool();
		antlr.process(g,false);
		parser = new DummyParser(g, g.atn, input);
		atnSimulator =
			new ParserATNSimulator(parser, g.atn, parser.decisionToDFA,
										  parser.sharedContextCache);
	}

	public int adaptivePredict(TokenStream input, int decision,
							   ParserRuleContext outerContext)
	{
		return atnSimulator.adaptivePredict(input, decision, outerContext);
	}

	public int matchATN(TokenStream input,
						ATNState startState)
	{
		if (startState.getNumberOfTransitions() == 1) {
			return 1;
		}
		else if (startState instanceof DecisionState) {
			return atnSimulator.adaptivePredict(input, ((DecisionState)startState).decision, null);
		}
		else if (startState.getNumberOfTransitions() > 0) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public ParserATNSimulator getATNSimulator() {
		return atnSimulator;
	}

}
