package org.antlr.v4.runtime.misc;

import org.antlr.runtime.Token;

/** */
public class LABitSet implements Cloneable {
	public final static int BITS = 64;    // number of bits / long
	public final static int LOG_BITS = 6; // 2^6 == 64

	/* We will often need to do a mod operator (i mod nbits).  Its
	 * turns out that, for powers of two, this mod operation is
	 * same as (i & (nbits-1)).  Since mod is slow, we use a
	 * precomputed mod mask to do the mod instead.
	 */
	public final static int MOD_MASK = BITS - 1;

	public static final LABitSet EOF_SET = LABitSet.of(Token.EOF);

	/** The actual data bits */
	public long bits[];

	public boolean EOF; // is EOF in set (-1)?

	/** Construct a bitset of size one word (64 bits) */
	public LABitSet() {
		this(BITS);
	}

	/** Construct a bitset given the size
	 * @param nbits The size of the bitset in bits
	 */
	public LABitSet(int nbits) {
		bits = new long[((nbits - 1) >> LOG_BITS) + 1];
	}

	/** Construction from a static array of longs */
	public LABitSet(long[] bits_) {
		if ( bits_==null || bits_.length==0 ) bits = new long[1];
		else bits = bits_;
	}

	/** Construction from a static array of longs */
	public LABitSet(long[] bits_, boolean EOF) {
		this(bits_);
		this.EOF = EOF;
	}

	public static LABitSet of(int el) {
		LABitSet s = new LABitSet(el + 1);
		s.add(el);
		return s;
	}
	
	/** or this element into this set (grow as necessary to accommodate) */
	public void add(int el) {
		//System.out.println("add("+el+")");
		if ( el==Token.EOF ) { EOF = true; return; }
		int n = wordNumber(el);
		//System.out.println("word number is "+n);
		//System.out.println("bits.length "+bits.length);
		if (n >= bits.length) {
			growToInclude(el);
		}
		bits[n] |= bitMask(el);
	}
	
	public boolean member(int el) {
		if ( el == Token.EOF ) return EOF;
		int n = wordNumber(el);
		if (n >= bits.length) return false;
		return (bits[n] & bitMask(el)) != 0;
	}

	/** return this | a in a new set */
	public LABitSet or(LABitSet a) {
		if ( a==null ) {
			return this;
		}
		LABitSet s = (LABitSet)this.clone();
		s.orInPlace((LABitSet)a);
		return s;
	}

	public void orInPlace(LABitSet a) {
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
		EOF = EOF | a.EOF;
	}

	// remove this element from this set
	public void remove(int el) {
		if ( el==Token.EOF ) { EOF = false; return; }
		int n = wordNumber(el);
		if (n >= bits.length) {
			throw new IllegalArgumentException(el+" is outside set range of "+bits.length+" words");
		}
		bits[n] &= ~bitMask(el);
	}

	public Object clone() {
		LABitSet s;
		try {
			s = (LABitSet)super.clone();
			s.bits = new long[bits.length];
			System.arraycopy(bits, 0, s.bits, 0, bits.length);
			s.EOF = EOF;
			return s;
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	/**
	 * Sets the size of a set.
	 * @param nwords how many words the new set should be
	 */
	void setSize(int nwords) {
		long newbits[] = new long[nwords];
		int n = Math.min(nwords, bits.length);
		System.arraycopy(bits, 0, newbits, 0, n);
		bits = newbits;
	}

	/** Get the first element you find and return it. */
	public int getSingleElement() {
		for (int i = 0; i < (bits.length << LOG_BITS); i++) {
			if (member(i)) {
				return i;
			}
		}
		return Token.INVALID_TOKEN_TYPE;
	}	

	/** Transform a bit set into a string by formatting each element as an integer
	 * separator The string to put in between elements
	 * @return A commma-separated list of values
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String separator = ",";
		boolean havePrintedAnElement = false;
		buf.append('{');
		if ( EOF ) { buf.append("EOF"); havePrintedAnElement=true; }

		for (int i = 0; i < (bits.length << LOG_BITS); i++) {
			if (member(i)) {
				if ( havePrintedAnElement ) {
					buf.append(separator);
				}
				buf.append(i);
				havePrintedAnElement = true;
			}
		}
		buf.append('}');
		return buf.toString();
	}

//	/**Create a string representation where instead of integer elements, the
//	 * ith element of vocabulary is displayed instead.  Vocabulary is a Vector
//	 * of Strings.
//	 * separator The string to put in between elements
//	 * @return A commma-separated list of character constants.
//	 */
//	public String toString(String separator, List vocabulary) {
//		String str = "";
//		for (int i = 0; i < (bits.length << LOG_BITS); i++) {
//			if (member(i)) {
//				if (str.length() > 0) {
//					str += separator;
//				}
//				if (i >= vocabulary.size()) {
//					str += "'" + (char)i + "'";
//				}
//				else if (vocabulary.get(i) == null) {
//					str += "'" + (char)i + "'";
//				}
//				else {
//					str += (String)vocabulary.get(i);
//				}
//			}
//		}
//		return str;
//	}

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

	static long bitMask(int bitNumber) {
		int bitPosition = bitNumber & MOD_MASK; // bitNumber mod BITS
		return 1L << bitPosition;
	}

	static int numWordsToHold(int el) {
		return (el >> LOG_BITS) + 1;
	}

	static int wordNumber(int bit) {
		return bit >> LOG_BITS; // bit / BITS
	}
}
