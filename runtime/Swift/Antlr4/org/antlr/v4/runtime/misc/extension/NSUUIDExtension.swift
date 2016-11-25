//
//  NSUUIDExtension.swift
//  objc2swiftwithswith
//
//  Created by janyou on 15/9/8.
//  Copyright © 2015 jlabs. All rights reserved.
//

import Foundation


extension UUID {

    public init(mostSigBits: Int64, leastSigBits: Int64) {
        let uuid: String = ""
        self.init(uuidString: uuid)!
    }

    private func toUUID(_ mostSigBits: Int64, _ leastSigBits: Int64) -> String {

        return (digits(mostSigBits >> 32, 8) + "-" +
                digits(mostSigBits >> 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits >> 48, 4) + "-" +
                digits(leastSigBits, 12))
    }

    private func digits(_ val: Int64, _ digits: Int) -> String {
        let hi = Int64(1) << Int64(digits * 4)
        let intLiteral = hi | (val & (hi - 1))
        let s: String = String(Character(UnicodeScalar(UInt32(intLiteral))!))
        return s[1 ..< s.length]
        // return s.substringFromIndex(1)
    }


}
