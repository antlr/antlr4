/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/** Sometimes we need to map a key to a value but key is two pieces of data.
 *  This nested hash table saves creating a single key each time we access
 *  map; avoids mem creation.
 */

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
