#pragma once

#include <string>
#include <vector>
#include <set>

#include "Declarations.h"
#include "TerminalNode.h"
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
                    /// <summary>
                    /// A set of utility routines useful for all kinds of ANTLR trees. </summary>
                    class Trees {

                    public:
#ifdef TODO
                        static std::wstring getPS(Tree *t, std::vector<std::wstring> &ruleNames, const std::wstring &fontName, int fontSize);

                        static std::wstring getPS(Tree *t, std::vector<std::wstring> &ruleNames);

                        static void writePS(Tree *t, std::vector<std::wstring> &ruleNames, const std::wstring &fileName, const std::wstring &fontName, int fontSize); //throw(IOException);

                        static void writePS(Tree *t, std::vector<std::wstring> &ruleNames, const std::wstring &fileName);// throw(IOException);
#endif
                        /// <summary>
                        /// Print out a whole tree in LISP form. <seealso cref="#getNodeText"/> is used on the
                        ///  node payloads to get the text for the nodes.  Detect
                        ///  parse trees and extract data appropriately.
                        /// </summary>
                        static std::wstring toStringTree(Tree *t);

                        /// <summary>
                        /// Print out a whole tree in LISP form. <seealso cref="#getNodeText"/> is used on the
                        ///  node payloads to get the text for the nodes.  Detect
                        ///  parse trees and extract data appropriately.
                        /// </summary>
                        static std::wstring toStringTree(Tree *t, Parser *recog);

                        /// <summary>
                        /// Print out a whole tree in LISP form. <seealso cref="#getNodeText"/> is used on the
                        ///  node payloads to get the text for the nodes.  Detect
                        ///  parse trees and extract data appropriately.
                        /// </summary>
                        static std::wstring toStringTree(Tree *t, const std::vector<std::wstring> &ruleNames);

                        static std::wstring getNodeText(Tree *t, Parser *recog);

                        static std::wstring getNodeText(Tree *t, const std::vector<std::wstring> &ruleNames);


                        /// <summary>
                        /// Return ordered list of all children of this node </summary>
                        static std::vector<Tree*> getChildren(Tree *t);

                        /// <summary>
                        /// Return a list of all ancestors of this node.  The first node of
                        ///  list is the root and the last is the parent of this node.
                        /// </summary>
                        static std::vector<Tree*> getAncestors(Tree *t);

                        static std::vector<ParseTree*> *findAllTokenNodes(ParseTree *t, int ttype);

                        static std::vector<ParseTree*> *findAllRuleNodes(ParseTree *t, int ruleIndex);

                        static std::vector<ParseTree*> *findAllNodes(ParseTree *t, int index, bool findTokens);

                        template<typename T1>
                        static void _findAllNodes(ParseTree *t, int index, bool findTokens, std::vector<T1> nodes) {
                            // check this node (the root) first
                            if (findTokens && (TerminalNode*)(t) != nullptr) {
                                TerminalNode *tnode = (TerminalNode*)(t);
                                if (tnode->getSymbol()->getType() == index) {
                                    nodes.push_back(t);
                                }
                            } else if (!findTokens && (ParserRuleContext*)(t) != nullptr) {
                                ParserRuleContext *ctx = (ParserRuleContext*)(t);
                                if (ctx->getRuleIndex() == index) {
                                    nodes.push_back(t);
                                }
                            }
                            // check children
                            for (int i = 0; i < t->getChildCount(); i++) {
                                _findAllNodes(t->getChild(i), index, findTokens, nodes);
                            }
                        }

                        static std::vector<ParseTree*>* descendants(ParseTree *t);

                    private:
                        Trees();
                    };

                }
            }
        }
    }
}
