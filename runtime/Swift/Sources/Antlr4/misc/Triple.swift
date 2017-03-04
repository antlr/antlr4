/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class Triple<A:Hashable, B:Hashable, C:Hashable>: Hashable, CustomStringConvertible {
    public let a: A
    public let b: B
    public let c: C

    public init(_ a: A, _ b: B, _ c: C) {
        self.a = a
        self.b = b
        self.c = c
    }
    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize()
        hash = MurmurHash.update(hash, a)
        hash = MurmurHash.update(hash, b)
        hash = MurmurHash.update(hash, c)
        return MurmurHash.finish(hash, 3)
    }


    public var description: String {
        return "\(a),\(b),(c)"
    }
}

public func ==<A, B, C>(lhs: Triple<A, B, C>, rhs: Triple<A, B, C>) -> Bool {
    if lhs === rhs {
        return true
    }
    // return false
    return lhs.a == rhs.a && lhs.b == rhs.b && lhs.c == rhs.c
}
