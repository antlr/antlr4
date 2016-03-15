#include "TokenTagToken.h"

/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
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

                        TokenTagToken::TokenTagToken(const std::wstring &tokenName, int type) :
                        CommonToken(type), tokenName(L""), label(L"") {
                        }

                        TokenTagToken::TokenTagToken(const std::wstring &tokenName, int type, const std::wstring &label) :
                        CommonToken(type), tokenName(tokenName), label(label) {
                        }

                        std::wstring TokenTagToken::getTokenName() {
                            return tokenName;
                        }

                        std::wstring TokenTagToken::getLabel() {
                            return label;
                        }

                        std::wstring TokenTagToken::getText() {
                            if (label != L"") {
                                return std::wstring(L"<") + label + std::wstring(L":") + tokenName + std::wstring(L">");
                            }

                            return std::wstring(L"<") + tokenName + std::wstring(L">");
                        }

                        std::wstring TokenTagToken::toString() {
                            return tokenName + std::wstring(L":") + std::to_wstring(type);
                        }
                    }
                }
            }
        }
    }
}
