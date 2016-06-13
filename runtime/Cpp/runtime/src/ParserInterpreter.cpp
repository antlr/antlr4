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

#include "dfa/DFA.h"
#include "atn/RuleStartState.h"
#include "InterpreterRuleContext.h"
#include "atn/ParserATNSimulator.h"
#include "ANTLRErrorStrategy.h"
#include "atn/LoopEndState.h"
#include "FailedPredicateException.h"
#include "atn/StarLoopEntryState.h"
#include "atn/AtomTransition.h"
#include "atn/RuleTransition.h"
#include "atn/PredicateTransition.h"
#include "atn/PrecedencePredicateTransition.h"
#include "atn/ActionTransition.h"
#include "atn/ATN.h"
#include "atn/RuleStopState.h"
#include "Token.h"
#include "Vocabulary.h"
#include "InputMismatchException.h"
#include "CommonToken.h"

#include "support/CPPUtils.h"

#include "ParserInterpreter.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

ParserInterpreter::ParserInterpreter(const std::string &grammarFileName, const std::vector<std::string>& tokenNames,
  const std::vector<std::string>& ruleNames, const atn::ATN &atn, TokenStream *input)
  : ParserInterpreter(grammarFileName, dfa::Vocabulary::fromTokenNames(tokenNames), ruleNames, atn, input) {
}

ParserInterpreter::ParserInterpreter(const std::string &grammarFileName, const dfa::Vocabulary &vocabulary,
  const std::vector<std::string> &ruleNames, const atn::ATN &atn, TokenStream *input)
  : Parser(input), _grammarFileName(grammarFileName), _atn(atn), _ruleNames(ruleNames), _vocabulary(vocabulary) {

  for (size_t i = 0; i < atn.maxTokenType; ++i) {
    _tokenNames.push_back(vocabulary.getDisplayName(i));
  }

  // init decision DFA
  for (int i = 0; i < atn.getNumberOfDecisions(); ++i) {
    atn::DecisionState *decisionState = atn.getDecisionState(i);
    _decisionToDFA.push_back(dfa::DFA(decisionState, i));
  }

  // get atn simulator that knows how to do predictions
  _interpreter = new atn::ParserATNSimulator(this, atn, _decisionToDFA, _sharedContextCache); /* mem-check: deleted in d-tor */
}

ParserInterpreter::~ParserInterpreter() {
  delete _interpreter;
}

void ParserInterpreter::reset() {
  Parser::reset();
  _overrideDecisionReached = false;
  _overrideDecisionRoot = nullptr;
}

const atn::ATN& ParserInterpreter::getATN() const {
  return _atn;
}

const std::vector<std::string>& ParserInterpreter::getTokenNames() const {
  return _tokenNames;
}

const dfa::Vocabulary& ParserInterpreter::getVocabulary() const {
  return _vocabulary;
}

const std::vector<std::string>& ParserInterpreter::getRuleNames() const {
  return _ruleNames;
}

std::string ParserInterpreter::getGrammarFileName() const {
  return _grammarFileName;
}

Ref<ParserRuleContext> ParserInterpreter::parse(int startRuleIndex) {
  atn::RuleStartState *startRuleStartState = _atn.ruleToStartState[(size_t)startRuleIndex];

  _rootContext = createInterpreterRuleContext(std::weak_ptr<ParserRuleContext>(), atn::ATNState::INVALID_STATE_NUMBER, startRuleIndex);
  
  if (startRuleStartState->isLeftRecursiveRule) {
    enterRecursionRule(_rootContext, startRuleStartState->stateNumber, startRuleIndex, 0);
  } else {
    enterRule(_rootContext, startRuleStartState->stateNumber, startRuleIndex);
  }

  while (true) {
    atn::ATNState *p = getATNState();
    switch (p->getStateType()) {
      case atn::ATNState::RULE_STOP :
        // pop; return from rule
        if (_ctx->isEmpty()) {
          if (startRuleStartState->isLeftRecursiveRule) {
            Ref<ParserRuleContext> result = _ctx;
            auto parentContext = _parentContextStack.top();
            _parentContextStack.pop();
            unrollRecursionContexts(parentContext.first);
            return result;
          } else {
            exitRule();
            return _rootContext;
          }
        }
        
        visitRuleStopState(p);
        break;

      default :
        try {
          visitState(p);
        }
        catch (RecognitionException &e) {
          setState(_atn.ruleToStopState[p->ruleIndex]->stateNumber);
          getErrorHandler()->reportError(this, e);
          getContext()->exception = std::current_exception();
          recover(e);
        }
        
        break;
    }
  }
}

void ParserInterpreter::enterRecursionRule(Ref<ParserRuleContext> const& localctx, int state, int ruleIndex, int precedence) {
  _parentContextStack.push({ _ctx, localctx->invokingState });
  Parser::enterRecursionRule(localctx, state, ruleIndex, precedence);
}

