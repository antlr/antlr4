/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const {Token} = require('./../Token');
const Lexer = require('./../Lexer');
const ATN = require('./ATN');
const ATNSimulator = require('./ATNSimulator');
const {DFAState} = require('./../dfa/DFAState');
const {OrderedATNConfigSet} = require('./ATNConfigSet');
const {PredictionContext} = require('./../PredictionContext');
const {SingletonPredictionContext} = require('./../PredictionContext');
const {RuleStopState} = require('./ATNState');
const {LexerATNConfig} = require('./ATNConfig');
const {Transition} = require('./Transition');
const LexerActionExecutor = require('./LexerActionExecutor');
const {LexerNoViableAltException} = require('./../error/Errors');

function resetSimState(sim) {
	sim.index = -1;
	sim.line = 0;
	sim.column = -1;
	sim.dfaState = null;
}

class SimState {
	constructor() {
		resetSimState(this);
	}

	reset() {
		resetSimState(this);
	}
}

class LexerATNSimulator extends ATNSimulator {
	/**
	 * When we hit an accept state in either the DFA or the ATN, we
	 * have to notify the character stream to start buffering characters
	 * via {@link IntStream//mark} and record the current state. The current sim state
	 * includes the current index into the input, the current line,
	 * and current character position in that line. Note that the Lexer is
	 * tracking the starting line and characterization of the token. These
	 * variables track the "state" of the simulator when it hits an accept state.
	 *
	 * <p>We track these variables separately for the DFA and ATN simulation
	 * because the DFA simulation often has to fail over to the ATN
	 * simulation. If the ATN simulation fails, we need the DFA to fall
	 * back to its previously accepted state, if any. If the ATN succeeds,
	 * then the ATN does the accept and the DFA simulator that invoked it
	 * can simply return the predicted token type.</p>
	 */
	constructor(recog, atn, decisionToDFA, sharedContextCache) {
		super(atn, sharedContextCache);
		this.decisionToDFA = decisionToDFA;
		this.recog = recog;
		/**
		 * The current token's starting index into the character stream.
		 * Shared across DFA to ATN simulation in case the ATN fails and the
		 * DFA did not have a previous accept state. In this case, we use the
		 * ATN-generated exception object
		 */
		this.startIndex = -1;
		// line number 1..n within the input///
		this.line = 1;
		/**
		 * The index of the character relative to the beginning of the line
		 * 0..n-1
		 */
		this.column = 0;
		this.mode = Lexer.DEFAULT_MODE;
		/**
		 * Used during DFA/ATN exec to record the most recent accept configuration
		 * info
		 */
		this.prevAccept = new SimState();
	}

	copyState(simulator) {
		this.column = simulator.column;
		this.line = simulator.line;
		this.mode = simulator.mode;
		this.startIndex = simulator.startIndex;
	}

	match(input, mode) {
		this.match_calls += 1;
		this.mode = mode;
		const mark = input.mark();
		try {
			this.startIndex = input.index;
			this.prevAccept.reset();
			const dfa = this.decisionToDFA[mode];
			if (dfa.s0 === null) {
				return this.matchATN(input);
			} else {
				return this.execATN(input, dfa.s0);
			}
		} finally {
			input.release(mark);
		}
	}

	reset() {
		this.prevAccept.reset();
		this.startIndex = -1;
		this.line = 1;
		this.column = 0;
		this.mode = Lexer.DEFAULT_MODE;
	}

	matchATN(input) {
		const startState = this.atn.modeToStartState[this.mode];

		if (LexerATNSimulator.debug) {
			console.log("matchATN mode " + this.mode + " start: " + startState);
		}
		const old_mode = this.mode;
		const s0_closure = this.computeStartState(input, startState);
		const suppressEdge = s0_closure.hasSemanticContext;
		s0_closure.hasSemanticContext = false;

		const next = this.addDFAState(s0_closure);
		if (!suppressEdge) {
			this.decisionToDFA[this.mode].s0 = next;
		}

		const predict = this.execATN(input, next);

		if (LexerATNSimulator.debug) {
			console.log("DFA after matchATN: " + this.decisionToDFA[old_mode].toLexerString());
		}
		return predict;
	}

