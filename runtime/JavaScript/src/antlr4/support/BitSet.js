/**
 * In JavaScript land we can only reliably work with the first 32 bits, even though all numbers are
 * 64-bit floating point.
 *
 * @type {number} */
const BITS_PER_WORD = 32;

/**
 * @param {number} bit - The bit index.
 * @returns {number}
 */
function indexForBit(bit) {
  return Math.trunc(bit / BITS_PER_WORD)
}

/**
 * @param {number} bit - The bit index.
 * @returns {number}
 */
function maskForBit(bit) {
  return 1 << Math.trunc(bit % BITS_PER_WORD);
}

/**
 * Returns the number of set bits, a.k.a. popcount.
 *
 * @param {number} value - The value.
 * @returns {number}
 */
function onesCount(value) {
  // https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
  value -= ((value >>> 1) & 0x55555555);
  value = (value & 0x33333333) + ((value >>> 2) & 0x33333333);
  return ((value + (value >>> 4) & 0xf0f0f0f) * 0x1010101) >>> 24;
}

/** @type {Array<number>} */
const DEBRUIJN32 = [
  0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
  31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9,
];

/**
 * Returns the number of trailing zeros, a.k.a. CTZ.
 *
 * @param {number} value - The word.
 * @returns {number}
 */
function trailingZeros(value) {
  // https://graphics.stanford.edu/~seander/bithacks.html#ZerosOnRightMultLookup
  return DEBRUIJN32[((value & -value) * 0x077cb531) >>> 27];
}

/**
 * Determines the minimum array length needed to represent all significant bits.
 *
 * @param {Array<number>} values - The words.
 * @returns {number}
 */
function bestFit(values) {
  for (let idx = values.length; idx > 0; idx--) {
    if (values[idx - 1] !== 0) {
      return idx;
    }
  }
  return 0;
}

/** BitSet represents an array of bits. */
class BitSet {
  constructor() {
    /** @type {Array<number>} */
    this._words = [];
  }

  /**
   * Sets or clears the given bit based on value.
   *
   * @param {number} bit - The bit index to set.
   * @param {boolean} value - The state to set the bit to.
   * @returns {BitSet} This.
   */
  set(bit, value = true) {
    const idx = indexForBit(bit);
    if (idx >= this._words.length) {
      if (!value) {
        return this;
      }
      const oldLength = this._words.length;
      this._words.length = idx + 1;
      this._words.fill(0, oldLength);
    }
    if (value) {
      this._words[idx] |= maskForBit(bit);
    } else {
      this._words[idx] &= (~maskForBit(bit));
    }
    return this;
  }

  /**
   * @see set
   * @deprecated
   */
  add(bit) {
    return this.set(bit);
  }

  /**
   * Perform a bitwise OR of the two bitsets, assigning the result to this.
   *
   * @param {BitSet} set - The BitSet to OR.
   * @returns {BitSet} This.
   */
  or(set) {
    const thisLength = bestFit(this._words);
    const thatLength = bestFit(set._words);
    const maxLength = thisLength < thatLength ? thatLength : thisLength;
    if (maxLength > this._words.length) {
      const oldLength = this._words.length;
      this._words.length = maxLength;
      this._words.fill(0, oldLength);
    }
    for (let idx = 0; idx < thatLength; idx++) {
      this._words[idx] |= set._words[idx];
    }
    return this;
  }

  /**
   * Unsets the given bit.
   *
   * @param {number} bit - The bit index to clear.
   * @returns {BitSet} This.
   */
  clear(bit) {
    return this.set(bit, false);
  }

  /**
   * @see clear
   * @deprecated
   */
  remove(bit) {
    return this.clear(bit);
  }

  /**
   * Tests whether the given bit is set.
   *
   * @param {number} bit - The bit index to test.
   * @returns {boolean}
   */
  test(bit) {
    const idx = indexForBit(bit);
    if (idx >= this._words.length) {
      return false;
    }
    return (this._words[idx] & maskForBit(bit)) !== 0;
  }

  /**
   * @see test
   * @deprecated
   */
  contains(bit) {
    return this.test(bit);
  }

  /**
   * Find first set bit, a.k.a. FFS.
   *
   * @returns {number}
   */
  find() {
    for (let idx = 0; idx < this._words.length; idx++) {
      const value = this._words[idx];
      if (value !== 0) {
        return idx * BITS_PER_WORD + trailingZeros(value);
      }
    }
    return Number.MAX_SAFE_INTEGER;
  }

  /**
   * @see find
   * @deprecated
   */
  minValue() {
    return this.find();
  }

  hashCode() {
    const hash = new Hash();
    hash.update(this._words.slice(0, bestFit(this._words)));
    return hash.finish();
  }

  /**
   * Tests the given bitset for equality with this bitset.
   *
   * @param {BitSet} other - The BitSet to test for equivalence.
   * @returns {boolean}
   */
  equals(other) {
    if (!(other instanceof BitSet)) {
      return false;
    }
    if (this === other) {
      return true;
    }
    const thisLength = bestFit(this._words);
    const thatLength = bestFit(other._words);
    if (thisLength !== thatLength) {
      return false;
    }
    for (let idx = 0; idx < thisLength; idx++) {
      if (this._words[idx] !== other._words[idx]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return a string representation of the bitset.
   *
   * @returns {string}
   */
  toString() {
    const values = [];
    for (let idx = 0; idx < this._words.length; idx++) {
      let value = this._words[idx];
      while (value !== 0) {
        const count = trailingZeros(value);
        values.push(String(idx * BITS_PER_WORD + count));
        value &= ~(1 << count);
      }
    }
    return "{" + values.join(", ") + "}";
  }

  /**
   * Returns the number of set bits, a.k.a. popcount.
   *
   * @returns {number}
   */
  get length() {
    const minLength = bestFit(this._words);
    let count = 0;
    for (let idx = 0; idx < minLength; idx++) {
      count += onesCount(this._words[idx]);
    }
    return count;
  }
}

module.exports = {
  BitSet,
};
