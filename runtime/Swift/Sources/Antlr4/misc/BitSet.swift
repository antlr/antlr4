/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

//
//  BitSet.swift
//  Antlr.swift
//
//  Created by janyou on 15/9/8.
//

import Foundation


/// 
/// This class implements a vector of bits that grows as needed. Each
/// component of the bit set has a `boolean` value. The
/// bits of a `BitSet` are indexed by nonnegative integers.
/// Individual indexed bits can be examined, set, or cleared. One
/// `BitSet` may be used to modify the contents of another
/// `BitSet` through logical AND, logical inclusive OR, and
/// logical exclusive OR operations.
/// 
/// By default, all bits in the set initially have the value
/// `false`.
/// 
/// Every bit set has a current size, which is the number of bits
/// of space currently in use by the bit set. Note that the size is
/// related to the implementation of a bit set, so it may change with
/// implementation. The length of a bit set relates to logical length
/// of a bit set and is defined independently of implementation.
/// 
/// A `BitSet` is not safe for multithreaded use without
/// external synchronization.
/// 
/// - note: Arthur van Hoff
/// - note: Michael McCloskey
/// - note: Martin Buchholz
/// - note: JDK1.0
/// 

public class BitSet: Hashable, CustomStringConvertible {
    /// 
    /// BitSets are packed into arrays of "words."  Currently a word is
    /// a long, which consists of 64 bits, requiring 6 address bits.
    /// The choice of word size is determined purely by performance concerns.
    /// 
    private static let ADDRESS_BITS_PER_WORD: Int = 6
    private static let BITS_PER_WORD: Int = 1 << ADDRESS_BITS_PER_WORD
    private static let BIT_INDEX_MASK: Int = BITS_PER_WORD - 1

    /// 
    /// Used to shift left or right for a partial word mask
    /// 
    private static let WORD_MASK: Int64 = Int64.max
    //0xfffffffffffffff//-1
    // 0xffffffffffffffffL;

    /// 
    /// -  bits long[]
    /// 
    /// The bits in this BitSet.  The ith bit is stored in bits[i/64] at
    /// bit position i % 64 (where bit position 0 refers to the least
    /// significant bit and 63 refers to the most significant bit).
    /// 


    /// 
    /// The internal field corresponding to the serialField "bits".
    /// 
    fileprivate var words: [Int64]

    /// 
    /// The number of words in the logical size of this BitSet.
    /// 
    fileprivate var wordsInUse: Int = 0
    //transient

    /// 
    /// Whether the size of "words" is user-specified.  If so, we assume
    /// the user knows what he's doing and try harder to preserve it.
    /// 
    private var sizeIsSticky: Bool = false
    //transient

    /// 
    /// use serialVersionUID from JDK 1.0.2 for interoperability
    /// 
    private let serialVersionUID: Int64 = 7997698588986878753
    //L;

    /// 
    /// Given a bit index, return word index containing it.
    /// 
    private static func wordIndex(_ bitIndex: Int) -> Int {
        return bitIndex >> ADDRESS_BITS_PER_WORD
    }

    /// 
    /// Every public method must preserve these invariants.
    /// 
    fileprivate func checkInvariants() {
        assert((wordsInUse == 0 || words[wordsInUse - 1] != 0), "Expected: (wordsInUse==0||words[wordsInUse-1]!=0)")
        assert((wordsInUse >= 0 && wordsInUse <= words.count), "Expected: (wordsInUse>=0&&wordsInUse<=words.length)")
        // print("\(wordsInUse),\(words.count),\(words[wordsInUse])")
        assert((wordsInUse == words.count || words[wordsInUse] == 0), "Expected: (wordsInUse==words.count||words[wordsInUse]==0)")
    }

    /// 
    /// Sets the field wordsInUse to the logical size in words of the bit set.
    /// WARNING:This method assumes that the number of words actually in use is
    /// less than or equal to the current value of wordsInUse!
    /// 
    private func recalculateWordsInUse() {
        // Traverse the bitset until a used word is found
        var i: Int = wordsInUse - 1
        while i >= 0 {
            if words[i] != 0 {
                break
            }
            i -= 1
        }

        wordsInUse = i + 1 // The new logical size
    }

    /// 
    /// Creates a new bit set. All bits are initially `false`.
    /// 
    public init() {
        sizeIsSticky = false
        words = [Int64](repeating: Int64(0), count: BitSet.wordIndex(BitSet.BITS_PER_WORD - 1) + 1)
        //initWords(BitSet.BITS_PER_WORD);

    }

