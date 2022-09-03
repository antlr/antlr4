/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Token from '../Token.js';
import ATN from './ATN.js';
import ATNType from './ATNType.js';

import ATNState from '../state/ATNState.js';
import BasicState from '../state/BasicState.js';
import DecisionState from '../state/DecisionState.js';
import BlockStartState from '../state/BlockStartState.js';
import BlockEndState from '../state/BlockEndState.js';
import LoopEndState from '../state/LoopEndState.js';
import RuleStartState from '../state/RuleStartState.js';
import RuleStopState from '../state/RuleStopState.js';
import TokensStartState from '../state/TokensStartState.js';
import PlusLoopbackState from '../state/PlusLoopbackState.js';
import StarLoopbackState from '../state/StarLoopbackState.js';
import StarLoopEntryState from '../state/StarLoopEntryState.js';
import PlusBlockStartState from '../state/PlusBlockStartState.js';
import StarBlockStartState from '../state/StarBlockStartState.js';
import BasicBlockStartState from '../state/BasicBlockStartState.js';

import Transition from '../transition/Transition.js';
import AtomTransition from '../transition/AtomTransition.js';
import SetTransition from '../transition/SetTransition.js';
import NotSetTransition from '../transition/NotSetTransition.js';
import RuleTransition from '../transition/RuleTransition.js';
import RangeTransition from '../transition/RangeTransition.js';
import ActionTransition from '../transition/ActionTransition.js';
import EpsilonTransition from '../transition/EpsilonTransition.js';
import WildcardTransition from '../transition/WildcardTransition.js';
import PredicateTransition from '../transition/PredicateTransition.js';
import PrecedencePredicateTransition from '../transition/PrecedencePredicateTransition.js';


import IntervalSet from '../misc/IntervalSet.js';
import ATNDeserializationOptions from './ATNDeserializationOptions.js';

import LexerActionType from './LexerActionType.js';
import LexerSkipAction from '../action/LexerSkipAction.js';
import LexerChannelAction from '../action/LexerChannelAction.js';
import LexerCustomAction from '../action/LexerCustomAction.js';
import LexerMoreAction from '../action/LexerMoreAction.js';
import LexerTypeAction from '../action/LexerTypeAction.js';
import LexerPushModeAction from '../action/LexerPushModeAction.js';
import LexerPopModeAction from '../action/LexerPopModeAction.js';
import LexerModeAction from '../action/LexerModeAction.js';

const SERIALIZED_VERSION = 4;

function initArray( length, value) {
	const tmp = [];
	tmp[length-1] = value;
	return tmp.map(function(i) {return value;});
}

export default class ATNDeserializer {

    constructor(options) {
        if ( options=== undefined || options === null ) {
            options = ATNDeserializationOptions.defaultOptions;
        }
        this.deserializationOptions = options;
        this.stateFactories = null;
        this.actionFactories = null;
    }

    deserialize(data) {
        const legacy = this.reset(data);
        this.checkVersion(legacy);
        if(legacy)
            this.skipUUID();
        const atn = this.readATN();
        this.readStates(atn, legacy);
        this.readRules(atn, legacy);
        this.readModes(atn);
        const sets = [];
        this.readSets(atn, sets, this.readInt.bind(this));
        if(legacy)
            this.readSets(atn, sets, this.readInt32.bind(this));
        this.readEdges(atn, sets);
        this.readDecisions(atn);
        this.readLexerActions(atn, legacy);
        this.markPrecedenceDecisions(atn);
        this.verifyATN(atn);
        if (this.deserializationOptions.generateRuleBypassTransitions && atn.grammarType === ATNType.PARSER ) {
            this.generateRuleBypassTransitions(atn);
            // re-verify after modification
            this.verifyATN(atn);
        }
        return atn;
    }

    reset(data) {
        const version = data.charCodeAt ? data.charCodeAt(0) : data[0];
        if(version === SERIALIZED_VERSION - 1) {
            const adjust = function (c) {
                const v = c.charCodeAt(0);
                return v > 1 ? v - 2 : v + 65534;
            };
            const temp = data.split("").map(adjust);
            // don't adjust the first value since that's the version number
            temp[0] = data.charCodeAt(0);
            this.data = temp;
            this.pos = 0;
            return true;
        } else {
            this.data = data
            this.pos = 0;
            return false;
        }
    }

