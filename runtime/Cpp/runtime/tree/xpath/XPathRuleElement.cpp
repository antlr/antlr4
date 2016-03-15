#include "XPathRuleElement.h"
#include "ParseTree.h"
#include "Trees.h"
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
                    namespace xpath {


                        XPathRuleElement::XPathRuleElement(const std::wstring &ruleName, int ruleIndex) : XPathElement(ruleName) {
                            InitializeInstanceFields();
                            this->ruleIndex = ruleIndex;
                        }

                        std::vector<ParseTree*> *XPathRuleElement::evaluate(ParseTree *t) {
                                    // return all children of t that match nodeName
                            std::vector<ParseTree*> *nodes = new std::vector<ParseTree*>();
                            for (auto c : Trees::getChildren(t)) {
                                if (dynamic_cast<ParserRuleContext*>(c) != nullptr) {
                                    ParserRuleContext *ctx = static_cast<ParserRuleContext*>(c);
                                    if ((((RuleContext*)ctx)->getRuleIndex() == ruleIndex && !invert) || (((RuleContext*)ctx)->getRuleIndex() != ruleIndex && invert)) {
                                        nodes->push_back((ParseTree*)ctx);
                                        
                                    }
                                }
                            }
                            return nodes;
                        }

                        void XPathRuleElement::InitializeInstanceFields() {
                            ruleIndex = 0;
                        }
                    }
                }
            }
        }
    }
}
