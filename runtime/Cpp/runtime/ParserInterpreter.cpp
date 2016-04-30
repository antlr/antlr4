/*
 * [The "BSD license"]
 * Copyright (c) 2016 Mike Lischke
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

#include "DFA.h"
#include "RuleStartState.h"
#include "InterpreterRuleContext.h"
#include "ParserATNSimulator.h"
#include "ANTLRErrorStrategy.h"
#include "LoopEndState.h"
#include "FailedPredicateException.h"
#include "StarLoopEntryState.h"
#include "AtomTransition.h"
#include "RuleTransition.h"
#include "PredicateTransition.h"
#include "PrecedencePredicateTransition.h"
#include "ActionTransition.h"
#include "ATN.h"
#include "RuleStopState.h"
#include "Token.h"
#include "CPPUtils.h"

#include "ParserInterpreter.h"

using namespace org::antlr::v4::runtime;
using namespace antlrcpp;

ParserInterpreter::ParserInterpreter(const std::wstring &grammarFileName, const std::vector<std::wstring>& tokenNames,
  const std::vector<std::wstring>& ruleNames, const atn::ATN &atn, TokenStream *input)
  : Parser(input), _grammarFileName(grammarFileName), _tokenNames(tokenNames), _atn(atn), _ruleNames(ruleNames) {

  for (int i = 0; i < _atn.getNumberOfDecisions(); i++) {
    _decisionToDFA.push_back(dfa::DFA(_atn.getDecisionState(i), i));
  }

  // identify the ATN states where pushNewRecursionContext must be called
  for (auto state : _atn.states) {
    if (!is<atn::StarLoopEntryState*>(state)) {
      continue;
    }

    atn::RuleStartState *ruleStartState = _atn.ruleToStartState[(size_t)state->ruleIndex];
    if (!ruleStartState->isLeftRecursiveRule) {
      continue;
    }

    atn::ATNState *maybeLoopEndState = state->transition(state->getNumberOfTransitions() - 1)->target;
    if (!is<atn::LoopEndState*>(maybeLoopEndState)) {
      continue;
    }

    if (maybeLoopEndState->epsilonOnlyTransitions && is<atn::RuleStopState*>(maybeLoopEndState->transition(0)->target)) {
      _pushRecursionContextStates.set((size_t)state->stateNumber);
    }
  }

  // get atn simulator that knows how to do predictions
  _interpreter = new atn::ParserATNSimulator(this, atn, _decisionToDFA, _sharedContextCache); /* mem-check: deleted in d-tor */
}

ParserInterpreter::~ParserInterpreter() {
  delete _interpreter;
}

const atn::ATN& ParserInterpreter::getATN() const {
  return _atn;
}

const std::vector<std::wstring>& ParserInterpreter::getTokenNames() const {
  return _tokenNames;
}

const std::vector<std::wstring>& ParserInterpreter::getRuleNames() const {
  return _ruleNames;
}

std::wstring ParserInterpreter::getGrammarFileName() const {
  return _grammarFileName;
}

Ref<ParserRuleContext> ParserInterpreter::parse(int startRuleIndex) {
  atn::RuleStartState *startRuleStartState = _atn.ruleToStartState[(size_t)startRuleIndex];

  Ref<InterpreterRuleContext> rootContext =
    std::make_shared<InterpreterRuleContext>(std::weak_ptr<ParserRuleContext>(), atn::ATNState::INVALID_STATE_NUMBER, startRuleIndex);
  
  if (startRuleStartState->isLeftRecursiveRule) {
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

void ParserInterpreter::enterRecursionRule(Ref<ParserRuleContext> localctx, int state, int ruleIndex, int precedence) {
  _parentContextStack.push({ _ctx, localctx->invokingState });
  Parser::enterRecursionRule(localctx, state, ruleIndex, precedence);
}

atn::ATNState *ParserInterpreter::getATNState() {
  return _atn.states[(size_t)getState()];
}

void ParserInterpreter::visitState(atn::ATNState *p) {
  int edge;
  if (p->getNumberOfTransitions() > 1) {
    edge = getInterpreter<atn::ParserATNSimulator>()->adaptivePredict(_input, ((atn::DecisionState*)p)->decision, _ctx);
  } else {
    edge = 1;
  }

  atn::Transition *transition = p->transition((size_t)edge - 1);
  switch (transition->getSerializationType()) {
    case atn::Transition::EPSILON:
      if (_pushRecursionContextStates[(size_t)p->stateNumber] == 1 && is<atn::LoopEndState*>(transition->target)) {
        Ref<InterpreterRuleContext> ruleContext = std::make_shared<InterpreterRuleContext>(_parentContextStack.top().first,
          _parentContextStack.top().second, _ctx->getRuleIndex());
        pushNewRecursionContext(ruleContext, _atn.ruleToStartState[(size_t)p->ruleIndex]->stateNumber,
          (int)ruleContext->getRuleIndex());
      }
      break;

    case atn::Transition::ATOM:
      match(((atn::AtomTransition*)(transition))->_label);
      break;

    case atn::Transition::RANGE:
    case atn::Transition::SET:
    case atn::Transition::NOT_SET:
      if (!transition->matches((int)_input->LA(1), Token::MIN_USER_TOKEN_TYPE, 65535)) {
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
      Ref<InterpreterRuleContext> ruleContext = std::make_shared<InterpreterRuleContext>(_ctx, p->stateNumber, ruleIndex);
      if (ruleStartState->isLeftRecursiveRule) {
        enterRecursionRule(ruleContext, ruleStartState->stateNumber, ruleIndex, ((atn::RuleTransition*)(transition))->precedence);
      } else {
        enterRule(_ctx, transition->target->stateNumber, ruleIndex);
      }
    }
      break;

    case atn::Transition::PREDICATE:
    {
      atn::PredicateTransition *predicateTransition = (atn::PredicateTransition*)(transition);
      if (!sempred(_ctx, predicateTransition->ruleIndex, predicateTransition->predIndex)) {
        throw FailedPredicateException(this);
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
        throw FailedPredicateException(this, "precpred(_ctx, " + std::to_string(((atn::PrecedencePredicateTransition*)(transition))->precedence) +  ")");
      }
    }
      break;

    default:
      throw UnsupportedOperationException("Unrecognized ATN transition type.");
  }

  setState(transition->target->stateNumber);
}

void ParserInterpreter::visitRuleStopState(atn::ATNState *p) {
  atn::RuleStartState *ruleStartState = _atn.ruleToStartState[(size_t)p->ruleIndex];
  if (ruleStartState->isLeftRecursiveRule) {
    std::pair<Ref<ParserRuleContext>, int> parentContext = _parentContextStack.top();
    _parentContextStack.pop();

    unrollRecursionContexts(parentContext.first);
    setState(parentContext.second);
  } else {
    exitRule();
  }

  atn::RuleTransition *ruleTransition = static_cast<atn::RuleTransition*>(_atn.states[(size_t)getState()]->transition(0));
  setState(ruleTransition->followState->stateNumber);
}
