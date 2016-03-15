#pragma once

#include <string>

#include "Declarations.h"
#include "ATNState.h"
#include "PredictionContext.h"
#include "SemanticContext.h"

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
                namespace atn {

                    /// <summary>
                    /// A tuple: (ATN state, predicted alt, syntactic, semantic context).
                    ///  The syntactic context is a graph-structured stack node whose
                    ///  path(s) to the root is the rule invocation(s)
                    ///  chain used to arrive at the state.  The semantic context is
                    ///  the tree of semantic predicates encountered before reaching
                    ///  an ATN state.
                    /// </summary>
                    class ATNConfig {
                        /// <summary>
                        /// The ATN state associated with this configuration </summary>
                    public:
                        ATNState * state;

                        /// <summary>
                        /// What alt (or lexer rule) is predicted by this configuration </summary>
                        const int alt;

                        /// <summary>
                        /// The stack of invoking states leading to the rule/states associated
                        ///  with this config.  We track only those contexts pushed during
                        ///  execution of the ATN simulator.
                        /// </summary>
                        PredictionContext *context;

                        /// <summary>
                        /// We cannot execute predicates dependent upon local context unless
                        /// we know for sure we are in the correct context. Because there is
                        /// no way to do this efficiently, we simply cannot evaluate
                        /// dependent predicates unless we are in the rule that initially
                        /// invokes the ATN simulator.
                        /// 
                        /// closure() tracks the depth of how far we dip into the
                        /// outer context: depth > 0.  Note that it may not be totally
                        /// accurate depth since I don't ever decrement. TODO: make it a boolean then
                        /// </summary>
                        int reachesIntoOuterContext;

                        SemanticContext *const semanticContext;

                        ATNConfig(ATNConfig *old); // dup

                        ATNConfig(ATNState *state, int alt, PredictionContext *context); //this(state, alt, context, SemanticContext.NONE);

                        ATNConfig(ATNState *state, int alt, PredictionContext *context, SemanticContext *semanticContext);

                        ATNConfig(ATNConfig *c, ATNState *state); //this(c, state, c.context, c.semanticContext);

                        ATNConfig(ATNConfig *c, ATNState *state, SemanticContext *semanticContext); //this(c, state, c.context, semanticContext);

                        ATNConfig(ATNConfig *c, SemanticContext *semanticContext); //this(c, c.state, c.context, semanticContext);

                        ATNConfig(ATNConfig *c, ATNState *state, PredictionContext *context); //this(c, state, context, c.semanticContext);

                        ATNConfig(ATNConfig *c, ATNState *state, PredictionContext *context, SemanticContext *semanticContext);

                        /// <summary>
                        /// An ATN configuration is equal to another if both have
                        ///  the same state, they predict the same alternative, and
                        ///  syntactic/semantic contexts are the same.
                        /// </summary>
                        virtual bool equals(void *o);

                        virtual bool equals(ATNConfig *other);

                        virtual size_t hashCode();


						struct ATNConfigHasher
						{
							size_t operator()(const ATNConfig& k) const
							{
								ATNConfig a = k;
								return a.hashCode();
							}
						};

						bool operator==(const ATNConfig& other) const
						{
							return alt == other.alt && state->stateNumber == other.state->stateNumber &&
								((context == nullptr && other.context == nullptr) || (context != nullptr && context->equals(other.context))) && semanticContext->equals(other.semanticContext);
						}
					
                        virtual std::wstring toString();

                        template<typename T1, typename T2>
                        std::wstring toString(Recognizer<T1, T2> *recog, bool showAlt) {
                            antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                            //		if ( state.ruleIndex>=0 ) {
                            //			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
                            //			else buf.append(state.ruleIndex+":");
                            //		}
                            buf->append(L'(');
                            buf->append(state);
                            if (showAlt) {
                                buf->append(L",");
                                buf->append(alt);
                            }
                            if (context != nullptr) {
                                buf->append(L",[");
                                //                            :
                                buf->append(context->toString());
                                buf->append(L"]");
                            }
                            if (semanticContext != nullptr && semanticContext != SemanticContext::NONE) {
                                buf->append(L",");
                                buf->append(semanticContext);
                            }
                            if (reachesIntoOuterContext > 0) {
                                buf->append(L",up=").append(reachesIntoOuterContext);
                            }
                            buf->append(L')');
                            
                            return buf->toString();

                        }

                    private:
                        void InitializeInstanceFields();
                    };

                }

            }
        }
    }
}

// Hash function for ATNConfig, used in the MurmurHash::update function

namespace std {
    using org::antlr::v4::runtime::atn::ATNConfig;
    
    template <> struct hash<ATNConfig>
    {
        size_t operator()( ATNConfig & x) const
        {
            return x.hashCode();
        }
    };
}
