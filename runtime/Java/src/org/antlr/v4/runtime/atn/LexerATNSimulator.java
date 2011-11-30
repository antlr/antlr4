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
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.io.IOException;
import java.io.OutputStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;

/** "dup" of ParserInterpreter */
public class LexerATNSimulator extends ATNSimulator {
	public static boolean debug = false;
	public static boolean dfa_debug = false;
	public static final int NUM_EDGES = 255;

	private boolean trace = false;
	private OutputStream traceStream = null;
	private boolean traceFailed = false;

	/** When we hit an accept state in either the DFA or the ATN, we
	 *  have to notify the character stream to start buffering characters
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
	 *  can simply return the predicted token type.
	 */
	protected static class ExecState {
		int index = -1;
		int line = 0;
		int charPos = -1;

		void reset() {
			index = -1;
			line = 0;
			charPos = -1;
		}
	}

	protected static class DFAExecState extends ExecState {
		DFAState state;

		@Override
		void reset() {
			super.reset();
			state = null;
		}
	}

	protected static class ATNExecState extends ExecState {
		ATNConfig config;

		@Override
		void reset() {
			super.reset();
			config = null;
		}
	}

	@Nullable
	protected final Lexer recog;

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

	@NotNull
	protected DFA[] dfa;
	protected int mode = Lexer.DEFAULT_MODE;

	/** Used during DFA/ATN exec to record the most recent accept configuration info */
	@NotNull
	protected final DFAExecState dfaPrevAccept = new DFAExecState();
	@NotNull
	protected final ATNExecState atnPrevAccept = new ATNExecState();

	public static int ATN_failover = 0;
	public static int match_calls = 0;

	public LexerATNSimulator(@NotNull ATN atn) {
		this(null, atn);
	}

	public LexerATNSimulator(@Nullable Lexer recog, @NotNull ATN atn) {
		super(atn);
		dfa = new DFA[atn.modeToStartState.size()];
		for (int i=0; i<atn.modeToStartState.size(); i++) {
			dfa[i] = new DFA(atn.modeToStartState.get(i));
		}
		this.recog = recog;
	}

	public void setTraceStream(OutputStream traceStream) {
		this.traceStream = traceStream;
		this.trace = traceStream != null;
		this.traceFailed = false;
	}

	public int match(@NotNull CharStream input, int mode) {
		match_calls++;
		this.mode = mode;
		int mark = input.mark();
		traceBeginMatch(input, mode);
		try {
			if ( dfa[mode].s0==null ) {
				return matchATN(input);
			}
			else {
				return exec(input, dfa[mode].s0);
			}
		}
        finally {
			traceEndMatch();
			input.release(mark);
		}
	}

	public void reset() {
		dfaPrevAccept.reset();
		atnPrevAccept.reset();
		startIndex = -1;
		line = 1;
		charPositionInLine = 0;
		mode = Lexer.DEFAULT_MODE;
	}

	// only called from test code from outside
	public int matchATN(@NotNull CharStream input) {
		traceMatchATN();
		startIndex = input.index();
		ATNState startState = atn.modeToStartState.get(mode);
		if ( debug ) System.out.println("mode "+ mode +" start: "+startState);
		OrderedHashSet<ATNConfig> s0_closure = computeStartState(input, startState);
		int old_mode = mode;
		dfa[mode].s0 = addDFAState(s0_closure);
		int predict = exec(input, s0_closure);
		if ( debug ) System.out.println("DFA after matchATN: "+dfa[old_mode].toLexerString());
		tracePredict(predict);
		return predict;
	}

