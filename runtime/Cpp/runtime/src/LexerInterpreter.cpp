/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/ATNType.h"
#include "atn/LexerATNSimulator.h"
#include "dfa/DFA.h"
#include "Exceptions.h"
#include "Vocabulary.h"

#include "LexerInterpreter.h"

using namespace antlr4;

LexerInterpreter::LexerInterpreter(std::string_view grammarFileName, const dfa::Vocabulary &vocabulary,
  antlrcpp::Span<const std::string_view> ruleNames, antlrcpp::Span<const std::string_view> channelNames, antlrcpp::Span<const std::string_view> modeNames,
  const atn::ATN &atn, CharStream *input)
  : Lexer(input), _grammarFileName(grammarFileName), _atn(atn), _ruleNames(ruleNames),
                  _channelNames(channelNames), _modeNames(modeNames),
                  _vocabulary(vocabulary) {

  if (_atn.grammarType != atn::ATNType::LEXER) {
    throw IllegalArgumentException("The ATN must be a lexer ATN.");
  }

  for (size_t i = 0; i < atn.getNumberOfDecisions(); ++i) {
    _decisionToDFA.push_back(dfa::DFA(_atn.getDecisionState(i), i));
  }
  _interpreter = new atn::LexerATNSimulator(this, _atn, antlrcpp::Span<antlr4::dfa::DFA>(_decisionToDFA), _sharedContextCache); /* mem-check: deleted in d-tor */
}

LexerInterpreter::~LexerInterpreter()
{
  delete _interpreter;
}

const atn::ATN& LexerInterpreter::getATN() const {
  return _atn;
}

std::string_view LexerInterpreter::getGrammarFileName() const {
  return _grammarFileName;
}

antlrcpp::Span<const std::string_view> LexerInterpreter::getRuleNames() const {
  return _ruleNames;
}

antlrcpp::Span<const std::string_view> LexerInterpreter::getChannelNames() const {
  return _channelNames;
}

antlrcpp::Span<const std::string_view> LexerInterpreter::getModeNames() const {
  return _modeNames;
}

const Vocabulary& LexerInterpreter::getVocabulary() const {
  return _vocabulary;
}
