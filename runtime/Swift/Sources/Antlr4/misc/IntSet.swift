/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// A generic set of integers.
/// 
/// - seealso: org.antlr.v4.runtime.misc.IntervalSet

public protocol IntSet {
    /// Adds the specified value to the current set.
    /// 
    /// - parameter el: the value to add
    /// 
    /// -  IllegalStateException if the current set is read-only
    func add(_ el: Int) throws

    /// Modify the current {@link org.antlr.v4.runtime.misc.IntSet} object to contain all elements that are
    /// present in itself, the specified {@code set}, or both.
    /// 
    /// - parameter set: The set to add to the current set. A {@code null} argument is
    /// treated as though it were an empty set.
    /// - returns: {@code this} (to support chained calls)
    /// 
    /// -  IllegalStateException if the current set is read-only

    func addAll(_ set: IntSet?) throws -> IntSet

    /// Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
    /// present in both the current set and the specified set {@code a}.
    /// 
    /// - parameter a: The set to intersect with the current set. A {@code null}
    /// argument is treated as though it were an empty set.
    /// - returns: A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the intersection of the
    /// current set and {@code a}. The value {@code null} may be returned in
    /// place of an empty result set.

    func and(_ a: IntSet?) throws -> IntSet?

    /// Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
    /// present in {@code elements} but not present in the current set. The
    /// following expressions are equivalent for input non-null {@link org.antlr.v4.runtime.misc.IntSet}
    /// instances {@code x} and {@code y}.
    /// 
    /// <ul>
    /// <li>{@code x.complement(y)}</li>
    /// <li>{@code y.subtract(x)}</li>
    /// </ul>
    /// 
    /// - parameter elements: The set to compare with the current set. A {@code null}
    /// argument is treated as though it were an empty set.
    /// - returns: A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the elements present in
    /// {@code elements} but not present in the current set. The value
    /// {@code null} may be returned in place of an empty result set.

    func complement(_ elements: IntSet?) throws -> IntSet?

    /// Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
    /// present in the current set, the specified set {@code a}, or both.
    /// 
    /// <p>
    /// This method is similar to {@link #addAll(org.antlr.v4.runtime.misc.IntSet)}, but returns a new
    /// {@link org.antlr.v4.runtime.misc.IntSet} instance instead of modifying the current set.</p>
    /// 
    /// - parameter a: The set to union with the current set. A {@code null} argument
    /// is treated as though it were an empty set.
    /// - returns: A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the union of the current
    /// set and {@code a}. The value {@code null} may be returned in place of an
    /// empty result set.

    func or(_ a: IntSet) throws -> IntSet

    /// Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
    /// present in the current set but not present in the input set {@code a}.
    /// The following expressions are equivalent for input non-null
    /// {@link org.antlr.v4.runtime.misc.IntSet} instances {@code x} and {@code y}.
    /// 
    /// <ul>
    /// <li>{@code y.subtract(x)}</li>
    /// <li>{@code x.complement(y)}</li>
    /// </ul>
    /// 
    /// - parameter a: The set to compare with the current set. A {@code null}
    /// argument is treated as though it were an empty set.
    /// - returns: A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the elements present in
    /// {@code elements} but not present in the current set. The value
    /// {@code null} may be returned in place of an empty result set.

    func subtract(_ a: IntSet?) throws -> IntSet

    /// Return the total number of elements represented by the current set.
    /// 
    /// - returns: the total number of elements represented by the current set,
    /// regardless of the manner in which the elements are stored.
    func size() -> Int

    /// Returns {@code true} if this set contains no elements.
    /// 
    /// - returns: {@code true} if the current set contains no elements; otherwise,
    /// {@code false}.
    func isNil() -> Bool

    /// {@inheritDoc}

    //func equals(obj : AnyObject) -> Bool;

    /// Returns the single value contained in the set, if {@link #size} is 1;
    /// otherwise, returns {@link org.antlr.v4.runtime.Token#INVALID_TYPE}.
    /// 
    /// - returns: the single value contained in the set, if {@link #size} is 1;
    /// otherwise, returns {@link org.antlr.v4.runtime.Token#INVALID_TYPE}.
    func getSingleElement() -> Int

    /// Returns {@code true} if the set contains the specified element.
    /// 
    /// - parameter el: The element to check for.
    /// - returns: {@code true} if the set contains {@code el}; otherwise {@code false}.
    func contains(_ el: Int) -> Bool

    /// Removes the specified value from the current set. If the current set does
    /// not contain the element, no changes are made.
    /// 
    /// - parameter el: the value to remove
    /// 
    /// -  IllegalStateException if the current set is read-only
    func remove(_ el: Int) throws

    /// Return a list containing the elements represented by the current set. The
    /// list is returned in ascending numerical order.
    /// 
    /// - returns: A list containing all element present in the current set, sorted
    /// in ascending numerical order.

    func toList() -> Array<Int>

    /// {@inheritDoc}

    func toString() -> String
}
