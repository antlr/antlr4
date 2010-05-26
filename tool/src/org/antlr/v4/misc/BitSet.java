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
import org.antlr.v4.tool.Grammar;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**A BitSet to replace java.util.BitSet.
 *
 * Primary differences are that most set operators return new sets
 * as opposed to oring and anding "in place".  Further, a number of
 * operations were added.  I cannot contain a BitSet because there
 * is no way to access the internal bits (which I need for speed)
 * and, because it is final, I cannot subclass to add functionality.
 * Consider defining set degree.  Without access to the bits, I must
 * call a method n times to test the ith bit...ack!
 *
 * Also seems like or() from util is wrong when size of incoming set is bigger
 * than this.bits.length.
 *
 * @author Terence Parr
 */
public class BitSet implements IntSet, Cloneable {
    protected final static int BITS = 64;    // number of bits / long
    protected final static int LOG_BITS = 6; // 2^6 == 64

    /* We will often need to do a mod operator (i mod nbits).  Its
     * turns out that, for powers of two, this mod operation is
     * same as (i & (nbits-1)).  Since mod is slow, we use a
     * precomputed mod mask to do the mod instead.
     */
    protected final static int MOD_MASK = BITS - 1;

    /** The actual data bits */
    protected long bits[];

    /** Construct a bitset of size one word (64 bits) */
    public BitSet() {
        this(BITS);
    }

    /** Construction from a static array of longs */
    public BitSet(long[] bits_) {
        bits = bits_;
    }

    /** Construct a bitset given the size
     * @param nbits The size of the bitset in bits
     */
    public BitSet(int nbits) {
        bits = new long[((nbits - 1) >> LOG_BITS) + 1];
    }

    /** or this element into this set (grow as necessary to accommodate) */
    public void add(int el) {
        //System.out.println("add("+el+")");
        int n = wordNumber(el);
        //System.out.println("word number is "+n);
        //System.out.println("bits.length "+bits.length);
        if (n >= bits.length) {
            growToInclude(el);
        }
        bits[n] |= bitMask(el);
    }

    public IntSet addAll(IntSet set) {
        if ( set instanceof BitSet ) {
            this.orInPlace((BitSet)set);
        }
		else if ( set instanceof IntervalSet ) {
			IntervalSet other = (IntervalSet)set;
			// walk set and add each interval
			for (Iterator iter = other.intervals.iterator(); iter.hasNext();) {
				Interval I = (Interval) iter.next();
				this.orInPlace(BitSet.range(I.a,I.b));
			}
		}
		else {
			throw new IllegalArgumentException("can't add "+
											   set.getClass().getName()+
											   " to BitSet");
		}
		return this;
    }

	public IntSet addAll(int[] elements) {
		if ( elements==null ) {
			return this;
		}
		for (int i = 0; i < elements.length; i++) {
			int e = elements[i];
			add(e);
		}
		return this;
	}

