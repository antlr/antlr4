/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "Exceptions.h"

using namespace antlr4;

RuntimeException::RuntimeException(const std::string &msg) : std::exception(), _message(msg) {
}

const char* RuntimeException::what() const NOEXCEPT {
  return _message.c_str();
}

//------------------ IOException ---------------------------------------------------------------------------------------

IOException::IOException(const std::string &msg) : std::exception(), _message(msg) {
}

const char* IOException::what() const NOEXCEPT {
  return _message.c_str();
}
