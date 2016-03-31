/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#include "Exceptions.h"

using namespace org::antlr::v4::runtime;

RuntimeException::RuntimeException(RuntimeException *cause) : RuntimeException("", cause) {
}

RuntimeException::RuntimeException(const std::string &msg, RuntimeException *cause)
  : std::exception(), _message(msg), _cause(cause) {
}

std::string RuntimeException::getMessage() const {
  return _message;
}
std::shared_ptr<RuntimeException> RuntimeException::getCause() const {
  return _cause;
}

const char* RuntimeException::what() const noexcept {
  return _message.c_str();
}

//------------------ IOException ---------------------------------------------------------------------------------------

IOException::IOException(RuntimeException *cause) : IOException("", cause) {
}

IOException::IOException(const std::string &msg, RuntimeException *cause) : std::exception(), _message(msg), _cause(cause) {
}

std::string IOException::getMessage() const {
  return _message;
}
std::shared_ptr<RuntimeException> IOException::getCause() const {
  return _cause;
}

const char* IOException::what() const noexcept {
  return _message.c_str();
}