	execATN(input, ds0) {
		if (LexerATNSimulator.debug) {
			console.log("start state closure=" + ds0.configs);
		}
		if (ds0.isAcceptState) {
			// allow zero-length tokens
			this.captureSimState(this.prevAccept, input, ds0);
		}
		let t = input.LA(1);
		let s = ds0; // s is current/from DFA state

		while (true) { // while more work
			if (LexerATNSimulator.debug) {
				console.log("execATN loop starting closure: " + s.configs);
			}

			/**
			 * As we move src->trg, src->trg, we keep track of the previous trg to
			 * avoid looking up the DFA state again, which is expensive.
			 * If the previous target was already part of the DFA, we might
			 * be able to avoid doing a reach operation upon t. If s!=null,
			 * it means that semantic predicates didn't prevent us from
			 * creating a DFA state. Once we know s!=null, we check to see if
			 * the DFA state has an edge already for t. If so, we can just reuse
			 * it's configuration set; there's no point in re-computing it.
			 * This is kind of like doing DFA simulation within the ATN
			 * simulation because DFA simulation is really just a way to avoid
			 * computing reach/closure sets. Technically, once we know that
			 * we have a previously added DFA state, we could jump over to
			 * the DFA simulator. But, that would mean popping back and forth
			 * a lot and making things more complicated algorithmically.
			 * This optimization makes a lot of sense for loops within DFA.
			 * A character will take us back to an existing DFA state
			 * that already has lots of edges out of it. e.g., .* in comments.
			 * print("Target for:" + str(s) + " and:" + str(t))
			 */
			let target = this.getExistingTargetState(s, t);
			// print("Existing:" + str(target))
			if (target === null) {
				target = this.computeTargetState(input, s, t);
				// print("Computed:" + str(target))
			}
			if (target === ATNSimulator.ERROR) {
				break;
			}
			// If this is a consumable input element, make sure to consume before
			// capturing the accept state so the input index, line, and char
			// position accurately reflect the state of the interpreter at the
			// end of the token.
			if (t !== Token.EOF) {
				this.consume(input);
			}
			if (target.isAcceptState) {
				this.captureSimState(this.prevAccept, input, target);
				if (t === Token.EOF) {
					break;
				}
			}
			t = input.LA(1);
			s = target; // flip; current DFA target becomes new src/from state
		}
		return this.failOrAccept(this.prevAccept, input, s.configs, t);
	}

	/**
	 * Get an existing target state for an edge in the DFA. If the target state
	 * for the edge has not yet been computed or is otherwise not available,
	 * this method returns {@code null}.
	 *
	 * @param s The current DFA state
	 * @param t The next input symbol
	 * @return The existing target DFA state for the given input symbol
	 * {@code t}, or {@code null} if the target state for this edge is not
	 * already cached
	 */
	getExistingTargetState(s, t) {
		if (s.edges === null || t < LexerATNSimulator.MIN_DFA_EDGE || t > LexerATNSimulator.MAX_DFA_EDGE) {
			return null;
		}

		let target = s.edges[t - LexerATNSimulator.MIN_DFA_EDGE];
		if(target===undefined) {
			target = null;
		}
		if (LexerATNSimulator.debug && target !== null) {
			console.log("reuse state " + s.stateNumber + " edge to " + target.stateNumber);
		}
		return target;
	}

