/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Sometimes we need to map a key to a value but key is two pieces of data.
/// This nested hash table saves creating a single key each time we access
/// map; avoids mem creation.
///
public struct DoubleKeyMap<Key1: Hashable, Key2: Hashable, Value> {
    private var data = [Key1: [Key2: Value]]()

    @discardableResult
    public mutating func put(_ k1: Key1, _ k2: Key2, _ v: Value) -> Value? {

        let prev: Value?
        if var data2 = data[k1] {
            prev = data2[k2]
            data2[k2] = v
            data[k1] = data2
        }
        else {
            prev = nil
            let data2 = [
                k2: v
            ]
            data[k1] = data2
        }
        return prev
    }

    public func get(_ k1: Key1, _ k2: Key2) -> Value? {
        if let data2 = data[k1] {
            return data2[k2]
        }
        return nil
    }

    public func get(_ k1: Key1) -> [Key2: Value]? {
        return data[k1]
    }
}
