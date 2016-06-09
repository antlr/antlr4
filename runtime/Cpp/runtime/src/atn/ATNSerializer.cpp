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

#include "misc/IntervalSet.h"
#include "atn/ATNType.h"
#include "atn/ATNState.h"
#include "atn/BlockEndState.h"

#include "atn/DecisionState.h"
#include "atn/RuleStartState.h"
#include "atn/LoopEndState.h"
#include "atn/BlockStartState.h"
#include "atn/Transition.h"
#include "atn/SetTransition.h"
#include "Token.h"
#include "misc/Interval.h"
#include "atn/ATN.h"

#include "atn/RuleTransition.h"
#include "atn/PrecedencePredicateTransition.h"
#include "atn/PredicateTransition.h"
#include "atn/RangeTransition.h"
#include "atn/AtomTransition.h"
#include "atn/ActionTransition.h"
#include "atn/ATNDeserializer.h"

#include "atn/TokensStartState.h"
#include "Exceptions.h"
#include "support/CPPUtils.h"

#include "atn/LexerChannelAction.h"
#include "atn/LexerCustomAction.h"
#include "atn/LexerModeAction.h"
#include "atn/LexerPushModeAction.h"
#include "atn/LexerTypeAction.h"

#include "Exceptions.h"

#include "atn/ATNSerializer.h"

using namespace antlrcpp;
using namespace antlr4::atn;

ATNSerializer::ATNSerializer(ATN *atn) { this->atn = atn; }

ATNSerializer::ATNSerializer(ATN *atn, const std::vector<std::string> &tokenNames) {
  this->atn = atn;
  _tokenNames = tokenNames;
}

