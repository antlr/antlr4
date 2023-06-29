﻿/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "misc/IntervalSet.h"
#include "atn/TransitionType.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// An ATN transition between any two ATN states.  Subclasses define
  ///  atom, set, epsilon, action, predicate, rule transitions.
  /// <p/>
  ///  This is a one way link.  It emanates from a state (usually via a list of
  ///  transitions) and has a target state.
  /// <p/>
  ///  Since we never have to change the ATN transitions once we construct it,
  ///  we can fix these transitions as specific classes. The DFA transitions
  ///  on the other hand need to update the labels as it adds transitions to
  ///  the states. We'll use the term Edge for the DFA to distinguish them from
  ///  ATN transitions.
  /// </summary>
  class ANTLR4CPP_PUBLIC Transition {
  public:
    /// The target of this transition.
    // ml: this is a reference into the ATN.
    ATNState *target;

    virtual ~Transition() = default;

    TransitionType getTransitionType() const { return _transitionType; }

    /**
     * Determines if the transition is an "epsilon" transition.
     *
     * <p>The default implementation returns {@code false}.</p>
     *
     * @return {@code true} if traversing this transition in the ATN does not
     * consume an input symbol; otherwise, {@code false} if traversing this
     * transition consumes (matches) an input symbol.
     */
    virtual bool isEpsilon() const;
    virtual misc::IntervalSet label() const;
    virtual bool matches(size_t symbol, size_t minVocabSymbol, size_t maxVocabSymbol) const = 0;

    virtual std::string toString() const;

    Transition(Transition const&) = delete;
    Transition& operator=(Transition const&) = delete;

  protected:
    Transition(TransitionType transitionType, ATNState *target);

  private:
    const TransitionType _transitionType;
  };

  using ConstTransitionPtr = std::unique_ptr<const Transition>;

} // namespace atn
} // namespace antlr4
