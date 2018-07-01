/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

import Foundation


extension UUID {
    public init(mostSigBits: Int64, leastSigBits: Int64) {
        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        defer {
            bytes.deallocate()
        }
        bytes.withMemoryRebound(to: Int64.self, capacity: 2) {
            $0.pointee = leastSigBits
            $0.advanced(by: 1).pointee = mostSigBits
        }
        let u = NSUUID(uuidBytes: bytes)
        self.init(uuidString: u.uuidString)!
    }
}
