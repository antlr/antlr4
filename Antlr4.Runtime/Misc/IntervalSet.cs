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
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
	/// <summary>
	/// A set of integers that relies on ranges being common to do
	/// "run-length-encoded" like compression (if you view an IntSet like
	/// a BitSet with runs of 0s and 1s).
	/// </summary>
	/// <remarks>
	/// A set of integers that relies on ranges being common to do
	/// "run-length-encoded" like compression (if you view an IntSet like
	/// a BitSet with runs of 0s and 1s).  Only ranges are recorded so that
	/// a few ints up near value 1000 don't cause massive bitsets, just two
	/// integer intervals.
	/// element values may be negative.  Useful for sets of EPSILON and EOF.
	/// 0..9 char range is index pair ['\u0030','\u0039'].
	/// Multiple ranges are encoded with multiple index pairs.  Isolated
	/// elements are encoded with an index pair where both intervals are the same.
	/// The ranges are ordered and disjoint so that 2..6 appears before 101..103.
	/// </remarks>
	public class IntervalSet : IntSet
	{
		public static readonly Antlr4.Runtime.Misc.IntervalSet CompleteCharSet = Antlr4.Runtime.Misc.IntervalSet
			.Of(0, Lexer.MaxCharValue);

		public static readonly Antlr4.Runtime.Misc.IntervalSet EmptySet = new Antlr4.Runtime.Misc.IntervalSet
			();

		/// <summary>The list of sorted, disjoint intervals.</summary>
		/// <remarks>The list of sorted, disjoint intervals.</remarks>
		protected internal IList<Interval> intervals;

		protected internal bool @readonly;

		public IntervalSet(IList<Interval> intervals)
		{
			this.intervals = intervals;
		}

		public IntervalSet(Antlr4.Runtime.Misc.IntervalSet set) : this()
		{
			AddAll(set);
		}

		public IntervalSet(params int[] els)
		{
			if (els == null)
			{
				intervals = new AList<Interval>(2);
			}
			else
			{
				// most sets are 1 or 2 elements
				intervals = new AList<Interval>(els.Length);
				foreach (int e in els)
				{
					Add(e);
				}
			}
		}

		/// <summary>Create a set with a single element, el.</summary>
		/// <remarks>Create a set with a single element, el.</remarks>
		[NotNull]
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
			for (ListIterator<Interval> iter = intervals.ListIterator(); iter.HasNext(); )
			{
				Interval r = iter.Next();
				if (addition.Equals(r))
				{
					return;
				}
				if (addition.Adjacent(r) || !addition.Disjoint(r))
				{
					// next to each other, make a single larger interval
					Interval bigger = addition.Union(r);
					iter.Set(bigger);
					// make sure we didn't just create an interval that
					// should be merged with next interval in list
					if (iter.HasNext())
					{
						Interval next = iter.Next();
						if (bigger.Adjacent(next) || !bigger.Disjoint(next))
						{
							// if we bump up against or overlap next, merge
							iter.Remove();
							// remove this one
							iter.Previous();
							// move backwards to what we just set
							iter.Set(bigger.Union(next));
						}
					}
					// set to 3 merged ones
					return;
				}
				if (addition.StartsBeforeDisjoint(r))
				{
					// insert before r
					iter.Previous();
					iter.Add(addition);
					return;
				}
			}
			// if disjoint and after r, a future iteration will handle it
			// ok, must be after last interval (and disjoint from last interval)
			// just add it
			intervals.AddItem(addition);
		}

		/// <summary>combine all sets in the array returned the or'd value</summary>
		public static Antlr4.Runtime.Misc.IntervalSet Or(Antlr4.Runtime.Misc.IntervalSet[]
			 sets)
		{
			Antlr4.Runtime.Misc.IntervalSet r = new Antlr4.Runtime.Misc.IntervalSet();
			foreach (Antlr4.Runtime.Misc.IntervalSet s in sets)
			{
				r.AddAll(s);
			}
			return r;
		}

		public virtual Antlr4.Runtime.Misc.IntervalSet AddAll(IntSet set)
		{
			if (set == null)
			{
				return this;
			}
			if (!(set is Antlr4.Runtime.Misc.IntervalSet))
			{
				throw new ArgumentException("can't add non IntSet (" + set.GetType().FullName + ") to IntervalSet"
					);
			}
			Antlr4.Runtime.Misc.IntervalSet other = (Antlr4.Runtime.Misc.IntervalSet)set;
			// walk set and add each interval
			int n = other.intervals.Count;
			for (int i = 0; i < n; i++)
			{
				Interval I = other.intervals[i];
				this.Add(I.a, I.b);
			}
			return this;
		}

		public virtual Antlr4.Runtime.Misc.IntervalSet Complement(int minElement, int maxElement
			)
		{
			return this.Complement(Antlr4.Runtime.Misc.IntervalSet.Of(minElement, maxElement)
				);
		}

		/// <summary>
		/// Given the set of possible values (rather than, say UNICODE or MAXINT),
		/// return a new set containing all elements in vocabulary, but not in
		/// this.
		/// </summary>
		/// <remarks>
		/// Given the set of possible values (rather than, say UNICODE or MAXINT),
		/// return a new set containing all elements in vocabulary, but not in
		/// this.  The computation is (vocabulary - this).
		/// 'this' is assumed to be either a subset or equal to vocabulary.
		/// </remarks>
		public virtual Antlr4.Runtime.Misc.IntervalSet Complement(IntSet vocabulary)
		{
			if (vocabulary == null)
			{
				return null;
			}
			// nothing in common with null set
			if (!(vocabulary is Antlr4.Runtime.Misc.IntervalSet))
			{
				throw new ArgumentException("can't complement with non IntervalSet (" + vocabulary
					.GetType().FullName + ")");
			}
			Antlr4.Runtime.Misc.IntervalSet vocabularyIS = ((Antlr4.Runtime.Misc.IntervalSet)
				vocabulary);
			int maxElement = vocabularyIS.GetMaxElement();
			Antlr4.Runtime.Misc.IntervalSet compl = new Antlr4.Runtime.Misc.IntervalSet();
			int n = intervals.Count;
			if (n == 0)
			{
				return compl;
			}
			Interval first = intervals[0];
			// add a range from 0 to first.a constrained to vocab
			if (first.a > 0)
			{
				Antlr4.Runtime.Misc.IntervalSet s = Antlr4.Runtime.Misc.IntervalSet.Of(0, first.a
					 - 1);
				Antlr4.Runtime.Misc.IntervalSet a = s.And(vocabularyIS);
				compl.AddAll(a);
			}
			for (int i = 1; i < n; i++)
			{
				// from 2nd interval .. nth
				Interval previous = intervals[i - 1];
				Interval current = intervals[i];
				Antlr4.Runtime.Misc.IntervalSet s = Antlr4.Runtime.Misc.IntervalSet.Of(previous.b
					 + 1, current.a - 1);
				Antlr4.Runtime.Misc.IntervalSet a = s.And(vocabularyIS);
				compl.AddAll(a);
			}
			Interval last = intervals[n - 1];
			// add a range from last.b to maxElement constrained to vocab
			if (last.b < maxElement)
			{
				Antlr4.Runtime.Misc.IntervalSet s = Antlr4.Runtime.Misc.IntervalSet.Of(last.b + 1
					, maxElement);
				Antlr4.Runtime.Misc.IntervalSet a = s.And(vocabularyIS);
				compl.AddAll(a);
			}
			return compl;
		}

		/// <summary>Compute this-other via this&~other.</summary>
		/// <remarks>
		/// Compute this-other via this&~other.
		/// Return a new set containing all elements in this but not in other.
		/// other is assumed to be a subset of this;
		/// anything that is in other but not in this will be ignored.
		/// </remarks>
		public virtual Antlr4.Runtime.Misc.IntervalSet Subtract(IntSet other)
		{
			// assume the whole unicode range here for the complement
			// because it doesn't matter.  Anything beyond the max of this' set
			// will be ignored since we are doing this & ~other.  The intersection
			// will be empty.  The only problem would be when this' set max value
			// goes beyond MAX_CHAR_VALUE, but hopefully the constant MAX_CHAR_VALUE
			// will prevent this.
			return this.And(((Antlr4.Runtime.Misc.IntervalSet)other).Complement(CompleteCharSet
				));
		}

		public virtual Antlr4.Runtime.Misc.IntervalSet Or(IntSet a)
		{
			Antlr4.Runtime.Misc.IntervalSet o = new Antlr4.Runtime.Misc.IntervalSet();
			o.AddAll(this);
			o.AddAll(a);
			return o;
		}

		/// <summary>Return a new set with the intersection of this set with other.</summary>
		/// <remarks>
		/// Return a new set with the intersection of this set with other.  Because
		/// the intervals are sorted, we can use an iterator for each list and
		/// just walk them together.  This is roughly O(min(n,m)) for interval
		/// list lengths n and m.
		/// </remarks>
		public virtual Antlr4.Runtime.Misc.IntervalSet And(IntSet other)
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

		/// <summary>Is el in any range of this set?</summary>
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

		/// <summary>return true if this set has no members</summary>
		public virtual bool IsNil()
		{
			return intervals == null || intervals.IsEmpty();
		}

		/// <summary>If this set is a single integer, return it otherwise Token.INVALID_TYPE</summary>
		public virtual int GetSingleElement()
		{
			if (intervals != null && intervals.Count == 1)
			{
				Interval I = intervals[0];
				if (I.a == I.b)
				{
					return I.a;
				}
			}
			return Token.InvalidType;
		}

		public virtual int GetMaxElement()
		{
			if (IsNil())
			{
				return Token.InvalidType;
			}
			Interval last = intervals[intervals.Count - 1];
			return last.b;
		}

		/// <summary>Return minimum element &gt;= 0</summary>
		public virtual int GetMinElement()
		{
			if (IsNil())
			{
				return Token.InvalidType;
			}
			int n = intervals.Count;
			for (int i = 0; i < n; i++)
			{
				Interval I = intervals[i];
				int a = I.a;
				int b = I.b;
				for (int v = a; v <= b; v++)
				{
					if (v >= 0)
					{
						return v;
					}
				}
			}
			return Token.InvalidType;
		}

		/// <summary>Return a list of Interval objects.</summary>
		/// <remarks>Return a list of Interval objects.</remarks>
		public virtual IList<Interval> GetIntervals()
		{
			return intervals;
		}

		public override int GetHashCode()
		{
			if (IsNil())
			{
				return 0;
			}
			int n = 0;
			// just add left edge of intervals
			foreach (Interval I in intervals)
			{
				n += I.a;
			}
			return n;
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
			return this.intervals.Equals(other.intervals);
		}

		public override string ToString()
		{
			return ToString(false);
		}

		public virtual string ToString(bool elemAreChar)
		{
			StringBuilder buf = new StringBuilder();
			if (this.intervals == null || this.intervals.IsEmpty())
			{
				return "{}";
			}
			if (this.Size() > 1)
			{
				buf.Append("{");
			}
			IEnumerator<Interval> iter = this.intervals.GetEnumerator();
			while (iter.HasNext())
			{
				Interval I = iter.Next();
				int a = I.a;
				int b = I.b;
				if (a == b)
				{
					if (a == -1)
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
				if (iter.HasNext())
				{
					buf.Append(", ");
				}
			}
			if (this.Size() > 1)
			{
				buf.Append("}");
			}
			return buf.ToString();
		}

		public virtual string ToString(string[] tokenNames)
		{
			StringBuilder buf = new StringBuilder();
			if (this.intervals == null || this.intervals.IsEmpty())
			{
				return "{}";
			}
			if (this.Size() > 1)
			{
				buf.Append("{");
			}
			IEnumerator<Interval> iter = this.intervals.GetEnumerator();
			while (iter.HasNext())
			{
				Interval I = iter.Next();
				int a = I.a;
				int b = I.b;
				if (a == b)
				{
					buf.Append(ElementName(tokenNames, a));
				}
				else
				{
					for (int i = a; i <= b; i++)
					{
						if (i > a)
						{
							buf.Append(", ");
						}
						buf.Append(ElementName(tokenNames, i));
					}
				}
				if (iter.HasNext())
				{
					buf.Append(", ");
				}
			}
			if (this.Size() > 1)
			{
				buf.Append("}");
			}
			return buf.ToString();
		}

		protected internal virtual string ElementName(string[] tokenNames, int a)
		{
			if (a == Token.Eof)
			{
				return "<EOF>";
			}
			else
			{
				if (a == Token.Epsilon)
				{
					return "<EPSILON>";
				}
				else
				{
					return tokenNames[a];
				}
			}
		}

		public virtual int Size()
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

		public virtual IntegerList ToIntegerList()
		{
			IntegerList values = new IntegerList(Size());
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
			IList<int> values = new AList<int>();
			int n = intervals.Count;
			for (int i = 0; i < n; i++)
			{
				Interval I = intervals[i];
				int a = I.a;
				int b = I.b;
				for (int v = a; v <= b; v++)
				{
					values.AddItem(v);
				}
			}
			return values;
		}

		public virtual ICollection<int> ToSet()
		{
			ICollection<int> s = new HashSet<int>();
			foreach (Interval I in intervals)
			{
				int a = I.a;
				int b = I.b;
				for (int v = a; v <= b; v++)
				{
					s.AddItem(v);
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
					intervals.Remove(i);
					break;
				}
				// if on left edge x..b, adjust left
				if (el == a)
				{
					intervals.Set(i, Interval.Of(I.a + 1, I.b));
					break;
				}
				// if on right edge a..x, adjust right
				if (el == b)
				{
					intervals.Set(i, Interval.Of(I.a, I.b - 1));
					break;
				}
				// if in middle a..x..b, split interval
				if (el > a && el < b)
				{
					// found in this interval
					int oldb = I.b;
					intervals.Set(i, Interval.Of(I.a, el - 1));
					// [a..x-1]
					Add(el + 1, oldb);
				}
			}
		}

		// add [x+1..b]
		public virtual bool IsReadonly()
		{
			return @readonly;
		}

		public virtual void SetReadonly(bool @readonly)
		{
			this.@readonly = @readonly;
		}
	}
}
