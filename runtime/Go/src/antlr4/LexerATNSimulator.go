package antlr4

import (
	"fmt"
	"strconv"
)

// When we hit an accept state in either the DFA or the ATN, we
//  have to notify the character stream to start buffering characters
//  via {@link IntStream//mark} and record the current state. The current sim state
//  includes the current index into the input, the current line,
//  and current character position in that line. Note that the Lexer is
//  tracking the starting line and characterization of the token. These
//  variables track the "state" of the simulator when it hits an accept state.
//
//  <p>We track these variables separately for the DFA and ATN simulation
//  because the DFA simulation often has to fail over to the ATN
//  simulation. If the ATN simulation fails, we need the DFA to fall
//  back to its previously accepted state, if any. If the ATN succeeds,
//  then the ATN does the accept and the DFA simulator that invoked it
//  can simply return the predicted token type.</p>
///

func resetSimState(sim *SimState) {
	sim.index = -1
	sim.line = 0
	sim.column = -1
	sim.dfaState = nil
}

type SimState struct {
	index    int
	line     int
	column   int
	dfaState *DFAState
}

func NewSimState() *SimState {

	this := new(SimState)
	resetSimState(this)
	return this

}

func (this *SimState) reset() {
	resetSimState(this)
}

type LexerATNSimulator struct {
	*BaseATNSimulator

	recog          Lexer
	predictionMode int
	decisionToDFA  []*DFA
	mergeCache     DoubleDict
	startIndex     int
	line           int
	column         int
	mode           int
	prevAccept     *SimState
	Match_calls    int
}

func NewLexerATNSimulator(recog Lexer, atn *ATN, decisionToDFA []*DFA, sharedContextCache *PredictionContextCache) *LexerATNSimulator {

	this := new(LexerATNSimulator)

	this.BaseATNSimulator = NewBaseATNSimulator(atn, sharedContextCache)

	this.decisionToDFA = decisionToDFA
	this.recog = recog
	// The current token's starting index into the character stream.
	// Shared across DFA to ATN simulation in case the ATN fails and the
	// DFA did not have a previous accept state. In this case, we use the
	// ATN-generated exception object.
	this.startIndex = -1
	// line number 1..n within the input///
	this.line = 1
	// The index of the character relative to the beginning of the line
	// 0..n-1///
	this.column = 0
	this.mode = LexerDefaultMode
	// Used during DFA/ATN exec to record the most recent accept configuration
	// info
	this.prevAccept = NewSimState()
	// done
	return this
}

var LexerATNSimulatorDebug = false
var LexerATNSimulatorDFADebug = false

var LexerATNSimulatorMIN_DFA_EDGE = 0
var LexerATNSimulatorMAX_DFA_EDGE = 127 // forces unicode to stay in ATN

var LexerATNSimulatorMatch_calls = 0

func (this *LexerATNSimulator) copyState(simulator *LexerATNSimulator) {
	this.column = simulator.column
	this.line = simulator.line
	this.mode = simulator.mode
	this.startIndex = simulator.startIndex
}

func (this *LexerATNSimulator) Match(input CharStream, mode int) int {

	if PortDebug {
		fmt.Println("Match")
	}

	this.Match_calls += 1
	this.mode = mode
	var mark = input.Mark()

	defer func() {
		if PortDebug {
			fmt.Println("FINALLY")
		}
		input.Release(mark)
	}()

	this.startIndex = input.Index()
	this.prevAccept.reset()

	var dfa = this.decisionToDFA[mode]

	if dfa.s0 == nil {
		if PortDebug {
			fmt.Println("MatchATN")
		}
		return this.MatchATN(input)
	} else {
		if PortDebug {
			fmt.Println("execATN")
		}
		return this.execATN(input, dfa.s0)
	}
}

func (this *LexerATNSimulator) reset() {
	this.prevAccept.reset()
	this.startIndex = -1
	this.line = 1
	this.column = 0
	this.mode = LexerDefaultMode
}

