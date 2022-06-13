/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.misc.IntegerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestIntegerList {
	@Test
	public void emptyListToEmptyCharArray() {
		IntegerList l = new IntegerList();
		assertArrayEquals(new char[0], l.toCharArray());
	}

	@Test
	public void negativeIntegerToCharArrayThrows() {
		IntegerList l = new IntegerList();
		l.add(-42);
		assertThrows(
				IllegalArgumentException.class,
				l::toCharArray
		);
	}

	@Test
	public void surrogateRangeIntegerToCharArray() {
		IntegerList l = new IntegerList();
		// Java allows dangling surrogates, so (currently) we do
		// as well. We could change this if desired.
		l.add(0xDC00);
		char[] expected = new char[] { 0xDC00 };
		assertArrayEquals(expected, l.toCharArray());
	}

	@Test
	public void tooLargeIntegerToCharArrayThrows() {
		IntegerList l = new IntegerList();
		l.add(0x110000);
		assertThrows(
				IllegalArgumentException.class,
				l::toCharArray
		);
	}

	@Test
	public void unicodeBMPIntegerListToCharArray() {
		IntegerList l = new IntegerList();
		l.add(0x35);
		l.add(0x4E94);
		l.add(0xFF15);
		char[] expected = new char[] { 0x35, 0x4E94, 0xFF15 };
		assertArrayEquals(expected, l.toCharArray());
	}

	@Test
	public void unicodeSMPIntegerListToCharArray() {
		IntegerList l = new IntegerList();
		l.add(0x104A5);
		l.add(0x116C5);
		l.add(0x1D7FB);
		char[] expected = new char[] { 0xD801, 0xDCA5, 0xD805, 0xDEC5, 0xD835, 0xDFFB };
		assertArrayEquals(expected, l.toCharArray());
	}
}
