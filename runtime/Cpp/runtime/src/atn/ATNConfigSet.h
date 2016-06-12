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

#include "support/BitSet.h"
#include "atn/PredictionContext.h"

namespace antlr4 {
namespace atn {

  /// Specialized set that can track info about the set, with support for combining similar configurations using a
  /// graph-structured stack.
  class ANTLR4CPP_PUBLIC ATNConfigSet {
  public:
    /// Track the elements as they are added to the set; supports get(i)
    std::vector<Ref<ATNConfig>> configs;

    // TO_DO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
    // TO_DO: can we track conflicts as they are added to save scanning configs later?
    int uniqueAlt;

    /** Currently this is only used when we detect SLL conflict; this does
     *  not necessarily represent the ambiguous alternatives. In fact,
     *  I should also point out that this seems to include predicated alternatives
     *  that have predicates that evaluate to false. Computed in computeTargetState().
     */
    antlrcpp::BitSet conflictingAlts;

    // Used in parser and lexer. In lexer, it indicates we hit a pred
    // while computing a closure operation.  Don't make a DFA state from this.
    bool hasSemanticContext;
    bool dipsIntoOuterContext;

    /// <summary>
    /// Indicates that this configuration set is part of a full context
    ///  LL prediction. It will be used to determine how to merge $. With SLL
    ///  it's a wildcard whereas it is not for LL context merge.
    /// </summary>
    const bool fullCtx;

    ATNConfigSet(bool fullCtx = true);
    ATNConfigSet(const Ref<ATNConfigSet> &old);

    virtual ~ATNConfigSet();

    virtual bool add(const Ref<ATNConfig> &config);

    /// <summary>
    /// Adding a new config means merging contexts with existing configs for
    /// {@code (s, i, pi, _)}, where {@code s} is the
    /// <seealso cref="ATNConfig#state"/>, {@code i} is the <seealso cref="ATNConfig#alt"/>, and
    /// {@code pi} is the <seealso cref="ATNConfig#semanticContext"/>. We use
    /// {@code (s,i,pi)} as key.
    /// <p/>
    /// This method updates <seealso cref="#dipsIntoOuterContext"/> and
    /// <seealso cref="#hasSemanticContext"/> when necessary.
    /// </summary>
    virtual bool add(const Ref<ATNConfig> &config, PredictionContextMergeCache *mergeCache);

    virtual std::vector<ATNState *> getStates();

    /**
     * Gets the complete set of represented alternatives for the configuration
     * set.
     *
     * @return the set of represented alternatives in this configuration set
     *
     * @since 4.3
     */
    antlrcpp::BitSet getAlts();
    virtual std::vector<Ref<SemanticContext>> getPredicates();

    virtual Ref<ATNConfig> get(size_t i) const;

    virtual void optimizeConfigs(ATNSimulator *interpreter);

    bool addAll(const Ref<ATNConfigSet> &other);

    bool operator == (const ATNConfigSet &other);
    virtual size_t hashCode();
    virtual size_t size();
    virtual bool isEmpty();
    virtual void clear();
    virtual bool isReadonly();
    virtual void setReadonly(bool readonly);
    virtual std::string toString();

  protected:
    /// All configs but hashed by (s, i, _, pi) not including context. Wiped out
    /// when we go readonly as this set becomes a DFA state.
    // ml: no need for a comparer here as by definition there can be no hash clashes.
    //     (same hashes always mean same object).
    std::unordered_map<size_t, ATNConfig *> _configLookup;

    /// Indicates that the set of configurations is read-only. Do not
    /// allow any code to manipulate the set; DFA states will point at
    /// the sets and they must not change. This does not protect the other
    /// fields; in particular, conflictingAlts is set after
    /// we've made this readonly.
    bool _readonly;

    virtual size_t getHash(ATNConfig *c); // Hash differs depending on set type.

  private:
    size_t _cachedHashCode;

    void InitializeInstanceFields();
  };

} // namespace atn
} // namespace antlr4