	protected int exec(@NotNull CharStream input, @NotNull DFAState s0) {
		traceMatchDFA();
		if ( dfa_debug ) System.out.println("DFA[mode "+(recog==null?0:recog.mode)+"] exec LA(1)=="+
											(char)input.LA(1));
		//System.out.println("DFA start of execDFA: "+dfa[mode].toLexerString());
		startIndex = input.index();
		dfaPrevAccept.reset();
		LexerNoViableAltException atnException = null;
		DFAState s = s0;
		traceLookahead1();
		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("state "+s.stateNumber+" LA(1)=="+(char)t);
			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || t <= CharStream.EOF ||
				 s.edges[t] == null )
			{
				try {
					ATN_failover++;
					return failOverToATN(input, s);
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
			traceLookahead1();
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
		tracePredict(dfaPrevAccept.state.prediction);
		return dfaPrevAccept.state.prediction;
	}

	protected int exec(@NotNull CharStream input, @NotNull OrderedHashSet<ATNConfig> s0) {
		//System.out.println("enter exec index "+input.index()+" from "+s0);
		@NotNull
		OrderedHashSet<ATNConfig> closure = new OrderedHashSet<ATNConfig>();
		closure.addAll(s0);
		if ( debug ) System.out.println("start state closure="+closure);

		@NotNull
		OrderedHashSet<ATNConfig> reach = new OrderedHashSet<ATNConfig>();
		atnPrevAccept.reset();

		traceLookahead1();
		int t = input.LA(1);

		while ( true ) { // while more work
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
			addDFAEdge(closure, t, reach);
			traceLookahead1();
			t = input.LA(1);

			// swap to avoid reallocating space
			// TODO: faster to reallocate?
			@NotNull
			OrderedHashSet<ATNConfig> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear();
		}


		if ( atnPrevAccept.config==null ) {
			// if no accept and EOF is first char, return EOF
			if ( t==CharStream.EOF && input.index()==startIndex ) {
				return Token.EOF;
			}
			throw new LexerNoViableAltException(recog, input, startIndex, reach);
		}

		int ruleIndex = atnPrevAccept.config.state.ruleIndex;
		accept(input, ruleIndex, atnPrevAccept);
		return atn.ruleToTokenType[ruleIndex];
	}

	protected void processAcceptStates(@NotNull CharStream input, @NotNull OrderedHashSet<ATNConfig> reach) {
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
					traceAcceptState(c.alt);
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

	protected void accept(@NotNull CharStream input, int ruleIndex, @NotNull ExecState prevAccept) {
		if ( debug ) {
			if ( recog!=null ) System.out.println("ACTION "+
												  recog.getRuleNames()[ruleIndex]+
												  ":"+ruleIndex);
			else System.out.println("ACTION "+ruleIndex+":"+ruleIndex);
		}
		int actionIndex = atn.ruleToActionIndex[ruleIndex];
		if ( actionIndex>=0 && recog!=null ) recog.action(null, ruleIndex, actionIndex);

		// seek to after last char in token
		traceSeek(prevAccept.index);
		input.seek(prevAccept.index);
		line = prevAccept.line;
		charPositionInLine = prevAccept.charPos;
		consume(input);
	}

	@Nullable
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
				 (not && !st.set.contains(t) && t!=CharStream.EOF) ) // ~set doesn't not match EOF
			{
				if ( debug ) System.out.println("match "+(not?"~":"")+"set "+st.set.toString(true));
				return st.target;
			}
		}
		else if ( trans instanceof WildcardTransition && t!=CharStream.EOF ) {
			return trans.target;
		}
		return null;
	}

	public void deleteWildcardConfigsForAlt(@NotNull OrderedHashSet<ATNConfig> closure, int ci, int alt) {
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

	@NotNull
	protected OrderedHashSet<ATNConfig> computeStartState(@NotNull IntStream input,
														  @NotNull ATNState p)
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

	protected void closure(@NotNull ATNConfig config, @NotNull OrderedHashSet<ATNConfig> configs) {
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

	@Nullable
	public ATNConfig getEpsilonTarget(@NotNull ATNConfig config, @NotNull Transition t) {
		ATNState p = config.state;
		ATNConfig c = null;
		if ( t.getClass() == RuleTransition.class ) {
			RuleContext newContext =
				new RuleContext(config.context, p.stateNumber, t.target.stateNumber);
			c = new ATNConfig(config, t.target, newContext);
		}
		else if ( t.getClass() == PredicateTransition.class ) {
			if (recog == null) {
				System.out.printf("Predicates cannot be evaluated without a recognizer; assuming true.\n");
			}

			PredicateTransition pt = (PredicateTransition)t;
			if ( recog == null || recog.sempred(null, pt.ruleIndex, pt.predIndex) ) {
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

	int failOverToATN(@NotNull CharStream input, @NotNull DFAState s) {
		traceFailOverToATN();
		if ( dfa_debug ) System.out.println("no edge for "+(char)input.LA(1));
		if ( dfa_debug ) {
			System.out.println("ATN exec upon "+
							   input.substring(startIndex,input.index())+
							   " at DFA state "+s.stateNumber+" = "+s.configs);
		}
		int ttype = exec(input, s.configs);
		if ( dfa_debug ) {
			System.out.println("back from DFA update, ttype="+ttype+
							   ", dfa[mode "+mode+"]=\n"+
							   dfa[mode].toLexerString());
		}
		// action already executed by ATN
		// we've updated DFA, exec'd action, and have our deepest answer
		tracePredict(ttype);
		return ttype;
	}

	protected void markAcceptState(@NotNull ExecState state, @NotNull CharStream input) {
		state.index = input.index();
		state.line = line;
		state.charPos = charPositionInLine;
	}

	protected void addDFAEdge(@NotNull OrderedHashSet<ATNConfig> p,
							  int t,
							  @NotNull OrderedHashSet<ATNConfig> q)
	{
//		System.out.println("MOVE "+p+" -> "+q+" upon "+getTokenName(t));
		DFAState from = addDFAState(p);
		DFAState to = addDFAState(q);
		addDFAEdge(from, t, to);
	}

	// TODO (sam): should we allow q==null?
	protected void addDFAEdge(@Nullable DFAState p, int t, DFAState q) {
		if ( p==null || t==CharStream.EOF || q==null ) return; // Don't track EOF edges from stop states
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
	@Nullable
	protected DFAState addDFAState(@NotNull OrderedHashSet<ATNConfig> configs) {
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

	@Nullable
	public DFA getDFA(int mode) {
		return dfa[mode];
	}

	/** Get the text of the current token */
	@NotNull
	public String getText(@NotNull CharStream input) {
		return input.substring(this.startIndex, input.index());
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public void consume(@NotNull CharStream input) {
		int curChar = input.LA(1);
		if ( curChar=='\n' ) {
			line++;
			charPositionInLine=0;
		} else {
			charPositionInLine++;
		}
		input.consume();
		traceConsume(input, curChar);
	}

	@NotNull
	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		//if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
		return "'"+(char)t+"'";
	}

	/*
	 * Trace helpers (API and file format are work in progress)
	 */

	public void traceEndMatch() {
		if (trace) {
			traceSlow(LexerOpCode.EndMatch);
		}
	}

	public void traceMatchATN() {
		if (trace) {
			traceSlow(LexerOpCode.MatchATN);
		}
	}

	public void traceMatchDFA() {
		if (trace) {
			traceSlow(LexerOpCode.MatchDFA);
		}
	}

	public void traceLookahead1() {
		if (trace) {
			traceSlow(LexerOpCode.Lookahead1);
		}
	}

	public void traceFailOverToATN() {
		if (trace) {
			traceSlow(LexerOpCode.FailOverToATN);
		}
	}

	public void tracePredict(int prediction) {
		if (trace) {
			traceIntSlow(LexerOpCode.Predict, prediction);
		}
	}

	public void traceAcceptState(int prediction) {
		if (trace) {
			traceIntSlow(LexerOpCode.AcceptState, prediction);
		}
	}

	public void traceSeek(int index) {
		if (trace) {
			traceIntSlow(LexerOpCode.Seek, index);
		}
	}

	public final void traceBeginMatch(CharStream input, int mode) {
		if (trace) {
			traceBeginMatchSlow(input, mode);
		}
	}

	public final void traceConsume(CharStream input, int c) {
		if (trace) {
			traceConsumeSlow(input, c);
		}
	}

	public final void tracePushMode(int mode) {
		if (trace) {
			traceByteSlow(LexerOpCode.PushMode, (byte)mode);
		}
	}

	public final void tracePopMode() {
		if (trace) {
			traceSlow(LexerOpCode.PopMode);
		}
	}

	public final void traceEmit(Token token) {
		if (trace) {
			traceEmitSlow(token);
		}
	}

	private void traceSlow(LexerOpCode opcode) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 0;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceByteSlow(LexerOpCode opcode, byte arg) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 1;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
				traceStream.write(arg);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceByteIntSlow(LexerOpCode opcode, byte arg1, int arg2) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 5;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
				traceStream.write(arg1);
				traceIntSlow(arg2);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceIntSlow(LexerOpCode opcode, int arg) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 4;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
				traceIntSlow(arg);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceIntIntSlow(LexerOpCode opcode, int arg1, int arg2) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 8;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
				traceIntSlow(arg1);
				traceIntSlow(arg2);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceIntIntIntIntSlow(LexerOpCode opcode, int arg1, int arg2, int arg3, int arg4) {
		assert traceStream != null;
		assert opcode.getArgumentSize() == 16;

		if (!traceFailed) {
			try {
				traceStream.write(opcode.ordinal());
				traceIntSlow(arg1);
				traceIntSlow(arg2);
				traceIntSlow(arg3);
				traceIntSlow(arg4);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceIntSlow(int arg) {
		assert traceStream != null;

		if (!traceFailed) {
			try {
				traceStream.write(arg);
				traceStream.write(arg >> 8);
				traceStream.write(arg >> 16);
				traceStream.write(arg >> 24);
			} catch (IOException e) {
				e.printStackTrace();
				traceFailed = true;
			}
		}
	}

	private void traceBeginMatchSlow(CharStream input, int mode) {
		traceByteIntSlow(LexerOpCode.BeginMatch, (byte)mode, input.index());
	}

	private void traceConsumeSlow(CharStream input, int c) {
		assert traceStream != null;

		if (!traceFailed) {
			traceIntIntSlow(LexerOpCode.Consume, c, input.index());
		}
	}

	private void traceEmitSlow(Token token) {
		assert traceStream != null;

		if (token != null && !traceFailed) {
			traceIntIntIntIntSlow(LexerOpCode.Emit, token.getStartIndex(), token.getStopIndex(), token.getType(), token.getChannel());
		}
	}

	public enum LexerOpCode {
		BeginMatch(5),
		EndMatch(0),
		MatchATN(0),
		MatchDFA(0),
		FailOverToATN(0),
		AcceptState(4),
		Predict(4),

		Seek(4),
		Consume(8),
		Lookahead1(0),

		PushMode(1),
		PopMode(0),
		Emit(16);

		private final int argumentSize;

		private LexerOpCode(int argumentSize) {
			this.argumentSize = argumentSize;
		}

		public int getArgumentSize() {
			return argumentSize;
		}
	}
}
