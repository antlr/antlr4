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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/** "dup" of ParserInterpreter */
public class LexerATNSimulator extends ATNSimulator {
	public static boolean debug = false;
	public static boolean dfa_debug = false;
	public static final int MAX_DFA_EDGE = 127; // forces unicode to stay in ATN

	private boolean trace = false;
	private OutputStream traceStream = null;
	private boolean traceFailed = false;

	/** When we hit an accept state in either the DFA or the ATN, we
	 *  have to notify the character stream to start buffering characters
	 *  via mark() and record the current state. The current sim state
	 *  includes the current index into the input, the current line,
	 *  and current character position in that line. Note that the Lexer is
	 *  tracking the starting line and characterization of the token. These
	 *  variables track the "state" of the simulator when it hits an accept state.
	 *
	 *  We track these variables separately for the DFA and ATN simulation
	 *  because the DFA simulation often has to fail over to the ATN
	 *  simulation. If the ATN simulation fails, we need the DFA to fall
	 *  back to its previously accepted state, if any. If the ATN succeeds,
	 *  then the ATN does the accept and the DFA simulator that invoked it
	 *  can simply return the predicted token type.
	 */
	protected static class SimState {
		protected int index = -1;
		protected int line = 0;
		protected int charPos = -1;
		protected DFAState dfaState;
		protected LexerATNConfig config;

		protected void reset() {
			index = -1;
			line = 0;
			charPos = -1;
			dfaState = null;
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
	public final DFA[] decisionToDFA;
	protected int mode = Lexer.DEFAULT_MODE;

	/** Used during DFA/ATN exec to record the most recent accept configuration info */
	@NotNull
	protected final SimState prevAccept = new SimState();

	public static int ATN_failover = 0;
	public static int match_calls = 0;

	public LexerATNSimulator(@NotNull ATN atn, @NotNull DFA[] decisionToDFA,
							 @NotNull PredictionContextCache sharedContextCache)
	{
		this(null, atn, decisionToDFA,sharedContextCache);
	}

	public LexerATNSimulator(@Nullable Lexer recog, @NotNull ATN atn,
							 @NotNull DFA[] decisionToDFA,
							 @NotNull PredictionContextCache sharedContextCache)
	{
		super(atn,sharedContextCache);
		this.decisionToDFA = decisionToDFA;
		if ( decisionToDFA[Lexer.DEFAULT_MODE]==null ) { // create all mode dfa
			synchronized (this.decisionToDFA) {
				if ( decisionToDFA[Lexer.DEFAULT_MODE]==null ) { // create all mode dfa
					for (int i=0; i<atn.modeToStartState.size(); i++) {
						this.decisionToDFA[i] = new DFA(atn.modeToStartState.get(i));
					}
				}
			}
		}
		this.recog = recog;
	}

	public void copyState(@NotNull LexerATNSimulator simulator) {
		this.charPositionInLine = simulator.charPositionInLine;
		this.line = simulator.line;
		this.mode = simulator.mode;
		this.startIndex = simulator.startIndex;

		this.trace = simulator.trace;
		this.traceStream = simulator.traceStream;
		this.traceFailed = simulator.traceFailed;
	}

	public OutputStream getTraceStream() {
		return this.traceStream;
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
			DFA dfa = decisionToDFA[mode];
			if ( dfa.s0==null ) {
				return matchATN(input);
			}
			else {
				return execDFA(input, dfa.s0);
			}
		}
		finally {
			traceEndMatch();
			input.release(mark);
		}
	}

	@Override
	public void reset() {
		prevAccept.reset();
		startIndex = -1;
		line = 1;
		charPositionInLine = 0;
		mode = Lexer.DEFAULT_MODE;
	}

	// only called from test code from outside
	public int matchATN(@NotNull CharStream input) {
		traceMatchATN();
		startIndex = input.index();
		this.prevAccept.reset();
		ATNState startState = atn.modeToStartState.get(mode);

		if ( debug ) {
			System.out.format("matchATN mode %d start: %s\n", mode, startState);
		}

		ATNConfigSet s0_closure = computeStartState(input, startState);
		int old_mode = mode;
		decisionToDFA[mode].s0 = addDFAState(s0_closure);
		int predict = execATN(input, s0_closure, decisionToDFA[mode].s0);

		if ( debug ) {
			System.out.format("DFA after matchATN: %s\n", decisionToDFA[old_mode].toLexerString());
		}

		tracePredict(predict);
		return predict;
	}