func (this *LexerATNSimulator) MatchATN(input CharStream) int {
	var startState = this.atn.modeToStartState[this.mode]

	if LexerATNSimulatorDebug {
		fmt.Println("MatchATN mode " + strconv.Itoa(this.mode) + " start: " + startState.String())
	}
	var old_mode = this.mode
	var s0_closure = this.computeStartState(input, startState)
	var suppressEdge = s0_closure.hasSemanticContext
	s0_closure.hasSemanticContext = false

	var next = this.addDFAState(s0_closure.BaseATNConfigSet)

	if !suppressEdge {
		this.decisionToDFA[this.mode].s0 = next
	}

	var predict = this.execATN(input, next)

	if LexerATNSimulatorDebug {
		fmt.Println("DFA after MatchATN: " + this.decisionToDFA[old_mode].ToLexerString())
	}
	return predict
}

func (this *LexerATNSimulator) execATN(input CharStream, ds0 *DFAState) int {

	if LexerATNSimulatorDebug {
		fmt.Println("start state closure=" + ds0.configs.String())
	}
	if ds0.isAcceptState {
		// allow zero-length tokens
		this.captureSimState(this.prevAccept, input, ds0)
	}
	var t = input.LA(1)
	var s = ds0 // s is current/from DFA state

	for true { // while more work
		if LexerATNSimulatorDebug {
			fmt.Println("execATN loop starting closure: " + s.configs.String())
		}

		// As we move src->trg, src->trg, we keep track of the previous trg to
		// avoid looking up the DFA state again, which is expensive.
		// If the previous target was already part of the DFA, we might
		// be able to avoid doing a reach operation upon t. If s!=nil,
		// it means that semantic predicates didn't prevent us from
		// creating a DFA state. Once we know s!=nil, we check to see if
		// the DFA state has an edge already for t. If so, we can just reuse
		// it's configuration set there's no point in re-computing it.
		// This is kind of like doing DFA simulation within the ATN
		// simulation because DFA simulation is really just a way to avoid
		// computing reach/closure sets. Technically, once we know that
		// we have a previously added DFA state, we could jump over to
		// the DFA simulator. But, that would mean popping back and forth
		// a lot and making things more complicated algorithmically.
		// This optimization makes a lot of sense for loops within DFA.
		// A character will take us back to an existing DFA state
		// that already has lots of edges out of it. e.g., .* in comments.
		// print("Target for:" + str(s) + " and:" + str(t))
		var target = this.getExistingTargetState(s, t)
		// print("Existing:" + str(target))
		if target == nil {
			target = this.computeTargetState(input, s, t)
			// print("Computed:" + str(target))
		}
		if target == ATNSimulatorError {
			break
		}
		// If this is a consumable input element, make sure to consume before
		// capturing the accept state so the input index, line, and char
		// position accurately reflect the state of the interpreter at the
		// end of the token.
		if t != TokenEOF {
			this.consume(input)
		}
		if target.isAcceptState {
			this.captureSimState(this.prevAccept, input, target)
			if t == TokenEOF {
				break
			}
		}
		t = input.LA(1)
		s = target // flip current DFA target becomes Newsrc/from state
	}

	if PortDebug {
		fmt.Println("DONE WITH execATN loop")
	}
	return this.failOrAccept(this.prevAccept, input, s.configs, t)
}

// Get an existing target state for an edge in the DFA. If the target state
// for the edge has not yet been computed or is otherwise not available,
// this method returns {@code nil}.
//
// @param s The current DFA state
// @param t The next input symbol
// @return The existing target DFA state for the given input symbol
// {@code t}, or {@code nil} if the target state for this edge is not
// already cached
func (this *LexerATNSimulator) getExistingTargetState(s *DFAState, t int) *DFAState {
	if s.edges == nil || t < LexerATNSimulatorMIN_DFA_EDGE || t > LexerATNSimulatorMAX_DFA_EDGE {
		return nil
	}

	var target = s.edges[t-LexerATNSimulatorMIN_DFA_EDGE]
	if target == nil {
		target = nil
	}
	if LexerATNSimulatorDebug && target != nil {
		fmt.Println("reuse state " + strconv.Itoa(s.stateNumber) + " edge to " + strconv.Itoa(target.stateNumber))
	}
	return target
}

