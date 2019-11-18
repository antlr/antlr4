/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// This class implements the _org.antlr.v4.runtime.misc.IntSet_ backed by a sorted array of
/// non-overlapping intervals. It is particularly efficient for representing
/// large collections of numbers, where the majority of elements appear as part
/// of a sequential range of numbers that are all part of the set. For example,
/// the set { 1, 2, 3, 4, 7, 8 } may be represented as { [1, 4], [7, 8] }.
/// 
/// 
/// This class is able to represent sets containing any combination of values in
/// the range _Integer#MIN_VALUE_ to _Integer#MAX_VALUE_
/// (inclusive).
/// 

public class IntervalSet: IntSet, Hashable, CustomStringConvertible {
    public static let COMPLETE_CHAR_SET: IntervalSet =
    {
        let set = IntervalSet.of(Lexer.MIN_CHAR_VALUE, Lexer.MAX_CHAR_VALUE)
        set.makeReadonly()
        return set
    }()

    public static let EMPTY_SET: IntervalSet = {
        let set = IntervalSet()
        set.makeReadonly()
        return set
    }()


    /// 
    /// The list of sorted, disjoint intervals.
    /// 
    internal var intervals: [Interval]

    internal var readonly = false

    public init(_ intervals: [Interval]) {
        self.intervals = intervals
    }

    public convenience init(_ set: IntervalSet) {
        self.init()
        try! addAll(set)
    }

    public init(_ els: Int...) {
        if els.isEmpty {
            intervals = [Interval]() // most sets are 1 or 2 elements
        } else {
            intervals = [Interval]()
            for e in els {
                try! add(e)
            }
        }
    }

    ///
    /// Create a set with all ints within range [a..b] (inclusive)
    /// 
    public static func of(_ a: Int, _ b: Int) -> IntervalSet {
        let s = IntervalSet()
        try! s.add(a, b)
        return s
    }

