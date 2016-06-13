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

#include "misc/Interval.h"
#include "Token.h"

#include "tree/TerminalNodeImpl.h"

using namespace antlr4;
using namespace antlr4::tree;

TerminalNodeImpl::TerminalNodeImpl(Token *symbol_) : symbol(symbol_) {
}

Token* TerminalNodeImpl::getSymbol() {
  return symbol;
}

misc::Interval TerminalNodeImpl::getSourceInterval() {
  if (symbol == nullptr) {
    return misc::Interval::INVALID;
  }

  int tokenIndex = symbol->getTokenIndex();
  return misc::Interval(tokenIndex, tokenIndex);
}

std::size_t TerminalNodeImpl::getChildCount() {
  return 0;
}

std::string TerminalNodeImpl::getText() {
  return symbol->getText();
}

std::string TerminalNodeImpl::toStringTree(Parser * /*parser*/) {
  return toString();
}

std::string TerminalNodeImpl::toString() {
  if (symbol->getType() == Token::EOF) {
    return "<EOF>";
  }
  return symbol->getText();
}

std::string TerminalNodeImpl::toStringTree() {
  return toString();
}

std::weak_ptr<Tree> TerminalNodeImpl::getParentReference() {
  return parent;
}

Ref<Tree> TerminalNodeImpl::getChildReference(size_t /*i*/) {
  return Ref<Tree>();
}
