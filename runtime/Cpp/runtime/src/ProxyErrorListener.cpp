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

#include "ProxyErrorListener.h"

using namespace antlr4;

void ProxyErrorListener::addErrorListener(ANTLRErrorListener *listener) {
  if (listener == nullptr) {
    throw "listener cannot be null.";
  }

  _delegates.insert(listener);
}

void ProxyErrorListener::removeErrorListener(ANTLRErrorListener *listener) {
  _delegates.erase(listener);
}

void ProxyErrorListener::removeErrorListeners() {
  _delegates.clear();
}

void ProxyErrorListener::syntaxError(IRecognizer *recognizer, Token *offendingSymbol, size_t line,
  int charPositionInLine, const std::string &msg, std::exception_ptr e) {

  for (auto listener : _delegates) {
    listener->syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
  }
}

void ProxyErrorListener::reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
  bool exact, const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) {
  for (auto listener : _delegates) {
    listener->reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
  }
}

void ProxyErrorListener::reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex,
  size_t stopIndex, const antlrcpp::BitSet &conflictingAlts, atn::ATNConfigSet *configs) {
  for (auto listener : _delegates) {
    listener->reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs);
  }
}

void ProxyErrorListener::reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
  int prediction, atn::ATNConfigSet *configs) {
  for (auto listener : _delegates) {
    listener->reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs);
  }
}