    skipUUID() {
        let count = 0;
        while(count++ < 8)
            this.readInt();
    }

    checkVersion(legacy) {
        const version = this.readInt();
        if ( !legacy && version !== SERIALIZED_VERSION ) {
            throw ("Could not deserialize ATN with version " + version + " (expected " + SERIALIZED_VERSION + ").");
        }
    }

    readATN() {
        const grammarType = this.readInt();
        const maxTokenType = this.readInt();
        return new ATN(grammarType, maxTokenType);
    }

    readStates(atn, legacy) {
        let j, pair, stateNumber;
        const  loopBackStateNumbers = [];
        const  endStateNumbers = [];
        const  nstates = this.readInt();
        for(let i=0; i<nstates; i++) {
            const  stype = this.readInt();
            // ignore bad type of states
            if (stype===ATNState.INVALID_TYPE) {
                atn.addState(null);
                continue;
            }
            let ruleIndex = this.readInt();
            if (legacy && ruleIndex === 0xFFFF) {
                ruleIndex = -1;
            }
            const  s = this.stateFactory(stype, ruleIndex);
            if (stype === ATNState.LOOP_END) { // special case
                const  loopBackStateNumber = this.readInt();
                loopBackStateNumbers.push([s, loopBackStateNumber]);
            } else if(s instanceof BlockStartState) {
                const  endStateNumber = this.readInt();
                endStateNumbers.push([s, endStateNumber]);
            }
            atn.addState(s);
        }
        // delay the assignment of loop back and end states until we know all the
        // state instances have been initialized
        for (j=0; j<loopBackStateNumbers.length; j++) {
            pair = loopBackStateNumbers[j];
            pair[0].loopBackState = atn.states[pair[1]];
        }

        for (j=0; j<endStateNumbers.length; j++) {
            pair = endStateNumbers[j];
            pair[0].endState = atn.states[pair[1]];
        }

        let numNonGreedyStates = this.readInt();
        for (j=0; j<numNonGreedyStates; j++) {
            stateNumber = this.readInt();
            atn.states[stateNumber].nonGreedy = true;
        }

        let numPrecedenceStates = this.readInt();
        for (j=0; j<numPrecedenceStates; j++) {
            stateNumber = this.readInt();
            atn.states[stateNumber].isPrecedenceRule = true;
        }
    }

    readRules(atn, legacy) {
        let i;
        const nrules = this.readInt();
        if (atn.grammarType === ATNType.LEXER ) {
            atn.ruleToTokenType = initArray(nrules, 0);
        }
        atn.ruleToStartState = initArray(nrules, 0);
        for (i=0; i<nrules; i++) {
            const s = this.readInt();
            atn.ruleToStartState[i] = atn.states[s];
            if ( atn.grammarType === ATNType.LEXER ) {
                let tokenType = this.readInt();
                if (legacy && tokenType === 0xFFFF) {
                    tokenType = Token.EOF;
                }
                atn.ruleToTokenType[i] = tokenType;
            }
        }
        atn.ruleToStopState = initArray(nrules, 0);
        for (i=0; i<atn.states.length; i++) {
            const state = atn.states[i];
            if (!(state instanceof RuleStopState)) {
                continue;
            }
            atn.ruleToStopState[state.ruleIndex] = state;
            atn.ruleToStartState[state.ruleIndex].stopState = state;
        }
    }

    readModes(atn) {
        const nmodes = this.readInt();
        for (let i=0; i<nmodes; i++) {
            let s = this.readInt();
            atn.modeToStartState.push(atn.states[s]);
        }
    }

    readSets(atn, sets, reader) {
        const m = this.readInt();
        for (let i=0; i<m; i++) {
            const iset = new IntervalSet();
            sets.push(iset);
            const n = this.readInt();
            const containsEof = this.readInt();
            if (containsEof!==0) {
                iset.addOne(-1);
            }
            for (let j=0; j<n; j++) {
                const i1 = reader();
                const i2 = reader();
                iset.addRange(i1, i2);
            }
        }
    }

