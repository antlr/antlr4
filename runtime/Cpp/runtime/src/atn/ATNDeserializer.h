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

#pragma once

#include "atn/LexerAction.h"
#include "atn/ATNDeserializationOptions.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC ATNDeserializer {
  public:
    static const size_t SERIALIZED_VERSION;

    /// This is the current serialized UUID.
    // ml: defined as function to avoid the “static initialization order fiasco”.
    static Guid SERIALIZED_UUID();

    ATNDeserializer();
    ATNDeserializer(const ATNDeserializationOptions& dso);
    virtual ~ATNDeserializer() {};

    static Guid toUUID(const unsigned short *data, int offset);

    virtual ATN deserialize(const std::vector<uint16_t> &input);
    virtual void verifyATN(const ATN &atn);

    static void checkCondition(bool condition);
    static void checkCondition(bool condition, const std::string &message);

    static Transition *edgeFactory(const ATN &atn, int type, int src, int trg, int arg1, int arg2, int arg3,
                                   const std::vector<misc::IntervalSet> &sets);

    static ATNState *stateFactory(int type, int ruleIndex);

  protected:
    /// Determines if a particular serialized representation of an ATN supports
    /// a particular feature, identified by the <seealso cref="UUID"/> used for serializing
    /// the ATN at the time the feature was first introduced.
    ///
    /// <param name="feature"> The <seealso cref="UUID"/> marking the first time the feature was
    /// supported in the serialized ATN. </param>
    /// <param name="actualUuid"> The <seealso cref="UUID"/> of the actual serialized ATN which is
    /// currently being deserialized. </param>
    /// <returns> {@code true} if the {@code actualUuid} value represents a
    /// serialized ATN at or after the feature identified by {@code feature} was
    /// introduced; otherwise, {@code false}. </returns>
    virtual bool isFeatureSupported(const Guid &feature, const Guid &actualUuid);
    void markPrecedenceDecisions(const ATN &atn);
    Ref<LexerAction> lexerActionFactory(LexerActionType type, int data1, int data2);

  private:
    /// This is the earliest supported serialized UUID.
    static Guid BASE_SERIALIZED_UUID();

    /// This UUID indicates an extension of <seealso cref="BASE_SERIALIZED_UUID"/> for the
    /// addition of precedence predicates.
    static Guid ADDED_PRECEDENCE_TRANSITIONS();

    /**
     * This UUID indicates an extension of ADDED_PRECEDENCE_TRANSITIONS
     * for the addition of lexer actions encoded as a sequence of
     * LexerAction instances.
     */
    static Guid ADDED_LEXER_ACTIONS();

    /// This list contains all of the currently supported UUIDs, ordered by when
    /// the feature first appeared in this branch.
    static std::vector<Guid>& SUPPORTED_UUIDS();
    
    ATNDeserializationOptions deserializationOptions;
  };

} // namespace atn
} // namespace antlr4