	protected int execDFA(@NotNull CharStream input, @NotNull DFAState s0) {
		traceMatchDFA();

		if ( dfa_debug ) {
			System.out.format("DFA[mode %d] exec LA(1)==%s\n", recog == null ? 0 : recog._mode, getTokenName(input.LA(1)));
		}

		//System.out.println("DFA start of execDFA: "+dfa[mode].toLexerString());
		startIndex = input.index();
		this.prevAccept.reset();
		DFAState s = s0;
		traceLookahead1();
		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) {
				System.out.format("state %d LA(1)==%s\n", s.stateNumber, getTokenName(t));
			}

			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || t <= IntStream.EOF ||
				 s.edges[t] == null )
			{
				ATN_failover++;
				return failOverToATN(input, s);
			}

			DFAState target = s.edges[t];
			if ( target == ERROR ) break;
			s = target;

			if ( s.isAcceptState ) {
				if ( dfa_debug ) {
					System.out.format("accept; predict %d in state %d\n", s.prediction, s.stateNumber);
				}

				captureSimState(prevAccept, input, s);
				// keep going unless we're at EOF; check if something else could match
				// EOF never in DFA
				if ( t==IntStream.EOF ) break;
			}

			consume(input);
			traceLookahead1();
			t = input.LA(1);
		}

		ATNConfigSet reach = prevAccept.dfaState != null ? prevAccept.dfaState.configs : null;
		return failOrAccept(prevAccept, input, reach, t);
	}

	protected int execATN(@NotNull CharStream input, @NotNull ATNConfigSet s0, @Nullable DFAState ds0) {
		//System.out.println("enter exec index "+input.index()+" from "+s0);
		@NotNull
		ATNConfigSet closure = s0;
		if ( debug ) {
			System.out.format("start state closure=%s\n", closure);
		}

		traceLookahead1();
		int t = input.LA(1);
		DFAState s = ds0; // s is current/from DFA state

		while ( true ) { // while more work
			if ( debug ) {
				System.out.format("execATN loop starting closure: %s\n", closure);
			}

			// As we move src->trg, src->trg, we keep track of the previous trg to
			// avoid looking up the DFA state again, which is expensive.
			// If the previous target was already part of the DFA, we might
			// be able to avoid doing a reach operation upon t. If s!=null,
			// it means that semantic predicates didn't prevent us from
			// creating a DFA state. Once we know s!=null, we check to see if
			// the DFA state has an edge already for t. If so, we can just reuse
			// it's configuration set; there's no point in re-computing it.
			// This is kind of like doing DFA simulation within the ATN
			// simulation because DFA simulation is really just a way to avoid
			// computing reach/closure sets. Technically, once we know that
			// we have a previously added DFA state, we could jump over to
			// the DFA simulator. But, that would mean popping back and forth
			// a lot and making things more complicated algorithmically.
			// This optimization makes a lot of sense for loops within DFA.
			// A character will take us back to an existing DFA state
			// that already has lots of edges out of it. e.g., .* in comments.
			DFAState target = null;
			ATNConfigSet reach = null;
			if (s != null) {
				if ( s.edges != null && t < s.edges.length && t > IntStream.EOF ) {
					closure = s.configs;
					target = s.edges[t];
					if (target == ERROR) {
						break;
					}
					else if (target != null) {
						if ( debug ) {
							System.out.println("reuse state "+s.stateNumber+
											   " edge to "+target.stateNumber);
						}
						reach = target.configs;
					}
				}
			}

			if (target == null) {
				reach = new ATNConfigSet();

				// if we don't find an existing DFA state
				// Fill reach starting from closure, following t transitions
				getReachableConfigSet(closure, reach, t);

				if ( reach.isEmpty() ) { // we got nowhere on t from s
					// we reached state associated with closure for sure, so
					// make sure it's defined. worst case, we define s0 from
					// start state configs.
					DFAState from = s != null ? s : addDFAState(closure);
					// we got nowhere on t, don't throw out this knowledge; it'd
					// cause a failover from DFA later.
					if (from != null) {
						addDFAEdge(from, t, ERROR);
					}
					break; // stop when we can't match any more char
				}

				// Did we hit a stop state during reach op?
				reach = processAcceptConfigs(input, reach);

				// Add an edge from s to target DFA found/created for reach
				target = addDFAEdge(s, t, reach);
			}
			else if (target.isAcceptState) {
				traceAcceptState(target.prediction);
				captureSimState(prevAccept, input, target);
			}

			consume(input);
			traceLookahead1();
			t = input.LA(1);

			closure = reach;
			s = target; // flip; current DFA target becomes new src/from state
		}

		return failOrAccept(prevAccept, input, closure, t);
	}

	protected int failOrAccept(SimState prevAccept, CharStream input,
							   ATNConfigSet reach, int t)
	{
		if (prevAccept.dfaState != null) {
			int ruleIndex = prevAccept.dfaState.lexerRuleIndex;
			int actionIndex = prevAccept.dfaState.lexerActionIndex;
			accept(input, ruleIndex, actionIndex,
				prevAccept.index, prevAccept.line, prevAccept.charPos);
			tracePredict(prevAccept.dfaState.prediction);
			return prevAccept.dfaState.prediction;
		}
		else if (prevAccept.config != null) {
			int ruleIndex = prevAccept.config.state.ruleIndex;
			accept(input, ruleIndex, prevAccept.config.lexerActionIndex,
				prevAccept.index, prevAccept.line, prevAccept.charPos);
			return atn.ruleToTokenType[ruleIndex];
		}
		else {
			// if no accept and EOF is first char, return EOF
			if ( t==IntStream.EOF && input.index()==startIndex ) {
				return Token.EOF;
			}

			throw new LexerNoViableAltException(recog, input, startIndex, reach);
		}
	}

	/** Given a starting configuration set, figure out all ATN configurations
	 *  we can reach upon input t. Parameter reach is a return parameter.
	 */
	protected void getReachableConfigSet(ATNConfigSet closure, ATNConfigSet reach, int t) {
		for (ATNConfig c : closure) {
			if ( debug ) {
				System.out.format("testing %s at %s\n", getTokenName(t), c.toString(recog, true));
			}

			int n = c.state.getNumberOfTransitions();
			for (int ti=0; ti<n; ti++) {               // for each transition
				Transition trans = c.state.transition(ti);
				ATNState target = getReachableTarget(trans, t);
				if ( target!=null ) {
					closure(new LexerATNConfig((LexerATNConfig)c, target), reach);
				}
			}
		}
	}

	protected ATNConfigSet processAcceptConfigs(@NotNull CharStream input, @NotNull ATNConfigSet reach) {
		if ( debug ) {
			System.out.format("processAcceptConfigs: reach=%s, prevAccept=%s, prevIndex=%d\n",
						 	  reach, prevAccept.config, prevAccept.index);
		}
		for (int ci=0; ci<reach.size(); ci++) {
			LexerATNConfig c = (LexerATNConfig)reach.get(ci);
			if ( c.state instanceof RuleStopState) {
				if ( debug ) {
					System.out.format("processAcceptConfigs: hit accept config %s index %d\n",
									  c, input.index());
				}

				int index = input.index();
				if ( index > prevAccept.index ) {
					if ( debug ) {
						if ( prevAccept.index>=0 ) {
							System.out.println("processAcceptConfigs: found longer token");
						}
					}
					// condition > not >= will favor prev accept at same index.
					// This way, "int" is keyword not ID if listed first.
					traceAcceptState(c.alt);
					if ( debug ) {
						System.out.format("markExecSettings for %s @ index=%d, line %d:%d\n", c, index, prevAccept.line, prevAccept.charPos);
					}
					captureSimState(prevAccept, input, c);
				}

				// if we reach lexer accept state with empty stack,
				// toss out any configs pointing at wildcard edges
				// in rest of configs work list associated with this
				// rule (config.alt); that rule is done. this is how we
				// cut off nongreedy .+ loops.
				reach = deleteWildcardConfigsForAlt(reach, ci, c);

			 	// move to next char, looking for longer match
				// (we continue processing if there are states in reach)
			}
		}
		return reach;
	}

	protected void accept(@NotNull CharStream input, int ruleIndex, int actionIndex,
						  int index, int line, int charPos)
	{
		if ( debug ) {
			System.out.format("ACTION %s:%d\n", recog != null ? recog.getRuleNames()[ruleIndex] : ruleIndex, actionIndex);
		}

		if ( actionIndex>=0 && recog!=null ) recog.action(null, ruleIndex, actionIndex);

		// seek to after last char in token
		traceSeek(index);
		input.seek(index);
		this.line = line;
		this.charPositionInLine = charPos;
		consume(input);
	}

	@Nullable
	public ATNState getReachableTarget(Transition trans, int t) {
		switch (trans.getSerializationType()) {
		case Transition.ATOM:
			AtomTransition at = (AtomTransition)trans;
			if ( at.label == t ) {
				if ( debug ) {
					System.out.format("match %s\n", getTokenName(at.label));
				}

				return at.target;
			}

			return null;

		case Transition.RANGE:
			RangeTransition rt = (RangeTransition)trans;
			if ( t>=rt.from && t<=rt.to ) {
				if ( debug ) {
					System.out.format("match range %s\n", rt);
				}

				return rt.target;
			}

			return null;

		case Transition.SET:
			SetTransition st = (SetTransition)trans;
			if ( st.set.contains(t) ) {
				if ( debug ) {
					System.out.format("match set %s\n", st.set.toString(true));
				}

				return st.target;
			}

			return null;

		case Transition.NOT_SET:
			NotSetTransition nst = (NotSetTransition)trans;
			if (!nst.set.contains(t) && t!=IntStream.EOF) // ~set doesn't not match EOF
			{
				if ( debug ) {
					System.out.format("match ~set %s\n", nst.set.toString(true));
				}

				return nst.target;
			}

			return null;

		case Transition.WILDCARD:
			if (t != IntStream.EOF) {
				return trans.target;
			}

			return null;

		default:
			return null;
		}
	}

	/** Delete configs for alt following ci that have a wildcard edge but
	 *  only for configs with empty stack. E.g., if we want to kill after
	 *  config (2,1,[$]), then we need to wack only configs with $ stack:
	 *
	 *  	[..., (2,1,[$]), ..., (7,1,[[$, 6 $]])]
	 *
	 *  That means wacking (7,1,[$]) but not (7,1,[6 $]).
	 *
	 *  Incoming config could have multiple stacks but we only care about
	 *  empty stack since that means we reached end of a lexer rule from
	 *  nextToken directly.
	 *
	 *  Closure is unmodified; copy returned.
	 */
	public ATNConfigSet deleteWildcardConfigsForAlt(@NotNull ATNConfigSet closure,
													int ci,
													ATNConfig config)
	{
		int alt = config.alt;
		if ( debug ) {
			System.out.printf("deleteWildcardConfigsForAlt for alt %d after config %d\n", alt, ci);
		}

		ATNConfigSet dup = new ATNConfigSet(); // build up as we go thru loop
		for (int j=0; j<=ci; j++) dup.add(closure.get(j)); // add stuff up to ci
		int j=ci+1;
		while ( j < closure.size() ) {
			LexerATNConfig c = (LexerATNConfig)closure.get(j);
			boolean isWildcard = c.state.getClass() == ATNState.class && // plain state only, not rulestop etc..
				    c.state.transition(0) instanceof WildcardTransition;
			if ( c.alt == alt && isWildcard ) {
				// found config to kill but only if empty stack.
				for (SingletonPredictionContext ctx : c.context) {
					if ( ctx.isEmpty() ) {
						// c.alt matches, empty stack, and j > ci => kill it
						if ( debug ) {
							System.out.format("delete config %s since alt %d and %d leads to wildcard\n",
											  c, c.alt, c.state.stateNumber);
						}
						// don't add
					}
					else {
						LexerATNConfig splitConfig =
							new LexerATNConfig(c.state, c.alt, ctx, c.lexerActionIndex);
						dup.add(splitConfig);
					}
				}
			}
			else {
				dup.add(c); // add entire config
			}
			j++;
		}
		return dup;
	}

	@NotNull
	protected ATNConfigSet computeStartState(@NotNull IntStream input,
											 @NotNull ATNState p)
	{
		PredictionContext initialContext = PredictionContext.EMPTY;
		ATNConfigSet configs = new ATNConfigSet();
		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			ATNState target = p.transition(i).target;
			LexerATNConfig c = new LexerATNConfig(target, i+1, initialContext);
			closure(c, configs);
		}
		return configs;
	}

	protected void closure(@NotNull LexerATNConfig config, @NotNull ATNConfigSet configs) {
		if ( debug ) {
			System.out.println("closure("+config.toString(recog, true)+")");
		}

		// TODO? if ( closure.contains(t) ) return;

		if ( config.state instanceof RuleStopState ) {
			if ( debug ) {
				if ( recog!=null ) {
					System.out.format("closure at %s rule stop %s\n", recog.getRuleNames()[config.state.ruleIndex], config);
				}
				else {
					System.out.format("closure at rule stop %s\n", config);
				}
			}

			if ( config.context == null || config.context.isEmpty() ) {
				configs.add(config);
				return;
			}
			if ( config.context!=null && !config.context.isEmpty() ) {
				for (SingletonPredictionContext ctx : config.context) {
					if ( !ctx.isEmpty() ) {
						PredictionContext newContext = ctx.parent; // "pop" invoking state
						if ( ctx.invokingState==PredictionContext.EMPTY_FULL_CTX_INVOKING_STATE ) {
							// we have no context info. Don't pursue but
							// record a config that indicates how we hit end
							LexerATNConfig c = new LexerATNConfig(config, config.state, ctx);
							if ( debug ) System.out.println("FALLING off token "+
														    recog.getRuleNames()[config.state.ruleIndex]+
														    " record "+c);
							configs.add(c);
							continue;
						}
						ATNState invokingState = atn.states.get(ctx.invokingState);
						RuleTransition rt = (RuleTransition)invokingState.transition(0);
						ATNState retState = rt.followState;
						LexerATNConfig c = new LexerATNConfig(retState, config.alt, newContext);
						closure(c, configs);
					}
				}
			}
			return;
		}

		// optimization
		if ( !config.state.onlyHasEpsilonTransitions() )	{
			configs.add(config);
		}

		ATNState p = config.state;
		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			Transition t = p.transition(i);
			LexerATNConfig c = getEpsilonTarget(config, t, configs);
			if ( c!=null ) closure(c, configs);
		}
	}

	// side-effect: can alter configs.hasSemanticContext
	@Nullable
	public LexerATNConfig getEpsilonTarget(@NotNull LexerATNConfig config,
									  @NotNull Transition t,
									  @NotNull ATNConfigSet configs)
	{
		ATNState p = config.state;

		LexerATNConfig c = null;
		switch (t.getSerializationType()) {
			case Transition.RULE:
				PredictionContext newContext =
					new SingletonPredictionContext(config.context, p.stateNumber);
				c = new LexerATNConfig(config, t.target, newContext);
				break;
			case Transition.PREDICATE:
//				if (recog == null) {
//					System.out.format("Predicates cannot be evaluated without a recognizer; assuming true.\n");
//				}

				/*  Track traversing semantic predicates. If we traverse,
				 we cannot add a DFA state for this "reach" computation
				 because the DFA would not test the predicate again in the
				 future. Rather than creating collections of semantic predicates
				 like v3 and testing them on prediction, v4 will test them on the
				 fly all the time using the ATN not the DFA. This is slower but
				 semantically it's not used that often. One of the key elements to
				 this predicate mechanism is not adding DFA states that see
				 predicates immediately afterwards in the ATN. For example,

				 a : ID {p1}? | ID {p2}? ;

				 should create the start state for rule 'a' (to save start state
				 competition), but should not create target of ID state. The
				 collection of ATN states the following ID references includes
				 states reached by traversing predicates. Since this is when we
				 test them, we cannot cash the DFA state target of ID.
			 */
				PredicateTransition pt = (PredicateTransition)t;
				if ( debug ) {
					System.out.println("EVAL rule "+pt.ruleIndex+":"+pt.predIndex);
				}
				configs.hasSemanticContext = true;
				if ( recog == null || recog.sempred(null, pt.ruleIndex, pt.predIndex) ) {
					c = new LexerATNConfig(config, t.target, pt.getPredicate());
				}
				break;
			// ignore actions; just exec one per rule upon accept
			case Transition.ACTION:
				c = new LexerATNConfig(config, t.target, ((ActionTransition)t).actionIndex);
				break;
			case Transition.EPSILON:
				c = new LexerATNConfig(config, t.target);
				break;
		}

		return c;
	}

	protected int failOverToATN(@NotNull CharStream input, @NotNull DFAState s) {
		traceFailOverToATN();

		if ( dfa_debug ) {
			System.out.format("no edge for %s\n", getTokenName(input.LA(1)));
			System.out.format("ATN exec upon %s at DFA state %d = %s\n",
							  input.getText(Interval.of(startIndex, input.index())), s.stateNumber, s.configs);
		}

		int ttype = execATN(input, s.configs, s);

		if ( dfa_debug ) {
			System.out.format("back from DFA update, ttype=%d, dfa[mode %d]=\n%s\n",
							  ttype, mode, decisionToDFA[mode].toLexerString());
		}

		// action already executed by ATN
		// we've updated DFA, exec'd action, and have our deepest answer
		tracePredict(ttype);
		return ttype;
	}

	protected void captureSimState(@NotNull SimState settings,
								   @NotNull CharStream input,
								   @NotNull DFAState dfaState)
	{
		settings.index = input.index();
		settings.line = line;
		settings.charPos = charPositionInLine;
		settings.config = null;
		settings.dfaState = dfaState;
	}

	protected void captureSimState(@NotNull SimState settings,
								   @NotNull CharStream input,
								   @NotNull LexerATNConfig config)
	{
		settings.index = input.index();
		settings.line = line;
		settings.charPos = charPositionInLine;
		settings.config = config;
		settings.dfaState = null;
	}

	protected DFAState addDFAEdge(@NotNull DFAState from,
								  int t,
								  @NotNull ATNConfigSet q)
	{
		DFAState to = addDFAState(q);

		// even if we can add the states, we can't add an edge for labels out of range
		if (t < 0 || t > MAX_DFA_EDGE) {
			return to;
		}

		if (from == null || to == null) {
			return to;
		}

		if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+((char)t));
		addDFAEdge(from, t, to);

		return to;
	}

	protected void addDFAEdge(@NotNull DFAState p, int t, @NotNull DFAState q) {
		if (t < 0 || t > MAX_DFA_EDGE) return; // Only track edges within the DFA bounds
		DFA dfa = decisionToDFA[mode];
		synchronized (dfa) {
			if ( p.edges==null ) {
				//  make room for tokens 1..n and -1 masquerading as index 0
				p.edges = new DFAState[MAX_DFA_EDGE+1]; // TODO: make adaptive
			}
			p.edges[t] = q; // connect
		}
	}

	/** Add a new DFA state if there isn't one with this set of
		configurations already. This method also detects the first
		configuration containing an ATN rule stop state. Later, when
		traversing the DFA, we will know which rule to accept.
	 */
	@Nullable
	protected DFAState addDFAState(@NotNull ATNConfigSet configs) {
		// If we eval'd a predicate while filling configs, mustn't create DFA state
		if ( configs.hasSemanticContext ) return null;

		DFAState proposed = new DFAState(configs);
		ATNConfig firstConfigWithRuleStopState = null;
		for (ATNConfig c : configs) {
			if ( c.state instanceof RuleStopState )	{
				firstConfigWithRuleStopState = c;
				break;
			}
		}

		if ( firstConfigWithRuleStopState!=null ) {
			proposed.isAcceptState = true;
			proposed.lexerRuleIndex = firstConfigWithRuleStopState.state.ruleIndex;
			proposed.lexerActionIndex =
				((LexerATNConfig)firstConfigWithRuleStopState).lexerActionIndex;
			proposed.prediction = atn.ruleToTokenType[proposed.lexerRuleIndex];
		}

		DFA dfa = decisionToDFA[mode];
		synchronized (dfa) {
			DFAState existing = dfa.states.get(proposed);
			if ( existing!=null ) return existing;

			DFAState newState = proposed;

			newState.stateNumber = dfa.states.size();
			configs.setReadonly(true);
			newState.configs = configs;
			decisionToDFA[mode].states.put(newState, newState);
			return newState;
		}
	}

	@Nullable
	public DFA getDFA(int mode) {
		return decisionToDFA[mode];
	}

	/** Get the text of the current token from an *action* in lexer not
	 *  predicate.
	 */
	@NotNull
	public String getText(@NotNull CharStream input) {
		// index is first lookahead char, don't include.
		return input.getText(Interval.of(startIndex, input.index()-1));
	}

	/** Get the text from start of token to current lookahead char.
	 *  Use this in predicates to test text matched so far in a lexer rule.
	 */
	@NotNull
	public String getSpeculativeText(@NotNull CharStream input) {
		// index is first lookahead char, don't include.
		return input.getText(Interval.of(startIndex, input.index()));
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public void setCharPositionInLine(int charPositionInLine) {
		this.charPositionInLine = charPositionInLine;
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
			traceByteSlow(LexerOpCode.PushMode, (byte) mode);
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