// Compute a target state for an edge in the DFA, and attempt to add the
// computed state and corresponding edge to the DFA.
//
// @param input The input stream
// @param s The current DFA state
// @param t The next input symbol
//
// @return The computed target DFA state for the given input symbol
// {@code t}. If {@code t} does not lead to a valid DFA state, this method
// returns {@link //ERROR}.
func (this *LexerATNSimulator) computeTargetState(input CharStream, s *DFAState, t int) *DFAState {
	var reach = NewOrderedATNConfigSet()
	// if we don't find an existing DFA state
	// Fill reach starting from closure, following t transitions
	this.getReachableConfigSet(input, s.configs, reach.BaseATNConfigSet, t)

	if len(reach.configs) == 0 { // we got nowhere on t from s
		if !reach.hasSemanticContext {
			// we got nowhere on t, don't panic out this knowledge it'd
			// cause a failover from DFA later.
			this.addDFAEdge(s, t, ATNSimulatorError, nil)
		}
		// stop when we can't Match any more char
		return ATNSimulatorError
	}
	// Add an edge from s to target DFA found/created for reach
	return this.addDFAEdge(s, t, nil, reach.BaseATNConfigSet)
}

func (this *LexerATNSimulator) failOrAccept(prevAccept *SimState, input CharStream, reach ATNConfigSet, t int) int {
	if this.prevAccept.dfaState != nil {
		var lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor
		this.accept(input, lexerActionExecutor, this.startIndex, prevAccept.index, prevAccept.line, prevAccept.column)

		if PortDebug {
			fmt.Println(prevAccept.dfaState.prediction)
		}
		return prevAccept.dfaState.prediction
	} else {
		// if no accept and EOF is first char, return EOF
		if t == TokenEOF && input.Index() == this.startIndex {
			return TokenEOF
		}
		panic(NewLexerNoViableAltException(this.recog, input, this.startIndex, reach))
	}
}

// Given a starting configuration set, figure out all ATN configurations
// we can reach upon input {@code t}. Parameter {@code reach} is a return
// parameter.
func (this *LexerATNSimulator) getReachableConfigSet(input CharStream, closure ATNConfigSet, reach ATNConfigSet, t int) {
	// this is used to skip processing for configs which have a lower priority
	// than a config that already reached an accept state for the same rule
	var skipAlt = ATNInvalidAltNumber
	for _, cfg := range closure.GetItems() {
		var currentAltReachedAcceptState = (cfg.GetAlt() == skipAlt)
		if currentAltReachedAcceptState && cfg.(*LexerATNConfig).passedThroughNonGreedyDecision {
			continue
		}
		if LexerATNSimulatorDebug {
			fmt.Printf("testing %s at %s\n", this.GetTokenName(t), cfg.String()) // this.recog, true))
		}
		for _, trans := range cfg.GetState().GetTransitions() {
			var target = this.getReachableTarget(trans, t)
			if target != nil {
				var lexerActionExecutor = cfg.(*LexerATNConfig).lexerActionExecutor
				if lexerActionExecutor != nil {
					lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.Index() - this.startIndex)
				}
				var treatEofAsEpsilon = (t == TokenEOF)
				var config = NewLexerATNConfig3(cfg.(*LexerATNConfig), target, lexerActionExecutor)
				if this.closure(input, config, reach,
					currentAltReachedAcceptState, true, treatEofAsEpsilon) {
					// any remaining configs for this alt have a lower priority
					// than the one that just reached an accept state.
					skipAlt = cfg.GetAlt()
				}
			}
		}
	}
}

func (this *LexerATNSimulator) accept(input CharStream, lexerActionExecutor *LexerActionExecutor, startIndex, index, line, charPos int) {
	if LexerATNSimulatorDebug {
		fmt.Printf("ACTION %s\n", lexerActionExecutor)
	}
	// seek to after last char in token
	input.Seek(index)
	this.line = line
	this.column = charPos
	if lexerActionExecutor != nil && this.recog != nil {
		lexerActionExecutor.execute(this.recog, input, startIndex)
	}
}

