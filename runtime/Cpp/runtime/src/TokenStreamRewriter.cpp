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

#include "Exceptions.h"
#include "Interval.h"
#include "Token.h"
#include "TokenStream.h"
#include "Strings.h"
#include "CPPUtils.h"

#include "TokenStreamRewriter.h"

using namespace org::antlr::v4::runtime;
using namespace antlrcpp;

using org::antlr::v4::runtime::misc::Interval;

TokenStreamRewriter::RewriteOperation::RewriteOperation(TokenStreamRewriter *outerInstance, size_t index) : outerInstance(outerInstance) {

  InitializeInstanceFields();
  this->index = index;
}

TokenStreamRewriter::RewriteOperation::RewriteOperation(TokenStreamRewriter *outerInstance, size_t index, const std::wstring& text) : outerInstance(outerInstance) {

  InitializeInstanceFields();
  this->index = index;
  this->text = text;
}

size_t TokenStreamRewriter::RewriteOperation::execute(std::wstring *buf) {
  return index;
}

std::wstring TokenStreamRewriter::RewriteOperation::toString() {
  std::wstring opName = L"TokenStreamRewriter";
  size_t index = opName.find(L'$');
  opName = opName.substr(index + 1, opName.length() - (index + 1));
  return L"<" + opName + L"@" + outerInstance->tokens->get(index)->getText() + L":\"" + text + L"\">";
}

void TokenStreamRewriter::RewriteOperation::InitializeInstanceFields() {
  instructionIndex = 0;
  index = 0;
}

TokenStreamRewriter::InsertBeforeOp::InsertBeforeOp(TokenStreamRewriter *outerInstance, size_t index, const std::wstring& text)
: RewriteOperation(outerInstance, index, text), outerInstance(outerInstance) {
}

size_t TokenStreamRewriter::InsertBeforeOp::execute(std::wstring *buf) {
  buf->append(text);
  if (outerInstance->tokens->get(index)->getType() != EOF) {
    buf->append(outerInstance->tokens->get(index)->getText());
  }
  return index + 1;
}

TokenStreamRewriter::ReplaceOp::ReplaceOp(TokenStreamRewriter *outerInstance, size_t from, size_t to, const std::wstring& text) : RewriteOperation(outerInstance, from, text), outerInstance(outerInstance) {

  InitializeInstanceFields();
  lastIndex = to;
}

size_t TokenStreamRewriter::ReplaceOp::execute(std::wstring *buf) {
  buf->append(text);
  return lastIndex + 1;
}

std::wstring TokenStreamRewriter::ReplaceOp::toString() {
  if (text.empty()) {
    return L"<DeleteOp@" + outerInstance->tokens->get(index)->getText() + L".." + outerInstance->tokens->get(lastIndex)->getText() + L">";
  }
  return L"<ReplaceOp@" + outerInstance->tokens->get(index)->getText() + L".." + outerInstance->tokens->get(lastIndex)->getText() + L":\"" + text + L"\">";
}

void TokenStreamRewriter::ReplaceOp::InitializeInstanceFields() {
  lastIndex = 0;
}

//------------------ TokenStreamRewriter -------------------------------------------------------------------------------

const std::wstring TokenStreamRewriter::DEFAULT_PROGRAM_NAME = L"default";

TokenStreamRewriter::TokenStreamRewriter(TokenStream *tokens) : tokens(tokens) {
  _programs.insert({ DEFAULT_PROGRAM_NAME, std::vector<RewriteOperation*>(PROGRAM_INIT_SIZE) });
}

TokenStreamRewriter::~TokenStreamRewriter() {
  for (auto program : _programs) {
    for (auto operation : program.second) {
      delete operation;
    }
  }
}

TokenStream *TokenStreamRewriter::getTokenStream() {
  return tokens;
}

void TokenStreamRewriter::rollback(int instructionIndex) {
  rollback(DEFAULT_PROGRAM_NAME, instructionIndex);
}

void TokenStreamRewriter::rollback(const std::wstring &programName, int instructionIndex) {
  std::vector<RewriteOperation*> is = _programs[programName];
  if (is.size() > 0) {
    _programs.insert({ programName, std::vector<RewriteOperation*>(is.begin() + MIN_TOKEN_INDEX, is.begin() + instructionIndex) });
  }
}

