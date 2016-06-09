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
#include "Lexer.h"
#include "support/CPPUtils.h"

#include "atn/LexerIndexedCustomAction.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlr4::misc;

LexerIndexedCustomAction::LexerIndexedCustomAction(int offset, Ref<LexerAction> const& action)
  : _offset(offset), _action(action) {
}

int LexerIndexedCustomAction::getOffset() const {
  return _offset;
}

Ref<LexerAction> LexerIndexedCustomAction::getAction() const {
  return _action;
}

LexerActionType LexerIndexedCustomAction::getActionType() const {
  return _action->getActionType();
}

bool LexerIndexedCustomAction::isPositionDependent() const {
  return true;
}

void LexerIndexedCustomAction::execute(Lexer *lexer) {
  // assume the input stream position was properly set by the calling code
  _action->execute(lexer);
}

size_t LexerIndexedCustomAction::hashCode() const {
  size_t hash = MurmurHash::initialize();
  hash = MurmurHash::update(hash, _offset);
  hash = MurmurHash::update(hash, _action->hashCode());
  return MurmurHash::finish(hash, 2);
}

bool LexerIndexedCustomAction::operator == (const LexerAction &obj) const {
  if (&obj == this) {
    return true;
  }

  const LexerIndexedCustomAction *action = dynamic_cast<const LexerIndexedCustomAction *>(&obj);
  if (action == nullptr) {
    return false;
  }

  return _offset == action->_offset && _action == action->_action;
}

std::string LexerIndexedCustomAction::toString() const {
  return antlrcpp::toString(this);
}
