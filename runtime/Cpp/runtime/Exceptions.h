/*
 * [The "BSD license"]
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

#include "Declarations.h"

#include <iostream>
#include <exception>
#include <string>


namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                class ANTLRException : public std::exception {
                private:
                    std::wstring errormsg;
                    
                    public:
                    ANTLRException() {}
                    ANTLRException(const std::wstring msg) {
                        this->errormsg = msg;
                    }
                    std::wstring getMessage() {
                        return errormsg;
                    }

                };
                
                class IllegalClassException : public ANTLRException {
                public:
                    IllegalClassException(const std::wstring msg) : ANTLRException(msg) {};
                    IllegalClassException() {};
                };

                class IllegalStateException : public ANTLRException {
                public:
                    IllegalStateException(const std::wstring msg) : ANTLRException(msg) {};
                    IllegalStateException(){};
                };
                
                class IllegalArgumentException : public ANTLRException {
                public:
                    IllegalArgumentException(const std::wstring msg)  : ANTLRException(msg) {};
                    IllegalArgumentException(const std::wstring msg, std::exception e);
                    IllegalArgumentException(){};
                };
                
                class NoSuchElementException : public ANTLRException {
                public:
                    NoSuchElementException(const std::wstring msg)  : ANTLRException(msg) {};
                    NoSuchElementException(){};
                };
                
                class NullPointerException : public ANTLRException {
                public:
                    NullPointerException(const std::wstring msg) : ANTLRException(msg) {};
                    NullPointerException(){};
                };
                class IndexOutOfBoundsException : public ANTLRException {
                public:
                    IndexOutOfBoundsException(const std::wstring msg) : ANTLRException(msg) {};
                    IndexOutOfBoundsException(){};
                };
                class UnsupportedOperationException : public ANTLRException {
                public:
                    UnsupportedOperationException(const std::wstring msg) : ANTLRException(msg) {};
                    UnsupportedOperationException(){};
                };
                class IOException : public ANTLRException {
                public:
                    IOException(const std::wstring msg)  : ANTLRException(msg) {};
                    IOException(){};
                };
                class TODOException : public ANTLRException {
                public:
                    TODOException(const std::wstring msg)  : ANTLRException(msg) {};
                    TODOException();
                };
                class ASSERTException : public ANTLRException {
                public:
                    ASSERTException(const std::wstring location,
                                    const std::wstring condition)
                                    : ANTLRException(location + L" condition= " + condition) {};
                    ASSERTException();
                };

                
            }
        }
    }
}