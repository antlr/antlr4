#pragma once

#include "UUID.h"
#include "Declarations.h"

#include <string>
#include <vector>


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

                    /// 
                    /// <summary>
                    /// @author Sam Harwell
                    /// </summary>
                    class ATNDeserializer {
                    public:
						static const int SERIALIZED_VERSION;
                        //static ATNDeserializer();

                        /// <summary>
                        /// This is the earliest supported serialized UUID.
                        /// </summary>
                    private:
                        static antlrcpp::UUID *const BASE_SERIALIZED_UUID;
                        /// <summary>
                        /// This UUID indicates an extension of <seealso cref="BASE_SERIALIZED_UUID"/> for the
                        /// addition of precedence predicates.
                        /// </summary>
                        static antlrcpp::UUID *const ADDED_PRECEDENCE_TRANSITIONS;
                        /// <summary>
                        /// This list contains all of the currently supported UUIDs, ordered by when
                        /// the feature first appeared in this branch.
                        /// </summary>
                        static const std::vector<antlrcpp::UUID*> SUPPORTED_UUIDS;

                        /// <summary>
                        /// This is the current serialized UUID.
                        /// </summary>
                    public:
                        static antlrcpp::UUID *const SERIALIZED_UUID;

                    private:
                        ATNDeserializationOptions *const deserializationOptions;

                    public:
                        ATNDeserializer();
                        ATNDeserializer(ATNDeserializationOptions *dso);

                        /// <summary>
                        /// Determines if a particular serialized representation of an ATN supports
                        /// a particular feature, identified by the <seealso cref="UUID"/> used for serializing
                        /// the ATN at the time the feature was first introduced.
                        /// </summary>
                        /// <param name="feature"> The <seealso cref="UUID"/> marking the first time the feature was
                        /// supported in the serialized ATN. </param>
                        /// <param name="actualUuid"> The <seealso cref="UUID"/> of the actual serialized ATN which is
                        /// currently being deserialized. </param>
                        /// <returns> {@code true} if the {@code actualUuid} value represents a
                        /// serialized ATN at or after the feature identified by {@code feature} was
                        /// introduced; otherwise, {@code false}. </returns>
                    protected:
                        virtual bool isFeatureSupported(antlrcpp::UUID *feature, antlrcpp::UUID *actualUuid);

                    public:
                        virtual ATN *deserialize(const std::wstring& data);

                    public:
                        virtual void verifyATN(ATN *atn);

                        virtual void checkCondition(bool condition);

                        virtual void checkCondition(bool condition, const std::wstring &message);

                        static int toInt(wchar_t c);

                        static int toInt32(const std::wstring& data, int offset);

                        static long long toLong(const std::wstring& data, int offset);

                        static antlrcpp::UUID *toUUID(const std::wstring& data, int offset);

                        virtual Transition *edgeFactory(ATN *atn, int type, int src, int trg, int arg1, int arg2, int arg3, std::vector<misc::IntervalSet*> &sets);

                        virtual ATNState *stateFactory(int type, int ruleIndex);

                    private:
                        static std::vector<antlrcpp::UUID*> supportedUUIDsInitializer();
                        ATNDeserializationOptions *deserializationOptionsInitializer(ATNDeserializationOptions *dso);

                    };

                }
            }
        }
    }
}