	/**
	 * Compute a target state for an edge in the DFA, and attempt to add the
	 * computed state and corresponding edge to the DFA.
	 *
	 * @param input The input stream
	 * @param s The current DFA state
	 * @param t The next input symbol
	 *
	 * @return The computed target DFA state for the given input symbol
	 * {@code t}. If {@code t} does not lead to a valid DFA state, this method
	 * returns {@link //ERROR}.
	 */
	computeTargetState(input, s, t) {
		const reach = new OrderedATNConfigSet();
		// if we don't find an existing DFA state
		// Fill reach starting from closure, following t transitions
		this.getReachableConfigSet(input, s.configs, reach, t);

		if (reach.items.length === 0) { // we got nowhere on t from s
			if (!reach.hasSemanticContext) {
				// we got nowhere on t, don't throw out this knowledge; it'd
				// cause a failover from DFA later.
				this.addDFAEdge(s, t, ATNSimulator.ERROR);
			}
			// stop when we can't match any more char
			return ATNSimulator.ERROR;
		}
		// Add an edge from s to target DFA found/created for reach
		return this.addDFAEdge(s, t, null, reach);
	}

	failOrAccept(prevAccept, input, reach, t) {
		if (this.prevAccept.dfaState !== null) {
			const lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor;
			this.accept(input, lexerActionExecutor, this.startIndex,
					prevAccept.index, prevAccept.line, prevAccept.column);
			return prevAccept.dfaState.prediction;
		} else {
			// if no accept and EOF is first char, return EOF
			if (t === Token.EOF && input.index === this.startIndex) {
				return Token.EOF;
			}
			throw new LexerNoViableAltException(this.recog, input, this.startIndex, reach);
		}
	}