std::vector<size_t> ATNSerializer::serialize() {
  std::vector<size_t> data;
  data.push_back(ATNDeserializer::SERIALIZED_VERSION);
  serializeUUID(data, ATNDeserializer::SERIALIZED_UUID());

  // convert grammar type to ATN const to avoid dependence on ANTLRParser
  data.push_back((size_t)atn->grammarType);
  data.push_back((size_t)atn->maxTokenType);
  size_t nedges = 0;

  std::unordered_map<misc::IntervalSet, int> setIndices;
  std::vector<misc::IntervalSet> sets;

  // dump states, count edges and collect sets while doing so
  std::vector<int> nonGreedyStates;
  std::vector<int> precedenceStates;
  data.push_back(atn->states.size());
  for (ATNState *s : atn->states) {
    if (s == nullptr) {  // might be optimized away
      data.push_back(ATNState::ATN_INVALID_TYPE);
      continue;
    }

    int stateType = s->getStateType();
    if (is<DecisionState *>(s) && (static_cast<DecisionState *>(s))->nonGreedy) {
      nonGreedyStates.push_back(s->stateNumber);
    }

    if (is<RuleStartState *>(s) && (static_cast<RuleStartState *>(s))->isLeftRecursiveRule) {
      precedenceStates.push_back(s->stateNumber);
    }

    data.push_back((size_t)stateType);

    if (s->ruleIndex == -1) {
      data.push_back(0xFFFF);
    }
    else {
      data.push_back((size_t)s->ruleIndex);
    }

    if (s->getStateType() == ATNState::LOOP_END) {
      data.push_back((size_t)(static_cast<LoopEndState *>(s))->loopBackState->stateNumber);
    }
    else if (is<BlockStartState *>(s)) {
      data.push_back((size_t)(static_cast<BlockStartState *>(s))->endState->stateNumber);
    }

    if (s->getStateType() != ATNState::RULE_STOP) {
      // the deserializer can trivially derive these edges, so there's no need
      // to serialize them
      nedges += s->getNumberOfTransitions();
    }

    for (size_t i = 0; i < s->getNumberOfTransitions(); i++) {
      Transition *t = s->transition(i);
      int edgeType = t->getSerializationType();
      if (edgeType == Transition::SET || edgeType == Transition::NOT_SET) {
        SetTransition *st = static_cast<SetTransition *>(t);
        if (setIndices.find(st->set) != setIndices.end()) {
          sets.push_back(st->set);
          setIndices.insert({ st->set, (int)sets.size() - 1 });
        }
      }
    }
  }

  // non-greedy states
  data.push_back(nonGreedyStates.size());
  for (size_t i = 0; i < nonGreedyStates.size(); i++) {
    data.push_back((size_t)nonGreedyStates.at(i));
  }

  // precedence states
  data.push_back(precedenceStates.size());
  for (size_t i = 0; i < precedenceStates.size(); i++) {
    data.push_back((size_t)precedenceStates.at(i));
  }

  size_t nrules = atn->ruleToStartState.size();
  data.push_back(nrules);
  for (size_t r = 0; r < nrules; r++) {
    ATNState *ruleStartState = atn->ruleToStartState[r];
    data.push_back((size_t)ruleStartState->stateNumber);
    if (atn->grammarType == ATNType::LEXER) {
      if (atn->ruleToTokenType[r] == Token::EOF) {
        data.push_back(0xFFFF);
      }
      else {
        data.push_back((size_t)atn->ruleToTokenType[r]);
      }
    }
  }

  size_t nmodes = atn->modeToStartState.size();
  data.push_back(nmodes);
  if (nmodes > 0) {
    for (const auto &modeStartState : atn->modeToStartState) {
      data.push_back((size_t)modeStartState->stateNumber);
    }
  }

  size_t nsets = sets.size();
  data.push_back(nsets);
  for (auto set : sets) {
    bool containsEof = set.contains(Token::EOF);
    if (containsEof && set.getIntervals().at(0).b == Token::EOF) {
      data.push_back(set.getIntervals().size() - 1);
    }
    else {
      data.push_back(set.getIntervals().size());
    }

    data.push_back(containsEof ? 1 : 0);
    for (auto &interval : set.getIntervals()) {
      if (interval.a == Token::EOF) {
        if (interval.b == Token::EOF) {
          continue;
        } else {
          data.push_back(0);
        }
      }
      else {
        data.push_back((size_t)interval.a);
      }

      data.push_back((size_t)interval.b);
    }
  }

  data.push_back(nedges);
  for (ATNState *s : atn->states) {
    if (s == nullptr) {
      // might be optimized away
      continue;
    }

    if (s->getStateType() == ATNState::RULE_STOP) {
      continue;
    }

    for (size_t i = 0; i < s->getNumberOfTransitions(); i++) {
      Transition *t = s->transition(i);

      if (atn->states[(size_t)t->target->stateNumber] == nullptr) {
        throw IllegalStateException("Cannot serialize a transition to a removed state.");
      }

      int src = s->stateNumber;
      int trg = t->target->stateNumber;
      int edgeType = t->getSerializationType();
      int arg1 = 0;
      int arg2 = 0;
      int arg3 = 0;
      switch (edgeType) {
        case Transition::RULE:
          trg = (static_cast<RuleTransition *>(t))->followState->stateNumber;
          arg1 = (static_cast<RuleTransition *>(t))->target->stateNumber;
          arg2 = (static_cast<RuleTransition *>(t))->ruleIndex;
          arg3 = (static_cast<RuleTransition *>(t))->precedence;
          break;
        case Transition::PRECEDENCE:
        {
          PrecedencePredicateTransition *ppt =
          static_cast<PrecedencePredicateTransition *>(t);
          arg1 = ppt->precedence;
        }
          break;
        case Transition::PREDICATE:
        {
          PredicateTransition *pt = static_cast<PredicateTransition *>(t);
          arg1 = pt->ruleIndex;
          arg2 = pt->predIndex;
          arg3 = pt->isCtxDependent ? 1 : 0;
        }
          break;
        case Transition::RANGE:
          arg1 = (int)(static_cast<RangeTransition *>(t))->from;
          arg2 = (int)(static_cast<RangeTransition *>(t))->to;
          if (arg1 == Token::EOF) {
            arg1 = 0;
            arg3 = 1;
          }

          break;
        case Transition::ATOM:
          arg1 = (int)(static_cast<AtomTransition *>(t))->_label;
          if (arg1 == Token::EOF) {
            arg1 = 0;
            arg3 = 1;
          }

          break;
        case Transition::ACTION:
        {
          ActionTransition *at = static_cast<ActionTransition *>(t);
          arg1 = at->ruleIndex;
          arg2 = at->actionIndex;
          if (arg2 == -1) {
            arg2 = 0xFFFF;
          }

          arg3 = at->isCtxDependent ? 1 : 0;
        }
          break;
        case Transition::SET:
          arg1 = setIndices[(static_cast<SetTransition *>(t))->set];
          break;

        case Transition::NOT_SET:
          arg1 = setIndices[(static_cast<SetTransition *>(t))->set];
          break;
        case Transition::WILDCARD:
          break;
      }

      data.push_back((size_t)src);
      data.push_back((size_t)trg);
      data.push_back((size_t)edgeType);
      data.push_back((size_t)arg1);
      data.push_back((size_t)arg2);
      data.push_back((size_t)arg3);
    }
  }

  size_t ndecisions = atn->decisionToState.size();
  data.push_back(ndecisions);
  for (DecisionState *decStartState : atn->decisionToState) {
    data.push_back((size_t)decStartState->stateNumber);
  }

  // LEXER ACTIONS
  if (atn->grammarType == ATNType::LEXER) {
    data.push_back(atn->lexerActions.size());
    for (Ref<LexerAction> &action : atn->lexerActions) {
      data.push_back((size_t)action->getActionType());
      switch (action->getActionType()) {
        case LexerActionType::CHANNEL:
        {
          int channel = std::dynamic_pointer_cast<LexerChannelAction>(action)->getChannel();
          data.push_back(channel != -1 ? channel : 0xFFFF);
          data.push_back(0);
          break;
        }

        case LexerActionType::CUSTOM:
        {
          int ruleIndex = std::dynamic_pointer_cast<LexerCustomAction>(action)->getRuleIndex();
          int actionIndex = std::dynamic_pointer_cast<LexerCustomAction>(action)->getActionIndex();
          data.push_back(ruleIndex != -1 ? ruleIndex : 0xFFFF);
          data.push_back(actionIndex != -1 ? actionIndex : 0xFFFF);
          break;
        }

        case LexerActionType::MODE:
        {
          int mode = std::dynamic_pointer_cast<LexerModeAction>(action)->getMode();
          data.push_back(mode != -1 ? mode : 0xFFFF);
          data.push_back(0);
          break;
        }

        case LexerActionType::MORE:
          data.push_back(0);
          data.push_back(0);
          break;

        case LexerActionType::POP_MODE:
          data.push_back(0);
          data.push_back(0);
          break;

        case LexerActionType::PUSH_MODE:
        {
          int mode = std::dynamic_pointer_cast<LexerPushModeAction>(action)->getMode();
          data.push_back(mode != -1 ? mode : 0xFFFF);
          data.push_back(0);
          break;
        }

        case LexerActionType::SKIP:
          data.push_back(0);
          data.push_back(0);
          break;

        case LexerActionType::TYPE:
        {
          int type = std::dynamic_pointer_cast<LexerTypeAction>(action)->getType();
          data.push_back(type != -1 ? type : 0xFFFF);
          data.push_back(0);
          break;
        }

        default:
          throw IllegalArgumentException("The specified lexer action type " +
                                         std::to_string((size_t)action->getActionType()) + " is not valid.");
      }
    }
  }

  // don't adjust the first value since that's the version number
  for (size_t i = 1; i < data.size(); i++) {
    if (data.at(i) > 0xFFFF) {
      throw UnsupportedOperationException("Serialized ATN data element out of range.");
    }

    size_t value = (data.at(i) + 2) & 0xFFFF;
    data.assign(i, value);
  }

  return data;
}