    /// 
    /// Creates a bit set whose initial size is large enough to explicitly
    /// represent bits with indices in the range `0` through
    /// `nbits-1`. All bits are initially `false`.
    /// 
    /// - parameter  nbits: the initial size of the bit set
    /// - throws: _ANTLRError.negativeArraySize_ if the specified initial size
    /// is negative
    /// 
    public init(_ nbits: Int) throws {
        // nbits can't be negative; size 0 is OK

        // words = [BitSet.wordIndex(nbits-1) + 1];
        words = [Int64](repeating: Int64(0), count: BitSet.wordIndex(BitSet.BITS_PER_WORD - 1) + 1)
        sizeIsSticky = true
        if nbits < 0 {
            throw ANTLRError.negativeArraySize(msg: "nbits < 0:\(nbits) ")

        }
        // initWords(nbits);
    }

    private func initWords(_ nbits: Int) {
        // words =  [Int64](count: BitSet.wordIndex(BitSet.BITS_PER_WORD-1) + 1, repeatedValue: Int64(0));
        //  words = [BitSet.wordIndex(nbits-1) + 1];
    }

    /// 
    /// Creates a bit set using words as the internal representation.
    /// The last word (if there is one) must be non-zero.
    /// 
    private init(_ words: [Int64]) {
        self.words = words
        self.wordsInUse = words.count
        checkInvariants()
    }


    /// 
    /// Returns a new long array containing all the bits in this bit set.
    /// 
    /// More precisely, if
    /// `long[] longs = s.toLongArray();`
    /// then `longs.length == (s.length()+63)/64` and
    /// `s.get(n) == ((longs[n/64] & (1L<<(n%64))) != 0)`
    /// for all `n < 64 * longs.length`.
    /// 
    /// - returns: a long array containing a little-endian representation
    /// of all the bits in this bit set
    /// 
    public func toLongArray() -> [Int64] {
        return copyOf(words, wordsInUse)
    }

    private func copyOf(_ words: [Int64], _ newLength: Int) -> [Int64] {
        var newWords = [Int64](repeating: Int64(0), count: newLength)
        let length = min(words.count, newLength)
        newWords[0 ..< length] = words[0 ..< length]
        return newWords
    }
    /// 
    /// Ensures that the BitSet can hold enough words.
    /// - parameter wordsRequired: the minimum acceptable number of words.
    /// 
    private func ensureCapacity(_ wordsRequired: Int) {
        if words.count < wordsRequired {
            // Allocate larger of doubled size or required size
            let request: Int = max(2 * words.count, wordsRequired)
            words = copyOf(words, request)
            sizeIsSticky = false
        }
    }

    /// 
    /// Ensures that the BitSet can accommodate a given wordIndex,
    /// temporarily violating the invariants.  The caller must
    /// restore the invariants before returning to the user,
    /// possibly using recalculateWordsInUse().
    /// - parameter wordIndex: the index to be accommodated.
    /// 
    private func expandTo(_ wordIndex: Int) {
        let wordsRequired: Int = wordIndex + 1
        if wordsInUse < wordsRequired {
            ensureCapacity(wordsRequired)
            wordsInUse = wordsRequired
        }
    }

