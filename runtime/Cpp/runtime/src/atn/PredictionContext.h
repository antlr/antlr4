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

#include "Recognizer.h"
#include "atn/ATN.h"
#include "atn/ATNState.h"

namespace antlr4 {
namespace atn {

  struct PredictionContextHasher;
  struct PredictionContextComparer;

  typedef std::unordered_set<Ref<PredictionContext>, PredictionContextHasher, PredictionContextComparer> PredictionContextCache;

  // For the keys we use raw pointers, as we don't need to access them.
  typedef std::map<std::pair<PredictionContext *, PredictionContext *>, Ref<PredictionContext>> PredictionContextMergeCache;

  class ANTLR4CPP_PUBLIC PredictionContext {
  public:
    /// Represents $ in local context prediction, which means wildcard.
    /// *+x = *.
    static const Ref<PredictionContext> EMPTY;

    /// Represents $ in an array in full context mode, when $
    /// doesn't mean wildcard: $ + x = [$,x]. Here,
    /// $ = EMPTY_RETURN_STATE.
    static const int EMPTY_RETURN_STATE = INT_MAX;

  private:
    static const int INITIAL_HASH = 1;

  public:
    static int globalNodeCount;
    const int id;

    /// <summary>
    /// Stores the computed hash code of this <seealso cref="PredictionContext"/>. The hash
    /// code is computed in parts to match the following reference algorithm.
    ///
    /// <pre>
    ///  private int referenceHashCode() {
    ///      int hash = <seealso cref="MurmurHash#initialize"/>(<seealso cref="#INITIAL_HASH"/>);
    ///
    ///      for (int i = 0; i < <seealso cref="#size()"/>; i++) {
    ///          hash = <seealso cref="MurmurHash#update"/>(hash, <seealso cref="#getParent"/>(i));
    ///      }
    ///
    ///      for (int i = 0; i < <seealso cref="#size()"/>; i++) {
    ///          hash = <seealso cref="MurmurHash#update"/>(hash, <seealso cref="#getReturnState"/>(i));
    ///      }
    ///
    ///      hash = <seealso cref="MurmurHash#finish"/>(hash, 2 * <seealso cref="#size()"/>);
    ///      return hash;
    ///  }
    /// </pre>
    /// </summary>
    const size_t cachedHashCode;

  protected:
    PredictionContext(size_t cachedHashCode);
    ~PredictionContext();

  public:
    /// Convert a RuleContext tree to a PredictionContext graph.
    /// Return EMPTY if outerContext is empty.
    static Ref<PredictionContext> fromRuleContext(const ATN &atn, const Ref<RuleContext> &outerContext);

    virtual size_t size() const = 0;
    virtual std::weak_ptr<PredictionContext> getParent(size_t index) const = 0;
    virtual int getReturnState(size_t index) const = 0;

    virtual bool operator == (const PredictionContext &o) const = 0;
    virtual bool operator != (const PredictionContext &o) const;

    /// This means only the EMPTY context is in set.
    virtual bool isEmpty() const;
    virtual bool hasEmptyPath() const;
    virtual size_t hashCode() const;

  protected:
    static size_t calculateEmptyHashCode();
    static size_t calculateHashCode(std::weak_ptr<PredictionContext> parent, int returnState);
    static size_t calculateHashCode(const std::vector<std::weak_ptr<PredictionContext>> &parents, const std::vector<int> &returnStates);

