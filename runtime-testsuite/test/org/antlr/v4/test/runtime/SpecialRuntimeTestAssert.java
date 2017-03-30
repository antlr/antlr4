/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

/** This interface acts like a tag on a Base*Test class that wants
 *  to use its own assertEquals() instead of jUnit's.
 */
public interface SpecialRuntimeTestAssert {
	void assertEqualStrings(String expected, String actual);
}
