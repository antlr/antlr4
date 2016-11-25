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


/**
 * A generic set of integers.
 *
 * @see org.antlr.v4.runtime.misc.IntervalSet
 */

public protocol IntSet {
    /**
     * Adds the specified value to the current set.
     *
     * @param el the value to add
     *
     * @exception IllegalStateException if the current set is read-only
     */
    func add(_ el: Int) throws

    /**
     * Modify the current {@link org.antlr.v4.runtime.misc.IntSet} object to contain all elements that are
     * present in itself, the specified {@code set}, or both.
     *
     * @param set The set to add to the current set. A {@code null} argument is
     * treated as though it were an empty set.
     * @return {@code this} (to support chained calls)
     *
     * @exception IllegalStateException if the current set is read-only
     */

    func addAll(_ set: IntSet?) throws -> IntSet

    /**
     * Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
     * present in both the current set and the specified set {@code a}.
     *
     * @param a The set to intersect with the current set. A {@code null}
     * argument is treated as though it were an empty set.
     * @return A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the intersection of the
     * current set and {@code a}. The value {@code null} may be returned in
     * place of an empty result set.
     */

    func and(_ a: IntSet?) throws -> IntSet?

    /**
     * Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
     * present in {@code elements} but not present in the current set. The
     * following expressions are equivalent for input non-null {@link org.antlr.v4.runtime.misc.IntSet}
     * instances {@code x} and {@code y}.
     *
     * <ul>
     * <li>{@code x.complement(y)}</li>
     * <li>{@code y.subtract(x)}</li>
     * </ul>
     *
     * @param elements The set to compare with the current set. A {@code null}
     * argument is treated as though it were an empty set.
     * @return A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the elements present in
     * {@code elements} but not present in the current set. The value
     * {@code null} may be returned in place of an empty result set.
     */

    func complement(_ elements: IntSet?) throws -> IntSet?

    /**
     * Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
     * present in the current set, the specified set {@code a}, or both.
     *
     * <p>
     * This method is similar to {@link #addAll(org.antlr.v4.runtime.misc.IntSet)}, but returns a new
     * {@link org.antlr.v4.runtime.misc.IntSet} instance instead of modifying the current set.</p>
     *
     * @param a The set to union with the current set. A {@code null} argument
     * is treated as though it were an empty set.
     * @return A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the union of the current
     * set and {@code a}. The value {@code null} may be returned in place of an
     * empty result set.
     */

    func or(_ a: IntSet) throws -> IntSet

    /**
     * Return a new {@link org.antlr.v4.runtime.misc.IntSet} object containing all elements that are
     * present in the current set but not present in the input set {@code a}.
     * The following expressions are equivalent for input non-null
     * {@link org.antlr.v4.runtime.misc.IntSet} instances {@code x} and {@code y}.
     *
     * <ul>
     * <li>{@code y.subtract(x)}</li>
     * <li>{@code x.complement(y)}</li>
     * </ul>
     *
     * @param a The set to compare with the current set. A {@code null}
     * argument is treated as though it were an empty set.
     * @return A new {@link org.antlr.v4.runtime.misc.IntSet} instance containing the elements present in
     * {@code elements} but not present in the current set. The value
     * {@code null} may be returned in place of an empty result set.
     */

    func subtract(_ a: IntSet?) throws -> IntSet

    /**
     * Return the total number of elements represented by the current set.
     *
     * @return the total number of elements represented by the current set,
     * regardless of the manner in which the elements are stored.
     */
    func size() -> Int

    /**
     * Returns {@code true} if this set contains no elements.
     *
     * @return {@code true} if the current set contains no elements; otherwise,
     * {@code false}.
     */
    func isNil() -> Bool

    /**
     * {@inheritDoc}
     */

    //func equals(obj : AnyObject) -> Bool;

    /**
     * Returns the single value contained in the set, if {@link #size} is 1;
     * otherwise, returns {@link org.antlr.v4.runtime.Token#INVALID_TYPE}.
     *
     * @return the single value contained in the set, if {@link #size} is 1;
     * otherwise, returns {@link org.antlr.v4.runtime.Token#INVALID_TYPE}.
     */
    func getSingleElement() -> Int

    /**
     * Returns {@code true} if the set contains the specified element.
     *
     * @param el The element to check for.
     * @return {@code true} if the set contains {@code el}; otherwise {@code false}.
     */
    func contains(_ el: Int) -> Bool

    /**
     * Removes the specified value from the current set. If the current set does
     * not contain the element, no changes are made.
     *
     * @param el the value to remove
     *
     * @exception IllegalStateException if the current set is read-only
     */
    func remove(_ el: Int) throws

    /**
     * Return a list containing the elements represented by the current set. The
     * list is returned in ascending numerical order.
     *
     * @return A list containing all element present in the current set, sorted
     * in ascending numerical order.
     */

    func toList() -> Array<Int>

    /**
     * {@inheritDoc}
     */

    func toString() -> String
}
