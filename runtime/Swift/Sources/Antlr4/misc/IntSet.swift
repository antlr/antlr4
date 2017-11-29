/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// A generic set of integers.
/// 
/// - seealso: org.antlr.v4.runtime.misc.IntervalSet
/// 

public protocol IntSet {
    /// 
    /// Adds the specified value to the current set.
    /// 
    /// - parameter el: the value to add
    /// 
    /// - throws: _ANTLRError.illegalState_ if the current set is read-only
    /// 
    func add(_ el: Int) throws

    /// 
    /// Modify the current _org.antlr.v4.runtime.misc.IntSet_ object to contain all elements that are
    /// present in itself, the specified `set`, or both.
    /// 
    /// - parameter set: The set to add to the current set. A `null` argument is
    /// treated as though it were an empty set.
    /// - returns: `this` (to support chained calls)
    /// 
    /// - throws: _ANTLRError.illegalState_ if the current set is read-only
    /// 
    func addAll(_ set: IntSet?) throws -> IntSet

    /// 
    /// Return a new _org.antlr.v4.runtime.misc.IntSet_ object containing all elements that are
    /// present in both the current set and the specified set `a`.
    /// 
    /// - parameter a: The set to intersect with the current set. A `null`
    /// argument is treated as though it were an empty set.
    /// - returns: A new _org.antlr.v4.runtime.misc.IntSet_ instance containing the intersection of the
    /// current set and `a`. The value `null` may be returned in
    /// place of an empty result set.
    /// 
    func and(_ a: IntSet?) -> IntSet?

    /// 
    /// Return a new _org.antlr.v4.runtime.misc.IntSet_ object containing all elements that are
    /// present in `elements` but not present in the current set. The
    /// following expressions are equivalent for input non-null _org.antlr.v4.runtime.misc.IntSet_
    /// instances `x` and `y`.
    /// 
    /// * `x.complement(y)`
    /// *`y.subtract(x)`
    /// 
    /// - parameter elements: The set to compare with the current set. A `null`
    /// argument is treated as though it were an empty set.
    /// - returns: A new _org.antlr.v4.runtime.misc.IntSet_ instance containing the elements present in
    /// `elements` but not present in the current set. The value
    /// `null` may be returned in place of an empty result set.
    /// 
    func complement(_ elements: IntSet?) -> IntSet?

    /// 
    /// Return a new _org.antlr.v4.runtime.misc.IntSet_ object containing all elements that are
    /// present in the current set, the specified set `a`, or both.
    /// 
    /// 
    /// This method is similar to _#addAll(org.antlr.v4.runtime.misc.IntSet)_, but returns a new
    /// _org.antlr.v4.runtime.misc.IntSet_ instance instead of modifying the current set.
    /// 
    /// - parameter a: The set to union with the current set. A `null` argument
    /// is treated as though it were an empty set.
    /// - returns: A new _org.antlr.v4.runtime.misc.IntSet_ instance containing the union of the current
    /// set and `a`. The value `null` may be returned in place of an
    /// empty result set.
    /// 
    func or(_ a: IntSet) -> IntSet

    /// 
    /// Return a new _org.antlr.v4.runtime.misc.IntSet_ object containing all elements that are
    /// present in the current set but not present in the input set `a`.
    /// The following expressions are equivalent for input non-null
    /// _org.antlr.v4.runtime.misc.IntSet_ instances `x` and `y`.
    /// 
    /// * `y.subtract(x)`
    /// * `x.complement(y)`
    /// 
    /// - parameter a: The set to compare with the current set. A `null`
    /// argument is treated as though it were an empty set.
    /// - returns: A new _org.antlr.v4.runtime.misc.IntSet_ instance containing the elements present in
    /// `elements` but not present in the current set. The value
    /// `null` may be returned in place of an empty result set.
    /// 
    func subtract(_ a: IntSet?) -> IntSet

    /// 
    /// Return the total number of elements represented by the current set.
    /// 
    /// - returns: the total number of elements represented by the current set,
    /// regardless of the manner in which the elements are stored.
    /// 
    func size() -> Int

    /// 
    /// Returns `true` if this set contains no elements.
    /// 
    /// - returns: `true` if the current set contains no elements; otherwise,
    /// `false`.
    /// 
    func isNil() -> Bool

    /// 
    /// Returns the single value contained in the set, if _#size_ is 1;
    /// otherwise, returns _org.antlr.v4.runtime.Token#INVALID_TYPE_.
    /// 
    /// - returns: the single value contained in the set, if _#size_ is 1;
    /// otherwise, returns _org.antlr.v4.runtime.Token#INVALID_TYPE_.
    /// 
    func getSingleElement() -> Int

    /// 
    /// Returns `true` if the set contains the specified element.
    /// 
    /// - parameter el: The element to check for.
    /// - returns: `true` if the set contains `el`; otherwise `false`.
    /// 
    func contains(_ el: Int) -> Bool

    /// 
    /// Removes the specified value from the current set. If the current set does
    /// not contain the element, no changes are made.
    /// 
    /// - parameter el: the value to remove
    /// 
    /// - throws: _ANTLRError.illegalState_ if the current set is read-only
    /// 
    func remove(_ el: Int) throws

    /// 
    /// Return a list containing the elements represented by the current set. The
    /// list is returned in ascending numerical order.
    /// 
    /// - returns: A list containing all element present in the current set, sorted
    /// in ascending numerical order.
    /// 
    func toList() -> [Int]
}
