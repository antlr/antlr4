/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Dan McLaughlin
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

#include "misc/MurmurHash.h"
#include "atn/LexerIndexedCustomAction.h"
#include "support/CPPUtils.h"
#include "support/Arrays.h"

#include "atn/LexerActionExecutor.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlr4::misc;
using namespace antlrcpp;

LexerActionExecutor::LexerActionExecutor(const std::vector<Ref<LexerAction>> &lexerActions)
  : _lexerActions(lexerActions), _hashCode(generateHashCode()) {
}

Ref<LexerActionExecutor> LexerActionExecutor::append(Ref<LexerActionExecutor> const& lexerActionExecutor,
                                                     Ref<LexerAction> const& lexerAction) {
  if (lexerActionExecutor == nullptr) {
    return std::make_shared<LexerActionExecutor>(std::vector<Ref<LexerAction>> { lexerAction });
  }

  std::vector<Ref<LexerAction>> lexerActions = lexerActionExecutor->_lexerActions; // Make a copy.
  lexerActions.push_back(lexerAction);
  return std::make_shared<LexerActionExecutor>(lexerActions);
}

Ref<LexerActionExecutor> LexerActionExecutor::fixOffsetBeforeMatch(int offset) {
  std::vector<Ref<LexerAction>> updatedLexerActions;
  for (size_t i = 0; i < _lexerActions.size(); i++) {
    if (_lexerActions[i]->isPositionDependent() && !is<LexerIndexedCustomAction>(_lexerActions[i])) {
      if (updatedLexerActions.empty()) {
        updatedLexerActions = _lexerActions; // Make a copy.
      }

      updatedLexerActions[i] = std::make_shared<LexerIndexedCustomAction>(offset, _lexerActions[i]);
    }
  }

  if (updatedLexerActions.empty()) {
    return shared_from_this();
  }

  return std::make_shared<LexerActionExecutor>(updatedLexerActions);
}

std::vector<Ref<LexerAction>> LexerActionExecutor::getLexerActions() const {
  return _lexerActions;
}

void LexerActionExecutor::execute(Lexer *lexer, CharStream *input, int startIndex) {
  bool requiresSeek = false;
  size_t stopIndex = input->index();

  auto onExit = finally([requiresSeek, input, stopIndex]() {
    if (requiresSeek) {
      input->seek(stopIndex);
    }
  });
  for (auto lexerAction : _lexerActions) {
    if (is<LexerIndexedCustomAction>(lexerAction)) {
      int offset = (std::static_pointer_cast<LexerIndexedCustomAction>(lexerAction))->getOffset();
      input->seek(startIndex + offset);
      lexerAction = std::static_pointer_cast<LexerIndexedCustomAction>(lexerAction)->getAction();
      requiresSeek = (size_t)(startIndex + offset) != stopIndex;
    } else if (lexerAction->isPositionDependent()) {
      input->seek(stopIndex);
      requiresSeek = false;
    }

    lexerAction->execute(lexer);
  }
}

size_t LexerActionExecutor::hashCode() const {
  return _hashCode;
}

bool LexerActionExecutor::operator == (const LexerActionExecutor &obj) const {
  if (&obj == this) {
    return true;
  }

  return _hashCode == obj._hashCode && Arrays::equals(_lexerActions, obj._lexerActions);
}

size_t LexerActionExecutor::generateHashCode() const {
  size_t hash = MurmurHash::initialize();
  for (auto lexerAction : _lexerActions) {
    hash = MurmurHash::update(hash, (size_t)lexerAction->hashCode());
  }
  MurmurHash::finish(hash, _lexerActions.size());
  
  return hash;
}
