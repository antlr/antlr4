/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 *
 * @author Sam Harwell
 */

public final class MurmurHash {

    private static let DEFAULT_SEED: Int = 0

    /**
     * Initialize the hash using the default seed value.
     *
     * @return the intermediate hash value
     */
    public static func initialize() -> Int {
        return initialize(DEFAULT_SEED)
    }

    /**
     * Initialize the hash using the specified {@code seed}.
     *
     * @param seed the seed
     * @return the intermediate hash value
     */
    public static func initialize(_ seed: Int) -> Int {
        return seed
    }

    /**
     * Update the intermediate hash value for the next input {@code value}.
     *
     * @param hash the intermediate hash value
     * @param value the value to add to the current hash
     * @return the updated intermediate hash value
     */
    public static func update2(_ hashIn: Int, _ value: Int) -> Int {

        let c1: Int32 = -862048943//0xCC9E2D51;
        let c2: Int32 = 0x1B873593
        let r1: Int32 = 15
        let r2: Int32 = 13
        let m: Int32 = 5
        let n: Int32 = -430675100//0xE6546B64;

        var k: Int32 = Int32(truncatingBitPattern: value)
        k = Int32.multiplyWithOverflow(k, c1).0
        // (k,_) = UInt32.multiplyWithOverflow(k, c1)     ;//( k * c1);
        //TODO: CHECKE >>>
        k = (k << r1) | (k >>> (Int32(32) - r1))  //k = (k << r1) | (k >>> (32 - r1));
        //k =  UInt32 (truncatingBitPattern:Int64(Int64(k) * Int64(c2)));//( k * c2);
        //(k,_) = UInt32.multiplyWithOverflow(k, c2)
        k = Int32.multiplyWithOverflow(k, c2).0
        var hash = Int32(hashIn)
        hash = hash ^ k
        hash = (hash << r2) | (hash >>> (Int32(32) - r2))//hash = (hash << r2) | (hash >>> (32 - r2));
        (hash, _) = Int32.multiplyWithOverflow(hash, m)
        (hash, _) = Int32.addWithOverflow(hash, n)
        //hash = hash * m + n;
        // print("murmur update2 : \(hash)")
        return Int(hash)
    }

    /**
     * Update the intermediate hash value for the next input {@code value}.
     *
     * @param hash the intermediate hash value
     * @param value the value to add to the current hash
     * @return the updated intermediate hash value
     */
    public static func update<T:Hashable>(_ hash: Int, _ value: T?) -> Int {
        return update2(hash, value != nil ? value!.hashValue : 0)
        // return update2(hash, value);
    }

    /**
     * Apply the final computation steps to the intermediate value {@code hash}
     * to form the final result of the MurmurHash 3 hash function.
     *
     * @param hash the intermediate hash value
     * @param numberOfWords the number of integer values added to the hash
     * @return the final hash result
     */
    public static func finish(_ hashin: Int, _ numberOfWordsIn: Int) -> Int {
        var hash = Int32(hashin)
        let numberOfWords = Int32(numberOfWordsIn)
        hash = hash ^ Int32.multiplyWithOverflow(numberOfWords, Int32(4)).0  //(numberOfWords * UInt32(4));
        hash = hash ^ (hash >>> Int32(16))   //hash = hash ^ (hash >>> 16);
        (hash, _) = Int32.multiplyWithOverflow(hash, Int32(-2048144789))//hash * UInt32(0x85EBCA6B);
        hash = hash ^ (hash >>> Int32(13))//hash = hash ^ (hash >>> 13);
        //hash = UInt32(truncatingBitPattern: UInt64(hash) * UInt64(0xC2B2AE35)) ;
        (hash, _) = Int32.multiplyWithOverflow(hash, Int32(-1028477387))
        hash = hash ^ (hash >>> Int32(16))//	hash = hash ^ (hash >>> 16);
        //print("murmur finish : \(hash)")
        return Int(hash)
    }

    /**
     * Utility function to compute the hash code of an array using the
     * MurmurHash algorithm.
     *
     * @param <T> the array element type
     * @param data the array data
     * @param seed the seed for the MurmurHash algorithm
     * @return the hash code of the data
     */
    public static func hashCode<T:Hashable>(_ data: [T], _ seed: Int) -> Int {
        var hash: Int = initialize(seed)
        for value: T in data {
            //var hashValue = value != nil ?  value.hashValue : 0
            hash = update(hash, value.hashValue)
        }

        hash = finish(hash, data.count)
        return hash
    }

    private init() {
    }
}