    public func clear() throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "can't alter readonly IntervalSet")
        }
        intervals.removeAll()
    }

    /// 
    /// Add a single element to the set.  An isolated element is stored
    /// as a range el..el.
    /// 

    public func add(_ el: Int) throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "can't alter readonly IntervalSet")
        }
        try! add(el, el)
    }

    /// 
    /// Add interval; i.e., add all integers from a to b to set.
    /// If b&lt;a, do nothing.
    /// Keep list in sorted order (by left range value).
    /// If overlap, combine ranges.  For example,
    /// If this is {1..5, 10..20}, adding 6..7 yields
    /// {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
    /// 
    public func add(_ a: Int, _ b: Int) throws {
        try add(Interval.of(a, b))
    }

    // copy on write so we can cache a..a intervals and sets of that
    internal func add(_ addition: Interval) throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "can't alter readonly IntervalSet")
        }
        if addition.b < addition.a {
            return
        }
        // find position in list
        // Use iterators as we modify list in place
        var i = 0

        while i < intervals.count {

            let r = intervals[i]
            if addition == r {
                return
            }
            if addition.adjacent(r) || !addition.disjoint(r) {
                // next to each other, make a single larger interval
                let bigger = addition.union(r)
                //iter.set(bigger);
                intervals[i] = bigger
                // make sure we didn't just create an interval that
                // should be merged with next interval in list
                while i < intervals.count - 1 {
                    i += 1
                    let next = intervals[i]
                    if !bigger.adjacent(next) && bigger.disjoint(next) {
                        break
                    }

                    // if we bump up against or overlap next, merge
                    /// 
                    /// iter.remove();   // remove this one
                    /// iter.previous(); // move backwards to what we just set
                    /// iter.set(bigger.union(next)); // set to 3 merged ones
                    /// iter.next(); // first call to next after previous duplicates the resul
                    /// 
                    intervals.remove(at: i)
                    i -= 1
                    intervals[i] = bigger.union(next)
                }
                return
            }
            if addition.startsBeforeDisjoint(r) {
                // insert before r
                intervals.insert(addition, at: i)
                return
            }
            // if disjoint and after r, a future iteration will handle it

            i += 1
        }
        // ok, must be after last interval (and disjoint from last interval)
        // just add it
        intervals.append(addition)
    }

    /// 
    /// combine all sets in the array returned the or'd value
    /// 
    public func or(_ sets: [IntervalSet]) -> IntSet {
        let r = IntervalSet()
        for s in sets {
            try! r.addAll(s)
        }
        return r
    }

    @discardableResult
    public func addAll(_ set: IntSet?) throws -> IntSet {

        guard let set = set else {
             return self
        }
        if let other = set as? IntervalSet {
            // walk set and add each interval
            for interval in other.intervals {
                try add(interval)
            }
        } else {
            let setList = set.toList()
            for value in setList {
                try add(value)
            }
        }

        return self
    }

    public func complement(_ minElement: Int, _ maxElement: Int) -> IntSet? {
        return complement(IntervalSet.of(minElement, maxElement))
    }

    ///
    /// 
    /// 

    public func complement(_ vocabulary: IntSet?) -> IntSet? {
        guard let vocabulary = vocabulary, !vocabulary.isNil() else {
            return nil  // nothing in common with null set
        }
        var vocabularyIS: IntervalSet
        if let vocabulary = vocabulary as? IntervalSet {
            vocabularyIS = vocabulary
        } else {
            vocabularyIS = IntervalSet()
            try! vocabularyIS.addAll(vocabulary)
        }

        return vocabularyIS.subtract(self)
    }


    public func subtract(_ a: IntSet?) -> IntSet {
        guard let a = a, !a.isNil() else {
            return IntervalSet(self)
        }
        if let a = a as? IntervalSet {
            return subtract(self, a)
        }

        let other = IntervalSet()
        try! other.addAll(a)
        return subtract(self, other)
    }

    /// 
    /// Compute the set difference between two interval sets. The specific
    /// operation is `left - right`. If either of the input sets is
    /// `null`, it is treated as though it was an empty set.
    /// 

    public func subtract(_ left: IntervalSet?, _ right: IntervalSet?) -> IntervalSet {

        guard let left = left, !left.isNil() else {
            return IntervalSet()
        }

        let result = IntervalSet(left)

        guard let right = right, !right.isNil() else {
            // right set has no elements; just return the copy of the current set
            return result
        }
        var resultI = 0
        var rightI = 0
        while resultI < result.intervals.count && rightI < right.intervals.count {
            let resultInterval = result.intervals[resultI]
            let rightInterval = right.intervals[rightI]

            // operation: (resultInterval - rightInterval) and update indexes

            if rightInterval.b < resultInterval.a {
                rightI += 1
                continue
            }

            if rightInterval.a > resultInterval.b {
                resultI += 1
                continue
            }

            var beforeCurrent: Interval? = nil
            var afterCurrent: Interval? = nil
            if rightInterval.a > resultInterval.a {
                beforeCurrent = Interval(resultInterval.a, rightInterval.a - 1)
            }

            if rightInterval.b < resultInterval.b {
                afterCurrent = Interval(rightInterval.b + 1, resultInterval.b)
            }

            if let beforeCurrent = beforeCurrent {
                if let afterCurrent = afterCurrent {
                    // split the current interval into two
                    result.intervals[resultI] = beforeCurrent
                    result.intervals.insert(afterCurrent, at: resultI + 1)
                    resultI += 1
                    rightI += 1
                    continue
                } else {
                    // replace the current interval
                    result.intervals[resultI] = beforeCurrent
                    resultI += 1
                    continue
                }
            } else {
                if let afterCurrent = afterCurrent {
                    // replace the current interval
                    result.intervals[resultI] = afterCurrent
                    rightI += 1
                    continue
                } else {
                    // remove the current interval (thus no need to increment resultI)
                    result.intervals.remove(at: resultI)
                    //result.intervals.remove(resultI);
                    continue
                }
            }
        }

        // If rightI reached right.intervals.size(), no more intervals to subtract from result.
        // If resultI reached result.intervals.size(), we would be subtracting from an empty set.
        // Either way, we are done.
        return result
    }


    public func or(_ a: IntSet) -> IntSet {
        let o = IntervalSet()
        try! o.addAll(self)
        try! o.addAll(a)
        return o
    }

    /// 
    /// 
    /// 

    public func and(_ other: IntSet?) -> IntSet? {
        if other == nil {
            return nil // nothing in common with null set
        }

        let myIntervals = self.intervals
        let theirIntervals = (other as! IntervalSet).intervals
        var intersection: IntervalSet? = nil
        let mySize = myIntervals.count
        let theirSize = theirIntervals.count
        var i = 0
        var j = 0
        // iterate down both interval lists looking for nondisjoint intervals
        while i < mySize && j < theirSize {
            let mine = myIntervals[i]
            let theirs = theirIntervals[j]

            if mine.startsBeforeDisjoint(theirs) {
                // move this iterator looking for interval that might overlap
                i += 1
            } else {
                if theirs.startsBeforeDisjoint(mine) {
                    // move other iterator looking for interval that might overlap
                    j += 1
                } else {
                    if mine.properlyContains(theirs) {
                        // overlap, add intersection, get next theirs
                        if intersection == nil {
                            intersection = IntervalSet()
                        }

                        try! intersection!.add(mine.intersection(theirs))
                        j += 1
                    } else {
                        if theirs.properlyContains(mine) {
                            // overlap, add intersection, get next mine
                            if intersection == nil {
                                intersection = IntervalSet()
                            }
                            try! intersection!.add(mine.intersection(theirs))
                            i += 1
                        } else {
                            if !mine.disjoint(theirs) {
                                // overlap, add intersection
                                if intersection == nil {
                                    intersection = IntervalSet()
                                }
                                try! intersection!.add(mine.intersection(theirs))
                                // Move the iterator of lower range [a..b], but not
                                // the upper range as it may contain elements that will collide
                                // with the next iterator. So, if mine=[0..115] and
                                // theirs=[115..200], then intersection is 115 and move mine
                                // but not theirs as theirs may collide with the next range
                                // in thisIter.
                                // move both iterators to next ranges
                                if mine.startsAfterNonDisjoint(theirs) {
                                    j += 1
                                } else {
                                    if theirs.startsAfterNonDisjoint(mine) {
                                        i += 1
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if intersection == nil {
            return IntervalSet()
        }
        return intersection
    }

    /// 
    /// 
    /// 

    public func contains(_ el: Int) -> Bool {
        for interval in intervals {
            let a = interval.a
            let b = interval.b
            if el < a {
                break // list is sorted and el is before this interval; not here
            }
            if el >= a && el <= b {
                return true // found in this interval
            }
        }
        return false
    }

    /// 
    /// 
    /// 

    public func isNil() -> Bool {
        return intervals.isEmpty
    }

    /// 
    /// 
    /// 

    public func getSingleElement() -> Int {
        if intervals.count == 1 {
            let interval = intervals[0]
            if interval.a == interval.b {
                return interval.a
            }
        }
        return CommonToken.INVALID_TYPE
    }

    /// 
    /// Returns the maximum value contained in the set.
    /// 
    /// - returns: the maximum value contained in the set. If the set is empty, this
    /// method returns _org.antlr.v4.runtime.Token#INVALID_TYPE_.
    /// 
    public func getMaxElement() -> Int {
        if isNil() {
            return CommonToken.INVALID_TYPE
        }
        let last = intervals[intervals.count - 1]
        return last.b
    }

    /// 
    /// Returns the minimum value contained in the set.
    /// 
    /// - returns: the minimum value contained in the set. If the set is empty, this
    /// method returns _org.antlr.v4.runtime.Token#INVALID_TYPE_.
    /// 
    public func getMinElement() -> Int {
        if isNil() {
            return CommonToken.INVALID_TYPE
        }

        return intervals[0].a
    }

    /// 
    /// Return a list of Interval objects.
    /// 
    public func getIntervals() -> [Interval] {
        return intervals
    }

    public func hash(into hasher: inout Hasher) {
        for interval in intervals {
            hasher.combine(interval.a)
            hasher.combine(interval.b)
        }
    }

    /// 
    /// Are two IntervalSets equal?  Because all intervals are sorted
    /// and disjoint, equals is a simple linear walk over both lists
    /// to make sure they are the same.  Interval.equals() is used
    /// by the List.equals() method to check the ranges.
    /// 

    /// 
    /// public func equals(obj : AnyObject) -> Bool {
    /// if ( obj==nil || !(obj is IntervalSet) ) {
    /// return false;
    /// }
    /// var other : IntervalSet = obj as! IntervalSet;
    /// return self.intervals.equals(other.intervals);
    /// 

    public var description: String {
        return toString(false)
    }

    public func toString(_ elemAreChar: Bool) -> String {
        if intervals.isEmpty {
            return "{}"
        }

        let selfSize = size()

        var buf = ""

        if selfSize > 1 {
            buf += "{"
        }
        var first = true
        for interval in intervals {
            if !first {
                buf += ", "
            }
            first = false

            let a = interval.a
            let b = interval.b
            if a == b {
                if a == CommonToken.EOF {
                    buf += "<EOF>"
                }
                else if elemAreChar {
                    buf += "'\(a)'"
                }
                else {
                    buf += "\(a)"
                }
            }
            else if elemAreChar {
                buf += "'\(a)'..'\(b)'"
            }
            else {
                buf += "\(a)..\(b)"
            }
        }

        if selfSize > 1 {
            buf += "}"
        }

        return buf
    }

    public func toString(_ vocabulary: Vocabulary) -> String {
        if intervals.isEmpty {
            return "{}"
        }

        let selfSize = size()

        var buf = ""

        if selfSize > 1 {
            buf += "{"
        }

        var first = true
        for interval in intervals {
            if !first {
                buf += ", "
            }
            first = false

            let a = interval.a
            let b = interval.b
            if a == b {
                buf += elementName(vocabulary, a)
            }
            else {
                for i in a...b {
                    if i > a {
                        buf += ", "
                    }
                    buf += elementName(vocabulary, i)
                }
            }
        }

        if selfSize > 1 {
            buf += "}"
        }

        return buf
    }

    internal func elementName(_ vocabulary: Vocabulary, _ a: Int) -> String {
        if a == CommonToken.EOF {
            return "<EOF>"
        }
        else if a == CommonToken.EPSILON {
            return "<EPSILON>"
        }
        else {
            return vocabulary.getDisplayName(a)
        }
    }


    public func size() -> Int {
        var n = 0
        for interval in intervals {
            n += (interval.b - interval.a + 1)
        }
        return n
    }


    public func toList() -> [Int] {
        var values = [Int]()
        for interval in intervals {
            let a = interval.a
            let b = interval.b

            for v in a...b  {
                values.append(v)
            }
        }
        return values
    }

    public func toSet() -> Set<Int> {
        var s = Set<Int>()
        for interval in intervals {
            let a = interval.a
            let b = interval.b
            for v in a...b  {
                s.insert(v)
            }
        }
        return s
    }

    /// 
    /// Get the ith element of ordered set.  Used only by RandomPhrase so
    /// don't bother to implement if you're not doing that for a new
    /// ANTLR code gen target.
    /// 
    public func get(_ i: Int) -> Int {
        var index = 0
        for interval in intervals {
            let a = interval.a
            let b = interval.b
            for v in a...b  {
                if index == i {
                    return v
                }
                index += 1
            }
        }
        return -1
    }

    public func remove(_ el: Int) throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "can't alter readonly IntervalSet")
        }
        for interval in intervals {
            let a = interval.a
            let b = interval.b
            if el < a {
                break // list is sorted and el is before this interval; not here
            }
            // if whole interval x..x, rm
            if el == a && el == b {
                intervals.removeObject(interval)
                break
            }
            // if on left edge x..b, adjust left
            if el == a {
                interval.a += 1
                break
            }
            // if on right edge a..x, adjust right
            if el == b {
                interval.b -= 1
                break
            }
            // if in middle a..x..b, split interval
            if el > a && el < b {
                // found in this interval
                let oldb = interval.b
                interval.b = el - 1      // [a..x-1]
                try add(el + 1, oldb) // add [x+1..b]
            }
        }
    }

    public func isReadonly() -> Bool {
        return readonly
    }

    public func makeReadonly() {
        readonly = true
    }
}

public func ==(lhs: IntervalSet, rhs: IntervalSet) -> Bool {
    return lhs.intervals == rhs.intervals
}