  public:
    // dispatch
    static Ref<PredictionContext> merge(const Ref<PredictionContext> &a,
      const Ref<PredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

    /// <summary>
    /// Merge two <seealso cref="SingletonPredictionContext"/> instances.
    ///
    /// <p/>
    ///
    /// Stack tops equal, parents merge is same; return left graph.<br/>
    /// <embed src="images/SingletonMerge_SameRootSamePar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Same stack top, parents differ; merge parents giving array node, then
    /// remainders of those graphs. A new root node is created to point to the
    /// merged parents.<br/>
    /// <embed src="images/SingletonMerge_SameRootDiffPar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Different stack tops pointing to same parent. Make array node for the
    /// root where both element in the root point to the same (original)
    /// parent.<br/>
    /// <embed src="images/SingletonMerge_DiffRootSamePar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Different stack tops pointing to different parents. Make array node for
    /// the root where each element points to the corresponding original
    /// parent.<br/>
    /// <embed src="images/SingletonMerge_DiffRootDiffPar.svg" type="image/svg+xml"/>
    /// </summary>
    /// <param name="a"> the first <seealso cref="SingletonPredictionContext"/> </param>
    /// <param name="b"> the second <seealso cref="SingletonPredictionContext"/> </param>
    /// <param name="rootIsWildcard"> {@code true} if this is a local-context merge,
    /// otherwise false to indicate a full-context merge </param>
    /// <param name="mergeCache"> </param>
    static Ref<PredictionContext> mergeSingletons(const Ref<SingletonPredictionContext> &a,
      const Ref<SingletonPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

    /**
     * Handle case where at least one of {@code a} or {@code b} is
     * {@link #EMPTY}. In the following diagrams, the symbol {@code $} is used
     * to represent {@link #EMPTY}.
     *
     * <h2>Local-Context Merges</h2>
     *
     * <p>These local-context merge operations are used when {@code rootIsWildcard}
     * is true.</p>
     *
     * <p>{@link #EMPTY} is superset of any graph; return {@link #EMPTY}.<br>
     * <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
     *
     * <p>{@link #EMPTY} and anything is {@code #EMPTY}, so merged parent is
     * {@code #EMPTY}; return left graph.<br>
     * <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/></p>
     *
     * <p>Special case of last merge if local context.<br>
     * <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/></p>
     *
     * <h2>Full-Context Merges</h2>
     *
     * <p>These full-context merge operations are used when {@code rootIsWildcard}
     * is false.</p>
     *
     * <p><embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/></p>
     *
     * <p>Must keep all contexts; {@link #EMPTY} in array is a special value (and
     * null parent).<br>
     * <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
     *
     * <p><embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/></p>
     *
     * @param a the first {@link SingletonPredictionContext}
     * @param b the second {@link SingletonPredictionContext}
     * @param rootIsWildcard {@code true} if this is a local-context merge,
     * otherwise false to indicate a full-context merge
     */
    static Ref<PredictionContext> mergeRoot(const Ref<SingletonPredictionContext> &a,
      const Ref<SingletonPredictionContext> &b, bool rootIsWildcard);

    /**
     * Merge two {@link ArrayPredictionContext} instances.
     *
     * <p>Different tops, different parents.<br>
     * <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/></p>
     *
     * <p>Shared top, same parents.<br>
     * <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/></p>
     *
     * <p>Shared top, different parents.<br>
     * <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/></p>
     *
     * <p>Shared top, all shared parents.<br>
     * <embed src="images/ArrayMerge_ShareTopSharePar.svg" type="image/svg+xml"/></p>
     *
     * <p>Equal tops, merge parents and reduce top to
     * {@link SingletonPredictionContext}.<br>
     * <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
     */
    static Ref<PredictionContext> mergeArrays(const Ref<ArrayPredictionContext> &a,
      const Ref<ArrayPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

  protected:
    /// Make pass over all M parents; merge any equal() ones.
    /// @returns true if the list has been changed (i.e. duplicates where found).
    static bool combineCommonParents(std::vector<std::weak_ptr<PredictionContext>> &parents);

  public:
    static std::string toDOTString(const Ref<PredictionContext> &context);

    static Ref<PredictionContext> getCachedContext(const Ref<PredictionContext> &context,
      PredictionContextCache &contextCache,
      std::map<Ref<PredictionContext>, Ref<PredictionContext>> &visited);

    // ter's recursive version of Sam's getAllNodes()
    static std::vector<Ref<PredictionContext>> getAllContextNodes(const Ref<PredictionContext> &context);
    static void getAllContextNodes_(const Ref<PredictionContext> &context,
      std::vector<Ref<PredictionContext>> &nodes, std::set<PredictionContext *> &visited);

    virtual std::string toString() const;
    virtual std::string toString(Recognizer *recog) const;

    std::vector<std::string> toStrings(Recognizer *recognizer, int currentState);
    std::vector<std::string> toStrings(Recognizer *recognizer, const Ref<PredictionContext> &stop, int currentState);
  };

  struct PredictionContextHasher {
    size_t operator () (const Ref<PredictionContext> &k) const {
      return k->hashCode();
    }
  };

  struct PredictionContextComparer {
    bool operator () (const Ref<PredictionContext> &lhs, const Ref<PredictionContext> &rhs) const
    {
      return *lhs == *rhs;
    }
  };

} // namespace atn
} // namespace antlr4

