#pragma once

#include "CommonToken.h"
#include "TokenFactory.h"
#include "CharStream.h"
#include "TokenSource.h"

#include <string>
#include <utility>


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
                
                class CommonTokenFactory : public TokenFactory<CommonToken*> {
                public:
                    static TokenFactory<CommonToken*> *const DEFAULT;

                    /// <summary>
                    /// Copy text for token out of input char stream. Useful when input
                    ///  stream is unbuffered. </summary>
                    ///  <seealso cref= UnbufferedCharStream </seealso>
                protected:
                    const bool copyText;

                    /// <summary>
                    /// Create factory and indicate whether or not the factory copy
                    ///  text out of the char stream.
                    /// </summary>
                public:
                    CommonTokenFactory(bool copyText);

                    CommonTokenFactory();

                    virtual CommonToken *create(std::pair<TokenSource*, CharStream*> *source, int type, const std::wstring &text, int channel, int start, int stop, int line, int charPositionInLine) override;

                    virtual CommonToken *create(int type, const std::wstring &text) override;
                };

            }
        }
    }
}
