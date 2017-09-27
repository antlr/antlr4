/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/// 
/// https://en.wikipedia.org/wiki/MurmurHash
/// 
/// - Author: Sam Harwell
/// 

public final class MurmurHash {

    private static let DEFAULT_SEED: UInt32 = 0

    private static let c1 = UInt32(0xCC9E2D51)
    private static let c2 = UInt32(0x1B873593)
    private static let r1 = UInt32(15)
    private static let r2 = UInt32(13)
    private static let m = UInt32(5)
    private static let n = UInt32(0xE6546B64)

    /// 
    /// Initialize the hash using the default seed value.
    /// 
    /// - Returns: the intermediate hash value
    /// 
    public static func initialize() -> UInt32 {
        return initialize(DEFAULT_SEED)
    }

    /// 
    /// Initialize the hash using the specified `seed`.
    /// 
    /// - Parameter seed: the seed
    /// - Returns: the intermediate hash value
    /// 
    public static func initialize(_ seed: UInt32) -> UInt32 {
        return seed
    }

    private static func calcK(_ value: UInt32) -> UInt32 {
        var k = value
        k = k &* c1
        k = (k << r1) | (k >> (32 - r1))
        k = k &* c2
        return k
     }

    /// 
    /// Update the intermediate hash value for the next input `value`.
    /// 
    /// - Parameter hash: the intermediate hash value
    /// - Parameter value: the value to add to the current hash
    /// - Returns: the updated intermediate hash value
    /// 
    public static func update2(_ hashIn: UInt32, _ value: Int) -> UInt32 {
        let k = calcK(UInt32(truncatingIfNeeded: value))
        var hash = hashIn
        hash = hash ^ k
        hash = (hash << r2) | (hash >> (32 - r2))
        hash = hash &* m &+ n
        // print("murmur update2 : \(hash)")
        return hash
    }

    /// 
    /// Update the intermediate hash value for the next input `value`.
    /// 
    /// - Parameter hash: the intermediate hash value
    /// - Parameter value: the value to add to the current hash
    /// - Returns: the updated intermediate hash value
    /// 
    public static func update<T:Hashable>(_ hash: UInt32, _ value: T?) -> UInt32 {
        return update2(hash, value != nil ? value!.hashValue : 0)
    }

    /// 
    /// Apply the final computation steps to the intermediate value `hash`
    /// to form the final result of the MurmurHash 3 hash function.
    /// 
    /// - Parameter hash: the intermediate hash value
    /// - Parameter numberOfWords: the number of UInt32 values added to the hash
    /// - Returns: the final hash result
    /// 
    public static func finish(_ hashin: UInt32, _ numberOfWords: Int) -> Int {
        return Int(finish(hashin, byteCount: (numberOfWords &* 4)))
    }

    private static func finish(_ hashin: UInt32, byteCount byteCountInt: Int) -> UInt32 {
        let byteCount = UInt32(truncatingIfNeeded: byteCountInt)
        var hash = hashin
        hash ^= byteCount
        hash ^= (hash >> 16)
        hash = hash &* 0x85EBCA6B
        hash ^= (hash >> 13)
        hash = hash &* 0xC2B2AE35
        hash ^= (hash >> 16)
        //print("murmur finish : \(hash)")
        return hash
    }

    /// 
    /// Utility function to compute the hash code of an array using the
    /// MurmurHash algorithm.
    /// 
    /// - Parameter <T>: the array element type
    /// - Parameter data: the array data
    /// - Parameter seed: the seed for the MurmurHash algorithm
    /// - Returns: the hash code of the data
    /// 
    public static func hashCode<T:Hashable>(_ data: [T], _ seed: Int) -> Int {
        var hash = initialize(UInt32(truncatingIfNeeded: seed))
        for value in data {
            hash = update(hash, value)
        }

        return finish(hash, data.count)
    }

    ///
    /// Compute a hash for the given String and seed.  The String is encoded
    /// using UTF-8, then the bytes are interpreted as unsigned 32-bit
    /// little-endian values, giving UInt32 values for the update call.
    ///
    /// If the bytes do not evenly divide by 4, the final bytes are treated
    /// slightly differently (not doing the final rotate / multiply / add).
    ///
    /// This matches the treatment of byte sequences in publicly available
    /// test patterns (see MurmurHashTests.swift) and the example code on
    /// Wikipedia.
    ///
    public static func hashString(_ s: String, _ seed: UInt32) -> UInt32 {
        let bytes = Array(s.utf8)
        return hashBytesLittleEndian(bytes, seed)
    }

    private static func hashBytesLittleEndian(_ bytes: [UInt8], _ seed: UInt32) -> UInt32 {
        let byteCount = bytes.count

        var hash = seed
        for i in stride(from: 0, to: byteCount - 3, by: 4) {
            var word = UInt32(bytes[i])
            word |= UInt32(bytes[i + 1]) << 8
            word |= UInt32(bytes[i + 2]) << 16
            word |= UInt32(bytes[i + 3]) << 24

            hash = update(hash, word)
        }
        let remaining = byteCount & 3
        if remaining != 0 {
            var lastWord = UInt32(0)
            for r in 0 ..< remaining {
                lastWord |= UInt32(bytes[byteCount - 1 - r]) << (8 * (remaining - 1 - r))
            }

            let k = calcK(lastWord)
            hash ^= k
        }

        return finish(hash, byteCount: byteCount)
    }

    private init() {
    }
}
