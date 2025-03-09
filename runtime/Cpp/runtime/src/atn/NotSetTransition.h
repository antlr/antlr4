/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <string>
#include <cstddef>
#include "antlr4-common.h"
#include "misc/IntervalSet.h"
#include "atn/TransitionType.h"
#include "atn/ATNState.h"
#include "atn/SetTransition.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC NotSetTransition final : public SetTransition {
  public:
    static bool is(const Transition &transition) { return transition.getTransitionType() == TransitionType::NOT_SET; }

    static bool is(const Transition *transition) { return transition != nullptr && is(*transition); }

    NotSetTransition(ATNState *target, misc::IntervalSet set);

    bool matches(size_t symbol, size_t minVocabSymbol, size_t maxVocabSymbol) const override;

    std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4
