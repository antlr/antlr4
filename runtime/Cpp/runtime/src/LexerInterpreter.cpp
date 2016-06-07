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

#include "atn/ATNType.h"
#include "atn/LexerATNSimulator.h"
#include "dfa/DFA.h"
#include "atn/EmptyPredictionContext.h"
#include "Exceptions.h"
#include "Vocabulary.h"

#include "LexerInterpreter.h"

using namespace antlr4;

LexerInterpreter::LexerInterpreter(const std::string &grammarFileName, const std::vector<std::string> &tokenNames,
  const std::vector<std::string> &ruleNames, const std::vector<std::string> &modeNames, const atn::ATN &atn,
  CharStream *input)
  : LexerInterpreter(grammarFileName, dfa::Vocabulary::fromTokenNames(tokenNames), ruleNames, modeNames, atn, input) {
}

LexerInterpreter::LexerInterpreter(const std::string &grammarFileName, const dfa::Vocabulary &vocabulary,
  const std::vector<std::string> &ruleNames, const std::vector<std::string> &modeNames, const atn::ATN &atn,
  CharStream *input)
  : Lexer(input), _grammarFileName(grammarFileName), _atn(atn), _ruleNames(ruleNames), _modeNames(modeNames),
                  _vocabulary(vocabulary) {

  if (_atn.grammarType != atn::ATNType::LEXER) {
    throw IllegalArgumentException("The ATN must be a lexer ATN.");
  }

  for (size_t i = 0; i < atn.maxTokenType; i++) {
    _tokenNames.push_back(vocabulary.getDisplayName(i));
  }

  for (size_t i = 0; i < (size_t)atn.getNumberOfDecisions(); ++i) {
    _decisionToDFA.push_back(dfa::DFA(_atn.getDecisionState((int)i), (int)i));
  }
  _interpreter = new atn::LexerATNSimulator(_atn, _decisionToDFA, _sharedContextCache); /* mem-check: deleted in d-tor */
}

LexerInterpreter::~LexerInterpreter()
{
  delete _interpreter;
}

const atn::ATN& LexerInterpreter::getATN() const {
  return _atn;
}

std::string LexerInterpreter::getGrammarFileName() const {
  return _grammarFileName;
}

const std::vector<std::string>& LexerInterpreter::getTokenNames() const {
  return _tokenNames;
}

const std::vector<std::string>& LexerInterpreter::getRuleNames() const {
  return _ruleNames;
}

const std::vector<std::string>& LexerInterpreter::getModeNames() const {
  return _modeNames;
}

const dfa::Vocabulary& LexerInterpreter::getVocabulary() const {
  return _vocabulary;
}
