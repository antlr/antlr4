#include "BailErrorStrategy.h"
#include "Exceptions.h"
#include "ParserRuleContext.h"
#include "Parser.h"
#include "ParseCancellationException.h"
#include "InputMismatchException.h"

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

                void BailErrorStrategy::recover(Parser *recognizer, RecognitionException *e) {
                    for (ParserRuleContext *context = recognizer->getContext(); context != nullptr; context = context->getParent()) {
                        context->exception = e;
                    }

                    throw new ParseCancellationException(e);
                }

                runtime::Token *BailErrorStrategy::recoverInline(Parser *recognizer)  {

                    InputMismatchException *e = new InputMismatchException(recognizer);
                    for (ParserRuleContext *context = recognizer->getContext();
                        context != nullptr;
                        context = context->getParent()) {
                            context->exception = e;
                    }

                    throw new ParseCancellationException(e);

                }

                void BailErrorStrategy::sync(Parser *recognizer) {
                }
            }
        }
    }
}
