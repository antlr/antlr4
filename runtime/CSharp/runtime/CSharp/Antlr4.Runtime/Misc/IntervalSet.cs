/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// This class implements the
    /// <see cref="IIntSet"/>
    /// backed by a sorted array of
    /// non-overlapping intervals. It is particularly efficient for representing
    /// large collections of numbers, where the majority of elements appear as part
    /// of a sequential range of numbers that are all part of the set. For example,
    /// the set { 1, 2, 3, 4, 7, 8 } may be represented as { [1, 4], [7, 8] }.
    /// <p>
    /// This class is able to represent sets containing any combination of values in
    /// the range
    /// <see cref="int.MinValue"/>
    /// to
    /// <see cref="int.MaxValue"/>
    /// (inclusive).</p>
    /// </summary>
    public class IntervalSet : IIntSet
    {
        public static readonly Antlr4.Runtime.Misc.IntervalSet CompleteCharSet = Antlr4.Runtime.Misc.IntervalSet.Of(Lexer.MinCharValue, Lexer.MaxCharValue);

        public static readonly Antlr4.Runtime.Misc.IntervalSet EmptySet = new Antlr4.Runtime.Misc.IntervalSet();

        static IntervalSet()
        {
            CompleteCharSet.SetReadonly(true);
            EmptySet.SetReadonly(true);
        }

        /// <summary>The list of sorted, disjoint intervals.</summary>
        /// <remarks>The list of sorted, disjoint intervals.</remarks>
        protected internal IList<Interval> intervals;

        protected internal bool @readonly;

        public IntervalSet(IList<Interval> intervals)
        {
            this.intervals = intervals;
        }

        public IntervalSet(Antlr4.Runtime.Misc.IntervalSet set)
            : this()
        {
            AddAll(set);
        }

        public IntervalSet(params int[] els)
        {
            if (els == null)
            {
                intervals = new List<Interval>(2);
            }
            else
            {
                // most sets are 1 or 2 elements
                intervals = new List<Interval>(els.Length);
                foreach (int e in els)
                {
                    Add(e);
                }
            }
        }

        /// <summary>Create a set with a single element, el.</summary>
        /// <remarks>Create a set with a single element, el.</remarks>
        [return: NotNull]
        public static Antlr4.Runtime.Misc.IntervalSet Of(int a)
        {
            Antlr4.Runtime.Misc.IntervalSet s = new Antlr4.Runtime.Misc.IntervalSet();
            s.Add(a);
            return s;
        }

        /// <summary>Create a set with all ints within range [a..b] (inclusive)</summary>
        public static Antlr4.Runtime.Misc.IntervalSet Of(int a, int b)
        {
            Antlr4.Runtime.Misc.IntervalSet s = new Antlr4.Runtime.Misc.IntervalSet();
            s.Add(a, b);
            return s;
        }

        public virtual void Clear()
        {
            if (@readonly)
            {
                throw new InvalidOperationException("can't alter readonly IntervalSet");
            }
            intervals.Clear();
        }

        /// <summary>Add a single element to the set.</summary>
        /// <remarks>
        /// Add a single element to the set.  An isolated element is stored
        /// as a range el..el.
        /// </remarks>
        public virtual void Add(int el)
        {
            if (@readonly)
            {
                throw new InvalidOperationException("can't alter readonly IntervalSet");
            }
            Add(el, el);
        }

        /// <summary>Add interval; i.e., add all integers from a to b to set.</summary>
        /// <remarks>
        /// Add interval; i.e., add all integers from a to b to set.
        /// If b&lt;a, do nothing.
        /// Keep list in sorted order (by left range value).
        /// If overlap, combine ranges.  For example,
        /// If this is {1..5, 10..20}, adding 6..7 yields
        /// {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
        /// </remarks>
        public virtual void Add(int a, int b)
        {
            Add(Interval.Of(a, b));
        }

        // copy on write so we can cache a..a intervals and sets of that
        protected internal virtual void Add(Interval addition)
        {
            if (@readonly)
            {
                throw new InvalidOperationException("can't alter readonly IntervalSet");
            }
            //System.out.println("add "+addition+" to "+intervals.toString());
            if (addition.b < addition.a)
            {
                return;
            }
            // find position in list
            // Use iterators as we modify list in place
            for (int i = 0; i < intervals.Count; i++)
            {
                Interval r = intervals[i];
                if (addition.Equals(r))
                {
                    return;
                }
                if (addition.Adjacent(r) || !addition.Disjoint(r))
                {
                    // next to each other, make a single larger interval
                    Interval bigger = addition.Union(r);
                    intervals[i] = bigger;
                    // make sure we didn't just create an interval that
                    // should be merged with next interval in list
                    while (i < intervals.Count - 1)
                    {
                        i++;
                        Interval next = intervals[i];
                        if (!bigger.Adjacent(next) && bigger.Disjoint(next))
                        {
                            break;
                        }
                        // if we bump up against or overlap next, merge
                        intervals.RemoveAt(i);
                        // remove this one
                        i--;
                        // move backwards to what we just set
                        intervals[i] = bigger.Union(next);
                        // set to 3 merged ones
                    }
                    // first call to next after previous duplicates the result
                    return;
                }
                if (addition.StartsBeforeDisjoint(r))
                {
                    // insert before r
                    intervals.Insert(i, addition);
                    return;
                }
            }
            // if disjoint and after r, a future iteration will handle it
            // ok, must be after last interval (and disjoint from last interval)
            // just add it
            intervals.Add(addition);
        }

        /// <summary>combine all sets in the array returned the or'd value</summary>
        public static Antlr4.Runtime.Misc.IntervalSet Or(Antlr4.Runtime.Misc.IntervalSet[] sets)
        {
            Antlr4.Runtime.Misc.IntervalSet r = new Antlr4.Runtime.Misc.IntervalSet();
            foreach (Antlr4.Runtime.Misc.IntervalSet s in sets)
            {
                r.AddAll(s);
            }
            return r;
        }

        public virtual Antlr4.Runtime.Misc.IntervalSet AddAll(IIntSet set)
        {
            if (set == null)
            {
                return this;
            }
            if (set is Antlr4.Runtime.Misc.IntervalSet)
            {
                Antlr4.Runtime.Misc.IntervalSet other = (Antlr4.Runtime.Misc.IntervalSet)set;
                // walk set and add each interval
                int n = other.intervals.Count;
                for (int i = 0; i < n; i++)
                {
                    Interval I = other.intervals[i];
                    this.Add(I.a, I.b);
                }
            }
            else
            {
                foreach (int value in set.ToList())
                {
                    Add(value);
                }
            }
            return this;
        }

        public virtual Antlr4.Runtime.Misc.IntervalSet Complement(int minElement, int maxElement)
        {
            return this.Complement(Antlr4.Runtime.Misc.IntervalSet.Of(minElement, maxElement));
        }

        /// <summary>
        /// <inheritDoc/>
        /// 
        /// </summary>
        public virtual Antlr4.Runtime.Misc.IntervalSet Complement(IIntSet vocabulary)
        {
            if (vocabulary == null || vocabulary.IsNil)
            {
                return null;
            }
            // nothing in common with null set
            Antlr4.Runtime.Misc.IntervalSet vocabularyIS;
            if (vocabulary is Antlr4.Runtime.Misc.IntervalSet)
            {
                vocabularyIS = (Antlr4.Runtime.Misc.IntervalSet)vocabulary;
            }
            else
            {
                vocabularyIS = new Antlr4.Runtime.Misc.IntervalSet();
                vocabularyIS.AddAll(vocabulary);
            }
            return vocabularyIS.Subtract(this);
        }

        public virtual Antlr4.Runtime.Misc.IntervalSet Subtract(IIntSet a)
        {
            if (a == null || a.IsNil)
            {
                return new Antlr4.Runtime.Misc.IntervalSet(this);
            }
            if (a is Antlr4.Runtime.Misc.IntervalSet)
            {
                return Subtract(this, (Antlr4.Runtime.Misc.IntervalSet)a);
            }
            Antlr4.Runtime.Misc.IntervalSet other = new Antlr4.Runtime.Misc.IntervalSet();
            other.AddAll(a);
            return Subtract(this, other);
        }

        /// <summary>Compute the set difference between two interval sets.</summary>
        /// <remarks>
        /// Compute the set difference between two interval sets. The specific
        /// operation is
        /// <c>left - right</c>
        /// . If either of the input sets is
        /// <see langword="null"/>
        /// , it is treated as though it was an empty set.
        /// </remarks>
        [return: NotNull]
        public static Antlr4.Runtime.Misc.IntervalSet Subtract(Antlr4.Runtime.Misc.IntervalSet left, Antlr4.Runtime.Misc.IntervalSet right)
        {
            if (left == null || left.IsNil)
            {
                return new Antlr4.Runtime.Misc.IntervalSet();
            }
            Antlr4.Runtime.Misc.IntervalSet result = new Antlr4.Runtime.Misc.IntervalSet(left);
            if (right == null || right.IsNil)
            {
                // right set has no elements; just return the copy of the current set
                return result;
            }
            int resultI = 0;
            int rightI = 0;
            while (resultI < result.intervals.Count && rightI < right.intervals.Count)
            {
                Interval resultInterval = result.intervals[resultI];
                Interval rightInterval = right.intervals[rightI];
                // operation: (resultInterval - rightInterval) and update indexes
                if (rightInterval.b < resultInterval.a)
                {
                    rightI++;
                    continue;
                }
                if (rightInterval.a > resultInterval.b)
                {
                    resultI++;
                    continue;
                }
                Interval? beforeCurrent = null;
                Interval? afterCurrent = null;
                if (rightInterval.a > resultInterval.a)
                {
                    beforeCurrent = new Interval(resultInterval.a, rightInterval.a - 1);
                }
                if (rightInterval.b < resultInterval.b)
                {
                    afterCurrent = new Interval(rightInterval.b + 1, resultInterval.b);
                }
                if (beforeCurrent != null)
                {
                    if (afterCurrent != null)
                    {
                        // split the current interval into two
                        result.intervals[resultI] = beforeCurrent.Value;
                        result.intervals.Insert(resultI + 1, afterCurrent.Value);
                        resultI++;
                        rightI++;
                        continue;
                    }
                    else
                    {
                        // replace the current interval
                        result.intervals[resultI] = beforeCurrent.Value;
                        resultI++;
                        continue;
                    }
                }
                else
                {
                    if (afterCurrent != null)
                    {
                        // replace the current interval
                        result.intervals[resultI] = afterCurrent.Value;
                        rightI++;
                        continue;
                    }
                    else
                    {
                        // remove the current interval (thus no need to increment resultI)
                        result.intervals.RemoveAt(resultI);
                        continue;
                    }
                }
            }
            // If rightI reached right.intervals.size(), no more intervals to subtract from result.
            // If resultI reached result.intervals.size(), we would be subtracting from an empty set.
            // Either way, we are done.
            return result;
        }

        public virtual Antlr4.Runtime.Misc.IntervalSet Or(IIntSet a)
        {
            Antlr4.Runtime.Misc.IntervalSet o = new Antlr4.Runtime.Misc.IntervalSet();
            o.AddAll(this);
            o.AddAll(a);
            return o;
        }

        /// <summary>
        /// <inheritDoc/>
        /// 
        /// </summary>
        public virtual Antlr4.Runtime.Misc.IntervalSet And(IIntSet other)
        {
            if (other == null)
            {
                //|| !(other instanceof IntervalSet) ) {
                return null;
            }
            // nothing in common with null set
            IList<Interval> myIntervals = this.intervals;
            IList<Interval> theirIntervals = ((Antlr4.Runtime.Misc.IntervalSet)other).intervals;
            Antlr4.Runtime.Misc.IntervalSet intersection = null;
            int mySize = myIntervals.Count;
            int theirSize = theirIntervals.Count;
            int i = 0;
            int j = 0;
            // iterate down both interval lists looking for nondisjoint intervals
            while (i < mySize && j < theirSize)
            {
                Interval mine = myIntervals[i];
                Interval theirs = theirIntervals[j];
                //System.out.println("mine="+mine+" and theirs="+theirs);
                if (mine.StartsBeforeDisjoint(theirs))
                {
                    // move this iterator looking for interval that might overlap
                    i++;
                }
                else
                {
                    if (theirs.StartsBeforeDisjoint(mine))
                    {
                        // move other iterator looking for interval that might overlap
                        j++;
                    }
                    else
                    {
                        if (mine.ProperlyContains(theirs))
                        {
                            // overlap, add intersection, get next theirs
                            if (intersection == null)
                            {
                                intersection = new Antlr4.Runtime.Misc.IntervalSet();
                            }
                            intersection.Add(mine.Intersection(theirs));
                            j++;
                        }
                        else
                        {
                            if (theirs.ProperlyContains(mine))
                            {
                                // overlap, add intersection, get next mine
                                if (intersection == null)
                                {
                                    intersection = new Antlr4.Runtime.Misc.IntervalSet();
                                }
                                intersection.Add(mine.Intersection(theirs));
                                i++;
                            }
                            else
                            {
                                if (!mine.Disjoint(theirs))
                                {
                                    // overlap, add intersection
                                    if (intersection == null)
                                    {
                                        intersection = new Antlr4.Runtime.Misc.IntervalSet();
                                    }
                                    intersection.Add(mine.Intersection(theirs));
                                    // Move the iterator of lower range [a..b], but not
                                    // the upper range as it may contain elements that will collide
                                    // with the next iterator. So, if mine=[0..115] and
                                    // theirs=[115..200], then intersection is 115 and move mine
                                    // but not theirs as theirs may collide with the next range
                                    // in thisIter.
                                    // move both iterators to next ranges
                                    if (mine.StartsAfterNonDisjoint(theirs))
                                    {
                                        j++;
                                    }
                                    else
                                    {
                                        if (theirs.StartsAfterNonDisjoint(mine))
                                        {
                                            i++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (intersection == null)
            {
                return new Antlr4.Runtime.Misc.IntervalSet();
            }
            return intersection;
        }

        /// <summary>
        /// <inheritDoc/>
        /// 
        /// </summary>
        public virtual bool Contains(int el)
        {
            int n = intervals.Count;
            for (int i = 0; i < n; i++)
            {
                Interval I = intervals[i];
                int a = I.a;
                int b = I.b;
                if (el < a)
                {
                    break;
                }
                // list is sorted and el is before this interval; not here
                if (el >= a && el <= b)
                {
                    return true;
                }
            }
            // found in this interval
            return false;
        }

        /// <summary>
        /// <inheritDoc/>
        /// 
        /// </summary>
        public virtual bool IsNil
        {
            get
            {
                return intervals == null || intervals.Count == 0;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// 
        /// </summary>
        public virtual int SingleElement
        {
            get
            {
                if (intervals != null && intervals.Count == 1)
                {
                    Interval I = intervals[0];
                    if (I.a == I.b)
                    {
                        return I.a;
                    }
                }
                return TokenConstants.InvalidType;
            }
        }

        /// <summary>Returns the maximum value contained in the set.</summary>
        /// <remarks>Returns the maximum value contained in the set.</remarks>
        /// <returns>
        /// the maximum value contained in the set. If the set is empty, this
        /// method returns
        /// <see cref="TokenConstants.InvalidType"/>
        /// .
        /// </returns>
        public virtual int MaxElement
        {
            get
            {
                if (IsNil)
                {
                    return TokenConstants.InvalidType;
                }
                Interval last = intervals[intervals.Count - 1];
                return last.b;
            }
        }

        /// <summary>Returns the minimum value contained in the set.</summary>
        /// <remarks>Returns the minimum value contained in the set.</remarks>
        /// <returns>
        /// the minimum value contained in the set. If the set is empty, this
        /// method returns
        /// <see cref="TokenConstants.InvalidType"/>
        /// .
        /// </returns>
        public virtual int MinElement
        {
            get
            {
                if (IsNil)
                {
                    return TokenConstants.InvalidType;
                }
                return intervals[0].a;
            }
        }

        /// <summary>Return a list of Interval objects.</summary>
        /// <remarks>Return a list of Interval objects.</remarks>
        public virtual IList<Interval> GetIntervals()
        {
            return intervals;
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            foreach (Interval I in intervals)
            {
                hash = MurmurHash.Update(hash, I.a);
                hash = MurmurHash.Update(hash, I.b);
            }
            hash = MurmurHash.Finish(hash, intervals.Count * 2);
            return hash;
        }

        /// <summary>
        /// Are two IntervalSets equal?  Because all intervals are sorted
        /// and disjoint, equals is a simple linear walk over both lists
        /// to make sure they are the same.
        /// </summary>
        /// <remarks>
        /// Are two IntervalSets equal?  Because all intervals are sorted
        /// and disjoint, equals is a simple linear walk over both lists
        /// to make sure they are the same.  Interval.equals() is used
        /// by the List.equals() method to check the ranges.
        /// </remarks>
        public override bool Equals(object obj)
        {
            if (obj == null || !(obj is Antlr4.Runtime.Misc.IntervalSet))
            {
                return false;
            }
            Antlr4.Runtime.Misc.IntervalSet other = (Antlr4.Runtime.Misc.IntervalSet)obj;
            return this.intervals.SequenceEqual(other.intervals);
        }

        public override string ToString()
        {
            return ToString(false);
        }

        public virtual string ToString(bool elemAreChar)
        {
            StringBuilder buf = new StringBuilder();
            if (this.intervals == null || this.intervals.Count == 0)
            {
                return "{}";
            }
            if (this.Count > 1)
            {
                buf.Append("{");
            }

            bool first = true;
            foreach (Interval I in intervals)
            {
                if (!first)
                    buf.Append(", ");

                first = false;
                int a = I.a;
                int b = I.b;
                if (a == b)
                {
                    if (a == TokenConstants.Eof)
                    {
                        buf.Append("<EOF>");
                    }
                    else
                    {
                        if (elemAreChar)
                        {
                            buf.Append("'").Append((char)a).Append("'");
                        }
                        else
                        {
                            buf.Append(a);
                        }
                    }
                }
                else
                {
                    if (elemAreChar)
                    {
                        buf.Append("'").Append((char)a).Append("'..'").Append((char)b).Append("'");
                    }
                    else
                    {
                        buf.Append(a).Append("..").Append(b);
                    }
                }
            }
            if (this.Count > 1)
            {
                buf.Append("}");
            }
            return buf.ToString();
        }

        public virtual string ToString(IVocabulary vocabulary)
        {
            StringBuilder buf = new StringBuilder();
            if (this.intervals == null || this.intervals.Count == 0)
            {
                return "{}";
            }
            if (this.Count > 1)
            {
                buf.Append("{");
            }

            bool first = true;
            foreach (Interval I in intervals)
            {
                if (!first)
                    buf.Append(", ");

                first = false;
                int a = I.a;
                int b = I.b;
                if (a == b)
                {
                    buf.Append(ElementName(vocabulary, a));
                }
                else
                {
                    for (int i = a; i <= b; i++)
                    {
                        if (i > a)
                        {
                            buf.Append(", ");
                        }
                        buf.Append(ElementName(vocabulary, i));
                    }
                }
            }
            if (this.Count > 1)
            {
                buf.Append("}");
            }
            return buf.ToString();
        }

        [return: NotNull]
        protected internal virtual string ElementName(IVocabulary vocabulary, int a)
        {
            if (a == TokenConstants.Eof)
            {
                return "<EOF>";
            }
            else
            {
                if (a == TokenConstants.Epsilon)
                {
                    return "<EPSILON>";
                }
                else
                {
                    return vocabulary.GetDisplayName(a);
                }
            }
        }

        public virtual int Count
        {
            get
            {
                int n = 0;
                int numIntervals = intervals.Count;
                if (numIntervals == 1)
                {
                    Interval firstInterval = this.intervals[0];
                    return firstInterval.b - firstInterval.a + 1;
                }
                for (int i = 0; i < numIntervals; i++)
                {
                    Interval I = intervals[i];
                    n += (I.b - I.a + 1);
                }
                return n;
            }
        }

        public virtual List<int> ToIntegerList()
        {
            List<int> values = new List<int>(Count);
            int n = intervals.Count;
            for (int i = 0; i < n; i++)
            {
                Interval I = intervals[i];
                int a = I.a;
                int b = I.b;
                for (int v = a; v <= b; v++)
                {
                    values.Add(v);
                }
            }
            return values;
        }

        public virtual IList<int> ToList()
        {
            IList<int> values = new List<int>();
            int n = intervals.Count;
            for (int i = 0; i < n; i++)
            {
                Interval I = intervals[i];
                int a = I.a;
                int b = I.b;
                for (int v = a; v <= b; v++)
                {
                    values.Add(v);
                }
            }
            return values;
        }

        public virtual HashSet<int> ToSet()
        {
            HashSet<int> s = new HashSet<int>();
            foreach (Interval I in intervals)
            {
                int a = I.a;
                int b = I.b;
                for (int v = a; v <= b; v++)
                {
                    s.Add(v);
                }
            }
            return s;
        }

        public virtual int[] ToArray()
        {
            return ToIntegerList().ToArray();
        }

        public virtual void Remove(int el)
        {
            if (@readonly)
            {
                throw new InvalidOperationException("can't alter readonly IntervalSet");
            }
            int n = intervals.Count;
            for (int i = 0; i < n; i++)
            {
                Interval I = intervals[i];
                int a = I.a;
                int b = I.b;
                if (el < a)
                {
                    break;
                }
                // list is sorted and el is before this interval; not here
                // if whole interval x..x, rm
                if (el == a && el == b)
                {
                    intervals.RemoveAt(i);
                    break;
                }
                // if on left edge x..b, adjust left
                if (el == a)
                {
                    intervals[i] = Interval.Of(I.a + 1, I.b);
                    break;
                }
                // if on right edge a..x, adjust right
                if (el == b)
                {
                    intervals[i] = Interval.Of(I.a, I.b - 1);
                    break;
                }
                // if in middle a..x..b, split interval
                if (el > a && el < b)
                {
                    // found in this interval
                    int oldb = I.b;
                    intervals[i] = Interval.Of(I.a, el - 1);
                    // [a..x-1]
                    Add(el + 1, oldb);
                }
            }
        }

        public virtual bool IsReadOnly
        {
            get
            {
                // add [x+1..b]
                return @readonly;
            }
        }

        public virtual void SetReadonly(bool @readonly)
        {
            if (this.@readonly && !@readonly)
            {
                throw new InvalidOperationException("can't alter readonly IntervalSet");
            }
            this.@readonly = @readonly;
        }

        IIntSet IIntSet.AddAll(IIntSet set)
        {
            return AddAll(set);
        }

        IIntSet IIntSet.And(IIntSet a)
        {
            return And(a);
        }

        IIntSet IIntSet.Complement(IIntSet elements)
        {
            return Complement(elements);
        }

        IIntSet IIntSet.Or(IIntSet a)
        {
            return Or(a);
        }

        IIntSet IIntSet.Subtract(IIntSet a)
        {
            return Subtract(a);
        }
    }
}
