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

#include "NoViableAltException.h"
#include "IntervalSet.h"
#include "ParserATNSimulator.h"
#include "InputMismatchException.h"
#include "FailedPredicateException.h"
#include "ParserRuleContext.h"
#include "RuleTransition.h"
#include "ATN.h"
#include "ATNState.h"
#include "Parser.h"
#include "Strings.h"
#include "CommonToken.h"
#include "Vocabulary.h"

#include "DefaultErrorStrategy.h"

using namespace org::antlr::v4::runtime;
using namespace antlrcpp;

void DefaultErrorStrategy::reset(Parser *recognizer) {
  endErrorCondition(recognizer);
}

void DefaultErrorStrategy::beginErrorCondition(Parser */*recognizer*/) {
  errorRecoveryMode = true;
}

bool DefaultErrorStrategy::inErrorRecoveryMode(Parser */*recognizer*/) {
  return errorRecoveryMode;
}

void DefaultErrorStrategy::endErrorCondition(Parser */*recognizer*/) {
  errorRecoveryMode = false;
  lastErrorIndex = -1;
}

void DefaultErrorStrategy::reportMatch(Parser *recognizer) {
  endErrorCondition(recognizer);
}

void DefaultErrorStrategy::reportError(Parser *recognizer, const RecognitionException &e) {
  // if we've already reported an error and have not matched a token
  // yet successfully, don't report any errors.
  if (inErrorRecoveryMode(recognizer)) {
    return; // don't report spurious errors
  }

  beginErrorCondition(recognizer);
  if (is<NoViableAltException>(e)) {
    reportNoViableAlternative(recognizer, (NoViableAltException&)e);
  } else if (is<const InputMismatchException>(e)) {
    reportInputMismatch(recognizer, (InputMismatchException&)e);
  } else if (is<const FailedPredicateException>(e)) {
    reportFailedPredicate(recognizer, (FailedPredicateException&)e);
  } else {

    // This is really bush league, I hate libraries that gratuitiously print stuff out.
    std::cerr << std::string("unknown recognition error type: ") << typeid(e).name() << std::endl;

    recognizer->notifyErrorListeners(e.getOffendingToken(), antlrcpp::s2ws(e.what()), std::make_exception_ptr(e));
  }
}

void DefaultErrorStrategy::recover(Parser *recognizer, const RecognitionException &/*e*/) {
  if (lastErrorIndex == (int)recognizer->getInputStream()->index() &&
      lastErrorStates.contains(recognizer->getState())) {

    // uh oh, another error at same token index and previously-visited
    // state in ATN; must be a case where LT(1) is in the recovery
    // token set so nothing got consumed. Consume a single token
    // at least to prevent an infinite loop; this is a failsafe.
    recognizer->consume();
  }
  lastErrorIndex = (int)recognizer->getInputStream()->index();
  lastErrorStates.add(recognizer->getState());
  misc::IntervalSet followSet = getErrorRecoverySet(recognizer);
  consumeUntil(recognizer, followSet);
}

void DefaultErrorStrategy::sync(Parser *recognizer) {
  atn::ATNState *s = recognizer->getInterpreter<atn::ATNSimulator>()->atn.states[(size_t)recognizer->getState()];

  // If already recovering, don't try to sync
  if (inErrorRecoveryMode(recognizer)) {
    return;
  }

  TokenStream *tokens = recognizer->getTokenStream();
  ssize_t la = tokens->LA(1);

  // try cheaper subset first; might get lucky. seems to shave a wee bit off
  if (recognizer->getATN().nextTokens(s).contains((int)la) || la == Token::EOF) {
    return;
  }

  // Return but don't end recovery. only do that upon valid token match
  if (recognizer->isExpectedToken((int)la)) {
    return;
  }

  switch (s->getStateType()) {
    case atn::ATNState::BLOCK_START:
    case atn::ATNState::STAR_BLOCK_START:
    case atn::ATNState::PLUS_BLOCK_START:
    case atn::ATNState::STAR_LOOP_ENTRY:
      // report error and recover if possible
      if (singleTokenDeletion(recognizer) != nullptr) {
        return;
      }

      throw InputMismatchException(recognizer);

    case atn::ATNState::PLUS_LOOP_BACK:
    case atn::ATNState::STAR_LOOP_BACK: {
      //			System.err.println("at loop back: "+s.getClass().getSimpleName());
      reportUnwantedToken(recognizer);
      misc::IntervalSet expecting = recognizer->getExpectedTokens();
      misc::IntervalSet whatFollowsLoopIterationOrRule = expecting.Or(getErrorRecoverySet(recognizer));
      consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
    }
      break;

    default:
      // do nothing if we can't identify the exact kind of ATN state
      break;
  }
}