//------------------------------------------------------------------------------------------------------------

std::string ATNSerializer::decode(const std::wstring &inpdata) {
  if (inpdata.size() < 10)
    throw IllegalArgumentException("Not enough data to decode");

  std::vector<uint16_t> data(inpdata.size());
  data[0] = (uint16_t)inpdata[0];

  // Don't adjust the first value since that's the version number.
  for (size_t i = 1; i < inpdata.size(); ++i) {
    data[i] = (uint16_t)inpdata[i] - 2;
  }

  std::string buf;
  int p = 0;
  size_t version = (size_t)data[p++];
  if (version != ATNDeserializer::SERIALIZED_VERSION) {
    std::string reason = "Could not deserialize ATN with version " + std::to_string(version) + "(expected " +
    std::to_string(ATNDeserializer::SERIALIZED_VERSION) + ").";
    throw UnsupportedOperationException("ATN Serializer" + reason);
  }

  Guid uuid = ATNDeserializer::toUUID(data.data(), p);
  p += 8;
  if (uuid != ATNDeserializer::SERIALIZED_UUID()) {
    std::string reason = "Could not deserialize ATN with UUID " + uuid.toString() + " (expected " +
    ATNDeserializer::SERIALIZED_UUID().toString() + ").";
    throw UnsupportedOperationException("ATN Serializer" + reason);
  }

  p++;  // skip grammarType
  int maxType = data[p++];
  buf.append("max type ").append(std::to_string(maxType)).append("\n");
  int nstates = data[p++];
  for (int i = 0; i < nstates; i++) {
    int stype = data[p++];
    if (stype == ATNState::ATN_INVALID_TYPE) {  // ignore bad type of states
      continue;
    }
    int ruleIndex = data[p++];
    if (ruleIndex == 0xFFFF) {
      ruleIndex = -1;
    }

    std::string arg = "";
    if (stype == ATNState::LOOP_END) {
      int loopBackStateNumber = data[p++];
      arg = std::string(" ") + std::to_string(loopBackStateNumber);
    }
    else if (stype == ATNState::PLUS_BLOCK_START ||
             stype == ATNState::STAR_BLOCK_START ||
             stype == ATNState::BLOCK_START) {
      int endStateNumber = data[p++];
      arg = std::string(" ") + std::to_string(endStateNumber);
    }
    buf.append(std::to_string(i))
    .append(":")
    .append(ATNState::serializationNames[(size_t)stype])
    .append(" ")
    .append(std::to_string(ruleIndex))
    .append(arg)
    .append("\n");
  }
  int numNonGreedyStates = data[p++];
  p += numNonGreedyStates; // Instead of that useless loop below.
  /*
   for (int i = 0; i < numNonGreedyStates; i++) {
   int stateNumber = data[p++];
   }
   */

  int numPrecedenceStates = data[p++];
  p += numPrecedenceStates;
  /*
   for (int i = 0; i < numPrecedenceStates; i++) {
   int stateNumber = data[p++];
   }
   */

  int nrules = data[p++];
  for (int i = 0; i < nrules; i++) {
    int s = data[p++];
    if (atn->grammarType == ATNType::LEXER) {
      int arg1 = data[p++];
      buf.append("rule ")
      .append(std::to_string(i))
      .append(":")
      .append(std::to_string(s))
      .append(" ")
      .append(std::to_string(arg1))
      .append("\n");
    }
    else {
      buf.append("rule ")
      .append(std::to_string(i))
      .append(":")
      .append(std::to_string(s))
      .append("\n");
    }
  }
  int nmodes = data[p++];
  for (int i = 0; i < nmodes; i++) {
    int s = data[p++];
    buf.append("mode ")
    .append(std::to_string(i))
    .append(":")
    .append(std::to_string(s))
    .append("\n");
  }
  int nsets = data[p++];
  for (int i = 0; i < nsets; i++) {
    int nintervals = data[p++];
    buf.append(std::to_string(i)).append(":");
    bool containsEof = data[p++] != 0;
    if (containsEof) {
      buf.append(getTokenName(Token::EOF));
    }

    for (int j = 0; j < nintervals; j++) {
      if (containsEof || j > 0) {
        buf.append(", ");
      }

      buf.append(getTokenName(data[p]))
      .append("..")
      .append(getTokenName(data[p + 1]));
      p += 2;
    }
    buf.append("\n");
  }
  int nedges = data[p++];
  for (int i = 0; i < nedges; i++) {
    int src = data[p];
    int trg = data[p + 1];
    int ttype = data[p + 2];
    int arg1 = data[p + 3];
    int arg2 = data[p + 4];
    int arg3 = data[p + 5];
    buf.append(std::to_string(src))
    .append("->")
    .append(std::to_string(trg))
    .append(" ")
    .append(Transition::serializationNames[(size_t)ttype])
    .append(" ")
    .append(std::to_string(arg1))
    .append(",")
    .append(std::to_string(arg2))
    .append(",")
    .append(std::to_string(arg3))
    .append("\n");
    p += 6;
  }
  int ndecisions = data[p++];
  for (int i = 0; i < ndecisions; i++) {
    int s = data[p++];
    buf.append(std::to_string(i)).append(":").append(std::to_string(s)).append("\n");
  }

  if (atn->grammarType == ATNType::LEXER) {
    //int lexerActionCount = data[p++];

    //p += lexerActionCount * 3; // Instead of useless loop below.
    /*
    for (int i = 0; i < lexerActionCount; i++) {
      LexerActionType actionType = (LexerActionType)data[p++];
      int data1 = data[p++];
      int data2 = data[p++];
    }
     */
  }

  return buf;
}

