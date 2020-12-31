/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;

namespace Antlr4.Runtime.Misc
{
    /// <summary>An immutable inclusive interval a..b.</summary>
    /// <remarks>An immutable inclusive interval a..b.</remarks>
    public struct Interval
    {
        public static readonly Antlr4.Runtime.Misc.Interval Invalid = new Antlr4.Runtime.Misc.Interval(-1, -2);

        /// <summary>The start of the interval.</summary>
        /// <remarks>The start of the interval.</remarks>
        public readonly int a;

        /// <summary>The end of the interval (inclusive).</summary>
        /// <remarks>The end of the interval (inclusive).</remarks>
        public readonly int b;

        public Interval(int a, int b)
        {
            this.a = a;
            this.b = b;
        }

        /// <summary>
        /// Interval objects are used readonly so share all with the
        /// same single value a==b up to some max size.
        /// </summary>
        /// <remarks>
        /// Interval objects are used readonly so share all with the
        /// same single value a==b up to some max size.  Use an array as a perfect hash.
        /// Return shared object for 0..INTERVAL_POOL_MAX_VALUE or a new
        /// Interval object with a..a in it.  On Java.g4, 218623 IntervalSets
        /// have a..a (set with 1 element).
        /// </remarks>
        public static Antlr4.Runtime.Misc.Interval Of(int a, int b)
        {
            return new Antlr4.Runtime.Misc.Interval(a, b);
        }

        /// <summary>return number of elements between a and b inclusively.</summary>
        /// <remarks>
        /// return number of elements between a and b inclusively. x..x is length 1.
        /// if b &lt; a, then length is 0. 9..10 has length 2.
        /// </remarks>
        public int Length
        {
            get
            {
                if (b < a)
                {
                    return 0;
                }
                return b - a + 1;
            }
        }

        public override bool Equals(object o)
        {
            if (!(o is Antlr4.Runtime.Misc.Interval))
            {
                return false;
            }

            Antlr4.Runtime.Misc.Interval other = (Antlr4.Runtime.Misc.Interval)o;
            return this.a == other.a && this.b == other.b;
        }

        public override int GetHashCode()
        {
            int hash = 23;
            hash = hash * 31 + a;
            hash = hash * 31 + b;
            return hash;
        }

        /// <summary>Does this start completely before other? Disjoint</summary>
        public bool StartsBeforeDisjoint(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a < other.a && this.b < other.a;
        }

        /// <summary>Does this start at or before other? Nondisjoint</summary>
        public bool StartsBeforeNonDisjoint(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a <= other.a && this.b >= other.a;
        }

        /// <summary>Does this.a start after other.b? May or may not be disjoint</summary>
        public bool StartsAfter(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a > other.a;
        }

        /// <summary>Does this start completely after other? Disjoint</summary>
        public bool StartsAfterDisjoint(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a > other.b;
        }

        /// <summary>Does this start after other? NonDisjoint</summary>
        public bool StartsAfterNonDisjoint(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a > other.a && this.a <= other.b;
        }

        // this.b>=other.b implied
        /// <summary>Are both ranges disjoint? I.e., no overlap?</summary>
        public bool Disjoint(Antlr4.Runtime.Misc.Interval other)
        {
            return StartsBeforeDisjoint(other) || StartsAfterDisjoint(other);
        }

        /// <summary>Are two intervals adjacent such as 0..41 and 42..42?</summary>
        public bool Adjacent(Antlr4.Runtime.Misc.Interval other)
        {
            return this.a == other.b + 1 || this.b == other.a - 1;
        }

        public bool ProperlyContains(Antlr4.Runtime.Misc.Interval other)
        {
            return other.a >= this.a && other.b <= this.b;
        }

        /// <summary>Return the interval computed from combining this and other</summary>
        public Antlr4.Runtime.Misc.Interval Union(Antlr4.Runtime.Misc.Interval other)
        {
            return Antlr4.Runtime.Misc.Interval.Of(Math.Min(a, other.a), Math.Max(b, other.b));
        }

        /// <summary>Return the interval in common between this and o</summary>
        public Antlr4.Runtime.Misc.Interval Intersection(Antlr4.Runtime.Misc.Interval other)
        {
            return Antlr4.Runtime.Misc.Interval.Of(Math.Max(a, other.a), Math.Min(b, other.b));
        }

        /// <summary>
        /// Return the interval with elements from
        /// <c>this</c>
        /// not in
        /// <paramref name="other"/>
        /// ;
        /// <paramref name="other"/>
        /// must not be totally enclosed (properly contained)
        /// within
        /// <c>this</c>
        /// , which would result in two disjoint intervals
        /// instead of the single one returned by this method.
        /// </summary>
        public Antlr4.Runtime.Misc.Interval? DifferenceNotProperlyContained(Antlr4.Runtime.Misc.Interval other)
        {
            Antlr4.Runtime.Misc.Interval? diff = null;
            // other.a to left of this.a (or same)
            if (other.StartsBeforeNonDisjoint(this))
            {
                diff = Antlr4.Runtime.Misc.Interval.Of(Math.Max(this.a, other.b + 1), this.b);
            }
            else
            {
                // other.a to right of this.a
                if (other.StartsAfterNonDisjoint(this))
                {
                    diff = Antlr4.Runtime.Misc.Interval.Of(this.a, other.a - 1);
                }
            }
            return diff;
        }

        public override string ToString()
        {
            return a + ".." + b;
        }
    }
}
