#include "ParseTreePattern.h"
#include "XPath.h"
#include "ParseTreePatternMatcher.h"
#include "ParseTreeMatch.h"
#include "ParseTree.h"
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

                        ParseTreePattern::ParseTreePattern(ParseTreePatternMatcher *matcher, const std::wstring &pattern, int patternRuleIndex, ParseTree *patternTree) : patternRuleIndex(patternRuleIndex), pattern(pattern), patternTree(patternTree), matcher(matcher) {
                        }

                        tree::pattern::ParseTreeMatch *ParseTreePattern::match(ParseTree *tree) {
                            return matcher->match(tree, this);
                        }

                        bool ParseTreePattern::matches(ParseTree *tree) {
                            return matcher->match(tree, this)->succeeded();
                        }

// TODO:  Come back to this after the base runtime works.
#if 0
                        std::vector<ParseTreeMatch*> ParseTreePattern::findAll(ParseTree *tree, const std::wstring &xpath) {
                            std::vector<ParseTree*> *subtrees = xpath::XPath::findAll(tree, xpath, matcher->getParser());
                            std::vector<ParseTreeMatch*> matches = std::vector<ParseTreeMatch*>();
                            for (auto t : *subtrees) {
                                ParseTreeMatch *aMatch = match(t);
                                if (aMatch->succeeded()) {
                                    matches.push_back(aMatch);
                                }
                            }
                            return matches;
                        }
#endif
                        
                        tree::pattern::ParseTreePatternMatcher *ParseTreePattern::getMatcher() {
                            return matcher;
                        }

                        std::wstring ParseTreePattern::getPattern() {
                            return pattern;
                        }

                        int ParseTreePattern::getPatternRuleIndex() {
                            return patternRuleIndex;
                        }

                        tree::ParseTree *ParseTreePattern::getPatternTree() {
                            return patternTree;
                        }
                    }
                }
            }
        }
    }
}
