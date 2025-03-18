/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <exception>
#include <string>
#include "antlr4-common.h"

namespace antlr4 {

  // An exception hierarchy modelled loosely after java.lang.* exceptions.
  class ANTLR4CPP_PUBLIC RuntimeException : public std::exception {
  private:
    std::string _message;
  public:
    RuntimeException(const std::string &msg = "");

    const char* what() const noexcept override;
  };

  class ANTLR4CPP_PUBLIC IllegalStateException : public RuntimeException {
  public:
    IllegalStateException(const std::string &msg = "") : RuntimeException(msg) {}
    IllegalStateException(IllegalStateException const&) = default;
    ~IllegalStateException() override;
    IllegalStateException& operator=(IllegalStateException const&) = default;
  };

  class ANTLR4CPP_PUBLIC IllegalArgumentException : public RuntimeException {
  public:
    IllegalArgumentException(IllegalArgumentException const&) = default;
    IllegalArgumentException(const std::string &msg = "") : RuntimeException(msg) {}
    ~IllegalArgumentException() override;
    IllegalArgumentException& operator=(IllegalArgumentException const&) = default;
  };

  class ANTLR4CPP_PUBLIC NullPointerException : public RuntimeException {
  public:
    NullPointerException(const std::string &msg = "") : RuntimeException(msg) {}
    NullPointerException(NullPointerException const&) = default;
    ~NullPointerException() override;
    NullPointerException& operator=(NullPointerException const&) = default;
  };

  class ANTLR4CPP_PUBLIC IndexOutOfBoundsException : public RuntimeException {
  public:
    IndexOutOfBoundsException(const std::string &msg = "") : RuntimeException(msg) {}
    IndexOutOfBoundsException(IndexOutOfBoundsException const&) = default;
    ~IndexOutOfBoundsException() override;
    IndexOutOfBoundsException& operator=(IndexOutOfBoundsException const&) = default;
  };

  class ANTLR4CPP_PUBLIC UnsupportedOperationException : public RuntimeException {
  public:
    UnsupportedOperationException(const std::string &msg = "") : RuntimeException(msg) {}
    UnsupportedOperationException(UnsupportedOperationException const&) = default;
    ~UnsupportedOperationException() override;
    UnsupportedOperationException& operator=(UnsupportedOperationException const&) = default;

  };

  class ANTLR4CPP_PUBLIC EmptyStackException : public RuntimeException {
  public:
    EmptyStackException(const std::string &msg = "") : RuntimeException(msg) {}
    EmptyStackException(EmptyStackException const&) = default;
    ~EmptyStackException() override;
    EmptyStackException& operator=(EmptyStackException const&) = default;
  };

  // IOException is not a runtime exception (in the java hierarchy).
  // Hence we have to duplicate the RuntimeException implementation.
  class ANTLR4CPP_PUBLIC IOException : public std::exception {
  private:
    std::string _message;

  public:
    IOException(const std::string &msg = "");

    const char* what() const noexcept override;
  };

  class ANTLR4CPP_PUBLIC CancellationException : public IllegalStateException {
  public:
    CancellationException(const std::string &msg = "") : IllegalStateException(msg) {}
    CancellationException(CancellationException const&) = default;
    ~CancellationException() override;
    CancellationException& operator=(CancellationException const&) = default;
  };

  class ANTLR4CPP_PUBLIC ParseCancellationException : public CancellationException {
  public:
    ParseCancellationException(const std::string &msg = "") : CancellationException(msg) {}
    ParseCancellationException(ParseCancellationException const&) = default;
    ~ParseCancellationException() override;
    ParseCancellationException& operator=(ParseCancellationException const&) = default;
  };

} // namespace antlr4