    readEdges(atn, sets) {
        let i, j, state, trans, target;
        const nedges = this.readInt();
        for (i=0; i<nedges; i++) {
            const src = this.readInt();
            const trg = this.readInt();
            const ttype = this.readInt();
            const arg1 = this.readInt();
            const arg2 = this.readInt();
            const arg3 = this.readInt();
            trans = this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
            const srcState = atn.states[src];
            srcState.addTransition(trans);
        }
        // edges for rule stop states can be derived, so they aren't serialized
        for (i=0; i<atn.states.length; i++) {
            state = atn.states[i];
            for (j=0; j<state.transitions.length; j++) {
                const t = state.transitions[j];
                if (!(t instanceof RuleTransition)) {
                    continue;
                }
                let outermostPrecedenceReturn = -1;
                if (atn.ruleToStartState[t.target.ruleIndex].isPrecedenceRule) {
                    if (t.precedence === 0) {
                        outermostPrecedenceReturn = t.target.ruleIndex;
                    }
                }

                trans = new EpsilonTransition(t.followState, outermostPrecedenceReturn);
                atn.ruleToStopState[t.target.ruleIndex].addTransition(trans);
            }
        }

        for (i=0; i<atn.states.length; i++) {
            state = atn.states[i];
            if (state instanceof BlockStartState) {
                // we need to know the end state to set its start state
                if (state.endState === null) {
                    throw ("IllegalState");
                }
                // block end states can only be associated to a single block start
                // state
                if ( state.endState.startState !== null) {
                    throw ("IllegalState");
                }
                state.endState.startState = state;
            }
            if (state instanceof PlusLoopbackState) {
                for (j=0; j<state.transitions.length; j++) {
                    target = state.transitions[j].target;
                    if (target instanceof PlusBlockStartState) {
                        target.loopBackState = state;
                    }
                }
            } else if (state instanceof StarLoopbackState) {
                for (j=0; j<state.transitions.length; j++) {
                    target = state.transitions[j].target;
                    if (target instanceof StarLoopEntryState) {
                        target.loopBackState = state;
                    }
                }
            }
        }
    }

    readDecisions(atn) {
        const ndecisions = this.readInt();
        for (let i=0; i<ndecisions; i++) {
            const s = this.readInt();
            const decState = atn.states[s];
            atn.decisionToState.push(decState);
            decState.decision = i;
        }
    }

    readLexerActions(atn, legacy) {
        if (atn.grammarType === ATNType.LEXER) {
            const count = this.readInt();
            atn.lexerActions = initArray(count, null);
            for (let i=0; i<count; i++) {
                const actionType = this.readInt();
                let data1 = this.readInt();
                if (legacy && data1 === 0xFFFF) {
                    data1 = -1;
                }
                let data2 = this.readInt();
                if (legacy && data2 === 0xFFFF) {
                    data2 = -1;
                }
                atn.lexerActions[i] = this.lexerActionFactory(actionType, data1, data2);
            }
        }
    }

    generateRuleBypassTransitions(atn) {
        let i;
        const count = atn.ruleToStartState.length;
        for(i=0; i<count; i++) {
            atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
        }
        for(i=0; i<count; i++) {
            this.generateRuleBypassTransition(atn, i);
        }
    }

