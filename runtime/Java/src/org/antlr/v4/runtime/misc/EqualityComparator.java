/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.misc;

/**
 * This interface provides an abstract concept of object equality independent of
 * {@link Object#equals} (object equality) and the {@code ==} operator
 * (reference equality). It can be used to provide algorithm-specific unordered
 * comparisons without requiring changes to the object itself.
 *
 * @author Sam Harwell
 */
public interface EqualityComparator<T> {

	/**
	 * This method returns a hash code for the specified object.
	 *
	 * @param obj The object.
	 * @return The hash code for {@code obj}.
	 */
	int hashCode(T obj);

	/**
	 * This method tests if two objects are equal.
	 *
	 * @param a The first object to compare.
	 * @param b The second object to compare.
	 * @return {@code true} if {@code a} equals {@code b}, otherwise {@code false}.
	 */
	boolean equals(T a, T b);

}