void DefaultErrorStrategy::reportNoViableAlternative(Parser *recognizer, const NoViableAltException &e) {
  TokenStream *tokens = recognizer->getTokenStream();
  std::wstring input;
  if (tokens != nullptr) {
    if (e.getStartToken()->getType() == Token::EOF) {
      input = L"<EOF>";
    } else {
      input = tokens->getText(e.getStartToken(), e.getOffendingToken());
    }
  } else {
    input = L"<unknown input>";
  }
  std::wstring msg = std::wstring(L"no viable alternative at input ") + escapeWSAndQuote(input);
  recognizer->notifyErrorListeners(e.getOffendingToken(), msg, std::make_exception_ptr(e));
}

void DefaultErrorStrategy::reportInputMismatch(Parser *recognizer, const InputMismatchException &e) {
  std::wstring msg = std::wstring(L"mismatched input ") + getTokenErrorDisplay(e.getOffendingToken()) +
  std::wstring(L" expecting ") + e.getExpectedTokens().toString(recognizer->getVocabulary());
  recognizer->notifyErrorListeners(e.getOffendingToken(), msg, std::make_exception_ptr(e));
}

void DefaultErrorStrategy::reportFailedPredicate(Parser *recognizer, const FailedPredicateException &e) {
  const std::wstring& ruleName = recognizer->getRuleNames()[(size_t)recognizer->getContext()->getRuleIndex()];
  std::wstring msg = std::wstring(L"rule ") + ruleName + std::wstring(L" ") + antlrcpp::s2ws(e.what());
  recognizer->notifyErrorListeners(e.getOffendingToken(), msg, std::make_exception_ptr(e));
}

void DefaultErrorStrategy::reportUnwantedToken(Parser *recognizer) {
  if (inErrorRecoveryMode(recognizer)) {
    return;
  }

  beginErrorCondition(recognizer);

  Ref<Token> t = recognizer->getCurrentToken();
  std::wstring tokenName = getTokenErrorDisplay(t);
  misc::IntervalSet expecting = getExpectedTokens(recognizer);

  std::wstring msg = std::wstring(L"extraneous input ") + tokenName + std::wstring(L" expecting ") +
    expecting.toString(recognizer->getVocabulary());
  recognizer->notifyErrorListeners(t, msg, nullptr);
}

void DefaultErrorStrategy::reportMissingToken(Parser *recognizer) {
  if (inErrorRecoveryMode(recognizer)) {
    return;
  }

  beginErrorCondition(recognizer);

  Ref<Token> t = recognizer->getCurrentToken();
  misc::IntervalSet expecting = getExpectedTokens(recognizer);
  std::wstring msg = L"missing " + expecting.toString(recognizer->getVocabulary()) + L" at " + getTokenErrorDisplay(t);

  recognizer->notifyErrorListeners(t, msg, nullptr);
}

Ref<Token> DefaultErrorStrategy::recoverInline(Parser *recognizer) {
  // SINGLE TOKEN DELETION
  Ref<Token> matchedSymbol = singleTokenDeletion(recognizer);
  if (matchedSymbol) {
    // we have deleted the extra token.
    // now, move past ttype token as if all were ok
    recognizer->consume();
    return matchedSymbol;
  }

  // SINGLE TOKEN INSERTION
  if (singleTokenInsertion(recognizer)) {
    return getMissingSymbol(recognizer);
  }

  // even that didn't work; must throw the exception
  throw InputMismatchException(recognizer);
}

bool DefaultErrorStrategy::singleTokenInsertion(Parser *recognizer) {
  ssize_t currentSymbolType = recognizer->getInputStream()->LA(1);

  // if current token is consistent with what could come after current
  // ATN state, then we know we're missing a token; error recovery
  // is free to conjure up and insert the missing token
  atn::ATNState *currentState = recognizer->getInterpreter<atn::ATNSimulator>()->atn.states[(size_t)recognizer->getState()];
  atn::ATNState *next = currentState->transition(0)->target;
  const atn::ATN &atn = recognizer->getInterpreter<atn::ATNSimulator>()->atn;
  misc::IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer->getContext());
  if (expectingAtLL2.contains((int)currentSymbolType)) {
    reportMissingToken(recognizer);
    return true;
  }
  return false;
}

