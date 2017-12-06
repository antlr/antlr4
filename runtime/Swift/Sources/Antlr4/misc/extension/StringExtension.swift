/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

import Foundation

extension String {
    func lastIndex(of target: String) -> String.Index? {
        if target.isEmpty {
            return nil
        }
        var result: String.Index? = nil
        var substring = self[...]
        while true {
            guard let targetRange = substring.range(of: target) else {
                return result
            }
            result = targetRange.lowerBound
            let nextChar = substring.index(after: targetRange.lowerBound)
            substring = self[nextChar...]
        }
    }

    subscript(integerRange: Range<Int>) -> String {
        let start = index(startIndex, offsetBy: integerRange.lowerBound)
        let end = index(startIndex, offsetBy: integerRange.upperBound)
        let range = start ..< end
        return String(self[range])
    }
}


// Implement Substring.hasPrefix, which is not currently in the Linux stdlib.
// https://bugs.swift.org/browse/SR-5627
#if os(Linux)
extension Substring {
    func hasPrefix(_ prefix: String) -> Bool {
        return String(self).hasPrefix(prefix)
    }
}
#endif