func (this *LexerATNSimulator) getReachableTarget(trans Transition, t int) ATNState {
	if trans.Matches(t, 0, 0xFFFE) {
		return trans.getTarget()
	} else {
		return nil
	}
}

func (this *LexerATNSimulator) computeStartState(input CharStream, p ATNState) *OrderedATNConfigSet {

	if PortDebug {
		fmt.Println("DEBUG" + strconv.Itoa(len(p.GetTransitions())))
	}

	var configs = NewOrderedATNConfigSet()
	for i := 0; i < len(p.GetTransitions()); i++ {
		var target = p.GetTransitions()[i].getTarget()
		var cfg = NewLexerATNConfig6(target, i+1, BasePredictionContextEMPTY)
		this.closure(input, cfg, configs.BaseATNConfigSet, false, false, false)
	}

	if PortDebug {
		fmt.Println("DEBUG" + configs.String())
	}

	return configs
}

// Since the alternatives within any lexer decision are ordered by
// preference, this method stops pursuing the closure as soon as an accept
// state is reached. After the first accept state is reached by depth-first
// search from {@code config}, all other (potentially reachable) states for
// this rule would have a lower priority.
//
// @return {@code true} if an accept state is reached, otherwise
// {@code false}.
func (this *LexerATNSimulator) closure(input CharStream, config *LexerATNConfig, configs ATNConfigSet,
	currentAltReachedAcceptState, speculative, treatEofAsEpsilon bool) bool {

	if LexerATNSimulatorDebug {
		fmt.Println("closure(" + config.String() + ")") // config.String(this.recog, true) + ")")
	}

	_, ok := config.state.(*RuleStopState)
	if ok {

		if LexerATNSimulatorDebug {
			if this.recog != nil {
				fmt.Printf("closure at %s rule stop %s\n", this.recog.GetRuleNames()[config.state.GetRuleIndex()], config)
			} else {
				fmt.Printf("closure at rule stop %s\n", config)
			}
		}

		if config.context == nil || config.context.hasEmptyPath() {
			if config.context == nil || config.context.isEmpty() {
				configs.Add(config, nil)
				return true
			} else {
				configs.Add(NewLexerATNConfig2(config, config.state, BasePredictionContextEMPTY), nil)
				currentAltReachedAcceptState = true
			}
		}
		if config.context != nil && !config.context.isEmpty() {
			for i := 0; i < config.context.length(); i++ {
				if config.context.getReturnState(i) != BasePredictionContextEMPTY_RETURN_STATE {
					var newContext = config.context.GetParent(i) // "pop" return state
					var returnState = this.atn.states[config.context.getReturnState(i)]
					cfg := NewLexerATNConfig2(config, returnState, newContext)
					currentAltReachedAcceptState = this.closure(input, cfg, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon)
				}
			}
		}
		return currentAltReachedAcceptState
	}
	// optimization
	if !config.state.GetEpsilonOnlyTransitions() {
		if !currentAltReachedAcceptState || !config.passedThroughNonGreedyDecision {
			configs.Add(config, nil)
		}
	}
	for j := 0; j < len(config.state.GetTransitions()); j++ {
		var trans = config.state.GetTransitions()[j]
		cfg := this.getEpsilonTarget(input, config, trans, configs, speculative, treatEofAsEpsilon)
		if cfg != nil {
			currentAltReachedAcceptState = this.closure(input, cfg, configs,
				currentAltReachedAcceptState, speculative, treatEofAsEpsilon)
		}
	}
	return currentAltReachedAcceptState
}