    generateRuleBypassTransition(atn, idx) {
        let i, state;
        const bypassStart = new BasicBlockStartState();
        bypassStart.ruleIndex = idx;
        atn.addState(bypassStart);

        const bypassStop = new BlockEndState();
        bypassStop.ruleIndex = idx;
        atn.addState(bypassStop);

        bypassStart.endState = bypassStop;
        atn.defineDecisionState(bypassStart);

        bypassStop.startState = bypassStart;

        let excludeTransition = null;
        let endState = null;

        if (atn.ruleToStartState[idx].isPrecedenceRule) {
            // wrap from the beginning of the rule to the StarLoopEntryState
            endState = null;
            for(i=0; i<atn.states.length; i++) {
                state = atn.states[i];
                if (this.stateIsEndStateFor(state, idx)) {
                    endState = state;
                    excludeTransition = state.loopBackState.transitions[0];
                    break;
                }
            }
            if (excludeTransition === null) {
                throw ("Couldn't identify final state of the precedence rule prefix section.");
            }
        } else {
            endState = atn.ruleToStopState[idx];
        }

        // all non-excluded transitions that currently target end state need to
        // target blockEnd instead
        for(i=0; i<atn.states.length; i++) {
            state = atn.states[i];
            for(let j=0; j<state.transitions.length; j++) {
                const transition = state.transitions[j];
                if (transition === excludeTransition) {
                    continue;
                }
                if (transition.target === endState) {
                    transition.target = bypassStop;
                }
            }
        }

        // all transitions leaving the rule start state need to leave blockStart
        // instead
        const ruleToStartState = atn.ruleToStartState[idx];
        const count = ruleToStartState.transitions.length;
        while ( count > 0) {
            bypassStart.addTransition(ruleToStartState.transitions[count-1]);
            ruleToStartState.transitions = ruleToStartState.transitions.slice(-1);
        }
        // link the new states
        atn.ruleToStartState[idx].addTransition(new EpsilonTransition(bypassStart));
        bypassStop.addTransition(new EpsilonTransition(endState));

        const matchState = new BasicState();
        atn.addState(matchState);
        matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[idx]));
        bypassStart.addTransition(new EpsilonTransition(matchState));
    }

    stateIsEndStateFor(state, idx) {
        if ( state.ruleIndex !== idx) {
            return null;
        }
        if (!( state instanceof StarLoopEntryState)) {
            return null;
        }
        const maybeLoopEndState = state.transitions[state.transitions.length - 1].target;
        if (!( maybeLoopEndState instanceof LoopEndState)) {
            return null;
        }
        if (maybeLoopEndState.epsilonOnlyTransitions &&
            (maybeLoopEndState.transitions[0].target instanceof RuleStopState)) {
            return state;
        } else {
            return null;
        }
    }

    /**
     * Analyze the {@link StarLoopEntryState} states in the specified ATN to set
     * the {@link StarLoopEntryState//isPrecedenceDecision} field to the
     * correct value.
     * @param atn The ATN.
     */
    markPrecedenceDecisions(atn) {
        for(let i=0; i<atn.states.length; i++) {
            const state = atn.states[i];
            if (!( state instanceof StarLoopEntryState)) {
                continue;
            }
            // We analyze the ATN to determine if this ATN decision state is the
            // decision for the closure block that determines whether a
            // precedence rule should continue or complete.
            if ( atn.ruleToStartState[state.ruleIndex].isPrecedenceRule) {
                const maybeLoopEndState = state.transitions[state.transitions.length - 1].target;
                if (maybeLoopEndState instanceof LoopEndState) {
                    if ( maybeLoopEndState.epsilonOnlyTransitions &&
                            (maybeLoopEndState.transitions[0].target instanceof RuleStopState)) {
                        state.isPrecedenceDecision = true;
                    }
                }
            }
        }
    }

    verifyATN(atn) {
        if (!this.deserializationOptions.verifyATN) {
            return;
        }
        // verify assumptions
        for(let i=0; i<atn.states.length; i++) {
            const state = atn.states[i];
            if (state === null) {
                continue;
            }
            this.checkCondition(state.epsilonOnlyTransitions || state.transitions.length <= 1);
            if (state instanceof PlusBlockStartState) {
                this.checkCondition(state.loopBackState !== null);
            } else  if (state instanceof StarLoopEntryState) {
                this.checkCondition(state.loopBackState !== null);
                this.checkCondition(state.transitions.length === 2);
                if (state.transitions[0].target instanceof StarBlockStartState) {
                    this.checkCondition(state.transitions[1].target instanceof LoopEndState);
                    this.checkCondition(!state.nonGreedy);
                } else if (state.transitions[0].target instanceof LoopEndState) {
                    this.checkCondition(state.transitions[1].target instanceof StarBlockStartState);
                    this.checkCondition(state.nonGreedy);
                } else {
                    throw("IllegalState");
                }
            } else if (state instanceof StarLoopbackState) {
                this.checkCondition(state.transitions.length === 1);
                this.checkCondition(state.transitions[0].target instanceof StarLoopEntryState);
            } else if (state instanceof LoopEndState) {
                this.checkCondition(state.loopBackState !== null);
            } else if (state instanceof RuleStartState) {
                this.checkCondition(state.stopState !== null);
            } else if (state instanceof BlockStartState) {
                this.checkCondition(state.endState !== null);
            } else if (state instanceof BlockEndState) {
                this.checkCondition(state.startState !== null);
            } else if (state instanceof DecisionState) {
                this.checkCondition(state.transitions.length <= 1 || state.decision >= 0);
            } else {
                this.checkCondition(state.transitions.length <= 1 || (state instanceof RuleStopState));
            }
        }
    }

    checkCondition(condition, message) {
        if (!condition) {
            if (message === undefined || message===null) {
                message = "IllegalState";
            }
            throw (message);
        }
    }

    readInt() {
        return this.data[this.pos++];
    }

    readInt32() {
        const low = this.readInt();
        const high = this.readInt();
        return low | (high << 16);
    }

    edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets) {
        const target = atn.states[trg];
        switch(type) {
        case Transition.EPSILON:
            return new EpsilonTransition(target);
        case Transition.RANGE:
            return arg3 !== 0 ? new RangeTransition(target, Token.EOF, arg2) : new RangeTransition(target, arg1, arg2);
        case Transition.RULE:
            return new RuleTransition(atn.states[arg1], arg2, arg3, target);
        case Transition.PREDICATE:
            return new PredicateTransition(target, arg1, arg2, arg3 !== 0);
        case Transition.PRECEDENCE:
            return new PrecedencePredicateTransition(target, arg1);
        case Transition.ATOM:
            return arg3 !== 0 ? new AtomTransition(target, Token.EOF) : new AtomTransition(target, arg1);
        case Transition.ACTION:
            return new ActionTransition(target, arg1, arg2, arg3 !== 0);
        case Transition.SET:
            return new SetTransition(target, sets[arg1]);
        case Transition.NOT_SET:
            return new NotSetTransition(target, sets[arg1]);
        case Transition.WILDCARD:
            return new WildcardTransition(target);
        default:
            throw "The specified transition type: " + type + " is not valid.";
        }
    }

    stateFactory(type, ruleIndex) {
        if (this.stateFactories === null) {
            const sf = [];
            sf[ATNState.INVALID_TYPE] = null;
            sf[ATNState.BASIC] = () => new BasicState();
            sf[ATNState.RULE_START] = () => new RuleStartState();
            sf[ATNState.BLOCK_START] = () => new BasicBlockStartState();
            sf[ATNState.PLUS_BLOCK_START] = () => new PlusBlockStartState();
            sf[ATNState.STAR_BLOCK_START] = () => new StarBlockStartState();
            sf[ATNState.TOKEN_START] = () => new TokensStartState();
            sf[ATNState.RULE_STOP] = () => new RuleStopState();
            sf[ATNState.BLOCK_END] = () => new BlockEndState();
            sf[ATNState.STAR_LOOP_BACK] = () => new StarLoopbackState();
            sf[ATNState.STAR_LOOP_ENTRY] = () => new StarLoopEntryState();
            sf[ATNState.PLUS_LOOP_BACK] = () => new PlusLoopbackState();
            sf[ATNState.LOOP_END] = () => new LoopEndState();
            this.stateFactories = sf;
        }
        if (type>this.stateFactories.length || this.stateFactories[type] === null) {
            throw("The specified state type " + type + " is not valid.");
        } else {
            const s = this.stateFactories[type]();
            if (s!==null) {
                s.ruleIndex = ruleIndex;
                return s;
            }
        }
    }

    lexerActionFactory(type, data1, data2) {
        if (this.actionFactories === null) {
            const af = [];
            af[LexerActionType.CHANNEL] = (data1, data2) => new LexerChannelAction(data1);
            af[LexerActionType.CUSTOM] = (data1, data2) => new LexerCustomAction(data1, data2);
            af[LexerActionType.MODE] = (data1, data2) => new LexerModeAction(data1);
            af[LexerActionType.MORE] = (data1, data2) => LexerMoreAction.INSTANCE;
            af[LexerActionType.POP_MODE] = (data1, data2) => LexerPopModeAction.INSTANCE;
            af[LexerActionType.PUSH_MODE] = (data1, data2) => new LexerPushModeAction(data1);
            af[LexerActionType.SKIP] = (data1, data2) => LexerSkipAction.INSTANCE;
            af[LexerActionType.TYPE] = (data1, data2) => new LexerTypeAction(data1);
            this.actionFactories = af;
        }
        if (type>this.actionFactories.length || this.actionFactories[type] === null) {
            throw("The specified lexer action type " + type + " is not valid.");
        } else {
            return this.actionFactories[type](data1, data2);
        }
    }
}

