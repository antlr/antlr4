#include "ParseTreeWalker.h"
#include "TerminalNode.h"
#include "ErrorNode.h"
#include "ParserRuleContext.h"

/*
* [The "BSD license"]
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


namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {

                    ParseTreeWalker *const ParseTreeWalker::DEFAULT = new ParseTreeWalker();

                    void ParseTreeWalker::walk(ParseTreeListener *listener, ParseTree *t) {
                        if (dynamic_cast<ErrorNode*>(t) != nullptr) {
                            listener->visitErrorNode(dynamic_cast<ErrorNode*>(t));
                            return;
                        } else if (dynamic_cast<TerminalNode*>(t) != nullptr) {
                            listener->visitTerminal(static_cast<TerminalNode*>(t));
                            return;
                        }
                        RuleNode *r = static_cast<RuleNode*>(t);
                        enterRule(listener, r);
                        int n = r->getChildCount();
                        for (int i = 0; i < n; i++) {
                            walk(listener, r->getChild(i));
                        }
                        exitRule(listener, r);
                    }

                    void ParseTreeWalker::enterRule(ParseTreeListener *listener, RuleNode *r) {
                        ParserRuleContext *ctx = dynamic_cast<ParserRuleContext*>(r->getRuleContext());
                        listener->enterEveryRule(ctx);
                        ctx->enterRule(listener);
                    }

                    void ParseTreeWalker::exitRule(ParseTreeListener *listener, RuleNode *r) {
                        ParserRuleContext *ctx = dynamic_cast<ParserRuleContext*>(r->getRuleContext());
                        ctx->exitRule(listener);
                        listener->exitEveryRule(ctx);
                    }
                }
            }
        }
    }
}
