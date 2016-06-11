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

#include "dfa/DFAState.h"

namespace antlr4 {
namespace dfa {

  class ANTLR4CPP_PUBLIC DFA {
  public:
    /// A set of all DFA states. Use a map so we can get old state back.
    /// Set only allows you to see if it's there.

    /// From which ATN state did we create this DFA?
    atn::DecisionState *const atnStartState;
    std::unordered_set<DFAState *, DFAState::Hasher, DFAState::Comparer> states; // States are owned by this class.
    DFAState *s0;
    const int decision;

    DFA(atn::DecisionState *atnStartState);
    DFA(atn::DecisionState *atnStartState, int decision);
    DFA(const DFA &other);
    DFA(DFA &&other);
    virtual ~DFA();

    /**
     * Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
     * start state {@link #s0} which is not stored in {@link #states}. The
     * {@link DFAState#edges} array for this start state contains outgoing edges
     * supplying individual start states corresponding to specific precedence
     * values.
     *
     * @return {@code true} if this is a precedence DFA; otherwise,
     * {@code false}.
     * @see Parser#getPrecedence()
     */
    bool isPrecedenceDfa() const;
    
    /**
     * Get the start state for a specific precedence value.
     *
     * @param precedence The current precedence.
     * @return The start state corresponding to the specified precedence, or
     * {@code null} if no start state exists for the specified precedence.
     *
     * @throws IllegalStateException if this is not a precedence DFA.
     * @see #isPrecedenceDfa()
     */
    DFAState* getPrecedenceStartState(int precedence) const;
    
    /**
     * Set the start state for a specific precedence value.
     *
     * @param precedence The current precedence.
     * @param startState The start state corresponding to the specified
     * precedence.
     *
     * @throws IllegalStateException if this is not a precedence DFA.
     * @see #isPrecedenceDfa()
     */
    void setPrecedenceStartState(int precedence, DFAState *startState);
    
    /// Return a list of all states in this DFA, ordered by state number.
    virtual std::vector<DFAState *> getStates() const;

    /**
     * @deprecated Use {@link #toString(Vocabulary)} instead.
     */
    virtual std::string toString(const std::vector<std::string>& tokenNames);
    std::string toString(const Vocabulary &vocabulary) const;

    virtual std::string toLexerString();

  private:
    /**
     * {@code true} if this DFA is for a precedence decision; otherwise,
     * {@code false}. This is the backing field for {@link #isPrecedenceDfa}.
     */
    bool _precedenceDfa;

    std::recursive_mutex _lock; // To synchronize access to s0.
  };

} // namespace atn
} // namespace antlr4
