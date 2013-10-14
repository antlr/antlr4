/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
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

import java.util.NoSuchElementException;

/** A parser simulator that mimics what ANTLR's generated
 *  parser code does. A ParserATNSimulator is used to make
 *  predictions via adaptivePredict but this class moves a pointer through the
 *  ATN to simulate parsing. ParserATNSimulator just
 *  makes us efficient rather than having to backtrack, for example.
 *
 *  This properly creates parse trees even for left recursive rules.
 *
 *  We rely on the left recursive rule invocation and special predicate
 *  transitions to make left recursive rules work. The ATN factory creates
 *  the special objects but, at least for now, we do not serialize and
 *  deserialize those specialties. That implies that this interpreter only
 *  works at tool time and cannot be used on an ATN built from deserialization.
 *
 *  See TestParserInterpreter for examples.
 */
public class ParserInterpreter extends Parser {
	public static final boolean debug = false;
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

		_input.LA(1); // init lookahead

		// Create space for rule function locals needed per rule invocation
		locals = new RuleFunctionCall(null);

		while ( true ) {
			if ( debug ) System.out.println("p is "+p.getClass().getCanonicalName()+": s"+getState());
			switch ( p.getStateType() ) {
				case ATNState.RULE_STOP : // pop; return from rule
					if ( p == startRuleStopState ) { // are we done parsing?
						// done; don't look for EOF unless they mentioned
						// which means it'll be inside the rule submachine proper
						if ( debug ) System.out.println("pop from start rule");
						locals = locals.parent; // pop local var context
						return rootContext;
					}
					p = visitRuleStopState(p);
					break;

				case ATNState.STAR_LOOP_BACK:
				case ATNState.PLUS_LOOP_BACK:
				case ATNState.BLOCK_START:
				case ATNState.STAR_BLOCK_START:
				case ATNState.PLUS_BLOCK_START:
				case ATNState.STAR_LOOP_ENTRY:
					p = visitDecisionState(p);
					break;

				default :
					// must be just 1 transition here as it's not a decision
					p = visitBasicState(p);
					break;
			}
		}
	}

	protected ATNState visitDecisionState(ATNState p) {
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
			if ( debug ) System.out.println("decision "+d.decision+", input="+_input.LT(1));
			alt = getInterpreter().adaptivePredict(_input,
												   d.decision,
												   null);
			if ( debug ) System.out.println("predict "+alt);
		}
		handleParseTreeForOperatorLoop(p, d, alt);


		p = d.transition(alt-1).target;
		// TODO: enterOuterAlt()
		setState(p.stateNumber);
		return p;
	}

	protected ATNState visitBasicState(ATNState p) {
		Transition t = p.getTransitions()[0];
		switch ( t.getSerializationType() ) {
			case Transition.RULE: // push
			case Transition.LEFT_RECUR_RULE:
				p = visitRuleInvocation(p, t);
				break;
			case Transition.ATOM:
				p = visitAtom((AtomTransition) t);
				break;
			case Transition.SET:
				break;
			case Transition.NOT_SET:
				break;
			case Transition.EPSILON:
			case Transition.PREDICATE:
			case Transition.PREC_PREDICATE:
			case Transition.ACTION:
				p = visitEpsilonOrAction(t);
				break;
		}
		return p;
	}

	protected ATNState visitRuleInvocation(ATNState p, Transition t) {
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
		if ( debug ) System.out.println("push " + targetRule);
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
		return p;
	}

	protected ATNState visitEpsilonOrAction(Transition t) {
		ATNState p;
		p = t.target;
		setState(p.stateNumber);
		return p;
	}

	protected ATNState visitAtom(AtomTransition t) {
		ATNState p;
		match(t.label);
		if ( debug ) System.out.println("MATCH " + g.getTokenDisplayName(t.label));
		p = t.target;
		setState(p.stateNumber);
		return p;
	}

	/** Handle case where we need to push new recursive context
	 	is this the STAR_LOOP_ENTRY built during left-recur
	 	elimination?  Check left-recur rule and it's
	 	getOperatorLoopBlockStartState(). Also only do it
	 	if we're not skipping loop.
	 */
	protected void handleParseTreeForOperatorLoop(ATNState p, DecisionState d, int alt) {
		Rule curRule = g.getRule(p.ruleIndex);
		boolean isOpLoopEntry =
			curRule.isLeftRecursive() &&
			d.getStateType()==ATNState.STAR_LOOP_ENTRY &&
			d == ((LeftRecursiveRule)curRule).getOperatorLoopBlockEntryState();
		boolean exitBranch = alt == 2; // always 2 alts in op loop star entry decision
		if ( isOpLoopEntry && !exitBranch ) {
			if ( debug ) System.out.println("left recur start of suffix block");
			if ( debug ) System.out.println("not exit branch");
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

	protected ATNState visitRuleStopState(ATNState p) {
		ATNState returnState = g.atn.states.get(_ctx.invokingState);
		if ( debug ) System.out.println("pop from " + g.getRule(p.ruleIndex) + " to " +
											g.getRule(returnState.ruleIndex));
		RuleTransition retStateCallEdge =
			(RuleTransition)returnState.transition(0);
		//
		if ( g.getRule(p.ruleIndex).isLeftRecursive() ) {
			unrollRecursionContexts(locals._parentctx);
		}
		else {
			exitRule();
		}
		p = retStateCallEdge.followState;
		locals = locals.parent; // pop local var context
		return p;
	}

	/** Predicates are evaluated during prediction and not by this interpreter.
	 *  However, the ATN simulator asks the recognizer to evaluate predicates
	 *  and so we override this method to interpret the special, generated
	 *  predicates created during left recursion elimination.
	 *
	 *  e.g., "{3 >= $_p}?";
	 */
	@Override
	public boolean sempred(ATNState state, @Nullable RuleContext localctx, int ruleIndex, int predIndex) {
		if ( state.transition(0) instanceof PrecedencePredicateTransition ) {
			PrecedencePredicateTransition pt = (PrecedencePredicateTransition)state.transition(0);
			if ( debug ) {
				System.out.print("EVAL pred rule:action=" + ruleIndex + ":" + predIndex);
				System.out.println(", prec = " + pt.precedence + ", rule arg is " + locals._prec);
			}
			return pt.precedence >= locals._prec;
		}
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