std::string ATNSerializer::getTokenName(ssize_t t) {
  if (t == -1) {
    return "EOF";
  }

  if (atn->grammarType == ATNType::LEXER && t >= 0 && t <= 0xFFFF) {
    switch (t) {
      case '\n':
        return "'\\n'";
      case '\r':
        return "'\\r'";
      case '\t':
        return "'\\t'";
      case '\b':
        return "'\\b'";
      case '\f':
        return "'\\f'";
      case '\\':
        return "'\\\\'";
      case '\'':
        return "'\\''";
      default:
        std::string s_hex = antlrcpp::toHexString((int)t);
        if (s_hex >= "0" && s_hex <= "7F" &&
            !iscntrl((int)t)) {
          return "'" + std::to_string(t) + "'";
        }
        // turn on the bit above max "\uFFFF" value so that we pad with zeros
        // then only take last 4 digits
        std::string hex = antlrcpp::toHexString((int)t | 0x10000).substr(1, 4);
        std::string unicodeStr = std::string("'\\u") + hex + std::string("'");
        return unicodeStr;
    }
  }

  if (_tokenNames.size() > 0 && t >= 0 && t < (ssize_t)_tokenNames.size()) {
    return _tokenNames[(size_t)t];
  }

  return std::to_string(t);
}

std::wstring ATNSerializer::getSerializedAsString(ATN *atn) {
  std::vector<size_t> data = getSerialized(atn);
  std::wstring result;
  for (size_t entry : data)
    result.push_back((wchar_t)entry);

  return result;
}

std::vector<size_t> ATNSerializer::getSerialized(ATN *atn) {
  return ATNSerializer(atn).serialize();
}

std::string ATNSerializer::getDecoded(ATN *atn, std::vector<std::string> &tokenNames) {
  std::wstring serialized = getSerializedAsString(atn);
  return ATNSerializer(atn, tokenNames).decode(serialized);
}

void ATNSerializer::serializeUUID(std::vector<size_t> &data, Guid uuid) {
  for (auto &entry : uuid)
    data.push_back(entry);
}
