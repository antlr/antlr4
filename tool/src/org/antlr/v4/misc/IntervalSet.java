/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.misc;

import org.antlr.v4.automata.Label;
import org.antlr.v4.runtime.misc.LABitSet;
import org.antlr.v4.tool.Grammar;

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
	public static final IntervalSet COMPLETE_SET = IntervalSet.of(0, Label.MAX_CHAR_VALUE);

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

    /** Add a single element to the set.  An isolated element is stored
     *  as a range el..el.
     */
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
		for (ListIterator iter = intervals.listIterator(); iter.hasNext();) {
			Interval r = (Interval) iter.next();
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
					Interval next = (Interval) iter.next();
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

	/*
	protected void add(Interval addition) {
        //System.out.println("add "+addition+" to "+intervals.toString());
        if ( addition.b<addition.a ) {
            return;
        }
        // find position in list
        //for (ListIterator iter = intervals.listIterator(); iter.hasNext();) {
		int n = intervals.size();
		for (int i=0; i<n; i++) {
			Interval r = (Interval)intervals.get(i);
            if ( addition.equals(r) ) {
                return;
            }
            if ( addition.adjacent(r) || !addition.disjoint(r) ) {
                // next to each other, make a single larger interval
                Interval bigger = addition.union(r);
				intervals.set(i, bigger);
                // make sure we didn't just create an interval that
                // should be merged with next interval in list
				if ( (i+1)<n ) {
					i++;
					Interval next = (Interval)intervals.get(i);
                    if ( bigger.adjacent(next)||!bigger.disjoint(next) ) {
                        // if we bump up against or overlap next, merge
						intervals.remove(i); // remove next one
						i--;
						intervals.set(i, bigger.union(next)); // set to 3 merged ones
                    }
                }
                return;
            }
            if ( addition.startsBeforeDisjoint(r) ) {
                // insert before r
				intervals.add(i, addition);
                return;
            }
            // if disjoint and after r, a future iteration will handle it
        }
        // ok, must be after last interval (and disjoint from last interval)
        // just add it
        intervals.add(addition);
    }
*/

	public void addAll(IntSet set) {
		if ( set==null ) {
			return;
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
			Interval I = (Interval) other.intervals.get(i);
			this.add(I.a,I.b);
		}
    }

    public IntSet complement(int minElement, int maxElement) {
        return this.complement(IntervalSet.of(minElement,maxElement));
    }

    /** Given the set of possible values (rather than, say UNICODE or MAXINT),
     *  return a new set containing all elements in vocabulary, but not in
     *  this.  The computation is (vocabulary - this).
     *
     *  'this' is assumed to be either a subset or equal to vocabulary.
     */
    public IntSet complement(IntSet vocabulary) {
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
		Interval first = (Interval)intervals.get(0);
		// add a range from 0 to first.a constrained to vocab
		if ( first.a > 0 ) {
			IntervalSet s = IntervalSet.of(0, first.a-1);
			IntervalSet a = (IntervalSet)s.and(vocabularyIS);
			compl.addAll(a);
		}
		for (int i=1; i<n; i++) { // from 2nd interval .. nth
			Interval previous = (Interval)intervals.get(i-1);
			Interval current = (Interval)intervals.get(i);
			IntervalSet s = IntervalSet.of(previous.b+1, current.a-1);
			IntervalSet a = (IntervalSet)s.and(vocabularyIS);
			compl.addAll(a);
		}
		Interval last = (Interval)intervals.get(n -1);
		// add a range from last.b to maxElement constrained to vocab
		if ( last.b < maxElement ) {
			IntervalSet s = IntervalSet.of(last.b+1, maxElement);
			IntervalSet a = (IntervalSet)s.and(vocabularyIS);
			compl.addAll(a);
		}
		return compl;
    }

	/** Compute this-other via this&~other.
	 *  Return a new set containing all elements in this but not in other.
	 *  other is assumed to be a subset of this;
     *  anything that is in other but not in this will be ignored.
	 */
	public IntSet subtract(IntSet other) {
		// assume the whole unicode range here for the complement
		// because it doesn't matter.  Anything beyond the max of this' set
		// will be ignored since we are doing this & ~other.  The intersection
		// will be empty.  The only problem would be when this' set max value
		// goes beyond MAX_CHAR_VALUE, but hopefully the constant MAX_CHAR_VALUE
		// will prevent this.
		return this.and(((IntervalSet)other).complement(COMPLETE_SET));
	}

	/** return a new set containing all elements in this but not in other.
     *  Intervals may have to be broken up when ranges in this overlap
     *  with ranges in other.  other is assumed to be a subset of this;
     *  anything that is in other but not in this will be ignored.
	 *
	 *  Keep around, but 10-20-2005, I decided to make complement work w/o
	 *  subtract and so then subtract can simply be a&~b
	 *
    public IntSet subtract(IntSet other) {
        if ( other==null || !(other instanceof IntervalSet) ) {
            return null; // nothing in common with null set
        }

        IntervalSet diff = new IntervalSet();

        // iterate down both interval lists
        ListIterator thisIter = this.intervals.listIterator();
        ListIterator otherIter = ((IntervalSet)other).intervals.listIterator();
        Interval mine=null;
        Interval theirs=null;
        if ( thisIter.hasNext() ) {
            mine = (Interval)thisIter.next();
        }
        if ( otherIter.hasNext() ) {
            theirs = (Interval)otherIter.next();
        }
        while ( mine!=null ) {
            //System.out.println("mine="+mine+", theirs="+theirs);
            // CASE 1: nothing in theirs removes a chunk from mine
            if ( theirs==null || mine.disjoint(theirs) ) {
                // SUBCASE 1a: finished traversing theirs; keep adding mine now
                if ( theirs==null ) {
                    // add everything in mine to difference since theirs done
                    diff.add(mine);
                    mine = null;
                    if ( thisIter.hasNext() ) {
                        mine = (Interval)thisIter.next();
                    }
                }
                else {
                    // SUBCASE 1b: mine is completely to the left of theirs
                    // so we can add to difference; move mine, but not theirs
                    if ( mine.startsBeforeDisjoint(theirs) ) {
                        diff.add(mine);
                        mine = null;
                        if ( thisIter.hasNext() ) {
                            mine = (Interval)thisIter.next();
                        }
                    }
                    // SUBCASE 1c: theirs is completely to the left of mine
                    else {
                        // keep looking in theirs
                        theirs = null;
                        if ( otherIter.hasNext() ) {
                            theirs = (Interval)otherIter.next();
                        }
                    }
                }
            }
            else {
                // CASE 2: theirs breaks mine into two chunks
                if ( mine.properlyContains(theirs) ) {
                    // must add two intervals: stuff to left and stuff to right
                    diff.add(mine.a, theirs.a-1);
                    // don't actually add stuff to right yet as next 'theirs'
                    // might overlap with it
                    // The stuff to the right might overlap with next "theirs".
                    // so it is considered next
                    Interval right = new Interval(theirs.b+1, mine.b);
                    mine = right;
                    // move theirs forward
                    theirs = null;
                    if ( otherIter.hasNext() ) {
                        theirs = (Interval)otherIter.next();
                    }
                }

                // CASE 3: theirs covers mine; nothing to add to diff
                else if ( theirs.properlyContains(mine) ) {
                    // nothing to add, theirs forces removal totally of mine
                    // just move mine looking for an overlapping interval
                    mine = null;
                    if ( thisIter.hasNext() ) {
                        mine = (Interval)thisIter.next();
                    }
                }

                // CASE 4: non proper overlap
                else {
                    // overlap, but not properly contained
                    diff.add(mine.differenceNotProperlyContained(theirs));
                    // update iterators
                    boolean moveTheirs = true;
                    if ( mine.startsBeforeNonDisjoint(theirs) ||
                         theirs.b > mine.b )
                    {
                        // uh oh, right of theirs extends past right of mine
                        // therefore could overlap with next of mine so don't
                        // move theirs iterator yet
                        moveTheirs = false;
                    }
                    // always move mine
                    mine = null;
                    if ( thisIter.hasNext() ) {
                        mine = (Interval)thisIter.next();
                    }
                    if ( moveTheirs ) {
                        theirs = null;
                        if ( otherIter.hasNext() ) {
                            theirs = (Interval)otherIter.next();
                        }
                    }
                }
            }
        }
        return diff;
    }
	 */

	public IntSet or(IntSet a) {
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
	public IntSet and(IntSet other) {
		if ( other==null ) { //|| !(other instanceof IntervalSet) ) {
			return null; // nothing in common with null set
		}

		ArrayList myIntervals = (ArrayList)this.intervals;
		ArrayList theirIntervals = (ArrayList)((IntervalSet)other).intervals;
		IntervalSet intersection = null;
		int mySize = myIntervals.size();
		int theirSize = theirIntervals.size();
		int i = 0;
		int j = 0;
		// iterate down both interval lists looking for nondisjoint intervals
		while ( i<mySize && j<theirSize ) {
			Interval mine = (Interval)myIntervals.get(i);
			Interval theirs = (Interval)theirIntervals.get(j);
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
    public boolean member(int el) {
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = (Interval) intervals.get(i);
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
    public boolean isNil() {
        return intervals==null || intervals.size()==0;
    }

    /** If this set is a single integer, return it otherwise Label.INVALID */
    public int getSingleElement() {
        if ( intervals!=null && intervals.size()==1 ) {
            Interval I = (Interval)intervals.get(0);
            if ( I.a == I.b ) {
                return I.a;
            }
        }
        return Label.INVALID;
    }

	public int getMaxElement() {
		if ( isNil() ) {
			return Label.INVALID;
		}
		Interval last = (Interval)intervals.get(intervals.size()-1);
		return last.b;
	}

	/** Return minimum element >= 0 */
	public int getMinElement() {
		if ( isNil() ) {
			return Label.INVALID;
		}
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = (Interval) intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				if ( v>=0 ) return v;
			}
		}
		return Label.INVALID;
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

    public String toString() {
        return toString((Grammar)null);
    }

    public String toString(Grammar g) {
        StringBuffer buf = new StringBuffer();
		if ( this.intervals==null || this.intervals.size()==0 ) {
			return "{}";
		}
        if ( this.size()>1 ) {
            buf.append("{");
        }
        Iterator iter = this.intervals.iterator();
        while (iter.hasNext()) {
            Interval I = (Interval) iter.next();
            int a = I.a;
            int b = I.b;
            if ( a==b ) {
                if ( g!=null ) {
                    buf.append(g.getTokenDisplayName(a));
                }
                else {
                    buf.append(a);
                }
            }
            else {
				if ( g!=null ) {
					if ( !g.isLexer() ) {
						for (int i=a; i<=b; i++) {
							if ( i>a ) buf.append(", ");
							buf.append(g.getTokenDisplayName(i));
						}
					}
					else {
						buf.append(g.getTokenDisplayName(a)+".."+g.getTokenDisplayName(b));
					}
				}
				else {
					buf.append(a+".."+b);
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

    public int size() {
		int n = 0;
		int numIntervals = intervals.size();
		if ( numIntervals==1 ) {
			Interval firstInterval = this.intervals.get(0);
			return firstInterval.b-firstInterval.a+1;
		}
		for (int i = 0; i < numIntervals; i++) {
			Interval I = (Interval) intervals.get(i);
			n += (I.b-I.a+1);
		}
		return n;
    }

    public List<Integer> toList() {
		List<Integer> values = new ArrayList<Integer>();
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = (Interval) intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				values.add(Utils.integer(v));
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
			Interval I = (Interval) intervals.get(j);
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
			Interval I = (Interval) intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				values[j] = v;
				j++;
			}
		}
		return values;
	}

	public LABitSet toRuntimeBitSet() {
		LABitSet s =
			new LABitSet(getMaxElement()+1);
		int n = intervals.size();
		for (int i = 0; i < n; i++) {
			Interval I = (Interval) intervals.get(i);
			int a = I.a;
			int b = I.b;
			for (int v=a; v<=b; v++) {
				s.add(v);
			}
		}
		return s;
	}

	public void remove(int el) {
        throw new NoSuchMethodError("IntervalSet.remove() unimplemented");
    }

	/*
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("size "+intervals.size()+" "+size());
	}
	*/
}
