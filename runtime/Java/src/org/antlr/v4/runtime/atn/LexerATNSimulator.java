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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.dfa.*;
import org.antlr.v4.runtime.misc.OrderedHashSet;

/** "dup" of ParserInterpreter */
public class LexerATNSimulator extends ATNSimulator {
	public static boolean debug = true;
	public static boolean dfa_debug = true;
	public static final int NUM_EDGES = 255;

	protected Lexer recog;

	protected DFA[] dfa;
	protected int mode = Lexer.DEFAULT_MODE;

	public static int ATN_failover = 0;
	public static int match_calls = 0;

	public LexerATNSimulator(ATN atn) {
		this(null, atn);
	}

	public LexerATNSimulator(Lexer recog, ATN atn) {
		super(atn);
		dfa = new DFA[atn.modeToStartState.size()];
		for (int i=0; i<atn.modeToStartState.size(); i++) {
			dfa[i] = new DFA(atn.modeToStartState.get(i));
		}
		this.recog = recog;
	}

	public int match(CharStream input, int mode) {
		match_calls++;
		this.mode = mode;
		if ( dfa[mode].s0==null ) {
			return matchATN(input);
		}
		else {
			return exec(input, dfa[mode].s0);
		}
	}

	public int matchATN(CharStream input) {
		ATNState startState = atn.modeToStartState.get(mode);
		if ( debug ) System.out.println("mode "+ mode +" start: "+startState);
		OrderedHashSet<ATNConfig> s0_closure = computeStartState(input, startState);
		int old_mode = mode;
		dfa[mode].s0 = addDFAState(s0_closure);
		int predict = exec(input, s0_closure);
		if ( debug ) System.out.println("DFA after matchATN: "+dfa[old_mode].toLexerString());
		return predict;
	}

