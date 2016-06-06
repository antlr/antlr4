/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
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

#include "misc/IntervalSet.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// The following images show the relation of states and
  /// <seealso cref="ATNState#transitions"/> for various grammar constructs.
  ///
  /// <ul>
  ///
  /// <li>Solid edges marked with an &#0949; indicate a required
  /// <seealso cref="EpsilonTransition"/>.</li>
  ///
  /// <li>Dashed edges indicate locations where any transition derived from
  /// <seealso cref="Transition"/> might appear.</li>
  ///
  /// <li>Dashed nodes are place holders for either a sequence of linked
  /// <seealso cref="BasicState"/> states or the inclusion of a block representing a nested
  /// construct in one of the forms below.</li>
  ///
  /// <li>Nodes showing multiple outgoing alternatives with a {@code ...} support
  /// any number of alternatives (one or more). Nodes without the {@code ...} only
  /// support the exact number of alternatives shown in the diagram.</li>
  ///
  /// </ul>
  ///
  /// <h2>Basic Blocks</h2>
  ///
  /// <h3>Rule</h3>
  ///
  /// <embed src="images/Rule.svg" type="image/svg+xml"/>
  ///
  /// <h3>Block of 1 or more alternatives</h3>
  ///
  /// <embed src="images/Block.svg" type="image/svg+xml"/>
  ///
  /// <h2>Greedy Loops</h2>
  ///
  /// <h3>Greedy Closure: {@code (...)*}</h3>
  ///
  /// <embed src="images/ClosureGreedy.svg" type="image/svg+xml"/>
  ///
  /// <h3>Greedy Positive Closure: {@code (...)+}</h3>
  ///
  /// <embed src="images/PositiveClosureGreedy.svg" type="image/svg+xml"/>
  ///
  /// <h3>Greedy Optional: {@code (...)?}</h3>
  ///
  /// <embed src="images/OptionalGreedy.svg" type="image/svg+xml"/>
  ///
  /// <h2>Non-Greedy Loops</h2>
  ///
  /// <h3>Non-Greedy Closure: {@code (...)*?}</h3>
  ///
  /// <embed src="images/ClosureNonGreedy.svg" type="image/svg+xml"/>
  ///
  /// <h3>Non-Greedy Positive Closure: {@code (...)+?}</h3>
  ///
  /// <embed src="images/PositiveClosureNonGreedy.svg" type="image/svg+xml"/>
  ///
  /// <h3>Non-Greedy Optional: {@code (...)??}</h3>
  ///
  /// <embed src="images/OptionalNonGreedy.svg" type="image/svg+xml"/>
  /// </summary>
  class ANTLR4CPP_PUBLIC ATNState {
  public:
    ATNState();
    
    virtual ~ATNState();

    static const int INITIAL_NUM_TRANSITIONS;
    static const int INVALID_STATE_NUMBER;

    enum {
      ATN_INVALID_TYPE = 0,
      BASIC = 1,
      RULE_START = 2,
      BLOCK_START = 3,
      PLUS_BLOCK_START = 4,
      STAR_BLOCK_START = 5,
      TOKEN_START = 6,
      RULE_STOP = 7,
      BLOCK_END = 8,
      STAR_LOOP_BACK = 9,
      STAR_LOOP_ENTRY = 10,
      PLUS_LOOP_BACK = 11,
      LOOP_END = 12
    };

    static const std::vector<std::string> serializationNames;

    /// Which ATN are we in?
    // ml: just a reference to the owner. Set when the state gets added to an ATN.
    //const ATN *atn = nullptr;
    int stateNumber = INVALID_STATE_NUMBER;
    int ruleIndex = 0; // at runtime, we don't have Rule objects
    bool epsilonOnlyTransitions = false;

  protected:
    /// Track the transitions emanating from this ATN state.
    std::vector<Transition*> transitions;

  public:
    /// Used to cache lookahead during parsing, not used during construction.
    misc::IntervalSet nextTokenWithinRule;

    virtual size_t hashCode();
    bool operator == (const ATNState &other);

    virtual bool isNonGreedyExitState();
    virtual std::string toString() const;
    virtual  std::vector<Transition*> getTransitions();
    virtual size_t getNumberOfTransitions();
    virtual void addTransition(Transition *e);
    virtual void addTransition(int index, Transition *e);
    virtual Transition *transition(size_t i);
    virtual void setTransition(size_t i, Transition *e);
    virtual Transition *removeTransition(int index);
    virtual int getStateType() = 0;
    bool onlyHasEpsilonTransitions();
    virtual void setRuleIndex(int ruleIndex);

  };

} // namespace atn
} // namespace antlr4
