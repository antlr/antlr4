/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
package org.antlr.v4.runtime.misc;

/** An immutable inclusive interval a..b */
public class Interval {
	public static final int INTERVAL_POOL_MAX_VALUE = 1000;

	public static final Interval INVALID = new Interval(-1,-2);

	static Interval[] cache = new Interval[INTERVAL_POOL_MAX_VALUE+1];

	public int a;
	public int b;

	public static int creates = 0;
	public static int misses = 0;
	public static int hits = 0;
	public static int outOfRange = 0;

	public Interval(int a, int b) { this.a=a; this.b=b; }

	/** Interval objects are used readonly so share all with the
	 *  same single value a==b up to some max size.  Use an array as a perfect hash.
	 *  Return shared object for 0..INTERVAL_POOL_MAX_VALUE or a new
	 *  Interval object with a..a in it.  On Java.g4, 218623 IntervalSets
	 *  have a..a (set with 1 element).
	 */
	public static Interval of(int a, int b) {
		// cache just a..a
		if ( a!=b || a<0 || a>INTERVAL_POOL_MAX_VALUE ) {
			return new Interval(a,b);
		}
		if ( cache[a]==null ) {
			cache[a] = new Interval(a,a);
		}
		return cache[a];
	}

	/** return number of elements between a and b inclusively. x..x is length 1.
	 *  if b &lt; a, then length is 0.  9..10 has length 2.
	 */
	public int length() {
		if ( b<a ) return 0;
		return b-a+1;
	}

	@Override
	public boolean equals(Object o) {
		if ( o==null || !(o instanceof Interval) ) {
			return false;
		}
		Interval other = (Interval)o;
		return this.a==other.a && this.b==other.b;
	}

	@Override
	public int hashCode() {
		int hash = 23;
		hash = hash * 31 + a;
		hash = hash * 31 + b;
		return hash;
	}

	/** Does this start completely before other? Disjoint */
	public boolean startsBeforeDisjoint(Interval other) {
		return this.a<other.a && this.b<other.a;
	}

	/** Does this start at or before other? Nondisjoint */
	public boolean startsBeforeNonDisjoint(Interval other) {
		return this.a<=other.a && this.b>=other.a;
	}

	/** Does this.a start after other.b? May or may not be disjoint */
	public boolean startsAfter(Interval other) { return this.a>other.a; }

	/** Does this start completely after other? Disjoint */
	public boolean startsAfterDisjoint(Interval other) {
		return this.a>other.b;
	}

	/** Does this start after other? NonDisjoint */
	public boolean startsAfterNonDisjoint(Interval other) {
		return this.a>other.a && this.a<=other.b; // this.b>=other.b implied
	}

	/** Are both ranges disjoint? I.e., no overlap? */
	public boolean disjoint(Interval other) {
		return startsBeforeDisjoint(other) || startsAfterDisjoint(other);
	}

	/** Are two intervals adjacent such as 0..41 and 42..42? */
	public boolean adjacent(Interval other) {
		return this.a == other.b+1 || this.b == other.a-1;
	}

	public boolean properlyContains(Interval other) {
		return other.a >= this.a && other.b <= this.b;
	}

	/** Return the interval computed from combining this and other */
	public Interval union(Interval other) {
		return Interval.of(Math.min(a, other.a), Math.max(b, other.b));
	}

	/** Return the interval in common between this and o */
	public Interval intersection(Interval other) {
		return Interval.of(Math.max(a, other.a), Math.min(b, other.b));
	}

	/** Return the interval with elements from this not in other;
	 *  other must not be totally enclosed (properly contained)
	 *  within this, which would result in two disjoint intervals
	 *  instead of the single one returned by this method.
	 */
	public Interval differenceNotProperlyContained(Interval other) {
		Interval diff = null;
		// other.a to left of this.a (or same)
		if ( other.startsBeforeNonDisjoint(this) ) {
			diff = Interval.of(Math.max(this.a, other.b + 1),
							   this.b);
		}

		// other.a to right of this.a
		else if ( other.startsAfterNonDisjoint(this) ) {
			diff = Interval.of(this.a, other.a - 1);
		}
		return diff;
	}

	@Override
	public String toString() {
		return a+".."+b;
	}
}
