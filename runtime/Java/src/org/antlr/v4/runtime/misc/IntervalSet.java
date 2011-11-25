/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** A set of integers that relies on ranges being common to do
 *  "run-length-encoded" like compression (if you view an IntSet like
 *  a BitSet with runs of 0s and 1s).  Only ranges are recorded so that
 *  a few ints up near value 1000 don't cause massive bitsets, just two
 *  integer intervals.
 *
 *  element values may be negative.  Useful for sets of EPSILON and EOF.
 *
 *  0..9 char range is index pair ['\u0030','\u0039'].
 *  Multiple ranges are encoded with multiple index pairs.  Isolated
 *  elements are encoded with an index pair where both intervals are the same.
 *
 *  The ranges are ordered and disjoint so that 2..6 appears before 101..103.
 */
public class IntervalSet implements IntSet {
	public static final IntervalSet COMPLETE_CHAR_SET = IntervalSet.of(0, Lexer.MAX_CHAR_VALUE);
	public static final IntervalSet EMPTY_SET = new IntervalSet();

	/** The list of sorted, disjoint intervals. */
    protected List<Interval> intervals;

	/** Create a set with no elements */
    public IntervalSet() {
        intervals = new ArrayList<Interval>(2); // most sets are 1 or 2 elements
    }

	public IntervalSet(List<Interval> intervals) {
		this.intervals = intervals;
	}

	public IntervalSet(IntervalSet set) {
		this();
		addAll(set);
	}

	/** Create a set with a single element, el. */
    @NotNull
    public static IntervalSet of(int a) {
		IntervalSet s = new IntervalSet();
        s.add(a);
        return s;
    }

    /** Create a set with all ints within range [a..b] (inclusive) */
	public static IntervalSet of(int a, int b) {
		IntervalSet s = new IntervalSet();
		s.add(a,b);
		return s;
	}

	public void clear() {
		intervals.clear();
	}

    /** Add a single element to the set.  An isolated element is stored
     *  as a range el..el.
     */
    @Override
    public void add(int el) {
        add(el,el);
    }

    /** Add interval; i.e., add all integers from a to b to set.
     *  If b<a, do nothing.
     *  Keep list in sorted order (by left range value).
     *  If overlap, combine ranges.  For example,
     *  If this is {1..5, 10..20}, adding 6..7 yields
     *  {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
     */
    public void add(int a, int b) {
        add(Interval.create(a,b));
    }

	// copy on write so we can cache a..a intervals and sets of that
	protected void add(Interval addition) {
		//System.out.println("add "+addition+" to "+intervals.toString());
		if ( addition.b<addition.a ) {
			return;
		}
		// find position in list
		// Use iterators as we modify list in place
		for (ListIterator<Interval> iter = intervals.listIterator(); iter.hasNext();) {
			Interval r = iter.next();
			if ( addition.equals(r) ) {
				return;
			}
			if ( addition.adjacent(r) || !addition.disjoint(r) ) {
				// next to each other, make a single larger interval
				Interval bigger = addition.union(r);
				iter.set(bigger);
				// make sure we didn't just create an interval that
				// should be merged with next interval in list
				if ( iter.hasNext() ) {
					Interval next = iter.next();
					if ( bigger.adjacent(next)||!bigger.disjoint(next) ) {
						// if we bump up against or overlap next, merge
						iter.remove();   // remove this one
						iter.previous(); // move backwards to what we just set
						iter.set(bigger.union(next)); // set to 3 merged ones
					}
				}
				return;
			}
			if ( addition.startsBeforeDisjoint(r) ) {
				// insert before r
				iter.previous();
				iter.add(addition);
				return;
			}
			// if disjoint and after r, a future iteration will handle it
		}
		// ok, must be after last interval (and disjoint from last interval)
		// just add it
		intervals.add(addition);
	}

	/** combine all sets in the array returned the or'd value */
	public static IntervalSet or(IntervalSet[] sets) {
		IntervalSet r = new IntervalSet();
		for (IntervalSet s : sets) r.addAll(s);
		return r;
	}

