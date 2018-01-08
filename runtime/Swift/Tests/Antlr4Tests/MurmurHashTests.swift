/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

/// The test patterns below are by Ian Boyd and have been released into the
/// public domain.
/// https://stackoverflow.com/questions/14747343/murmurhash3-test-vectors

import XCTest
import Antlr4

class MurmurHashTests: XCTestCase {

    func testMurmurHash() {
        doMurmurHashTest("", 0, 0) //empty string with zero seed should give zero
        doMurmurHashTest("", 1, 0x514E28B7)
        doMurmurHashTest("", 0xffffffff, 0x81F16F39) //make sure seed value is handled unsigned
        doMurmurHashTest("\0\0\0\0", 0, 0x2362F9DE) //make sure we handle embedded nulls

        doMurmurHashTest("aaaa", 0x9747b28c, 0x5A97808A) //one full chunk
        doMurmurHashTest("aaa", 0x9747b28c, 0x283E0130) //three characters
        doMurmurHashTest("aa", 0x9747b28c, 0x5D211726) //two characters
        doMurmurHashTest("a", 0x9747b28c, 0x7FA09EA6) //one character

        //Endian order within the chunks
        doMurmurHashTest("abcd", 0x9747b28c, 0xF0478627) //one full chunk
        doMurmurHashTest("abc", 0x9747b28c, 0xC84A62DD)
        doMurmurHashTest("ab", 0x9747b28c, 0x74875592)
        doMurmurHashTest("a", 0x9747b28c, 0x7FA09EA6)

        doMurmurHashTest("Hello, world!", 0x9747b28c, 0x24884CBA)

        //Make sure you handle UTF-8 high characters. A bcrypt implementation messed this up
        doMurmurHashTest("ππππππππ", 0x9747b28c, 0xD58063C1) //U+03C0: Greek Small Letter Pi

        //String of 256 characters.
        doMurmurHashTest(String(repeating: "a", count: 256), 0x9747b28c, 0x37405BDC)

        doMurmurHashTest("abc", 0, 0xB3DD93FA)
        doMurmurHashTest("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq", 0, 0xEE925B90)
        doMurmurHashTest("The quick brown fox jumps over the lazy dog", 0x9747b28c, 0x2FA826CD)
    }
}

private func doMurmurHashTest(_ input: String, _ seed: UInt32, _ expected: UInt32) {
    XCTAssertEqual(MurmurHash.hashString(input, seed), expected)
}
