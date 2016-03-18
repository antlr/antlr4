#pragma once

#include <string>
#include <vector>
#include <algorithm>

#include "RuleContext.h"
#include "Declarations.h"

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
namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {


                /// <summary>
                /// A rule invocation record for parsing.
                /// 
                ///  Contains all of the information about the current rule not stored in the
                ///  RuleContext. It handles parse tree children list, Any ATN state
                ///  tracing, and the default values available for rule indications:
                ///  start, stop, rule index, current alt number, current
                ///  ATN state.
                /// 
                ///  Subclasses made for each rule and grammar track the parameters,
                ///  return values, locals, and labels specific to that rule. These
                ///  are the objects that are returned from rules.
                /// 
                ///  Note text is not an actual field of a rule return value; it is computed
                ///  from start and stop using the input stream's toString() method.  I
                ///  could add a ctor to this so that we can pass in and store the input
                ///  stream, but I'm not sure we want to do that.  It would seem to be undefined
                ///  to get the .text property anyway if the rule matches tokens from multiple
                ///  input streams.
                /// 
                ///  I do not use getters for fields of objects that are used simply to
                ///  group values such as this aggregate.  The getters/setters are there to
                ///  satisfy the superclass interface.
                /// </summary>
                class ParserRuleContext : public RuleContext {
                    /// <summary>
                    /// If we are debugging or building a parse tree for a visitor,
                    ///  we need to track all of the tokens and rule invocations associated
                    ///  with this rule's context. This is empty for parsing w/o tree constr.
                    ///  operation because we don't the need to track the details about
                    ///  how we parse this rule.
                    /// </summary>
                public:
                    std::vector<ParseTree*> _children;

                    /// <summary>
                    /// For debugging/tracing purposes, we want to track all of the nodes in
                    ///  the ATN traversed by the parser for a particular rule.
                    ///  This list indicates the sequence of ATN nodes used to match
                    ///  the elements of the children list. This list does not include
                    ///  ATN nodes and other rules used to match rule invocations. It
                    ///  traces the rule invocation node itself but nothing inside that
                    ///  other rule's ATN submachine.
                    /// 
                    ///  There is NOT a one-to-one correspondence between the children and
                    ///  states list. There are typically many nodes in the ATN traversed
                    ///  for each element in the children list. For example, for a rule
                    ///  invocation there is the invoking state and the following state.
                    /// 
                    ///  The parser setState() method updates field s and adds it to this list
                    ///  if we are debugging/tracing.
                    ///    
                    ///  This does not trace states visited during prediction.
                    /// </summary>
                //	public List<Integer> states;

                    Token *start, *stop;

                    /// <summary>
                    /// The exception that forced this rule to return. If the rule successfully
                    /// completed, this is {@code null}.
                    /// </summary>
                    RecognitionException *exception;

                    ParserRuleContext();
                    virtual ~ParserRuleContext() {}

                    /// <summary>
                    /// COPY a ctx (I'm deliberately not copy constructor) </summary>
                    virtual void copyFrom(ParserRuleContext *ctx);

                    ParserRuleContext(ParserRuleContext *parent, int invokingStateNumber);

                    // Double dispatch methods for listeners

                    virtual void enterRule(tree::ParseTreeListener *listener);
                    virtual void exitRule(tree::ParseTreeListener *listener);

                    /// <summary>
                    /// Does not set parent link; other add methods do that </summary>
                    virtual tree::TerminalNode* addChild(tree::TerminalNode *t);

                    virtual RuleContext* addChild(RuleContext *ruleInvocation);

                    /// <summary>
                    /// Used by enterOuterAlt to toss out a RuleContext previously added as
                    ///  we entered a rule. If we have # label, we will need to remove
                    ///  generic ruleContext object.
                    /// </summary>
                    virtual void removeLastChild();

                    virtual tree::TerminalNode* addChild(Token *matchedToken);

                    virtual tree::ErrorNode* addErrorNode(Token *badToken);

                    virtual ParserRuleContext* getParent() override;

                    virtual ParseTree* getChild(std::size_t i) override;

                    template<typename T>
                    T* getChild(size_t i) {
                        if (_children.empty() || i >= _children.size()) {
                            return nullptr;
                        }
                        
                        int j = -1; // what element have we found with ctxType?
                        for (auto &child : _children) {
                            if (dynamic_cast<T *>(child) != nullptr) {
                                j++;
                                if (j == i) {
                                    return dynamic_cast<T *>(child);
                                }
                            }
                        }
                        return nullptr;
                    }

                  virtual tree::TerminalNode* getToken(int ttype, std::size_t i);

                    virtual std::vector<tree::TerminalNode *> getTokens(int ttype);

                    template<typename T>
                    T* getRuleContext(int i) {
                        return getChild<T>(i);
                    }

                    template<typename T>
                    std::vector<T*> getRuleContexts() {
                        if (_children.empty()) {
                            return std::vector<T *>();
                        }
                        
                        std::vector<T *> contexts;
                        for (auto &child : _children) {
                            if (dynamic_cast<T *>(child)) {
                                if (contexts.empty()) {
                                    contexts = std::vector<T *>();
                                }
                                
                                contexts.push_back((T *)(child));
                            }
                        }
                        
                        if (contexts.empty()) {
                            return std::vector<T *>();
                        }
                        
                        return contexts;
                    }

                    virtual std::size_t getChildCount() override;
                    virtual misc::Interval *getSourceInterval() override;

                    virtual Token *getStart();
                    virtual Token *getStop();

                    /// <summary>
                    /// Used for rule context info debugging during parse-time, not so much for ATN debugging </summary>
                    virtual std::wstring toInfoString(Parser *recognizer);
                };

            }
        }
    }
}