void TokenStreamRewriter::deleteProgram() {
  deleteProgram(DEFAULT_PROGRAM_NAME);
}

void TokenStreamRewriter::deleteProgram(const std::wstring &programName) {
  rollback(programName, MIN_TOKEN_INDEX);
}

void TokenStreamRewriter::insertAfter(Token *t, const std::wstring& text) {
  insertAfter(DEFAULT_PROGRAM_NAME, t, text);
}

void TokenStreamRewriter::insertAfter(size_t index, const std::wstring& text) {
  insertAfter(DEFAULT_PROGRAM_NAME, index, text);
}

void TokenStreamRewriter::insertAfter(const std::wstring &programName, Token *t, const std::wstring& text) {
  insertAfter(programName, (size_t)t->getTokenIndex(), text);
}

void TokenStreamRewriter::insertAfter(const std::wstring &programName, size_t index, const std::wstring& text) {
  // to insert after, just insert before next index (even if past end)
  insertBefore(programName, index + 1, text);
}

void TokenStreamRewriter::insertBefore(Token *t, const std::wstring& text) {
  insertBefore(DEFAULT_PROGRAM_NAME, t, text);
}

void TokenStreamRewriter::insertBefore(size_t index, const std::wstring& text) {
  insertBefore(DEFAULT_PROGRAM_NAME, index, text);
}

void TokenStreamRewriter::insertBefore(const std::wstring &programName, Token *t, const std::wstring& text) {
  insertBefore(programName, (size_t)t->getTokenIndex(), text);
}

void TokenStreamRewriter::insertBefore(const std::wstring &programName, size_t index, const std::wstring& text) {
  RewriteOperation *op = new InsertBeforeOp(this, index, text); /* mem-check: deleted in d-tor */
  std::vector<RewriteOperation*> &rewrites = getProgram(programName);
  op->instructionIndex = (int)rewrites.size();
  rewrites.push_back(op);
}

void TokenStreamRewriter::replace(size_t index, const std::wstring& text) {
  replace(DEFAULT_PROGRAM_NAME, index, index, text);
}

void TokenStreamRewriter::replace(size_t from, size_t to, const std::wstring& text) {
  replace(DEFAULT_PROGRAM_NAME, from, to, text);
}

void TokenStreamRewriter::replace(Token *indexT, const std::wstring& text) {
  replace(DEFAULT_PROGRAM_NAME, indexT, indexT, text);
}

void TokenStreamRewriter::replace(Token *from, Token *to, const std::wstring& text) {
  replace(DEFAULT_PROGRAM_NAME, from, to, text);
}

void TokenStreamRewriter::replace(const std::wstring &programName, size_t from, size_t to, const std::wstring& text) {
  if (from > to || to >= tokens->size()) {
    throw IllegalArgumentException("replace: range invalid: " + std::to_string(from) + ".." + std::to_string(to) +
                                   "(size = " + std::to_string(tokens->size()) + ")");
  }
  RewriteOperation *op = new ReplaceOp(this, from, to, text); /* mem-check: deleted in d-tor */
  std::vector<RewriteOperation*> &rewrites = getProgram(programName);
  op->instructionIndex = (int)rewrites.size();
  rewrites.push_back(op);
}

void TokenStreamRewriter::replace(const std::wstring &programName, Token *from, Token *to, const std::wstring& text) {
  replace(programName, (size_t)from->getTokenIndex(), (size_t)to->getTokenIndex(), text);
}

void TokenStreamRewriter::Delete(size_t index) {
  Delete(DEFAULT_PROGRAM_NAME, index, index);
}

void TokenStreamRewriter::Delete(size_t from, size_t to) {
  Delete(DEFAULT_PROGRAM_NAME, from, to);
}

void TokenStreamRewriter::Delete(Token *indexT) {
  Delete(DEFAULT_PROGRAM_NAME, indexT, indexT);
}

void TokenStreamRewriter::Delete(Token *from, Token *to) {
  Delete(DEFAULT_PROGRAM_NAME, from, to);
}

void TokenStreamRewriter::Delete(const std::wstring &programName, size_t from, size_t to) {
  replace(programName, from, to, nullptr);
}

void TokenStreamRewriter::Delete(const std::wstring &programName, Token *from, Token *to) {
  replace(programName, from, to, nullptr);
}

