package org.antlr.v4.tool.interp;


import org.antlr.v4.Tool;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.RuleStopState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.StarLoopbackState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.util.NoSuchElementException;

/** A parser simulator that mimics what ANTLR's generated
 *  parser code does. A ParserATNSimulator is used to make
 *  predictions but this class moves a pointer through the
 *  ATN to simulate parsing. ParserATNSimulator just
 *  makes us efficient rather than, say, backtracking.
 */
public class ParserInterpreter extends Parser {
	protected Grammar g;
	protected ParserATNSimulator atnSimulator;

	public final DFA[] decisionToDFA; // not shared like it is for generated parsers
	public final PredictionContextCache sharedContextCache =
		new PredictionContextCache();

	public ParserInterpreter(Grammar g, TokenStream input) {
		super(input);
		this.g = g;
		Tool antlr = new Tool();
		antlr.process(g,false);
		decisionToDFA = new DFA[g.atn.getNumberOfDecisions()];
		for (int i = 0; i < decisionToDFA.length; i++) {
			decisionToDFA[i] = new DFA(g.atn.getDecisionState(i), i);
		}
		// get atn simulator that knows how to do predictions
		atnSimulator = new ParserATNSimulator(this, g.atn,
											  decisionToDFA,
											  sharedContextCache);
	}

	public static class InterpreterRuleContext extends ParserRuleContext {
		protected int ruleIndex;

		public InterpreterRuleContext(@Nullable ParserRuleContext parent,
									  int invokingStateNumber,
									  int ruleIndex)
		{
			super(parent, invokingStateNumber);
			this.ruleIndex = ruleIndex;
		}

		@Override
		public int getRuleIndex() {	return ruleIndex; }
	}

	/** Begin parsing at startRuleName */
	public ParserRuleContext parse(String startRuleName) {
		Rule r = g.getRule(startRuleName);
		if ( r==null ) {
			throw new NoSuchElementException(startRuleName+" is not a valid rule name in "+g.getName());
		}
		RuleStartState startState = g.atn.ruleToStartState[r.index];
		RuleStopState stopState = g.atn.ruleToStopState[r.index];
		// Where are we in the grammar's ATN during the parse.
		ATNState p = startState;

		InterpreterRuleContext rootContext =
			new InterpreterRuleContext(null, startState.stateNumber, r.index);
		_ctx = rootContext;

loop:
		while ( true ) {
			System.out.println("p is "+p.getClass().getCanonicalName());
			switch ( p.getStateType() ) {
				case ATNState.RULE_STOP :
					if ( p == stopState ) {
						// done; don't look for EOF unless they mentioned
						// which means it'll be inside the rule submachine
						break loop;
					}
					else { // pop
						ATNState returnState = g.atn.states.get(_ctx.invokingState);
						System.out.println("pop from "+g.getRule(p.ruleIndex)+" to "+
										   g.getRule(returnState.ruleIndex));
						RuleTransition retStateCallEdge =
							(RuleTransition)returnState.transition(0);
						p = retStateCallEdge.followState;
						_ctx = _ctx.getParent();
					}
					break;

				// start a decision
				case ATNState.STAR_LOOP_BACK:
				case ATNState.PLUS_LOOP_BACK:
				case ATNState.BLOCK_START:
				case ATNState.STAR_BLOCK_START:
				case ATNState.PLUS_BLOCK_START:
				case ATNState.STAR_LOOP_ENTRY:
					DecisionState d;
					// A (...)* loop has an entry decision and then the block decision
					// The loop back state should simply jump back to the entry point.
					if ( p.getStateType()==ATNState.STAR_LOOP_BACK ) {
						d = ((StarLoopbackState)p).getLoopEntryState();
					}
					else {
						d = (DecisionState)p;
					}
					if ( d.getNumberOfTransitions()>1 ) {
						System.out.println("decision "+d.decision);
						int alt = atnSimulator.adaptivePredict(_input,
															   d.decision,
															   null);
						System.out.println("predict "+alt);
						p = d.transition(alt-1).target;
					}
					else {
						p = d.transition(0).target;

					}
					continue loop;
			}

			// must be just 1 transition here as it's not a decision
			Transition t = p.getTransitions()[0];
			switch ( t.getSerializationType() ) {
				case Transition.RULE: // push
					_ctx = new ParserRuleContext(_ctx, p.stateNumber);
					p = t.target;
					System.out.println("push "+g.getRule(p.ruleIndex));
					break;

				case Transition.ATOM:
					AtomTransition at = (AtomTransition)t;
					if ( at.matches(_input.LA(1), 0, g.atn.maxTokenType) ) {
						p = at.target;
						System.out.println("matched "+g.getTokenDisplayName(_input.LA(1)));
						_input.consume();
					}
					else {
						throw new InputMismatchException(this);
					}
				case Transition.SET:
					break;

				case Transition.NOT_SET:
					break;

				case Transition.EPSILON:
				case Transition.PREDICATE:
				case Transition.ACTION:
					p = t.target;
					break;

				default:
					// error
					break loop;
			}
		}
		return rootContext;
	}

	@Override
	public ATN getATN() {
		return g.atn;
	}

	@Override
	public String[] getTokenNames() {
		return g.getTokenNames();
	}

	@Override
	public String[] getRuleNames() {
		return g.rules.keySet().toArray(new String[0]);
	}

	@Override
	public String getGrammarFileName() {
		return g.getName();
	}
}
