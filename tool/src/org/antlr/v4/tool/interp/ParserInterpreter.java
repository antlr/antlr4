package org.antlr.v4.tool.interp;


import org.antlr.v4.Tool;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LeftRecursiveRuleTransition;
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
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.PredAST;

import java.util.NoSuchElementException;

/** A parser simulator that mimics what ANTLR's generated
 *  parser code does. A ParserATNSimulator is used to make
 *  predictions but this class moves a pointer through the
 *  ATN to simulate parsing. ParserATNSimulator just
 *  makes us efficient rather than, say, backtracking.
 */
public class ParserInterpreter extends Parser {
	protected Grammar g;

	public final DFA[] decisionToDFA; // not shared like it is for generated parsers
	public final PredictionContextCache sharedContextCache =
		new PredictionContextCache();
	public RuleFunctionCall locals;

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
		setInterpreter(new ParserATNSimulator(this, g.atn,
											  decisionToDFA,
											  sharedContextCache));
	}

	/** Begin parsing at startRuleName */
	public ParserRuleContext parse(String startRuleName) {
		Rule r = g.getRule(startRuleName);
		if ( r==null ) {
			throw new NoSuchElementException(startRuleName+" is not a valid rule name in "+g.getName());
		}
		RuleStartState startRuleStartState = g.atn.ruleToStartState[r.index];
		RuleStopState startRuleStopState = g.atn.ruleToStopState[r.index];
		// Where are we in the grammar's ATN during the parse.
		ATNState p = startRuleStartState;
		setState(startRuleStartState.stateNumber);

		InterpreterRuleContext rootContext =
			new InterpreterRuleContext(null, startRuleStartState.stateNumber, r.index);
		_ctx = rootContext;

		// Create space for rule function locals needed per rule invocation
		locals = new RuleFunctionCall(null);

		loop:
		while ( true ) {
			System.out.println("p is "+p.getClass().getCanonicalName()+": s"+getState());
			switch ( p.getStateType() ) {
				case ATNState.RULE_STOP : // pop; return from rule
					if ( p == startRuleStopState ) {
						// done; don't look for EOF unless they mentioned
						// which means it'll be inside the rule submachine proper
						System.out.println("pop from start rule");
						locals = locals.parent; // pop local var context
						break loop;
					}
					ATNState returnState = g.atn.states.get(_ctx.invokingState);
					System.out.println("pop from " + g.getRule(p.ruleIndex) + " to " +
										   g.getRule(returnState.ruleIndex));
					RuleTransition retStateCallEdge =
						(RuleTransition)returnState.transition(0);
					if ( g.getRule(p.ruleIndex).isLeftRecursive() ) {
						System.out.println("unrolling");
						unrollRecursionContexts(locals._parentctx);
					}
					else {
						exitRule();
					}
					p = retStateCallEdge.followState;
					locals = locals.parent; // pop local var context
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

					int alt = 1;
					if ( d.getNumberOfTransitions()>1 ) {
						System.out.println("decision "+d.decision+", input="+_input.LT(1));
						alt = getInterpreter().adaptivePredict(_input,
															   d.decision,
															   null);
						System.out.println("predict "+alt);
					}

					// handle case where we need to push new recursive context
					// is this the STAR_LOOP_ENTRY built during left-recur
					// elimination?  Check left-recur rule and it's
					// getOperatorLoopBlockStartState(). Also only do it
					// if we're not skipping loop.
					Rule curRule = g.getRule(p.ruleIndex);
					if ( d.getStateType()==ATNState.STAR_LOOP_ENTRY &&
						curRule.isLeftRecursive() &&
						d == ((LeftRecursiveRule)curRule).getOperatorLoopBlockEntryState()) // always 2 alts in op loop star entry decision
					{
						System.out.println("left recur start of suffix block");
						if ( alt != 2 ) {
							System.out.println("not exit branch");
							// if we get past STAR_LOOP_ENTRY, one of (...)* blk
							// will match. This new recur ctx is pushed for any kind
							locals._localctx =
								new InterpreterRuleContext(locals._parentctx,
														   locals._parentState,
														   p.ruleIndex);
							pushNewRecursionContext(locals._localctx,
													locals._startState,
													curRule.index);
						}
					}

					p = d.transition(alt-1).target;
					// TODO: enterOuterAlt()
					setState(p.stateNumber);
					continue loop;
			}

			// must be just 1 transition here as it's not a decision
			Transition t = p.getTransitions()[0];
			switch ( t.getSerializationType() ) {
				case Transition.RULE: // push
				case Transition.LEFT_RECUR_RULE:
					locals = new RuleFunctionCall(locals); // new local var space
					locals._parentctx = _ctx;
					int returnState = p.stateNumber; // invoker is current state until we jump
					locals._parentState = returnState;
					ATNState targetStartState = t.target;
					locals._startState = targetStartState.stateNumber;

					InterpreterRuleContext callctx =
						new InterpreterRuleContext(_ctx, returnState,
												   targetStartState.ruleIndex);
					locals._localctx = callctx;
					Rule targetRule = g.getRule(targetStartState.ruleIndex);
					System.out.println("push " + targetRule);
					if ( targetRule.isLeftRecursive() ) {
						locals._prec = ((LeftRecursiveRuleTransition)t).precedence;
//						System.out.println("arg "+locals._prec);
						enterRecursionRule(callctx, targetRule.index);
					}
					else {
						enterRule(callctx, targetStartState.stateNumber, targetRule.index);
					}
					p = targetStartState; // jump to rule
					setState(p.stateNumber);
					break;

				case Transition.ATOM:
					AtomTransition at = (AtomTransition)t;
					match(at.label);
					System.out.println("MATCH " + g.getTokenDisplayName(at.label));
					p = at.target;
					setState(p.stateNumber);
					break;
				case Transition.SET:
					break;

				case Transition.NOT_SET:
					break;

				case Transition.EPSILON:
				case Transition.PREDICATE:
				case Transition.PREC_PREDICATE:
				case Transition.ACTION:
					p = t.target;
					setState(p.stateNumber);
					break;

				default:
					// error
					break loop;
			}
		}
		return rootContext;
	}

	/** Predicates are evaluated during prediction and not by this interpreter.
	 *  However, the ATN simulator asks the recognizer to evaluate predicates
	 *  and so we override this method to interpret the special, generated
	 *  predicates created during left recursion elimination.
	 *
	 *  e.g., "{3 >= $_p}?";
	 */
	@Override
	public boolean sempred(@Nullable RuleContext localctx, int ruleIndex, int predIndex) {
		System.out.print("EVAL pred rule:action=" + ruleIndex + ":" + predIndex);
		PredAST predAST = g.sempreds.get(predIndex);
		Rule r = g.getRule(ruleIndex);
		System.out.print(", sempred " + predAST.getText());
		String[] elems = predAST.getText().split(" ");
		if ( elems.length==3 && elems[1].equals(">=") && elems[2].equals("$_p}?") ) {
			String digits = elems[0].substring(1);
			int precedence = Integer.parseInt(digits);
			System.out.println(", prec = " + precedence + ", rule arg is " + locals._prec);
			return precedence >= locals._prec;
		}
		System.out.println();

//		System.out.println(g.sempreds.get(ac));
		return true;
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
