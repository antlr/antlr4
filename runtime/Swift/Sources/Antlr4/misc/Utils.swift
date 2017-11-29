///
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


import Foundation

public class Utils {

    public static func escapeWhitespace(_ s: String, _ escapeSpaces: Bool) -> String {
        var buf = ""
        for c in s {
            if c == " " && escapeSpaces {
                buf += "\u{00B7}"
            }
            else if c == "\t" {
                    buf += "\\t"
            }
            else if c == "\n" {
                buf += "\\n"
            }
            else if c == "\r" {
                buf += "\\r"
            }
            else {
                buf.append(c)
            }
        }
        return buf
    }


    public static func toMap(_ keys: [String]) -> [String: Int] {
        var m = [String: Int]()
        for (index, v) in keys.enumerated() {
            m[v] = index
        }
        return m
    }

    public static func bitLeftShift(_ n: Int) -> Int64 {
       return (Int64(1) << Int64(n % 64))
    }


    public static func testBitLeftShiftArray(_ nArray: [Int],_ bitsShift: Int) -> Bool {
        let test: Bool = (((nArray[0] - bitsShift) & ~0x3f) == 0)

        var temp: Int64 =  Int64(nArray[0] - bitsShift)
        temp = (temp < 0) ? (64 + (temp % 64 )) : (temp % 64)
        let test1: Int64 = (Int64(1) << temp)

        var test2: Int64 = Utils.bitLeftShift(nArray[1] - bitsShift)

        for i in 1 ..< nArray.count {
            test2 = test2 |  Utils.bitLeftShift(nArray[i] - bitsShift)
        }
        return test && (( test1 & test2 ) != 0)
    }
}
