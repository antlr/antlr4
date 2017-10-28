/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

final class Entry<K: Hashable,V>: CustomStringConvertible {
    final var key: K
    final var value: V
    final var next: Entry<K,V>!
    final var hash: Int

    /// 
    /// Creates new entry.
    /// 
    init(_ h: Int, _ k: K, _ v: V, _ n: Entry<K,V>!) {
        value = v
        next = n
        key = k
        hash = h
    }

    final func getKey() -> K {
        return key
    }

    final func getValue() -> V {
        return value
    }

    final func setValue(_ newValue: V) -> V {
        let oldValue: V = value
        value = newValue
        return oldValue
    }

    final var hashValue: Int {
        return  key.hashValue
    }

    var description: String { return "\(getKey())=\(getValue())" }

}
func == <K, V: Equatable>(lhs: Entry<K,V>, rhs: Entry<K,V>) -> Bool {
    if lhs === rhs {
        return true
    }
    if  lhs.key == rhs.key {
        if  lhs.value == rhs.value {
            return true
        }
    }
    return false
}
func == <K, V: Equatable>(lhs: Entry<K,V?>, rhs: Entry<K,V?>) -> Bool {
    if lhs === rhs {
        return true
    }
    if  lhs.key == rhs.key {
        if lhs.value == nil && rhs.value == nil {
            return true
        } else if lhs.value != nil && rhs.value != nil && lhs.value! == rhs.value! {
            return true
        }
    }
    return false
}



public final class HashMap<K: Hashable,V>: Sequence
{

    /// 
    /// The default initial capacity - MUST be a power of two.
    /// 
    private let DEFAULT_INITIAL_CAPACITY: Int = 16

    /// 
    /// The maximum capacity, used if a higher value is implicitly specified
    /// by either of the constructors with arguments.
    /// MUST be a power of two <= 1<<30.
    /// 
    private let MAXIMUM_CAPACITY: Int = 1 << 30

    /// 
    /// The load factor used when none specified in constructor.
    /// 
    private let DEFAULT_LOAD_FACTOR: Float = 0.75

    /// 
    /// The table, resized as necessary. Length MUST Always be a power of two.
    /// 
     var table: [Entry<K,V>?]

    /// 
    /// The number of key-value mappings contained in this map.
    /// 
     var size: Int = 0

    /// 
    /// The next size value at which to resize (capacity * load factor).
    /// -
    /// 
    var threshold: Int = 0

    /// 
    /// The load factor for the hash table.
    /// 
    /// -
    /// 
     var loadFactor: Float = 0

    /// 
    /// The number of times this HashMap has been structurally modified
    /// Structural modifications are those that change the number of mappings in
    /// the HashMap or otherwise modify its internal structure (e.g.,
    /// rehash).  This field is used to make iterators on Collection-views of
    /// the HashMap fail-fast.  (See ConcurrentModificationException).
    /// 
    var modCount: Int = 0

    public init(count: Int) {
        var initialCapacity = count
        if (count < 0)
        {
            initialCapacity = DEFAULT_INITIAL_CAPACITY
        }
        else if (count > MAXIMUM_CAPACITY)
        {
            initialCapacity = MAXIMUM_CAPACITY
        } else {
            // Find a power of 2 >= initialCapacity
            initialCapacity = 1
            while initialCapacity < count
            {
                initialCapacity <<= 1
            }
        }

        self.loadFactor = DEFAULT_LOAD_FACTOR
        threshold = Int(Float(initialCapacity) * loadFactor)
        table =  [Entry<K,V>?](repeating: nil, count: initialCapacity)
    }
    public init() {
        self.loadFactor = DEFAULT_LOAD_FACTOR
        threshold = Int(Float(DEFAULT_INITIAL_CAPACITY) * DEFAULT_LOAD_FACTOR)
        table =  [Entry<K,V>?](repeating: nil, count: DEFAULT_INITIAL_CAPACITY)
    }

