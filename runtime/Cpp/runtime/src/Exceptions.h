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

#include "antlr4-common.h"

namespace antlr4 {

  // An exception hierarchy modelled loosely after java.lang.* exceptions.
  class ANTLR4CPP_PUBLIC RuntimeException : public std::exception {
  private:
    std::string _message;
  public:
    RuntimeException(const std::string &msg = "");

    virtual const char* what() const NOEXCEPT override;
  };

  class ANTLR4CPP_PUBLIC IllegalStateException : public RuntimeException {
  public:
    IllegalStateException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  class ANTLR4CPP_PUBLIC IllegalArgumentException : public RuntimeException {
  public:
    IllegalArgumentException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  class ANTLR4CPP_PUBLIC NullPointerException : public RuntimeException {
  public:
    NullPointerException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  class ANTLR4CPP_PUBLIC IndexOutOfBoundsException : public RuntimeException {
  public:
    IndexOutOfBoundsException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  class ANTLR4CPP_PUBLIC UnsupportedOperationException : public RuntimeException {
  public:
    UnsupportedOperationException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  class ANTLR4CPP_PUBLIC EmptyStackException : public RuntimeException {
  public:
    EmptyStackException(const std::string &msg = "") : RuntimeException(msg) {};
  };

  // IOException is not a runtime exception (in the java hierarchy).
  // Hence we have to duplicate the RuntimeException implementation.
  class ANTLR4CPP_PUBLIC IOException : public std::exception {
  private:
    std::string _message;

  public:
    IOException(const std::string &msg = "");

    virtual const char* what() const NOEXCEPT override;
  };

  class ANTLR4CPP_PUBLIC CancellationException : public IllegalStateException {
  public:
    CancellationException(const std::string &msg = "") : IllegalStateException(msg) {};
  };

  class ANTLR4CPP_PUBLIC ParseCancellationException : public CancellationException {
  public:
    ParseCancellationException(const std::string &msg = "") : CancellationException(msg) {};
  };

} // namespace antlr4
