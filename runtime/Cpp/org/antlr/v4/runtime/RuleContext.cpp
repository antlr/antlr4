#include <string>
#include <vector>

#include "RuleContext.h"
#include "Trees.h"
#include "Interval.h"
#include "StringBuilder.h"
#include "ParserRuleContext.h"
#include "Parser.h"
#include "Arrays.h"

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

                ParserRuleContext *const RuleContext::EMPTY = new ParserRuleContext();

                RuleContext::RuleContext() {
                    InitializeInstanceFields();
                }

                RuleContext::RuleContext(RuleContext *parent, int invokingState) {
                    InitializeInstanceFields();
                    this->parent = parent;
                    //if ( parent!=null ) System.out.println("invoke "+stateNumber+" from "+parent);
                    this->invokingState = invokingState;
                }

                int RuleContext::depth() {
                    int n = 0;
                    RuleContext *p = this;
                    while (p != nullptr) {
                        p = p->parent;
                        n++;
                    }
                    return n;
                }

                bool RuleContext::isEmpty() {
                    return invokingState == -1;
                }

                misc::Interval *RuleContext::getSourceInterval() {
                    return misc::Interval::INVALID;
                }

                RuleContext *RuleContext::getRuleContext() {
                    return this;
                }

                RuleContext *RuleContext::getParent() {
                    return parent;
                }

                void *RuleContext::getPayload()
                /// <summary>
                /// Return the combined text of all child nodes. This method only considers
                ///  tokens which have been added to the parse tree.
                ///  <para>
                ///  Since tokens on hidden channels (e.g. whitespace or comments) are not
                ///  added to the parse trees, they will not appear in the output of this
                ///  method.
                /// </para>
                /// </summary>
                {
                    return this;
                }

                std::wstring RuleContext::getText() {
                    if (getChildCount() == 0) {
                        return L"";
                    }

                    antlrcpp::StringBuilder *builder = new antlrcpp::StringBuilder();
                    for (int i = 0; i < getChildCount(); i++) {
                        builder->append(getChild(i)->getText());
                    }

                    return builder->toString();
                }

                int RuleContext::getRuleIndex() {
                    return -1;
                }

                tree::ParseTree *RuleContext::getChild(int i) {
                    return nullptr;
                }

                int RuleContext::getChildCount() {
                    return 0;
                }

#ifdef TODO
                Future<JDialog*> *RuleContext::inspect(Parser *parser) {
                    return inspect(parser->getRuleNames());
                }


                Future<JDialog*> *RuleContext::inspect(const std::vector<std::wstring> &ruleNames) {
                    TreeViewer *viewer = new TreeViewer(ruleNames, this);
                    return viewer->open();
                }
#endif
                void RuleContext::save(Parser *parser, const std::wstring &fileName) {
                    std::vector<std::wstring> ruleNames;
                    if (parser != nullptr) {
                        ruleNames = parser->getRuleNames();
                    }
                    save(ruleNames, fileName);
                }

                void RuleContext::save(Parser *parser, const std::wstring &fileName, const std::wstring &fontName, int fontSize) {
                    std::vector<std::wstring> ruleNames;
                    if (parser != nullptr) {
                        ruleNames = parser->getRuleNames();
                    }
                    save(ruleNames, fileName, fontName, fontSize);
                }

                void RuleContext::save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName) {
#ifdef TODO
                    tree::Trees::writePS(this, ruleNames, fileName);
#endif
                }

                void RuleContext::save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName, const std::wstring &fontName, int fontSize) {
#ifdef TODO
                    tree::Trees::writePS(this, ruleNames, fileName, fontName, fontSize);
#endif
                }

                std::wstring RuleContext::toStringTree(Parser *recog) {
                    return tree::Trees::toStringTree(this, recog);
                }

                std::wstring RuleContext::toStringTree(std::vector<std::wstring> &ruleNames) {
                    return tree::Trees::toStringTree(this, ruleNames);
                }

                std::wstring RuleContext::toStringTree() {
                    return toStringTree(nullptr);
                }


                std::wstring RuleContext::toString(const std::vector<std::wstring> &ruleNames) {
                    return toString(ruleNames, static_cast<RuleContext*>(nullptr));
                }


                std::wstring RuleContext::toString(const std::vector<std::wstring> &ruleNames, RuleContext *stop) {
                    antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                    RuleContext *p = this;
                    buf->append(L"[");
                    while (p != nullptr && p != stop) {
                        if (ruleNames.empty()) {
                            if (!p->isEmpty()) {
                                buf->append(p->invokingState);
                            }
                        } else {
                            int ruleIndex = p->getRuleIndex();

                            std::wstring ruleName = ruleIndex >= 0 && ruleIndex < (int)ruleNames.size() ? ruleNames[ruleIndex] : std::to_wstring(ruleIndex);
                            buf->append(ruleName);
                        }

                        if (p->parent != nullptr && (ruleNames.size() > 0 || !p->parent->isEmpty())) {
                            buf->append(L" ");
                        }

                        p = p->parent;
                    }

                    buf->append(L"]");
    
                    return buf->toString();
                }

				std::wstring RuleContext::toString() {
#ifdef TODO
#endif
					return L"TODO";
				};

                void RuleContext::InitializeInstanceFields() {
                    invokingState = -1;
                }
            }
        }
    }
}
