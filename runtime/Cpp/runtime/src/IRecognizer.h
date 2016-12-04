/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

namespace antlr4 {

  class ANTLR4CPP_PUBLIC IRecognizer {
  public:
    virtual ~IRecognizer() {};

    virtual size_t getState() const = 0;

    // Get the ATN used by the recognizer for prediction.
    virtual const atn::ATN& getATN() const = 0;

  };

} // namespace antlr4
