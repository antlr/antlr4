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

#include "misc/Interval.h"

namespace antlr4 {
namespace misc {

  /**
   * This class implements the {@link IntSet} backed by a sorted array of
   * non-overlapping intervals. It is particularly efficient for representing
   * large collections of numbers, where the majority of elements appear as part
   * of a sequential range of numbers that are all part of the set. For example,
   * the set { 1, 2, 3, 4, 7, 8 } may be represented as { [1, 4], [7, 8] }.
   *
   * <p>
   * This class is able to represent sets containing any combination of values in
   * the range {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
   * (inclusive).</p>
   */
  class ANTLR4CPP_PUBLIC IntervalSet {
  public:
    static IntervalSet const COMPLETE_CHAR_SET;
    static IntervalSet const EMPTY_SET;

  protected:
    /// The list of sorted, disjoint intervals.
    std::vector<Interval> _intervals;
    bool _readonly;

  public:
    IntervalSet();
    IntervalSet(const std::vector<Interval> &intervals);
    IntervalSet(const IntervalSet &set);
    IntervalSet(int numArgs, ...);

    virtual ~IntervalSet() {}

    /// <summary>
    /// Create a set with a single element, el. </summary>
    static IntervalSet of(int a);

    /// <summary>
    /// Create a set with all ints within range [a..b] (inclusive) </summary>
    static IntervalSet of(int a, int b);

    virtual void clear();

    /// <summary>
    /// Add a single element to the set.  An isolated element is stored
    ///  as a range el..el.
    /// </summary>
    virtual void add(int el);

    /// <summary>
    /// Add interval; i.e., add all integers from a to b to set.
    ///  If b<a, do nothing.
    ///  Keep list in sorted order (by left range value).
    ///  If overlap, combine ranges.  For example,
    ///  If this is {1..5, 10..20}, adding 6..7 yields
    ///  {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
    /// </summary>
    virtual void add(int a, int b);

  public:
    /// combine all sets in the array returned the or'd value
    static IntervalSet Or(const std::vector<IntervalSet> &sets);

    // Copy on write so we can cache a..a intervals and sets of that.
    virtual void add(const Interval &addition);
    virtual IntervalSet& addAll(const IntervalSet &set);

    virtual IntervalSet complement(int minElement, int maxElement) const;

    /// Given the set of possible values (rather than, say UNICODE or MAXINT),
    /// return a new set containing all elements in vocabulary, but not in
    /// this.  The computation is (vocabulary - this).
    ///
    /// 'this' is assumed to be either a subset or equal to vocabulary.
    virtual IntervalSet complement(const IntervalSet &vocabulary) const;

    /// Compute this-other via this&~other.
    /// Return a new set containing all elements in this but not in other.
    /// other is assumed to be a subset of this;
    /// anything that is in other but not in this will be ignored.
    virtual IntervalSet subtract(const IntervalSet &other) const;

    /**
     * Compute the set difference between two interval sets. The specific
     * operation is {@code left - right}. If either of the input sets is
     * {@code null}, it is treated as though it was an empty set.
     */
    static IntervalSet subtract(const IntervalSet &left, const IntervalSet &right);

    virtual IntervalSet Or(const IntervalSet &a) const;

    /// <summary>
    /// Return a new set with the intersection of this set with other.  Because
    ///  the intervals are sorted, we can use an iterator for each list and
    ///  just walk them together.  This is roughly O(min(n,m)) for interval
    ///  list lengths n and m.
    /// </summary>
    virtual IntervalSet And(const IntervalSet &other) const;

    /// <summary>
    /// Is el in any range of this set? </summary>
    virtual bool contains(int el) const;

    /// return true if this set has no members
    virtual bool isEmpty() const;

    /// <summary>
    /// If this set is a single integer, return it otherwise Token.INVALID_TYPE </summary>
    virtual int getSingleElement() const;

    /**
     * Returns the maximum value contained in the set.
     *
     * @return the maximum value contained in the set. If the set is empty, this
     * method returns {@link Token#INVALID_TYPE}.
     */
    virtual int getMaxElement() const;

    /**
     * Returns the minimum value contained in the set.
     *
     * @return the minimum value contained in the set. If the set is empty, this
     * method returns {@link Token#INVALID_TYPE}.
     */
    virtual int getMinElement() const;

    /// <summary>
    /// Return a list of Interval objects. </summary>
    virtual std::vector<Interval> getIntervals() const;

    virtual size_t hashCode() const;

    /// Are two IntervalSets equal?  Because all intervals are sorted
    ///  and disjoint, equals is a simple linear walk over both lists
    ///  to make sure they are the same.
    bool operator == (const IntervalSet &other) const;
    virtual std::string toString() const;
    virtual std::string toString(bool elemAreChar) const;

    /**
     * @deprecated Use {@link #toString(Vocabulary)} instead.
     */
    virtual std::string toString(const std::vector<std::string> &tokenNames) const;
    virtual std::string toString(const dfa::Vocabulary &vocabulary) const;

  protected:
    /**
     * @deprecated Use {@link #elementName(Vocabulary, int)} instead.
     */
    virtual std::string elementName(const std::vector<std::string> &tokenNames, ssize_t a) const;
    virtual std::string elementName(const dfa::Vocabulary &vocabulary, ssize_t a) const;

  public:
    virtual size_t size() const;
    virtual std::vector<int> toList() const;
    virtual std::set<int> toSet() const;

    /// <summary>
    /// Get the ith element of ordered set.  Used only by RandomPhrase so
    ///  don't bother to implement if you're not doing that for a new
    ///  ANTLR code gen target.
    /// </summary>
    virtual int get(int i) const;
    virtual void remove(int el);
    virtual bool isReadOnly() const;
    virtual void setReadOnly(bool readonly);

  private:
    void InitializeInstanceFields();
  };
  
} // namespace atn
} // namespace antlr4

// Hash function for IntervalSet.

namespace std {
  using antlr4::misc::IntervalSet;

  template <> struct hash<IntervalSet>
  {
    size_t operator() (const IntervalSet &x) const
    {
      return x.hashCode();
    }
  };
}
