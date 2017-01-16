/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// Sometimes we need to map a key to a value but key is two pieces of data.
/// This nested hash table saves creating a single key each time we access
/// map; avoids mem creation.

public struct DoubleKeyMap<Key1:Hashable, Key2:Hashable, Value> {
    private var data: HashMap<Key1, HashMap<Key2, Value>> = HashMap<Key1, HashMap<Key2, Value>>()
    @discardableResult
    public mutating func put(_ k1: Key1, _ k2: Key2, _ v: Value) -> Value? {

        var data2 = data[k1]
        var prev: Value? = nil
        if data2 == nil {
            data2 = HashMap<Key2, Value>()

        } else {
            prev = data2![k2]
        }
        data2![k2] = v
        data[k1] = data2
        return prev
    }

    public  func get(_ k1: Key1, _ k2: Key2) -> Value? {

        if let data2 = data[k1] {
            return data2[k2]
        }
            return nil

    }

    public func get(_ k1: Key1) -> HashMap<Key2, Value>? {
        return data[k1]
    }

//    /** Get all values associated with primary key */
//    public func values(k1: Key1) -> LazyMapCollection<[Key2:Value], Value>? {
//        let data2: Dictionary<Key2, Value>? = data[k1]
//        if data2 == nil {
//            return nil
//        }
//        return data2!.values
//    }
//
//    /** get all primary keys */
//    public func keySet() -> LazyMapCollection<Dictionary<Key1, Dictionary<Key2, Value>>, Key1> {
//        return data.keys
//    }
//
//    /** get all secondary keys associated with a primary key */
//    public func keySet(k1: Key1) -> LazyMapCollection<[Key2:Value], Key2>? {
//        let data2: Dictionary<Key2, Value>? = data[k1]
//        if data2 == nil {
//            return nil
//        }
//        return data2!.keys
//    }
}