// side-effect: can alter configs.hasSemanticContext
func (this *LexerATNSimulator) getEpsilonTarget(input CharStream, config *LexerATNConfig, trans Transition,
	configs ATNConfigSet, speculative, treatEofAsEpsilon bool) *LexerATNConfig {

	var cfg *LexerATNConfig

	if trans.getSerializationType() == TransitionRULE {

		rt := trans.(*RuleTransition)
		var newContext = SingletonBasePredictionContextCreate(config.context, rt.followState.GetStateNumber())
		cfg = NewLexerATNConfig2(config, trans.getTarget(), newContext)

	} else if trans.getSerializationType() == TransitionPRECEDENCE {
		panic("Precedence predicates are not supported in lexers.")
	} else if trans.getSerializationType() == TransitionPREDICATE {
		// Track traversing semantic predicates. If we traverse,
		// we cannot add a DFA state for this "reach" computation
		// because the DFA would not test the predicate again in the
		// future. Rather than creating collections of semantic predicates
		// like v3 and testing them on prediction, v4 will test them on the
		// fly all the time using the ATN not the DFA. This is slower but
		// semantically it's not used that often. One of the key elements to
		// this predicate mechanism is not adding DFA states that see
		// predicates immediately afterwards in the ATN. For example,

		// a : ID {p1}? | ID {p2}?

		// should create the start state for rule 'a' (to save start state
		// competition), but should not create target of ID state. The
		// collection of ATN states the following ID references includes
		// states reached by traversing predicates. Since this is when we
		// test them, we cannot cash the DFA state target of ID.

		pt := trans.(*PredicateTransition)

		if LexerATNSimulatorDebug {
			fmt.Println("EVAL rule " + strconv.Itoa(trans.(*PredicateTransition).ruleIndex) + ":" + strconv.Itoa(pt.predIndex))
		}
		configs.SetHasSemanticContext(true)
		if this.evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative) {
			cfg = NewLexerATNConfig4(config, trans.getTarget())
		}
	} else if trans.getSerializationType() == TransitionACTION {
		if config.context == nil || config.context.hasEmptyPath() {
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
			var lexerActionExecutor = LexerActionExecutorappend(config.lexerActionExecutor, this.atn.lexerActions[trans.(*ActionTransition).actionIndex])
			cfg = NewLexerATNConfig3(config, trans.getTarget(), lexerActionExecutor)
		} else {
			// ignore actions in referenced rules
			cfg = NewLexerATNConfig4(config, trans.getTarget())
		}
	} else if trans.getSerializationType() == TransitionEPSILON {
		cfg = NewLexerATNConfig4(config, trans.getTarget())
	} else if trans.getSerializationType() == TransitionATOM ||
		trans.getSerializationType() == TransitionRANGE ||
		trans.getSerializationType() == TransitionSET {
		if treatEofAsEpsilon {
			if trans.Matches(TokenEOF, 0, 0xFFFF) {
				cfg = NewLexerATNConfig4(config, trans.getTarget())
			}
		}
	}
	return cfg
}

// Evaluate a predicate specified in the lexer.
//
// <p>If {@code speculative} is {@code true}, this method was called before
// {@link //consume} for the Matched character. This method should call
// {@link //consume} before evaluating the predicate to ensure position
// sensitive values, including {@link Lexer//GetText}, {@link Lexer//getLine},
// and {@link Lexer//getcolumn}, properly reflect the current
// lexer state. This method should restore {@code input} and the simulator
// to the original state before returning (i.e. undo the actions made by the
// call to {@link //consume}.</p>
//
// @param input The input stream.
// @param ruleIndex The rule containing the predicate.
// @param predIndex The index of the predicate within the rule.
// @param speculative {@code true} if the current index in {@code input} is
// one character before the predicate's location.
//
// @return {@code true} if the specified predicate evaluates to
// {@code true}.
// /
func (this *LexerATNSimulator) evaluatePredicate(input CharStream, ruleIndex, predIndex int, speculative bool) bool {
	// assume true if no recognizer was provided
	if this.recog == nil {
		return true
	}
	if !speculative {
		return this.recog.Sempred(nil, ruleIndex, predIndex)
	}
	var savedcolumn = this.column
	var savedLine = this.line
	var index = input.Index()
	var marker = input.Mark()

	defer func() {
		this.column = savedcolumn
		this.line = savedLine
		input.Seek(index)
		input.Release(marker)
	}()

	this.consume(input)
	return this.recog.Sempred(nil, ruleIndex, predIndex)
}