int TokenStreamRewriter::getLastRewriteTokenIndex() {
  return getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME);
}

int TokenStreamRewriter::getLastRewriteTokenIndex(const std::wstring &programName) {
  if (_lastRewriteTokenIndexes.find(programName) == _lastRewriteTokenIndexes.end()) {
    return -1;
  }
  return _lastRewriteTokenIndexes[programName];
}

void TokenStreamRewriter::setLastRewriteTokenIndex(const std::wstring &programName, int i) {
  _lastRewriteTokenIndexes.insert({ programName, i });
}

std::vector<TokenStreamRewriter::RewriteOperation*>& TokenStreamRewriter::getProgram(const std::wstring &name) {
  std::vector<TokenStreamRewriter::RewriteOperation*> &is = _programs[name];
  if (is.empty()) {
    is = initializeProgram(name);
  }
  return is;
}

std::vector<TokenStreamRewriter::RewriteOperation*> TokenStreamRewriter::initializeProgram(const std::wstring &name) {
  std::vector<TokenStreamRewriter::RewriteOperation*> is(PROGRAM_INIT_SIZE);
  _programs.insert({ name, is });
  return is;
}

std::wstring TokenStreamRewriter::getText() {
  return getText(DEFAULT_PROGRAM_NAME, Interval(0, (int)tokens->size() - 1));
}

std::wstring TokenStreamRewriter::getText(std::wstring programName) {
  return getText(programName, Interval(0, (int)tokens->size() - 1));
}

std::wstring TokenStreamRewriter::getText(const Interval &interval) {
  return getText(DEFAULT_PROGRAM_NAME, interval);
}

std::wstring TokenStreamRewriter::getText(const std::wstring &programName, const Interval &interval) {
  std::vector<TokenStreamRewriter::RewriteOperation*> rewrites = _programs[programName];
  int start = interval.a;
  int stop = interval.b;

  // ensure start/end are in range
  if (stop > (int)tokens->size() - 1) {
    stop = (int)tokens->size() - 1;
  }
  if (start < 0) {
    start = 0;
  }

  if (rewrites.empty() || rewrites.empty()) {
    return tokens->getText(interval); // no instructions to execute
  }
  std::wstring buf;

  // First, optimize instruction stream
  std::unordered_map<size_t, TokenStreamRewriter::RewriteOperation*> indexToOp = reduceToSingleOperationPerIndex(rewrites);

  // Walk buffer, executing instructions and emitting tokens
  size_t i = (size_t)start;
  while (i <= (size_t)stop && i < tokens->size()) {
    RewriteOperation *op = indexToOp[i];
    indexToOp.erase(i); // remove so any left have index size-1
    Ref<Token> t = tokens->get(i);
    if (op == nullptr) {
      // no operation at that index, just dump token
      if (t->getType() != EOF) {
        buf.append(t->getText());
      }
      i++; // move to next token
    }
    else {
      i = op->execute(&buf); // execute operation and skip
    }
  }

  // include stuff after end if it's last index in buffer
  // So, if they did an insertAfter(lastValidIndex, "foo"), include
  // foo if end==lastValidIndex.
  if (stop == (int)tokens->size() - 1) {
    // Scan any remaining operations after last token
    // should be included (they will be inserts).
    for (auto op : indexToOp) {
      if (op.second->index >= tokens->size() - 1) {
        buf.append(op.second->text);
      }
    }
  }
  return buf;
}

