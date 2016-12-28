﻿/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/Transition.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// A transition containing a set of values. </summary>
  class ANTLR4CPP_PUBLIC SetTransition : public Transition {
  public:
    const misc::IntervalSet set;

    SetTransition(ATNState *target, const misc::IntervalSet &set);

    virtual SerializationType getSerializationType() const override;

    virtual misc::IntervalSet label() const override;
    virtual bool matches(size_t symbol, size_t minVocabSymbol, size_t maxVocabSymbol) const override;

    virtual std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4
