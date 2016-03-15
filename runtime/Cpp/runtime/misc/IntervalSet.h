#pragma once

#include <string>
#include <vector>
#include <set>

#include "IntSet.h"
#include "vectorhelper.h"
#include "Declarations.h"


/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace misc {

                    
                    /// <summary>
                    /// A set of integers that relies on ranges being common to do
                    ///  "run-length-encoded" like compression (if you view an IntSet like
                    ///  a BitSet with runs of 0s and 1s).  Only ranges are recorded so that
                    ///  a few ints up near value 1000 don't cause massive bitsets, just two
                    ///  integer intervals.
                    /// 
                    ///  element values may be negative.  Useful for sets of EPSILON and EOF.
                    /// 
                    ///  0..9 char range is index pair ['\u0030','\u0039'].
                    ///  Multiple ranges are encoded with multiple index pairs.  Isolated
                    ///  elements are encoded with an index pair where both intervals are the same.
                    /// 
                    ///  The ranges are ordered and disjoint so that 2..6 appears before 101..103.
                    /// </summary>
                    class IntervalSet : public IntSet {
                    public:
                        static IntervalSet *const COMPLETE_CHAR_SET;
                        static IntervalSet *const EMPTY_SET;

                        /// <summary>
                        /// The list of sorted, disjoint intervals. </summary>
                    protected:
                        std::vector<Interval*> intervals;

                        bool readonly;

                    public:
                        IntervalSet(std::vector<Interval*> &intervals);

                        IntervalSet(IntervalSet *set); //this();

                        IntervalSet(int numArgs, ...);

			virtual ~IntervalSet() {}

                        /// <summary>
                        /// Create a set with a single element, el. </summary>
                        static IntervalSet *of(int a);

                        /// <summary>
                        /// Create a set with all ints within range [a..b] (inclusive) </summary>
                        static IntervalSet *of(int a, int b);

                        virtual void clear();

                        /// <summary>
                        /// Add a single element to the set.  An isolated element is stored
                        ///  as a range el..el.
                        /// </summary>
                        virtual void add(int el) override;

                        /// <summary>
                        /// Add interval; i.e., add all integers from a to b to set.
                        ///  If b<a, do nothing.
                        ///  Keep list in sorted order (by left range value).
                        ///  If overlap, combine ranges.  For example,
                        ///  If this is {1..5, 10..20}, adding 6..7 yields
                        ///  {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
                        /// </summary>
                        virtual void add(int a, int b);

                        // copy on write so we can cache a..a intervals and sets of that
                    protected:
                        virtual void add(Interval *addition);

                        /// <summary>
                        /// combine all sets in the array returned the or'd value </summary>
                    public:
                        static IntervalSet *Or(std::vector<IntervalSet*> sets);

                        virtual IntervalSet *addAll(IntSet *set) override;

                        virtual IntervalSet *complement(int minElement, int maxElement);

                        /// <summary>
                        /// Given the set of possible values (rather than, say UNICODE or MAXINT),
                        ///  return a new set containing all elements in vocabulary, but not in
                        ///  this.  The computation is (vocabulary - this).
                        /// 
                        ///  'this' is assumed to be either a subset or equal to vocabulary.
                        /// </summary>
                        virtual IntervalSet *complement(IntSet *vocabulary) override;

                        /// <summary>
                        /// Compute this-other via this&~other.
                        ///  Return a new set containing all elements in this but not in other.
                        ///  other is assumed to be a subset of this;
                        ///  anything that is in other but not in this will be ignored.
                        /// </summary>
                        virtual IntervalSet *subtract(IntSet *other) override;

                        virtual IntervalSet *Or(IntSet *a) override;

                        /// <summary>
                        /// Return a new set with the intersection of this set with other.  Because
                        ///  the intervals are sorted, we can use an iterator for each list and
                        ///  just walk them together.  This is roughly O(min(n,m)) for interval
                        ///  list lengths n and m.
                        /// </summary>
                        virtual IntervalSet *And(IntSet *other) override;

                        /// <summary>
                        /// Is el in any range of this set? </summary>
                        virtual bool contains(int el) override;

                        /// <summary>
                        /// return true if this set has no members </summary>
                        virtual bool isNil() override;

                        /// <summary>
                        /// If this set is a single integer, return it otherwise Token.INVALID_TYPE </summary>
                        virtual int getSingleElement() override;

                        virtual int getMaxElement();

                        /// <summary>
                        /// Return minimum element >= 0 </summary>
                        virtual int getMinElement();

                        /// <summary>
                        /// Return a list of Interval objects. </summary>
                        virtual std::vector<Interval*> getIntervals();

                        virtual int hashCode();

                        /// <summary>
                        /// Are two IntervalSets equal?  Because all intervals are sorted
                        ///  and disjoint, equals is a simple linear walk over both lists
                        ///  to make sure they are the same.  Interval.equals() is used
                        ///  by the List.equals() method to check the ranges.
                        /// </summary>
                        virtual bool equals(void *obj) override;

                        virtual std::wstring toString() override;

                        virtual std::wstring toString(bool elemAreChar);

                        // TODO(dsisson): See if we can eliminate this version.
                        virtual std::wstring toString(std::wstring tokenNames[]);
                        
                        virtual std::wstring toString(std::vector<std::wstring> tokenNames);

                    protected:
                        // TODO(dsisson): See if we can eliminate this version.
                        virtual std::wstring elementName(std::wstring tokenNames[], int a);
                        
                        virtual std::wstring elementName(std::vector<std::wstring> tokenNames, int a);

                    public:
                        virtual int size() override;

                        virtual std::vector<int> toList() override;

                        virtual std::set<int> *toSet();
                        
                        /// <summary>
                        /// Get the ith element of ordered set.  Used only by RandomPhrase so
                        ///  don't bother to implement if you're not doing that for a new
                        ///  ANTLR code gen target.
                        /// </summary>
                        virtual int get(int i);

                        virtual void remove(int el) override;

                        virtual bool isReadonly();

                        virtual void setReadonly(bool readonly);

                    private:
                        void InitializeInstanceFields();
                    };

                }
            }
        }
    }
}