	public int exec(CharStream input, DFAState s0) {
		if ( dfa_debug ) System.out.println("DFA[mode "+(recog==null?0:recog.mode)+"] exec LA(1)=="+
											(char)input.LA(1));
		//System.out.println("DFA start of execDFA: "+dfa[mode].toLexerString());
		int prevAcceptMarker = -1;
		DFAState prevAcceptState = null;
		DFAState s = s0;
		int startIndex = input.index();
		int t = input.LA(1);
		if ( t==CharStream.EOF ) return -1; // TODO: how to match EOF in lexer rule?
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("state "+s.stateNumber+" LA(1)=="+(char)t);
			if ( s.isAcceptState ) {
				if ( dfa_debug ) System.out.println("accept; predict "+s.prediction+
													" in state "+s.stateNumber);
				prevAcceptState = s;
				prevAcceptMarker = input.index();
				// keep going unless we're at EOF; check if something else could match
				if ( t==CharStream.EOF ) break;
			}
			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || s.edges[t] == null ) {
				if ( dfa_debug ) System.out.println("no edge for "+(char)t);
				int ttype = -1;
				try {
					if ( dfa_debug ) {
					System.out.println("ATN exec upon "+
									   input.substring(startIndex,input.index())+
									   " at DFA state "+s.stateNumber+" = "+s.configs);
					}
					ATN_failover++;
					ttype = exec(input, s.configs);
				}
				catch (LexerNoViableAltException nvae) {
					addDFAEdge(s, t, ERROR);
				}
				if ( dfa_debug ) {
					System.out.println("back from DFA update, ttype="+ttype+
									   ", dfa[mode "+mode+"]=\n"+dfa[mode].toLexerString());
				}

				if ( ttype==-1 ) {
					addDFAEdge(s, t, ERROR);
					break loop; // dead end; no where to go, fall back on prev if any
				}
				// action already executed
				return ttype; // we've updated DFA, exec'd action, and have our deepest answer
			}
			DFAState target = s.edges[t];
			if ( target == ERROR ) break;
			s = target;
			input.consume();
			t = input.LA(1);
		}
		if ( prevAcceptState==null ) {
			System.out.println("!!! no viable alt in dfa");
			return -1;
		}
		if ( recog!=null ) {
			int actionIndex = atn.ruleToActionIndex[prevAcceptState.ruleIndex];
			if ( dfa_debug ) {
				System.out.println("ACTION "+
								   recog.getRuleNames()[prevAcceptState.ruleIndex]+
								   ":"+ actionIndex);
			}
			if ( actionIndex>=0 ) recog.action(null, prevAcceptState.ruleIndex, actionIndex);
		}
		input.seek(prevAcceptMarker);
		return prevAcceptState.prediction;
	}

	public int exec(CharStream input, OrderedHashSet<ATNConfig> s0) {
		//System.out.println("enter exec index "+input.index()+" from "+s0);
		OrderedHashSet<ATNConfig> closure = new OrderedHashSet<ATNConfig>();
		closure.addAll(s0);
		if ( debug ) System.out.println("start state closure="+closure);

		OrderedHashSet<ATNConfig> reach = new OrderedHashSet<ATNConfig>();
		ATNConfig prevAccept = null;
		int prevAcceptIndex = -1;

		int t = input.LA(1);
		if ( t==Token.EOF ) return Token.EOF;

		do { // while more work
			if ( debug ) System.out.println("in reach starting closure: " + closure);
			for (int ci=0; ci<closure.size(); ci++) { // TODO: foreach
				ATNConfig c = closure.get(ci);
				if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString(recog, true));

				if ( c.state instanceof RuleStopState ) {
					if ( debug ) {
						System.out.println("in reach we hit accept state "+c+" index "+
										   input.index()+", reach="+reach+
										   ", prevAccept="+prevAccept+", prevIndex="+prevAcceptIndex);
					}
					if ( input.index() > prevAcceptIndex ) {
						// will favor prev accept at same index so "int" is keyword not ID
						prevAccept = c;
						prevAcceptIndex = input.index();
					}
 					// move to next char, looking for longer match
					// (we continue processing if there are states in reach)
				}

				int n = c.state.getNumberOfTransitions();
				for (int ti=0; ti<n; ti++) {               // for each transition
					Transition trans = c.state.transition(ti);
					ATNState target = getReachableTarget(trans, t);
					if ( target!=null ) {
						closure(new ATNConfig(c, target), reach);
					}
				}
			}

			if ( reach.size()==0 ) {
				// we reached closure state for sure, make sure it's defined.
				// worst case, we define s0 from start state configs.
				DFAState from = addDFAState(closure);
				// we got nowhere on t, don't throw out this knowledge; it'd
				// cause a failover from DFA later.
				if ( t!=Token.EOF ) addDFAEdge(from, t, ERROR);
				break;
			}

			input.consume();
			addDFAEdge(closure, t, reach);
			t = input.LA(1);

			// swap to avoid reallocating space
			OrderedHashSet<ATNConfig> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear();
		} while ( true );

		if ( prevAccept==null ) {
			if ( t==Token.EOF ) {
				System.out.println("EOF in token at input index "+input.index());
				return Token.EOF;
			}
//					System.out.println("no viable token at input "+getTokenName(input.LA(1))+", index "+input.index());
			throw new LexerNoViableAltException(recog, input, closure); // TODO: closure is empty
		}

		if ( debug ) System.out.println("ACCEPT " + prevAccept.toString(recog, true) + " index " + prevAcceptIndex);

		int ruleIndex = prevAccept.state.ruleIndex;
		int ttype = atn.ruleToTokenType[ruleIndex];
		if ( debug ) {
			if ( recog!=null ) System.out.println("ACTION "+recog.getRuleNames()[ruleIndex]+":"+ruleIndex);
			else System.out.println("ACTION "+ruleIndex+":"+ruleIndex);
		}
		int actionIndex = atn.ruleToActionIndex[ruleIndex];
		if ( actionIndex>=0 ) recog.action(null, ruleIndex, actionIndex);
		return ttype;
	}

	public ATNState getReachableTarget(Transition trans, int t) {
		if ( trans instanceof AtomTransition ) {
			AtomTransition at = (AtomTransition)trans;
			boolean not = trans instanceof NotAtomTransition;
			if ( !not && at.label == t || not && at.label!=t ) {
				if ( debug ) {
					System.out.println("match "+getTokenName(at.label));
				}
				return at.target;
			}
		}
		else if ( trans.getClass() == RangeTransition.class ) {
			RangeTransition rt = (RangeTransition)trans;
			if ( t>=rt.from && t<=rt.to ) {
				if ( debug ) System.out.println("match range "+rt.toString());
				return rt.target;
			}
		}
		else if ( trans instanceof SetTransition ) {
			SetTransition st = (SetTransition)trans;
			boolean not = trans instanceof NotSetTransition;
			if ( !not && st.set.member(t) || not && !st.set.member(t) ) {
//				if ( st.set.toString().equals("0") ) {
//					System.out.println("eh?");
//				}
				if ( debug ) System.out.println("match set "+st.set.toString());
				return st.target;
			}
		}
		else if ( trans instanceof WildcardTransition && t!=Token.EOF ) {
			return trans.target;
		}
		return null;
	}

	/* TODO: use if we need nongreedy
	public void deleteConfigsForAlt(OrderedHashSet<ATNConfig> closure, int ci, int alt) {
		int j=ci+1;
		while ( j<closure.size() ) {
			ATNConfig c = closure.get(j);
			if ( c.alt == alt ) {
				System.out.println("kill "+c);
				closure.remove(j);
			}
			else j++;
		}
	}
	*/

	protected OrderedHashSet<ATNConfig> computeStartState(IntStream input,
														  ATNState p)
	{
		RuleContext initialContext = RuleContext.EMPTY;
		OrderedHashSet<ATNConfig> configs = new OrderedHashSet<ATNConfig>();
		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			ATNState target = p.transition(i).target;
			ATNConfig c = new ATNConfig(target, i+1, initialContext);
			closure(c, configs);
		}
		return configs;
	}

	protected void closure(ATNConfig config, OrderedHashSet<ATNConfig> configs) {
		if ( debug ) {
			System.out.println("closure("+config.toString(recog, true)+")");
		}

		// TODO? if ( closure.contains(t) ) return;

		if ( config.state instanceof RuleStopState ) {
			if ( debug ) System.out.println("closure at rule stop "+config);
			if ( config.context == null || config.context.isEmpty() ) {
				configs.add(config);
				return;
			}
			RuleContext newContext = config.context.parent; // "pop" invoking state
			ATNState invokingState = atn.states.get(config.context.invokingState);
			RuleTransition rt = (RuleTransition)invokingState.transition(0);
			ATNState retState = rt.followState;
			ATNConfig c = new ATNConfig(retState, config.alt, newContext);
			closure(c, configs);
			return;
		}

		// optimization
		if ( !config.state.onlyHasEpsilonTransitions() )	{
			configs.add(config);
		}

		ATNState p = config.state;
		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			Transition t = p.transition(i);
			ATNConfig c = null;
			if ( t.getClass() == RuleTransition.class ) {
				RuleContext newContext =
					new RuleContext(config.context, p.stateNumber, t.target.stateNumber);
				c = new ATNConfig(config, t.target, newContext);
			}
			else if ( t.getClass() == PredicateTransition.class ) {
				PredicateTransition pt = (PredicateTransition)t;
				if ( recog.sempred(null, pt.ruleIndex, pt.predIndex) ) {
					c = new ATNConfig(config, t.target);
					c.traversedPredicate = true;
				}
			}
			// ignore actions; just exec one per rule upon accept
			else if ( t.getClass() == ActionTransition.class ) {
				c = new ATNConfig(config, t.target);
			}
			// TODO: forced actions?
			else if ( t.isEpsilon() ) {
				c = new ATNConfig(config, t.target);
			}
			if ( c!=null ) closure(c, configs);
		}
	}

	protected void addDFAEdge(OrderedHashSet<ATNConfig> p,
							  int t,
							  OrderedHashSet<ATNConfig> q)
	{
//		System.out.println("MOVE "+p+" -> "+q+" upon "+getTokenName(t));
		DFAState from = addDFAState(p);
		DFAState to = addDFAState(q);
		addDFAEdge(from, t, to);
	}

	protected void addDFAEdge(DFAState p, int t, DFAState q) {
		if ( p==null ) return;
		if ( p.edges==null ) {
			//  make room for tokens 1..n and -1 masquerading as index 0
			p.edges = new DFAState[NUM_EDGES+1]; // TODO: make adaptive
		}
//		if ( t==Token.EOF ) {
//			System.out.println("state "+p+" has EOF edge");
//			t = 0;
//		}
		p.edges[t] = q; // connect
	}

	/** Add a new DFA state if there isn't one with this set of
		configurations already. This method also detects the first
		configuration containing an ATN rule stop state. Later, when
		traversing the DFA, we will know which rule to accept. Also, we
		detect if any of the configurations derived from traversing a
		semantic predicate. If so, we cannot add a DFA state for this
		because the DFA would not test the predicate again in the
		future. Rather than creating collections of semantic predicates
		like v3 and testing them on prediction, v4 will test them on the
		fly all the time using the ATN not the DFA. This is slower but
		semantically it's not use that often. One of the key elements to
		this predicate mechanism is not adding DFA states that see
		predicates immediately afterwards in the ATN. For example,

		a : ID {p1}? | ID {p2}? ;

		should create the start state for rule 'a' (to save start state
		competition), but should not create target of ID state. The
		collection of ATN states the following ID references includes
		states reached by traversing predicates. Since this is when we
		test them, we cannot cash the DFA state target of ID.
	 */
	protected DFAState addDFAState(OrderedHashSet<ATNConfig> configs) {
		DFAState proposed = new DFAState(configs);
		DFAState existing = dfa[mode].states.get(proposed);
		if ( existing!=null ) return existing;

		DFAState newState = proposed;

		ATNConfig firstConfigWithRuleStopState = null;
		boolean traversedPredicate = false;
		for (ATNConfig c : configs) {
			if ( firstConfigWithRuleStopState==null &&
				 c.state instanceof RuleStopState )
			{
				firstConfigWithRuleStopState = c;
			}
			if ( c.traversedPredicate ) traversedPredicate = true;
		}

		if ( firstConfigWithRuleStopState!=null ) {
			newState.isAcceptState = true;
			newState.ruleIndex = firstConfigWithRuleStopState.state.ruleIndex;
			newState.prediction = atn.ruleToTokenType[newState.ruleIndex];
		}

		if ( traversedPredicate ) return null; // cannot cache

		newState.stateNumber = dfa[mode].states.size();
		newState.configs = new OrderedHashSet<ATNConfig>();
		newState.configs.addAll(configs);
		dfa[mode].states.put(newState, newState);
		return newState;
	}

	public DFA getDFA(int mode) {
		return dfa[mode];
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		//if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
		return "'"+(char)t+"'";
	}

}
