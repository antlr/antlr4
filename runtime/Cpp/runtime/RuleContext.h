#pragma once

#include <string>
#include <vector>

#include "RuleNode.h"
#include "ParseTreeVisitor.h"
#include "Recognizer.h"
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
                /// A rule context is a record of a single rule invocation. It knows
                ///  which context invoked it, if any. If there is no parent context, then
                ///  naturally the invoking state is not valid.  The parent link
                ///  provides a chain upwards from the current rule invocation to the root
                ///  of the invocation tree, forming a stack. We actually carry no
                ///  information about the rule associated with this context (except
                ///  when parsing). We keep only the state number of the invoking state from
                ///  the ATN submachine that invoked this. Contrast this with the s
                ///  pointer inside ParserRuleContext that tracks the current state
                ///  being "executed" for the current rule.
                /// 
                ///  The parent contexts are useful for computing lookahead sets and
                ///  getting error information.
                /// 
                ///  These objects are used during parsing and prediction.
                ///  For the special case of parsers, we use the subclass
                ///  ParserRuleContext.
                /// </summary>
                ///  <seealso cref= ParserRuleContext </seealso>
                class RuleContext : public tree::RuleNode {
                public:
                    static ParserRuleContext *const EMPTY;

                    /// <summary>
                    /// What context invoked this rule? </summary>
                    RuleContext *parent;

                    /// <summary>
                    /// What state invoked the rule associated with this context?
                    ///  The "return address" is the followState of invokingState
                    ///  If parent is null, this should be -1.
                    /// </summary>
                    int invokingState;

                    RuleContext();

                    RuleContext(RuleContext *parent, int invokingState);

                    virtual int depth();

                    /// <summary>
                    /// A context is empty if there is no invoking state; meaning nobody call
                    ///  current context.
                    /// </summary>
                    virtual bool isEmpty();

                    // satisfy the ParseTree / SyntaxTree interface

                    virtual misc::Interval *getSourceInterval() override;

                    virtual RuleContext *getRuleContext() override;
                    virtual RuleContext *getParent() override;
                    virtual void *getPayload() override;
                    virtual std::wstring getText() override;

                    virtual int getRuleIndex();

                    virtual ParseTree *getChild(std::size_t i) override;

                  virtual std::size_t getChildCount() override;

                    template<typename T, typename T1>
                    T accept(tree::ParseTreeVisitor<T1> *visitor)  {
                        return visitor->visitChildren(this);
                    }

                    /// <summary>
                    /// Save this tree in a postscript file </summary>
                    virtual void save(Parser *parser, const std::wstring &fileName);

                    /// <summary>
                    /// Save this tree in a postscript file using a particular font name and size </summary>
                    virtual void save(Parser *parser, const std::wstring &fileName, const std::wstring &fontName, int fontSize);

                    /// <summary>
                    /// Save this tree in a postscript file </summary>
                    virtual void save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName);

                    /// <summary>
                    /// Save this tree in a postscript file using a particular font name and size </summary>
                    virtual void save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName, const std::wstring &fontName, int fontSize);

                    /// <summary>
                    /// Print out a whole tree, not just a node, in LISP format
                    ///  (root child1 .. childN). Print just a node if this is a leaf.
                    ///  We have to know the recognizer so we can get rule names.
                    /// </summary>
                    virtual std::wstring toStringTree(Parser *recog) override;

                    /// <summary>
                    /// Print out a whole tree, not just a node, in LISP format
                    ///  (root child1 .. childN). Print just a node if this is a leaf.
                    /// </summary>
                    virtual std::wstring toStringTree(std::vector<std::wstring> &ruleNames);

                    virtual std::wstring toStringTree() override;

                    virtual std::wstring toString() override;

                    
                    template<typename T1, typename T2>
					std::wstring toString(Recognizer<T1, T2> *recog) {
                        // Circular include issue, TODO
						//return toString(recog, ParserRuleContext::EMPTY);
                        return toString(recog, nullptr);
					}

                    std::wstring toString(const std::vector<std::wstring> &ruleNames);

                    // recog null unless ParserRuleContext, in which case we use subclass toString(...)
                    template<typename T1, typename T2>
					std::wstring toString(Recognizer<T1, T2> *recog, RuleContext *stop) {
						return toString(recog->getRuleNames(), stop);
					}

					std::wstring toString(Token *, atn::ParserATNSimulator *) {
						return L"TODO";
					}

                    virtual std::wstring toString(const std::vector<std::wstring> &ruleNames, RuleContext *stop);

                private:
                    void InitializeInstanceFields();
                };

            }
        }
    }
}