std::unordered_map<size_t, TokenStreamRewriter::RewriteOperation*> TokenStreamRewriter::reduceToSingleOperationPerIndex(
  std::vector<TokenStreamRewriter::RewriteOperation*> rewrites) {

  // WALK REPLACES
  for (size_t i = 0; i < rewrites.size(); ++i) {
    TokenStreamRewriter::RewriteOperation *op = rewrites[i];
    if (op == nullptr) {
      continue;
    }
    if (!is<ReplaceOp *>(op)) {
      continue;
    }
    ReplaceOp *rop = static_cast<ReplaceOp*>(op);
    // Wipe prior inserts within range
    InsertBeforeOp* type = nullptr;
    std::vector<InsertBeforeOp*> inserts = getKindOfOps(rewrites, type, i);
    for (auto iop : inserts) {
      if (iop->index == rop->index) {
        // E.g., insert before 2, delete 2..2; update replace
        // text to include insert before, kill insert
        rewrites[(size_t)iop->instructionIndex] = nullptr;
        rop->text = iop->text + (!rop->text.empty() ? rop->text : L"");
      }
      else if (iop->index > rop->index && iop->index <= rop->lastIndex) {
        // delete insert as it's a no-op.
        rewrites[(size_t)iop->instructionIndex] = nullptr;
      }
    }
    // Drop any prior replaces contained within
    ReplaceOp* type2 = nullptr;
    std::vector<ReplaceOp*> prevReplaces = getKindOfOps(rewrites, type2, i);
    for (auto prevRop : prevReplaces) {
      if (prevRop->index >= rop->index && prevRop->lastIndex <= rop->lastIndex) {
        // delete replace as it's a no-op.
        rewrites[(size_t)prevRop->instructionIndex] = nullptr;
        continue;
      }
      // throw exception unless disjoint or identical
      bool disjoint = prevRop->lastIndex < rop->index || prevRop->index > rop->lastIndex;
      bool same = prevRop->index == rop->index && prevRop->lastIndex == rop->lastIndex;
      // Delete special case of replace (text==null):
      // D.i-j.u D.x-y.v    | boundaries overlap    combine to max(min)..max(right)
      if (prevRop->text.empty() && rop->text.empty() && !disjoint) {
        //System.out.println("overlapping deletes: "+prevRop+", "+rop);
        rewrites[(size_t)prevRop->instructionIndex] = nullptr; // kill first delete
        rop->index = std::min(prevRop->index, rop->index);
        rop->lastIndex = std::max(prevRop->lastIndex, rop->lastIndex);
        std::wcout << L"new rop " << rop << std::endl;
      }
      else if (!disjoint && !same) {
        throw IllegalArgumentException("replace op boundaries of " + antlrcpp::ws2s(rop->toString()) +
                                       " overlap with previous " + antlrcpp::ws2s(prevRop->toString()));
      }
    }
  }

  // WALK INSERTS
  for (size_t i = 0; i < rewrites.size(); i++) {
    RewriteOperation *op = rewrites[i];
    if (op == nullptr) {
      continue;
    }
    if (!is<InsertBeforeOp*>(op)) {
      continue;
    }
    InsertBeforeOp *iop = static_cast<InsertBeforeOp*>(rewrites[i]);
    // combine current insert with prior if any at same index

    std::vector<InsertBeforeOp*> prevInserts = getKindOfOps(rewrites, iop, i);
    for (auto prevIop : prevInserts) {
      if (prevIop->index == iop->index) { // combine objects
                                          // convert to strings...we're in process of toString'ing
                                          // whole token buffer so no lazy eval issue with any templates
        iop->text = catOpText(&iop->text, &prevIop->text);
        // delete redundant prior insert
        rewrites[(size_t)prevIop->instructionIndex] = nullptr;
      }
    }
    // look for replaces where iop.index is in range; error
    ReplaceOp *type = nullptr;
    std::vector<ReplaceOp*> prevReplaces = getKindOfOps(rewrites, type, i);
    for (auto rop : prevReplaces) {
      if (iop->index == rop->index) {
        rop->text = catOpText(&iop->text, &rop->text);
        rewrites[i] = nullptr; // delete current insert
        continue;
      }
      if (iop->index >= rop->index && iop->index <= rop->lastIndex) {
        throw IllegalArgumentException("insert op " + antlrcpp::ws2s(iop->toString()) + " within boundaries of previous " + antlrcpp::ws2s(rop->toString()));
      }
    }
  }

  std::unordered_map<size_t, TokenStreamRewriter::RewriteOperation*> m;
  for (TokenStreamRewriter::RewriteOperation *op : rewrites) {
    if (op == nullptr) { // ignore deleted ops
      continue;
    }
    if (m.count(op->index) > 0) {
      throw RuntimeException("should only be one op per index");
    }
    m[op->index] = op;
  }

  return m;
}

std::wstring TokenStreamRewriter::catOpText(std::wstring *a, std::wstring *b) {
  std::wstring x = L"";
  std::wstring y = L"";
  if (a != nullptr) {
    x = std::wstring(*a);
  }
  if (b != nullptr) {
    y = std::wstring(*b);
  }
  return x + y;
}
