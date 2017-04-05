/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.misc;


import java.util.List;

/**
 * A generic set of integers.
 *
 * @see IntervalSet
 */
public interface IntSet {
	/**
	 * Adds the specified value to the current set.
	 *
	 * @param el the value to add
	 *
	 * @exception IllegalStateException if the current set is read-only
	 */
	void add(int el);

	/**
	 * Modify the current {@link IntSet} object to contain all elements that are
	 * present in itself, the specified {@code set}, or both.
	 *
	 * @param set The set to add to the current set. A {@code null} argument is
	 * treated as though it were an empty set.
	 * @return {@code this} (to support chained calls)
	 *
	 * @exception IllegalStateException if the current set is read-only
	 */

	IntSet addAll(IntSet set);

	/**
	 * Return a new {@link IntSet} object containing all elements that are
	 * present in both the current set and the specified set {@code a}.
	 *
	 * @param a The set to intersect with the current set. A {@code null}
	 * argument is treated as though it were an empty set.
	 * @return A new {@link IntSet} instance containing the intersection of the
	 * current set and {@code a}. The value {@code null} may be returned in
	 * place of an empty result set.
	 */

	IntSet and(IntSet a);

	/**
	 * Return a new {@link IntSet} object containing all elements that are
	 * present in {@code elements} but not present in the current set. The
	 * following expressions are equivalent for input non-null {@link IntSet}
	 * instances {@code x} and {@code y}.
	 *
	 * <ul>
	 * <li>{@code x.complement(y)}</li>
	 * <li>{@code y.subtract(x)}</li>
	 * </ul>
	 *
	 * @param elements The set to compare with the current set. A {@code null}
	 * argument is treated as though it were an empty set.
	 * @return A new {@link IntSet} instance containing the elements present in
	 * {@code elements} but not present in the current set. The value
	 * {@code null} may be returned in place of an empty result set.
	 */

	IntSet complement(IntSet elements);

	/**
	 * Return a new {@link IntSet} object containing all elements that are
	 * present in the current set, the specified set {@code a}, or both.
	 *
	 * <p>
	 * This method is similar to {@link #addAll(IntSet)}, but returns a new
	 * {@link IntSet} instance instead of modifying the current set.</p>
	 *
	 * @param a The set to union with the current set. A {@code null} argument
	 * is treated as though it were an empty set.
	 * @return A new {@link IntSet} instance containing the union of the current
	 * set and {@code a}. The value {@code null} may be returned in place of an
	 * empty result set.
	 */

	IntSet or(IntSet a);

	/**
	 * Return a new {@link IntSet} object containing all elements that are
	 * present in the current set but not present in the input set {@code a}.
	 * The following expressions are equivalent for input non-null
	 * {@link IntSet} instances {@code x} and {@code y}.
	 *
	 * <ul>
	 * <li>{@code y.subtract(x)}</li>
	 * <li>{@code x.complement(y)}</li>
	 * </ul>
	 *
	 * @param a The set to compare with the current set. A {@code null}
	 * argument is treated as though it were an empty set.
	 * @return A new {@link IntSet} instance containing the elements present in
	 * {@code elements} but not present in the current set. The value
	 * {@code null} may be returned in place of an empty result set.
	 */

	IntSet subtract(IntSet a);

	/**
	 * Return the total number of elements represented by the current set.
	 *
	 * @return the total number of elements represented by the current set,
	 * regardless of the manner in which the elements are stored.
	 */
	int size();

	/**
	 * Returns {@code true} if this set contains no elements.
	 *
	 * @return {@code true} if the current set contains no elements; otherwise,
	 * {@code false}.
	 */
	boolean isNil();

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns {@code true} if the set contains the specified element.
	 *
	 * @param el The element to check for.
	 * @return {@code true} if the set contains {@code el}; otherwise {@code false}.
	 */
	boolean contains(int el);

	/**
	 * Removes the specified value from the current set. If the current set does
	 * not contain the element, no changes are made.
	 *
	 * @param el the value to remove
	 *
	 * @exception IllegalStateException if the current set is read-only
	 */
	void remove(int el);

	/**
	 * Return a list containing the elements represented by the current set. The
	 * list is returned in ascending numerical order.
	 *
	 * @return A list containing all element present in the current set, sorted
	 * in ascending numerical order.
	 */

	List<Integer> toList();

	/**
	 * {@inheritDoc}
	 */
	@Override
	String toString();
}
