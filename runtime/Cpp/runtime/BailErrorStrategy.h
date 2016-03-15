#pragma once

#include "DefaultErrorStrategy.h"
#include "Declarations.h"

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

                /// <summary>
                /// Bail out of parser at first syntax error. Do this to use it:
                ///  <p/>
                ///  {@code myparser.setErrorHandler(new BailErrorStrategy());}
                /// </summary>
                class BailErrorStrategy : public DefaultErrorStrategy {
                    /// <summary>
                    /// Instead of recovering from exception {@code e}, re-throw it wrapped
                    ///  in a <seealso cref="ParseCancellationException"/> so it is not caught by the
                    ///  rule function catches.  Use <seealso cref="Exception#getCause()"/> to get the
                    ///  original <seealso cref="RecognitionException"/>.
                    /// </summary>
                public:
                    virtual void recover(Parser *recognizer, RecognitionException *e) override;

                    /// <summary>
                    /// Make sure we don't attempt to recover inline; if the parser
                    ///  successfully recovers, it won't throw an exception.
                    /// </summary>
                    virtual Token *recoverInline(Parser *recognizer) override;

                    /// <summary>
                    /// Make sure we don't attempt to recover from problems in subrules. </summary>
                    virtual void sync(Parser *recognizer) override;
                };

            }
        }
    }
}
