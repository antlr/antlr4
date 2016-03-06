#pragma once

#include "BufferedTokenStream.h"
#include "TokenSource.h"
#include "Token.h"

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
                /// The most common stream of tokens where every token is buffered up
                ///  and tokens are filtered for a certain channel (the parser will only
                ///  see these tokens).
                /// 
                ///  Even though it buffers all of the tokens, this token stream pulls tokens
                ///  from the tokens source on demand. In other words, until you ask for a
                ///  token using consume(), LT(), etc. the stream does not pull from the lexer.
                /// 
                ///  The only difference between this stream and <seealso cref="BufferedTokenStream"/> superclass
                ///  is that this stream knows how to ignore off channel tokens. There may be
                ///  a performance advantage to using the superclass if you don't pass
                ///  whitespace and comments etc. to the parser on a hidden channel (i.e.,
                ///  you set {@code $channel} instead of calling {@code skip()} in lexer rules.)
                /// </summary>
                ///  <seealso cref= UnbufferedTokenStream </seealso>
                ///  <seealso cref= BufferedTokenStream </seealso>
                class CommonTokenStream : public BufferedTokenStream {
                    /// <summary>
                    /// Skip tokens on any channel but this one; this is how we skip whitespace... </summary>
                protected:
                    int channel;

                public:
                    CommonTokenStream(TokenSource *tokenSource);

                    CommonTokenStream(TokenSource *tokenSource, int channel); 

                protected:
                    virtual int adjustSeekIndex(int i) override;

                    virtual Token *LB(int k) override;

                public:
                    virtual Token *LT(int k) override;

                    /// <summary>
                    /// Count EOF just once. </summary>
                    virtual int getNumberOfOnChannelTokens();

                private:
                    void InitializeInstanceFields();
                };

            }
        }
    }
}
