/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool.interp;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.tool.Grammar;

public class ParserInterpreter {
	class DummyParser extends Parser {
		public Grammar g;
		public DummyParser(Grammar g, TokenStream input) {
			super(input);
			this.g = g;
		}

		@Override
		public String getGrammarFileName() {
			return null;
		}

		@Override
		public String[] getRuleNames() {
			return g.rules.keySet().toArray(new String[g.rules.size()]);
		}

		@Override
		public String[] getTokenNames() {
			return g.getTokenNames();
		}

		@Override
		public ATN getATN() {
			return null;
		}
	}

	protected Grammar g;
	protected ParserATNSimulator<Token> atnSimulator;
	protected TokenStream input;

	public ParserInterpreter(@NotNull Grammar g) {
		this.g = g;
	}

	public ParserInterpreter(@NotNull Grammar g, @NotNull TokenStream input) {
		Tool antlr = new Tool();
		antlr.process(g,false);
		atnSimulator = new ParserATNSimulator<Token>(new DummyParser(g, input), g.atn);
	}

	public int predictATN(@NotNull DFA dfa, @NotNull SymbolStream<Token> input,
						  @Nullable ParserRuleContext outerContext,
						  boolean useContext)
	{
		return atnSimulator.predictATN(dfa, input, outerContext, useContext);
	}

	public int adaptivePredict(@NotNull SymbolStream<Token> input, int decision,
							   @Nullable ParserRuleContext outerContext)
	{
		return atnSimulator.adaptivePredict(input, decision, outerContext);
	}

	public int matchATN(@NotNull TokenStream input,
						@NotNull ATNState startState)
	{
		if (startState.getNumberOfTransitions() == 1) {
			return 1;
		}
		else if (startState instanceof DecisionState) {
			return atnSimulator.adaptivePredict(input, ((DecisionState)startState).decision, null, false);
		}
		else if (startState.getNumberOfTransitions() > 0) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public ParserATNSimulator<Token> getATNSimulator() {
		return atnSimulator;
	}

}
