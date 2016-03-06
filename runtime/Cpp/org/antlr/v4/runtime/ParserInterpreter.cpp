#include <deque>

#include "ParserInterpreter.h"
#include "ATN.h"
#include "DFA.h"
#include "RuleStartState.h"
#include "InterpreterRuleContext.h"
#include "ParserATNSimulator.h"
#include "DecisionState.h"
#include "Transition.h"
#include "ANTLRErrorStrategy.h"
#include "Token.h"
#include "AtomTransition.h"
#include "ActionTransition.h"
#include "Exceptions.h"
#include "RuleTransition.h"
#include "PrecedencePredicateTransition.h"
#include "PredicateTransition.h"
#include "LoopEndState.h"
#include "FailedPredicateException.h"
#include "RuleStartState.h"
#include "StarLoopEntryState.h"
#include "PredictionContextCache.h"
#include "IntervalSet.h"

/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {

                ParserInterpreter::ParserInterpreter(const std::wstring &grammarFileName, const std::vector<std::wstring>& tokenNames, const std::vector<std::wstring>& ruleNames, atn::ATN *atn, TokenStream *input) : Parser(_input), grammarFileName(grammarFileName), _tokenNames(tokenNames), atn(atn), _ruleNames(ruleNames), pushRecursionContextStates(new antlrcpp::BitSet()), sharedContextCache(new atn::PredictionContextCache()), _parentContextStack(new std::deque<std::pair<ParserRuleContext *, int>*>()) {
              

                    for (int i = 0; i < atn->getNumberOfDecisions(); i++) {
                        _decisionToDFA.emplace_back(atn->getDecisionState(i), i);
                    }

                    // identify the ATN states where pushNewRecursionContext must be called
                    for (auto state : atn->states) {
                        if (!(dynamic_cast<atn::StarLoopEntryState*>(state) != nullptr)) {
                            continue;
                        }

                        atn::RuleStartState *ruleStartState = atn->ruleToStartState[state->ruleIndex];
                        if (!ruleStartState->isPrecedenceRule) {
                            continue;
                        }

                        atn::ATNState *maybeLoopEndState = state->transition(state->getNumberOfTransitions() - 1)->target;
                        if (!(dynamic_cast<atn::LoopEndState*>(maybeLoopEndState) != nullptr)) {
                            continue;
                        }

                        if (maybeLoopEndState->epsilonOnlyTransitions && dynamic_cast<atn::RuleStopState*>(maybeLoopEndState->transition(0)->target) != nullptr) {
                            this->pushRecursionContextStates->set(state->stateNumber);
                        }
                    }

                    // get atn simulator that knows how to do predictions
                    setInterpreter(new atn::ParserATNSimulator(this, atn, _decisionToDFA, sharedContextCache));
                }

                org::antlr::v4::runtime::atn::ATN *ParserInterpreter::getATN() {
                    return atn;
                }

                const std::vector<std::wstring>& ParserInterpreter::getTokenNames() {
                    return _tokenNames;
                }

                const std::vector<std::wstring>& ParserInterpreter::getRuleNames() {
                    return _ruleNames;
                }

                std::wstring ParserInterpreter::getGrammarFileName() {
                    return grammarFileName;
                }

                ParserRuleContext *ParserInterpreter::parse(int startRuleIndex) {
                    atn::RuleStartState *startRuleStartState = atn->ruleToStartState[startRuleIndex];

                    InterpreterRuleContext *rootContext = new InterpreterRuleContext(nullptr, atn::ATNState::INVALID_STATE_NUMBER, startRuleIndex);
                    if (startRuleStartState->isPrecedenceRule) {
                        enterRecursionRule(rootContext, startRuleStartState->stateNumber, startRuleIndex, 0);
                    } else {
                        enterRule(rootContext, startRuleStartState->stateNumber, startRuleIndex);
                    }

                    while (true) {
                        atn::ATNState *p = getATNState();
                        switch (p->getStateType()) {
                            case atn::ATNState::RULE_STOP :
                            // pop; return from rule
                            if (_ctx->isEmpty()) {
                                exitRule();
                                return rootContext;
                            }

                            visitRuleStopState(p);
                            break;

                        default :
                            visitState(p);
                            break;
                        }
                    }
                }

                void ParserInterpreter::enterRecursionRule(ParserRuleContext *localctx, int state, int ruleIndex, int precedence) {
                    _parentContextStack->push_back(new std::pair<ParserRuleContext*, int>(_ctx, localctx->invokingState));
                    Parser::enterRecursionRule(localctx, state, ruleIndex, precedence);
                }

                atn::ATNState *ParserInterpreter::getATNState() {
                    return atn->states.at(getState());
                }

                void ParserInterpreter::visitState(atn::ATNState *p) {
                    int edge;
                    if (p->getNumberOfTransitions() > 1) {
                        edge = getInterpreter()->adaptivePredict(_input, ((atn::DecisionState*)p)->decision, _ctx);
                    } else {
                        edge = 1;
                    }

                    atn::Transition *transition = p->transition(edge - 1);
                    switch (transition->getSerializationType()) {
                        case atn::Transition::EPSILON:
                            if (pushRecursionContextStates->data[p->stateNumber] == 1 && !(dynamic_cast<atn::LoopEndState*>(transition->target) != nullptr)) {
                            InterpreterRuleContext *ctx = new InterpreterRuleContext(_parentContextStack->front()->first, _parentContextStack->front()->second, _ctx->getRuleIndex());
                            pushNewRecursionContext(ctx, atn->ruleToStartState[p->ruleIndex]->stateNumber, _ctx->getRuleIndex());
                        }
                        break;

                        case atn::Transition::ATOM:
                            match(((atn::AtomTransition*)(transition))->_label);
                        break;

                        case atn::Transition::RANGE:
                        case atn::Transition::SET:
                        case atn::Transition::NOT_SET:
                            if (!transition->matches(_input->LA(1), Token::MIN_USER_TOKEN_TYPE, 65535)) {
                                _errHandler->recoverInline(this);
                            }
                            matchWildcard();
                        break;

                        case atn::Transition::WILDCARD:
                            matchWildcard();
                        break;

                        case atn::Transition::RULE:
                        {
                            atn::RuleStartState *ruleStartState = (atn::RuleStartState*)(transition->target);
                            int ruleIndex = ruleStartState->ruleIndex;
                            InterpreterRuleContext *ctx = new InterpreterRuleContext(_ctx, p->stateNumber, ruleIndex);
                            if (ruleStartState->isPrecedenceRule) {
                                enterRecursionRule(ctx, ruleStartState->stateNumber, ruleIndex, ((atn::RuleTransition*)(transition))->precedence);
                            } else {
                                enterRule(ctx, transition->target->stateNumber, ruleIndex);
                            }
                        }
                        break;
                            
                        case atn::Transition::PREDICATE:
                        {
                            atn::PredicateTransition *predicateTransition = (atn::PredicateTransition*)(transition);
                            if (!sempred(_ctx, predicateTransition->ruleIndex, predicateTransition->predIndex)) {
                                throw new FailedPredicateException(this);
                            }
                        }
                            break;
                        
                        case atn::Transition::ACTION:
                        {
                            atn::ActionTransition *actionTransition = (atn::ActionTransition*)(transition);
                            action(_ctx, actionTransition->ruleIndex, actionTransition->actionIndex);
                        }
                            break;
                            
                        case atn::Transition::PRECEDENCE:
                        {
                            if (!precpred(_ctx, ((atn::PrecedencePredicateTransition*)(transition))->precedence)) {
                                throw new FailedPredicateException(this, L"precpred(_ctx, " + std::to_wstring(((atn::PrecedencePredicateTransition*)(transition))->precedence) +  L")");
                            }
                        }
                            break;
                            
                        default:
                            throw UnsupportedOperationException(L"Unrecognized ATN transition type.");
                    }
                    
                    setState(transition->target->stateNumber);
                }

                void ParserInterpreter::visitRuleStopState(atn::ATNState *p) {
                    atn::RuleStartState *ruleStartState = atn->ruleToStartState[p->ruleIndex];
                    if (ruleStartState->isPrecedenceRule) {
                        std::pair<ParserRuleContext*, int> *parentContext = _parentContextStack->back(); // Dan - make sure this is equivalent
                        _parentContextStack->pop_back();
                        unrollRecursionContexts(parentContext->first);
                        setState(parentContext->second);
                    } else {
                        exitRule();
                    }

                    atn::RuleTransition *ruleTransition = static_cast<atn::RuleTransition*>(atn->states.at(getState())->transition(0));
                    setState(ruleTransition->followState->stateNumber);
                }
            }
        }
    }
}