void ParserInterpreter::addDecisionOverride(int decision, int tokenIndex, int forcedAlt) {
  _overrideDecision = decision;
  _overrideDecisionInputIndex = tokenIndex;
  _overrideDecisionAlt = forcedAlt;
}

Ref<InterpreterRuleContext> ParserInterpreter::getOverrideDecisionRoot() const {
  return _overrideDecisionRoot;
}

Ref<InterpreterRuleContext> ParserInterpreter::getRootContext() {
  return _rootContext;
}

atn::ATNState* ParserInterpreter::getATNState() {
  return _atn.states[(size_t)getState()];
}

void ParserInterpreter::visitState(atn::ATNState *p) {
  int predictedAlt = 1;
  if (is<DecisionState *>(p)) {
    predictedAlt = visitDecisionState(dynamic_cast<DecisionState *>(p));
  }

  atn::Transition *transition = p->transition(predictedAlt - 1);
  switch (transition->getSerializationType()) {
    case atn::Transition::EPSILON:
      if (p->getStateType() == ATNState::STAR_LOOP_ENTRY &&
        (dynamic_cast<StarLoopEntryState *>(p))->isPrecedenceDecision &&
        !is<LoopEndState *>(transition->target)) {
        // We are at the start of a left recursive rule's (...)* loop
        // and we're not taking the exit branch of loop.
        Ref<InterpreterRuleContext> localctx = createInterpreterRuleContext(_parentContextStack.top().first,
          _parentContextStack.top().second, (int)_ctx->getRuleIndex());
        pushNewRecursionContext(localctx, _atn.ruleToStartState[p->ruleIndex]->stateNumber, (int)_ctx->getRuleIndex());
      }
      break;

    case atn::Transition::ATOM:
      match((int)((atn::AtomTransition*)(transition))->_label);
      break;

    case atn::Transition::RANGE:
    case atn::Transition::SET:
    case atn::Transition::NOT_SET:
      if (!transition->matches((int)_input->LA(1), Token::MIN_USER_TOKEN_TYPE, 65535)) {
        recoverInline();
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
      Ref<InterpreterRuleContext> newctx = createInterpreterRuleContext(_ctx, p->stateNumber, ruleIndex);
      if (ruleStartState->isLeftRecursiveRule) {
        enterRecursionRule(newctx, ruleStartState->stateNumber, ruleIndex, ((atn::RuleTransition*)(transition))->precedence);
      } else {
        enterRule(newctx, transition->target->stateNumber, ruleIndex);
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

int ParserInterpreter::visitDecisionState(DecisionState *p) {
  int predictedAlt = 1;
  if (p->getNumberOfTransitions() > 1) {
    getErrorHandler()->sync(this);
    int decision = p->decision;
    if (decision == _overrideDecision && (int)_input->index() == _overrideDecisionInputIndex && !_overrideDecisionReached) {
      predictedAlt = _overrideDecisionAlt;
      _overrideDecisionReached = true;
    } else {
      predictedAlt = getInterpreter<ParserATNSimulator>()->adaptivePredict(_input, decision, _ctx);
    }
  }
  return predictedAlt;
}

Ref<InterpreterRuleContext> ParserInterpreter::createInterpreterRuleContext(std::weak_ptr<ParserRuleContext> parent,
  int invokingStateNumber, int ruleIndex) {
  return std::make_shared<InterpreterRuleContext>(parent, invokingStateNumber, ruleIndex);
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

void ParserInterpreter::recover(RecognitionException &e) {
  size_t i = _input->index();
  getErrorHandler()->recover(this, std::make_exception_ptr(e));

  if (_input->index() == i) {
    // no input consumed, better add an error node
    if (is<InputMismatchException *>(&e)) {
      InputMismatchException &ime = (InputMismatchException&)e;
      Token *tok = e.getOffendingToken();
      int expectedTokenType = ime.getExpectedTokens().getMinElement(); // get any element
      _errorToken = getTokenFactory()->create({ tok->getTokenSource(), tok->getTokenSource()->getInputStream() },
        expectedTokenType, tok->getText(), Token::DEFAULT_CHANNEL, -1, -1, // invalid start/stop
        tok->getLine(), tok->getCharPositionInLine());
      _ctx->addErrorNode(_errorToken.get());
    }
    else { // NoViableAlt
      Token *tok = e.getOffendingToken();
      _errorToken = getTokenFactory()->create({ tok->getTokenSource(), tok->getTokenSource()->getInputStream() },
        Token::INVALID_TYPE, tok->getText(), Token::DEFAULT_CHANNEL, -1, -1, // invalid start/stop
        tok->getLine(), tok->getCharPositionInLine());
      _ctx->addErrorNode(_errorToken.get());
    }
  }
}

Token* ParserInterpreter::recoverInline() {
  return _errHandler->recoverInline(this);
}