	public IntSet addAll(Iterable elements) {
		if ( elements==null ) {
			return this;
		}
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Object o = (Object) it.next();
			if ( !(o instanceof Integer) ) {
				throw new IllegalArgumentException();
			}
			Integer eI = (Integer)o;
			add(eI.intValue());
		}
		return this;
	}

    public IntSet and(IntSet a) {
        BitSet s = (BitSet)this.clone();
        s.andInPlace((BitSet)a);
        return s;
    }

    public void andInPlace(BitSet a) {
        int min = Math.min(bits.length, a.bits.length);
        for (int i = min - 1; i >= 0; i--) {
            bits[i] &= a.bits[i];
        }
        // clear all bits in this not present in a (if this bigger than a).
        for (int i = min; i < bits.length; i++) {
            bits[i] = 0;
        }
    }

    private final static long bitMask(int bitNumber) {
        int bitPosition = bitNumber & MOD_MASK; // bitNumber mod BITS
        return 1L << bitPosition;
    }

    public void clear() {
        for (int i = bits.length - 1; i >= 0; i--) {
            bits[i] = 0;
        }
    }

    public void clear(int el) {
        int n = wordNumber(el);
        if (n >= bits.length) {	// grow as necessary to accommodate
            growToInclude(el);
        }
        bits[n] &= ~bitMask(el);
    }

    public Object clone() {
        BitSet s;
        try {
            s = (BitSet)super.clone();
            s.bits = new long[bits.length];
            System.arraycopy(bits, 0, s.bits, 0, bits.length);
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        return s;
    }

    public int size() {
        int deg = 0;
        for (int i = bits.length - 1; i >= 0; i--) {
            long word = bits[i];
            if (word != 0L) {
                for (int bit = BITS - 1; bit >= 0; bit--) {
                    if ((word & (1L << bit)) != 0) {
                        deg++;
                    }
                }
            }
        }
        return deg;
    }

    public boolean equals(Object other) {
        if ( other == null || !(other instanceof BitSet) ) {
            return false;
        }

        BitSet otherSet = (BitSet)other;

        int n = Math.min(this.bits.length, otherSet.bits.length);

        // for any bits in common, compare
        for (int i=0; i<n; i++) {
            if (this.bits[i] != otherSet.bits[i]) {
                return false;
            }
        }

        // make sure any extra bits are off

        if (this.bits.length > n) {
            for (int i = n+1; i<this.bits.length; i++) {
                if (this.bits[i] != 0) {
                    return false;
                }
            }
        }
        else if (otherSet.bits.length > n) {
            for (int i = n+1; i<otherSet.bits.length; i++) {
                if (otherSet.bits[i] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Grows the set to a larger number of bits.
     * @param bit element that must fit in set
     */
    public void growToInclude(int bit) {
        int newSize = Math.max(bits.length << 1, numWordsToHold(bit));
        long newbits[] = new long[newSize];
        System.arraycopy(bits, 0, newbits, 0, bits.length);
        bits = newbits;
    }

    public boolean member(int el) {
        int n = wordNumber(el);
        if (n >= bits.length) return false;
        return (bits[n] & bitMask(el)) != 0;
    }

    /** Get the first element you find and return it.  Return Label.INVALID
     *  otherwise.
     */
    public int getSingleElement() {
        for (int i = 0; i < (bits.length << LOG_BITS); i++) {
            if (member(i)) {
                return i;
            }
        }
        return Label.INVALID;
    }

    public boolean isNil() {
        for (int i = bits.length - 1; i >= 0; i--) {
            if (bits[i] != 0) return false;
        }
        return true;
    }

    public IntSet complement() {
        BitSet s = (BitSet)this.clone();
        s.notInPlace();
        return s;
    }

    public IntSet complement(IntSet set) {
		if ( set==null ) {
			return this.complement();
		}
        return set.subtract(this);
    }

    public void notInPlace() {
        for (int i = bits.length - 1; i >= 0; i--) {
            bits[i] = ~bits[i];
        }
    }

    /** complement bits in the range 0..maxBit. */
    public void notInPlace(int maxBit) {
        notInPlace(0, maxBit);
    }

    /** complement bits in the range minBit..maxBit.*/
    public void notInPlace(int minBit, int maxBit) {
        // make sure that we have room for maxBit
        growToInclude(maxBit);
        for (int i = minBit; i <= maxBit; i++) {
            int n = wordNumber(i);
            bits[n] ^= bitMask(i);
        }
    }

    private final int numWordsToHold(int el) {
        return (el >> LOG_BITS) + 1;
    }

    public static BitSet of(int el) {
        BitSet s = new BitSet(el + 1);
        s.add(el);
        return s;
    }

    public static BitSet of(Collection elements) {
        BitSet s = new BitSet();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            Integer el = (Integer) iter.next();
            s.add(el.intValue());
        }
        return s;
    }

	public static BitSet of(IntSet set) {
		if ( set==null ) {
			return null;
		}

		if ( set instanceof BitSet ) {
			return (BitSet)set;
		}
		if ( set instanceof IntervalSet ) {
			BitSet s = new BitSet();
			s.addAll(set);
			return s;
		}
		throw new IllegalArgumentException("can't create BitSet from "+set.getClass().getName());
	}

    public static BitSet of(Map elements) {
        return BitSet.of(elements.keySet());
    }

	public static BitSet range(int a, int b) {
		BitSet s = new BitSet(b + 1);
		for (int i = a; i <= b; i++) {
			int n = wordNumber(i);
			s.bits[n] |= bitMask(i);
		}
		return s;
	}

    /** return this | a in a new set */
    public IntSet or(IntSet a) {
		if ( a==null ) {
			return this;
		}
        BitSet s = (BitSet)this.clone();
        s.orInPlace((BitSet)a);
        return s;
    }

    public void orInPlace(BitSet a) {
		if ( a==null ) {
			return;
		}
        // If this is smaller than a, grow this first
        if (a.bits.length > bits.length) {
            setSize(a.bits.length);
        }
        int min = Math.min(bits.length, a.bits.length);
        for (int i = min - 1; i >= 0; i--) {
            bits[i] |= a.bits[i];
        }
    }

    // remove this element from this set
    public void remove(int el) {
        int n = wordNumber(el);
        if (n >= bits.length) {
            growToInclude(el);
        }
        bits[n] &= ~bitMask(el);
    }

    /**
     * Sets the size of a set.
     * @param nwords how many words the new set should be
     */
    private void setSize(int nwords) {
        long newbits[] = new long[nwords];
        int n = Math.min(nwords, bits.length);
        System.arraycopy(bits, 0, newbits, 0, n);
        bits = newbits;
    }

    public int numBits() {
        return bits.length << LOG_BITS; // num words * bits per word
    }

    /** return how much space is being used by the bits array not
     *  how many actually have member bits on.
     */
    public int lengthInLongWords() {
        return bits.length;
    }

    /**Is this contained within a? */
    public boolean subset(BitSet a) {
        if (a == null) return false;
        return this.and(a).equals(this);
    }

    /**Subtract the elements of 'a' from 'this' in-place.
     * Basically, just turn off all bits of 'this' that are in 'a'.
     */
    public void subtractInPlace(BitSet a) {
        if (a == null) return;
        // for all words of 'a', turn off corresponding bits of 'this'
        for (int i = 0; i < bits.length && i < a.bits.length; i++) {
            bits[i] &= ~a.bits[i];
        }
    }

    public IntSet subtract(IntSet a) {
        if (a == null || !(a instanceof BitSet)) return null;

        BitSet s = (BitSet)this.clone();
        s.subtractInPlace((BitSet)a);
        return s;
    }

	public List toList() {
		throw new NoSuchMethodError("BitSet.toList() unimplemented");
	}

    public int[] toArray() {
        int[] elems = new int[size()];
        int en = 0;
        for (int i = 0; i < (bits.length << LOG_BITS); i++) {
            if (member(i)) {
                elems[en++] = i;
            }
        }
        return elems;
    }

    public long[] toPackedArray() {
        return bits;
    }

    public String toString() {
        return toString(null);
    }

    /** Transform a bit set into a string by formatting each element as an integer
     * separator The string to put in between elements
     * @return A commma-separated list of values
     */
    public String toString(Grammar g) {
        StringBuffer buf = new StringBuffer();
        String separator = ",";
		boolean havePrintedAnElement = false;
		buf.append('{');

        for (int i = 0; i < (bits.length << LOG_BITS); i++) {
            if (member(i)) {
                if (i > 0 && havePrintedAnElement ) {
                    buf.append(separator);
                }
                if ( g!=null ) {
                    buf.append(g.getTokenDisplayName(i));
                }
                else {
                    buf.append(i);
                }
				havePrintedAnElement = true;
            }
        }
		buf.append('}');
        return buf.toString();
    }

    /**Create a string representation where instead of integer elements, the
     * ith element of vocabulary is displayed instead.  Vocabulary is a Vector
     * of Strings.
     * separator The string to put in between elements
     * @return A commma-separated list of character constants.
     */
    public String toString(String separator, List vocabulary) {
        if (vocabulary == null) {
            return toString(null);
        }
        String str = "";
        for (int i = 0; i < (bits.length << LOG_BITS); i++) {
            if (member(i)) {
                if (str.length() > 0) {
                    str += separator;
                }
                if (i >= vocabulary.size()) {
                    str += "'" + (char)i + "'";
                }
                else if (vocabulary.get(i) == null) {
                    str += "'" + (char)i + "'";
                }
                else {
                    str += (String)vocabulary.get(i);
                }
            }
        }
        return str;
    }

    /**
     * Dump a comma-separated list of the words making up the bit set.
     * Split each 64 bit number into two more manageable 32 bit numbers.
     * This generates a comma-separated list of C++-like unsigned long constants.
     */
    public String toStringOfHalfWords() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < bits.length; i++) {
            if (i != 0) s.append(", ");
            long tmp = bits[i];
            tmp &= 0xFFFFFFFFL;
            s.append(tmp);
			s.append("UL");
            s.append(", ");
            tmp = bits[i] >>> 32;
            tmp &= 0xFFFFFFFFL;
			s.append(tmp);
			s.append("UL");
        }
		return s.toString();
    }

    /**
     * Dump a comma-separated list of the words making up the bit set.
     * This generates a comma-separated list of Java-like long int constants.
     */
    public String toStringOfWords() {
		StringBuffer s = new StringBuffer();
        for (int i = 0; i < bits.length; i++) {
            if (i != 0) s.append(", ");
            s.append(bits[i]);
			s.append("L");
        }
        return s.toString();
    }

    public String toStringWithRanges() {
        return toString();
    }

    private final static int wordNumber(int bit) {
        return bit >> LOG_BITS; // bit / BITS
    }
}
