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

#include "tree/ErrorNode.h"
#include "ParserRuleContext.h"
#include "tree/ParseTreeListener.h"
#include "support/CPPUtils.h"

#include "tree/ParseTreeWalker.h"

using namespace antlr4::tree;
using namespace antlrcpp;

ParseTreeWalker ParseTreeWalker::DEFAULT;

void ParseTreeWalker::walk(ParseTreeListener *listener, ParseTree *t) const {
  if (is<ErrorNode *>(t)) {
    listener->visitErrorNode(dynamic_cast<ErrorNode *>(t));
    return;
  } else if (is<TerminalNode *>(t)) {
    listener->visitTerminal(dynamic_cast<TerminalNode *>(t));
    return;
  }

  enterRule(listener, t);
  for (auto &child : t->children) {
    walk(listener, dynamic_cast<ParseTree *>(child));
  }
  exitRule(listener, t);
}

void ParseTreeWalker::enterRule(ParseTreeListener *listener, ParseTree *r) const {
  ParserRuleContext *ctx = dynamic_cast<ParserRuleContext *>(r);
  listener->enterEveryRule(ctx);
  ctx->enterRule(listener);
}

void ParseTreeWalker::exitRule(ParseTreeListener *listener, ParseTree *r) const {
  ParserRuleContext *ctx = dynamic_cast<ParserRuleContext *>(r);
  ctx->exitRule(listener);
  listener->exitEveryRule(ctx);
}