    /// 
    /// Checks that fromIndex ... toIndex is a valid range of bit indices.
    /// 
    private static func checkRange(_ fromIndex: Int, _ toIndex: Int) throws {
        if fromIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "fromIndex < 0: \(fromIndex)")

        }

        if toIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "toIndex < 0: \(toIndex)")

        }
        if fromIndex > toIndex {
            throw ANTLRError.indexOutOfBounds(msg: "fromInde: \(fromIndex) > toIndex: \(toIndex)")

        }
    }

    /// 
    /// Sets the bit at the specified index to the complement of its
    /// current value.
    /// 
    /// - parameter  bitIndex: the index of the bit to flip
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// 
    public func flip(_ bitIndex: Int) throws {
        if bitIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "bitIndex < 0: \(bitIndex)")


        }
        let index: Int = BitSet.wordIndex(bitIndex)
        expandTo(index)

        words[index] ^= (Int64(1) << Int64(bitIndex % 64))

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Sets each bit from the specified `fromIndex` (inclusive) to the
    /// specified `toIndex` (exclusive) to the complement of its current
    /// value.
    /// 
    /// - parameter  fromIndex: index of the first bit to flip
    /// - parameter  toIndex: index after the last bit to flip
    /// - throws: _ANTLRError.IndexOutOfBounds_ if `fromIndex` is negative,
    /// or `toIndex` is negative, or `fromIndex` is
    /// larger than `toIndex`
    /// 
    public func flip(_ fromIndex: Int, _ toIndex: Int) throws {
        try BitSet.checkRange(fromIndex, toIndex)

        if fromIndex == toIndex {
            return
        }

        let startWordIndex: Int = BitSet.wordIndex(fromIndex)
        let endWordIndex: Int = BitSet.wordIndex(toIndex - 1)
        expandTo(endWordIndex)

        let firstWordMask: Int64 = BitSet.WORD_MASK << Int64(fromIndex % 64)
        let lastWordMask: Int64 = BitSet.WORD_MASK >>> Int64(-toIndex)
        //var lastWordMask : Int64  = WORD_MASK >>> Int64(-toIndex);
        if startWordIndex == endWordIndex {
            // Case 1: One word
            words[startWordIndex] ^= (firstWordMask & lastWordMask)
        } else {
            // Case 2: Multiple words
            // Handle first word
            words[startWordIndex] ^= firstWordMask

            // Handle intermediate words, if any
            let start = startWordIndex + 1
            for i in start..<endWordIndex {
                words[i] ^= BitSet.WORD_MASK
            }

            // Handle last word
            words[endWordIndex] ^= lastWordMask
        }

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Sets the bit at the specified index to `true`.
    /// 
    /// - parameter  bitIndex: a bit index
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// 
    public func set(_ bitIndex: Int) throws {
        if bitIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "bitIndex < 0: \(bitIndex)")

        }
        let index: Int = BitSet.wordIndex(bitIndex)
        expandTo(index)

        // print(words.count)
        words[index] |= (Int64(1) << Int64(bitIndex % 64))  // Restores invariants

        checkInvariants()
    }

    /// 
    /// Sets the bit at the specified index to the specified value.
    /// 
    /// - parameter  bitIndex: a bit index
    /// - parameter  value: a boolean value to set
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// 
    public func set(_ bitIndex: Int, _ value: Bool) throws {
        if value {
            try set(bitIndex)
        } else {
            try clear(bitIndex)
        }
    }

    /// 
    /// Sets the bits from the specified `fromIndex` (inclusive) to the
    /// specified `toIndex` (exclusive) to `true`.
    /// 
    /// - parameter  fromIndex: index of the first bit to be set
    /// - parameter  toIndex: index after the last bit to be set
    /// - throws: _ANTLRError.IndexOutOfBounds_ if `fromIndex` is negative,
    /// or `toIndex` is negative, or `fromIndex` is
    /// larger than `toIndex`
    /// 
    public func set(_ fromIndex: Int, _ toIndex: Int) throws {
        try BitSet.checkRange(fromIndex, toIndex)

        if fromIndex == toIndex {
            return
        }

        // Increase capacity if necessary
        let startWordIndex: Int = BitSet.wordIndex(fromIndex)
        let endWordIndex: Int = BitSet.wordIndex(toIndex - 1)
        expandTo(endWordIndex)

        let firstWordMask: Int64 = BitSet.WORD_MASK << Int64(fromIndex % 64)
        let lastWordMask: Int64 = BitSet.WORD_MASK >>> Int64(-toIndex)
        //var lastWordMask : Int64  = WORD_MASK >>>Int64( -toIndex);
        if startWordIndex == endWordIndex {
            // Case 1: One word
            words[startWordIndex] |= (firstWordMask & lastWordMask)
        } else {
            // Case 2: Multiple words
            // Handle first word
            words[startWordIndex] |= firstWordMask

            // Handle intermediate words, if any
            let start = startWordIndex + 1
            for i in start..<endWordIndex {
                words[i] = BitSet.WORD_MASK
            }

            // Handle last word (restores invariants)
            words[endWordIndex] |= lastWordMask
        }

        checkInvariants()
    }

    /// 
    /// Sets the bits from the specified `fromIndex` (inclusive) to the
    /// specified `toIndex` (exclusive) to the specified value.
    /// 
    /// - parameter  fromIndex: index of the first bit to be set
    /// - parameter  toIndex: index after the last bit to be set
    /// - parameter  value: value to set the selected bits to
    /// - throws: _ANTLRError.IndexOutOfBounds_ if `fromIndex` is negative,
    /// or `toIndex` is negative, or `fromIndex` is
    /// larger than `toIndex`
    /// 
    public func set(_ fromIndex: Int, _ toIndex: Int, _ value: Bool) throws {
        if value {
            try set(fromIndex, toIndex)
        } else {
            try clear(fromIndex, toIndex)
        }
    }

    /// 
    /// Sets the bit specified by the index to `false`.
    /// 
    /// - parameter  bitIndex: the index of the bit to be cleared
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// -   JDK1.0
    /// 
    public func clear(_ bitIndex: Int) throws {
        if bitIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "bitIndex < 0: \(bitIndex)")
        }
        let index: Int = BitSet.wordIndex(bitIndex)
        if index >= wordsInUse {
            return
        }
        let option = Int64(1) << Int64(bitIndex % 64)
        words[index] &= ~option

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Sets the bits from the specified `fromIndex` (inclusive) to the
    /// specified `toIndex` (exclusive) to `false`.
    /// 
    /// - parameter  fromIndex: index of the first bit to be cleared
    /// - parameter  toIndex: index after the last bit to be cleared
    /// - throws: _ANTLRError.IndexOutOfBounds_ if `fromIndex` is negative,
    /// or `toIndex` is negative, or `fromIndex` is
    /// larger than `toIndex`
    /// 
    public func clear(_ fromIndex: Int,  _ toIndex: Int) throws {
        var toIndex = toIndex
        try BitSet.checkRange(fromIndex, toIndex)

        if fromIndex == toIndex {
            return
        }

        let startWordIndex: Int = BitSet.wordIndex(fromIndex)
        if startWordIndex >= wordsInUse {
            return
        }

        var endWordIndex: Int = BitSet.wordIndex(toIndex - 1)
        if endWordIndex >= wordsInUse {
            toIndex = length()
            endWordIndex = wordsInUse - 1
        }

        let firstWordMask: Int64 = BitSet.WORD_MASK << Int64(fromIndex % 64)
        // ar lastWordMask : Int64  = WORD_MASK >>> Int64((-toIndex);
        let lastWordMask: Int64 = BitSet.WORD_MASK >>> Int64(-toIndex)
        if startWordIndex == endWordIndex {
            // Case 1: One word
            words[startWordIndex] &= ~(firstWordMask & lastWordMask)
        } else {
            // Case 2: Multiple words
            // Handle first word
            words[startWordIndex] &= ~firstWordMask

            // Handle intermediate words, if any
            let start = startWordIndex + 1
            for i in start..<endWordIndex {
                words[i] = 0
            }

            // Handle last word
            words[endWordIndex] &= ~lastWordMask
        }

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Sets all of the bits in this BitSet to `false`.
    /// 
    public func clear() {
        while wordsInUse > 0 {
            wordsInUse -= 1
            words[wordsInUse] = 0
        }
    }

    /// 
    /// Returns the value of the bit with the specified index. The value
    /// is `true` if the bit with the index `bitIndex`
    /// is currently set in this `BitSet`; otherwise, the result
    /// is `false`.
    /// 
    /// - parameter  bitIndex:   the bit index
    /// - returns: the value of the bit with the specified index
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// 
    public func get(_ bitIndex: Int) throws -> Bool {
        if bitIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "bitIndex < 0: \(bitIndex)")

        }
        checkInvariants()

        let index: Int = BitSet.wordIndex(bitIndex)

        return (index < wordsInUse)
                && ((words[index] & ((Int64(1) << Int64(bitIndex % 64)))) != 0)
    }

    /// 
    /// Returns a new `BitSet` composed of bits from this `BitSet`
    /// from `fromIndex` (inclusive) to `toIndex` (exclusive).
    /// 
    /// - parameter  fromIndex: index of the first bit to include
    /// - parameter  toIndex: index after the last bit to include
    /// - returns: a new `BitSet` from a range of this `BitSet`
    /// - throws: _ANTLRError.IndexOutOfBounds_ if `fromIndex` is negative,
    /// or `toIndex` is negative, or `fromIndex` is
    /// larger than `toIndex`
    /// 
    public func get(_ fromIndex: Int, _ toIndex: Int) throws -> BitSet {
        var toIndex = toIndex
        try  BitSet.checkRange(fromIndex, toIndex)

        checkInvariants()

        let len: Int = length()

        // If no set bits in range return empty bitset
        if len <= fromIndex || fromIndex == toIndex {
            return try  BitSet(0)
        }

        // An optimization
        if toIndex > len {
            toIndex = len
        }

        let result: BitSet = try BitSet(toIndex - fromIndex)
        let targetWords: Int = BitSet.wordIndex(toIndex - fromIndex - 1) + 1
        var sourceIndex: Int = BitSet.wordIndex(fromIndex)
        let wordAligned: Bool = (fromIndex & BitSet.BIT_INDEX_MASK) == 0

        // Process all words but the last word
        var i: Int = 0;
        while i < targetWords - 1 {
            let wordOption1: Int64 = (words[sourceIndex] >>> Int64(fromIndex))
            let wordOption2: Int64 = (words[sourceIndex + 1] << Int64(-fromIndex % 64))
            let wordOption = wordOption1 | wordOption2
            result.words[i] = wordAligned ? words[sourceIndex] : wordOption

            i += 1
            sourceIndex += 1
        }
        // Process the last word
        // var lastWordMask : Int64 = WORD_MASK >>> Int64(-toIndex);
        let lastWordMask: Int64 = BitSet.WORD_MASK >>> Int64(-toIndex)
        let toIndexTest = ((toIndex - 1) & BitSet.BIT_INDEX_MASK)
        let fromIndexTest = (fromIndex & BitSet.BIT_INDEX_MASK)

        let wordOption1: Int64 = (words[sourceIndex] >>> Int64(fromIndex))
        let wordOption2: Int64 = (words[sourceIndex + 1] & lastWordMask)
        let wordOption3: Int64 = (64 + Int64(-fromIndex % 64))
        let wordOption = wordOption1 | wordOption2 << wordOption3

        let wordOption4 = (words[sourceIndex] & lastWordMask)
        let wordOption5 = wordOption4 >>> Int64(fromIndex)
        result.words[targetWords - 1] =
                toIndexTest < fromIndexTest
                ? wordOption : wordOption5

        // Set wordsInUse correctly
        result.wordsInUse = targetWords

        result.recalculateWordsInUse()
        result.checkInvariants()

        return result
    }

    ///
    /// Equivalent to nextSetBit(0), but guaranteed not to throw an exception.
    ///
    public func firstSetBit() -> Int {
        return try! nextSetBit(0)
    }

    ///
    /// Returns the index of the first bit that is set to `true`
    /// that occurs on or after the specified starting index. If no such
    /// bit exists then `-1` is returned.
    /// 
    /// To iterate over the `true` bits in a `BitSet`,
    /// use the following loop:
    /// 
    /// `
    /// for (int i = bs.firstSetBit(); i >= 0; i = bs.nextSetBit(i+1)) {
    /// // operate on index i here
    /// `}
    /// 
    /// - parameter  fromIndex: the index to start checking from (inclusive)
    /// - returns: the index of the next set bit, or `-1` if there
    /// is no such bit
    /// - throws: _ANTLRError.IndexOutOfBounds_ if the specified index is negative
    /// 
    public func nextSetBit(_ fromIndex: Int) throws -> Int {
        if fromIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "fromIndex < 0: \(fromIndex)")

        }
        checkInvariants()

        var u: Int = BitSet.wordIndex(fromIndex)
        if u >= wordsInUse {
            return -1
        }

        var word: Int64 = words[u] & (BitSet.WORD_MASK << Int64(fromIndex % 64))

        while true {
            if word != 0 {
                let bit = (u * BitSet.BITS_PER_WORD) + BitSet.numberOfTrailingZeros(word)
                return bit
            }
            u += 1
            if u == wordsInUse {
                return -1
            }
            word = words[u]
        }
    }

    public static func numberOfTrailingZeros(_ i: Int64) -> Int {
        // HD, Figure 5-14
        var x: Int32, y: Int32
        if i == 0 {
            return 64
        }
        var n: Int32 = 63
        y = Int32(truncatingIfNeeded: i)
        if y != 0 {
            n = n - 32
            x = y
        } else {
            x = Int32(truncatingIfNeeded: i >>> 32)
        }

        y = x << 16
        if y != 0 {
            n = n - 16
            x = y
        }
        y = x << 8
        if y != 0 {
            n = n - 8
            x = y
        }
        y = x << 4
        if y != 0 {
            n = n - 4
            x = y
        }
        y = x << 2
        if y != 0 {
            n = n - 2
            x = y
        }
        return Int(n - ((x << 1) >>> 31))
    }

    /// 
    /// Returns the index of the first bit that is set to `false`
    /// that occurs on or after the specified starting index.
    /// 
    /// - parameter  fromIndex: the index to start checking from (inclusive)
    /// - returns: the index of the next clear bit
    /// - throws: _ANTLRError.IndexOutOfBounds if the specified index is negative
    /// 
    public func nextClearBit(_ fromIndex: Int) throws -> Int {
        // Neither spec nor implementation handle bitsets of maximal length.
        // See 4816253.
        if fromIndex < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "fromIndex < 0: \(fromIndex)")

        }
        checkInvariants()

        var u: Int = BitSet.wordIndex(fromIndex)
        if u >= wordsInUse {
            return fromIndex
        }

        var word: Int64 = ~words[u] & (BitSet.WORD_MASK << Int64(fromIndex % 64))

        while true {
            if word != 0 {
                return (u * BitSet.BITS_PER_WORD) + BitSet.numberOfTrailingZeros(word)
            }
            u += 1
            if u == wordsInUse {
                return wordsInUse * BitSet.BITS_PER_WORD
            }

            word = ~words[u]
        }
    }

    /// 
    /// Returns the index of the nearest bit that is set to `true`
    /// that occurs on or before the specified starting index.
    /// If no such bit exists, or if `-1` is given as the
    /// starting index, then `-1` is returned.
    /// 
    /// To iterate over the `true` bits in a `BitSet`,
    /// use the following loop:
    /// 
    /// `
    /// for (int i = bs.length(); (i = bs.previousSetBit(i-1)) >= 0; ) {
    /// // operate on index i here
    /// `}
    /// 
    /// - parameter  fromIndex: the index to start checking from (inclusive)
    /// - returns: the index of the previous set bit, or `-1` if there
    /// is no such bit
    /// - throws: _ANTLRError.IndexOutOfBounds if the specified index is less
    /// than `-1`
    /// - note: 1.7
    /// 
    public func previousSetBit(_ fromIndex: Int) throws -> Int {
        if fromIndex < 0 {
            if fromIndex == -1 {
                return -1
            }
            throw ANTLRError.indexOutOfBounds(msg: "fromIndex < -1: \(fromIndex)")

        }

        checkInvariants()

        var u: Int = BitSet.wordIndex(fromIndex)
        if u >= wordsInUse {
            return length() - 1
        }

        var word: Int64 = words[u] & (BitSet.WORD_MASK >>> Int64(-(fromIndex + 1)))
        while true {
            if word != 0 {
                return (u + 1) * BitSet.BITS_PER_WORD - 1 - BitSet.numberOfLeadingZeros(word)
            }
            if u == 0 {
                return -1
            }
            u -= 1
            word = words[u]
        }
    }

    /// 
    /// Returns the index of the nearest bit that is set to `false`
    /// that occurs on or before the specified starting index.
    /// If no such bit exists, or if `-1` is given as the
    /// starting index, then `-1` is returned.
    /// 
    /// - parameter  fromIndex: the index to start checking from (inclusive)
    /// - returns: the index of the previous clear bit, or `-1` if there
    /// is no such bit
    /// - throws: _ANTLRError.IndexOutOfBounds if the specified index is less
    /// than `-1`
    /// - note: 1.7
    /// 
    public func previousClearBit(_ fromIndex: Int) throws -> Int {
        if fromIndex < 0 {
            if fromIndex == -1 {
                return -1
            }
            throw ANTLRError.indexOutOfBounds(msg: "fromIndex < -1: \(fromIndex)")

        }

        checkInvariants()

        var u: Int = BitSet.wordIndex(fromIndex)
        if u >= wordsInUse {
            return fromIndex
        }

        var word: Int64 = ~words[u] & (BitSet.WORD_MASK >>> Int64(-(fromIndex + 1)))
        // var word : Int64 = ~words[u] & (WORD_MASK >>> -(fromIndex+1));

        while true {
            if word != 0 {
                return (u + 1) * BitSet.BITS_PER_WORD - 1 - BitSet.numberOfLeadingZeros(word)
            }
            if u == 0 {
                return -1
            }
            u -= 1
            word = ~words[u]
        }
    }

    public static func numberOfLeadingZeros(_ i: Int64) -> Int {
        // HD, Figure 5-6
        if i == 0 {
            return 64
        }
        var n: Int32 = 1
        var x = Int32(i >>> 32)
        if x == 0 {
            n += 32
            x = Int32(i)
        }
        if x >>> 16 == 0 {
            n += 16
            x <<= 16
        }
        if x >>> 24 == 0 {
            n += 8
            x <<= 8
        }
        if x >>> 28 == 0 {
            n += 4
            x <<= 4
        }
        if x >>> 30 == 0 {
            n += 2
            x <<= 2
        }
        n -= x >>> 31

        return Int(n)
    }
    /// 
    /// Returns the "logical size" of this `BitSet`: the index of
    /// the highest set bit in the `BitSet` plus one. Returns zero
    /// if the `BitSet` contains no set bits.
    /// 
    /// - returns: the logical size of this `BitSet`
    /// 
    public func length() -> Int {
        if wordsInUse == 0 {
            return 0
        }

        return BitSet.BITS_PER_WORD * (wordsInUse - 1) +
                (BitSet.BITS_PER_WORD - BitSet.numberOfLeadingZeros(words[wordsInUse - 1]))
    }

    /// 
    /// Returns true if this `BitSet` contains no bits that are set
    /// to `true`.
    /// 
    /// - returns: boolean indicating whether this `BitSet` is empty
    /// 
    public func isEmpty() -> Bool {
        return wordsInUse == 0
    }

    /// 
    /// Returns true if the specified `BitSet` has any bits set to
    /// `true` that are also set to `true` in this `BitSet`.
    /// 
    /// - parameter  set: `BitSet` to intersect with
    /// - returns: boolean indicating whether this `BitSet` intersects
    /// the specified `BitSet`
    /// 
    public func intersects(_ set: BitSet) -> Bool {
        var i: Int = min(wordsInUse, set.wordsInUse) - 1
        while i >= 0 {
            if (words[i] & set.words[i]) != 0 {
                return true
            }
            i -= 1
        }
        return false
    }

    /// 
    /// Returns the number of bits set to `true` in this `BitSet`.
    /// 
    /// - returns: the number of bits set to `true` in this `BitSet`
    /// 
    public func cardinality() -> Int {
        var sum: Int = 0
        for i in 0..<wordsInUse {
            sum += BitSet.bitCount(words[i])
        }
        return sum
    }

    public static func bitCount(_ i: Int64) -> Int {
        var i = i
        // HD, Figure 5-14
        i = i - ((i >>> 1) & 0x5555555555555555)
        i = (i & 0x3333333333333333) + ((i >>> 2) & 0x3333333333333333)
        i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0f
        i = i + (i >>> 8)
        i = i + (i >>> 16)
        i = i + (i >>> 32)

        return Int(i) & 0x7f
    }

    /// 
    /// Performs a logical __AND__ of this target bit set with the
    /// argument bit set. This bit set is modified so that each bit in it
    /// has the value `true` if and only if it both initially
    /// had the value `true` and the corresponding bit in the
    /// bit set argument also had the value `true`.
    /// 
    /// - parameter set: a bit set
    /// 
    public func and(_ set: BitSet) {
        if self == set {
            return
        }

        while wordsInUse > set.wordsInUse {
            wordsInUse -= 1
            words[wordsInUse] = 0
        }

        // Perform logical AND on words in common
        for i in 0..<wordsInUse {
            words[i] &= set.words[i]
        }

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Performs a logical __OR__ of this bit set with the bit set
    /// argument. This bit set is modified so that a bit in it has the
    /// value `true` if and only if it either already had the
    /// value `true` or the corresponding bit in the bit set
    /// argument has the value `true`.
    /// 
    /// - parameter set: a bit set
    /// 
    public func or(_ set: BitSet) {
        if self == set {
            return
        }

        let wordsInCommon: Int = min(wordsInUse, set.wordsInUse)

        if wordsInUse < set.wordsInUse {
            ensureCapacity(set.wordsInUse)
            wordsInUse = set.wordsInUse
        }

        // Perform logical OR on words in common
        for i in 0..<wordsInCommon {
            words[i] |= set.words[i]
        }

        // Copy any remaining words
        if wordsInCommon < set.wordsInUse {
            words[wordsInCommon ..< wordsInUse] = set.words[wordsInCommon ..< wordsInUse]

        }

        // recalculateWordsInUse() is unnecessary
        checkInvariants()
    }

    /// 
    /// Performs a logical __XOR__ of this bit set with the bit set
    /// argument. This bit set is modified so that a bit in it has the
    /// value `true` if and only if one of the following
    /// statements holds:
    /// 
    /// * The bit initially has the value `true`, and the
    /// corresponding bit in the argument has the value `false`.
    /// * The bit initially has the value `false`, and the
    /// corresponding bit in the argument has the value `true`.
    /// 
    /// - parameter  set: a bit set
    /// 
    public func xor(_ set: BitSet) {
        let wordsInCommon: Int = min(wordsInUse, set.wordsInUse)

        if wordsInUse < set.wordsInUse {
            ensureCapacity(set.wordsInUse)
            wordsInUse = set.wordsInUse
        }

        // Perform logical XOR on words in common
        for i in 0..<wordsInCommon {
            words[i] ^= set.words[i]
        }

        // Copy any remaining words
        if wordsInCommon < set.wordsInUse {
            words[wordsInCommon ..< wordsInUse] = set.words[wordsInCommon ..< wordsInUse]


        }

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Clears all of the bits in this `BitSet` whose corresponding
    /// bit is set in the specified `BitSet`.
    /// 
    /// - parameter  set: the `BitSet` with which to mask this
    /// `BitSet`
    /// 
    public func andNot(_ set: BitSet) {
        // Perform logical (a & !b) on words in common
        var i: Int = min(wordsInUse, set.wordsInUse) - 1
        while i >= 0 {
            words[i] &= ~set.words[i]
            i -= 1
        }

        recalculateWordsInUse()
        checkInvariants()
    }

    /// 
    /// Returns the hash code value for this bit set. The hash code depends
    /// only on which bits are set within this `BitSet`.
    /// 
    /// The hash code is defined to be the result of the following
    /// calculation:
    /// `
    /// public int hashCode() {
    /// long h = 1234;
    /// long[] words = toLongArray();
    /// for (int i = words.length; --i >= 0; )
    /// h ^= words[i] * (i + 1);
    /// return (int)((h >> 32) ^ h);
    /// `}
    /// Note that the hash code changes if the set of bits is altered.
    /// 
    /// - returns: the hash code value for this bit set
    /// 
    public var hashValue: Int {
        var h: Int64 = 1234
        var i: Int = wordsInUse
        i -= 1
        while i >= 0 {
             h ^= words[i] * Int64(i + 1)
             i -= 1
        }

        return Int(Int32((h >> 32) ^ h))
    }

    /// 
    /// Returns the number of bits of space actually in use by this
    /// `BitSet` to represent bit values.
    /// The maximum element in the set is the size - 1st element.
    /// 
    /// - returns: the number of bits currently in this bit set
    /// 
    public func size() -> Int {
        return words.count * BitSet.BITS_PER_WORD
    }





    /// 
    /// Attempts to reduce internal storage used for the bits in this bit set.
    /// Calling this method may, but is not required to, affect the value
    /// returned by a subsequent call to the _#size()_ method.
    /// 
    private func trimToSize() {
        if wordsInUse != words.count {
            words = copyOf(words, wordsInUse)
            checkInvariants()
        }
    }


    /// 
    /// Returns a string representation of this bit set. For every index
    /// for which this `BitSet` contains a bit in the set
    /// state, the decimal representation of that index is included in
    /// the result. Such indices are listed in order from lowest to
    /// highest, separated by ",&nbsp;" (a comma and a space) and
    /// surrounded by braces, resulting in the usual mathematical
    /// notation for a set of integers.
    /// 
    /// Example:
    /// 
    /// BitSet drPepper = new BitSet();
    /// Now `drPepper.toString()` returns "`{`}".
    /// 
    /// drPepper.set(2);
    /// Now `drPepper.toString()` returns "`{2`}".
    /// 
    /// drPepper.set(4);
    /// drPepper.set(10);
    /// Now `drPepper.toString()` returns "`{2, 4, 10`}".
    /// 
    /// - returns: a string representation of this bit set
    /// 
    public var description: String {
        checkInvariants()

        //let numBits: Int = (wordsInUse > 128) ?
        // cardinality() : wordsInUse * BitSet.BITS_PER_WORD
        let b = StringBuilder()
        b.append("{")
        var i = firstSetBit()
        if i != -1 {
            b.append(i)
            i = try! nextSetBit(i + 1)
            while i >= 0 {
                let endOfRun = try! nextClearBit(i)
                repeat {
                    b.append(", ").append(i)
                    i += 1
                } while i < endOfRun
                i = try! nextSetBit(i + 1)
            }
        }
        b.append("}")
        return b.toString()

    }
    public func toString() -> String {
        return description
    }

}

public func ==(lhs: BitSet, rhs: BitSet) -> Bool {

    if lhs === rhs {
        return true
    }


    lhs.checkInvariants()
    rhs.checkInvariants()

    if lhs.wordsInUse != rhs.wordsInUse {
        return false
    }

    // Check words in use by both BitSets
    let length = lhs.wordsInUse
    for i in 0..<length {
        if lhs.words[i] != rhs.words[i] {
            return false
        }
    }

    return true

}
