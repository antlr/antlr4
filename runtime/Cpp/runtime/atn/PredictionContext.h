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
#include "ATN.h"
#include "ATNState.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  // Cannot use PredictionContext::Ref here as this declared below first.
  typedef std::set<std::shared_ptr<PredictionContext>> PredictionContextCache;

  // For the keys we use raw pointers, as we don't need to access them.
  typedef std::map<std::pair<PredictionContext *, PredictionContext *>, std::shared_ptr<PredictionContext>> PredictionContextMergeCache;

  class PredictionContext {
  public:
    typedef std::shared_ptr<PredictionContext> Ref;

    /// Represents $ in local context prediction, which means wildcard.
    /// *+x = *.
    static const Ref EMPTY;

    /// Represents $ in an array in full context mode, when $
    /// doesn't mean wildcard: $ + x = [$,x]. Here,
    /// $ = EMPTY_RETURN_STATE.
    static const int EMPTY_RETURN_STATE = INT16_MAX;

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
    static PredictionContext::Ref fromRuleContext(const ATN &atn, RuleContext::Ref outerContext);

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
    static PredictionContext::Ref merge(PredictionContext::Ref a,
      PredictionContext::Ref b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

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
    static PredictionContext::Ref mergeSingletons(std::shared_ptr<SingletonPredictionContext> a,
      std::shared_ptr<SingletonPredictionContext> b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

    /// <summary>
    /// Handle case where at least one of {@code a} or {@code b} is
    /// <seealso cref="#EMPTY"/>. In the following diagrams, the symbol {@code $} is used
    /// to represent <seealso cref="#EMPTY"/>.
    ///
    /// <h2>Local-Context Merges</h2>
    ///
    /// These local-context merge operations are used when {@code rootIsWildcard}
    /// is true.
    ///
    /// <p/>
    ///
    /// <seealso cref="#EMPTY"/> is superset of any graph; return <seealso cref="#EMPTY"/>.<br/>
    /// <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// <seealso cref="#EMPTY"/> and anything is {@code #EMPTY}, so merged parent is
    /// {@code #EMPTY}; return left graph.<br/>
    /// <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Special case of last merge if local context.<br/>
    /// <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/>
    ///
    /// <h2>Full-Context Merges</h2>
    ///
    /// These full-context merge operations are used when {@code rootIsWildcard}
    /// is false.
    ///
    /// <p/>
    ///
    /// <embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Must keep all contexts; <seealso cref="#EMPTY"/> in array is a special value (and
    /// null parent).<br/>
    /// <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// <embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/>
    /// </summary>
    /// <param name="a"> the first <seealso cref="SingletonPredictionContext"/> </param>
    /// <param name="b"> the second <seealso cref="SingletonPredictionContext"/> </param>
    /// <param name="rootIsWildcard"> {@code true} if this is a local-context merge,
    /// otherwise false to indicate a full-context merge </param>
    static PredictionContext::Ref mergeRoot(std::shared_ptr<SingletonPredictionContext> a,
      std::shared_ptr<SingletonPredictionContext> b, bool rootIsWildcard);

    /// <summary>
    /// Merge two <seealso cref="ArrayPredictionContext"/> instances.
    ///
    /// <p/>
    ///
    /// Different tops, different parents.<br/>
    /// <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Shared top, same parents.<br/>
    /// <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Shared top, different parents.<br/>
    /// <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Shared top, all shared parents.<br/>
    /// <embed src="images/ArrayMerge_ShareTopSharePar.svg" type="image/svg+xml"/>
    ///
    /// <p/>
    ///
    /// Equal tops, merge parents and reduce top to
    /// <seealso cref="SingletonPredictionContext"/>.<br/>
    /// <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/>
    /// </summary>
    static PredictionContext::Ref mergeArrays(std::shared_ptr<ArrayPredictionContext> a,
      std::shared_ptr<ArrayPredictionContext> b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache);

  protected:
    /// Make pass over all M parents; merge any equal() ones.
    /// @returns true if the list has been changed (i.e. duplicates where found).
    static bool combineCommonParents(std::vector<std::weak_ptr<PredictionContext>> &parents);

  public:
    static std::wstring toDOTString(PredictionContext::Ref context);

    static PredictionContext::Ref getCachedContext(PredictionContext::Ref context,
      std::shared_ptr<PredictionContextCache> contextCache,
      std::map<PredictionContext::Ref, PredictionContext::Ref> &visited);

    // ter's recursive version of Sam's getAllNodes()
    static std::vector<PredictionContext::Ref> getAllContextNodes(PredictionContext::Ref context);
    static void getAllContextNodes_(PredictionContext::Ref context,
      std::vector<PredictionContext::Ref> &nodes, std::map<PredictionContext::Ref, PredictionContext::Ref> &visited);

    virtual std::wstring toString() const;
    virtual std::wstring toString(Recognizer *recog) const;

    std::vector<std::wstring> toStrings(Recognizer *recognizer, int currentState);
    std::vector<std::wstring> toStrings(Recognizer *recognizer, PredictionContext::Ref stop, int currentState);
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org

// Hash function for PredictionContext, used in the MurmurHash::update function

namespace std {
  using org::antlr::v4::runtime::atn::PredictionContext;

  template <> struct hash<PredictionContext>
  {
    size_t operator () (const PredictionContext &x) const
    {
      return x.hashCode();
    }
  };
}
