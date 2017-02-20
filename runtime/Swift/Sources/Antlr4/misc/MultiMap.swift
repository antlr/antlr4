/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public class MultiMap<K:Hashable, V> {
    private var mapping = [K: Array < V>]()
    public func map(_ key: K, _ value: V) {
        var elementsForKey: Array<V>? = mapping[key]
        if elementsForKey == nil {
            elementsForKey = Array<V>()
            mapping[key] = elementsForKey
        }
        elementsForKey?.append(value)
    }

    public func getPairs() -> Array<(K, V)> {
        var pairs: Array<(K, V)> = Array<(K, V)>()
        for key: K in mapping.keys {
            for value: V in mapping[key]! {
                pairs.append((key, value))
            }
        }
        return pairs
    }

    public func get(_ key: K) -> Array<(V)>? {
        return mapping[key]
    }

    public func size() -> Int {
        return mapping.count
    }

}