	@Override
	public IntervalSet addAll(IntSet set) {
		if ( set==null ) {
			return this;
		}
        if ( !(set instanceof IntervalSet) ) {
            throw new IllegalArgumentException("can't add non IntSet ("+
											   set.getClass().getName()+
											   ") to IntervalSet");
        }
        IntervalSet other = (IntervalSet)set;
        // walk set and add each interval
		int n = other.intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = other.intervals.get(i);
			this.add(I.a,I.b);
		}
		return this;
    }

    public IntervalSet complement(int minElement, int maxElement) {
        return this.complement(IntervalSet.of(minElement,maxElement));
    }

    /** Given the set of possible values (rather than, say UNICODE or MAXINT),
     *  return a new set containing all elements in vocabulary, but not in
     *  this.  The computation is (vocabulary - this).
     *
     *  'this' is assumed to be either a subset or equal to vocabulary.
     */
    @Override
    public IntervalSet complement(IntSet vocabulary) {
        if ( vocabulary==null ) {
            return null; // nothing in common with null set
        }
		if ( !(vocabulary instanceof IntervalSet ) ) {
			throw new IllegalArgumentException("can't complement with non IntervalSet ("+
											   vocabulary.getClass().getName()+")");
		}
		IntervalSet vocabularyIS = ((IntervalSet)vocabulary);
		int maxElement = vocabularyIS.getMaxElement();

		IntervalSet compl = new IntervalSet();
		int n = intervals.size();
		if ( n ==0 ) {
			return compl;
		}
		Interval first = intervals.get(0);
		// add a range from 0 to first.a constrained to vocab
		if ( first.a > 0 ) {
			IntervalSet s = IntervalSet.of(0, first.a-1);
			IntervalSet a = s.and(vocabularyIS);
			compl.addAll(a);
		}
		for (int i=1; i<n; i++) { // from 2nd interval .. nth
			Interval previous = intervals.get(i-1);
			Interval current = intervals.get(i);
			IntervalSet s = IntervalSet.of(previous.b+1, current.a-1);
			IntervalSet a = s.and(vocabularyIS);
			compl.addAll(a);
		}
		Interval last = intervals.get(n -1);
		// add a range from last.b to maxElement constrained to vocab
		if ( last.b < maxElement ) {
			IntervalSet s = IntervalSet.of(last.b+1, maxElement);
			IntervalSet a = s.and(vocabularyIS);
			compl.addAll(a);
		}
		return compl;
    }

	/** Compute this-other via this&~other.
	 *  Return a new set containing all elements in this but not in other.
	 *  other is assumed to be a subset of this;
     *  anything that is in other but not in this will be ignored.
	 */
	@Override
	public IntervalSet subtract(IntSet other) {
		// assume the whole unicode range here for the complement
		// because it doesn't matter.  Anything beyond the max of this' set
		// will be ignored since we are doing this & ~other.  The intersection
		// will be empty.  The only problem would be when this' set max value
		// goes beyond MAX_CHAR_VALUE, but hopefully the constant MAX_CHAR_VALUE
		// will prevent this.
		return this.and(((IntervalSet)other).complement(COMPLETE_CHAR_SET));
	}

	@Override
	public IntervalSet or(IntSet a) {
		IntervalSet o = new IntervalSet();
		o.addAll(this);
		o.addAll(a);
		return o;
	}

    /** Return a new set with the intersection of this set with other.  Because
     *  the intervals are sorted, we can use an iterator for each list and
     *  just walk them together.  This is roughly O(min(n,m)) for interval
     *  list lengths n and m.
     */
	@Override
	public IntervalSet and(IntSet other) {
		if ( other==null ) { //|| !(other instanceof IntervalSet) ) {
			return null; // nothing in common with null set
		}

		List<Interval> myIntervals = this.intervals;
		List<Interval> theirIntervals = ((IntervalSet)other).intervals;
		IntervalSet intersection = null;
		int mySize = myIntervals.size();
		int theirSize = theirIntervals.size();
		int i = 0;
		int j = 0;
		// iterate down both interval lists looking for nondisjoint intervals
		while ( i<mySize && j<theirSize ) {
			Interval mine = myIntervals.get(i);
			Interval theirs = theirIntervals.get(j);
			//System.out.println("mine="+mine+" and theirs="+theirs);
			if ( mine.startsBeforeDisjoint(theirs) ) {
				// move this iterator looking for interval that might overlap
				i++;
			}
			else if ( theirs.startsBeforeDisjoint(mine) ) {
				// move other iterator looking for interval that might overlap
				j++;
			}
			else if ( mine.properlyContains(theirs) ) {
				// overlap, add intersection, get next theirs
				if ( intersection==null ) {
					intersection = new IntervalSet();
				}
				intersection.add(mine.intersection(theirs));
				j++;
			}
			else if ( theirs.properlyContains(mine) ) {
				// overlap, add intersection, get next mine
				if ( intersection==null ) {
					intersection = new IntervalSet();
				}
				intersection.add(mine.intersection(theirs));
				i++;
			}
			else if ( !mine.disjoint(theirs) ) {
				// overlap, add intersection
				if ( intersection==null ) {
					intersection = new IntervalSet();
				}
				intersection.add(mine.intersection(theirs));
				// Move the iterator of lower range [a..b], but not
				// the upper range as it may contain elements that will collide
				// with the next iterator. So, if mine=[0..115] and
				// theirs=[115..200], then intersection is 115 and move mine
				// but not theirs as theirs may collide with the next range
				// in thisIter.
				// move both iterators to next ranges
				if ( mine.startsAfterNonDisjoint(theirs) ) {
					j++;
				}
				else if ( theirs.startsAfterNonDisjoint(mine) ) {
					i++;
				}
			}
		}
		if ( intersection==null ) {
			return new IntervalSet();
		}
		return intersection;
	}

    /** Is el in any range of this set? */
    @Override
    public boolean contains(int el) {
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = intervals.get(i);
			int a = I.a;
			int b = I.b;
			if ( el<a ) {
				break; // list is sorted and el is before this interval; not here
			}
			if ( el>=a && el<=b ) {
				return true; // found in this interval
			}
		}
		return false;
