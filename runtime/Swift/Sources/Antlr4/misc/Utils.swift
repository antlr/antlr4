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
}