    static func hash(_ h: Int) -> Int {
        var h = h
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12)
        return h ^ (h >>> 7) ^ (h >>> 4)
    }

    /// 
    /// Returns index for hash code h.
    /// 
    static func indexFor(_ h: Int, _ length: Int) -> Int {
        return h & (length-1)
    }

    /// 
    /// Returns <tt>true</tt> if this map contains no key-value mappings.
    /// 
    /// - returns: <tt>true</tt> if this map contains no key-value mappings
    /// 
    public final var isEmpty: Bool {
        return size == 0
    }
    public final subscript(key: K) -> V? {
        get {
            return get(key)
        }
        set {
            if newValue == nil {
                remove(key)
            }else{
                put(key,newValue!)
            }
        }
    }

    public final var count: Int {
        return size
    }
    /// 
    /// Returns the value to which the specified key is mapped,
    /// or `null` if this map contains no mapping for the key.
    /// 
    /// More formally, if this map contains a mapping from a key
    /// `k` to a value `v` such that `(key==null ? k==null :
    /// key.equals(k))`, then this method returns `v`; otherwise
    /// it returns `null`.  (There can be at most one such mapping.)
    /// 
    /// A return value of `null` does not necessarily
    /// indicate that the map contains no mapping for the key; it's also
    /// possible that the map explicitly maps the key to `null`.
    /// The _#containsKey containsKey_ operation may be used to
    /// distinguish these two cases.
    /// 
    /// - seealso: #put(Object, Object)
    /// 
    public final func get(_ key: K) -> V? {
        let hash: Int = HashMap.hash(key.hashValue)
        var e = table[HashMap.indexFor(hash, table.count)]
        while let eWrap = e {
            if  eWrap.hash == hash &&  eWrap.key == key
            {
                return eWrap.value
            }
            e = eWrap.next
        }

        return nil
    }
    /// 
    /// Returns <tt>true</tt> if this map contains a mapping for the
    /// specified key.
    /// 
    /// - parameter   key:   The key whose presence in this map is to be tested
    /// - returns: <tt>true</tt> if this map contains a mapping for the specified
    /// key.
    /// 
    public final func containsKey(_ key: K) -> Bool {
        return getEntry(key) != nil
    }

    /// 
    /// Returns the entry associated with the specified key in the
    /// HashMap.  Returns null if the HashMap contains no mapping
    /// for the key.
    /// 
    final func getEntry(_ key: K) -> Entry<K,V>! {
        let hash: Int =  HashMap.hash(key.hashValue)
        var e = table[HashMap.indexFor(hash, table.count)]
        while let eWrap = e {
            if eWrap.hash == hash && eWrap.key == key
            {
                return eWrap
            }
            e = eWrap.next
        }

        return nil
    }


    /// 
    /// Associates the specified value with the specified key in this map.
    /// If the map previously contained a mapping for the key, the old
    /// value is replaced.
    /// 
    /// - parameter key: key with which the specified value is to be associated
    /// - parameter value: value to be associated with the specified key
    /// - returns: the previous value associated with <tt>key</tt>, or
    /// <tt>null</tt> if there was no mapping for <tt>key</tt>.
    /// (A <tt>null</tt> return can also indicate that the map
    /// previously associated <tt>null</tt> with <tt>key</tt>.)
    /// 
    @discardableResult
    public final func put(_ key: K, _ value: V) -> V? {

        let hash: Int = HashMap.hash(key.hashValue)
        let i: Int = HashMap.indexFor(hash, table.count)
        var e = table[i]
        while let eWrap = e {
            if  eWrap.hash == hash &&  eWrap.key == key {
                let oldValue = eWrap.value
                eWrap.value = value
                return oldValue
            }
            e = eWrap.next
        }


        modCount += 1
        addEntry(hash, key, value, i)
        return nil
    }

    /// 
    /// Adds a new entry with the specified key, value and hash code to
    /// the specified bucket.  It is the responsibility of this
    /// method to resize the table if appropriate.
    /// 
    /// Subclass overrides this to alter the behavior of put method.
    /// 
    final func addEntry(_ hash: Int, _ key: K, _ value: V, _ bucketIndex: Int) {
        let e = table[bucketIndex]
        table[bucketIndex] = Entry<K,V>(hash, key, value, e)
        let oldSize = size
        size += 1
        if oldSize >= threshold {
            resize(2 * table.count)
        }
    }
    /// 
    /// Rehashes the contents of this map into a new array with a
    /// larger capacity.  This method is called automatically when the
    /// number of keys in this map reaches its threshold.
    /// 
    /// If current capacity is MAXIMUM_CAPACITY, this method does not
    /// resize the map, but sets threshold to Integer.MAX_VALUE.
    /// This has the effect of preventing future calls.
    /// 
    /// - parameter newCapacity: the new capacity, MUST be a power of two;
    /// must be greater than current capacity unless current
    /// capacity is MAXIMUM_CAPACITY (in which case value
    /// is irrelevant).
    /// 
    final func resize(_ newCapacity: Int) {
        let oldCapacity: Int = table.count
        if oldCapacity == MAXIMUM_CAPACITY {
            threshold = Int.max
            return
        }

        var newTable  = [Entry<K,V>?](repeating: nil, count: newCapacity)
        transfer(&newTable)
        table = newTable
        threshold = Int(Float(newCapacity) * loadFactor)
    }

    /// 
    /// Transfers all entries from current table to newTable.
    /// 
    final func transfer(_ newTable: inout [Entry<K,V>?]) {

        let newCapacity: Int = newTable.count
        let length = table.count
        for  j in 0..<length {
            if let e = table[j] {
                table[j] = nil
                var eOption: Entry<K,V>? = e
                while let e = eOption {
                    let next = e.next
                    let i: Int = HashMap.indexFor(e.hash, newCapacity)
                    e.next = newTable[i]
                    newTable[i] = e
                    eOption = next
                }
            }
        }
    }
    /// 
    /// Removes all of the mappings from this map.
    /// The map will be empty after this call returns.
    /// 
    public final func clear() {
        modCount += 1
        let length = table.count
        for  i in 0..<length {
            table[i] = nil
        }
        size = 0
    }
    @discardableResult
    public func remove(_ key: K) -> V? {
        if let e  = removeEntryForKey(key) {
            return e.value
        }
        return nil
    }


    final func removeEntryForKey(_ key: K) -> Entry<K,V>? {
        let hash: Int = HashMap.hash(Int(key.hashValue))
        let i = Int(HashMap.indexFor(hash, Int(table.count)))
        var prev  = table[i]
        var e  = prev

        while let eWrap = e {
            let next  = eWrap.next
            var _: AnyObject
            if eWrap.hash == hash &&  eWrap.key == key{
                modCount += 1
                size -= 1
                if prev === eWrap
                {table[i] = next}
                else
                {prev?.next = next}
                return eWrap
            }
            prev = eWrap
            e = next
        }

        return e
    }

    public final var values: [V]{
        var valueList: [V] = [V]()
        let length = table.count
        for  j in 0..<length {
            if let e = table[j] {
                valueList.append(e.value)
                var eOption: Entry<K,V>? = e
                while let e = eOption {
                    let next = e.next
                    eOption = next
                    if let eOption = eOption  {
                        valueList.append(eOption.value)
                    }

                }
            }
        }
        return valueList
    }

    public final var keys: [K]{
        var keyList: [K] = [K]()
        let length = table.count
        for  j in 0..<length {
            if let e = table[j] {
                keyList.append(e.key)
                var eOption: Entry<K,V>? = e
                while let e = eOption {
                    let next = e.next
                    eOption = next
                    if let eOption = eOption  {
                        keyList.append(eOption.key )
                    }

                }
            }
        }
        return keyList
    }


    public func makeIterator() ->  AnyIterator<(K,V)> {
        var _next: Entry<K,V>? // next entry to return
        let expectedModCount: Int = modCount // For fast-fail
        var index: Int = 0 // current slot
        //var current: HashMapEntry<K,V> // current entry
        if size > 0{ // advance to first entry

            while index < table.count &&  _next == nil
            {
                _next = table[index]
                index += 1
            }
        }

        return AnyIterator {
            if self.modCount != expectedModCount
            {
                fatalError("\(#function) ConcurrentModificationException")
            }
            if let e  = _next {
                _next = e.next
                if _next == nil{
                    while index < self.table.count &&  _next == nil
                    {
                        _next = self.table[index]
                        index += 1
                    }
                }
                //current = e
                return (e.getKey(),e.getValue())
            } else {
                return nil
            }

        }

    }

}
