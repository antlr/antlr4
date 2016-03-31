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

#pragma once

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {

  // An exception hierarchy modelled loosely after java.lang.* exceptions.
  class RuntimeException : public std::exception {
  private:
    std::string _message;
    std::shared_ptr<RuntimeException> _cause; // Optionally assigned if this exception is wrapping another one.

  public:
    RuntimeException(RuntimeException *cause = nullptr);
    RuntimeException(const std::string &msg, RuntimeException *cause = nullptr);

    std::string getMessage() const;
    std::shared_ptr<RuntimeException> getCause() const;

    virtual const char* what() const noexcept override;
  };

  class IllegalStateException : public RuntimeException {
  public:
    IllegalStateException(RuntimeException *cause = nullptr) : IllegalStateException("", cause) {};
    IllegalStateException(const std::string &msg, RuntimeException *cause = nullptr) : RuntimeException(msg, cause) {};
  };

  class IllegalArgumentException : public RuntimeException {
  public:
    IllegalArgumentException(RuntimeException *cause = nullptr) : IllegalArgumentException("", cause) {};
    IllegalArgumentException(const std::string &msg, RuntimeException *cause = nullptr) : RuntimeException(msg, cause) {};
  };

  class NullPointerException : public RuntimeException {
  public:
    NullPointerException(RuntimeException *cause = nullptr) : NullPointerException("", cause) {};
    NullPointerException(const std::string &msg, RuntimeException *cause = nullptr) : RuntimeException(msg, cause) {};
  };

  class IndexOutOfBoundsException : public RuntimeException {
  public:
    IndexOutOfBoundsException(RuntimeException *cause = nullptr) : IndexOutOfBoundsException("", cause) {};
    IndexOutOfBoundsException(const std::string &msg, RuntimeException *cause = nullptr) : RuntimeException(msg, cause) {};
  };

  class UnsupportedOperationException : public RuntimeException {
  public:
    UnsupportedOperationException(RuntimeException *cause = nullptr) : UnsupportedOperationException("", cause) {};
    UnsupportedOperationException(const std::string &msg, RuntimeException *cause = nullptr) : RuntimeException(msg, cause) {};
  };

  // IOException is not a runtime exception (in the java hierarchy).
  // Hence we have to duplicate the RuntimeException implementation.
  class IOException : public std::exception {
  private:
    std::string _message;
    std::shared_ptr<RuntimeException> _cause; // Optionally assigned if this exception is wrapping another one.

  public:
    IOException(RuntimeException *cause = nullptr);
    IOException(const std::string &msg, RuntimeException *cause = nullptr);

    std::string getMessage() const;
    std::shared_ptr<RuntimeException> getCause() const;

    virtual const char* what() const noexcept override;
  };

} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