Ref<Token> DefaultErrorStrategy::singleTokenDeletion(Parser *recognizer) {
  ssize_t nextTokenType = recognizer->getInputStream()->LA(2);
  misc::IntervalSet expecting = getExpectedTokens(recognizer);
  if (expecting.contains((int)nextTokenType)) {
    reportUnwantedToken(recognizer);
    recognizer->consume(); // simply delete extra token
                           // we want to return the token we're actually matching
    Ref<Token> matchedSymbol = recognizer->getCurrentToken();
    reportMatch(recognizer); // we know current token is correct
    return matchedSymbol;
  }
  return nullptr;
}

Ref<Token> DefaultErrorStrategy::getMissingSymbol(Parser *recognizer) {
  Ref<Token> currentSymbol = recognizer->getCurrentToken();
  misc::IntervalSet expecting = getExpectedTokens(recognizer);
  ssize_t expectedTokenType = expecting.getMinElement(); // get any element
  std::wstring tokenText;
  if (expectedTokenType == Token::EOF) {
    tokenText = L"<missing EOF>";
  } else {
    tokenText = L"<missing " + recognizer->getVocabulary()->getDisplayName(expectedTokenType) + L">";
  }
  Ref<Token> current = currentSymbol;
  Ref<Token> lookback = recognizer->getTokenStream()->LT(-1);
  if (current->getType() == Token::EOF && lookback != nullptr) {
    current = lookback;
  }
  return std::dynamic_pointer_cast<Token>(recognizer->getTokenFactory()->create({ current->getTokenSource(),
    current->getTokenSource()->getInputStream() }, (int)expectedTokenType, tokenText, Token::DEFAULT_CHANNEL, -1, -1,
    current->getLine(), current->getCharPositionInLine()));
}

misc::IntervalSet DefaultErrorStrategy::getExpectedTokens(Parser *recognizer) {
  return recognizer->getExpectedTokens();
}

std::wstring DefaultErrorStrategy::getTokenErrorDisplay(Ref<Token> t) {
  if (t == nullptr) {
    return L"<no Token>";
  }
  std::wstring s = getSymbolText(t);
  if (s == L"") {
    if (getSymbolType(t) == Token::EOF) {
      s = L"<EOF>";
    } else {
      s = std::wstring(L"<") + std::to_wstring(getSymbolType(t)) + std::wstring(L">");
    }
  }
  return escapeWSAndQuote(s);
}

std::wstring DefaultErrorStrategy::getSymbolText(Ref<Token> symbol) {
  return symbol->getText();
}

int DefaultErrorStrategy::getSymbolType(Ref<Token> symbol) {
  return symbol->getType();
}

std::wstring DefaultErrorStrategy::escapeWSAndQuote(std::wstring &s) {
  //		if ( s==null ) return s;
  antlrcpp::replaceAll(s, L"\n", L"\\n");
  antlrcpp::replaceAll(s, L"\r",L"\\r");
  antlrcpp::replaceAll(s, L"\t",L"\\t");
  return std::wstring(L"'") + s + std::wstring(L"'");
}

misc::IntervalSet DefaultErrorStrategy::getErrorRecoverySet(Parser *recognizer) {
  const atn::ATN &atn = recognizer->getInterpreter<atn::ATNSimulator>()->atn;
  Ref<RuleContext> ctx = recognizer->getContext();
  misc::IntervalSet recoverSet;
  while (ctx->invokingState >= 0) {
    // compute what follows who invoked us
    atn::ATNState *invokingState = atn.states[(size_t)ctx->invokingState];
    atn::RuleTransition *rt = dynamic_cast<atn::RuleTransition*>(invokingState->transition(0));
    misc::IntervalSet follow = atn.nextTokens(rt->followState);
    recoverSet.addAll(follow);

    if (ctx->parent.expired())
      break;
    ctx = ctx->parent.lock();
  }
  recoverSet.remove(Token::EPSILON);

  return recoverSet;
}

void DefaultErrorStrategy::consumeUntil(Parser *recognizer, const misc::IntervalSet &set) {
  ssize_t ttype = recognizer->getInputStream()->LA(1);
  while (ttype != Token::EOF && !set.contains((int)ttype)) {
    recognizer->consume();
    ttype = recognizer->getInputStream()->LA(1);
  }
}

void DefaultErrorStrategy::InitializeInstanceFields() {
  errorRecoveryMode = false;
  lastErrorIndex = -1;
}
