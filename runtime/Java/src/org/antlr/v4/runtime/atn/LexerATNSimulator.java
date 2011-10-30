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
	public static boolean debug = false;
	public static boolean dfa_debug = false;
	public static final int NUM_EDGES = 255;

	/** When we hit an accept state in either the DFA or the ATN, we
	 *  have to notify the character stream to start offering characters
	 *  via mark() and record the current state. The current state includes
	 *  the current index into the input, the current line, and current
	 *  character position in that line. Note that the Lexer is tracking
	 *  the starting line and characterization of the token. These
	 *  variables track the state of the simulator when it hits an accept state.
	 *
	 *  We track these variables separately for the DFA and ATN simulation
	 *  because the DFA simulation often has to fail over to the ATN
	 *  simulation. If the ATN simulation fails, we need the DFA to fall
	 *  back to its previously accepted state, if any. If the ATN succeeds,
	 *  then the ATN does the accept and the DFA simulator that invoked it
	 *  can simply return thepredicted token type.
	 */
	protected static class ExecState {
		int marker = -1;
		int index = -1;
		int line = 0;
		int charPos = -1;
	}
	protected static class DFAExecState extends ExecState {
		DFAState state = null;
	}
	protected static class ATNExecState extends ExecState {
		ATNConfig config = null;
	}

	protected Lexer recog;

	/** In case the stream is not offering characters, we need to track
	 * at minimum the text for the current token. This is what
	 * getText() returns.
	 */
	protected char[] text = new char[100];
	protected int textIndex = -1;

	/** The current token's starting index into the character stream.
	 *  Shared across DFA to ATN simulation in case the ATN fails and the
	 *  DFA did not have a previous accept state. In this case, we use the
	 *  ATN-generated exception object.
	 */
	protected int startIndex = -1;

	/** line number 1..n within the input */
	protected int line = 1;

	/** The index of the character relative to the beginning of the line 0..n-1 */
	protected int charPositionInLine = 0;

	protected DFA[] dfa;
	protected int mode = Lexer.DEFAULT_MODE;

	/** Used during DFA/ATN exec to record the most recent accept configuration info */
	protected DFAExecState dfaPrevAccept = new DFAExecState();
	protected ATNExecState atnPrevAccept = new ATNExecState();

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

	// only called from test code from outside
	public int matchATN(CharStream input) {
		textIndex = -1;
		startIndex = input.index();
		ATNState startState = atn.modeToStartState.get(mode);
		if ( debug ) System.out.println("mode "+ mode +" start: "+startState);
		OrderedHashSet<ATNConfig> s0_closure = computeStartState(input, startState);
		int old_mode = mode;
		dfa[mode].s0 = addDFAState(s0_closure);
		int predict = exec(input, s0_closure);
		if ( debug ) System.out.println("DFA after matchATN: "+dfa[old_mode].toLexerString());
		return predict;
	}

	protected int exec(CharStream input, DFAState s0) {
		if ( dfa_debug ) System.out.println("DFA[mode "+(recog==null?0:recog.mode)+"] exec LA(1)=="+
											(char)input.LA(1));
		//System.out.println("DFA start of execDFA: "+dfa[mode].toLexerString());
		textIndex = -1;
		startIndex = input.index();
		resetPrevAccept(dfaPrevAccept);
		dfaPrevAccept.state = null;
		LexerNoViableAltException atnException = null;
		DFAState s = s0;
		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("state "+s.stateNumber+" LA(1)=="+(char)t);
			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || t <= CharStream.EOF ||
				 s.edges[t] == null )
			{
				try {
					int ttype = failOverToATN(input, s);
					return ttype;
				}
				catch (LexerNoViableAltException nvae) {
					atnException = nvae;
					break loop; // dead end; no where to go, fall back on prev
				}
			}

			DFAState target = s.edges[t];
			if ( target == ERROR ) break;
			s = target;

			if ( s.isAcceptState ) {
				if ( dfa_debug ) System.out.println("accept; predict "+s.prediction+
													" in state "+s.stateNumber);
				markAcceptState(dfaPrevAccept, input);
				dfaPrevAccept.state = s;
				// keep going unless we're at EOF; check if something else could match
				// EOF never in DFA
				if ( t==CharStream.EOF ) break;
			}

			consume(input);
			t = input.LA(1);
		}
		if ( dfaPrevAccept.state==null ) {
			// if no accept and EOF is first char, return EOF
			if ( t==CharStream.EOF && input.index()==startIndex ) {
				return Token.EOF;
			}
			if ( atnException!=null ) throw atnException;
			throw new LexerNoViableAltException(recog, input, startIndex, s.configs);
		}

		int ruleIndex = dfaPrevAccept.state.ruleIndex;
		accept(input, ruleIndex, dfaPrevAccept);
		return dfaPrevAccept.state.prediction;
	}

	int failOverToATN(CharStream input, DFAState s) {
		LexerNoViableAltException atnException = null;
		if ( dfa_debug ) System.out.println("no edge for "+(char)input.LA(1));
		if ( dfa_debug ) {
			System.out.println("ATN exec upon "+
							   input.substring(startIndex,input.index())+
							   " at DFA state "+s.stateNumber+" = "+s.configs);
		}
//		try {
			ATN_failover++;
			int ttype = exec(input, s.configs);
			if ( dfa_debug ) {
				System.out.println("back from DFA update, ttype="+ttype+
								   ", dfa[mode "+mode+"]=\n"+
								   dfa[mode].toLexerString());
			}
			// action already executed by ATN
			// we've updated DFA, exec'd action, and have our deepest answer
			return ttype;
//		}
//		catch (LexerNoViableAltException nvae) {
//			// The ATN could not match anything starting from s.configs
//			// so we had an error edge. Re-throw the exception
//			// if there was no previous accept state here in DFA.
//			throw nvae;
//			// dead end; no where to go, fall back on prev
//		}
	}

	protected void markAcceptState(ExecState state, CharStream input) {
		state.marker = input.mark();
		state.index = input.index();
		state.line = line;
		state.charPos = charPositionInLine;
	}

	protected int exec(CharStream input, OrderedHashSet<ATNConfig> s0) {
		//System.out.println("enter exec index "+input.index()+" from "+s0);
		OrderedHashSet<ATNConfig> closure = new OrderedHashSet<ATNConfig>();
		closure.addAll(s0);
		if ( debug ) System.out.println("start state closure="+closure);

		OrderedHashSet<ATNConfig> reach = new OrderedHashSet<ATNConfig>();
		resetPrevAccept(atnPrevAccept);
		atnPrevAccept.config = null;

		int t = input.LA(1);

		do { // while more work
			if ( debug ) System.out.println("in reach starting closure: " + closure);
			for (int ci=0; ci<closure.size(); ci++) { // TODO: foreach
				ATNConfig c = closure.get(ci);
				if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString(recog, true));

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
				// we reached state associated with closure for sure, so
				// make sure it's defined. worst case, we define s0 from
				// start state configs.
				DFAState from = addDFAState(closure);
				// we got nowhere on t, don't throw out this knowledge; it'd
				// cause a failover from DFA later.
				addDFAEdge(from, t, ERROR);
				break;
			}

			// Did we hit a stop state during reach op?
			processAcceptStates(input, reach);

			consume(input);
			if ( t!=CharStream.EOF ) addDFAEdge(closure, t, reach);
			t = input.LA(1);

			// swap to avoid reallocating space
			// TODO: faster to reallocate?
			OrderedHashSet<ATNConfig> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear();
		} while ( true );


		if ( atnPrevAccept.config==null ) {
			// if no accept and EOF is first char, return EOF
			if ( t==CharStream.EOF && input.index()==startIndex ) {
				return Token.EOF;
			}
			throw new LexerNoViableAltException(recog, input, startIndex, reach);
		}

		if ( debug ) {
			System.out.println("ACCEPT " +atnPrevAccept.config.toString(recog, true) +
							   " index " +atnPrevAccept.index);
		}

		int ruleIndex = atnPrevAccept.config.state.ruleIndex;
		accept(input, ruleIndex, atnPrevAccept);
		return atn.ruleToTokenType[ruleIndex];
	}

	protected void processAcceptStates(CharStream input, OrderedHashSet<ATNConfig> reach) {
		for (int ci=0; ci<reach.size(); ci++) {
			ATNConfig c = reach.get(ci);
			if ( c.state instanceof RuleStopState) {
				if ( debug ) {
					System.out.println("in reach we hit accept state "+c+" index "+
									   input.index()+", reach="+reach+
									   ", prevAccept="+atnPrevAccept.config+
									   ", prevIndex="+atnPrevAccept.index);
				}
				int index = input.index();
				if ( index > atnPrevAccept.index ) {
					// will favor prev accept at same index so "int" is keyword not ID
					markAcceptState(atnPrevAccept, input);
					atnPrevAccept.config = c;
					if ( debug ) {
						System.out.println("mark "+c+" @ index="+index+", "+
										  atnPrevAccept.line+":"+atnPrevAccept.charPos);
					}
				}

				// if we reach lexer accept state, toss out any configs in rest
				// of configs work list associated with this rule (config.alt);
				// that rule is done. this is how we cut off nongreedy .+ loops.
				deleteWildcardConfigsForAlt(reach, ci, c.alt); // CAUSES INF LOOP if reach not closure

				 // move to next char, looking for longer match
				// (we continue processing if there are states in reach)
			}
		}
	}

	protected void accept(CharStream input, int ruleIndex, ExecState prevAccept) {
		if ( debug ) {
			if ( recog!=null ) System.out.println("ACTION "+
												  recog.getRuleNames()[ruleIndex]+
												  ":"+ruleIndex);
			else System.out.println("ACTION "+ruleIndex+":"+ruleIndex);
		}
		int actionIndex = atn.ruleToActionIndex[ruleIndex];
		if ( actionIndex>=0 && recog!=null ) recog.action(null, ruleIndex, actionIndex);

		// seek to after last char in token
		input.release(prevAccept.marker);
		input.seek(prevAccept.index);
		line = prevAccept.line;
		charPositionInLine = prevAccept.charPos;
		consume(input);
	}

	public ATNState getReachableTarget(Transition trans, int t) {
		if ( trans instanceof AtomTransition ) {
			AtomTransition at = (AtomTransition)trans;
			if ( at.label == t ) {
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
			if ( (!not && st.set.contains(t)) ||
				 (not && !st.set.contains(t) && t!=Token.EOF) ) // ~set doesn't not match EOF
			{
				if ( debug ) System.out.println("match "+(not?"~":"")+"set "+st.set.toString(true));
				return st.target;
			}
		}
		else if ( trans instanceof WildcardTransition && t!=Token.EOF ) {
			return trans.target;
		}
		return null;
	}

	public void deleteWildcardConfigsForAlt(OrderedHashSet<ATNConfig> closure, int ci, int alt) {
		int j=ci+1;
		while ( j<closure.size() ) {
			ATNConfig c = closure.get(j);
			boolean isWildcard = c.state.getClass() == ATNState.class &&
				c.state.transition(0).getClass() == WildcardTransition.class;
			if ( c.alt == alt && isWildcard ) {
				if ( debug ) System.out.println("deleteWildcardConfigsForAlt "+c);
				closure.remove(j);
			}
			else j++;
		}
	}

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
			if ( debug ) {
				if ( recog!=null ) System.out.println("closure at "+
													  recog.getRuleNames()[config.state.ruleIndex]+
													  " rule stop "+config);
				else System.out.println("closure at rule stop "+config);
			}
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
			ATNConfig c = getEpsilonTarget(config, t);
			if ( c!=null ) closure(c, configs);
		}
	}

	public ATNConfig getEpsilonTarget(ATNConfig config, Transition t) {
		ATNState p = config.state;
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
		else if ( t.isEpsilon() ) {
			c = new ATNConfig(config, t.target);
		}
		return c;
	}

	protected void resetPrevAccept(ExecState prevAccept) {
		prevAccept.marker = -1;
		prevAccept.index = -1;
		prevAccept.line = 0;
		prevAccept.charPos = -1;
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
		if ( p==null || t==CharStream.EOF ) return; // Don't track EOF edges from stop states
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

	/** Get the text of the current token */
	public String getText() {
		if ( textIndex<0 ) return "";
		return new String(text, 0, textIndex+1);
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public void consume(CharStream input) {
		int curChar = input.LA(1);
		if ( curChar!=CharStream.EOF ) {
			if ( (textIndex+1)>=text.length ) {
				char[] txt = new char[text.length*2];
				System.arraycopy(text, 0, txt, 0, text.length);
				text = txt;
			}
			text[++textIndex] = (char)curChar;
		}
		charPositionInLine++;
		if ( curChar=='\n' ) {
			line++;
			charPositionInLine=0;
		}
		input.consume();
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		//if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
		return "'"+(char)t+"'";
	}

}
