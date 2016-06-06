/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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

#include "atn/LexerChannelAction.h"

using namespace antlr4::atn;
using namespace antlr4::misc;

LexerChannelAction::LexerChannelAction(int channel) : _channel(channel) {
}

int LexerChannelAction::getChannel() const {
  return _channel;
}

LexerActionType LexerChannelAction::getActionType() const {
  return LexerActionType::CHANNEL;
}

bool LexerChannelAction::isPositionDependent() const {
  return false;
}

void LexerChannelAction::execute(Lexer *lexer) {
  lexer->setChannel(_channel);
}

size_t LexerChannelAction::hashCode() const {
  size_t hash = MurmurHash::initialize();
  hash = MurmurHash::update(hash, (size_t)getActionType());
  hash = MurmurHash::update(hash, _channel);
  return MurmurHash::finish(hash, 2);
}

bool LexerChannelAction::operator == (const LexerAction &obj) const {
  if (&obj == this) {
    return true;
  }

  const LexerChannelAction *action = dynamic_cast<const LexerChannelAction *>(&obj);
  if (action == nullptr) {
    return false;
  }

  return _channel == action->_channel;
}

std::string LexerChannelAction::toString() const {
  return "channel(" + std::to_string(_channel) + ")";
}