func (this *LexerATNSimulator) captureSimState(settings *SimState, input CharStream, dfaState *DFAState) {
	settings.index = input.Index()
	settings.line = this.line
	settings.column = this.column
	settings.dfaState = dfaState
}

func (this *LexerATNSimulator) addDFAEdge(from_ *DFAState, tk int, to *DFAState, cfgs ATNConfigSet) *DFAState {
	if to == nil && cfgs != nil {
		// leading to this call, ATNConfigSet.hasSemanticContext is used as a
		// marker indicating dynamic predicate evaluation makes this edge
		// dependent on the specific input sequence, so the static edge in the
		// DFA should be omitted. The target DFAState is still created since
		// execATN has the ability to reSynchronize with the DFA state cache
		// following the predicate evaluation step.
		//
		// TJP notes: next time through the DFA, we see a pred again and eval.
		// If that gets us to a previously created (but dangling) DFA
		// state, we can continue in pure DFA mode from there.
		// /
		var suppressEdge = cfgs.HasSemanticContext()
		cfgs.SetHasSemanticContext(false)

		to = this.addDFAState(cfgs)

		if suppressEdge {
			return to
		}
	}
	// add the edge
	if tk < LexerATNSimulatorMIN_DFA_EDGE || tk > LexerATNSimulatorMAX_DFA_EDGE {
		// Only track edges within the DFA bounds
		return to
	}
	if LexerATNSimulatorDebug {
		fmt.Println("EDGE " + from_.String() + " -> " + to.String() + " upon " + strconv.Itoa(tk))
	}
	if from_.edges == nil {
		// make room for tokens 1..n and -1 masquerading as index 0
		from_.edges = make([]*DFAState, LexerATNSimulatorMAX_DFA_EDGE-LexerATNSimulatorMIN_DFA_EDGE+1)
	}
	from_.edges[tk-LexerATNSimulatorMIN_DFA_EDGE] = to // connect

	return to
}

// Add a NewDFA state if there isn't one with this set of
// configurations already. This method also detects the first
// configuration containing an ATN rule stop state. Later, when
// traversing the DFA, we will know which rule to accept.
func (this *LexerATNSimulator) addDFAState(configs ATNConfigSet) *DFAState {

	var proposed = NewDFAState(-1, configs)
	var firstConfigWithRuleStopState ATNConfig = nil

	for _, cfg := range configs.GetItems() {

		_, ok := cfg.GetState().(*RuleStopState)

		if ok {
			firstConfigWithRuleStopState = cfg
			break
		}
	}
	if firstConfigWithRuleStopState != nil {
		proposed.isAcceptState = true
		proposed.lexerActionExecutor = firstConfigWithRuleStopState.(*LexerATNConfig).lexerActionExecutor
		proposed.setPrediction(this.atn.ruleToTokenType[firstConfigWithRuleStopState.GetState().GetRuleIndex()])
	}
	var hash = proposed.Hash()
	var dfa = this.decisionToDFA[this.mode]
	var existing = dfa.GetStates()[hash]
	if existing != nil {
		return existing
	}
	var newState = proposed
	newState.stateNumber = len(dfa.GetStates())
	configs.SetReadOnly(true)
	newState.configs = configs
	dfa.GetStates()[hash] = newState
	return newState
}

func (this *LexerATNSimulator) getDFA(mode int) *DFA {
	return this.decisionToDFA[mode]
}

// Get the text Matched so far for the current token.
func (this *LexerATNSimulator) GetText(input CharStream) string {
	// index is first lookahead char, don't include.
	return input.GetTextFromInterval(NewInterval(this.startIndex, input.Index()-1))
}

func (this *LexerATNSimulator) consume(input CharStream) {
	var curChar = input.LA(1)
	if curChar == int('\n') {
		this.line += 1
		this.column = 0
	} else {
		this.column += 1
	}
	input.Consume()
}

func (this *LexerATNSimulator) GetTokenName(tt int) string {
	if PortDebug {
		fmt.Println(tt)
	}
	if tt == -1 {
		return "EOF"
	} else {
		return "'" + string(tt) + "'"
	}
}