/*
		for (ListIterator iter = intervals.listIterator(); iter.hasNext();) {
            Interval I = (Interval) iter.next();
            if ( el<I.a ) {
                break; // list is sorted and el is before this interval; not here
            }
            if ( el>=I.a && el<=I.b ) {
                return true; // found in this interval
            }
        }
        return false;
        */
    }

    /** return true if this set has no members */
    @Override
    public boolean isNil() {
        return intervals==null || intervals.size()==0;
    }

    /** If this set is a single integer, return it otherwise Token.INVALID_TYPE */
    @Override
    public int getSingleElement() {
        if ( intervals!=null && intervals.size()==1 ) {
            Interval I = intervals.get(0);
            if ( I.a == I.b ) {
                return I.a;
            }
        }
        return Token.INVALID_TYPE;
    }

	public int getMaxElement() {
		if ( isNil() ) {
			return Token.INVALID_TYPE;
		}
		Interval last = intervals.get(intervals.size()-1);
		return last.b;
	}

	/** Return minimum element >= 0 */
	public int getMinElement() {
		if ( isNil() ) {
			return Token.INVALID_TYPE;
		}
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				if ( v>=0 ) return v;
			}
		}
		return Token.INVALID_TYPE;
	}

    /** Return a list of Interval objects. */
    public List<Interval> getIntervals() {
        return intervals;
    }

	@Override
	public int hashCode() {
		if ( isNil() ) return 0;
		int n = 0;
		// just add left edge of intervals
		for (Interval I : intervals) n += I.a;
		return n;
	}

	/** Are two IntervalSets equal?  Because all intervals are sorted
     *  and disjoint, equals is a simple linear walk over both lists
     *  to make sure they are the same.  Interval.equals() is used
     *  by the List.equals() method to check the ranges.
     */
    public boolean equals(Object obj) {
        if ( obj==null || !(obj instanceof IntervalSet) ) {
            return false;
        }
        IntervalSet other = (IntervalSet)obj;
		return this.intervals.equals(other.intervals);
	}

	public String toString() { return toString(false); }

	public String toString(boolean elemAreChar) {
		StringBuffer buf = new StringBuffer();
		if ( this.intervals==null || this.intervals.size()==0 ) {
			return "{}";
		}
		if ( this.size()>1 ) {
			buf.append("{");
		}
		Iterator<Interval> iter = this.intervals.iterator();
		while (iter.hasNext()) {
			Interval I = iter.next();
			int a = I.a;
			int b = I.b;
			if ( a==b ) {
				if ( a==-1 ) buf.append("<EOF>");
				else if ( elemAreChar ) buf.append("'"+(char)a+"'");
				else buf.append(a);
			}
			else {
				if ( elemAreChar ) buf.append("'"+(char)a+"'..'"+(char)b+"'");
				else buf.append(a+".."+b);
			}
			if ( iter.hasNext() ) {
				buf.append(", ");
			}
		}
		if ( this.size()>1 ) {
			buf.append("}");
		}
		return buf.toString();
	}

	public String toString(String[] tokenNames) {
		StringBuffer buf = new StringBuffer();
		if ( this.intervals==null || this.intervals.size()==0 ) {
			return "{}";
		}
		if ( this.size()>1 ) {
			buf.append("{");
		}
		Iterator<Interval> iter = this.intervals.iterator();
		while (iter.hasNext()) {
			Interval I = iter.next();
			int a = I.a;
			int b = I.b;
			if ( a==b ) {
				if ( a==-1 ) buf.append("<EOF>");
				else buf.append(tokenNames[a]);
			}
			else {
				for (int i=a; i<=b; i++) {
					if ( i>a ) buf.append(", ");
					buf.append(tokenNames[i]);
				}
			}
			if ( iter.hasNext() ) {
				buf.append(", ");
			}
		}
		if ( this.size()>1 ) {
			buf.append("}");
		}
		return buf.toString();
	}

    @Override
    public int size() {
		int n = 0;
		int numIntervals = intervals.size();
		if ( numIntervals==1 ) {
			Interval firstInterval = this.intervals.get(0);
			return firstInterval.b-firstInterval.a+1;
		}
		for (int i = 0; i < numIntervals; i++) {
			Interval I = intervals.get(i);
			n += (I.b-I.a+1);
		}
		return n;
    }

    @Override
    public List<Integer> toList() {
		List<Integer> values = new ArrayList<Integer>();
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				values.add(v);
			}
		}
		return values;
    }

	/** Get the ith element of ordered set.  Used only by RandomPhrase so
	 *  don't bother to implement if you're not doing that for a new
	 *  ANTLR code gen target.
	 */
	public int get(int i) {
		int n = intervals.size();
		int index = 0;
		for (int j = 0; j < n; j++) {
			Interval I = intervals.get(j);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				if ( index==i ) {
					return v;
				}
				index++;
			}
		}
		return -1;
	}

	public int[] toArray() {
		int[] values = new int[size()];
		int n = intervals.size();
		int j = 0;
		for (int i = 0; i < n; i++) {
			Interval I = intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				values[j] = v;
				j++;
			}
		}
		return values;
	}

	@Override
	public void remove(int el) {
        int n = intervals.size();
        for (int i = 0; i < n; i++) {
            Interval I = intervals.get(i);
            int a = I.a;
            int b = I.b;
            if ( el<a ) {
                break; // list is sorted and el is before this interval; not here
            }
            // if whole interval x..x, rm
            if ( el==a && el==b ) {
                intervals.remove(i);
                return;
            }
            // if on left edge x..b, adjust left
            if ( el==a ) {
                I.a++;
                return;
            }
            // if on right edge a..x, adjust right
            if ( el==b ) {
                I.b--;
                return;
            }
            // if in middle a..x..b, split interval
            if ( el>a && el<b ) { // found in this interval
                int oldb = I.b;
                I.b = el-1;      // [a..x-1]
                add(el+1, oldb); // add [x+1..b]
            }
        }
    }
}
