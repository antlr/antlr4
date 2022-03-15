/* Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "misc/MurmurHash.h"
#include "Lexer.h"

#include "atn/LexerLessAction.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlr4::misc;

const Ref<LexerLessAction> LexerLessAction::getInstance() {
  static Ref<LexerLessAction> instance(new LexerLessAction());
  return instance;
}

LexerLessAction::LexerLessAction() {
}

LexerActionType LexerLessAction::getActionType() const {
  return LexerActionType::LESS;
}

bool LexerLessAction::isPositionDependent() const {
  return false;
}

void LexerLessAction::execute(Lexer *lexer) {
  lexer->less();
}

size_t LexerLessAction::hashCode() const {
  size_t hash = MurmurHash::initialize();
  hash = MurmurHash::update(hash, static_cast<size_t>(getActionType()));
  return MurmurHash::finish(hash, 1);
}

bool LexerLessAction::operator == (const LexerAction &obj) const {
  return &obj == this;
}

std::string LexerLessAction::toString() const {
  return "less";
}
