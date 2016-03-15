#include <utility>
#include <assert.h>

#include "LexerATNSimulator.h"
#include "IntStream.h"
#include "OrderedATNConfigSet.h"
#include "Token.h"
#include "LexerNoViableAltException.h"
#include "PredictionContext.h"
#include "RuleStopState.h"
#include "RuleTransition.h"
#include "SingletonPredictionContext.h"
#include "PredicateTransition.h"
#include "ActionTransition.h"
#include "ATNConfig.h"
#include "Interval.h"
#include "DFA.h"
#include "Lexer.h"
#include "ATN.h"
#include "DFAState.h"
#include "LexerATNConfig.h"
#include "LexerNoViableAltException.h"
#include "Exceptions.h"

/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {

                    void LexerATNSimulator::SimState::reset() {
                        index = -1;
                        line = 0;
                        charPos = -1;
                        // TODO: Memory Management - delete
                        delete dfaState;
                    }

                    void LexerATNSimulator::SimState::InitializeInstanceFields() {
                        index = -1;
                        line = 0;
                        charPos = -1;
                    }

                    int LexerATNSimulator::match_calls = 0;


                    LexerATNSimulator::LexerATNSimulator(ATN *atn, std::vector<dfa::DFA*> decisionToDFA, PredictionContextCache *sharedContextCache) : prevAccept(new SimState()), recog(nullptr) {
                    }

                    LexerATNSimulator::LexerATNSimulator(Lexer *recog, ATN *atn, std::vector<dfa::DFA*> decisionToDFA, PredictionContextCache *sharedContextCache) : ATNSimulator(atn,sharedContextCache), recog(recog), _decisionToDFA(decisionToDFA), prevAccept(new SimState()) {
                        InitializeInstanceFields();
                    }

                    void LexerATNSimulator::copyState(LexerATNSimulator *simulator) {
                        this->charPositionInLine = simulator->charPositionInLine;
                        this->line = simulator->line;
                        this->mode = simulator->mode;
                        this->startIndex = simulator->startIndex;
                    }

                    int LexerATNSimulator::match(CharStream *input, int mode) {
                        match_calls++;
                        this->mode = mode;
                        int mark = input->mark();
                        try {
                            this->startIndex = input->index();
                            this->prevAccept->reset();
                            dfa::DFA *dfa = _decisionToDFA[mode];
                            if (dfa->s0 == nullptr) {
                                return matchATN(input);
                            } else {
                                return execATN(input, dfa->s0);
                            }
                        }
                        catch(...) {
                            input->release(mark);
                        }
                        return -1;
                    }

                    void LexerATNSimulator::reset() {
                        prevAccept->reset();
                        startIndex = -1;
                        line = 1;
                        charPositionInLine = 0;
                        mode = Lexer::DEFAULT_MODE;
                    }

                    int LexerATNSimulator::matchATN(CharStream *input) {
                        ATNState *startState = (ATNState *)atn->modeToStartState->at(mode);

                        if (debug) {
                            std::wcout << L"matchATN mode" << mode << L" start: " << startState << std::endl;
                        }

                        int old_mode = mode;

                        ATNConfigSet *s0_closure = computeStartState(input, startState);
                        bool suppressEdge = s0_closure->hasSemanticContext;
                        s0_closure->hasSemanticContext = false;

                        dfa::DFAState *next = addDFAState(s0_closure);
                        if (!suppressEdge) {
                            _decisionToDFA[mode]->s0 = next;
                        }

                        int predict = execATN(input, next);

                        if (debug) {
                            std::wcout << L"DFA after matchATN: " << _decisionToDFA[old_mode]->toLexerString() << std::endl;
                        }

                        return predict;
                    }

                    int LexerATNSimulator::execATN(CharStream *input, dfa::DFAState *ds0) {
                        //System.out.println("enter exec index "+input.index()+" from "+ds0.configs);
                        if (debug) {
                            std::wcout << L"start state closure=" << ds0->configs << std::endl;
                        }

                        int t = input->LA(1);
                        dfa::DFAState *s = ds0; // s is current/from DFA state

                        while (true) { // while more work
                            if (debug) {
                                std::wcout << L"execATN loop starting closure: " << s->configs << std::endl;
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
                            dfa::DFAState *target = getExistingTargetState(s, t);
                            if (target == nullptr) {
                                target = computeTargetState(input, s, t);
                            }

                            if (target == ERROR) {
                                break;
                            }

                            if (target->isAcceptState) {
                                captureSimState(prevAccept, input, target);
                                if (t == IntStream::_EOF) {
                                    break;
                                }
                            }

                            if (t != IntStream::_EOF) {
                                consume(input);
                                t = input->LA(1);
                            }

                            s = target; // flip; current DFA target becomes new src/from state
                        }

                        return failOrAccept(prevAccept, input, s->configs, t);
                    }

                    dfa::DFAState *LexerATNSimulator::getExistingTargetState(dfa::DFAState *s, int t) {
                        if (s->edges.size() == 0 || t < MIN_DFA_EDGE || t > MAX_DFA_EDGE) {
                            return nullptr;
                        }

                        dfa::DFAState *target = s->edges[t - MIN_DFA_EDGE];
                        if (debug && target != nullptr) {
                            std::wcout << std::wstring(L"reuse state ") << s->stateNumber << std::wstring(L" edge to ") << target->stateNumber << std::endl;
                        }

                        return target;
                    }

                    dfa::DFAState *LexerATNSimulator::computeTargetState(CharStream *input, dfa::DFAState *s, int t) {
                        ATNConfigSet *reach = new OrderedATNConfigSet();

                        // if we don't find an existing DFA state
                        // Fill reach starting from closure, following t transitions
                        getReachableConfigSet(input, s->configs, reach, t);

                        if (reach->isEmpty()) { // we got nowhere on t from s
                            // we got nowhere on t, don't throw out this knowledge; it'd
                            // cause a failover from DFA later.
                            addDFAEdge(s, t, ERROR);
                            // stop when we can't match any more char
                            return ERROR;
                        }

                        // Add an edge from s to target DFA found/created for reach
                        return addDFAEdge(s, t, reach);
                    }

                    int LexerATNSimulator::failOrAccept(SimState *prevAccept, CharStream *input, ATNConfigSet *reach, int t) {
                        if (prevAccept->dfaState != nullptr) {
                            int ruleIndex = prevAccept->dfaState->lexerRuleIndex;
                            int actionIndex = prevAccept->dfaState->lexerActionIndex;
                            accept(input, ruleIndex, actionIndex, prevAccept->index, prevAccept->line, prevAccept->charPos);
                            return prevAccept->dfaState->prediction;
                        } else {
                            // if no accept and EOF is first char, return EOF
                            if (t == IntStream::_EOF && input->index() == startIndex) {
                                return Token::_EOF;
                            }

                            throw LexerNoViableAltException(recog, input, startIndex, reach);
                        }
                    }

                    void LexerATNSimulator::getReachableConfigSet(CharStream *input, ATNConfigSet *closure, ATNConfigSet *reach, int t) {
                        // this is used to skip processing for configs which have a lower priority
                        // than a config that already reached an accept state for the same rule
                        int skipAlt = ATN::INVALID_ALT_NUMBER;
                        for (auto c : *closure) {
                            bool currentAltReachedAcceptState = c->alt == skipAlt;
                            if (currentAltReachedAcceptState && (static_cast<LexerATNConfig*>(c))->hasPassedThroughNonGreedyDecision()) {
                                continue;
                            }

                            if (debug) {
                                std::wcout << L"testing " << getTokenName(t) << " at " <<c->toString(recog, true) << std::endl;
                            }

                            int n = c->state->getNumberOfTransitions();
                            for (int ti = 0; ti < n; ti++) { // for each transition
                                Transition *trans = c->state->transition(ti);
                                ATNState *target = getReachableTarget(trans, t);
                                if (target != nullptr) {
                                    if (this->closure(input, new LexerATNConfig(static_cast<LexerATNConfig*>(c), target), reach, currentAltReachedAcceptState, true)) {
                                        // any remaining configs for this alt have a lower priority than
                                        // the one that just reached an accept state.
                                        skipAlt = c->alt;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    void LexerATNSimulator::accept(CharStream *input, int ruleIndex, int actionIndex, int index, int line, int charPos) {
                        if (debug) {
                            std::wcout << L"ACTION ";
                            if (recog != nullptr) {
                                std::wcout << recog->getRuleNames()[ruleIndex];
                            } else {
                                std::wcout << ruleIndex;
                            }
                            std::wcout << ":" << actionIndex << std::endl;
                        }

                        if (actionIndex >= 0 && recog != nullptr) {
                            recog->action(nullptr, ruleIndex, actionIndex);
                        }

                        // seek to after last char in token
                        input->seek(index);
                        this->line = line;
                        this->charPositionInLine = charPos;
                        if (input->LA(1) != IntStream::_EOF) {
                            consume(input);
                        }
                    }

                    atn::ATNState *LexerATNSimulator::getReachableTarget(Transition *trans, int t) {
                        if (trans->matches(t, WCHAR_MIN, WCHAR_MAX)) {
                            return trans->target;
                        }

                        return nullptr;
                    }

                    atn::ATNConfigSet *LexerATNSimulator::computeStartState(CharStream *input, ATNState *p) {
                        EmptyPredictionContext * initialContext  = PredictionContext::EMPTY;
                        ATNConfigSet *configs = new OrderedATNConfigSet();
                        for (int i = 0; i < p->getNumberOfTransitions(); i++) {
                            ATNState *target = p->transition(i)->target;
                            LexerATNConfig *c = new LexerATNConfig(target, i + 1, (PredictionContext*)initialContext);
                            closure(input, c, configs, false, false);
                        }
                        return configs;
                    }

                    bool LexerATNSimulator::closure(CharStream *input, LexerATNConfig *config, ATNConfigSet *configs, bool currentAltReachedAcceptState, bool speculative) {
                        if (debug) {
                            std::wcout << L"closure(" << config->toString(recog, true) << L")" << std::endl;
                        }

                        if (dynamic_cast<RuleStopState*>(config->state) != nullptr) {
                            if (debug) {
                                if (recog != nullptr) {
                                    std::wcout << L"closure at " << recog->getRuleNames()[config->state->ruleIndex] << L" rule stop " << config << std::endl;
                                } else {
                                    std::wcout << L"closure at rule stop " << config << std::endl;
                                }
                            }

                            if (config->context == nullptr || config->context->hasEmptyPath()) {
                                if (config->context == nullptr || config->context->isEmpty()) {
                                    configs->add(config);
                                    return true;
                                } else {
                                    configs->add(new LexerATNConfig(config, config->state, (PredictionContext*)PredictionContext::EMPTY));
                                    currentAltReachedAcceptState = true;
                                }
                            }

                            if (config->context != nullptr && !config->context->isEmpty()) {
                                for (int i = 0; i < config->context->size(); i++) {
                                    if (config->context->getReturnState(i) != PredictionContext::EMPTY_RETURN_STATE) {
                                        PredictionContext *newContext = config->context->getParent(i); // "pop" return state
                                        ATNState *returnState = atn->states[config->context->getReturnState(i)];
                                        LexerATNConfig *c = new LexerATNConfig(returnState, config->alt, newContext);
                                        currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState, speculative);
                                    }
                                }
                            }

                            return currentAltReachedAcceptState;
                        }

                        // optimization
                        if (!config->state->onlyHasEpsilonTransitions()) {
                            if (!currentAltReachedAcceptState || !config->hasPassedThroughNonGreedyDecision()) {
                                configs->add(config);
                            }
                        }

                        ATNState *p = config->state;
                        for (int i = 0; i < p->getNumberOfTransitions(); i++) {
                            Transition *t = p->transition(i);
                            LexerATNConfig *c = getEpsilonTarget(input, config, t, configs, speculative);
                            if (c != nullptr) {
                                currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState, speculative);
                            }
                        }

                        return currentAltReachedAcceptState;
                    }

                    atn::LexerATNConfig *LexerATNSimulator::getEpsilonTarget(CharStream *input, LexerATNConfig *config, Transition *t, ATNConfigSet *configs, bool speculative) {
                        LexerATNConfig *c = nullptr;
                        switch (t->getSerializationType()) {
                            case Transition::RULE: {
                                RuleTransition *ruleTransition = static_cast<RuleTransition*>(t);
                                PredictionContext *newContext = SingletonPredictionContext::create(config->context, ruleTransition->followState->stateNumber);
                                c = new LexerATNConfig(config, t->target, newContext);
                            }
                                break;

                            case Transition::PRECEDENCE:
                            //{
                                throw new UnsupportedOperationException(L"Precedence predicates are not supported in lexers.");
                            //}
                                break;

                            case Transition::PREDICATE: {
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
                                PredicateTransition *pt = static_cast<PredicateTransition*>(t);
                                if (debug) {
                                    std::wcout << L"EVAL rule " << pt->ruleIndex << L":" << pt->predIndex << std::endl;
                                }
                                configs->hasSemanticContext = true;
                                if (evaluatePredicate(input, pt->ruleIndex, pt->predIndex, speculative)) {
                                    c = new LexerATNConfig(config, t->target);
                                }
                            }
                                break;
                            // ignore actions; just exec one per rule upon accept
                            case Transition::ACTION:
                                c = new LexerATNConfig(config, t->target, (static_cast<ActionTransition*>(t))->actionIndex);
                                break;
                            case Transition::EPSILON:
                                c = new LexerATNConfig(config, t->target);
                                break;
                        }

                        return c;
                    }

                    bool LexerATNSimulator::evaluatePredicate(CharStream *input, int ruleIndex, int predIndex, bool speculative) {
                        // assume true if no recognizer was provided
                        if (recog == nullptr) {
                            return true;
                        }

                        if (!speculative) {
                            return recog->sempred(nullptr, ruleIndex, predIndex);
                        }

                        int savedCharPositionInLine = charPositionInLine;
                        int savedLine = line;
                        int index = input->index();
                        int marker = input->mark();
                        try {
                            consume(input);
                            return recog->sempred(nullptr, ruleIndex, predIndex);
                        } catch(...) {
                            charPositionInLine = savedCharPositionInLine;
                            line = savedLine;
                            input->seek(index);
                            input->release(marker);
                        }
                        return false;
                    }

                    void LexerATNSimulator::captureSimState(SimState *settings, CharStream *input, dfa::DFAState *dfaState) {
                        settings->index = input->index();
                        settings->line = line;
                        settings->charPos = charPositionInLine;
                        settings->dfaState = dfaState;
                    }

                    dfa::DFAState *LexerATNSimulator::addDFAEdge(dfa::DFAState *from, int t, ATNConfigSet *q) {
                        /* leading to this call, ATNConfigSet.hasSemanticContext is used as a
                         * marker indicating dynamic predicate evaluation makes this edge
                         * dependent on the specific input sequence, so the static edge in the
                         * DFA should be omitted. The target DFAState is still created since
                         * execATN has the ability to resynchronize with the DFA state cache
                         * following the predicate evaluation step.
                         *
                         * TJP notes: next time through the DFA, we see a pred again and eval.
                         * If that gets us to a previously created (but dangling) DFA
                         * state, we can continue in pure DFA mode from there.
                         */
                        bool suppressEdge = q->hasSemanticContext;
                        q->hasSemanticContext = false;

                        dfa::DFAState *to = addDFAState(q);

                        if (suppressEdge) {
                            return to;
                        }

                        addDFAEdge(from, t, to);
                        return to;
                    }

                    void LexerATNSimulator::addDFAEdge(dfa::DFAState *p, int t, dfa::DFAState *q) {
                        if (t < MIN_DFA_EDGE || t > MAX_DFA_EDGE) {
                            // Only track edges within the DFA bounds
                            return;
                        }

                        if (debug) {
                            std::wcout << std::wstring(L"EDGE ") << p << std::wstring(L" -> ") << q << std::wstring(L" upon ") << (static_cast<wchar_t>(t)) << std::endl;
                        }

                        if(true) {
							std::lock_guard<std::mutex> lck(mtx);
                            if (p->edges.empty()) {
                                //  make room for tokens 1..n and -1 masquerading as index 0
								p->edges = std::vector<dfa::DFAState*>(MAX_DFA_EDGE - MIN_DFA_EDGE + 1);
                            }
                            p->edges[t - MIN_DFA_EDGE] = q; // connect
                        }
                    }

                    dfa::DFAState *LexerATNSimulator::addDFAState(ATNConfigSet *configs) {
                        /* the lexer evaluates predicates on-the-fly; by this point configs
                         * should not contain any configurations with unevaluated predicates.
                         */
                        assert(!configs->hasSemanticContext);

                        dfa::DFAState *proposed = new dfa::DFAState(configs);
                        ATNConfig *firstConfigWithRuleStopState = nullptr;
                        for (auto c : *configs) {
                            if (dynamic_cast<RuleStopState*>(c->state) != nullptr) {
                                firstConfigWithRuleStopState = c;
                                break;
                            }
                        }

                        if (firstConfigWithRuleStopState != nullptr) {
                            proposed->isAcceptState = true;
                            proposed->lexerRuleIndex = firstConfigWithRuleStopState->state->ruleIndex;
                            proposed->lexerActionIndex = (static_cast<LexerATNConfig*>(firstConfigWithRuleStopState))->lexerActionIndex;
                            proposed->prediction = atn->ruleToTokenType[proposed->lexerRuleIndex];
                        }

                        dfa::DFA *dfa = _decisionToDFA[mode];

                        if(true) {
							std::lock_guard<std::mutex> lck(mtx);
                            dfa::DFAState *existing = dfa->states->at(proposed);
                            if (existing != nullptr) {
                                return existing;
                            }

                            dfa::DFAState *newState = proposed;

                            newState->stateNumber = (int)dfa->states->size();
                            configs->setReadonly(true);
                            newState->configs = configs;
                            dfa->states->emplace(newState, newState);
                            return newState;
                        }                       
                    }

                    dfa::DFA *LexerATNSimulator::getDFA(int mode) {
                        return _decisionToDFA[mode];
                    }

                    std::wstring LexerATNSimulator::getText(CharStream *input) {
                        // index is first lookahead char, don't include.
                        return input->getText(misc::Interval::of(startIndex, input->index() - 1));
                    }

                    int LexerATNSimulator::getLine() {
                        return line;
                    }

                    void LexerATNSimulator::setLine(int line) {
                        this->line = line;
                    }

                    int LexerATNSimulator::getCharPositionInLine() {
                        return charPositionInLine;
                    }

                    void LexerATNSimulator::setCharPositionInLine(int charPositionInLine) {
                        this->charPositionInLine = charPositionInLine;
                    }

                    void LexerATNSimulator::consume(CharStream *input) {
                        int curChar = input->LA(1);
                        if (curChar == L'\n') {
                            line++;
                            charPositionInLine = 0;
                        } else {
                            charPositionInLine++;
                        }
                        input->consume();
                    }

                    std::wstring LexerATNSimulator::getTokenName(int t) {
                        if (t == -1) {
                            return L"EOF";
                        }
                        //if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
                        return std::wstring(L"'") + static_cast<wchar_t>(t) + std::wstring(L"'");
                    }

                    void LexerATNSimulator::InitializeInstanceFields() {
                        startIndex = -1;
                        line = 1;
                        charPositionInLine = 0;
                        mode = org::antlr::v4::runtime::Lexer::DEFAULT_MODE;
                    }
                }
            }
        }
    }
}
