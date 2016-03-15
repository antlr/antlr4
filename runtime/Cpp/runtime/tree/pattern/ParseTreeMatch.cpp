#include "ParseTreeMatch.h"
#include "Exceptions.h"

/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace pattern {

                        ParseTreeMatch::ParseTreeMatch(ParseTree *tree, ParseTreePattern *pattern, misc::MultiMap<std::wstring, ParseTree*> *labels, ParseTree *mismatchedNode) : tree(tree), pattern(pattern), labels(labels), mismatchedNode(mismatchedNode) {
                            if (tree == nullptr) {
                                throw IllegalArgumentException(L"tree cannot be null");
                            }

                            if (pattern == nullptr) {
                                throw IllegalArgumentException(L"pattern cannot be null");
                            }

                            if (labels == nullptr) {
                                throw IllegalArgumentException(L"labels cannot be null");
                            }

                        }

                        org::antlr::v4::runtime::tree::ParseTree *ParseTreeMatch::get(const std::wstring &label) {
                            std::vector<ParseTree*> parseTrees = labels->at(label);
                            if (parseTrees.empty()) {
                                return nullptr;
                            }

                            return parseTrees[parseTrees.size() - 1]; // return last if multiple
                        }

                        std::vector<ParseTree*> ParseTreeMatch::getAll(const std::wstring &label) {
                            std::vector<ParseTree*> nodes = labels->at(label);
                            if (nodes.empty()) {
                                return std::vector<ParseTree*>();// Collections::emptyList();
                            }

                            return nodes;
                        }

                        org::antlr::v4::runtime::misc::MultiMap<std::wstring, ParseTree*> *ParseTreeMatch::getLabels() {
                            return labels;
                        }

                        org::antlr::v4::runtime::tree::ParseTree *ParseTreeMatch::getMismatchedNode() {
                            return mismatchedNode;
                        }

                        bool ParseTreeMatch::succeeded() {
                            return mismatchedNode == nullptr;
                        }

                        org::antlr::v4::runtime::tree::pattern::ParseTreePattern *ParseTreeMatch::getPattern() {
                            return pattern;
                        }

                        org::antlr::v4::runtime::tree::ParseTree *ParseTreeMatch::getTree() {
                            return tree;
                        }

                        std::wstring ParseTreeMatch::toString() {
                            if (succeeded()) {
                                return L"Match succeeded; found " + std::to_wstring(getLabels()->size()) + L" labels";
                            } else {
                                return L"Match failed; found " + std::to_wstring(getLabels()->size()) + L" labels";
                            }
                        }
                    }
                }
            }
        }
    }
}