	/**
	 * Given a starting configuration set, figure out all ATN configurations
	 * we can reach upon input {@code t}. Parameter {@code reach} is a return
	 * parameter.
	 */
	getReachableConfigSet(input, closure,
			reach, t) {
		// this is used to skip processing for configs which have a lower priority
		// than a config that already reached an accept state for the same rule
		let skipAlt = ATN.INVALID_ALT_NUMBER;
		for (let i = 0; i < closure.items.length; i++) {
			const cfg = closure.items[i];
			const currentAltReachedAcceptState = (cfg.alt === skipAlt);
			if (currentAltReachedAcceptState && cfg.passedThroughNonGreedyDecision) {
				continue;
			}
			if (LexerATNSimulator.debug) {
				console.log("testing %s at %s\n", this.getTokenName(t), cfg
						.toString(this.recog, true));
			}
			for (let j = 0; j < cfg.state.transitions.length; j++) {
				const trans = cfg.state.transitions[j]; // for each transition
				const target = this.getReachableTarget(trans, t);
				if (target !== null) {
					let lexerActionExecutor = cfg.lexerActionExecutor;
					if (lexerActionExecutor !== null) {
						lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.index - this.startIndex);
					}
					const treatEofAsEpsilon = (t === Token.EOF);
					const config = new LexerATNConfig({state:target, lexerActionExecutor:lexerActionExecutor}, cfg);
					if (this.closure(input, config, reach,
							currentAltReachedAcceptState, true, treatEofAsEpsilon)) {
						// any remaining configs for this alt have a lower priority
						// than the one that just reached an accept state.
						skipAlt = cfg.alt;
					}
				}
			}
		}
	}

	accept(input, lexerActionExecutor,
			   startIndex, index, line, charPos) {
		   if (LexerATNSimulator.debug) {
			   console.log("ACTION %s\n", lexerActionExecutor);
		   }
		   // seek to after last char in token
		   input.seek(index);
		   this.line = line;
		   this.column = charPos;
		   if (lexerActionExecutor !== null && this.recog !== null) {
			   lexerActionExecutor.execute(this.recog, input, startIndex);
		   }
	   }

	getReachableTarget(trans, t) {
		if (trans.matches(t, 0, Lexer.MAX_CHAR_VALUE)) {
			return trans.target;
		} else {
			return null;
		}
	}

	computeStartState(input, p) {
		const initialContext = PredictionContext.EMPTY;
		const configs = new OrderedATNConfigSet();
		for (let i = 0; i < p.transitions.length; i++) {
			const target = p.transitions[i].target;
			const cfg = new LexerATNConfig({state:target, alt:i+1, context:initialContext}, null);
			this.closure(input, cfg, configs, false, false, false);
		}
		return configs;
	}

	/**
	 * Since the alternatives within any lexer decision are ordered by
	 * preference, this method stops pursuing the closure as soon as an accept
	 * state is reached. After the first accept state is reached by depth-first
	 * search from {@code config}, all other (potentially reachable) states for
	 * this rule would have a lower priority.
	 *
	 * @return {Boolean} {@code true} if an accept state is reached, otherwise
	 * {@code false}.
	 */
	closure(input, config, configs,
			currentAltReachedAcceptState, speculative, treatEofAsEpsilon) {
		let cfg = null;
		if (LexerATNSimulator.debug) {
			console.log("closure(" + config.toString(this.recog, true) + ")");
		}
		if (config.state instanceof RuleStopState) {
			if (LexerATNSimulator.debug) {
				if (this.recog !== null) {
					console.log("closure at %s rule stop %s\n", this.recog.ruleNames[config.state.ruleIndex], config);
				} else {
					console.log("closure at rule stop %s\n", config);
				}
			}
			if (config.context === null || config.context.hasEmptyPath()) {
				if (config.context === null || config.context.isEmpty()) {
					configs.add(config);
					return true;
				} else {
					configs.add(new LexerATNConfig({ state:config.state, context:PredictionContext.EMPTY}, config));
					currentAltReachedAcceptState = true;
				}
			}
			if (config.context !== null && !config.context.isEmpty()) {
				for (let i = 0; i < config.context.length; i++) {
					if (config.context.getReturnState(i) !== PredictionContext.EMPTY_RETURN_STATE) {
						const newContext = config.context.getParent(i); // "pop" return state
						const returnState = this.atn.states[config.context.getReturnState(i)];
						cfg = new LexerATNConfig({ state:returnState, context:newContext }, config);
						currentAltReachedAcceptState = this.closure(input, cfg,
								configs, currentAltReachedAcceptState, speculative,
								treatEofAsEpsilon);
					}
				}
			}
			return currentAltReachedAcceptState;
		}
		// optimization
		if (!config.state.epsilonOnlyTransitions) {
			if (!currentAltReachedAcceptState || !config.passedThroughNonGreedyDecision) {
				configs.add(config);
			}
		}
		for (let j = 0; j < config.state.transitions.length; j++) {
			const trans = config.state.transitions[j];
			cfg = this.getEpsilonTarget(input, config, trans, configs, speculative, treatEofAsEpsilon);
			if (cfg !== null) {
				currentAltReachedAcceptState = this.closure(input, cfg, configs,
						currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
			}
		}
		return currentAltReachedAcceptState;
	}

	// side-effect: can alter configs.hasSemanticContext
	getEpsilonTarget(input, config, trans,
			configs, speculative, treatEofAsEpsilon) {
		let cfg = null;
		if (trans.serializationType === Transition.RULE) {
			const newContext = SingletonPredictionContext.create(config.context, trans.followState.stateNumber);
			cfg = new LexerATNConfig( { state:trans.target, context:newContext}, config);
		} else if (trans.serializationType === Transition.PRECEDENCE) {
			throw "Precedence predicates are not supported in lexers.";
		} else if (trans.serializationType === Transition.PREDICATE) {
			// Track traversing semantic predicates. If we traverse,
			// we cannot add a DFA state for this "reach" computation
			// because the DFA would not test the predicate again in the
			// future. Rather than creating collections of semantic predicates
			// like v3 and testing them on prediction, v4 will test them on the
			// fly all the time using the ATN not the DFA. This is slower but
			// semantically it's not used that often. One of the key elements to
			// this predicate mechanism is not adding DFA states that see
			// predicates immediately afterwards in the ATN. For example,

			// a : ID {p1}? | ID {p2}? ;

			// should create the start state for rule 'a' (to save start state
			// competition), but should not create target of ID state. The
			// collection of ATN states the following ID references includes
			// states reached by traversing predicates. Since this is when we
			// test them, we cannot cash the DFA state target of ID.

			if (LexerATNSimulator.debug) {
				console.log("EVAL rule " + trans.ruleIndex + ":" + trans.predIndex);
			}
			configs.hasSemanticContext = true;
			if (this.evaluatePredicate(input, trans.ruleIndex, trans.predIndex, speculative)) {
				cfg = new LexerATNConfig({ state:trans.target}, config);
			}
		} else if (trans.serializationType === Transition.ACTION) {
			if (config.context === null || config.context.hasEmptyPath()) {
				// execute actions anywhere in the start rule for a token.
				//
				// TODO: if the entry rule is invoked recursively, some
				// actions may be executed during the recursive call. The
				// problem can appear when hasEmptyPath() is true but
				// isEmpty() is false. In this case, the config needs to be
				// split into two contexts - one with just the empty path
				// and another with everything but the empty path.
				// Unfortunately, the current algorithm does not allow
				// getEpsilonTarget to return two configurations, so
				// additional modifications are needed before we can support
				// the split operation.
				const lexerActionExecutor = LexerActionExecutor.append(config.lexerActionExecutor,
						this.atn.lexerActions[trans.actionIndex]);
				cfg = new LexerATNConfig({ state:trans.target, lexerActionExecutor:lexerActionExecutor }, config);
			} else {
				// ignore actions in referenced rules
				cfg = new LexerATNConfig( { state:trans.target}, config);
			}
		} else if (trans.serializationType === Transition.EPSILON) {
			cfg = new LexerATNConfig({ state:trans.target}, config);
		} else if (trans.serializationType === Transition.ATOM ||
					trans.serializationType === Transition.RANGE ||
					trans.serializationType === Transition.SET) {
			if (treatEofAsEpsilon) {
				if (trans.matches(Token.EOF, 0, Lexer.MAX_CHAR_VALUE)) {
					cfg = new LexerATNConfig( { state:trans.target }, config);
				}
			}
		}
		return cfg;
	}

	/**
	 * Evaluate a predicate specified in the lexer.
	 *
	 * <p>If {@code speculative} is {@code true}, this method was called before
	 * {@link //consume} for the matched character. This method should call
	 * {@link //consume} before evaluating the predicate to ensure position
	 * sensitive values, including {@link Lexer//getText}, {@link Lexer//getLine},
	 * and {@link Lexer//getcolumn}, properly reflect the current
	 * lexer state. This method should restore {@code input} and the simulator
	 * to the original state before returning (i.e. undo the actions made by the
	 * call to {@link //consume}.</p>
	 *
	 * @param input The input stream.
	 * @param ruleIndex The rule containing the predicate.
	 * @param predIndex The index of the predicate within the rule.
	 * @param speculative {@code true} if the current index in {@code input} is
	 * one character before the predicate's location.
	 *
	 * @return {@code true} if the specified predicate evaluates to
	 * {@code true}.
	 */
	evaluatePredicate(input, ruleIndex,
			predIndex, speculative) {
		// assume true if no recognizer was provided
		if (this.recog === null) {
			return true;
		}
		if (!speculative) {
			return this.recog.sempred(null, ruleIndex, predIndex);
		}
		const savedcolumn = this.column;
		const savedLine = this.line;
		const index = input.index;
		const marker = input.mark();
		try {
			this.consume(input);
			return this.recog.sempred(null, ruleIndex, predIndex);
		} finally {
			this.column = savedcolumn;
			this.line = savedLine;
			input.seek(index);
			input.release(marker);
		}
	}

	captureSimState(settings, input, dfaState) {
		settings.index = input.index;
		settings.line = this.line;
		settings.column = this.column;
		settings.dfaState = dfaState;
	}

	addDFAEdge(from_, tk, to, cfgs) {
		if (to === undefined) {
			to = null;
		}
		if (cfgs === undefined) {
			cfgs = null;
		}
		if (to === null && cfgs !== null) {
			// leading to this call, ATNConfigSet.hasSemanticContext is used as a
			// marker indicating dynamic predicate evaluation makes this edge
			// dependent on the specific input sequence, so the static edge in the
			// DFA should be omitted. The target DFAState is still created since
			// execATN has the ability to resynchronize with the DFA state cache
			// following the predicate evaluation step.
			//
			// TJP notes: next time through the DFA, we see a pred again and eval.
			// If that gets us to a previously created (but dangling) DFA
			// state, we can continue in pure DFA mode from there.
			// /
			const suppressEdge = cfgs.hasSemanticContext;
			cfgs.hasSemanticContext = false;

			to = this.addDFAState(cfgs);

			if (suppressEdge) {
				return to;
			}
		}
		// add the edge
		if (tk < LexerATNSimulator.MIN_DFA_EDGE || tk > LexerATNSimulator.MAX_DFA_EDGE) {
			// Only track edges within the DFA bounds
			return to;
		}
		if (LexerATNSimulator.debug) {
			console.log("EDGE " + from_ + " -> " + to + " upon " + tk);
		}
		if (from_.edges === null) {
			// make room for tokens 1..n and -1 masquerading as index 0
			from_.edges = [];
		}
		from_.edges[tk - LexerATNSimulator.MIN_DFA_EDGE] = to; // connect

		return to;
	}

	/**
	 * Add a new DFA state if there isn't one with this set of
	 * configurations already. This method also detects the first
	 * configuration containing an ATN rule stop state. Later, when
	 * traversing the DFA, we will know which rule to accept.
	 */
	addDFAState(configs) {
		const proposed = new DFAState(null, configs);
		let firstConfigWithRuleStopState = null;
		for (let i = 0; i < configs.items.length; i++) {
			const cfg = configs.items[i];
			if (cfg.state instanceof RuleStopState) {
				firstConfigWithRuleStopState = cfg;
				break;
			}
		}
		if (firstConfigWithRuleStopState !== null) {
			proposed.isAcceptState = true;
			proposed.lexerActionExecutor = firstConfigWithRuleStopState.lexerActionExecutor;
			proposed.prediction = this.atn.ruleToTokenType[firstConfigWithRuleStopState.state.ruleIndex];
		}
		const dfa = this.decisionToDFA[this.mode];
		const existing = dfa.states.get(proposed);
		if (existing!==null) {
			return existing;
		}
		const newState = proposed;
		newState.stateNumber = dfa.states.length;
		configs.setReadonly(true);
		newState.configs = configs;
		dfa.states.add(newState);
		return newState;
	}

	getDFA(mode) {
		return this.decisionToDFA[mode];
	}

// Get the text matched so far for the current token.
	getText(input) {
		// index is first lookahead char, don't include.
		return input.getText(this.startIndex, input.index - 1);
	}

	consume(input) {
		const curChar = input.LA(1);
		if (curChar === "\n".charCodeAt(0)) {
			this.line += 1;
			this.column = 0;
		} else {
			this.column += 1;
		}
		input.consume();
	}

	getTokenName(tt) {
		if (tt === -1) {
			return "EOF";
		} else {
			return "'" + String.fromCharCode(tt) + "'";
		}
	}
}

LexerATNSimulator.debug = false;
LexerATNSimulator.dfa_debug = false;

LexerATNSimulator.MIN_DFA_EDGE = 0;
LexerATNSimulator.MAX_DFA_EDGE = 127; // forces unicode to stay in ATN

LexerATNSimulator.match_calls = 0;

module.exports = LexerATNSimulator;
